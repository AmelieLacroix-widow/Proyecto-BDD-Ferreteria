package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Pantalla de Productos conectada al backend Spring Boot via ApiClient.
 *
 * Botones del .form y su función:
 *   jButton10  → Agregar Producto (busca por código y lo agrega a la tabla)
 *   jButton11  → Modificar (guarda cambios del producto seleccionado)
 *   jButton12  → Eliminar (elimina el producto seleccionado)
 *   jButton15  → REGRESAR al menú principal
 *   jTextField1 → campo de búsqueda por código de barras
 *
 * La tabla jTable1 muestra: Código, Descripción, Precio, Existencia
 * jLabel3 muestra el precio del producto seleccionado
 */
public class PRODUCTOS extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(PRODUCTOS.class.getName());

    private final ApiClient api = ApiClient.getInstance();
    private final ObjectMapper mapper = api.getMapper();

    // Modelo de tabla — se inicializa en configurarTabla()
    private DefaultTableModel tableModel;

    public PRODUCTOS() {
        initComponents();
        setLocationRelativeTo(null);
        configurarTabla();
        cargarTodosLosProductos();
    }

    // -------------------------------------------------------------------------
    // Configuración de la tabla
    // -------------------------------------------------------------------------

    private void configurarTabla() {
        tableModel = new DefaultTableModel(
            new String[]{"Código de Barras", "Descripcion", "Precio", "Cantidad ", "Importe ", "Existencia"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(tableModel);

        // Al seleccionar una fila, mostrar el precio en jLabel3
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                int fila = jTable1.getSelectedRow();
                Object precio = tableModel.getValueAt(fila, 2);
                jLabel3.setText("$ " + (precio != null ? precio.toString() : "0.00"));
                // Poner el código en el campo de texto para facilitar búsqueda/modificación
                jTextField1.setText((String) tableModel.getValueAt(fila, 0));
            }
        });
    }

    // -------------------------------------------------------------------------
    // Cargar todos los productos al abrir la pantalla
    // -------------------------------------------------------------------------

    private void cargarTodosLosProductos() {
        new SwingWorker<List<ProductoDTO>, Void>() {
            @Override
            protected List<ProductoDTO> doInBackground() throws Exception {
                String json = api.get("/productos");
                return mapper.readValue(json, new TypeReference<List<ProductoDTO>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<ProductoDTO> productos = get();
                    tableModel.setRowCount(0);
                    for (ProductoDTO p : productos) {
                        tableModel.addRow(new Object[]{
                            p.getCodigoBarras(),
                            p.getDescripcion(),
                            p.getPrecioVentaLista(),
                            BigDecimal.ZERO,        // Cantidad (para venta, inicia en 0)
                            BigDecimal.ZERO,        // Importe
                            p.getExistencia()
                        });
                    }
                } catch (Exception ex) {
                    mostrarError("Error al cargar productos", ex);
                }
            }
        }.execute();
    }

    // -------------------------------------------------------------------------
    // jButton10 → "Agregar Producto"
    // Busca el producto por código en jTextField1 y lo carga en la tabla
    // -------------------------------------------------------------------------

    private void agregarProducto() {
        String codigo = jTextField1.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa un código de barras para buscar.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<ProductoDTO, Void>() {
            @Override
            protected ProductoDTO doInBackground() throws Exception {
                String json = api.get("/productos/" +
                        URLEncoder.encode(codigo, StandardCharsets.UTF_8));
                return mapper.readValue(json, ProductoDTO.class);
            }

            @Override
            protected void done() {
                try {
                    ProductoDTO p = get();
                    // Verificar si ya existe en la tabla
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (codigo.equals(tableModel.getValueAt(i, 0))) {
                            JOptionPane.showMessageDialog(PRODUCTOS.this,
                                    "El producto ya está en la lista.",
                                    "Duplicado", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                    tableModel.addRow(new Object[]{
                        p.getCodigoBarras(),
                        p.getDescripcion(),
                        p.getPrecioVentaLista(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        p.getExistencia()
                    });
                    jLabel3.setText("$ " + p.getPrecioVentaLista());
                    jTextField1.setText("");
                } catch (Exception ex) {
                    mostrarError("Producto no encontrado", ex);
                }
            }
        }.execute();
    }

    // -------------------------------------------------------------------------
    // jButton11 → "Modificar"
    // Abre un diálogo simple para editar descripción y precio del producto seleccionado
    // -------------------------------------------------------------------------

    private void modificarProducto() {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un producto de la tabla para modificar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) tableModel.getValueAt(fila, 0);
        String descripcionActual = (String) tableModel.getValueAt(fila, 1);
        Object precioActual = tableModel.getValueAt(fila, 2);

        // Diálogo simple para capturar nuevos valores
        JTextField campoDesc = new JTextField(descripcionActual, 25);
        JTextField campoPrecio = new JTextField(precioActual != null ? precioActual.toString() : "", 10);

        JPanel panel = new JPanel(new java.awt.GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Descripción:"));
        panel.add(campoDesc);
        panel.add(new JLabel("Precio de Venta:"));
        panel.add(campoPrecio);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Modificar Producto: " + codigo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            ProductoDTO p = new ProductoDTO();
            p.setCodigoBarras(codigo);
            p.setDescripcion(campoDesc.getText().trim());
            p.setPrecioVentaLista(new BigDecimal(campoPrecio.getText().trim()));
            p.setPrecioCosto(BigDecimal.ZERO);
            p.setPorcentajeGanancia(BigDecimal.ZERO);
            p.setExistencia((BigDecimal) tableModel.getValueAt(fila, 5));
            p.setUnidad("Pza");
            p.setUsaInventario(true);

            String jsonBody = mapper.writeValueAsString(p);

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return api.put("/productos/" +
                            URLEncoder.encode(codigo, StandardCharsets.UTF_8), jsonBody);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                                "Producto modificado correctamente.",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarTodosLosProductos();
                    } catch (Exception ex) {
                        mostrarError("Error al modificar producto", ex);
                    }
                }
            }.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El precio debe ser un número válido.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            mostrarError("Error inesperado", e);
        }
    }

    // -------------------------------------------------------------------------
    // jButton12 → "Eliminar"
    // -------------------------------------------------------------------------

    private void eliminarProducto() {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un producto de la tabla para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) tableModel.getValueAt(fila, 0);
        String descripcion = (String) tableModel.getValueAt(fila, 1);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto: " + descripcion + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return api.delete("/productos/" +
                        URLEncoder.encode(codigo, StandardCharsets.UTF_8));
            }

            @Override
            protected void done() {
                try {
                    int status = get();
                    if (status == 204) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                                "Producto eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        jLabel3.setText("$ 0.00");
                        cargarTodosLosProductos();
                    } else if (status == 409) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                                "No se puede eliminar: el producto tiene ventas registradas.",
                                "Conflicto", JOptionPane.WARNING_MESSAGE);
                    } else if (status == 404) {
                        JOptionPane.showMessageDialog(PRODUCTOS.this,
                                "El producto no existe en el sistema.",
                                "No encontrado", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    mostrarError("Error al eliminar producto", ex);
                }
            }
        }.execute();
    }

    // -------------------------------------------------------------------------
    // Utilidad de error
    // -------------------------------------------------------------------------

    private void mostrarError(String titulo, Exception ex) {
        logger.log(java.util.logging.Level.SEVERE, titulo, ex);
        JOptionPane.showMessageDialog(this,
                titulo + ":\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================================
    // CÓDIGO GENERADO POR NETBEANS — NO MODIFICAR MANUALMENTE
    // =========================================================================

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 36));
        jLabel2.setForeground(new java.awt.Color(255, 153, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setToolTipText("");
        jLabel2.setOpaque(true);

        jButton1.setBackground(new java.awt.Color(255, 153, 0));
        jButton1.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton1.setText("VENTAS");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setBackground(new java.awt.Color(255, 153, 0));
        jButton2.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton2.setText("CRÉDITOS");

        jButton3.setBackground(new java.awt.Color(255, 153, 0));
        jButton3.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton3.setText("CLIENTES");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setBackground(new java.awt.Color(255, 153, 0));
        jButton4.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton4.setText("PRODUCTOS");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setBackground(new java.awt.Color(255, 153, 0));
        jButton5.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton5.setText("INVENTARIO");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setBackground(new java.awt.Color(255, 153, 0));
        jButton6.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton6.setText("PROVEEDORES");

        jButton7.setBackground(new java.awt.Color(255, 153, 0));
        jButton7.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton7.setText("COMPRAS");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        jButton8.setBackground(new java.awt.Color(255, 153, 0));
        jButton8.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton8.setText("REPORTES");

        jButton9.setBackground(new java.awt.Color(255, 153, 0));
        jButton9.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 14));
        jButton9.setText("CONFIGURACIÓN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1071, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(255, 153, 0));
        jLabel1.setText("Codigo del Producto");
        jLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED,
                new java.awt.Color(255, 153, 0), new java.awt.Color(255, 153, 51),
                java.awt.Color.black, java.awt.Color.black));

        jTextField1.setBorder(javax.swing.BorderFactory.createBevelBorder(
                javax.swing.border.BevelBorder.RAISED,
                java.awt.Color.black, new java.awt.Color(255, 102, 0),
                java.awt.Color.black, java.awt.Color.black));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jButton10.setBackground(new java.awt.Color(51, 204, 0));
        jButton10.setText("Agregar Producto ");
        jButton10.addActionListener(this::jButton10ActionPerformed);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"Codigo De Barras", "Descripcion", "Precio", "Cantidad ", "Importe ", "Existencia"}
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton11.setBackground(new java.awt.Color(255, 102, 0));
        jButton11.setText("Modificar");
        jButton11.addActionListener(this::jButton11ActionPerformed);

        jButton12.setBackground(new java.awt.Color(204, 51, 0));
        jButton12.setText("Eliminar");
        jButton12.addActionListener(this::jButton12ActionPerformed);

        jButton13.setBackground(new java.awt.Color(255, 153, 0));
        jButton13.setText("Asignar Cliente");

        jButton14.setBackground(new java.awt.Color(102, 204, 0));
        jButton14.setText("Cobrar");

        jLabel3.setBackground(new java.awt.Color(255, 204, 102));
        jLabel3.setFont(new java.awt.Font("ITF Devanagari Marathi", 0, 24));
        jLabel3.setText("$ 0.00");

        jButton15.setText("REGRESAR");
        jButton15.addActionListener(this::jButton15ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton10)
                .addGap(58, 58, 58))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1048, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14))
                    .addComponent(jButton15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10)
                    .addComponent(jTextField1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // =========================================================================
    // HANDLERS DE BOTONES — conectados en initComponents con addActionListener
    // =========================================================================

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO: abrir pantalla de Ventas
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO: abrir pantalla de Clientes
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Ya estamos en PRODUCTOS — no hace falta navegar
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO: abrir pantalla de Inventario
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO: abrir pantalla de Compras
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // Al presionar Enter en el campo de código, busca el producto
        agregarProducto();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        agregarProducto();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        modificarProducto();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        eliminarProducto();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        new MenúPrincipal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton15ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new PRODUCTOS().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
