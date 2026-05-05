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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla de Historial de Ventas.
 *
 * Endpoints reales que consume:
 *   GET /tickets/fecha?fecha=YYYY-MM-DD
 *        Devuelve List<Ticket> del dia (filtrado a tipo=Ticket y estado=Pagado)
 *
 *   GET /tickets/{folio}
 *        Devuelve el objeto Ticket con usuario.nombreUsuario anidado
 *
 *   GET /tickets/{folio}/detalle
 *        Devuelve List<DetalleTicket> con producto.descripcion gracias al
 *        JOIN FETCH agregado en DetalleTicketRepository.findByFolioTicket()
 */
public class Historial extends JFrame {

    private static final Logger logger = Logger.getLogger(Historial.class.getName());

    private final ApiClient api    = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    private final String rol;
    private final String nombreUsuario;

    private static final Color NARANJA       = new Color(255, 153, 0);
    private static final Color NARANJA_CLARO = new Color(255, 204, 102);
    private static final Color GRIS          = new Color(217, 217, 217);

    private LocalDate fechaActual = LocalDate.now();

    private JTextField     txtBusqueda;
    private JLabel         lblFechaValor;
    private JTable         tablaFolios;
    private DefaultTableModel modeloFolios;

    private JLabel         lblFolioValor;
    private JLabel         lblCajeroValor;
    private JLabel         lblTotalValor;
    private JLabel         lblFechaTicket;
    private JTable         tablaDetalle;
    private DefaultTableModel modeloDetalle;

    public Historial(String rol, String nombreUsuario) {
        this.rol           = rol;
        this.nombreUsuario = nombreUsuario;
        setTitle("Ferretería e Instalaciones Eléctricas Alanís");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        cargarHistorialDelDia();
    }

    /** Compatibilidad: si alguien llama Historial(rol) toma el usuario de SesionActual. */
    public Historial(String rol) {
        this(rol, SesionActual.getNombreUsuario());
    }

    // ─── CONSTRUCCIÓN DE LA INTERFAZ ─────────────────────────────────────────

