package com.speedfast;

public class Main {
    public static void main(String[] args) {
        PedidoComida comida = new PedidoComida("P001", "Calle 10 #20", 4.0);
        PedidoEncomienda encomienda = new PedidoEncomienda("P002", "Av. Central 45", 8.0);
        PedidoExpress express = new PedidoExpress("P003", "Plaza Mayor 8", 6.5);

        System.out.println("=== RESUMEN Y TIEMPOS ===\n");

        System.out.println("--- Pedido Comida ---");
        comida.mostrarResumen();
        System.out.println("Tiempo estimado: " + comida.calcularTiempoEntrega() + " min\n");

        System.out.println("--- Pedido Encomienda ---");
        encomienda.mostrarResumen();
        System.out.println("Tiempo estimado: " + encomienda.calcularTiempoEntrega() + " min\n");

        System.out.println("--- Pedido Express ---");
        express.mostrarResumen();
        System.out.println("Tiempo estimado: " + express.calcularTiempoEntrega() + " min\n");

        System.out.println("=== ASIGNACIÓN DE REPARTIDORES ===\n");
        comida.asignarRepartidor("Juan Pérez");
        System.out.println();
        encomienda.asignarRepartidor("Camila Soto");
        System.out.println();
        express.asignarRepartidor("Luis Díaz");
    }
}