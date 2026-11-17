package br.com.oficina;

public abstract class Produto extends ItemOrdemServico{
    private String marca;
    private double valorVendaUnitario;
    private Double custo;

    public Produto(String nome, int quantidade, String marca, double valorVendaUnitario) {
        super(nome, quantidade);
        if(marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("A marca é inválida.");
        }
        if(!Double.isFinite(valorVendaUnitario) || valorVendaUnitario < 0) {
            throw new IllegalArgumentException("O valor de venda é inválido.");
        }

        this.marca = marca;
        this.valorVendaUnitario = valorVendaUnitario;
    }

    @Override
    public double valorTotal() {
        return valorVendaUnitario * getQuantidade();
    }

    public void setCusto(double custo) {
        if(!Double.isFinite(custo) || custo < 0) {
            throw new IllegalArgumentException("O custo é inválido.");
        }

        this.custo = custo;
    }

    public void limparCusto() {
        this.custo = null;
    }

    public String getMarca() {
        return marca;
    }

    public double getValorVendaUnitario() {
        return valorVendaUnitario;
    }

    public Double getCusto() {
        return custo;
    }

    @Override
    public void alterarValor(double novoValorVendaUnitario) {
        if(!Double.isFinite(novoValorVendaUnitario) || novoValorVendaUnitario < 0) {
            throw new IllegalArgumentException("O valor de venda é inválido.");
        }

        this.valorVendaUnitario = novoValorVendaUnitario;
    }

    public void alterarMarca(String novaMarca) {
        if(novaMarca == null || novaMarca.isBlank()) {
            throw new IllegalArgumentException("A marca é inválida.");
        }

        this.marca = novaMarca;
    }
}
