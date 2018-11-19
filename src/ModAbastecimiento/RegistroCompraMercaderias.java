/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento;

import ModRegistros.Articulos;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.StatementManager;
import static utiles.Utiles.padLefth;
import views.busca.BuscaArticuloFiltro;
import views.busca.DlgConsultas;
import views.busca.DlgConsultasTipoComprob;

/**
 *
 * @author Andres
 */
public class RegistroCompraMercaderias extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector, codCentroCosto;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    DefaultTableModel dtmDetallesCompra;
    String codigoBarrasDigitado;
    boolean esModificacion;
    int filaExistente;
    
    // costo calculado
    double totalItem, costo;
    float cantidad;
    
    // montos cabecera con pattern
    int totalExento, totalGrav5, totalGrav10, totalMontoCab;
    
    public RegistroCompraMercaderias(java.awt.Frame parent, boolean modal) {
        super(parent, modal);       
        initComponents();
        jBTotalizar.setVisible(false);
        inicio();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        codLocal = jCBCodLocal.getSelectedItem().toString();
        codSector = jCBCodSector.getSelectedItem().toString();
        codCentroCosto = jCBCodCentroCosto.getSelectedItem().toString();
        configCampos();
        llenarCampos();
        setEstadoComponentes(false);
        jBGrabar.setEnabled(false);
        jBCancelar.setEnabled(false);
        jBConfirmarDetalle.setEnabled(false);
        cerrarVentana();
        configTabla();
        limpiarTabla();
    }

    private void inicio(){
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = getSize();
        if (dialogSize.height > screenSize.height) {
            dialogSize.height = screenSize.height;
        }
        if (dialogSize.width > screenSize.width) {
            dialogSize.width = screenSize.width;
        }
        setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
    }
    
    private void configTabla(){
        dtmDetallesCompra = (DefaultTableModel) jTDetallesCompra.getModel();
        jTDetallesCompra.getColumnModel().getColumn(0).setPreferredWidth(80); // barras
        jTDetallesCompra.getColumnModel().getColumn(1).setPreferredWidth(20); // codigo
        jTDetallesCompra.getColumnModel().getColumn(2).setPreferredWidth(200); // descripcion
        jTDetallesCompra.getColumnModel().getColumn(3).setPreferredWidth(15); // sigla
        jTDetallesCompra.getColumnModel().getColumn(4).setPreferredWidth(15); // um
        jTDetallesCompra.getColumnModel().getColumn(5).setPreferredWidth(30); // cantidad
        jTDetallesCompra.getColumnModel().getColumn(6).setPreferredWidth(35); // costo
        jTDetallesCompra.getColumnModel().getColumn(7).setPreferredWidth(15); // iva
        jTDetallesCompra.getColumnModel().getColumn(8).setPreferredWidth(35); // totalitem
        jTDetallesCompra.setRowHeight(20);
    }
    
    
    private void setEstadoComponentes(boolean estado){
        jCBCodEmpresa.setEnabled(estado);
        jCBCodLocal.setEnabled(estado);
        jCBCodSector.setEnabled(estado);
        jCBCodCentroCosto.setEnabled(estado);
        jTFCodProveedor.setEnabled(estado);
        jTFNroDocumento.setEnabled(estado);
        jTFCodTipoDocumento.setEnabled(estado);
        jTFCodMotivo.setEnabled(estado);
        jTFFecDocumento.setEnabled(estado);
        jTFCodMoneda.setEnabled(estado);
        jTFTipoCambio.setEnabled(estado);
        jTFMontoExento.setEnabled(estado);
        jTFMontoGrav5.setEnabled(estado);
        jTFMontoGrav10.setEnabled(estado);
        jTFMontoTotal.setEnabled(estado);
        jTFCodArticulo.setEnabled(estado);
        jTFCantRecibida.setEnabled(estado);
        jTFCosto.setEnabled(estado);
        jTFTotalItem.setEnabled(estado);
        jRBDescPorc.setEnabled(estado);
        jRBDescMonto.setEnabled(estado);
        jTFDescuento.setEnabled(estado);
        jChBAplicaDescAProductos.setEnabled(estado);
        jTFNroTimbrado.setEnabled(estado);
        jTFFecVencTimbrado.setEnabled(estado);
    }
    
    private void configCampos(){
        jTFCodProveedor.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFNroDocumento.setDocument(new MaxLength(15, "UPPER", "ALFA"));
        jTFCodTipoDocumento.setDocument(new MaxLength(6, "UPPER", "ALFA"));
        jTFCodMotivo.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFCodMotivo.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFFecDocumento.setInputVerifier(new FechaInputVerifier(jTFFecDocumento));
        jTFFecVencTimbrado.setInputVerifier(new FechaInputVerifier(jTFFecVencTimbrado));
        
        jTFCodProveedor.addFocusListener(new Focus());
        jTFNroDocumento.addFocusListener(new Focus());
        jTFCodTipoDocumento.addFocusListener(new Focus());
        jTFCodMotivo.addFocusListener(new Focus());
        jTFFecDocumento.addFocusListener(new Focus());
        jTFCodMoneda.addFocusListener(new Focus());
        jTFTipoCambio.addFocusListener(new Focus());
        jTFMontoExento.addFocusListener(new Focus());
        jTFMontoGrav5.addFocusListener(new Focus());
        jTFMontoGrav10.addFocusListener(new Focus());
        jTFMontoTotal.addFocusListener(new Focus());
        
        jTFCodArticulo.addFocusListener(new Focus());
        jTFCantRecibida.addFocusListener(new Focus());
        jTFCosto.addFocusListener(new Focus());
        jTFTotalItem.addFocusListener(new Focus());
        jTFDescuento.addFocusListener(new Focus());
        jTFNroTimbrado.addFocusListener(new Focus());
        jTFFecVencTimbrado.addFocusListener(new Focus());
    }
    
    private void llenarCampos(){
        jTFCodMoneda.setText("1");
        jLDescMoneda.setText("GUARANI");
        jTFTipoCambio.setText("1");
        jTFCodProveedor.setText("1");
        jTFCodMotivo.setText("1");
        jTFCodTipoDocumento.setText("FPC");
        jTFFecDocumento.setText(fecVigencia);
        jTFFecVencTimbrado.setText(fecVigencia);
        jTFNroTimbrado.setText("0");
        jTFMontoExento.setText("0.00");
        jTFMontoGrav5.setText("0.00");
        jTFMontoGrav10.setText("0.00");
        jTFMontoTotal.setText("0.00");      
        jTFDescuento.setText("0.00");
        jTFMontoDescuento.setText("0.00");
        jTFMontoTotalDetalle.setText("0.00");
        jLRazonSocEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLDescLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jLDescSector.setText(utiles.Utiles.getSectorDescripcion(codLocal, codSector));
        jLDescCentroCosto.setText(utiles.Utiles.getCentroCostoDescripcion(codLocal, codSector));
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
        utiles.Utiles.cargaComboCentroCosto(this.jCBCodCentroCosto);
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
    
    private boolean controlarNroDoc(){
        boolean result = false;
        long nroComprob = Long.valueOf(eliminaCaracteres(jTFNroDocumento.getText(), "-"));
        System.out.println("NRO DE COMPROBANTE CONTROL: " + nroComprob);
        String sql = "SELECT nro_comprob FROM compra_cab WHERE nro_comprob = " + nroComprob + " AND nro_timbrado = '" + jTFNroTimbrado.getText().trim() + "' AND"
                   + " cod_proveedor = " + jTFCodProveedor.getText().trim() + " LIMIT 1";
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
    
    private void getTimbradoDoc(){
        try{
                String sql = "SELECT nro_timbrado, to_char(ven_timbrado, 'dd/MM/yyyy') AS vencimiento FROM compra_cab WHERE "
                           + "cod_proveedor = " + jTFCodProveedor.getText().trim() + " AND tip_comprob  = '" + jTFCodTipoDocumento.getText() + "' "
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
    
    private void getDescripcionTipoDocumento(String codigo){
        if(jTFCodTipoDocumento.getText().trim().equals("0")){
            jLDescTipoDoc.setText("DOCUMENTO INEXISTENTE");
        }else{
            jLDescTipoDoc.setText("");
            try{
                String sql = "SELECT descripcion FROM tipo_comprobante WHERE tip_comprob = '" + codigo + "'";
                System.out.println("SELECT TIPO COMPROBANTE: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescTipoDoc.setText(rs.getString("descripcion"));
                    }else{
                        jLDescTipoDoc.setText("DOCUMENTO INEXISTENTE");
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
    
    private void cargaDatosArticulo(String codigo){
        try{
            String sql = "SELECT articulo.descripcion, articulo.hab_compra, articulo.pct_iva, costoart.sigla_compra, costoart.cansi_compra, costoart.costo_neto " +
                         "FROM articulo " +
                         "INNER JOIN costoart " +
                         "ON articulo.cod_articulo = costoart.cod_articulo "+
                         "WHERE costoart.vigente = 'S' AND articulo.cod_articulo = " + codigo + "";
            ResultSet rs1;
            rs1 = DBManager.ejecutarDSL(sql);
            if(rs1 != null){
                if(rs1.next()){
                    if(rs1.getString("hab_compra").equals("N")){
                        jTFDescArticulo.setText("ARTICULO NO HABILITADO PARA COMPRA");
                        jTFDescArticulo.setForeground(Color.red);
                        jTFCodArticulo.requestFocus();
                    }else{
                        jTFDescArticulo.setText("");
                        jTFDescArticulo.setForeground(Color.black);
                        jTFDescArticulo.setText(rs1.getString("descripcion"));
                        jTFSiglaCompra.setText(rs1.getString("sigla_compra"));
                        jTFCansiCompra.setText(String.valueOf(rs1.getInt("cansi_compra")));
                        jTFIva.setText(String.valueOf(rs1.getInt("pct_iva")));
                        jTFCosto.setText(String.valueOf(rs1.getDouble("costo_neto")));
                    }
                    
                }else{                    
                    jTFDescArticulo.setText("ARTICULO NO ENCONTRADO");
                    jTFDescArticulo.setForeground(Color.red);
                    jTFCodArticulo.requestFocus();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane.showMessageDialog(RegistroCompraMercaderias.this, "Clic en el botón SALIR!", "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private void salirDelModulo()
    {
        int exit = JOptionPane.showConfirmDialog(this, "Desea salir del módulo?", "Salir del Módulo", JOptionPane.YES_NO_OPTION);
        if(exit == 0){
            this.dispose();
        }
    }
    
    private void limpiarTabla(){
        int nroFilas = dtmDetallesCompra.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesCompra.removeRow(i);
            nroFilas = dtmDetallesCompra.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesCompra.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetallesCompra.setModel(dtmDetallesCompra);
    }
    
    private String getCodArticulo(String codigo){
        String result = "0";
        try{
            String sql = "SELECT articulo.cod_articulo FROM articulo LEFT JOIN barras ON articulo.cod_articulo = barras.cod_articulo "
                       + "WHERE barras.cod_barras = '" + codigo + "' OR articulo.cod_articulo = " + codigo;
            System.out.println(">> SQL COD ART FOCUS LOST: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = String.valueOf(rs.getInt("cod_articulo"));
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
    
    private void addArticuloDetalle(){
        String tBarras = jLBarras.getText();
        long tCodArticulo = Long.parseLong(jTFCodArticulo.getText().trim());
        String tDescArt = jTFDescArticulo.getText().trim();
        String tSigla = jTFSiglaCompra.getText().trim();
        int tUm = Integer.parseInt(jTFCansiCompra.getText().trim());
        float tCantidad = Float.parseFloat(jTFCantRecibida.getText().trim());
        int tIva = Integer.parseInt(jTFIva.getText().trim());
        double tTotalItem = Double.parseDouble(jTFTotalItem.getText().trim());
        double tCosto = Double.parseDouble(jTFCosto.getText().trim());
        
        if(!articuloExisteDetalle(String.valueOf(tCodArticulo))){
            dtmDetallesCompra.addRow(new Object[]{tBarras, tCodArticulo, tDescArt, tSigla, tUm, tCantidad, tCosto, tIva, tTotalItem});
            jBTotalizar.doClick();
        }else{
            dtmDetallesCompra.removeRow(filaExistente);
            dtmDetallesCompra.addRow(new Object[]{tBarras, tCodArticulo, tDescArt, tSigla, tUm, tCantidad, tCosto, tIva, tTotalItem});
            jBTotalizar.doClick();
        }
        jTDetallesCompra.setModel(dtmDetallesCompra);
    }
    
    private boolean articuloExisteDetalle(String codigo){
       esModificacion = false;
       boolean result = false;
       int cantFilas = dtmDetallesCompra.getRowCount();
       for(int i = 0; i < cantFilas; i++){
           Long codArticuloTabla = ((Long)dtmDetallesCompra.getValueAt(i, 1));
           String codArtTablaStr = String.valueOf(codArticuloTabla);
           if(codArtTablaStr.equalsIgnoreCase(codigo)){
               esModificacion = true;
               result = true;
               filaExistente = i;
           }
       }
       return result;
    }
    
    private String getBarrasArticulo(String codigo){
        String barras = "0";
        String sql = "SELECT cod_barras FROM barras WHERE cod_barras = '" + codigoBarrasDigitado + "' OR cod_articulo = " + codigo + " AND vigente = 'S'";
        System.out.println("SQL BARRAS ART: " + sql);
        ResultSet rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    barras = rs.getString("cod_barras");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return barras;
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
    
    private void calcularDescuento(){
        double monto = 0;
        if(jRBDescPorc.isSelected()){
            if(!jTFDescuento.getText().trim().equals("0") && !jTFDescuento.getText().trim().equals(".00")){
                float descuento = Float.parseFloat(jTFDescuento.getText().trim());
                if(descuento < 0){
                }else{
                    double totalDetalle = Double.parseDouble(jTFMontoTotalDetalle.getText().trim().replace(",", ""));
                    monto = (descuento / 100) * totalDetalle;
                    jTFMontoDescuento.setText(decimalFormat.format(monto));
                }
            }else{
                jTFMontoDescuento.setText("0");
            }
        }
        
        if(jRBDescMonto.isSelected()){
            if(!jTFDescuento.getText().trim().equals("0") && !jTFDescuento.getText().trim().equals(".00")){
                monto = Double.parseDouble(jTFDescuento.getText().trim());
                if(monto < 0){
                }else{
                    jTFMontoDescuento.setText(decimalFormat.format(monto));
                }
            }else{
                jTFMontoDescuento.setText("0");
            }
        }
        
        // totalizar 
        if(!jTFMontoDescuento.getText().trim().equals("000.00") && !jTFMontoDescuento.getText().trim().equals(".00") && 
           !jTFMontoDescuento.getText().trim().equals("0")){
            String totalDetalleStr = jTFMontoTotalDetalle.getText().trim().replace(",", "");
            double totalDetalle = Double.parseDouble(totalDetalleStr);
            if(monto >= totalDetalle){
                JOptionPane.showMessageDialog(this, "Verificar descuento!\nIgual o mayor a total detalle.", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jTFDescuento.requestFocus();
            }else{
                double calculo = totalDetalle - monto;
                jTFMontoTotalDetalle.setText(decimalFormat.format(calculo));
            }
        }
    }
    
    private boolean camposCabeceraOk(){
        boolean result = true;
        if(jTFNroDocumento.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de documento!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFNroDocumento.requestFocus();
            result = false;
        }
        
        if(jTFCodTipoDocumento.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Seleccione tipo de documento!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodTipoDocumento.requestFocus();
            result = false;
        }
        
        if(!tipoDocCorresponde(jTFCodTipoDocumento.getText().trim())){
            JOptionPane.showMessageDialog(this, "Tipo de documento no corresponde!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodTipoDocumento.requestFocus();
            result = false;
        }
        return result;
    }
    
    private boolean tipoDocCorresponde(String tipo){
        boolean result = true;
        String sql = "SELECT tipo_operacion FROM tipo_comprobante WHERE tip_comprob = '" + tipo + "' AND tipo_operacion = 'COMPRA' "
                   + "AND tip_comprob NOT IN ('NCI', 'NCP')";
        ResultSet rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    result = true;
                }else{
                    result = false;
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
    
    private boolean compararMontos(){
        double montoTotalCab, montoTotalDet, diferencia;
        String montoTotalCabStr, montoTotalDetStr;
        boolean result = true;
        if(jTFMontoTotal.getText().trim().equals("0.00") && jTFMontoTotal.getText().trim().equals(".00")){
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto del documento!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFMontoExento.requestFocus();
            result = false;
        }else{
            montoTotalCabStr = jTFMontoTotal.getText().trim().replace(",", "");
            montoTotalDetStr = jTFMontoTotalDetalle.getText().trim().replace(",", "");
            montoTotalCab = Double.parseDouble(montoTotalCabStr);
            montoTotalDet = Double.parseDouble(montoTotalDetStr);
            diferencia = montoTotalCab - montoTotalDet;
            
            if((montoTotalCab > montoTotalDet) || (montoTotalCab < montoTotalDet )){
                JOptionPane.showMessageDialog(this, "Diferencia totales: \n"
                                                  + "Total documento : " + decimalFormat.format(montoTotalCab) + "\n"
                                                  + "Monto total detalle: " + decimalFormat.format(montoTotalDet) + "\n"
                                                  + "DIFERENCIA: " + decimalFormat.format(diferencia) + "", 
                        "DIFERENCIA", JOptionPane.WARNING_MESSAGE);
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }
    
    private boolean grabarDatos(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoTabla = leerTablaDetalles();
        if(!problemLeyendoTabla){
            estadoGrabado = true;
        }else{
            estadoGrabado = false;
        }
        
        return estadoGrabado;
    }
    
    private boolean leerTablaDetalles(){
        int iterador;
        int cantidadFilasTabla = dtmDetallesCompra.getRowCount();
        
        boolean existeStockArticulo = false;
        boolean resultStockArticulo = false;
        boolean problemFound = false;
        boolean resultHistMovimiento = false;
        boolean resultCostoArt = false;
        boolean resultComprasCab = false;
        boolean resultComprasDet = false;
        boolean resultComprasDetCuotas = false;
        boolean resultOperacionGrabaCompras = true;
        
        // datos a grabar 
        int vCodEmpresa = Integer.parseInt(jCBCodEmpresa.getSelectedItem().toString());
        int vCodLocal = Integer.parseInt(jCBCodLocal.getSelectedItem().toString());
        int vCodSector = Integer.parseInt(jCBCodSector.getSelectedItem().toString());
        int vCodCentroCosto = Integer.parseInt(jCBCodCentroCosto.getSelectedItem().toString());
        int vCodUsuario = FormMain.codUsuario;
        String vFecComprob = "to_timestamp('" + jTFFecDocumento.getText() + "', 'dd/MM/yyyy')";
        String vTipoDoc = jTFCodTipoDocumento.getText().trim();
        int vCodMotivo = Integer.parseInt(jTFCodMotivo.getText().trim());
        boolean tieneDescuento = false;
        long vNroComprob = Long.valueOf(eliminaCaracteres(jTFNroDocumento.getText(), "-"));
        int vCodProveedor = Integer.parseInt(jTFCodProveedor.getText().trim());
        String vFecVencimiento = calculaVencimiento(jTFFecDocumento.getText().trim());
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
        int vCodComprobFiscal = getCodComprobFiscal(vTipoDoc);
        String vNroTimbrado = jTFNroTimbrado.getText().trim();
        String vFecVencTimbrado = "to_timestamp('" + jTFFecVencTimbrado.getText().trim() + "', 'dd/MM/yyyy')";
        
        double vMontoIva5 = vTotalGrav5 / 21;
        double vMontoIva10 = vTotalGrav10 / 11;
        
        float vPctDescuento = 0f;
        
        int vCodMoneda = Integer.parseInt(jTFCodMoneda.getText().trim());
        double vTipoCambio = Double.parseDouble(jTFTipoCambio.getText().trim());
        
        
        if(jChBAplicaDescAProductos.isSelected()){
            tieneDescuento = true;
        }
        
        if(jRBDescPorc.isSelected()){
            if(!jTFDescuento.getText().trim().equals("") && !jTFDescuento.getText().trim().equals("0")){
                vPctDescuento = Float.parseFloat(jTFDescuento.getText().trim());
            }
        }
        
        if(jRBDescMonto.isSelected()){
            if(!jTFDescuento.getText().trim().equals("") && !jTFDescuento.getText().trim().equals("0")){
                float montoDescuento = Float.parseFloat(jTFDescuento.getText().trim());
                float montoTotalDetalle = Float.parseFloat(jTFMontoTotalDetalle.getText().trim().replace(",", ""));
                float descuento = (montoDescuento * 100) / (montoTotalDetalle + montoDescuento);
                String monto = decimalFormat.format(descuento);
                vPctDescuento = Float.parseFloat(monto);
                System.out.println("JRBMONTO SELECTED % DESCUENTO: " + vPctDescuento);
            }
        }
        
        // Compras Cabecera
        
        resultOperacionGrabaCompras = grabarCompraCab(vCodEmpresa, vCodLocal, vTipoDoc, vNroComprob, vFecComprob, vCodProveedor, vFecVencimiento, 
                                                      vRucProveedor, vTotalExento, vTotalGrav10, vTotalGrav5, vTotalMonto, vCodUsuario, vCodSector, 
                                                      vCodMotivo, vCodCentroCosto, vCodComprobFiscal, vMontoIva5, vMontoIva10, vNroTimbrado, vFecVencTimbrado);
        
        if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar compra cab!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultComprasCab = true;
                resultComprasDetCuotas = true;
                resultStockArticulo = true;
        }
        
        
        // Compras Cuotas
        
        resultOperacionGrabaCompras = grabarComprasDetCuotas(vCodEmpresa, vCodLocal, vTipoDoc, vNroComprob, vFecComprob, vFecVencimiento, vTotalMonto, 
                                                             vCodMoneda, vTipoCambio, vCodProveedor, vCodUsuario, vCodSector);
        
        if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar compra cuotas!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultComprasDetCuotas = true;
                resultStockArticulo = true;
        }
        
        
        for(iterador = 0; iterador < cantidadFilasTabla; iterador++){
            
            long vCodArticulo = ((Long)dtmDetallesCompra.getValueAt(iterador, 1));
            String vSiglaCompra = dtmDetallesCompra.getValueAt(iterador, 3).toString();
            int vCansiCompra = ((Integer)dtmDetallesCompra.getValueAt(iterador, 4));
            double vCostoEmpaqueBruto = ((Double)dtmDetallesCompra.getValueAt(iterador, 6));
            float vCantRecibida = ((Float)dtmDetallesCompra.getValueAt(iterador, 5)) * vCansiCompra ;
            double vCostoUnitario = (vCostoEmpaqueBruto / vCansiCompra);
            int vPctIva = ((Integer)dtmDetallesCompra.getValueAt(iterador, 7));
            double vMontoTotal = ((Double)dtmDetallesCompra.getValueAt(iterador, 8));
            double vCostoEmpaqueNeto = ((Double)dtmDetallesCompra.getValueAt(iterador, 6));
            
            if(jChBAplicaDescAProductos.isSelected()){
                double montoDescuento = ((vPctDescuento / 100) * ((Double)dtmDetallesCompra.getValueAt(iterador, 6)));
                vCostoEmpaqueNeto = vCostoEmpaqueBruto - montoDescuento;
                vCostoUnitario = (vCostoEmpaqueNeto / vCansiCompra);
            }else{
                vPctDescuento = 0;
            }
            

            // Compras Detalle
            resultOperacionGrabaCompras = grabarCompraDet(vCodEmpresa, vCodLocal, vTipoDoc, vNroComprob, vFecComprob, vCodArticulo, vSiglaCompra, 
                                                          vCansiCompra, vCostoEmpaqueBruto, vCostoUnitario, vCantRecibida, vPctDescuento, vPctIva, vMontoTotal, 
                                                          vCodMotivo, vCodSector, vCodProveedor, vCodCentroCosto);
            
            if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar detalle de compra!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultStockArticulo = true;
                resultComprasDet = true;
            }
            
            // Stock Articulo
            
            existeStockArticulo = verExistenciaStock(vCodArticulo, vCodEmpresa, vCodLocal, vCodSector);
            if(existeStockArticulo){
                resultOperacionGrabaCompras = actualizarStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vCansiCompra, vCantRecibida);
            }else{
                resultOperacionGrabaCompras = agregarNuevoStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vCansiCompra, vCantRecibida, 
                                                                vCodUsuario, vCostoUnitario);
            }
            
            if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar stock!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultStockArticulo = true;
                break;
            }
            
            // Historico
            
            if(!resultStockArticulo){
                resultOperacionGrabaCompras = agregarArticulosHistorico(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vSiglaCompra, 
                                                                        vCansiCompra, vCantRecibida, vTipoDoc, vNroComprob);
            }
            
            if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al agregar artículo en histórico de movimientos!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultHistMovimiento = true;
                break;
            }
            
            // Costo
            
            if(!resultHistMovimiento){
                resultOperacionGrabaCompras = grabaCostoArticulo(vCostoEmpaqueBruto, vCostoEmpaqueNeto, vCostoUnitario, vPctDescuento, vCodArticulo, 
                                                                 vCansiCompra, vSiglaCompra, vCodUsuario);
            }
            
            if(!resultOperacionGrabaCompras){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar nuevos costos!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultCostoArt = true;
                break;
            }
            
            if(resultStockArticulo || resultHistMovimiento || resultCostoArt || resultComprasCab || resultComprasDet){
                problemFound = true;
                if(!rollBacktDatos()){
                    for(iterador = 0; iterador < 10; iterador++){
                        JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el RollBack!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    }
                    problemFound = true;
                }
            }else{
                if(!commitDatos()){
                    for(iterador = 0; iterador < 10; iterador++){
                        JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el Commit!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    }
                    problemFound = true;
                }
            }
        }
        
        return problemFound;
    }
    
    private String eliminaCaracteres(String cadena, String caracter) {

        cadena = cadena.replace(caracter, "");

        return cadena;
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
    
    private boolean grabaCostoArticulo(double cosCostoBruto, double cosCostoNeto, double cosCostoUnitario, double cosPctDesc, long cosCodArticulo, 
            int cosCansiCompra, String cosSiglaCompra, int cosCodUsuario){
        boolean resultOk = false;
        try{
            if(!setNuevoCosto(cosCostoBruto, cosCostoNeto, cosCostoUnitario, cosPctDesc, "COMPRAS", cosCodArticulo, cosCansiCompra, cosSiglaCompra, 
                              cosCodUsuario)){
                resultOk = false;
                JOptionPane.showMessageDialog(this, "ATENCION: Error al Grabar Nuevo Costo!", "Error", JOptionPane.WARNING_MESSAGE);
            }else{
                resultOk = true;
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            resultOk = false;
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "Error al Grabar Precio de Costo!", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
        return resultOk;
    }
    
    private boolean setNuevoCosto(double costoBruto, double costoNeto, double costoUnitario, double descuento, String modulo, long codArticulo, 
                                  int nCosCansiCompra, String nCosSiglaCompra, int nCosCodUsuario){
        boolean resultOk = false;
        try{
            String sql = "";
            System.out.println("CODIGO DEL ARTICULO: " + codArticulo);
            
            setPrecioCostoAnteriorInactivo(1, codArticulo);
            sql = "INSERT INTO costoart (cod_lista, cod_articulo, cansi_compra, fec_vigencia, sigla_compra, costo_bruto, descuento_pct, "
                                       + "flete_valor, costo_neto, costo_promedio, vigente, cod_usuario, nom_modulo) "
                 + "VALUES (" + 1 + ", "
                 + "" + codArticulo + ", " 
                 + nCosCansiCompra + ", 'now()', '" 
                 + nCosSiglaCompra + "', "
                 + "ROUND(" + costoBruto + ", 2), "
                 + descuento + ", "
                 + "0, "
                 + "ROUND(" + costoNeto + ", 2), "
                 + "ROUND(" + costoUnitario + ", 2), "
                 + "'S', "
                 + nCosCodUsuario + ", '"
                 + modulo + "')";
            System.out.println("SQL INSERT DE COSTO COMPRA: " + sql); // -- Control por consola del SQL
            DBManager.ejecutarSecuencia(sql);
            resultOk = true;
            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            resultOk = false;
        }finally{
            DBManager.CerrarStatements();
        }
        
        return resultOk;
    }
    
    private int setPrecioCostoAnteriorInactivo(int codLista, long codArticulo) {
        String sql = "UPDATE costoArt SET vigente='N'"
                + " WHERE vigente     ='S'"
                + "   AND cod_articulo=" + codArticulo
                + "   AND cod_lista   =" + codLista;
        return DBManager.ejecutarDML(sql);
    }
    
    private boolean agregarArticulosHistorico(int hCodEmpresa, int hCodLocal, int hCodSector, long hCodArticulo, String hSigla, int hCansi, 
                                              float hCantRecibida, String hTipoComprob, long hNroComprob){
        
        
        String sql = "INSERT INTO hismovi_articulo (cod_empresa, cod_local, cod_sector, cod_articulo, sigla_venta, cansi_venta, cantidad, "
                   + "tipo_movimiento, tip_comprob, fec_movimiento, fec_vigencia, estado, cod_traspaso, nro_comprob) "
                   + "VALUES (" + hCodEmpresa + ", " + hCodLocal + ", " + hCodSector + ", " + hCodArticulo + ", '" + hSigla + "', "
                   + "" + hCansi + ", " + hCantRecibida + ", 'ENT', '" + hTipoComprob + "', 'now()', 'now()', "
                   + "'V', 0, " + hNroComprob + ")";
        System.out.println("INSERT HISMOVI-ARTICULO COMPRAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    
    private boolean agregarNuevoStock(int agCodEmpresa, int agCodLocal, int agCodSector, long agCodArticulo, int agCansi, float agCantRecibida, 
                                      int agCodUsuario, double agCostoNeto){
        float agStock = agCansi * agCantRecibida;
        double agCostoPromedio = agCostoNeto / agCansi;
        
        String sql = "INSERT INTO stockart (cod_empresa, cod_local, cod_articulo, stock, giro_dia, fec_ultcompra, "
                   + "fec_vigencia, cod_usuario, cod_sector, costo_promedio_un) VALUES (" + agCodEmpresa + ", " + agCodLocal + ", "+ agCodArticulo + ", " + agStock + ", 0.01, 'now()', "
                   + "'now()', " + agCodUsuario + ", " + agCodSector + ", " + agCostoPromedio + ")";
        System.out.println("INSERT STOCK: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean actualizarStock(int aCodEmpresa, int aCodLocal, int aCodSector, long aCodArticulo, int aCansi, float aCantRecibida){
        float cantRecibida = 0;
        cantRecibida = aCansi * aCantRecibida;
        String sql = "UPDATE stockart SET stock = stock + " + cantRecibida + ", fec_ultcompra = 'now()', fec_vigencia = 'now()' "
                   + "WHERE cod_empresa = " + aCodEmpresa + " AND cod_local = " + aCodLocal + " AND cod_sector = " + aCodSector + " "
                   + "AND cod_articulo = " +  aCodArticulo;
        System.out.println("ACTUALIZAR STOCK COMPRAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean verExistenciaStock(long eCodArticulo, int eCodEmpresa, int eCodLocal, int eCodSector){
        boolean found = false;
        StatementManager theQuery = new StatementManager();
        theQuery.TheSql = "SELECT * FROM stockart WHERE cod_empresa = " + eCodEmpresa + " AND cod_local = " + eCodLocal + " "
                        + "AND cod_sector = " + eCodSector + " AND cod_articulo = " + eCodArticulo;
        theQuery.EjecutarSql();
        if(theQuery.TheResultSet != null){
            try {
                if (theQuery.TheResultSet.next()) {
                    found = true;
                } else {
                    found = false;
                }
            } catch (SQLException sqlex) {
                found = false;
                InfoErrores.errores(sqlex);
            }
        }
        
        theQuery.CerrarStatement();
        theQuery.ClearDBManagerstmt();
        theQuery = null;
        return (found);
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
    
    private boolean grabarCompraDet(int detCodEmpresa, int detCodLocal, String detTipoComprob, long detNroComprob, String detFecComprob, long detCodArticulo, 
                                    String detSiglaCompra, int detCansiCompra, double detCostoEmpaque, double detCostoUnitario, float detCantRecibida, 
                                    float detPctDescuento, int detPctIva, double detMontoTotal, int detCodMotivo, int detCodSector, int detCodProveedor, 
                                    int detCodCentroCosto){
        String sql = "INSERT INTO compra_det (cod_empresa, cod_local, tip_comprob, nro_comprob, fec_comprob, cod_articulo, sigla_compra, cansi_compra, "
                   + "costo_empaque, costo_unitario, cant_pedido, cant_recib, costo_flete, pct_dcto, pct_iva, mon_total, estado, cod_motivo, cod_sector, "
                   + "fec_vigencia, cod_proveedor, es_ajuste, confirmado, cod_centrocosto, observacion)"
                   + "VALUES (" + detCodEmpresa + ", "
                   + "" + detCodLocal + ", '"
                   + "" + detTipoComprob + "', "
                   + "" + detNroComprob + ", "
                   + "" + detFecComprob + ", "
                   + "" + detCodArticulo + ", '"
                   + "" + detSiglaCompra + "', "
                   + "" + detCansiCompra + ", "
                   + "" + detCostoEmpaque + ", "
                   + "" + detCostoUnitario + ", 0, "
                   + "" + detCantRecibida + ", 0, "
                   + "" + detPctDescuento + ", "
                   + "" + detPctIva + ", "
                   + "" + detMontoTotal + ", 'V', "
                   + "" + detCodMotivo + ", "
                   + "" + detCodSector + ", 'now()', "
                   + "" + detCodProveedor + ", 'N', 'N', "
                   + "" + detCodCentroCosto + ", '-')";
        System.out.println("COMPRA DET: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarCompraCab(int cabCodEmpresa, int cabCodLocal, String cabTipoComprob, long cabNroComprob, String cabFecComprob, int cabCodProveedor, 
                                    String cabFecVencimiento, String cabRucProveedor, double cabTotalExento, double cabTotalGrav10, double cabTotalGrav5, 
                                    double cabTotalMonto, int cabCodUsuario, int cabCodSector, int cabCodMotivo, int cabCodCentroCosto, 
                                    int cabCodComprobFiscal, double cabIva5, double cabIva10, String cabNroTimbrado, String cabFecVencTimbrado){
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
                   + "" + cabCodUsuario + ", 'now()', 1, 0, '-', 'S', 0, "
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
        limpiarTabla();
        llenarCampos();
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBConfirmarDetalle.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        setEstadoComponentes(false);
        limpiarCampos();
        jRBDescPorc.setSelected(false);
        jRBDescMonto.setSelected(false);
        jChBAplicaDescAProductos.setSelected(false);
    }
    
    private void limpiarCampos(){
        jTFCodArticulo.setText("");
        jLBarras.setText("");
        jTFDescArticulo.setText("");
        jTFSiglaCompra.setText("");
        jTFCansiCompra.setText("");
        jTFCantRecibida.setText("");
        jTFIva.setText("");
        jTFCosto.setText("");
        jTFTotalItem.setText("");
        jTFDescuento.setText("");
        jTFNroDocumento.setText("");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jCBCodSector = new javax.swing.JComboBox<>();
        jCBCodCentroCosto = new javax.swing.JComboBox<>();
        jLRazonSocEmpresa = new javax.swing.JLabel();
        jLDescLocal = new javax.swing.JLabel();
        jLDescSector = new javax.swing.JLabel();
        jLDescCentroCosto = new javax.swing.JLabel();
        jBSalir = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTFMontoTotal = new javax.swing.JTextField();
        jTFTipoCambio = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTFMontoGrav5 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTFCodMoneda = new javax.swing.JTextField();
        jTFMontoGrav10 = new javax.swing.JTextField();
        jLDescMoneda = new javax.swing.JLabel();
        jTFMontoExento = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLDescTipoDoc = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTFNroDocumento = new javax.swing.JTextField();
        jTFCodTipoDocumento = new javax.swing.JTextField();
        jTFFecDocumento = new javax.swing.JTextField();
        jLDescMotivo = new javax.swing.JLabel();
        jTFRazonSocProveedor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFCodProveedor = new javax.swing.JTextField();
        jTFCodMotivo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTFNroTimbrado = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jTFFecVencTimbrado = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesCompra = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jTFCodArticulo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTFDescArticulo = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTFSiglaCompra = new javax.swing.JTextField();
        jTFCansiCompra = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTFCantRecibida = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTFIva = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTFCosto = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTFTotalItem = new javax.swing.JTextField();
        jLBarras = new javax.swing.JLabel();
        jLMensajes = new javax.swing.JLabel();
        jTFMontoTotalDetalle = new javax.swing.JTextField();
        jBTotalizar = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jRBDescPorc = new javax.swing.JRadioButton();
        jRBDescMonto = new javax.swing.JRadioButton();
        jTFDescuento = new javax.swing.JTextField();
        jChBAplicaDescAProductos = new javax.swing.JCheckBox();
        jBConfirmarDetalle = new javax.swing.JButton();
        jTFMontoDescuento = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Compras de Mercaderias");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("REGISTRO DE COMPRA DE MERCADERIAS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Local:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Sector:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("C.Costo:");

        jCBCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodEmpresaKeyPressed(evt);
            }
        });

        jCBCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodLocalKeyPressed(evt);
            }
        });

        jCBCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodSectorKeyPressed(evt);
            }
        });

        jCBCodCentroCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodCentroCostoKeyPressed(evt);
            }
        });

        jLRazonSocEmpresa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLRazonSocEmpresa.setText("***");

        jLDescLocal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescLocal.setText("***");

        jLDescSector.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescSector.setText("***");

        jLDescCentroCosto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescCentroCosto.setText("***");

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Valor del Documento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Exento:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Grav. 5%:");

        jTFMontoTotal.setEditable(false);
        jTFMontoTotal.setBackground(new java.awt.Color(255, 255, 102));
        jTFMontoTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMontoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoTotalFocusGained(evt);
            }
        });
        jTFMontoTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoTotalKeyPressed(evt);
            }
        });

        jTFTipoCambio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoCambio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Cambio:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Total:");

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

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Moneda:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Grav. 10%:");

        jTFCodMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodMoneda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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

        jLDescMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescMoneda.setText("***");

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLDescMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTipoCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel16)
                                .addComponent(jLabel15)
                                .addComponent(jLabel17))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTFMontoGrav5)
                                .addComponent(jTFMontoGrav10)
                                .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel14)
                                .addComponent(jLabel12))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTFMontoExento, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jTFCodMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLDescMoneda)
                        .addComponent(jLabel13))
                    .addComponent(jTFTipoCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFMontoExento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTFMontoGrav5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTFMontoGrav10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Datos del Documento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLDescTipoDoc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescTipoDoc.setText("***");
        jLDescTipoDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Motivo:");

        jTFNroDocumento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroDocumento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroDocumento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroDocumentoFocusGained(evt);
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

        jTFCodTipoDocumento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodTipoDocumento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodTipoDocumento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodTipoDocumentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodTipoDocumentoFocusLost(evt);
            }
        });
        jTFCodTipoDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodTipoDocumentoKeyPressed(evt);
            }
        });

        jTFFecDocumento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecDocumento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecDocumento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecDocumentoFocusGained(evt);
            }
        });
        jTFFecDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecDocumentoKeyPressed(evt);
            }
        });

        jLDescMotivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescMotivo.setText("***");

        jTFRazonSocProveedor.setEditable(false);
        jTFRazonSocProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Prov:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Tipo:");

        jTFCodProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodProveedor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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

        jTFCodMotivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodMotivo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Fecha:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Nro. Doc:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Timbrado:");

        jTFNroTimbrado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Vencimiento:");

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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFCodTipoDocumento)
                            .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLDescTipoDoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLDescMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTFFecVencTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFFecDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFNroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLDescTipoDoc)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jTFCodTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFCodMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescMotivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTFFecDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jTFFecVencTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLRazonSocEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBCodCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGrabar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRazonSocEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescSector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jCBCodCentroCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescCentroCosto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBGrabar)
                    .addComponent(jBCancelar)
                    .addComponent(jBSalir))
                .addGap(36, 36, 36))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jTDetallesCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barras", "Código", "Descripción", "Sigla", "U/M", "Cantidad", "Precio Costo", "IVA", "Total Item"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetallesCompra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDetallesCompraFocusGained(evt);
            }
        });
        jTDetallesCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallesCompraKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetallesCompra);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Código:");

        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodArticulo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodArticulo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodArticuloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodArticuloFocusLost(evt);
            }
        });
        jTFCodArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodArticuloKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFCodArticuloKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Descripción");

        jTFDescArticulo.setEditable(false);
        jTFDescArticulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Empaque");

        jTFSiglaCompra.setEditable(false);
        jTFSiglaCompra.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFSiglaCompra.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTFCansiCompra.setEditable(false);
        jTFCansiCompra.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCansiCompra.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Cant Rec.");

        jTFCantRecibida.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCantRecibida.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCantRecibida.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantRecibidaFocusGained(evt);
            }
        });
        jTFCantRecibida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantRecibidaKeyPressed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("IVA");

        jTFIva.setEditable(false);
        jTFIva.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFIva.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Costo");

        jTFCosto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCostoFocusGained(evt);
            }
        });
        jTFCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCostoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFCostoKeyReleased(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Total Item");

        jTFTotalItem.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalItem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTotalItemFocusGained(evt);
            }
        });
        jTFTotalItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTotalItemKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFTotalItemKeyReleased(evt);
            }
        });

        jLBarras.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLBarras.setForeground(new java.awt.Color(51, 51, 255));
        jLBarras.setText("***");

        jLMensajes.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLMensajes.setForeground(new java.awt.Color(51, 51, 255));
        jLMensajes.setText("F1 búsqueda de Artículos");

        jTFMontoTotalDetalle.setEditable(false);
        jTFMontoTotalDetalle.setBackground(new java.awt.Color(255, 255, 102));
        jTFMontoTotalDetalle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMontoTotalDetalle.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoTotalDetalle.setText("000.00");

        jBTotalizar.setText("Totalizar");
        jBTotalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTotalizarActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Descuento:");

        jRBDescPorc.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBDescPorc);
        jRBDescPorc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBDescPorc.setText("%");

        jRBDescMonto.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBDescMonto);
        jRBDescMonto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBDescMonto.setText("Monto");

        jTFDescuento.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescuentoFocusGained(evt);
            }
        });

        jChBAplicaDescAProductos.setBackground(new java.awt.Color(204, 255, 204));
        jChBAplicaDescAProductos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jChBAplicaDescAProductos.setText("Aplica a productos?");

        jBConfirmarDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarDetalle.setText("Confirmar");
        jBConfirmarDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarDetalleActionPerformed(evt);
            }
        });

        jTFMontoDescuento.setEditable(false);
        jTFMontoDescuento.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFMontoDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoDescuento.setText("000.00");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Descuento:");

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("TOTAL:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(92, 92, 92)
                                .addComponent(jLabel19)
                                .addGap(229, 229, 229)
                                .addComponent(jLabel20))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFSiglaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCansiCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jTFCantRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTFIva, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(0, 42, Short.MAX_VALUE))
                            .addComponent(jTFTotalItem)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBTotalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel26))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRBDescPorc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRBDescMonto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jChBAplicaDescAProductos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBConfirmarDetalle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel27)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFMontoTotalDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(jTFMontoDescuento))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSiglaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCansiCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCantRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLBarras)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFMontoDescuento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLMensajes)
                        .addComponent(jBTotalizar)
                        .addComponent(jLabel26)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFMontoTotalDetalle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(jRBDescPorc)
                        .addComponent(jRBDescMonto)
                        .addComponent(jTFDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jChBAplicaDescAProductos)
                        .addComponent(jBConfirmarDetalle)
                        .addComponent(jLabel27)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1318, 714));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodProveedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusGained
        jTFCodProveedor.selectAll();
    }//GEN-LAST:event_jTFCodProveedorFocusGained

    private void jTFNroDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocumentoFocusGained
        jTFNroDocumento.selectAll();
    }//GEN-LAST:event_jTFNroDocumentoFocusGained

    private void jTFCodTipoDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodTipoDocumentoFocusGained
        jTFCodTipoDocumento.selectAll();
    }//GEN-LAST:event_jTFCodTipoDocumentoFocusGained

    private void jTFCodMotivoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMotivoFocusGained
        jTFCodMotivo.selectAll();
    }//GEN-LAST:event_jTFCodMotivoFocusGained

    private void jTFFecDocumentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDocumentoFocusGained
        jTFFecDocumento.selectAll();
    }//GEN-LAST:event_jTFFecDocumentoFocusGained

    private void jTFCodMonedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMonedaFocusGained
        jTFCodMoneda.selectAll();
    }//GEN-LAST:event_jTFCodMonedaFocusGained

    private void jTFTipoCambioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoCambioFocusGained
        jTFTipoCambio.selectAll();
    }//GEN-LAST:event_jTFTipoCambioFocusGained

    private void jTFMontoExentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoExentoFocusGained
        quitarPatternMontos();
        jTFMontoExento.selectAll();
    }//GEN-LAST:event_jTFMontoExentoFocusGained

    private void jTFMontoGrav5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav5FocusGained
        quitarPatternMontos();
        jTFMontoGrav5.selectAll();
    }//GEN-LAST:event_jTFMontoGrav5FocusGained

    private void jTFMontoGrav10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav10FocusGained
        quitarPatternMontos();
        jTFMontoGrav10.selectAll();
    }//GEN-LAST:event_jTFMontoGrav10FocusGained

    private void jTFMontoTotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoTotalFocusGained
        jTFMontoTotal.selectAll();
    }//GEN-LAST:event_jTFMontoTotalFocusGained

    private void jCBCodEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodEmpresaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodLocal.requestFocus();
        }
    }//GEN-LAST:event_jCBCodEmpresaKeyPressed

    private void jCBCodLocalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodLocalKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodSector.requestFocus();
        }
    }//GEN-LAST:event_jCBCodLocalKeyPressed

    private void jCBCodSectorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodSectorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodCentroCosto.requestFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jCBCodCentroCostoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodCentroCostoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodProveedor.requestFocus();
        }
    }//GEN-LAST:event_jCBCodCentroCostoKeyPressed

    private void jTFCodProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodProveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodProveedor.getText().equals("")){
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

    private void jTFNroDocumentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocumentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFNroDocumento.getText().trim().equals("")){
                jTFCodTipoDocumento.requestFocus();
                formatoNroDocumento();                                
                if (jTFNroDocumento.getText().length() == 15) {
                    jTFFecDocumento.select(0, 0);
                } else {
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

    private void jTFCodTipoDocumentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodTipoDocumentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodTipoDocumento.getText().trim().equals("")){
                jTFCodMotivo.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultasTipoComprob dCons = new DlgConsultasTipoComprob(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta Tipo Documento");
                dCons.dConsultas("tipo_comprobante", "descripcion", "cod_comprobante", "descripcion", "tip_comprob", "es_legal", "Código", "Descripción", "Tipo", "Legal");
                dCons.setText(jTFCodTipoDocumento);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodTipoDocumentoKeyPressed

    private void jTFCodMotivoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodMotivoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodMotivo.getText().trim().equals("")){
                jTFFecDocumento.requestFocus();
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

    private void jTFFecDocumentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDocumentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFFecDocumento.getText().trim().equals("")){
                jTFNroTimbrado.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFFecDocumentoKeyPressed

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

    private void jTFTipoCambioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTipoCambioKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFTipoCambio.getText().trim().equals("")){
                jTFMontoExento.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFTipoCambioKeyPressed

    private void jTFMontoExentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoExentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFMontoExento.getText().trim().equals("")){
                jTFMontoGrav5.requestFocus();
            }
        }   
    }//GEN-LAST:event_jTFMontoExentoKeyPressed

    private void jTFMontoGrav5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoGrav5KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFMontoGrav5.getText().trim().equals("")){
                jTFMontoGrav10.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFMontoGrav5KeyPressed

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
            
            jTFCodArticulo.requestFocus();

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

    private void jTFCodTipoDocumentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodTipoDocumentoFocusLost
        if(jTFCodTipoDocumento.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodTipoDocumento.requestFocus();
        }else{
            getDescripcionTipoDocumento(jTFCodTipoDocumento.getText().trim());
            getTimbradoDoc();
        }
    }//GEN-LAST:event_jTFCodTipoDocumentoFocusLost

    private void jTFCodMotivoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMotivoFocusLost
        if(jTFCodMotivo.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodMotivo.requestFocus();
        }else{
            getDescripcionMotivo(jTFCodMotivo.getText().trim());
        }
    }//GEN-LAST:event_jTFCodMotivoFocusLost

    private void jTFCodMonedaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMonedaFocusLost
        if(jTFCodMoneda.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodMoneda.requestFocus();
        }else{
            getNombreMoneda(jTFCodMoneda.getText().trim());
        }
    }//GEN-LAST:event_jTFCodMonedaFocusLost

    private void jTFNroDocumentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocumentoKeyTyped
        jTFNroDocumento.getText().replace(".", "-");
    }//GEN-LAST:event_jTFNroDocumentoKeyTyped

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        setEstadoComponentes(true);
        limpiarTabla();
        llenarCampos();
        jTFCodArticulo.setText("");
        jTFDescArticulo.setText("");
        jTFSiglaCompra.setText("");
        jTFCansiCompra.setText("");
        jTFCantRecibida.setText("");
        jTFIva.setText("");
        jTFCosto.setText("");
        jTFTotalItem.setText("");
        jBNuevo.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBGrabar.setEnabled(true);
        jCBCodEmpresa.requestFocus();
        jBSalir.setEnabled(false);
        jBConfirmarDetalle.setEnabled(true);
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoComponentes(false);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBNuevo.requestFocus();
        jBSalir.setEnabled(true);
        jBConfirmarDetalle.setEnabled(false);
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodArticuloKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodArticulo.getText().trim().equals("")){
                jTFCantRecibida.requestFocus();
                jTFCantRecibida.setText("1");
                if(dtmDetallesCompra.getRowCount() > 0){
                    if(articuloExisteDetalle(jTFCodArticulo.getText().trim())){
                        JOptionPane.showMessageDialog(this, "Artículo ya está en detalle!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
                        jTDetallesCompra.changeSelection(filaExistente, 5, true, false);
                    }
                }
                jTFTotalItem.setText(jTFCosto.getText().trim());
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try{
            BuscaArticuloFiltro busquedaArt = new BuscaArticuloFiltro(new JFrame(), true);
            busquedaArt.pack();
            busquedaArt.dConsultas("articulo", "descripcion", "cod_articulo", "descripcion", "precio_venta", "Empaque", "Código", "Descripción", "Precio de Venta", "Empaque");
            busquedaArt.jTFDescripcion.setText("%");
            busquedaArt.setText(jTFCodArticulo);
            busquedaArt.setVisible(true);            
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodArticuloKeyPressed

    private void jTFCantRecibidaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantRecibidaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCantRecibida.getText().trim().equals("") && !jTFCantRecibida.getText().trim().equals("0")){
                jTFCosto.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFCantRecibidaKeyPressed

    private void jTFCostoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCostoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCosto.getText().trim().equals("")){                
                jTFTotalItem.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFCostoKeyPressed

    private void jTFCodArticuloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusGained
        jTFCodArticulo.selectAll();
        jLMensajes.setText("F1 para búsqueda de Artículos");
    }//GEN-LAST:event_jTFCodArticuloFocusGained

    private void jTFCantRecibidaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantRecibidaFocusGained
        jTFCantRecibida.selectAll();
        if(jTFSiglaCompra.getText().trim().equals("UN")){
            jTFCantRecibida.setDocument(new MaxLength(12, "", "ENTERO"));
        }else if(!jTFSiglaCompra.getText().trim().equals("UN")){
            jTFCantRecibida.setDocument(new MaxLength(12, "", "FLOAT"));
        }
    }//GEN-LAST:event_jTFCantRecibidaFocusGained

    private void jTFCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCostoFocusGained
        jTFCosto.selectAll();
    }//GEN-LAST:event_jTFCostoFocusGained

    private void jTFTotalItemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTotalItemFocusGained
        jTFTotalItem.selectAll();
    }//GEN-LAST:event_jTFTotalItemFocusGained

    private void jTFCodArticuloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusLost
        if(!jTFCodArticulo.getText().trim().equals("")){            
            String codigo = getCodArticulo(jTFCodArticulo.getText().trim());
            jTFCodArticulo.setText(codigo);            
            cargaDatosArticulo(codigo);
            jLBarras.setText(getBarrasArticulo(codigo));
        }
    }//GEN-LAST:event_jTFCodArticuloFocusLost

    private void jTFMontoTotalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoTotalKeyPressed
        
    }//GEN-LAST:event_jTFMontoTotalKeyPressed

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        salirDelModulo();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFTotalItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTotalItemKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFTotalItem.getText().trim().equals("") && !jTFTotalItem.getText().trim().equals("0")){
                jTFCodArticulo.requestFocus();
                addArticuloDetalle();
            }
        }
    }//GEN-LAST:event_jTFTotalItemKeyPressed

    private void jTFCodArticuloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodArticuloKeyTyped
        codigoBarrasDigitado = jTFCodArticulo.getText().trim();
        System.out.println("BARRAS DIGITADO: " + codigoBarrasDigitado);
    }//GEN-LAST:event_jTFCodArticuloKeyTyped

    private void jTDetallesCompraFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDetallesCompraFocusGained
        jLMensajes.setText("F2 Registro Artículo \n DELETE borrar artículo seleccionado");
    }//GEN-LAST:event_jTDetallesCompraFocusGained

    private void jTDetallesCompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallesCompraKeyPressed
        int filaSeleccionada = jTDetallesCompra.getSelectedRow();
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            for(int i = 0; i < jTDetallesCompra.getRowCount(); i++){
                if(i == filaSeleccionada){
                    dtmDetallesCompra.removeRow(i);
                }
            }
            jBTotalizar.doClick();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F2){
            String codigoArticulo = dtmDetallesCompra.getValueAt(filaSeleccionada, 1).toString();
            Articulos articulos = new Articulos(new JFrame(), true, "COMPRAS", codigoArticulo);
            articulos.pack();
            articulos.setVisible(true);
        }
    }//GEN-LAST:event_jTDetallesCompraKeyPressed

    private void jTFTotalItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTotalItemKeyReleased
        totalItem = Double.parseDouble(jTFTotalItem.getText().trim());
        cantidad = Float.parseFloat(jTFCantRecibida.getText());
        costo = Math.round(totalItem / cantidad);
        jTFCosto.setText(String.valueOf(costo));
        System.out.println(">> ESTO PASA EN KEY RELEASE: " + costo);  
    }//GEN-LAST:event_jTFTotalItemKeyReleased

    private void jTFCostoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCostoKeyReleased
        //double totalItem, costo;
        costo = Double.parseDouble(jTFCosto.getText().trim());
        float cantRecibida = Float.parseFloat(jTFCantRecibida.getText().trim());
        totalItem = Math.round(costo * cantRecibida);
        jTFTotalItem.setText(String.valueOf(totalItem));
    }//GEN-LAST:event_jTFCostoKeyReleased

    private void jTFMontoGrav10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoGrav10FocusLost
        int sum = totalExento + totalGrav5 + totalGrav10;
        jTFMontoTotal.setText(decimalFormat.format(sum));
    }//GEN-LAST:event_jTFMontoGrav10FocusLost

    private void jBTotalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTotalizarActionPerformed
        long suma = 0;
        for(int i = 0; i < jTDetallesCompra.getRowCount(); i++){
            double valor =((Double)dtmDetallesCompra.getValueAt(i, 8));
            suma += valor;
            jTFMontoTotalDetalle.setText(decimalFormat.format(suma));
        }
    }//GEN-LAST:event_jBTotalizarActionPerformed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(dtmDetallesCompra.getRowCount() > 0){
            if(camposCabeceraOk()){
                if(compararMontos()){
                    if(grabarDatos()){
                        JOptionPane.showMessageDialog(this,"ATENCION: Datos grabados correctamente!",     "EXITO",  JOptionPane.INFORMATION_MESSAGE);
                        setComponentesGrabado();
                    }else{
                        JOptionPane.showMessageDialog(this,"ATENCION: Error grabando Documento!",     "ATENCION",  JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }else{
            JOptionPane.showMessageDialog(this, "Ingrese detalle para grabar!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            jTFCodArticulo.requestFocus();
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jBConfirmarDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarDetalleActionPerformed
        if(dtmDetallesCompra.getRowCount() > 0){
            jBTotalizar.doClick();
            if(!jTFDescuento.getText().trim().equals("")){
                float montoDescuento = Float.parseFloat(jTFDescuento.getText().trim());
                float montoTotalDetalle = Float.parseFloat(jTFMontoTotalDetalle.getText().trim().replace(",", ""));
                float vPctDescuento = (montoDescuento * 100) / montoTotalDetalle;
                String monto = decimalFormat.format(vPctDescuento);
                vPctDescuento = Float.parseFloat(monto);
                System.out.println("JRBMONTO SELECTED % DESCUENTO: " + vPctDescuento);                
                
                calcularDescuento();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Debe ingresar detalle de compra!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jBConfirmarDetalleActionPerformed

    private void jTFDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescuentoFocusGained
        jTFDescuento.selectAll();
    }//GEN-LAST:event_jTFDescuentoFocusGained

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

    private void jTFFecVencTimbradoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecVencTimbradoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFFecVencTimbrado.getText().trim().equals("")){                
                jTFCodMoneda.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFFecVencTimbradoKeyPressed

    private void jTFFecVencTimbradoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencTimbradoFocusLost
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try{
            System.out.println("FECHA ACTUAL: " + fecVigencia + "\nFECHA TIMBRADO: " + jTFFecVencTimbrado.getText());
            Date fechaActual = sdf.parse(fecVigencia);
            Date fechaTimbrado = sdf.parse(jTFFecVencTimbrado.getText());
            if(fechaTimbrado.equals(fechaActual) || fechaTimbrado.after(fechaActual)){
                jTFCodMoneda.requestFocus();
            }else{
                JOptionPane.showMessageDialog(this, "Vencimiento del timbrado no puede ser menor a la fecha actual", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jTFFecVencTimbrado.requestFocus();
            }
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jTFFecVencTimbradoFocusLost

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
            java.util.logging.Logger.getLogger(RegistroCompraMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroCompraMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroCompraMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroCompraMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroCompraMercaderias dialog = new RegistroCompraMercaderias(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBConfirmarDetalle;
    private javax.swing.JButton jBGrabar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JButton jBTotalizar;
    private javax.swing.JComboBox<String> jCBCodCentroCosto;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JCheckBox jChBAplicaDescAProductos;
    private javax.swing.JLabel jLBarras;
    private javax.swing.JLabel jLDescCentroCosto;
    private javax.swing.JLabel jLDescLocal;
    private javax.swing.JLabel jLDescMoneda;
    private javax.swing.JLabel jLDescMotivo;
    private javax.swing.JLabel jLDescSector;
    private javax.swing.JLabel jLDescTipoDoc;
    private javax.swing.JLabel jLMensajes;
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
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRBDescMonto;
    private javax.swing.JRadioButton jRBDescPorc;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesCompra;
    private javax.swing.JTextField jTFCansiCompra;
    private javax.swing.JTextField jTFCantRecibida;
    private javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFCodMoneda;
    private javax.swing.JTextField jTFCodMotivo;
    private javax.swing.JTextField jTFCodProveedor;
    private javax.swing.JTextField jTFCodTipoDocumento;
    private javax.swing.JTextField jTFCosto;
    private javax.swing.JTextField jTFDescArticulo;
    private javax.swing.JTextField jTFDescuento;
    private javax.swing.JTextField jTFFecDocumento;
    private javax.swing.JTextField jTFFecVencTimbrado;
    private javax.swing.JTextField jTFIva;
    private javax.swing.JTextField jTFMontoDescuento;
    private javax.swing.JTextField jTFMontoExento;
    private javax.swing.JTextField jTFMontoGrav10;
    private javax.swing.JTextField jTFMontoGrav5;
    private javax.swing.JTextField jTFMontoTotal;
    private javax.swing.JTextField jTFMontoTotalDetalle;
    private javax.swing.JTextField jTFNroDocumento;
    private javax.swing.JTextField jTFNroTimbrado;
    private javax.swing.JTextField jTFRazonSocProveedor;
    private javax.swing.JTextField jTFSiglaCompra;
    private javax.swing.JTextField jTFTipoCambio;
    private javax.swing.JTextField jTFTotalItem;
    // End of variables declaration//GEN-END:variables
}
