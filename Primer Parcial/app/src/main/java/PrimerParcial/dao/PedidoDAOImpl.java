package PrimerParcial.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import PrimerParcial.model.Pedido;
import PrimerParcial.util.ConnectionManager;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public void crear(Pedido pedido) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Pedido (clienteId, fecha, total) VALUES (?, ?, ?)")) {
            stmt.setInt(1, pedido.getClienteId());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setDouble(3, pedido.getTotal());
            stmt.executeUpdate();
        }
    }

    @Override
    public Pedido buscarPorId(int id) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Pedido WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Pedido(
                        rs.getInt("id"),
                        rs.getInt("clienteId"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("total")
                );
            }
        }
        return null;
    }

    @Override
    public List<Pedido> listar() throws Exception {
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Pedido")) {
            while (rs.next()) {
                lista.add(new Pedido(
                        rs.getInt("id"),
                        rs.getInt("clienteId"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("total")
                ));
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Pedido SET clienteId = ?, fecha = ?, total = ? WHERE id = ?")) {
            stmt.setInt(1, pedido.getClienteId());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setDouble(3, pedido.getTotal());
            stmt.setInt(4, pedido.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Pedido WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Busca todos los pedidos de un cliente
    @Override
    public List<Pedido> buscarPorCliente(int clienteId) throws Exception {
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Pedido WHERE clienteId = ?")) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Pedido(
                        rs.getInt("id"),
                        rs.getInt("clienteId"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("total")
                ));
            }
        }
        return lista;
    }

    // Elimina todos los pedidos de un cliente (usado al eliminar cliente)
    public void eliminarPorCliente(int clienteId) throws Exception {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Pedido WHERE clienteId = ?")) {
            stmt.setInt(1, clienteId);
            stmt.executeUpdate();
        }
    }
}
