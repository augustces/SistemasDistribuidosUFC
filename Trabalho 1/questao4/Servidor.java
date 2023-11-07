package questao4;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private static final String UDP_IP_ADDRESS = "224.0.0.1"; // Endereço IP multicast para comunicação UDP
    private static final int SERVER_PORT_TCP = 12345; // Porta usada para comunicação TCP com clientes
    private static final int SERVER_PORT_UDP = 1357; // Porta usada para comunicação UDP multicast
    private static List<Candidato> candidates = new CopyOnWriteArrayList<>(); // Lista de candidatos
    private static List<Voto> votes = new CopyOnWriteArrayList<>(); // Lista de votos
    private static boolean votingOpen = true; // Indica se a votação está aberta
    private static int votos = 0; // Contador total de votos

    public static void main(String[] args) {
        // Inicialização dos candidatos com nomes e votos iniciais
        candidates.add(new Candidato("Silas", 0));
        candidates.add(new Candidato("Gustavo", 0));
        candidates.add(new Candidato("Augusto", 0));
        candidates.add(new Candidato("Carlos", 0));

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT_TCP)) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Informe a duração da votação em milissegundos: ");
            int duracao = sc.nextInt();
            long tempoInicial = System.currentTimeMillis();
            System.out.println("Server is running on port " + SERVER_PORT_TCP);

            // Configuração de um servidor UDP multicast
            MulticastSocket multicastServer = new MulticastSocket(SERVER_PORT_UDP);
            InetAddress group = InetAddress.getByName(UDP_IP_ADDRESS);

            // Loop principal do servidor
            while (votingOpen) {
                Socket clientSocket = serverSocket.accept();
                long tempoAtual = System.currentTimeMillis();

                if ((tempoAtual - tempoInicial) > duracao) {
                    System.out.println("Votação encerrada!");
                    List<Candidato> ganhadores = Ganhadores();

                    // Envio das informações sobre os ganhadores via multicast
                    String info = "Nome(s) do(s) ganhador(es): ";
                    byte[] messageBytes = info.getBytes();
                    DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, group, multicastServer.getLocalPort());
                    multicastServer.send(packet);

                    // Envio dos resultados de cada candidato via multicast
                    for (Candidato candidato : candidates) {
                        info = "";
                        info += candidato.getNome() + " - ";
                        info += candidato.getVotos() + " votos (" + candidato.getPercentagem() + "%) ";
                        if (ganhadores.contains(candidato)) {
                            info += "- ganhador";
                        }
                        messageBytes = info.getBytes();
                        packet = new DatagramPacket(messageBytes, messageBytes.length, group, multicastServer.getLocalPort());
                        multicastServer.send(packet);
                    }

                    // Marca o final da transmissão via multicast
                    info = "end";
                    messageBytes = info.getBytes();
                    packet = new DatagramPacket(messageBytes, messageBytes.length, group, multicastServer.getLocalPort());
                    multicastServer.send(packet);

                    break;
                }

                System.out.println("Conectado");

                // Cria uma nova thread para lidar com a solicitação do cliente
                new Thread(new ClientHandler(clientSocket, multicastServer, group)).start();
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para determinar os ganhadores da eleição
    private static List<Candidato> Ganhadores() {
        List<Candidato> ganhadores = new CopyOnWriteArrayList<>();
        Candidato ganhador = candidates.get(0);

        // Calcula o candidato com mais votos
        for (Candidato candidato : candidates) {
            if (ganhador.getVotos() < candidato.getVotos()) {
                ganhador = candidato;
            }
            String percentagem = String.format("%.0f", (100.0 * candidato.getVotos() / votos));
            candidato.setPercentagem(percentagem);
        }

        // Adiciona todos os candidatos com a mesma quantidade de votos que o ganhador à lista de ganhadores
        for (Candidato candidato : candidates) {
            if (ganhador.getVotos() == candidato.getVotos()) {
                ganhadores.add(candidato);
            }
        }

        return ganhadores;
    }

    // Classe interna para lidar com as solicitações de clientes
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private String cpf;
        private MulticastSocket multicastServer;
        private InetAddress group;

        public ClientHandler(Socket socket, MulticastSocket multicastServer, InetAddress group) {
            this.socket = socket;
            this.multicastServer = multicastServer;
            this.group = group;
        }

        @Override
        public void run() {
            try (
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                DataInputStream reader = new DataInputStream(input);
                DataOutputStream writer = new DataOutputStream(output)) {

                this.cpf = reader.readUTF();
                System.out.println(cpf + " conectado.");

                // Envie a lista de candidatos para o cliente se o CPF for "admin"
                if (cpf.equals("admin")) {
                    while (votingOpen) {
                        int value = reader.readInt();
                        if (value == 1) {
                            sendCandidateList(writer);
                        } else if (value == 2) {
                            InserirCandidato(reader, writer);
                        } else if (value == 3) {
                            RemoverCandidato(reader, writer);
                        } else if (value == 4) {
                            EnviarInformativos(reader, writer);
                        } else if (value == 5) {
                            break;
                        }
                    }
                } else {
                    eleitor(reader, writer);
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Métodos para manipular solicitações dos clientes
        private void sendCandidateList(DataOutputStream writer) throws IOException, EOFException {
            List<Candidato> candidatesCopy = candidates;
            writer.writeInt(candidatesCopy.size());

            if (candidates.size() > 0) {
                for (Candidato candidate : candidatesCopy) {
                    writer.writeUTF(candidate.getNome());
                }
            }
        }

        private void verificarCandidato(DataInputStream reader, DataOutputStream writer) throws IOException, EOFException {
            int numeroCandidato = reader.readInt();

            if (numeroCandidato <= candidates.size() && numeroCandidato > 0) {
                Voto voto = new Voto(cpf, candidates.get(numeroCandidato - 1).getNome());
                if (!votes.contains(voto)){
                    votes.add(voto);
                    candidates.get(numeroCandidato - 1).addVoto();
                    votos +=1;
                    System.out.println("Voto inserido!");
                    writer.writeInt(1);
                }
               else{
                writer.writeInt(2);
               } 
            } else {
                    writer.writeInt(0);
            }
        }

            // Método para lidar com a interação de eleitores
            private void eleitor(DataInputStream reader, DataOutputStream writer) throws IOException, EOFException {
                // Envia a lista de candidatos para o eleitor
                sendCandidateList(writer);

                // Verifica o candidato escolhido pelo eleitor e registra o voto, se válido
                verificarCandidato(reader, writer);
            }

            // Método para inserir um novo candidato (apenas para admin)
            private void InserirCandidato(DataInputStream reader, DataOutputStream writer) throws IOException, EOFException {
                // Lê o nome do novo candidato do cliente
                String nomeCandidato = reader.readUTF();
                
                // Adiciona o novo candidato à lista de candidatos
                candidates.add(new Candidato(nomeCandidato, 0));
                writer.writeInt(1); // Confirmação de sucesso para o cliente
            }

            // Método para remover um candidato existente (apenas para admin)
            private void RemoverCandidato(DataInputStream reader, DataOutputStream writer) throws IOException, EOFException {
                // Envia a lista de candidatos para o cliente
                sendCandidateList(writer);
                
                // Lê o número do candidato que o cliente deseja remover
                int valor = reader.readInt();
                if (valor > 0 && valor <= candidates.size()) {
                    // Remove o candidato da lista
                    candidates.remove(valor - 1);
                    writer.writeInt(1); // Confirmação de sucesso para o cliente
                } else {
                    writer.writeInt(0); // Indica que a operação de remoção não foi bem-sucedida
                }
            }

            // Método para enviar informações via multicast
            private void EnviarInformativos(DataInputStream reader, DataOutputStream writer) throws IOException, EOFException {
                // Lê as informações do cliente
                String info = reader.readUTF();

                // Converte as informações em bytes e envia via multicast
                byte[] messageBytes = info.getBytes();
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, group, this.multicastServer.getLocalPort());
                this.multicastServer.send(packet);
            }
        }
    }