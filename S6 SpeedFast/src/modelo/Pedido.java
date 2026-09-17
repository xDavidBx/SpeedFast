package modelo;

import interfaces.EstadoPedido;

public abstract class Pedido {
    private int id;
    private String direccionEntrega;
    private String tipo;
    private EstadoPedido estado;

    public Pedido(int id, String direccionEntrega, String tipo) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.tipo = tipo;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String express) {
    }

    public int getId() { return id; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public String getTipo() { return tipo; }
    public EstadoPedido getEstado() { return estado; }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + direccionEntrega + " (" + tipo + ") [" + estado + "]";
    }

    public abstract int calcularTiempoEntrega();

    public abstract void asignarRepartidor();

    public abstract void asignarRepartidor(String nombreRepartidor);
}