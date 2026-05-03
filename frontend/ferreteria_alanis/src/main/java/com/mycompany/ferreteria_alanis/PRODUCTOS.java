package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
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
 *   1. Producto   — formulario para crear/editar un producto [FUNCIONAL]
 *   2. Departamento — gestión de departamentos            [pendiente]
 *   3. Proveedor    — gestión de proveedores              [pendiente]
 *   4. Catálogo     — tabla con todos los productos       [pendiente]
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

    // ─────────────────────────────────────────────────────────────────────────

    public PRODUCTOS(String rol, String nombreUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís – Productos");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        // Cargar combos al abrir
        cargarDepartamentos();
        cargarProveedores();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la UI
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),    BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    /** Franja superior idéntica a VENTAS. */
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

        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(COLOR_NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
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

    /** Contenido principal: franja naranja "Productos" + JTabbedPane. */
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);

        // Franja naranja
        JLabel lblTitulo = new JLabel("  Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 30));

        // JTabbedPane con los 4 sub-módulos
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Producto",     crearTabProducto());
        tabs.addTab("Departamento", crearTabPendiente("Departamento"));
        tabs.addTab("Proveedor",    crearTabPendiente("Proveedor"));
        tabs.addTab("Catálogo",     crearTabPendiente("Catálogo"));

        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(tabs,      BorderLayout.CENTER);
        return contenido;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab: Producto (formulario completo)
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

        // Código de barras
        txtCodigo = new JTextField(20);
        agregarFila(form, g, fila++, "Código de barras", txtCodigo);

        // Descripción
        txtDescripcion = new JTextField(20);
        agregarFila(form, g, fila++, "Descripción", txtDescripcion);

        // Se vende (radio buttons)
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

        // Precio Costo
        txtPrecioCosto = new JTextField(10);
        agregarFila(form, g, fila++, "Precio Costo", txtPrecioCosto);

        // % Ganancia → auto-calcula Precio Venta
        txtPorcentaje = new JTextField(10);
        txtPorcentaje.setToolTipText("Porcentaje de ganancia sobre el costo");
        agregarFila(form, g, fila++, "% Ganancia", txtPorcentaje);

        // Precio Venta (solo lectura, calculado)
        txtPrecioVenta = new JTextField(10);
        txtPrecioVenta.setEditable(false);
        txtPrecioVenta.setBackground(new Color(220, 220, 220));
        agregarFila(form, g, fila++, "Precio Venta", txtPrecioVenta);

        // Listener para auto-calcular precio venta
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

        // Departamento
        cmbDepartamento = new JComboBox<>();
        agregarFila(form, g, fila++, "Departamento", cmbDepartamento);

        // Proveedor
        cmbProveedor = new JComboBox<>();
        agregarFila(form, g, fila++, "Proveedor", cmbProveedor);

        // Checkbox inventario
        chkInventario = new JCheckBox("Este Producto utiliza inventario");
        chkInventario.setBackground(COLOR_FONDO);
        g.gridx = 0; g.gridy = fila++; g.gridwidth = 2;
        form.add(chkInventario, g);
        g.gridwidth = 1;

        // Campos de inventario (deshabilitados hasta marcar checkbox)
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

        // Botones Guardar / Cancelar
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

    /** Agrega una fila etiqueta + campo al GridBagLayout. */
    private void agregarFila(JPanel panel, GridBagConstraints g, int fila,
                              String etiqueta, Component campo) {
        g.gridx = 0; g.gridy = fila; g.weightx = 0; g.gridwidth = 1;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        panel.add(campo, g);
    }

    /** Panel derecho con el logo de la empresa. */
    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setPreferredSize(new Dimension(300, 0));

        ImageIcon icono = new ImageIcon("logo.png");
        Image img = icono.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
        panel.add(new JLabel(new ImageIcon(img)));
        return panel;
    }

    /** Panel placeholder para tabs aún no implementados. */
    private JPanel crearTabPendiente(String nombre) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        JLabel lbl = new JLabel("Submódulo \"" + nombre + "\" en construcción.");
        lbl.setFont(new Font("Arial", Font.ITALIC, 16));
        lbl.setForeground(Color.GRAY);
        panel.add(lbl);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica de negocio
    // ─────────────────────────────────────────────────────────────────────────

    /** Carga los departamentos del backend y puebla el JComboBox. */
    private void cargarDepartamentos() {
        new SwingWorker<List<ProductoDTO.DepartamentoRef>, Void>() {
            @Override
            protected List<ProductoDTO.DepartamentoRef> doInBackground() throws Exception {
                String json = api.get("/departamentos");
                return mapper.readValue(json,
                    new com.fasterxml.jackson.core.type.TypeReference<
                        List<ProductoDTO.DepartamentoRef>>() {});
            }

            @Override
            protected void done() {
                try {
                    cmbDepartamento.removeAllItems();
                    cmbDepartamento.addItem(new ProductoDTO.DepartamentoRef(null)); // vacío
                    for (ProductoDTO.DepartamentoRef d : get()) {
                        cmbDepartamento.addItem(d);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron cargar departamentos", ex);
                }
            }
        }.execute();
    }

    /** Carga los proveedores del backend y puebla el JComboBox. */
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
                    cmbProveedor.addItem(new ProductoDTO.ProveedorRef(null)); // vacío
                    for (ProductoDTO.ProveedorRef p : get()) {
                        cmbProveedor.addItem(p);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron cargar proveedores", ex);
                }
            }
        }.execute();
    }

    /** Valida el formulario y hace POST /productos al backend. */
    private void guardarProducto() {
        // ── Validaciones básicas ──────────────────────────────────────────
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
            precioCosto  = new BigDecimal(txtPrecioCosto.getText().trim());
            porcentaje   = new BigDecimal(txtPorcentaje.getText().trim());
            precioVenta  = new BigDecimal(txtPrecioVenta.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Precio Costo y % Ganancia deben ser números válidos.",
                "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Construir DTO ─────────────────────────────────────────────────
        ProductoDTO dto = new ProductoDTO();
        dto.setCodigoBarras(txtCodigo.getText().trim());
        dto.setDescripcion(txtDescripcion.getText().trim());
        dto.setUnidad(rdPza.isSelected() ? "Pza" : "Granel");
        dto.setPrecioCosto(precioCosto);
        dto.setPorcentajeGanancia(porcentaje);
        dto.setPrecioVentaLista(precioVenta);
        dto.setUsaInventario(chkInventario.isSelected());

        if (chkInventario.isSelected()) {
            try {
                dto.setExistencia(new BigDecimal(txtExistencia.getText().trim()));
            } catch (NumberFormatException ignored) { dto.setExistencia(BigDecimal.ZERO); }
            try {
                dto.setInvMinimo(new BigDecimal(txtMinimo.getText().trim()));
            } catch (NumberFormatException ignored) {}
            try {
                dto.setInvMaximo(new BigDecimal(txtMaximo.getText().trim()));
            } catch (NumberFormatException ignored) {}
        } else {
            dto.setExistencia(BigDecimal.ZERO);
        }

        ProductoDTO.DepartamentoRef dep =
            (ProductoDTO.DepartamentoRef) cmbDepartamento.getSelectedItem();
        if (dep != null && dep.getIdDepartamento() != null) dto.setDepartamento(dep);

        ProductoDTO.ProveedorRef prov =
            (ProductoDTO.ProveedorRef) cmbProveedor.getSelectedItem();
        if (prov != null && prov.getIdProveedor() != null) dto.setProveedor(prov);

        // ── POST al backend ───────────────────────────────────────────────
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
                        "Producto guardado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al guardar producto", ex);
                    JOptionPane.showMessageDialog(PRODUCTOS.this,
                        "Error al guardar:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /** Limpia todos los campos del formulario. */
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
}
