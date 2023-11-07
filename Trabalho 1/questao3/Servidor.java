package questao;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        final int PORTA = 9876;

        ServerSocket serverSocket = new ServerSocket(PORTA);
        Controle controle = new Controle();

        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        Mensagem mensagem = null;
        Mensagem reply = null;
        Socket socket = null;

        try {

            System.out.println("Servidor iniciado na porta " + PORTA);
            socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());

            while (true) {
                // Desempacotando a mensagem recebida sobre qual operação o cliente deseja
                ois = new ObjectInputStream(socket.getInputStream());
                mensagem = (Mensagem) ois.readObject();

                System.out.println("Mensagem recebida: " + mensagem.getConteudo());

                // Empacotando a mensagem de resposta do servidor a depender da operação
                // Caso o cliente deseje Trocar um produto
                if (mensagem.getConteudo().equals("troca")) {
                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem(
                            "Menu de Troca:");
                    oos.writeObject(reply);

                    // Desempacotando a mensagem recebida
                    // Conteudo: tipoProduto, nomeProdutoAntigo, nomeProdutoNovo, precoProdutoNovo,
                    // autorProdutoNovo, numPaginasProdutoNovo
                    // Que serão agrupadas em um array de Strings:
                    // info1, info2, ...
                    ois = new ObjectInputStream(socket.getInputStream());
                    mensagem = (Mensagem) ois.readObject();

                    // Qual o tipo do produto?
                    String[] info = mensagem.getConteudo().split(", ");
                    String tipoProduto = info[0];

                    for (int i = 0; i < info.length; i++) {
                        System.out.println(info[i]);
                    }

                    // 1 é livro
                    if (tipoProduto.equals("1")) {
                        // Removendo o livro antigo

                        String nomeLivroAntigo = info[1];
                        String nomeLivroNovo = info[2];
                        double precoLivroNovo = Double.parseDouble(info[3]);
                        String autorLivroNovo = info[4];
                        int numPaginasLivroNovo = Integer.parseInt(info[5]);

                        controle.trocarProdutoLivro(nomeLivroAntigo, nomeLivroNovo, precoLivroNovo, autorLivroNovo,
                                numPaginasLivroNovo);

                        System.out.println("Livro trocado com sucesso");

                        // 2 é ebook
                    } else if (tipoProduto.equals("2")) {
                        // Removendo o ebook antigo

                        String nomeEbookAntigo = info[1];
                        String nomeEbookNovo = info[2];
                        double precoEbookNovo = Double.parseDouble(info[3]);
                        String autorEbookNovo = info[4];
                        double tamanhoArquivoMbEbookNovo = Double.parseDouble(info[5]);

                        controle.trocarProdutoEbook(nomeEbookAntigo, nomeEbookNovo, precoEbookNovo, autorEbookNovo,
                                tamanhoArquivoMbEbookNovo);

                        // 3 é apostila
                    } else if (tipoProduto.equals("3")) {
                        // Removendo a apostila antiga

                        String nomeApostilaAntiga = info[1];
                        String nomeApostilaNova = info[2];
                        double precoApostilaNova = Double.parseDouble(info[3]);
                        String materiaApostilaNova = info[4];
                        int numPaginasApostilaNova = Integer.parseInt(info[5]);

                        controle.trocarProdutoApostila(nomeApostilaAntiga, nomeApostilaNova, precoApostilaNova,
                                materiaApostilaNova, numPaginasApostilaNova);

                    }

                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem("Produto trocado com sucesso!");
                    oos.writeObject(reply);

                    // Caso o cliente deseje cadastrar um novo produto
                } else if (mensagem.getConteudo().equals("cadastro")) {
                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem(
                            "Menu de Cadastro:");
                    oos.writeObject(reply);

                    // Desempacotando a mensagem recebida
                    ois = new ObjectInputStream(socket.getInputStream());
                    mensagem = (Mensagem) ois.readObject();

                    Produto produto = mensagem.getProduto();

                    // qual o tipo do produto
                    if (produto instanceof Livro) {
                        Livro livro = (Livro) produto;
                        controle.addLivro(livro.nome, livro.preco, livro.autor, livro.numPaginas);
                    } else if (produto instanceof Ebook) {
                        Ebook ebook = (Ebook) produto;
                        controle.addEbook(ebook.nome, ebook.preco, ebook.autor, ebook.tamanhoArquivoMb);
                    } else if (produto instanceof Apostila) {
                        Apostila apostila = (Apostila) produto;
                        controle.addApostila(apostila.nome, apostila.preco, apostila.materia, apostila.numPaginas);
                    }

                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem("Produto cadastrado com sucesso!");
                    oos.writeObject(reply);

                    // Caso o cliente deseje remover um produto
                } else if (mensagem.getConteudo().equals("remocao")) {
                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem(
                            "Menu de Remoção:");
                    oos.writeObject(reply);

                    // Desempacotando a mensagem recebida
                    ois = new ObjectInputStream(socket.getInputStream());
                    mensagem = (Mensagem) ois.readObject();

                    // Removendo o produto
                    String[] info = mensagem.getConteudo().split(", ");
                    String tipoProduto = info[0];
                    String nomeProduto = info[1];

                    if (tipoProduto.equals("1")) {
                        controle.removerLivroComDeslocamento(nomeProduto);
                    } else if (tipoProduto.equals("2")) {
                        controle.removerEbookComDeslocamento(nomeProduto);
                    } else if (tipoProduto.equals("3")) {
                        controle.removerApostilaComDeslocamento(nomeProduto);
                    }

                    // Empacotando a mensagem de resposta do servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem("Produto removido com sucesso!");
                    oos.writeObject(reply);

                } else if (mensagem.getConteudo().equals("listagem")) {
                    // Empacotando a mensagem de resposta do servidor

                    Produto[] produtos = controle.getProdutos();

                    oos = new ObjectOutputStream(socket.getOutputStream());
                    reply = new Mensagem(produtos);
                    oos.writeObject(reply);

                }
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        } finally {
            socket.close();
            serverSocket.close();
            oos.close();
            ois.close();
        }

    }
}
