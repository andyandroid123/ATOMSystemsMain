/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
import principal.FormMain;

/**
 *
 * @author Andres
 */
public class DlgFinVenta extends javax.swing.JDialog {

    // MOSTRAR HORA ACTUAL DEL SO 
    private String hora, minutos, segundos;
    Thread thread;
    
    private TimerTask task;
    private Timer tiempo;
    private int indice = 0;
    private int speed = 80;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    
    
    public DlgFinVenta(java.awt.Frame parent, boolean modal, double vuelto) {
        super(parent, modal);
        initComponents();
        startTitleAnimation();
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(utiles.Utiles.getCodEmpresaDefault()));
        jLNombreEmpresaDlg.setText(FormMain.empresaBean.getDescripcion());
        jLDescLocalDlg.setText(FormMain.empresaBean.getDescripcion());
        jTFVuelto.setText(decimalFormat.format(vuelto));
        getFechaActual();
    }

    private void getFechaActual(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        jLFechaActual.setText(sdf.format(date));
    }
    
    public void run(){
        Thread current = Thread.currentThread();
        
        while(current == thread){
            time();
            jLHoraActual.setText(hora + ":" + minutos + ":" + segundos);
        }
    }
    
    private void time(){
        Calendar calendario = new GregorianCalendar();
        Date currentTime = new Date();
        calendario.setTime(currentTime);
        hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY): "0" + calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE): "0" + calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND): "0" + calendario.get(Calendar.SECOND);
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            }
        });
    }
    
    private String titulo(){
        return "¡GRACIAS POR SU PREFERENCIA!";
    }
    
    private void setTitle(int i){
        //this.setTitle(titulo().substring(0, i));
        jLGraciasPref.setText(titulo().substring(0,i));
    }
    
    private void startTitleAnimation(){
        tiempo = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                
                // hora
                time();
                jLHoraActual.setText(hora + ":" + minutos + ":" + segundos);
                    
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
        jLabel1 = new javax.swing.JLabel();
        jLGraciasPref = new javax.swing.JLabel();
        jLNombreEmpresa = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTFVuelto = new javax.swing.JTextField();
        jLHoraActual = new javax.swing.JLabel();
        jLFechaActual = new javax.swing.JLabel();
        jLNombreEmpresaDlg = new javax.swing.JLabel();
        jLDescLocalDlg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Ventas");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/atomLogo128.png"))); // NOI18N

        jLGraciasPref.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
        jLGraciasPref.setText("GRACIAS POR SU PREFERENCIA!");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLNombreEmpresa.setText("***");

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("Su Vuelto: ");

        jTFVuelto.setEditable(false);
        jTFVuelto.setBackground(new java.awt.Color(0, 0, 0));
        jTFVuelto.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jTFVuelto.setForeground(new java.awt.Color(255, 0, 0));
        jTFVuelto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFVuelto.setText("000.00");
        jTFVuelto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFVueltoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jLHoraActual.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLHoraActual.setText("00:00:00");

        jLFechaActual.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLFechaActual.setText("dd/MM/yyyy");

        jLNombreEmpresaDlg.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLNombreEmpresaDlg.setForeground(new java.awt.Color(153, 153, 153));
        jLNombreEmpresaDlg.setText("***");

        jLDescLocalDlg.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLDescLocalDlg.setForeground(new java.awt.Color(153, 153, 153));
        jLDescLocalDlg.setText("***");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLDescLocalDlg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLHoraActual))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLNombreEmpresaDlg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLFechaActual))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLGraciasPref, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLNombreEmpresa))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jLNombreEmpresa)
                        .addGap(18, 18, 18)
                        .addComponent(jLGraciasPref)))
                .addGap(29, 29, 29)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLFechaActual)
                    .addComponent(jLNombreEmpresaDlg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLHoraActual)
                    .addComponent(jLDescLocalDlg))
                .addGap(21, 21, 21))
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

        setSize(new java.awt.Dimension(774, 543));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            this.dispose();
        }
    }//GEN-LAST:event_formKeyPressed

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            this.dispose();
        }
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jTFVueltoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFVueltoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            this.dispose();
        }
    }//GEN-LAST:event_jTFVueltoKeyPressed

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
            java.util.logging.Logger.getLogger(DlgFinVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgFinVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgFinVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgFinVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgFinVenta dialog = new DlgFinVenta(new javax.swing.JFrame(), true, 0);
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
    private javax.swing.JLabel jLDescLocalDlg;
    private javax.swing.JLabel jLFechaActual;
    private javax.swing.JLabel jLGraciasPref;
    private javax.swing.JLabel jLHoraActual;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreEmpresaDlg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTFVuelto;
    // End of variables declaration//GEN-END:variables
}
