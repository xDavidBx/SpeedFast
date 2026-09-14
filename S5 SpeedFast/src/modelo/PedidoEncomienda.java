package modelo;

public class PedidoEncomienda extends Pedido {
    public PedidoEncomienda(String idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm, "Encomienda");
    }

    public int calcularTiempoEntrega() {
        double distanciaKm = 0;
        return 20 + (int)(1.5 * distanciaKm);
    }

    public void asignarRepartidor() {
        System.out.println("[Pedido Encomienda]");
        System.out.println("Asignando repartidor...");
        System.out.println("→ Validando peso y embalaje... OK");
    }

    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}