package questao4;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String SERVER_ADDRESS = "localhost";
    private static final String UDP_SERVER_ADDRESS = "224.0.0.1";
    private static final int SERVER_PORT_TCP = 12345;
    private static final int SERVER_PORT_UDP = 1357;
    private static MulticastSocket multicastClient;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT_TCP);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            // Solicita o CPF do cliente
            System.out.println("Informe seu CPF: ");
            Scanner sc = new Scanner(System.in);
            String cpf = sc.nextLine();
            output.writeUTF(cpf);

            if (cpf.equals("admin")) {
                administrador(input, output);
            } else {
                multicastClient = new MulticastSocket(SERVER_PORT_UDP);
                InetAddress group = InetAddress.getByName(UDP_SERVER_ADDRESS);
                multicastClient.joinGroup(group);
                eleitor(input, output);
                receberInformativos();
            }
            sc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void eleitor(DataInputStream input, DataOutputStream output) throws IOException {
        Scanner sc = new Scanner(System.in);
        
        // Solicita a lista de candidatos
        LerCandidatos(input);
        
        // Implementa a lógica de votação
        System.out.println("Vote em um candidato informando o número dele: ");
        int numeroCandidato = sc.nextInt();
        
        // Envie o voto para o servidor
        output.writeInt(numeroCandidato);
        sc.close();
        
        // Recebe uma confirmação do servidor
        int votado = input.readInt();

        if (votado == 1) {
            System.out.println("Voto depositado.");
        } else if (votado == 0) {
            System.out.println("Candidato não encontrado.");
        } else {
            System.out.println("Você já votou!");
        }
    }

    private static void administrador(DataInputStream input, DataOutputStream output) throws IOException {
        boolean votingOpen = true;
        Scanner sc = new Scanner(System.in);

        while (votingOpen) {
            System.out.println("Informe o que deseja fazer (o número): ");
            System.out.println("1 - Mostrar lista de candidatos.");
            System.out.println("2 - Inserir candidato.");
            System.out.println("3 - Remover candidato.");
            System.out.println("4 - Enviar informativo.");
            System.out.println("5 - Sair da aplicação.");
            String entrada = sc.nextLine();
            int value = Integer.parseInt(entrada);

            if (value == 1) {
                // Solicita a lista de candidatos ao servidor
                output.writeInt(value);
                LerCandidatos(input);
            } else if (value == 2) {
                // Solicita a inserção de um novo candidato
                output.writeInt(value);
                InserirCandidato(input, output, sc);
            } else if (value == 3) {
                // Solicita a remoção de um candidato
                output.writeInt(value);
                RemoverCandidato(input, output, sc);
            } else if (value == 4) {
                // Solicita o envio de informativos
                output.writeInt(value);
                EnviarInformativos(output, sc);
            } else if (value == 5) {
                // Encerra a aplicação
                output.writeInt(value);
                votingOpen = false;
            } else {
                System.out.println("Ação inválida.");
            }
        }
        sc.close();
    }

    private static void RemoverCandidato(DataInputStream input, DataOutputStream output, Scanner sc) throws IOException {
        System.out.println("Digite o número correspondente ao candidato a ser removido: ");
        
        // Solicita a lista de candidatos ao servidor
        LerCandidatos(input); 
        
        String entrada = sc.nextLine();
        int valor = Integer.parseInt(entrada);
        
        // Solicita a remoção do candidato com o número fornecido
        output.writeInt(valor);
        
        // Recebe uma confirmação do servidor
        int bool = input.readInt();
        
        if (bool == 1) {
            System.out.println("Candidato removido.");
        } else {
            System.out.println("Candidato não encontrado.");
        }
    }

    private static void InserirCandidato(DataInputStream input, DataOutputStream output, Scanner sc) throws IOException {
        System.out.println("Digite o nome do novo candidato: ");
        String nome = sc.nextLine();
        
        // Envia o nome do novo candidato ao servidor
        output.writeUTF(nome);
        
        // Recebe uma confirmação do servidor
        int bool = input.readInt();
        
        if (bool == 1) {
            System.out.println("Candidato " + nome + " inserido.");
        }
    }

    private static void LerCandidatos(DataInputStream input) throws IOException {
        // Recebe a lista de candidatos do servidor
        int quantidadeCandidatos = input.readInt();
        
        if (quantidadeCandidatos > 0) {
            String[] candidatos = new String[quantidadeCandidatos];
            
            // Recebe e armazena os nomes dos candidatos
            for (int i = 0; i < quantidadeCandidatos; i += 1) {
                String candidato = input.readUTF();
                candidatos[i] = candidato;
            }
            
            // Exibe a lista de candidatos
            System.out.println("Lista de candidatos: ");
            int i = 1;
            for (String candidato : candidatos) {
                System.out.println(i + "-" + candidato);
                i += 1;
            }
            i = 0;
        } else {
            System.out.println("Não há candidatos.");
        }
    }

    private static void EnviarInformativos(DataOutputStream output, Scanner sc) throws IOException {
        System.out.println("Informe o informativo a ser enviado: ");
        String info = sc.nextLine();
        
        // Envia o informativo ao servidor
        output.writeUTF(info);
    }

    private static void receberInformativos() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastClient.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                if (message.equals("end")) {
                    // Se o servidor sinalizar o fim dos informativos, encerra o loop
                    break;
                }
                System.out.println("Recebido: " + message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
