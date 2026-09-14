package modelo;

import interfaces.EstadoPedido;

public class Pedido {
    private int id;
    private String direccionEntrega;
    private EstadoPedido estado;

    public Pedido(int id, String direccionEntrega) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String express) {

    }

    public int getId() { return id; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public EstadoPedido getEstado() { return estado; }

    public void setEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + direccionEntrega + " [" + estado + "]";
    }
}