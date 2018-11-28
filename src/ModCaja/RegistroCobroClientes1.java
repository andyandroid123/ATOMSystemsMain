/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import ModFinanciero.informes.DetalleDocVenta;
import controls.ClienteCtrl;
import controls.EmpleadoCtrl;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import views.busca.BuscaCobroCliente;
import views.busca.DlgConsultas;
import views.busca.DlgConsultasCuentasMonedas;

/**
 *
 * @author Andres
 */
public class RegistroCobroClientes1 extends javax.swing.JDialog {

    private static String fecVigencia = "";
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    public static DefaultTableModel dtmDocPendientes;    
    public static DefaultTableModel dtmFormaPago;    
    public static double total_docs_mas_interes;
    String codCajero, nroTurno;
    ResultSet rsConsultas;
    String modulo;
    String nroPago;
    
    // ** FORMA DE PAGO **
    double total_docs_seleccionados = 0, total_forma_pago = 0, diferencia = 0; 
    String tipo_cuenta = "";
    
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    double montoCobrado, vuelto, montoDebito, montoCredito;
    
    public RegistroCobroClientes1(java.awt.Frame parent, boolean modal, String modulo, String nroPago) {
        super(parent, modal);
        initComponents();
        configCampos();
        getFecVigencia();
        llenarCombos();
        getDatosTurnoAbierto();
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        setEstadoCampos(false);
        configTablaDetalleDocs();
        configTablaFormaPago();
        getDatosEmpresaReport();
        this.modulo = modulo.trim();
        this.nroPago = nroPago;
        System.out.println("DE QUE MODULO FUE LLAMADO: " + modulo);
        if(modulo.equalsIgnoreCase("COBRANZAS")){
            getFecVigencia();
            llenarCombos();
            getDatosTurnoAbierto();
            jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
            jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
            setEstadoCampos(false);
            configTablaDetalleDocs();
            configTablaFormaPago();
            getDatosEmpresaReport();
        }
        
        if(modulo.equalsIgnoreCase("CONSULTA-COBRO")){
            limpiarTablaDetalleDocs();
            limpiarTablaFormaPago();
            cargaDetallesCobro(nroPago);
            setEstadoCampos(false);
            jBNuevo.setEnabled(false);
        }
        
        System.out.println("RESULT DEL IF 1: " + (modulo.equalsIgnoreCase("COBRANZAS")));
        System.out.println("RESULT DEL IF 2: " + (modulo.equalsIgnoreCase("CONSULTA-COBRO")));
        jTFEstado.setBackground(new Color(240,240,240));
    }

    private void configCampos(){
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFInteres.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodEmpleado.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFNroRecibo.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFComentarios.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFFecCobro.setInputVerifier(new FechaInputVerifier(jTFFecCobro));
        jTFCodCuenta.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFNroDoc.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFCodBco.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFValor.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFFecEmision.setInputVerifier(new FechaInputVerifier(jTFFecEmision));
        jTFFecVenc.setInputVerifier(new FechaInputVerifier(jTFFecVenc));
        
        jTFCodCliente.addFocusListener(new Focus());
        jTFInteres.addFocusListener(new Focus());
        jTFCodEmpleado.addFocusListener(new Focus());
        jTFNroRecibo.addFocusListener(new Focus());
        jTFComentarios.addFocusListener(new Focus());
        jTFFecCobro.addFocusListener(new Focus());
        jTFCodCuenta.addFocusListener(new Focus());
        jTFNroDoc.addFocusListener(new Focus());
        jTFCodBco.addFocusListener(new Focus());
        jTFValor.addFocusListener(new Focus());
        jTFFecEmision.addFocusListener(new Focus());
        jTFFecVenc.addFocusListener(new Focus());
        jTFCodCta.addFocusListener(new Focus());
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
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
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
    
    private void setEstadoCampos(boolean estado){
        jTFCodCliente.setEnabled(estado);
        jTFInteres.setEnabled(estado);
        jTFFecCobro.setEnabled(estado);
        jTFCodEmpleado.setEnabled(estado);
        jTFNroRecibo.setEnabled(estado);
        jTFComentarios.setEnabled(estado);
        jCBCodEmpresa.setEnabled(estado);
        jCBCodLocal.setEnabled(estado);
        jTFCodCta.setEnabled(estado);
        jTFNroDoc.setEnabled(estado);
        jTFCodBco.setEnabled(estado);
        jTFFecVenc.setEnabled(estado);
        jTFValor.setEnabled(estado);
    }
    
    private void configTablaDetalleDocs(){
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
        jTDetalleDocs.getColumnModel().getColumn(11).setMaxWidth(0); // cód caja
        jTDetalleDocs.getColumnModel().getColumn(11).setMinWidth(0); // cód caja
        jTDetalleDocs.getTableHeader().getColumnModel().getColumn(11).setMaxWidth(0); // cód caja
        jTDetalleDocs.getTableHeader().getColumnModel().getColumn(11).setMinWidth(0); // cód caja    
        
        utiles.Utiles.punteroTablaF(jTDetalleDocs, this);        
        jTDetalleDocs.setFont(new Font("Tahoma", 1, 10) );
        jTDetalleDocs.setRowHeight(18);
    }
    
    private void configTablaFormaPago(){
        dtmFormaPago = (DefaultTableModel)jTFormaPago.getModel();
        
        jTFormaPago.getColumnModel().getColumn(0).setPreferredWidth(20); // cta
        jTFormaPago.getColumnModel().getColumn(1).setPreferredWidth(50); // descripcion 
        jTFormaPago.getColumnModel().getColumn(2).setPreferredWidth(20); // tipo
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(50); // nro doc
        jTFormaPago.getColumnModel().getColumn(4).setPreferredWidth(10); // bco 
        jTFormaPago.getColumnModel().getColumn(5).setPreferredWidth(50); // nombre bco
        jTFormaPago.getColumnModel().getColumn(6).setPreferredWidth(30); // fec emision
        jTFormaPago.getColumnModel().getColumn(7).setPreferredWidth(30); // fec vencimiento
        jTFormaPago.getColumnModel().getColumn(8).setPreferredWidth(50); // valor 
        jTFormaPago.getColumnModel().getColumn(9).setPreferredWidth(50); // cotizacion
        jTFormaPago.getColumnModel().getColumn(10).setPreferredWidth(50); // valor final 
        
        utiles.Utiles.punteroTablaF(jTFormaPago, this);        
        jTFormaPago.setFont(new Font("Tahoma", 1, 10) );
        jTFormaPago.setRowHeight(18);
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
    
    private void setEstadoBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBSalir.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBVerDetalle.setEnabled(true);
        jBMarcarTodos.setEnabled(true);
        jBDesmarcarTodos.setEnabled(true);
        jBConfirmarDocs.setEnabled(true);
        jBBuscarDocs.setEnabled(true);
        jBImprimir.setEnabled(false);
        jBRefresh.setEnabled(true);
        jBConfirmarFormaPago.setEnabled(true);
    }
    
    private void setEstadoBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBSalir.setEnabled(true);
        jBImprimir.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBBuscarDocs.setEnabled(false);
        jBVerDetalle.setEnabled(false);
        jBMarcarTodos.setEnabled(false);        
        jBDesmarcarTodos.setEnabled(false);
        jBConfirmarDocs.setEnabled(false);
        jBRefresh.setEnabled(false);
        jBConfirmarFormaPago.setEnabled(false);
    }
    
    private void setEstadoBotonesGrabado(){
        jBNuevo.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBBuscarDocs.setEnabled(false);
        jBVerDetalle.setEnabled(false);
        jBMarcarTodos.setEnabled(false);
        jBDesmarcarTodos.setEnabled(false);
        jBConfirmarDocs.setEnabled(false);
        jBImprimir.setEnabled(true);
        jBRefresh.setEnabled(false);
        jBConfirmarFormaPago.setEnabled(false);
    }
    
