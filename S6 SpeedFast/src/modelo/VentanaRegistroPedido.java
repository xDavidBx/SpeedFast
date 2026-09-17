package modelo;

import javax.swing.*;
import java.awt.*;

public class VentanaRegistroPedido extends JFrame {
    public VentanaRegistroPedido() {
        setTitle("Registrar Pedido");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 5, 5));

        JTextField txtId = new JTextField();
        JTextField txtDireccion = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"comida", "encomienda", "express"});

        add(new JLabel("ID:"));
        add(txtId);
        add(new JLabel("Dirección:"));
        add(txtDireccion);
        add(new JLabel("Tipo:"));
        add(cmbTipo);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String dir = txtDireccion.getText().trim();
                String tipo = (String) cmbTipo.getSelectedItem();

                if (dir.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dirección obligatoria");
                    return;
                }

                Pedido p = new Pedido(id, dir, tipo) {
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
                ControladorPedidos.agregar(p);
                JOptionPane.showMessageDialog(this, "Pedido registrado correctamente");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID debe ser numérico");
            }
        });

        add(new JLabel());
        add(btnGuardar);
    }
}