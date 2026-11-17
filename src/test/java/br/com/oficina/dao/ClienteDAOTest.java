
package br.com.oficina.dao;

import br.com.oficina.Cliente;
import br.com.oficina.Conexao;
import br.com.oficina.PessoaFisica;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteDAOTest {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Test
    public void testarConexaoMySQL() throws SQLException {

        try (Connection conexao = Conexao.getConnection()) {

            assertNotNull(conexao);
            assertTrue(conexao.isValid(2));

        }
    }

    @Test
    void testarExcluirCliente() throws SQLException {

        String cpf = "52998224725";

        PessoaFisica pessoa = new PessoaFisica(
                0,
                "Cliente para exclusão",
                "Rua Teste",
                cpf
        );

        int id = clienteDAO.cadastrarCliente(pessoa);

        clienteDAO.excluirCliente(id);

        Cliente resultado = clienteDAO.buscarClientePorId(id);

        assertNull(resultado);
    }
}
