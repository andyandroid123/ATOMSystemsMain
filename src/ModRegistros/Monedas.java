/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import ModRRHH.GenericInfoAuditoria;
import beans.MonedaBean;
import controls.MonedaCtrl;
import controls.UsuarioCtrl;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class Monedas extends javax.swing.JDialog {

    boolean esNuevo = false;
    private static String usuario = null;
    private static String fecha = null;
    
    public Monedas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        configBotonesInicio();
        setConfigCampos();
        cargaMoneda();
    }

    private void configBotonesInicio(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
    }
    
    private void configBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(true);
    }
    
    private void configBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
    }
    
    private void configBotonesGrabado(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
    }
    
    private void configBotonesModificar(){
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(true);
    }
    
    private void setComponentes(boolean estado){
        jTFCodMoneda.setEnabled(estado);
        jTFAlias.setEnabled(estado);
        jTFCompra.setEnabled(estado);
        jTFDenominacionMoneda.setEnabled(estado);
        jTFVenta.setEnabled(estado);
    }
    
    private void limpiarCampos(){
        jTFAlias.setText("");
        jTFCompra.setText("");
        jTFDenominacionMoneda.setText("");
        jTFVenta.setText("");
    }
    
    private String getCodigoMoneda() {
        MonedaCtrl bCtrl = new MonedaCtrl();
        int result = bCtrl.buscaMaxCodMoneda();
        result = result + 1;
        return String.valueOf(result);
    }
    
    private void setConfigCampos() {

        jTFDenominacionMoneda.setDocument(new MaxLength(40, "UPPER", "ALFA"));
        jTFAlias.setDocument(new MaxLength(6, "UPPER", "ALFA"));
        jTFCodMoneda.setDocument(new MaxLength(5, "", "ENTERO"));
        jTFCompra.setDocument(new MaxLength(5, "", "ENTERO"));
        jTFVenta.setDocument(new MaxLength(5, "", "ENTERO"));
        
        jTFCompra.addFocusListener(new Focus());
        jTFDenominacionMoneda.addFocusListener(new Focus());
        jTFAlias.addFocusListener(new Focus());
        jTFCodMoneda.addFocusListener(new Focus());
        jTFCompra.addFocusListener(new Focus());
    }
    
    private boolean verificaCampos(){
        boolean result = true;
        if(jTFCodMoneda.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Informe un Código para la moneda!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodMoneda.requestFocus();
            result = false;
        }
        if(jTFDenominacionMoneda.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: El campo DESCRIPCION no puede quedar vacío!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFDenominacionMoneda.requestFocus();
            result = false;
        }
        if(jTFAlias.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: El campo ALIAS no puede quedar vacío!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFAlias.requestFocus();
            result = false;
        }
        if(jTFCompra.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe informar de un valor de COMPRA!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCompra.requestFocus();
            result = false;
        }
        if(jTFVenta.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe informar de un valor de VENTA!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFVenta.requestFocus();
            result = false;
        }
        return result;
    }
    
    private void grabarMoneda(){
        try{
            MonedaBean bean = new MonedaBean();
            bean.setAlias(jTFAlias.getText());
            bean.setCodMoneda(Integer.parseInt(jTFCodMoneda.getText()));
            bean.setCodUsuario(FormMain.codUsuario);
            bean.setCotCompra(Integer.parseInt(jTFCompra.getText()));
            bean.setCotVenta(Integer.parseInt(jTFVenta.getText()));
            bean.setNombre(jTFDenominacionMoneda.getText());
            bean.setVigente("S");
            bean.setOperacion("/");
            
            MonedaCtrl ctrl = new MonedaCtrl();
            if(esNuevo){
                if(ctrl.catastraMoneda(bean)){
                     JOptionPane.showMessageDialog(this, "Nuevo registro grabado!", "OK", JOptionPane.INFORMATION_MESSAGE);
                     setComponentes(false);
                     cargaMoneda();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Moneda!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{ // modificacion
                if(ctrl.modificaMoneda(bean)){
                    JOptionPane.showMessageDialog(this, "Moneda modificada con Suceso!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setComponentes(false);
                }else{
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al Modificar Moneda!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } 
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar Moneda!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cargaMoneda() {
        MonedaCtrl bCtrl = new MonedaCtrl();
        int max = bCtrl.buscaMaxCodMoneda();
        if (max > 0) {
            getMoneda(max);
        } else {
            jBModificar.setEnabled(false);
        }
    }
    
    private void getMoneda(int codigo){
        MonedaCtrl ctrl = new MonedaCtrl();
        MonedaBean bean = ctrl.buscaMonedaCodMoneda(codigo);
        if(bean != null){
            jTFCodMoneda.setText(String.valueOf(bean.getCodMoneda()));
            jTFDenominacionMoneda.setText(bean.getNombre());
            jTFAlias.setText(bean.getAlias());
            jTFCompra.setText(String.valueOf(bean.getCotCompra()));
            jTFVenta.setText(String.valueOf(bean.getCotVenta()));
            usuario = bean.getCodUsuario() + " - " + getNombreUsuario(String.valueOf(bean.getCodUsuario()));
            fecha = bean.getFecVigencia();
        }
    }
    
    private String getNombreUsuario(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            UsuarioCtrl ctrl = new UsuarioCtrl();
            result = ctrl.getNombreUsuario(Integer.valueOf(codigo));
            ctrl = null;
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
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTFCodMoneda = new javax.swing.JTextField();
        jTFDenominacionMoneda = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFAlias = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jTFCompra = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFVenta = new javax.swing.JTextField();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBInformacion = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Monedas");

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("MANTENIMIENTO DE MONEDAS");

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
        jLabel1.setText("Moneda:");

        jTFCodMoneda.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodMoneda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodMoneda.setEnabled(false);
        jTFCodMoneda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodMonedaFocusGained(evt);
            }
        });
        jTFCodMoneda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodMonedaKeyPressed(evt);
            }
        });

        jTFDenominacionMoneda.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDenominacionMoneda.setEnabled(false);
        jTFDenominacionMoneda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDenominacionMonedaFocusGained(evt);
            }
        });
        jTFDenominacionMoneda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDenominacionMonedaKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Alias:");

        jTFAlias.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFAlias.setEnabled(false);
        jTFAlias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFAliasFocusGained(evt);
            }
        });
        jTFAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFAliasKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Compra:");

        jTFCompra.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCompra.setEnabled(false);
        jTFCompra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCompraFocusGained(evt);
            }
        });
        jTFCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCompraKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Venta:");

        jTFVenta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFVenta.setEnabled(false);
        jTFVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFVentaFocusGained(evt);
            }
        });
        jTFVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFVentaKeyPressed(evt);
            }
        });

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
            }
        });

        jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png"))); // NOI18N
        jBModificar.setText("Modificar");
        jBModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBModificarActionPerformed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jBInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/informacion24.png"))); // NOI18N
        jBInformacion.setText("Info");
        jBInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInformacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jBNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFDenominacionMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTFAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTFCompra)
                                    .addComponent(jTFVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDenominacionMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBModificar)
                    .addComponent(jBBuscar)
                    .addComponent(jBCancelar)
                    .addComponent(jBSalir)
                    .addComponent(jBInformacion))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(632, 317));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        if(jBModificar.getText().equals("Modificar")){
            jBModificar.setText("Guardar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
            setComponentes(true);
            configBotonesModificar();
            jTFCodMoneda.requestFocus();
            esNuevo = false;
        }else
        if(jBModificar.getText().equals("Guardar")){
            jBModificar.setText("Modificar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png")));
            if(verificaCampos()){
                grabarMoneda();
            }
            setComponentes(false);
            configBotonesGrabado();
        }
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        if(jBModificar.getText().equals("Modificar")){
            jBModificar.setText("Guardar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
        }else{
            jBModificar.setText("Modificar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png")));
        }
        esNuevo = true;
        setComponentes(true);
        jTFCodMoneda.setText(getCodigoMoneda());
        limpiarCampos();
        configBotonesNuevo();
        jTFCodMoneda.requestFocus();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        jBModificar.setText("Modificar");
        jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
        configBotonesCancelar();
        setComponentes(false);
        cargaMoneda();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodMonedaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodMonedaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFDenominacionMoneda.grabFocus();
        }
    }//GEN-LAST:event_jTFCodMonedaKeyPressed

    private void jTFDenominacionMonedaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDenominacionMonedaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFAlias.grabFocus();
        }
    }//GEN-LAST:event_jTFDenominacionMonedaKeyPressed

    private void jTFAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFAliasKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCompra.grabFocus();
        }
    }//GEN-LAST:event_jTFAliasKeyPressed

    private void jTFCompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCompraKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFVenta.grabFocus();
        }
    }//GEN-LAST:event_jTFCompraKeyPressed

    private void jTFVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBModificar.grabFocus();
        }
    }//GEN-LAST:event_jTFVentaKeyPressed

    private void jBInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInformacionActionPerformed
        GenericInfoAuditoria info = new GenericInfoAuditoria(new JFrame(), true, usuario, fecha, "Monedas");
        info.setTitle("ATOMSystems|Main - Auditoria");
        info.pack();
        info.setVisible(true);
    }//GEN-LAST:event_jBInformacionActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        DlgConsultas moneda = new DlgConsultas(new JFrame(), true);
        moneda.pack();
        moneda.setTitle("ATOMSystems|Main - Consulta Monedas");
        moneda.dConsultas("moneda", "nombre", "cod_moneda", "nombre", "fec_vigencia", "vigente", "Codigo", "Descripción", "Registro", "Vigente");
        moneda.setText(jTFCodMoneda);
        moneda.tfDescripcionBusqueda.setText("%");
        moneda.setVisible(true);
        getMoneda(Integer.parseInt(jTFCodMoneda.getText()));
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jTFCodMonedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMonedaFocusGained
        jTFCodMoneda.selectAll();
    }//GEN-LAST:event_jTFCodMonedaFocusGained

    private void jTFDenominacionMonedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDenominacionMonedaFocusGained
        jTFDenominacionMoneda.selectAll();
    }//GEN-LAST:event_jTFDenominacionMonedaFocusGained

    private void jTFAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFAliasFocusGained
        jTFAlias.selectAll();
    }//GEN-LAST:event_jTFAliasFocusGained

    private void jTFCompraFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCompraFocusGained
        jTFCompra.selectAll();
    }//GEN-LAST:event_jTFCompraFocusGained

    private void jTFVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFVentaFocusGained
        jTFVenta.selectAll();
    }//GEN-LAST:event_jTFVentaFocusGained

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
            java.util.logging.Logger.getLogger(Monedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Monedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Monedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Monedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Monedas dialog = new Monedas(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBInformacion;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFAlias;
    private javax.swing.JTextField jTFCodMoneda;
    private javax.swing.JTextField jTFCompra;
    private javax.swing.JTextField jTFDenominacionMoneda;
    private javax.swing.JTextField jTFVenta;
    // End of variables declaration//GEN-END:variables
}
