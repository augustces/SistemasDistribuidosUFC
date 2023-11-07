package questao;

import java.io.Serializable;

class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String conteudo;
    private Produto produto;
    private Produto[] arrayProdutos;

    public Mensagem(String conteudo) {
        this.conteudo = conteudo;
    }

    public Mensagem(Produto produto) {
        this.produto = produto;
    }

    public Mensagem(Produto[] arrayProdutos) {
        this.arrayProdutos = arrayProdutos;
    }

    public String getConteudo() {
        return conteudo;
    }

    public Produto getProduto() {
        return produto;
    }

    public Produto[] getArrayProdutos() {
        return arrayProdutos;
    }

    public void setConteudo(String mensagem) {
        this.conteudo = mensagem;
    }
}
