package questao4;

public class Voto {
    private String cpf_eleitor;
    private String cpf_candidato;

    public Voto(String cpf_eleitor, String cpf_candidato) {
        this.cpf_eleitor = cpf_eleitor;
        this.cpf_candidato = cpf_candidato;
    }

    public String getEleitor() {
        return cpf_eleitor;
    }

    public String getCandidato() {
        return cpf_candidato;
    }

    public Voto toProto() {
        Voto voto = new Voto(cpf_eleitor, cpf_candidato);
        return voto;
    }

    public static Voto fromProto(Voto protoVoto) {
        return new Voto(protoVoto.getEleitor(), protoVoto.getCandidato());
    }

}