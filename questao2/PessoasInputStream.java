import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PessoasInputStream extends InputStream {
    private InputStream inputStream;

    public PessoasInputStream(InputStream sourceInputStream) throws IOException {
        this.inputStream = sourceInputStream;
    }

    public void readFIle() throws IOException, ClassNotFoundException {
        // Leia o número de pessoas
        // Scanner sc = new Scanner(this.inputStream);
        Path filePath = Paths.get("./dados_pessoas.dat");

        Charset charset = StandardCharsets.UTF_8;

        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            int quantidadePessoa = Integer.parseInt(lines.get(0));
            System.out.println("Quantidade de pessoas: " + quantidadePessoa);
            for (int i =1; i < lines.size(); i+=1) {
                System.out.println("Tamanho do nome da pessoa: " + lines.get(i));
                i += 1;
                System.out.println("Nome da pessoa: " + lines.get(i));
                i+=1;
                System.out.println("CPF da pessoa: " + lines.get(i));
                i+=1;
                System.out.println("Idade da pessoa: " + lines.get(i));
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
        
    }

    public List<Pessoa> readSystem() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(this.inputStream);
        List<Pessoa> pessoas = new ArrayList<>();
        System.out.println("Qual a quantidade de pessoas?");
        int quantidadePessoa = sc.nextInt();
        for (int i = 0; i < quantidadePessoa; i += 1) {
            // System.out.println("Qual o tamanho do nome da pessoa?");
            // int tamanhoNome = this.inputStream.readInt();
            System.out.println("Qual o nome da pessoa?");
            String nome = sc.next();
            System.out.println("Qual o CPF da pessoa?");
            String cpf = sc.next();
            System.out.println("Qual a idade da pessoa?");
            int idade = sc.nextInt();
            Pessoa pessoa = new Pessoa(nome, cpf, idade);
            pessoas.add(pessoa);
        }
        sc.close();
        return pessoas;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public static void main(String[] args) {
        try {
            List<Pessoa> pessoas = new ArrayList<>();
            // Input via Terminal
            // PessoasInputStream pessoasInputStreamSys = new PessoasInputStream(System.in);
            // pessoas = pessoasInputStreamSys.readSystem();
            // for (Pessoa p : pessoas) {
            // System.out.println(p.toString());
            // }
            // pessoasInputStreamSys.close();

            // Input via File
            InputStream fileInputStream = new FileInputStream("dados_pessoas.dat");
            PessoasInputStream pessoasInputStreamFile = new PessoasInputStream(fileInputStream); // objeto de leitura

            pessoasInputStreamFile.readFIle();
            pessoasInputStreamFile.close();

            

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
