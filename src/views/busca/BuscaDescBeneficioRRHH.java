/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.busca;

import controls.EmpleadoCtrl;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.StatementManager;
import utiles.TGridTableModel;
import utiles.Utiles;

/**
 *
 * @author Andres
 */
public class BuscaDescBeneficioRRHH extends javax.swing.JDialog {

    /**
     * Creates new form BuscaDescBeneficioRRHH
     */
    
    DefaultTableModel dtmDebCre = new DefaultTableModel(null, new String[]{"Número", "Monto", "Fecha"});
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    private ResultSet rs = null;
    private static JTextField jText;
    String periodo = "";
    String modulo;
    
    public BuscaDescBeneficioRRHH(java.awt.Frame parent, boolean modal, String modulo) {
        super(parent, modal);
        initComponents();
        
        this.modulo = modulo;
        configTabla();
        periodo = getPeriodoRRHH();
        jTFPeriodoRRHH.setText(periodo.substring(0,2) + "/" + periodo.substring(2,6) );
        

        jTFCodEmpleado.addFocusListener(new Focus());
        jTFCodEmpleado.grabFocus();
        jTFCodEmpleado.setText("0");       
    }

    private String getPeriodoRRHH(){
        String periodo = "";
        try{
            String sql = "SELECT periodo FROM periodo_rrhh WHERE vigente = 'S'"; 
            System.out.println("PERIODO RRHH: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    periodo = rs.getString("periodo");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return periodo;
    }
    
    private void limpiarTabla(){
        for(int i = 0; i < jTDebCre.getRowCount(); i++){
            dtmDebCre.removeRow(i);
            i--;
        }
    }
    
    private void buscarDebCreEmpleado(){
        boolean result = true;
        int rows = 0;
        String sql = "";
        StatementManager sm = new StatementManager();
        try{
            if(!jTFCodEmpleado.getText().equals("") || jTFCodEmpleado.getText().equals("")){
                if(modulo.equals("ANTICIPO")){
                    sql = "SELECT deb.nro_comprob, deb.monto, to_char(deb.fec_carga, 'dd/MM/yyyy hh:mm:ss AM') AS fecCarga " +
                          "FROM debcre_empleado deb " +
                          "INNER JOIN empleado em " +
                          "ON deb.cod_empleado = em.cod_empleado " +
                          "WHERE em.cod_empleado = " + jTFCodEmpleado.getText() + " AND deb.fec_carga between (SELECT fechainicial FROM periodo_rrhh WHERE periodo = '" + periodo + "')" +
                          "AND (SELECT fechafinal FROM periodo_rrhh WHERE periodo = '" + periodo + "' " +
                          "AND deb.cod_concepto = (SELECT valor::numeric FROM parametro_rrhh WHERE parametro = 'RRHH_COD_CONCEPTO_ANTICIPO')" +
                          "AND estado = 'V' ORDER BY deb.fec_carga)";
                }else{
                    sql = "SELECT deb.nro_comprob, deb.monto, to_char(deb.fec_carga, 'dd/MM/yyyy hh:mm:ss AM') AS fecCarga " +
                          "FROM debcre_empleado deb " +
                          "INNER JOIN empleado em " +
                          "ON deb.cod_empleado = em.cod_empleado " +
                          "WHERE em.cod_empleado = " + jTFCodEmpleado.getText() + " AND deb.fec_carga between (SELECT fechainicial FROM periodo_rrhh WHERE periodo = '" + periodo + "')" +
                          "AND (SELECT fechafinal FROM periodo_rrhh WHERE periodo = '" + periodo + "' " +
                          "AND deb.cod_concepto <> (SELECT valor::numeric FROM parametro_rrhh WHERE parametro = 'RRHH_COD_CONCEPTO_ANTICIPO')" +
                          "AND estado = 'V' ORDER BY deb.fec_carga)";
                }
                
                
                sm.TheSql = sql;
                sm.EjecutarSql();
                dtmDebCre.getRowCount();
                
                System.out.println("SQL BUSQUEDA: " + sql);
                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        Object[] row = new Object[3];
                        row[0] = sm.TheResultSet.getString("nro_comprob");
                        row[1] = sm.TheResultSet.getDouble("monto");
                        row[2] = sm.TheResultSet.getString("fecCarga");
                        dtmDebCre.addRow(row);
                    }
                    rows = dtmDebCre.getRowCount();
                    jTDebCre.updateUI();
                }else{
                    rows = 0;
                    JOptionPane.showMessageDialog(this, "ATENCION: Ningún registro encontrado!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: debe seleccionar un empleado!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            sm.CerrarResultSet();
            sm = null;
        }
    }
    
    
    private static void setDataSet(ResultSet resultSet, Vector columnName)
    {
        try
        {
            if(resultSet != null && columnName != null)
            {
                Vector lineas = new Vector();
                while(resultSet.next())
                {
                    Vector nuevaLinea = new Vector();
                    for(int cont = 1; cont <= resultSet.getMetaData().getColumnCount(); cont++)
                    {
                        nuevaLinea.addElement(resultSet.getObject(cont));
                    }
                    lineas.addElement(nuevaLinea);
                }
                jTDebCre.setModel(new TGridTableModel(lineas, columnName));
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }
    
    
    private void limpiarGrillas()
    {
        for (int i = 0; i < jTDebCre.getRowCount(); i++) {
                dtmDebCre.removeRow(i);
                i--;
            }
    }
    
    
    public static void setText(JTextField jTf)
    {
        jText = jTf;
    }
        
    
    private void configTabla(){
        
        jTDebCre.setModel(dtmDebCre);
        jTDebCre.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTDebCre.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTDebCre.getColumnModel().getColumn(1).setPreferredWidth(50);
        jTDebCre.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTDebCre.setRowHeight(23);
        jTFCodEmpleado.grabFocus();
    }
    
    private String getNombreEmpleado(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            EmpleadoCtrl ctrl = new EmpleadoCtrl();
            result = ctrl.getNombreEmpleado(Integer.valueOf(codigo));
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
        jLabel1 = new javax.swing.JLabel();
        jTFCodEmpleado = new javax.swing.JTextField();
        jTFNombreEmpleado = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTFPeriodoRRHH = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDebCre = new javax.swing.JTable();
        jBSeleccionar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Búsqueda de Descuentos/Beneficios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empleado:");

        jTFCodEmpleado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCodEmpleado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodEmpleado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusGained(evt);
            }
        });
        jTFCodEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodEmpleadoKeyPressed(evt);
            }
        });

