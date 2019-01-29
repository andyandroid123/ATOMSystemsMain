/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento;

import ModRegistros.Articulos;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JDialog;
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
import utiles.StatementManager;
import views.busca.BuscaArticuloFiltro;

/**
 *
 * @author Andres
 */
public class AjusteStock extends javax.swing.JDialog {

    String fecVigencia = "";
    DefaultTableModel dtmDetallesAjuste;
    String razonSocEmpresa, descLocal, descSector;
    String codEmpresa, codLocal, codSector, codArticulo;
    boolean esModificacion;
    int filaExistente;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    
    public AjusteStock(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        ctrlTipoOperacion();
        getFecVigencia();
        configCampos();
        configTabla();
        llenarCampos();
        limpiarTabla();
        jCBCodEmpresa.requestFocus();
        cerrarVentana();
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane.showMessageDialog(AjusteStock.this, "Clic en el botón CANCELAR!", "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    private void limpiarTabla(){
        int nroFilas = dtmDetallesAjuste.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesAjuste.removeRow(i);
            nroFilas = dtmDetallesAjuste.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesAjuste.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetallesAjuste.setModel(dtmDetallesAjuste);
    }
    
    private void llenarCampos(){
        jTFFechaRegistro.setText(fecVigencia);
        jTFObs.setText("AJUSTE DE STOCK - " + jTFTipoOperacion.getText());
        llenarCombos();
        jTFNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jTFDescLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jTFDescSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
        getSecuenciaAjuste();
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
    }
    
    private void getSecuenciaAjuste(){
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('seq_ajuste_stock') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    jTFNroAjuste.setText(TheQuery.TheResultSet.getString(1));
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
    
    private void configTabla(){
        dtmDetallesAjuste = (DefaultTableModel) jTDetallesAjuste.getModel();
        jTDetallesAjuste.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTDetallesAjuste.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTDetallesAjuste.getColumnModel().getColumn(2).setPreferredWidth(5);
        jTDetallesAjuste.getColumnModel().getColumn(3).setPreferredWidth(5);
        jTDetallesAjuste.getColumnModel().getColumn(4).setPreferredWidth(10);
        jTDetallesAjuste.getColumnModel().getColumn(5).setPreferredWidth(10);
        jTDetallesAjuste.getColumnModel().getColumn(6).setPreferredWidth(10);
        jTDetallesAjuste.getColumnModel().getColumn(7).setPreferredWidth(10);
        jTDetallesAjuste.setRowHeight(20);
    }
    
    private void configCampos(){
        jTFNroAjuste.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFObs.setDocument(new MaxLength(35, "UPPER", "ALFA"));
        jTFCodArticulo.setDocument(new MaxLength(13, "UPPER", "ALFA"));
        jTFFechaRegistro.setInputVerifier(new FechaInputVerifier(jTFFechaRegistro));
        
        jTFNroAjuste.addFocusListener(new Focus());
        jTFFechaRegistro.addFocusListener(new Focus());
        jTFObs.addFocusListener(new Focus());
        jTFCodArticulo.addFocusListener(new Focus());
        jTFCantidadAjuste.addFocusListener(new Focus());
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
    
    private void ctrlTipoOperacion(){
        if(jRBEntrada.isSelected()){
            jTFTipoOperacion.setText("ENTRADA");
            jTFTipoOperacion.setForeground(new Color(0,153,51));
        }else{
            jTFTipoOperacion.setText("SALIDA");
            jTFTipoOperacion.setForeground(Color.red);
        }
    }
    
    private void salirDelModulo()
    {
        int exit = JOptionPane.showConfirmDialog(this, "Desea salir del módulo?", "Salir del Módulo", JOptionPane.YES_NO_OPTION);
        if(exit == 0){
            this.dispose();
        }
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
    
    private void cargaDatosArticulo(String codigo){
        try{
            String sqlDescripcion = "SELECT descripcion FROM articulo WHERE cod_articulo = " + codigo;
            String sqlEmpaque = "SELECT sigla_compra, cansi_compra FROM costoart WHERE cod_articulo = " + codigo + " ORDER BY fec_vigencia DESC LIMIT 1";
            String sqlStock = "SELECT stock FROM stockart WHERE cod_articulo = " + codigo;
            ResultSet rs1, rs2, rs3;
            rs1 = DBManager.ejecutarDSL(sqlDescripcion);
            rs2 = DBManager.ejecutarDSL(sqlEmpaque);
            rs3 = DBManager.ejecutarDSL(sqlStock);
            if(rs1 != null){
                if(rs1.next()){
                    jTFDescArticulo.setText(rs1.getString("descripcion"));
                    jLAvisos.setText("");
                }else{
                    jTFDescArticulo.setText("");
                    jLAvisos.setText("¡CODIGO NO ENCONTRADO!");
                }
            }
            
            if(rs2 != null){
                if(rs2.next()){
                    jTFSiglaVenta.setText(rs2.getString("sigla_compra"));
                    jTFUm.setText(rs2.getString("cansi_compra"));
                }else{
                    jTFSiglaVenta.setText("");
                }
            }
            
            if(rs3 != null){
                if(rs3.next()){
                    jTFStock.setText(decimalFormat.format(rs3.getDouble("stock")));
                }else{
                    jTFStock.setText("0");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private boolean articuloExisteEnDetalle(String codigo){
        boolean result = false;
        esModificacion = false;
        
        int nroFilas = dtmDetallesAjuste.getRowCount();
        for(int i = 0; i < nroFilas; i++){
            Long codArticuloTabla = ((Long) dtmDetallesAjuste.getValueAt(i, 0)).longValue();
            String codArticuloTablaStr = codArticuloTabla.toString();
            if(codArticuloTablaStr.equalsIgnoreCase(codigo)){
                result = true;
                esModificacion = true;
                filaExistente = i;
            }
        }
        return result;
    }
    
    private void addArticuloDetalle(){
        long vCodArticulo = Long.parseLong(jTFCodArticulo.getText().trim());
        String vDescArticulo = jTFDescArticulo.getText().trim();
        String vSiglaVenta = jTFSiglaVenta.getText().trim();
        String vCansi = jTFUm.getText().trim();        
        float vStockActual = Float.parseFloat(jTFStock.getText().trim().replace(",", ""));
        float vCantAjuste = Float.parseFloat(jTFCantidadAjuste.getText().trim().replace(",", ""));
        double vCostoNeto = getCostoNeto(vCodArticulo, vCantAjuste);
        float vStockFinal = 0;
        
        if(jRBEntrada.isSelected()){
            vStockFinal = vStockActual + vCantAjuste;
        }
        
        if(jRBSalida.isSelected()){
            vStockFinal = vStockActual - vCantAjuste;
        }
        
        if(!articuloExisteEnDetalle(codArticulo)){
            dtmDetallesAjuste.addRow(new Object[]{vCodArticulo, vDescArticulo, vSiglaVenta, vCansi, vCostoNeto,
                                     vStockActual, vCantAjuste, vStockFinal});
        }else{
            Float vCantidadDetalle = ((Float)dtmDetallesAjuste.getValueAt(filaExistente, 6)).floatValue();
            if(jRBEntrada.isSelected()){
                dtmDetallesAjuste.setValueAt(vCantidadDetalle + vCantAjuste, filaExistente, 6);
                dtmDetallesAjuste.setValueAt(vStockActual + vCantAjuste + vCantidadDetalle, filaExistente, 7);
            }else{
                dtmDetallesAjuste.setValueAt(vCantidadDetalle + vCantAjuste, filaExistente, 6);
                dtmDetallesAjuste.setValueAt(vStockActual - vCantAjuste - vCantidadDetalle, filaExistente, 7);
            }
            jTDetallesAjuste.setModel(dtmDetallesAjuste);
        }
    }
    
    private double getCostoNeto(long codigo, float cantAjuste){
        double result = 0;
        String sql = "SELECT (costo_neto * " + cantAjuste + ") AS costoNeto FROM costoart WHERE cod_articulo = " + codigo + " AND vigente = 'S'";
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getDouble("costoNeto");
                    System.out.println("COSTO NETO RECUPERADO PARA EL ARTICULO: " + codigo );
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
    
    private void componentesGrabado(){
        limpiarTabla();
        limpiarCampos();
        jBGrabar.setText("Nuevo");
        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo48.png")));
    }
    
    private void componentesNuevo(){
        jTFNroAjuste.requestFocus();
        getSecuenciaAjuste();
        jRBEntrada.setEnabled(true);
        jRBSalida.setEnabled(true);
        jBGrabar.setText("Grabar");
        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check48.png")));
    }
    
    private void limpiarCampos(){
        jTFCodArticulo.setText("");
        jTFCantidadAjuste.setText("");
        jTFSiglaVenta.setText("");
        jTFUm.setText("");
        jLAvisos.setText("");
        jTFDescArticulo.setText("");
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
        int cantidadFilasTabla = dtmDetallesAjuste.getRowCount();
        
        boolean existeStockArticulo = false; 
        boolean resultStockArticulo = false;
        boolean problemFound = false;
        boolean resultHistMovimiento = false;
        boolean resultTraspasoCab = false;
        boolean resultTraspasoDet = false;
        boolean resultOperacionGrabaAjuste = true;
                
        // DATOS A GRABAR
        int vCodEmpresa = Integer.parseInt(jCBCodEmpresa.getSelectedItem().toString());
        int vCodLocal = Integer.parseInt(jCBCodLocal.getSelectedItem().toString());
        int vCodSector = Integer.parseInt(jCBCodSector.getSelectedItem().toString());
        int vCodUsuario = FormMain.codUsuario;
        String vFecAjuste = "to_timestamp('" + jTFFechaRegistro.getText() + "', 'DD/MM/YYYY hh24:mi:ss')";
        int vNroAjuste = Integer.parseInt(jTFNroAjuste.getText().trim());
        String vObs = jTFObs.getText().trim();
        String hTipoMovimiento = ""; // Entrada o salida
        int vCodMotivoTraspaso = 0;
        // cod_motivo_traspaso 4 ENT, 5 SAL
        
        if(jRBEntrada.isSelected()){
            hTipoMovimiento = "ENT";
            vCodMotivoTraspaso = 4;
        }
        
        if(jRBSalida.isSelected()){
            hTipoMovimiento = "SAL";
            vCodMotivoTraspaso = 5;
        }
        
        double vCostoEmpaqueTabla = 0;
        double vCostoUnitarioTabla = 0;
        double vCostoUnitarioNeto = 0;
        long vCodArticuloTabla = 0;
        float vStockTabla = 0;
        float vCantAjuste = 0;
        int vCansiEmpaque = 0;
        String vEmpaque = "";
        
        //TRASPASO CABECERA
        resultOperacionGrabaAjuste = grabarAjusteCab(vCodEmpresa, vCodLocal, vCodSector, vNroAjuste, vCodMotivoTraspaso, hTipoMovimiento, 
                                                     vObs, vCodUsuario, vFecAjuste);

        if(!resultOperacionGrabaAjuste){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar traspaso (ajuste) cab!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultStockArticulo = true;
                resultTraspasoCab = true;
        }
        
        for(iterador = 0; iterador < cantidadFilasTabla; iterador++){
            vCodArticuloTabla = ((Long)dtmDetallesAjuste.getValueAt(iterador, 0));
            vCansiEmpaque = Integer.parseInt(dtmDetallesAjuste.getValueAt(iterador, 3).toString());
            vCantAjuste = ((Float)dtmDetallesAjuste.getValueAt(iterador, 6));
            vCostoEmpaqueTabla = getCostoEmpaqueTabla(vCodArticuloTabla);
            vCostoUnitarioTabla = vCostoEmpaqueTabla / vCansiEmpaque;
            vCostoUnitarioNeto = vCostoUnitarioTabla;
            vStockTabla = ((Float)dtmDetallesAjuste.getValueAt(iterador, 5));
            vEmpaque = dtmDetallesAjuste.getValueAt(iterador, 2).toString();                        
            
            //TRASPASO DETALLE
            resultOperacionGrabaAjuste = grabarAjusteDet(vCodEmpresa, vCodLocal, vCodSector, vNroAjuste, vCodMotivoTraspaso, vCodArticuloTabla, 
                                                         vCansiEmpaque, vEmpaque, vCantAjuste, vCostoUnitarioNeto, vStockTabla, hTipoMovimiento);
            
            if(!resultOperacionGrabaAjuste){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar traspaso (ajuste) detalle!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultStockArticulo = true;
                resultTraspasoDet = true;
            }
            
            // STOCK ARTICULO
            
            existeStockArticulo = verExistenciaStock(vCodArticuloTabla, vCodEmpresa, vCodLocal, vCodSector);
            if(existeStockArticulo){
                resultOperacionGrabaAjuste = actualizarStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticuloTabla, vCansiEmpaque, vCantAjuste);
            }else{
                resultOperacionGrabaAjuste = agregarNuevoStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticuloTabla, vCansiEmpaque, vCantAjuste,
                                                               vCodUsuario, vCostoUnitarioNeto);
            }
            
            if(!resultOperacionGrabaAjuste){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar stock!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultStockArticulo = true;
                    break;
            }
            
            // HISTORICO
            
            if(!resultStockArticulo){
                resultOperacionGrabaAjuste = agregarArticulosHistorico(vCodEmpresa, vCodLocal, vCodSector, vCodArticuloTabla, vEmpaque, vCansiEmpaque, vCantAjuste, vFecAjuste);
                
                if(!resultOperacionGrabaAjuste){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al agregar artículo en histórico de movimientos!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultHistMovimiento = true;
                    break;
                }
            }
            
            if( resultStockArticulo || resultHistMovimiento || resultTraspasoCab || resultTraspasoDet){
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
    
    private boolean grabarAjusteCab(int acCodEmpresa, int acCodLocal, int acCodSector, int acNroTraspaso, int acCodMotivoTraspaso, 
                                    String acTipoTraspaso, String acComentario, int acCodUsuario, String acfecRegistro){
        String sql = "INSERT INTO traspaso_cab (cod_empresa, cod_local, cod_sector, nro_traspaso, fec_traspaso, cod_mot_traspaso, tipo_traspaso, "
                   + "comentario, fec_vigencia, cod_usuario, estado, fec_registro, nro_asiento) "
                   + "VALUES (" + acCodEmpresa + ", "
                   + "" + acCodLocal + ", "
                   + "" + acCodSector + ", "
                   + "" + acNroTraspaso + ", "
                   + "" + acfecRegistro + ", "
                   + "" + acCodMotivoTraspaso + ", '"
                   + "" + acTipoTraspaso + "', '"
                   + "" + acComentario + "', 'now()', "
                   + "" + acCodUsuario + ", 'V', "
                   + "" + acfecRegistro + ", 0)";
        System.out.println("AJUSTE CAB: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarAjusteDet(int adCodEmpresa, int adCodLocal, int adCodSector, int adNroTraspaso, int adCodMotivoTraspaso, long adCodArticulo, 
                                    int adCansiVenta, String adSiglaVenta, float adCantTraspaso, double adCostoNeto, float adStockAnterior, 
                                    String adTipoTraspaso){
        String sql = "INSERT INTO traspaso_det (cod_empresa, cod_local, cod_sector, nro_traspaso, cod_motivo_traspaso, tipo_traspaso, cod_articulo, "
                   + "cansi_venta, sigla_venta, cant_traspaso, costo_neto, costo_promedio, stock_anterior, estado, fec_vigencia)"
                   + "VALUES (" + adCodEmpresa + ", "
                   + "" + adCodLocal + ", "
                   + "" + adCodSector + ", "
                   + "" + adNroTraspaso + ", "
                   + "" + adCodMotivoTraspaso + ", '"
                   + "" + adTipoTraspaso + "', "
                   + "" + adCodArticulo + ", "
                   + "" + adCansiVenta + ", '"
                   + "" + adSiglaVenta + "', "
                   + "" + adCantTraspaso + ", "
                   + "" + adCostoNeto + ", 0, "
                   + "" + adStockAnterior + ", 'V', 'now()')";
        System.out.println("AJUSTE DET: " + sql);
        return (grabarPrevioCommit(sql));
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
    
    private boolean agregarArticulosHistorico(int hCodEmpresa, int hCodLocal, int hCodSector, long hCodArticulo, String hSigla, int hCansi, 
                                              float hCantRecibida, String hFecMovimiento){
        
        String hTipoMovimiento = ""; // Entrada o salida
        String hTipoComprobante = "TRS"; // En tipo_comprobante = TRASPASO DE STOCK
        int hNroComprob = Integer.parseInt(jTFNroAjuste.getText());
        
        if(jRBEntrada.isSelected()){
            hTipoMovimiento = "ENT";
        }
        
        if(jRBSalida.isSelected()){
            hTipoMovimiento = "SAL";
        }
        
        String sql = "INSERT INTO hismovi_articulo (cod_empresa, cod_local, cod_sector, cod_articulo, sigla_venta, cansi_venta, cantidad, "
                   + "tipo_movimiento, tip_comprob, fec_movimiento, fec_vigencia, estado, cod_traspaso, nro_comprob) "
                   + "VALUES (" + hCodEmpresa + ", " + hCodLocal + ", " + hCodSector + ", " + hCodArticulo + ", '" + hSigla + "', "
                   + "" + hCansi + ", " + hCantRecibida + ", '" + hTipoMovimiento + "', '" + hTipoComprobante + "', " + hFecMovimiento + ", 'now()', "
                   + "'V', 0, " + hNroComprob + ")";
        System.out.println("INSERT HISMOVI-ARTICULO: " + sql);
        return (grabarPrevioCommit(sql));
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
    
    private boolean grabarPrevioCommit(String sql){
        boolean result;
        if(DBManager.ejecutarDML(sql) > 0){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
    
    private boolean actualizarStock(int aCodEmpresa, int aCodLocal, int aCodSector, long aCodArticulo, int aCansi, float aCantRecibida){
        float cantRecibida = 0;
        cantRecibida = aCansi * aCantRecibida;
        String sql = "";
        if(jRBEntrada.isSelected()){
            sql = "UPDATE stockart SET stock = stock + " + cantRecibida + ", fec_ultcompra = 'now()', fec_vigencia = 'now()' "
                + "WHERE cod_empresa = " + aCodEmpresa + " AND cod_local = " + aCodLocal + " AND cod_sector = " + aCodSector + " "
                + "AND cod_articulo = " +  aCodArticulo;
        }else{
            sql = "UPDATE stockart SET stock = stock - " + cantRecibida + ", fec_ultcompra = 'now()', fec_vigencia = 'now()' "
                + "WHERE cod_empresa = " + aCodEmpresa + " AND cod_local = " + aCodLocal + " AND cod_sector = " + aCodSector + " "
                + "AND cod_articulo = " +  aCodArticulo;
        }
        
        System.out.println("ACTUALIZAR STOCK: " + sql);
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
    
    private void imprimirAjuste(){
        String fecActual = utiles.Utiles.getSysDateTimeString();
        String pTipoCopia = "ORIGINAL";
        String pOperador = FormMain.nombreUsuario;
        int pNroAjuste = Integer.parseInt(jTFNroAjuste.getText());
        int vMontoTotal = 0;
        
        String sql = "SELECT traspaso_cab.nro_traspaso, traspaso_cab.fec_traspaso::date, traspaso_cab.comentario, " +
                     "traspaso_det.cod_articulo, articulo.descripcion, traspaso_det.sigla_venta, " +
                     "(CASE WHEN traspaso_det.tipo_traspaso = 'ENT' THEN traspaso_det.cant_traspaso ELSE 0 END) AS entrada, " +
                     "(CASE WHEN traspaso_det.tipo_traspaso = 'SAL' THEN traspaso_det.cant_traspaso ELSE 0 END) AS salida, " +
                     "traspaso_det.stock_anterior, traspaso_det.costo_neto, " +
                     "(traspaso_det.cant_traspaso * traspaso_det.costo_neto) AS totalcosto " +
                     "FROM traspaso_cab " +
                     "INNER JOIN traspaso_det " +
                     "ON traspaso_cab.nro_traspaso = traspaso_det.nro_traspaso " +
                     "INNER JOIN articulo " +
                     "ON traspaso_det.cod_articulo = articulo.cod_articulo " +
                     "WHERE traspaso_cab.nro_traspaso = " + pNroAjuste + " " +
                     "ORDER BY traspaso_det.cod_articulo, articulo.descripcion, traspaso_det.fec_vigencia";
        
        try{
            LibReportes.parameters.put("pEmpresa", jTFNombreEmpresa.getText().trim());
            LibReportes.parameters.put("pLocal", jTFDescLocal.getText().trim());
            LibReportes.parameters.put("pSector", jTFDescSector.getText().trim());
            LibReportes.parameters.put("pFechaActual", fecActual);
            LibReportes.parameters.put("pOperador", pOperador);
            LibReportes.parameters.put("pTipoCopia", pTipoCopia);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "ajusteStock");
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jCBCodSector = new javax.swing.JComboBox<>();
        jTFNombreEmpresa = new javax.swing.JTextField();
        jTFDescLocal = new javax.swing.JTextField();
        jTFDescSector = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTFTipoOperacion = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jRBEntrada = new javax.swing.JRadioButton();
        jRBSalida = new javax.swing.JRadioButton();
        jTFNroAjuste = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFFechaRegistro = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFObs = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTFCodArticulo = new javax.swing.JTextField();
        jTFDescArticulo = new javax.swing.JTextField();
        jTFSiglaVenta = new javax.swing.JTextField();
        jTFStock = new javax.swing.JTextField();
        jTFCantidadAjuste = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLMensajes = new javax.swing.JLabel();
        jLAvisos = new javax.swing.JLabel();
        jTFUm = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesAjuste = new javax.swing.JTable();
        jBCancelar = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Ajuste de Stock ");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("AJUSTE DE STOCK");

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
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Sector:");

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

        jTFNombreEmpresa.setEditable(false);
        jTFNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTFDescLocal.setEditable(false);
        jTFDescLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTFDescSector.setEditable(false);
        jTFDescSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescLocal))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescSector)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescSector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Tipo de Ajuste:");

        jTFTipoOperacion.setEditable(false);
        jTFTipoOperacion.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFTipoOperacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFTipoOperacion.setText("OPERACION");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Nro. Ajuste:");

        jRBEntrada.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBEntrada);
        jRBEntrada.setSelected(true);
        jRBEntrada.setText("ENTRADA");
        jRBEntrada.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRBEntradaMouseClicked(evt);
            }
        });
        jRBEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBEntradaActionPerformed(evt);
            }
        });

        jRBSalida.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBSalida);
        jRBSalida.setText("SALIDA");
        jRBSalida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRBSalidaMouseClicked(evt);
            }
        });
        jRBSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSalidaActionPerformed(evt);
            }
        });

