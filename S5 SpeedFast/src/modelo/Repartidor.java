package modelo;

import interfaces.EstadoPedido;

import java.util.Random;

public class Repartidor implements Runnable {
    private String nombre;
    private ZonaDeCarga zonaDeCarga;

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    @Override
    public void run() {
        Random rnd = new Random();
        while (true) {
            Pedido p = zonaDeCarga.retirarPedido();
            if (p == null) break;

            p.setEstado(EstadoPedido.EN_REPARTO);
            System.out.println("[Repartidor - " + nombre + "] Retirando pedido #" + p.getId() + "...");
            System.out.println("[Repartidor - " + nombre + "] Estado: EN_REPARTO");

            try {
                Thread.sleep(1500 + rnd.nextInt(1500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            p.setEstado(EstadoPedido.ENTREGADO);
            System.out.println("[Repartidor - " + nombre + "] Entregando pedido #" + p.getId() + "...");
            System.out.println("[Repartidor - " + nombre + "] Estado: ENTREGADO");
        }
    }
}