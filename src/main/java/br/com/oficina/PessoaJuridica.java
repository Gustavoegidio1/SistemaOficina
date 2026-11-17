
package br.com.oficina;

public class PessoaJuridica extends Cliente {

    private String cnpj;
    private String razaoSocial;
    private String inscricaoEstadual;

    public PessoaJuridica(
            int id,
            String nome,
            String endereco,
            String cnpj,
            String razaoSocial
    ) {
        super(id, nome, endereco);

        this.cnpj = ValidacaoCliente.validarCnpj(cnpj);
        this.razaoSocial = validarRazaoSocial(razaoSocial);
    }

    public PessoaJuridica(
            int id,
            String nome,
            String endereco,
            String cnpj,
            String razaoSocial,
            String inscricaoEstadual,
            String email,
            String telefone,
            boolean carregarDoBanco
    ) {
        super(id, nome, endereco, email, telefone, true);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }


    public void setInscricaoEstadual(String inscricaoEstadual) {

        if (inscricaoEstadual == null || inscricaoEstadual.isBlank()) {
            this.inscricaoEstadual = null;
            return;
        }

        String valor = inscricaoEstadual.trim();

        if (!valor.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "A inscrição estadual deve conter apenas números."
            );
        }

        if (valor.length() > 20) {
            throw new IllegalArgumentException(
                    "A inscrição estadual deve possuir no máximo 20 caracteres."
            );
        }

        this.inscricaoEstadual = valor;
    }


    public void alterarCnpj(String novoCnpj) {
        this.cnpj = ValidacaoCliente.validarCnpj(novoCnpj);
    }

    public void alterarRazaoSocial(String novaRazaoSocial) {
        this.razaoSocial = validarRazaoSocial(novaRazaoSocial);
    }

    private String validarRazaoSocial(String razaoSocial) {

        if (razaoSocial == null || razaoSocial.isBlank()) {
            throw new IllegalArgumentException(
                    "A razão social é obrigatória."
            );
        }

        String valor = razaoSocial.trim();

        if (valor.length() > 150) {
            throw new IllegalArgumentException(
                    "A razão social deve possuir no máximo 150 caracteres."
            );
        }

        return valor;
    }
}