    private void initComponents() {

        // ── Barra superior: logo + empresa + botón usuario ─────────────────
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

        // ── Barra de módulos ───────────────────────────────────────────────
        JPanel barraModulos = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 3));
        barraModulos.setBackground(GRIS);
        barraModulos.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JButton btnVentas    = botonModulo("VENTAS");
        JButton btnProductos = botonModulo("PRODUCTOS");
        JButton btnInventario = botonModulo("INVENTARIO");
        JButton btnCorte     = botonModulo("CORTE");
        JButton btnUsuario   = botonModulo("USUARIO");

        btnVentas.addActionListener(e -> {
            new VENTAS(rol, nombreUsuario).setVisible(true);
            dispose();
        });

        if ("ADMIN".equalsIgnoreCase(rol)) {
            btnProductos.addActionListener(e -> {
                new PRODUCTOS(rol, nombreUsuario).setVisible(true);
                dispose();
            });
            btnCorte.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte",
                    JOptionPane.INFORMATION_MESSAGE));
            btnUsuario.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Usuario",
                    JOptionPane.INFORMATION_MESSAGE));
        } else {
            btnProductos.setVisible(false);
            btnCorte.setVisible(false);
            btnUsuario.setVisible(false);
        }

        barraModulos.add(btnVentas);
        barraModulos.add(btnProductos);
        barraModulos.add(btnInventario);
        barraModulos.add(btnCorte);
        barraModulos.add(btnUsuario);

        // ── Franja naranja "Ventas" + subtabs Ticket / Historial ──────────
        JPanel barraNaranja = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        barraNaranja.setBackground(NARANJA);
        JLabel lblVentas = new JLabel("Ventas");
        lblVentas.setFont(new Font("Arial", Font.BOLD, 13));
        barraNaranja.add(lblVentas);

        JPanel barraSubtabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        barraSubtabs.setBackground(GRIS);
        barraSubtabs.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JButton btnTicket = new JButton("Ticket");
        btnTicket.setBackground(GRIS);
        btnTicket.setFont(new Font("Arial", Font.PLAIN, 12));
        btnTicket.setFocusPainted(false);
        btnTicket.addActionListener(e -> {
            new VENTAS(rol, nombreUsuario).setVisible(true);
            dispose();
        });

        // Historial es la pestaña activa → resaltado
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

        // ── Layout principal: split izquierda/derecha ─────────────────────
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

    // ── Panel izquierdo: búsqueda + tabla de folios ────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(GRIS);
        p.setBorder(new EmptyBorder(10, 10, 10, 6));

        JLabel titulo = new JLabel("Búsqueda por folio");
        titulo.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(GRIS);
        txtBusqueda = new JTextField();
        txtBusqueda.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filtrarPorFolio(txtBusqueda.getText().trim());
            }
        });
        panelBusqueda.add(new JLabel("🔍"), BorderLayout.WEST);
        panelBusqueda.add(txtBusqueda,      BorderLayout.CENTER);

        // Selector de fecha
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

        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelFecha.setBackground(GRIS);
        panelFecha.add(new JLabel("Del día:"));
        panelFecha.add(lblFechaValor);
        panelFecha.add(btnFlecha);
        panelFecha.add(btnHoy);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(GRIS);
        norte.add(titulo,        BorderLayout.NORTH);
        norte.add(panelBusqueda, BorderLayout.CENTER);
        norte.add(panelFecha,    BorderLayout.SOUTH);

        // Tabla de folios
        String[] cols = {"Folio", "Arts", "Hora", "Total"};
        modeloFolios = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaFolios = new JTable(modeloFolios);
        estilizarTabla(tablaFolios);
        tablaFolios.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaFolios.getColumnModel().getColumn(1).setPreferredWidth(40);
        tablaFolios.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaFolios.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Al seleccionar un folio, carga su detalle en el panel derecho
        tablaFolios.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && tablaFolios.getSelectedRow() >= 0) {
                Object val = modeloFolios.getValueAt(tablaFolios.getSelectedRow(), 0);
                cargarDetalleTicket(Integer.parseInt(val.toString()));
            }
        });

        JScrollPane scroll = new JScrollPane(tablaFolios);
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        p.add(norte,  BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Panel derecho: encabezado del ticket + tabla de renglones ─────────
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

        lblFolioValor  = new JLabel("—");
        lblCajeroValor = new JLabel("—");
        lblTotalValor  = new JLabel("—");
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblFechaTicket = new JLabel("—");
        lblFechaTicket.setFont(new Font("Arial", Font.BOLD, 12));

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; g.anchor = GridBagConstraints.CENTER;
        encabezado.add(tTicket, g);

        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 1; encabezado.add(new JLabel("Folio:"),  g);
        g.gridx = 1;               encabezado.add(lblFolioValor,         g);
        g.gridx = 2;               encabezado.add(new JLabel("Total:"),  g);
        g.gridx = 3;               encabezado.add(lblTotalValor,         g);
        g.gridx = 0; g.gridy = 2;  encabezado.add(new JLabel("Cajero:"), g);
        g.gridx = 1; g.gridwidth = 3; encabezado.add(lblCajeroValor,     g);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 4; g.anchor = GridBagConstraints.CENTER;
        encabezado.add(lblFechaTicket, g);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(Color.WHITE);
        norte.add(encabezado,        BorderLayout.CENTER);
        norte.add(new JSeparator(), BorderLayout.SOUTH);

        String[] colsDet = {"Código", "Descripción", "Precio", "Cant.", "Importe", "%"};
        modeloDetalle = new DefaultTableModel(colsDet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        p.add(norte,  BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── LÓGICA DE DATOS ─────────────────────────────────────────────────────

    /**
     * GET /tickets/fecha?fecha=YYYY-MM-DD
     * Solo muestra tickets de tipo "Ticket" y estado "Pagado".
     * FIX: el estado correcto es "Pagado", no "Abierto" ni "Activo".
     * Los tickets en proceso tienen estado "Activo" hasta que PagoController
     * los cambia a "Pagado" al registrar el pago.
     */
    private void cargarHistorialDelDia() {
        modeloFolios.setRowCount(0);
        limpiarDetalle();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String json  = api.get("/tickets/fecha?fecha=" + fechaActual);
                JsonNode arr = mapper.readTree(json);

                SwingUtilities.invokeLater(() -> {
                    modeloFolios.setRowCount(0);
                    for (JsonNode t : arr) {
                        String tipo   = t.path("tipoDocumento").asText();
                        String estado = t.path("estadoDocumento").asText();

                        // Solo ventas completadas
                        if (!"Ticket".equals(tipo) || !"Pagado".equals(estado)) continue;

                        int    folio = t.path("folioTicket").asInt();
                        String hora  = t.path("horaTransaccion").asText();
                        double total = t.path("totalNeto").asDouble(0);

                        modeloFolios.addRow(new Object[]{
                            folio,
                            0,    // artículos: se rellena al seleccionar el folio
                            hora.isBlank() ? "—" : formatearHora(hora),
                            "$" + String.format("%.2f", total)
                        });
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                try { get(); }
                catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error al cargar historial", ex);
                    mostrarError("Error al cargar historial", ex);
                }
            }
        }.execute();
    }

    /**
     * GET /tickets/{folio}  →  cabecera (usuario, totales, fecha)
     * GET /tickets/{folio}/detalle  →  renglones con producto.descripcion
     *
     * producto.descripcion funciona gracias al JOIN FETCH en
     * DetalleTicketRepository.findByFolioTicket().
     * usuario.nombreUsuario funciona gracias a @JsonIgnoreProperties en
     * Ticket.java y @JsonProperty(WRITE_ONLY) en Usuario.java.
     */
    private void cargarDetalleTicket(int folio) {
        modeloDetalle.setRowCount(0);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                JsonNode obj      = mapper.readTree(api.get("/tickets/" + folio));
                JsonNode detalles = mapper.readTree(api.get("/tickets/" + folio + "/detalle"));

                SwingUtilities.invokeLater(() -> {
                    // Cabecera
                    lblFolioValor.setText(String.valueOf(obj.path("folioTicket").asInt()));
                    lblCajeroValor.setText(
                        textOrDefault(obj.path("usuario"), "nombreUsuario", "—"));
                    lblTotalValor.setText(
                        "$" + String.format("%.2f", obj.path("totalNeto").asDouble()));

                    String fechaStr = textOrDefault(obj, "fechaTransaccion",
                        LocalDate.now().toString());
                    try {
                        lblFechaTicket.setText(
                            formatearFechaLarga(LocalDate.parse(fechaStr)));
                    } catch (Exception ex) {
                        lblFechaTicket.setText(fechaStr);
                    }

                    // Renglones
                    // FIX: BigDecimal.valueOf(asDouble) pierde precisión en granel.
                    // Se usa new BigDecimal(asText) para respetar el valor exacto
                    // que envía el backend (precision=10, scale=3).
                    int totalArts = 0;
                    for (JsonNode d : detalles) {
                        String cod  = d.path("codigoBarras").asText();

                        // descripcion viene de producto gracias al JOIN FETCH
                        String desc = d.path("producto").path("descripcion").asText();
                        if (desc.isBlank()) desc = cod;

                        String cantStr = textOrDefault(d, "cantidad", "0");
                        BigDecimal cant;
                        try { cant = new BigDecimal(cantStr); }
                        catch (NumberFormatException e) { cant = BigDecimal.ZERO; }

                        totalArts += cant.intValue();

                        modeloDetalle.addRow(new Object[]{
                            cod,
                            desc,
                            "$" + String.format("%.2f", d.path("precioUnitarioVenta").asDouble()),
                            cant.stripTrailingZeros().toPlainString(),
                            "$" + String.format("%.2f", d.path("importe").asDouble()),
                            String.format("%.0f%%", d.path("descuentoProducto").asDouble())
                        });
                    }

                    // Actualizar columna Arts en la tabla izquierda
                    final int arts = totalArts;
                    for (int i = 0; i < modeloFolios.getRowCount(); i++) {
                        if (Integer.valueOf(folio).equals(modeloFolios.getValueAt(i, 0))) {
                            modeloFolios.setValueAt(arts, i, 1);
                            break;
                        }
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                try { get(); }
                catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error al cargar detalle", ex);
                    mostrarError("Error al cargar ticket " + folio, ex);
                }
            }
        }.execute();
    }

    // ─── UTILIDADES ──────────────────────────────────────────────────────────

    /** Filtra la tabla de folios por número sin hacer una nueva petición HTTP. */
    private void filtrarPorFolio(String texto) {
        if (texto.isEmpty()) {
            tablaFolios.setRowSorter(null);
            return;
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloFolios);
        tablaFolios.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
    }

    private void limpiarDetalle() {
        if (lblFolioValor  != null) lblFolioValor.setText("—");
        if (lblCajeroValor != null) lblCajeroValor.setText("—");
        if (lblTotalValor  != null) lblTotalValor.setText("—");
        if (lblFechaTicket != null) lblFechaTicket.setText("—");
        if (modeloDetalle  != null) modeloDetalle.setRowCount(0);
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
        b.setFont(new Font("Arial", Font.PLAIN, 14));
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

    /**
     * Lee un campo de texto de un JsonNode devolviendo un default si
     * el nodo es null, está ausente o es JSON null.
     */
    private String textOrDefault(JsonNode node, String campo, String defaultValue) {
        if (node == null || node.isMissingNode() || node.isNull()) return defaultValue;
        JsonNode v = node.path(campo);
        if (v.isMissingNode() || v.isNull()) return defaultValue;
        String s = v.asText();
        return s.isBlank() ? defaultValue : s;
    }
}