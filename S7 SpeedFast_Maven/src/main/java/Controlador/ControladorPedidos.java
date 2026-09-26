package Controlador;

import modelo.Pedido;

import java.util.ArrayList;
import java.util.List;

public class ControladorPedidos {
    private static List<Pedido> pedidos = new ArrayList<>();

    public static void agregar(Pedido p) {
        pedidos.add(p);
    }

    public static List<Pedido> getPedidos() {
        return pedidos;
    }
}