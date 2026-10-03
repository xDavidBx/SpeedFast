package vista;

import DAO.EntregaDAO;
import DAO.PedidoDAO;
import DAO.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Formulario Swing para administrar entregas y filtrar por pedido o repartidor. */
public class PanelEntregas extends JPanel {
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final JComboBox<Pedido> cmbPedido = new JComboBox<>();
    private final JComboBox<Repartidor> cmbRepartidor = new JComboBox<>();
    private final JComboBox<OpcionPedido> filtroPedido = new JComboBox<>();
    private final JComboBox<OpcionRepartidor> filtroRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField txtHora = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString(), 7);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Pedido", "Repartidor", "Fecha (AAAA-MM-DD)", "Hora"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final Runnable alCambiarPedido;
    private int idSeleccionado = -1;
    private int idPedidoSeleccionado = -1;

    public PanelEntregas() {
        this(() -> {});
    }

    public PanelEntregas(Runnable alCambiarPedido) {
        this.alCambiarPedido = alCambiarPedido;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel superior = new JPanel(new BorderLayout(6, 6));
        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Pedido:"));
        formulario.add(cmbPedido);
        formulario.add(new JLabel("Repartidor:"));
        formulario.add(cmbRepartidor);
        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        formulario.add(txtFecha);
        formulario.add(new JLabel("Hora (HH:mm):"));
        formulario.add(txtHora);
        JButton btnCrear = new JButton("Registrar");
        JButton btnEditar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnConfirmar = new JButton("Confirmar entrega");
        JButton btnLimpiar = new JButton("Limpiar formulario");
        formulario.add(btnCrear);
        formulario.add(btnEditar);
        formulario.add(btnEliminar);
        formulario.add(btnConfirmar);
        formulario.add(btnLimpiar);
        superior.add(formulario, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros opcionales"));
        filtros.add(new JLabel("Pedido:"));
        filtros.add(filtroPedido);
        filtros.add(new JLabel("Repartidor:"));
        filtros.add(filtroRepartidor);
        JButton btnFiltrar = new JButton("Aplicar filtros");
        JButton btnTodos = new JButton("Mostrar todas");
        filtros.add(btnFiltrar);
        filtros.add(btnTodos);
        superior.add(filtros, BorderLayout.CENTER);
        add(superior, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crear());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnConfirmar.addActionListener(e -> confirmarEntrega());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnFiltrar.addActionListener(e -> refrescar());
        btnTodos.addActionListener(e -> {
            filtroPedido.setSelectedIndex(0);
            filtroRepartidor.setSelectedIndex(0);
            refrescar();
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        refrescarCatalogos();
    }

    /** Actualiza los combos de relaciones después de cambios en pedidos o repartidores. */
    public void refrescarCatalogos() {
        Integer idPedidoFormulario = idPedidoSeleccionado();
        Integer idRepartidorFormulario = idRepartidorSeleccionado();
        Integer idPedidoFiltrado = idPedidoFiltro();
        Integer idRepartidorFiltrado = idRepartidorFiltro();
        try {
            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();
            cmbPedido.removeAllItems();
            cmbRepartidor.removeAllItems();
            filtroPedido.removeAllItems();
            filtroRepartidor.removeAllItems();
            filtroPedido.addItem(new OpcionPedido(null));
            filtroRepartidor.addItem(new OpcionRepartidor(null));
            for (Pedido pedido : pedidos) {
                cmbPedido.addItem(pedido);
                filtroPedido.addItem(new OpcionPedido(pedido));
            }
            for (Repartidor repartidor : repartidores) {
                cmbRepartidor.addItem(repartidor);
                filtroRepartidor.addItem(new OpcionRepartidor(repartidor));
            }
            seleccionarPedido(cmbPedido, idPedidoFormulario);
            seleccionarRepartidor(cmbRepartidor, idRepartidorFormulario);
            seleccionarPedidoFiltro(idPedidoFiltrado);
            seleccionarRepartidorFiltro(idRepartidorFiltrado);
            refrescar();
        } catch (SQLException ex) {
            error("No se pudieron cargar los pedidos y repartidores", ex);
        }
    }

    public void refrescar() {
        try {
            List<Entrega> entregas = entregaDAO.readAll(idPedidoFiltro(), idRepartidorFiltro());
            modelo.setRowCount(0);
            for (Entrega entrega : entregas) {
                modelo.addRow(new Object[]{entrega.getId(),
                        entrega.getIdPedido() + " - " + entrega.getDireccionPedido(),
                        entrega.getIdRepartidor() + " - " + entrega.getNombreRepartidor(),
                        entrega.getFecha(), entrega.getHora()});
            }
        } catch (SQLException ex) {
            error("No se pudieron cargar las entregas", ex);
        }
    }

    private void crear() {
        Entrega entrega = desdeFormulario(-1);
        if (entrega == null) return;
        try {
            int id = entregaDAO.create(entrega);
            refrescar();
            limpiarFormulario();
            alCambiarPedido.run();
            JOptionPane.showMessageDialog(this, "Entrega registrada. ID: " + id);
        } catch (SQLException ex) {
            error("No se pudo registrar la entrega", ex);
        }
    }

    private void editar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona una entrega de la tabla para editarla.");
            return;
        }
        Entrega entrega = desdeFormulario(idSeleccionado);
        if (entrega == null) return;
        try {
            if (!entregaDAO.update(entrega)) {
                aviso("No se encontró la entrega seleccionada.");
                return;
            }
            refrescar();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Entrega actualizada.");
        } catch (SQLException ex) {
            error("No se pudo actualizar la entrega", ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona una entrega de la tabla para eliminarla.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar la entrega seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            if (!entregaDAO.delete(idSeleccionado)) {
                aviso("No se encontró la entrega seleccionada.");
                return;
            }
            refrescar();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Entrega eliminada.");
        } catch (SQLException ex) {
            error("No se pudo eliminar la entrega", ex);
        }
    }

    private void confirmarEntrega() {
        if (idSeleccionado < 0 || idPedidoSeleccionado < 0) {
            aviso("Selecciona una entrega de la tabla para confirmar el pedido.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Confirmar que se entregó el pedido " + idPedidoSeleccionado + "?",
                "Confirmar entrega", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            if (!pedidoDAO.actualizarEstado(idPedidoSeleccionado, "ENTREGADO")) {
                aviso("No se encontró el pedido de esta entrega.");
                return;
            }
            alCambiarPedido.run();
            JOptionPane.showMessageDialog(this, "Pedido " + idPedidoSeleccionado + " marcado como ENTREGADO.");
        } catch (SQLException ex) {
            error("No se pudo confirmar la entrega", ex);
        }
    }

    private Entrega desdeFormulario(int id) {
        Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cmbRepartidor.getSelectedItem();
        if (pedido == null || repartidor == null) {
            aviso("Primero registra al menos un pedido y un repartidor.");
            return null;
        }
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            Entrega entrega = new Entrega(pedido.getId(), repartidor.getId(),
                    java.sql.Date.valueOf(fecha), java.sql.Time.valueOf(hora));
            entrega.setId(id);
            return entrega;
        } catch (java.time.format.DateTimeParseException ex) {
            aviso("Fecha u hora no válida. Usa AAAA-MM-DD (ejemplo: 2026-10-02) y HH:mm (ejemplo: 14:30).");
            return null;
        }
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) return;
        int fila = tabla.convertRowIndexToModel(filaVista);
        idSeleccionado = ((Number) modelo.getValueAt(fila, 0)).intValue();
        String pedido = String.valueOf(modelo.getValueAt(fila, 1));
        String repartidor = String.valueOf(modelo.getValueAt(fila, 2));
        int idPedido = Integer.parseInt(pedido.substring(0, pedido.indexOf(" - ")));
        idPedidoSeleccionado = idPedido;
        int idRepartidor = Integer.parseInt(repartidor.substring(0, repartidor.indexOf(" - ")));
        seleccionarPedido(cmbPedido, idPedido);
        seleccionarRepartidor(cmbRepartidor, idRepartidor);
        txtFecha.setText(String.valueOf(modelo.getValueAt(fila, 3)));
        txtHora.setText(String.valueOf(modelo.getValueAt(fila, 4)));
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        idPedidoSeleccionado = -1;
        tabla.clearSelection();
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().withSecond(0).withNano(0).toString());
    }

    private Integer idPedidoSeleccionado() {
        Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
        return pedido == null ? null : pedido.getId();
    }

    private Integer idRepartidorSeleccionado() {
        Repartidor repartidor = (Repartidor) cmbRepartidor.getSelectedItem();
        return repartidor == null ? null : repartidor.getId();
    }

    private Integer idPedidoFiltro() {
        OpcionPedido opcion = (OpcionPedido) filtroPedido.getSelectedItem();
        return opcion == null ? null : opcion.id();
    }

    private Integer idRepartidorFiltro() {
        OpcionRepartidor opcion = (OpcionRepartidor) filtroRepartidor.getSelectedItem();
        return opcion == null ? null : opcion.id();
    }

    private void seleccionarPedido(JComboBox<Pedido> combo, Integer id) {
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarRepartidor(JComboBox<Repartidor> combo, Integer id) {
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarPedidoFiltro(Integer id) {
        if (id == null) return;
        for (int i = 1; i < filtroPedido.getItemCount(); i++) {
            if (id.equals(filtroPedido.getItemAt(i).id())) {
                filtroPedido.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarRepartidorFiltro(Integer id) {
        if (id == null) return;
        for (int i = 1; i < filtroRepartidor.getItemCount(); i++) {
            if (id.equals(filtroRepartidor.getItemAt(i).id())) {
                filtroRepartidor.setSelectedIndex(i);
                return;
            }
        }
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String texto, SQLException ex) {
        JOptionPane.showMessageDialog(this, texto + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private record OpcionPedido(Pedido pedido) {
        private Integer id() { return pedido == null ? null : pedido.getId(); }
        @Override public String toString() { return pedido == null ? "TODOS" : pedido.toString(); }
    }

    private record OpcionRepartidor(Repartidor repartidor) {
        private Integer id() { return repartidor == null ? null : repartidor.getId(); }
        @Override public String toString() { return repartidor == null ? "TODOS" : repartidor.toString(); }
    }
}
