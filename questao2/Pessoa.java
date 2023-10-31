public class Pessoa {

    private String nome;
    private String cpf;
    private int idade;

    public Pessoa(String nome, String cpf, int idade) {
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
    }

    public Pessoa() {
        this.nome = "";
        this.cpf = "";
        this.idade = 0;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCpf() {
        return this.cpf;
    }

    public int getIdade() {
        return this.idade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        if (cpf.length() == 11) {
            this.cpf = cpf;
        } else {
            System.out.println("CPF inválido!");
        }
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String toString(){
        String out = "NomeDaPessoa: " + this.getNome() + "\n";
        out += "CPF: " + this.getCpf() + "\n";
        out += "IdadeDaPessoa: " + this.getIdade() + "\n";
        return out;
    }

}
