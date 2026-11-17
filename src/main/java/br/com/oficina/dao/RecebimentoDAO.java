
package br.com.oficina.dao;

import br.com.oficina.Recebimento;
import br.com.oficina.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecebimentoDAO {

    public void cadastrarRecebimento(int idOrdem, Recebimento recebimento) throws SQLException {

        String sql = """
            INSERT INTO Recebimento
            (Valor, Data_Prevista, Data_Recebimento, Forma_Pagamento, Id_Ordem_Servico)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, recebimento.getValor());
            stmt.setDate(2, Date.valueOf(recebimento.getDataPrevista()));

            if (recebimento.getDataRecebimento() != null) {
                stmt.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, recebimento.getFormaPagamento());
            stmt.setInt(5, idOrdem);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    recebimento.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Recebimento> buscarRecebimentosPorOrdem(int idOrdem) throws SQLException {

        List<Recebimento> recebimentos = new ArrayList<>();

        String sql = """
            SELECT *
            FROM Recebimento
            WHERE Id_Ordem_Servico = ?
            ORDER BY Data_Prevista, Id_Recebimento
            """;

        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idOrdem);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Date dataRecebimentoSQL = rs.getDate("Data_Recebimento");

                    Recebimento recebimento = new Recebimento(
                            rs.getInt("Id_Recebimento"),
                            rs.getDouble("Valor"),
                            rs.getDate("Data_Prevista").toLocalDate(),
                            dataRecebimentoSQL != null
                                    ? dataRecebimentoSQL.toLocalDate()
                                    : null,
                            rs.getString("Forma_Pagamento")
                    );

                    recebimentos.add(recebimento);
                }
            }
        }

        return recebimentos;
    }

    public boolean atualizarRecebimento(int idOrdem, Recebimento recebimento) throws SQLException {

        String sql = """
            UPDATE Recebimento
            SET Valor = ?, Data_Prevista = ?, Data_Recebimento = ?, Forma_Pagamento = ?
            WHERE Id_Recebimento = ? AND Id_Ordem_Servico = ?
            """;

        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setDouble(1, recebimento.getValor());
            stmt.setDate(2, Date.valueOf(recebimento.getDataPrevista()));

            if (recebimento.getDataRecebimento() != null) {
                stmt.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, recebimento.getFormaPagamento());
            stmt.setInt(5, recebimento.getId());
            stmt.setInt(6, idOrdem);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removerRecebimento(int idOrdem, int idRecebimento) throws SQLException {

        String sql = """
            DELETE FROM Recebimento
            WHERE Id_Recebimento = ? AND Id_Ordem_Servico = ?
            """;

        try (Connection conexao = Conexao.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idRecebimento);
            stmt.setInt(2, idOrdem);

            return stmt.executeUpdate() > 0;
        }
    }
}
