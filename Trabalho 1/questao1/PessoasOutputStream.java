package questao1;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class PessoasOutputStream extends OutputStream {
    private OutputStream op; //atributo para saída de dados
    private Pessoa[] pessoas; // atriuto para armazenar os objetos a serem enviados

    // Construtor sem parâmetros
    public PessoasOutputStream() {}

   // Construtor recebendo parâmetros (Vetor de pessoas e um OutputStream) e armazenando nos atributos
    public PessoasOutputStream(Pessoa[] pessoas, OutputStream os) {
        this.pessoas = pessoas;
        this.op = os;
    }

    // Método para escrever informações de Pessoa na saída via terminal
    public void writeSystem() {
        PrintStream opTemp = new PrintStream(this.op); // Cria um PrintStream temporario para escrever
 
        int qtdPessoa = this.pessoas.length; // armazena o número de pessoas no vetor
        opTemp.println("Quantidade de pessoas: " + qtdPessoa); // Envia a quantidade de pessoas
            
        // Envia os dados de cada pessoa em um vetor de Pessoas
        for (Pessoa pessoa : this.pessoas) {
            if (pessoa != null) {
                int tamanhoNomePessoa = pessoa.getNome().getBytes().length; //Pega os o tamanho do nome em bytes da pessoa e armazena
                String nome = pessoa.getNome(); // armazena o nome da pessoa
                String cpf = pessoa.getCpf(); // armazena o cpf da pessoa
                int idade = pessoa.getIdade(); // armazena a idade da pessoa

                // Escreve as informações da Pessoa na saída
                opTemp.println(" tamanhoNomePessoa: " + tamanhoNomePessoa + "\n" + " nomePessoa: " + nome + "\n" + " cpf: " + cpf + "\n" + " idade: " + idade);
            }
        }
    }

    public void writeFile(FileOutputStream fout){
        // destino um arquivo FileOutputStream
        try {
            // cria um arquivo
            System.out.println("Arquivo criado com sucesso!");
            // cria um buffer para escrever no arquivo
            BufferedOutputStream buffer = new BufferedOutputStream(fout);

            // envia quantidade de pessoas;
            int qtdpessoa = pessoas.length;
            buffer.write((qtdpessoa +"\n").getBytes());

            // Envia os dados de cada pessoa em um vetor de Pessoas para o arquivo fout
            for (Pessoa pessoa : pessoas) {
                if (pessoa != null) {
                    int tamanhoNomePessoa = pessoa.getNome().getBytes().length;
                    String nome = pessoa.getNome();
                    String cpf = pessoa.getCpf();
                    int idade = pessoa.getIdade();

                    buffer.write((tamanhoNomePessoa + "\n" +
                            nome + "\n" +
                            cpf + "\n" +
                            idade +"\n").getBytes());
                }
            }

            // fecha o buffer
            buffer.close();

            // mensagem de sucesso
           System.out.println("Arquivo escrito com sucesso!"); 

        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void writeTCP(){
        try{
            DataOutputStream dataOutputStream = new DataOutputStream(this.op);

            // Envia o número de pessoas
            int numeroDePessoas = pessoas.length;
            dataOutputStream.writeInt(numeroDePessoas);

            for (Pessoa pessoa : pessoas) {
                // Envie o número de bytes utilizados para o nome da pessoa
                int tamanhoNome = pessoa.getNome().getBytes("UTF-8").length;
                dataOutputStream.writeInt(tamanhoNome);

                // Envie o nome da pessoa
                dataOutputStream.write(pessoa.getNome().getBytes());

                // Envie o CPF e a idade
                dataOutputStream.writeUTF(pessoa.getCpf());
                dataOutputStream.writeInt(pessoa.getIdade());
            }
        }
        catch(IOException error){
           error.printStackTrace(); 
        }
        
    }

    public static void main (String args[]){
        try {
            // Exemplo de vetor de pessoas
            Pessoa pessoa1 = new Pessoa("Joao", "123456789", 30);
            Pessoa pessoa2 = new Pessoa("Maria", "987654321", 25);
            Pessoa[] pessoas = {pessoa1, pessoa2};

            // // Imprimir pessoas via terminal - ITEM D
            // PessoasOutputStream pessoaOutputStreamSys = new PessoasOutputStream(pessoas, System.out);
            // pessoaOutputStreamSys.writeSystem();
            // pessoaOutputStreamSys.close();

            // // Imprimir pessoas com File de destino - ITEM E
            // File arquivo = new File("dados_pessoas.dat");
            // FileOutputStream fileOutputStream = new FileOutputStream(arquivo);
            // PessoasOutputStream pessoasOutputStreamFile = new PessoasOutputStream(pessoas, fileOutputStream);
            // pessoasOutputStreamFile.writeFile(fileOutputStream);
            // pessoasOutputStreamFile.close();
           
            // Imprimir pessoas via servidor remoto TCP - ITEM F 
            String servidorRemoto = "172.25.250.237";
            int porta = 12345;
            Socket socket = new Socket(servidorRemoto, porta); 
            PessoasOutputStream pessoasOutputStream = new PessoasOutputStream(pessoas, socket.getOutputStream());
            pessoasOutputStream.writeTCP();
            socket.close();
            pessoasOutputStream.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(int b) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "Estamos sobrescrevendo o método toString()!\n"
                + " PessoasOutputStream [ \n"
                + " getClass()=" + getClass() + ",\n"
                + " hashCode()=" + hashCode() + ",\n"
                + " toString()=" + super.toString() + "]";
    }
}
