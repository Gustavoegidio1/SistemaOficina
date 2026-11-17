
package br.com.oficina;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemOrdemServicoTest {

    // 1. Verifica o cálculo do valor total de um produto
    @Test
    void deveCalcularValorTotalDoProduto() {
        Produto produto = new Peca(
                "Pastilha de freio",
                2,
                "Bosch",
                150.0,
                "Freio"
        );

        assertEquals(300.0, produto.valorTotal(), 0.001);
    }

    // 2. Verifica o cálculo do valor total de um serviço
    @Test
    void deveCalcularValorTotalDoServico() {
        Servico servico = new Servico(
                "Alinhamento",
                2,
                100.0
        );

        assertEquals(200.0, servico.valorTotal(), 0.001);
    }

    // 3. Verifica a alteração do valor de venda de um produto
    @Test
    void deveAlterarValorDoProduto() {
        Produto produto = new Peca(
                "Pastilha",
                2,
                "Bosch",
                150.0,
                "Freio"
        );

        produto.alterarValor(200.0);

        assertEquals(400.0, produto.valorTotal(), 0.001);
    }

    // 4. Verifica a alteração da marca
    @Test
    void deveAlterarMarcaDoProduto() {
        Produto produto = new Peca(
                "Pastilha",
                1,
                "Bosch",
                150.0,
                "Freio"
        );

        produto.alterarMarca("Cobreq");

        assertEquals("Cobreq", produto.getMarca());
    }

    // 5. Verifica a rejeição de custo negativo
    @Test
    void naoDevePermitirCustoNegativo() {
        Produto produto = new Peca(
                "Pastilha",
                1,
                "Bosch",
                150.0,
                "Freio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> produto.setCusto(-50.0)
        );
    }

    // 6. Verifica a rejeição de valor de venda negativo
    @Test
    void naoDevePermitirValorDeVendaNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Peca(
                        "Pastilha",
                        1,
                        "Bosch",
                        -150.0,
                        "Freio"
                )
        );
    }

    // 7. Verifica a rejeição de quantidade negativa
    @Test
    void naoDevePermitirQuantidadeNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Servico(
                        "Alinhamento",
                        -1,
                        100.0
                )
        );
    }
}

