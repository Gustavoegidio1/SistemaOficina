
package br.com.oficina;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {

    private OrdemServico criarOS() {
        Cliente cliente = new PessoaFisica(
                1,
                "João da Silva",
                "Rua das Flores",
                "12345678900",
                "joao@gmail.com",
                "41999999999",
                true
        );

        return new OrdemServico(
                1,
                cliente,
                LocalDate.now(),
                "Honda Civic",
                "ABC1234",
                50000
        );
    }

    // 1. Deve criar uma ordem de serviço corretamente
    @Test
    void deveCriarOrdemServico() {
        OrdemServico os = criarOS();

        assertNotNull(os);
        assertEquals(1, os.getId());
        assertEquals("Honda Civic", os.getModeloVeiculo());
        assertEquals("ABC1234", os.getPlaca());
    }

    // 2. Deve adicionar um serviço à ordem
    @Test
    void deveAdicionarServico() {
        OrdemServico os = criarOS();

        Servico servico = new Servico("Alinhamento", 1, 200.0);
        os.adicionarServico(servico);

        assertEquals(200.0, os.getValorTotal(), 0.001);
    }

    // 3. Deve calcular o valor total da ordem
    @Test
    void deveCalcularValorTotal() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));
        os.adicionarServico(new Servico("Balanceamento", 1, 100.0));

        assertEquals(300.0, os.getValorTotal(), 0.001);
    }

    // 4. Deve calcular o saldo pendente
    @Test
    void deveCalcularSaldoPendente() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));

        assertEquals(200.0, os.getSaldoPendente(), 0.001);
    }

    // 5. Deve iniciar com status não pago
    @Test
    void deveIniciarComStatusNaoPago() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));

        assertEquals("Não pago", os.getStatusPagamento());
    }

    // 6. Deve registrar recebimento parcial
    @Test
    void deveRegistrarRecebimentoEAtualizarStatus() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));

        Recebimento recebimento = new Recebimento(
                1,
                100.0,
                LocalDate.now(),
                LocalDate.now(),
                "Pix"
        );

        os.adicionarRecebimento(recebimento);

        assertEquals(100.0, os.getTotalRecebido(), 0.001);
        assertEquals(100.0, os.getSaldoPendente(), 0.001);
        assertEquals("Parcialmente pago", os.getStatusPagamento());
    }

    // 7. Deve identificar pagamento completo
    @Test
    void deveIdentificarPagamentoCompleto() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));

        Recebimento recebimento = new Recebimento(
                1,
                200.0,
                LocalDate.now(),
                LocalDate.now(),
                "Pix"
        );

        os.adicionarRecebimento(recebimento);

        assertEquals(200.0, os.getTotalRecebido(), 0.001);
        assertEquals(0.0, os.getSaldoPendente(), 0.001);
        assertEquals("Pago", os.getStatusPagamento());
    }

    // 8. Deve contabilizar o recebimento registrado
    @Test
    void deveContabilizarRecebimentoAcimaDoTotal() {
        OrdemServico os = criarOS();

        os.adicionarServico(new Servico("Alinhamento", 1, 200.0));

        Recebimento recebimento = new Recebimento(
                1,
                250.0,
                LocalDate.now(),
                LocalDate.now(),
                "Pix"
        );

        os.adicionarRecebimento(recebimento);

        assertEquals(250.0, os.getTotalRecebido(), 0.001);
    }

    // 9. Não deve permitir recebimento com valor negativo
    @Test
    void naoDevePermitirRecebimentoNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Recebimento(
                        1,
                        -50.0,
                        LocalDate.now(),
                        LocalDate.now(),
                        "Pix"
                )
        );
    }

    // 10. Não deve permitir recebimento nulo
    @Test
    void naoDevePermitirRecebimentoNulo() {
        OrdemServico os = criarOS();

        assertThrows(
                IllegalArgumentException.class,
                () -> os.adicionarRecebimento(null)
        );
    }
}
