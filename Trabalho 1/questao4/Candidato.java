package questao4;

public class Candidato {
    private String nome;
    private int votos = 0;
    private String percentagem;
    Candidato(String nome, int votos) {
        this.votos = votos;
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }

    public void addVoto(){
        this.votos +=1;
    }

    public int getVotos() {
        return votos;
    }
    public String getPercentagem(){
        return percentagem;
    }
    public void setPercentagem(String value){
        percentagem = value;
    }

    public Candidato toProto() {
        Candidato candidato = new Candidato(nome, votos);
        return candidato;
    }

    public static Candidato fromProto(Candidato protoCandidato) {
        return new Candidato(protoCandidato.getNome(), protoCandidato.getVotos());
    }
}
