package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Módulo de Corte de Caja.
 *
 * Muestra el resumen de ventas del día seleccionado.
 * Por cada ticket Pagado de tipo "Ticket" carga también su Pago para desglosar
 * el método de cobro (efectivo / tarjeta).
 *
 * Endpoints usados:
 *   GET /tickets/fecha?fecha=YYYY-MM-DD  → lista de tickets del día
 *   GET /tickets/{folio}/pago            → detalle de pago de cada ticket
 *
 * Columnas de la tabla:
 *   Folio | Venta (totalBruto) | Efectivo | Descuento | Neta (totalNeto) | Tarjeta
 */
public class CORTE extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(CORTE.class.getName());

    private static final Color NARANJA      = new Color(255, 153, 0);
    private static final Color NARANJA_SUAVE = new Color(255, 204, 102);
    private static final Color NEGRO        = Color.BLACK;
    private static final Color GRIS_NAV     = new Color(50, 50, 50);
    private static final Color GRIS_FONDO   = new Color(217, 217, 217);
    private static final Color FILA_PAR     = new Color(255, 255, 200);
    private static final Color FILA_IMPAR   = Color.WHITE;
    private static final Color RESUMEN_BG   = new Color(200, 200, 200);

    private static final String[] COLUMNAS = {
        "Folio", "Venta", "Efectivo", "Descuento", "Neta", "Tarjeta"
    };
    private static final int COL_FOLIO     = 0;
    private static final int COL_VENTA     = 1;
    private static final int COL_EFECTIVO  = 2;
    private static final int COL_DESCUENTO = 3;
    private static final int COL_NETA      = 4;
    private static final int COL_TARJETA   = 5;

    private final String rol;
    private final String nombreUsuario;
    private final ApiClient    api    = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    private LocalDate fechaActual = LocalDate.now();
    private JLabel    lblFechaValor;

    // Modelo de la tabla principal (tickets individuales)
    private DefaultTableModel modeloTickets;
    private JTable            tablaTickets;

    // Etiquetas del resumen (fila inferior)
    private JLabel lblSumVenta;
    private JLabel lblSumEfectivo;
    private JLabel lblSumDescuento;
    private JLabel lblSumNeta;
    private JLabel lblSumTarjeta;
    private JLabel lblEfectivoNeto;   // Efectivo – abs(Descuento)

    // ─────────────────────────────────────────────────────────────────────────

    public CORTE(String rol, String nombreUsuario) {
        this.rol           = rol;
        this.nombreUsuario = nombreUsuario;
        setTitle("Ferretería Alanís – Corte");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1050, 620);
        setLocationRelativeTo(null);
        initUI();
        cargarCorte();
    }

    // ─── Construcción de la interfaz ─────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),    BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(NEGRO);

        // ── Header (logo + nombre empresa + botón usuario) ─────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NEGRO);
        header.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setOpaque(false);
        ImageIcon icon = new ImageIcon("logo.png");
        Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        izq.add(new JLabel(new ImageIcon(img)));

        JLabel lblNombre = new JLabel(
            "<html>Ferretería e Instalaciones<br>Eléctricas Alanís</html>");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 12));
        izq.add(lblNombre);
        header.add(izq, BorderLayout.WEST);

        JButton btnUsuarioInfo = new JButton("Usuario: " + nombreUsuario);
        btnUsuarioInfo.setBackground(NARANJA);
        btnUsuarioInfo.setFont(new Font("Arial", Font.BOLD, 12));
        btnUsuarioInfo.setFocusPainted(false);
        btnUsuarioInfo.setBorderPainted(false);
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));
        header.add(btnUsuarioInfo, BorderLayout.EAST);
        nav.add(header, BorderLayout.NORTH);

        // ── Barra de módulos ───────────────────────────────────────────────
        JPanel modulos = new JPanel(new BorderLayout());
        modulos.setBackground(GRIS_NAV);

        JPanel izqMod = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        izqMod.setOpaque(false);

        JButton btnVentas = btnNav("Ventas", false);
        btnVentas.addActionListener(e -> { new VENTAS(rol, nombreUsuario).setVisible(true); dispose(); });
        izqMod.add(btnVentas);

        if ("ADMIN".equalsIgnoreCase(rol)) {
            JButton btnProductos = btnNav("Productos", false);
            btnProductos.addActionListener(e -> { new PRODUCTOS(rol, nombreUsuario).setVisible(true); dispose(); });
            izqMod.add(btnProductos);
        }

        JButton btnInventario = btnNav("Inventario", false);
        btnInventario.addActionListener(e -> {
            new INVENTARIO(rol, nombreUsuario).setVisible(true); dispose();
        });
        izqMod.add(btnInventario);
        modulos.add(izqMod, BorderLayout.WEST);

        if ("ADMIN".equalsIgnoreCase(rol)) {
            JPanel derMod = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
            derMod.setOpaque(false);

            JButton btnCorte = btnNav("Corte", true);   // activo
            derMod.add(btnCorte);

            JButton btnUsuario = btnNav("Usuario", false);
            btnUsuario.addActionListener(e -> { new USUARIO(rol, nombreUsuario).setVisible(true); dispose(); });
            derMod.add(btnUsuario);

            modulos.add(derMod, BorderLayout.EAST);
        }

        nav.add(modulos, BorderLayout.SOUTH);
        return nav;
    }

    private JButton btnNav(String texto, boolean activo) {
        JButton b = new JButton(texto);
        b.setBackground(activo ? NARANJA : new Color(80, 80, 80));
        b.setForeground(activo ? NEGRO : Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(110, 30));
        return b;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(GRIS_FONDO);

        // ── Franja naranja "Corte" ─────────────────────────────────────────
        JPanel franjaCorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        franjaCorte.setBackground(NARANJA);
        JLabel lblCorte = new JLabel("Corte");
        lblCorte.setFont(new Font("Arial", Font.BOLD, 16));
        franjaCorte.add(lblCorte);

        // ── Barra de fecha ─────────────────────────────────────────────────
        JPanel barraFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraFecha.setBackground(GRIS_FONDO);
        barraFecha.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JButton btnHoy = new JButton("Hoy");
        btnHoy.setFont(new Font("Arial", Font.PLAIN, 12));
        btnHoy.setFocusPainted(false);
        btnHoy.addActionListener(e -> {
            fechaActual = LocalDate.now();
            lblFechaValor.setText(formatearFecha(fechaActual));
            cargarCorte();
        });

        lblFechaValor = new JLabel(formatearFecha(fechaActual));
        lblFechaValor.setFont(new Font("Arial", Font.PLAIN, 13));
        lblFechaValor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        lblFechaValor.setPreferredSize(new Dimension(250, 26));

        JButton btnCambiarFecha = new JButton("▼");
        btnCambiarFecha.setPreferredSize(new Dimension(30, 26));
        btnCambiarFecha.setFocusPainted(false);
        btnCambiarFecha.setMargin(new Insets(0, 0, 0, 0));
        btnCambiarFecha.addActionListener(e -> mostrarSelectorFecha());

        barraFecha.add(btnHoy);
        barraFecha.add(lblFechaValor);
        barraFecha.add(btnCambiarFecha);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(franjaCorte, BorderLayout.NORTH);
        norte.add(barraFecha,  BorderLayout.SOUTH);

        // ── Área principal: tabla + logo ───────────────────────────────────
        JPanel areaMain = new JPanel(new BorderLayout(10, 0));
        areaMain.setBackground(GRIS_FONDO);
        areaMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaMain.add(crearPanelTabla(), BorderLayout.CENTER);
        areaMain.add(crearPanelLogo(),  BorderLayout.EAST);

        contenido.add(norte,    BorderLayout.NORTH);
        contenido.add(areaMain, BorderLayout.CENTER);
        return contenido;
    }

    // ── Tabla de tickets ──────────────────────────────────────────────────

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FONDO);

        // Modelo: no editable, sin tipos especiales (todo String para formateo manual)
        modeloTickets = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTickets = new JTable(modeloTickets);
        tablaTickets.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaTickets.setRowHeight(22);
        tablaTickets.setBackground(FILA_IMPAR);
        tablaTickets.setGridColor(new Color(200, 200, 200));
        tablaTickets.setSelectionBackground(NARANJA_SUAVE);
        tablaTickets.setSelectionForeground(NEGRO);
        tablaTickets.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaTickets.getTableHeader().setBackground(GRIS_FONDO);
        tablaTickets.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

        // Anchos de columna
        int[] anchos = {65, 90, 90, 90, 90, 90};
        for (int i = 0; i < anchos.length; i++) {
            tablaTickets.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        // Renderer: filas alternas + alineación derecha para dinero
        DefaultTableCellRenderer rendDerecha = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(col == COL_FOLIO
                    ? SwingConstants.CENTER : SwingConstants.RIGHT);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? FILA_PAR : FILA_IMPAR);
                    // Descuento en rojo si es negativo
                    if (col == COL_DESCUENTO) {
                        String s = val == null ? "" : val.toString();
                        c.setForeground(s.startsWith("-") ? Color.RED : NEGRO);
                    } else {
                        c.setForeground(NEGRO);
                    }
                }
                return c;
            }
        };
        for (int i = 0; i < COLUMNAS.length; i++) {
            tablaTickets.getColumnModel().getColumn(i).setCellRenderer(rendDerecha);
        }

        JScrollPane scroll = new JScrollPane(tablaTickets);
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        scroll.setBackground(GRIS_FONDO);

        // ── Fila de resumen ────────────────────────────────────────────────
        JPanel panelResumen = crearPanelResumen();

        p.add(scroll,        BorderLayout.CENTER);
        p.add(panelResumen,  BorderLayout.SOUTH);
        return p;
    }

    /** Dos filas de totales fijadas al fondo de la tabla. */
    private JPanel crearPanelResumen() {
        JPanel p = new JPanel(new GridLayout(2, COLUMNAS.length, 0, 0));
        p.setBackground(RESUMEN_BG);
        p.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.DARK_GRAY));

        Font fuenteNegrita = new Font("Arial", Font.BOLD, 12);

        lblSumVenta     = labelResumen("", fuenteNegrita);
        lblSumEfectivo  = labelResumen("", fuenteNegrita);
        lblSumDescuento = labelResumen("", fuenteNegrita);
        lblSumNeta      = labelResumen("", fuenteNegrita);
        lblSumTarjeta   = labelResumen("", fuenteNegrita);
        lblEfectivoNeto = labelResumen("", fuenteNegrita);

        // Fila 1: [Folio vacío] | Venta | Efectivo | Descuento | Neta | Tarjeta
        p.add(labelResumen("", fuenteNegrita));   // folio — vacío
        p.add(lblSumVenta);
        p.add(lblSumEfectivo);
        p.add(lblSumDescuento);
        p.add(lblSumNeta);
        p.add(lblSumTarjeta);

        // Fila 2: [vacío] | [vacío] | EfectivoNeto | [vacío] | [vacío] | [vacío]
        p.add(labelResumen("", fuenteNegrita));
        p.add(labelResumen("", fuenteNegrita));
        p.add(lblEfectivoNeto);
        p.add(labelResumen("", fuenteNegrita));
        p.add(labelResumen("", fuenteNegrita));
        p.add(labelResumen("", fuenteNegrita));

        return p;
    }

    private JLabel labelResumen(String texto, Font f) {
        JLabel lbl = new JLabel(texto, SwingConstants.RIGHT);
        lbl.setFont(f);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 4, 3, 6)));
        lbl.setOpaque(true);
        lbl.setBackground(RESUMEN_BG);
        return lbl;
    }

    // ── Panel del logo (derecha) ──────────────────────────────────────────

    private JPanel crearPanelLogo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FONDO);
        p.setPreferredSize(new Dimension(320, 0));

        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icono = new ImageIcon("logo.png");
            Image img = icono.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            lblLogo.setText("Logo");
            lblLogo.setFont(new Font("Arial", Font.BOLD, 18));
        }

        p.add(lblLogo, BorderLayout.CENTER);
        return p;
    }

    // ─── Carga de datos ───────────────────────────────────────────────────

    private void cargarCorte() {
        modeloTickets.setRowCount(0);
        limpiarResumen();

        new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground() throws Exception {
                // 1 — Obtener tickets Pagados del día
                String jsonTickets = api.get("/tickets/fecha?fecha=" + fechaActual);
                JsonNode arrTickets = mapper.readTree(jsonTickets);

                List<Object[]> filas = new ArrayList<>();

                for (JsonNode t : arrTickets) {
                    String tipo   = t.path("tipoDocumento").asText();
                    String estado = t.path("estadoDocumento").asText();
                    if (!"Ticket".equals(tipo) || !"Pagado".equals(estado)) continue;

                    int    folio    = t.path("folioTicket").asInt();

                    // 2a — Calcular Venta (bruto) y Neta desde el detalle del ticket
                    // Venta  = Σ (precioUnitarioVenta × cantidad)
                    // Neta   = Σ importe  (ya con descuento aplicado)
                    // Descuento = Neta - Venta  (negativo si hubo descuento)
                    BigDecimal bruto = BigDecimal.ZERO;
                    BigDecimal neta  = BigDecimal.ZERO;
                    try {
                        String jsonDet = api.get("/tickets/" + folio + "/detalle");
                        JsonNode detalles = mapper.readTree(jsonDet);
                        for (JsonNode d : detalles) {
                            BigDecimal precio = bdOf(d, "precioUnitarioVenta");
                            BigDecimal cant   = bdOf(d, "cantidad");
                            BigDecimal imp    = bdOf(d, "importe");
                            bruto = bruto.add(precio.multiply(cant)
                                .setScale(2, java.math.RoundingMode.HALF_UP));
                            neta  = neta.add(imp);
                        }
                    } catch (Exception ex) {
                        // Si no hay detalle disponible, caer de vuelta al API del ticket
                        bruto = bdOf(t, "totalBruto");
                        neta  = bdOf(t, "totalNeto");
                        LOGGER.log(Level.WARNING, "Sin detalle para folio " + folio + ", usando totalBruto/totalNeto", ex);
                    }
                    BigDecimal desc = neta.subtract(bruto);   // negativo si hubo descuento

                    // 2b — Obtener pago de cada ticket
                    BigDecimal efectivo = BigDecimal.ZERO;
                    BigDecimal tarjeta  = BigDecimal.ZERO;
                    try {
                        String jsonPago = api.get("/tickets/" + folio + "/pago");
                        JsonNode pago   = mapper.readTree(jsonPago);
                        efectivo = bdOf(pago, "montoEfectivo");
                        tarjeta  = bdOf(pago, "montoTarjeta");
                    } catch (Exception ex) {
                        // Si no hay pago registrado aún, se deja en 0
                        LOGGER.log(Level.WARNING, "Sin pago para folio " + folio, ex);
                    }

                    filas.add(new Object[]{
                        folio,
                        bruto,
                        efectivo,
                        desc,       // negativo si hubo descuento, 0 si no
                        neta,
                        tarjeta
                    });
                }
                return filas;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> filas = get();

                    BigDecimal sumVenta     = BigDecimal.ZERO;
                    BigDecimal sumEfectivo  = BigDecimal.ZERO;
                    BigDecimal sumDescuento = BigDecimal.ZERO;
                    BigDecimal sumNeta      = BigDecimal.ZERO;
                    BigDecimal sumTarjeta   = BigDecimal.ZERO;

                    for (Object[] fila : filas) {
                        int        folio    = (int)        fila[COL_FOLIO];
                        BigDecimal bruto    = (BigDecimal) fila[COL_VENTA];
                        BigDecimal efectivo = (BigDecimal) fila[COL_EFECTIVO];
                        BigDecimal desc     = (BigDecimal) fila[COL_DESCUENTO];
                        BigDecimal neta     = (BigDecimal) fila[COL_NETA];
                        BigDecimal tarjeta  = (BigDecimal) fila[COL_TARJETA];

                        sumVenta     = sumVenta.add(bruto);
                        sumEfectivo  = sumEfectivo.add(efectivo);
                        sumDescuento = sumDescuento.add(desc);
                        sumNeta      = sumNeta.add(neta);
                        sumTarjeta   = sumTarjeta.add(tarjeta);

                        modeloTickets.addRow(new Object[]{
                            folio,
                            fmt(bruto),
                            fmtSiPositivo(efectivo),
                            fmtSiNoNulo(desc),
                            fmtSiPositivo(neta),
                            fmtSiPositivo(tarjeta)
                        });
                    }

                    // Actualizar resumen
                    lblSumVenta.setText(fmt(sumVenta));
                    lblSumEfectivo.setText(fmtSiPositivo(sumEfectivo));
                    lblSumDescuento.setText(fmtSiNoNulo(sumDescuento));
                    lblSumNeta.setText(fmtSiPositivo(sumNeta));
                    lblSumTarjeta.setText(fmtSiPositivo(sumTarjeta));

                    // Efectivo neto = efectivo + descuento (desc ya es negativo)
                    BigDecimal efectivoNeto = sumEfectivo.add(sumDescuento);
                    lblEfectivoNeto.setText(fmt(efectivoNeto));

                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al cargar corte", ex);
                    JOptionPane.showMessageDialog(CORTE.this,
                        "Error al cargar el corte:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ─── Utilidades ───────────────────────────────────────────────────────

    private void limpiarResumen() {
        if (lblSumVenta     != null) lblSumVenta.setText("");
        if (lblSumEfectivo  != null) lblSumEfectivo.setText("");
        if (lblSumDescuento != null) lblSumDescuento.setText("");
        if (lblSumNeta      != null) lblSumNeta.setText("");
        if (lblSumTarjeta   != null) lblSumTarjeta.setText("");
        if (lblEfectivoNeto != null) lblEfectivoNeto.setText("");
    }

    private BigDecimal bdOf(JsonNode node, String campo) {
        JsonNode valueNode = node.path(campo);
        String s = valueNode.isMissingNode() || valueNode.isNull() ? "0" : valueNode.asText();
        try { return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    /** Siempre formatea como $X,XXX.XX */
    private String fmt(BigDecimal v) {
        if (v == null) return "$-";
        return "$" + String.format(Locale.US, "%,.2f", v.setScale(2, RoundingMode.HALF_UP));
    }

    /** Muestra el valor solo si es > 0; de lo contrario celda vacía. */
    private String fmtSiPositivo(BigDecimal v) {
        if (v == null || v.compareTo(BigDecimal.ZERO) <= 0) return "";
        return fmt(v);
    }

    /** Muestra el valor solo si es distinto de 0; de lo contrario celda vacía. */
    private String fmtSiNoNulo(BigDecimal v) {
        if (v == null || v.compareTo(BigDecimal.ZERO) == 0) return "";
        return fmt(v);
    }

    private String formatearFecha(LocalDate d) {
        try {
            return d.format(DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("es-MX")));
        } catch (Exception ex) {
            return d.toString();
        }
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
            lblFechaValor.setText(formatearFecha(fechaActual));
            cargarCorte();
        }
    }

    private void mostrarPopupSesion(JButton origen) {
        JDialog popup = new JDialog(this, false);
        popup.setUndecorated(true);
        popup.setLayout(new BorderLayout());

        JPanel cont = new JPanel(new GridLayout(0, 1, 0, 0));
        cont.setBackground(new Color(230, 230, 230));
        cont.setBorder(BorderFactory.createLineBorder(Color.GRAY));

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

        cont.add(titulo);
        cont.add(btnCerrar);
        cont.add(btnCancelar);
        popup.add(cont);
        popup.pack();

        Point p = origen.getLocationOnScreen();
        popup.setLocation(p.x, p.y + origen.getHeight());
        popup.setVisible(true);
    }
}