
package br.com.oficina;

public abstract class Cliente {

    private int id;
    private String nome;
    private String endereco;
    private String email;
    private String telefone;

    public Cliente(int id, String nome, String endereco) {

        if (id < 0) {
            throw new IllegalArgumentException("O id é inválido.");
        }

        this.id = id;
        this.nome = ValidacaoCliente.validarNome(nome);
        this.endereco = ValidacaoCliente.validarEndereco(endereco);
    }

    protected Cliente(
            int id,
            String nome,
            String endereco,
            String email,
            String telefone,
            boolean carregarDoBanco
    ) {
        if (id < 0) {
            throw new IllegalArgumentException("O id é inválido.");
        }

        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setNome(String nome) {
        this.nome = ValidacaoCliente.validarNome(nome);
    }

    public void setEndereco(String endereco) {
        this.endereco = ValidacaoCliente.validarEndereco(endereco);
    }

    public void setEmail(String email) {
        this.email = ValidacaoCliente.validarEmail(email);
    }

    public void setTelefone(String telefone) {
        this.telefone = ValidacaoCliente.validarTelefone(telefone);
    }

    public void alterarNome(String novoNome) {
        this.nome = ValidacaoCliente.validarNome(novoNome);
    }

    public void alterarEndereco(String novoEndereco) {
        this.endereco = ValidacaoCliente.validarEndereco(novoEndereco);
    }
}
