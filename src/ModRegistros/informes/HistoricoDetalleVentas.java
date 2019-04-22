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
public class HistoricoDetalleVentas extends javax.swing.JDialog {

    private DecimalFormat decimal = new DecimalFormat("#,##0.00");
    private int codEmpresa;
    private int codLocal;
    private String periodo;
    private static DefaultTableModel dtmDetallesVentas;
    
    
    
    public HistoricoDetalleVentas(java.awt.Frame parent, boolean modal, int codEmpresa, int codLocal, String periodo, long codArticulo, String descripcion) {
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
                DefaultTableModel dtm = (DefaultTableModel)this.jTDetallesVentas.getModel();
                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        // local | fec doc | tipo | nro doc | caja | cliente | cantidad | sigla | um | costo iva incl | margen | iva | monto iva | monto venta | vendedor
                        dtm.addRow(new Object[]{sm.TheResultSet.getInt("cod_local"), sm.TheResultSet.getString("fec_comprob"), 
                                                sm.TheResultSet.getString("tip_comprob"), sm.TheResultSet.getInt("nro_comprob"), sm.TheResultSet.getInt("cod_caja"),
                                                sm.TheResultSet.getString("nom_cliente"), sm.TheResultSet.getFloat("can_venta"), 
                                                sm.TheResultSet.getString("sigla_venta"), sm.TheResultSet.getInt("cansi_venta"), sm.TheResultSet.getDouble("mon_costo"),
                                                sm.TheResultSet.getDouble("mon_margen"), sm.TheResultSet.getInt("pct_iva"), sm.TheResultSet.getDouble("mon_iva"), 
                                                sm.TheResultSet.getDouble("mon_venta"), sm.TheResultSet.getString("vendedor")});
                    }
                    this.jTDetallesVentas.setModel(dtm);
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
        dtmDetallesVentas = (DefaultTableModel)jTDetallesVentas.getModel();
        
        jTDetallesVentas.getColumnModel().getColumn(0).setPreferredWidth(30); // local
        jTDetallesVentas.getColumnModel().getColumn(1).setPreferredWidth(80); // fec doc 
        jTDetallesVentas.getColumnModel().getColumn(2).setPreferredWidth(30); // tipo 
        jTDetallesVentas.getColumnModel().getColumn(3).setPreferredWidth(70); // nro doc 
        jTDetallesVentas.getColumnModel().getColumn(4).setPreferredWidth(30); // caja
        jTDetallesVentas.getColumnModel().getColumn(5).setPreferredWidth(100); // cliente
        jTDetallesVentas.getColumnModel().getColumn(6).setPreferredWidth(40); // cantidad 
        jTDetallesVentas.getColumnModel().getColumn(7).setPreferredWidth(30); // sigla
        jTDetallesVentas.getColumnModel().getColumn(8).setPreferredWidth(20); // um
        jTDetallesVentas.getColumnModel().getColumn(9).setPreferredWidth(80); // costo iva inc
        jTDetallesVentas.getColumnModel().getColumn(10).setPreferredWidth(60); // margen 
        jTDetallesVentas.getColumnModel().getColumn(11).setPreferredWidth(30); // iva pct
        jTDetallesVentas.getColumnModel().getColumn(12).setPreferredWidth(80); // monto iva 
        jTDetallesVentas.getColumnModel().getColumn(13).setPreferredWidth(80); // monto venta
        jTDetallesVentas.getColumnModel().getColumn(14).setPreferredWidth(50); // vendedor
        
        
        utiles.Utiles.punteroTablaF(jTDetallesVentas, this);        
        jTDetallesVentas.setFont(new Font("Tahoma", 1, 12) );
        jTDetallesVentas.setRowHeight(20);
    }
    
    private void limpiarGrilla(){
        DefaultTableModel dtm = (DefaultTableModel)this.jTDetallesVentas.getModel();
        while(dtm.getRowCount() > 0){
            dtm.removeRow(0);
        }
        this.jTDetallesVentas.setModel(dtm);
    }
    
    private String getSql(){
        String sql = "SELECT vd.cod_empresa, vd.cod_local, vd.cod_sector, TO_CHAR(vd.fec_comprob,'DD/MM/YYYY') AS fec_comprob,  vd.cod_caja, "
                   + "vd.tip_comprob, vd.nro_comprob, vc.cod_cliente, vc.nom_cliente, vd.can_venta, vd.sigla_venta,  vd.cansi_venta, vd.mon_costo, vd.mon_margen, "
                   + "vd.pct_iva, vd.mon_iva, vd.mon_venta, "
                   + "CASE WHEN vc.cod_vendedor != 0 THEN    (vc.cod_vendedor || ' ' || e.nombre || ' ' || e.apellido)  ELSE    (vc.cod_cajero || ' ' || usr.nombre)  END AS vendedor "
                   + "FROM venta_det vd  INNER JOIN venta_cab vc ON vd.cod_empresa = vc.cod_empresa "
                   + "AND vd.cod_local = vc.cod_local "
                   + "AND vd.cod_caja = vc.cod_caja "
                   + "AND vd.nro_timbrado = vc.nro_timbrado "
                   + "AND vd.nro_ticket = vc.nro_ticket "
                   + "AND vd.tip_comprob = vc.tip_comprob "
                   + "AND vd.estado = vc.estado "
                   + "INNER JOIN usuario usr ON usr.cod_usuario = vc.cod_cajero "
                   + "LEFT OUTER JOIN empleado e ON e.cod_empleado = vc.cod_vendedor "
                   + "AND vc.cod_vendedor != 0  "
                   + "WHERE vd.cod_empresa = " + this.codEmpresa + " "
                   + "AND vd.cod_local = " + this.codLocal + " "
                   + "AND vd.cod_articulo = " + jTFCodArticulo.getText() + " "
                   + "AND TO_CHAR(vd.fec_comprob,'MM/YYYY') = '" + this.periodo + "' "
                   + "AND vd.estado ='V' "
                   + "ORDER BY vd.fec_comprob DESC, vd.nro_comprob, vd.cod_empresa, vd.cod_local, vd.cod_sector";
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
        jTDetallesVentas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Histórico de Ventas");

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

        jTDetallesVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Fecha Doc", "Tipo", "Nro. Doc", "Caja", "Cliente", "Cantidad", "Sigla", "U/M", "Costo IVA Inc", "Margen", "IVA %", "Monto IVA", "Monto Venta", "Vendedor/Cajero"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTDetallesVentas);
        if (jTDetallesVentas.getColumnModel().getColumnCount() > 0) {
            jTDetallesVentas.getColumnModel().getColumn(0).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(1).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(2).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(3).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(4).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(5).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(6).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(7).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(8).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(9).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(10).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(11).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(12).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(13).setResizable(false);
            jTDetallesVentas.getColumnModel().getColumn(14).setResizable(false);
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
            java.util.logging.Logger.getLogger(HistoricoDetalleVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoricoDetalleVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
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
    private javax.swing.JTable jTDetallesVentas;
    private javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFDescArticulo;
    // End of variables declaration//GEN-END:variables
}
