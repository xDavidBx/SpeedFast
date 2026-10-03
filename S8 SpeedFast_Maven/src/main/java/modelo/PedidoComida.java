package modelo;

public class PedidoComida extends Pedido {
    public PedidoComida(String direccionEntrega, String estado) {
        super(direccionEntrega, "COMIDA", estado);
    }

    @Override
    public int calcularTiempoEntrega() {
        int distanciaKm = 1;
        return 15 + (int)(2 * distanciaKm);
    }

    @Override
    public void asignarRepartidor() {
        System.out.println("[Pedido Comida]");
        System.out.println("Asignando repartidor...");
        System.out.println("→ Verificando mochila térmica... OK");
    }

    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();
        System.out.println("→ Pedido asignado a " + nombreRepartidor);
    }
}
