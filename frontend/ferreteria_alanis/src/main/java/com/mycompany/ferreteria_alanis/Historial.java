package com.mycompany.ferreteria_alanis;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel de Historial de Ventas
 * Muestra los tickets del día y el detalle de cada uno al seleccionarlo.
 * Se integra como JPanel dentro del MenuPrincipal existente.
 */
public class Historial extends JPanel {

    // ── Colores de la app ──────────────────────────────────
    private static final Color NARANJA      = new Color(0xFF, 0x99, 0x33);
    private static final Color NARANJA_CLARO= new Color(0xFF, 0xCC, 0x66);
    private static final Color GRIS_FONDO   = new Color(0xD9, 0xD9, 0xD9);
    private static final Color BLANCO       = Color.WHITE;
    private static final Color NEGRO        = Color.BLACK;

    // ── Panel izquierdo ────────────────────────────────────
    private JTextField campoBusqueda;
    private JLabel     lblFecha;
    private JTable     tablaFolios;
    private DefaultTableModel modeloFolios;

    // ── Panel derecho ──────────────────────────────────────
    private JPanel     panelDetalle;
    private JLabel     lblTitulo;
    private JLabel     lblFolioValor;
    private JLabel     lblCajeroValor;
    private JLabel     lblTotalValor;
    private JLabel     lblFechaValor;
    private JTable     tablaProductos;
    private DefaultTableModel modeloProductos;

    // ── Fecha seleccionada (por defecto hoy) ───────────────
    private Date fechaSeleccionada = new Date();

    // ──────────────────────────────────────────────────────
    public Historial() {
        setLayout(new BorderLayout());
        setBackground(GRIS_FONDO);
        construirUI();
        cargarFoliosDelDia();
    }

    // ══════════════════════════════════════════════════════
    // CONSTRUCCIÓN DE LA INTERFAZ
    // ══════════════════════════════════════════════════════
    private void construirUI() {

        // ── Barra naranja superior "Ventas" ────────────────
        JPanel barraVentas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barraVentas.setBackground(NARANJA);
        JLabel lblVentas = new JLabel("Ventas");
        lblVentas.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblVentas.setForeground(NEGRO);
        barraVentas.add(lblVentas);
        add(barraVentas, BorderLayout.NORTH);

        // ── Contenedor principal dividido izq / der ────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelIzquierdo(), crearPanelDerecho());
        split.setDividerLocation(430);
        split.setDividerSize(4);
        split.setBackground(GRIS_FONDO);
        add(split, BorderLayout.CENTER);
    }

    // ── Panel izquierdo ────────────────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(GRIS_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 6));

        // Título
        JLabel titulo = new JLabel("Búsqueda por folio");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(titulo, BorderLayout.NORTH);

        // Centro: buscador + fecha + tabla
        JPanel centro = new JPanel(new BorderLayout(4, 6));
        centro.setBackground(GRIS_FONDO);

