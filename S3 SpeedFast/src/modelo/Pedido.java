package com.speedfast;

public abstract class Pedido implements Despachable, Cancelable, Rastreable {
    protected String idPedido;
    protected String direccionEntrega;
    protected double distanciaKm;
    protected String tipoPedido;
    protected String estado;

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.tipoPedido = tipoPedido;
        this.estado = "Pendiente";
    }

    public void mostrarResumen() {
        System.out.println("ID: " + idPedido);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
        System.out.println("Tipo: " + tipoPedido);
        System.out.println("Estado: " + estado);
    }

    public abstract int calcularTiempoEntrega();

    public void asignarRepartidor() {
        System.out.println("Asignando repartidor genérico...");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido asignado a " + nombreRepartidor);
    }

    @Override
    public void despachar() {
        estado = "Despachado";
        System.out.println("Pedido " + idPedido + " despachado.");
    }

    @Override
    public void cancelar() {
        estado = "Cancelado";
        System.out.println("Pedido " + idPedido + " cancelado.");
    }

    @Override
    public void verHistorial() {
        System.out.println("Historial del pedido " + idPedido + ": Estado actual = " + estado);
    }
}