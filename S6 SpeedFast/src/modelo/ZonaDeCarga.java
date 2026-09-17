package modelo;

import java.util.LinkedList;
import java.util.Queue;

public class ZonaDeCarga {
    private Queue<Pedido> pedidos = new LinkedList<>();

    public synchronized void agregarPedido(Pedido p) {
        pedidos.add(p);
        System.out.println("Pedido " + p.getId() + " agregado a zona de carga");
        notifyAll();
    }

    public synchronized Pedido retirarPedido() {
        while (pedidos.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        Pedido p = pedidos.poll();
        return p;
    }

    public synchronized boolean hayPedidos() {
        return !pedidos.isEmpty();
    }
}