/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.busca;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.StatementManager;

/**
 *
 * @author Andres
 */
public class BuscaCobroCliente extends javax.swing.JDialog {

    public static DefaultTableModel dtmPagos; 
    private static JTextField jText;
    
    public BuscaCobroCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodCliente.addFocusListener(new Focus());
        jTFCodCliente.setText("0");
        jTFCodCliente.grabFocus();
        configTabla();
        llenarCombosLabels();
    }
    
    private void getDatosCliente(String codigo){
        String razonsoc = "", codcuenta = "";
        try{
            String sql = "SELECT cliente.razon_soc, cuenta.cod_cuenta, cliente.ruc_cliente "
                       + "FROM cliente "
                       + "LEFT OUTER JOIN cuenta "
                       + "ON cliente.cod_cliente = cuenta.cod_cliente "
                       + "WHERE cliente.cod_cliente = " + codigo; 
            System.out.println("SQL DATOS CLIENTE: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreCliente.setText(rs.getString("razon_soc"));
                    jTFCodCuentaCliente.setText(rs.getString("cod_cuenta"));
                    jTFRucCliente.setText(rs.getString("ruc_cliente"));
                    
                    razonsoc = rs.getString("razon_soc");
                    codcuenta = rs.getString("cod_cuenta");
                    System.out.println("RAZON - SOC: " + razonsoc + "\nCOD - CUENTA: " + codcuenta);
                }
            }else{
                jLNombreCliente.setText("INEXISTENTE");
                jTFCodCuentaCliente.setText("0");
                jTFRucCliente.setText("0");
                jTFCodCliente.grabFocus();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void limpiarTabla(){
        for(int i = 0; i < jTDetallePago.getRowCount(); i++){
            dtmPagos.removeRow(i);
            i--;
        }
    }
    
    private void llenarTabla(){
        StatementManager sm = new StatementManager();
        String codCliente = jTFCodCliente.getText().trim();
        
        String sql = "SELECT pago.cod_local, pago.nro_pago, pago.nro_recibo, to_char(pago.fec_pago, 'dd/MM/yyyy') as fec_pago, pago.cod_cliente, "
                   + "cliente.razon_soc, pago.monto_pago, pago.cod_caja, pago.nro_turno, to_char(pago.fec_vigencia, 'dd/MM/yyyy hh:mm:ss') as fec_vigencia, pago.estado "
                   + "FROM pagocli_cab pago "
                   + "INNER JOIN cliente "
                   + "ON pago.cod_cliente = cliente.cod_cliente "
                   + "WHERE pago.cod_cliente = " + codCliente + " "
                   + "ORDER BY pago.nro_pago";
        
        try{
            sm.TheSql = sql;
            System.out.println("SQL PAGOS - CLIENTE: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm.TheResultSet.getString("cod_local");
                    row[1] = sm.TheResultSet.getInt("nro_pago");
                    row[2] = sm.TheResultSet.getInt("nro_recibo");
                    row[3] = sm.TheResultSet.getString("fec_pago");
                    row[4] = sm.TheResultSet.getInt("cod_cliente");
                    row[5] = sm.TheResultSet.getString("razon_soc");
                    row[6] = sm.TheResultSet.getInt("cod_caja");
                    row[7] = sm.TheResultSet.getInt("nro_turno");
                    row[8] = sm.TheResultSet.getDouble("monto_pago");
                    row[9] = sm.TheResultSet.getString("fec_vigencia");
                    row[10] = sm.TheResultSet.getString("estado");
                    dtmPagos.addRow(row);
                }
                jTDetallePago.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: El cliente no posee pagos registrados!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void configTabla(){
        dtmPagos = (DefaultTableModel)jTDetallePago.getModel();
        
        jTDetallePago.getColumnModel().getColumn(0).setPreferredWidth(15); // local
        jTDetallePago.getColumnModel().getColumn(1).setPreferredWidth(20); // nro pago 
        jTDetallePago.getColumnModel().getColumn(2).setPreferredWidth(20); // nro recibo
        jTDetallePago.getColumnModel().getColumn(3).setPreferredWidth(20); // fec pago
        jTDetallePago.getColumnModel().getColumn(4).setPreferredWidth(15); // cod cliente 
        jTDetallePago.getColumnModel().getColumn(5).setPreferredWidth(80); // nombre cliente 
        jTDetallePago.getColumnModel().getColumn(6).setPreferredWidth(15); // caja
        jTDetallePago.getColumnModel().getColumn(7).setPreferredWidth(15); // nro turno 
        jTDetallePago.getColumnModel().getColumn(8).setPreferredWidth(40); // monto pago 
        jTDetallePago.getColumnModel().getColumn(9).setPreferredWidth(80); // fec operacion
        jTDetallePago.getColumnModel().getColumn(10).setPreferredWidth(15); // estado
        
        utiles.Utiles.punteroTablaF(jTDetallePago, this);        
        jTDetallePago.setFont(new Font("Tahoma", 1, 11) );
        jTDetallePago.setRowHeight(20);
    }
    
    public static void setText(JTextField jTf)
    {
        jText = jTf;
    }
    
    private void llenarCombosLabels(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
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
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreLocal = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jTFCodCuentaCliente = new javax.swing.JTextField();
        jTFRucCliente = new javax.swing.JTextField();
        jBBuscarPagos = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallePago = new javax.swing.JTable();
        jBSeleccionar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Búsqueda de Cobro de Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Cliente:");

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

        jTFCodCuentaCliente.setEditable(false);
        jTFCodCuentaCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCuentaCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCuentaCliente.setText("0");

        jTFRucCliente.setEditable(false);
        jTFRucCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFRucCliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFRucCliente.setText("0");

        jBBuscarPagos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBBuscarPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscarPagos.setText("Buscar Pagos");
        jBBuscarPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarPagosActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Pagos realizados"));

        jTDetallePago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Nro Pago", "Nro Recibo", "Fec Pago", "Cod Cliente", "Nombre Cliente", "Caja", "Nro Turno", "Monto Pago", "Fec Operación", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetallePago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTDetallePagoMouseClicked(evt);
            }
        });
        jTDetallePago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallePagoKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetallePago);
        if (jTDetallePago.getColumnModel().getColumnCount() > 0) {
            jTDetallePago.getColumnModel().getColumn(0).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(1).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(2).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(3).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(4).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(5).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(6).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(7).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(8).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(9).setResizable(false);
            jTDetallePago.getColumnModel().getColumn(10).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addContainerGap())
        );

        jBSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBSeleccionar.setText("Seleccionar");
        jBSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSeleccionarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar y Salir");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBBuscarPagos, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSeleccionar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLNombreCliente))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBBuscarPagos)))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSeleccionar)
                    .addComponent(jBCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBSeleccionar});

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

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

    private void jTFCodClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            BuscaCliente bCliente = new BuscaCliente(new JFrame(), true);
            bCliente.pack();
            bCliente.setVisible(true);
            if(bCliente.codigo != 0){
                jTFCodCliente.setText(String.valueOf(bCliente.codigo));
            }
        }
        
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
                jBBuscarPagos.grabFocus();
            }
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jBBuscarPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarPagosActionPerformed
       limpiarTabla();
       llenarTabla();
    }//GEN-LAST:event_jBBuscarPagosActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTDetallePagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallePagoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jText.setText(jTDetallePago.getValueAt(jTDetallePago.getSelectedRow(), 1).toString());
            dispose();
        }else{
            jText.setText(jText.getText());
            dispose();
        }
    }//GEN-LAST:event_jTDetallePagoKeyPressed

    private void jTDetallePagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTDetallePagoMouseClicked
        if(evt.getClickCount() == 2){
            if(jTDetallePago.getValueAt(0, 0) == null)
            {
                dispose();
            }else
            {
                jText.setText(String.valueOf(jTDetallePago.getValueAt(jTDetallePago.getSelectedRow(), 1)));
                dispose();
            }
        }
    }//GEN-LAST:event_jTDetallePagoMouseClicked

    private void jBSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSeleccionarActionPerformed
        jText.setText(String.valueOf(jTDetallePago.getValueAt(jTDetallePago.getSelectedRow(), 1)));
        dispose();
    }//GEN-LAST:event_jBSeleccionarActionPerformed

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
            java.util.logging.Logger.getLogger(BuscaCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscaCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscaCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscaCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscaCobroCliente dialog = new BuscaCobroCliente(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBBuscarPagos;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBSeleccionar;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTDetallePago;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCuentaCliente;
    private javax.swing.JTextField jTFRucCliente;
    // End of variables declaration//GEN-END:variables
}
