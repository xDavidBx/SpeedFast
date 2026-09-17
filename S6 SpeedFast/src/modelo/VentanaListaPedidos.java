package modelo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaListaPedidos extends JFrame {
    public VentanaListaPedidos() {
        setTitle("Lista de Pedidos");
        setSize(500, 300);
        setLocationRelativeTo(null);

        String[] columnas = {"ID", "Dirección", "Tipo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Pedido p : ControladorPedidos.getPedidos()) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getDireccionEntrega(),
                    p.getTipo(),
                    p.getEstado()
            });
        }

        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> {
            modelo.setRowCount(0);
            for (Pedido p : ControladorPedidos.getPedidos()) {
                modelo.addRow(new Object[]{
                        p.getId(),
                        p.getDireccionEntrega(),
                        p.getTipo(),
                        p.getEstado()
                });
            }
        });
        add(btnRefrescar, BorderLayout.SOUTH);
    }
}