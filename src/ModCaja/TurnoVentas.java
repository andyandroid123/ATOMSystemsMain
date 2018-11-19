/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import controls.EmpleadoCtrl;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class TurnoVentas extends javax.swing.JDialog {

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    String fecVigencia = "";
    String nroTurno = "", fecCierreTurno = "";
    
    
    public TurnoVentas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configCampos();
        getFecVigencia();
        jTFNroCaja.setText(String.valueOf(FormMain.codCaja));
        nroTurno = getNroTurno();
        verificarEstadoTurno(nroTurno);       
        jTFFecHabilitacion.requestFocus();
    }

    private String getNroTurno(){
        String nroTurno = "";
        try{
            String sql = "SELECT max(nro_turno) AS nro_turno FROM turno WHERE cod_caja = " + jTFNroCaja.getText().trim();
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    nroTurno = rs.getString("nro_turno");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return nroTurno;
    }
    
    private void verificarEstadoTurno(String nroTurno){
        try{
            String sql = "SELECT nro_turno, to_char(fec_hab_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_habilitacion, to_char(fec_cierre_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_cierre,"
                       + "ticket_inicial, ticket_final, cod_cajero, cod_fis_habil, sal_inicial "
                       + "FROM turno WHERE nro_turno = " + nroTurno + " AND cod_caja = " + jTFNroCaja.getText().trim();
            System.out.println("SQL VERIFICA ESTADO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jBGrabarTurno.setEnabled(true);
                    jBConsultarMovimiento.setEnabled(true);
                    jBGrabarTurno.setText("Cerrar turno");
                    jBGrabarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/liquidacion_definitiva24.png")));
                    jBNuevoTurno.setEnabled(false);
                    jTFNroTurno.setText(rs.getString("nro_turno"));
                    jTFCodCajero.setText(rs.getString("cod_cajero"));
                    jLNombreCajero.setText(getNombreCajero(jTFCodCajero.getText().trim()));
                    jTFCodFiscal.setText(rs.getString("cod_fis_habil"));
                    jLNombreFiscal.setText(FormMain.nombreUsuario);
                    jTFFecCierre.setText(rs.getString("fec_cierre"));
                    jTFFecHabilitacion.setText(rs.getString("fec_habilitacion"));
                    jTFNroTicketFinal.setText(rs.getString("ticket_final"));
                    System.out.println("TICKET FINAL: " + rs.getString("ticket_final"));
                    jTFNroTicketInicial.setText(rs.getString("ticket_inicial"));
                    int saldoInicial = rs.getInt("sal_inicial");
                    if(saldoInicial == 0){
                        jTFSaldoInicial.setText("0.00");
                    }else{
                        jTFSaldoInicial.setText(decimalFormat.format(saldoInicial));
                    }
                    
                    String fec_cierre = rs.getString("fec_cierre");
                    if(fec_cierre == null){
                        jTFEstado.setForeground(Color.black);
                        jTFEstado.setText("HABILITADO"); 
                        jTFNroTicketFinal.setText(getNroTicketFinalTurno());
                    }else{
                        jBNuevoTurno.setEnabled(true);
                        jBGrabarTurno.setEnabled(false);
                        jBGrabarTurno.setText("Grabar turno");
                        jBGrabarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
                        jTFEstado.setForeground(Color.red);
                        jTFEstado.setText("CERRADO");
                    }
                    
                }else{
                    jBGrabarTurno.setText("Grabar turno");
                    jBGrabarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
                    JOptionPane.showMessageDialog(this, "No existe turno abierto!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
                    jBNuevoTurno.setEnabled(true);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void configCampos(){
        jTFCodCajero.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodFiscal.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFFecHabilitacion.setInputVerifier(new FechaInputVerifier(jTFFecHabilitacion));
        
        jTFCodCajero.addFocusListener(new Focus());
    }
    
    private String getNombreCajero(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            EmpleadoCtrl ctrl = new EmpleadoCtrl();
            result = ctrl.getNombreEmpleado(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private String getNroTicketFinalTurno(){
        String result = "";
        try{
            String sql = "SELECT MAX(nro_ticket) AS nro_ticket FROM venta_cab WHERE cod_caja = " + jTFNroCaja.getText().trim();
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = String.valueOf(rs.getInt("nro_ticket"));
                }else{
                    result = "1";
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
    
    private void llenarCampos(){
        jTFFecHabilitacion.setText(fecVigencia);
        jTFCodFiscal.setText(String.valueOf(FormMain.codUsuario));
        jLNombreFiscal.setText(FormMain.nombreUsuario);
        jTFNroTicketInicial.setText(getUltimoNroTicket());
        jTFNroTurno.setText(getNuevoNroTurno());
        jTFEstado.setText("***");
        jTFNroTicketFinal.setText("");
        jTFFecCierre.setText("");
        jTFCodCajero.setText("");
        jLNombreCajero.setText("***");
        jTFSaldoInicial.setText("0");
    }
    
    private String getNuevoNroTurno(){
        String result = "";
        try{
            String sql = "SELECT MAX(nro_turno) AS nro_turno FROM turno WHERE cod_caja = " + jTFNroCaja.getText().trim();
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = String.valueOf(rs.getInt("nro_turno") + 1);
                }else{
                    result = "1";
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
    
    private String getUltimoNroTicket(){
        String result = "";
        try{
            String sql = "SELECT MAX(nro_comprob) AS ticket_final FROM venta_cab WHERE nro_turno = (SELECT MAX(nro_turno) "
                       + "FROM turno WHERE cod_caja = " + jTFNroCaja.getText().trim() + ")";
            System.out.println("SQL ULTIMO NRO TICKET: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = String.valueOf(rs.getInt("ticket_final") + 1);
                }else{
                    result = String.valueOf(0 + 1);
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
    
    private void getFecVigencia(){
        try {
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                java.util.Date d = new java.util.Date();
                fecVigencia = sdf.format(d);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Formateando Fecha...");
            }
    }
    
    private void estadoBotonSalir(){
        if(!jBNuevoTurno.isEnabled()){
            jBSalir.setText("Cancelar");
            jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png")));            
        }else{
            jBSalir.setText("Salir");
            jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/exit24.png")));
        }
    }
    
    private void quitarPatternSaldoInicial(){
        if(!jTFSaldoInicial.getText().trim().equals(".00")){
            String valorExento = jTFSaldoInicial.getText().trim().replace(",", "");
            jTFSaldoInicial.setText(valorExento.replace(".00", ""));
        }
    }
    
    private boolean verificaCampos(){
        if(jTFCodCajero.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe llenar el campo CAJERO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodCajero.requestFocus();
            return false;
        }
        
        if(jTFSaldoInicial.getText().equals("")){
            jTFSaldoInicial.setText("0");
            return false;
        }
        return true;
    }
    
    private void grabarTurno(){
        String codEmpresa = utiles.Utiles.getCodEmpresaDefault();
        String codLocal = utiles.Utiles.getCodLocalDefault(codEmpresa);
        String codCaja = jTFNroCaja.getText().trim();
        String nroNuevoTurno = jTFNroTurno.getText().trim();
        String fechaHabTurno = jTFFecHabilitacion.getText().trim();
        String codFisHabil = jTFCodFiscal.getText().trim();
        String codCajero  = jTFCodCajero.getText().trim();
        String saldoInicial = jTFSaldoInicial.getText().trim().replace(",", "");
        String codSector = utiles.Utiles.getCodSectorDefault(codLocal);
        String nroTicketInicial = jTFNroTicketInicial.getText().trim();
        
        String sql = "INSERT INTO turno (cod_empresa, cod_local, cod_caja, nro_turno, fec_hab_turno, "
                   + "cod_fis_habil, cod_cajero, sal_inicial, ticket_inicial, cant_bolsa, vuelto_donado, fec_vigencia, tiene_rendicion, cod_sector) "
                   + "VALUES (" + codEmpresa + ", "
                   + codLocal + ", "
                   + codCaja + ", "
                   + nroNuevoTurno + ", '"
                   + fechaHabTurno + "', "
                   + codFisHabil + ", "
                   + codCajero + ", "
                   + saldoInicial + ", "
                   + nroTicketInicial + ", 0, 0, 'now()', 'N', "
                   + codSector + ")";
        System.out.println("SQL GRABA NUEVO TURNO: " + sql);
        if(DBManager.ejecutarDML(sql) > 0){
            try{
                DBManager.conn.commit();
                //setEstadoComponentes(false);
                //setEstadoBotonesCancelar();
                JOptionPane.showMessageDialog(this, "Nuevo turno registrado!!!", "Exito", JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "Error grabando nuevo turno (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Error al nuevo turno, Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jBSalir.grabFocus();
        }
    }
    
    private void cerrarTurno(){
        String sql = "UPDATE turno SET fec_cierre_turno = 'now()', fec_cierre_caja = 'now()', cod_fis_cierrex = " + FormMain.codUsuario + ", "
                   + "cod_fis_cierrez = " + FormMain.codUsuario + ", folio_inicial = " + jTFNroTicketInicial.getText().trim() + ", "
                   + "folio_final = " + jTFNroTicketFinal.getText().trim() + ", ticket_final = " + jTFNroTicketFinal.getText().trim() + ", "
                   + "nro_zeta = 0 WHERE cod_caja = " + jTFNroCaja.getText().trim() + " AND nro_turno = " + jTFNroTurno.getText().trim();
        
        System.out.println("SQL CIERRE TURNO ACTUAL: " + sql);
        if(DBManager.ejecutarDML(sql) > 0){
            try{
                DBManager.conn.commit();
                //setEstadoComponentes(false);
                //setEstadoBotonesCancelar();
                JOptionPane.showMessageDialog(this, "Cierre de turno realizado!!!", "Exito", JOptionPane.INFORMATION_MESSAGE);
                verificarEstadoTurno(nroTurno);
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "Error grabando cierre de turno (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Error cerrando turno, Operación Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jBSalir.grabFocus();
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
        jTFNroCaja = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFEstado = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFFecHabilitacion = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFFecCierre = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFNroTicketInicial = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFNroTicketFinal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFCodCajero = new javax.swing.JTextField();
        jLNombreCajero = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTFCodFiscal = new javax.swing.JTextField();
        jLNombreFiscal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTFSaldoInicial = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFNroTurno = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jBNuevoTurno = new javax.swing.JButton();
        jBConsultarMovimiento = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBGrabarTurno = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Turno de Ventas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("TURNO DE VENTAS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("CAJA");

        jTFNroCaja.setEditable(false);
        jTFNroCaja.setBackground(new java.awt.Color(255, 255, 102));
        jTFNroCaja.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFNroCaja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroCaja.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("ESTADO:");

        jTFEstado.setEditable(false);
        jTFEstado.setBackground(new java.awt.Color(255, 255, 102));
        jTFEstado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFEstado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFEstado.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Fecha habilitación:");

        jTFFecHabilitacion.setEditable(false);
        jTFFecHabilitacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecHabilitacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecHabilitacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecHabilitacionFocusGained(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Fecha cierre:");

        jTFFecCierre.setEditable(false);
        jTFFecCierre.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecCierre.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecCierre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecCierreFocusGained(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Nro. Ticket Inicial:");

        jTFNroTicketInicial.setEditable(false);
        jTFNroTicketInicial.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroTicketInicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroTicketInicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroTicketInicialFocusGained(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Nro. Ticket Final:");

        jTFNroTicketFinal.setEditable(false);
        jTFNroTicketFinal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroTicketFinal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroTicketFinal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroTicketFinalFocusGained(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Cajero:");

        jTFCodCajero.setEditable(false);
        jTFCodCajero.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCajero.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodCajero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCajeroFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodCajeroFocusLost(evt);
            }
        });
        jTFCodCajero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCajeroKeyPressed(evt);
            }
        });

        jLNombreCajero.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreCajero.setText("***");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Fiscal:");

        jTFCodFiscal.setEditable(false);
        jTFCodFiscal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodFiscal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodFiscal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodFiscalFocusGained(evt);
            }
        });
        jTFCodFiscal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodFiscalKeyPressed(evt);
            }
        });

        jLNombreFiscal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreFiscal.setText("***");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Saldo Inicial:");

        jTFSaldoInicial.setBackground(new java.awt.Color(255, 255, 102));
        jTFSaldoInicial.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFSaldoInicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFSaldoInicial.setText("0.00");
        jTFSaldoInicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFSaldoInicialFocusGained(evt);
            }
        });
        jTFSaldoInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFSaldoInicialKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("NRO. TURNO:");

        jTFNroTurno.setEditable(false);
        jTFNroTurno.setBackground(new java.awt.Color(255, 255, 102));
        jTFNroTurno.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFNroTurno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroTurno.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTFCodFiscal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                    .addComponent(jTFCodCajero, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLNombreCajero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLNombreFiscal, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)))
                            .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFFecCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFFecHabilitacion, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTFNroTicketFinal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addComponent(jTFNroTicketInicial, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFNroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addComponent(jTFNroTurno))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFFecHabilitacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFFecCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTFNroTicketInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFNroTicketFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodCajero, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLNombreCajero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFCodFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreFiscal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBNuevoTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/agregar24.png"))); // NOI18N
        jBNuevoTurno.setText("Nuevo turno ");
        jBNuevoTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoTurnoActionPerformed(evt);
            }
        });

        jBConsultarMovimiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/consulta_movimiento24.png"))); // NOI18N
        jBConsultarMovimiento.setText("Consultar Movimiento");
        jBConsultarMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConsultarMovimientoActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jBGrabarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGrabarTurno.setText("Grabar turno");
        jBGrabarTurno.setEnabled(false);
        jBGrabarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarTurnoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jBNuevoTurno)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGrabarTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBConsultarMovimiento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBGrabarTurno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jBConsultarMovimiento)
                                .addComponent(jBSalir))
                            .addComponent(jBNuevoTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConsultarMovimiento, jBNuevoTurno, jBSalir});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(665, 467));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        String textBoton = jBSalir.getText().trim();
        if(textBoton.equals("Salir")){
            this.dispose();
        }
        if(textBoton.equals("Cancelar")){
            jBSalir.setText("Salir");
            jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png")));
            jBNuevoTurno.setEnabled(true);
            jTFCodCajero.setEditable(false);
            jBGrabarTurno.setEnabled(false);
            verificarEstadoTurno(nroTurno);
        }
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFFecHabilitacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHabilitacionFocusGained
        jTFFecHabilitacion.selectAll();
    }//GEN-LAST:event_jTFFecHabilitacionFocusGained

    private void jTFFecCierreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecCierreFocusGained
        jTFFecCierre.selectAll();
    }//GEN-LAST:event_jTFFecCierreFocusGained

    private void jTFNroTicketInicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroTicketInicialFocusGained
        jTFNroTicketInicial.selectAll();
    }//GEN-LAST:event_jTFNroTicketInicialFocusGained

    private void jTFNroTicketFinalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroTicketFinalFocusGained
        jTFNroTicketFinal.selectAll();
    }//GEN-LAST:event_jTFNroTicketFinalFocusGained

    private void jTFCodCajeroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCajeroFocusGained
        jTFCodCajero.selectAll();
    }//GEN-LAST:event_jTFCodCajeroFocusGained

    private void jTFCodFiscalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodFiscalFocusGained
        jTFCodFiscal.selectAll();
    }//GEN-LAST:event_jTFCodFiscalFocusGained

    private void jTFSaldoInicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFSaldoInicialFocusGained
        quitarPatternSaldoInicial();
        jTFSaldoInicial.selectAll();
    }//GEN-LAST:event_jTFSaldoInicialFocusGained

    private void jBNuevoTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoTurnoActionPerformed
        jTFCodCajero.setEditable(true);
        jTFCodCajero.requestFocus();
        jBNuevoTurno.setEnabled(false);
        jBGrabarTurno.setEnabled(true);
        llenarCampos();
        estadoBotonSalir();
        
    }//GEN-LAST:event_jBNuevoTurnoActionPerformed

    private void jTFCodCajeroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCajeroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFSaldoInicial.requestFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            DlgConsultas empleado = new DlgConsultas(new JFrame(), true);
            empleado.pack();
            empleado.setTitle("ATOMSystems|Main - Consulta de Empleados");
            empleado.dConsultas("empleado", "nombre", "cod_empleado", "nombre", "apellido", "fec_vigencia", "Codigo", "Nombre", "Apellido", "Registro");
            empleado.setText(jTFCodCajero);
            empleado.tfDescripcionBusqueda.setText("%");
            empleado.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodCajeroKeyPressed

    private void jTFCodFiscalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodFiscalKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFSaldoInicial.requestFocus();
        }
    }//GEN-LAST:event_jTFCodFiscalKeyPressed

    private void jTFSaldoInicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFSaldoInicialKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFSaldoInicial.getText().trim().equals("0.00") && !jTFSaldoInicial.getText().trim().equals(".00")){
                String saldo = jTFSaldoInicial.getText().trim();
                jTFSaldoInicial.setText(decimalFormat.format(Integer.parseInt(jTFSaldoInicial.getText().trim())));
            }
            jBGrabarTurno.requestFocus();
        }
    }//GEN-LAST:event_jTFSaldoInicialKeyPressed

    private void jTFCodCajeroFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCajeroFocusLost
        jLNombreCajero.setText(getNombreCajero(jTFCodCajero.getText().trim()));
    }//GEN-LAST:event_jTFCodCajeroFocusLost

    private void jBGrabarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarTurnoActionPerformed
        String textBoton = jBGrabarTurno.getText();
        if(textBoton == "Grabar turno"){
            if(verificaCampos()){
                grabarTurno();
                jBSalir.setText("Salir");
                jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png")));
                verificarEstadoTurno(getNroTurno());
            }
        }
        
        if(textBoton == "Cerrar turno"){
            int exit = JOptionPane.showConfirmDialog(this, "¿Seguro que desea CERRAR TURNO?", "CERRAR TURNO DE VENTAS", JOptionPane.YES_NO_OPTION);
            if(exit == 0){
                cerrarTurno();
            }
        }
        
    }//GEN-LAST:event_jBGrabarTurnoActionPerformed

    private void jBConsultarMovimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConsultarMovimientoActionPerformed
        DlgConsultaMovimientoTurno turno = new DlgConsultaMovimientoTurno(new JFrame(), true);
        turno.pack();
        turno.setVisible(true);
    }//GEN-LAST:event_jBConsultarMovimientoActionPerformed

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
            java.util.logging.Logger.getLogger(TurnoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TurnoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TurnoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TurnoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TurnoVentas dialog = new TurnoVentas(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBConsultarMovimiento;
    private javax.swing.JButton jBGrabarTurno;
    private javax.swing.JButton jBNuevoTurno;
    private javax.swing.JButton jBSalir;
    public static javax.swing.JLabel jLNombreCajero;
    private javax.swing.JLabel jLNombreFiscal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    public static javax.swing.JTextField jTFCodCajero;
    private javax.swing.JTextField jTFCodFiscal;
    private javax.swing.JTextField jTFEstado;
    public static javax.swing.JTextField jTFFecCierre;
    public static javax.swing.JTextField jTFFecHabilitacion;
    public static javax.swing.JTextField jTFNroCaja;
    private javax.swing.JTextField jTFNroTicketFinal;
    private javax.swing.JTextField jTFNroTicketInicial;
    public static javax.swing.JTextField jTFNroTurno;
    public static javax.swing.JTextField jTFSaldoInicial;
    // End of variables declaration//GEN-END:variables
}
