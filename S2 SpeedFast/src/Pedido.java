package com.speedfast;

public abstract class Pedido {
    protected String idPedido;
    protected String direccionEntrega;
    protected double distanciaKm;
    protected String tipoPedido;

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.tipoPedido = tipoPedido;
    }

    public void mostrarResumen() {
        System.out.println("ID: " + idPedido);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
        System.out.println("Tipo: " + tipoPedido);
    }

    public abstract int calcularTiempoEntrega();

    // Métodos de la semana 1 (se mantienen)
    public void asignarRepartidor() {
        System.out.println("Asignando repartidor genérico...");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido asignado a " + nombreRepartidor);
    }
}