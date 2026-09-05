package com.speedfast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Crear pedidos
        PedidoComida c1 = new PedidoComida("C001", "Calle 10", 3.0);
        PedidoComida c2 = new PedidoComida("C002", "Calle 15", 5.0);
        PedidoEncomienda e1 = new PedidoEncomienda("E001", "Av. Central", 7.0);
        PedidoEncomienda e2 = new PedidoEncomienda("E002", "Av. Norte", 4.5);
        PedidoExpress x1 = new PedidoExpress("X001", "Plaza 1", 6.0);
        PedidoExpress x2 = new PedidoExpress("X002", "Plaza 2", 2.0);

        // Crear repartidores
        Repartidor r1 = new Repartidor("Juan Pérez");
        r1.agregarPedido(c1);
        r1.agregarPedido(e1);

        Repartidor r2 = new Repartidor("Camila Soto");
        r2.agregarPedido(c2);
        r2.agregarPedido(x1);

        Repartidor r3 = new Repartidor("Luis Díaz");
        r3.agregarPedido(e2);
        r3.agregarPedido(x2);

        // Ejecutar en paralelo
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(r1);
        executor.execute(r2);
        executor.execute(r3);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("=== TODAS LAS ENTREGAS FINALIZADAS ===");
    }
}