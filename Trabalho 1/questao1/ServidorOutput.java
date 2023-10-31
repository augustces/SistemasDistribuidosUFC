package questao1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorOutput {

    public static void main(String[] args) {
        try 
        {
            System.out.println("Servidor iniciado");
            ServerSocket serverSocket = new ServerSocket(12345); // Porta do servidor

            System.out.println("Aguardando conexão do cliente...");
            Socket clientSocket = serverSocket.accept();
			System.out.println(clientSocket.getInetAddress());
			System.out.println("conexão estabelecida");
			Connection c = new Connection(clientSocket);
            serverSocket.close();
            // Array de pessoas a ser enviado
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Connection extends Thread {
        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;
    public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		try { // an echo server
            int quantidadePessoa = in.readInt();
            System.out.println("Quantidade de pessoas: " + quantidadePessoa);
            for (int i =0; i < quantidadePessoa; i+=1){
                int tamanhoNome = in.readInt();
                System.out.println("Tamanho do nome: " + tamanhoNome);
                byte[] nomeBytes = in.readNBytes(tamanhoNome);
                String nome = new String(nomeBytes);
                System.out.println("Nome: " + nome);
                String cpf = in.readUTF();
                System.out.println("CPF: " + cpf);
                int idade = in.readInt();
                System.out.println("Idade: " + idade);
                
            }
            this.clientSocket.close();
			
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				/* close failed */}
		}
    	}
    	}
}
