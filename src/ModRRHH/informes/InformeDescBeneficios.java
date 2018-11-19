/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRRHH.informes;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.LibReportes;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class InformeDescBeneficios extends javax.swing.JDialog {

    boolean esMultiEmpresa;
    boolean esMultiLocal;
    String razonSocEmpresaDefault, descLocalDefault, codEmpresa, codLocal;
    private SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");
    
    public InformeDescBeneficios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setComponentes();
        datePickerFormat();
        codEmpresa = utiles.Utiles.getCodEmpresaDefault();
        codLocal = utiles.Utiles.getCodLocalDefault(codEmpresa);
        razonSocEmpresaDefault = utiles.Utiles.getRazonSocialEmpresa(codEmpresa);
        descLocalDefault = utiles.Utiles.getDescripcionLocal(codLocal);
        esMultiEmpresa = utiles.Utiles.esMultiEmpresa();
        esMultiLocal = utiles.Utiles.esMultiLocal();
        verificarMultiEmpresa(esMultiEmpresa);
        verificarMultiLocal(esMultiLocal);
        
    }

    private void getDatosConcepto(String codigo){
        try{
            if(codigo.equals("0")){
                jTFDescConcepto.setText("TODOS");
            }else{
                String sql = " SELECT des_concepto FROM concepto WHERE cod_concepto = " + codigo + " AND activo = 'S' "; 
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jTFDescConcepto.setBackground(new java.awt.Color(240,240,240));
                        jTFDescConcepto.setText(rs.getString("des_concepto"));
                    }else{
                        jTFDescConcepto.setText("CONCEPTO NO DISPONIBLE");
                        jTFCodConcepto.grabFocus();
                        jTFDescConcepto.setBackground(new java.awt.Color(255,0,0));
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "No existen datos para la consulta!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void datePickerFormat(){
        Date date = new Date();        
        jXDPDesde.setFormats(new String[]{"dd/MM/yyyy"});
        jXDPHasta.setFormats(new String[]{"dd/MM/yyyy"});
        jXDPDesde.setDate(date);
        jXDPHasta.setDate(date);
    }
    
    private void setComponentes(){
        jTFCodEmpresa.setText("1");
        jTFCodLocal.setText("1");
        jTFCodEmpleado.setText("0");
        jTFCodConcepto.setText("0");
    }
    
    private void verificarMultiEmpresa(boolean result){
        if(result){
            jTFCodEmpresa.setEditable(true);
        }else{
            jTFCodEmpresa.setEditable(false);
            jTFDescEmpresa.setText(razonSocEmpresaDefault);
        }
    }
    
    private void verificarMultiLocal(boolean result ){
        if(result){
            jTFCodLocal.setEditable(true);
        }else{
            jTFCodLocal.setEditable(false);
            jTFDescLocal.setText(descLocalDefault);
        }
    }
    
    
    private void generarInforme(){
        String fecDesde = sdf.format(jXDPDesde.getDate());
        String fecHasta = sdf.format(jXDPHasta.getDate());
        String horaFechaActual = utiles.Utiles.getSysDateTimeString();
        String sql = "SELECT dce.cod_empleado, (e.apellido || ', ' || e.nombre) AS apellido, dce.cod_concepto, c.des_concepto,"
                   + "CASE dce.debcre WHEN 'D' THEN dce.monto ELSE 0 END AS debito, "
                   + "CASE dce.debcre WHEN 'C' THEN dce.monto ELSE 0 END AS credito, "
                   + "dce.fec_vencimiento, dce.tip_comprob, dce.nro_comprob, dce.observacion "
                   + "FROM debcre_empleado dce, empleado e, concepto c "
                   + "WHERE dce.cod_empresa = " + codEmpresa + " AND dce.cod_local = " + codLocal + " "
                   + "AND dce.fec_vencimiento >= TO_DATE('" + fecDesde + "', 'dd/MM/yyyy') "
                   + "AND dce.fec_vencimiento <= TO_DATE('" + fecHasta + "', 'dd/MM/yyyy') "
                   + "AND (dce.cod_concepto = " + this.jTFCodConcepto.getText() + " OR " + this.jTFCodConcepto.getText() + " = 0) "
                   + "AND (dce.cod_empleado = " + this.jTFCodEmpleado.getText() + " OR " + this.jTFCodEmpleado.getText() + " = 0) "
                   + "AND dce.cod_empleado = e.cod_empleado AND dce.cod_concepto = c.cod_concepto AND dce.estado = 'V' "
                   + "UNION ALL "
                   + "SELECT dcp.cod_empleado, (e.apellido || ', ' || e.nombre) AS apellido, dcp.cod_concepto, c.des_concepto, "
                   + "CASE c.debcre WHEN 'D' THEN dcp.monto ELSE 0 END AS debito, "
                   + "CASE c.debcre WHEN 'C' THEN dcp.monto ELSE 0 END AS credito, "
                   + "TO_DATE('" + fecHasta + "', 'dd/MM/yyyy') AS fec_vencimiento, 'XX', 'XXX', dcp.observacion "
                   + "FROM debcre_empleado_programado dcp, concepto c, empleado e "
                   + "WHERE dcp.cod_empresa = " + codEmpresa + " "
                   + "AND dcp.cod_local = " + codLocal + " "
                   + "AND (dcp.cod_empleado = " + this.jTFCodEmpleado.getText() + " OR " + this.jTFCodEmpleado.getText() + " = 0) "
                   + "AND (dcp.cod_concepto = " + this.jTFCodConcepto.getText() + " OR " + this.jTFCodConcepto.getText() + " = 0) "
                   + "AND dcp.fec_inicio <= TO_DATE('" + fecHasta + "', 'dd/MM/yyyy') "
                   + "AND dcp.cod_concepto = c.cod_concepto "
                   + "AND dcp.cod_empleado = e.cod_empleado "
                   + "AND dcp.activo = 'S' "
                   + "ORDER BY 1,2,7";
        try{
            LibReportes.parameters.put("pEmpresa", razonSocEmpresaDefault);
            LibReportes.parameters.put("pLocal", descLocalDefault);
            LibReportes.parameters.put("pFechaInicial", fecDesde);
            LibReportes.parameters.put("pFechaFinal", fecHasta);
            LibReportes.parameters.put("pFechaHora", horaFechaActual);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "rrhhDescuentosBeneficios");
            
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
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
        jTFCodEmpresa = new javax.swing.JTextField();
        jTFDescEmpresa = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFCodLocal = new javax.swing.JTextField();
        jTFDescLocal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFCodEmpleado = new javax.swing.JTextField();
        jTFNombreEmpleado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFCodConcepto = new javax.swing.JTextField();
        jTFDescConcepto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jXDPDesde = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jXDPHasta = new org.jdesktop.swingx.JXDatePicker();
        jBGenerarInforme = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe de Descuentos - Beneficios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("INFORME DE DESCUENTOS - BENEFICIOS");

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

        jTFCodEmpresa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodEmpresaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodEmpresaFocusLost(evt);
            }
        });
        jTFCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodEmpresaKeyPressed(evt);
            }
        });

        jTFDescEmpresa.setEditable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jTFCodLocal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodLocalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodLocalFocusLost(evt);
            }
        });
        jTFCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodLocalKeyPressed(evt);
            }
        });

        jTFDescLocal.setEditable(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Empleado:");

        jTFCodEmpleado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusLost(evt);
            }
        });
        jTFCodEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodEmpleadoKeyPressed(evt);
            }
        });

        jTFNombreEmpleado.setEditable(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Concepto:");

        jTFCodConcepto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodConceptoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodConceptoFocusLost(evt);
            }
        });
        jTFCodConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodConceptoKeyPressed(evt);
            }
        });

        jTFDescConcepto.setEditable(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Desde:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Hasta:");

        jBGenerarInforme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/generarReport24.png"))); // NOI18N
        jBGenerarInforme.setText("Generar Informe");
        jBGenerarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerarInformeActionPerformed(evt);
            }
        });

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
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jBGenerarInforme)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jBSalir))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTFCodEmpleado)
                                        .addComponent(jTFCodLocal)
                                        .addComponent(jTFCodEmpresa)
                                        .addComponent(jTFCodConcepto, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTFDescEmpresa)
                                        .addComponent(jTFDescLocal)
                                        .addComponent(jTFNombreEmpleado)
                                        .addComponent(jTFDescConcepto, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)))))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFCodConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBGenerarInforme)
                    .addComponent(jBSalir))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(514, 345));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusLost
        if(jTFCodEmpleado.getText().equals("") || jTFCodEmpleado.getText().equals("0")){
            jTFCodEmpleado.setText("0");
            jTFNombreEmpleado.setText("TODOS");
        }else{
            jTFNombreEmpleado.setText(utiles.Utiles.getNombreEmpleado(jTFCodEmpleado.getText()));
        }
    }//GEN-LAST:event_jTFCodEmpleadoFocusLost

    private void jTFCodEmpresaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpresaFocusGained
        jTFCodEmpresa.selectAll();
    }//GEN-LAST:event_jTFCodEmpresaFocusGained

    private void jTFCodEmpresaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpresaFocusLost
        if(esMultiEmpresa){
            jTFDescEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jTFCodEmpresa.getText()));
        }
    }//GEN-LAST:event_jTFCodEmpresaFocusLost

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodLocalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodLocalFocusGained
        jTFCodLocal.selectAll();
    }//GEN-LAST:event_jTFCodLocalFocusGained

    private void jTFCodLocalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodLocalFocusLost
        if(esMultiLocal){
            jTFDescLocal.setText(utiles.Utiles.getDescripcionLocal(jTFCodLocal.getText()));
        }
    }//GEN-LAST:event_jTFCodLocalFocusLost

    private void jTFCodEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpresaKeyPressed
        if(esMultiEmpresa){
            if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodLocal.requestFocus();
            }
        
            if (evt.getKeyCode() == KeyEvent.VK_F1) {
                try {
                    DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                    grupo.pack();
                    grupo.setTitle("ATOMSystems|Main - Consulta de Empresa");
                    grupo.dConsultas("empresa", "descripcion", "cod_empresa", "descripcion", "es_empresadefault", null, "Codigo", "Descripción", "Pricipal", null);
                    grupo.setText(jTFCodEmpresa);
                    grupo.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                    JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
    }//GEN-LAST:event_jTFCodEmpresaKeyPressed

    private void jTFCodLocalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodLocalKeyPressed
        if(esMultiLocal){
            if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodEmpleado.requestFocus();
            }
        
            if (evt.getKeyCode() == KeyEvent.VK_F1) {
                try {
                    DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                    grupo.pack();
                    grupo.setTitle("ATOMSystems|Main - Consulta de Local");
                    grupo.dConsultas("local", "descripcion", "cod_local", "descripcion", "es_localdefault", null, "Codigo", "Descripción", "Pricipal", null);
                    grupo.setText(jTFCodLocal);
                    grupo.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                    JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
    }//GEN-LAST:event_jTFCodLocalKeyPressed

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodConcepto.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas empleados = new DlgConsultas(new JFrame(), true);
                empleados.pack();
                empleados.setTitle("ATOMSystems|Main - Consulta de Empleados");
                empleados.dConsultas("empleado", "nombre", "cod_empleado", "nombre", "apellido", "fec_ingreso", "Codigo", "Nombre", "Apellido", "Ingreso");
                empleados.setText(jTFCodEmpleado);
                empleados.tfDescripcionBusqueda.setText("%");
                empleados.tfDescripcionBusqueda.selectAll();
                empleados.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodEmpleadoKeyPressed

    private void jTFCodConceptoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodConceptoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jXDPDesde.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas empleados = new DlgConsultas(new JFrame(), true);
                empleados.pack();
                empleados.setTitle("ATOMSystems|Main - Consulta de Conceptos");
                empleados.dConsultas("concepto", "des_concepto", "cod_concepto", "des_concepto", "tip_concepto", "debcre", "Codigo", "Descripción", "Tipo", "Deb-Cre");
                empleados.setText(jTFCodConcepto);
                empleados.tfDescripcionBusqueda.setText("%");
                empleados.tfDescripcionBusqueda.selectAll();
                empleados.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodConceptoKeyPressed

    private void jTFCodConceptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodConceptoFocusGained
        jTFCodConcepto.selectAll();
    }//GEN-LAST:event_jTFCodConceptoFocusGained

    private void jBGenerarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGenerarInformeActionPerformed
        if(jTFCodEmpleado.getText().equals("")){
            jTFCodEmpleado.setText("0");
            jTFNombreEmpleado.setText("TODOS");
        }
        
        if(jTFCodConcepto.getText().equals("")){
            jTFCodConcepto.setText("0");
            jTFDescConcepto.setText("TODOS");
        }
        
        if(jTFCodEmpleado.getText().equals("0")){ 
            jTFNombreEmpleado.setText("TODOS");
        }
        
        if(jTFCodConcepto.getText().equals("0")){
            jTFDescConcepto.setText("TODOS");
        }
        
        generarInforme();
    }//GEN-LAST:event_jBGenerarInformeActionPerformed

    private void jTFCodConceptoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodConceptoFocusLost
        if(jTFCodConcepto.getText().equals("") || jTFCodConcepto.getText().equals("0")){
            jTFCodConcepto.setText("0");
            jTFDescConcepto.setText("TODOS");
        }else{
            getDatosConcepto(jTFCodConcepto.getText());
        }
    }//GEN-LAST:event_jTFCodConceptoFocusLost
    
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
            java.util.logging.Logger.getLogger(InformeDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeDescBeneficios dialog = new InformeDescBeneficios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBGenerarInforme;
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTFCodConcepto;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFCodEmpresa;
    private javax.swing.JTextField jTFCodLocal;
    private javax.swing.JTextField jTFDescConcepto;
    private javax.swing.JTextField jTFDescEmpresa;
    private javax.swing.JTextField jTFDescLocal;
    private javax.swing.JTextField jTFNombreEmpleado;
    private org.jdesktop.swingx.JXDatePicker jXDPDesde;
    private org.jdesktop.swingx.JXDatePicker jXDPHasta;
    // End of variables declaration//GEN-END:variables
}
