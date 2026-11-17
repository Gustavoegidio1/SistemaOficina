
package br.com.oficina;

public class ValidacaoCliente {

    // CPF
    public static String validarCpf(String cpf) {

        String documento = normalizarDocumento(cpf);

        if (documento.length() != 11) {
            throw new IllegalArgumentException(
                    "O CPF deve possuir exatamente 11 dígitos."
            );
        }

        if (documento.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("O CPF informado é inválido.");
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(documento.charAt(i)) * (10 - i);
        }

        int primeiroDigito = 11 - (soma % 11);

        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        if (primeiroDigito != Character.getNumericValue(documento.charAt(9))) {
            throw new IllegalArgumentException("O CPF informado é inválido.");
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(documento.charAt(i)) * (11 - i);
        }

        int segundoDigito = 11 - (soma % 11);

        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        if (segundoDigito != Character.getNumericValue(documento.charAt(10))) {
            throw new IllegalArgumentException("O CPF informado é inválido.");
        }

        return documento;
    }

    // CNPJ
    public static String validarCnpj(String cnpj) {

        String documento = normalizarDocumento(cnpj);

        if (documento.length() != 14) {
            throw new IllegalArgumentException(
                    "O CNPJ deve possuir exatamente 14 dígitos."
            );
        }

        if (documento.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("O CNPJ informado é inválido.");
        }

        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;

        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(documento.charAt(i))
                    * pesosPrimeiro[i];
        }

        int primeiroDigito = soma % 11;
        primeiroDigito = primeiroDigito < 2 ? 0 : 11 - primeiroDigito;

        if (primeiroDigito != Character.getNumericValue(documento.charAt(12))) {
            throw new IllegalArgumentException("O CNPJ informado é inválido.");
        }

        soma = 0;

        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(documento.charAt(i))
                    * pesosSegundo[i];
        }

        int segundoDigito = soma % 11;
        segundoDigito = segundoDigito < 2 ? 0 : 11 - segundoDigito;

        if (segundoDigito != Character.getNumericValue(documento.charAt(13))) {
            throw new IllegalArgumentException("O CNPJ informado é inválido.");
        }

        return documento;
    }

    // E-mail
    public static String validarEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        String valor = email.trim();

        if (valor.length() > 254 ||
                !valor.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException("O e-mail informado é inválido.");
        }

        return valor;
    }

    // Telefone
    public static String validarTelefone(String telefone) {

        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        if (!telefone.matches("[0-9()\\-+\\s]+")) {
            throw new IllegalArgumentException(
                    "O telefone deve conter apenas números."
            );
        }

        String numero = telefone.replaceAll("\\D", "");

        if (numero.length() != 10 && numero.length() != 11) {
            throw new IllegalArgumentException(
                    "O telefone deve possuir 10 ou 11 dígitos, incluindo o DDD."
            );
        }

        return numero;
    }

    // Nome
    public static String validarNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        String valor = nome.trim();

        if (valor.length() > 150) {
            throw new IllegalArgumentException(
                    "O nome deve possuir no máximo 150 caracteres."
            );
        }

        return valor;
    }

    // Endereço
    public static String validarEndereco(String endereco) {

        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("O endereço é obrigatório.");
        }

        String valor = endereco.trim();

        if (valor.length() > 255) {
            throw new IllegalArgumentException(
                    "O endereço deve possuir no máximo 255 caracteres."
            );
        }

        return valor;
    }

    // Normalização de documentos
    private static String normalizarDocumento(String documento) {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("O documento é obrigatório.");
        }

        if (!documento.matches("[0-9./\\-\\s]+")) {
            throw new IllegalArgumentException(
                    "O documento possui caracteres inválidos."
            );
        }

        return documento.replaceAll("[./\\-\\s]", "");
    }
}
