package modelo;

public class PedidoEncomienda extends Pedido {
    public PedidoEncomienda(String direccionEntrega, String estado) {
        super(direccionEntrega, "ENCOMIENDA", estado);
    }

    @Override
    public int calcularTiempoEntrega() {
        double distanciaKm = 0;
        return 20 + (int)(1.5 * distanciaKm);
    }

    @Override
    public void asignarRepartidor() {
        System.out.println("[Pedido Encomienda]");
        System.out.println("Asignando repartidor...");
        System.out.println("→ Validando peso y embalaje... OK");
    }

    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}