        // Buscador con ícono lupa
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        panelBusqueda.setBackground(GRIS_FONDO);
        JLabel lupa = new JLabel("🔍");
        lupa.setFont(new Font("SansSerif", Font.PLAIN, 16));
        campoBusqueda = new JTextField();
        campoBusqueda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoBusqueda.setBorder(BorderFactory.createLineBorder(NEGRO));
        campoBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filtrarPorFolio(campoBusqueda.getText().trim());
            }
        });
        panelBusqueda.add(lupa, BorderLayout.WEST);
        panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);

        // Selector de fecha
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelFecha.setBackground(GRIS_FONDO);
        JLabel lblDel = new JLabel("Del día:");
        lblDel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFecha = new JLabel(formatearFecha(fechaSeleccionada));
        lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFecha.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblFecha.setPreferredSize(new Dimension(180, 22));

        // Flecha para cambiar fecha (simplificado: abre diálogo de fecha)
        JButton btnFecha = new JButton("▼");
        estilizarBotonPequeno(btnFecha);
        btnFecha.addActionListener(e -> mostrarSelectorFecha());

        JButton btnHoy = new JButton("Hoy");
        estilizarBotonPequeno(btnHoy);
        btnHoy.addActionListener(e -> {
            fechaSeleccionada = new Date();
            lblFecha.setText(formatearFecha(fechaSeleccionada));
            cargarFoliosDelDia();
        });

        panelFecha.add(lblDel);
        panelFecha.add(lblFecha);
        panelFecha.add(btnFecha);
        panelFecha.add(btnHoy);

        JPanel panelArriba = new JPanel(new BorderLayout(0, 4));
        panelArriba.setBackground(GRIS_FONDO);
        panelArriba.add(panelBusqueda, BorderLayout.NORTH);
        panelArriba.add(panelFecha,    BorderLayout.CENTER);

        centro.add(panelArriba, BorderLayout.NORTH);

        // Tabla de folios
        String[] columnas = {"Folio", "Arts", "Hora", "Total"};
        modeloFolios = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaFolios = new JTable(modeloFolios);
        tablaFolios.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaFolios.setRowHeight(24);
        tablaFolios.setBackground(GRIS_FONDO);
        tablaFolios.setSelectionBackground(NARANJA_CLARO);
        tablaFolios.setSelectionForeground(NEGRO);
        tablaFolios.setGridColor(Color.LIGHT_GRAY);
        tablaFolios.getTableHeader().setBackground(GRIS_FONDO);
        tablaFolios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Anchos de columnas
        tablaFolios.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaFolios.getColumnModel().getColumn(1).setPreferredWidth(50);
        tablaFolios.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaFolios.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Clic en fila → cargar detalle
        tablaFolios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaFolios.getSelectedRow() >= 0) {
                int fila = tablaFolios.getSelectedRow();
                int folio = Integer.parseInt(modeloFolios.getValueAt(fila, 0).toString());
                cargarDetalleTicket(folio);
            }
        });

        JScrollPane scrollFolios = new JScrollPane(tablaFolios);
        scrollFolios.setBorder(BorderFactory.createLineBorder(NEGRO));
        centro.add(scrollFolios, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // ── Panel derecho ──────────────────────────────────────
    private JPanel crearPanelDerecho() {
        panelDetalle = new JPanel(new BorderLayout(0, 8));
        panelDetalle.setBackground(BLANCO);
        panelDetalle.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Encabezado del ticket
        JPanel encabezado = new JPanel(new GridBagLayout());
        encabezado.setBackground(BLANCO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.anchor = GridBagConstraints.WEST;

        lblTitulo = new JLabel("Ticket");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblFolioValor  = new JLabel("—");
        lblCajeroValor = new JLabel("—");
        lblTotalValor  = new JLabel("—");
        lblTotalValor.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFechaValor  = new JLabel("—");
        lblFechaValor.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblFechaValor.setHorizontalAlignment(SwingConstants.CENTER);

        // Fila 0: título centrado
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        encabezado.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Folio + Total
        gbc.gridx = 0; gbc.gridy = 1;
        encabezado.add(new JLabel("Folio:"), gbc);
        gbc.gridx = 1;
        encabezado.add(lblFolioValor, gbc);
        gbc.gridx = 2;
        encabezado.add(new JLabel("Total:"), gbc);
        gbc.gridx = 3;
        encabezado.add(lblTotalValor, gbc);

        // Fila 2: Cajero
        gbc.gridx = 0; gbc.gridy = 2;
        encabezado.add(new JLabel("Cajero:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        encabezado.add(lblCajeroValor, gbc);

        // Fila 3: fecha centrada
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        encabezado.add(lblFechaValor, gbc);

        // Separador
        JSeparator sep = new JSeparator();
        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(BLANCO);
        norte.add(encabezado, BorderLayout.CENTER);
        norte.add(sep, BorderLayout.SOUTH);

        panelDetalle.add(norte, BorderLayout.NORTH);

        // Tabla de productos del ticket
        String[] cols = {"Código", "Descripción", "Precio", "Cant.", "Importe", "%"};
        modeloProductos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaProductos.setRowHeight(24);
        tablaProductos.setBackground(GRIS_FONDO);
        tablaProductos.setSelectionBackground(NARANJA_CLARO);
        tablaProductos.setGridColor(Color.LIGHT_GRAY);
        tablaProductos.getTableHeader().setBackground(GRIS_FONDO);
        tablaProductos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Anchos
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(65);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(40);

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        scrollProductos.setBorder(BorderFactory.createLineBorder(NEGRO));
        panelDetalle.add(scrollProductos, BorderLayout.CENTER);

        return panelDetalle;
    }

    // ══════════════════════════════════════════════════════
    // LÓGICA DE DATOS
    // ══════════════════════════════════════════════════════

    /**
     * Carga en la tabla izquierda los tickets PAGADOS del día seleccionado.
     */
    private void cargarFoliosDelDia() {
        modeloFolios.setRowCount(0);
        limpiarDetalle();

        String sql = """
            SELECT
                T.folio_ticket,
                COUNT(DT.codigo_barras)                     AS articulos,
                TIME_FORMAT(T.hora_transaccion, '%h:%i %p') AS hora,
                CONCAT('$', FORMAT(T.total_neto, 2))        AS total
            FROM TICKET T
            INNER JOIN DETALLE_TICKET DT ON T.folio_ticket = DT.folio_ticket
            WHERE T.estado_documento = 'Pagado'
              AND T.fecha_transaccion = ?
            GROUP BY T.folio_ticket, T.hora_transaccion, T.total_neto
            ORDER BY T.hora_transaccion DESC
            """;

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(fechaSeleccionada.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloFolios.addRow(new Object[]{
                    rs.getInt("folio_ticket"),
                    rs.getInt("articulos"),
                    rs.getString("hora"),
                    rs.getString("total")
                });
            }
        } catch (SQLException ex) {
            mostrarError("Error al cargar historial: " + ex.getMessage());
        }
    }

    /**
     * Filtra la tabla izquierda por el texto del buscador (folio o parcial).
     */
    private void filtrarPorFolio(String texto) {
        if (texto.isEmpty()) {
            cargarFoliosDelDia();
            return;
        }
        modeloFolios.setRowCount(0);
        limpiarDetalle();

        String sql = """
            SELECT
                T.folio_ticket,
                COUNT(DT.codigo_barras)                     AS articulos,
                TIME_FORMAT(T.hora_transaccion, '%h:%i %p') AS hora,
                CONCAT('$', FORMAT(T.total_neto, 2))        AS total
            FROM TICKET T
            INNER JOIN DETALLE_TICKET DT ON T.folio_ticket = DT.folio_ticket
            WHERE T.estado_documento = 'Pagado'
              AND T.fecha_transaccion = ?
              AND CAST(T.folio_ticket AS CHAR) LIKE ?
            GROUP BY T.folio_ticket, T.hora_transaccion, T.total_neto
            ORDER BY T.hora_transaccion DESC
            """;

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(fechaSeleccionada.getTime()));
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloFolios.addRow(new Object[]{
                    rs.getInt("folio_ticket"),
                    rs.getInt("articulos"),
                    rs.getString("hora"),
                    rs.getString("total")
                });
            }
        } catch (SQLException ex) {
            mostrarError("Error al buscar: " + ex.getMessage());
        }
    }

    /**
     * Carga en el panel derecho el detalle del ticket seleccionado.
     */
    private void cargarDetalleTicket(int folio) {
        modeloProductos.setRowCount(0);

        // Consulta encabezado
        String sqlEncabezado = """
            SELECT T.folio_ticket, U.nombre_usuario, T.total_neto,
                   T.fecha_transaccion, T.porcentaje_descuento
            FROM TICKET T
            INNER JOIN USUARIO U ON T.id_usuario = U.id_usuario
            WHERE T.folio_ticket = ?
            """;

        // Consulta productos
        String sqlProductos = """
            SELECT DT.codigo_barras, P.descripcion,
                   DT.precio_unitario_venta, DT.cantidad,
                   DT.importe, DT.descuento_producto
            FROM DETALLE_TICKET DT
            INNER JOIN PRODUCTO P ON DT.codigo_barras = P.codigo_barras
            WHERE DT.folio_ticket = ?
            ORDER BY P.descripcion
            """;

        try (Connection conn = ConexionDB.obtenerConexion()) {

            // Encabezado
            try (PreparedStatement ps = conn.prepareStatement(sqlEncabezado)) {
                ps.setInt(1, folio);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    lblTitulo.setText("Ticket");
                    lblFolioValor.setText(String.valueOf(rs.getInt("folio_ticket")));
                    lblCajeroValor.setText(rs.getString("nombre_usuario"));
                    lblTotalValor.setText(String.format("$%.2f", rs.getDouble("total_neto")));
                    lblFechaValor.setText(formatearFechaLarga(rs.getDate("fecha_transaccion")));
                }
            }

            // Productos
            try (PreparedStatement ps = conn.prepareStatement(sqlProductos)) {
                ps.setInt(1, folio);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    modeloProductos.addRow(new Object[]{
                        rs.getString("codigo_barras"),
                        rs.getString("descripcion"),
                        String.format("$%.2f", rs.getDouble("precio_unitario_venta")),
                        rs.getString("cantidad"),
                        String.format("$%.2f", rs.getDouble("importe")),
                        String.format("%.0f%%", rs.getDouble("descuento_producto"))
                    });
                }
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar detalle: " + ex.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════════════════

    private void limpiarDetalle() {
        lblTitulo.setText("Ticket");
        lblFolioValor.setText("—");
        lblCajeroValor.setText("—");
        lblTotalValor.setText("—");
        lblFechaValor.setText("—");
        modeloProductos.setRowCount(0);
    }

    private void estilizarBotonPequeno(JButton btn) {
        btn.setBackground(GRIS_FONDO);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Muestra un selector de fecha simple usando JOptionPane con un spinner.
     */
    private void mostrarSelectorFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(fechaSeleccionada, null, new Date(), java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));

        int r = JOptionPane.showConfirmDialog(this, spinner, "Seleccionar fecha",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            fechaSeleccionada = (Date) spinner.getValue();
            lblFecha.setText(formatearFecha(fechaSeleccionada));
            cargarFoliosDelDia();
        }
    }

    private String formatearFecha(Date fecha) {
        return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
    }

    private String formatearFechaLarga(java.sql.Date fecha) {
        String[] dias   = {"domingo","lunes","martes","miércoles","jueves","viernes","sábado"};
        String[] meses  = {"enero","febrero","marzo","abril","mayo","junio",
                           "julio","agosto","septiembre","octubre","noviembre","diciembre"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fecha);
        String dia  = dias[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
        int    num  = cal.get(java.util.Calendar.DAY_OF_MONTH);
        String mes  = meses[cal.get(java.util.Calendar.MONTH)];
        int    anio = cal.get(java.util.Calendar.YEAR);
        // Capitalizar primera letra
        dia = dia.substring(0,1).toUpperCase() + dia.substring(1);
        return dia + " , " + num + " de " + mes + " de " + anio;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
