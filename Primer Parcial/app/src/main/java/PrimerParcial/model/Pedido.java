package PrimerParcial.model;

import java.time.LocalDate;

public class Pedido {
    private int id;
    private int clienteId; // Relación con Cliente
    private LocalDate fecha;
    private double total;

    public Pedido() {}

    public Pedido(int id, int clienteId, LocalDate fecha, double total) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
