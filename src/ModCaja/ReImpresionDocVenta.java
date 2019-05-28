/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import TableUtiles.DateCellRender;
import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.TableRowSorter;
import utiles.ButtonReImpresionDocsVentas;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.MaxLength;
import utiles.TableReImpresionDocsVentas;

/**
 *
 * @author ANDRES
 */
public class ReImpresionDocVenta extends javax.swing.JDialog {

    String fecVigencia = "";
    private TableRowSorter sorter;
    boolean hayFilas = false;
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    
    public ReImpresionDocVenta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        configCampos();
        llenarCampos();
    }

    private void llenarCampos(){
        jTFCodCaja.setText("0");
        jLNroCaja.setText("(TODAS)");
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
        getDatosReport();
    }
    
    private void getFecVigencia(){
        try {
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date d = new java.util.Date();
                fecVigencia = sdf.format(d);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Formateando Fecha...");
            }
    }
    
    private void configCampos(){
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        jTFCodCaja.setDocument(new MaxLength(6, "", "ENTERO"));
        
        jTFFecDesde.addFocusListener(new Focus()); 
        jTFFecHasta.addFocusListener(new Focus()); 
        jTFCodCaja.addFocusListener(new Focus());
    }
    
    private void llenarTabla(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String cod_caja = jTFCodCaja.getText().trim();
        String fecDesde = jTFFecDesde.getText().trim();
        String fecHasta = jTFFecHasta.getText().trim();
        ResultSet updateTable = rsDocsVentas(cod_caja, fecDesde, fecHasta);
        if(updateTable != null){
            jTDocsVenta.setModel(new TableReImpresionDocsVentas(updateTable));
            configTabla();
            jTDocsVenta.setFont(new Font("Tahoma", 1, 11));
            jTDocsVenta.setRowHeight(22);
            sorter = new TableRowSorter(jTDocsVenta.getModel());
            jTDocsVenta.setRowSorter(sorter);
            checkTable();
        }
        
    }
    
    private ResultSet rsDocsVentas(String codCaja, String fecDesde, String fecHasta){
        ResultSet rs = null;
        String sql = "";
        
        if(!codCaja.equals("0")){
            sql =     "SELECT DISTINCT cab.cod_caja, cab.nro_comprob, cab.cod_cliente || ' ' || cli.razon_soc AS cliente, "
                    + "to_char(cab.fec_comprob, 'dd/MM/yyyy') AS fecha, cab.mon_total, cab.nro_turno, "
                    + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cajero) AS cajero "
                    + "FROM venta_cab cab "
                    + "INNER JOIN cliente cli "
                    + "ON cab.cod_cliente = cli.cod_cliente "
                    + "WHERE cab.fec_comprob::date >= to_date('" + fecDesde +"', 'dd/MM/yyyy') "
                    + "AND  cab.fec_comprob::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                    + "AND cab.cod_caja = " + codCaja
                    + " ORDER BY cab.cod_caja, cab.nro_comprob";
        }else{
            sql =     "SELECT DISTINCT cab.cod_caja, cab.nro_comprob, cab.cod_cliente || ' ' || cli.razon_soc AS cliente, "
                    + "to_char(cab.fec_comprob, 'dd/MM/yyyy') AS fecha, cab.mon_total, cab.nro_turno, "
                    + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cajero) AS cajero "
                    + "FROM venta_cab cab "
                    + "INNER JOIN cliente cli "
                    + "ON cab.cod_cliente = cli.cod_cliente "
                    + "WHERE cab.fec_comprob::date >= to_date('" + fecDesde +"', 'dd/MM/yyyy') "
                    + "AND  cab.fec_comprob::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                    + "ORDER BY cab.cod_caja, cab.nro_comprob";
        }
        
        System.out.println("SQL DETALLE DOCS: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private void configTabla(){
        jTDocsVenta.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTDocsVenta.getColumnModel().getColumn(0).setPreferredWidth(20);    // Caja
        jTDocsVenta.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDocsVenta.getColumnModel().getColumn(1).setPreferredWidth(20); // nro comprob
        jTDocsVenta.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTDocsVenta.getColumnModel().getColumn(2).setPreferredWidth(60);    // cliente
        jTDocsVenta.getColumnModel().getColumn(2).setCellRenderer(new StringCellRender());
        jTDocsVenta.getColumnModel().getColumn(3).setPreferredWidth(40);    // Fecha comprob
        jTDocsVenta.getColumnModel().getColumn(3).setCellRenderer(new DateCellRender());
        jTDocsVenta.getColumnModel().getColumn(4).setPreferredWidth(60);    // Monto comprob 
        jTDocsVenta.getColumnModel().getColumn(4).setCellRenderer(new DecimalCellRender(0));
     
        // implementacion del button - imprimir
        ButtonReImpresionDocsVentas button = new ButtonReImpresionDocsVentas(jTDocsVenta, 5);
        jTDocsVenta.getColumnModel().getColumn(5).setPreferredWidth(20); // Impresion
        jTDocsVenta.getColumnModel().getColumn(6).setMaxWidth(0); // nro_turno    
        jTDocsVenta.getColumnModel().getColumn(6).setMinWidth(0); // nro_turno    
        jTDocsVenta.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0); // nro_turno    
        jTDocsVenta.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0); // nro_turno    
        jTDocsVenta.getColumnModel().getColumn(6).setCellRenderer(new NumberCellRender());
        jTDocsVenta.getColumnModel().getColumn(7).setMaxWidth(0); // cajero    
        jTDocsVenta.getColumnModel().getColumn(7).setMinWidth(0); // cajero    
        jTDocsVenta.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0); // cajero    
        jTDocsVenta.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0); // cajero 
        jTDocsVenta.getColumnModel().getColumn(7).setCellRenderer(new StringCellRender());
    }
    
    private void checkTable(){
        if(jTDocsVenta.getRowCount() == 0){
            hayFilas = false;
            JOptionPane.showMessageDialog(this,"No se han encontrado registros!","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            jTDocsVenta.requestFocus();
        }else{
            hayFilas = true;
        }
    }
    
    private void getDatosReport(){
    
        try{
            String sql = "SELECT empresa.actividad, local.direccion, local.telefono, local.ciudad "
                       + "FROM empresa "
                       + "INNER JOIN local "
                       + "ON empresa.cod_empresa = local.cod_empresa "; 
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    actividadEmpresa = rs.getString("actividad");
                    direccionEmpresa = rs.getString("direccion");
                    telEmpresa = rs.getString("telefono");
                    ciudadEmpresa = rs.getString("ciudad");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
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
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodCaja = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFFecDesde = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFFecHasta = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDocsVenta = new javax.swing.JTable();
        jBSalir = new javax.swing.JButton();
        jLNroCaja = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Re impresión de docs de ventas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("RE IMPRESION DE DOCS DE VENTAS");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Nro. Caja:");

        jTFCodCaja.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCaja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodCaja.setText("0");
        jTFCodCaja.setToolTipText("0 para todas las cajas");
        jTFCodCaja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCajaFocusGained(evt);
            }
        });
        jTFCodCaja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCajaKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Desde:");

        jTFFecDesde.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecDesde.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecDesde.setText("dd/MM/yyyy");
        jTFFecDesde.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecDesdeFocusGained(evt);
            }
        });
        jTFFecDesde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecDesdeKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Hasta:");

        jTFFecHasta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecHasta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecHasta.setText("dd/MM/yyyy");
        jTFFecHasta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecHastaFocusGained(evt);
            }
        });
        jTFFecHasta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecHastaKeyPressed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jTDocsVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTDocsVenta);

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jLNroCaja.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLNroCaja.setText("***");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 825, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBSalir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar)
                    .addComponent(jLNroCaja))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBSalir)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodCajaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCajaFocusGained
        jTFCodCaja.selectAll();
    }//GEN-LAST:event_jTFCodCajaFocusGained

    private void jTFCodCajaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCajaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCodCaja.getText().trim().equals("0")){
                jLNroCaja.setText("(TODAS)");
                jTFFecDesde.grabFocus();
            }
            
            else if(!jTFCodCaja.getText().trim().equals("")){
                jLNroCaja.setText("CAJA - " + jTFCodCaja.getText().trim());
                jTFFecDesde.grabFocus();
            }else{
                jLNroCaja.setText("");
                jTFCodCaja.setText("0");
                jTFCodCaja.requestFocus();
            }
            
        }
    }//GEN-LAST:event_jTFCodCajaKeyPressed

    private void jTFFecDesdeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDesdeFocusGained
        jTFFecDesde.selectAll();
    }//GEN-LAST:event_jTFFecDesdeFocusGained

    private void jTFFecDesdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDesdeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecHasta.grabFocus();
        }
    }//GEN-LAST:event_jTFFecDesdeKeyPressed

    private void jTFFecHastaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHastaFocusGained
        jTFFecHasta.selectAll();
    }//GEN-LAST:event_jTFFecHastaFocusGained

    private void jTFFecHastaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecHastaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.grabFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        llenarTabla();
    }//GEN-LAST:event_jBBuscarActionPerformed

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
            java.util.logging.Logger.getLogger(ReImpresionDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReImpresionDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReImpresionDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReImpresionDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReImpresionDocVenta dialog = new ReImpresionDocVenta(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLNroCaja;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDocsVenta;
    private javax.swing.JTextField jTFCodCaja;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    // End of variables declaration//GEN-END:variables
}
