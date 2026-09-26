package modelo;

public abstract class Pedido {
    private int id;
    private String direccion;
    private String tipo;
    private String estado;

    public Pedido() {}

    public Pedido(String direccion, String tipo, double distanciaKm, String estado) {
        this.direccion = direccion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public abstract int calcularTiempoEntrega();

    public abstract void asignarRepartidor();

    public abstract void asignarRepartidor(String nombreRepartidor);
}