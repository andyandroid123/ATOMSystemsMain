/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.StatementManager;

/**
 *
 * @author Andres
 */
public class ConsultaMovimientoOtrosTurnos extends javax.swing.JDialog {

    public static DefaultTableModel dtmTurnos;
    public static SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");
    String fecVigencia = "", nroTurno = "", nroTerminal = "";
    
    public ConsultaMovimientoOtrosTurnos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        configuraCampos();
        llenarCamposInicio();
        jTFNroCaja.setText("0");
        configTabla();
    }

    private void configTabla(){
        dtmTurnos = (DefaultTableModel)jTTurnos.getModel();
        
        jTTurnos.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTTurnos.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTTurnos.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTTurnos.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTTurnos.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTTurnos.setRowHeight(20);
    }
    
    private void llenarCamposInicio(){
        if(nroTurno == null){
            jTFNroCaja.setText("0");
        }else{
            jTFNroCaja.setText(nroTurno);
        }
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
    }
    
    private void configuraCampos(){
        jTFNroCaja.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        
        jTFNroCaja.addFocusListener(new Focus());
        jTFFecDesde.addFocusListener(new Focus());
        jTFFecHasta.addFocusListener(new Focus());
    }
    
    private void getFecVigencia(){
        try {
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date d = new java.util.Date();
                fecVigencia = sdf.format(d);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Formateando Fecha...");
            }
    }
    
    public void limpiarTabla(){
        for(int i = 0; i < jTTurnos.getRowCount(); i++){
            dtmTurnos.removeRow(i);
            i--;
        }
    }
    
    private void llenarTabla(){
        StatementManager sm = new StatementManager();
        String nroCaja = jTFNroCaja.getText().trim();
        String fecDesde = "to_date('" + jTFFecDesde.getText().trim() + "', 'dd/MM/yyyy')";
        String fecHasta = "to_date('" + jTFFecHasta.getText().trim() + "', 'dd/MM/yyyy')";
        String sql = "";
        
        if(nroCaja.equals("0")){
            sql =    "SELECT DISTINCT turno.nro_turno, turno.cod_caja, TO_CHAR(turno.fec_hab_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_habilitacion, "
                   + "TO_CHAR(turno.fec_cierre_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_cierre_turno, empleado.nombre || ' ' || empleado.apellido AS cajero "
                   + "FROM turno "
                   + "LEFT OUTER JOIN empleado "
                   + "ON turno.cod_cajero = empleado.cod_empleado "
                   + "WHERE turno.fec_hab_turno::date >= " + fecDesde + " AND turno.fec_hab_turno::date <=" + fecHasta + " "
                   + "ORDER BY turno.nro_turno";
        }else{
            sql =    "SELECT DISTINCT turno.nro_turno, turno.cod_caja, TO_CHAR(turno.fec_hab_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_habilitacion, "
                   + "TO_CHAR(turno.fec_cierre_turno, 'dd/MM/yyyy hh:mm:ss') AS fec_cierre_turno, empleado.nombre || ' ' || empleado.apellido AS cajero "
                   + "FROM turno "
                   + "LEFT OUTER JOIN empleado "
                   + "ON turno.cod_cajero = empleado.cod_empleado "
                   + "WHERE turno.cod_caja = " + nroCaja + " AND turno.fec_hab_turno::date >= " + fecDesde + " AND turno.fec_hab_turno::date <=" + fecHasta + " "
                   + "ORDER BY turno.nro_turno";
        }
        
        
        System.out.println("SQL CONSULTA DE TURNOS: " + sql);
        try{
            sm.TheSql = sql;
            sm.EjecutarSql();            
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object[] row = new Object[5];
                    row[0] = sm.TheResultSet.getString("nro_turno");
                    row[1] = sm.TheResultSet.getString("cod_caja");
                    row[2] = sm.TheResultSet.getString("fec_habilitacion");
                    row[3] = sm.TheResultSet.getString("fec_cierre_turno");
                    row[4] = sm.TheResultSet.getString("cajero");
                    
                    dtmTurnos.addRow(row);
                }
                jTTurnos.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No existen turnos habilitados en los rangos de fechas especificados!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void imprimirResumenTurnoSeleccionado(String turno, String nroCaja, String fecHabilitacion, String fecCierre){
        int saldo_inicial = getSaldoInicialTurno(turno);
        int total_vta_credito = getTotalVentaCredito();
        String cajero = getNombreCajero(turno, nroCaja);
        
        String sql = "SELECT "
                   + "CASE WHEN SUM(forma_pago.monto_pago) > 0 THEN SUM(forma_pago.monto_pago) ELSE 0 END AS total_venta_efectivo "
                   + "FROM venta_cab "
                   + "INNER JOIN forma_pago "
                   + "ON venta_cab.nro_ticket = forma_pago.nro_ticket "
                   + "WHERE venta_cab.nro_turno = " + turno + " AND venta_cab.cod_caja = " + nroCaja + " AND forma_pago.tipo_cuenta = 'EFE' "
                   + "AND forma_pago.nro_turno = " + turno + " AND forma_pago.cod_caja = " + nroCaja + " AND forma_pago.cod_cuenta = 1";
        
        System.out.println("SQL RESUMEN ULTIMO TURNO: " + sql);
        
        try{
            LibReportes.parameters.put("pNroTurno", Integer.parseInt(turno));
            LibReportes.parameters.put("pSaldoInicial", saldo_inicial);
            LibReportes.parameters.put("pTerminal", Integer.parseInt(nroCaja));
            LibReportes.parameters.put("pFechaInicioTurno", fecHabilitacion);
            LibReportes.parameters.put("pFechaFinTurno", fecCierre);
            LibReportes.parameters.put("pCajero", cajero);            
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
    
    private int getTotalDescuentos(){
        int total = 0;
        try{

            String sql = "SELECT SUM(mon_descuento) AS total "
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal;
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
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal;
            
            String sql = "SELECT SUM(mon_total) AS total "
                       + "FROM venta_cab WHERE nro_turno = " + nroTurno + " AND cod_caja = " + nroTerminal;
            
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
    
    private int getSaldoInicialTurno(String turno){
        int total = 0;
        try{
            String sql = "SELECT sal_inicial FROM turno WHERE nro_turno = " + turno + " AND cod_caja = " + nroTerminal;
            System.out.println("SQL GET SALDO INICIAL TURNO SELECCIONADO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    total = rs.getInt("sal_inicial");
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
    
    private String getNombreCajero(String turno, String nroCaja){
        String nombre = "";
        String sql = "SELECT turno.cod_cajero|| '-' ||empleado.nombre|| ' ' ||empleado.apellido AS cajero "
                   + "FROM turno "
                   + "INNER JOIN empleado "
                   + "ON turno.cod_cajero = empleado.cod_empleado "
                   + "WHERE turno.nro_turno = " + turno + " AND turno.cod_caja = " + nroCaja;        
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    nombre = rs.getString("cajero");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return nombre;
    }
    
    private void imprimeResumenVentas(){
        String fecCierre = null;
        if(jTTurnos.getRowCount() > 0){
            nroTurno = jTTurnos.getValueAt(jTTurnos.getSelectedRow(), 0).toString();
            nroTerminal = jTTurnos.getValueAt(jTTurnos.getSelectedRow(), 1).toString();
            String fecha_habilitacion = jTTurnos.getValueAt(jTTurnos.getSelectedRow(), 2).toString();
            
            try{
                fecCierre = jTTurnos.getValueAt(jTTurnos.getSelectedRow(), 3).toString();
            }catch(NullPointerException ex){
                ex.printStackTrace();
            }
            
            System.out.println("FECHA DE CIERRE: " + fecCierre);
            imprimirResumenTurnoSeleccionado(nroTurno, nroTerminal, fecha_habilitacion, fecCierre);
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
        jTFFecDesde = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFFecHasta = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTTurnos = new javax.swing.JTable();
        jBImprimir = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

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

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Nro. Caja:");

        jTFNroCaja.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroCaja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroCaja.setText("0");
        jTFNroCaja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroCajaFocusGained(evt);
            }
        });
        jTFNroCaja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroCajaKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Desde:");

        jTFFecDesde.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecDesde.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecDesde.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecDesdeFocusGained(evt);
            }
        });
        jTFFecDesde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecDesdeKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Hasta:");

        jTFFecHasta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecHasta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecHasta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecHastaFocusGained(evt);
            }
        });
        jTFFecHasta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecHastaKeyPressed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFNroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFNroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTTurnos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTTurnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "Nro Turno", "Nro Caja", "Fecha Inicio", "Fecha Fin", "Cajero"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTTurnos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTTurnosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTTurnos);
        if (jTTurnos.getColumnModel().getColumnCount() > 0) {
            jTTurnos.getColumnModel().getColumn(0).setResizable(false);
            jTTurnos.getColumnModel().getColumn(1).setResizable(false);
            jTTurnos.getColumnModel().getColumn(2).setResizable(false);
            jTTurnos.getColumnModel().getColumn(3).setResizable(false);
            jTTurnos.getColumnModel().getColumn(4).setResizable(false);
        }

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBImprimir)
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBImprimir)
                    .addComponent(jBCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFNroCajaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroCajaFocusGained
        jTFNroCaja.selectAll();
    }//GEN-LAST:event_jTFNroCajaFocusGained

    private void jTFNroCajaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroCajaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecDesde.requestFocus();
        }
    }//GEN-LAST:event_jTFNroCajaKeyPressed

    private void jTFFecDesdeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDesdeFocusGained
        jTFFecDesde.selectAll();
    }//GEN-LAST:event_jTFFecDesdeFocusGained

    private void jTFFecDesdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDesdeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecHasta.requestFocus();
        }
    }//GEN-LAST:event_jTFFecDesdeKeyPressed

    private void jTFFecHastaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHastaFocusGained
        jTFFecHasta.selectAll();
    }//GEN-LAST:event_jTFFecHastaFocusGained

    private void jTFFecHastaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecHastaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.requestFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        if(jTTurnos.getRowCount() > 0){
            limpiarTabla();
        }
        llenarTabla();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        imprimeResumenVentas();
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void jTTurnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTTurnosMouseClicked
        if(evt.getClickCount() == 2){
            if(jTTurnos.getValueAt(0, 0) == null)
            {
                dispose();
            }else
            {
                imprimeResumenVentas();
            }
        }
    }//GEN-LAST:event_jTTurnosMouseClicked

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
            java.util.logging.Logger.getLogger(ConsultaMovimientoOtrosTurnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovimientoOtrosTurnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovimientoOtrosTurnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovimientoOtrosTurnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConsultaMovimientoOtrosTurnos dialog = new ConsultaMovimientoOtrosTurnos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    private javax.swing.JTextField jTFNroCaja;
    private javax.swing.JTable jTTurnos;
    // End of variables declaration//GEN-END:variables
}
