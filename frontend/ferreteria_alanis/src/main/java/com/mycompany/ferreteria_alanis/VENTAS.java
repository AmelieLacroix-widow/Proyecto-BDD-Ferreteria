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

        // "Usuario: X" — popup para cerrar sesión
        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(COLOR_NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));
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
        btnHistorial.addActionListener(e -> {
            new Historial(rol, nombreUsuario).setVisible(true);
            dispose();
        });
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

        // ── Pestañas de tickets (inicia con solo 1) ───────────────────────
        ticketPane = new JTabbedPane();
        ticketPane.setBackground(COLOR_FONDO);

        // Solo el primer ticket al arrancar
        DefaultTableModel model1 = crearModeloTicket();
        ticketModels.add(model1);
        ticketPane.addTab("Ticket 1", crearPanelTicket(model1));

        // El botón "Ticket" agrega pestañas dinámicamente (sin límite fijo)
        btnTicket.addActionListener(e -> {
            int num = ticketModels.size() + 1;
            DefaultTableModel nuevoModel = crearModeloTicket();
            ticketModels.add(nuevoModel);
            ticketPane.addTab("Ticket " + num, crearPanelTicket(nuevoModel));
            ticketPane.setSelectedIndex(ticketPane.getTabCount() - 1);
        });

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
        btnEliminar.addActionListener(e -> eliminarSeleccion());

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
        btnCobrar.addActionListener(e -> mostrarDialogoCobro());

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

    // ─────────────────────────────────────────────────────────────────────────
    // Popup de sesión (botón Usuario)
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

        // Posicionar debajo del botón origen
        Point p = origen.getLocationOnScreen();
        popup.setLocation(p.x, p.y + origen.getHeight());
        popup.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Eliminar: ticket si no hay producto seleccionado, producto si lo hay
    // ─────────────────────────────────────────────────────────────────────────

    private void eliminarSeleccion() {
        JTable tabla = getTablaActiva();
        int filaProducto = tabla.getSelectedRow();

        if (filaProducto >= 0) {
            // Hay un producto seleccionado → eliminar ese renglón
            eliminarRenglonDelTicket();
        } else {
            // No hay producto → ofrecer eliminar el ticket activo
            int tabIdx = ticketPane.getSelectedIndex();
            if (ticketModels.size() <= 1) {
                JOptionPane.showMessageDialog(this,
                    "No puedes eliminar el único ticket abierto.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                "¿Cerrar el ticket \"" + ticketPane.getTitleAt(tabIdx) + "\"?",
                "Eliminar ticket", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                ticketModels.remove(tabIdx);
                ticketPane.removeTabAt(tabIdx);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Diálogo de cobro con métodos: Efectivo, Tarjeta, Mixto
    // Flujo real: POST /tickets → POST /tickets/{folio}/detalle (x renglon) → POST /tickets/{folio}/pago
    // ─────────────────────────────────────────────────────────────────────────

    private void mostrarDialogoCobro() {
        DefaultTableModel modelActivo = getModeloActivo();

        // ── Calcular totales ─────────────────────────────────────────────
        java.math.BigDecimal totalVenta = java.math.BigDecimal.ZERO;
        int totalArticulos = 0;
        for (int i = 0; i < modelActivo.getRowCount(); i++) {
            totalVenta = totalVenta.add(parseBD(modelActivo.getValueAt(i, COL_IMPORTE)));
            totalArticulos += parseBD(modelActivo.getValueAt(i, COL_CANT)).intValue();
        }
        if (totalVenta.compareTo(java.math.BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(this, "El ticket está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final java.math.BigDecimal totalFinal = totalVenta;
        final int artsFinal = totalArticulos;

        // Snapshot de renglones para el SwingWorker (captura antes de cerrar diálogo)
        record Renglon(String codigo, java.math.BigDecimal precio,
                       java.math.BigDecimal cantidad, java.math.BigDecimal importe,
                       java.math.BigDecimal descuento) {}
        java.util.List<Renglon> renglones = new java.util.ArrayList<>();
        for (int i = 0; i < modelActivo.getRowCount(); i++) {
            renglones.add(new Renglon(
                modelActivo.getValueAt(i, COL_CODIGO).toString(),
                parseBD(modelActivo.getValueAt(i, COL_PRECIO)),
                parseBD(modelActivo.getValueAt(i, COL_CANT)),
                parseBD(modelActivo.getValueAt(i, COL_IMPORTE)),
                java.math.BigDecimal.ZERO   // descuento por producto = 0 por ahora
            ));
        }

        // ── Construir diálogo ────────────────────────────────────────────
        JDialog dlg = new JDialog(this, "Ticket", true);
        dlg.setSize(520, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JLabel header = new JLabel("Ticket", SwingConstants.LEFT);
        header.setOpaque(true);
        header.setBackground(COLOR_NARANJA);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dlg.add(header, BorderLayout.NORTH);

        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(new Color(220, 220, 220));
        izq.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 10));

        JLabel lblMonto = new JLabel("$" + totalFinal.setScale(2, java.math.RoundingMode.HALF_UP));
        lblMonto.setFont(new Font("Arial", Font.BOLD, 32));
        lblMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        izq.add(lblMonto);
        izq.add(Box.createVerticalStrut(12));

        JPanel panelMetodos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelMetodos.setOpaque(false);
        JButton btnEfectivo = new JButton("Efectivo");
        JButton btnTarjeta  = new JButton("Tarjeta");
        JButton btnMixto    = new JButton("Mixto");
        Color inactivo = new Color(180, 180, 180);
        for (JButton b : new JButton[]{btnEfectivo, btnTarjeta, btnMixto}) {
            b.setBackground(inactivo);
            b.setFont(new Font("Arial", Font.BOLD, 12));
            b.setPreferredSize(new Dimension(80, 55));
            b.setFocusPainted(false);
            panelMetodos.add(b);
        }
        izq.add(panelMetodos);
        izq.add(Box.createVerticalStrut(12));

        JPanel panelCampos = new JPanel();
        panelCampos.setOpaque(false);
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        izq.add(panelCampos);

        JPanel der = new JPanel();
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        der.setBackground(new Color(200, 200, 200));
        der.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        der.setPreferredSize(new Dimension(160, 0));

        JButton btnCobrarFinal = new JButton("Cobrar");
        btnCobrarFinal.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCobrarFinal.setMaximumSize(new Dimension(120, 35));
        btnCobrarFinal.setFont(new Font("Arial", Font.BOLD, 13));
        btnCobrarFinal.setBackground(new Color(102, 204, 0));

        JButton btnCancelarDlg = new JButton("Cancelar");
        btnCancelarDlg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancelarDlg.setMaximumSize(new Dimension(120, 35));
        btnCancelarDlg.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelarDlg.addActionListener(ev -> dlg.dispose());

        JLabel lblArtsLbl = new JLabel("Total de artículos:");
        lblArtsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblArtsLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lblArtsVal = new JLabel(String.valueOf(artsFinal));
        lblArtsVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblArtsVal.setFont(new Font("Arial", Font.BOLD, 28));

        der.add(btnCobrarFinal);
        der.add(Box.createVerticalStrut(6));
        der.add(btnCancelarDlg);
        der.add(Box.createVerticalStrut(20));
        der.add(lblArtsLbl);
        der.add(lblArtsVal);

        dlg.add(izq, BorderLayout.CENTER);
        dlg.add(der, BorderLayout.EAST);

        // ── Estado compartido de pago ────────────────────────────────────
        // Usamos arrays de 1 elemento para poder mutar desde lambdas
        final String[]                   metodoActual  = {null};
        final java.math.BigDecimal[]     montoEfectivo = {java.math.BigDecimal.ZERO};
        final java.math.BigDecimal[]     pagoCon       = {java.math.BigDecimal.ZERO};
        final java.math.BigDecimal[]     cambio        = {java.math.BigDecimal.ZERO};
        final java.math.BigDecimal[]     montoTarjeta  = {java.math.BigDecimal.ZERO};
        final String[]                   refTarjeta    = {""};
        final java.math.BigDecimal[]     montoTransf   = {java.math.BigDecimal.ZERO};

        // ── Lógica de cobro real ─────────────────────────────────────────
        // Capturamos la pestaña activa ANTES de que el worker cierre el diálogo
        final int tabActiva = ticketPane.getSelectedIndex();

        Runnable ejecutarCobro = () -> {
            if (metodoActual[0] == null) {
                JOptionPane.showMessageDialog(dlg, "Selecciona un método de pago.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            btnCobrarFinal.setEnabled(false);
            btnCobrarFinal.setText("Procesando…");

            // Obtener el idUsuario de la sesión actual (necesario para el Ticket)
            // Se pasa como objeto anidado { "idUsuario": X }
            // Si SesionActual no guarda el id, usaremos un endpoint de login para obtenerlo
            // Por ahora lo leemos buscando por nombre de usuario
            new SwingWorker<Integer, String>() {

                @Override
                protected Integer doInBackground() throws Exception {
                    // ── Paso 0: obtener idUsuario de la sesión ───────────
                    publish("Verificando usuario…");
                    int idUsuario = SesionActual.getIdUsuario();
                    if (idUsuario == 0) {
                        throw new Exception("No se encontró el ID del usuario en la sesión. Vuelve a iniciar sesión.");
                    }

                    // ── Paso 1: crear el Ticket ──────────────────────────
                    publish("Creando ticket…");
                    java.time.LocalDate hoy   = java.time.LocalDate.now();
                    java.time.LocalTime ahora = java.time.LocalTime.now();
                    // totalBruto = totalNeto (sin descuento global por ahora)
                    String jsonTicket = "{"
                        + "\"tipoDocumento\":\"Ticket\","
                        + "\"estadoDocumento\":\"Pagado\","
                        + "\"fechaTransaccion\":\"" + hoy + "\","
                        + "\"horaTransaccion\":\"" + ahora.toString().substring(0, 8) + "\","
                        + "\"totalBruto\":"          + totalFinal + ","
                        + "\"porcentajeDescuento\":0,"
                        + "\"totalDescuento\":0,"
                        + "\"totalNeto\":"            + totalFinal + ","
                        + "\"usuario\":{\"idUsuario\":" + idUsuario + "}"
                        + "}";
                    String respTicket = api.post("/tickets", jsonTicket);
                    com.fasterxml.jackson.databind.JsonNode nTicket = mapper.readTree(respTicket);
                    int folio = nTicket.path("folioTicket").asInt();
                    if (folio == 0) throw new Exception("El backend no devolvió un folio válido.");

                    // ── Paso 2: registrar cada renglón ───────────────────
                    publish("Registrando productos…");
                    for (var r : renglones) {
                        String jsonDet = "{"
                            + "\"codigoBarras\":\"" + r.codigo() + "\","
                            + "\"cantidad\":"        + r.cantidad() + ","
                            + "\"precioUnitarioVenta\":" + r.precio() + ","
                            + "\"importe\":"         + r.importe() + ","
                            + "\"descuentoProducto\":" + r.descuento()
                            + "}";
                        api.post("/tickets/" + folio + "/detalle", jsonDet);
                    }

                    // ── Paso 3: registrar el pago ────────────────────────
                    publish("Registrando pago…");
                    // Construir JSON de Pago con los campos NOT NULL del modelo
                    // (todos los montos que no aplican van a 0)
                    java.math.BigDecimal cero = java.math.BigDecimal.ZERO;
                    String jsonPago = "{"
                        + "\"metodoPago\":\""          + metodoActual[0]    + "\","
                        + "\"montoEfectivo\":"          + montoEfectivo[0]   + ","
                        + "\"pagoCon\":"                + pagoCon[0]         + ","
                        + "\"cambio\":"                 + cambio[0]          + ","
                        + "\"montoTarjeta\":"           + montoTarjeta[0]    + ","
                        + "\"referenciaTarjeta\":\""    + refTarjeta[0]      + "\","
                        + "\"voucherTarjeta\":false,"
                        + "\"montoTransferencia\":"     + montoTransf[0]     + ","
                        + "\"referenciaTransferencia\":\"\","
                        + "\"voucherTransferencia\":false,"
                        + "\"montoCheque\":"            + cero               + ","
                        + "\"referenciaCheque\":\"\","
                        + "\"montoCredito\":"           + cero
                        + "}";
                    api.post("/tickets/" + folio + "/pago", jsonPago);

                    return folio;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    btnCobrarFinal.setText(chunks.getLast());
                }

                @Override
                protected void done() {
                    try {
                        int folio = get();
                        dlg.dispose();
                        // Limpiar la pestaña del ticket que se cobró
                        modelActivo.setRowCount(0);
                        actualizarTotal(modelActivo);
                        JOptionPane.showMessageDialog(VENTAS.this,
                            "✓ Venta registrada  —  Folio: " + folio,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error al cobrar", ex);
                        btnCobrarFinal.setEnabled(true);
                        btnCobrarFinal.setText("Cobrar");
                        JOptionPane.showMessageDialog(dlg,
                            "Error al registrar la venta:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        };

        // ── Campos dinámicos según método ────────────────────────────────
        Runnable actualizarCampos = () -> {
            panelCampos.removeAll();
            // Limpiar listeners previos del botón Cobrar reaplicando uno nuevo
            for (var l : btnCobrarFinal.getActionListeners()) btnCobrarFinal.removeActionListener(l);
            btnCobrarFinal.addActionListener(ev -> ejecutarCobro.run());

            String m = metodoActual[0];

            if ("efectivo".equals(m)) {
                montoTarjeta[0] = java.math.BigDecimal.ZERO;
                montoTransf[0]  = java.math.BigDecimal.ZERO;
                refTarjeta[0]   = "";

                JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row1.setOpaque(false);
                row1.add(new JLabel("Pago con:"));
                JTextField txtPago = new JTextField(8);
                txtPago.setText(totalFinal.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
                row1.add(txtPago);
                panelCampos.add(row1);

                JLabel lblCambio = new JLabel("Su cambio es:  $0.00");
                lblCambio.setFont(new Font("Arial", Font.BOLD, 13));
                panelCampos.add(lblCambio);

                txtPago.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    void recalc() {
                        try {
                            java.math.BigDecimal pagado = parseBDStr(txtPago.getText());
                            java.math.BigDecimal diff   = pagado.subtract(totalFinal);
                            montoEfectivo[0] = totalFinal;
                            pagoCon[0]       = pagado;
                            cambio[0]        = diff.max(java.math.BigDecimal.ZERO);
                            if (diff.compareTo(java.math.BigDecimal.ZERO) >= 0) {
                                lblCambio.setForeground(Color.BLACK);
                                lblCambio.setText("Su cambio es:  $"
                                    + diff.setScale(2, java.math.RoundingMode.HALF_UP));
                            } else {
                                lblCambio.setForeground(Color.RED);
                                lblCambio.setText("Restante:  $"
                                    + diff.abs().setScale(2, java.math.RoundingMode.HALF_UP));
                            }
                        } catch (Exception ignored) {}
                    }
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                });
                // Valores por defecto al seleccionar efectivo
                montoEfectivo[0] = totalFinal;
                pagoCon[0]       = totalFinal;
                cambio[0]        = java.math.BigDecimal.ZERO;

            } else if ("tarjeta".equals(m)) {
                montoEfectivo[0] = java.math.BigDecimal.ZERO;
                pagoCon[0]       = java.math.BigDecimal.ZERO;
                cambio[0]        = java.math.BigDecimal.ZERO;
                montoTarjeta[0]  = totalFinal;
                montoTransf[0]   = java.math.BigDecimal.ZERO;

                JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row1.setOpaque(false);
                row1.add(new JLabel("Referencia:"));
                JTextField txtRef = new JTextField(18);
                txtRef.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    void upd() { refTarjeta[0] = txtRef.getText().trim(); }
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { upd(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { upd(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { upd(); }
                });
                row1.add(txtRef);
                panelCampos.add(row1);

            } else if ("mixto".equals(m)) {
                montoTransf[0] = java.math.BigDecimal.ZERO;
                refTarjeta[0]  = "";

                JPanel rowEf = new JPanel(new FlowLayout(FlowLayout.LEFT));
                rowEf.setOpaque(false);
                rowEf.add(new JLabel("Efectivo:"));
                JTextField txtEfectivo = new JTextField(8);
                rowEf.add(txtEfectivo);
                panelCampos.add(rowEf);

                JPanel rowTj = new JPanel(new FlowLayout(FlowLayout.LEFT));
                rowTj.setOpaque(false);
                rowTj.add(new JLabel("Tarjeta:  "));
                JTextField txtTarjeta = new JTextField(8);
                rowTj.add(txtTarjeta);
                panelCampos.add(rowTj);

                JLabel lblRestante = new JLabel("Restante: $"
                    + totalFinal.setScale(2, java.math.RoundingMode.HALF_UP));
                lblRestante.setForeground(Color.RED);
                lblRestante.setFont(new Font("Arial", Font.BOLD, 13));
                panelCampos.add(lblRestante);

                javax.swing.event.DocumentListener dlMixto = new javax.swing.event.DocumentListener() {
                    void recalc() {
                        java.math.BigDecimal ef   = parseBDStr(txtEfectivo.getText());
                        java.math.BigDecimal tj   = parseBDStr(txtTarjeta.getText());
                        java.math.BigDecimal rest = totalFinal.subtract(ef).subtract(tj);
                        montoEfectivo[0] = ef;
                        montoTarjeta[0]  = tj;
                        pagoCon[0]       = ef;
                        cambio[0]        = rest.compareTo(java.math.BigDecimal.ZERO) < 0
                                           ? rest.abs() : java.math.BigDecimal.ZERO;
                        if (rest.compareTo(java.math.BigDecimal.ZERO) >= 0) {
                            lblRestante.setForeground(Color.RED);
                            lblRestante.setText("Restante: $"
                                + rest.setScale(2, java.math.RoundingMode.HALF_UP));
                        } else {
                            lblRestante.setForeground(new Color(0, 150, 0));
                            lblRestante.setText("Cambio: $"
                                + rest.abs().setScale(2, java.math.RoundingMode.HALF_UP));
                        }
                    }
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
                };
                txtEfectivo.getDocument().addDocumentListener(dlMixto);
                txtTarjeta.getDocument().addDocumentListener(dlMixto);
            }

            panelCampos.revalidate();
            panelCampos.repaint();
        };

        btnEfectivo.addActionListener(ev -> {
            metodoActual[0] = "efectivo";
            btnEfectivo.setBackground(COLOR_NARANJA);
            btnTarjeta.setBackground(inactivo);
            btnMixto.setBackground(inactivo);
            actualizarCampos.run();
        });
        btnTarjeta.addActionListener(ev -> {
            metodoActual[0] = "tarjeta";
            btnTarjeta.setBackground(COLOR_NARANJA);
            btnEfectivo.setBackground(inactivo);
            btnMixto.setBackground(inactivo);
            actualizarCampos.run();
        });
        btnMixto.addActionListener(ev -> {
            metodoActual[0] = "mixto";
            btnMixto.setBackground(COLOR_NARANJA);
            btnEfectivo.setBackground(inactivo);
            btnTarjeta.setBackground(inactivo);
            actualizarCampos.run();
        });

        dlg.setVisible(true);
    }

    private java.math.BigDecimal parseBDStr(String s) {
        if (s == null || s.isBlank()) return java.math.BigDecimal.ZERO;
        try { return new java.math.BigDecimal(s.replace("$","").replace(",","").trim()); }
        catch (Exception e) { return java.math.BigDecimal.ZERO; }
    }

    private BigDecimal parseBD(Object val) {
        if (val == null) return BigDecimal.ZERO;
        try { return new BigDecimal(val.toString()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}