import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorInput {

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
            // Exemplo de vetor de pessoas
            Pessoa pessoa1 = new Pessoa("Joao", "123456789", 30);
            Pessoa pessoa2 = new Pessoa("Maria", "987654321", 25);
            Pessoa[] pessoas = {pessoa1, pessoa2};
            
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
