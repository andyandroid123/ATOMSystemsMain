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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.ButtonDetalleCuentaCliente;
import utiles.ButtonHistoricoCompraVenta;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class HistoricoComprasVentasArticulo extends javax.swing.JDialog {

    private static DefaultTableModel dtmHisCompras, dtmHisVentas;
    
    public HistoricoComprasVentasArticulo(java.awt.Frame parent, long codArticulo, boolean modal) {
        super(parent, modal);
        initComponents();
        getDescArt(codArticulo);
        cargaCombos();
        jTFCodArticulo.setText(String.valueOf(codArticulo));
        jTFNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jTFNombreSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
        configTablaHistoricoCompras();
        configTablaHistoricoVentas();
        jBProcesar.doClick();
    }

    private void configTablaHistoricoCompras(){
        dtmHisCompras = (DefaultTableModel)jTHisCompras.getModel();
        
        // local | periodo | cantidad | Total compra | + detalles
        
        jTHisCompras.getColumnModel().getColumn(0).setPreferredWidth(20); // local
        jTHisCompras.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTHisCompras.getColumnModel().getColumn(1).setPreferredWidth(20); // periodo
        jTHisCompras.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTHisCompras.getColumnModel().getColumn(2).setPreferredWidth(50); // cantidad
        jTHisCompras.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(2));
        jTHisCompras.getColumnModel().getColumn(3).setPreferredWidth(50); // total compra
        jTHisCompras.getColumnModel().getColumn(3).setCellRenderer(new DecimalCellRender(0));
        jTHisCompras.getColumnModel().getColumn(4).setPreferredWidth(15);
        
        // implementacion del button - detalle
        ButtonHistoricoCompraVenta button = new ButtonHistoricoCompraVenta(jTHisCompras, 4, 1);
        
        utiles.Utiles.punteroTablaF(jTHisCompras, this);        
        jTHisCompras.setFont(new Font("Tahoma", 1, 12) );
        jTHisCompras.setRowHeight(20);
    }
    
    private void configTablaHistoricoVentas(){
        dtmHisVentas = (DefaultTableModel)jTHisVentas.getModel();
        
        // local | periodo | cantidad | total venta | + detalles
        
        jTHisVentas.getColumnModel().getColumn(0).setPreferredWidth(20); // local
        jTHisVentas.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTHisVentas.getColumnModel().getColumn(1).setPreferredWidth(20); // periodo
        jTHisVentas.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTHisVentas.getColumnModel().getColumn(2).setPreferredWidth(50); // cantidad
        jTHisVentas.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(2));
        jTHisVentas.getColumnModel().getColumn(3).setPreferredWidth(50); // total venta
        jTHisVentas.getColumnModel().getColumn(3).setCellRenderer(new DecimalCellRender(0));
        jTHisVentas.getColumnModel().getColumn(4).setPreferredWidth(15);
        
        // implementacion del button - detalle
        ButtonHistoricoCompraVenta button = new ButtonHistoricoCompraVenta(jTHisVentas, 4, 2);
        utiles.Utiles.punteroTablaF(jTHisVentas, this);        
        jTHisVentas.setFont(new Font("Tahoma", 1, 12) );
        jTHisVentas.setRowHeight(20);
    }
    
    private void cargaCombos(){
        utiles.Utiles.cargaComboLocales(jCBCodLocal);
        utiles.Utiles.cargaComboSectores(jCBCodSector);
    }
    
    private void getDescArt(long codArt) {
        StatementManager sm = new StatementManager();
        try {
            sm.TheSql = "SELECT descripcion " +
                        "FROM articulo " +
                        "WHERE cod_articulo = " + codArt;
            sm.EjecutarSql();
            if (sm.TheResultSet.next()) {
                jTFDescripcionArticulo.setText(sm.TheResultSet.getString("descripcion"));
            } else {
                jTFDescripcionArticulo.setText("INEXISTENTE !!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            sm.CerrarStatement();
            sm = null;
        }
    }
    
    private void llenarTablaHisCompras(){
        if(utiles.Utiles.isNumeric(jTFCodArticulo.getText())){
            StatementManager sm = new StatementManager();
            try{
                String sql = "SELECT cd.cod_empresa, cd.cod_local, cd.cod_sector, TO_CHAR(cd.fec_comprob,'MM/YYYY') AS periodo,  TO_CHAR(cd.fec_comprob,'YYYY/MM') AS orden, "
                           + "SUM(cd.cant_recib*cd.cansi_compra*tp.afecta) AS can_compra,  SUM(cd.mon_total*tp.afecta) AS mon_compra, SUM(cd.costo_flete*tp.afecta) AS costo_flete "
                           + "FROM compra_det cd  "
                           + "INNER JOIN tipo_comprobante tp "
                           + "ON cd.tip_comprob = tp.tip_comprob "
                           + "WHERE cd.cod_empresa = " + utiles.Utiles.getCodEmpresaDefault() + " "
                           + "AND cd.cod_local = " + jCBCodLocal.getSelectedItem().toString() + " "
                           + "AND cd.cod_sector = " + jCBCodSector.getSelectedItem().toString() + " "
                           + "AND cd.cod_articulo = " + jTFCodArticulo.getText() + " "
                           + "AND cd.estado = 'V' "
                           + "GROUP BY cd.cod_empresa, cd.cod_local, cd.cod_sector, TO_CHAR(cd.fec_comprob,'MM/YYYY'), TO_CHAR(cd.fec_comprob,'YYYY/MM') "
                           + "ORDER BY TO_CHAR(cd.fec_comprob,'YYYY/MM') DESC, cd.cod_empresa, cd.cod_local, cd.cod_sector";
                System.out.println("HISTORICO DE COMPRAS: " + sql);
                sm.TheSql = sql;
                sm.EjecutarSql();
                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        Object[] row = new Object[4];
                        row[0] = sm.TheResultSet.getInt("cod_local");
                        row[1] = sm.TheResultSet.getString("periodo");
                        row[2] = sm.TheResultSet.getFloat("can_compra");
                        row[3] = sm.TheResultSet.getDouble("mon_compra");
                        dtmHisCompras.addRow(row);
                    }
                    jTHisCompras.updateUI();
                }
                
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "ATENCION: \n Error recuperando datos de compras!", "Error", 2);
            }finally{
                DBManager.CerrarStatements();
                sm = null;
            }
        }
    }
    
    private void llenarTablaHisVentas(){
        if(utiles.Utiles.isNumeric(jTFCodArticulo.getText())){
            StatementManager sm = new StatementManager();
            try{
                String sql = "SELECT vd.cod_empresa, vd.cod_local, vd.cod_sector, TO_CHAR(vd.fec_comprob,'MM/YYYY') AS periodo,  TO_CHAR(vd.fec_comprob,'YYYY/MM') AS orden, "
                           + "SUM(vd.can_venta*cansi_venta) AS can_venta, SUM(vd.mon_venta) AS mon_venta,  SUM(vd.mon_costo) AS mon_costo, SUM(vd.mon_iva) AS mon_iva, SUM(vd.mon_margen) AS mon_margen "
                           + "FROM venta_det vd "
                           + "WHERE vd.cod_empresa = " + utiles.Utiles.getCodEmpresaDefault() + " "
                           + "AND vd.cod_local = " + jCBCodLocal.getSelectedItem().toString() + " "
                           + "AND vd.cod_sector = " + jCBCodSector.getSelectedItem().toString() + " "
                           + "AND vd.cod_articulo = " + jTFCodArticulo.getText() + " "
                           + "AND vd.estado ='V' "
                           + "GROUP BY vd.cod_empresa, vd.cod_local, vd.cod_sector, TO_CHAR(vd.fec_comprob,'MM/YYYY'), TO_CHAR(vd.fec_comprob,'YYYY/MM') "
                           + "ORDER BY TO_CHAR(vd.fec_comprob,'YYYY/MM') DESC, vd.cod_empresa, vd.cod_local, vd.cod_sector";
                System.out.println("HISTORICO DE VENTAS: " + sql);
                sm.TheSql = sql;
                sm.EjecutarSql();
                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        Object[] row = new Object[4];
                        row[0] = sm.TheResultSet.getInt("cod_local");
                        row[1] = sm.TheResultSet.getString("periodo");
                        row[2] = sm.TheResultSet.getFloat("can_venta");
                        row[3] = sm.TheResultSet.getDouble("mon_venta");
                        dtmHisVentas.addRow(row);
                    }
                    jTHisVentas.updateUI();
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "ATENCION: \n Error recuperando datos de ventas!", "Error", 2);
            }finally{
                DBManager.CerrarStatements();
                sm = null;
            }
        }
    }
    
    private void limpiarGrillas(){
        DefaultTableModel dtm = (DefaultTableModel)this.jTHisCompras.getModel();
        while(dtm.getRowCount() > 0){
            dtm.removeRow(0);
        }
        this.jTHisCompras.setModel(dtm);
        
        DefaultTableModel dtmv = (DefaultTableModel)this.jTHisVentas.getModel();
        while(dtmv.getRowCount() > 0){
            dtmv.removeRow(0);
        }
        this.jTHisVentas.setModel(dtmv);
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
        jTFDescripcionArticulo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jTFNombreLocal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jCBCodSector = new javax.swing.JComboBox<>();
        jTFNombreSector = new javax.swing.JTextField();
        jBProcesar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTHisCompras = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTHisVentas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Histórico Compras/Ventas Artículo");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Artículo:");

        jTFCodArticulo.setEditable(false);
        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodArticulo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodArticulo.setText("0");

        jTFDescripcionArticulo.setEditable(false);
        jTFDescripcionArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Local:");

        jTFNombreLocal.setEditable(false);
        jTFNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Sector:");

        jTFNombreSector.setEditable(false);
        jTFNombreSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jBProcesar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBProcesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBProcesar.setText("Procesar");
        jBProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBProcesarActionPerformed(evt);
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
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Histórico de Compras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTHisCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Periodo", "Cantidad", "Total Compra", "+ Detalles"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTHisCompras);
        if (jTHisCompras.getColumnModel().getColumnCount() > 0) {
            jTHisCompras.getColumnModel().getColumn(0).setResizable(false);
            jTHisCompras.getColumnModel().getColumn(1).setResizable(false);
            jTHisCompras.getColumnModel().getColumn(2).setResizable(false);
            jTHisCompras.getColumnModel().getColumn(3).setResizable(false);
            jTHisCompras.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Histórico de Ventas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTHisVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Periodo", "Cantidad", "Total Venta", "+ Detalles"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTHisVentas);
        if (jTHisVentas.getColumnModel().getColumnCount() > 0) {
            jTHisVentas.getColumnModel().getColumn(0).setResizable(false);
            jTHisVentas.getColumnModel().getColumn(1).setResizable(false);
            jTHisVentas.getColumnModel().getColumn(2).setResizable(false);
            jTHisVentas.getColumnModel().getColumn(3).setResizable(false);
            jTHisVentas.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDescripcionArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNombreSector)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBProcesar, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFDescripcionArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTFNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTFNombreSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jBProcesar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBProcesarActionPerformed
        limpiarGrillas();
        llenarTablaHisCompras();
        llenarTablaHisVentas();
    }//GEN-LAST:event_jBProcesarActionPerformed

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
            java.util.logging.Logger.getLogger(HistoricoComprasVentasArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoricoComprasVentasArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoricoComprasVentasArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoricoComprasVentasArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HistoricoComprasVentasArticulo dialog = new HistoricoComprasVentasArticulo(new javax.swing.JFrame(), 0, true);
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
    private javax.swing.JButton jBProcesar;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTextField jTFCodArticulo;
    public static javax.swing.JTextField jTFDescripcionArticulo;
    private javax.swing.JTextField jTFNombreLocal;
    private javax.swing.JTextField jTFNombreSector;
    private javax.swing.JTable jTHisCompras;
    private javax.swing.JTable jTHisVentas;
    // End of variables declaration//GEN-END:variables
}