    private void llenarCampos(){        
        jTFCodCuenta.setText("0");
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
        jLNombreCliente.setText("***");
        jLNombreEmpleado.setText("***");
        jTFDenominacionCta.setText("");
        jTFTipoCuenta.setText("");
        jTFNombreBco.setText("");
        jTFFecEmision.setText("");
        jTFFecVenc.setText("");
        jTFValor.setText("");
        jTFCotizacion.setText("");
        jTFValorFinal.setText("");
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
    
    private int getSecuenciaMovTesoreria(){
        int result = 0;
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('movtesoreria') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    result = TheQuery.TheResultSet.getInt(1);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de moviimiento de tesoreria", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException sqlex) {
                JOptionPane.showMessageDialog(this, "Error en la Obtencion de Secuencia de movimiento de tesoreria!! ", "ATENCION", JOptionPane.WARNING_MESSAGE);
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
    
    private void limpiarTablaFormaPago(){
        for(int i = 0; i < jTFormaPago.getRowCount(); i++){
            dtmFormaPago.removeRow(i);
            i--;
        }
    }
    
    private void limpiarTablaDetalleDocs(){
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
        
        String sql = "SELECT nro_comprob, tip_comprob, nro_cuota, can_cuota, to_char(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "to_char(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, cod_caja, "
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
                    Object [] row = new Object[12];
                    row[0] = sm.TheResultSet.getString("nro_comprob");
                    row[1] = sm.TheResultSet.getString("tip_comprob");
                    row[2] = sm.TheResultSet.getString("nro_cuota");
                    row[3] = sm.TheResultSet.getString("can_cuota");
                    row[4] = sm.TheResultSet.getString("fec_emision");
                    row[5] = sm.TheResultSet.getString("fec_vencimiento");
                    row[6] = sm.TheResultSet.getInt("dias_vencidos");                    
                    
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
                    
                    row[7] = cuota;
                    row[8] = Math.round(interes);
                    row[9] = Math.round(cuota + interes);
                    row[10] = false;
                    row[11] = sm.TheResultSet.getInt("cod_caja");
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
        String razonsoc = "", codcuenta = "";
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
                    jTFCodCuenta.setText(rs.getString("cod_cuenta"));
                    
                    razonsoc = rs.getString("razon_soc");
                    codcuenta = rs.getString("cod_cuenta");
                    System.out.println("RAZON - SOC: " + razonsoc + "\nCOD - CUENTA: " + codcuenta);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
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
        
        jTFTotalDocsSeleccionados.setText(decimalFormat.format(total_docs_seleccionados));
        jTFTotalInteres.setText(decimalFormat.format(total_interes));
        jTFTotalCobro.setText(decimalFormat.format(total_docs_mas_interes));
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
    
    private void getCtaDenCotiz(String codigo){        
        String sql = "SELECT cod_cuenta, moneda.cotiz_compra, denominacion_cta, tipo_cuenta "
                   + "FROM cuenta "
                   + "INNER JOIN moneda "
                   + "ON cuenta.cod_moneda = moneda.cod_moneda "
                   + "WHERE cod_cuenta = " + codigo + " AND cuenta.activa = 'S'";
        System.out.println("GET DENOMINACION COTIZACION CTA: " + sql);
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    tipo_cuenta = rs.getString("tipo_cuenta");
                    System.out.println("TIPO CUENTA DESDE FOCUS LOST: " + tipo_cuenta);
                    if(tipo_cuenta.equals("CRE")){
                    }else{
                        jTFDenominacionCta.setForeground(Color.black);
                        jTFCotizacion.setText(decimalFormat.format(rs.getInt("cotiz_compra")));
                        jTFDenominacionCta.setText(rs.getString("denominacion_cta"));
                        jTFTipoCuenta.setText(rs.getString("tipo_cuenta"));
                    }
                }else{
                    jTFDenominacionCta.setForeground(Color.red);
                    jTFCotizacion.setText("1");
                    jTFTipoCuenta.setText("");
                    jTFDenominacionCta.setText("INEXISTENTE");
                    jTFCodCuenta.requestFocus();
                    jTFCodCuenta.setText("1");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void controlFoco(){
        System.out.println("CONTROL TIPO CUENTA DESDE CONTROL FOCO: " + tipo_cuenta);
        if(tipo_cuenta.equals("EFE")){
            jTFNroDoc.setEditable(false);
            jTFNroDoc.setText("0");
            jTFCodBco.setEditable(false);
            jTFCodBco.setText("1");
            jTFNombreBco.setText("GENERICO");
            jTFFecVenc.setEditable(false);            
            jTFValor.grabFocus();
        }else{
            jTFNroDoc.setEditable(true);
            jTFCodBco.setEditable(true);
            jTFFecVenc.setEditable(true);
            jTFNroDoc.grabFocus();
        }
    }
    
    private String getNombreBanco(String codigo) {
        String banco = "";
        try{
            String sql = "SELECT nombre FROM banco WHERE cod_banco = " + codigo;
            System.out.println("BANCO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    banco = rs.getString("nombre");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return banco;
    }
    
     private void addFormaPagoDetalle(){
        /* COLUMNS
         * cta (string)
         * descripcion (string) 
         * tipo (string)
         * nro doc (string)
         * bco (string)
         * nom bco (string) 
         * fec emision (string)
         * fec venc (string) 
         * valor (double)
         * cotizacion (double)
         * valor final (double)
        */
        
        String cta = jTFCodCta.getText().trim();
        String descCta = jTFDenominacionCta.getText().trim();
        String tipoCta = jTFTipoCuenta.getText().trim();
        String nroDoc = jTFNroDoc.getText().trim();
        String codBco = jTFCodBco.getText().trim();
        String nomBco = jTFNombreBco.getText().trim();
        String fecEmision = jTFFecEmision.getText().trim();
        String fecVenc = jTFFecVenc.getText().trim();
        double valor = Double.parseDouble(jTFValor.getText().trim());
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",", ""));
        double valorFinal = Double.parseDouble(jTFValorFinal.getText().trim().replace(",", ""));
        
        // dtmDetallesVenta.addRow(new Object[]{vCodArticulo, vBarras, vDescripcion, vSigla, vUm, vIva, vPrecioVenta, vCant, vTotal});
        
        dtmFormaPago.addRow(new Object[]{cta, descCta, tipoCta, nroDoc, codBco, nomBco, fecEmision, fecVenc, valor, cotizacion, valorFinal});
    }
    
    private void totalizar(){
        double totalGrilla = 0;
        for(int i = 0; i < dtmFormaPago.getRowCount(); i++){
            totalGrilla += Double.parseDouble(jTFormaPago.getValueAt(i, 10).toString());
        }
        total_docs_seleccionados = Double.parseDouble(jTFTotalCobro.getText().trim().replace(",", ""));
        total_forma_pago = totalGrilla;
        diferencia = total_forma_pago - total_docs_seleccionados;
        jTFTotalFormaPago.setText(decimalFormat.format(total_forma_pago));
        jTFDiferencia.setText(decimalFormat.format(diferencia));
        jTFCodCta.requestFocus();
        if(diferencia < 0){
            jLDiferencia.setText("FALTAN");
            jTFDiferencia.setBackground(new Color(255,153,153));
        }
        
        if(diferencia == 0){
            jLDiferencia.setText("SIN DIFERENCIAS");
            jTFDiferencia.setBackground(new Color(102,255,102));
        }
        
        if(diferencia > 0){
            jLDiferencia.setText("VUELTO");
            jTFDiferencia.setBackground(new Color(102,255,102));
        }
    }
    
    private void estadoBotonNuevo(){
        double total_pago = Double.parseDouble(jTFTotalFormaPago.getText().trim().replace(",", ""));
        if(total_pago == 0 || total_pago < 0 || jTFTotalFormaPago.getText().trim().equals("")){
            jBNuevo.setEnabled(false);
        }
        else{
            jBNuevo.setText("Guardar");
            jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
            jBNuevo.setEnabled(true);
        }
    }
    
    private boolean grabarDatos(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoDatos = leerDetalles();
        if(!problemLeyendoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = false;
        }
        
        return estadoGrabado;
    }
    
    private boolean leerDetalles(){
        int iterador;
        int cantFilasDetallesDocs = jTDetalleDocs.getRowCount();
        
        boolean resultPagoCliCab = false;
        boolean resultPagoCliDet = false;
        boolean resultMovTesoreria = false;
        boolean resultMovCuenta = false;
        boolean resultUpdateVentaDetCuotas= true;
        boolean problemFound = false;
        boolean resultOperacionGrabaCobro = true;
        
        // Datos globales a grabar
        int cobroCodEmpresa = Integer.parseInt(jCBCodEmpresa.getSelectedItem().toString());
        int cobroCodLocal = Integer.parseInt(jCBCodLocal.getSelectedItem().toString());
        String cobroCodSeccion = utiles.Utiles.getCodSectorDefault(jCBCodLocal.getSelectedItem().toString());
        int cobroCodCentroCosto = utiles.Utiles.getCodCentroCosto(Integer.parseInt(cobroCodSeccion));
        int cobroCodCaja = Integer.parseInt(jTFCodCaja.getText().trim());
        int cobroNroTurno = Integer.parseInt(jTFNroTurno.getText().trim());
        int cobroNroPago = Integer.parseInt(jTFNroPago.getText().trim());
        int cobroCodCliente = Integer.parseInt(jTFCodCliente.getText().trim());
        String cobroFecPago = jTFFecCobro.getText().trim();
        double montoPago = Double.parseDouble(jTFTotalFormaPago.getText().trim().replace(",", ""));
        int montoRetenido = 0, nroAsiento = 0;
        String estado = "V";
        int cobroCodCobrador = Integer.parseInt(jTFCodEmpleado.getText().trim());
        String cobroNroRecibo = jTFNroRecibo.getText().trim();
        String cobroObservacion = jTFComentarios.getText().trim();
        double montoVuelto = 0, montoCredito = 0, montoDebito = 0;
        String cobroFechaTesoreria = getFechaTesoreria();
        
        // Cobro Cabecera - si es que hay diferencia graba en venta_det_cuotas 
        
        // -- datos para el deb/cre que genera por la diferencia
        montoVuelto = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", ""));
        String tipo_documento_venta_det = "";
        String obs_venta_det = "DIFERENCIA EN COBRO NRO " + cobroNroPago;
        int cod_sector_venta_det = Integer.parseInt(utiles.Utiles.getCodSectorDefault(String.valueOf(cobroCodLocal)));
        String vencimiento_venta_det = calculaVencimiento(cobroFecPago, getCondicionPlazoCreditoCliente(cobroCodCliente));
        String tipo_insert = "";
        // -- fin datos para el deb/cre que genera por la diferencia
        
        if(montoVuelto == 0){
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
        }else if(montoVuelto > 0){
            int grabarMas = JOptionPane.showConfirmDialog(this, "Qué hacer con la diferencia:\nSI = Generar CREDITO en la cuenta del cliente.\nNO = Devolver vuelto.",
                                                          "Diferencia", JOptionPane.YES_NO_CANCEL_OPTION);
            if(grabarMas == 0){
                montoCredito = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) ;
                montoVuelto = 0;
                montoDebito = 0;
                tipo_documento_venta_det = "CRE";
                tipo_insert = "SOBRANTE - CREDITO";
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                insertDiferencia(cobroCodEmpresa, cobroCodLocal, cod_sector_venta_det, cobroCodCaja, tipo_documento_venta_det, cobroNroPago, 
                                 cobroNroPago, cobroFecPago, cobroCodCliente, 1, 1, vencimiento_venta_det, montoCredito, montoRetenido, 1, 
                                 1, 0, estado, cobroCodCobrador, 0, obs_venta_det, cobroNroTurno, "N", 0, "0", 0, tipo_insert);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
            }else if(grabarMas == 1){
                montoCredito = 0;
                montoVuelto = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) * -1;
                montoDebito = 0;
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
            }else{
                resultPagoCliCab = true;
                resultPagoCliDet = true;
                resultMovTesoreria = true;
            }
        }else{ 
            
            int grabarMenos = JOptionPane.showConfirmDialog(this, "Si continua la operación generará un DEBITO por el faltante.", "Diferencia", 
                                                            JOptionPane.YES_NO_OPTION);
            if(grabarMenos == 0){
                montoDebito = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) * -1;
                montoVuelto = 0;
                montoCredito = 0;
                tipo_documento_venta_det = "DEB";
                tipo_insert = "FALTANTE - DEBITO";
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                insertDiferencia(cobroCodEmpresa, cobroCodLocal, cod_sector_venta_det, cobroCodCaja, tipo_documento_venta_det, cobroNroPago, 
                                 cobroNroPago, cobroFecPago, cobroCodCliente, 1, 1, vencimiento_venta_det, montoDebito, montoRetenido, 1,
                                 1, 0, estado, cobroCodCobrador, 0, obs_venta_det, cobroNroTurno, "N", 0, "0", 0,tipo_insert);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    resultUpdateVentaDetCuotas = true;
                }
            }else{
                resultPagoCliCab = true;
                resultPagoCliDet = true;
                resultMovTesoreria = true;
                resultUpdateVentaDetCuotas = true;
            }
        }
        
        // Graba forma pago tesoreria - mov cuenta 
        
        int secuencia_tesoreria;
        String observacion_tesoreria = jTFComentarios.getText().trim() + " " + jLNombreCliente.getText().trim();
        String es_ajuste_tesoreria = "N";
        String serie_pago_tesoreria = "A";
        int cod_proveedor_tesoreria = cobroCodCliente;
        String cod_operacion_tesoreria = "CRE"; // por ingreso en tesoreria
        String generado_por_tesoreria = "COCLIE";
        
        for(secuencia_tesoreria = 0; secuencia_tesoreria < jTFormaPago.getRowCount(); secuencia_tesoreria++){
            String tipo_comprobante = jTFormaPago.getValueAt(secuencia_tesoreria, 2).toString();
            String fecha_comprobante = jTFormaPago.getValueAt(secuencia_tesoreria, 6).toString();
            String fecha_vencimiento = jTFormaPago.getValueAt(secuencia_tesoreria, 7).toString();
            String numero_comprobante = jTFormaPago.getValueAt(secuencia_tesoreria, 3).toString();
            int cod_cuenta = Integer.parseInt(jTFormaPago.getValueAt(secuencia_tesoreria, 0).toString());
            int cod_pcuenta = 0;
            int codigo_moneda = getCodMoneda(cod_cuenta);
            double tipo_cambio = (Double)jTFormaPago.getValueAt(secuencia_tesoreria, 9);
            double monto_moneda = (Double)jTFormaPago.getValueAt(secuencia_tesoreria, 8);
            double monto_total = (Double)jTFormaPago.getValueAt(secuencia_tesoreria, 10);
            int nro_movimiento_tesoreria = getSecuenciaMovTesoreria();
            int cod_banco = Integer.parseInt(jTFormaPago.getValueAt(secuencia_tesoreria, 4).toString());
             
            
            resultOperacionGrabaCobro = grabarMovTesoreria(cobroCodEmpresa, cobroCodLocal, tipo_comprobante, numero_comprobante, fecha_comprobante, 
                                                           secuencia_tesoreria, cod_cuenta, cod_pcuenta, codigo_moneda, tipo_cambio, monto_moneda, 
                                                           monto_total, observacion_tesoreria, es_ajuste_tesoreria, fecha_vencimiento, serie_pago_tesoreria, 
                                                           cobroNroPago, cod_proveedor_tesoreria, cobroCodCobrador, cod_operacion_tesoreria, nro_movimiento_tesoreria, 
                                                           generado_por_tesoreria, cobroFechaTesoreria, 0, cobroCodCentroCosto, 0, 0, cobroFechaTesoreria);
            
            if(!resultOperacionGrabaCobro){
                resultPagoCliCab = true;
                resultPagoCliDet = true;
                resultMovTesoreria = true;
                resultUpdateVentaDetCuotas = true;
            }
            
            resultOperacionGrabaCobro = grabarMovCuenta(cobroCodEmpresa, cobroCodLocal, cod_cuenta, tipo_comprobante, numero_comprobante, fecha_comprobante, 
                                                        cod_banco, fecha_vencimiento, cod_operacion_tesoreria, codigo_moneda, tipo_cambio, monto_total, 
                                                        observacion_tesoreria, "-", "V", cobroCodCobrador, cod_proveedor_tesoreria, secuencia_tesoreria, 
                                                        serie_pago_tesoreria, cobroNroPago, nro_movimiento_tesoreria, generado_por_tesoreria, cobroFechaTesoreria, 
                                                        0, cobroCodCentroCosto, "-", fecha_vencimiento, "0", "0", 0, 0);
            
            
            if(!resultOperacionGrabaCobro){
                resultPagoCliCab = true;
                resultPagoCliDet = true;
                resultMovTesoreria = true;
                resultMovCuenta= true;
                resultUpdateVentaDetCuotas = true;
            }
            
        }
        
        
        // Cobro detalles 
        
        int secuencia;
        System.out.println("CANTIDAD DE FILAS DE LA TABLA DETALLES: " + cantFilasDetallesDocs);
        for(secuencia = 0; secuencia < cantFilasDetallesDocs; secuencia++){
            System.out.println("CUANTAS VECES ESTA ENTRANDO: " + secuencia);
            if(jTDetalleDocs.getValueAt(secuencia, 10).toString() == "true"){
                String tipo_comprob = jTDetalleDocs.getValueAt(secuencia, 1).toString();
                int nro_comprobante = Integer.parseInt(jTDetalleDocs.getValueAt(secuencia, 0).toString());
                int nro_cuota = Integer.parseInt(jTDetalleDocs.getValueAt(secuencia, 2).toString());
                String fec_comprob = jTDetalleDocs.getValueAt(secuencia, 4).toString();
                String fec_venc = jTDetalleDocs.getValueAt(secuencia, 5).toString();
                int cod_moneda = 1;
                int tipo_cambio = 1, monto_retenido = 0, afecta = 0, nroTimbrado = 0;
                String codPCuenta = "0";
                double monto_comprob = Double.parseDouble(jTDetalleDocs.getValueAt(secuencia, 7).toString());
                int interes = Integer.parseInt(jTFInteres.getText().trim());
                double valor_interes = Double.parseDouble(jTDetalleDocs.getValueAt(secuencia, 8).toString());
                String observacion = getObservacionDoc(nro_comprobante, cobroCodCliente, tipo_comprob);
                int can_cuota = Integer.parseInt(jTDetalleDocs.getValueAt(secuencia, 3).toString());
                int nro_caja_ticket = getNroCaja(nro_comprobante, cobroCodCliente, tipo_comprob);

                resultOperacionGrabaCobro = grabarPagoDet(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroPago, secuencia, tipo_comprob, nro_comprobante, 
                                                          nro_cuota, fec_comprob, fec_venc, cod_moneda, tipo_cambio, monto_comprob, montoRetenido, 
                                                          interes, valor_interes, afecta, codPCuenta, observacion, can_cuota, estado, cobroCodCliente, 
                                                          nroTimbrado);                                               
                
                if(!resultOperacionGrabaCobro){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar cobro detalle!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    break;
                }                               
                
                
                resultUpdateVentaDetCuotas = updateDocVentaDetCuotas(nro_comprobante, tipo_comprob, nro_caja_ticket, cobroCodCliente, nro_cuota, cobroNroRecibo, 
                                                                    cobroNroPago, cobroFecPago);
                
                if(!resultUpdateVentaDetCuotas){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error updating detalles de cuota!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    break;
                }                                
                
                System.out.println("RESULTPAGOCLICAB: " + resultPagoCliCab);
                System.out.println("RESULTPAGOCLIDET: " + resultPagoCliDet);
                System.out.println("RESULTMOVTESORERIA: " + resultMovTesoreria);                                                             
            }                        
        }    
        
        if(resultPagoCliCab || resultPagoCliDet || resultMovTesoreria || resultMovCuenta){
            System.out.println("ENTRA EN EL PROCESO DE GRABACION - ROLLBACK");
            problemFound = true;
            boolean rollback = rollBacktDatos();
            if(!rollback){
                System.out.println("RESULTADO DEL ROLLBACK: " + rollback);
                for(iterador = 0; iterador < 10; iterador++){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el RollBack!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
                problemFound = true;
            }
        }else{
            System.out.println("ENTRA EN EL PROCESO DE GRABACION - COMMIT");
            boolean commit = commitDatos();
            if(!commit){
                System.out.println("RESULTADO DEL COMMIT: " + commit);
                for(iterador = 0; iterador < 10; iterador++){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el Commit!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
                problemFound = true;
            }
        }
        return problemFound;
    }
    
    private String calculaVencimiento(String fecCobro, int plazo){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecActual = new Date();
        try{
            fecActual = sdf.parse(fecCobro);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        calendar.setTime(fecActual);
        int vence = plazo;
        calendar.add(Calendar.DATE, vence);
        result = sdf.format(calendar.getTime());
        return result;
    }
    
    private int getCondicionPlazoCreditoCliente(int codCliente){
        int result = 0;
        try{
            String sql = "SELECT cond_pago FROM cliente WHERE cod_cliente = " + codCliente;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cond_pago");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private boolean grabarMovCuenta(int codEmpresa, int codLocal, int codCuenta, String tipoComprob, String nroComprob, String fecComprob, int codBanco, 
                                    String fecVence, String codOperacion, int codMoneda, double tipoCambio, double montoTotal, String concepto, String ordenCheque, 
                                    String estado, int codUsuario, int codProveedor, int nroSecuencia, String seriePago, int nroPago, int nroMovimiento, 
                                    String generadoPor, String fecTesoreria, double pctIva, int codCentroCosto, String conciliadoPor, String fecVencimiento, 
                                    String nroDocDep, String codPCuenta, int nroAsiento, int nroTimbrado){
        String sql = "INSERT INTO movcuenta (cod_empresa, cod_local, cod_cuenta, tip_comprob, nro_comprob, fec_comprob, cod_banco, fec_vence, cod_operacion, "
                   + "cod_moneda, tip_cambio, mon_total, concepto, orden_cheque, estado, cod_usuario, fec_vigencia, cod_proveedor, nro_secuencia, serie_pago, "
                   + "nro_pago, nro_movimiento, generado_por, fec_tesoreria, pct_iva, cod_centrocosto, conciliado_por, fec_vencimiento, nro_documento_dep, "
                   + "cod_pcuenta, nro_asiento, nro_timbrado) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", " + codCuenta + ", '" + tipoComprob + "', '" + nroComprob + "', to_date('" + fecComprob + "', 'dd/MM/yyyy'), "
                   + codBanco + ", to_date('" + fecVence + "', 'dd/MM/yyyy'), '" + codOperacion + "', " + codMoneda + ", " + tipoCambio + ", " + montoTotal + ", '"
                   + concepto + "', '" + ordenCheque + "', '" + estado + "', " + codUsuario + ", current_timestamp, " + codProveedor + ", " + nroSecuencia + ", '"
                   + seriePago + "', " + nroPago + ", " + nroMovimiento + ", '" + generadoPor + "', to_date('" + fecTesoreria + "', 'dd/MM/yyyy'), " + pctIva + ", "
                   + codCentroCosto + ", '" + conciliadoPor + "', to_date('" + fecVencimiento + "', 'dd/MM/yyyy'), '" + nroDocDep + "', '" + codPCuenta + "', "
                   + nroAsiento + ", " + nroTimbrado + ")";
        System.out.println("GRABAR MOV - CUENTA: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarMovTesoreria(int codEmpresa, int codLocal, String tipoComprob, String nroComprob, String fecComprob, int nroSecuencia, int codCuenta, 
                                       int codPCuenta, int codMoneda, double tipCambio, double montoMoneda, double montoTotal, String obs, String esAjuste, 
                                       String fecVencimiento, String seriePago, int nroPago, int codProveedor, int codUsuario, String codOperacion, int nroMovimiento, 
                                       String generadoPor, String fecTesoreria, int pctIva, int codCentroCosto, int nroTimbrado, int nroMovReversion, String fecTesoreriaReversion){
        String sql = "INSERT INTO mov_tesoreria (cod_empresa, cod_local, tip_comprob, nro_comprob, fec_comprob, nro_secuencia, cod_cuenta, cod_pcuenta, "
                   + "cod_moneda, tip_cambio, monto_moneda, monto_total, observacion, es_ajuste, fec_vencimiento, serie_pago, nro_pago, cod_proveedor, "
                   + "estado, cod_usuario, fec_vigencia, cod_operacion, nro_movimiento, generado_por, fec_tesoreria, pct_iva, cod_centrocosto, nro_timbrado, "
                   + "nro_movimiento_reversion, fec_tesoreria_reversion) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", '" + tipoComprob + "', '" + nroComprob + "', to_date('" + fecComprob + "', 'dd/MM/yyyy'), "
                   + nroSecuencia + ", " + codCuenta + ", " + codPCuenta + ", " + codMoneda + ", " + tipCambio + ", " + montoMoneda + ", " + montoTotal + ", '"
                   + obs + "', '" + esAjuste + "', to_date('" + fecVencimiento + "', 'dd/MM/yyyy'), '" + seriePago + "', " + nroPago + ", " + codProveedor + ", '"
                   + "A', " + codUsuario + ", current_timestamp, '" + codOperacion + "', " + nroMovimiento + ", '" + generadoPor + "', to_date('" + fecTesoreria + "', 'dd/MM/yyyy'), "
                   + pctIva + ", " + codCentroCosto + ", " + nroTimbrado + ", " + nroMovReversion + ", to_date('" + fecTesoreriaReversion + "', 'dd/MM/yyyy'))";
        System.out.println("GRABAR MOV - TESORERIA: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean updateDocVentaDetCuotas(int nroComprob, String tipoComprob, int codCaja, int codCliente, int nroCuota, String nroRecibo, int nroPago, 
                                            String fecPago){
        String sql = "UPDATE venta_det_cuotas SET nro_recibo = " + nroRecibo + ", fec_recibo = '" + fecPago + "'::date, nro_pago = " + nroPago + " "
                   + "WHERE nro_comprob = " + nroComprob + " AND tip_comprob = '" + tipoComprob + "' AND cod_caja = " + codCaja + " AND cod_cliente = " + codCliente + " "
                   + "AND nro_cuota = " + nroCuota;
        System.out.println("UPDATE VENTA_DET_CUOTAS (Coming from cobrocli): " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarPagoDet(int codEmpresa, int codLocal, int codCaja, int nroPago, int nroSecuencia, String tipoComprob, int nroComprob, 
                                  int nroCuota, String fecComprob, String fecVencimiento, int codMoneda, int tipoCambio, double montoComprob, int montoRetenido, 
                                  int pctInteres, double vlrInteres, int afecta, String codPCuenta, String obs, int canCuota, String estado, int codCliente, 
                                  int nroTimbrado){
        String sql = "INSERT INTO pagocli_det (cod_empresa, cod_local, cod_caja, nro_pago, nro_secuencia, tip_comprob, nro_comprob, nro_cuota, "
                   + "fec_comprob, fec_vencimiento, cod_moneda, tip_cambio, monto_comprob, monto_retenido, pct_interes, vlr_interes, afecta, "
                   + "cod_pcuenta, observacion, fec_vigencia, can_cuota, estado, cod_cliente, nro_timbrado) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", " + codCaja + ", " + nroPago + ", " + nroSecuencia + ", '" + tipoComprob + "', "
                   + nroComprob + ", " + nroCuota + ", '" + fecComprob + "'::date, '" + fecVencimiento + "'::date, " + codMoneda + ", " + tipoCambio + ", "
                   + montoComprob + ", " + montoRetenido + ", " + pctInteres + ", " + vlrInteres + ", " + afecta + ", '" + codPCuenta + "', '"
                   + obs + "', current_timestamp, " + canCuota + ", '" + estado + "', " + codCliente + ", " + nroTimbrado + ")";
        System.out.println("COBRO-CLI DETALLE: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarPagoCab(int cod_empresa, int cod_local, int cod_caja, int nro_turno, int nro_pago, int cod_cliente, String fec_pago, 
                                  double monto_pago, int monto_retenido, String nro_recibo, int nro_asiento, String estado, int cod_cobrador, 
                                  String observacion, int cod_usuario, double monto_vuelto, double monto_credito, double monto_debito){
        String sql = "INSERT INTO pagocli_cab (cod_empresa, cod_local, cod_caja, nro_turno, nro_pago, cod_cliente, fec_pago, monto_pago, monto_retenido, "
                   + "nro_recibo, nro_asiento, estado, cod_cobrador, observacion, fec_vigencia, cod_usuario, monto_vuelto, monto_credito, monto_debito) "
                   + "VALUES(" + cod_empresa + ", " + cod_local + ", " + cod_caja + ", " + nro_turno + ", " + nro_pago + ", " + cod_cliente + ", '"
                   + fec_pago + "'::date, " + monto_pago + ", " + monto_retenido + ", '" + nro_recibo + "', " + nro_asiento + ", '" + estado + "', "
                   + cod_cobrador + ", '" + observacion + "', current_timestamp, " + cod_cobrador + ", " + monto_vuelto + ", " + monto_credito + ", "
                   + monto_debito + ")";
        System.out.println("COBRO-CLI CABECERA: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private void insertDiferencia(int codEmpresa, int codLocal, int codSector, int codCaja, String tipoComprob, int nroTicket, int nroComprob, String fecComprob, 
                              int codCliente, int nroCuota, int canCuota, String fecVencimiento, double montoCuota, int montoRetenido, int codMoneda, 
                              double tipoCambio, int nroRecibo, String estado, int codUsuario, int nroPago, String obs, int nroTurno, 
                              String esVenta, int nroTimbrado, String nroFactura, int nroTimbradoFactura, String tipoInsert){
        String sql = "INSERT INTO venta_det_cuotas (cod_empresa, cod_local, cod_sector, cod_caja, tip_comprob, nro_ticket, nro_comprob, fec_comprob, "
                   + "cod_cliente, nro_cuota, can_cuota, fec_vencimiento, monto_cuota, monto_retenido, cod_moneda, tip_cambio, nro_recibo, "
                   + "estado, cod_usuario, fec_vigencia, nro_pago, observacion, nro_turno, es_venta, nro_timbrado, ven_timbrado, nro_factura, nro_timbrado_factura) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", " + codSector + ", " + codCaja + ", '" + tipoComprob + "', " + nroTicket + ", "
                   + nroComprob + ", '" + fecComprob + "'::date, " + codCliente + ", " + nroCuota + ", " + canCuota + ", '" + fecVencimiento + "', "
                   + montoCuota + ", " + montoRetenido + ", " + codMoneda + ", " + tipoCambio + ", " + nroRecibo + ", '" + estado + "', " + codUsuario + ", "
                   + "current_timestamp, " + nroPago + ", '" + obs + "', " + nroTurno + ", '" + esVenta + "', " + nroTimbrado + ", current_timestamp, '"
                   + nroFactura + "', " + nroTimbradoFactura + ")";
        System.out.println("INSERT DEBITO - (" + tipoInsert + "): " + sql);
        grabarPrevioCommit(sql);
    }
    
    private boolean grabarPrevioCommit(String sql){
        boolean result;
        if(DBManager.ejecutarDML(sql) > 0){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
    
    private boolean commitDatos() {
        boolean result = true;
        try {
            DBManager.conn.commit();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private boolean rollBacktDatos() {
        boolean result = true;
        try {
            DBManager.conn.rollback();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private String getObservacionDoc(int nroComprob, int codCliente, String tipComprob){
        String obs = "";
        try{
            String sql = "SELECT observacion FROM venta_det_cuotas WHERE nro_comprob = " + nroComprob + " AND cod_cliente = " + codCliente + " "
                       + "AND tip_comprob = '" + tipComprob + "'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    obs = rs.getString("observacion");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return obs;
    }
    
    private int getNroCaja(int nroComprob, int codCliente, String tipComprob){
        int result = 0;
        try{
            String sql = "SELECT cod_caja FROM venta_det_cuotas WHERE nro_comprob = " + nroComprob + " AND cod_cliente = " + codCliente + " "
                       + "AND tip_comprob = '" + tipComprob + "'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_caja");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private void imprimirRecibo(){
        
        String razon_soc_empresa = jLNombreEmpresa.getText().trim();
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
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " " + FormMain.nombreUsuario);
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
    
    private int getCodMoneda(int codCuenta){
        int result = 0;
        try{
            String sql = "SELECT cod_moneda FROM cuenta WHERE cod_cuenta = " + codCuenta;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_moneda");
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
    
    private String getFechaTesoreria(){
        String result = "";
        try{
            String sql = "SELECT to_char(fec_tesoreria, 'dd/MM/yyyy') as fec_tesoreria FROM mae_tesoreria WHERE abierto = 'S' AND cerrado = 'N'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("fec_tesoreria");
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
    
    private void cargaDetallesCobro(String nroPago){
        StatementManager sm_cab = new StatementManager();
        StatementManager sm_det = new StatementManager();
        StatementManager sm_mov = new StatementManager();
        
        double det_total_docs_seleccionados = 0, det_total_interes = 0, det_docs_interes = 0, det_total_forma_pago = 0, det_diferencia = 0, 
               det_monto_vuelto = 0, det_monto_credito = 0, det_monto_debito = 0;
        
        try{
            String sql_cab = "SELECT cab.cod_empresa, cab.cod_local, cab.cod_cliente, cta.cod_cuenta, det.pct_interes, "
                           + "TO_CHAR(cab.fec_pago, 'dd/MM/yyyy') AS fec_pago, "
                           + "cab.cod_cobrador, cab.nro_pago, cab.nro_recibo, cab.observacion, cab.estado, cab.nro_turno, cab.cod_caja, "
                           + "cab.monto_vuelto, cab.monto_credito, cab.monto_debito " 
                           + "FROM pagocli_cab cab "
                           + "INNER JOIN cuenta cta "
                           + "ON cab.cod_cliente = cta.cod_cliente "
                           + "INNER JOIN pagocli_det det "
                           + "ON cab.nro_pago = det.nro_pago "
                           + "WHERE cab.nro_pago = " + nroPago + " "
                           + "GROUP BY det.pct_interes, cab.cod_empresa, cab.cod_local, cab.cod_cliente, cta.cod_cuenta, det.pct_interes, "
                           + "cab.fec_pago, cab.cod_cobrador, cab.nro_pago, cab.nro_recibo, cab.observacion, cab.estado, cab.nro_turno, cab.cod_caja";
            
            String sql_det = "SELECT det.nro_comprob, det.tip_comprob, det.nro_cuota, det.can_cuota, TO_CHAR(det.fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                           + "TO_CHAR(det.fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, "
                           + "CASE WHEN (cab.fec_pago::date - det.fec_vencimiento::date) < 0 THEN 0 "
                           + "ELSE (cab.fec_pago::date - det.fec_vencimiento::date) END AS dias_vencidos, "
                           + "det.monto_comprob, det.vlr_interes, (det.monto_comprob + det.vlr_interes) AS total "
                           + "FROM pagocli_det det "
                           + "INNER JOIN pagocli_cab cab "
                           + "ON det.nro_pago = cab.nro_pago "
                           + "WHERE det.nro_pago = " + nroPago;
            
            String sql_mov = "SELECT mov.cod_cuenta, cta.denominacion_cta, mov.tip_comprob, mov.nro_comprob, mov.cod_banco, bco.nombre, "
                           + "TO_CHAR(mov.fec_comprob, 'dd/MM/yyyy') AS fec_comprob, TO_CHAR(mov.fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, "
                           + "TO_CHAR((mov.mon_total / mov.tip_cambio), 'FM999999999.00')::NUMERIC AS valor, mov.tip_cambio, mov.mon_total "
                           + "FROM movcuenta mov "
                           + "INNER JOIN cuenta cta "
                           + "ON mov.cod_cuenta = cta.cod_cuenta "
                           + "INNER JOIN banco bco "
                           + "ON mov.cod_banco = bco.cod_banco "
                           + "WHERE mov.nro_pago = " + nroPago;
            
            // -- detalles mov cuenta - forma de pago 
            
            sm_mov.TheSql = sql_mov;
            System.out.println("SQL MOV - CUENTA: " + sql_mov);
            sm_mov.EjecutarSql();
            if(sm_mov.TheResultSet != null){
                while(sm_mov.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm_mov.TheResultSet.getString("cod_cuenta");
                    row[1] = sm_mov.TheResultSet.getString("denominacion_cta");
                    row[2] = sm_mov.TheResultSet.getString("tip_comprob");
                    row[3] = sm_mov.TheResultSet.getString("nro_comprob");
                    row[4] = sm_mov.TheResultSet.getString("cod_banco");
                    row[5] = sm_mov.TheResultSet.getString("nombre");
                    row[6] = sm_mov.TheResultSet.getString("fec_comprob");
                    row[7] = sm_mov.TheResultSet.getString("fec_vencimiento");
                    row[8] = sm_mov.TheResultSet.getDouble("valor");
                    row[9] = sm_mov.TheResultSet.getDouble("tip_cambio");
                    row[10] = sm_mov.TheResultSet.getDouble("mon_total");
                    det_total_forma_pago += sm_mov.TheResultSet.getDouble("mon_total");
                    dtmFormaPago.addRow(row);
                }
                jTFormaPago.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "No existen datos para cobro - movcuenta!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // -- detalles docs 
            
            sm_det.TheSql = sql_det;
            System.out.println("SQL DETALLES - DET: " + sql_det);
            sm_det.EjecutarSql();
            if(sm_det.TheResultSet != null){
                while(sm_det.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm_det.TheResultSet.getString("nro_comprob");
                    row[1] = sm_det.TheResultSet.getString("tip_comprob");
                    row[2] = sm_det.TheResultSet.getString("nro_cuota");
                    row[3] = sm_det.TheResultSet.getString("can_cuota");
                    row[4] = sm_det.TheResultSet.getString("fec_emision");
                    row[5] = sm_det.TheResultSet.getString("fec_vencimiento"); 
                    row[6] = sm_det.TheResultSet.getInt("dias_vencidos");
                    row[7] = sm_det.TheResultSet.getDouble("monto_comprob");
                    row[8] = sm_det.TheResultSet.getDouble("vlr_interes");
                    row[9] = sm_det.TheResultSet.getDouble("total");
                    row[10] = true;
                    det_total_docs_seleccionados += sm_det.TheResultSet.getDouble("monto_comprob");
                    det_total_interes += sm_det.TheResultSet.getDouble("vlr_interes");
                    det_docs_interes = det_total_docs_seleccionados + det_total_interes;
                    dtmDocPendientes.addRow(row);
                }
                /*if(jTDetalleDocs.getRowCount() > 0){
                    limpiarTablaDetallaDocs();
                }*/
                jTDetalleDocs.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "No existen datos para cobro - detalles!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // -- cabecera
            
            sm_cab.TheSql = sql_cab;
            System.out.println("SQL DETALLES - CAB: " + sql_cab);
            
            sm_cab.EjecutarSql();
            if(sm_cab.TheResultSet != null){
                if(sm_cab.TheResultSet.next()){
                    jCBCodEmpresa.setSelectedItem(sm_cab.TheResultSet.getInt("cod_empresa")); //
                    jCBCodLocal.setSelectedItem(sm_cab.TheResultSet.getInt("cod_local")); // 
                    jTFCodCliente.setText(sm_cab.TheResultSet.getString("cod_cliente")); //
                    jTFCodCuenta.setText(sm_cab.TheResultSet.getString("cod_cuenta")); 
                    jTFInteres.setText(String.valueOf(sm_cab.TheResultSet.getInt("pct_interes")));
                    jTFFecCobro.setText(sm_cab.TheResultSet.getString("fec_pago"));
                    jTFCodEmpleado.setText(sm_cab.TheResultSet.getString("cod_cobrador"));//
                    jTFNroPago.setText(sm_cab.TheResultSet.getString("nro_pago"));
                    jTFNroRecibo.setText(sm_cab.TheResultSet.getString("nro_recibo"));
                    jTFComentarios.setText(sm_cab.TheResultSet.getString("observacion"));
                    String estado = sm_cab.TheResultSet.getString("estado");
                    jTFEstado.setText(estado);
                    
                    if(estado.equals("V")){
                        jLEstado.setText("VIGENTE");
                        jTFEstado.setBackground(new Color(240,240,240));
                    }else{
                        jTFEstado.setBackground(new Color(255,153,153));
                        jLEstado.setText("ANULADO");
                    }
                    
                    jTFNroTurno.setText(sm_cab.TheResultSet.getString("nro_turno"));
                    jTFCodCaja.setText(sm_cab.TheResultSet.getString("cod_caja"));
                    
                    jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
                    jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
                    jLNombreCliente.setText(getNombreCliente(Integer.parseInt(jTFCodCliente.getText().trim())));
                    jLNombreEmpleado.setText(getNombreCobrador(Integer.parseInt(jTFCodEmpleado.getText().trim())));
                    
                    det_monto_credito = sm_cab.TheResultSet.getDouble("monto_credito");
                    det_monto_debito = sm_cab.TheResultSet.getDouble("monto_debito");
                    det_monto_vuelto = sm_cab.TheResultSet.getDouble("monto_vuelto");
                }
            }else{
                JOptionPane.showMessageDialog(this, "No existen datos para cobro - cabecera!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
            
            /*if(diferencia < 0){
            jLDiferencia.setText("FALTAN");
            jTFDiferencia.setBackground(new Color(255,153,153));
        }
        
        if(diferencia == 0){
            jLDiferencia.setText("SIN DIFERENCIAS");
            jTFDiferencia.setBackground(new Color(102,255,102));
        }
        
        if(diferencia > 0){
            jLDiferencia.setText("VUELTO");
            jTFDiferencia.setBackground(new Color(102,255,102));*/
            
            System.out.println("VUELTO: " + det_monto_vuelto + "\n"
                             + "CREDITO: " + det_monto_credito + "\n"
                             + "DEBITO: " + det_monto_debito);
            
            
            
            if(det_monto_vuelto == 0){
                jLDiferencia.setText("SIN DIFERENCIAS");
                jTFDiferencia.setBackground(new Color(102,255,102));
                jTFDiferencia.setText(decimalFormat.format(det_monto_vuelto));
             
                if(det_monto_credito == 0){
                    jLDiferencia.setText("SIN DIFERENCIAS");
                    jTFDiferencia.setBackground(new Color(102,255,102));
                    jTFDiferencia.setText(decimalFormat.format(det_monto_credito));
                    if(det_monto_debito == 0){
                        jLDiferencia.setText("SIN DIFERENCIAS");
                        jTFDiferencia.setBackground(new Color(102,255,102));
                        jTFDiferencia.setText(decimalFormat.format(det_monto_debito));
                    }else if(det_monto_debito != 0){
                        jLDiferencia.setText("DEBITO");
                        jTFDiferencia.setBackground(new Color(102,255,102));
                        jTFDiferencia.setText(decimalFormat.format(det_monto_debito));
                    }
                }else if(det_monto_credito != 0){
                    jLDiferencia.setText("CREDITO");
                    jTFDiferencia.setBackground(new Color(255,153,153));
                    jTFDiferencia.setText(decimalFormat.format(det_monto_credito));
                }
            }else if(det_monto_vuelto > 0){
                jLDiferencia.setText("VUELTO");
                jTFDiferencia.setBackground(new Color(102,255,102));
                jTFDiferencia.setText(decimalFormat.format(det_monto_vuelto));
            }
            
            jTFTotalDocsSeleccionados.setText(decimalFormat.format(det_total_docs_seleccionados));
            jTFTotalInteres.setText(decimalFormat.format(det_total_interes));
            jTFTotalCobro.setText(decimalFormat.format(det_docs_interes));
            jTFTotalFormaPago.setText(decimalFormat.format(det_total_forma_pago));
            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private String getNombreCliente(int codCliente){
        String result = "";
        ClienteCtrl clieCtrl = new ClienteCtrl();
        return result = clieCtrl.getNombreCliente(codCliente);
    }
    
    private String getNombreCobrador(int codCobrador){
        String result = "";
        EmpleadoCtrl empleadoCtrl = new EmpleadoCtrl();
        return result = empleadoCtrl.getNombreEmpleado(codCobrador);
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
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreLocal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTFInteres = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTFFecCobro = new javax.swing.JTextField();
        jBBuscarDocs = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jTFCodEmpleado = new javax.swing.JTextField();
        jLNombreEmpleado = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
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
        jLabel14 = new javax.swing.JLabel();
        jTFNroTurno = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFCodCaja = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalleDocs = new javax.swing.JTable();
        jBVerDetalle = new javax.swing.JButton();
        jBMarcarTodos = new javax.swing.JButton();
        jBDesmarcarTodos = new javax.swing.JButton();
        jBConfirmarDocs = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTFormaPago = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        jTFCodCta = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFDenominacionCta = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFTipoCuenta = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTFNroDoc = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTFCodBco = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTFNombreBco = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTFFecEmision = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTFFecVenc = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTFValor = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTFCotizacion = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTFValorFinal = new javax.swing.JTextField();
        jBRefresh = new javax.swing.JButton();
        jBConfirmarFormaPago = new javax.swing.JButton();
        jBNuevo = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jTFTotalDocsSeleccionados = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jTFTotalInteres = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jTFTotalFormaPago = new javax.swing.JTextField();
        jLDiferencia = new javax.swing.JLabel();
        jTFDiferencia = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTFTotalCobro = new javax.swing.JTextField();
        jTFCodCuenta = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Cobro de Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("REGISTRO DE COBRO DE CUENTAS DE CLIENTES");

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

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Cliente:");

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

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Interés:");

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
        jLabel7.setText("Fecha de cobro:");

        jTFFecCobro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecCobro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Cobrador:");

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

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Nro. Pago:");

        jTFNroPago.setEditable(false);
        jTFNroPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Nro. Recibo:");

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
        jLabel11.setText("Total Cuenta:");

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

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Nro. Turno:");

        jTFNroTurno.setEditable(false);
        jTFNroTurno.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroTurno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Cod. Caja:");

        jTFCodCaja.setEditable(false);
        jTFCodCaja.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Documentos Pendientes de Cobro"));

        jTDetalleDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro Comprob", "Tipo Doc", "Cuota", "Cant Cuota", "Fec Emisión", "Fec Venc", "Días Venc", "Vlr Cuota", "Vlr Interés", "Cuota + Interés", "Seleccionar", "Cod. Caja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
            jTDetalleDocs.getColumnModel().getColumn(11).setResizable(false);
        }

        jBVerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/detalles24.png"))); // NOI18N
        jBVerDetalle.setText("Ver Detalle");
        jBVerDetalle.setEnabled(false);
        jBVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBVerDetalleActionPerformed(evt);
            }
        });

        jBMarcarTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/marcar_todos32.png"))); // NOI18N
        jBMarcarTodos.setEnabled(false);
        jBMarcarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMarcarTodosActionPerformed(evt);
            }
        });

        jBDesmarcarTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/desmarcar_todos32.png"))); // NOI18N
        jBDesmarcarTodos.setEnabled(false);
        jBDesmarcarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDesmarcarTodosActionPerformed(evt);
            }
        });

        jBConfirmarDocs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarDocs.setText("Confirmar");
        jBConfirmarDocs.setEnabled(false);
        jBConfirmarDocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarDocsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBVerDetalle)
                    .addComponent(jBMarcarTodos)
                    .addComponent(jBDesmarcarTodos)
                    .addComponent(jBConfirmarDocs))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConfirmarDocs, jBDesmarcarTodos, jBMarcarTodos, jBVerDetalle});

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Formas de pago"));

        jTFormaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cta", "Descripción", "Tipo", "Nro. Doc", "Bco.", "Nom. Bco.", "Fec. Emisión ", "Fec. Venc. ", "Valor", "Cotización ", "Valor Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTFormaPago);
        if (jTFormaPago.getColumnModel().getColumnCount() > 0) {
            jTFormaPago.getColumnModel().getColumn(0).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(1).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(2).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(3).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(4).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(5).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(6).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(7).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(8).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(9).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(10).setResizable(false);
        }

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel16.setText("CUENTA");

        jTFCodCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodCta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCtaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodCtaFocusLost(evt);
            }
        });
        jTFCodCta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCtaKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel17.setText("DESCRIPCIÓN");

        jTFDenominacionCta.setEditable(false);
        jTFDenominacionCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel18.setText("TIPO");

        jTFTipoCuenta.setEditable(false);
        jTFTipoCuenta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoCuenta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel19.setText("NRO. DOC");

        jTFNroDoc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroDocFocusGained(evt);
            }
        });
        jTFNroDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroDocKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel20.setText("BCO");

        jTFCodBco.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodBco.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodBco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodBcoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodBcoFocusLost(evt);
            }
        });
        jTFCodBco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodBcoKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel21.setText("NOMBRE BANCO");

        jTFNombreBco.setEditable(false);
        jTFNombreBco.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel22.setText("FEC. EMISIÓN");

        jTFFecEmision.setEditable(false);
        jTFFecEmision.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecEmision.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel23.setText("FEC. VENC. ");

        jTFFecVenc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecVenc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecVenc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecVencFocusGained(evt);
            }
        });
        jTFFecVenc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecVencKeyPressed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel24.setText("VALOR");

        jTFValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFValorFocusGained(evt);
            }
        });
        jTFValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFValorKeyPressed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel25.setText("COTIZACION");

        jTFCotizacion.setEditable(false);
        jTFCotizacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCotizacion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel26.setText("VALOR FINAL");

        jTFValorFinal.setEditable(false);
        jTFValorFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFValorFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N
        jBRefresh.setEnabled(false);
        jBRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshActionPerformed(evt);
            }
        });

        jBConfirmarFormaPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarFormaPago.setEnabled(false);
        jBConfirmarFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarFormaPagoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBRefresh)
                            .addComponent(jBConfirmarFormaPago)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTFCodCta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jTFDenominacionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFCodBco, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jTFNombreBco, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFFecEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFFecVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jTFValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDenominacionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCodBco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreBco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jBRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBConfirmarFormaPago))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConfirmarFormaPago, jBRefresh});

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
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

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir Recibo");
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel27.setText("TOTAL DOCS SELECCIONADOS");

        jTFTotalDocsSeleccionados.setEditable(false);
        jTFTotalDocsSeleccionados.setBackground(new java.awt.Color(255, 255, 204));
        jTFTotalDocsSeleccionados.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalDocsSeleccionados.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalDocsSeleccionados.setText("0");

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel28.setText("INTERÉS");

        jTFTotalInteres.setEditable(false);
        jTFTotalInteres.setBackground(new java.awt.Color(255, 255, 204));
        jTFTotalInteres.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalInteres.setText("0");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel29.setText("TOTAL FORMA PAGO");

        jTFTotalFormaPago.setEditable(false);
        jTFTotalFormaPago.setBackground(new java.awt.Color(102, 255, 102));
        jTFTotalFormaPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalFormaPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalFormaPago.setText("0");

        jLDiferencia.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLDiferencia.setText("DIFERENCIA");

        jTFDiferencia.setEditable(false);
        jTFDiferencia.setBackground(new java.awt.Color(102, 255, 102));
        jTFDiferencia.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDiferencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDiferencia.setText("0");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel31.setText("DOCS + INTERES");

        jTFTotalCobro.setEditable(false);
        jTFTotalCobro.setBackground(new java.awt.Color(255, 255, 204));
        jTFTotalCobro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotalCobro.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCobro.setText("0");

        jTFCodCuenta.setEditable(false);
        jTFCodCuenta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCuenta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFCodCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBBuscarDocs, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTFComentarios))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFTotalCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jBNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFTotalDocsSeleccionados, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFTotalInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31)
                            .addComponent(jTFTotalCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFTotalFormaPago)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel29)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLDiferencia)
                            .addComponent(jTFDiferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente)
                    .addComponent(jLabel5)
                    .addComponent(jTFInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jTFFecCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscarDocs)
                    .addComponent(jTFCodCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpleado)
                    .addComponent(jLabel8)
                    .addComponent(jTFNroPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTFTotalCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTFComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLEstado)
                    .addComponent(jLabel14)
                    .addComponent(jTFNroTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jTFCodCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFTotalDocsSeleccionados, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFTotalCobro)
                            .addComponent(jTFTotalInteres)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(jLDiferencia)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFTotalFormaPago)
                            .addComponent(jTFDiferencia)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBNuevo)
                        .addComponent(jBBuscar)
                        .addComponent(jBCancelar)
                        .addComponent(jBImprimir)
                        .addComponent(jBSalir)))
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        if(jBNuevo.getText().trim().equals("Nuevo")){
            setEstadoCampos(true);
            setEstadoBotonesNuevo();        
            llenarCampos();          
            jTFCodCliente.addFocusListener(new Focus());
            jTFCodCliente.requestFocus();
            
            jTDetalleDocs.setEnabled(true);
            jTFormaPago.setEnabled(true);
            
            if(jTFormaPago.getRowCount() > 0){
                limpiarTablaFormaPago();
            }
            
            jTFTotalCuenta.setText("0");
            jTFTotalDocsSeleccionados.setText("0");
            jTFTotalInteres.setText("0");
            jTFTotalCobro.setText("0");
            jTFTotalFormaPago.setText("0");
            jTFDiferencia.setText("0");
            jLDiferencia.setText("SIN DIFERENCIAS");
            jTFDiferencia.setBackground(new Color(102,255,102));
        }
        
        if(jBNuevo.getText().trim().equals("Guardar")){
            int grabar = JOptionPane.showConfirmDialog(this, "Seguro de grabar cobro?", "Confirmar registro", JOptionPane.YES_NO_OPTION);
            if(grabar == 0){
                if(grabarDatos()){
                    JOptionPane.showMessageDialog(this, "Cobro registrado con suceso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoCampos(false);
                    setEstadoBotonesGrabado();
                    jBNuevo.setText("Nuevo");
                    jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png")));
                    jBNuevo.grabFocus();
                    imprimirRecibo();
                    
                    jTDetalleDocs.setEnabled(true);
                    jTFormaPago.setEnabled(true);
                   
                }
            }
        }
        
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBBuscarDocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarDocsActionPerformed
        limpiarTablaDetalleDocs();
        llenarTabla();
        jTFTotalDocsSeleccionados.setText("0");
        jTFTotalInteres.setText("0");
        jTFTotalCobro.setText("0");
    }//GEN-LAST:event_jBBuscarDocsActionPerformed

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

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
                limpiarTablaDetalleDocs();
            }
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFInteresFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFInteresFocusGained
        jTFInteres.selectAll();
    }//GEN-LAST:event_jTFInteresFocusGained

    private void jTFInteresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFInteresKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecCobro.requestFocus();
        }
    }//GEN-LAST:event_jTFInteresKeyPressed

    private void jTFFecCobroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecCobroFocusGained
        jTFFecCobro.selectAll();
    }//GEN-LAST:event_jTFFecCobroFocusGained

    private void jTFFecCobroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecCobroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscarDocs.requestFocus();
        }
    }//GEN-LAST:event_jTFFecCobroKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoBotonesCancelar();
        setEstadoCampos(false);
        limpiarTablaDetalleDocs();
        jBNuevo.setText("Nuevo");
        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png")));
        jBNuevo.grabFocus();
        jTFTotalCuenta.setText("0");
        jTFTotalDocsSeleccionados.setText("0");
        jTFTotalInteres.setText("0");
        jTFTotalCobro.setText("0");
        jTFTotalFormaPago.setText("0");
        jTFDiferencia.setText("0");
        jLDiferencia.setText("SIN DIFERENCIAS");
        jTFDiferencia.setBackground(new Color(102,255,102));
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBMarcarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMarcarTodosActionPerformed
        marcarTodos();
    }//GEN-LAST:event_jBMarcarTodosActionPerformed

    private void jBDesmarcarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDesmarcarTodosActionPerformed
        desMarcarTodos();
    }//GEN-LAST:event_jBDesmarcarTodosActionPerformed

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
            jTFCodCta.grabFocus();
            jTFCodCta.setText("1");
            jTFFecVenc.setText(fecVigencia);
            jTFFecEmision.setText(fecVigencia);
        }
    }//GEN-LAST:event_jBConfirmarDocsActionPerformed

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

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

    private void jTFNroReciboFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroReciboFocusGained
        jTFNroRecibo.selectAll();
    }//GEN-LAST:event_jTFNroReciboFocusGained

    private void jTFNroReciboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroReciboKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFComentarios.requestFocus();
        }
    }//GEN-LAST:event_jTFNroReciboKeyPressed

    private void jTFComentariosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFComentariosFocusGained
        jTFComentarios.selectAll();
    }//GEN-LAST:event_jTFComentariosFocusGained

    private void jTFCodCtaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCtaFocusGained
        jTFCodCta.selectAll();
        jTFValor.setText("0");
    }//GEN-LAST:event_jTFCodCtaFocusGained

    private void jTFCodCtaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCtaFocusLost
        if(!jTFCodCta.getText().trim().equals("")){
            getCtaDenCotiz(jTFCodCta.getText().trim());
            controlFoco();
        }
    }//GEN-LAST:event_jTFCodCtaFocusLost

    private void jTFCodCtaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCtaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroDoc.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            DlgConsultasCuentasMonedas cuentas = new DlgConsultasCuentasMonedas(new JFrame(), true);
            cuentas.pack();
            cuentas.setText(jTFCodCuenta);
            cuentas.setVisible(true);
            cuentas.jTFDescripcion.setText("%");
            cuentas.jTFDescripcion.selectAll();
        }
    }//GEN-LAST:event_jTFCodCtaKeyPressed

    private void jTFNroDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocFocusGained
        jTFNroDoc.selectAll();
    }//GEN-LAST:event_jTFNroDocFocusGained

    private void jTFNroDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodBco.grabFocus();
        }
    }//GEN-LAST:event_jTFNroDocKeyPressed

    private void jTFCodBcoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBcoFocusGained
        jTFCodBco.selectAll();
    }//GEN-LAST:event_jTFCodBcoFocusGained

    private void jTFCodBcoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBcoFocusLost
        jTFNombreBco.setText(getNombreBanco(jTFCodBco.getText().trim()));
    }//GEN-LAST:event_jTFCodBcoFocusLost

    private void jTFCodBcoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodBcoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecVenc.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("ATOMSystems|Main - Consulta de Bancos");
                grupo.dConsultas("banco", "nombre", "cod_banco", "nombre", "activo", null, "Codigo", "Nombre", "Activo", null);
                grupo.setText(jTFCodBco);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodBcoKeyPressed

    private void jTFFecVencFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencFocusGained
        jTFFecVenc.selectAll();
    }//GEN-LAST:event_jTFFecVencFocusGained

    private void jTFFecVencKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecVencKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFValor.grabFocus();
        }
    }//GEN-LAST:event_jTFFecVencKeyPressed

    private void jTFValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFValorFocusGained
        jTFValor.selectAll();
    }//GEN-LAST:event_jTFValorFocusGained

    private void jTFValorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFValorKeyPressed
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",",""));
        double valor_ingresado = Double.parseDouble(jTFValor.getText().trim().replace(",", ""));
        double valor_final = cotizacion * valor_ingresado;
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(valor_ingresado < 0 || valor_ingresado == 0 || jTFValor.getText().trim().equals("")){
                jTFCodCta.requestFocus();
            }
            else{
                jTFValorFinal.setText(decimalFormat.format(valor_final));
                addFormaPagoDetalle();
                totalizar();
                jTFValor.setText("0");
                jTFCodCta.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFValorKeyPressed

    private void jBRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshActionPerformed
        limpiarTablaFormaPago();
        totalizar();
        estadoBotonNuevo();
    }//GEN-LAST:event_jBRefreshActionPerformed

    private void jBBuscarDocsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBBuscarDocsKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodEmpleado.grabFocus();
        }
    }//GEN-LAST:event_jBBuscarDocsKeyPressed

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroRecibo.requestFocus();
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

    private void jBConfirmarFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarFormaPagoActionPerformed
        estadoBotonNuevo();
    }//GEN-LAST:event_jBConfirmarFormaPagoActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(jTDetalleDocs.getRowCount() > 0){
            imprimirRecibo();
        }
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        BuscaCobroCliente busca_cobro = new BuscaCobroCliente(new JFrame(), true);
        busca_cobro.setText(jTFNroPago);
        busca_cobro.pack();
        busca_cobro.setVisible(true);
        if(!jTFNroPago.getText().trim().equals("")){
            limpiarTablaDetalleDocs();
            limpiarTablaFormaPago();
            cargaDetallesCobro(jTFNroPago.getText().trim());
        }
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBVerDetalleActionPerformed
        
        //for(int i =0; i < jTDetalleDocs.getRowCount(); i++){
            if(jTDetalleDocs.getValueAt(jTDetalleDocs.getSelectedRow(), 10).toString() == "true"){
                String nroDocumento = jTDetalleDocs.getValueAt(jTDetalleDocs.getSelectedRow(), 0).toString().trim();
                String codCaja = jTDetalleDocs.getValueAt(jTDetalleDocs.getSelectedRow(), 11).toString().trim();
                String fecDocumento = jTDetalleDocs.getValueAt(jTDetalleDocs.getSelectedRow(), 4).toString().trim();

                DetalleDocVenta detalle_venta = new DetalleDocVenta(new JFrame(), true, nroDocumento, codCaja, fecDocumento, "COBRANZA");
                detalle_venta.pack();
                detalle_venta.setVisible(true);
            }
        //}
        
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
            java.util.logging.Logger.getLogger(RegistroCobroClientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroCobroClientes1 dialog = new RegistroCobroClientes1(new javax.swing.JFrame(), true, "", "");
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
    private javax.swing.JButton jBConfirmarDocs;
    private javax.swing.JButton jBConfirmarFormaPago;
    private javax.swing.JButton jBDesmarcarTodos;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBMarcarTodos;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JButton jBSalir;
    private javax.swing.JButton jBVerDetalle;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLDiferencia;
    private javax.swing.JLabel jLEstado;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpleado;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTDetalleDocs;
    private javax.swing.JTextField jTFCodBco;
    private javax.swing.JTextField jTFCodCaja;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCta;
    private javax.swing.JTextField jTFCodCuenta;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFComentarios;
    private javax.swing.JTextField jTFCotizacion;
    private javax.swing.JTextField jTFDenominacionCta;
    private javax.swing.JTextField jTFDiferencia;
    private javax.swing.JTextField jTFEstado;
    private javax.swing.JTextField jTFFecCobro;
    private javax.swing.JTextField jTFFecEmision;
    private javax.swing.JTextField jTFFecVenc;
    private javax.swing.JTextField jTFInteres;
    private javax.swing.JTextField jTFNombreBco;
    private javax.swing.JTextField jTFNroDoc;
    private javax.swing.JTextField jTFNroPago;
    private javax.swing.JTextField jTFNroRecibo;
    private javax.swing.JTextField jTFNroTurno;
    private javax.swing.JTextField jTFTipoCuenta;
    private javax.swing.JTextField jTFTotalCobro;
    private javax.swing.JTextField jTFTotalCuenta;
    private javax.swing.JTextField jTFTotalDocsSeleccionados;
    private javax.swing.JTextField jTFTotalFormaPago;
    private javax.swing.JTextField jTFTotalInteres;
    private javax.swing.JTextField jTFValor;
    private javax.swing.JTextField jTFValorFinal;
    private javax.swing.JTable jTFormaPago;
    // End of variables declaration//GEN-END:variables
}
