package br.com.oficina;

public class Servico extends ItemOrdemServico{
    private double valorCobrado;

    public Servico(String nome, int quantidade, double valorCobrado) {
        super(nome, quantidade);

        if(!Double.isFinite(valorCobrado) || valorCobrado < 0) {
            throw new IllegalArgumentException("O valor cobrado é inválido.");
        }

        this.valorCobrado = valorCobrado;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }

    public void alterarValor(double novoValorCobrado) {
        if(!Double.isFinite(novoValorCobrado) || novoValorCobrado < 0) {
            throw new IllegalArgumentException("O valor cobrado é inválido.");
        }

        this.valorCobrado = novoValorCobrado;
    }

    @Override
    public double valorTotal() {
        return valorCobrado * getQuantidade();
    }
}
