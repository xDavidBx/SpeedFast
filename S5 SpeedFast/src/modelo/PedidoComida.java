package modelo;

public class PedidoComida extends Pedido {
    public PedidoComida(String idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm, "Comida");
    }

    public int calcularTiempoEntrega(int distanciaKm) {
        return 15 + (int)(2 * distanciaKm);
    }

    public int calcularTiempoEntrega() {
        return 0;
    }

    public void asignarRepartidor() {
        System.out.println("[Pedido Comida]");
        System.out.println("Asignando repartidor...");
        System.out.println("→ Verificando mochila térmica... OK");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}