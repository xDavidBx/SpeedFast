package modelo;

import java.util.LinkedList;
import java.util.Queue;

public class ZonaDeCarga {
    private Queue<Pedido> pedidos = new LinkedList<>();

    public synchronized void agregarPedido(Pedido p) {
        pedidos.add(p);
        System.out.println("Pedido #" + p.getId() + " agregado. Destino: " + p.getDireccionEntrega());
        notifyAll();
    }

    public synchronized Pedido retirarPedido() {
        while (pedidos.isEmpty()) {
            try {
                wait(1000); // espera corta
                if (pedidos.isEmpty()) return null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return pedidos.poll();
    }
}