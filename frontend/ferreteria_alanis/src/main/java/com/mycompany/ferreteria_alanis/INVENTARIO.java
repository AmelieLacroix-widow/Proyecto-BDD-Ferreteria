package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Módulo de Inventario.
 *
 * Muestra:
 *   - Costo total del inventario  (suma de precioCosto × existencia)
 *   - Cantidad total en inventario (suma de existencias)
 *   - Tabla con todos los productos que usan inventario:
 *       Código de barras | Descripción | Costo | Venta | Existencia | Inv. Min. | Inv. Max.
 *
 * Endpoint usado: GET /productos/inventario
 */
public class INVENTARIO extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(INVENTARIO.class.getName());

    private static final Color COLOR_NARANJA = new Color(255, 153, 0);
    private static final Color COLOR_NEGRO   = Color.BLACK;
    private static final Color COLOR_FONDO   = new Color(230, 230, 230);

    private final String rol;
    private final String nombreUsuario;
    private final ApiClient    api    = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    // ── Componentes del reporte ──────────────────────────────────────────────
    private JLabel           lblCostoValor;
    private JLabel           lblCantidadValor;
    private DefaultTableModel modeloInventario;
    private JTextField        txtBusqueda;

    // ─────────────────────────────────────────────────────────────────────────

    public INVENTARIO(String rol, String nombreUsuario) {
        this.rol           = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís – Inventario");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        cargarInventario();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI principal
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),     BorderLayout.NORTH);
        add(crearContenido(),  BorderLayout.CENTER);
    }

    // ── Barra de navegación (idéntica a PRODUCTOS) ───────────────────────────

    private JPanel crearNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(COLOR_NEGRO);

        // Header: logo + nombre empresa + botón usuario
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
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));
        header.add(btnUsuarioInfo, BorderLayout.EAST);
        nav.add(header, BorderLayout.NORTH);

        // Fila de módulos
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

        JButton btnProductos = crearBtnNav("Productos", false);
        btnProductos.addActionListener(e -> {
            new PRODUCTOS(rol, nombreUsuario).setVisible(true);
            dispose();
        });
        // Solo admin ve Productos
        btnProductos.setVisible("ADMIN".equalsIgnoreCase(rol));
        izqMod.add(btnProductos);

        JButton btnInventario = crearBtnNav("Inventario", true);
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

        if ("ADMIN".equalsIgnoreCase(rol)) {
            derMod.add(btnCorte);
            derMod.add(btnUsuario);
        }
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

    // ── Contenido principal ──────────────────────────────────────────────────

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);

        // Título naranja
        JLabel lblTitulo = new JLabel("  Inventario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 30));

        // Panel interior con todo el reporte
        JPanel reporte = new JPanel(new BorderLayout(0, 8));
        reporte.setBackground(COLOR_FONDO);
        reporte.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        reporte.add(crearPanelReporte(), BorderLayout.NORTH);
        reporte.add(crearPanelTabla(),   BorderLayout.CENTER);

        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(reporte,   BorderLayout.CENTER);
        return contenido;
    }

    /** Panel superior: título "Reporte de Inventario" + métricas */
    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_FONDO);

        JLabel lblReporte = new JLabel("Reporte de Inventario");
        lblReporte.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblReporte, BorderLayout.NORTH);

        // Métricas: Costo del inventario | Cantidad en inventario
        JPanel metricas = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        metricas.setBackground(COLOR_FONDO);

        JPanel panelCosto = new JPanel(new GridLayout(2, 1));
        panelCosto.setBackground(COLOR_FONDO);
        JLabel lblCostoTitulo = new JLabel("Costo del inventario:");
        lblCostoTitulo.setFont(new Font("Arial", Font.BOLD, 13));
        lblCostoValor = new JLabel("$0.00");
        lblCostoValor.setFont(new Font("Arial", Font.PLAIN, 14));
        panelCosto.add(lblCostoTitulo);
        panelCosto.add(lblCostoValor);

        JPanel panelCantidad = new JPanel(new GridLayout(2, 1));
        panelCantidad.setBackground(COLOR_FONDO);
        JLabel lblCantidadTitulo = new JLabel("Cantidad en inventario:");
        lblCantidadTitulo.setFont(new Font("Arial", Font.BOLD, 13));
        lblCantidadValor = new JLabel("0");
        lblCantidadValor.setFont(new Font("Arial", Font.PLAIN, 14));
        panelCantidad.add(lblCantidadTitulo);
        panelCantidad.add(lblCantidadValor);

        metricas.add(panelCosto);
        metricas.add(panelCantidad);
        panel.add(metricas, BorderLayout.CENTER);

        return panel;
    }

    /** Panel con búsqueda + tabla de productos con inventario */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(COLOR_FONDO);

        // Barra de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(COLOR_FONDO);
        panelBusqueda.setMaximumSize(new Dimension(460, 30));
        JLabel lblLupa = new JLabel("🔍");
        txtBusqueda = new JTextField();
        txtBusqueda.setPreferredSize(new Dimension(460, 28));
        panelBusqueda.add(lblLupa,       BorderLayout.WEST);
        panelBusqueda.add(txtBusqueda,   BorderLayout.CENTER);

        // Tabla
        String[] columnas = {
            "Código de barras", "Descripción",
            "Costo", "Venta", "Existencia", "Inv. Min.", "Inv. Max."
        };
        modeloInventario = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloInventario);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.setBackground(COLOR_FONDO);
        tabla.setSelectionBackground(new Color(255, 204, 102));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.getTableHeader().setBackground(COLOR_FONDO);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(75);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(75);

        // Filtro de búsqueda en tiempo real
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBusqueda.getText().trim();
                if (texto.isEmpty()) {
                    tabla.setRowSorter(null);
                } else {
                    TableRowSorter<DefaultTableModel> sorter =
                        new TableRowSorter<>(modeloInventario);
                    tabla.setRowSorter(sorter);
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        panel.add(panelBusqueda,       BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Carga de datos
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarInventario() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // GET /productos/inventario — sólo productos con usaInventario=true
                String json = api.get("/productos/inventario");
                JsonNode array = mapper.readTree(json);

                BigDecimal costoTotal    = BigDecimal.ZERO;
                BigDecimal cantidadTotal = BigDecimal.ZERO;

                SwingUtilities.invokeLater(() -> modeloInventario.setRowCount(0));

                for (JsonNode n : array) {
                    BigDecimal precioCosto = getBD(n, "precioCosto");
                    BigDecimal precioVenta = getBD(n, "precioVentaLista");
                    BigDecimal existencia  = getBD(n, "existencia");
                    BigDecimal invMin      = n.path("invMinimo").isNull()
                        ? null : getBD(n, "invMinimo");
                    BigDecimal invMax      = n.path("invMaximo").isNull()
                        ? null : getBD(n, "invMaximo");

                    // Acumular totales
                    costoTotal    = costoTotal.add(precioCosto.multiply(existencia));
                    cantidadTotal = cantidadTotal.add(existencia);

                    final BigDecimal fCosto    = precioCosto;
                    final BigDecimal fVenta    = precioVenta;
                    final BigDecimal fExist    = existencia;
                    final String    fCod       = n.path("codigoBarras").asText();
                    final String    fDesc      = n.path("descripcion").asText();
                    final String    fInvMin    = invMin != null
                        ? invMin.stripTrailingZeros().toPlainString() : "---";
                    final String    fInvMax    = invMax != null
                        ? invMax.stripTrailingZeros().toPlainString() : "---";

                    final Object[] fila = {
                        fCod,
                        fDesc,
                        "$" + fCosto.setScale(2, RoundingMode.HALF_UP),
                        "$" + fVenta.setScale(2, RoundingMode.HALF_UP),
                        fExist.stripTrailingZeros().toPlainString(),
                        fInvMin,
                        fInvMax
                    };
                    SwingUtilities.invokeLater(() -> modeloInventario.addRow(fila));
                }

                // Actualizar métricas en el EDT
                final BigDecimal costoFinal    = costoTotal;
                final BigDecimal cantidadFinal = cantidadTotal;
                SwingUtilities.invokeLater(() -> {
                    lblCostoValor.setText(
                        "$" + costoFinal.setScale(2, RoundingMode.HALF_UP)
                            .toPlainString());
                    lblCantidadValor.setText(
                        cantidadFinal.stripTrailingZeros().toPlainString());
                });

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al cargar inventario", ex);
                    JOptionPane.showMessageDialog(INVENTARIO.this,
                        "Error al cargar el inventario:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private BigDecimal getBD(JsonNode node, String campo) {
        JsonNode n = node.path(campo);
        if (n.isNull() || n.isMissingNode()) return BigDecimal.ZERO;
        try { return new BigDecimal(n.asText()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Popup de sesión (idéntico al de PRODUCTOS y VENTAS)
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
}