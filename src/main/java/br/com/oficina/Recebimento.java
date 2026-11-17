
package br.com.oficina;

import java.time.LocalDate;

public class Recebimento {

    private int id;
    private double valor;
    private LocalDate dataPrevista;
    private LocalDate dataRecebimento;
    private String formaPagamento;

    public Recebimento(
            int id,
            double valor,
            LocalDate dataPrevista,
            LocalDate dataRecebimento,
            String formaPagamento) {

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        if (dataPrevista == null) {
            throw new IllegalArgumentException("A data prevista é obrigatória.");
        }

        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new IllegalArgumentException("A forma de pagamento é obrigatória.");
        }

        if (dataRecebimento != null && dataRecebimento.isBefore(dataPrevista)) {
            // Não impede pagamento antecipado.
        }

        this.id = id;
        this.valor = valor;
        this.dataPrevista = dataPrevista;
        this.dataRecebimento = dataRecebimento;
        this.formaPagamento = formaPagamento;
    }

    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public LocalDate getDataRecebimento() {
        return dataRecebimento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public boolean isPago() {
        return dataRecebimento != null;
    }

    public boolean isVencido() {
        return !isPago() && dataPrevista.isBefore(LocalDate.now());
    }

    public String getStatus() {
        if (isPago()) return "Pago";
        if (isVencido()) return "Vencido";
        return "Pendente";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void alterarValor(double novoValor) {
        if (novoValor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        this.valor = novoValor;
    }

    public void alterarDataPrevista(LocalDate novaData) {
        if (novaData == null) {
            throw new IllegalArgumentException("A data prevista é obrigatória.");
        }
        this.dataPrevista = novaData;
    }

    public void alterarDataRecebimento(LocalDate novaData) {
        this.dataRecebimento = novaData;
    }

    public void alterarFormaPagamento(String novaFormaPagamento) {
        if (novaFormaPagamento == null || novaFormaPagamento.isBlank()) {
            throw new IllegalArgumentException("A forma de pagamento é obrigatória.");
        }
        this.formaPagamento = novaFormaPagamento;
    }
}
