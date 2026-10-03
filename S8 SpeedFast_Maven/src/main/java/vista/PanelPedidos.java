package vista;

import DAO.PedidoDAO;
import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

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
import java.util.List;

/** Formulario Swing para registrar, filtrar y administrar pedidos. */
public class PanelPedidos extends JPanel {
    private static final String TODOS = "TODOS";
    private static final String[] TIPOS = {"COMIDA", "ENCOMIENDA", "EXPRESS"};
    private static final String[] ESTADOS = {"PENDIENTE", "EN_REPARTO", "ENTREGADO"};
    private final PedidoDAO dao = new PedidoDAO();
    private final JTextField txtDireccion = new JTextField(24);
    private final JComboBox<String> cmbTipo = new JComboBox<>(TIPOS);
    private final JComboBox<String> cmbEstado = new JComboBox<>(ESTADOS);
    private final JComboBox<String> filtroTipo = new JComboBox<>(new String[]{TODOS, "COMIDA", "ENCOMIENDA", "EXPRESS"});
    private final JComboBox<String> filtroEstado = new JComboBox<>(new String[]{TODOS, "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final Runnable alCambiarCatalogo;
    private int idSeleccionado = -1;

    public PanelPedidos(Runnable alCambiarCatalogo) {
        this.alCambiarCatalogo = alCambiarCatalogo;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel superior = new JPanel(new BorderLayout(6, 6));
        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Dirección:"));
        formulario.add(txtDireccion);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);
        JButton btnCrear = new JButton("Registrar");
        JButton btnEditar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar formulario");
        formulario.add(btnCrear);
        formulario.add(btnEditar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);
        superior.add(formulario, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros opcionales"));
        filtros.add(new JLabel("Estado:"));
        filtros.add(filtroEstado);
        filtros.add(new JLabel("Tipo:"));
        filtros.add(filtroTipo);
        JButton btnFiltrar = new JButton("Aplicar filtros");
        JButton btnTodos = new JButton("Mostrar todos");
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
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnFiltrar.addActionListener(e -> refrescar());
        btnTodos.addActionListener(e -> {
            filtroEstado.setSelectedItem(TODOS);
            filtroTipo.setSelectedItem(TODOS);
            refrescar();
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        refrescar();
    }

    public void refrescar() {
        try {
            String estado = valorFiltro(filtroEstado);
            String tipo = valorFiltro(filtroTipo);
            List<Pedido> pedidos = dao.readAll(estado, tipo);
            modelo.setRowCount(0);
            for (Pedido pedido : pedidos) {
                modelo.addRow(new Object[]{pedido.getId(), pedido.getDireccion(), pedido.getTipo(), pedido.getEstado()});
            }
        } catch (SQLException ex) {
            error("No se pudieron cargar los pedidos", ex);
        }
    }

    private void crear() {
        Pedido pedido = desdeFormulario(-1);
        if (pedido == null) return;
        try {
            int id = dao.create(pedido);
            refrescar();
            limpiarFormulario();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Pedido registrado. ID: " + id);
        } catch (SQLException ex) {
            error("No se pudo registrar el pedido", ex);
        }
    }

    private void editar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona un pedido de la tabla para editarlo.");
            return;
        }
        Pedido pedido = desdeFormulario(idSeleccionado);
        if (pedido == null) return;
        try {
            if (!dao.update(pedido)) {
                aviso("No se encontró el pedido seleccionado.");
                return;
            }
            refrescar();
            limpiarFormulario();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Pedido actualizado.");
        } catch (SQLException ex) {
            error("No se pudo actualizar el pedido", ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona un pedido de la tabla para eliminarlo.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar el pedido seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            if (!dao.delete(idSeleccionado)) {
                aviso("No se encontró el pedido seleccionado.");
                return;
            }
            refrescar();
            limpiarFormulario();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Pedido eliminado.");
        } catch (SQLException ex) {
            error("No se pudo eliminar. Si tiene una entrega asociada, elimina o reasigna esa entrega primero", ex);
        }
    }

    private Pedido desdeFormulario(int id) {
        String direccion = txtDireccion.getText().trim();
        if (direccion.isBlank()) {
            aviso("La dirección del pedido es obligatoria.");
            txtDireccion.requestFocusInWindow();
            return null;
        }
        if (direccion.length() > 100) {
            aviso("La dirección debe tener como máximo 100 caracteres.");
            txtDireccion.requestFocusInWindow();
            return null;
        }
        String tipo = (String) cmbTipo.getSelectedItem();
        String estado = (String) cmbEstado.getSelectedItem();
        Pedido pedido = switch (tipo) {
            case "COMIDA" -> new PedidoComida(direccion, estado);
            case "ENCOMIENDA" -> new PedidoEncomienda(direccion, estado);
            default -> new PedidoExpress(direccion, estado);
        };
        pedido.setId(id);
        return pedido;
    }

    private String valorFiltro(JComboBox<String> combo) {
        String valor = (String) combo.getSelectedItem();
        return TODOS.equals(valor) ? null : valor;
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) return;
        int fila = tabla.convertRowIndexToModel(filaVista);
        idSeleccionado = ((Number) modelo.getValueAt(fila, 0)).intValue();
        txtDireccion.setText(String.valueOf(modelo.getValueAt(fila, 1)));
        cmbTipo.setSelectedItem(modelo.getValueAt(fila, 2));
        cmbEstado.setSelectedItem(modelo.getValueAt(fila, 3));
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        tabla.clearSelection();
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedItem("PENDIENTE");
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String texto, SQLException ex) {
        JOptionPane.showMessageDialog(this, texto + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
