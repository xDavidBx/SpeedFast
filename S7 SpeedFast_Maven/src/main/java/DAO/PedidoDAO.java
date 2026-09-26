package DAO;

import Conexiones.ConexionBD;
import modelo.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    public List<Object[]> listar() throws SQLException {
        String sql = "SELECT id, direccion, tipo, estado FROM pedido ORDER BY id DESC";
        List<Object[]> pedidos = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pedidos.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        rs.getString("tipo"),
                        rs.getString("estado")
                });
            }
        }
        return pedidos;
    }

    public int guardar(Pedido p) throws SQLException {
        String sql = "INSERT INTO pedido (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getTipo());
            ps.setString(3, p.getEstado());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se pudo crear el pedido.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("La base de datos no devolvió el código del pedido.");
    }

    public boolean actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            if (ps.executeUpdate() > 0) {
                return true;
            }
            try (PreparedStatement existe = con.prepareStatement("SELECT 1 FROM pedido WHERE id = ?")) {
                existe.setInt(1, id);
                try (ResultSet rs = existe.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

}
