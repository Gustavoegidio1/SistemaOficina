package br.com.oficina;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import br.com.oficina.dao.ClienteDAO;

public class Main {
    public static void main (String[] args) throws SQLException {
        ClienteDAO clienteDAO = new ClienteDAO();

        List<Cliente> clientes = clienteDAO.listarClientes();

        for (Cliente cliente : clientes) {
            System.out.println(cliente.getId());
            System.out.println(cliente.getNome());
            System.out.println(cliente.getClass().getSimpleName());
        }

    }

}
