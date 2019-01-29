/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import static utiles.Utiles.padLefth;
import views.busca.DlgConsultas;

/**
 *
 * @author ANDRES
 */
public class AnulacionDocCompraGasto extends javax.swing.JDialog {

    String fecVigencia = "";
    DefaultTableModel dtmDetalles;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    long nroComprob;
    
    public AnulacionDocCompraGasto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configCampos();
        llenarCombosLabels();
        getFecVigencia();
        llenarCampos();
        configTabla();
        jTFTipoComprob.setDocument(new MaxLength(3, "UPPER", "ALFA"));
        jTFFecDoc.setInputVerifier(new FechaInputVerifier(jTFFecDoc));
        jTFNroComprob.grabFocus();
    }

    private void configCampos(){
        jTFNroComprob.addFocusListener(new Focus());
        jTFFecDoc.addFocusListener(new Focus());
        jTFCodProveedor.addFocusListener(new Focus());
        jTFTipoComprob.addFocusListener(new Focus());
    }
    
    private void llenarCombosLabels(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
        
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jLNombreSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
    }
    
    private void formatoNroDocumento(){
        String sinPunto = jTFNroComprob.getText().replace(".", "");
        String str1 = "";
        String str2 = "";
        String str3 = "";
        int tam = sinPunto.length();
        if (tam > 0 && tam < 4) {
            jTFNroComprob.setText(padLefth(3, "0", sinPunto));
        }
        if (tam > 3 && tam < 7) {
            str1 = jTFNroComprob.getText().substring(0, 3);
            str2 = jTFNroComprob.getText();
            str2 = sinPunto.substring(3, tam);
            jTFNroComprob.setText(str1 + "-" + padLefth(3, "0", str2));
        }

        if (tam > 7) {
            tam = jTFNroComprob.getText().length();
            str1 = jTFNroComprob.getText().substring(0, 3);
            str2 = jTFNroComprob.getText().substring(4, 7);
            str3 = jTFNroComprob.getText().substring(8, tam);
            jTFNroComprob.setText("");
            jTFNroComprob.setText(str1 + "-" + str2 + "-" + padLefth(7, "0", str3));
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
    
    private void llenarCampos(){
        jTFFecDoc.setText(fecVigencia);
        jTFCodProveedor.setText("1");
    }
    
    private void completarDatosProveedor(String codigo){
        if(jTFCodProveedor.getText().equals("0")){
            jLRazonSocProveedor.setText("PROVEEDOR INEXISTENTE!");
            jLRazonSocProveedor.setForeground(Color.red);
            jTFCodProveedor.requestFocus();
        }else{
            jLRazonSocProveedor.setForeground(Color.black);
            jLRazonSocProveedor.setText("");
            try{
                String sql = "SELECT razon_soc || ' ( ' || ruc_proveedor || ' )'AS datosproveedor FROM proveedor WHERE cod_proveedor = " + codigo ;
                System.out.println("SQL Proveedor: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLRazonSocProveedor.setText(rs.getString("datosproveedor"));
                }else{
                    jLRazonSocProveedor.setText("PROVEEDOR INEXISTENTE!");
                    jLRazonSocProveedor.setForeground(Color.red);
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
    private void getDescripcionTipoDocumento(String codigo){
        if(jTFTipoComprob.getText().trim().equals("0")){
            jLDescripcionTipoComprob.setText("DOCUMENTO INEXISTENTE");
        }else{
            jLDescripcionTipoComprob.setText("");
            try{
                String sql = "SELECT descripcion FROM tipo_comprobante WHERE tip_comprob = '" + codigo + "'";
                System.out.println("SELECT TIPO COMPROBANTE: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
                if(rs != null){
                    if(rs.next()){
                        jLDescripcionTipoComprob.setText(rs.getString("descripcion"));
                    }else{
                        jLDescripcionTipoComprob.setText("DOCUMENTO INEXISTENTE");
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
    
    private void getDatosDoc(){      
        nroComprob = Long.valueOf(eliminaCaracteres(jTFNroComprob.getText(), "-"));
        try{
            String sql = "SELECT cc.estado, cc.nro_timbrado, cc.cod_tipomerc, tp.descripcion, cc.total_monto "
                       + "FROM compra_cab cc "
                       + "INNER JOIN tipo_mercaderia tp "
                       + "ON cc.cod_tipomerc = tp.cod_tipomerc "
                       + "WHERE cc.nro_comprob = " + nroComprob + " "
                       + "AND cc.tip_comprob = '" + jTFTipoComprob.getText().trim() + "' "
                       + "AND cc.cod_proveedor = " + jTFCodProveedor.getText().trim() + " "
                       + "AND cc.nro_comprob = " + nroComprob + " "
                       + "AND fec_comprob = to_date('" + jTFFecDoc.getText().trim() + "', 'dd/MM/yyyy');";
            System.out.println("SQL - BUSQ DATOS DEL DOC: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFNroTimbrado.setText(rs.getString("nro_timbrado"));
                    jTFCodTipoMercGasto.setText(rs.getString("cod_tipomerc"));
                    jLDescTipoMercGasto.setText(rs.getString("descripcion"));
                    jTFEstado.setText(rs.getString("estado"));
                    jTFMontoTotal.setText(decimalFormat.format(rs.getDouble("total_monto")));
                }else{
                    JOptionPane.showMessageDialog(this, "Documento inexistente, verifique los datos ingresados!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                    jTFNroComprob.grabFocus();
                    jTFNroTimbrado.setText("");
                    jTFCodTipoMercGasto.setText("");
                    jLDescTipoMercGasto.setText("***");
                    jTFEstado.setText("");
                    jTFMontoTotal.setText("0");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void llenarTabla(){
        limpiarTabla();
        String fecComprob = "to_date('" + jTFFecDoc.getText() + "', 'dd/MM/yyyy')";
        
        long vNroComprob = Long.valueOf(eliminaCaracteres(jTFNroComprob.getText(), "-"));
        String timbrado = jTFNroTimbrado.getText().trim();
        String tipoComprob = jTFTipoComprob.getText().trim();
        String codProveedor = jTFCodProveedor.getText().trim();
        
        int empaque, iva;
        double costoUnitario, totalItem;
        float cantidad;
        String descripcion, sigla, codArticulo;
        
        String sql = "SELECT compra_det.cod_articulo, articulo.descripcion, compra_det.cant_recib, "
                   + "compra_det.sigla_compra, compra_det.cansi_compra, "
                   + "compra_det.costo_unitario, compra_det.mon_total, compra_det.pct_iva "
                   + "FROM compra_det "
                   + "INNER JOIN articulo "
                   + "ON compra_det.cod_articulo = articulo.cod_articulo "
                   + "INNER JOIN compra_cab "
                   + "ON compra_cab.nro_comprob = compra_det.nro_comprob "
                   + "WHERE compra_det.nro_comprob = " + vNroComprob+ " AND compra_cab.nro_timbrado = '" + timbrado + "' "
                   + "AND compra_det.tip_comprob = '" + tipoComprob + "' "
                   + "AND compra_cab.cod_proveedor = " + codProveedor + ";";
        System.out.println("SQL DETALLES DOC (Anulacion de comprob de compras/gastos): " + sql);
        ResultSet rs = DBManager.ejecutarDSL(sql);
        
        try{
            if(rs != null){
                while(rs.next()){
                    codArticulo = rs.getString("cod_articulo");
                    empaque = rs.getInt("cansi_compra");
                    iva = rs.getInt("pct_iva");
                    costoUnitario = rs.getDouble("costo_unitario");
                    totalItem = rs.getDouble("mon_total");
                    cantidad = rs.getFloat("cant_recib");
                    descripcion = rs.getString("descripcion");
                    sigla = rs.getString("sigla_compra");
                    
                    dtmDetalles.addRow(new Object[]{new String(codArticulo), new String(descripcion), new String(sigla), new Integer(empaque), 
                                       new Integer(iva), new Double(costoUnitario), new Float(cantidad), new Double(totalItem)});
                    jTDetalleDoc.setModel(dtmDetalles);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        
    }
    
    private String eliminaCaracteres(String cadena, String caracter) {

        cadena = cadena.replace(caracter, "");

        return cadena;
    }
    
    private void configTabla(){
        dtmDetalles = (DefaultTableModel) jTDetalleDoc.getModel();
        jTDetalleDoc.getColumnModel().getColumn(0).setPreferredWidth(30); // articulo
        jTDetalleDoc.getColumnModel().getColumn(1).setPreferredWidth(150); // descripcion 
        jTDetalleDoc.getColumnModel().getColumn(2).setPreferredWidth(30); // sigla 
        jTDetalleDoc.getColumnModel().getColumn(3).setPreferredWidth(30); // empaque
        jTDetalleDoc.getColumnModel().getColumn(4).setPreferredWidth(30); // iva 
        jTDetalleDoc.getColumnModel().getColumn(5).setPreferredWidth(80); // costo un 
        jTDetalleDoc.getColumnModel().getColumn(6).setPreferredWidth(30); // cantidad
        jTDetalleDoc.getColumnModel().getColumn(7).setPreferredWidth(80); // total item
        jTDetalleDoc.setRowHeight(20);
    }
    
    private void limpiarTabla(){
        int nroFilas = dtmDetalles.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetalles.removeRow(i);
            nroFilas = dtmDetalles.getRowCount();
            
            if(nroFilas == 1){
                dtmDetalles.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetalleDoc.setModel(dtmDetalles);
    }
    
    private void anularDocCompra(){
        
        boolean resultOperacionesAnulacionCompra = false;
        boolean resultGrabarAnulacion = true;
        
        String codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        String codLocal = jCBCodLocal.getSelectedItem().toString();
        String codSector = jCBCodSector.getSelectedItem().toString();
        String tipoComprob = jTFTipoComprob.getText().trim(); 
        String codProveedor = jTFCodProveedor.getText().trim();
        String fecDocumento = jTFFecDoc.getText().trim();
        String nroTimbrado = jTFNroTimbrado.getText().trim();
        String tieneDetalle = "";
        
        if(jTFEstado.getText().trim().equals("A")){
            JOptionPane.showMessageDialog(this, "El documento ya se encuentra anulado!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            jTFFecDoc.setText(fecVigencia);
            jTFCodProveedor.setText("1");
            jLRazonSocProveedor.setText("");
            jTFTipoComprob.setText("");
            jLDescripcionTipoComprob.setText("***");
            jTFNroTimbrado.setText("");
            jTFCodTipoMercGasto.setText("");
            jLDescTipoMercGasto.setText("***");
            jTFEstado.setText("");
            jTFMontoTotal.setText("0");
            jTFNroComprob.grabFocus();
            limpiarTabla();
        }else{            
            
            if(dtmDetalles.getRowCount() > 0){
                tieneDetalle = "S";
                
                resultGrabarAnulacion = anulacionCompraCab(tieneDetalle, codEmpresa, codLocal, tipoComprob, codProveedor, fecDocumento, nroTimbrado);
            
                if(resultGrabarAnulacion){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al anular cabecera!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultOperacionesAnulacionCompra = true;
                }
                
                System.out.println("ENTRA EN EL IF DE MAYOR A CERO");
                for(int i = 0; i < dtmDetalles.getRowCount(); i++){
                    System.out.println("VECES QUE ENTRA EN EL FOR: " + i);
                    String codArticulo = dtmDetalles.getValueAt(i, 0).toString();
                    double cantidad = Double.parseDouble(jTDetalleDoc.getValueAt(i, 6).toString());
                    String sigla = dtmDetalles.getValueAt(i, 2).toString();
                    String cansi = dtmDetalles.getValueAt(i, 3).toString();
                    updateHisMoviArticulo(codEmpresa, codLocal, codSector, codArticulo, sigla, cansi, cantidad, tipoComprob, String.valueOf(nroComprob));
                    System.out.println("COD ARTICULO: " + codArticulo + "\nCANTIDAD: " + cantidad);
                    updateStock(codArticulo, cantidad);
                }
            }else{
                tieneDetalle = "N";
                resultGrabarAnulacion = anulacionCompraCab(tieneDetalle, codEmpresa, codLocal, tipoComprob, codProveedor, fecDocumento, nroTimbrado);
            
                if(!resultGrabarAnulacion){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al anular cabecera!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultOperacionesAnulacionCompra = true;
                }
            }
            
            if(resultOperacionesAnulacionCompra){
                if(!rollBacktDatos()){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el RollBack!", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(this, "No se pudo realizar la anulación!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                if(!commitDatos()){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el Commit!", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(this, "ANULACIÓN realizada con éxito!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
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
    
    private void updateStock(String codArticulo, double cantidad){
        updateStockArtResta(codArticulo, cantidad);
    }
    
    private void updateStockArtResta(String codArticulo, double canVenta){
        String sql = "UPDATE stockart SET stock = (stock - " + canVenta + "), fec_vigencia = 'now()' WHERE cod_articulo = " + codArticulo;
        System.out.println("UPDATE STOCK ANULACION COMPRA: " + sql);
        grabarPrevioCommit(sql);
    }
    
    private void updateHisMoviArticulo(String codEmpresa, String codLocal, String codSector, String codArticulo, String siglaVenta, String cansiVenta, 
                                       double cantidad, String tipoComp, String nroComprob){
        String sql = "INSERT INTO hismovi_articulo (cod_empresa, cod_local, cod_sector, cod_articulo, sigla_venta, cansi_venta, cantidad, "
                   + "tipo_movimiento, tip_comprob, fec_movimiento, fec_vigencia, estado, cod_traspaso, nro_comprob) VALUES ("
                   + codEmpresa + ", "
                   + codLocal + ", "
                   + codSector + ", "
                   + codArticulo + ", '"
                   + siglaVenta + "', "
                   + cansiVenta + ", "
                   + cantidad + ", 'SAL', '"
                   + tipoComp + "', 'now()', 'now()', 'V', 0, "
                   + nroComprob + ");";
        grabarPrevioCommit(sql);
    }
    
    private boolean anulacionCompraCab(String tieneDetalle, String codEmpresa, String codLocal, String tipoComprob, String codProveedor, String fecDocumento, 
                                       String nroTimbrado){
        String sql = "";
        if(!tieneDetalle.equals("S")){
               sql = "BEGIN; "
                   + ""
                   + "UPDATE compra_cab SET estado = 'A', fec_vigencia = 'now()', cod_usuario = " + FormMain.codUsuario + " "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy') "
                   + "AND nro_timbrado = '" + nroTimbrado + "'; "
                   + ""
                   + "UPDATE compra_det_cuotas SET estado = 'A', fec_vigencia = 'now()', cod_usuario = " + FormMain.codUsuario + " "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy'); "
                   + ""
                   + "COMMIT;";
        }else{
               sql = "BEGIN; "
                   + ""
                   + "UPDATE compra_cab SET estado = 'A', fec_vigencia = 'now()', cod_usuario = " + FormMain.codUsuario + " "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy') "
                   + "AND nro_timbrado = '" + nroTimbrado + "'; "
                   + ""                   
                   + "UPDATE compra_det SET estado = 'A', fec_vigencia = 'now()' "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy'); "
                   + ""
                   + "UPDATE compra_det_cuotas SET estado = 'A', fec_vigencia = 'now()', cod_usuario = " + FormMain.codUsuario + " "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy'); "
                   + ""
                   + "COMMIT;";
        }
        
        System.out.println("SQL - ANULACION DOC DE COMPRA (cabecera + det_cuotas): " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean anulacionCompraDet(String codEmpresa, String codLocal, String tipoComprob, String codProveedor, String fecDocumento){
        String sql = "UPDATE compra_det SET estado = 'A', fec_vigencia = 'now()' "
                   + "WHERE cod_empresa = " + codEmpresa + " "
                   + "AND cod_local = " + codLocal + " "
                   + "AND nro_comprob = " + nroComprob + " "
                   + "AND tip_comprob = '" + tipoComprob + "' "
                   + "AND cod_proveedor = " + codProveedor + " "
                   + "AND fec_comprob = to_date('" + fecDocumento + "', 'dd/MM/yyyy'); ";
        System.out.println("SQL - ANULACION DE DOC DE COMPRA (detalles):" +  sql);
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
        jCBCodSector = new javax.swing.JComboBox<>();
        jLNombreSector = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jTFNroComprob = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFNroTimbrado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFFecDoc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFCodProveedor = new javax.swing.JTextField();
        jLRazonSocProveedor = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTFTipoComprob = new javax.swing.JTextField();
        jLDescripcionTipoComprob = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTFCodTipoMercGasto = new javax.swing.JTextField();
        jLDescTipoMercGasto = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTFEstado = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalleDoc = new javax.swing.JTable();
        jBCancelar = new javax.swing.JButton();
        jBAnularDocumento = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jTFMontoTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Anulación de documento de compras - gastos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("ANULACION DE DOCUMENTO DE COMPRAS - GASTOS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jCBCodEmpresa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCBCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodEmpresaKeyPressed(evt);
            }
        });

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel2.setText("Local:");

        jCBCodLocal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCBCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodLocalKeyPressed(evt);
            }
        });

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel4.setText("Sector:");

        jCBCodSector.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCBCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodSectorKeyPressed(evt);
            }
        });

        jLNombreSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreSector.setText("***");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Nro. Comprobante:");

        jTFNroComprob.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroComprob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroComprob.setToolTipText("");
        jTFNroComprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroComprobFocusGained(evt);
            }
        });
        jTFNroComprob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroComprobKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFNroComprobKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Nro. Timbrado:");

        jTFNroTimbrado.setEditable(false);
        jTFNroTimbrado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroTimbrado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Fecha del documento:");

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

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Cód. Proveedor:");

        jTFCodProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLRazonSocProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLRazonSocProveedor.setText("***");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Tipo comprobante:");

        jTFTipoComprob.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLDescripcionTipoComprob.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescripcionTipoComprob.setText("***");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Tipo merc/gasto:");

        jTFCodTipoMercGasto.setEditable(false);
        jTFCodTipoMercGasto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodTipoMercGasto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLDescTipoMercGasto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescTipoMercGasto.setText("***");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Estado:");

        jTFEstado.setEditable(false);
        jTFEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFEstado.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTDetalleDoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Articulo", "Descripción", "Sigla", "Emp", "IVA", "Costo Un. ", "Cantidad", "Total Item"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Float.class, java.lang.Double.class
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
        jScrollPane1.setViewportView(jTDetalleDoc);
        if (jTDetalleDoc.getColumnModel().getColumnCount() > 0) {
            jTDetalleDoc.getColumnModel().getColumn(0).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(1).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(2).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(3).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(4).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(5).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(6).setResizable(false);
            jTDetalleDoc.getColumnModel().getColumn(7).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBAnularDocumento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eliminar24.png"))); // NOI18N
        jBAnularDocumento.setText("Anular documento");
        jBAnularDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAnularDocumentoActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Total del documento:");

        jTFMontoTotal.setEditable(false);
        jTFMontoTotal.setBackground(new java.awt.Color(0, 0, 0));
        jTFMontoTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMontoTotal.setForeground(new java.awt.Color(255, 0, 0));
        jTFMontoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoTotal.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTFNroComprob)
                                            .addComponent(jTFFecDoc, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTFTipoComprob, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                            .addComponent(jTFCodProveedor, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLDescripcionTipoComprob, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTFCodTipoMercGasto)
                                    .addComponent(jTFEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLDescTipoMercGasto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 931, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBAnularDocumento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreSector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFNroComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTFNroTimbrado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFFecDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRazonSocProveedor)
                    .addComponent(jLabel10)
                    .addComponent(jTFCodTipoMercGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescTipoMercGasto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescripcionTipoComprob)
                    .addComponent(jLabel11)
                    .addComponent(jTFEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBCancelar)
                        .addComponent(jBAnularDocumento))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFNroComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroComprobFocusGained
        jTFNroComprob.selectAll();
    }//GEN-LAST:event_jTFNroComprobFocusGained

    private void jTFNroComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFNroComprob.getText().trim().equals("")){
                jTFFecDoc.requestFocus();
                formatoNroDocumento();                                
                if (jTFNroComprob.getText().length() == 15) {
                    jTFNroComprob.select(0, 0);
                } else {
                    JOptionPane.showMessageDialog(this, "ATENCION... Formato de Factura Incorrecto, favor verifique!\n"
                                                      + "FORMATO >> (000-000-0000000)");
                    jTFNroComprob.selectAll();
                    jTFNroComprob.grabFocus();
                }
            }
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_DECIMAL) {
            formatoNroDocumento();
        }
    }//GEN-LAST:event_jTFNroComprobKeyPressed

    private void jTFFecDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDocFocusGained
        jTFFecDoc.selectAll();
    }//GEN-LAST:event_jTFFecDocFocusGained

    private void jCBCodEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodEmpresaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodLocal.grabFocus();
        }
    }//GEN-LAST:event_jCBCodEmpresaKeyPressed

    private void jCBCodLocalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodLocalKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBCodSector.grabFocus();
        }
    }//GEN-LAST:event_jCBCodLocalKeyPressed

    private void jCBCodSectorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCodSectorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroComprob.grabFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jTFFecDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodProveedor.grabFocus();
        }
    }//GEN-LAST:event_jTFFecDocKeyPressed

    private void jTFCodProveedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusGained
        jTFCodProveedor.selectAll();
    }//GEN-LAST:event_jTFCodProveedorFocusGained

    private void jTFCodProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodProveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodProveedor.getText().equals("")){
                jTFTipoComprob.requestFocus();
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

    private void jTFTipoComprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoComprobFocusGained
        jTFTipoComprob.selectAll();
    }//GEN-LAST:event_jTFTipoComprobFocusGained

    private void jTFNroComprobKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroComprobKeyTyped
        jTFNroComprob.getText().replace(".", "-");
    }//GEN-LAST:event_jTFNroComprobKeyTyped

    private void jTFCodProveedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusLost
        if(jTFCodProveedor.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodProveedor.requestFocus();
        }else{
            completarDatosProveedor(jTFCodProveedor.getText().trim());            
        }
    }//GEN-LAST:event_jTFCodProveedorFocusLost

    private void jTFTipoComprobFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTipoComprobFocusLost
        if(jTFTipoComprob.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFTipoComprob.requestFocus();
        }else{
            getDescripcionTipoDocumento(jTFTipoComprob.getText().trim());
            getDatosDoc();
            llenarTabla();
        }
    }//GEN-LAST:event_jTFTipoComprobFocusLost

    private void jTFTipoComprobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTipoComprobKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBAnularDocumento.grabFocus();
        }
    }//GEN-LAST:event_jTFTipoComprobKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        jTFFecDoc.setText(fecVigencia);
        jTFCodProveedor.setText("1");
        jLRazonSocProveedor.setText("");
        jTFTipoComprob.setText("");
        jLDescripcionTipoComprob.setText("***");
        jTFNroTimbrado.setText("");
        jTFCodTipoMercGasto.setText("");
        jLDescTipoMercGasto.setText("***");
        jTFEstado.setText("");
        jTFMontoTotal.setText("0");
        jTFNroComprob.grabFocus();
        limpiarTabla();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBAnularDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAnularDocumentoActionPerformed
        int question = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar documento?", "Atención", JOptionPane.YES_NO_OPTION);
        if(question == 0){
            getDatosDoc();
            anularDocCompra();
        }
    }//GEN-LAST:event_jBAnularDocumentoActionPerformed

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
            java.util.logging.Logger.getLogger(AnulacionDocCompraGasto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnulacionDocCompraGasto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnulacionDocCompraGasto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnulacionDocCompraGasto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnulacionDocCompraGasto dialog = new AnulacionDocCompraGasto(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBAnularDocumento;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLDescTipoMercGasto;
    private javax.swing.JLabel jLDescripcionTipoComprob;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLNombreSector;
    private javax.swing.JLabel jLRazonSocProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTDetalleDoc;
    private javax.swing.JTextField jTFCodProveedor;
    private javax.swing.JTextField jTFCodTipoMercGasto;
    private javax.swing.JTextField jTFEstado;
    private javax.swing.JTextField jTFFecDoc;
    private javax.swing.JTextField jTFMontoTotal;
    private javax.swing.JTextField jTFNroComprob;
    private javax.swing.JTextField jTFNroTimbrado;
    private javax.swing.JTextField jTFTipoComprob;
    // End of variables declaration//GEN-END:variables
}
