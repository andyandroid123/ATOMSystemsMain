/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import static utiles.Utiles.padLefth;
import views.busca.DlgConsultas;
import views.busca.DlgConsultasTipoComprob;

/**
 *
 * @author Andres
 */
public class RegistroComprobanteGastos extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector, codCentroCosto;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    private final DecimalFormat decimalFormatIva = new DecimalFormat("###,###,###.##");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // montos cabecera con pattern
    double totalExento, totalGrav5, totalGrav10, totalMontoCab;
    
    public RegistroComprobanteGastos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        codLocal = jCBCodLocal.getSelectedItem().toString();
        codSector = jCBCodSector.getSelectedItem().toString();
        codCentroCosto = jCBCodCentroCosto.getSelectedItem().toString();
        configCampos();
        llenarCampos();
        cerrarVentana();
        setEstadoComponentes(false);
        jBGrabar.setEnabled(false);
        jBCancelar.setEnabled(false);
    }

    private void setEstadoComponentes(boolean estado){
        jCBCodEmpresa.setEnabled(estado);
        jCBCodLocal.setEnabled(estado);
        jCBCodSector.setEnabled(estado);
        jTFCodProveedor.setEnabled(estado);
        jTFNroDocumento.setEnabled(estado);
        jTFFecComprob.setEnabled(estado);
        jTFTipoComprob.setEnabled(estado);
        jTFCodMotivo.setEnabled(estado);
        jTFNroTimbrado.setEnabled(estado);
        jTFFecVencTimbrado.setEnabled(estado);
        jCBCodCentroCosto.setEnabled(estado);
        jTFObs.setEnabled(estado);
        jTFCodMoneda.setEnabled(estado);
        jTFTipoCambio.setEnabled(estado);
        jTFMontoExento.setEnabled(estado);
        jTFMontoGrav5.setEnabled(estado);
        jTFMontoGrav10.setEnabled(estado);
        jTFFecVencimiento.setEnabled(estado);
        jTFCodTipoGasto.setEnabled(estado);
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane.showMessageDialog(RegistroComprobanteGastos.this, "Clic en el botón SALIR!", "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private void llenarCampos(){
        jTFCodMoneda.setText("1");
        jLDescMoneda.setText("GUARANI");
        jTFTipoCambio.setText("1");
        jTFCodProveedor.setText("1");
        jTFCodMotivo.setText("1");
        jTFCodTipoGasto.setText("2");
        jTFTipoComprob.setText("FPC");
        jTFFecComprob.setText(fecVigencia);
        jTFFecVencTimbrado.setText(fecVigencia);
        jTFFecVencimiento.setText(fecVigencia);
        jTFNroTimbrado.setText("0");
        jTFMontoExento.setText("0.00");
        jTFMontoGrav5.setText("0.00");
        jTFMontoGrav10.setText("0.00");
        jTFMontoTotal.setText("0.00");
        jTFIva5.setText("0.00");
        jTFIva10.setText("0.00");
        jLRazonSocEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLDescLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jLDescSector.setText(utiles.Utiles.getSectorDescripcion(codLocal, codSector));
        jLDescCentroCosto.setText(utiles.Utiles.getCentroCostoDescripcion(codLocal, codSector));
    }
    
    private void configCampos(){
        jTFCodProveedor.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFNroDocumento.setDocument(new MaxLength(15, "UPPER", "ALFA"));
        jTFTipoComprob.setDocument(new MaxLength(3, "UPPER", "ALFA"));
        jTFNroTimbrado.setDocument(new MaxLength(8, "", "ENTERO"));
        jTFCodMotivo.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFCodTipoGasto.setDocument(new MaxLength(4, "", "ENTERO"));
        jTFCodMoneda.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFObs.setDocument(new MaxLength(35, "UPPER", "ALFA"));
        jTFFecVencTimbrado.setInputVerifier(new FechaInputVerifier(jTFFecVencTimbrado));
        jTFFecComprob.setInputVerifier(new FechaInputVerifier(jTFFecComprob));
        jTFFecVencimiento.setInputVerifier(new FechaInputVerifier(jTFFecVencimiento));
        
        jTFCodProveedor.addFocusListener(new Focus()); 
        jTFNroDocumento.addFocusListener(new Focus()); 
        jTFFecComprob.addFocusListener(new Focus()); 
        jTFTipoComprob.addFocusListener(new Focus());
        jTFCodMotivo.addFocusListener(new Focus()); 
        jTFObs.addFocusListener(new Focus()); 
        jTFCodMoneda.addFocusListener(new Focus()); 
        jTFTipoCambio.addFocusListener(new Focus());
        jTFMontoExento.addFocusListener(new Focus());
        jTFMontoGrav5.addFocusListener(new Focus());
        jTFMontoGrav10.addFocusListener(new Focus());
        jTFMontoTotal.addFocusListener(new Focus());
        jTFFecVencimiento.addFocusListener(new Focus());
        
        jTFNroTimbrado.addFocusListener(new Focus());
        jTFFecVencTimbrado.addFocusListener(new Focus());
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
        utiles.Utiles.cargaComboCentroCosto(this.jCBCodCentroCosto);
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
    
    private void formatoNroDocumento(){
        String sinPunto = jTFNroDocumento.getText().replace(".", "");
        String str1 = "";
        String str2 = "";
        String str3 = "";
        int tam = sinPunto.length();
        if (tam > 0 && tam < 4) {
            jTFNroDocumento.setText(padLefth(3, "0", sinPunto));
        }
        if (tam > 3 && tam < 7) {
            str1 = jTFNroDocumento.getText().substring(0, 3);
            str2 = jTFNroDocumento.getText();
            str2 = sinPunto.substring(3, tam);
            jTFNroDocumento.setText(str1 + "-" + padLefth(3, "0", str2));
        }

        if (tam > 7) {
            tam = jTFNroDocumento.getText().length();
            str1 = jTFNroDocumento.getText().substring(0, 3);
            str2 = jTFNroDocumento.getText().substring(4, 7);
            str3 = jTFNroDocumento.getText().substring(8, tam);
            jTFNroDocumento.setText("");
            jTFNroDocumento.setText(str1 + "-" + str2 + "-" + padLefth(7, "0", str3));
        }
    }
    
    private void getDescripcionTipoDocumento(String codigo){
        if(jTFTipoComprob.getText().trim().equals("0")){
            jLDescTipoComprob.setText("DOCUMENTO INEXISTENTE");
        }else{
            jLDescTipoComprob.setText("");
            try{
                String sql = "SELECT descripcion FROM tipo_comprobante WHERE tip_comprob = '" + codigo + "'";
                System.out.println("SELECT TIPO COMPROBANTE: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescTipoComprob.setText(rs.getString("descripcion"));
                    }else{
                        jLDescTipoComprob.setText("DOCUMENTO INEXISTENTE");
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
        }
    }
    
    private void getTimbradoDoc(){
        try{
                String sql = "SELECT nro_timbrado, to_char(ven_timbrado, 'dd/MM/yyyy') AS vencimiento FROM compra_cab WHERE "
                           + "cod_proveedor = " + jTFCodProveedor.getText().trim() + " AND tip_comprob  = '" + jTFTipoComprob.getText() + "' "
                           + "ORDER BY fec_vigencia DESC LIMIT 1";
                System.out.println("GETTING NRO TIMBRADO: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jTFNroTimbrado.setText(String.valueOf(rs.getInt("nro_timbrado")));
                        jTFFecVencTimbrado.setText(rs.getString("vencimiento"));
                    }else{
                        jTFNroTimbrado.setText("0");
                        jTFFecVencTimbrado.setText(fecVigencia);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
    }
    
    private void getDescripcionMotivo(String codigo){
        if(jTFCodMotivo.getText().trim().equals("0")){
            jLDescMotivo.setText("MOTIVO INEXISTENTE");
        }else{
            jLDescMotivo.setText("");
            try{
                String sql = "SELECT descripcion FROM motivo WHERE cod_motivo = " + codigo + "";
                System.out.println("SELECT MOTIVO: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescMotivo.setText(rs.getString("descripcion"));
                    }else{
                        jLDescMotivo.setText("MOTIVO INEXISTENTE");
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
        }
    }
    
    private void getDescTipoGasto(String codigo){
        if(jTFCodMotivo.getText().trim().equals("0")){
            jLDescTipoGasto.setText("GASTO INEXISTENTE");
        }else{
            jLDescTipoGasto.setText("");
            try{
                String sql = "SELECT descripcion FROM tipo_mercaderia WHERE cod_tipomerc = " + codigo + "";
                System.out.println("SELECT TIPO GASTO: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescTipoGasto.setText(rs.getString("descripcion"));
                    }else{
                        jLDescTipoGasto.setText("GASTO INEXISTENTE");
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
        }
    }
    
    private boolean controlarNroDoc(){
        boolean result = false;
        long nroComprob = Long.valueOf(eliminaCaracteres(jTFNroDocumento.getText(), "-"));
        System.out.println("NRO DE COMPROBANTE CONTROL: " + nroComprob);
        String sql = "SELECT nro_comprob FROM compra_cab WHERE nro_comprob = " + nroComprob + " AND nro_timbrado = '" + jTFNroTimbrado.getText().trim() + "' AND"
                   + " cod_proveedor = " + jTFCodProveedor.getText().trim() + " AND tip_comprob = '" + jTFTipoComprob.getText().trim() + "' LIMIT 1";
        System.out.println("CONSULTA DOC EXISTENTE: " + sql);
        ResultSet rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    result = true;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        
        return result;
    }
    
    private String eliminaCaracteres(String cadena, String caracter) {

        cadena = cadena.replace(caracter, "");

        return cadena;
    }
    
    private boolean vencimientoTimbrado(){
        boolean result = true;
        try{
            System.out.println("FECHA ACTUAL: " + fecVigencia + "\nFECHA TIMBRADO: " + jTFFecVencTimbrado.getText());
            Date fechaActual = sdf.parse(fecVigencia);
            Date fechaTimbrado = sdf.parse(jTFFecVencTimbrado.getText());
            if(fechaTimbrado.equals(fechaActual) || fechaTimbrado.after(fechaActual)){
                result = true;
            }else{
                result = false;                
            }
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    private void getNombreMoneda(String codigo){
        if(jTFCodMoneda.getText().trim().equals("0")){
            jLDescMoneda.setText("MONEDA INEXISTENTE");
        }else{
            jLDescMoneda.setText("");
            try{
                String sql = "SELECT nombre, cotiz_venta FROM moneda WHERE cod_moneda = " + codigo + "";
                System.out.println("SELECT MONEDA: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescMoneda.setText(rs.getString("nombre"));
                        jTFTipoCambio.setText(decimalFormat.format(rs.getInt("cotiz_venta")));
                    }else{
                        jLDescMoneda.setText("MONEDA INEXISTENTE");
                        jTFTipoCambio.setText("1");
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
        }
    }
    
    private void quitarPatternMontos(){
        if(!jTFMontoExento.getText().trim().equals(".00")){
            String valorExento = jTFMontoExento.getText().trim().replace(",", "");
            jTFMontoExento.setText(valorExento.replace(".00", ""));
        }
        
        if(!jTFMontoGrav5.getText().trim().equals(".00")){
            String valorGrav5 = jTFMontoGrav5.getText().trim().replace(",", "");
            jTFMontoGrav5.setText(valorGrav5.replace(".00", ""));
        }
        
        if(!jTFMontoGrav10.getText().trim().equals(".00")){
            String valorGrav10 = jTFMontoGrav10.getText().trim().replace(",", "");
            jTFMontoGrav10.setText(valorGrav10.replace(".00", ""));
        }
    }
    
    private void completarDatosProveedor(String codigo){
        if(jTFCodProveedor.getText().equals("0")){
            jTFRazonSocProveedor.setText("PROVEEDOR INEXISTENTE!");
            jTFRazonSocProveedor.setForeground(Color.red);
            jTFCodProveedor.requestFocus();
        }else{
            jTFRazonSocProveedor.setForeground(Color.black);
            jTFRazonSocProveedor.setText("");
            try{
                String sql = "SELECT razon_soc || ' - ' || ruc_proveedor AS datosproveedor FROM proveedor WHERE cod_proveedor = " + codigo;
                System.out.println("SQL Proveedor: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFRazonSocProveedor.setText(rs.getString("datosproveedor"));
                }else{
                    jTFRazonSocProveedor.setText("PROVEEDOR INEXISTENTE!");
                    jTFRazonSocProveedor.setForeground(Color.red);
                    jTFCodProveedor.requestFocus();
                }
            }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }finally{
                DBManager.CerrarStatements();
            }
        }        
    }
    
    private boolean camposOk(){
        boolean result = true;
        if(jTFObs.getText().trim().equals("")){
            jTFObs.setText("-");
        }
        
        if(jTFMontoTotal.getText().trim().equals(".00")){
            result = false;
            JOptionPane.showMessageDialog(this, "Debe indicar el monto del documento!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            jTFMontoExento.requestFocus();
        }
        return result;
    }
    
    private boolean grabarDatos(){
        boolean estadoGrabado = false;
        
        boolean problemRecuperandoDatos = recuperarDatos();
        if(!problemRecuperandoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = false;
        }
        
        return estadoGrabado;
    }
    
    private boolean recuperarDatos(){
        
        boolean resultOperacionGrabaCompras = true;
        boolean resultComprasCab = false;
        boolean resultComprasDetCuotas = false;
        boolean problemFound = false;
        
        // Datos a grabar 
        int vCodEmpresa = Integer.parseInt(jCBCodEmpresa.getSelectedItem().toString());
        int vCodLocal = Integer.parseInt(jCBCodLocal.getSelectedItem().toString());
        int vCodSector = Integer.parseInt(jCBCodSector.getSelectedItem().toString());
        int vCodCentroCosto = Integer.parseInt(jCBCodCentroCosto.getSelectedItem().toString());
        int vCodUsuario = FormMain.codUsuario;
        String vFecComprob = "to_timestamp('" + jTFFecComprob.getText() + "', 'dd/MM/yyyy')";
        String vTipoDoc = jTFTipoComprob.getText().trim();
        String vCodTipoGasto = jTFCodTipoGasto.getText().trim();
        int vCodMotivo = Integer.parseInt(jTFCodMotivo.getText().trim());
        long vNroComprob = Long.valueOf(eliminaCaracteres(jTFNroDocumento.getText(), "-"));
        int vCodProveedor = Integer.parseInt(jTFCodProveedor.getText().trim());
        String vFecVencimiento = jTFFecVencimiento.getText().trim();
        String[] nada = jTFRazonSocProveedor.getText().trim().split("-");
        String vRucProveedor = (nada[1] + "-" + nada[2]).trim();
        String totalExentoStr = jTFMontoExento.getText().trim().replace(",", "");
        double vTotalExento = Double.parseDouble(totalExentoStr);
        String totalGrav10Str = jTFMontoGrav10.getText().trim().replace(",", "");
        double vTotalGrav10 = Double.parseDouble(totalGrav10Str);
        String totalGrav5Str = jTFMontoGrav5.getText().trim().replace(",", "");
        double vTotalGrav5 = Double.parseDouble(totalGrav5Str);
        String totalMontoStr = jTFMontoTotal.getText().trim().replace(",", "");
        double vTotalMonto = Double.parseDouble(totalMontoStr);
        double vMontoIva5 = Double.parseDouble(jTFIva5.getText().trim().replace(",", ""));
        double vMontoIva10 = Double.parseDouble(jTFIva10.getText().trim().replace(",", ""));
        int vCodComprobFiscal = getCodComprobFiscal(vTipoDoc);
        String vNroTimbrado = jTFNroTimbrado.getText().trim();
        String vFecVencTimbrado = "to_timestamp('" + jTFFecVencTimbrado.getText().trim() + "', 'dd/MM/yyyy')";
        int vCodMoneda = Integer.parseInt(jTFCodMoneda.getText().trim());
        double vTipoCambio = Double.parseDouble(jTFTipoCambio.getText().trim());
        
        // Compras Cabecera
        resultOperacionGrabaCompras = grabarCompraCab(vCodEmpresa, vCodLocal, vTipoDoc, vNroComprob, vFecComprob, vCodProveedor, vFecVencimiento, 
                                                      vRucProveedor, vTotalExento, vTotalGrav10, vTotalGrav5, vTotalMonto, vCodUsuario, vCodSector, 
                                                      vCodMotivo, vCodCentroCosto, vCodComprobFiscal, vMontoIva5, vMontoIva10, vNroTimbrado, 
                                                      vFecVencTimbrado, vCodTipoGasto);
        
        if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar compra cab!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultComprasCab = true;
                resultComprasDetCuotas = true;
        }
        
        // Compras cuotas
        
        resultOperacionGrabaCompras = grabarComprasDetCuotas(vCodEmpresa, vCodLocal, vTipoDoc, vNroComprob, vFecComprob, vFecVencimiento, vTotalMonto, 
                                                             vCodMoneda, vTipoCambio, vCodProveedor, vCodUsuario, vCodSector);
        
        if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar compra cuotas!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultComprasDetCuotas = true;
        }
        
        if(resultComprasCab || resultComprasDetCuotas){
            problemFound = true;
            if(!rollBacktDatos()){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el RollBack!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    problemFound = true;
                }
        }else{
            if(!commitDatos()){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el Commit!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                problemFound = true;
            }
        }
        return problemFound;
    }
    
    private boolean commitDatos() {
        boolean result = true;
        try {
            DBManager.conn.commit();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
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
        }
        return result;
    }
    
    private boolean grabarComprasDetCuotas(int ctCodEmpresa, int ctCodLocal, String ctTipComprob, long ctNroComprob, String ctFecComprob, String ctFecVenc, 
                                           double ctMontoCuota, int ctCodMoneda, double ctTipoCambio, int ctCodProv, int ctCodUsuario, int ctCodSector){
        String sql = "INSERT INTO compra_det_cuotas (cod_empresa, cod_local, tip_comprob, nro_comprob, fec_comprob, nro_cuota, fec_vencimiento, "
                   + "monto_cuota, cod_moneda, tip_cambio, nro_pago, estado, monto_retenido, serie_pago, fec_vigencia, cod_proveedor, "
                   + "confirmado, cod_usuario, cod_sector, nro_timbrado, es_gasto) "
                   + "VALUES (" + ctCodEmpresa + ", "
                   + "" + ctCodLocal + ", '"
                   + "" + ctTipComprob + "', "
                   + "" + ctNroComprob + ", "
                   + "" + ctFecComprob + ", 1, '"
                   + "" + ctFecVenc + "', "
                   + "" + ctMontoCuota + ", "
                   + "" + ctCodMoneda + ", "
                   + "" + ctTipoCambio + ", 0, 'V', 0, 'A', 'now()', "
                   + "" + ctCodProv + ", 'N', "
                   + "" + ctCodUsuario + ", "
                   + "" + ctCodSector + ", 0, 'N')"; 
        System.out.println("COMPRA DET CUOTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarCompraCab(int cabCodEmpresa, int cabCodLocal, String cabTipoComprob, long cabNroComprob, String cabFecComprob, int cabCodProveedor, 
                                    String cabFecVencimiento, String cabRucProveedor, double cabTotalExento, double cabTotalGrav10, double cabTotalGrav5, 
                                    double cabTotalMonto, int cabCodUsuario, int cabCodSector, int cabCodMotivo, int cabCodCentroCosto, 
                                    int cabCodComprobFiscal, double cabIva5, double cabIva10, String cabNroTimbrado, String cabFecVencTimbrado, 
                                    String cabCodTipoGasto){
        String sql = "INSERT INTO compra_cab (cod_empresa, cod_local, tip_comprob, nro_comprob, fec_comprob, cod_proveedor, fec_vencimiento, nro_comprob_ref, "
                   + "ruc_proveedor, nro_pedido, monto_flete, total_exento, total_grava10, total_grava05, total_iva10, total_iva05, total_monto, estado, "
                   + "cod_usuario, fec_vigencia, cod_tipomerc, nro_asiento, referencia, es_iva_incluido, monto_retenido, cod_sector, nro_comprob_nci, "
                   + "fec_documento, cod_motivo, es_bonificacion, situacion, nro_timbrado, ven_timbrado, cod_centrocosto, cod_comprobante_fiscal, "
                   + "nro_comprob_ncp, tiene_detalle, cod_pcuenta, tip_comp, confirmado, ajuste_grava10, ajuste_grava05, ajuste_exento, es_gasto) "
                   + "VALUES (" + cabCodEmpresa + ", "
                   + "" + cabCodLocal + ", '"
                   + "" + cabTipoComprob + "', "
                   + "" + cabNroComprob + ", "
                   + "" + cabFecComprob + ", "
                   + "" + cabCodProveedor + ", '"
                   + "" + cabFecVencimiento + "', "
                   + "" + cabNroComprob + ", '"
                   + "" + cabRucProveedor + "', 0, 0, "
                   + "" + cabTotalExento + ", "
                   + "" + cabTotalGrav10 + ", "
                   + "" + cabTotalGrav5 + ", "
                   + "" + cabIva10 + ", "
                   + "" + cabIva5 + ", "
                   + "" + cabTotalMonto + ", 'V', "
                   + "" + cabCodUsuario + ", 'now()', " + cabCodTipoGasto + ", 0, '-', 'S', 0, "
                   + "" + cabCodSector + ", 0, "
                   + "" + cabFecComprob + ", "
                   + "" + cabCodMotivo + ", 'N', 'V', '" + cabNroTimbrado + "', " + cabFecVencTimbrado + ", " + cabCodCentroCosto + ", "
                   + "" + cabCodComprobFiscal + ", 0, 'S', 0, '"
                   + "" + cabTipoComprob + "', 'N', 0, 0, 0, 'N')";
        System.out.println("COMPRA CAB: " + sql);
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
    
    private String calculaVencimiento(String fecha) {
        String result = "";
        Calendar calen = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaAc = new Date();
        try {
            fechaAc = sdf.parse(fecha);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        calen.setTime(fechaAc);
        int vence = 30;
        calen.add(Calendar.DATE, vence);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        result = sdf2.format(calen.getTime());
        return result;

    }
    
    private int getCodComprobFiscal(String tipo){
        int result = 0;
        String sql = "SELECT cod_comprobante FROM tipo_comprobante WHERE tip_comprob = '" + tipo + "'";
        ResultSet rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_comprobante");
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
    
    private void setComponentesGrabado(){
        llenarCampos();
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        setEstadoComponentes(false);
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
        jLDescSector = new javax.swing.JLabel();
        jCBCodSector = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLDescLocal = new javax.swing.JLabel();
        jLRazonSocEmpresa = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jTFCodProveedor = new javax.swing.JTextField();
        jTFRazonSocProveedor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFNroDocumento = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFFecComprob = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFTipoComprob = new javax.swing.JTextField();
        jLDescTipoComprob = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTFCodMotivo = new javax.swing.JTextField();
        jLDescMotivo = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTFNroTimbrado = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFFecVencTimbrado = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jCBCodCentroCosto = new javax.swing.JComboBox<>();
        jLDescCentroCosto = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTFObs = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTFFecVencimiento = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTFCodTipoGasto = new javax.swing.JTextField();
        jLDescTipoGasto = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTFCodMoneda = new javax.swing.JTextField();
        jLDescMoneda = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTFTipoCambio = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFMontoExento = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFMontoGrav5 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFMontoGrav10 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFMontoTotal = new javax.swing.JTextField();
        jTFIva5 = new javax.swing.JTextField();
        jTFIva10 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jBNuevo = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Comprobantes de Gastos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("REGISTRO DE COMPROBANTES DE GASTOS");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Datos del Documento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLDescSector.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescSector.setText("***");

        jCBCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodSectorKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Sector:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Local:");

        jCBCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodLocalKeyPressed(evt);
            }
        });

        jLDescLocal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescLocal.setText("***");

        jLRazonSocEmpresa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLRazonSocEmpresa.setText("***");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Empresa:");

        jCBCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodEmpresaKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Prov:");

        jTFCodProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodProveedor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodProveedor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodProveedorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodProveedorFocusLost(evt);
            }
        });
        jTFCodProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodProveedorKeyPressed(evt);
            }
        });

        jTFRazonSocProveedor.setEditable(false);
        jTFRazonSocProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Nro. Doc.:");

        jTFNroDocumento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroDocumento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroDocumento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroDocumentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFNroDocumentoFocusLost(evt);
            }
        });
        jTFNroDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroDocumentoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFNroDocumentoKeyTyped(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Fecha:");

        jTFFecComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecComprob.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecComprobFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFFecComprobFocusLost(evt);
            }
        });
        jTFFecComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecComprobKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Tipo:");

        jTFTipoComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoComprob.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFTipoComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTipoComprobFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFTipoComprobFocusLost(evt);
            }
        });
        jTFTipoComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTipoComprobKeyPressed(evt);
            }
        });

        jLDescTipoComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescTipoComprob.setText("***");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Motivo:");

        jTFCodMotivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodMotivo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodMotivo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodMotivoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodMotivoFocusLost(evt);
            }
        });
        jTFCodMotivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodMotivoKeyPressed(evt);
            }
        });

        jLDescMotivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescMotivo.setText("***");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Timbrado:");

        jTFNroTimbrado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroTimbrado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroTimbrado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroTimbradoFocusGained(evt);
            }
        });
        jTFNroTimbrado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroTimbradoKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Venc. Timbrado:");

        jTFFecVencTimbrado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecVencTimbrado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecVencTimbrado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecVencTimbradoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFFecVencTimbradoFocusLost(evt);
            }
        });
        jTFFecVencTimbrado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecVencTimbradoKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Centro Costo:");

        jCBCodCentroCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodCentroCostoKeyPressed(evt);
            }
        });

        jLDescCentroCosto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescCentroCosto.setText("***");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Obs.:");

        jTFObs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFObs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFObsFocusGained(evt);
            }
        });
        jTFObs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFObsKeyPressed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Vencimiento:");

        jTFFecVencimiento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecVencimiento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecVencimiento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecVencimientoFocusGained(evt);
            }
        });
        jTFFecVencimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecVencimientoKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Tipo Gasto:");

        jTFCodTipoGasto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodTipoGasto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodTipoGasto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodTipoGastoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodTipoGastoFocusLost(evt);
            }
        });
        jTFCodTipoGasto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodTipoGastoKeyPressed(evt);
            }
        });

        jLDescTipoGasto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescTipoGasto.setText("***");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLRazonSocEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescSector, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel10)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel22))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel6)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFNroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTFFecComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLDescTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLDescMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTFObs, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTFFecVencimiento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                            .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTFFecVencTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCBCodCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLDescCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel23)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTFCodTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLDescTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRazonSocEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescLocal)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescSector))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTFNroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFFecComprob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTFTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescTipoComprob)
                    .addComponent(jLabel9)
                    .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescMotivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTFFecVencTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jCBCodCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescCentroCosto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTFFecVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jTFCodTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescTipoGasto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTFObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Valor del Documento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Moneda:");

        jTFCodMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodMoneda.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodMoneda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodMonedaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodMonedaFocusLost(evt);
            }
        });
        jTFCodMoneda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodMonedaKeyPressed(evt);
            }
        });

        jLDescMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescMoneda.setText("***");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Tipo Cambio:");

        jTFTipoCambio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoCambio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTipoCambio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTipoCambioFocusGained(evt);
            }
        });
        jTFTipoCambio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTipoCambioKeyPressed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Exento:");

        jTFMontoExento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFMontoExento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoExento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoExentoFocusGained(evt);
            }
        });
        jTFMontoExento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoExentoKeyPressed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Grav. 5%:");

        jTFMontoGrav5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFMontoGrav5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoGrav5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoGrav5FocusGained(evt);
            }
        });
        jTFMontoGrav5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoGrav5KeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Grav. 10%:");

        jTFMontoGrav10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFMontoGrav10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoGrav10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoGrav10FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFMontoGrav10FocusLost(evt);
            }
        });
        jTFMontoGrav10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoGrav10KeyPressed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Total:");

        jTFMontoTotal.setEditable(false);
        jTFMontoTotal.setBackground(new java.awt.Color(255, 255, 102));
        jTFMontoTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMontoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFIva5.setEditable(false);
        jTFIva5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFIva5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFIva10.setEditable(false);
        jTFIva10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFIva10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("IVA 5%");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("IVA 10%");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTipoCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFMontoTotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                            .addComponent(jTFMontoGrav10, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFMontoGrav5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFMontoExento, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFIva5)
                            .addComponent(jTFIva10, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescMoneda)
                    .addComponent(jLabel14)
                    .addComponent(jTFTipoCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTFMontoExento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTFMontoGrav5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIva5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTFMontoGrav10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIva10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
            }
        });

        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGrabar.setText("Grabar");
        jBGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jBNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBGrabar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jBCancelar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jBSalir))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBCancelar, jBGrabar, jBNuevo, jBSalir});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(jBGrabar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBCancelar)
                            .addComponent(jBSalir))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBNuevo, jBSalir});

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

        setSize(new java.awt.Dimension(906, 528));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jCBCodSectorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodSectorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodProveedor.requestFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jCBCodLocalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodLocalKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodSector.requestFocus();
        }
    }//GEN-LAST:event_jCBCodLocalKeyPressed

    private void jCBCodEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodEmpresaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodLocal.requestFocus();
        }
    }//GEN-LAST:event_jCBCodEmpresaKeyPressed

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodProveedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusGained
        jTFCodProveedor.selectAll();
    }//GEN-LAST:event_jTFCodProveedorFocusGained

    private void jTFCodProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodProveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodProveedor.getText().trim().equals("")){
                jTFNroDocumento.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta de Proveedores");
                dCons.dConsultas("proveedor", "razon_soc", "cod_proveedor", "razon_soc", "telefono", null, "Código", "Razón Social", "Teléfono", null);
                dCons.setText(jTFCodProveedor);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodProveedorKeyPressed

    private void jTFNroDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocumentoFocusGained
        jTFNroDocumento.selectAll();
    }//GEN-LAST:event_jTFNroDocumentoFocusGained

    private void jTFNroDocumentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocumentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFNroDocumento.getText().trim().equals("")){
                formatoNroDocumento();
                if (jTFNroDocumento.getText().length() == 15) {
                    jTFNroDocumento.select(0,0);
                    jTFFecComprob.requestFocus();
                }else{
                    JOptionPane.showMessageDialog(this, "ATENCION... Formato de Factura Incorrecto, favor verifique!\n"
                                                      + "FORMATO >> (000-000-0000000)");
                    jTFNroDocumento.selectAll();
                    jTFNroDocumento.grabFocus();
                }
            }
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_DECIMAL) {
            formatoNroDocumento();
        }
    }//GEN-LAST:event_jTFNroDocumentoKeyPressed

    private void jTFFecComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecComprobFocusGained
        jTFFecComprob.selectAll();
    }//GEN-LAST:event_jTFFecComprobFocusGained

    private void jTFFecComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFFecComprob.getText().trim().equals("")){
                jTFTipoComprob.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFFecComprobKeyPressed

    private void jTFTipoComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoComprobFocusGained
        jTFTipoComprob.selectAll();
    }//GEN-LAST:event_jTFTipoComprobFocusGained

    private void jTFTipoComprobFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoComprobFocusLost
        if(jTFTipoComprob.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFTipoComprob.requestFocus();
        }else{
            getDescripcionTipoDocumento(jTFTipoComprob.getText().trim());
            getTimbradoDoc();
        }
    }//GEN-LAST:event_jTFTipoComprobFocusLost

    private void jTFTipoComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTipoComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFTipoComprob.getText().trim().equals("")){
                jTFCodMotivo.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultasTipoComprob dCons = new DlgConsultasTipoComprob(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta Tipo Documento");
                dCons.dConsultas("tipo_comprobante", "descripcion", "cod_comprobante", "descripcion", "tip_comprob", "es_legal", "Código", "Descripción", "Tipo", "Legal");
                dCons.setText(jTFTipoComprob);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFTipoComprobKeyPressed

    private void jTFCodMotivoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMotivoFocusGained
        jTFCodMotivo.selectAll();
    }//GEN-LAST:event_jTFCodMotivoFocusGained

    private void jTFCodMotivoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMotivoFocusLost
        if(jTFCodMotivo.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodMotivo.requestFocus();
        }else{
            getDescripcionMotivo(jTFCodMotivo.getText().trim());
        }
    }//GEN-LAST:event_jTFCodMotivoFocusLost

    private void jTFCodMotivoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodMotivoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodMotivo.getText().trim().equals("")){
                jTFNroTimbrado.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta Motivo");
                dCons.dConsultas("motivo", "descripcion", "cod_motivo", "descripcion", "afecta_stock", null, "Código", "Descripción", "Afecta Stock", null);
                dCons.setText(jTFCodMotivo);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodMotivoKeyPressed

    private void jTFNroTimbradoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroTimbradoFocusGained
        jTFNroTimbrado.selectAll();
    }//GEN-LAST:event_jTFNroTimbradoFocusGained

    private void jTFNroTimbradoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroTimbradoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(controlarNroDoc()){
                JOptionPane.showMessageDialog(this, "Documento ya está registrado para el proveedor!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jTFNroDocumento.requestFocus();
            }else{
                jTFFecVencTimbrado.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFNroTimbradoKeyPressed

    private void jTFFecVencTimbradoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencTimbradoFocusGained
        jTFFecVencTimbrado.selectAll();
    }//GEN-LAST:event_jTFFecVencTimbradoFocusGained

    private void jTFFecVencTimbradoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencTimbradoFocusLost
        if(vencimientoTimbrado()){
            jCBCodCentroCosto.requestFocus();
        }else{
            JOptionPane.showMessageDialog(this, "Vencimiento del timbrado no puede ser menor a la fecha actual", "ATENCION", JOptionPane.WARNING_MESSAGE);
            jTFFecVencTimbrado.requestFocus();
        }
    }//GEN-LAST:event_jTFFecVencTimbradoFocusLost

    private void jCBCodCentroCostoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodCentroCostoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecVencimiento.requestFocus();
        }
    }//GEN-LAST:event_jCBCodCentroCostoKeyPressed

    private void jTFObsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFObsFocusGained
        jTFObs.selectAll();
    }//GEN-LAST:event_jTFObsFocusGained

    private void jTFObsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFObsKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodMoneda.requestFocus();
        }
    }//GEN-LAST:event_jTFObsKeyPressed

    private void jTFCodMonedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMonedaFocusGained
        jTFCodMoneda.selectAll();
    }//GEN-LAST:event_jTFCodMonedaFocusGained

    private void jTFCodMonedaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMonedaFocusLost
        if(jTFCodMoneda.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodMoneda.requestFocus();
        }else{
            getNombreMoneda(jTFCodMoneda.getText().trim());
        }
    }//GEN-LAST:event_jTFCodMonedaFocusLost

    private void jTFCodMonedaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodMonedaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodMoneda.getText().trim().equals("")){
                jTFTipoCambio.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta Moneda");
                dCons.dConsultas("moneda", "nombre", "cod_moneda", "nombre", "cotiz_compra", "cotiz_venta", "Código", "Nombre", "Compra", "Venta");
                dCons.setText(jTFCodMoneda);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodMonedaKeyPressed

    private void jTFTipoCambioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoCambioFocusGained
        jTFTipoCambio.selectAll();
    }//GEN-LAST:event_jTFTipoCambioFocusGained

    private void jTFTipoCambioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTipoCambioKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFTipoCambio.getText().trim().equals("")){
                jTFMontoExento.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFTipoCambioKeyPressed

    private void jTFMontoExentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoExentoFocusGained
        quitarPatternMontos();
        jTFMontoExento.selectAll();
    }//GEN-LAST:event_jTFMontoExentoFocusGained

    private void jTFMontoExentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoExentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFMontoExento.getText().trim().equals("")){
                jTFMontoGrav5.requestFocus();
            }
        } 
    }//GEN-LAST:event_jTFMontoExentoKeyPressed

    private void jTFMontoGrav5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav5FocusGained
        quitarPatternMontos();
        jTFMontoGrav5.selectAll();
    }//GEN-LAST:event_jTFMontoGrav5FocusGained

    private void jTFMontoGrav5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoGrav5KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFMontoGrav5.getText().trim().equals("")){
                jTFMontoGrav10.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFMontoGrav5KeyPressed

    private void jTFMontoGrav10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav10FocusGained
        quitarPatternMontos();
        jTFMontoGrav10.selectAll();
    }//GEN-LAST:event_jTFMontoGrav10FocusGained

    private void jTFMontoGrav10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav10FocusLost
        double iva5, iva10;
        double sum = totalExento + totalGrav5 + totalGrav10;
        jTFMontoTotal.setText(decimalFormat.format(sum));
        // calcular iva 
        iva5 = totalGrav5 / 21;
        iva10 = totalGrav10 / 11;
        System.out.println("Total Grav5: " + totalGrav5 + "\nTotal Grav10: " + totalGrav10 + "\nIVA 5: " + iva5 + "\nIVA 10: " + iva10);
        jTFIva5.setText(decimalFormatIva.format(iva5));
        jTFIva10.setText(decimalFormatIva.format(iva10));
    }//GEN-LAST:event_jTFMontoGrav10FocusLost

    private void jTFMontoGrav10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoGrav10KeyPressed
        String vTotalExento, vTotalGrav5, vTotalGrav10;
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFMontoGrav10.getText().trim().equals("0.00") && !jTFMontoGrav10.getText().trim().equals(".00")){
                vTotalGrav10 = jTFMontoGrav10.getText().trim();
                totalGrav10 = Integer.parseInt(vTotalGrav10);
                jTFMontoGrav10.setText(decimalFormat.format(totalGrav10));
            }
            
            
            if(!jTFMontoGrav5.getText().trim().equals("0.00") && !jTFMontoGrav5.getText().trim().equals(".00")){
                vTotalGrav5 = jTFMontoGrav5.getText().trim();
                totalGrav5 = Integer.parseInt(vTotalGrav5);
                jTFMontoGrav5.setText(decimalFormat.format(totalGrav5));
            }
            
            
            if(!jTFMontoExento.getText().trim().equals("0.00") && !jTFMontoExento.getText().trim().equals(".00")){
                vTotalExento = jTFMontoExento.getText().trim();
                totalExento = Integer.parseInt(vTotalExento);
                jTFMontoExento.setText(decimalFormat.format(totalExento));
            }
            
            jBGrabar.requestFocus();
        }
    }//GEN-LAST:event_jTFMontoGrav10KeyPressed

    private void jTFCodProveedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusLost
        if(jTFCodProveedor.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodProveedor.requestFocus();
        }else{
            completarDatosProveedor(jTFCodProveedor.getText().trim());    
        }
    }//GEN-LAST:event_jTFCodProveedorFocusLost

    private void jTFNroDocumentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocumentoKeyTyped
        jTFNroDocumento.getText().replace(".", "-");
    }//GEN-LAST:event_jTFNroDocumentoKeyTyped

    private void jTFFecVencTimbradoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecVencTimbradoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFFecVencTimbrado.getText().trim().equals("")){                
                jCBCodCentroCosto.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFFecVencTimbradoKeyPressed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        setEstadoComponentes(true);
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBCancelar.setEnabled(true);
        jCBCodEmpresa.requestFocus();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(camposOk()){
            if(grabarDatos()){
                JOptionPane.showMessageDialog(this,"ATENCION: Datos grabados correctamente!",     "EXITO",  JOptionPane.INFORMATION_MESSAGE);
                setComponentesGrabado();
            }else{
                JOptionPane.showMessageDialog(this,"ATENCION: Error grabando Documento!",     "ATENCION",  JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jTFNroDocumentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocumentoFocusLost
        if(jTFNroDocumento.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFNroDocumento.requestFocus();
        }
    }//GEN-LAST:event_jTFNroDocumentoFocusLost

    private void jTFFecVencimientoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencimientoFocusGained
        jTFFecVencimiento.selectAll();
    }//GEN-LAST:event_jTFFecVencimientoFocusGained

    private void jTFFecVencimientoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecVencimientoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodTipoGasto.requestFocus();
        }
    }//GEN-LAST:event_jTFFecVencimientoKeyPressed

    private void jTFFecComprobFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecComprobFocusLost
        jTFFecVencimiento.setText(calculaVencimiento(jTFFecComprob.getText().trim()));
    }//GEN-LAST:event_jTFFecComprobFocusLost

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoComponentes(false);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBNuevo.requestFocus();
        jBSalir.setEnabled(true);
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodTipoGastoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodTipoGastoFocusGained
        jTFCodTipoGasto.selectAll();
    }//GEN-LAST:event_jTFCodTipoGastoFocusGained

    private void jTFCodTipoGastoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodTipoGastoFocusLost
        if(jTFCodTipoGasto.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodTipoGasto.requestFocus();
        }else{
            getDescTipoGasto(jTFCodTipoGasto.getText().trim());
        }
    }//GEN-LAST:event_jTFCodTipoGastoFocusLost

    private void jTFCodTipoGastoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodTipoGastoKeyPressed

        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodMotivo.getText().trim().equals("")){
                jTFObs.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta Tipo Gasto");
                dCons.dConsultas("tipo_mercaderia", "descripcion", "cod_tipomerc", "descripcion", "mercad_gastos", "tipo_gasto", "Código", "Descripción", "Merc/Gasto", "Tipo");
                dCons.setText(jTFCodTipoGasto);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodTipoGastoKeyPressed

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
            java.util.logging.Logger.getLogger(RegistroComprobanteGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroComprobanteGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroComprobanteGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroComprobanteGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroComprobanteGastos dialog = new RegistroComprobanteGastos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBGrabar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodCentroCosto;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLDescCentroCosto;
    private javax.swing.JLabel jLDescLocal;
    private javax.swing.JLabel jLDescMoneda;
    private javax.swing.JLabel jLDescMotivo;
    private javax.swing.JLabel jLDescSector;
    private javax.swing.JLabel jLDescTipoComprob;
    private javax.swing.JLabel jLDescTipoGasto;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFCodMoneda;
    private javax.swing.JTextField jTFCodMotivo;
    private javax.swing.JTextField jTFCodProveedor;
    private javax.swing.JTextField jTFCodTipoGasto;
    private javax.swing.JTextField jTFFecComprob;
    private javax.swing.JTextField jTFFecVencTimbrado;
    private javax.swing.JTextField jTFFecVencimiento;
    private javax.swing.JTextField jTFIva10;
    private javax.swing.JTextField jTFIva5;
    private javax.swing.JTextField jTFMontoExento;
    private javax.swing.JTextField jTFMontoGrav10;
    private javax.swing.JTextField jTFMontoGrav5;
    private javax.swing.JTextField jTFMontoTotal;
    private javax.swing.JTextField jTFNroDocumento;
    private javax.swing.JTextField jTFNroTimbrado;
    private javax.swing.JTextField jTFObs;
    private javax.swing.JTextField jTFRazonSocProveedor;
    private javax.swing.JTextField jTFTipoCambio;
    private javax.swing.JTextField jTFTipoComprob;
    // End of variables declaration//GEN-END:variables
}
