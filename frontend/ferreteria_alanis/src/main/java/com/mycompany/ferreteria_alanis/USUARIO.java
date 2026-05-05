package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Módulo de Usuario (solo visible para ADMIN).
 *
 * Muestra:
 *   - Tabla izquierda: lista de usuarios (nombreUsuario, contraseña enmascarada)
 *   - Formulario derecho: crear nuevo usuario con validación de contraseña
 *
 * Endpoints:
 *   GET  /usuarios          — listar todos
 *   POST /usuarios          — crear nuevo
 */
public class USUARIO extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(USUARIO.class.getName());

    private static final Color COLOR_NARANJA = new Color(255, 153, 0);
    private static final Color COLOR_NEGRO   = Color.BLACK;
    private static final Color COLOR_FONDO   = new Color(230, 230, 230);

    private final String rol;
    private final String nombreUsuario;
    private final ApiClient    api    = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    // ── Componentes ──────────────────────────────────────────────────────────
    private DefaultTableModel modeloUsuarios;
    private JTextField        txtNuevoUsuario;
    private JPasswordField    txtPassword;
    private JPasswordField    txtConfirmar;

    // ─────────────────────────────────────────────────────────────────────────

    public USUARIO(String rol, String nombreUsuario) {
        this.rol           = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís – Usuario");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        cargarUsuarios();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI principal
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(),    BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    // ── Barra de navegación ──────────────────────────────────────────────────

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
        btnUsuarioInfo.addActionListener(e -> mostrarPopupSesion(btnUsuarioInfo));
        header.add(btnUsuarioInfo, BorderLayout.EAST);
        nav.add(header, BorderLayout.NORTH);

        // Fila de módulos
        JPanel modulos = new JPanel(new BorderLayout());
        modulos.setBackground(new Color(50, 50, 50));

        JPanel izqMod = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        izqMod.setOpaque(false);

        JButton btnVentas = crearBtnNav("Ventas", false);
        btnVentas.addActionListener(e -> { new VENTAS(rol, nombreUsuario).setVisible(true); dispose(); });
        izqMod.add(btnVentas);

        JButton btnProductos = crearBtnNav("Productos", false);
        btnProductos.addActionListener(e -> { new PRODUCTOS(rol, nombreUsuario).setVisible(true); dispose(); });
        btnProductos.setVisible("ADMIN".equalsIgnoreCase(rol));
        izqMod.add(btnProductos);

        JButton btnInventario = crearBtnNav("Inventario", false);
        btnInventario.addActionListener(e -> { new INVENTARIO(rol, nombreUsuario).setVisible(true); dispose(); });
        izqMod.add(btnInventario);

        modulos.add(izqMod, BorderLayout.WEST);

        JPanel derMod = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        derMod.setOpaque(false);

        JButton btnCorte = crearBtnNav("Corte", false);
        btnCorte.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte",
                JOptionPane.INFORMATION_MESSAGE));

        JButton btnUsuarioMod = crearBtnNav("Usuario", true);

        derMod.add(btnCorte);
        derMod.add(btnUsuarioMod);
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
        JLabel lblTitulo = new JLabel("  Nuevo usuario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 30));

        // Panel dividido: tabla izquierda | formulario derecho
        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 15, 0));
        cuerpo.setBackground(COLOR_FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cuerpo.add(crearPanelTabla());
        cuerpo.add(crearPanelFormulario());

        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(cuerpo,    BorderLayout.CENTER);
        return contenido;
    }

    /** Tabla izquierda con la lista de usuarios */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        modeloUsuarios = new DefaultTableModel(new String[]{"Usuario", "Contraseña"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloUsuarios);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.setBackground(COLOR_FONDO);
        tabla.setSelectionBackground(new Color(255, 204, 102));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.getTableHeader().setBackground(COLOR_FONDO);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    /** Formulario derecho para crear un nuevo usuario */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(8, 10, 4, 10);
        g.anchor  = GridBagConstraints.NORTHWEST;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int fila = 0;

        // Título "Nuevo Usuario"
        g.gridx = 0; g.gridy = fila++;
        JLabel lblTitulo = new JLabel("Nuevo Usuario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitulo, g);

        // Usuario
        g.gridy = fila++;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblUsuario, g);

        g.gridy = fila++;
        txtNuevoUsuario = new JTextField();
        txtNuevoUsuario.setPreferredSize(new Dimension(0, 32));
        txtNuevoUsuario.setBackground(Color.WHITE);
        panel.add(txtNuevoUsuario, g);

        // Contraseña
        g.gridy = fila++;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblPass, g);

        g.gridy = fila++;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 32));
        txtPassword.setBackground(Color.WHITE);
        panel.add(txtPassword, g);

        // Confirmar contraseña
        g.gridy = fila++;
        JLabel lblConfirmar = new JLabel("Confirmar contraseña:");
        lblConfirmar.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblConfirmar, g);

        g.gridy = fila++;
        txtConfirmar = new JPasswordField();
        txtConfirmar.setPreferredSize(new Dimension(0, 32));
        txtConfirmar.setBackground(Color.WHITE);
        panel.add(txtConfirmar, g);

        // Espaciador
        g.gridy = fila++;
        g.weighty = 1;
        panel.add(Box.createVerticalGlue(), g);
        g.weighty = 0;

        // Botón Crear
        g.gridy = fila++;
        g.insets = new Insets(10, 10, 10, 10);
        JButton btnCrear = new JButton("Crear");
        btnCrear.setBackground(COLOR_NARANJA);
        btnCrear.setFont(new Font("Arial", Font.BOLD, 15));
        btnCrear.setPreferredSize(new Dimension(0, 42));
        btnCrear.setFocusPainted(false);
        btnCrear.addActionListener(e -> crearUsuario());
        panel.add(btnCrear, g);

        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica de datos
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarUsuarios() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String json = api.get("/usuarios");
                JsonNode array = mapper.readTree(json);
                SwingUtilities.invokeLater(() -> {
                    modeloUsuarios.setRowCount(0);
                    for (JsonNode n : array) {
                        String nombre = n.path("nombreUsuario").asText();
                        String pass   = n.path("contrasenaHash").asText();
                        // Solo mostrar usuarios con rol USER (los ADMIN no se listan)
                        String rolN = n.path("rol").asText();
                        if ("USER".equalsIgnoreCase(rolN)) {
                            modeloUsuarios.addRow(new Object[]{nombre, pass});
                        }
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                try { get(); }
                catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error al cargar usuarios", ex);
                    JOptionPane.showMessageDialog(USUARIO.this,
                        "Error al cargar usuarios:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void crearUsuario() {
        String nombre = txtNuevoUsuario.getText().trim();
        String pass1  = new String(txtPassword.getPassword());
        String pass2  = new String(txtConfirmar.getPassword());

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNuevoUsuario.requestFocus();
            return;
        }
        if (pass1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contraseña es obligatoria.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.",
                "Error de validación", JOptionPane.WARNING_MESSAGE);
            txtConfirmar.setText("");
            txtConfirmar.requestFocus();
            return;
        }

        // Construir JSON — rol siempre USER para cuentas creadas desde este módulo
        // La contraseña se guarda tal cual (contrasenaHash) según el modelo Usuario.java
        String json = "{"
            + "\"nombreUsuario\":\"" + esc(nombre) + "\","
            + "\"contrasenaHash\":\"" + esc(pass1)  + "\","
            + "\"rol\":\"USER\""
            + "}";

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                api.post("/usuarios", json);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(USUARIO.this,
                        "Usuario \"" + nombre + "\" creado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar formulario
                    txtNuevoUsuario.setText("");
                    txtPassword.setText("");
                    txtConfirmar.setText("");
                    txtNuevoUsuario.requestFocus();
                    // Recargar tabla
                    cargarUsuarios();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al crear usuario", ex);
                    String msg = ex.getMessage() != null
                        && ex.getMessage().contains("ya está en uso")
                        ? "El nombre de usuario \"" + nombre + "\" ya existe."
                        : "Error al crear usuario:\n" + ex.getMessage();
                    JOptionPane.showMessageDialog(USUARIO.this, msg,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
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
}