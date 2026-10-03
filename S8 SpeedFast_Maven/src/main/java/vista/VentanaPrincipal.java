package vista;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

/** Ventana principal con una pestaña CRUD por entidad. */
public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de pedidos y entregas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1150, 700);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        PanelEntregas[] entregas = new PanelEntregas[1];
        PanelPedidos[] pedidos = new PanelPedidos[1];
        Runnable actualizarCombosEntrega = () -> {
            if (entregas[0] != null) entregas[0].refrescarCatalogos();
        };

        pestañas.addTab("Repartidores", new PanelRepartidores(actualizarCombosEntrega));
        pedidos[0] = new PanelPedidos(actualizarCombosEntrega);
        pestañas.addTab("Pedidos", pedidos[0]);
        entregas[0] = new PanelEntregas(() -> pedidos[0].refrescar());
        pestañas.addTab("Entregas", entregas[0]);
        pestañas.addChangeListener(e -> {
            if (pestañas.getSelectedComponent() == entregas[0]) entregas[0].refrescarCatalogos();
            if (pestañas.getSelectedComponent() == pedidos[0]) pedidos[0].refrescar();
        });
        setContentPane(pestañas);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
