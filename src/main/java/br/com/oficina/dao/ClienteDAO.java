package br.com.oficina.dao;

import br.com.oficina.Cliente;
import br.com.oficina.PessoaFisica;
import br.com.oficina.PessoaJuridica;
import br.com.oficina.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {


    public List<Cliente> listarClientes() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT
                c.Id_Cliente,
                c.Nome,
                c.Endereco,
                c.Email,
                c.Telefone,
                pf.CPF,
                pj.CNPJ,
                pj.Razao_Social,
                pj.Inscricao_Estadual
            FROM Cliente c
            LEFT JOIN Pessoa_Fisica pf
                ON c.Id_Cliente = pf.Id_Cliente
            LEFT JOIN Pessoa_Juridica pj
                ON c.Id_Cliente = pj.Id_Cliente
            """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultado = stmt.executeQuery()
        ) {

            while (resultado.next()) {

                int id = resultado.getInt("Id_Cliente");
                String nome = resultado.getString("Nome");
                String endereco = resultado.getString("Endereco");
                String email = resultado.getString("Email");
                String telefone = resultado.getString("Telefone");

                String cpf = resultado.getString("CPF");
                String cnpj = resultado.getString("CNPJ");

                Cliente cliente;

                if (cpf != null && cnpj == null) {

                    cliente = new PessoaFisica(
                            id,
                            nome,
                            endereco,
                            cpf,
                            email,
                            telefone,
                            true
                    );

                } else if (cnpj != null && cpf == null) {

                    cliente = new PessoaJuridica(
                            id,
                            nome,
                            endereco,
                            cnpj,
                            resultado.getString("Razao_Social"),
                            resultado.getString("Inscricao_Estadual"),
                            email,
                            telefone,
                            true
                    );

                } else {
                    throw new SQLException(
                            "Tipo de cliente inválido para o ID: " + id
                    );
                }

                clientes.add(cliente);
            }
        }

        return clientes;
    }


    public boolean cpfExiste(String cpf) throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM Pessoa_Fisica
            WHERE CPF = ?
            """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, cpf);

            try (ResultSet resultado = stmt.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    public boolean cnpjExiste(String cnpj) throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM Pessoa_Juridica
            WHERE CNPJ = ?
            """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, cnpj);

            try (ResultSet resultado = stmt.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    public int cadastrarCliente(Cliente cliente) throws SQLException {

        String sqlCliente = """
            INSERT INTO Cliente (Nome, Endereco, Email, Telefone)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {
                int idCliente;

                try (
                        PreparedStatement stmt = conexao.prepareStatement(
                                sqlCliente,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
                ) {
                    stmt.setString(1, cliente.getNome());
                    stmt.setString(2, cliente.getEndereco());
                    stmt.setString(3, cliente.getEmail());
                    stmt.setString(4, cliente.getTelefone());

                    stmt.executeUpdate();

                    try (ResultSet resultado = stmt.getGeneratedKeys()) {
                        if (!resultado.next()) {
                            throw new SQLException("Não foi possível obter o ID do cliente.");
                        }

                        idCliente = resultado.getInt(1);
                    }
                }

                if (cliente instanceof PessoaFisica pessoaFisica) {

                    String sql = """
                        INSERT INTO Pessoa_Fisica (Id_Cliente, CPF)
                        VALUES (?, ?)
                        """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, idCliente);
                        stmt.setString(2, pessoaFisica.getCpf());
                        stmt.executeUpdate();
                    }

                } else if (cliente instanceof PessoaJuridica pessoaJuridica) {

                    String sql = """
                        INSERT INTO Pessoa_Juridica
                        (Id_Cliente, CNPJ, Razao_Social, Inscricao_Estadual)
                        VALUES (?, ?, ?, ?)
                        """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, idCliente);
                        stmt.setString(2, pessoaJuridica.getCnpj());
                        stmt.setString(3, pessoaJuridica.getRazaoSocial());
                        stmt.setString(4, pessoaJuridica.getInscricaoEstadual());
                        stmt.executeUpdate();
                    }

                } else {
                    throw new SQLException("Tipo de cliente não reconhecido.");
                }

                conexao.commit();
                return idCliente;

            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
    }



    public Cliente buscarClientePorId(int id) throws SQLException {

        String sql = """
        SELECT
            c.Id_Cliente,
            c.Nome,
            c.Endereco,
            c.Email,
            c.Telefone,
            pf.CPF,
            pj.CNPJ,
            pj.Razao_Social,
            pj.Inscricao_Estadual
        FROM Cliente c
        LEFT JOIN Pessoa_Fisica pf
            ON c.Id_Cliente = pf.Id_Cliente
        LEFT JOIN Pessoa_Juridica pj
            ON c.Id_Cliente = pj.Id_Cliente
        WHERE c.Id_Cliente = ?
        """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (!resultado.next()) {
                    return null;
                }

                String nome = resultado.getString("Nome");
                String endereco = resultado.getString("Endereco");
                String email = resultado.getString("Email");
                String telefone = resultado.getString("Telefone");

                String cpf = resultado.getString("CPF");
                String cnpj = resultado.getString("CNPJ");

                Cliente cliente;

                if (cpf != null && cnpj == null) {

                    cliente = new PessoaFisica(
                            id,
                            nome,
                            endereco,
                            cpf,
                            email,
                            telefone,
                            true
                    );

                } else if (cnpj != null && cpf == null) {

                    cliente = new PessoaJuridica(
                            id,
                            nome,
                            endereco,
                            cnpj,
                            resultado.getString("Razao_Social"),
                            resultado.getString("Inscricao_Estadual"),
                            email,
                            telefone,
                            true
                    );

                } else {
                    throw new SQLException(
                            "Tipo de cliente inválido para o ID: " + id
                    );
                }

                return cliente;
            }
        }
    }


    public void atualizarCliente(Cliente cliente) throws SQLException {

        if (cliente == null || cliente.getId() <= 0) {
            throw new IllegalArgumentException("Cliente inválido.");
        }

        String sqlCliente = """
            UPDATE Cliente
            SET Nome = ?, Endereco = ?, Email = ?, Telefone = ?
            WHERE Id_Cliente = ?
            """;

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                try (PreparedStatement stmt = conexao.prepareStatement(sqlCliente)) {

                    stmt.setString(1, cliente.getNome());
                    stmt.setString(2, cliente.getEndereco());
                    stmt.setString(3, cliente.getEmail());
                    stmt.setString(4, cliente.getTelefone());
                    stmt.setInt(5, cliente.getId());

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas == 0) {
                        throw new SQLException("Cliente não encontrado.");
                    }
                }

                if (cliente instanceof PessoaFisica pessoaFisica) {

                    String sql = """
                        UPDATE Pessoa_Fisica
                        SET CPF = ?
                        WHERE Id_Cliente = ?
                        """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

                        stmt.setString(1, pessoaFisica.getCpf());
                        stmt.setInt(2, cliente.getId());

                        if (stmt.executeUpdate() == 0) {
                            throw new SQLException("Pessoa física não encontrada.");
                        }
                    }

                } else if (cliente instanceof PessoaJuridica pessoaJuridica) {

                    String sql = """
                        UPDATE Pessoa_Juridica
                        SET CNPJ = ?, Razao_Social = ?, Inscricao_Estadual = ?
                        WHERE Id_Cliente = ?
                        """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

                        stmt.setString(1, pessoaJuridica.getCnpj());
                        stmt.setString(2, pessoaJuridica.getRazaoSocial());
                        stmt.setString(3, pessoaJuridica.getInscricaoEstadual());
                        stmt.setInt(4, cliente.getId());

                        if (stmt.executeUpdate() == 0) {
                            throw new SQLException("Pessoa jurídica não encontrada.");
                        }
                    }

                } else {
                    throw new SQLException("Tipo de cliente não reconhecido.");
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    public boolean possuiOrdensServico(int idCliente) throws SQLException {

        String sql = """
        SELECT 1
        FROM Ordem_Servico
        WHERE Id_Cliente = ?
        LIMIT 1
        """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, idCliente);

            try (ResultSet resultado = stmt.executeQuery()) {
                return resultado.next();
            }
        }
    }


    public void excluirCliente(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("ID do cliente inválido.");
        }

        String sql = """
            DELETE FROM Cliente
            WHERE Id_Cliente = ?
            """;

        try (
                Connection conexao = Conexao.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Cliente não encontrado.");
            }
        }
    }



}