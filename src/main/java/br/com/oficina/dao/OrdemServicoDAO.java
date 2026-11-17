package br.com.oficina.dao;

import br.com.oficina.OrdemServico;
import br.com.oficina.Conexao;
import br.com.oficina.ItemOrdemServico;
import br.com.oficina.Servico;
import br.com.oficina.Produto;
import br.com.oficina.Pneu;
import br.com.oficina.Peca;
import br.com.oficina.Cliente;
import br.com.oficina.Recebimento;

import java.time.LocalDate;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrdemServicoDAO {

    public int cadastrarOrdemServico(OrdemServico ordem) throws SQLException {

        if (ordem == null) {
            throw new IllegalArgumentException("A ordem de serviço não pode ser nula.");
        }

        if (ordem.getId() != 0) {
            throw new IllegalArgumentException("A ordem de serviço já possui um ID.");
        }

        if (ordem.getCliente().getId() <= 0) {
            throw new IllegalArgumentException("O cliente precisa estar cadastrado.");
        }

        String sql = """
        INSERT INTO Ordem_Servico
        (Data_Abertura, Modelo_Veiculo, Placa, Quilometragem, Id_Cliente)
        VALUES (?, ?, ?, ?, ?)
        """;

        int idOrdem;
        Map<ItemOrdemServico, Integer> idsItens = new IdentityHashMap<>();

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                // 1. Cadastra a ordem de serviço

                try (PreparedStatement stmt = conexao.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setDate(1, java.sql.Date.valueOf(ordem.getDataAbertura()));
                    stmt.setString(2, ordem.getModeloVeiculo());
                    stmt.setString(3, ordem.getPlaca());
                    stmt.setInt(4, ordem.getQuilometragem());
                    stmt.setInt(5, ordem.getCliente().getId());

                    stmt.executeUpdate();

                    try (ResultSet resultado = stmt.getGeneratedKeys()) {
                        if (!resultado.next()) {
                            throw new SQLException("Não foi possível gerar o ID da OS.");
                        }

                        idOrdem = resultado.getInt(1);
                    }
                }

                // 2. Cadastra os itens da ordem

                for (ItemOrdemServico item : ordem.getItensOrdemServico()) {

                    int idItem = cadastrarItem(conexao, idOrdem, item);

                    idsItens.put(item, idItem);

                    // 3. Cadastra os dados específicos

                    if (item instanceof Produto produto) {

                        cadastrarProduto(conexao, idItem, produto);

                        if (item instanceof Pneu pneu) {
                            cadastrarPneu(conexao, idItem, pneu);

                        } else if (item instanceof Peca peca) {
                            cadastrarPeca(conexao, idItem, peca);

                        } else {
                            throw new IllegalArgumentException("Tipo de produto inválido.");
                        }

                    } else if (item instanceof Servico servico) {

                        cadastrarServico(conexao, idItem, servico);

                    } else {
                        throw new IllegalArgumentException("Tipo de item inválido.");
                    }
                }

                // 4. Confirma todas as operações

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();
                throw e;
            }
        }

        // 5. Atualiza os IDs dos objetos Java após o commit

        ordem.setId(idOrdem);

        for (Map.Entry<ItemOrdemServico, Integer> entrada : idsItens.entrySet()) {
            entrada.getKey().setId(entrada.getValue());
        }

        return idOrdem;
    }


    private int cadastrarItem(
            Connection conexao,
            int idOrdem,
            ItemOrdemServico item
    ) throws SQLException {

        String sql = """
            INSERT INTO Item_Ordem_Servico
            (Nome, Quantidade, Id_Ordem_Servico)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement stmt = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getNome());
            stmt.setInt(2, item.getQuantidade());
            stmt.setInt(3, idOrdem);

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }

                throw new SQLException("Não foi possível gerar o ID do item.");
            }
        }
    }

    private void cadastrarServico(
            Connection conexao,
            int idItem,
            Servico servico
    ) throws SQLException {

        String sql = """
            INSERT INTO Servico
            (Id_Item, Valor_Cobrado)
            VALUES (?, ?)
            """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.setDouble(2, servico.getValorCobrado());

            stmt.executeUpdate();
        }
    }

    private void cadastrarProduto(
            Connection conexao,
            int idItem,
            Produto produto
    ) throws SQLException {

        String sql = """
            INSERT INTO Produto
            (Id_Item, Marca, Valor_Venda_Unitario, Custo_Total)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.setString(2, produto.getMarca());
            stmt.setDouble(3, produto.getValorVendaUnitario());

            if (produto.getCusto() == null) {
                stmt.setNull(4, java.sql.Types.DECIMAL);
            } else {
                stmt.setDouble(4, produto.getCusto());
            }

            stmt.executeUpdate();
        }
    }

    private void cadastrarPneu(
            Connection conexao,
            int idItem,
            Pneu pneu
    ) throws SQLException {

        String sql = """
            INSERT INTO Pneu
            (Id_Item, Modelo, Largura, Perfil, Aro)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.setString(2, pneu.getModelo());
            stmt.setInt(3, pneu.getLargura());
            stmt.setInt(4, pneu.getPerfil());
            stmt.setInt(5, pneu.getAro());

            stmt.executeUpdate();
        }
    }

    private void cadastrarPeca(
            Connection conexao,
            int idItem,
            Peca peca
    ) throws SQLException {

        String sql = """
            INSERT INTO Peca
            (Id_Item, Categoria)
            VALUES (?, ?)
            """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.setString(2, peca.getCategoria());

            stmt.executeUpdate();
        }
    }


    public OrdemServico buscarOrdemServicoPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID da ordem de serviço é inválido.");
        }

        String sql = """
    SELECT Data_Abertura, Modelo_Veiculo, Placa,
           Quilometragem, Id_Cliente
    FROM Ordem_Servico
    WHERE Id_Ordem_Servico = ?
    """;

        try (Connection conexao = Conexao.getConnection()) {

            LocalDate dataAbertura;
            String modelo;
            String placa;
            int quilometragem;
            int idCliente;

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setInt(1, id);

                try (ResultSet resultado = stmt.executeQuery()) {

                    if (!resultado.next()) {
                        return null;
                    }

                    dataAbertura = resultado.getDate("Data_Abertura").toLocalDate();
                    modelo = resultado.getString("Modelo_Veiculo");
                    placa = resultado.getString("Placa");
                    quilometragem = resultado.getInt("Quilometragem");
                    idCliente = resultado.getInt("Id_Cliente");
                }
            }

            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente cliente = clienteDAO.buscarClientePorId(idCliente);

            if (cliente == null) {
                throw new SQLException("Cliente associado à OS não encontrado.");
            }

            OrdemServico ordem = new OrdemServico(
                    id,
                    cliente,
                    dataAbertura,
                    modelo,
                    placa,
                    quilometragem
            );

            carregarItens(conexao, ordem, id);

            RecebimentoDAO recebimentoDAO = new RecebimentoDAO();

            List<Recebimento> recebimentos =
                    recebimentoDAO.buscarRecebimentosPorOrdem(id);

            for (Recebimento recebimento : recebimentos) {
                ordem.adicionarRecebimento(recebimento);
            }

            return ordem;
        }
    }


    private void carregarItens(
            Connection conexao,
            OrdemServico ordem,
            int idOrdem
    ) throws SQLException {

        String sql = """
        SELECT
            i.Id_Item,
            i.Nome,
            i.Quantidade,

            p.Id_Item AS Id_Produto,
            p.Marca,
            p.Valor_Venda_Unitario,
            p.Custo_Total,

            pn.Id_Item AS Id_Pneu,
            pn.Modelo AS Modelo_Pneu,
            pn.Largura,
            pn.Perfil,
            pn.Aro,

            pc.Id_Item AS Id_Peca,
            pc.Categoria,

            s.Id_Item AS Id_Servico,
            s.Valor_Cobrado

        FROM Item_Ordem_Servico i

        LEFT JOIN Produto p ON i.Id_Item = p.Id_Item
        LEFT JOIN Pneu pn ON i.Id_Item = pn.Id_Item
        LEFT JOIN Peca pc ON i.Id_Item = pc.Id_Item
        LEFT JOIN Servico s ON i.Id_Item = s.Id_Item

        WHERE i.Id_Ordem_Servico = ?
        ORDER BY i.Id_Item
        """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idOrdem);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {

                    int idItem = resultado.getInt("Id_Item");
                    String nome = resultado.getString("Nome");
                    int quantidade = resultado.getInt("Quantidade");

                    ItemOrdemServico item;

                    if (resultado.getObject("Id_Pneu") != null) {

                        Pneu pneu = new Pneu(
                                nome,
                                quantidade,
                                resultado.getString("Marca"),
                                resultado.getDouble("Valor_Venda_Unitario"),
                                resultado.getString("Modelo_Pneu"),
                                resultado.getInt("Largura"),
                                resultado.getInt("Perfil"),
                                resultado.getInt("Aro")
                        );

                        Double custo = resultado.getObject("Custo_Total", Double.class);

                        if (custo != null) {
                            pneu.setCusto(custo);
                        }

                        item = pneu;
                        ordem.adicionarPneu(pneu);

                    } else if (resultado.getObject("Id_Peca") != null) {

                        Peca peca = new Peca(
                                nome,
                                quantidade,
                                resultado.getString("Marca"),
                                resultado.getDouble("Valor_Venda_Unitario"),
                                resultado.getString("Categoria")
                        );

                        Double custo = resultado.getObject("Custo_Total", Double.class);

                        if (custo != null) {
                            peca.setCusto(custo);
                        }

                        item = peca;
                        ordem.adicionarPeca(peca);

                    } else if (resultado.getObject("Id_Servico") != null) {

                        Servico servico = new Servico(
                                nome,
                                quantidade,
                                resultado.getDouble("Valor_Cobrado")
                        );

                        item = servico;
                        ordem.adicionarServico(servico);

                    } else {

                        throw new SQLException("Tipo de item não reconhecido: " + idItem);
                    }

                    // Substitui o ID temporário pelo ID real do banco.
                    item.setId(idItem);
                }
            }
        }
    }


    public boolean atualizarOrdemServico(OrdemServico ordem) throws SQLException {

        if (ordem == null) {
            throw new IllegalArgumentException("A ordem de serviço não pode ser nula.");
        }

        if (ordem.getId() <= 0) {
            throw new IllegalArgumentException("A ordem de serviço não possui um ID válido.");
        }

        if (ordem.getCliente().getId() <= 0) {
            throw new IllegalArgumentException("O cliente precisa estar cadastrado.");
        }

        String sql = """
    UPDATE Ordem_Servico
    SET Data_Abertura = ?,
        Modelo_Veiculo = ?,
        Placa = ?,
        Quilometragem = ?,
        Id_Cliente = ?
    WHERE Id_Ordem_Servico = ?
    """;

        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(ordem.getDataAbertura()));
            stmt.setString(2, ordem.getModeloVeiculo());
            stmt.setString(3, ordem.getPlaca());
            stmt.setInt(4, ordem.getQuilometragem());
            stmt.setInt(5, ordem.getCliente().getId());
            stmt.setInt(6, ordem.getId());

            return stmt.executeUpdate() > 0;
        }
    }


    public boolean removerOrdemServico(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID da ordem de serviço é inválido.");
        }

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                // 1. Remove os pneus
                String sqlPneu = """
                DELETE FROM Pneu
                WHERE Id_Item IN (
                    SELECT Id_Item FROM Item_Ordem_Servico
                    WHERE Id_Ordem_Servico = ?
                )
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlPneu)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 2. Remove as peças
                String sqlPeca = """
                DELETE FROM Peca
                WHERE Id_Item IN (
                    SELECT Id_Item FROM Item_Ordem_Servico
                    WHERE Id_Ordem_Servico = ?
                )
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlPeca)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 3. Remove os produtos
                String sqlProduto = """
                DELETE FROM Produto
                WHERE Id_Item IN (
                    SELECT Id_Item FROM Item_Ordem_Servico
                    WHERE Id_Ordem_Servico = ?
                )
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlProduto)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 4. Remove os serviços
                String sqlServico = """
                DELETE FROM Servico
                WHERE Id_Item IN (
                    SELECT Id_Item FROM Item_Ordem_Servico
                    WHERE Id_Ordem_Servico = ?
                )
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlServico)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 5. Remove os itens gerais
                String sqlItem = """
                DELETE FROM Item_Ordem_Servico
                WHERE Id_Ordem_Servico = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlItem)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 6. Remove a ordem de serviço
                String sqlOrdem = """
                DELETE FROM Ordem_Servico
                WHERE Id_Ordem_Servico = ?
                """;

                int linhasAfetadas;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlOrdem)) {
                    stmt.setInt(1, id);
                    linhasAfetadas = stmt.executeUpdate();
                }

                conexao.commit();

                return linhasAfetadas > 0;

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();
                throw e;
            }
        }
    }

    public int adicionarItemOrdemServico(OrdemServico ordem, ItemOrdemServico item) throws SQLException {

        if (ordem == null || ordem.getId() <= 0) {
            throw new IllegalArgumentException("Ordem de serviço inválida.");
        }

        if (item == null || item.getId() != 0) {
            throw new IllegalArgumentException("Item inválido.");
        }

        if (!(item instanceof Pneu || item instanceof Peca || item instanceof Servico)) {
            throw new IllegalArgumentException("Tipo de item não permitido.");
        }

        int idItem;

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                // Verifica se a OS existe
                String sqlVerificar = """
                SELECT 1 FROM Ordem_Servico
                WHERE Id_Ordem_Servico = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlVerificar)) {

                    stmt.setInt(1, ordem.getId());

                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            throw new IllegalArgumentException("Ordem de serviço não encontrada.");
                        }
                    }
                }

                // Insere o item geral e recupera seu ID
                idItem = cadastrarItem(conexao, ordem.getId(), item);

                // Insere os dados específicos do item
                if (item instanceof Pneu pneu) {

                    cadastrarProduto(conexao, idItem, pneu);
                    cadastrarPneu(conexao, idItem, pneu);

                } else if (item instanceof Peca peca) {

                    cadastrarProduto(conexao, idItem, peca);
                    cadastrarPeca(conexao, idItem, peca);

                } else if (item instanceof Servico servico) {

                    cadastrarServico(conexao, idItem, servico);
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();
                throw e;
            }
        }

        // Atualiza a lista de itens do objeto em memória
        if (item instanceof Pneu pneu) {
            ordem.adicionarPneu(pneu);

        } else if (item instanceof Peca peca) {
            ordem.adicionarPeca(peca);

        } else if (item instanceof Servico servico) {
            ordem.adicionarServico(servico);
        }

        // Substitui o ID temporário pelo ID real do banco
        item.setId(idItem);

        return idItem;
    }

    public void atualizarItemOrdemServico(OrdemServico ordem, ItemOrdemServico item) throws SQLException {

        if (ordem == null || ordem.getId() <= 0) {
            throw new IllegalArgumentException("Ordem de serviço inválida.");
        }

        if (item == null || item.getId() <= 0) {
            throw new IllegalArgumentException("Item inválido.");
        }

        if (!(item instanceof Pneu || item instanceof Peca || item instanceof Servico)) {
            throw new IllegalArgumentException("Tipo de item não permitido.");
        }

        if (ordem.getValorTotal() < ordem.getTotalRecebido()) {
            throw new IllegalArgumentException(
                    "O valor total da OS não pode ser menor que o total já recebido."
            );
        }

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                // Verifica se o item pertence à OS informada
                String sqlVerificar = """
                SELECT 1
                FROM Item_Ordem_Servico
                WHERE Id_Item = ? AND Id_Ordem_Servico = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlVerificar)) {

                    stmt.setInt(1, item.getId());
                    stmt.setInt(2, ordem.getId());

                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            throw new IllegalArgumentException(
                                    "Item não encontrado nesta ordem de serviço."
                            );
                        }
                    }
                }

                // Atualiza os dados gerais
                String sqlItem = """
                UPDATE Item_Ordem_Servico
                SET Nome = ?, Quantidade = ?
                WHERE Id_Item = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlItem)) {

                    stmt.setString(1, item.getNome());
                    stmt.setInt(2, item.getQuantidade());
                    stmt.setInt(3, item.getId());

                    stmt.executeUpdate();
                }

                // Atualiza os dados específicos
                if (item instanceof Pneu pneu) {

                    String sqlProduto = """
                    UPDATE Produto
                    SET Marca = ?, Valor_Venda_Unitario = ?, Custo_Total = ?
                    WHERE Id_Item = ?
                    """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sqlProduto)) {

                        stmt.setString(1, pneu.getMarca());
                        stmt.setDouble(2, pneu.getValorVendaUnitario());
                        stmt.setObject(3, pneu.getCusto());
                        stmt.setInt(4, pneu.getId());

                        stmt.executeUpdate();
                    }

                    String sqlPneu = """
                    UPDATE Pneu
                    SET Modelo = ?, Largura = ?, Perfil = ?, Aro = ?
                    WHERE Id_Item = ?
                    """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sqlPneu)) {

                        stmt.setString(1, pneu.getModelo());
                        stmt.setInt(2, pneu.getLargura());
                        stmt.setInt(3, pneu.getPerfil());
                        stmt.setInt(4, pneu.getAro());
                        stmt.setInt(5, pneu.getId());

                        stmt.executeUpdate();
                    }

                } else if (item instanceof Peca peca) {

                    String sqlProduto = """
                    UPDATE Produto
                    SET Marca = ?, Valor_Venda_Unitario = ?, Custo_Total = ?
                    WHERE Id_Item = ?
                    """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sqlProduto)) {

                        stmt.setString(1, peca.getMarca());
                        stmt.setDouble(2, peca.getValorVendaUnitario());
                        stmt.setObject(3, peca.getCusto());
                        stmt.setInt(4, peca.getId());

                        stmt.executeUpdate();
                    }

                    String sqlPeca = """
                    UPDATE Peca
                    SET Categoria = ?
                    WHERE Id_Item = ?
                    """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sqlPeca)) {

                        stmt.setString(1, peca.getCategoria());
                        stmt.setInt(2, peca.getId());

                        stmt.executeUpdate();
                    }

                } else if (item instanceof Servico servico) {

                    String sqlServico = """
                    UPDATE Servico
                    SET Valor_Cobrado = ?
                    WHERE Id_Item = ?
                    """;

                    try (PreparedStatement stmt = conexao.prepareStatement(sqlServico)) {

                        stmt.setDouble(1, servico.getValorCobrado());
                        stmt.setInt(2, servico.getId());

                        stmt.executeUpdate();
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();
                throw e;
            }
        }
    }

    public boolean removerItemOrdemServico(int idOrdem, int idItem) throws SQLException {

        if (idOrdem <= 0 || idItem <= 0) {
            throw new IllegalArgumentException("IDs inválidos.");
        }

        try (Connection conexao = Conexao.getConnection()) {

            conexao.setAutoCommit(false);

            try {

                // Verifica se o item pertence à OS
                String sqlVerificar = """
                SELECT 1 FROM Item_Ordem_Servico
                WHERE Id_Item = ? AND Id_Ordem_Servico = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlVerificar)) {

                    stmt.setInt(1, idItem);
                    stmt.setInt(2, idOrdem);

                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            conexao.rollback();
                            return false;
                        }
                    }
                }

                // Remove o registro específico do tipo
                String[] tabelas = {"Pneu", "Peca", "Produto", "Servico"};

                for (String tabela : tabelas) {

                    String sql = "DELETE FROM " + tabela + " WHERE Id_Item = ?";

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, idItem);
                        stmt.executeUpdate();
                    }
                }

                // Remove o registro geral
                String sqlItem = """
                DELETE FROM Item_Ordem_Servico
                WHERE Id_Item = ? AND Id_Ordem_Servico = ?
                """;

                try (PreparedStatement stmt = conexao.prepareStatement(sqlItem)) {
                    stmt.setInt(1, idItem);
                    stmt.setInt(2, idOrdem);
                    stmt.executeUpdate();
                }

                conexao.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();
                throw e;
            }
        }
    }


    public List<OrdemServico> listarOrdensServico() throws SQLException {

        List<Integer> ids = new ArrayList<>();

        String sql = """
        SELECT Id_Ordem_Servico
        FROM Ordem_Servico
        ORDER BY Data_Abertura DESC, Id_Ordem_Servico DESC
        """;

        // 1. Busca os IDs de todas as ordens.
        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                ids.add(resultado.getInt("Id_Ordem_Servico"));
            }
        }

        // 2. Carrega cada ordem com seus dados completos.
        List<OrdemServico> ordens = new ArrayList<>();

        for (int id : ids) {
            OrdemServico ordem = buscarOrdemServicoPorId(id);

            if (ordem != null) {
                ordens.add(ordem);
            }
        }

        return ordens;
    }

}
