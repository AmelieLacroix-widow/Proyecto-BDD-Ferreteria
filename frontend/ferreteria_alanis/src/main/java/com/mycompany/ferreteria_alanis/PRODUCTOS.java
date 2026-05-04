package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla del módulo de Productos (solo visible para ADMIN).
 *
 * Sub-módulos (pestañas):
 *   1. Producto     — formulario para crear/editar un producto  [FUNCIONAL]
 *   2. Departamento — gestión de departamentos                  [FUNCIONAL]
 *   3. Proveedor    — gestión de proveedores                    [FUNCIONAL]
 *   4. Catálogo     — tabla con todos los productos             [FUNCIONAL]
 */
public class PRODUCTOS extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(PRODUCTOS.class.getName());

    private static final Color COLOR_NARANJA = new Color(255, 153, 0);
    private static final Color COLOR_NEGRO   = Color.BLACK;
    private static final Color COLOR_FONDO   = new Color(230, 230, 230);

    private final String rol;
    private final String nombreUsuario;
    private final ApiClient api    = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    // ── Campos del formulario Producto ────────────────────────────────────
    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JRadioButton rdPza;
    private JRadioButton rdGranel;
    private JTextField txtPrecioCosto;
    private JTextField txtPorcentaje;
    private JTextField txtPrecioVenta;
    private JComboBox<ProductoDTO.DepartamentoRef> cmbDepartamento;
    private JComboBox<ProductoDTO.ProveedorRef>    cmbProveedor;
    private JCheckBox chkInventario;
    private JTextField txtExistencia;
    private JTextField txtMinimo;
    private JTextField txtMaximo;

    // ── Campos del sub-módulo Departamento ───────────────────────────────
    private DefaultTableModel modeloDepartamentos;
    private JTable tablaDepartamentos;
    private JTextField txtDepNombre;
    private JTextField txtDepDescripcion;
    private Integer depIdSeleccionado = null;

    // ── Campos del sub-módulo Proveedor ──────────────────────────────────
    private DefaultTableModel modeloProveedores;
    private JTable tablaProveedores;
    private JTextField txtProvNombre;
    private JTextField txtProvTelefono;
    private JTextField txtProvCorreo;
    private JTextField txtProvDireccion;
    private JTextField txtProvNotas;
    private Integer provIdSeleccionado = null;

    // ── Campos del sub-módulo Catálogo ────────────────────────────────────
    private DefaultTableModel modeloCatalogo;
    private JTextField txtCatBusqueda;

    // ─────────────────────────────────────────────────────────────────────────

    public PRODUCTOS(String rol, String nombreUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís – Productos");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        cargarDepartamentos();
        cargarProveedores();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI principal
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),    BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(COLOR_NEGRO);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_NEGRO);
        header.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        ImageIcon icon = new ImageIcon("logo.png");
        Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        izquierda.add(new JLabel(new ImageIcon(img)));
        JLabel lblNombre = new JLabel("<html>Ferretería e Instalaciones<br>Eléctricas Alanís</html>");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 12));
        izquierda.add(lblNombre);
        header.add(izquierda, BorderLayout.WEST);

        // Botón usuario con popup de sesión (CORREGIDO)
        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(COLOR_NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));
        header.add(btnUsuarioInfo, BorderLayout.EAST);
        nav.add(header, BorderLayout.NORTH);

        JPanel modulos = new JPanel(new BorderLayout());
        modulos.setBackground(new Color(50, 50, 50));
        JPanel izqMod = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        izqMod.setOpaque(false);

        JButton btnVentas = crearBtnNav("Ventas", false);
        btnVentas.addActionListener(e -> {
            new VENTAS(rol, nombreUsuario).setVisible(true);
            dispose();
        });
        izqMod.add(btnVentas);

        JButton btnProductos = crearBtnNav("Productos", true);
        izqMod.add(btnProductos);

        JButton btnInventario = crearBtnNav("Inventario", false);
        btnInventario.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Inventario",
                JOptionPane.INFORMATION_MESSAGE));
        izqMod.add(btnInventario);
        modulos.add(izqMod, BorderLayout.WEST);

        JPanel derMod = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        derMod.setOpaque(false);
        JButton btnCorte = crearBtnNav("Corte", false);
        btnCorte.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte",
                JOptionPane.INFORMATION_MESSAGE));
        JButton btnUsuario = crearBtnNav("Usuario", false);
        btnUsuario.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Usuario",
                JOptionPane.INFORMATION_MESSAGE));
        derMod.add(btnCorte);
        derMod.add(btnUsuario);
        modulos.add(derMod, BorderLayout.EAST);

        nav.add(modulos, BorderLayout.SOUTH);
        return nav;
    }

    private JButton crearBtnNav(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setBackground(activo ? COLOR_NARANJA : new Color(80, 80, 80));
        btn.setForeground(activo ? COLOR_NEGRO : Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 30));
        return btn;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("  Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 30));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Producto",     crearTabProducto());
        tabs.addTab("Departamento", crearTabDepartamento());
        tabs.addTab("Proveedor",    crearTabProveedor());
        tabs.addTab("Catálogo",     crearTabCatalogo());

        // Recargar datos al cambiar de pestaña
        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 1 -> cargarTablaDepartamentos();
                case 2 -> cargarTablaProveedores();
                case 3 -> cargarTablaCatalogo();
            }
        });

        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(tabs,      BorderLayout.CENTER);
        return contenido;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab 1: Producto
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearTabProducto() {
        JPanel tab = new JPanel(new BorderLayout(10, 0));
        tab.setBackground(COLOR_FONDO);
        tab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tab.add(crearFormulario(), BorderLayout.CENTER);
        tab.add(crearPanelLogo(), BorderLayout.EAST);
        return tab;
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_FONDO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 5, 5, 5);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        txtCodigo = new JTextField(20);
        agregarFila(form, g, fila++, "Código de barras", txtCodigo);

        txtDescripcion = new JTextField(20);
        agregarFila(form, g, fila++, "Descripción", txtDescripcion);

        rdPza    = new JRadioButton("Por Unidad/Pza", true);
        rdGranel = new JRadioButton("A Granel (Usa decimales)");
        rdPza.setBackground(COLOR_FONDO);
        rdGranel.setBackground(COLOR_FONDO);
        ButtonGroup grupoUnidad = new ButtonGroup();
        grupoUnidad.add(rdPza);
        grupoUnidad.add(rdGranel);
        JPanel panelUnidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelUnidad.setBackground(COLOR_FONDO);
        panelUnidad.add(rdPza);
        panelUnidad.add(rdGranel);
        agregarFila(form, g, fila++, "Se vende", panelUnidad);

        txtPrecioCosto = new JTextField(10);
        agregarFila(form, g, fila++, "Precio Costo", txtPrecioCosto);

        txtPorcentaje = new JTextField(10);
        txtPorcentaje.setToolTipText("Porcentaje de ganancia sobre el costo");
        agregarFila(form, g, fila++, "% Ganancia", txtPorcentaje);

        txtPrecioVenta = new JTextField(10);
        txtPrecioVenta.setEditable(false);
        txtPrecioVenta.setBackground(new Color(220, 220, 220));
        agregarFila(form, g, fila++, "Precio Venta", txtPrecioVenta);

        Runnable calcularPrecio = () -> {
            try {
                BigDecimal costo = new BigDecimal(txtPrecioCosto.getText().trim());
                BigDecimal pct   = new BigDecimal(txtPorcentaje.getText().trim());
                BigDecimal venta = costo.multiply(BigDecimal.ONE.add(pct.divide(
                    BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
                txtPrecioVenta.setText(venta.toString());
            } catch (Exception ignored) {
                txtPrecioVenta.setText("");
            }
        };
        txtPrecioCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calcularPrecio.run(); }
        });
        txtPorcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calcularPrecio.run(); }
        });

        cmbDepartamento = new JComboBox<>();
        agregarFila(form, g, fila++, "Departamento", cmbDepartamento);

        cmbProveedor = new JComboBox<>();
        agregarFila(form, g, fila++, "Proveedor", cmbProveedor);

        chkInventario = new JCheckBox("Este Producto utiliza inventario");
        chkInventario.setBackground(COLOR_FONDO);
        g.gridx = 0; g.gridy = fila++; g.gridwidth = 2;
        form.add(chkInventario, g);
        g.gridwidth = 1;

        txtExistencia = new JTextField(10);
        txtMinimo     = new JTextField(10);
        txtMaximo     = new JTextField(10);
        txtExistencia.setEnabled(false);
        txtMinimo.setEnabled(false);
        txtMaximo.setEnabled(false);

        chkInventario.addActionListener(e -> {
            boolean activo = chkInventario.isSelected();
            txtExistencia.setEnabled(activo);
            txtMinimo.setEnabled(activo);
            txtMaximo.setEnabled(activo);
        });

        agregarFila(form, g, fila++, "Hay",    txtExistencia);
        agregarFila(form, g, fila++, "Mínimo", txtMinimo);
        agregarFila(form, g, fila++, "Máximo", txtMaximo);

        JButton btnGuardar  = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.setBackground(new Color(51, 153, 51));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.addActionListener(e  -> guardarProducto());
        btnCancelar.addActionListener(e -> limpiarFormulario());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        botones.setBackground(COLOR_FONDO);
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        g.gridx = 0; g.gridy = fila; g.gridwidth = 2;
        g.insets = new Insets(15, 5, 5, 5);
        form.add(botones, g);

        return form;
    }

    private void agregarFila(JPanel panel, GridBagConstraints g, int fila,
                              String etiqueta, Component campo) {
        g.gridx = 0; g.gridy = fila; g.weightx = 0; g.gridwidth = 1;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        panel.add(campo, g);
    }

    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setPreferredSize(new Dimension(300, 0));
        ImageIcon icono = new ImageIcon("logo.png");
        Image img = icono.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
        panel.add(new JLabel(new ImageIcon(img)));
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab 2: Departamento
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearTabDepartamento() {
        JPanel tab = new JPanel(new BorderLayout(8, 0));
        tab.setBackground(COLOR_FONDO);
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Panel izquierdo: búsqueda + tabla ────────────────────────────
        JPanel izq = new JPanel(new BorderLayout(0, 6));
        izq.setBackground(COLOR_FONDO);

        JTextField txtBusquedaDep = new JTextField();
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(COLOR_FONDO);
        panelBusqueda.add(new JLabel("🔍"), BorderLayout.WEST);
        panelBusqueda.add(txtBusquedaDep, BorderLayout.CENTER);
        izq.add(panelBusqueda, BorderLayout.NORTH);

        modeloDepartamentos = new DefaultTableModel(new String[]{"Departamento", "Descripción"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDepartamentos = new JTable(modeloDepartamentos);
        estilizarTabla(tablaDepartamentos);
        tablaDepartamentos.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaDepartamentos.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Filtro de búsqueda
        txtBusquedaDep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBusquedaDep.getText().trim();
                if (texto.isEmpty()) {
                    tablaDepartamentos.setRowSorter(null);
                } else {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        new javax.swing.table.TableRowSorter<>(modeloDepartamentos);
                    tablaDepartamentos.setRowSorter(sorter);
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        izq.add(new JScrollPane(tablaDepartamentos), BorderLayout.CENTER);

        // ── Panel derecho: formulario + botones ──────────────────────────
        JPanel der = new JPanel(new BorderLayout(0, 8));
        der.setBackground(COLOR_FONDO);
        der.setPreferredSize(new Dimension(420, 0));

        DefaultTableModel modeloFormDep = new DefaultTableModel(
            new String[]{"Departamento", "Descripción"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaFormDep = new JTable(modeloFormDep);
        estilizarTabla(tablaFormDep);
        tablaFormDep.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaFormDep.getColumnModel().getColumn(1).setPreferredWidth(240);

        txtDepNombre      = new JTextField();
        txtDepDescripcion = new JTextField();

        // Cuando se selecciona fila en la tabla izquierda → llenar form
        tablaDepartamentos.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && tablaDepartamentos.getSelectedRow() >= 0) {
                int fila = tablaDepartamentos.getSelectedRow();
                String nombre = (String) modeloDepartamentos.getValueAt(fila, 0);
                String desc   = (String) modeloDepartamentos.getValueAt(fila, 1);
                txtDepNombre.setText(nombre);
                txtDepDescripcion.setText(desc);
                // Obtener ID buscando en la lista cargada
                buscarIdDepartamentoPorNombre(nombre);
                // Reflejar en tabla derecha
                modeloFormDep.setRowCount(0);
                modeloFormDep.addRow(new Object[]{nombre, desc});
            }
        });

        JScrollPane scrollFormDep = new JScrollPane(tablaFormDep);
        scrollFormDep.setPreferredSize(new Dimension(400, 80));

        JPanel camposDep = new JPanel(new GridBagLayout());
        camposDep.setBackground(COLOR_FONDO);
        GridBagConstraints gd = new GridBagConstraints();
        gd.insets = new Insets(4, 4, 4, 4);
        gd.fill   = GridBagConstraints.HORIZONTAL;
        gd.anchor = GridBagConstraints.WEST;

        gd.gridx = 0; gd.gridy = 0; gd.weightx = 0;
        camposDep.add(new JLabel("Departamento:"), gd);
        gd.gridx = 1; gd.weightx = 1;
        camposDep.add(txtDepNombre, gd);

        gd.gridx = 0; gd.gridy = 1; gd.weightx = 0;
        camposDep.add(new JLabel("Descripción:"), gd);
        gd.gridx = 1; gd.weightx = 1;
        camposDep.add(txtDepDescripcion, gd);

        JPanel botonesDep = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        botonesDep.setBackground(COLOR_FONDO);

        JButton btnElimDep  = new JButton("Eliminar");
        JButton btnNuevoDep = new JButton("Nuevo");
        JButton btnGuarDep  = new JButton("Guardar");
        JButton btnModDep   = new JButton("Modificar");
        JButton btnCanDep   = new JButton("Cancelar");

        for (JButton b : new JButton[]{btnElimDep, btnNuevoDep, btnGuarDep, btnModDep, btnCanDep}) {
            b.setFont(new Font("Arial", Font.BOLD, 12));
        }
        btnElimDep.setBackground(new Color(204, 51, 0));
        btnElimDep.setForeground(Color.WHITE);
        btnGuarDep.setBackground(new Color(51, 153, 51));
        btnGuarDep.setForeground(Color.WHITE);

        botonesDep.add(btnElimDep);
        botonesDep.add(btnNuevoDep);
        botonesDep.add(btnGuarDep);
        botonesDep.add(btnModDep);
        botonesDep.add(btnCanDep);

        der.add(scrollFormDep, BorderLayout.NORTH);
        der.add(camposDep,     BorderLayout.CENTER);
        der.add(botonesDep,    BorderLayout.SOUTH);

        // ── Acciones botones ─────────────────────────────────────────────
        btnNuevoDep.addActionListener(e -> {
            tablaDepartamentos.clearSelection();
            modeloFormDep.setRowCount(0);
            txtDepNombre.setText("");
            txtDepDescripcion.setText("");
            depIdSeleccionado = null;
            txtDepNombre.requestFocus();
        });

        btnCanDep.addActionListener(e -> {
            tablaDepartamentos.clearSelection();
            modeloFormDep.setRowCount(0);
            txtDepNombre.setText("");
            txtDepDescripcion.setText("");
            depIdSeleccionado = null;
        });

        btnGuarDep.addActionListener(e -> {
            String nombre = txtDepNombre.getText().trim();
            String desc   = txtDepDescripcion.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del departamento es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Siempre POST para crear nuevo (Guardar = nuevo)
            String json = "{\"nombreDepartamento\":\"" + nombre + "\","
                        + "\"descripcionDepartamento\":\"" + desc + "\"}";
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    api.post("/departamentos", json);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Departamento guardado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        txtDepNombre.setText(""); txtDepDescripcion.setText("");
                        depIdSeleccionado = null;
                        cargarTablaDepartamentos();
                        cargarDepartamentos();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnModDep.addActionListener(e -> {
            if (depIdSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un departamento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nombre = txtDepNombre.getText().trim();
            String desc   = txtDepDescripcion.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = depIdSeleccionado;
            String json = "{\"idDepartamento\":" + id + ","
                        + "\"nombreDepartamento\":\"" + nombre + "\","
                        + "\"descripcionDepartamento\":\"" + desc + "\"}";
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    api.put("/departamentos/" + id, json);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Departamento modificado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarTablaDepartamentos();
                        cargarDepartamentos();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnElimDep.addActionListener(e -> {
            if (depIdSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un departamento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el departamento \"" + txtDepNombre.getText() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            int id = depIdSeleccionado;
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    int status = api.delete("/departamentos/" + id);
                    if (status == 409) throw new Exception("El departamento tiene productos asociados.");
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Departamento eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        txtDepNombre.setText(""); txtDepDescripcion.setText("");
                        depIdSeleccionado = null;
                        modeloFormDep.setRowCount(0);
                        cargarTablaDepartamentos();
                        cargarDepartamentos();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        tab.add(izq, BorderLayout.CENTER);
        tab.add(der, BorderLayout.EAST);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab 3: Proveedor
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearTabProveedor() {
        JPanel tab = new JPanel(new BorderLayout(8, 0));
        tab.setBackground(COLOR_FONDO);
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Panel izquierdo ──────────────────────────────────────────────
        JPanel izq = new JPanel(new BorderLayout(0, 6));
        izq.setBackground(COLOR_FONDO);

        JTextField txtBusquedaProv = new JTextField();
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(COLOR_FONDO);
        panelBusqueda.add(new JLabel("🔍"), BorderLayout.WEST);
        panelBusqueda.add(txtBusquedaProv, BorderLayout.CENTER);
        izq.add(panelBusqueda, BorderLayout.NORTH);

        modeloProveedores = new DefaultTableModel(new String[]{"Proveedor", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaProveedores = new JTable(modeloProveedores);
        estilizarTabla(tablaProveedores);
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(180);
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(120);

        txtBusquedaProv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBusquedaProv.getText().trim();
                if (texto.isEmpty()) {
                    tablaProveedores.setRowSorter(null);
                } else {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        new javax.swing.table.TableRowSorter<>(modeloProveedores);
                    tablaProveedores.setRowSorter(sorter);
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        izq.add(new JScrollPane(tablaProveedores), BorderLayout.CENTER);

        // ── Panel derecho: tabla resumen + formulario ────────────────────
        JPanel der = new JPanel(new BorderLayout(0, 8));
        der.setBackground(COLOR_FONDO);
        der.setPreferredSize(new Dimension(460, 0));

        // Tabla resumen derecha (igual diseño que en el prototipo)
        DefaultTableModel modeloFormProv = new DefaultTableModel(
            new String[]{"Campo", "Valor"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaFormProv = new JTable(modeloFormProv);
        estilizarTabla(tablaFormProv);
        tablaFormProv.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaFormProv.getColumnModel().getColumn(1).setPreferredWidth(270);
        JScrollPane scrollFormProv = new JScrollPane(tablaFormProv);
        scrollFormProv.setPreferredSize(new Dimension(430, 170));

        // Campos de formulario
        txtProvNombre    = new JTextField();
        txtProvTelefono  = new JTextField();
        txtProvCorreo    = new JTextField();
        txtProvDireccion = new JTextField();
        txtProvNotas     = new JTextField();

        JPanel camposProv = new JPanel(new GridBagLayout());
        camposProv.setBackground(COLOR_FONDO);
        GridBagConstraints gp = new GridBagConstraints();
        gp.insets = new Insets(3, 4, 3, 4);
        gp.fill   = GridBagConstraints.HORIZONTAL;
        gp.anchor = GridBagConstraints.WEST;

        String[] labsProv = {"Nombre:", "Teléfono:", "Correo:", "Dirección:", "Notas:"};
        JTextField[] camposArr = {txtProvNombre, txtProvTelefono, txtProvCorreo,
                                  txtProvDireccion, txtProvNotas};
        for (int i = 0; i < labsProv.length; i++) {
            gp.gridx = 0; gp.gridy = i; gp.weightx = 0;
            camposProv.add(new JLabel(labsProv[i]), gp);
            gp.gridx = 1; gp.weightx = 1;
            camposProv.add(camposArr[i], gp);
        }

        // Cuando se selecciona fila izquierda → llenar formulario y tabla resumen
        tablaProveedores.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && tablaProveedores.getSelectedRow() >= 0) {
                int fila = tablaProveedores.getSelectedRow();
                String nombre = (String) modeloProveedores.getValueAt(fila, 0);
                String tel    = (String) modeloProveedores.getValueAt(fila, 1);
                buscarProveedorPorNombre(nombre, tablaFormProv, modeloFormProv);
            }
        });

        JPanel botonesProv = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        botonesProv.setBackground(COLOR_FONDO);

        JButton btnElimProv  = new JButton("Eliminar");
        JButton btnNuevoProv = new JButton("Nuevo");
        JButton btnGuarProv  = new JButton("Guardar");
        JButton btnModProv   = new JButton("Modificar");
        JButton btnCanProv   = new JButton("Cancelar");

        for (JButton b : new JButton[]{btnElimProv, btnNuevoProv, btnGuarProv, btnModProv, btnCanProv}) {
            b.setFont(new Font("Arial", Font.BOLD, 12));
        }
        btnElimProv.setBackground(new Color(204, 51, 0));
        btnElimProv.setForeground(Color.WHITE);
        btnGuarProv.setBackground(new Color(51, 153, 51));
        btnGuarProv.setForeground(Color.WHITE);
        botonesProv.add(btnElimProv);
        botonesProv.add(btnNuevoProv);
        botonesProv.add(btnGuarProv);
        botonesProv.add(btnModProv);
        botonesProv.add(btnCanProv);

        der.add(scrollFormProv, BorderLayout.NORTH);
        der.add(camposProv,     BorderLayout.CENTER);
        der.add(botonesProv,    BorderLayout.SOUTH);

        // ── Acciones ─────────────────────────────────────────────────────
        btnNuevoProv.addActionListener(e -> {
            tablaProveedores.clearSelection();
            modeloFormProv.setRowCount(0);
            for (JTextField tf : camposArr) tf.setText("");
            provIdSeleccionado = null;
            txtProvNombre.requestFocus();
        });

        btnCanProv.addActionListener(e -> {
            tablaProveedores.clearSelection();
            modeloFormProv.setRowCount(0);
            for (JTextField tf : camposArr) tf.setText("");
            provIdSeleccionado = null;
        });

        btnGuarProv.addActionListener(e -> {
            if (txtProvNombre.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "El nombre del proveedor es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String json = construirJsonProveedor(null);
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    api.post("/proveedores", json);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Proveedor guardado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        for (JTextField tf : camposArr) tf.setText("");
                        provIdSeleccionado = null;
                        modeloFormProv.setRowCount(0);
                        cargarTablaProveedores();
                        cargarProveedores();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnModProv.addActionListener(e -> {
            if (provIdSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un proveedor primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtProvNombre.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = provIdSeleccionado;
            String json = construirJsonProveedor(id);
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    api.put("/proveedores/" + id, json);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Proveedor modificado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarTablaProveedores();
                        cargarProveedores();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnElimProv.addActionListener(e -> {
            if (provIdSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un proveedor primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el proveedor \"" + txtProvNombre.getText() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            int id = provIdSeleccionado;
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    int status = api.delete("/proveedores/" + id);
                    if (status >= 400) throw new Exception("No se pudo eliminar el proveedor.");
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Proveedor eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        for (JTextField tf : camposArr) tf.setText("");
                        provIdSeleccionado = null;
                        modeloFormProv.setRowCount(0);
                        cargarTablaProveedores();
                        cargarProveedores();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        tab.add(izq, BorderLayout.CENTER);
        tab.add(der, BorderLayout.EAST);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab 4: Catálogo
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearTabCatalogo() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(COLOR_FONDO);
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(COLOR_FONDO);
        txtCatBusqueda = new JTextField();
        panelBusqueda.add(new JLabel("🔍"), BorderLayout.WEST);
        panelBusqueda.add(txtCatBusqueda, BorderLayout.CENTER);

        // Tabla
        String[] colsCat = {"Código", "Descripción", "Proveedor", "Departamento",
                            "Costo", "Venta", "Existencia", "Inv. Mín.", "Inv. Máx.", "Unidad"};
        modeloCatalogo = new DefaultTableModel(colsCat, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaCatalogo = new JTable(modeloCatalogo);
        estilizarTabla(tablaCatalogo);
        tablaCatalogo.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaCatalogo.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaCatalogo.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaCatalogo.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaCatalogo.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaCatalogo.getColumnModel().getColumn(5).setPreferredWidth(65);
        tablaCatalogo.getColumnModel().getColumn(6).setPreferredWidth(75);
        tablaCatalogo.getColumnModel().getColumn(7).setPreferredWidth(70);
        tablaCatalogo.getColumnModel().getColumn(8).setPreferredWidth(70);
        tablaCatalogo.getColumnModel().getColumn(9).setPreferredWidth(60);

        txtCatBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtCatBusqueda.getText().trim();
                if (texto.isEmpty()) {
                    tablaCatalogo.setRowSorter(null);
                } else {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        new javax.swing.table.TableRowSorter<>(modeloCatalogo);
                    tablaCatalogo.setRowSorter(sorter);
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        tab.add(panelBusqueda,           BorderLayout.NORTH);
        tab.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Carga de datos desde el backend
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarDepartamentos() {
        new SwingWorker<List<ProductoDTO.DepartamentoRef>, Void>() {
            @Override
            protected List<ProductoDTO.DepartamentoRef> doInBackground() throws Exception {
                String json = api.get("/departamentos");
                return mapper.readValue(json,
                    new TypeReference<List<ProductoDTO.DepartamentoRef>>() {});
            }
            @Override
            protected void done() {
                try {
                    cmbDepartamento.removeAllItems();
                    cmbDepartamento.addItem(new ProductoDTO.DepartamentoRef(null));
                    for (ProductoDTO.DepartamentoRef d : get()) cmbDepartamento.addItem(d);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron cargar departamentos", ex);
                }
            }
        }.execute();
    }

    private void cargarProveedores() {
        new SwingWorker<List<ProductoDTO.ProveedorRef>, Void>() {
            @Override
            protected List<ProductoDTO.ProveedorRef> doInBackground() throws Exception {
                String json = api.get("/proveedores");
                return mapper.readValue(json,
                    new TypeReference<List<ProductoDTO.ProveedorRef>>() {});
            }
            @Override
            protected void done() {
                try {
                    cmbProveedor.removeAllItems();
                    cmbProveedor.addItem(new ProductoDTO.ProveedorRef(null));
                    for (ProductoDTO.ProveedorRef p : get()) cmbProveedor.addItem(p);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron cargar proveedores", ex);
                }
            }
        }.execute();
    }

    private void cargarTablaDepartamentos() {
        if (modeloDepartamentos == null) return;
        new SwingWorker<java.util.List<com.fasterxml.jackson.databind.JsonNode>, Void>() {
            @Override
            protected java.util.List<com.fasterxml.jackson.databind.JsonNode> doInBackground() throws Exception {
                String json = api.get("/departamentos");
                com.fasterxml.jackson.databind.JsonNode arr = mapper.readTree(json);
                java.util.List<com.fasterxml.jackson.databind.JsonNode> lista = new java.util.ArrayList<>();
                arr.forEach(lista::add);
                return lista;
            }
            @Override
            protected void done() {
                try {
                    modeloDepartamentos.setRowCount(0);
                    for (com.fasterxml.jackson.databind.JsonNode n : get()) {
                        modeloDepartamentos.addRow(new Object[]{
                            n.path("nombreDepartamento").asText(),
                            n.path("descripcionDepartamento").asText()
                        });
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error cargando departamentos", ex);
                }
            }
        }.execute();
    }

    private void cargarTablaProveedores() {
        if (modeloProveedores == null) return;
        new SwingWorker<java.util.List<com.fasterxml.jackson.databind.JsonNode>, Void>() {
            @Override
            protected java.util.List<com.fasterxml.jackson.databind.JsonNode> doInBackground() throws Exception {
                String json = api.get("/proveedores");
                com.fasterxml.jackson.databind.JsonNode arr = mapper.readTree(json);
                java.util.List<com.fasterxml.jackson.databind.JsonNode> lista = new java.util.ArrayList<>();
                arr.forEach(lista::add);
                return lista;
            }
            @Override
            protected void done() {
                try {
                    modeloProveedores.setRowCount(0);
                    for (com.fasterxml.jackson.databind.JsonNode n : get()) {
                        modeloProveedores.addRow(new Object[]{
                            n.path("nombreProveedor").asText(),
                            n.path("telefono").asText()
                        });
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error cargando proveedores", ex);
                }
            }
        }.execute();
    }

    private void cargarTablaCatalogo() {
        if (modeloCatalogo == null) return;
        new SwingWorker<java.util.List<com.fasterxml.jackson.databind.JsonNode>, Void>() {
            @Override
            protected java.util.List<com.fasterxml.jackson.databind.JsonNode> doInBackground() throws Exception {
                String json = api.get("/productos");
                com.fasterxml.jackson.databind.JsonNode arr = mapper.readTree(json);
                java.util.List<com.fasterxml.jackson.databind.JsonNode> lista = new java.util.ArrayList<>();
                arr.forEach(lista::add);
                return lista;
            }
            @Override
            protected void done() {
                try {
                    modeloCatalogo.setRowCount(0);
                    for (com.fasterxml.jackson.databind.JsonNode n : get()) {
                        String prov = n.path("proveedor").path("nombreProveedor").asText();
                        String dep  = n.path("departamento").path("nombreDepartamento").asText();
                        BigDecimal costo = n.path("precioCosto").decimalValue();
                        BigDecimal venta = n.path("precioVentaLista").decimalValue();
                        String exist = n.path("existencia").isNull() ? "---"
                            : String.valueOf(n.path("existencia").asInt());
                        String invMin = n.path("invMinimo").isNull() ? "---"
                            : String.valueOf(n.path("invMinimo").asInt());
                        String invMax = n.path("invMaximo").isNull() ? "---"
                            : String.valueOf(n.path("invMaximo").asInt());
                        modeloCatalogo.addRow(new Object[]{
                            n.path("codigoBarras").asText(),
                            n.path("descripcion").asText(),
                            prov, dep,
                            "$" + costo.setScale(2, RoundingMode.HALF_UP),
                            "$" + venta.setScale(2, RoundingMode.HALF_UP),
                            exist, invMin, invMax,
                            n.path("unidad").asText()
                        });
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error cargando catálogo", ex);
                }
            }
        }.execute();
    }

    /** Busca en la lista de departamentos cargados para obtener el ID por nombre. */
    private void buscarIdDepartamentoPorNombre(String nombre) {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                String json = api.get("/departamentos");
                com.fasterxml.jackson.databind.JsonNode arr = mapper.readTree(json);
                for (com.fasterxml.jackson.databind.JsonNode n : arr) {
                    if (nombre.equals(n.path("nombreDepartamento").asText())) {
                        return n.path("idDepartamento").asInt();
                    }
                }
                return null;
            }
            @Override
            protected void done() {
                try { depIdSeleccionado = get(); }
                catch (Exception ignored) {}
            }
        }.execute();
    }

    /** Carga el detalle de un proveedor por nombre y llena el formulario. */
    private void buscarProveedorPorNombre(String nombre,
            JTable tablaFormProv, DefaultTableModel modeloFormProv) {
        new SwingWorker<com.fasterxml.jackson.databind.JsonNode, Void>() {
            @Override
            protected com.fasterxml.jackson.databind.JsonNode doInBackground() throws Exception {
                String json = api.get("/proveedores/buscar?nombre="
                    + java.net.URLEncoder.encode(nombre, java.nio.charset.StandardCharsets.UTF_8));
                com.fasterxml.jackson.databind.JsonNode arr = mapper.readTree(json);
                return arr.isEmpty() ? null : arr.get(0);
            }
            @Override
            protected void done() {
                try {
                    com.fasterxml.jackson.databind.JsonNode n = get();
                    if (n == null) return;
                    provIdSeleccionado = n.path("idProveedor").asInt();
                    txtProvNombre.setText(n.path("nombreProveedor").asText());
                    txtProvTelefono.setText(n.path("telefono").asText());
                    txtProvCorreo.setText(n.path("correo").asText());
                    txtProvDireccion.setText(n.path("direccion").asText());
                    txtProvNotas.setText(n.path("notas").asText());
                    // Tabla resumen derecha
                    modeloFormProv.setRowCount(0);
                    String[][] filas = {
                        {"Nombre",    txtProvNombre.getText()},
                        {"Teléfono",  txtProvTelefono.getText()},
                        {"Correo",    txtProvCorreo.getText()},
                        {"Dirección", txtProvDireccion.getText()},
                        {"Notas",     txtProvNotas.getText()}
                    };
                    for (String[] fila : filas) modeloFormProv.addRow(fila);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error al cargar proveedor", ex);
                }
            }
        }.execute();
    }

    private String construirJsonProveedor(Integer id) {
        StringBuilder sb = new StringBuilder("{");
        if (id != null) sb.append("\"idProveedor\":").append(id).append(",");
        sb.append("\"nombreProveedor\":\"").append(esc(txtProvNombre.getText())).append("\",");
        sb.append("\"telefono\":\"").append(esc(txtProvTelefono.getText())).append("\",");
        sb.append("\"correo\":\"").append(esc(txtProvCorreo.getText())).append("\",");
        sb.append("\"direccion\":\"").append(esc(txtProvDireccion.getText())).append("\",");
        sb.append("\"notas\":\"").append(esc(txtProvNotas.getText())).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica guardar producto (Tab 1)
    // ─────────────────────────────────────────────────────────────────────────

    private void guardarProducto() {
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "El código de barras es obligatorio.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtDescripcion.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal precioCosto, porcentaje, precioVenta;
        try {
            precioCosto = new BigDecimal(txtPrecioCosto.getText().trim());
            porcentaje  = new BigDecimal(txtPorcentaje.getText().trim());
            precioVenta = new BigDecimal(txtPrecioVenta.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Precio Costo y % Ganancia deben ser números válidos.",
                "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProductoDTO dto = new ProductoDTO();
        dto.setCodigoBarras(txtCodigo.getText().trim());
        dto.setDescripcion(txtDescripcion.getText().trim());
        dto.setUnidad(rdPza.isSelected() ? "Pza" : "Granel");
        dto.setPrecioCosto(precioCosto);
        dto.setPorcentajeGanancia(porcentaje);
        dto.setPrecioVentaLista(precioVenta);
        dto.setUsaInventario(chkInventario.isSelected());

        if (chkInventario.isSelected()) {
            try { dto.setExistencia(new BigDecimal(txtExistencia.getText().trim())); }
            catch (NumberFormatException ignored) { dto.setExistencia(BigDecimal.ZERO); }
            try { dto.setInvMinimo(new BigDecimal(txtMinimo.getText().trim())); }
            catch (NumberFormatException ignored) {}
            try { dto.setInvMaximo(new BigDecimal(txtMaximo.getText().trim())); }
            catch (NumberFormatException ignored) {}
        } else {
            dto.setExistencia(BigDecimal.ZERO);
        }

        ProductoDTO.DepartamentoRef dep =
            (ProductoDTO.DepartamentoRef) cmbDepartamento.getSelectedItem();
        if (dep != null && dep.getIdDepartamento() != null) dto.setDepartamento(dep);

        ProductoDTO.ProveedorRef prov =
            (ProductoDTO.ProveedorRef) cmbProveedor.getSelectedItem();
        if (prov != null && prov.getIdProveedor() != null) dto.setProveedor(prov);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = mapper.writeValueAsString(dto);
                return api.post("/productos", json);
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(PRODUCTOS.this,
                        "Producto guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al guardar producto", ex);
                    JOptionPane.showMessageDialog(PRODUCTOS.this,
                        "Error al guardar:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtDescripcion.setText("");
        rdPza.setSelected(true);
        txtPrecioCosto.setText("");
        txtPorcentaje.setText("");
        txtPrecioVenta.setText("");
        cmbDepartamento.setSelectedIndex(0);
        cmbProveedor.setSelectedIndex(0);
        chkInventario.setSelected(false);
        txtExistencia.setText(""); txtExistencia.setEnabled(false);
        txtMinimo.setText("");     txtMinimo.setEnabled(false);
        txtMaximo.setText("");     txtMaximo.setEnabled(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Popup de sesión
    // ─────────────────────────────────────────────────────────────────────────

    private void mostrarPopupSesion(JButton origen) {
        JDialog popup = new JDialog(this, false);
        popup.setUndecorated(true);
        popup.setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new GridLayout(0, 1, 0, 0));
        contenedor.setBackground(new Color(230, 230, 230));
        contenedor.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel titulo = new JLabel("Sesión", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(COLOR_NARANJA);
        titulo.setFont(new Font("Arial", Font.BOLD, 13));
        titulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnCerrar = new JButton("Cerrar sesión");
        btnCerrar.setBackground(new Color(230, 230, 230));
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(ev -> {
            popup.dispose();
            SesionActual.setNombreUsuario(null);
            SesionActual.setRol(null);
            new Login().setVisible(true);
            dispose();
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(230, 230, 230));
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(ev -> popup.dispose());

        contenedor.add(titulo);
        contenedor.add(btnCerrar);
        contenedor.add(btnCancelar);
        popup.add(contenedor);
        popup.pack();

        Point p = origen.getLocationOnScreen();
        popup.setLocation(p.x, p.y + origen.getHeight());
        popup.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper de tablas
    // ─────────────────────────────────────────────────────────────────────────

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.setBackground(COLOR_FONDO);
        tabla.setSelectionBackground(new Color(255, 204, 102));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.getTableHeader().setBackground(COLOR_FONDO);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }
}