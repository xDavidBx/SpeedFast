package vista;

import DAO.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

/** Formulario Swing para administrar repartidores. */
public class PanelRepartidores extends JPanel {
    private final RepartidorDAO dao = new RepartidorDAO();
    private final JTextField txtNombre = new JTextField(28);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final Runnable alCambiarCatalogo;
    private int idSeleccionado = -1;

    public PanelRepartidores(Runnable alCambiarCatalogo) {
        this.alCambiarCatalogo = alCambiarCatalogo;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre del repartidor:"));
        formulario.add(txtNombre);
        JButton btnCrear = new JButton("Registrar");
        JButton btnEditar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnActualizar = new JButton("Actualizar tabla");
        formulario.add(btnCrear);
        formulario.add(btnEditar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);
        formulario.add(btnActualizar);
        add(formulario, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crear());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnActualizar.addActionListener(e -> refrescar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        refrescar();
    }

    public void refrescar() {
        try {
            modelo.setRowCount(0);
            for (Repartidor repartidor : dao.readAll()) {
                modelo.addRow(new Object[]{repartidor.getId(), repartidor.getNombre()});
            }
        } catch (SQLException ex) {
            error("No se pudieron cargar los repartidores", ex);
        }
    }

    private void crear() {
        String nombre = txtNombre.getText().trim();
        if (!validarNombre(nombre)) return;
        try {
            int id = dao.create(new Repartidor(nombre));
            refrescar();
            limpiar();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Repartidor registrado. ID: " + id);
        } catch (SQLException ex) {
            error("No se pudo registrar el repartidor", ex);
        }
    }

    private void editar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona un repartidor de la tabla para editarlo.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (!validarNombre(nombre)) return;
        try {
            Repartidor repartidor = new Repartidor(nombre);
            repartidor.setId(idSeleccionado);
            if (!dao.update(repartidor)) {
                aviso("No se encontró el repartidor seleccionado.");
                return;
            }
            refrescar();
            limpiar();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Repartidor actualizado.");
        } catch (SQLException ex) {
            error("No se pudo actualizar el repartidor", ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            aviso("Selecciona un repartidor de la tabla para eliminarlo.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar el repartidor seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            if (!dao.delete(idSeleccionado)) {
                aviso("No se encontró el repartidor seleccionado.");
                return;
            }
            refrescar();
            limpiar();
            alCambiarCatalogo.run();
            JOptionPane.showMessageDialog(this, "Repartidor eliminado.");
        } catch (SQLException ex) {
            error("No se pudo eliminar. Si tiene entregas asociadas, elimina o reasigna esas entregas primero", ex);
        }
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) return;
        int fila = tabla.convertRowIndexToModel(filaVista);
        idSeleccionado = ((Number) modelo.getValueAt(fila, 0)).intValue();
        txtNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
    }

    private void limpiar() {
        idSeleccionado = -1;
        tabla.clearSelection();
        txtNombre.setText("");
    }

    private boolean validarNombre(String nombre) {
        if (nombre.isBlank()) {
            aviso("El nombre del repartidor es obligatorio.");
            txtNombre.requestFocusInWindow();
            return false;
        }
        if (nombre.length() > 100) {
            aviso("El nombre debe tener como máximo 100 caracteres.");
            txtNombre.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String texto, SQLException ex) {
        JOptionPane.showMessageDialog(this, texto + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
