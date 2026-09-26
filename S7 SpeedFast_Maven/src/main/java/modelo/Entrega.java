package modelo;

import java.sql.Date;
import java.sql.Time;

public class Entrega {
    private int id;
    private int idPedido;
    private int idRepartidor;
    private Date fecha;
    private Time hora;

    public Entrega(int idPedido, int idRepartidor, Date fecha, Time hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getIdPedido() { return idPedido; }
    public int getIdRepartidor() { return idRepartidor; }
    public Date getFecha() { return fecha; }
    public Time getHora() { return hora; }
}