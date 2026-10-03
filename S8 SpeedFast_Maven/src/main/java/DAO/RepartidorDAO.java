package DAO;

import Conexiones.ConexionBD;
import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Operaciones de persistencia para repartidores. */
public class RepartidorDAO {
    public int create(Repartidor repartidor) throws SQLException {
        if (repartidor == null) throw new SQLException("Debes indicar un repartidor.");
        validarNombre(repartidor.getNombre());
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, repartidor.getNombre().trim());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("La base de datos no devolvió el ID del repartidor.");
    }

    public List<Repartidor> readAll() throws SQLException {
        String sql = "SELECT id, nombre FROM repartidores ORDER BY nombre, id";
        List<Repartidor> repartidores = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Repartidor repartidor = new Repartidor(rs.getString("nombre"));
                repartidor.setId(rs.getInt("id"));
                repartidores.add(repartidor);
            }
        }
        return repartidores;
    }

    public boolean update(Repartidor repartidor) throws SQLException {
        if (repartidor == null) throw new SQLException("Debes indicar un repartidor.");
        validarNombre(repartidor.getNombre());
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, repartidor.getNombre().trim());
            ps.setInt(2, repartidor.getId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
            return existe(con, repartidor.getId()); // UPDATE puede informar cero si el nombre no cambió.
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Compatibilidad con el formulario previo del proyecto. */
    @Deprecated
    public void guardar(Repartidor repartidor) {
        try {
            create(repartidor);
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo guardar el repartidor.", ex);
        }
    }

    /** Compatibilidad con el formulario previo del proyecto. */
    @Deprecated
    public List<Repartidor> listarTodos() {
        try {
            return readAll();
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudieron listar los repartidores.", ex);
        }
    }

    private boolean existe(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM repartidores WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void validarNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.isBlank() || nombre.trim().length() > 100) {
            throw new SQLException("El nombre es obligatorio y debe tener como máximo 100 caracteres.");
        }
    }
}