        jTFNombreEmpleado.setEditable(false);
        jTFNombreEmpleado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });
        jBBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBBuscarKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Periodo RRHH:");

        jTFPeriodoRRHH.setEditable(false);
        jTFPeriodoRRHH.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFPeriodoRRHH.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFPeriodoRRHH.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFPeriodoRRHHFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFPeriodoRRHH)
                    .addComponent(jBBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFPeriodoRRHH, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jTDebCre = new JTable(){

            public boolean isCellEditable(int rowIndex, int colIndex) {

                return false; //Las celdas no son editables.

            }
        };
        jTDebCre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTDebCre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTDebCreMouseClicked(evt);
            }
        });
        jTDebCre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDebCreKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDebCre);

        jBSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/seleccionar24.png"))); // NOI18N
        jBSeleccionar.setText("Seleccionar");
        jBSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSeleccionarActionPerformed(evt);
            }
        });
        jBSeleccionar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBSeleccionarKeyPressed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSeleccionar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBCancelar, jBSeleccionar});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSeleccionar)
                    .addComponent(jBCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBSeleccionar});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(687, 477));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
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
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.grabFocus();
        }
        
    }//GEN-LAST:event_jTFCodEmpleadoKeyPressed

    private void jTFCodEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusLost
        jTFNombreEmpleado.setText(getNombreEmpleado(jTFCodEmpleado.getText()));
        
    }//GEN-LAST:event_jTFCodEmpleadoFocusLost

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

    private void jBBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBBuscarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.doClick();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            jText.setText("0");
            dispose();
        }
    }//GEN-LAST:event_jBBuscarKeyPressed

    private void jTDebCreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDebCreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            jText.setText(String.valueOf(jTDebCre.getValueAt(jTDebCre.getSelectedRow(), 0)));
            dispose();
        }else{
            jText.setText(jText.getText());
            dispose();
        }
    }//GEN-LAST:event_jTDebCreKeyPressed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        
        limpiarTabla();
        
        if(jTFCodEmpleado.getText().equals("0")){
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado válido!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.grabFocus();
        }else if(jTFCodEmpleado.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.grabFocus();
        }else{
            buscarDebCreEmpleado();
        } 
        
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jTFPeriodoRRHHFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPeriodoRRHHFocusGained
        jTFCodEmpleado.grabFocus();
    }//GEN-LAST:event_jTFPeriodoRRHHFocusGained

    private void jBSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSeleccionarActionPerformed
        jText.setText(String.valueOf(jTDebCre.getValueAt(jTDebCre.getSelectedRow(), 0)));
        dispose();
    }//GEN-LAST:event_jBSeleccionarActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        dispose();        
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBSeleccionarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBSeleccionarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBSeleccionarKeyPressed

    private void jTDebCreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTDebCreMouseClicked
        if(evt.getClickCount() == 2){
            if(jTDebCre.getValueAt(0, 0) == null)
            {
                dispose();
            }else
            {
                jText.setText(String.valueOf(jTDebCre.getValueAt(jTDebCre.getSelectedRow(), 0)));
                dispose();
            }
        }
    }//GEN-LAST:event_jTDebCreMouseClicked

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
            java.util.logging.Logger.getLogger(BuscaDescBeneficioRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscaDescBeneficioRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscaDescBeneficioRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscaDescBeneficioRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscaDescBeneficioRRHH dialog = new BuscaDescBeneficioRRHH(new javax.swing.JFrame(), true, "");
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
    private javax.swing.JButton jBSeleccionar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTDebCre;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFNombreEmpleado;
    private javax.swing.JTextField jTFPeriodoRRHH;
    // End of variables declaration//GEN-END:variables
}
