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
}
    class Connection extends Thread {
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
            
            this.out.writeInt(pessoas.length);
			for (int i =0; i < pessoas.length; i+=1){
				this.out.writeInt(pessoas[i].getNome().getBytes("UTF-8").length);
				this.out.write(pessoas[i].getNome().getBytes());
				this.out.writeUTF(pessoas[i].getCpf());
				this.out.writeInt(pessoas[i].getIdade());
			}
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
