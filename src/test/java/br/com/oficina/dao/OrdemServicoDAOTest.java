
package br.com.oficina.dao;

import br.com.oficina.Cliente;
import br.com.oficina.OrdemServico;
import br.com.oficina.PessoaFisica;
import br.com.oficina.Servico;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OrdemServicoDAOTest {

    private final OrdemServicoDAO ordemDAO = new OrdemServicoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Test
    void testarRemoverItem() throws SQLException {

        int idCliente = 0;
        int idOrdem = 0;

        try {

            // 1. Cadastra um cliente para o teste
            PessoaFisica pessoa = new PessoaFisica(
                    0,
                    "Cliente teste OS",
                    "Rua Teste",
                    gerarCpfValido()
            );

            idCliente = clienteDAO.cadastrarCliente(pessoa);

            Cliente cliente = clienteDAO.buscarClientePorId(idCliente);

            assertNotNull(cliente);

            // 2. Cadastra uma ordem de serviço
            OrdemServico ordem = new OrdemServico(
                    0,
                    cliente,
                    LocalDate.now(),
                    "Veículo teste",
                    "ABC1D23",
                    1000
            );

            idOrdem = ordemDAO.cadastrarOrdemServico(ordem);

            // 3. Adiciona um serviço à ordem
            Servico servico = new Servico(
                    "Serviço de teste",
                    1,
                    100.0
            );

            int idItem = ordemDAO.adicionarItemOrdemServico(
                    ordem,
                    servico
            );

            // 4. Confirma que o item foi cadastrado
            OrdemServico ordemAntes = ordemDAO.buscarOrdemServicoPorId(idOrdem);

            assertNotNull(ordemAntes);
            assertNotNull(ordemAntes.buscarItemPorId(idItem));

            // 5. Remove o item
            boolean removido = ordemDAO.removerItemOrdemServico(
                    idOrdem,
                    idItem
            );

            assertTrue(removido);

            // 6. Confirma que o item não existe mais
            OrdemServico ordemAtualizada =
                    ordemDAO.buscarOrdemServicoPorId(idOrdem);

            assertNotNull(ordemAtualizada);
            assertNull(ordemAtualizada.buscarItemPorId(idItem));

        } finally {

            // Limpa os registros criados pelo teste
            if (idOrdem > 0) {
                ordemDAO.removerOrdemServico(idOrdem);
            }

            if (idCliente > 0) {
                clienteDAO.excluirCliente(idCliente);
            }
        }
    }

    private String gerarCpfValido() {

        long numero = Math.floorMod(
                System.nanoTime(),
                1_000_000_000L
        );

        String base = String.format("%09d", numero);

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += (base.charAt(i) - '0') * (10 - i);
        }

        int primeiroDigito = soma % 11 < 2
                ? 0
                : 11 - (soma % 11);

        soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += (base.charAt(i) - '0') * (11 - i);
        }

        soma += primeiroDigito * 2;

        int segundoDigito = soma % 11 < 2
                ? 0
                : 11 - (soma % 11);

        return base + primeiroDigito + segundoDigito;
    }
}
