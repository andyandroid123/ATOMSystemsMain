/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero.informes;

import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.DBManager;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class DetalleDocVenta extends javax.swing.JDialog {

    private static DefaultTableModel dtmDetallesDoc;
    private String nroDocumento = "", codCaja = "", fecDocumento = "";
    
    public DetalleDocVenta(java.awt.Frame parent, boolean modal, String nroDocumento, String codCaja, String fecDocumento, String modulo) {
        super(parent, modal);
        initComponents();
        this.setTitle("ATOMSystems|Main - Detalle del documento de venta: " + nroDocumento);
        this.nroDocumento = nroDocumento;
        this.codCaja = codCaja;
        this.fecDocumento = fecDocumento;
        configTabla();
        cargarDetalle();
        
        if(modulo.equals("COBRANZA")){
            jBImprimir.setVisible(false);
        }
    }

    private void configTabla(){
        dtmDetallesDoc = (DefaultTableModel)jTDetallesDoc.getModel();
        
        jTDetallesDoc.getColumnModel().getColumn(0).setPreferredWidth(30); // código (int) 
        jTDetallesDoc.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetallesDoc.getColumnModel().getColumn(1).setPreferredWidth(150); // descripción (String)  
        jTDetallesDoc.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTDetallesDoc.getColumnModel().getColumn(2).setPreferredWidth(40); // cantidad (float) 
        jTDetallesDoc.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(2));
        jTDetallesDoc.getColumnModel().getColumn(3).setPreferredWidth(60); // empaque (String) 
        jTDetallesDoc.getColumnModel().getColumn(3).setCellRenderer(new StringCellRender());
        jTDetallesDoc.getColumnModel().getColumn(4).setPreferredWidth(20); // iva (int)
        jTDetallesDoc.getColumnModel().getColumn(4).setCellRenderer(new NumberCellRender());
        jTDetallesDoc.getColumnModel().getColumn(5).setPreferredWidth(80); // monto iva (double)
        jTDetallesDoc.getColumnModel().getColumn(5).setCellRenderer(new DecimalCellRender(0));
        jTDetallesDoc.getColumnModel().getColumn(6).setPreferredWidth(80); // monto costo (double)
        jTDetallesDoc.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(0));
        jTDetallesDoc.getColumnModel().getColumn(7).setPreferredWidth(80); // monto margen (double)
        jTDetallesDoc.getColumnModel().getColumn(7).setCellRenderer(new DecimalCellRender(0));
        jTDetallesDoc.getColumnModel().getColumn(8).setPreferredWidth(80); // monto venta (double)
        jTDetallesDoc.getColumnModel().getColumn(8).setCellRenderer(new DecimalCellRender(0));
        
        utiles.Utiles.punteroTablaF(jTDetallesDoc, this);        
        jTDetallesDoc.setFont(new Font("Tahoma", 1, 12) );
        jTDetallesDoc.setRowHeight(20);
    }
    
    private void cargarTabla(){
        StatementManager sm = new StatementManager();
        
        String sql = "SELECT DISTINCT d.cod_articulo, a.descripcion, d.can_venta, d.sigla_venta || '-' || d.cansi_venta AS empaque, d.pct_iva, d.mon_iva, "
                   + "d.mon_costo, d.mon_margen, d.mon_venta "
                   + "FROM venta_det d "
                   + "INNER JOIN articulo a "
                   + "ON d.cod_articulo = a.cod_articulo "
                   + "WHERE d.nro_ticket = " + nroDocumento + " "
                   + "AND d.cod_caja = " + codCaja + " "
                   + "AND d.fec_comprob::date = '" + fecDocumento +"'::date";
        
        try{
            sm.TheSql = sql;
            System.out.println("DETALLES DEL DOCUMENTO SELECCIONADO: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[9];
                    row[0] = sm.TheResultSet.getInt("cod_articulo");
                    row[1] = sm.TheResultSet.getString("descripcion");
                    row[2] = sm.TheResultSet.getFloat("can_venta");
                    row[3] = sm.TheResultSet.getString("empaque");
                    row[4] = sm.TheResultSet.getInt("pct_iva");
                    row[5] = sm.TheResultSet.getDouble("mon_iva");
                    row[6] = sm.TheResultSet.getDouble("mon_costo");
                    row[7] = sm.TheResultSet.getDouble("mon_margen");
                    row[8] = sm.TheResultSet.getDouble("mon_venta");
                    
                    dtmDetallesDoc.addRow(row);
                }
                jTDetallesDoc.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No fue posible recuperar detalles del documento!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void limpiarTablaDetalle(){
        for(int i = 0; i < jTDetallesDoc.getRowCount(); i++){
            dtmDetallesDoc.removeRow(i);
            i--;
        }
    }
    
    private void cargarDetalle(){
        limpiarTablaDetalle();
        cargarTabla();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesDoc = new javax.swing.JTable();
        jBSalir = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jTDetallesDoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Cantidad", "Empaque", "IVA", "Monto IVA", "Monto Costo", "Monto Margen ", "Monto Venta"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTDetallesDoc);
        if (jTDetallesDoc.getColumnModel().getColumnCount() > 0) {
            jTDetallesDoc.getColumnModel().getColumn(0).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(1).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(2).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(3).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(4).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(5).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(6).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(7).setResizable(false);
            jTDetallesDoc.getColumnModel().getColumn(8).setResizable(false);
        }

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSalir)
                    .addComponent(jBImprimir))
                .addContainerGap(27, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(DetalleDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetalleDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetalleDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetalleDocVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DetalleDocVenta dialog = new DetalleDocVenta(new javax.swing.JFrame(), true, "", "", "", "");
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
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBSalir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesDoc;
    // End of variables declaration//GEN-END:variables
}
