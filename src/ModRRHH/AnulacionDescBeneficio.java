/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRRHH;

import controls.EmpleadoCtrl;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableAnulacionDescBeneficio;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 * @VERIFICAR
 * 1. No está haciendo el recorrido para la verificación de elementos seleccionados para eliminar - 23/05/2018
 * 
 */

public class AnulacionDescBeneficio extends javax.swing.JDialog {

    /**
     * Creates new form AnulacionDescBeneficio
     */
    TableAnulacionDescBeneficio tableAnulacionModel;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    
    public AnulacionDescBeneficio(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        utiles.Utiles.punteroTablaF(jTDescuentosBeneficios, this);        
        jTDescuentosBeneficios.setFont(new Font("Tahoma", 1, 12) );
        jTDescuentosBeneficios.setRowHeight(22);
        
        jTFPeriodoRRHH.setText(getPeriodoRRHH());
        
        jTFCodEmpleado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);        
        jTFCodEmpleado.addFocusListener(new Focus());
        jTFCodEmpleado.setDocument(new MaxLength(4, "", "ENTERO"));
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
    
    private void configuraTabla(){             
        
        jTDescuentosBeneficios.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTDescuentosBeneficios.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTDescuentosBeneficios.getColumnModel().getColumn(1).setPreferredWidth(20);
        jTDescuentosBeneficios.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTDescuentosBeneficios.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTDescuentosBeneficios.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTDescuentosBeneficios.getColumnModel().getColumn(5).setPreferredWidth(150);
        jTDescuentosBeneficios.getColumnModel().getColumn(6).setPreferredWidth(20);                
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
    
    private void buscarDescuentoBeneficio(){
        ResultSet rs = null;
        
        String sql = "SELECT deb.nro_comprob, deb.cod_concepto, con.des_concepto, deb.monto, " +
                     "to_char(deb.fec_carga, 'dd/MM/yyyy') AS fecCarga, deb.observacion " +
                     "FROM debcre_empleado deb " +
                     "INNER JOIN concepto con " +
                     "ON deb.cod_concepto = con.cod_concepto " +
                     "WHERE fec_carga BETWEEN (SELECT fechainicial FROM periodo_rrhh WHERE vigente = 'S') " +
                     "AND (SELECT fechafinal FROM periodo_rrhh WHERE vigente = 'S') AND deb.cod_empleado = " + jTFCodEmpleado.getText() + 
                    " AND deb.estado = 'V' ORDER BY deb.fec_carga";
        System.out.println("SQL DESC BEN: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        tableAnulacionModel = new TableAnulacionDescBeneficio(rs);
        jTDescuentosBeneficios.setModel(tableAnulacionModel);
        habilitaBotonAnular();
        configuraTabla();
        
    }
    
    private void updateTabla(){
        
        if(jTFCodEmpleado.getText().equals("0")){
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado válido!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.grabFocus();
        }else if(jTFCodEmpleado.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.grabFocus();
        }else{
            buscarDescuentoBeneficio();
        }
    }
    
    private void limpiarDatos() {         
        tableAnulacionModel = new TableAnulacionDescBeneficio(null);
        jTDescuentosBeneficios.setModel(tableAnulacionModel);        
        configuraTabla();
        //ocultarColumnas();
    }
    
    private void habilitaBotonAnular(){
        if(tableAnulacionModel.getRowCount() > 0){
            jBAnular.setEnabled(true);
        }else{
            jBAnular.setEnabled(false);
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFPeriodoRRHH = new javax.swing.JTextField();
        jTFCodEmpleado = new javax.swing.JTextField();
        jTFNombreEmpleado = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jBAnular = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDescuentosBeneficios = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Anulación de Descuentos - Beneficios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("ANULACION DE DESCUENTO - BENEFICIO");

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
        jLabel1.setText("Empleado:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Periodo RRHH:");

        jTFPeriodoRRHH.setEditable(false);
        jTFPeriodoRRHH.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFPeriodoRRHH.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTFCodEmpleado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodEmpleado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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
        jTFNombreEmpleado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jBAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eliminar24.png"))); // NOI18N
        jBAnular.setText("Anular");
        jBAnular.setEnabled(false);
        jBAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAnularActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jBBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBAnular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar))
                    .addComponent(jTFPeriodoRRHH, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBAnular, jBBuscar, jBCancelar});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar)
                    .addComponent(jBAnular)
                    .addComponent(jBCancelar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFPeriodoRRHH, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jTDescuentosBeneficios = new JTable(){

            public boolean isCellEditable(int rowIndex, int colIndex) {

                if(colIndex == 6){ // La 4ta columna isEditable(true)
                    return true;
                }else{
                    return false; //Las celdas no son editables.
                }
                //return false; //Las celdas no son editables.
            }
        };
        jTDescuentosBeneficios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTDescuentosBeneficios);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        setSize(new java.awt.Dimension(910, 440));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.grabFocus();
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

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

    private void jTFCodEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusLost
        jTFNombreEmpleado.setText(getNombreEmpleado(jTFCodEmpleado.getText()));
    }//GEN-LAST:event_jTFCodEmpleadoFocusLost

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        updateTabla();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAnularActionPerformed
        String sql = "";
        boolean result = true;
        int cantFilas = jTDescuentosBeneficios.getRowCount();
        for(int i = 0; i < cantFilas; i++){
            boolean estado = (Boolean)tableAnulacionModel.getValueAt(i,6);
            if(estado){
                String nroComprob = tableAnulacionModel.getValueAt(i, 0).toString();
                sql = "UPDATE debcre_empleado SET estado = 'A' WHERE nro_comprob = '" + nroComprob + "'";
                System.out.println("SQL DELETE DEBCRE EMPLEADO: " + sql);
                if(DBManager.ejecutarDML(sql) > 0){
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
                    JOptionPane.showMessageDialog(this,
                    "Error al Grabar Datos, Operacion Cancelada !!!",
                    "Error", JOptionPane.WARNING_MESSAGE);
                    result = false;
                    jTDescuentosBeneficios.changeSelection(i,0,false, false);
                        break;
                }
            }else{
                result = false;
                break;
            }
        }
        
        if(result){
            JOptionPane.showMessageDialog(this, "Anulación EXITOSA...", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            updateTabla();
            configuraTabla();
            //jBBuscar.setEnabled(true);
            //jBGrabar.setEnabled(false);
            jTDescuentosBeneficios.clearSelection();
        }else{
            JOptionPane.showMessageDialog(this, "Debe seleccionar el registro a anular!", "Aviso", JOptionPane.WARNING_MESSAGE);
            jBBuscar.doClick();
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
            java.util.logging.Logger.getLogger(AnulacionDescBeneficio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnulacionDescBeneficio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnulacionDescBeneficio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnulacionDescBeneficio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnulacionDescBeneficio dialog = new AnulacionDescBeneficio(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBBuscar;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDescuentosBeneficios;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFNombreEmpleado;
    private javax.swing.JTextField jTFPeriodoRRHH;
    // End of variables declaration//GEN-END:variables
}
