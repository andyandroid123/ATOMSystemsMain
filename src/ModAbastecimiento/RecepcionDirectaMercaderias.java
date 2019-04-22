/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento;

import controls.EmpresaCtrl;
import controls.LocalCtrl;
import controls.SectorCtrl;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.StatementManager;
import utiles.Utiles;
import views.busca.BuscaArticuloFiltro;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class RecepcionDirectaMercaderias extends javax.swing.JDialog {

    String codEmpresa, codLocal, codSector, codArticulo;
    String fecVigencia = "";
    boolean esModificacion;
    DefaultTableModel dtmDetallesRecepcion;
    int filaExistente;
    
    // IMPRESION
    public static JTextField jTPrinterSelected = new javax.swing.JTextField();
    public static JTextField jTCPYDETECT = new javax.swing.JTextField();
    
    public RecepcionDirectaMercaderias(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        codEmpresa = utiles.Utiles.getCodEmpresaDefault();
        codLocal = utiles.Utiles.getCodLocalDefault(codEmpresa);
        codSector = utiles.Utiles.getCodSectorDefault(codLocal);
        jTFCodProveedor.requestFocus();
        getFecVigencia();
        configCampos();
        configTabla();
        llenarCampos();
        limpiarTabla();
        cerrarVentana();
    }
    
    private void limpiarCampos(){
        jTFRazonSocProveedor.setText("");
        jTFRucProveedor.setText("");
        jTFNroRecepcion.setText("");
        jTFCodArticulo.setText("");
        jTFCantidadRecibida.setText("");
        jTFEmpaque.setText("");
        jTFMensajes.setText("");
        jTFDescripcionArticulo.setText("");
    }
    
    private void componentesGrabado(){
        limpiarTabla();
        limpiarCampos();
        jBGrabar.setText("Nuevo");
        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo48.png")));
    }
    
    private void componentesNuevo(){
        jTFCodProveedor.requestFocus();
        getSecuenciaRecepcion();
        jBGrabar.setText("Grabar");
        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check48.png")));
    }
    
    private void limpiarTabla(){
        int nroFilas = dtmDetallesRecepcion.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesRecepcion.removeRow(i);
            nroFilas = dtmDetallesRecepcion.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesRecepcion.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetallesRecepcion.setModel(dtmDetallesRecepcion);
    }
    
    private void configCampos(){
        jTFCodEmpresa.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodLocal.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodSector.setDocument(new MaxLength(6, "", "ENTERO"));
        
        jTFFechaRecepcion.setInputVerifier(new FechaInputVerifier(jTFFechaRecepcion));
        jTFCodEmpresa.addFocusListener(new Focus());
        jTFCodLocal.addFocusListener(new Focus());
        jTFCodSector.addFocusListener(new Focus());
        jTFCodProveedor.addFocusListener(new Focus());
        jTFFechaRecepcion.addFocusListener(new Focus());
        jTFNroRecepcion.addFocusListener(new Focus());
        jTFCodArticulo.addFocusListener(new Focus());
        jTFCantidadRecibida.addFocusListener(new Focus());
    }
    
    private void llenarCampos(){
        jTFCodEmpresa.setText(codEmpresa);
        jTFCodLocal.setText(codLocal);
        jTFCodSector.setText(codSector);
        jTFCodProveedor.setText("1");
        jTFFechaRecepcion.setText(fecVigencia);
        jTFDescEmpresa.setText(getDescripcionEmpresa(jTFCodEmpresa.getText()));
        jTFDescLocal.setText(getDescripcionLocal(jTFCodLocal.getText()));
        jTFDescSector.setText(getDescripcionSector(jTFCodSector.getText()));
        jTFSerie.setText("A");
        jTFCodProveedor.requestFocus();
        getSecuenciaRecepcion();
    }
    
    private void getSecuenciaRecepcion(){
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('seq_recepcion_directa_mercaderias') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    jTFNroRecepcion.setText(TheQuery.TheResultSet.getString(1));
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException sqlex) {
                JOptionPane.showMessageDialog(this, "Error en la Obtencion de Secuencia! ", "ATENCION", JOptionPane.WARNING_MESSAGE);
                InfoErrores.errores(sqlex);
            }finally
            {
                TheQuery.CerrarStatement();
                TheQuery.ClearDBManagerstmt();
                TheQuery = null;
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane.showMessageDialog(RecepcionDirectaMercaderias.this, "Clic en el botón CANCELAR!", "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private String getDescripcionEmpresa(String codigo){
        String result = "";
        if(Utiles.isIntegerValid(codigo)){
            EmpresaCtrl ctrl = new EmpresaCtrl();
            result = ctrl.getDescripcionEmpresa(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private String getDescripcionLocal(String codigo){
        String result = "";
        if(Utiles.isIntegerValid(codigo)){
            LocalCtrl ctrl = new LocalCtrl();
            result = ctrl.getDescripcionLocal(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private String getDescripcionSector(String codigo){
        String result = "";
        if(Utiles.isIntegerValid(codigo)){
            SectorCtrl ctrl = new SectorCtrl();
            result = ctrl.getDescripcionSector(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
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
                String sql = "SELECT razon_soc, ruc_proveedor FROM proveedor WHERE cod_proveedor = " + codigo;
                System.out.println("SQL Proveedor: " + sql);
                ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFRazonSocProveedor.setText(rs.getString("razon_soc"));
                    jTFRucProveedor.setText(rs.getString("ruc_proveedor"));
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
    
    private void salirDelModulo()
    {
        int exit = JOptionPane.showConfirmDialog(this, "Desea salir del módulo?", "Salir del Módulo", JOptionPane.YES_NO_OPTION);
        if(exit == 0){
            this.dispose();
        }
    }
    
    private void cargaDatosArticulo(String codigo){
        
        if(articuloPerteneceAProveedor(codigo)){
            getDatosArticulo(codigo);
            jTFMensajes.setText("");
            
            jTFCantidadRecibida.requestFocus();
            if(dtmDetallesRecepcion.getRowCount() > 0){
                articuloExisteEnDetalle(codArticulo);
            } 
        }else{
            jTFCodArticulo.requestFocus();
            jTFCodArticulo.selectAll();
            jTFMensajes.setText("ARTICULO NO CORRESPONDE A PROVEEDOR!");
            jTFEmpaque.setText("");
            jTFDescripcionArticulo.setText("");
        }
        
        
    }
    
    private void getDatosArticulo(String codigo){
        try{
            String sqlDescripcion = "SELECT descripcion FROM articulo WHERE cod_articulo = " + codigo;
            String sqlEmpaque = "SELECT sigla_compra FROM costoart WHERE cod_articulo = " + codigo + " ORDER BY fec_vigencia DESC LIMIT 1";
            ResultSet rs1, rs2;
            rs1 = DBManager.ejecutarDSL(sqlDescripcion);
            rs2 = DBManager.ejecutarDSL(sqlEmpaque);
            if(rs1 != null){
                if(rs1.next()){
                    jTFDescripcionArticulo.setText(rs1.getString("descripcion"));
                }
            }
            
            if(rs2 != null){
                if(rs2.next()){
                    jTFEmpaque.setText(rs2.getString("sigla_compra"));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private boolean articuloPerteneceAProveedor(String codigo){
        boolean result = false;
        int codProveedor = 0;
        try{
            String sql = "SELECT cod_proveedor FROM articulo WHERE cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codProveedor = rs.getInt("cod_proveedor");
                }
            }
            if(codProveedor == Integer.parseInt(jTFCodProveedor.getText())){
                result = true;
            }else{
                result = false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private int getCodArticulo(String codigo){
        int result = 0;
        try{
            String sql = "SELECT articulo.cod_articulo FROM articulo INNER JOIN barras ON articulo.cod_articulo = barras.cod_articulo "
                       + "WHERE barras.cod_barras = '" + codigo + "' OR articulo.cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_articulo");
                }else{
                    result = Integer.parseInt(codigo);
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
        long vCodArticulo;
        String vDescArticulo;
        float vCantidadRecibida;
        String vEmpaqueArticulo;
        
        vCodArticulo = Long.parseLong(jTFCodArticulo.getText().trim());
        vDescArticulo = jTFDescripcionArticulo.getText().trim();
        vCantidadRecibida = Float.parseFloat(jTFCantidadRecibida.getText().trim());
        vEmpaqueArticulo = jTFEmpaque.getText();
        if(!articuloExisteEnDetalle(codArticulo)){
            dtmDetallesRecepcion.addRow(new Object[]{new Long(vCodArticulo), new String(vDescArticulo), new Float(vCantidadRecibida), new String(vEmpaqueArticulo)});
            jTDetallesRecepcion.setModel(dtmDetallesRecepcion);
        }else{
            Float vCantidadDetalle = ((Float)dtmDetallesRecepcion.getValueAt(filaExistente, 2)).floatValue();
            dtmDetallesRecepcion.setValueAt(vCantidadDetalle + vCantidadRecibida, filaExistente, 2);
            jTDetallesRecepcion.setModel(dtmDetallesRecepcion);
        }
    }
    
    private void configTabla(){
        dtmDetallesRecepcion = (DefaultTableModel) jTDetallesRecepcion.getModel();
        jTDetallesRecepcion.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTDetallesRecepcion.getColumnModel().getColumn(1).setPreferredWidth(320);
        jTDetallesRecepcion.getColumnModel().getColumn(2).setPreferredWidth(5);
        jTDetallesRecepcion.getColumnModel().getColumn(3).setPreferredWidth(1);
        jTDetallesRecepcion.setRowHeight(20);
    }
    
    private boolean articuloExisteEnDetalle(String codigo){
        boolean result = false;
        esModificacion = false;
        
        int nroFilas = dtmDetallesRecepcion.getRowCount();
        for(int i = 0; i < nroFilas; i++){
            Long codArticuloTabla = ((Long) dtmDetallesRecepcion.getValueAt(i, 0)).longValue();
            String codArticuloTablaStr = codArticuloTabla.toString();
            if(codArticuloTablaStr.equalsIgnoreCase(codigo)){
                result = true;
                esModificacion = true;
                filaExistente = i;
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
        int cantFilasTabla = dtmDetallesRecepcion.getRowCount();
        
        
        boolean resultLectura = false; //SomeTroublePedDet
        boolean resultOperacionGrabaRecepcion = true; //ResultOKOperatSave
        boolean resultRecepcion = false; //SomeTroubleRecep
        boolean resultHistMovimiento = false; //SomeTroubleHisArt
        boolean resultStockArticulo = false; //SomeTroubleStock
        boolean problemFound = false;
        boolean existeStockArticulo = false; 
        
        //DATOS PARA GRABAR
        long codArticuloTabla = 0;
        String codBarrasTabla = "";
        String siglaCompraTabla = "";
        int cansiCompraTabla = 0;
        double costoEmpaqueTabla = 0;
        double costoUnitarioTabla = 0;
        double costoUnitarioNeto = 0;
        float cantRecepcionTabla = 0;
        float cantFaltanteTabla = 0; // recepcion directa, cuando se haga recepcion por pedido condicionar
        double costoFleteTabla = 0; // idem
        float pctDescuentoTabla = 0; // idem
        int pctIvaTabla = 0;
        double montoTotalTabla = 0; // se va a usar en la impresion del comprobante de recepcion, no en la iteracion
        int vCodEmpresa = Integer.parseInt(jTFCodEmpresa.getText());
        int vCodLocal = Integer.parseInt(jTFCodLocal.getText());
        int vCodSector = Integer.parseInt(jTFCodSector.getText());
        String vFecRecepcion = "to_timestamp('" + jTFFechaRecepcion.getText() + "', 'DD/MM/YYYY hh24:mi:ss')";
        int vCodUsuario = FormMain.codUsuario;
        
        for(iterador = 0; iterador < cantFilasTabla; iterador++){
            codArticuloTabla = ((Long)dtmDetallesRecepcion.getValueAt(iterador, 0));
            siglaCompraTabla = dtmDetallesRecepcion.getValueAt(iterador, 3).toString();
            codBarrasTabla = getCodBarrasArtTabla(codArticuloTabla, siglaCompraTabla);
            cansiCompraTabla = getCansiCompraTabla(codArticuloTabla, siglaCompraTabla);
            costoEmpaqueTabla = getCostoEmpaqueTabla(codArticuloTabla);
            costoUnitarioTabla = costoEmpaqueTabla / cansiCompraTabla;
            costoUnitarioNeto = costoUnitarioTabla;
            cantRecepcionTabla = ((Float)dtmDetallesRecepcion.getValueAt(iterador, 2));
            pctIvaTabla = getIvaArtTabla(codArticuloTabla);
            montoTotalTabla = costoEmpaqueTabla * cantRecepcionTabla;
            
            // AGREGAR ARTICULO A LA TABLA RECEPCION
            resultOperacionGrabaRecepcion = agregarArticulosRecepcion(vCodEmpresa, vCodLocal, vCodSector, codArticuloTabla, siglaCompraTabla, codBarrasTabla, 
                                                                      cansiCompraTabla, costoEmpaqueTabla, costoUnitarioTabla, costoUnitarioNeto, cantRecepcionTabla, 
                                                                      pctIvaTabla, montoTotalTabla, vFecRecepcion);
            
            if(!resultOperacionGrabaRecepcion){
                JOptionPane.showMessageDialog(this, "ATENCION: Error agregando detalles de recepción!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultRecepcion = true;
                break;
            }
            
            // AGREGAR ARTICULO EN HISTORICO
            resultOperacionGrabaRecepcion = agregarArticulosHistorico(vCodEmpresa, vCodLocal, vCodSector, codArticuloTabla, siglaCompraTabla, 
                                                                      cansiCompraTabla, cantRecepcionTabla, vFecRecepcion);
            
            if(!resultOperacionGrabaRecepcion){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al agregar artículo en histórico de movimientos!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultRecepcion = true;
                resultHistMovimiento = true;
                break;
            }
            
            // AGREGAR - ACTUALIZAR STOCK
            
            if(!resultRecepcion){
                existeStockArticulo = verExistenciaStock(codArticuloTabla, vCodEmpresa, vCodLocal, vCodSector);
                if(existeStockArticulo){
                    resultOperacionGrabaRecepcion = actualizarStock(vCodEmpresa, vCodLocal, vCodSector, codArticuloTabla, cansiCompraTabla, 
                                                                    cantRecepcionTabla);
                }else{
                    resultOperacionGrabaRecepcion = agregarNuevoStock(vCodEmpresa, vCodLocal, vCodSector, codArticuloTabla, cansiCompraTabla, 
                                                                      cantRecepcionTabla, vCodUsuario, costoUnitarioNeto);
                }
                if(!resultOperacionGrabaRecepcion){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar stock!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultStockArticulo = true;
                    break;
                }
            }
        }
        
        if(resultLectura || resultRecepcion || resultStockArticulo || resultHistMovimiento){
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
    
    private boolean agregarNuevoStock(int agCodEmpresa, int agCodLocal, int agCodSector, long agCodArticulo, int agCansi, float agCantRecibida, 
                                      int agCodUsuario, double agCostoNeto){
        float agStock = agCansi * agCantRecibida;
        double agCostoPromedio = agCostoNeto / agCansi;
        
        String sql = "INSERT INTO stockart (cod_empresa, cod_local, cod_articulo, stock, giro_dia, fec_ultcompra, "
                   + "fec_vigencia, cod_usuario, cod_sector, costo_promedio_un) VALUES (" + agCodEmpresa + ", " + agCodLocal + ", "+ agCodArticulo + ", " + agStock + ", 0.01, 'now()', "
                   + "'now()', " + agCodUsuario + ", " + agCodSector + ", " + agCostoPromedio + ")";
        return (grabarPrevioCommit(sql));
    }
    
    private boolean actualizarStock(int aCodEmpresa, int aCodLocal, int aCodSector, long aCodArticulo, int aCansi, float aCantRecibida){
        float cantRecibida = aCansi * aCantRecibida;
        String sql = "UPDATE stockart SET stock = stock + " + cantRecibida + ", fec_ultcompra = 'now()', fec_vigencia = 'now()' "
                   + "WHERE cod_empresa = " + aCodEmpresa + " AND cod_local = " + aCodLocal + " AND cod_sector = " + aCodSector + " "
                   + "AND cod_articulo = " +  aCodArticulo;
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
    
    private boolean agregarArticulosHistorico(int hCodEmpresa, int hCodLocal, int hCodSector, long hCodArticulo, String hSigla, int hCansi, 
                                              float hCantRecibida, String hFecMovimiento){
        
        String hTipoMovimiento = "ENT"; // Entrada
        String hTipoComprobante = "RMP";
        int hNroComprob = Integer.parseInt(jTFNroRecepcion.getText());
        
        String sql = "INSERT INTO hismovi_articulo (cod_empresa, cod_local, cod_sector, cod_articulo, sigla_venta, cansi_venta, cantidad, "
                   + "tipo_movimiento, tip_comprob, fec_movimiento, fec_vigencia, estado, cod_traspaso, nro_comprob) "
                   + "VALUES (" + hCodEmpresa + ", " + hCodLocal + ", " + hCodSector + ", " + hCodArticulo + ", '" + hSigla + "', "
                   + "" + hCansi + ", " + hCantRecibida + ", '" + hTipoMovimiento + "', '" + hTipoComprobante + "', " + hFecMovimiento + ", 'now()', "
                   + "'V', 0, " + hNroComprob + ")";
        System.out.println("INSERT HISMOVI-ARTICULO: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean agregarArticulosRecepcion(int vCodEmpresa, int vCodLocal, int vCodSector, long codArticulo, String sigla, String barras, 
                                              int cansi, double costo, double costoUnitario, double costoUnitarioNeto, float cantidadRecibida, 
                                              int iva, double montoTotal, String vFecRecepcion){
        
        int vNroPedido = 0;
        int vCantFaltante = 0;
        int vCostoFlete = 0;
        int vPctDcto = 0;
        String vSeriePedido = jTFSerie.getText();
        int vCodProveedor = Integer.parseInt(jTFCodProveedor.getText());
        String vEstado = "V";
        int vCodUsuario = FormMain.codUsuario;
        String vFecVigencia = "now()";
        int vSecuencia = 1;
        String vFacturado = "N";
        int vNroComprob = Integer.parseInt(jTFNroRecepcion.getText());
        
        String sql = "INSERT INTO recepcion_ped (cod_empresa, cod_local, cod_sector, nro_pedido, serie_pedido, cod_articulo, cod_barras, cod_proveedor, "
                   + "sigla_compra, cansi_compra, costo_empaque, costo_unitario, costo_unitario_neto, cant_recepcion, cant_faltante, costo_flete, "
                   + "pct_dcto, pct_iva, mon_total, estado, cod_usuario, fec_vigencia, secuencia, fec_recepcion, facturado, nro_comprob) "
                   + "VALUES (" + vCodEmpresa + ", " + vCodLocal + ", " + vCodSector + ", " + vNroPedido + ", '" + vSeriePedido + "', " + codArticulo + ", '"
                   + "" + barras + "', " + vCodProveedor + ", '" + sigla + "', " + cansi + ", " + costo + ", " + costoUnitario + ", " + costoUnitarioNeto + ", "
                   + "" + cantidadRecibida + ", " + vCantFaltante + ", " + vCostoFlete + ", " + vPctDcto + ", " + iva + ", " + montoTotal + ", '"
                   + "" + vEstado + "', " + vCodUsuario + ", '" + vFecVigencia + "', " + vSecuencia + ", " + vFecRecepcion + ", '" + vFacturado + "', "
                   + "" + vNroComprob + ")";
        System.out.println("INSERT RECEPCION: " + sql);
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
    
    private int getIvaArtTabla(long codArticulo){
        int iva = 0;
        try{
            String sql = "SELECT pct_iva FROM articulo WHERE cod_articulo = " + codArticulo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    iva = rs.getInt("pct_iva");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return iva;
    }
    
    private double getCostoEmpaqueTabla(long codArticulo){
        double costo = 0;
        try{
            String sql = "SELECT costo_neto FROM costoart WHERE cod_articulo = " + codArticulo + " AND vigente = 'S'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    costo = rs.getDouble("costo_neto");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return costo;
    }
    
    private int getCansiCompraTabla(long codArticulo, String empaque){
        int cansi = 0;
        try{
            String sql = "SELECT cansi_compra FROM costoart WHERE cod_articulo = " + codArticulo + " AND sigla_compra = '" + empaque + "'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    cansi = rs.getInt("cansi_compra");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return cansi;
    }
    
    private String getCodBarrasArtTabla(long codArticulo, String empaque){
        String barras = "";
        try{
            String sqlBarras = "SELECT cod_barras FROM barras WHERE cod_articulo = " + codArticulo + " AND sigla_venta = '" + empaque + "' "
                             + "AND vigente = 'S'";
            ResultSet rs = DBManager.ejecutarDSL(sqlBarras);
            if(rs != null){
                if(rs.next()){
                    barras = rs.getString("cod_barras");
                }else{
                    barras = "";
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTFCodProveedor = new javax.swing.JTextField();
        jTFRazonSocProveedor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFRucProveedor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFFechaRecepcion = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFNroRecepcion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFSerie = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodEmpresa = new javax.swing.JTextField();
        jTFDescEmpresa = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFCodLocal = new javax.swing.JTextField();
        jTFDescLocal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFCodSector = new javax.swing.JTextField();
        jTFDescSector = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jTFCodArticulo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFCantidadRecibida = new javax.swing.JTextField();
        jTFEmpaque = new javax.swing.JTextField();
        jTFMensajes = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTFDescripcionArticulo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesRecepcion = new javax.swing.JTable();
        jLCodArticulo = new javax.swing.JLabel();
        jBCancelar = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jLMensajes = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Recepción Directa de Mercaderías");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Recepción"));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Proveedor:");

        jTFCodProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        jTFRazonSocProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("RUC:");

        jTFRucProveedor.setEditable(false);
        jTFRucProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Fecha recepción:");

        jTFFechaRecepcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFechaRecepcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFechaRecepcionFocusGained(evt);
            }
        });
        jTFFechaRecepcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFechaRecepcionKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Nro. Recepción:");

        jTFNroRecepcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroRecepcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroRecepcionFocusGained(evt);
            }
        });
        jTFNroRecepcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroRecepcionKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Serie:");

        jTFSerie.setEditable(false);
        jTFSerie.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFRucProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFFechaRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFRazonSocProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTFRucProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFFechaRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTFNroRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTFSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Empresa:");

        jTFCodEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodEmpresa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodEmpresaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodEmpresaFocusLost(evt);
            }
        });
        jTFCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodEmpresaKeyPressed(evt);
            }
        });

        jTFDescEmpresa.setEditable(false);
        jTFDescEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Local:");

        jTFCodLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodLocal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodLocalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodLocalFocusLost(evt);
            }
        });
        jTFCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodLocalKeyPressed(evt);
            }
        });

        jTFDescLocal.setEditable(false);
        jTFDescLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Sector:");

        jTFCodSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodSector.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodSectorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodSectorFocusLost(evt);
            }
        });
        jTFCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodSectorKeyPressed(evt);
            }
        });

        jTFDescSector.setEditable(false);
        jTFDescSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFDescEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTFDescLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFDescSector, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTFCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Detalles"));

        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCodArticulo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("ARTICULO:");

        jTFCantidadRecibida.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCantidadRecibida.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCantidadRecibida.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantidadRecibidaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCantidadRecibidaFocusLost(evt);
            }
        });
        jTFCantidadRecibida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantidadRecibidaKeyPressed(evt);
            }
        });

        jTFEmpaque.setEditable(false);
        jTFEmpaque.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFEmpaque.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTFMensajes.setEditable(false);
        jTFMensajes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMensajes.setForeground(new java.awt.Color(255, 0, 0));
        jTFMensajes.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("CANT RECIBIDA:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("EMPAQUE:");

        jTFDescripcionArticulo.setEditable(false);
        jTFDescripcionArticulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jScrollPane1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jScrollPane1FocusGained(evt);
            }
        });
        jScrollPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane1KeyPressed(evt);
            }
        });

        jTDetallesRecepcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTDetallesRecepcion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Cant. Recibida", "Empaq."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetallesRecepcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDetallesRecepcionFocusGained(evt);
            }
        });
        jTDetallesRecepcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallesRecepcionKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetallesRecepcion);

        jLCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLCodArticulo.setForeground(new java.awt.Color(51, 51, 255));
        jLCodArticulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLCodArticulo.setText("***");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jTFDescripcionArticulo, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLCodArticulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFCantidadRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTFEmpaque, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLCodArticulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFCantidadRecibida, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jTFEmpaque)
                    .addComponent(jTFMensajes)
                    .addComponent(jTFCodArticulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFDescripcionArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar48.png"))); // NOI18N
        jBCancelar.setText("Cancelar / Salir");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check48.png"))); // NOI18N
        jBGrabar.setText("Grabar");
        jBGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarActionPerformed(evt);
            }
        });

        jLMensajes.setBackground(new java.awt.Color(51, 51, 255));
        jLMensajes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLMensajes.setForeground(new java.awt.Color(51, 51, 255));
        jLMensajes.setText(">>Para consultas presione F1<<");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBGrabar))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 960, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBCancelar)
                            .addComponent(jBGrabar)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLMensajes)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBGrabar});

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

        setSize(new java.awt.Dimension(996, 792));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        salirDelModulo();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodEmpresaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpresaFocusGained
        jTFCodEmpresa.selectAll();
    }//GEN-LAST:event_jTFCodEmpresaFocusGained

    private void jTFCodEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpresaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodLocal.grabFocus();
        }
    }//GEN-LAST:event_jTFCodEmpresaKeyPressed

    private void jTFCodLocalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodLocalFocusGained
        jTFCodLocal.selectAll();
    }//GEN-LAST:event_jTFCodLocalFocusGained

    private void jTFCodLocalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodLocalKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodSector.grabFocus();
        }
    }//GEN-LAST:event_jTFCodLocalKeyPressed

    private void jTFCodSectorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodSectorFocusGained
        jTFCodSector.selectAll();
    }//GEN-LAST:event_jTFCodSectorFocusGained

    private void jTFCodSectorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodSectorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodProveedor.grabFocus();
        }
    }//GEN-LAST:event_jTFCodSectorKeyPressed

    private void jTFCodProveedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusGained
        jTFCodProveedor.selectAll();
    }//GEN-LAST:event_jTFCodProveedorFocusGained

    private void jTFCodProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodProveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFechaRecepcion.grabFocus();
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

    private void jTFFechaRecepcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFechaRecepcionFocusGained
        jTFFechaRecepcion.selectAll();
    }//GEN-LAST:event_jTFFechaRecepcionFocusGained

    private void jTFFechaRecepcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFechaRecepcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroRecepcion.grabFocus();
        }
    }//GEN-LAST:event_jTFFechaRecepcionKeyPressed

    private void jTFNroRecepcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroRecepcionFocusGained
        jTFNroRecepcion.selectAll();
    }//GEN-LAST:event_jTFNroRecepcionFocusGained

    private void jTFNroRecepcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroRecepcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodArticulo.grabFocus();
        }
    }//GEN-LAST:event_jTFNroRecepcionKeyPressed

    private void jTFCodArticuloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusGained
        jTFCodArticulo.selectAll();
        jLMensajes.setText(">>Para búsqueda de artículos presione F1<<");
    }//GEN-LAST:event_jTFCodArticuloFocusGained

    private void jTFCantidadRecibidaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantidadRecibidaFocusGained
        jTFCantidadRecibida.selectAll();
        if(jTFEmpaque.getText().trim().equals("UN")){
            jTFCantidadRecibida.setDocument(new MaxLength(12, "", "ENTERO"));
        }else if(!jTFEmpaque.getText().trim().equals("UN")){
            jTFCantidadRecibida.setDocument(new MaxLength(12, "", "FLOAT"));
        }
    }//GEN-LAST:event_jTFCantidadRecibidaFocusGained

    private void jTFCodEmpresaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpresaFocusLost
        if(jTFCodEmpresa.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodEmpresa.requestFocus();
        }else{
            jTFDescEmpresa.setText(getDescripcionEmpresa(jTFCodEmpresa.getText()));
        }
    }//GEN-LAST:event_jTFCodEmpresaFocusLost

    private void jTFCodLocalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodLocalFocusLost
        if(jTFCodLocal.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodLocal.requestFocus();
        }else{
            jTFDescLocal.setText(getDescripcionLocal(jTFCodLocal.getText()));
        }
    }//GEN-LAST:event_jTFCodLocalFocusLost

    private void jTFCodSectorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodSectorFocusLost
        if(jTFCodSector.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodSector.requestFocus();
        }else{
            jTFDescSector.setText(getDescripcionSector(jTFCodSector.getText()));
        }
    }//GEN-LAST:event_jTFCodSectorFocusLost

    private void jTFCodProveedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusLost
        if(jTFCodProveedor.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El campo no puede quedar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTFCodProveedor.requestFocus();
        }else{
            completarDatosProveedor(jTFCodProveedor.getText());
        }
    }//GEN-LAST:event_jTFCodProveedorFocusLost

    private void jTFCodArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodArticuloKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            
            if(!jTFCodArticulo.getText().trim().equals("")){
                
                codArticulo = String.valueOf(getCodArticulo(jTFCodArticulo.getText().trim()));
                jTFCodArticulo.setText(codArticulo);
                jLCodArticulo.setText("(" + jTFCodArticulo.getText() + ")");
                codArticulo = jTFCodArticulo.getText().trim();
                cargaDatosArticulo(codArticulo);
                
                /*jTFCantidadRecibida.requestFocus();
                if(dtmDetallesRecepcion.getRowCount() > 0){
                    articuloExisteEnDetalle(codArticulo);
                }*/                
            }else{

                jTFCodArticulo.requestFocus();
                jTFMensajes.setText("INGRESE UN CODIGO DE ARTICULO!");
                
                dtmDetallesRecepcion = (DefaultTableModel) jTDetallesRecepcion.getModel();
                int nroFilas = dtmDetallesRecepcion.getRowCount();
                if(nroFilas > 0){
                    jTDetallesRecepcion.setRowSelectionInterval(0, 0);
                    jTDetallesRecepcion.changeSelection(0, 2, false, false);
                    jTDetallesRecepcion.requestFocus();
                }else{
                    evt.consume();
                }
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try{
            BuscaArticuloFiltro busquedaArt = new BuscaArticuloFiltro(new JFrame(), true);
            busquedaArt.pack();
            busquedaArt.dConsultas("articulo", "descripcion", "cod_articulo", "descripcion", "precio_venta", "Empaque", "Código", "Descripción", "Precio de Venta", "Empaque");
            busquedaArt.jTFCodProveedor.setText(jTFCodProveedor.getText());
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

    private void jTFCodArticuloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusLost
        /*if(jTFCodArticulo.getText().equals("")){
            jTFCodArticulo.requestFocus();
            jTFMensajes.setText("INGRESE UN CODIGO DE ARTICULO!");
        }else{
            codArticulo = String.valueOf(getCodArticulo(jTFCodArticulo.getText().trim()));
            jTFCodArticulo.setText(codArticulo);
            jLCodArticulo.setText("(" + jTFCodArticulo.getText() + ")");
            codArticulo = jTFCodArticulo.getText().trim();
            cargaDatosArticulo(codArticulo);
        }*/        
    }//GEN-LAST:event_jTFCodArticuloFocusLost

    private void jTFCantidadRecibidaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantidadRecibidaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jLMensajes.setText(">>ATAJO: Para grabar e imprimir registro, presione F10<<");
            if(!jTFCantidadRecibida.getText().equals("") && !jTFCantidadRecibida.getText().equals("0") && !jTFDescripcionArticulo.getText().equals("")){
                jTFCodArticulo.requestFocus();
                jTFMensajes.setText("");
                addArticuloDetalle();
                codArticulo = "";
            }else {
                jTFMensajes.setText("DEBE INGRESAR LA CANTIDAD RECIBIDA!");
                jTFCantidadRecibida.setText("0");
                jTFCantidadRecibida.selectAll();
            }
            
            if(jTFCantidadRecibida.getText().equals("0")){
                jTFMensajes.setText("DEBE INGRESAR LA CANTIDAD RECIBIDA!");
                jTFCantidadRecibida.setText("0");
                jTFCantidadRecibida.selectAll();
            }
        }
    }//GEN-LAST:event_jTFCantidadRecibidaKeyPressed

    private void jTFCantidadRecibidaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantidadRecibidaFocusLost
        if(jTFCantidadRecibida.getText().equals("")){
            jTFMensajes.setText("DEBE INGRESAR LA CANTIDAD RECIBIDA!");
            jTFCantidadRecibida.selectAll();
        }
    }//GEN-LAST:event_jTFCantidadRecibidaFocusLost

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        
        if(!jBGrabar.getText().trim().equals("Grabar")){
            componentesNuevo();
        }else{
            if(dtmDetallesRecepcion.getRowCount() > 0){
                if(grabarDatos()){
                    JOptionPane.showMessageDialog(this,"ATENCION: Datos grabados correctamente!", "EXITO",  JOptionPane.INFORMATION_MESSAGE);
                    int question = JOptionPane.showConfirmDialog(this, "¿Desea imprimir el resumen?", "Impresión", JOptionPane.YES_NO_OPTION);
                    if(question == 0){
                        imprimirRecepcion();
                    }
                    componentesGrabado();
                }else{
                    JOptionPane.showMessageDialog(this,"ATENCION: Error al grabando Recepción!", "ATENCION",  JOptionPane.WARNING_MESSAGE);
                    jTFCodArticulo.grabFocus();
                }
            }
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jScrollPane1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jScrollPane1FocusGained
        jLMensajes.setText(">>ELIMINAR: seleccione el articulo y presione DELETE<<");
    }//GEN-LAST:event_jScrollPane1FocusGained

    private void jTDetallesRecepcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDetallesRecepcionFocusGained
        jLMensajes.setText("Para eliminar: seleccione el articulo y presione DELETE.");
    }//GEN-LAST:event_jTDetallesRecepcionFocusGained

    private void jScrollPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1KeyPressed

    private void jTDetallesRecepcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallesRecepcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            int filaSeleccionada = jTDetallesRecepcion.getSelectedRow();
            for(int i = 0; i < jTDetallesRecepcion.getRowCount(); i++){
                if(i == filaSeleccionada){
                    dtmDetallesRecepcion.removeRow(i);
                }
            }
        }
    }//GEN-LAST:event_jTDetallesRecepcionKeyPressed

    private void imprimirRecepcion(){
        String fecActual = utiles.Utiles.getSysDateTimeString();
        String pTipoCopia = "ORIGINAL";
        String pOperador = FormMain.nombreUsuario;
        int pNroRecepcion = Integer.parseInt(jTFNroRecepcion.getText());
        int vMontoTotal = 0;
        
        String sql = "SELECT DISTINCT (CASE WHEN recep.estado = 'V' THEN 'Recibido' WHEN recep.estado = 'A' THEN 'Anulado' ELSE 'No disponible' END) "
                   + "AS estado, to_char(recep.fec_recepcion, 'dd/MM/yyyy hh:mm:ss') AS fecRecepcion, recep.nro_comprob AS nroRecepcion, recep.serie_pedido "
                   + "AS serie, prov.cod_proveedor || ' - ' || prov.razon_soc AS proveedor, prov.ruc_proveedor AS ruc, recep.cod_barras AS barras, "
                   + "recep.cod_articulo AS codArticulo, art.descripcion AS descripcion, recep.sigla_compra AS empaque, recep.cant_recepcion, recep.mon_total "
                   + "FROM recepcion_ped recep "
                   + "INNER JOIN proveedor prov "
                   + "ON recep.cod_proveedor = prov.cod_proveedor "
                   + "INNER JOIN articulo art "
                   + "ON recep.cod_articulo = art.cod_articulo "
                   + "WHERE recep.nro_comprob = " + jTFNroRecepcion.getText().trim() + " "
                   + "ORDER BY recep.cod_articulo";
        
        try{
            
            String sqlMontoTotal = "SELECT sum(mon_total) AS monto FROM recepcion_ped WHERE nro_comprob = " + jTFNroRecepcion.getText().trim() + "";
            ResultSet rs = DBManager.ejecutarDSL(sqlMontoTotal);
            if(rs != null){
                if(rs.next()){
                    vMontoTotal = rs.getInt("monto");
                }
            }
            
            LibReportes.parameters.put("pEmpresa", jTFDescEmpresa.getText());
            LibReportes.parameters.put("pLocal", jTFDescLocal.getText());
            LibReportes.parameters.put("pSector", jTFDescSector.getText());
            LibReportes.parameters.put("pFechaActual", fecActual);
            LibReportes.parameters.put("pOperador", pOperador);
            LibReportes.parameters.put("pTipoCopia", pTipoCopia);
            LibReportes.parameters.put("pNroRecepcion", pNroRecepcion);
            LibReportes.parameters.put("pMontoTotal", vMontoTotal);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "recepcionMercaderias");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
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
            java.util.logging.Logger.getLogger(RecepcionDirectaMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecepcionDirectaMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecepcionDirectaMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecepcionDirectaMercaderias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RecepcionDirectaMercaderias dialog = new RecepcionDirectaMercaderias(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLCodArticulo;
    private javax.swing.JLabel jLMensajes;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesRecepcion;
    private javax.swing.JTextField jTFCantidadRecibida;
    private javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFCodEmpresa;
    private javax.swing.JTextField jTFCodLocal;
    private javax.swing.JTextField jTFCodProveedor;
    private javax.swing.JTextField jTFCodSector;
    private javax.swing.JTextField jTFDescEmpresa;
    private javax.swing.JTextField jTFDescLocal;
    private javax.swing.JTextField jTFDescSector;
    private javax.swing.JTextField jTFDescripcionArticulo;
    private javax.swing.JTextField jTFEmpaque;
    private javax.swing.JTextField jTFFechaRecepcion;
    private javax.swing.JTextField jTFMensajes;
    private javax.swing.JTextField jTFNroRecepcion;
    private javax.swing.JTextField jTFRazonSocProveedor;
    private javax.swing.JTextField jTFRucProveedor;
    private javax.swing.JTextField jTFSerie;
    // End of variables declaration//GEN-END:variables
}
