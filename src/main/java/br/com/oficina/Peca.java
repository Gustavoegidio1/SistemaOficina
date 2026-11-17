package br.com.oficina;

public class Peca extends Produto{
    private String categoria;

    public Peca(String nome, int quantidade, String marca, double valorVenda, String categoria) {
        super(nome, quantidade, marca, valorVenda);
        if(categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("A categoria é inválida.");
        }

        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void alterarCategoria(String novaCategoria) {
        if(novaCategoria == null || novaCategoria.isBlank()) {
            throw new IllegalArgumentException("A categoria é inválida.");
        }
        this.categoria = novaCategoria;
    }
}
