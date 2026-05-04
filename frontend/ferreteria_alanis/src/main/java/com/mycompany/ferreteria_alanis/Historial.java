package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla de Historial de Ventas.
 * Endpoints que el backend debe exponer:
 *
 *   GET /ventas/historial?fecha=YYYY-MM-DD
 *        Devuelve: [{folioTicket, articulos, horaTicket, totalNeto}, ...]
 *
 *   GET /ventas/historial/{folio}
 *        Devuelve: {folioTicket, nombreUsuario, totalNeto, fechaTicket,
 *                   porcentajeDescuento,
 *                   detalles:[{codigoBarras, nombreProducto,
 *                              precioUnitarioVenta, cantidad,
 *                              importe, descuentoProducto}]}
 */
public class Historial extends JFrame {

    private static final Logger logger =
            Logger.getLogger(Historial.class.getName());

    private final ApiClient api = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    private final String rol;
    // nombreUsuario se obtiene de SesionActual para pasarlo al navegar a VENTAS
    private final String nombreUsuario;

    private static final Color NARANJA       = new Color(255, 153, 0);
    private static final Color NARANJA_CLARO = new Color(255, 204, 102);
    private static final Color GRIS          = new Color(217, 217, 217);

    private LocalDate fechaActual = LocalDate.now();

    private JTextField txtBusqueda;
    private JLabel lblFechaValor;
    private JTable tablaFolios;
    private DefaultTableModel modeloFolios;

    private JLabel lblFolioValor;
    private JLabel lblCajeroValor;
    private JLabel lblTotalValor;
    private JLabel lblFechaTicket;
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;

    // ─── Constructor corregido: ahora recibe también nombreUsuario ───────────
    public Historial(String rol, String nombreUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        setTitle("Ferretería e Instalaciones Eléctricas Alanís");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        cargarHistorialDelDia();
    }

    // ─── Compatibilidad: si alguien llama Historial(rol) usa SesionActual ───
    public Historial(String rol) {
        this(rol, SesionActual.getNombreUsuario());
    }

    //  CONSTRUCCIÓN DE LA INTERFAZ

    private void initComponents() {

        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setBackground(GRIS);
        barraTop.setPreferredSize(new Dimension(0, 55));

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icono = new ImageIcon("logo.png");
            Image img = icono.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            logger.log(Level.WARNING, "No se pudo cargar logo.png", ex);
        }

        JLabel nombreEmpresa = new JLabel("  Ferretería e Instalaciones Eléctricas Alanís");
        nombreEmpresa.setFont(new Font("Arial", Font.BOLD, 13));

