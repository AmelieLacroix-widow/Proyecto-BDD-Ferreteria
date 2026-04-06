package com.mycompany.ferreteria_alanis;

public class Ferreteria_Alanis {

    public static void main(String[] args) {
        // Aplica el look and feel Nimbus antes de mostrar cualquier ventana
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ferreteria_Alanis.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
