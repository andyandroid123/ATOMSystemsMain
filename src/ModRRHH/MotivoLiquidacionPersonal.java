/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRRHH;

import static ModRRHH.Horarios.fecha;
import static ModRRHH.Horarios.usuario;
import beans.MotivoSalidaPersonalBean;
import controls.HorarioCtrl;
import controls.MotivoSalidaPersonalCtrl;
import controls.UsuarioCtrl;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class MotivoLiquidacionPersonal extends javax.swing.JDialog {

    /**
     * Creates new form MotivoLiquidacionPersonal
     */
    public static String usuario = null;
    public static String fecha = null;
    boolean esNuevo = false;
    
    public MotivoLiquidacionPersonal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setConfigCampos();
        setEstadoComponentes(false);
        cargaMotivo();
    }

    private void setCamposRegistro(){
        jTFCodMotivo.setText(getCodigoMotivo());
        jTFDescripcion.setText("");
        jChBDctoIndemnizacion.setSelected(false);
        jChBDctoPreaviso.setSelected(false);
        jChBIndemnizacion.setSelected(false);
        jChBPreaviso.setSelected(false);
        jChBVacCausadas.setSelected(false);
        jChBVacProporcional.setSelected(false);
    }
    
    private String getCodigoMotivo() {
        MotivoSalidaPersonalCtrl pCtrl = new MotivoSalidaPersonalCtrl();
        int result = pCtrl.buscaMaxCodMotivo();
        result = result + 1;
        return String.valueOf(result);
    }
    
    private void cargaMotivo(){
        MotivoSalidaPersonalCtrl ctrl = new MotivoSalidaPersonalCtrl();
        int max = ctrl.buscaMaxCodMotivo();
        if(max > 0){
            getMotivo(max);
        }else{
            jBModificar.grabFocus();
        }
    }
    
    private void getMotivo(int codigo){
        MotivoSalidaPersonalCtrl ctrl = new MotivoSalidaPersonalCtrl();
        MotivoSalidaPersonalBean bean = ctrl.buscaMotivoCodigo(codigo);
        if(bean != null){
            jTFCodMotivo.setText(String.valueOf(bean.getCodMotivoSalida()));
            jTFDescripcion.setText(bean.getDescripcion());
            String indemnizacion = bean.getIndemnizacion();
            String vacCausadas = bean.getVacCausada();
            String preaviso = bean.getPreaviso();
            String vacProporcional = bean.getVacProporcional();
            String dctoPreaviso = bean.getDctoPreaviso();
            String dctoIndemnizacion = bean.getDctoIndemnizacion();
            
            if(indemnizacion.equals("S")){
                jChBIndemnizacion.setSelected(true);
            }else{
                jChBIndemnizacion.setSelected(false);
            }
            
            if(vacCausadas.equals("S")){
                jChBVacCausadas.setSelected(true);
            }else{
                jChBVacCausadas.setSelected(false);
            }
            
            if(preaviso.equals("S")){
                jChBPreaviso.setSelected(true);
            }else{
                jChBPreaviso.setSelected(false);
            }
            
            if(vacProporcional.equals("S")){
                jChBVacProporcional.setSelected(true);
            }else{
                jChBVacProporcional.setSelected(false);
            }
            
            if(dctoPreaviso.equals("S")){
                jChBDctoPreaviso.setSelected(true);
            }else{
                jChBDctoPreaviso.setSelected(false);
            }
            
            if(dctoIndemnizacion.equals("S")){
                jChBDctoIndemnizacion.setSelected(true);
            }else{
                jChBDctoIndemnizacion.setSelected(false);
            }
            
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
    
    private void setConfigCampos(){
        jTFDescripcion.setDocument(new MaxLength(30, "UPPER", "ALFA"));
    }
    
    private void setEstadoComponentes(boolean estado){
        jTFDescripcion.setEnabled(estado);
        jChBDctoIndemnizacion.setEnabled(estado);
        jChBDctoPreaviso.setEnabled(estado);
        jChBIndemnizacion.setEnabled(estado);
        jChBVacCausadas.setEnabled(estado);
        jChBPreaviso.setEnabled(estado);
        jChBVacCausadas.setEnabled(estado);
        jChBVacProporcional.setEnabled(estado);
    }
    
    private void setEstadoBotonesModificar(){
        jBNuevo.setEnabled(false);
        jBModificar.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBCancelar.setEnabled(true);
        jBBuscar.setEnabled(false);
        jBInformacion.setEnabled(false);
    }
    
    private void setEstadoBotonesNuevo(){
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBGrabar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(false);
        jBNuevo.setEnabled(false);
    }
    
    private void setEstadoBotonesCancelar(){
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
        jBNuevo.setEnabled(true);
    }
    
    private void setEstadoBotonesGrabado(){
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
        jBNuevo.setEnabled(true);
    }
    
    private boolean verificaCampos(){
        if(jTFDescripcion.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo DESCRIPCION !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFDescripcion.requestFocus();
            return false;
        }
        return true;
    }
    
    private void grabarMotivo(){
        try{
            MotivoSalidaPersonalBean bean = new MotivoSalidaPersonalBean();
            bean.setCodEmpresa(Integer.parseInt(utiles.Utiles.getCodEmpresaDefault()));
            bean.setCodLocal(Integer.parseInt(utiles.Utiles.getCodLocalDefault("1")));
            bean.setCodMotivoSalida(Integer.parseInt(jTFCodMotivo.getText()));
            bean.setCodUsuario(FormMain.codUsuario);
            bean.setDescripcion(jTFDescripcion.getText());
            
            if(jChBDctoIndemnizacion.isSelected()){
                bean.setDctoIndemnizacion("S");
            }else{
                bean.setDctoIndemnizacion("N");
            }
            
            if(jChBDctoPreaviso.isSelected()){
                bean.setDctoPreaviso("S");
            }else{
                bean.setDctoPreaviso("N");
            }
            
            if(jChBIndemnizacion.isSelected()){
                bean.setIndemnizacion("S");
            }else{
                bean.setIndemnizacion("N");
            }
            
            if(jChBPreaviso.isSelected()){
                bean.setPreaviso("S");
            }else{
                bean.setPreaviso("N");
            }
            
            if(jChBVacCausadas.isSelected()){
                bean.setVacCausada("S");
            }else{
                bean.setVacCausada("N");
            }
            
            if(jChBVacProporcional.isSelected()){
                bean.setVacProporcional("S");
            }else{
                bean.setVacProporcional("N");
            }
            
            MotivoSalidaPersonalCtrl ctrl = new MotivoSalidaPersonalCtrl();
            if(esNuevo){
                if(ctrl.catastraMotivo(bean)){
                    JOptionPane.showMessageDialog(this, "Motivo registrado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                    cargaMotivo();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Motivo !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                if(ctrl.alterarMotivo(bean)){
                    JOptionPane.showMessageDialog(this, "Motivo modificado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Motivo !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar Motivo de Salida de Personal !!!", "Error", JOptionPane.WARNING_MESSAGE);
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

        bGTipo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTFCodMotivo = new javax.swing.JTextField();
        jTFDescripcion = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jChBIndemnizacion = new javax.swing.JCheckBox();
        jChBVacCausadas = new javax.swing.JCheckBox();
        jChBPreaviso = new javax.swing.JCheckBox();
        jChBVacProporcional = new javax.swing.JCheckBox();
        jChBDctoPreaviso = new javax.swing.JCheckBox();
        jChBDctoIndemnizacion = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBInformacion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Motivos de Liquidación de Personal");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("MANTENIMIENTO DE MOTIVOS DE LIQUIDACION ");

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

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Motivo:");

        jTFCodMotivo.setEnabled(false);

        jChBIndemnizacion.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBIndemnizacion);
        jChBIndemnizacion.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBIndemnizacion.setText("Indemnización?");

        jChBVacCausadas.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBVacCausadas);
        jChBVacCausadas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBVacCausadas.setText("Vac. Causadas?");

        jChBPreaviso.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBPreaviso);
        jChBPreaviso.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBPreaviso.setText("Preaviso?");

        jChBVacProporcional.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBVacProporcional);
        jChBVacProporcional.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBVacProporcional.setText("Vac. Proporcional?");

        jChBDctoPreaviso.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBDctoPreaviso);
        jChBDctoPreaviso.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBDctoPreaviso.setText("Dcto. Preaviso?");

        jChBDctoIndemnizacion.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jChBDctoIndemnizacion);
        jChBDctoIndemnizacion.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBDctoIndemnizacion.setText("Dcto. Indemnización?");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jChBIndemnizacion)
                            .addComponent(jChBVacCausadas))
                        .addGap(56, 56, 56)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jChBPreaviso)
                            .addComponent(jChBVacProporcional))
                        .addGap(66, 66, 66)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jChBDctoIndemnizacion)
                            .addComponent(jChBDctoPreaviso))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChBIndemnizacion)
                    .addComponent(jChBPreaviso)
                    .addComponent(jChBDctoPreaviso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChBVacCausadas)
                    .addComponent(jChBVacProporcional)
                    .addComponent(jChBDctoIndemnizacion))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

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

        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGrabar.setText("Grabar");
        jBGrabar.setEnabled(false);
        jBGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarActionPerformed(evt);
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
        jBCancelar.setEnabled(false);
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/informacion24.png"))); // NOI18N
        jBInformacion.setText("Info");
        jBInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInformacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGrabar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBInformacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBModificar)
                    .addComponent(jBGrabar)
                    .addComponent(jBBuscar)
                    .addComponent(jBCancelar)
                    .addComponent(jBInformacion))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(654, 328));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInformacionActionPerformed
        GenericInfoAuditoria info = new GenericInfoAuditoria(new JFrame(), true, usuario, fecha, "Motivos Liquidación de Personal");
        info.setTitle("ATOMSystems|Main - Auditoria");
        info.pack();
        info.setVisible(true);
    }//GEN-LAST:event_jBInformacionActionPerformed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        esNuevo = true;
        setCamposRegistro();
        setEstadoComponentes(true);
        setEstadoBotonesNuevo();        
        jTFDescripcion.grabFocus();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        esNuevo = false;
        setEstadoComponentes(true);
        setEstadoBotonesModificar();
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(verificaCampos()){
            grabarMotivo();
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        DlgConsultas motivos = new DlgConsultas(new JFrame(), true);
        motivos.pack();
        motivos.setTitle("ATOMSystems|Main - Consulta de Motivos de Liquidación de Personal");
        motivos.tfDescripcionBusqueda.setText("%");
        motivos.dConsultas("motivo_salida_personal", "descripcion", "cod_motivo_salida", "descripcion", "fec_vigencia", null, "Codigo", "Descripción", "Registro", null);
        motivos.setText(jTFCodMotivo);
        motivos.setVisible(true);
        getMotivo(Integer.parseInt(jTFCodMotivo.getText()));
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoBotonesCancelar();
        setEstadoComponentes(false);
        cargaMotivo();
    }//GEN-LAST:event_jBCancelarActionPerformed

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
            java.util.logging.Logger.getLogger(MotivoLiquidacionPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MotivoLiquidacionPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MotivoLiquidacionPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MotivoLiquidacionPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MotivoLiquidacionPersonal dialog = new MotivoLiquidacionPersonal(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGTipo;
    private javax.swing.JButton jBBuscar;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBGrabar;
    private javax.swing.JButton jBInformacion;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JCheckBox jChBDctoIndemnizacion;
    private javax.swing.JCheckBox jChBDctoPreaviso;
    private javax.swing.JCheckBox jChBIndemnizacion;
    private javax.swing.JCheckBox jChBPreaviso;
    private javax.swing.JCheckBox jChBVacCausadas;
    private javax.swing.JCheckBox jChBVacProporcional;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFCodMotivo;
    private javax.swing.JTextField jTFDescripcion;
    // End of variables declaration//GEN-END:variables
}
