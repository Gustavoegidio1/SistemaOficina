
package br.com.oficina;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection getConnection() throws SQLException {

        String url = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:3306/sistemaoficina"
        );

        String usuario = System.getenv().getOrDefault(
                "DB_USER",
                "root"
        );

        String senha = System.getenv().getOrDefault(
                "DB_PASSWORD",
                ""
        );

        return DriverManager.getConnection(url, usuario, senha);
    }
}


