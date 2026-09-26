package vista;

import DAO.PedidoDAO;
import modelo.Pedido;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaRegistroPedido extends JFrame {
    public VentanaRegistroPedido() {
        setTitle("Registrar Pedido");
        setSize(350, 260);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 5, 5));

        JTextField txtDireccion = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"COMIDA", "ENCOMIENDA", "EXPRESS"});

        add(new JLabel("Dirección:"));
        add(txtDireccion);
        add(new JLabel("Tipo:"));
        add(cmbTipo);

        JButton btnGuardar = new JButton("Registrar Pedido");
        btnGuardar.addActionListener(e -> {
            String dir = txtDireccion.getText().trim();
            if (dir.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dirección obligatoria");
                return;
            }
            double distanciaKm = 0;
            Pedido p = new Pedido(dir, (String) cmbTipo.getSelectedItem(), distanciaKm, "PENDIENTE") {
                @Override
                public int calcularTiempoEntrega() {
                    return 0;
                }

                @Override
                public void asignarRepartidor() {

                }

                @Override
                public void asignarRepartidor(String nombreRepartidor) {

                }
            };
            try {
                int codigo = new PedidoDAO().guardar(p);
                JOptionPane.showMessageDialog(this, "Pedido guardado. Código: " + codigo);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el pedido: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(btnGuardar);
    }
}
