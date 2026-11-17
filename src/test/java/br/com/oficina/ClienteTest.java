
package br.com.oficina;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    // 1. Verifica a criação de pessoa física
    @Test
    void deveCriarPessoaFisica() {
        assertDoesNotThrow(() ->
                new PessoaFisica(
                        1,
                        "João Silva",
                        "Rua das Flores",
                        "52998224725"
                )
        );
    }

    // 2. Verifica a criação de pessoa jurídica
    @Test
    void deveCriarPessoaJuridica() {
        assertDoesNotThrow(() ->
                new PessoaJuridica(
                        2,
                        "Oficina Central",
                        "Avenida Brasil",
                        "11222333000181",
                        "Oficina Central Ltda."
                )
        );
    }

    // 3. Rejeita CPF vazio
    @Test
    void naoDevePermitirCpfVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PessoaFisica(
                        1, "João", "Rua das Flores", ""
                )
        );
    }

    // 4. Rejeita CNPJ vazio
    @Test
    void naoDevePermitirCnpjVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PessoaJuridica(
                        1,
                        "Oficina",
                        "Avenida Brasil",
                        "",
                        "Oficina Ltda."
                )
        );
    }

    // 5. Rejeita nome vazio
    @Test
    void naoDevePermitirNomeVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PessoaFisica(
                        1, "", "Rua das Flores", "52998224725"
                )
        );
    }

    // 6. Rejeita endereço vazio
    @Test
    void naoDevePermitirEnderecoVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PessoaFisica(
                        1, "João", "", "52998224725"
                )
        );
    }

    // 7. Permite campos opcionais nulos
    @Test
    void devePermitirCamposOpcionaisNulos() {
        PessoaFisica cliente = new PessoaFisica(
                1, "João", "Rua das Flores", "52998224725"
        );

        PessoaJuridica empresa = new PessoaJuridica(
                2,
                "Oficina",
                "Avenida Brasil",
                "11222333000181",
                "Oficina Ltda."
        );

        assertDoesNotThrow(() -> {
            cliente.setEmail(null);
            cliente.setTelefone(null);
            empresa.setEmail(null);
            empresa.setTelefone(null);
            empresa.setInscricaoEstadual(null);
        });
    }

    // 8. Rejeita campos opcionais vazios
    @Test
    void deveTratarCamposOpcionaisVaziosComoNulos() {
        PessoaJuridica empresa = new PessoaJuridica(
                1,
                "Oficina",
                "Avenida Brasil",
                "11222333000181",
                "Oficina Ltda."
        );

        assertDoesNotThrow(() -> {
            empresa.setEmail("");
            empresa.setTelefone("");
            empresa.setInscricaoEstadual("");
        });

        assertNull(empresa.getEmail());
        assertNull(empresa.getTelefone());
        assertNull(empresa.getInscricaoEstadual());
    }
}