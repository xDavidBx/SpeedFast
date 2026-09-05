package com.speedfast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Repartidor implements Runnable {
    private String nombre;
    private List<Pedido> pedidos;

    public Repartidor(String nombre) {
        this.nombre = nombre;
        this.pedidos = new ArrayList<>();
    }

    public void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    @Override
    public void run() {
        System.out.println(">>> " + nombre + " inicia entregas (" + pedidos.size() + " pedidos)");
        Random random = new Random();

        for (Pedido p : pedidos) {
            try {
                System.out.println(nombre + " → Iniciando pedido " + p.idPedido);
                p.mostrarResumen();
                System.out.println("Tiempo estimado: " + p.calcularTiempoEntrega() + " min");

                int espera = 1000 + random.nextInt(2000); // 1-3 segundos
                Thread.sleep(espera);

                p.despachar();
                System.out.println(nombre + " → Pedido " + p.idPedido + " ENTREGADO\n");
            } catch (InterruptedException e) {
                System.out.println(nombre + " interrumpido.");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("<<< " + nombre + " terminó todas sus entregas\n");
    }
}