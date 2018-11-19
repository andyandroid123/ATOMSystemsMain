/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero;

import TableUtiles.DateCellRender;
import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.TableRowSorter;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableModificacionVencDocsClientes;
import views.busca.BuscaCliente;

/**
 *
 * @author Administrador
 */
public class ModificarVencimientos extends javax.swing.JDialog {

    private TableRowSorter sorter;
    boolean hayFilas = false;
    
    public ModificarVencimientos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodCliente.addFocusListener(new Focus());
        jTFCodCliente.setText("0");
    }

    private void configTabla(){
        jTDetalleDocs.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTDetalleDocs.getColumnModel().getColumn(0).setPreferredWidth(40); // nro documento
        jTDetalleDocs.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetalleDocs.getColumnModel().getColumn(1).setPreferredWidth(20); // nro cuota 
        jTDetalleDocs.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTDetalleDocs.getColumnModel().getColumn(2).setPreferredWidth(20); // cant cuota
        jTDetalleDocs.getColumnModel().getColumn(2).setCellRenderer(new NumberCellRender());
        jTDetalleDocs.getColumnModel().getColumn(3).setPreferredWidth(20); // tipo comprob
        jTDetalleDocs.getColumnModel().getColumn(3).setCellRenderer(new StringCellRender());
        jTDetalleDocs.getColumnModel().getColumn(4).setPreferredWidth(30); // fec emision
        jTDetalleDocs.getColumnModel().getColumn(4).setCellRenderer(new DateCellRender());
        jTDetalleDocs.getColumnModel().getColumn(5).setPreferredWidth(30); // fec vencimiento 
        jTDetalleDocs.getColumnModel().getColumn(5).setCellRenderer(new DateCellRender());
        //jTDetalleDocs.getColumnModel().getColumn(4).setCellRenderer(new StringCellRenderModVencimiento());
        jTDetalleDocs.getColumnModel().getColumn(6).setPreferredWidth(60); // valor cuota
        jTDetalleDocs.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(0));
        jTDetalleDocs.getColumnModel().getColumn(7).setPreferredWidth(20); // modificar
        
        utiles.Utiles.punteroTablaF(jTDetalleDocs, this);        
        jTDetalleDocs.setFont(new Font("Tahoma", 1, 12) );
        jTDetalleDocs.setRowHeight(22);
    }
    
    private void getDatosCliente(String codigo){        
        try{
            String sql = "SELECT cliente.razon_soc, cuenta.cod_cuenta "
                       + "FROM cliente "
                       + "INNER JOIN cuenta "
                       + "ON cliente.cod_cliente = cuenta.cod_cliente "
                       + "WHERE cliente.cod_cliente = " + codigo; 
            System.out.println("SQL DATOS CLIENTE: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreCliente.setText(rs.getString("razon_soc"));
                    jTFCodCta.setText(rs.getString("cod_cuenta"));
                }else{
                    jLNombreCliente.setText("INEXISTENTE");
                    jTFCodCta.setText("0");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void llenarTabla(){
        String codCliente = jTFCodCliente.getText().trim();
        ResultSet updateTabla = rSDetallesDocs(codCliente);
        if(updateTabla != null){
            jTDetalleDocs.setModel(new TableModificacionVencDocsClientes(updateTabla));
            configTabla();
            sorter = new TableRowSorter(jTDetalleDocs.getModel());
            jTDetalleDocs.setRowSorter(sorter);
            checkTable();
        }
    }
    
    private ResultSet rSDetallesDocs(String codCliente){
        ResultSet rs = null;
        String sql = "SELECT DISTINCT nro_comprob, nro_cuota, can_cuota, TO_CHAR(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "TO_CHAR(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, monto_cuota, tip_comprob "
                   + "FROM venta_det_cuotas "
                   + "WHERE cod_cliente = " + codCliente + " AND nro_pago = 0 AND estado = 'V' "
                   + "ORDER BY nro_comprob";
        System.out.println("DETALLES DE DOCS PENDIENTES - MOD FEC VENCIMIENTO: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private void checkTable(){
        if(jTDetalleDocs.getRowCount() == 0){
            hayFilas = false;
            JOptionPane.showMessageDialog(this,"No se han encontrado registros!","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            jTFCodCliente.requestFocus();
        }else{
            hayFilas = true;
        }
    }
    
    private boolean grabarModificaciones(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoDatos = leerTablaDetallesDocs();
        if(!problemLeyendoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = true;
        }
        return estadoGrabado;
    }
   
    
    private boolean leerTablaDetallesDocs(){
        
        String codCliente = jTFCodCliente.getText().trim();
        boolean resultUpdateVentaDetCuotas = false;
        boolean resultOperacionUpdate = false;
        boolean problemFound = false;
        
        for(int i = 0; i < jTDetalleDocs.getRowCount(); i++){
            if(jTDetalleDocs.getValueAt(i, 7).toString() == "true"){
                String fecVencimiento = jTDetalleDocs.getValueAt(i, 5).toString().trim();
                String nroComprob = jTDetalleDocs.getValueAt(i, 0).toString();
                int nroCuota = Integer.parseInt(jTDetalleDocs.getValueAt(i, 1).toString());
                resultOperacionUpdate = updateVentaDetCuotas(fecVencimiento, nroComprob, codCliente, nroCuota);
                
                if(!resultOperacionUpdate){
                    resultUpdateVentaDetCuotas = true;
                }
            }
        }
        
        if(resultUpdateVentaDetCuotas){
            problemFound = true;
            if(!rollBacktDatos()){
                problemFound = true;
            }
        }else{
            if(!commitDatos()){
                problemFound = true;
            }
        }
        return problemFound;
    }
    
    private boolean updateVentaDetCuotas(String fecVencimiento, String nroComprob, String codCliente, int nroCuota){
        String sql = "UPDATE venta_det_cuotas SET fec_vencimiento = '" + fecVencimiento + "'::date WHERE nro_comprob = " + nroComprob + " "
                   + "AND estado = 'V' AND cod_cliente = " + codCliente + " AND nro_cuota = " + nroCuota;
        System.out.println("UPDATE VTA-DET-CUOTAS-FEC VENCIMIENTO: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarPrevioCommit(String sql){
        boolean result;
        if(DBManager.ejecutarDML(sql) > 0){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
    
    private boolean commitDatos() {
        boolean result = true;
        try {
            DBManager.conn.commit();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private boolean rollBacktDatos() {
        boolean result = true;
        try {
            DBManager.conn.rollback();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodCta = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalleDocs = new javax.swing.JTable();
        jBGrabar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Modificación de Vencimientos de Cuotas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("MODIFICACION DE VENCIMIENTOS DE CUOTAS");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCliente.setText("0");
        jTFCodCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodClienteFocusGained(evt);
            }
        });
        jTFCodCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodClienteKeyPressed(evt);
            }
        });

        jLNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreCliente.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Cod. Cta.:");

        jTFCodCta.setEditable(false);
        jTFCodCta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCta.setText("0");

        jTDetalleDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTDetalleDocs.setToolTipText("");
        jTDetalleDocs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetalleDocsKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetalleDocs);

        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGrabar.setText("Grabar");
        jBGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel4.setText("Doble clic sobre fecha de vencimiento para modificar ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
                        .addComponent(jBBuscar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBGrabar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBGrabar)
                        .addComponent(jBCancelar))
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(jTDetalleDocs.getRowCount() > 0){
            if(grabarModificaciones()){
                JOptionPane.showMessageDialog(this, "Fechas modificadas con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Error modificando fecha de vencimiento", "Éxito", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

    private void jTFCodClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCodCliente.getText().trim().equals("0")){
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else if(jTFCodCliente.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else{
                getDatosCliente(jTFCodCliente.getText().trim()); 
                jBBuscar.grabFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            BuscaCliente bCliente = new BuscaCliente(new JFrame(), true);
            bCliente.pack();
            bCliente.setVisible(true);
            if(bCliente.codigo != 0){
                jTFCodCliente.setText(String.valueOf(bCliente.codigo));
            }
        }
        
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        llenarTabla();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jTDetalleDocsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetalleDocsKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTDetalleDocs.setValueAt(true, jTDetalleDocs.getSelectedRow(), 7);
        }
    }//GEN-LAST:event_jTDetalleDocsKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ModificarVencimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModificarVencimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModificarVencimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModificarVencimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ModificarVencimientos dialog = new ModificarVencimientos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBBuscar;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBGrabar;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetalleDocs;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCta;
    // End of variables declaration//GEN-END:variables
}
