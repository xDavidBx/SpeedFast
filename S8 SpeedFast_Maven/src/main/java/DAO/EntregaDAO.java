package DAO;

import Conexiones.ConexionBD;
import modelo.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Operaciones de persistencia para entregas y sus entidades relacionadas. */
public class EntregaDAO {
    public int create(Entrega entrega) throws SQLException {
        validar(entrega);
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar()) {
            boolean autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                marcarPedidoEnReparto(con, entrega.getIdPedido());
                int id;
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    asignarDatos(ps, entrega);
                    ps.executeUpdate();
                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        if (!claves.next()) throw new SQLException("La base de datos no devolvió el ID de la entrega.");
                        id = claves.getInt(1);
                    }
                }
                con.commit();
                return id;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public List<Entrega> readAll() throws SQLException {
        return readAll(null, null);
    }

    /** Lista todas las entregas o aplica uno o ambos filtros por pedido y repartidor. */
    public List<Entrega> readAll(Integer idPedido, Integer idRepartidor) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                + "p.direccion AS direccion_pedido, r.nombre AS nombre_repartidor "
                + "FROM entregas e JOIN pedidos p ON p.id = e.id_pedido "
                + "JOIN repartidores r ON r.id = e.id_repartidor WHERE 1 = 1");
        if (idPedido != null) sql.append(" AND e.id_pedido = ?");
        if (idRepartidor != null) sql.append(" AND e.id_repartidor = ?");
        sql.append(" ORDER BY e.fecha DESC, e.hora DESC, e.id DESC");

        List<Entrega> entregas = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int indice = 1;
            if (idPedido != null) ps.setInt(indice++, idPedido);
            if (idRepartidor != null) ps.setInt(indice, idRepartidor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega entrega = new Entrega(rs.getInt("id_pedido"), rs.getInt("id_repartidor"),
                            rs.getDate("fecha"), rs.getTime("hora"));
                    entrega.setId(rs.getInt("id"));
                    entrega.setDireccionPedido(rs.getString("direccion_pedido"));
                    entrega.setNombreRepartidor(rs.getString("nombre_repartidor"));
                    entregas.add(entrega);
                }
            }
        }
        return entregas;
    }

    public boolean update(Entrega entrega) throws SQLException {
        validar(entrega);
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarDatos(ps, entrega);
            ps.setInt(5, entrega.getId());
            if (ps.executeUpdate() > 0) return true;
            try (PreparedStatement existe = con.prepareStatement("SELECT 1 FROM entregas WHERE id = ?")) {
                existe.setInt(1, entrega.getId());
                try (ResultSet rs = existe.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM entregas WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void asignarDatos(PreparedStatement ps, Entrega entrega) throws SQLException {
        ps.setInt(1, entrega.getIdPedido());
        ps.setInt(2, entrega.getIdRepartidor());
        ps.setDate(3, entrega.getFecha());
        ps.setTime(4, entrega.getHora());
    }

    /** Cambia únicamente el pedido asignado y evita volver a poner en reparto uno entregado. */
    private void marcarPedidoEnReparto(Connection con, int idPedido) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE pedidos SET estado = 'EN_REPARTO' WHERE id = ? AND estado <> 'ENTREGADO'")) {
            ps.setInt(1, idPedido);
            if (ps.executeUpdate() > 0) return;
        }

        try (PreparedStatement ps = con.prepareStatement("SELECT estado FROM pedidos WHERE id = ?")) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("El pedido seleccionado ya no existe.");
                if ("ENTREGADO".equals(rs.getString("estado"))) {
                    throw new SQLException("No se puede asignar un pedido que ya está ENTREGADO.");
                }
            }
        }
    }

    private void validar(Entrega entrega) throws SQLException {
        if (entrega == null || entrega.getIdPedido() <= 0 || entrega.getIdRepartidor() <= 0) {
            throw new SQLException("Selecciona un pedido y un repartidor válidos.");
        }
        if (entrega.getFecha() == null || entrega.getHora() == null) {
            throw new SQLException("La fecha y la hora de la entrega son obligatorias.");
        }
    }
}
