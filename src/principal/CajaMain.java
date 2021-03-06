/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import ModCaja.AnulacionDocVenta;
import ModCaja.ConsultaMovimientoOtrosTurnos;
import ModCaja.ReImpresionDocVenta;
import ModCaja.RegistroCobroClientes1;
import ModCaja.TurnoVentas;
import ModCaja.Ventas;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import utiles.ClassMenu;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class CajaMain extends javax.swing.JFrame {

    // para el título dinámico
    private TimerTask task;
    private Timer tiempo;
    private int indice = 0;
    private int speed = 200;
    
    public static boolean resultExitVentas = false; 
    public static boolean resultExitReImpresiones = false; 
    public static boolean resultExitCobroCuentas = false; 
    public static boolean resultExitTurnos = false; 
    
    // para los resúmenes de ventas
    int nroTerminal = FormMain.codCaja;
    String nroTurno = "", fecHabTurno = "", fecCierreTurno = "", cajero = "";
    double salInicialTurno = 0;
    
    
    Ventas ventas = null;
    RegistroCobroClientes1 cobroClientes = null;
    
    ClassMenu classMenu = new ClassMenu();
    
    public CajaMain(String user, String grupo) {
        initComponents();
        this.setTitle("ATOMSystems|Main - Módulo de Ventas - (CAJA " + nroTerminal + ")");
        cerrarVentana();
        labelBotones();
        startTitleAnimation();
        jMenuBar1.setVisible(false);
        classMenu.permisosMenu(user, grupo, jMenuBar1);
    }
    
    private void msgDesarrollo(){
        JOptionPane.showMessageDialog(this, "Módulo en desarrollo!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void salir(){
        //int exit = JOptionPane.showConfirmDialog(this, "Desea salir del sistema?", "Salir del sistema", JOptionPane.YES_NO_OPTION);
        //if(exit == 0){
            FormMain.resultExitCajaMain = false;
            FormMain.formVENTAS = null;
            this.dispose();
        //}
    }
    
    private boolean controlForms(){
        boolean result = true;
        if(resultExitVentas == false && resultExitReImpresiones == false && resultExitCobroCuentas == false && resultExitTurnos == false){
            result = false;
        }
        return result;
    }
    
    private void labelBotones(){
        jBReImpresionDocs.setLabel("<html><p align = " + "center" + ">Consulta<br>Re Impresión</br><br>Docs</br></p></html>");
        jBTurnoVenta.setLabel("<html><p align = " + "center" + ">Turno<br>de</br><br>Ventas</br></p></html>");
        jBVentas.setLabel("<html><p align = " + "center" + ">Registro<br>de</br><br>Ventas</br></p></html>");
        jBCobroClientes.setLabel("<html><p align = " + "center" + ">Cobro<br>de</br><br>Cuentas</br></p></html>");
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(controlForms()){
                    JOptionPane.showMessageDialog(CajaMain.this, "Existen ventanas abiertas" , "ATENCION", JOptionPane.WARNING_MESSAGE);
                }else{
                    salir();
                }
            }
        });
    }
    
    private boolean existeTurnoHabilitado(){
        boolean result = false;
        try{
            String sql = "SELECT nro_turno FROM turno WHERE cod_caja = " + FormMain.codCaja + " AND fec_cierre_turno IS null";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = true;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private String titulo(){
        return "ATOMSystems";
    }
    
    private String subTitulo(){
        return "Módulo CAJA";
    }
    
    private void setTitle(int i){
        //this.setTitle(titulo().substring(0, i));
        jTFTitulo.setText(titulo().substring(0, i));
        jTFSubTitulo.setText(subTitulo().substring(0, i));
    }
    
    private void startTitleAnimation(){
        tiempo = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(indice == titulo().length()){
                    indice = 1;
                }else{
                    indice++;
                }
                setTitle(indice);
            }
        };
        tiempo.schedule(task, 0, speed);
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
        jBTurnoVenta = new javax.swing.JButton();
        jBReImpresionDocs = new javax.swing.JButton();
        jBVentas = new javax.swing.JButton();
        jBCobroClientes = new javax.swing.JButton();
        jTFTitulo = new javax.swing.JTextField();
        jTFSubTitulo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMnuOperacionesCaja = new javax.swing.JMenu();
        jMnuIAnulacionDocVenta = new javax.swing.JMenuItem();
        jMnuInformeDeMovimientos = new javax.swing.JMenu();
        jMnuIOtrosTurnos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Módulo de Ventas");
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jBTurnoVenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBTurnoVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/agregar24.png"))); // NOI18N
        jBTurnoVenta.setText("Turnos");
        jBTurnoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTurnoVentaActionPerformed(evt);
            }
        });

        jBReImpresionDocs.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBReImpresionDocs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBReImpresionDocs.setText("Re Impresion docs");
        jBReImpresionDocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBReImpresionDocsActionPerformed(evt);
            }
        });

        jBVentas.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/venta24.png"))); // NOI18N
        jBVentas.setText("Ventas");
        jBVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBVentasActionPerformed(evt);
            }
        });

        jBCobroClientes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBCobroClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/registro_compra24.png"))); // NOI18N
        jBCobroClientes.setText("Cobro Cuentas");
        jBCobroClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCobroClientesActionPerformed(evt);
            }
        });

        jTFTitulo.setBackground(new java.awt.Color(0, 0, 0));
        jTFTitulo.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jTFTitulo.setForeground(new java.awt.Color(255, 0, 0));
        jTFTitulo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFTitulo.setText("***");

        jTFSubTitulo.setBackground(new java.awt.Color(0, 0, 0));
        jTFSubTitulo.setFont(new java.awt.Font("Times New Roman", 1, 32)); // NOI18N
        jTFSubTitulo.setForeground(new java.awt.Color(255, 0, 0));
        jTFSubTitulo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFSubTitulo.setText("***");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/forma_pago_maestro64.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/forma_pago_mastercard64.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/forma_pago_visa64.png"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/forma_pago_ae64.png"))); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/toldo_caja.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jBTurnoVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCobroClientes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBReImpresionDocs))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                            .addComponent(jTFSubTitulo)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBCobroClientes, jBReImpresionDocs, jBTurnoVenta, jBVentas});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBTurnoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBReImpresionDocs)
                    .addComponent(jBVentas)
                    .addComponent(jBCobroClientes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFSubTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(20, 20, 20))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCobroClientes, jBReImpresionDocs, jBTurnoVenta, jBVentas});

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMnuOperacionesCaja.setText("Operaciones ");
        jMnuOperacionesCaja.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuOperacionesCaja.setName("jMnuOperacionesCaja"); // NOI18N

        jMnuIAnulacionDocVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIAnulacionDocVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eliminar24.png"))); // NOI18N
        jMnuIAnulacionDocVenta.setText("Anulación de doc de venta");
        jMnuIAnulacionDocVenta.setName("jMnuIAnulacionDocVenta"); // NOI18N
        jMnuIAnulacionDocVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIAnulacionDocVentaActionPerformed(evt);
            }
        });
        jMnuOperacionesCaja.add(jMnuIAnulacionDocVenta);

        jMenuBar1.add(jMnuOperacionesCaja);

        jMnuInformeDeMovimientos.setText("Informes de movimientos");
        jMnuInformeDeMovimientos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuInformeDeMovimientos.setName("jMnuInformeDeMovimientos"); // NOI18N

        jMnuIOtrosTurnos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIOtrosTurnos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/seleccionar24_1.png"))); // NOI18N
        jMnuIOtrosTurnos.setText("Resumen por turno de ventas");
        jMnuIOtrosTurnos.setName("jMnuIOtrosTurnos"); // NOI18N
        jMnuIOtrosTurnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIOtrosTurnosActionPerformed(evt);
            }
        });
        jMnuInformeDeMovimientos.add(jMnuIOtrosTurnos);

        jMenuBar1.add(jMnuInformeDeMovimientos);

        setJMenuBar(jMenuBar1);

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

        setSize(new java.awt.Dimension(630, 483));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBVentasActionPerformed
        salir();
        if(existeTurnoHabilitado()){
            if (ventas == null){
            ventas= new Ventas();
            ventas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventas.setVisible(true);
            resultExitVentas = true;
            }else{
                ventas.setState(Frame.NORMAL);
                ventas.setVisible(true);
                resultExitVentas = true;
            }
        }else{
            JOptionPane.showMessageDialog(this, "No existe turno habilitado!", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jBVentasActionPerformed

    private void jBTurnoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTurnoVentaActionPerformed
        salir();
        TurnoVentas turno = new TurnoVentas(new JFrame(), true);
        turno.pack();
        turno.setVisible(true);        
    }//GEN-LAST:event_jBTurnoVentaActionPerformed

    private void jBReImpresionDocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBReImpresionDocsActionPerformed
        ReImpresionDocVenta impresion = new ReImpresionDocVenta(new JFrame(), true);
        impresion.pack();
        impresion.setVisible(true);
        //msgDesarrollo();
    }//GEN-LAST:event_jBReImpresionDocsActionPerformed

    private void jBCobroClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCobroClientesActionPerformed
        salir();
        if(existeTurnoHabilitado()){
            cobroClientes= new RegistroCobroClientes1(new JFrame(), true, "COBRANZAS", "");
            cobroClientes.pack();
            cobroClientes.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(this, "No existe turno habilitado!", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jBCobroClientesActionPerformed

    private void jMnuIAnulacionDocVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIAnulacionDocVentaActionPerformed
        AnulacionDocVenta anulacion = new AnulacionDocVenta(new JFrame(), true);
        anulacion.pack();
        anulacion.setVisible(true);
    }//GEN-LAST:event_jMnuIAnulacionDocVentaActionPerformed

    private void jMnuIOtrosTurnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIOtrosTurnosActionPerformed
        ConsultaMovimientoOtrosTurnos consulta = new ConsultaMovimientoOtrosTurnos(new JFrame(), true);
        consulta.pack();
        consulta.setVisible(true);
    }//GEN-LAST:event_jMnuIOtrosTurnosActionPerformed

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
            java.util.logging.Logger.getLogger(CajaMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CajaMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CajaMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CajaMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new CajaMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCobroClientes;
    private javax.swing.JButton jBReImpresionDocs;
    private javax.swing.JButton jBTurnoVenta;
    private javax.swing.JButton jBVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMnuIAnulacionDocVenta;
    private javax.swing.JMenuItem jMnuIOtrosTurnos;
    private javax.swing.JMenu jMnuInformeDeMovimientos;
    private javax.swing.JMenu jMnuOperacionesCaja;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTFSubTitulo;
    private javax.swing.JTextField jTFTitulo;
    // End of variables declaration//GEN-END:variables
}
