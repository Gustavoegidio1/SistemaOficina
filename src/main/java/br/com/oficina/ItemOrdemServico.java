package br.com.oficina;

public abstract class ItemOrdemServico {
    private int id;
    private String nome;
    private int quantidade;

    public ItemOrdemServico(String nome, int quantidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        this.nome = nome;
        this.quantidade = quantidade;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID é inválido.");
        }

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public abstract double valorTotal();

    public abstract void alterarValor(double novoValor);

    public void alterarNome(String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }

        this.nome = novoNome;
    }

    public void alterarQuantidade(int novaQuantidade) {
        if(novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        this.quantidade = novaQuantidade;
    }

}
