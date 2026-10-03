package DAO;

import Conexiones.ConexionBD;
import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Operaciones de persistencia para pedidos, con filtros opcionales por tipo y estado. */
public class PedidoDAO {
    private static final Set<String> TIPOS = Set.of("COMIDA", "ENCOMIENDA", "EXPRESS");
    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "EN_REPARTO", "ENTREGADO");

    public int create(Pedido pedido) throws SQLException {
        validar(pedido);
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(ps, pedido);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("La base de datos no devolvió el ID del pedido.");
    }

    public List<Pedido> readAll() throws SQLException {
        return readAll(null, null);
    }

    /** Los filtros nulos se ignoran. */
    public List<Pedido> readAll(String estado, String tipo) throws SQLException {
        validarFiltro(estado, ESTADOS, "estado");
        validarFiltro(tipo, TIPOS, "tipo");
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1 = 1");
        if (estado != null) sql.append(" AND estado = ?");
        if (tipo != null) sql.append(" AND tipo = ?");
        sql.append(" ORDER BY id DESC");

        List<Pedido> pedidos = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int indice = 1;
            if (estado != null) ps.setString(indice++, estado);
            if (tipo != null) ps.setString(indice, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = crearModelo(rs.getString("tipo"), rs.getString("direccion"), rs.getString("estado"));
                    pedido.setId(rs.getInt("id"));
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    public boolean update(Pedido pedido) throws SQLException {
        validar(pedido);
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarDatos(ps, pedido);
            ps.setInt(4, pedido.getId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
            return existe(con, pedido.getId());
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM pedidos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Compatibilidad con la ventana de pedidos de la versión anterior. */
    @Deprecated
    public int guardar(Pedido pedido) throws SQLException {
        return create(pedido);
    }

    /** Compatibilidad con la ventana de pedidos de la versión anterior. */
    @Deprecated
    public List<Object[]> listar() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        for (Pedido pedido : readAll()) {
            filas.add(new Object[]{pedido.getId(), pedido.getDireccion(), pedido.getTipo(), pedido.getEstado()});
        }
        return filas;
    }

    /** Compatibilidad con la ventana de pedidos de la versión anterior. */
    @Deprecated
    public boolean actualizarEstado(int id, String estado) throws SQLException {
        if (!ESTADOS.contains(estado)) throw new SQLException("El estado seleccionado no es válido.");
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("UPDATE pedidos SET estado = ? WHERE id = ?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0 || existe(con, id);
        }
    }

    private void asignarDatos(PreparedStatement ps, Pedido pedido) throws SQLException {
        ps.setString(1, pedido.getDireccion().trim());
        ps.setString(2, pedido.getTipo());
        ps.setString(3, pedido.getEstado());
    }

    private void validar(Pedido pedido) throws SQLException {
        if (pedido == null || pedido.getDireccion() == null || pedido.getDireccion().isBlank()
                || pedido.getDireccion().trim().length() > 100) {
            throw new SQLException("La dirección es obligatoria y debe tener como máximo 100 caracteres.");
        }
        if (!TIPOS.contains(pedido.getTipo())) throw new SQLException("El tipo de pedido no es válido.");
        if (!ESTADOS.contains(pedido.getEstado())) throw new SQLException("El estado del pedido no es válido.");
    }

    private void validarFiltro(String valor, Set<String> permitidos, String campo) throws SQLException {
        if (valor != null && !permitidos.contains(valor)) throw new SQLException("El filtro de " + campo + " no es válido.");
    }

    private Pedido crearModelo(String tipo, String direccion, String estado) throws SQLException {
        return switch (tipo) {
            case "COMIDA" -> new PedidoComida(direccion, estado);
            case "ENCOMIENDA" -> new PedidoEncomienda(direccion, estado);
            case "EXPRESS" -> new PedidoExpress(direccion, estado);
            default -> throw new SQLException("Tipo de pedido desconocido: " + tipo);
        };
    }

    private boolean existe(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM pedidos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}