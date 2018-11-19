/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero.informes;

import TableUtiles.DateCellRender;
import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.ButtonDetalleDocCuentaCliente;
import utiles.DBManager;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class DetalleCuentaCliente extends javax.swing.JDialog {

    private static DefaultTableModel dtmDetalles;
    private int codigoCliente = 0;
    private double interesForm = 0;
    
    public DetalleCuentaCliente(java.awt.Frame parent, boolean modal, String cliente, int codCliente, double interes) {
        super(parent, modal);
        initComponents();
        this.setTitle("ATOMSystems|Main - Detalle de cuenta del cliente: " + cliente);
        codigoCliente = codCliente;
        interesForm = interes;
        configTableSaldoDetallado();
        cargarTabla();
    }

    private void cargarTabla(){
        limpiarTablaDetallado();
        llenarTablaDetallado();
        calculoInteresTablaDetallado();
    }
    
    private void configTableSaldoDetallado(){
        dtmDetalles = (DefaultTableModel)jTDetalles.getModel();
        
        jTDetalles.getColumnModel().getColumn(0).setPreferredWidth(20); // local (int) 
        jTDetalles.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(1).setPreferredWidth(20); // caja (int)  
        jTDetalles.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(2).setPreferredWidth(50); // cód cliente (int) 
        jTDetalles.getColumnModel().getColumn(2).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(3).setPreferredWidth(150); // cliente (string) 
        jTDetalles.getColumnModel().getColumn(3).setCellRenderer(new StringCellRender());
        jTDetalles.getColumnModel().getColumn(4).setPreferredWidth(50); // nro doc (int)
        jTDetalles.getColumnModel().getColumn(4).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(5).setPreferredWidth(40); // tipo doc (string)
        jTDetalles.getColumnModel().getColumn(5).setCellRenderer(new StringCellRender());
        jTDetalles.getColumnModel().getColumn(6).setPreferredWidth(60); // fec. doc (string)
        jTDetalles.getColumnModel().getColumn(6).setCellRenderer(new DateCellRender());
        jTDetalles.getColumnModel().getColumn(7).setPreferredWidth(40); // cuota (String)
        jTDetalles.getColumnModel().getColumn(7).setCellRenderer(new StringCellRender());
        jTDetalles.getColumnModel().getColumn(8).setPreferredWidth(60); // vencimiento (string)
        jTDetalles.getColumnModel().getColumn(8).setCellRenderer(new DateCellRender());
        jTDetalles.getColumnModel().getColumn(9).setPreferredWidth(30); // días vencidos (int)
        jTDetalles.getColumnModel().getColumn(9).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(10).setPreferredWidth(60); // fec. pago (string)
        jTDetalles.getColumnModel().getColumn(10).setCellRenderer(new DateCellRender());
        jTDetalles.getColumnModel().getColumn(11).setPreferredWidth(40); // nro. pago (int)
        jTDetalles.getColumnModel().getColumn(11).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(12).setPreferredWidth(40); // nro. recibo (int)
        jTDetalles.getColumnModel().getColumn(12).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(13).setPreferredWidth(80); // valor doc (double)
        jTDetalles.getColumnModel().getColumn(13).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(14).setPreferredWidth(90); // total + interés (double)
        jTDetalles.getColumnModel().getColumn(14).setCellRenderer(new DecimalCellRender(0));
        
        // implementacion del button - detalle
        ButtonDetalleDocCuentaCliente button = new ButtonDetalleDocCuentaCliente(jTDetalles, 15);
        jTDetalles.getColumnModel().getColumn(15).setPreferredWidth(50); // detalles
        
        utiles.Utiles.punteroTablaF(jTDetalles, this);        
        jTDetalles.setFont(new Font("Tahoma", 1, 12) );
        jTDetalles.setRowHeight(20);
    }
    
    private void limpiarTablaDetallado(){
        for(int i = 0; i < jTDetalles.getRowCount(); i++){
            dtmDetalles.removeRow(i);
            i--;
        }
    }
    
    private void llenarTablaDetallado(){
        StatementManager sm = new StatementManager();
        
        
        String sql = 
                    "SELECT DISTINCT v.cod_local, v.cod_caja, v.cod_cliente, c.razon_soc, v.nro_comprob, v.tip_comprob, "
                  + "to_char(v.fec_comprob, 'dd/MM/yyyy') AS fec_documento, "
                  + "v.nro_cuota || '/' || v.can_cuota AS cuota, to_char(v.fec_vencimiento , 'dd/MM/yyyy') AS fec_vencimiento, "
                  + "to_char(v.fec_recibo, 'dd/MM/yyyy') AS fec_pago, v.nro_pago, v.nro_recibo, v.monto_cuota, "
                  + "CASE WHEN current_date - fec_vencimiento::date <= 0 "
                  + "THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos "
                  + "FROM venta_det_cuotas v "
                  + "INNER JOIN cliente c "
                  + "ON v.cod_cliente = c.cod_cliente "
                  + "WHERE v.nro_pago = 0 "
                  + "AND v.estado = 'V' "
                  + "AND v.cod_cliente = " + codigoCliente + " "
                  + "ORDER BY fec_documento desc ";
        
        try{
            sm.TheSql = sql;
            System.out.println("SQL DOCS PENDIENTES X CLIENTE: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[16];
                    row[0] = sm.TheResultSet.getInt("cod_local");
                    row[1] = sm.TheResultSet.getInt("cod_caja");
                    row[2] = sm.TheResultSet.getInt("cod_cliente");
                    row[3] = sm.TheResultSet.getString("razon_soc");
                    row[4] = sm.TheResultSet.getInt("nro_comprob");
                    row[5] = sm.TheResultSet.getString("tip_comprob");
                    row[6] = sm.TheResultSet.getString("fec_documento");
                    row[7] = sm.TheResultSet.getString("cuota");
                    row[8] = sm.TheResultSet.getString("fec_vencimiento");
                    row[9] = sm.TheResultSet.getInt("dias_vencidos");
                    row[10] = sm.TheResultSet.getString("fec_pago");
                    row[11] = sm.TheResultSet.getInt("nro_pago");
                    row[12] = sm.TheResultSet.getInt("nro_recibo");
                    row[13] = sm.TheResultSet.getDouble("monto_cuota");
                    row[14] = 0;
                    
                    dtmDetalles.addRow(row);
                }
                jTDetalles.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No exiten detalles !", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }        
    }
    
    private void calculoInteresTablaDetallado(){
        double dias_vencidos = 0, monto_cuota = 0, interes, total = 0;
        String tipo_documento = "";
        for(int i = 0;i < jTDetalles.getRowCount(); i++){
            tipo_documento = jTDetalles.getValueAt(i, 5).toString().trim();
            monto_cuota = Double.parseDouble(jTDetalles.getValueAt(i, 13).toString());
            interes = 0;
            if(tipo_documento.equalsIgnoreCase("CRE") || tipo_documento.equalsIgnoreCase("NCC")){
            }else{
                dias_vencidos = Double.parseDouble(jTDetalles.getValueAt(i, 9).toString());
                interes = Math.round(((monto_cuota * interesForm)/100) * (dias_vencidos/30));
            }
            total = monto_cuota + interes;
            jTDetalles.setValueAt(total, i, 14);
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

        bGFiltro = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalles = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jRBPendientes = new javax.swing.JRadioButton();
        jRBPagados = new javax.swing.JRadioButton();
        jRBTodos = new javax.swing.JRadioButton();
        jBRefresh = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jTDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Caja", "Código", "Cliente", "Nro. Doc. ", "Tipo", "Fec. Doc. ", "Cuota", "Vencimiento", "Días venc.", "Fec. Pago", "Pago", "Recibo", "Valor Doc. ", "Total + Interés", "Detalles"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTDetalles);
        if (jTDetalles.getColumnModel().getColumnCount() > 0) {
            jTDetalles.getColumnModel().getColumn(0).setResizable(false);
            jTDetalles.getColumnModel().getColumn(1).setResizable(false);
            jTDetalles.getColumnModel().getColumn(2).setResizable(false);
            jTDetalles.getColumnModel().getColumn(3).setResizable(false);
            jTDetalles.getColumnModel().getColumn(4).setResizable(false);
            jTDetalles.getColumnModel().getColumn(5).setResizable(false);
            jTDetalles.getColumnModel().getColumn(6).setResizable(false);
            jTDetalles.getColumnModel().getColumn(7).setResizable(false);
            jTDetalles.getColumnModel().getColumn(8).setResizable(false);
            jTDetalles.getColumnModel().getColumn(9).setResizable(false);
            jTDetalles.getColumnModel().getColumn(10).setResizable(false);
            jTDetalles.getColumnModel().getColumn(11).setResizable(false);
            jTDetalles.getColumnModel().getColumn(12).setResizable(false);
            jTDetalles.getColumnModel().getColumn(13).setResizable(false);
            jTDetalles.getColumnModel().getColumn(14).setResizable(false);
            jTDetalles.getColumnModel().getColumn(15).setResizable(false);
        }

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBPendientes.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBPendientes);
        jRBPendientes.setSelected(true);
        jRBPendientes.setText("Pendientes");

        jRBPagados.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBPagados);
        jRBPagados.setText("Pagados");

        jRBTodos.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBTodos);
        jRBTodos.setText("Todos");

        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBPendientes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBPagados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBTodos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jBRefresh)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBPendientes)
                    .addComponent(jRBPagados)
                    .addComponent(jRBTodos)
                    .addComponent(jBRefresh))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 495, Short.MAX_VALUE)
                        .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSalir)))
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
            java.util.logging.Logger.getLogger(DetalleCuentaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetalleCuentaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetalleCuentaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetalleCuentaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DetalleCuentaCliente dialog = new DetalleCuentaCliente(new javax.swing.JFrame(), true, "", 0, 0);
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
    private javax.swing.ButtonGroup bGFiltro;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JButton jBSalir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRBPagados;
    private javax.swing.JRadioButton jRBPendientes;
    private javax.swing.JRadioButton jRBTodos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetalles;
    // End of variables declaration//GEN-END:variables
}
