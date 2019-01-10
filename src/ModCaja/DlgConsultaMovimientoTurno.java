/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ModCaja;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.LibReportes;

/**
 *
 * @author Andres
 */
public class DlgConsultaMovimientoTurno extends javax.swing.JDialog {

    int nroTerminal, nroTurno;     
    
    public DlgConsultaMovimientoTurno(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        labelBotones();
        nroTerminal = Integer.parseInt(TurnoVentas.jTFNroCaja.getText().trim());
        nroTurno = Integer.parseInt(TurnoVentas.jTFNroTurno.getText());
        
        if(TurnoVentas.jTFFecHabilitacion.getText().equals("")){
            jBUltimoTurno.setEnabled(false);
        }else{
            jBUltimoTurno.setEnabled(true);
        }
    }

    private void resumenUltimoTurno(){
        
        String saldo_ini = TurnoVentas.jTFSaldoInicial.getText().trim().replace(",", "");
        String saldo_inicial = saldo_ini.replace(".00", "");
        int total_vta_credito = getTotalVentaCredito();
        
        String sql = "SELECT "
                   + "CASE WHEN SUM(forma_pago.monto_pago) > 0 THEN SUM(forma_pago.monto_pago) ELSE 0 END AS total_venta_efectivo "
                   + "FROM venta_cab "
                   + "INNER JOIN forma_pago "
                   + "ON venta_cab.nro_ticket = forma_pago.nro_ticket "
                   + "WHERE venta_cab.nro_turno = " + nroTurno + " AND venta_cab.cod_caja = " + nroTerminal + " AND forma_pago.tipo_cuenta = 'EFE' "
                   + "AND forma_pago.nro_turno = " + nroTurno + " AND forma_pago.cod_caja = " + nroTerminal + " AND forma_pago.cod_cuenta = 1 "
                   + "AND forma_pago.estado = 'V' "
                   + "AND venta_cab.estado = 'V'";
        
        System.out.println("SQL RESUMEN ULTIMO TURNO: " + sql);
        
        try{
            LibReportes.parameters.put("pNroTurno", nroTurno);
            LibReportes.parameters.put("pSaldoInicial", Integer.parseInt(saldo_inicial));
            LibReportes.parameters.put("pTerminal", nroTerminal);
            LibReportes.parameters.put("pFechaInicioTurno", TurnoVentas.jTFFecHabilitacion.getText().trim());
            LibReportes.parameters.put("pFechaFinTurno", TurnoVentas.jTFFecCierre.getText().trim());
            LibReportes.parameters.put("pCajero", (TurnoVentas.jTFCodCajero.getText().trim() + " - " + TurnoVentas.jLNombreCajero.getText().trim()));            
            LibReportes.parameters.put("pTotalVtaCredito", total_vta_credito);            
            LibReportes.parameters.put("pTotalVentas", getTotalVentas());            
            LibReportes.parameters.put("pDescuentos", getTotalDescuentos());            
            
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "consulta_movimiento_turno");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private int getTotalVentaCredito(){
        int total = 0;
        try{
            String sql = "SELECT SUM(forma_pago.monto_pago) AS total_credito "
                       + "FROM venta_cab "
                       + "INNER JOIN forma_pago "
                       + "ON venta_cab.nro_ticket = forma_pago.nro_ticket "
                       + "WHERE venta_cab.nro_turno = " + nroTurno + " AND venta_cab.cod_caja = " + nroTerminal + " AND forma_pago.tipo_cuenta = 'CRE' "
                       + "AND forma_pago.nro_turno = " + nroTurno + " AND forma_pago.cod_caja = " + nroTerminal;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    total = rs.getInt("total_credito");
                }else{
                    total = 0;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return total;
    }
    
    private int getTotalVentas(){
        int total = 0;
        try{
            String sql1 = "SELECT (SUM(mon_total) - SUM(mon_descuento)) AS total "
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal + " AND estado = 'V'";
            
            String sql = "SELECT SUM(mon_total) AS total "
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal + " AND estado = 'V'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    total = rs.getInt("total");
                }else{
                    total = 0;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return total;
    }
    
    private int getTotalDescuentos(){
        int total = 0;
        try{

            String sql = "SELECT SUM(mon_descuento) AS total "
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal + " AND estado = 'V'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    total = rs.getInt("total");
                }else{
                    total = 0;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return total;
    }
    
    private void labelBotones(){
        jBUltimoTurno.setLabel("<html><p align = " + "center" + ">Último<br>turno</br></p></html>");
        jBOtrosTurnos.setLabel("<html><p align = " + "center" + ">Otros<br>turnos</br></p></html>");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jBUltimoTurno = new javax.swing.JButton();
        jBOtrosTurnos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Consulta de Movimiento de Turno");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("CONSULTA DE MOVIMIENTO DE TURNO ");

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

        jBUltimoTurno.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBUltimoTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check48.png"))); // NOI18N
        jBUltimoTurno.setText("Turno");
        jBUltimoTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUltimoTurnoActionPerformed(evt);
            }
        });

        jBOtrosTurnos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jBOtrosTurnos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/seleccionar48.png"))); // NOI18N
        jBOtrosTurnos.setText("Otros");
        jBOtrosTurnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBOtrosTurnosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jBUltimoTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBOtrosTurnos)))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBOtrosTurnos, jBUltimoTurno});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBUltimoTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBOtrosTurnos))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBOtrosTurnos, jBUltimoTurno});

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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBUltimoTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUltimoTurnoActionPerformed
        resumenUltimoTurno();
    }//GEN-LAST:event_jBUltimoTurnoActionPerformed

    private void jBOtrosTurnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBOtrosTurnosActionPerformed
        ConsultaMovimientoOtrosTurnos consulta = new ConsultaMovimientoOtrosTurnos(new JFrame(), true);
        consulta.pack();
        consulta.setVisible(true);
    }//GEN-LAST:event_jBOtrosTurnosActionPerformed

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
            java.util.logging.Logger.getLogger(DlgConsultaMovimientoTurno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgConsultaMovimientoTurno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgConsultaMovimientoTurno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgConsultaMovimientoTurno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgConsultaMovimientoTurno dialog = new DlgConsultaMovimientoTurno(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBOtrosTurnos;
    private javax.swing.JButton jBUltimoTurno;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    // End of variables declaration//GEN-END:variables

}
