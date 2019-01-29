/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableAnulacionCobroClientes;
import views.busca.BuscaCliente;

/**
 *
 * @author ANDRES
 */
public class AnulacionCobroCliente extends javax.swing.JDialog {

    private static DefaultTableModel dtmPagos; 
    TableAnulacionCobroClientes tableAnulacionModel;
    
    public AnulacionCobroCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configCampos();
        llenarCombosLabels();
    }

    private void configTabla(){
        jTCobrosDelCliente.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTCobrosDelCliente.getColumnModel().getColumn(0).setPreferredWidth(20); // nro pago
        jTCobrosDelCliente.getColumnModel().getColumn(1).setPreferredWidth(20); // nro recibo
        jTCobrosDelCliente.getColumnModel().getColumn(2).setPreferredWidth(20); // fec pago
        jTCobrosDelCliente.getColumnModel().getColumn(3).setPreferredWidth(15); // cod cliente
        jTCobrosDelCliente.getColumnModel().getColumn(4).setPreferredWidth(80); // nombre cliente
        jTCobrosDelCliente.getColumnModel().getColumn(5).setPreferredWidth(40); // monto pago
        jTCobrosDelCliente.getColumnModel().getColumn(6).setPreferredWidth(80); // fec operacion 
        jTCobrosDelCliente.getColumnModel().getColumn(7).setPreferredWidth(15); // estado
        jTCobrosDelCliente.getColumnModel().getColumn(8).setPreferredWidth(15); // anular?
        
        utiles.Utiles.punteroTablaF(jTCobrosDelCliente, this);        
        jTCobrosDelCliente.setFont(new Font("Tahoma", 1, 11) );
        jTCobrosDelCliente.setRowHeight(20);
    }
    
    private void updateTabla(){
        String codCliente = jTFCodCliente.getText().trim();
        ResultSet rs = null;
        
        String sql = "SELECT pago.nro_pago, pago.nro_recibo, to_char(pago.fec_pago, 'dd/MM/yyyy') as fec_pago, pago.cod_cliente, cliente.razon_soc, "
                   + "pago.monto_pago, to_char(pago.fec_vigencia, 'dd/MM/yyyy hh:mm:ss') as fec_vigencia, pago.estado "
                   + "FROM pagocli_cab pago "
                   + "INNER JOIN cliente "
                   + "ON pago.cod_cliente = cliente.cod_cliente "
                   + "WHERE pago.cod_cliente = " + codCliente + " "
                   + "ORDER BY pago.nro_pago";
        
        System.out.println("BUSQ. COBROS DEL CLIENTE: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        tableAnulacionModel = new TableAnulacionCobroClientes(rs);
        jTCobrosDelCliente.setModel(tableAnulacionModel);
        configTabla();
    }
    
    private void updateTablaNroPago(){
        String nroPago = jTFNroPago.getText().trim();
        ResultSet rs = null;
        
        String sql = "SELECT pago.nro_pago, pago.nro_recibo, to_char(pago.fec_pago, 'dd/MM/yyyy') as fec_pago, pago.cod_cliente, cliente.razon_soc, "
                   + "pago.monto_pago, to_char(pago.fec_vigencia, 'dd/MM/yyyy hh:mm:ss') as fec_vigencia, pago.estado "
                   + "FROM pagocli_cab pago "
                   + "INNER JOIN cliente "
                   + "ON pago.cod_cliente = cliente.cod_cliente "
                   + "WHERE pago.nro_pago = " + nroPago;
        
        System.out.println("BUSQ. X NRO DE PAGO: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        tableAnulacionModel = new TableAnulacionCobroClientes(rs);
        jTCobrosDelCliente.setModel(tableAnulacionModel);
        configTabla();
    }
    
    private void limpiarTabla(){
        for(int i = 0; i < jTCobrosDelCliente.getRowCount(); i++){
            dtmPagos.removeRow(i);
            i--;
        }
    }
    
    private void configCampos(){
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFNroPago.setDocument(new MaxLength(12, "", "ENTERO"));
        
        jTFCodCliente.addFocusListener(new Focus());
        jTFNroPago.addFocusListener(new Focus());
        jTFCodCliente.setText("0");
        jTFNroPago.setText("0");
        jTFCodCliente.grabFocus();
    }
    
    private void llenarCombosLabels(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
    }
    
    private void getDatosCliente(String codigo){
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
                }else{
                    jLNombreCliente.setText("INEXISTENTE");
                    jTFCodCliente.requestFocus();
                }
            }else{
                jLNombreCliente.setText("INEXISTENTE");
                jTFCodCliente.requestFocus();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void anularCobro(){
        String sql = "";
        boolean result = false;
        int cantFilas = jTCobrosDelCliente.getRowCount();
        for(int i = 0; i < cantFilas; i++){
            boolean estado = (Boolean)tableAnulacionModel.getValueAt(i,8);
            String estado_cobro = (String)tableAnulacionModel.getValueAt(i,7).toString();
            System.out.println("ESTADO DE LA SELECCION: " + estado);
            if(estado){
                if(estado_cobro.equals("A")){
                    JOptionPane.showMessageDialog(this, "Cobro ya anulado!", "ATENCIÓN", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }else{
                    String nroPago = jTCobrosDelCliente.getValueAt(i, 0).toString();
                    String codCliente = jTCobrosDelCliente.getValueAt(i, 3).toString();
                    sql =     "BEGIN; "
                            + "UPDATE pagocli_cab SET estado = 'A', fec_vigencia = 'now()' WHERE nro_pago = " + nroPago + " AND cod_cliente = " + codCliente + "; "
                            + "UPDATE pagocli_det SET estado = 'A', fec_vigencia = 'now()' WHERE nro_pago = " + nroPago + " AND cod_cliente = " + codCliente + "; "
                            + "UPDATE venta_det_cuotas SET estado = 'A', fec_vigencia = 'now()' WHERE nro_comprob = " + nroPago + " AND cod_caja = 99 AND cod_cliente = " + codCliente + "; "
                            + "UPDATE venta_det_cuotas SET nro_pago = 0, nro_recibo = 0, fec_recibo = null, fec_vigencia = 'now()' WHERE nro_pago = " + nroPago + " AND cod_cliente = "
                            + codCliente + "; "
                            + "COMMIT;";
                    System.out.println("SQL ANULACION DE COBRO DE CLIENTE: " + sql);
                    int result_operacion = DBManager.ejecutarDML(sql);
                    System.out.println("RESULTADO DE LA OPERACION: " + result_operacion);
                    if(result_operacion == 0){
                        try{
                            DBManager.conn.commit();
                            cantFilas = cantFilas -1;
                            i = i -1;
                            result = true;
                        }catch(Exception ex){
                            ex.printStackTrace();
                            result = false;
                        }
                    }else{
                        System.out.println("ERROR EN EL COMMIT DE LA ANULACION DE COBRO");
                        result = false;
                        jTCobrosDelCliente.changeSelection(i,0,false, false);
                        break;
                    }
                }
            }
        }
        
        if(result){
            JOptionPane.showMessageDialog(this, "Anulación EXITOSA...", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            updateTabla();
            configTabla();
            //jBBuscar.setEnabled(true);
            //jBGrabar.setEnabled(false);
            jTCobrosDelCliente.clearSelection();
        }else{
            //JOptionPane.showMessageDialog(this, "Debe seleccionar el registro a anular!", "Aviso", JOptionPane.WARNING_MESSAGE);
            jBBuscarPago.doClick();
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
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreLocal = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTFNroPago = new javax.swing.JTextField();
        jBBuscarPago = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTCobrosDelCliente = new javax.swing.JTable();
        jBAnular = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Anulación de Cobro de Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("ANULACION DE COBRO DE CLIENTES");

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
        jLabel1.setText("Empresa:");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Cliente:");

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

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Nro. Pago:");

        jTFNroPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroPago.setText("0");
        jTFNroPago.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroPagoFocusGained(evt);
            }
        });
        jTFNroPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroPagoKeyPressed(evt);
            }
        });

        jBBuscarPago.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBBuscarPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscarPago.setText("Buscar Pago");
        jBBuscarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarPagoActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pagos del cliente"));

        jTCobrosDelCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTCobrosDelCliente);

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
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jBAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eliminar24.png"))); // NOI18N
        jBAnular.setText("Anular");
        jBAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAnularActionPerformed(evt);
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
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(161, 161, 161)
                        .addComponent(jBBuscarPago, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBAnular)
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
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente)
                    .addComponent(jLabel5)
                    .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscarPago))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBAnular)
                    .addComponent(jBCancelar))
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

    private void jBBuscarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarPagoActionPerformed
        if(jTFCodCliente.getText().equals("0") || jTFCodCliente.getText().equals("") || jLNombreCliente.getText().equals("INEXISTENTE")){
        }else{
            updateTabla();
        }
        
        if(!jTFNroPago.getText().equals("0") && !jTFNroPago.getText().equals("")){
            updateTablaNroPago();
        }
    }//GEN-LAST:event_jBBuscarPagoActionPerformed

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
                jTFNroPago.grabFocus();
            }                                                
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFNroPagoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroPagoFocusGained
        jTFNroPago.selectAll();
    }//GEN-LAST:event_jTFNroPagoFocusGained

    private void jTFNroPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroPagoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscarPago.grabFocus();
        }
    }//GEN-LAST:event_jTFNroPagoKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAnularActionPerformed
        if(jTCobrosDelCliente.getRowCount() > 0){
            anularCobro();
        }
    }//GEN-LAST:event_jBAnularActionPerformed

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
            java.util.logging.Logger.getLogger(AnulacionCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnulacionCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnulacionCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnulacionCobroCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnulacionCobroCliente dialog = new AnulacionCobroCliente(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBAnular;
    private javax.swing.JButton jBBuscarPago;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTCobrosDelCliente;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFNroPago;
    // End of variables declaration//GEN-END:variables
}
