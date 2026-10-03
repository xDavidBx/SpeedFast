package Conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = configuracion("speedfast.db.url", "SPEEDFAST_DB_URL",
            "jdbc:mysql://localhost:3306/speedfast_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = configuracion("speedfast.db.user", "SPEEDFAST_DB_USER", "root");
    private static final String PASSWORD = configuracion("speedfast.db.password", "SPEEDFAST_DB_PASSWORD", "ADMIN");

    private ConexionBD() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String configuracion(String propiedad, String variable, String valorPorDefecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variable);
        }
        return valor == null || valor.isBlank() ? valorPorDefecto : valor;
    }
}
