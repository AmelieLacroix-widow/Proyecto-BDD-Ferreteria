package com.mycompany.ferreteria_alanis;

public class Ferreteria_Alanis {

    public static void main(String[] args) {

        // 🔹 Aplicar tema Nimbus (opcional pero recomendado)
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

        // 🔥 INICIO DE LA APP → abrir LOGIN
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
