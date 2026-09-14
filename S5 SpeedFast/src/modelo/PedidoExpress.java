package modelo;

public class PedidoExpress extends Pedido {
    public PedidoExpress(String idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm, "Express");
    }

    public int calcularTiempoEntrega() {
        int tiempo = 10;
        int distanciaKm = 0;
        if (distanciaKm > 5) tiempo += 5;
        return tiempo;
    }

    public void asignarRepartidor() {
        System.out.println("[Pedido Express]");
        System.out.println("Asignando repartidor...");
        System.out.println("→ Repartidor más cercano con disponibilidad inmediata encontrado.");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}