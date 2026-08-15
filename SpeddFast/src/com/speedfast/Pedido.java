package com.speedfast;

public class Pedido {
    protected String idPedido;
    protected String direccionEntrega;
    protected String tipoPedido;

    public Pedido(String idPedido, String direccionEntrega, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.tipoPedido = tipoPedido;
    }

    public void asignarRepartidor() {
        System.out.println("Asignando repartidor genérico...");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}