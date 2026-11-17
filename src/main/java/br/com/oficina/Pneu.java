package br.com.oficina;

public class Pneu extends Produto{
    private String modelo;
    private int largura;
    private int perfil;
    private int aro;

    public Pneu(String nome, int quantidade, String marca, double valorVenda, String modelo, int largura, int perfil, int aro) {
        super(nome, quantidade, marca, valorVenda);
        if(modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("O modelo é inválido");
        }
        if(largura <= 0) {
            throw new IllegalArgumentException("A largura é inválida.");
        }
        if(perfil <= 0) {
            throw new IllegalArgumentException("O perfil é inválido.");
        }
        if(aro <= 0) {
            throw new IllegalArgumentException("O aro é inválido.");
        }
        this.modelo = modelo;
        this.largura = largura;
        this.perfil = perfil;
        this.aro = aro;
    }

    public String getModelo() {
        return modelo;
    }

    public int getLargura() {
        return largura;
    }

    public int getPerfil() {
        return perfil;
    }

    public int getAro() {
        return aro;
    }

    public void alterarModelo(String novoModelo) {
        if(novoModelo == null || novoModelo.isBlank()) {
            throw new IllegalArgumentException("O modelo é inválido");
        }

        this.modelo = novoModelo;
    }

    public void alterarLargura(int novaLargura) {
        if(novaLargura <= 0) {
            throw new IllegalArgumentException("A largura é inválida.");
        }
        this.largura = novaLargura;
    }

    public void alterarPerfil(int novoPerfil) {
        if(novoPerfil <= 0) {
            throw new IllegalArgumentException("O perfil é inválido.");
        }

        this.perfil = novoPerfil;
    }

    public void alterarAro(int novoAro) {
        if(novoAro <= 0) {
            throw new IllegalArgumentException("O aro é inválido.");
        }
        this.aro = novoAro;
    }
}
