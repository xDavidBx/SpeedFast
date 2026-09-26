package vista;

import DAO.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de Entregas");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnRegistrarPedido = new JButton("Registrar Pedido");
        JButton btnListarPedidos = new JButton("Listar Pedidos");
        JButton btnRegistrarRepartidor = new JButton("Registrar Repartidor");
        JButton btnSalir = new JButton("Salir");

        btnRegistrarPedido.addActionListener(e -> new VentanaRegistroPedido().setVisible(true));
        btnListarPedidos.addActionListener(e -> new VentanaListaPedidos().setVisible(true));
        btnRegistrarRepartidor.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del repartidor:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                new RepartidorDAO().guardar(new Repartidor(nombre.trim()));
                JOptionPane.showMessageDialog(this, "Repartidor guardado");
            }
        });
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnRegistrarPedido);
        add(btnListarPedidos);
        add(btnRegistrarRepartidor);
        add(btnSalir);
    }
}