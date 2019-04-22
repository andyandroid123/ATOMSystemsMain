/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.informes;

import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class HistoricoDetalleCompras extends javax.swing.JDialog {

    private DecimalFormat decimal = new DecimalFormat("#,##0.00");
    private int codEmpresa;
    private int codLocal;
    private String periodo;
    private static DefaultTableModel dtmDetallesCompras;
    
    
    
    public HistoricoDetalleCompras(java.awt.Frame parent, boolean modal, int codEmpresa, int codLocal, String periodo, long codArticulo, String descripcion) {
        super(parent, modal);
        initComponents();
        this.codEmpresa = codEmpresa;
        this.codLocal = codLocal;
        this.periodo = periodo;
        jTFCodArticulo.setText(String.valueOf(codArticulo));
        jTFDescArticulo.setText(descripcion);
        configGrilla();
        cargaGrilla();
    }

    private void cargaGrilla(){
        limpiarGrilla();
        if(utiles.Utiles.isNumeric(jTFCodArticulo.getText())){
            StatementManager sm = new StatementManager();
            try{
                sm.TheSql = getSql();
                sm.EjecutarSql();
                DefaultTableModel dtm = (DefaultTableModel)this.jTDetallesCompras.getModel();
                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        // local | fec doc | nro doc | proveedor | cantidad | sigla | um | costo iva inc | costo uni | monto compra 
                        dtm.addRow(new Object[]{sm.TheResultSet.getString("cod_local"), sm.TheResultSet.getString("fec_comprob"), 
                                                sm.TheResultSet.getString("nro_comprob"), (sm.TheResultSet.getString("cod_proveedor") + " " + sm.TheResultSet.getString("razon_soc")), 
                                                sm.TheResultSet.getDouble("cant_recib"), sm.TheResultSet.getString("sigla_compra"), sm.TheResultSet.getString("cansi_compra"), 
                                                sm.TheResultSet.getDouble("costo_empaque"), sm.TheResultSet.getDouble("costo_unitario"), 
                                                sm.TheResultSet.getDouble("mon_total")});
                    }
                    this.jTDetallesCompras.setModel(dtm);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "ATENCION: \n Error recuperando detalles de compras!", "Error", 2);
            }finally{
                DBManager.CerrarStatements();
                sm = null;
            }
        }
    }
    
    private void configGrilla(){
        dtmDetallesCompras = (DefaultTableModel)jTDetallesCompras.getModel();
        
        jTDetallesCompras.getColumnModel().getColumn(0).setPreferredWidth(20); // local
        jTDetallesCompras.getColumnModel().getColumn(1).setPreferredWidth(20); // fec doc 
        jTDetallesCompras.getColumnModel().getColumn(2).setPreferredWidth(50); // nro doc 
        jTDetallesCompras.getColumnModel().getColumn(3).setPreferredWidth(50); // proveedor 
        jTDetallesCompras.getColumnModel().getColumn(4).setPreferredWidth(15); // cantidad
        jTDetallesCompras.getColumnModel().getColumn(5).setPreferredWidth(15); // sigla
        jTDetallesCompras.getColumnModel().getColumn(6).setPreferredWidth(15); // um 
        jTDetallesCompras.getColumnModel().getColumn(7).setPreferredWidth(15); // costo iva inc
        jTDetallesCompras.getColumnModel().getColumn(8).setPreferredWidth(15); // costo unit iva inc
        jTDetallesCompras.getColumnModel().getColumn(9).setPreferredWidth(15); // monto compra
        
        
        utiles.Utiles.punteroTablaF(jTDetallesCompras, this);        
        jTDetallesCompras.setFont(new Font("Tahoma", 1, 12) );
        jTDetallesCompras.setRowHeight(20);
    }
    
    private void limpiarGrilla(){
        DefaultTableModel dtm = (DefaultTableModel)this.jTDetallesCompras.getModel();
        while(dtm.getRowCount() > 0){
            dtm.removeRow(0);
        }
        this.jTDetallesCompras.setModel(dtm);
    }
    
    private String getSql(){
        String sql = "SELECT cd.cod_empresa, cd.cod_local, cd.cod_sector, TO_CHAR(cd.fec_comprob,'DD/MM/YYYY') AS fec_comprob, cd.tip_comprob, cd.nro_comprob,  cd.cod_proveedor, "
                   + "p.razon_soc, cd.cant_recib, cd.sigla_compra, cd.cansi_compra, cd.costo_empaque, cd.costo_unitario, cd.costo_flete,  cd.pct_dcto, cd.pct_iva, cd.mon_total "
                   + "FROM compra_det cd  INNER JOIN proveedor p ON cd.cod_proveedor = p.cod_proveedor "
                   + "WHERE cd.cod_empresa = " + this.codEmpresa + " "
                   + "AND cd.cod_local = " + this.codLocal + " "
                   + "AND cd.cod_articulo = " + jTFCodArticulo.getText() + " "
                   + "AND TO_CHAR(cd.fec_comprob,'MM/YYYY') = '" + this.periodo + "' "
                   + "AND cd.estado ='V' "
                   + "ORDER BY cd.fec_comprob DESC, cd.cod_empresa, cd.cod_local, cd.cod_sector";
        return sql;
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
        jLabel1 = new javax.swing.JLabel();
        jTFCodArticulo = new javax.swing.JTextField();
        jTFDescArticulo = new javax.swing.JTextField();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesCompras = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Histórico de Compras");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel1.setText("Artículo:");

        jTFCodArticulo.setEditable(false);
        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodArticulo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodArticulo.setText("0");

        jTFDescArticulo.setEditable(false);
        jTFDescArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jBImprimir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
            }
        });

        jBSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTDetallesCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Fecha Doc", "Nro Doc", "Proveedor", "Cantidad", "Sigla", "U/M", "Costo IVA Inc", "Costo Unit IVA Inc", "Monto Compra"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTDetallesCompras);
        if (jTDetallesCompras.getColumnModel().getColumnCount() > 0) {
            jTDetallesCompras.getColumnModel().getColumn(0).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(1).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(2).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(3).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(4).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(5).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(6).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(7).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(8).setResizable(false);
            jTDetallesCompras.getColumnModel().getColumn(9).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(jBImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBImprimir)
                    .addComponent(jBSalir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        JOptionPane.showMessageDialog(this, "Atención: \n Informe en desarrollo!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jBImprimirActionPerformed

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
            java.util.logging.Logger.getLogger(HistoricoDetalleCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                /*HistoricoDetalleCompras dialog = new HistoricoDetalleCompras(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);*/
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesCompras;
    private javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFDescArticulo;
    // End of variables declaration//GEN-END:variables
}
