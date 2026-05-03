package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla del módulo de Ventas.
 *
 * Responsabilidades de esta pantalla:
 *   - Agregar productos a un ticket mediante código de barras
 *   - Modificar cantidad directamente en la tabla
 *   - Aplicar descuento a un renglón seleccionado
 *   - Eliminar un renglón del ticket (NO del catálogo)
 *   - Gestionar hasta 3 tickets simultáneos
 *   - Cobrar (pendiente de implementar el flujo de pago)
 *
 * Lo que NO hace esta pantalla (pertenece a PRODUCTOS):
 *   - Crear / editar / eliminar productos del catálogo
 *   - Cargar el catálogo completo
 */
public class VENTAS extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(VENTAS.class.getName());

    private static final Color COLOR_NARANJA    = new Color(255, 153, 0);
    private static final Color COLOR_NEGRO      = Color.BLACK;
    private static final Color COLOR_FONDO      = new Color(230, 230, 230);
    private static final String[] COLUMNAS      = {
        "Código de barras", "Descripción", "Precio", "Cant.", "Importe", "Existencia", "Descuento"
    };
    // Índices de columna
    private static final int COL_CODIGO      = 0;
    private static final int COL_DESC        = 1;
    private static final int COL_PRECIO      = 2;
    private static final int COL_CANT        = 3;
    private static final int COL_IMPORTE     = 4;
    private static final int COL_EXISTENCIA  = 5;
    private static final int COL_DESCUENTO   = 6;

    private final String rol;
    private final String nombreUsuario;
    private final ApiClient api = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    // Un modelo de tabla por cada ticket
    private final List<DefaultTableModel> ticketModels = new ArrayList<>();
    private JTabbedPane ticketPane;
    private JLabel lblTotal;
    private JTextField txtCodigo;

    // ─────────────────────────────────────────────────────────────────────────

    public VENTAS(String rol, String nombreUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís – Ventas");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la UI
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),         BorderLayout.NORTH);
        add(crearContenido(),      BorderLayout.CENTER);
        add(crearBarraInferior(),  BorderLayout.SOUTH);
    }

    /** Franja superior con logo, navegación de módulos y usuario. */
    private JPanel crearNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(COLOR_NEGRO);

        // ── Header (logo + nombre + usuario) ──────────────────────────────
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

        // "Usuario: X" — sin acción por ahora
        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(COLOR_NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
        header.add(btnUsuarioInfo, BorderLayout.EAST);
        nav.add(header, BorderLayout.NORTH);

        // ── Botones de módulos ─────────────────────────────────────────────
        JPanel modulos = new JPanel(new BorderLayout());
        modulos.setBackground(new Color(50, 50, 50));

        JPanel izqModulos = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        izqModulos.setOpaque(false);

        // VENTAS (activo)
        JButton btnVentas = crearBtnNav("Ventas", true);
        btnVentas.addActionListener(e -> { /* ya estamos aquí */ });
        izqModulos.add(btnVentas);

        // PRODUCTOS (solo admin)
        if ("ADMIN".equalsIgnoreCase(rol)) {
            JButton btnProductos = crearBtnNav("Productos", false);
            btnProductos.addActionListener(e -> {
                new PRODUCTOS(rol, nombreUsuario).setVisible(true);
                dispose();
            });
            izqModulos.add(btnProductos);
        }

        // INVENTARIO
        JButton btnInventario = crearBtnNav("Inventario", false);
        btnInventario.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Inventario",
                JOptionPane.INFORMATION_MESSAGE));
        izqModulos.add(btnInventario);

        modulos.add(izqModulos, BorderLayout.WEST);

        // Derecha: Corte y Usuario (solo admin)
        if ("ADMIN".equalsIgnoreCase(rol)) {
            JPanel derModulos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
            derModulos.setOpaque(false);
            JButton btnCorte = crearBtnNav("Corte", false);
            btnCorte.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte",
                    JOptionPane.INFORMATION_MESSAGE));
            JButton btnUsuario = crearBtnNav("Usuario", false);
            btnUsuario.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Usuario",
                    JOptionPane.INFORMATION_MESSAGE));
            derModulos.add(btnCorte);
            derModulos.add(btnUsuario);
            modulos.add(derModulos, BorderLayout.EAST);
        }

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

    /** Zona central: encabezado naranja + buscador + pestañas de tickets. */
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);

        // ── Franja naranja "Ventas" + botón Historial ──────────────────────
        JPanel franjaVentas = new JPanel(new BorderLayout());
        franjaVentas.setBackground(COLOR_NARANJA);
        franjaVentas.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JLabel lblVentas = new JLabel("Ventas");
        lblVentas.setFont(new Font("Arial", Font.BOLD, 16));
        franjaVentas.add(lblVentas, BorderLayout.WEST);

        JPanel btnsFranja = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnsFranja.setOpaque(false);
        JButton btnTicket    = new JButton("Ticket");
        JButton btnHistorial = new JButton("Historial");
        btnHistorial.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Historial en construcción.", "Historial",
                JOptionPane.INFORMATION_MESSAGE));
        btnsFranja.add(btnTicket);
        btnsFranja.add(btnHistorial);
        franjaVentas.add(btnsFranja, BorderLayout.EAST);

        // ── Barra de búsqueda de producto ─────────────────────────────────
        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        barraBusqueda.setBackground(COLOR_FONDO);

        JLabel lblCodigo = new JLabel("Código del Producto:");
        lblCodigo.setFont(new Font("Arial", Font.PLAIN, 13));
        txtCodigo = new JTextField(20);
        txtCodigo.addActionListener(e -> agregarProductoAlTicket()); // Enter también agrega

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(51, 204, 0));
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAgregar.addActionListener(e -> agregarProductoAlTicket());

        barraBusqueda.add(lblCodigo);
        barraBusqueda.add(txtCodigo);
        barraBusqueda.add(btnAgregar);

        // ── Pestañas de tickets ───────────────────────────────────────────
        ticketPane = new JTabbedPane();
        ticketPane.setBackground(COLOR_FONDO);

        for (int i = 1; i <= 3; i++) {
            DefaultTableModel model = crearModeloTicket();
            ticketModels.add(model);
            ticketPane.addTab("Ticket " + i, crearPanelTicket(model));
        }

        // Panel superior (franja + buscador)
        JPanel superior = new JPanel(new BorderLayout());
        superior.add(franjaVentas,  BorderLayout.NORTH);
        superior.add(barraBusqueda, BorderLayout.SOUTH);

        contenido.add(superior,   BorderLayout.NORTH);
        contenido.add(ticketPane, BorderLayout.CENTER);
        return contenido;
    }

    /** Crea el modelo de tabla para un ticket (columnas fijas, sin filas iniciales). */
    private DefaultTableModel crearModeloTicket() {
        DefaultTableModel model = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == COL_CANT; // solo la cantidad es editable
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == COL_CANT) return BigDecimal.class;
                return Object.class;
            }
        };

        // Recalcula importe cuando cambia la cantidad
        model.addTableModelListener(e -> {
            if (e.getColumn() == COL_CANT) {
                int fila = e.getFirstRow();
                recalcularImporte(model, fila);
                actualizarTotal(model);
            }
        });

        return model;
    }

    /** Crea el JScrollPane+JTable para una pestaña de ticket. */
    private JScrollPane crearPanelTicket(DefaultTableModel model) {
        JTable tabla = new JTable(model);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Columnas que no necesitan mucho ancho
        tabla.getColumnModel().getColumn(COL_CODIGO).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(COL_DESC).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(COL_PRECIO).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(COL_CANT).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(COL_IMPORTE).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(COL_EXISTENCIA).setPreferredWidth(85);
        tabla.getColumnModel().getColumn(COL_DESCUENTO).setPreferredWidth(80);

        // Renderer para centrar descuento como "X%"
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(COL_DESCUENTO).setCellRenderer(centrado);

        // Filas alternas en amarillo claro (como en el prototipo)
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 200) : Color.WHITE);
                }
                return c;
            }
        });

        return new JScrollPane(tabla);
    }

    /** Barra inferior: Eliminar + Descuento a la izquierda, Cobrar + Total a la derecha. */
    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(COLOR_FONDO);
        barra.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Izquierda: Regresar | Eliminar | Descuento
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new MenuPrincipal(rol, nombreUsuario).setVisible(true);
            dispose();
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(204, 51, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.addActionListener(e -> eliminarRenglonDelTicket());

        JButton btnDescuento = new JButton("Descuento");
        btnDescuento.setBackground(new Color(255, 102, 0));
        btnDescuento.setForeground(Color.WHITE);
        btnDescuento.setFont(new Font("Arial", Font.BOLD, 13));
        btnDescuento.addActionListener(e -> aplicarDescuento());

        izquierda.add(btnRegresar);
        izquierda.add(btnEliminar);
        izquierda.add(btnDescuento);

        // Derecha: Cobrar + total
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        JButton btnCobrar = new JButton("Cobrar");
        btnCobrar.setBackground(new Color(102, 204, 0));
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCobrar.setPreferredSize(new Dimension(90, 35));
        btnCobrar.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Flujo de cobro en construcción.", "Cobrar",
                JOptionPane.INFORMATION_MESSAGE));

        lblTotal = new JLabel("$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotal.setBackground(new Color(255, 204, 102));
        lblTotal.setOpaque(true);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        derecha.add(btnCobrar);
        derecha.add(lblTotal);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha,   BorderLayout.EAST);
        return barra;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica de negocio
    // ─────────────────────────────────────────────────────────────────────────

    /** Busca el producto por código y lo agrega al ticket activo. */
    private void agregarProductoAlTicket() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingresa el código de barras del producto.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelActivo = getModeloActivo();

        // Si ya existe en el ticket, solo incrementa cantidad
        for (int i = 0; i < modelActivo.getRowCount(); i++) {
            if (codigo.equals(modelActivo.getValueAt(i, COL_CODIGO))) {
                BigDecimal cantActual = parseBD(modelActivo.getValueAt(i, COL_CANT));
                modelActivo.setValueAt(cantActual.add(BigDecimal.ONE), i, COL_CANT);
                recalcularImporte(modelActivo, i);
                actualizarTotal(modelActivo);
                txtCodigo.setText("");
                return;
            }
        }

        // Si no existe, consulta la API y agrega el renglón
        new SwingWorker<ProductoDTO, Void>() {
            @Override
            protected ProductoDTO doInBackground() throws Exception {
                String encoded = URLEncoder.encode(codigo, StandardCharsets.UTF_8);
                String json = api.get("/productos/" + encoded);
                return mapper.readValue(json, ProductoDTO.class);
            }

            @Override
            protected void done() {
                try {
                    ProductoDTO p = get();
                    BigDecimal precio = p.getPrecioVentaLista() != null
                        ? p.getPrecioVentaLista() : BigDecimal.ZERO;
                    BigDecimal cantidad   = BigDecimal.ONE;
                    BigDecimal importe    = precio;
                    BigDecimal existencia = p.getExistencia() != null
                        ? p.getExistencia() : BigDecimal.ZERO;

                    modelActivo.addRow(new Object[]{
                        p.getCodigoBarras(),
                        p.getDescripcion(),
                        precio,
                        cantidad,
                        importe,
                        existencia,
                        "0%"         // descuento inicial
                    });
                    actualizarTotal(modelActivo);
                    txtCodigo.setText("");
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Producto no encontrado", ex);
                    JOptionPane.showMessageDialog(VENTAS.this,
                        "Producto no encontrado: " + codigo,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Elimina el renglón seleccionado del ticket activo.
     * No hace ninguna llamada a la API: solo borra la fila de la tabla.
     */
    private void eliminarRenglonDelTicket() {
        JTable tabla = getTablaActiva();
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un producto de la lista para eliminarlo.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = getModeloActivo();
        String desc = (String) model.getValueAt(fila, COL_DESC);
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Quitar \"" + desc + "\" del ticket?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            model.removeRow(fila);
            actualizarTotal(model);
        }
    }

    /** Muestra un diálogo para ingresar el % de descuento al renglón seleccionado. */
    private void aplicarDescuento() {
        JTable tabla = getTablaActiva();
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un producto para aplicar descuento.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = getModeloActivo();
        String desc = (String) model.getValueAt(fila, COL_DESC);

        String input = JOptionPane.showInputDialog(this,
            "Descuento (%) para \"" + desc + "\":",
            "Aplicar Descuento", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) return;

        try {
            double pct = Double.parseDouble(input.replace("%", "").trim());
            if (pct < 0 || pct > 100) throw new NumberFormatException();

            model.setValueAt(pct + "%", fila, COL_DESCUENTO);
            recalcularImporte(model, fila);
            actualizarTotal(model);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Ingresa un número válido entre 0 y 100.",
                "Valor inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cálculos
    // ─────────────────────────────────────────────────────────────────────────

    /** Importe = precio * cantidad * (1 - descuento/100) */
    private void recalcularImporte(DefaultTableModel model, int fila) {
        BigDecimal precio    = parseBD(model.getValueAt(fila, COL_PRECIO));
        BigDecimal cantidad  = parseBD(model.getValueAt(fila, COL_CANT));
        String descStr       = String.valueOf(model.getValueAt(fila, COL_DESCUENTO));
        double descPct       = 0.0;
        try {
            descPct = Double.parseDouble(descStr.replace("%", "").trim());
        } catch (NumberFormatException ignored) {}

        BigDecimal factor  = BigDecimal.ONE.subtract(
            BigDecimal.valueOf(descPct / 100.0));
        BigDecimal importe = precio.multiply(cantidad).multiply(factor)
            .setScale(2, RoundingMode.HALF_UP);
        model.setValueAt(importe, fila, COL_IMPORTE);
    }

    /** Suma los importes de todas las filas del modelo y actualiza la etiqueta. */
    private void actualizarTotal(DefaultTableModel model) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            total = total.add(parseBD(model.getValueAt(i, COL_IMPORTE)));
        }
        lblTotal.setText("$ " + total.setScale(2, RoundingMode.HALF_UP));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private DefaultTableModel getModeloActivo() {
        return ticketModels.get(ticketPane.getSelectedIndex());
    }

    private JTable getTablaActiva() {
        JScrollPane scroll = (JScrollPane) ticketPane.getSelectedComponent();
        return (JTable) scroll.getViewport().getView();
    }

    private BigDecimal parseBD(Object val) {
        if (val == null) return BigDecimal.ZERO;
        try { return new BigDecimal(val.toString()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}