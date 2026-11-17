package br.com.oficina;

public class PessoaFisica extends Cliente{
    private String cpf;

    public PessoaFisica(int id, String nome, String endereco, String cpf) {
        super(id, nome, endereco);
        this.cpf = ValidacaoCliente.validarCpf(cpf);
    }

    public PessoaFisica(int id, String nome, String endereco, String cpf, String email, String telefone, boolean carregarDoBanco) {
        super(id, nome, endereco, email, telefone, true);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void alterarCpf(String novoCpf) {
        this.cpf = ValidacaoCliente.validarCpf(novoCpf);
    }

}
