package modelo;

import interfaces.EstadoPedido;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de Entregas");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnRegistrar = new JButton("Registrar Pedido");
        JButton btnListar = new JButton("Listar Pedidos");
        JButton btnAsignar = new JButton("Asignar Repartidor / Iniciar Entrega");
        JButton btnSalir = new JButton("Salir");

        btnRegistrar.addActionListener(e -> new VentanaRegistroPedido().setVisible(true));
        btnListar.addActionListener(e -> new VentanaListaPedidos().setVisible(true));
        btnAsignar.addActionListener(e -> {
            for (Pedido p : ControladorPedidos.getPedidos()) {
                if (p.getEstado() == EstadoPedido.PENDIENTE) {
                    p.setEstado(EstadoPedido.EN_REPARTO);
                }
            }
            JOptionPane.showMessageDialog(this, "Pedidos asignados e iniciados");
        });
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnRegistrar);
        add(btnListar);
        add(btnAsignar);
        add(btnSalir);
    }
}