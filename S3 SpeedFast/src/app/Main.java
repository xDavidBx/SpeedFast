package com.speedfast;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Pedido> historial = new ArrayList<>();

        com.speedfast.PedidoComida comida = new com.speedfast.PedidoComida("P001", "Calle 10 #20", 4.0);
        com.speedfast.PedidoEncomienda encomienda = new com.speedfast.PedidoEncomienda("P002", "Av. Central 45", 8.0);
        com.speedfast.PedidoExpress express = new com.speedfast.PedidoExpress("P003", "Plaza Mayor 8", 6.5);

        historial.add(comida);
        historial.add(encomienda);
        historial.add(express);

        System.out.println("=== ASIGNACIÓN Y TIEMPOS ===\n");

        comida.asignarRepartidor("Juan Pérez");
        System.out.println("Tiempo: " + comida.calcularTiempoEntrega() + " min\n");

        encomienda.asignarRepartidor("Camila Soto");
        System.out.println("Tiempo: " + encomienda.calcularTiempoEntrega() + " min\n");

        express.asignarRepartidor("Luis Díaz");
        System.out.println("Tiempo: " + express.calcularTiempoEntrega() + " min\n");

        System.out.println("=== DESPACHO ===");
        comida.despachar();
        encomienda.despachar();
        System.out.println();

        System.out.println("=== CANCELACIÓN ===");
        express.cancelar();
        System.out.println();

        System.out.println("=== HISTORIAL ===");
        for (Pedido p : historial) {
            p.verHistorial();
            p.mostrarResumen();
            System.out.println();
        }
    }
}