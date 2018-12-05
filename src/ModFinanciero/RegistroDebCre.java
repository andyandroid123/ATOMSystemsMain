/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero;

import static ModCaja.RegistroCobroClientes1.dtmFormaPago;
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
import utiles.Utiles;
import views.busca.BuscaCliente;
import views.busca.DlgConsultas;
import views.busca.DlgConsultasCuentasMonedas;

/**
 *
 * @author ANDRES
 */
public class RegistroDebCre extends javax.swing.JDialog {

    String fecVigencia = "";
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    private static DefaultTableModel dtmFormaPago; 
    private static DefaultTableModel dtmCuotas; 
    
    public RegistroDebCre(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        llenarCombos();
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jTFFecTesoreria.setForeground(new Color(0,0,0));
        getFechaTesoreriaHabilitada();
        getFecVigencia();
        configCampos();
        configTablaFormaPago();
        configTablaCuotas();
        setEstadoComponentes(false);
    }

    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
    }

    private void getFechaTesoreriaHabilitada(){
        String fecha = "";
        try{
            String sql = "SELECT to_char(fec_tesoreria, 'dd/MM/yyyy') AS fec_tesoreria FROM mae_tesoreria WHERE abierto = 'S' AND cerrado = 'N'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    fecha = rs.getString("fec_tesoreria");
                }else{
                    fecha = "INEXISTENTE";
                }
                
                if(fecha.equals("INEXISTENTE")){
                    jTFFecTesoreria.setForeground(new Color(255,0,0));
                }else{
                    jTFFecTesoreria.setForeground(new Color(0,0,0));
                }
                jTFFecTesoreria.setText(fecha);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
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
    
    private void configCampos(){
        jTFNroDoc.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodVendedor.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodCta.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFNroComprob.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFConcepto.setDocument(new MaxLength(50, "UPPER", "ALFA"));
        jTFCantCuotas.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFPrimerVencimiento.setInputVerifier(new FechaInputVerifier(jTFPrimerVencimiento));
        jTFFecComprob.setInputVerifier(new FechaInputVerifier(jTFFecComprob));
        jTFFecDoc.setInputVerifier(new FechaInputVerifier(jTFFecDoc));
        jTFPrimerVencimiento.setInputVerifier(new FechaInputVerifier(jTFPrimerVencimiento));
        
        jTFNroDoc.addFocusListener(new Focus());
        jTFFecDoc.addFocusListener(new Focus());
        jTFCodCliente.addFocusListener(new Focus());
        jTFCodVendedor.addFocusListener(new Focus());
        jTFCodCta.addFocusListener(new Focus());
        jTFNroComprob.addFocusListener(new Focus());
        jTFFecComprob.addFocusListener(new Focus());
        jTFImporteComprob.addFocusListener(new Focus());
        jTFConcepto.addFocusListener(new Focus()); 
        jTFCantCuotas.addFocusListener(new Focus()); 
        jTFPrimerVencimiento.addFocusListener(new Focus()); 
    }
    
    private void tipoDeOperacion(){
        String tipo_doc = jCBTipoOperacion.getSelectedItem().toString().trim();
        if(tipo_doc.equals("DEB")){
            jLTipoOperacion.setText("DEBITO");
        }else{
            jLTipoOperacion.setText("CREDITO");
        }
    }
    
    private void llenarCampos(){
        jTFFecDoc.setText(fecVigencia);
        jTFPrimerVencimiento.setText(fecVigencia);
        jTFNroDoc.setText(getNroComprob());
        jTFCodVendedor.setText(String.valueOf(FormMain.codUsuario));
        jTFCodCta.setText("1");
        jTFCantCuotas.setText("1");
        jTFCodCliente.setText("");
        JTFRucCliente.setText("");
        jLNombreCliente.setText("");
        jLNombreVendedor.setText("");
        jTFDescCta.setText("");
        jTFTipoCta.setText("");
        jTFNroComprob.setText("");
        jTFFecComprob.setText("");
        jTFImporteComprob.setText("");
        jTFCotizacion.setText("");
        jTFImporteFinal.setText("");
        jTFConcepto.setText("");
        jTFTotalDoc.setText("");
        jTFNroDoc.requestFocus();
        limpiarTablaCuotas();
        limpiarTablaFormaPago();
    }
    
    
    private String getNroComprob(){
        String result = "0";
        try
        {
            ResultSet rs = DBManager.ejecutarDSL("SELECT MAX(nro_comprob) AS codigo FROM venta_det_cuotas WHERE tip_comprob IN ('DEB', 'CRE')");            
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
    
    private void addFormaPago(){
        /* COLUMNS
         * cta (int)
         * descripcion (String)
         * tipo (string)
         * nro comprob (int)
         * fecha (String)
         * importe (double)
         * cotizacion (double)
         * importe final (double)
         */
        
        int cod_cuenta = Integer.parseInt(jTFCodCta.getText().trim());
        String descripcion = jTFDescCta.getText().trim();
        String tipo = jTFTipoCta.getText().trim();
        int nro_comprob = Integer.parseInt(jTFNroComprob.getText().trim());
        String fecha = jTFFecComprob.getText().trim();
        double importe = Double.parseDouble(jTFImporteComprob.getText().trim().replace(",", ""));
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",", ""));
        double importe_final = Double.parseDouble(jTFImporteFinal.getText().trim().replace(",", ""));
        
        dtmFormaPago.addRow(new Object[]{cod_cuenta, descripcion, tipo, nro_comprob, fecha, importe, cotizacion, importe_final});
    }
    
    private void configTablaFormaPago(){
        dtmFormaPago = (DefaultTableModel)jTFormaPago.getModel();
        
        jTFormaPago.getColumnModel().getColumn(0).setPreferredWidth(20); // cta
        jTFormaPago.getColumnModel().getColumn(1).setPreferredWidth(60); // descripcion 
        jTFormaPago.getColumnModel().getColumn(2).setPreferredWidth(20); // tipo
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(40); // nro comprob
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(40); // fecha
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(40); // importe
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(40); // cotizacion
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(40); // importe final
        
        utiles.Utiles.punteroTablaF(jTFormaPago, this);        
        jTFormaPago.setFont(new Font("Tahoma", 1, 10) );
        jTFormaPago.setRowHeight(18);
    }
    
    private void configTablaCuotas(){
        dtmCuotas = (DefaultTableModel)jTCuotas.getModel();
        
        jTCuotas.getColumnModel().getColumn(0).setPreferredWidth(20); // cuota
        jTCuotas.getColumnModel().getColumn(1).setPreferredWidth(60); // monto cuota 
        jTCuotas.getColumnModel().getColumn(2).setPreferredWidth(40); // fec vencimiento
        
        utiles.Utiles.punteroTablaF(jTCuotas, this);        
        jTCuotas.setFont(new Font("Tahoma", 1, 10) );
        jTCuotas.setRowHeight(18);
    }
    
    private void getDatosCliente(String codigo){
        try{
            String sql = "SELECT razon_soc, ruc_cliente "
                       + "FROM cliente "
                       + "WHERE cod_cliente = " + codigo; 
            System.out.println("SQL DATOS CLIENTE: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreCliente.setText(rs.getString("razon_soc"));
                    JTFRucCliente.setText(rs.getString("ruc_cliente"));
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
    
    private void getCtaDenCotiz(String codigo){     
        String tipo_cuenta = "";
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
                        jTFDescCta.setForeground(Color.black);
                        jTFCotizacion.setText(decimalFormat.format(rs.getInt("cotiz_compra")));
                        jTFDescCta.setText(rs.getString("denominacion_cta"));
                        jTFTipoCta.setText(rs.getString("tipo_cuenta"));
                    }
                }else{
                    jTFDescCta.setForeground(Color.red);
                    jTFCotizacion.setText("1");
                    jTFTipoCta.setText("");
                    jTFDescCta.setText("INEXISTENTE");
                    jTFCodCta.requestFocus();
                    jTFCodCta.setText("1");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void setBotonesNuevo(){
        jBRefresh.setEnabled(true);
        jBConfirmarFormaPago.setEnabled(true);
        jBConfirmarCuota.setEnabled(true);
        jBNuevo.setEnabled(false);
        jBGuardar.setEnabled(false);
        jBSalir.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBImpPagare.setEnabled(false);
        jBImpVale.setEnabled(false);
        jBImpRecibo.setEnabled(false);
        
    }
    
    private void setBotonesGrabado(){
        jBRefresh.setEnabled(false);
        jBConfirmarFormaPago.setEnabled(false);
        jBConfirmarCuota.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBGuardar.setEnabled(false);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
        
        if(jCBTipoOperacion.getSelectedItem().toString().equals("DEB")){
            //jBImpPagare.setEnabled(true);
            jBImpVale.setEnabled(true);
            //jBImpRecibo.setEnabled(true);
        }else{
            //jBImpPagare.setEnabled(false);
            jBImpVale.setEnabled(false);
            //jBImpRecibo.setEnabled(true);
        }
        
        
    }
    
    private void setBotonesCancelar(){
        jBRefresh.setEnabled(false);
        jBConfirmarFormaPago.setEnabled(false);
        jBConfirmarCuota.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBGuardar.setEnabled(false);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
    }
    
    private void setEstadoComponentes(boolean estado){
        jCBCodEmpresa.setEnabled(estado);
        jCBCodLocal.setEnabled(estado);
        jTFNroDoc.setEnabled(estado);
        jTFFecDoc.setEnabled(estado);
        jCBTipoOperacion.setEnabled(estado);
        jTFCodCliente.setEnabled(estado);
        jTFCodVendedor.setEnabled(estado);
        jTFCodCta.setEnabled(estado);
        jTFNroComprob.setEnabled(estado);
        jTFFecComprob.setEnabled(estado);
        jTFImporteComprob.setEnabled(estado);
        jTFConcepto.setEnabled(estado);
        jTFCantCuotas.setEnabled(estado);
        jTFPrimerVencimiento.setEnabled(estado);
        jTFormaPago.setEnabled(estado);
        jTCuotas.setEnabled(estado);
    }
    
    private void limpiarTablaFormaPago(){
        for(int i = 0; i < dtmFormaPago.getRowCount(); i++){
            dtmFormaPago.removeRow(i);
            i--;
        }
    }
    
    private void limpiarTablaCuotas(){
        for(int i = 0; i < dtmCuotas.getRowCount(); i++){
            dtmCuotas.removeRow(i);
            i--;
        }
    }
    
    private void totalizar(){
        double totalGrilla = 0;
        for(int i = 0; i < dtmFormaPago.getRowCount(); i++){
            totalGrilla += Double.parseDouble(jTFormaPago.getValueAt(i, 7).toString());
        }
        jTFTotalDoc.setText(decimalFormat.format(totalGrilla));
    }
    
    private void addCuotas(){
        /* COLUMNS
         * cuota (int)
         * monto cuota (double)
         * fec vencimiento (string)
         */
        
        int dias_vencimiento = getDiasVencimientoCliente(jTFCodCliente.getText().trim());
        int cantidad_cuota = Integer.parseInt(jTFCantCuotas.getText().trim());
        double monto = Double.parseDouble(jTFTotalDoc.getText().trim().replace(",", ""));
        double monto_cuotas = Math.round(monto / cantidad_cuota);
        String fechaVencimiento = jTFPrimerVencimiento.getText().trim();        
        String fecha_vencimiento2 = "";
        
        String fecha = "01/01/2000";
        for(int i = 0; i < cantidad_cuota; i++){
            int nro_cuota = i+ 1;                         
            
            fecha_vencimiento2 = calculaVencimiento(dias_vencimiento, fecha); 

            if(nro_cuota == 1){
                fecha = fechaVencimiento;
                dtmCuotas.addRow(new Object[]{nro_cuota, monto_cuotas, fecha});
            }else{
                dtmCuotas.addRow(new Object[]{nro_cuota, monto_cuotas, fecha_vencimiento2});
                fecha = fecha_vencimiento2;
            }
        }
        
        
    }
    
    private int getDiasVencimientoCliente(String codCliente){
        int dias_vencimiento = 0;
        try{
            String sql = "SELECT cond_pago FROM cliente WHERE cod_cliente = " + codCliente;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    dias_vencimiento = rs.getInt("cond_pago");
                }else{
                    dias_vencimiento = 0;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return dias_vencimiento;
    }
    
    private String calculaVencimiento(int condPago, String fecha){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecActual = new Date();
        try{
            fecActual = sdf.parse(fecha);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        calendar.setTime(fecActual);
        int vence = condPago;
        calendar.add(Calendar.MONTH, 1); // mantiene siempre la misma fecha y le suma + 1 al mes 
        result = sdf.format(calendar.getTime());
        return result;
    }
    
    private void confirmarDatos(){
        double montoTotal = Double.parseDouble(jTFTotalDoc.getText().trim().replace(",", ""));
            double montoTable = 0;
            
            for(int i = 0; i < jTCuotas.getRowCount(); i++){
                montoTable += Double.parseDouble(jTCuotas.getValueAt(i, 1).toString());
            }
            
            System.out.println("MONTO TOTAL: " + montoTotal + "\nMONTO TABLE: " + montoTable);
            if(montoTotal == montoTable){
                jBGuardar.setEnabled(true);
                jBGuardar.grabFocus();
            }else{
                jBGuardar.setEnabled(false);
            } 
    }
    
    private boolean grabarDocumento(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoDatos = leerTablaCuotas();
        if(!problemLeyendoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = true;
        }
        return estadoGrabado;
    }
    
    private boolean leerTablaCuotas(){
        
        boolean resultOperacion = false;
        boolean resultOperacionInsertCuotas = false;
        boolean problemFound = false;
        
        String vCodEmpresa = utiles.Utiles.getCodEmpresaDefault();
        String vCodLocal = utiles.Utiles.getCodLocalDefault(vCodEmpresa);
        String vCodSector = utiles.Utiles.getCodSectorDefault(vCodLocal);
        String vTipoComprob = jCBTipoOperacion.getSelectedItem().toString();
        int vCodCaja = 99; // nro de caja para las operaciones administrativas
        String vCodCliente = jTFCodCliente.getText().trim();
        int nroComprob = Integer.parseInt(jTFNroDoc.getText().trim());
        int cantidad_cuotas = Integer.parseInt(jTFCantCuotas.getText().trim());
        String vObs = jTFConcepto.getText().trim();
        
        for(int i = 0; i < jTCuotas.getRowCount(); i++){
            String fec_vencimiento = jTCuotas.getValueAt(i, 2).toString();
            double monto_cuota = Double.parseDouble(jTCuotas.getValueAt(i, 1).toString());
            int nro_cuota = Integer.parseInt(jTCuotas.getValueAt(i, 0).toString()); 
            vObs = vObs + " " + nro_cuota;
            
            resultOperacion = insertNuevasCuotas(vCodEmpresa, vCodLocal, vCodSector, vCodCaja, vTipoComprob, nroComprob, vCodCliente, fec_vencimiento, 
                                                 monto_cuota, nro_cuota, cantidad_cuotas, vObs);
            
            if(!resultOperacion){
                resultOperacionInsertCuotas = true;
                break;
            }
        }
        
        if(resultOperacionInsertCuotas){
            problemFound = true;
            if(!rollBacktDatos()){
                problemFound = true;
            }
        }else{
            if(!commitDatos()){
                problemFound = true;
            }
        }
        return problemFound;
        
    }
    
    private boolean insertNuevasCuotas(String codEmpresa, String codLocal, String codSector, int codCaja, String tipoComprob, int nroTicket, 
                                       String codCliente, String fecVencimiento, double montoCuota, int nroCuota, int cantCuotas, String obs){
        String sql = "INSERT INTO venta_det_cuotas (cod_empresa, cod_local, cod_sector, cod_caja, tip_comprob, nro_ticket, nro_comprob, fec_comprob, "
                   + "cod_cliente, nro_cuota, can_cuota, fec_vencimiento, monto_cuota, monto_retenido, cod_moneda, tip_cambio, nro_recibo, fec_recibo, "
                   + "estado, cod_usuario, fec_vigencia, nro_pago, observacion, nro_turno, es_venta, nro_timbrado, ven_timbrado, nro_factura, nro_timbrado_factura) "
                   + "VALUES (" + codEmpresa + ", "
                   + codLocal + ", "
                   + codSector + ", "
                   + codCaja + ", '"
                   + tipoComprob + "', "
                   + nroTicket + ", "
                   + nroTicket + ", current_timestamp, "
                   + codCliente + ", "
                   + nroCuota + ", "
                   + cantCuotas + ", "
                   + "TO_DATE('" + fecVencimiento + "', 'dd/MM/yyyy'), "
                   + montoCuota + ", "
                   + "0, " // monto retenido
                   + "1, " // codigo moneda
                   + "1, " // tipo de cambio
                   + "0, " // nro recibo
                   + "null, " // fec_recibo
                   + "'V', " // estado 
                   + FormMain.codUsuario + ", "
                   + "current_timestamp, " // fec_vigencia
                   + "0, '" // nro pago
                   + obs + "', "
                   + "0, " // nro turno 
                   + "'N', " // es venta
                   + "0, " // nro timbrado 
                   + "current_timestamp, " // vencimiento timbrado 
                   + "0, 0)" ; // nro de factura, nro_timbrado_factura
        System.out.println("INSERT NUEVAS CUOTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean updateVentaDetCuotas(String codCliente, String nroCaja, int nroComprob, String tipoComprob){
        String sql = "UPDATE venta_det_cuotas SET estado = 'A' WHERE cod_cliente = " + codCliente + " AND cod_caja = " + nroCaja + " "
                   + "AND nro_comprob = " + nroComprob + " AND tip_comprob = '" + tipoComprob + "'";
        System.out.println("UPDATE VTA-DET-CUOTAS DEL DOC MODIFICADO: " + sql);
        return (grabarPrevioCommit(sql));
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
    
    private void imprimirVale(){
        String sql = "SELECT * FROM cliente WHERE cod_cliente = 1";
        
        String nro_vale = jTFNroDoc.getText().trim();
        String monto_vale = jTFTotalDoc.getText().trim();
        //double monto = Double.parseDouble(jTFTotalDoc.getText().trim().replace(",", ""));
        String fec_vale = jTFFecDoc.getText().trim();
        String nombre_cliente = jTFCodCliente.getText().trim() + " " + jLNombreCliente.getText().trim();
        String ruc_cliente = JTFRucCliente.getText().trim();
        String nombre_empresa = utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString());
        
        // -- Convertir en texto el monto
            String mon;
            //String mont = monto_moneda.substring(0, String.valueOf(monto).length() - 2); // ejemplo 300,000.00 deja en 300000.
            String mont = monto_vale.replace(",", ""); // ejemplo 300,000.00 deja en 300000.
            if(mont.length() > 6){ // si alcanza el millon
                mont = mont.substring(0, mont.length());
                mon = mont.replace(",", ""); // deja en 300000
            }else{
                mon = mont.replace(",", ""); // deja en 300000
            }
            int monto_entero = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = "Guaraníes " + numero.convertirLetras(monto_entero);
        // -- Fin de convertir en texto el monto
        
        try{
            LibReportes.parameters.put("pNroVale", nro_vale);
            LibReportes.parameters.put("pMontoValeNumero", monto_vale);
            LibReportes.parameters.put("pMontoValeLetras", montoTxt);
            LibReportes.parameters.put("pFechaVale", fec_vale);
            LibReportes.parameters.put("pNombreCliente", nombre_cliente);
            LibReportes.parameters.put("pRucCliente", ruc_cliente);
            LibReportes.parameters.put("pNombreEmpresa", nombre_empresa);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "vale_debcre_cliente");
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLNombreLocal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTFFecTesoreria = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTFNroDoc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFFecDoc = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jCBTipoOperacion = new javax.swing.JComboBox<>();
        jLTipoOperacion = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        JTFRucCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFCodVendedor = new javax.swing.JTextField();
        jLNombreVendedor = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTFormaPago = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jTFCodCta = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFDescCta = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFTipoCta = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFNroComprob = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFFecComprob = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTFImporteComprob = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFCotizacion = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFImporteFinal = new javax.swing.JTextField();
        jBRefresh = new javax.swing.JButton();
        jBConfirmarFormaPago = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTFConcepto = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFTotalDoc = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTCuotas = new javax.swing.JTable();
        jBConfirmarCuota = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jTFCantCuotas = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTFPrimerVencimiento = new javax.swing.JTextField();
        jBNuevo = new javax.swing.JButton();
        jBGuardar = new javax.swing.JButton();
        jBImpPagare = new javax.swing.JButton();
        jBImpVale = new javax.swing.JButton();
        jBImpRecibo = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de DEB/CRE a Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Fecha de Tesorería:");

        jTFFecTesoreria.setEditable(false);
        jTFFecTesoreria.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecTesoreria.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del documento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Nro. Documento:");

        jTFNroDoc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroDoc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroDoc.setText("0");
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

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Fec. Documento:");

        jTFFecDoc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecDoc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecDocFocusGained(evt);
            }
        });
        jTFFecDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecDocKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Operación:");

        jCBTipoOperacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DEB", "CRE" }));
        jCBTipoOperacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jCBTipoOperacionFocusLost(evt);
            }
        });
        jCBTipoOperacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTipoOperacionActionPerformed(evt);
            }
        });
        jCBTipoOperacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBTipoOperacionKeyPressed(evt);
            }
        });

        jLTipoOperacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLTipoOperacion.setText("DEBITO");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodCliente.setText("0");
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

        JTFRucCliente.setEditable(false);
        JTFRucCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        JTFRucCliente.setText("0");

        jLNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreCliente.setText("***");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Vendedor:");

        jTFCodVendedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodVendedor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodVendedor.setText("0");
        jTFCodVendedor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodVendedorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodVendedorFocusLost(evt);
            }
        });
        jTFCodVendedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodVendedorKeyPressed(evt);
            }
        });

        jLNombreVendedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreVendedor.setText("***");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jCBTipoOperacion, 0, 70, Short.MAX_VALUE)
                                    .addComponent(jTFCodCliente)
                                    .addComponent(jTFCodVendedor))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(JTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLNombreVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTFFecDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jCBTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLTipoOperacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(JTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFCodVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreVendedor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Formas de pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTFormaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cta", "Descripción", "Tipo", "Nro. comprob. ", "Fecha", "Importe", "Cotización", "Imp final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTFormaPago);
        if (jTFormaPago.getColumnModel().getColumnCount() > 0) {
            jTFormaPago.getColumnModel().getColumn(0).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(1).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(2).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(3).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(4).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(5).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(6).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(7).setResizable(false);
        }

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel9.setText("CUENTA");

        jTFCodCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodCta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodCta.setText("0");
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

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel10.setText("DESCRIPCIÓN");

        jTFDescCta.setEditable(false);
        jTFDescCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel11.setText("TIPO");

        jTFTipoCta.setEditable(false);
        jTFTipoCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoCta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel12.setText("NRO. COMPROB.");

        jTFNroComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroComprob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroComprob.setText("0");
        jTFNroComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroComprobFocusGained(evt);
            }
        });
        jTFNroComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroComprobKeyPressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel13.setText("FECHA");

        jTFFecComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecComprob.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecComprobFocusGained(evt);
            }
        });
        jTFFecComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecComprobKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel14.setText("IMPORTE");

        jTFImporteComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFImporteComprob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFImporteComprob.setText("0");
        jTFImporteComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFImporteComprobFocusGained(evt);
            }
        });
        jTFImporteComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFImporteComprobKeyPressed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel15.setText("COTIZACIÓN");

        jTFCotizacion.setEditable(false);
        jTFCotizacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCotizacion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCotizacion.setText("0");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel16.setText("IMPORTE FINAL");

        jTFImporteFinal.setEditable(false);
        jTFImporteFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFImporteFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFImporteFinal.setText("0");

        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N
        jBRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshActionPerformed(evt);
            }
        });

        jBConfirmarFormaPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarFormaPagoActionPerformed(evt);
            }
        });
        jBConfirmarFormaPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBConfirmarFormaPagoKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Concepto:");

        jTFConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFConcepto.setText("***");
        jTFConcepto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFConceptoFocusGained(evt);
            }
        });
        jTFConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFConceptoKeyPressed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("Total:");

        jTFTotalDoc.setEditable(false);
        jTFTotalDoc.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jTFTotalDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalDoc.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTotalDoc))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTFCodCta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTFDescCta, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFTipoCta, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFImporteComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(58, 58, 58)
                                .addComponent(jLabel11)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addGap(58, 58, 58)
                                .addComponent(jLabel14)
                                .addGap(57, 57, 57)
                                .addComponent(jLabel15)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jTFImporteFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBRefresh)
                    .addComponent(jBConfirmarFormaPago))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel10))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(jLabel14)
                        .addComponent(jLabel15)
                        .addComponent(jLabel16)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTipoCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNroComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFImporteComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFImporteFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jBRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBConfirmarFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTFConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jTFTotalDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cuotas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTCuotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cuota", "Monto cuota", "Fecha venc"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTCuotas);
        if (jTCuotas.getColumnModel().getColumnCount() > 0) {
            jTCuotas.getColumnModel().getColumn(0).setResizable(false);
            jTCuotas.getColumnModel().getColumn(1).setResizable(false);
            jTCuotas.getColumnModel().getColumn(2).setResizable(false);
        }

        jBConfirmarCuota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarCuota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarCuotaActionPerformed(evt);
            }
        });
        jBConfirmarCuota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBConfirmarCuotaKeyPressed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setText("Cuotas:");

        jTFCantCuotas.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCantCuotas.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCantCuotas.setText("1");
        jTFCantCuotas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantCuotasFocusGained(evt);
            }
        });
        jTFCantCuotas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantCuotasKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setText("Primer vencimiento:");

        jTFPrimerVencimiento.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFPrimerVencimiento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFPrimerVencimiento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFPrimerVencimientoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFPrimerVencimientoFocusLost(evt);
            }
        });
        jTFPrimerVencimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFPrimerVencimientoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel20))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTFCantCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTFPrimerVencimiento)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBConfirmarCuota, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jBConfirmarCuota, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFCantCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFPrimerVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
            }
        });

        jBGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGuardar.setText("Grabar");
        jBGuardar.setEnabled(false);
        jBGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGuardarActionPerformed(evt);
            }
        });

        jBImpPagare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImpPagare.setText("Imprimir Pagaré");
        jBImpPagare.setEnabled(false);

        jBImpVale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImpVale.setText("Imprimir Vale");
        jBImpVale.setEnabled(false);
        jBImpVale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImpValeActionPerformed(evt);
            }
        });

        jBImpRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImpRecibo.setText("Imprimir Recibo");
        jBImpRecibo.setEnabled(false);
        jBImpRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImpReciboActionPerformed(evt);
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
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(102, 102, 102)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecTesoreria))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jBNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImpPagare)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImpVale)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImpRecibo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa)
                    .addComponent(jLabel3)
                    .addComponent(jTFFecTesoreria, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBGuardar)
                    .addComponent(jBImpPagare)
                    .addComponent(jBImpVale)
                    .addComponent(jBImpRecibo)
                    .addComponent(jBCancelar)
                    .addComponent(jBSalir))
                .addContainerGap(12, Short.MAX_VALUE))
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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jCBTipoOperacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTipoOperacionActionPerformed
        tipoDeOperacion();
    }//GEN-LAST:event_jCBTipoOperacionActionPerformed

    private void jTFNroDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocFocusGained
        jTFNroDoc.selectAll();
    }//GEN-LAST:event_jTFNroDocFocusGained

    private void jTFNroDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecDoc.grabFocus();
        }
    }//GEN-LAST:event_jTFNroDocKeyPressed

    private void jTFFecDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDocFocusGained
        jTFFecDoc.selectAll();
    }//GEN-LAST:event_jTFFecDocFocusGained

    private void jTFFecDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroComprob.setText(jTFNroDoc.getText().trim());
            jTFFecComprob.setText(jTFFecDoc.getText().trim());
            jCBTipoOperacion.grabFocus();
        }
    }//GEN-LAST:event_jTFFecDocKeyPressed

    private void jCBTipoOperacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBTipoOperacionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodCliente.grabFocus();
        }
    }//GEN-LAST:event_jCBTipoOperacionKeyPressed

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
                jTFCodVendedor.grabFocus();
            }
            
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFCodVendedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodVendedorFocusGained
        jTFCodVendedor.selectAll();
    }//GEN-LAST:event_jTFCodVendedorFocusGained

    private void jTFCodVendedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodVendedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodCta.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas empleados = new DlgConsultas(new JFrame(), true);
                empleados.pack();
                empleados.setTitle("ATOMSystems|Main - Consulta de Empleados");
                empleados.dConsultas("empleado", "nombre", "cod_empleado", "nombre", "apellido", "fec_ingreso", "Codigo", "Nombre", "Apellido", "Ingreso");
                empleados.setText(jTFCodVendedor);
                empleados.tfDescripcionBusqueda.setText("%");
                empleados.tfDescripcionBusqueda.selectAll();
                empleados.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodVendedorKeyPressed

    private void jTFCodCtaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCtaFocusGained
        jTFCodCta.selectAll();
        jTFImporteComprob.setText("0");
    }//GEN-LAST:event_jTFCodCtaFocusGained

    private void jTFCodCtaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCtaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroComprob.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            DlgConsultasCuentasMonedas cuentas = new DlgConsultasCuentasMonedas(new JFrame(), true);
            cuentas.pack();
            cuentas.setText(jTFCodCta);
            cuentas.setVisible(true);
            cuentas.jTFDescripcion.setText("%");
            cuentas.jTFDescripcion.selectAll();
        }
    }//GEN-LAST:event_jTFCodCtaKeyPressed

    private void jTFNroComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroComprobFocusGained
        jTFNroComprob.selectAll();
    }//GEN-LAST:event_jTFNroComprobFocusGained

    private void jTFNroComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecComprob.grabFocus();
        }
    }//GEN-LAST:event_jTFNroComprobKeyPressed

    private void jTFFecComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecComprobFocusGained
        jTFFecComprob.selectAll();
    }//GEN-LAST:event_jTFFecComprobFocusGained

    private void jTFFecComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFImporteComprob.grabFocus();
        }
    }//GEN-LAST:event_jTFFecComprobKeyPressed

    private void jTFImporteComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFImporteComprobFocusGained
        jTFImporteComprob.setText(jTFImporteComprob.getText().trim().replace(",", ""));
        jTFImporteComprob.selectAll();
    }//GEN-LAST:event_jTFImporteComprobFocusGained

    private void jTFImporteComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFImporteComprobKeyPressed
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",",""));
        double valor_ingresado = Double.parseDouble(jTFImporteComprob.getText().trim().replace(",", ""));
        double valor_final = cotizacion * valor_ingresado;
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(valor_ingresado < 0 || valor_ingresado == 0 || jTFImporteComprob.getText().trim().equals("")){
                jTFCodCta.requestFocus();
            }
            else{
                jTFImporteFinal.setText(decimalFormat.format(valor_final));
                addFormaPago();
                jBConfirmarFormaPago.grabFocus();
            }
        }
    }//GEN-LAST:event_jTFImporteComprobKeyPressed

    private void jBConfirmarFormaPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBConfirmarFormaPagoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            String concepto = jCBTipoOperacion.getSelectedItem().toString() + " NRO: " + jTFNroDoc.getText().trim() + " EN CUENTA DE CLIENTE.";
            jTFConcepto.setText(concepto);
            jTFConcepto.grabFocus();
        }
    }//GEN-LAST:event_jBConfirmarFormaPagoKeyPressed

    private void jTFConceptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFConceptoFocusGained
        jTFConcepto.selectAll();
    }//GEN-LAST:event_jTFConceptoFocusGained

    private void jTFConceptoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFConceptoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCantCuotas.grabFocus();
        }
    }//GEN-LAST:event_jTFConceptoKeyPressed

    private void jTFCantCuotasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantCuotasFocusGained
        jTFCantCuotas.selectAll();
    }//GEN-LAST:event_jTFCantCuotasFocusGained

    private void jTFCantCuotasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantCuotasKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFPrimerVencimiento.grabFocus();
        }
    }//GEN-LAST:event_jTFCantCuotasKeyPressed

    private void jTFPrimerVencimientoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPrimerVencimientoFocusGained
        jTFPrimerVencimiento.selectAll();
    }//GEN-LAST:event_jTFPrimerVencimientoFocusGained

    private void jTFPrimerVencimientoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFPrimerVencimientoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBConfirmarCuota.grabFocus();
        }
    }//GEN-LAST:event_jTFPrimerVencimientoKeyPressed

    private void jBConfirmarCuotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBConfirmarCuotaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            confirmarDatos();
        }
    }//GEN-LAST:event_jBConfirmarCuotaKeyPressed

    private void jTFCodVendedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodVendedorFocusLost
        if(jTFCodVendedor.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío. Favor verifique.", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodVendedor.requestFocus();
        }else if(jTFCodVendedor.getText().trim().equals("0")){
            JOptionPane.showMessageDialog(this, "Informe de un cobrador válido!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodVendedor.requestFocus();
        }else {
            jLNombreVendedor.setText(getNombreEmpleado(jTFCodVendedor.getText().trim()));
        }
    }//GEN-LAST:event_jTFCodVendedorFocusLost

    private void jTFCodCtaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCtaFocusLost
        if(!jTFCodCta.getText().trim().equals("")){
            getCtaDenCotiz(jTFCodCta.getText().trim());
        }
    }//GEN-LAST:event_jTFCodCtaFocusLost

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        setBotonesNuevo();
        setEstadoComponentes(true);
        llenarCampos();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setBotonesCancelar();
        setEstadoComponentes(false);
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshActionPerformed
        limpiarTablaFormaPago();
        jTFCodCta.grabFocus();
        jTFCodCta.setText("1");
    }//GEN-LAST:event_jBRefreshActionPerformed

    private void jBConfirmarFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarFormaPagoActionPerformed
        totalizar();
    }//GEN-LAST:event_jBConfirmarFormaPagoActionPerformed

    private void jTFPrimerVencimientoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPrimerVencimientoFocusLost
        limpiarTablaCuotas();
        addCuotas();
    }//GEN-LAST:event_jTFPrimerVencimientoFocusLost

    private void jBConfirmarCuotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarCuotaActionPerformed
        confirmarDatos();
    }//GEN-LAST:event_jBConfirmarCuotaActionPerformed

    private void jBGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGuardarActionPerformed
        if(grabarDocumento()){
            JOptionPane.showMessageDialog(this, "Documento grabado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            setEstadoComponentes(false);
            setBotonesGrabado();
        }else{
            JOptionPane.showMessageDialog(this, "Error grabando documento", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jBGuardarActionPerformed

    private void jCBTipoOperacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBTipoOperacionFocusLost
        if(jCBTipoOperacion.getSelectedItem().toString().equals("CRE")){
            jTFCantCuotas.setEditable(false);
        }else{
            jTFCantCuotas.setEditable(true);
        }
    }//GEN-LAST:event_jCBTipoOperacionFocusLost

    private void jBImpValeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImpValeActionPerformed
        imprimirVale();
    }//GEN-LAST:event_jBImpValeActionPerformed

    private void jBImpReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImpReciboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBImpReciboActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroDebCre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroDebCre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroDebCre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroDebCre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroDebCre dialog = new RegistroDebCre(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField JTFRucCliente;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBConfirmarCuota;
    private javax.swing.JButton jBConfirmarFormaPago;
    private javax.swing.JButton jBGuardar;
    private javax.swing.JButton jBImpPagare;
    private javax.swing.JButton jBImpRecibo;
    private javax.swing.JButton jBImpVale;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBTipoOperacion;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLNombreVendedor;
    private javax.swing.JLabel jLTipoOperacion;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTCuotas;
    private javax.swing.JTextField jTFCantCuotas;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCta;
    private javax.swing.JTextField jTFCodVendedor;
    private javax.swing.JTextField jTFConcepto;
    private javax.swing.JTextField jTFCotizacion;
    private javax.swing.JTextField jTFDescCta;
    private javax.swing.JTextField jTFFecComprob;
    private javax.swing.JTextField jTFFecDoc;
    private javax.swing.JTextField jTFFecTesoreria;
    private javax.swing.JTextField jTFImporteComprob;
    private javax.swing.JTextField jTFImporteFinal;
    private javax.swing.JTextField jTFNroComprob;
    private javax.swing.JTextField jTFNroDoc;
    private javax.swing.JTextField jTFPrimerVencimiento;
    private javax.swing.JTextField jTFTipoCta;
    private javax.swing.JTextField jTFTotalDoc;
    private javax.swing.JTable jTFormaPago;
    // End of variables declaration//GEN-END:variables
}