        jTFNroAjuste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroAjuste.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroAjuste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroAjusteFocusGained(evt);
            }
        });
        jTFNroAjuste.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroAjusteKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Fecha:");

        jTFFechaRegistro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFechaRegistro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFechaRegistro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFechaRegistroFocusGained(evt);
            }
        });
        jTFFechaRegistro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFechaRegistroKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Obs.:");

        jTFObs.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRBEntrada)
                            .addComponent(jRBSalida)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTFNroAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTFObs))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTFTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTFNroAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jTFFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRBEntrada)
                        .addGap(2, 2, 2)
                        .addComponent(jRBSalida)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFObs, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Código");

        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        });

        jTFDescArticulo.setEditable(false);
        jTFDescArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTFSiglaVenta.setEditable(false);
        jTFSiglaVenta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFSiglaVenta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTFStock.setEditable(false);
        jTFStock.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFStock.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFCantidadAjuste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCantidadAjuste.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCantidadAjuste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantidadAjusteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCantidadAjusteFocusLost(evt);
            }
        });
        jTFCantidadAjuste.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantidadAjusteKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Descripción");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Empaque - U/M");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Stock");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Cant. Ajuste");

        jLMensajes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLMensajes.setForeground(new java.awt.Color(51, 51, 255));
        jLMensajes.setText("***");

        jLAvisos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLAvisos.setForeground(new java.awt.Color(255, 0, 0));
        jLAvisos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jTFUm.setEditable(false);
        jTFUm.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFUm.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel14.setText("/");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addComponent(jLMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTFSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFUm, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFStock, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jTFCantidadAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLAvisos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFStock, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCantidadAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFUm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLAvisos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addComponent(jLMensajes)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTFSiglaVenta, jTFUm});

        jTDetallesAjuste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTDetallesAjuste.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Sigla", "U/M", "Costo Neto", "Stock Actual", "Cantidad", "Stock Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jTDetallesAjuste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDetallesAjusteFocusGained(evt);
            }
        });
        jTDetallesAjuste.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallesAjusteKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetallesAjuste);

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar48.png"))); // NOI18N
        jBCancelar.setText("Cancelar/Salir");
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBGrabar)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBCancelar)
                    .addComponent(jBGrabar))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBGrabar});

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

        setSize(new java.awt.Dimension(960, 691));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jRBEntradaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBEntradaMouseClicked
        
    }//GEN-LAST:event_jRBEntradaMouseClicked

    private void jRBSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSalidaActionPerformed
        jTFTipoOperacion.setText("SALIDA");
        jTFTipoOperacion.setForeground(Color.red);
        jTFObs.setText("AJUSTE DE STOCK - " + jTFTipoOperacion.getText());
    }//GEN-LAST:event_jRBSalidaActionPerformed

    private void jRBSalidaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBSalidaMouseClicked
        
    }//GEN-LAST:event_jRBSalidaMouseClicked

    private void jRBEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBEntradaActionPerformed
        jTFTipoOperacion.setText("ENTRADA");
        jTFTipoOperacion.setForeground(new Color(0,153,51));
        jTFObs.setText("AJUSTE DE STOCK - " + jTFTipoOperacion.getText());
    }//GEN-LAST:event_jRBEntradaActionPerformed

    private void jTFNroAjusteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroAjusteFocusGained
        jTFNroAjuste.selectAll();
    }//GEN-LAST:event_jTFNroAjusteFocusGained

    private void jTFNroAjusteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroAjusteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFechaRegistro.requestFocus();
        }
    }//GEN-LAST:event_jTFNroAjusteKeyPressed

    private void jTFFechaRegistroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFechaRegistroFocusGained
        jTFFechaRegistro.selectAll();
    }//GEN-LAST:event_jTFFechaRegistroFocusGained

    private void jTFFechaRegistroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFechaRegistroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFObs.requestFocus();
        }
    }//GEN-LAST:event_jTFFechaRegistroKeyPressed

    private void jTFObsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFObsFocusGained
        jTFObs.selectAll();
    }//GEN-LAST:event_jTFObsFocusGained

    private void jTFObsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFObsKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodArticulo.requestFocus();
        }
    }//GEN-LAST:event_jTFObsKeyPressed

    private void jTFCodArticuloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusGained
        jTFCodArticulo.selectAll();
        jLMensajes.setText("Para búsqueda de artículos presione F1");
    }//GEN-LAST:event_jTFCodArticuloFocusGained

    private void jTFCodArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodArticuloKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){            
            if(!jTFCodArticulo.getText().trim().equals("") || !jTFCodArticulo.getText().trim().equals("0")){
                if(dtmDetallesAjuste.getRowCount() > 0){
                    articuloExisteEnDetalle(codArticulo);
                }  
                if(!jLAvisos.getText().equals("¡CODIGO NO ENCONTRADO!")){
                    jTFCantidadAjuste.requestFocus();
                }
            }else{
                dtmDetallesAjuste = (DefaultTableModel) jTDetallesAjuste.getModel();
                int nroFilas = dtmDetallesAjuste.getRowCount();
                if(nroFilas > 0){
                    jTDetallesAjuste.setRowSelectionInterval(0, 0);
                    jTDetallesAjuste.changeSelection(0, 2, false, false);
                    jTDetallesAjuste.requestFocus();
                }else{
                    evt.consume();
                }
            }
            jTFCantidadAjuste.requestFocus();
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

    private void jTFCantidadAjusteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantidadAjusteFocusGained
        jTFCantidadAjuste.selectAll();
        String empaque = jTFSiglaVenta.getText().trim();
        int checkEmpaque = empaque.indexOf("UN");
        System.out.println("CHECK EMPAQUE: " + checkEmpaque);
        if(checkEmpaque == 0){
            jTFCantidadAjuste.setDocument(new MaxLength(12, "", "ENTERO"));
        }else {
            jTFCantidadAjuste.setDocument(new MaxLength(12, "", "FLOAT"));
        }
    }//GEN-LAST:event_jTFCantidadAjusteFocusGained

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
            jTFNroAjuste.requestFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        salirDelModulo();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodArticuloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusLost
        if(jTFCodArticulo.getText().equals("") || jTFCodArticulo.getText().equals("0")){
            jTFCodArticulo.requestFocus();
            jLAvisos.setText("¡INGRESE UN CODIGO DE ARTICULO!");
        }else{
            codArticulo = String.valueOf(getCodArticulo(jTFCodArticulo.getText().trim()));
            jTFCodArticulo.setText(codArticulo);
            codArticulo = jTFCodArticulo.getText().trim();
            cargaDatosArticulo(codArticulo);
        }
    }//GEN-LAST:event_jTFCodArticuloFocusLost

    private void jTDetallesAjusteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDetallesAjusteFocusGained
        jLMensajes.setText("Eliminar: DELETE. Consulta Articulos: F2");
    }//GEN-LAST:event_jTDetallesAjusteFocusGained

    private void jTFCantidadAjusteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantidadAjusteFocusLost
        if(jTFCantidadAjuste.getText().equals("")){
            jLAvisos.setText("¡INGRESE CANTIDAD!");
        }
    }//GEN-LAST:event_jTFCantidadAjusteFocusLost

    private void jTFCantidadAjusteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantidadAjusteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCantidadAjuste.getText().equals("") && !jTFCantidadAjuste.getText().equals("0")){
                if(jRBSalida.isSelected() && jTFStock.getText().trim().equals("0")){
                    JOptionPane.showMessageDialog(this, "No se puede ajustar STOCK si el actual es 0", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    jTFCodArticulo.requestFocus();
                }else{
                    jTFCodArticulo.requestFocus();
                    jLAvisos.setText("");
                    addArticuloDetalle();
                    codArticulo = "";
                }
                
                if(dtmDetallesAjuste.getRowCount() > 0){
                    jRBEntrada.setEnabled(false);
                    jRBSalida.setEnabled(false);
                }
                
            }else {
                jLAvisos.setText("¡INGRESE CANTIDAD!");
            }
            
            if(jTFCantidadAjuste.getText().equals("0")){
                jLAvisos.setText("¡INGRESE CANTIDAD!");
            }
        }
    }//GEN-LAST:event_jTFCantidadAjusteKeyPressed

    private void jTDetallesAjusteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallesAjusteKeyPressed
        int filaSeleccionada = jTDetallesAjuste.getSelectedRow();
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            for(int i = 0; i < jTDetallesAjuste.getRowCount(); i++){
                if(i == filaSeleccionada){
                    dtmDetallesAjuste.removeRow(i);
                }
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F2){
            String codigoArticulo = dtmDetallesAjuste.getValueAt(filaSeleccionada, 0).toString();
            Articulos articulos = new Articulos(new JFrame(), true, "CONSULTA", codigoArticulo);
            articulos.pack();
            articulos.setVisible(true);
        }
    }//GEN-LAST:event_jTDetallesAjusteKeyPressed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(!jBGrabar.getText().trim().equals("Grabar")){
            componentesNuevo();
        }else{
            if(dtmDetallesAjuste.getRowCount() > 0){
                if(grabarDatos()){
                    JOptionPane.showMessageDialog(this,"ATENCION: Datos grabados correctamente!",     "EXITO",  JOptionPane.INFORMATION_MESSAGE);
                    int question = JOptionPane.showConfirmDialog(this, "Impresión", "¿Desea imprimir el resumen?", JOptionPane.YES_NO_OPTION);
                    if(question == 0){
                        imprimirAjuste();
                    }
                    componentesGrabado();
                }else{
                    JOptionPane.showMessageDialog(this,"ATENCION: Error grabando Recepción!",     "ATENCION",  JOptionPane.WARNING_MESSAGE);
                    jTFCodArticulo.grabFocus();
                }
            }
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

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
            java.util.logging.Logger.getLogger(AjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AjusteStock dialog = new AjusteStock(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBGrabar;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLAvisos;
    private javax.swing.JLabel jLMensajes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRBEntrada;
    private javax.swing.JRadioButton jRBSalida;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesAjuste;
    private javax.swing.JTextField jTFCantidadAjuste;
    private javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFDescArticulo;
    private javax.swing.JTextField jTFDescLocal;
    private javax.swing.JTextField jTFDescSector;
    private javax.swing.JTextField jTFFechaRegistro;
    private javax.swing.JTextField jTFNombreEmpresa;
    private javax.swing.JTextField jTFNroAjuste;
    private javax.swing.JTextField jTFObs;
    private javax.swing.JTextField jTFSiglaVenta;
    private javax.swing.JTextField jTFStock;
    private javax.swing.JTextField jTFTipoOperacion;
    private javax.swing.JTextField jTFUm;
    // End of variables declaration//GEN-END:variables
}
