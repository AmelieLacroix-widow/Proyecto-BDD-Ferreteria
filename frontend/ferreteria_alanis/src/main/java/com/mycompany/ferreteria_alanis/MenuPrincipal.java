package com.mycompany.ferreteria_alanis;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

/**
 * Pantalla principal del sistema. Se muestra tras el login y actúa como menú
 * de navegación. Los módulos visibles dependen del rol del usuario.
 *
 * Constructores:
 *   new MenuPrincipal()                    — usa SesionActual (recomendado)
 *   new MenuPrincipal(String rol)          — compatibilidad con Login.java actual
 */
public class MenuPrincipal extends JFrame {

    private final String rol;
    private final String nombreUsuario;

    // ─── Compatibilidad con Login.java (que aún llama MenuPrincipal(rol)) ───
    public MenuPrincipal(String rol) {
        this(rol, SesionActual.getNombreUsuario());
    }

    public MenuPrincipal(String rol, String nombreUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        initUI();
        setTitle("Ferretería Alanís");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la UI
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setLayout(new BorderLayout());
        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelBotones(), BorderLayout.CENTER);
    }

    /** Franja superior: logo + nombre empresa + "Usuario: X" a la derecha. */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Lado izquierdo: logo + nombre
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);

        ImageIcon icono = new ImageIcon("logo.png");
        Image img = icono.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(img));
        izquierda.add(logo);

        JLabel nombre = new JLabel("<html>Ferretería e Instalaciones<br>Eléctricas Alanís</html>");
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("Arial", Font.BOLD, 13));
        izquierda.add(nombre);

        // Lado derecho: "Usuario: X" con popup de sesión
        JButton btnUsuario = new JButton("Usuario: " + nombreUsuario);
        btnUsuario.setBackground(new Color(255, 153, 0));
        btnUsuario.setForeground(Color.BLACK);
        btnUsuario.setFont(new Font("Arial", Font.BOLD, 13));
        btnUsuario.setFocusPainted(false);
        btnUsuario.setBorderPainted(false);
        btnUsuario.addActionListener(e -> mostrarPopupSesion(btnUsuario));

        header.add(izquierda, BorderLayout.WEST);
        header.add(btnUsuario, BorderLayout.EAST);
        return header;
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
        titulo.setBackground(new Color(255, 153, 0));
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

    /** Panel central con los botones de módulos según el rol. */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título
        JLabel titulo = new JLabel("Ferretería e Instalaciones Eléctricas Alanís");
        titulo.setFont(new Font("Times New Roman", Font.BOLD, 26));
        titulo.setForeground(new Color(255, 153, 0));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titulo, gbc);

        // Botones (tamaño fijo para uniformidad)
        Dimension tamBoton = new Dimension(160, 60);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        boolean esAdmin = "ADMIN".equalsIgnoreCase(rol);

        // Fila 1
        int col = 0;
        gbc.gridy = 1;

        gbc.gridx = col++;
        panel.add(crearBotonModulo("VENTAS", tamBoton, e -> abrirVentas()), gbc);

        if (esAdmin) {
            gbc.gridx = col++;
            panel.add(crearBotonModulo("PRODUCTOS", tamBoton, e -> abrirProductos()), gbc);
        }

        gbc.gridx = col++;
        panel.add(crearBotonModulo("INVENTARIO", tamBoton, e -> {
            JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
        }), gbc);

        // Fila 2 (solo admin)
        if (esAdmin) {
            gbc.gridy = 2;
            gbc.gridx = 0;
            panel.add(crearBotonModulo("CORTE", tamBoton, e -> {
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Corte", JOptionPane.INFORMATION_MESSAGE);
            }), gbc);

            gbc.gridx = 1;
            panel.add(crearBotonModulo("USUARIO", tamBoton, e -> {
                JOptionPane.showMessageDialog(this, "Módulo en construcción.", "Usuario", JOptionPane.INFORMATION_MESSAGE);
            }), gbc);
        }

        return panel;
    }

    private JButton crearBotonModulo(String texto, Dimension tam,
                                     java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(tam);
        btn.setBackground(new Color(255, 153, 0));
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.addActionListener(accion);
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navegación
    // ─────────────────────────────────────────────────────────────────────────

    private void abrirVentas() {
        new VENTAS(rol, nombreUsuario).setVisible(true);
        this.dispose();
    }

    private void abrirProductos() {
        new PRODUCTOS(rol, nombreUsuario).setVisible(true);
        this.dispose();
    }

    // main de respaldo (para pruebas directas)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal("ADMIN", "Admin").setVisible(true));
    }
}
