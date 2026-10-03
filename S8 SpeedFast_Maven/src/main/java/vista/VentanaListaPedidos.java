package vista;

import DAO.PedidoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaListaPedidos extends JFrame {
    public VentanaListaPedidos() {
        setTitle("Lista de Pedidos");
        setSize(550, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnas = {"ID", "Dirección", "Tipo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargar(modelo));
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN_REPARTO", "ENTREGADO"});
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                int filaModelo = tabla.convertRowIndexToModel(fila);
                cmbEstado.setSelectedItem(modelo.getValueAt(filaModelo, 3));
            }
        });

        JButton btnActualizarEstado = new JButton("Actualizar estado");
        btnActualizarEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
                return;
            }

            int filaModelo = tabla.convertRowIndexToModel(fila);
            int id = (int) modelo.getValueAt(filaModelo, 0);
            String estado = (String) cmbEstado.getSelectedItem();
            String estadoActual = (String) modelo.getValueAt(filaModelo, 3);
            if (estado.equals(estadoActual)) {
                JOptionPane.showMessageDialog(this, "El pedido ya tiene el estado " + estado + ".");
                return;
            }

            try {
                if (new PedidoDAO().actualizarEstado(id, estado)) {
                    cargar(modelo);
                    JOptionPane.showMessageDialog(this, "Estado del pedido actualizado.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el pedido seleccionado.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el estado: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(new JLabel("Nuevo estado:"));
        acciones.add(cmbEstado);
        acciones.add(btnActualizarEstado);
        acciones.add(btnRefrescar);
        add(acciones, BorderLayout.SOUTH);

        cargar(modelo);
    }

    private void cargar(DefaultTableModel modelo) {
        try {
            List<Object[]> pedidos = new PedidoDAO().listar();
            modelo.setRowCount(0);
            for (Object[] pedido : pedidos) {
                modelo.addRow(pedido);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los pedidos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
