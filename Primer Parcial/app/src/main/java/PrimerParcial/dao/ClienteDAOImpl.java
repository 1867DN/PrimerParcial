package PrimerParcial.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import PrimerParcial.model.Cliente;
import PrimerParcial.util.ConnectionManager;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void crear(Cliente cliente) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Cliente (nombre, email) VALUES (?, ?)")
        ) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.executeUpdate();
        }
    }

    @Override
    public Cliente buscarPorId(int id) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Cliente WHERE id = ?")
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email")
                );
            }
        }
        return null;
    }

    @Override
    public List<Cliente> listar() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Cliente")
        ) {
            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email")
                ));
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Cliente cliente) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Cliente SET nombre = ?, email = ? WHERE id = ?")
        ) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setInt(3, cliente.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Cliente WHERE id = ?")
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