        // Botón "Usuario: X" en la esquina superior derecha
        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));

        JPanel izqTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        izqTop.setBackground(GRIS);
        izqTop.add(logoLabel);
        izqTop.add(nombreEmpresa);
        barraTop.add(izqTop, BorderLayout.WEST);
        barraTop.add(btnUsuarioInfo, BorderLayout.EAST);

        JPanel barraModulos = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 3));
        barraModulos.setBackground(GRIS);
        barraModulos.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JButton btnVentas = botonModulo("VENTAS");
        // CORRECCIÓN: se pasan los dos parámetros requeridos por VENTAS
        btnVentas.addActionListener(e -> {
            new VENTAS(rol, nombreUsuario).setVisible(true);
            this.dispose();
        });

        JButton btnProductos = botonModulo("PRODUCTOS");
        JButton btnInventario = botonModulo("INVENTARIO");
        JButton btnCorte = botonModulo("CORTE");
        JButton btnUsuario = botonModulo("USUARIO");

        barraModulos.add(btnVentas);
        barraModulos.add(btnProductos);
        barraModulos.add(btnInventario);
        barraModulos.add(btnCorte);
        barraModulos.add(btnUsuario);

        if ("ADMIN".equalsIgnoreCase(rol)) {
            btnProductos.setVisible(true);
            btnProductos.addActionListener(e -> {
                new PRODUCTOS(rol, nombreUsuario).setVisible(true);
                this.dispose();
            });
            btnCorte.setVisible(true);
            btnCorte.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte",
                    JOptionPane.INFORMATION_MESSAGE));
            btnUsuario.setVisible(true);
            btnUsuario.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Usuario",
                    JOptionPane.INFORMATION_MESSAGE));
        } else {
            btnProductos.setVisible(false);
            btnCorte.setVisible(false);
            btnUsuario.setVisible(false);
        }

        JPanel barraNaranja = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        barraNaranja.setBackground(NARANJA);
        JLabel lblVentas = new JLabel("Ventas");
        lblVentas.setFont(new Font("Arial", Font.BOLD, 13));
        barraNaranja.add(lblVentas);

        JPanel barraSubtabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        barraSubtabs.setBackground(GRIS);
        barraSubtabs.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JButton btnTicket = new JButton("Ticket");
        btnTicket.setBackground(GRIS);
        btnTicket.setFont(new Font("Arial", Font.PLAIN, 12));
        btnTicket.setFocusPainted(false);
        // CORRECCIÓN: se pasan los dos parámetros requeridos por VENTAS
        btnTicket.addActionListener(e -> {
            new VENTAS(rol, nombreUsuario).setVisible(true);
            this.dispose();
        });

        JButton btnHistorial = new JButton("Historial");
        btnHistorial.setBackground(NARANJA_CLARO);
        btnHistorial.setFont(new Font("Arial", Font.BOLD, 12));
        btnHistorial.setFocusPainted(false);

        barraSubtabs.add(btnTicket);
        barraSubtabs.add(btnHistorial);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(barraTop);
        norte.add(barraModulos);
        norte.add(barraNaranja);
        norte.add(barraSubtabs);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                crearPanelIzquierdo(),
                crearPanelDerecho());
        split.setDividerLocation(420);
        split.setDividerSize(3);
        split.setBackground(GRIS);

        setLayout(new BorderLayout());
        add(norte, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private JPanel crearPanelIzquierdo() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(GRIS);
        p.setBorder(new EmptyBorder(10, 10, 10, 6));

        JLabel titulo = new JLabel("Búsqueda por folio");
        titulo.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(GRIS);
        JLabel lupa = new JLabel("🔍");
        txtBusqueda = new JTextField();
        txtBusqueda.setBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarPorFolio(txtBusqueda.getText().trim());
            }
        });

        panelBusqueda.add(lupa, BorderLayout.WEST);
        panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);

        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelFecha.setBackground(GRIS);
        panelFecha.add(new JLabel("Del día:"));

        lblFechaValor = new JLabel(formatearFechaLarga(fechaActual));
        lblFechaValor.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFechaValor.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblFechaValor.setPreferredSize(new Dimension(190, 22));

        JButton btnFlecha = new JButton("▼");
        btnFlecha.setPreferredSize(new Dimension(30, 22));
        btnFlecha.setFocusPainted(false);
        btnFlecha.setMargin(new Insets(0, 0, 0, 0));
        btnFlecha.addActionListener(e -> mostrarSelectorFecha());

        JButton btnHoy = new JButton("Hoy");
        btnHoy.setFont(new Font("Arial", Font.PLAIN, 11));
        btnHoy.setFocusPainted(false);
        btnHoy.addActionListener(e -> {
            fechaActual = LocalDate.now();
            lblFechaValor.setText(formatearFechaLarga(fechaActual));
            cargarHistorialDelDia();
        });

        panelFecha.add(lblFechaValor);
        panelFecha.add(btnFlecha);
        panelFecha.add(btnHoy);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(GRIS);
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(panelBusqueda, BorderLayout.CENTER);
        norte.add(panelFecha, BorderLayout.SOUTH);

        String[] colsFolios = {"Folio", "Arts", "Hora", "Total"};
        modeloFolios = new DefaultTableModel(colsFolios, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaFolios = new JTable(modeloFolios);
        estilizarTabla(tablaFolios);
        tablaFolios.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaFolios.getColumnModel().getColumn(1).setPreferredWidth(40);
        tablaFolios.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaFolios.getColumnModel().getColumn(3).setPreferredWidth(90);

        tablaFolios.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()
                    && tablaFolios.getSelectedRow() >= 0) {
                Object val = modeloFolios.getValueAt(
                        tablaFolios.getSelectedRow(), 0);
                cargarDetalleTicket(Integer.parseInt(val.toString()));
            }
        });

        JScrollPane scroll = new JScrollPane(tablaFolios);
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        p.add(norte, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelDerecho() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new GridBagLayout());
        encabezado.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 6, 2, 6);

        JLabel tTicket = new JLabel("Ticket");
        tTicket.setFont(new Font("Arial", Font.BOLD, 15));

        lblFolioValor = new JLabel("—");
        lblCajeroValor = new JLabel("—");
        lblTotalValor = new JLabel("—");
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblFechaTicket = new JLabel("—");
        lblFechaTicket.setFont(new Font("Arial", Font.BOLD, 12));

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        encabezado.add(tTicket, g);

        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 1; encabezado.add(new JLabel("Folio:"), g);
        g.gridx = 1; encabezado.add(lblFolioValor, g);
        g.gridx = 2; encabezado.add(new JLabel("Total:"), g);
        g.gridx = 3; encabezado.add(lblTotalValor, g);
        g.gridx = 0; g.gridy = 2; encabezado.add(new JLabel("Cajero:"), g);
        g.gridx = 1; g.gridwidth = 3; encabezado.add(lblCajeroValor, g);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        encabezado.add(lblFechaTicket, g);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(Color.WHITE);
        norte.add(encabezado, BorderLayout.CENTER);
        norte.add(new JSeparator(), BorderLayout.SOUTH);

        String[] colsDet = {"Código", "Descripción", "Precio",
                            "Cant.", "Importe", "%"};
        modeloDetalle = new DefaultTableModel(colsDet, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaDetalle = new JTable(modeloDetalle);
        estilizarTabla(tablaDetalle);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaDetalle.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaDetalle.getColumnModel().getColumn(3).setPreferredWidth(45);
        tablaDetalle.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaDetalle.getColumnModel().getColumn(5).setPreferredWidth(35);

        JScrollPane scroll = new JScrollPane(tablaDetalle);
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        p.add(norte, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    //  LÓGICA DE DATOS — ApiClient

    /** GET /ventas/historial?fecha=YYYY-MM-DD */
    private void cargarHistorialDelDia() {
        modeloFolios.setRowCount(0);
        limpiarDetalle();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String endpoint = "/ventas/historial?fecha="
                        + fechaActual.toString();
                String json = api.get(endpoint);
                JsonNode array = mapper.readTree(json);

                SwingUtilities.invokeLater(() -> {
                    modeloFolios.setRowCount(0);
                    for (JsonNode t : array) {
                        modeloFolios.addRow(new Object[]{
                            t.path("folioTicket").asInt(),
                            t.path("articulos").asInt(),
                            formatearHora(textOrDefault(t, "horaTicket", "")),
                            "$" + String.format("%.2f",
                                t.path("totalNeto").asDouble())
                        });
                    }
                });

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error al cargar historial", ex);
                    mostrarError("Error al cargar historial", ex);
                }
            }
        }.execute();
    }

    /** GET /ventas/historial/{folio} */
    private void cargarDetalleTicket(int folio) {
        modeloDetalle.setRowCount(0);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String json = api.get("/ventas/historial/" + folio);
                JsonNode obj = mapper.readTree(json);

                SwingUtilities.invokeLater(() -> {
                    lblFolioValor.setText(
                            String.valueOf(obj.path("folioTicket").asInt()));
                    lblCajeroValor.setText(
                            textOrDefault(obj, "nombreUsuario", "—"));
                    lblTotalValor.setText(
                            "$" + String.format("%.2f",
                                obj.path("totalNeto").asDouble()));
                    String fechaStr = textOrDefault(obj, "fechaTicket", LocalDate.now().toString());
                    try {
                        lblFechaTicket.setText(
                                formatearFechaLarga(LocalDate.parse(fechaStr)));
                    } catch (Exception ex) {
                        lblFechaTicket.setText(fechaStr);
                    }

                    JsonNode detalles = obj.path("detalles");
                    for (JsonNode d : detalles) {
                        modeloDetalle.addRow(new Object[]{
                            d.path("codigoBarras").asText(),
                            d.path("nombreProducto").asText(),
                            "$" + String.format("%.2f",
                                d.path("precioUnitarioVenta").asDouble()),
                            d.path("cantidad").asText(),
                            "$" + String.format("%.2f",
                                d.path("importe").asDouble()),
                            String.format("%.0f%%",
                                d.path("descuentoProducto").asDouble())
                        });
                    }
                });

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error al cargar detalle", ex);
                    mostrarError("Error al cargar ticket " + folio, ex);
                }
            }
        }.execute();
    }

    /** Filtra la tabla izquierda sin nueva petición HTTP */
    private void filtrarPorFolio(String texto) {
        if (texto.isEmpty()) {
            tablaFolios.setRowSorter(null);
            return;
        }

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(modeloFolios);
        tablaFolios.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
    }

    //  UTILIDADES

    private void limpiarDetalle() {
        if (lblFolioValor != null) lblFolioValor.setText("—");
        if (lblCajeroValor != null) lblCajeroValor.setText("—");
        if (lblTotalValor != null) lblTotalValor.setText("—");
        if (lblFechaTicket != null) lblFechaTicket.setText("—");
        if (modeloDetalle != null) modeloDetalle.setRowCount(0);
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.setBackground(GRIS);
        tabla.setSelectionBackground(NARANJA_CLARO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.getTableHeader().setBackground(GRIS);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private JButton botonModulo(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(NARANJA);
        b.setFont(new Font("ITF Devanagari Marathi", Font.PLAIN, 14));
        b.setFocusPainted(false);
        return b;
    }

    private void mostrarError(String titulo, Exception ex) {
        JOptionPane.showMessageDialog(this,
                titulo + ":\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarSelectorFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(
                java.sql.Date.valueOf(fechaActual),
                null,
                java.sql.Date.valueOf(LocalDate.now()),
                java.util.Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));

        int r = JOptionPane.showConfirmDialog(this, spinner,
                "Seleccionar fecha",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (r == JOptionPane.OK_OPTION) {
            java.util.Date d = (java.util.Date) spinner.getValue();
            fechaActual = d.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            lblFechaValor.setText(formatearFechaLarga(fechaActual));
            cargarHistorialDelDia();
        }
    }

    /** "HH:mm:ss" → "9:15 am" */
    private String formatearHora(String horaStr) {
        try {
            LocalTime t = LocalTime.parse(horaStr,
                    DateTimeFormatter.ofPattern("HH:mm:ss"));
            return t.format(DateTimeFormatter.ofPattern(
                    "h:mm a", Locale.ENGLISH)).toLowerCase();
        } catch (Exception ex) {
            return horaStr;
        }
    }

    /** LocalDate → "Martes, 1 de abril de 2026" */
    private String formatearFechaLarga(LocalDate d) {
        try {
            return d.format(DateTimeFormatter.ofPattern(
                    "EEEE, d 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("es-MX")));
        } catch (Exception ex) {
            return d.toString();
        }
    }

    private void mostrarPopupSesion(JButton origen) {
        JDialog popup = new JDialog(this, false);
        popup.setUndecorated(true);
        popup.setLayout(new BorderLayout());
 
        JPanel contenedor = new JPanel(new GridLayout(0, 1, 0, 0));
        contenedor.setBackground(new Color(230, 230, 230));
        contenedor.setBorder(BorderFactory.createLineBorder(Color.GRAY));
 
        JLabel titulo = new JLabel("Sesión", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(NARANJA);
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
 
    private String textOrDefault(JsonNode node, String campo, String defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        JsonNode value = node.path(campo);
        if (value.isMissingNode() || value.isNull()) {
            return defaultValue;
        }
        return value.asText();
    }
}