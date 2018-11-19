/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import controls.EmpleadoCtrl;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.NumeroATexto;
import utiles.StatementManager;
import utiles.Utiles;
import views.busca.BuscaCliente;

/**
 *
 * @author Andres
 */
public class RegistroCobroClientes extends javax.swing.JDialog {

    public static String fecVigencia = "";
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    public static DefaultTableModel dtmDocPendientes;    
    public static double total_docs_mas_interes;
    String codCajero, nroTurno;
    
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    double montoCobrado, vuelto, montoDebito, montoCredito;
    
    public RegistroCobroClientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configCampos();
        getFecVigencia();
        llenarCombos();
        getDatosTurnoAbierto();
        jLRazonSocEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        setEstadoCampos(false);
        configTabla();
        jBConfirmarCobro.setLabel("<html><p align = " + "center" + ">Confirmar<br>Cobro</br></p></html>");
        jBMsg.setVisible(false);
        getDatosEmpresaReport();
    }

    private void configCampos(){
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFInteres.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodEmpleado.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFNroRecibo.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFComentarios.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFFecCobro.setInputVerifier(new FechaInputVerifier(jTFFecCobro));
        
        jTFCodCliente.addFocusListener(new Focus());
        jTFInteres.addFocusListener(new Focus());
        jTFCodEmpleado.addFocusListener(new Focus());
        jTFNroRecibo.addFocusListener(new Focus());
        jTFComentarios.addFocusListener(new Focus());
        jTFFecCobro.addFocusListener(new Focus());
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
    
    private void llenarCampos(){        
        jTFCodCuentaCliente.setText("0");
        jTFTotalCuenta.setText("0");
        jTFInteres.setText(getInteresCobro());
        jTFFecCobro.setText(fecVigencia);
        jTFCodEmpleado.setText(codCajero);
        jTFNroTurno.setText(nroTurno);
        jTFCodCaja.setText(String.valueOf(FormMain.codCaja));
        jTFComentarios.setText("COBRO DE CUENTAS");
        jTFNroPago.setText(getSecuenciaCobroCliente());
        jTFNroRecibo.setText(getNroRecibo());
        jTFEstado.setText("V");
        jLEstado.setText("VIGENTE");
        jTFCodCliente.setText("0");
        jTFCodCliente.grabFocus();
        jTFCodCliente.requestFocusInWindow();
    }
    
    private void setEstadoCampos(boolean estado){
        jTFCodCliente.setEnabled(estado);
        jTFInteres.setEnabled(estado);
        jTFFecCobro.setEnabled(estado);
        jTFCodEmpleado.setEnabled(estado);
        jTFNroRecibo.setEnabled(estado);
        jTFComentarios.setEnabled(estado);
        jCBCodEmpresa.setEnabled(estado);
        jCBCodLocal.setEnabled(estado);
    }
    
    private String getInteresCobro(){
        String result = "";
        try{
            String sql = "SELECT valor FROM parametros WHERE parametro = 'CAJA_INTERES_COBRO_CUENTAS_CLIENTES'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("valor");
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
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
    }
    
    private void setEstadoBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBSalir.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBConfirmarCobro.setEnabled(true);
        jBVerDetalle.setEnabled(true);
        jBMarcarTodos.setEnabled(true);
        jBDesmarcarTodos.setEnabled(true);
        jBConfirmarDocs.setEnabled(true);
        jBBuscarDocs.setEnabled(true);
        jBImprimir.setEnabled(false);
    }
    
    private void setEstadoBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBConfirmarCobro.setEnabled(false);
    }
    
    private void setEstadoBotonesGrabado(){
        jBNuevo.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBConfirmarCobro.setEnabled(false);
        jBBuscarDocs.setEnabled(false);
        jBVerDetalle.setEnabled(false);
        jBMarcarTodos.setEnabled(false);
        jBDesmarcarTodos.setEnabled(false);
        jBConfirmarDocs.setEnabled(false);
        jBImprimir.setEnabled(true);
    }
    
    private void configTabla(){
        dtmDocPendientes = (DefaultTableModel)jTDetalleDocs.getModel();
        
        jTDetalleDocs.getColumnModel().getColumn(0).setPreferredWidth(40); // nro comprob
        jTDetalleDocs.getColumnModel().getColumn(1).setPreferredWidth(20); // tipo doc 
        jTDetalleDocs.getColumnModel().getColumn(2).setPreferredWidth(20); // cuota
        jTDetalleDocs.getColumnModel().getColumn(3).setPreferredWidth(20); // cantidad cuota
        jTDetalleDocs.getColumnModel().getColumn(4).setPreferredWidth(60); // fec emision 
        jTDetalleDocs.getColumnModel().getColumn(5).setPreferredWidth(60); // fec vencimiento 
        jTDetalleDocs.getColumnModel().getColumn(6).setPreferredWidth(20); // dias vencidos
        jTDetalleDocs.getColumnModel().getColumn(7).setPreferredWidth(50); // valor cuota
        jTDetalleDocs.getColumnModel().getColumn(8).setPreferredWidth(50); // valor interes
        jTDetalleDocs.getColumnModel().getColumn(9).setPreferredWidth(50); // cuota + interes
        jTDetalleDocs.getColumnModel().getColumn(10).setPreferredWidth(20); // seleccionar
        
        utiles.Utiles.punteroTablaF(jTDetalleDocs, this);        
        jTDetalleDocs.setFont(new Font("Tahoma", 1, 10) );
        jTDetalleDocs.setRowHeight(18);
    }
    
    private void limpiarTabla(){
        for(int i = 0; i < jTDetalleDocs.getRowCount(); i++){
            dtmDocPendientes.removeRow(i);
            i--;
        }
    }
    
    private void llenarTabla(){
        StatementManager sm = new StatementManager();
        String codCliente = jTFCodCliente.getText().trim();
        int pct_interes = Integer.parseInt(jTFInteres.getText().trim());
        double total_cuenta = 0;
        
        String sql = "SELECT DISTINCT nro_comprob, tip_comprob, nro_cuota, can_cuota, to_char(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "to_char(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, "
                   + "CASE WHEN current_date - fec_vencimiento::date <= 0 THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos, monto_cuota "
                   + "FROM venta_det_cuotas "
                   + "WHERE cod_cliente = " + codCliente + " AND nro_pago = 0 AND estado = 'V' "
                   + "ORDER BY nro_comprob";
        
        try{
            sm.TheSql = sql;
            System.out.println("SQL DOCS PENDIENTES: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm.TheResultSet.getString("nro_comprob");
                    row[1] = sm.TheResultSet.getString("tip_comprob");
                    row[2] = sm.TheResultSet.getString("nro_cuota");
                    row[3] = sm.TheResultSet.getString("can_cuota");
                    row[4] = sm.TheResultSet.getString("fec_emision");
                    row[5] = sm.TheResultSet.getString("fec_vencimiento");
                    row[6] = sm.TheResultSet.getInt("dias_vencidos");
                    row[7] = sm.TheResultSet.getDouble("monto_cuota");
                    
                    String tipo_comprob = sm.TheResultSet.getString("tip_comprob");
                    double cuota = sm.TheResultSet.getDouble("monto_cuota");
                    double diasVencidos = sm.TheResultSet.getInt("dias_vencidos");
                    double interes = 0;
                    
                    if(tipo_comprob.equalsIgnoreCase("CRE") || tipo_comprob.equalsIgnoreCase("NCC")){
                        cuota = cuota * -1;
                    }else{
                        // -- calculo interes
                        interes = ((cuota * pct_interes)/100) * (diasVencidos/30);
                    }
                    
                    row[8] = Math.round(interes);
                    row[9] = Math.round(cuota + interes);
                    row[10] = false;
                    total_cuenta += cuota;
                    dtmDocPendientes.addRow(row);
                }
                jTDetalleDocs.updateUI();
                jTFTotalCuenta.setText(decimalFormat.format(total_cuenta));
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No existen documentos pendientes de pago!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void getDatosCliente(String codigo){
        try{
            String sql = "SELECT cliente.razon_soc, cuenta.cod_cuenta "
                       + "FROM cliente "
                       + "INNER JOIN cuenta "
                       + "ON cliente.cod_cliente = cuenta.cod_cliente "
                       + "WHERE cliente.cod_cliente = " + codigo; 
            System.out.println("SQL DATOS CLIENTE: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreCliente.setText(rs.getString("razon_soc"));
                    jTFCodCuentaCliente.setText(rs.getString("cod_cuenta"));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
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
    
    private String getSecuenciaCobroCliente(){
        String result = "";
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('secuencia_cobro_cli') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    result = TheQuery.TheResultSet.getString(1);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de cobro cliente", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException sqlex) {
                JOptionPane.showMessageDialog(this, "Error en la Obtencion de Secuencia de cobro cliente!! ", "ATENCION", JOptionPane.WARNING_MESSAGE);
                InfoErrores.errores(sqlex);
            }finally
            {
                TheQuery.CerrarStatement();
                TheQuery.ClearDBManagerstmt();
                TheQuery = null;
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de cobro cliente", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private String getNroRecibo(){
        String result = "0";
        try
        {
            ResultSet rs = DBManager.ejecutarDSL("SELECT MAX(nro_recibo) AS nro_recibo FROM pagocli_cab");            
            if(rs != null)
            {
                if(rs.next())
                {
                    result = String.valueOf(rs.getInt(1) + 1);
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally
        {
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private void totalizarDocsSeleccionados(){
        int rows = dtmDocPendientes.getRowCount();
        double total_docs_seleccionados = 0;
        double total_interes = 0;      
        total_docs_mas_interes = 0;
        
        for(int i = 0; i < rows; i++){
            
            if(dtmDocPendientes.getValueAt(i, 10).toString() == "true"){
                total_docs_seleccionados += Integer.parseInt(dtmDocPendientes.getValueAt(i, 7).toString().replace(".0", ""));
                total_interes += Integer.parseInt(dtmDocPendientes.getValueAt(i, 8).toString().replace(".0", ""));
                total_docs_mas_interes += Integer.parseInt(dtmDocPendientes.getValueAt(i, 9).toString().replace(".0", ""));
            }
        }
        
        jTFTotalSeleccionado.setText(decimalFormat.format(total_docs_seleccionados));
        jTFTotalInteres.setText(decimalFormat.format(total_interes));
        jTFTotalCobro.setText(decimalFormat.format(total_docs_mas_interes));
    }
    
    private void marcarTodos(){
        for(int i = 0; i < dtmDocPendientes.getRowCount(); i++){
            dtmDocPendientes.setValueAt(new Boolean(true), i, 10);
        }
    }
    
    private void desMarcarTodos(){
        for(int i = 0; i < dtmDocPendientes.getRowCount(); i++){
            dtmDocPendientes.setValueAt(new Boolean(false), i, 10);
        }
    }
    
    private void getDatosTurnoAbierto(){
        try{
            String sql = "SELECT nro_turno, cod_cajero FROM turno WHERE cod_caja = " + FormMain.codCaja + " AND fec_cierre_turno IS null";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    nroTurno = rs.getString("nro_turno");
                    codCajero = rs.getString("cod_cajero");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        System.out.println("NRO DE TURNO: " + nroTurno + "\nCOD CAJERO: " + codCajero);
    }
    
    private String getDatosCajero(String codCajero){
        String result = "";
        try{
            String sql = "SELECT (cip_empleado || ' - ' || nombre || ' ' || apellido) AS nombre FROM empleado WHERE cod_empleado = " + codCajero;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("nombre");
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
    
    private String getRucCliente(int codCliente){
        String ruc_cliente = "";
        try{
            String sql = "SELECT ruc_cliente FROM cliente WHERE cod_cliente = " + codCliente;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    ruc_cliente = rs.getString("ruc_cliente");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return ruc_cliente;
    }
    
    private void imprimirRecibo(){
        
        String razon_soc_empresa = jLRazonSocEmpresa.getText().trim();
        String nro_recibo = jTFNroRecibo.getText().trim();
        String fecha_emision = jTFFecCobro.getText().trim();
        int nro_pago = Integer.parseInt(jTFNroPago.getText().trim());
        int cod_cliente = Integer.parseInt(jTFCodCliente.getText().trim());
        String nombre_cliente = cod_cliente + " - " +jLNombreCliente.getText().trim();
        String ruc_cliente = getRucCliente(cod_cliente);
        getDatosCabeceraCobro(nro_pago);

        // -- Convertir en texto el monto
            String mon;
            String mont = String.valueOf(montoCobrado).substring(0, String.valueOf(montoCobrado).length() - 2); // ejemplo 300,000.00 deja en 300000.
            if(mont.length() > 6){ // si alcanza el millon
                mont = mont.substring(0, mont.length());
                mon = mont.replace(",", ""); // deja en 300000
            }else{
                mon = mont.replace(",", ""); // deja en 300000
            }
            int monto = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = numero.convertirLetras(monto);
        // -- Fin de convertir en texto el monto
        
        String sql = "SELECT DISTINCT nro_comprob, tip_comprob, to_char(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "to_char(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, vlr_interes, monto_comprob, "
                   + "(vlr_interes + monto_comprob) AS cuota_mas_interes "
                   + "FROM pagocli_det "
                   + "WHERE nro_pago = " + nro_pago + " AND cod_cliente = " + cod_cliente;
        
        System.out.println("IMPRESION DE RECIBO POR COBRO DE CLIENTE: " + sql);
        
        try{
            LibReportes.parameters.put("pRazonSocEmpresa", razon_soc_empresa);
            LibReportes.parameters.put("pActividadEmpresa", actividadEmpresa);
            LibReportes.parameters.put("pDireccionEmpresa", direccionEmpresa);
            LibReportes.parameters.put("pCiudadEmpresa", ciudadEmpresa);
            LibReportes.parameters.put("pTelEmpresa", telEmpresa);
            LibReportes.parameters.put("pNroRecibo", nro_recibo);
            LibReportes.parameters.put("pFechaEmision", fecha_emision);
            LibReportes.parameters.put("pNroPago", nro_pago);
            LibReportes.parameters.put("pFecActual", utiles.Utiles.getSysDateTimeString());
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + FormMain.nombreUsuario);
            LibReportes.parameters.put("pNombreCliente", nombre_cliente);
            LibReportes.parameters.put("pRucCliente", ruc_cliente);
            LibReportes.parameters.put("pTotalEnLetras", montoTxt + " .-");
            LibReportes.parameters.put("pCodCliente", cod_cliente);
            LibReportes.parameters.put("pMontoCobrado", montoCobrado);
            LibReportes.parameters.put("pMontoVuelto", vuelto);
            LibReportes.parameters.put("pMontoCredito", montoCredito);
            LibReportes.parameters.put("pMontoDebito", montoDebito);
            LibReportes.parameters.put("pCajero", getDatosCajero(jTFCodEmpleado.getText().trim()));
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "recibo_cobro_cliente_laser");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private void getDatosCabeceraCobro(int nroPago){
        
        try{
            String sql = "SELECT monto_pago, monto_vuelto, monto_credito, monto_debito FROM pagocli_cab "
                       + "WHERE nro_pago = " + nroPago;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    montoCobrado = rs.getDouble("monto_pago");
                    vuelto = rs.getDouble("monto_vuelto");
                    montoDebito = rs.getDouble("monto_debito");
                    montoCredito = rs.getDouble("monto_credito");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        
    }
    
    private void getDatosEmpresaReport(){
    
        try{
            String sql = "SELECT empresa.actividad, local.direccion, local.telefono, local.ciudad "
                       + "FROM empresa "
                       + "INNER JOIN local "
                       + "ON empresa.cod_empresa = local.cod_empresa "; 
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    actividadEmpresa = rs.getString("actividad");
                    direccionEmpresa = rs.getString("direccion");
                    telEmpresa = rs.getString("telefono");
                    ciudadEmpresa = rs.getString("ciudad");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    // esto hice porque pierde el foco al querer hacer 2 cobranzas seguidas
    private void cargarTablaDetalleUltimoCobro(String nroPago, String codCliente){
        //limpiarTabla();
        dtmDocPendientes.setRowCount(0);
        cargarDetalles(nroPago, codCliente);
    }
    
    private void cargarDetalles(String nroPago, String codCliente){
        StatementManager sm = new StatementManager();
        double pct_interes = Double.parseDouble(jTFInteres.getText().trim());
        
        String sql = "SELECT DISTINCT nro_comprob, tip_comprob, nro_cuota, can_cuota, to_char(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "to_char(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, "
                   + "CASE WHEN fec_recibo::date - fec_vencimiento::date <= 0 THEN 0 ELSE fec_recibo::date - fec_vencimiento END AS dias_vencidos, monto_cuota "
                   + "FROM venta_det_cuotas "
                   + "WHERE cod_cliente = " + codCliente + " AND nro_pago = " + nroPago;
        
        try{
            sm.TheSql = sql;
            System.out.println("DOCS ULTIMO COBRO: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm.TheResultSet.getString("nro_comprob");
                    row[1] = sm.TheResultSet.getString("tip_comprob");
                    row[2] = sm.TheResultSet.getString("nro_cuota");
                    row[3] = sm.TheResultSet.getString("can_cuota");
                    row[4] = sm.TheResultSet.getString("fec_emision");
                    row[5] = sm.TheResultSet.getString("fec_vencimiento");
                    row[6] = sm.TheResultSet.getInt("dias_vencidos");
                    row[7] = sm.TheResultSet.getDouble("monto_cuota");
                    
                    String tipo_comprob = sm.TheResultSet.getString("tip_comprob");
                    double cuota = sm.TheResultSet.getDouble("monto_cuota");
                    double diasVencidos = sm.TheResultSet.getInt("dias_vencidos");
                    double interes = 0;
                    
                    if(tipo_comprob.equalsIgnoreCase("CRE") || tipo_comprob.equalsIgnoreCase("NCC")){
                        cuota = cuota * -1;
                    }else{
                        // -- calculo interes
                        interes = ((cuota * pct_interes)/100) * (diasVencidos/30);
                    }
                    
                    row[8] = Math.round(interes);
                    row[9] = Math.round(cuota + interes);
                    row[10] = false;
                    dtmDocPendientes.addRow(row);
                }
                jTDetalleDocs.updateUI();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    /**
     * Encargada de transferir el foco al siguiente componente.
     */
    private ActionListener  tranfiereElFoco = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // Se transfiere el foco al siguiente elemento.
            ((Component) arg0.getSource()).transferFocus();
        }
    };
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLRazonSocEmpresa = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLLocal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jTFCodCuentaCliente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFInteres = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTFFecCobro = new javax.swing.JTextField();
        jBBuscarDocs = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jTFCodEmpleado = new javax.swing.JTextField();
        jLNombreEmpleado = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTFNroPago = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFNroRecibo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFTotalCuenta = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFComentarios = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFEstado = new javax.swing.JTextField();
        jLEstado = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalleDocs = new javax.swing.JTable();
        jBVerDetalle = new javax.swing.JButton();
        jBMarcarTodos = new javax.swing.JButton();
        jBDesmarcarTodos = new javax.swing.JButton();
        jBConfirmarDocs = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTFNroTurno = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFCodCaja = new javax.swing.JTextField();
        jBMsg = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTFTotalSeleccionado = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFTotalInteres = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFTotalCobro = new javax.swing.JTextField();
        jBConfirmarCobro = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Cobro de Clientes");

        mainPanel.setBackground(new java.awt.Color(204, 255, 204));
        mainPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                mainPanelFocusGained(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("REGISTRO DE COBRO DE CLIENTES");

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

        jLRazonSocEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLRazonSocEmpresa.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Local:");

        jLLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLLocal.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodClienteFocusGained(evt);
            }
        });
        jTFCodCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodClienteKeyPressed(evt);
            }
        });

        jLNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreCliente.setText("***");

        jTFCodCuentaCliente.setEditable(false);
        jTFCodCuentaCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCuentaCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Interés");

        jTFInteres.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFInteres.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFInteresFocusGained(evt);
            }
        });
        jTFInteres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFInteresKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("%");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Fecha de Cobro:");

        jTFFecCobro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecCobro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFFecCobro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecCobroFocusGained(evt);
            }
        });
        jTFFecCobro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecCobroKeyPressed(evt);
            }
        });

        jBBuscarDocs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscarDocs.setText("Buscar Docs");
        jBBuscarDocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarDocsActionPerformed(evt);
            }
        });
        jBBuscarDocs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBBuscarDocsKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Cobrador:");

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

        jLNombreEmpleado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpleado.setText("***");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Nro Pago:");

        jTFNroPago.setEditable(false);
        jTFNroPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Nro Recibo:");

        jTFNroRecibo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroRecibo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroReciboFocusGained(evt);
            }
        });
        jTFNroRecibo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroReciboKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Total cuenta:");

        jTFTotalCuenta.setEditable(false);
        jTFTotalCuenta.setBackground(new java.awt.Color(255, 153, 153));
        jTFTotalCuenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalCuenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCuenta.setText("0");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Comentarios:");

        jTFComentarios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFComentarios.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFComentariosFocusGained(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Estado:");

        jTFEstado.setEditable(false);
        jTFEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFEstado.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLEstado.setText("***");

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Documentos Pendientes de Cobro"));
        jPanel2.setFocusable(false);
        jPanel2.setRequestFocusEnabled(false);

        jTDetalleDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro Comprob", "Tipo Doc", "Cuota", "Cant Cuota", "Fec Emisión", "Fec Venc", "Días Venc", "Vlr Cuota", "Vlr Interés", "Cuota + Interés", "Seleccionar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetalleDocs.setFocusable(false);
        jTDetalleDocs.setRequestFocusEnabled(false);
        jTDetalleDocs.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTDetalleDocs);
        if (jTDetalleDocs.getColumnModel().getColumnCount() > 0) {
            jTDetalleDocs.getColumnModel().getColumn(0).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(1).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(2).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(3).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(4).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(5).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(6).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(7).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(8).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(9).setResizable(false);
            jTDetalleDocs.getColumnModel().getColumn(10).setResizable(false);
        }

        jBVerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/detalles24.png"))); // NOI18N
        jBVerDetalle.setText("Ver detalle");
        jBVerDetalle.setToolTipText("Ver detalle del documento seleccionado");
        jBVerDetalle.setEnabled(false);
        jBVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBVerDetalleActionPerformed(evt);
            }
        });

        jBMarcarTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/selectall24.png"))); // NOI18N
        jBMarcarTodos.setToolTipText("Marcar todos los documentos ");
        jBMarcarTodos.setEnabled(false);
        jBMarcarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMarcarTodosActionPerformed(evt);
            }
        });

        jBDesmarcarTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/unselectall24.png"))); // NOI18N
        jBDesmarcarTodos.setToolTipText("Des-marcar todos los documentos");
        jBDesmarcarTodos.setEnabled(false);
        jBDesmarcarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDesmarcarTodosActionPerformed(evt);
            }
        });

        jBConfirmarDocs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarDocs.setText("Confirmar");
        jBConfirmarDocs.setToolTipText("Confirmar documentos seleccionados");
        jBConfirmarDocs.setEnabled(false);
        jBConfirmarDocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarDocsActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Nro. Turno:");

        jTFNroTurno.setEditable(false);
        jTFNroTurno.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroTurno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("Nro. Caja:");

        jTFCodCaja.setEditable(false);
        jTFCodCaja.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jBMsg.setText("Mgs");
        jBMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMsgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(95, 95, 95)
                        .addComponent(jBMsg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBVerDetalle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBMarcarTodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBDesmarcarTodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBConfirmarDocs)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBVerDetalle)
                        .addComponent(jBMarcarTodos)
                        .addComponent(jBDesmarcarTodos)
                        .addComponent(jBConfirmarDocs))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(jTFNroTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)
                        .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBMsg)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConfirmarDocs, jBDesmarcarTodos, jBMarcarTodos, jBVerDetalle});

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Totales"));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Total Seleccionado:");

        jTFTotalSeleccionado.setEditable(false);
        jTFTotalSeleccionado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalSeleccionado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalSeleccionado.setText("0");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Interés:");

        jTFTotalInteres.setEditable(false);
        jTFTotalInteres.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalInteres.setText("0");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Total:");

        jTFTotalCobro.setEditable(false);
        jTFTotalCobro.setBackground(new java.awt.Color(255, 153, 153));
        jTFTotalCobro.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalCobro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCobro.setText("0");

        jBConfirmarCobro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBConfirmarCobro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarCobro.setText("Confirmar Cobro");
        jBConfirmarCobro.setEnabled(false);
        jBConfirmarCobro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarCobroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTotalSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFTotalCobro, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .addComponent(jTFTotalInteres, javax.swing.GroupLayout.Alignment.LEADING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBConfirmarCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFTotalSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFTotalInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTFTotalCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jBConfirmarCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Opciones"));

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jBNuevoMouseClicked(evt);
            }
        });
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
            }
        });

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
        jBImprimir.setEnabled(false);
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jBNuevo)
                        .addGap(6, 6, 6)
                        .addComponent(jBBuscar))
                    .addComponent(jBImprimir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jBSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscar, jBCancelar, jBNuevo});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBCancelar)
                            .addComponent(jBBuscar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBSalir)
                            .addComponent(jBImprimir)))
                    .addComponent(jBNuevo))
                .addGap(32, 32, 32))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscar, jBCancelar, jBImprimir, jBNuevo, jBSalir});

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel1))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLRazonSocEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFFecCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jBBuscarDocs, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFTotalCuenta))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jTFComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLRazonSocEmpresa)
                        .addComponent(jLabel4)
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLLocal)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente)
                    .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTFInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jTFFecCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscarDocs))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpleado)
                    .addComponent(jLabel9)
                    .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTFTotalCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTFComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        //this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            BuscaCliente bCliente = new BuscaCliente(new JFrame(), true);
            bCliente.pack();
            bCliente.setVisible(true);
            if(bCliente.codigo != 0){
                jTFCodCliente.setText(String.valueOf(bCliente.codigo));
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCodCliente.getText().trim().equals("0")){
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFCodCliente.requestFocus();
            jLNombreCliente.setText("");
            }else if(jTFCodCliente.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else{
                getDatosCliente(jTFCodCliente.getText().trim()); 
                jTFTotalCuenta.setText("0.00");
                jTFInteres.requestFocus();
            }
            
            if(jTDetalleDocs.getRowCount() > 0){
                System.out.println("LIMPIANDO TABLA");
                limpiarTabla();
            }else{
                System.out.println("NO LIMPIA LA TABLA");
            }
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFInteresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFInteresKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecCobro.requestFocus();
        }
    }//GEN-LAST:event_jTFInteresKeyPressed

    private void jTFFecCobroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecCobroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscarDocs.requestFocus();
        }
    }//GEN-LAST:event_jTFFecCobroKeyPressed

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

    private void jTFInteresFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFInteresFocusGained
        jTFInteres.selectAll();
    }//GEN-LAST:event_jTFInteresFocusGained

    private void jTFFecCobroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecCobroFocusGained
        jTFFecCobro.selectAll();
    }//GEN-LAST:event_jTFFecCobroFocusGained

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroRecibo.requestFocus();
        }                
    }//GEN-LAST:event_jTFCodEmpleadoKeyPressed

    private void jTFNroReciboFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroReciboFocusGained
        jTFNroRecibo.selectAll();
    }//GEN-LAST:event_jTFNroReciboFocusGained

    private void jTFNroReciboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroReciboKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFComentarios.requestFocus();
        }
    }//GEN-LAST:event_jTFNroReciboKeyPressed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        //JOptionPane.showMessageDialog(this, "Para búsqueda Presione F1", "Información", JOptionPane.INFORMATION_MESSAGE);
        setEstadoCampos(true);
        setEstadoBotonesNuevo();        
        llenarCampos();          
        jTFCodCliente.addFocusListener(new Focus());
        jTFCodCliente.requestFocus();
        
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBBuscarDocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarDocsActionPerformed
        limpiarTabla();
        llenarTabla();
        jTFTotalSeleccionado.setText("0");
        jTFTotalInteres.setText("0");
        jTFTotalCobro.setText("0");
    }//GEN-LAST:event_jBBuscarDocsActionPerformed

    private void jBBuscarDocsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBBuscarDocsKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodEmpleado.grabFocus();
        }
    }//GEN-LAST:event_jBBuscarDocsKeyPressed

    private void jTFCodEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusLost
        if(jTFCodEmpleado.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío. Favor verifique.", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.requestFocus();
        }else if(jTFCodEmpleado.getText().trim().equals("0")){
            JOptionPane.showMessageDialog(this, "Informe de un cobrador válido!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodEmpleado.requestFocus();
        }else {
            jLNombreEmpleado.setText(getNombreEmpleado(jTFCodEmpleado.getText().trim()));
        }
    }//GEN-LAST:event_jTFCodEmpleadoFocusLost

    private void jTFComentariosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFComentariosFocusGained
        jTFComentarios.selectAll();
    }//GEN-LAST:event_jTFComentariosFocusGained

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoBotonesCancelar();
        setEstadoCampos(false);
        limpiarTabla();
        jBNuevo.grabFocus();
        jTFTotalCuenta.setText("0");
        jTFTotalSeleccionado.setText("0");
        jTFTotalInteres.setText("0");
        jTFTotalCobro.setText("0");
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBConfirmarDocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarDocsActionPerformed
        int cantSeleccionada = 0; 
        for(int i = 0; i < dtmDocPendientes.getRowCount(); i++){
            if(jTDetalleDocs.getValueAt(i, 10).toString() == "true"){
                cantSeleccionada += 1;
            }
        }
        if(cantSeleccionada == 0){
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos 1 documento!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            totalizarDocsSeleccionados();
        }else{
            totalizarDocsSeleccionados();
        }
    }//GEN-LAST:event_jBConfirmarDocsActionPerformed

    private void jBMarcarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMarcarTodosActionPerformed
        marcarTodos();
    }//GEN-LAST:event_jBMarcarTodosActionPerformed

    private void jBDesmarcarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDesmarcarTodosActionPerformed
        desMarcarTodos();
    }//GEN-LAST:event_jBDesmarcarTodosActionPerformed

    private void jBConfirmarCobroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarCobroActionPerformed
        int total = Integer.parseInt(jTFTotalCobro.getText().trim().replace(",", ""));
        if(total == 0 || total < 0){
            JOptionPane.showMessageDialog(this, "Favor verificar datos seleccionados!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            RegistroCobroClientesFormaPago forma_pago = new RegistroCobroClientesFormaPago(new JFrame(), true);
            forma_pago.pack();
            forma_pago.setVisible(true);
        }
    }//GEN-LAST:event_jBConfirmarCobroActionPerformed

    private void jBMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMsgActionPerformed
        JOptionPane.showMessageDialog(this, "Cobro realizado con suceso!", "Registro de cobro", JOptionPane.INFORMATION_MESSAGE);
        setEstadoBotonesGrabado();
        cargarTablaDetalleUltimoCobro(jTFNroPago.getText().trim(), jTFCodCliente.getText().trim());
        //setEstadoCampos(false);
        //JOptionPane.showConfirmDialog(this, "Desea imprimir el recibo?", "Impresión de recibo", JOptionPane.YES_NO_OPTION);
        
    }//GEN-LAST:event_jBMsgActionPerformed

    private void mainPanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mainPanelFocusGained
        jTFCodCliente.requestFocus();
    }//GEN-LAST:event_mainPanelFocusGained

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        imprimirRecibo();
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void jBNuevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBNuevoMouseClicked
        jBNuevo.setNextFocusableComponent(jTFCodCliente);
    }//GEN-LAST:event_jBNuevoMouseClicked

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBVerDetalleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBVerDetalleActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroCobroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroCobroClientes dialog = new RegistroCobroClientes(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBBuscarDocs;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBConfirmarCobro;
    private javax.swing.JButton jBConfirmarDocs;
    private javax.swing.JButton jBDesmarcarTodos;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBMarcarTodos;
    private javax.swing.JButton jBMsg;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JButton jBVerDetalle;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLEstado;
    private javax.swing.JLabel jLLocal;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpleado;
    private javax.swing.JLabel jLRazonSocEmpresa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTDetalleDocs;
    private javax.swing.JTextField jTFCodCaja;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCuentaCliente;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFComentarios;
    private javax.swing.JTextField jTFEstado;
    private javax.swing.JTextField jTFFecCobro;
    private javax.swing.JTextField jTFInteres;
    private javax.swing.JTextField jTFNroPago;
    javax.swing.JTextField jTFNroRecibo;
    private javax.swing.JTextField jTFNroTurno;
    private javax.swing.JTextField jTFTotalCobro;
    private javax.swing.JTextField jTFTotalCuenta;
    private javax.swing.JTextField jTFTotalInteres;
    private javax.swing.JTextField jTFTotalSeleccionado;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
