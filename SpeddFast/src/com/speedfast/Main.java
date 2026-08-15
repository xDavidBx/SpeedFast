package com.speedfast;

public class Main {
    public static void main(String[] args) {
        PedidoComida comida = new PedidoComida("P001", "Calle 10 #20");
        comida.asignarRepartidor("Juan Pérez");

        System.out.println();

        PedidoEncomienda encomienda = new PedidoEncomienda("P002", "Av. Principal 45");
        encomienda.asignarRepartidor("Camila Soto");

        System.out.println();

        PedidoExpress express = new PedidoExpress("P003", "Calle 5 #12");
        express.asignarRepartidor("Luis Díaz");
    }
}