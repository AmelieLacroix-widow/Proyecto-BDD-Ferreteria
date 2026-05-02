import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Login extends JFrame {

    public Login() {
        setTitle("Login");
        setSize(500, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;                  
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.weighty = 0;                
        gbc.insets = new Insets(10, 40, 5, 40);

        // ===== 1) Logo + nombre =====
        gbc.gridy = 0;
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.LIGHT_GRAY);


        ImageIcon icono = new ImageIcon("logo.png");
        Image img = icono.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(img));
        JLabel nombre = new JLabel("Ferretería e Instalaciones Eléctricas Alanís");
        nombre.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(logo);
        header.add(nombre);

        panel.add(header, gbc);

        // ===== 2) "Crear" =====
        gbc.gridy = 1;
        JLabel crear = new JLabel("Acceder", SwingConstants.CENTER);
        crear.setOpaque(true);
        crear.setBackground(new Color(255, 153, 51));
        crear.setFont(new Font("Arial", Font.BOLD, 22));
        crear.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        crear.setPreferredSize(new Dimension(0, 40));
        panel.add(crear, gbc);

        // ===== 3) "Usuario:" =====
        gbc.gridy = 2;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblUsuario, gbc);

        // ===== 4) JTextField =====
        gbc.gridy = 3;
        JTextField usuario = new JTextField();
        usuario.setPreferredSize(new Dimension(0, 35));
        panel.add(usuario, gbc);

        // ===== 5) "Contraseña:" =====
        gbc.gridy = 4;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblPass, gbc);

        // ===== 6) JPasswordField =====
        gbc.gridy = 5;
        JPasswordField pass = new JPasswordField();
        pass.setPreferredSize(new Dimension(0, 35));
        JButton ojo = new JButton("👁");
        ojo.setBorderPainted(false);
        ojo.setContentAreaFilled(false);
        ojo.setFocusPainted(false);
        JPanel panelContraseña = new JPanel(new BorderLayout());
        panelContraseña.add(pass, BorderLayout.CENTER);
        panelContraseña.add(ojo, BorderLayout.EAST);
        panel.add(panelContraseña, gbc);

        // ===== 7) Mensaje de error =====
        gbc.gridy = 6;
        JLabel error = new JLabel("Usuario o contraseña incorrecta", SwingConstants.CENTER);
        error.setForeground(Color.RED);
        error.setVisible(false);
        panel.add(error, gbc);

        // ===== 8) Botón =====
        gbc.gridy = 7;
        JButton boton = new JButton("Acceder");
        boton.setBackground(new Color(255, 204, 102));
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setPreferredSize(new Dimension(0, 50));
        panel.add(boton, gbc);

        gbc.gridy = 8;          
        gbc.weighty = 1;         
        panel.add(Box.createVerticalGlue(), gbc);

        // Evento
        boton.addActionListener(e -> {
            String user = usuario.getText().trim();
            String p1 = new String(pass.getPassword());
            // Aquí iría la lógica de autenticación, por ahora solo es un ejemplo
            if (user.equals("admin") && p1.equals("1234")) {
                JOptionPane.showMessageDialog(this, "¡Bienvenido, " + user + "!");
            } else {
                error.setVisible(true);
            }
        });


        char echoPass = pass.getEchoChar();

        //Ojo
        ojo.addActionListener(e -> {
        boolean visible = pass.getEchoChar() == (char) 0;

        if (visible) {
            pass.setEchoChar(echoPass); // ocultar
        } else {
           pass.setEchoChar((char) 0); // mostrar
        }
        });

        add(panel);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}