package PrimerParcial.dao;

import java.util.List;

import PrimerParcial.model.Pedido;

public interface PedidoDAO extends GenericDAO<Pedido> {
    List<Pedido> buscarPorCliente(int clienteId) throws Exception;
}
