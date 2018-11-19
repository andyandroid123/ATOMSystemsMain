/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import principal.FormMain;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.StatementManager;

/**
 *
 * @author Andres
 */
public class PrecioCosto extends javax.swing.JDialog {

    DefaultTableModel dtm = new DefaultTableModel(null, 
                                                  new String[]{"Lista", "Lista Precio", "Sigla", "U/M", "Margen %", "Precio", "Fec Vig", 
                                                               "Usuario"});                                                   
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    String codArticulo;
    double descuento, costoB, costoN, costoU; // Precio de costo
    double margenPVenta, precioVenta; // Precios de venta
    double difCostoVenta; // Para calcular el margen en el caso de que en el margen esté 0 (cero)
    
    /**
     * Creates new form PrecioCosto
     * @param parent
     * @param modal
     * @param codArt
     */
    public PrecioCosto(java.awt.Frame parent, boolean modal, long codArt, String codLista) {
        super(parent, modal);
        initComponents();
        componentesInicio();
        utiles.Utiles.punteroTablaF(jTPrecios, this);        
        jTPrecios.setFont(new Font("Tahoma", 1, 11) );
        jTPrecios.setRowHeight(22);
        configuraTabla();
        cargaCombos();
        codArticulo = String.valueOf(codArt);
        getDescArt(codArt);
        getCostoArt(codArt);
        getPrecioArt(codArt);        
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jLNombreSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
        jTFCodListaCosto.setText(codLista);
        jLListaCosto.setText(utiles.Utiles.getInfoListaPrecio("descripcion", codLista));
        jCBEmpaque.grabFocus();
        cerrarVentana();
    }

    private void cargaCombos(){
        utiles.Utiles.cargaComboEmpresas(jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(jCBCodLocal);
        utiles.Utiles.cargaComboSectores(jCBCodSector);
        cargaComboSigla();
        cargaComboListaVenta();
        cargaComboSiglaVenta();
    }
    
    private void cargaComboSiglaVenta() {         
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "select  sigla from sigla order by descripcion desc";
        TheQuery.EjecutarSql();
        jCBSiglaVenta.removeAllItems();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {  
                    jCBSiglaVenta.addItem(TheQuery.TheResultSet.getString("sigla"));                   
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            DBManager.CerrarStatements();
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
    }
    
    private void cargaComboListaVenta(){
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "select cod_lista from listaprecio";
        TheQuery.EjecutarSql();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {                    
                    jCBListaPrecioVenta.addItem(TheQuery.TheResultSet.getString("cod_lista"));                   
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            DBManager.CerrarStatements();
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
    }
    
    private void cargaComboSigla() {         
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "select  sigla from sigla order by descripcion desc";
        TheQuery.EjecutarSql();
        jCBEmpaque.removeAllItems();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {                    
                    jCBEmpaque.addItem(TheQuery.TheResultSet.getString("sigla"));                   
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            DBManager.CerrarStatements();
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
    }
    
    private void componentesInicio(){
        
        jTFCostoBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCostoNeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCostoUnitario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        
        jTFUMVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMargenVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFPrecioVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    }
    
    private void eliminarPrecioVentaFila(){
        int row = jTPrecios.getSelectedRow();
        
        //dtm.removeRow(row);
    }
    
    private boolean eliminarPrecioVenta(long codArt, int codLista) {
        boolean result = true;
        String sql = "UPDATE preciosart SET vigente = 'N' WHERE cod_articulo = " + codArt + " AND cod_lista = " + codLista + " AND vigente = 'S'";                     
        System.out.println("SCRIPT DE LA OPERACION ELIMINAR: " + sql);        
        try{
            if(DBManager.ejecutarDML(sql) > 0){                
            //getPrecioArt(codArt);
            DBManager.conn.commit();
            JOptionPane.showMessageDialog(this, "ATENCION: El precio fue eliminado!", "OK", JOptionPane.INFORMATION_MESSAGE);
            //jTPrecios.updateUI();
            //Articulos.cargaLineasVenta();
        }else{
            result = false;
            DBManager.conn.rollback();
        }
        }catch(SQLException ex){
            result = false;
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error eliminando precio seleccionado!", "Error", JOptionPane.WARNING_MESSAGE);
        }
        System.out.println("RESULTADO DE LA OPERACION: " + result);
        return result;
    }
    
    private ResultSet rsGetPreciosARt(String codArt){    
        ResultSet rs = null;
        String sql = "SELECT listaprecio.cod_lista, listaprecio.descripcion, preciosart.sigla_venta, preciosart.cansi_venta, " +
                         "preciosart.precio_venta, to_char(preciosart.fec_vigencia, 'dd/MM/yyyy') as fecVigencia, usuario.nombre, " +
                         "preciosart.margen_pct " +
                         "FROM preciosart " +
                         "INNER JOIN listaprecio " +
                         "ON preciosart.cod_lista = listaprecio.cod_lista " +
                         "INNER JOIN usuario " +
                         "ON preciosart.cod_usuario = usuario.cod_usuario " +
                         "WHERE preciosart.vigente = 'S' AND preciosart.cod_articulo = " + codArt;
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private boolean getPrecioArt(long codArt){
        boolean result = true;
        int rows = 0;
        /*try{
            String sql = "SELECT listaprecio.cod_lista, listaprecio.descripcion, preciosart.sigla_venta, preciosart.cansi_venta, " +
                         "preciosart.precio_venta, to_char(preciosart.fec_vigencia, 'dd/MM/yyyy') as fecVigencia, usuario.nombre, " +
                         "preciosart.margen_pct " +
                         "FROM preciosart " +
                         "INNER JOIN listaprecio " +
                         "ON preciosart.cod_lista = listaprecio.cod_lista " +
                         "INNER JOIN usuario " +
                         "ON preciosart.cod_usuario = usuario.cod_usuario " +
                         "WHERE preciosart.vigente = 'S' AND preciosart.cod_articulo = " + codArt;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            //preciosArticulos = new TablePreciosArticulos(rs);
            /*jTPrecios.setModel(new TablePreciosArticulos2(rsGetPreciosARt(codArticulo)));
            jScrollPane1.setViewportView(jTPrecios);
            configuraTabla();
            sorter = new TableRowSorter(jTPrecios.getModel());
            jTPrecios.setRowSorter(sorter);
            jTPrecios.getRowCount();
            if(rows > 0){
                jCBListaPrecioVenta.addItem(jTPrecios.getValueAt(0, 0).toString().trim());
                jLDescListaPrecio.setText(jTPrecios.getValueAt(0, 1).toString().trim());
                jCBSiglaVenta.addItem(jTPrecios.getValueAt(0, 2).toString().trim());
                jTFUMVenta.setText(jTPrecios.getValueAt(0, 3).toString().trim());
                jTFMargenVenta.setText(decimalFormat.format(jTPrecios.getValueAt(0, 4)));
                jTFPrecioVenta.setText(decimalFormat.format(jTPrecios.getValueAt(0, 5)));
            }else{}
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }*/
        StatementManager sm = new StatementManager();
        try{
            String sql = "SELECT listaprecio.cod_lista, listaprecio.descripcion, preciosart.sigla_venta, preciosart.cansi_venta, " +
                         "preciosart.precio_venta, to_char(preciosart.fec_vigencia, 'dd/MM/yyyy') as fecVigencia, usuario.nombre, " +
                         "preciosart.margen_pct " +
                         "FROM preciosart " +
                         "INNER JOIN listaprecio " +
                         "ON preciosart.cod_lista = listaprecio.cod_lista " +
                         "INNER JOIN usuario " +
                         "ON preciosart.cod_usuario = usuario.cod_usuario " +
                         "WHERE preciosart.vigente = 'S' AND preciosart.cod_articulo = " + codArt;
            sm.TheSql = sql;
            sm.EjecutarSql();
            dtm.setRowCount(0);
                    
            if(sm.TheResultSet != null){
                
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[8];
                    row[0] = sm.TheResultSet.getString("cod_lista");
                    row[1] = sm.TheResultSet.getString("descripcion");
                    row[2] = sm.TheResultSet.getString("sigla_venta");
                    row[3] = sm.TheResultSet.getInt("cansi_venta");
                    row[4] = sm.TheResultSet.getDouble("margen_pct");
                    row[5] = sm.TheResultSet.getDouble("precio_venta");
                    row[6] = sm.TheResultSet.getString("fecVigencia");
                    row[7] = sm.TheResultSet.getString("nombre");
                    dtm.addRow(row);
                }
                rows = dtm.getRowCount();
                jTPrecios.updateUI();
                //jTPrecios.grabFocus();
            }else{
                rows = 0;
            }
        }catch(Exception ex){
            result = false;
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            sm.CerrarResultSet();
            sm = null;
        }
        if(rows > 0){
            jCBListaPrecioVenta.setSelectedItem(jTPrecios.getValueAt(0, 0).toString().trim());
            jLDescListaPrecio.setText(jTPrecios.getValueAt(0, 1).toString().trim());
            jCBSiglaVenta.setSelectedItem(jTPrecios.getValueAt(0, 2).toString().trim());
            jTFUMVenta.setText(jTPrecios.getValueAt(0, 3).toString().trim());
            jTFMargenVenta.setText(decimalFormat.format(jTPrecios.getValueAt(0, 4)));
            jTFPrecioVenta.setText(decimalFormat.format(jTPrecios.getValueAt(0, 5)));
        }else{}
        return result;
    }
    
    private boolean getCostoArt(long codArt){
        boolean result = true;
        StatementManager sm = new StatementManager();
        try{
            sm.TheSql = "SELECT sigla_compra, cansi_compra, costo_bruto, costo_neto, costo_promedio, descuento_pct, fec_vigencia "
                      + "FROM costoart WHERE cod_articulo = " + codArt + " AND vigente = 'S' AND cod_lista = 1";
            sm.EjecutarSql();
            if(sm.TheResultSet.next()){
                jTFCostoBruto.setText(decimalFormat.format(sm.TheResultSet.getInt("costo_bruto")));
                jTFDescuento.setText(String.valueOf(sm.TheResultSet.getDouble("descuento_pct")));
                jTFCostoNeto.setText(decimalFormat.format(sm.TheResultSet.getInt("costo_neto")));
                jTFCostoUnitario.setText(decimalFormat.format(sm.TheResultSet.getInt("costo_promedio")));
                jCBEmpaque.setSelectedItem(sm.TheResultSet.getString("sigla_compra"));
                jTFUM.setText(sm.TheResultSet.getString("cansi_compra"));
            }else{}
        }catch(Exception ex){
            result = false;
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            sm.CerrarResultSet();
            sm = null;
        }
        return result;
    }
    
    private boolean getDescArt(long codArt) {
        boolean result = true;
        StatementManager sm = new StatementManager();
        try {
            sm.TheSql = "SELECT descripcion "+
                    " FROM articulo "+
                    " WHERE cod_articulo = " + codArt;
            sm.EjecutarSql();
            if (sm.TheResultSet.next()) {
                jLDescripcion.setText(sm.TheResultSet.getString("descripcion"));
            } else {
                jLDescripcion.setText("INEXISTENTE !!!");
            }
        } catch (Exception ex) {
            result = false;
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            sm.CerrarStatement();
            sm = null;
        }
        return result;
    }
    
    private void formatNumeric(){
        if(jTFCostoUnitario.getText().contains(",")){
        }else{
            jTFCostoUnitario.setText(decimalFormat.format(costoU));
        }
        
        if(jTFCostoBruto.getText().contains(",")){
        }else{
            jTFCostoBruto.setText(decimalFormat.format(costoB));
        }
        
        if(jTFDescuento.getText().contains(",")){
        }else{
            jTFDescuento.setText(decimalFormat.format(descuento));
        }
        
        if(jTFCostoNeto.getText().contains("")){
        }else{
            costoN = Double.parseDouble(jTFCostoNeto.getText());
            jTFCostoNeto.setText(decimalFormat.format(costoN));
        }
        jTFUMVenta.setText(decimalFormat.format(costoU));
        jTFMargenVenta.setText(decimalFormat.format(costoU));
        jTFPrecioVenta.setText(decimalFormat.format(costoU));
    }
    
    private boolean grabaPrecioVentaArticulo(){
        boolean resultOk = false;
        String operacion = "";
        int rows = 0;
        try{
            int cansiVenta = Integer.parseInt(jTFUMVenta.getText().replace(",", ""));
            int listaPVenta = Integer.valueOf(jCBListaPrecioVenta.getSelectedItem().toString());
            String siglaVenta = jCBSiglaVenta.getSelectedItem().toString();
            double precioVenta = Double.parseDouble(jTFPrecioVenta.getText().replace(",", ""));
            double margenPct = Double.parseDouble(jTFMargenVenta.getText());  
            rows = jTPrecios.getRowCount();
            System.out.println("CANTIDAD DE FILAS: " + rows);
            
            if(!(margenPct < 0)){
                
                if(rows == 0){
                operacion = "INSERT";
                if(!setNuevoPrecioVenta(cansiVenta, listaPVenta, siglaVenta, precioVenta, margenPct, "MANTENIMIENTO", operacion)){
                    resultOk = false;
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al Grabar Nuevo Precio de Venta!", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    resultOk = true;
                }
            }else{                
                boolean sameRow = false;
                for(int i= 0; i < rows; i++){
                    if((Integer.parseInt(jCBListaPrecioVenta.getSelectedItem().toString()) == Integer.parseInt(jTPrecios.getValueAt(i, 0).toString())) && 
                        (jCBSiglaVenta.getSelectedItem().toString().equalsIgnoreCase(jTPrecios.getValueAt(i, 2).toString()))){
                        System.out.println("LISTA EN EL COMBO: " + jCBListaPrecioVenta.getSelectedItem().toString());
                        System.out.println("LISTA EN LA TABLA: " + jTPrecios.getValueAt(i, 0).toString());
                        System.out.println("SIGLA EN EL COMBO: " + jCBSiglaVenta.getSelectedItem().toString());
                        System.out.println("SIGLA EN LA TABLA: " + jTPrecios.getValueAt(i, 2).toString());
                        sameRow = true;
                        System.out.println("Samerow: " + sameRow);                        
                    }                                        
                }
                if(sameRow == true){
                        operacion = "UPDATE";
                        System.out.println("TIPO DE OPERACION: " + operacion);
                        if(!setNuevoPrecioVenta(cansiVenta, listaPVenta, siglaVenta, precioVenta, margenPct, "MANTENIMIENTO", operacion)){                                
                            resultOk = false;
                            JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar precio de Venta!", "Error", JOptionPane.WARNING_MESSAGE);
                        }else{
                            resultOk = true;
                        }
                }else{
                    operacion = "INSERT";
                    System.out.println("TIPO DE OPERACION: " + operacion);
                    if(!setNuevoPrecioVenta(cansiVenta, listaPVenta, siglaVenta, precioVenta, margenPct, "MANTENIMIENTO", operacion)){                                
                        resultOk = false;
                        JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar precio de Venta!", "Error", JOptionPane.WARNING_MESSAGE);
                    }else{
                        resultOk = true;
                    }
                }
                }
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: El sistema no permite ingresar precio de venta por debajo del costo!", "Mensaje", JOptionPane.WARNING_MESSAGE);
                jTFPrecioVenta.grabFocus();
            }            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            resultOk = false;
            JOptionPane.showMessageDialog(this, "Error al Grabar Precio de Venta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
        return resultOk;
    }
    
    private boolean setNuevoPrecioVenta(int cansiVenta, int listaVenta, String siglaVenta, double precioVenta, double margenPct, String modulo, 
                                        String operacion){
        boolean resultOk = false;
        String sql;
        try{
            //setPrecioVentaAnteriorInactivo(listaVenta);
            System.out.println("tipo de operacion del setnuevoprecio: " + operacion);
            if(operacion == "INSERT"){
                sql = "INSERT INTO preciosart (cod_lista, cod_articulo, cansi_venta, fec_vigencia, sigla_venta, precio_venta, margen_pct, cod_usuario, "
                                        + "vigente, nom_modulo) "
                                        + "VALUES (" + listaVenta + ", "
                                                     + codArticulo + ", "
                                                     + cansiVenta + ", "
                                                     + "'now()', '"
                                                     + siglaVenta + "', "
                                                     + precioVenta + ", "
                                                     + margenPct + ", "
                                                     + FormMain.codUsuario + ", "
                                                     + "'S', '"
                                                     + modulo + "');";
            }else{
                sql = "UPDATE preciosart SET "
                        + "cansi_venta = " + cansiVenta + ", "
                        + "fec_vigencia = 'now()', "
                        + "sigla_venta = '" + siglaVenta + "', "
                        + "precio_venta = " + precioVenta + ", "
                        + "margen_pct = " + margenPct + ", "
                        + "cod_usuario = " + FormMain.codUsuario + ", "
                        + "vigente = 'S', "
                        + "nom_modulo = '" + modulo + "' "
                        + "WHERE cod_articulo = " + codArticulo + " "
                        + "AND cod_lista = " + listaVenta + " "
                        + "AND sigla_venta = '" + siglaVenta + "' "
                        + "AND vigente = 'S'";
            }
            
            System.out.println("SCRIPT DE LA OPERACION: " + sql);
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
    
    private boolean grabaCostoArticulo(){
        boolean resultOk = false;
        try{
            int cansiCompra = Integer.parseInt(jTFUM.getText());
            double costoBruto = Double.parseDouble(jTFCostoBruto.getText().replace(",", ""));
            double costoNeto = Double.parseDouble(jTFCostoNeto.getText().replace(",", ""));
            double costoUnitario = Double.parseDouble(jTFCostoUnitario.getText().replace(",", ""));
            double descuento = Double.parseDouble(jTFDescuento.getText());
            if(!setNuevoCosto(costoBruto, costoNeto, costoUnitario, descuento, "MANTENIMIENTO")){
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
    
    private boolean setNuevoCosto(double costoBruto, double costoNeto, double costoUnitario, double descuento, String modulo){
        boolean resultOk = false;
        StatementManager TheQuery = new StatementManager();
        try{
            String sql = "";
            String sqlEmpaque = "";
            double costoAVG = costoUnitario; // si el select no recupera nada queda este como nuevo costo unitario o avg
            int cansiCostoActual = Integer.parseInt(jTFUM.getText());
            int cansiCostoAnterior = 1;
            System.out.println("CODIGO DEL ARTICULO: " + codArticulo);
            
            // -- CONSULTA QUE VOY A DEJAR DE USAR
            /*TheQuery.TheSql = "SELECT costo_promedio, cansi_compra FROM costoart"
                    + " WHERE cod_articulo = " + codArticulo + " AND cod_lista = " + jTFCodListaCosto.getText() + " AND vigente = 'S'";
            TheQuery.EjecutarSql();
            try{
                if(TheQuery.TheResultSet.next()){
                    costoAVG = TheQuery.TheResultSet.getDouble("costo_promedio");
                    cansiCostoAnterior = TheQuery.TheResultSet.getInt("cansi_compra");
                    costoAVG = (costoAVG / cansiCostoAnterior) * cansiCostoAnterior; // por si hubo un cambio de empaque
                }
            }catch(SQLException sqlex){
                resultOk = false;
                sqlex.printStackTrace();
                InfoErrores.errores(sqlex);
                costoAVG = Double.parseDouble(jTFCostoNeto.getText()); 
                JOptionPane.showMessageDialog(this, "ATENCION: No se pudo hallar el Precio Promedio (AVG)!", "Error", JOptionPane.WARNING_MESSAGE);
            }catch(Exception ex){
                resultOk = false;
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }
            
            TheQuery.CerrarStatement();
            TheQuery = null;*/
            // -- CONSULTA QUE DEJO DE USAR
            
            setPrecioCostoAnteriorInactivo(1);
            sql = "INSERT INTO costoart (cod_lista, cod_articulo, cansi_compra, fec_vigencia, sigla_compra, costo_bruto, descuento_pct, "
                                       + "flete_valor, costo_neto, costo_promedio, vigente, cod_usuario, nom_modulo) "
                 + "VALUES (" + 1 + ", "
                 + "" + codArticulo + ", " 
                 + jTFUM.getText() + ", 'now()', '" 
                 + jCBEmpaque.getSelectedItem().toString() + "', "
                 + "ROUND(" + costoBruto + ", 2), "
                 + descuento + ", "
                 + "0, "
                 + "ROUND(" + costoNeto + ", 2), "
                 + "ROUND(" + costoUnitario + ", 2), "
                 + "'S', "
                 + FormMain.codUsuario + ", '"
                 + modulo + "')";
            System.out.println("SQL INSERT DE COSTO: " + sql); // -- Control por consola del SQL
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
    
    private int setPrecioCostoAnteriorInactivo(int codLista) {
        String sql = "UPDATE costoArt SET vigente='N'"
                + " WHERE vigente     ='S'"
                + "   AND cod_articulo=" + codArticulo
                + "   AND cod_lista   =" + codLista;
        return DBManager.ejecutarDML(sql);
    }
    
    private int setPrecioVentaAnteriorInactivo(int codLista) {
        String sql = "UPDATE preciosart SET vigente='N'"
                + " WHERE vigente     ='S'"
                + "   AND cod_articulo=" + codArticulo
                + "   AND cod_lista   =" + codLista;
        return DBManager.ejecutarDML(sql);
    }
    
    private boolean inputOK() {
        boolean result = true;
        if (!utiles.Utiles.esSiglaValida(jCBEmpaque.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Sigla de Compra Inexistente !!!", "ATENCION...", JOptionPane.WARNING_MESSAGE);
            jCBEmpaque.grabFocus();
            result = false;
        } else {
            if (jTFUM.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Debe Especificar una Cantidad para la Sigla !!!", "ATENCION...", JOptionPane.WARNING_MESSAGE);
                jTFUM.grabFocus();
                result = false;
            } else {
                if (jTFCostoBruto.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Debe Especificar un precio de costo !!!", "ATENCION...", JOptionPane.WARNING_MESSAGE);
                    jTFCostoBruto.grabFocus();
                    result = false;
                }
            }
        }
        return result;
    }
    
    private void configuraTabla(){
        jTPrecios.setModel(dtm);       
        jTPrecios.setDefaultRenderer(Object.class, new CellRenderer()); // esto le da formato a la tabla dependiendo del valor que tenga (double, string, int)
        
        jTPrecios.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTPrecios.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTPrecios.getColumnModel().getColumn(2).setPreferredWidth(50);
        
        jTPrecios.getColumnModel().getColumn(3).setPreferredWidth(70);
        jTPrecios.getColumnModel().getColumn(4).setPreferredWidth(70);
        jTPrecios.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTPrecios.getColumnModel().getColumn(6).setPreferredWidth(130);
        jTPrecios.getColumnModel().getColumn(7).setPreferredWidth(130);
    }
    
    private void getPrecioClic(){
        int row = jTPrecios.getSelectedRow();
        jCBListaPrecioVenta.setSelectedItem(jTPrecios.getValueAt(row, 0).toString());
        jLDescListaPrecio.setText(jTPrecios.getValueAt(row, 1).toString().trim());
        jCBSiglaVenta.setSelectedItem(jTPrecios.getValueAt(row, 2).toString().trim());
        jTFUMVenta.setText(jTPrecios.getValueAt(row, 3).toString().trim());
        jTFMargenVenta.setText(decimalFormat.format(jTPrecios.getValueAt(row, 4)));
        jTFPrecioVenta.setText(decimalFormat.format(jTPrecios.getValueAt(row, 5)));
        //jTFMargenVenta.grabFocus();
    }
    
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane.showMessageDialog(PrecioCosto.this, "Clic en el botón CANCELAR!", "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        });
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
        jPanel3 = new javax.swing.JPanel();
        jLDescripcion = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jCBCodSector = new javax.swing.JComboBox<>();
        jTFCodListaCosto = new javax.swing.JTextField();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLNombreLocal = new javax.swing.JLabel();
        jLNombreSector = new javax.swing.JLabel();
        jLListaCosto = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jCBEmpaque = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jTFUM = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTFCostoBruto = new javax.swing.JTextField();
        jTFDescuento = new javax.swing.JTextField();
        jTFCostoNeto = new javax.swing.JTextField();
        jTFCostoUnitario = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jCBListaPrecioVenta = new javax.swing.JComboBox<>();
        jLDescListaPrecio = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jCBSiglaVenta = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jTFUMVenta = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFMargenVenta = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTFPrecioVenta = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jBCancelar = new javax.swing.JButton();
        jBConfirmarPrecios = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTPrecios = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Mantenimiento de Costo ");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLDescripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLDescripcion.setText("***");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLDescripcion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Sector:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Lista Costo:");

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

        jTFCodListaCosto.setEditable(false);
        jTFCodListaCosto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodListaCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodListaCostoKeyPressed(evt);
            }
        });

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLNombreSector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLNombreSector.setText("***");

        jLListaCosto.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLListaCosto.setText("***");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Empaque de Compra:");

        jCBEmpaque.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBEmpaqueKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("U/M:");

        jTFUM.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFUM.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFUMFocusGained(evt);
            }
        });
        jTFUM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFUMKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Costo Bruto c/ IVA:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Descuento:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Costo Neto c/ IVA:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Costo Unitario final:");

        jTFCostoBruto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCostoBruto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCostoBrutoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCostoBrutoFocusLost(evt);
            }
        });
        jTFCostoBruto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCostoBrutoKeyPressed(evt);
            }
        });

        jTFDescuento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescuentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFDescuentoFocusLost(evt);
            }
        });
        jTFDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescuentoKeyPressed(evt);
            }
        });

        jTFCostoNeto.setEditable(false);
        jTFCostoNeto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTFCostoUnitario.setEditable(false);
        jTFCostoUnitario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("%");

        jLabel12.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel12.setText("Precio de Costo");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel13.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel13.setText("Precio de Venta");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Lista de Precio:");

        jCBListaPrecioVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBListaPrecioVentaActionPerformed(evt);
            }
        });
        jCBListaPrecioVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBListaPrecioVentaKeyPressed(evt);
            }
        });

        jLDescListaPrecio.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLDescListaPrecio.setText("***");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Sigla de Venta:");

        jCBSiglaVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBSiglaVentaKeyPressed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("U/M:");

        jTFUMVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFUMVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFUMVentaFocusGained(evt);
            }
        });
        jTFUMVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFUMVentaKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Margen:");

        jTFMargenVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFMargenVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMargenVentaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFMargenVentaFocusLost(evt);
            }
        });
        jTFMargenVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMargenVentaKeyPressed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("%");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setText("Precio de Venta:");

        jTFPrecioVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFPrecioVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFPrecioVentaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFPrecioVentaFocusLost(evt);
            }
        });
        jTFPrecioVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFPrecioVentaKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCBCodEmpresa, 0, 92, Short.MAX_VALUE)
                            .addComponent(jCBCodLocal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCBCodSector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTFCodListaCosto))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLNombreEmpresa)
                            .addComponent(jLNombreLocal)
                            .addComponent(jLNombreSector)
                            .addComponent(jLListaCosto)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jCBEmpaque, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFUM, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTFCostoBruto)
                                    .addComponent(jTFDescuento)
                                    .addComponent(jTFCostoUnitario)
                                    .addComponent(jTFCostoNeto))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11))
                            .addComponent(jLabel12))
                        .addGap(49, 49, 49)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFUMVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTFPrecioVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                                            .addComponent(jTFMargenVenta))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel15)
                                            .addComponent(jLabel14))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jCBListaPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jCBSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLDescListaPrecio))))))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreSector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodListaCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLListaCosto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jCBEmpaque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)
                                    .addComponent(jTFUM, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jTFCostoBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTFDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jTFCostoNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jTFCostoUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(jCBListaPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLDescListaPrecio))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jCBSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(jTFUMVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel17)
                                    .addComponent(jTFMargenVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(jTFPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setMnemonic('C');
        jBCancelar.setText("Cancelar y Salir");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBConfirmarPrecios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarPrecios.setMnemonic('N');
        jBConfirmarPrecios.setText("Confirmar Nuevos Precios");
        jBConfirmarPrecios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarPreciosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBConfirmarPrecios)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBCancelar)
                    .addComponent(jBConfirmarPrecios))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBConfirmarPrecios});

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Precios de Ventas por Lista"));

        jTPrecios = new JTable(){

            public boolean isCellEditable(int rowIndex, int colIndex) {

                /* if(colIndex == 4){ // La 4ta columna isEditable(true)
                    return true;
                }else{
                    return false; //Las celdas no son editables.
                }*/
                return false; //Las celdas no son editables.
            }
        };
        jTPrecios.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTPrecios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTPrecios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTPreciosMouseClicked(evt);
            }
        });
        jTPrecios.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTPreciosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTPrecios);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );

        jLabel20.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel20.setText("Para eliminar un precio de venta, seleccione el mismo y presione DELETE/SUPRIMIR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(848, 707));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        dispose();
        Articulos.cargaLineasVenta();
    }//GEN-LAST:event_jBCancelarActionPerformed

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
            jTFCodListaCosto.grabFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jTFCodListaCostoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodListaCostoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBEmpaque.grabFocus();
        }
    }//GEN-LAST:event_jTFCodListaCostoKeyPressed

    private void jCBEmpaqueKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBEmpaqueKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFUM.grabFocus();
            if(jCBEmpaque.getSelectedItem().toString().equals("UN") || jCBEmpaque.getSelectedItem().toString().equals("KG")
               || jCBEmpaque.getSelectedItem().toString().equals("LT") || jCBEmpaque.getSelectedItem().toString().equals("MT")){
                jTFUM.setText("1");
                jTFUM.setEditable(false);
                jTFCostoBruto.grabFocus();
            }else{
                jTFUM.setEditable(true);
                jTFUM.selectAll();
            }
        }
    }//GEN-LAST:event_jCBEmpaqueKeyPressed

    private void jTFUMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFUMKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCostoBruto.grabFocus();
        }
    }//GEN-LAST:event_jTFUMKeyPressed

    private void jTFUMFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFUMFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFUMFocusGained

    private void jTFCostoBrutoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCostoBrutoFocusLost
        /*double costoB = Double.parseDouble(jTFCostoBruto.getText());
        jTFCostoBruto.setText(decimalFormat.format(costoB));*/
    }//GEN-LAST:event_jTFCostoBrutoFocusLost

    private void jTFCostoBrutoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCostoBrutoFocusGained
        jTFCostoBruto.selectAll();
    }//GEN-LAST:event_jTFCostoBrutoFocusGained

    private void jTFCostoBrutoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCostoBrutoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFDescuento.grabFocus();
        }
    }//GEN-LAST:event_jTFCostoBrutoKeyPressed

    private void jTFDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescuentoFocusGained
        jTFDescuento.selectAll();
    }//GEN-LAST:event_jTFDescuentoFocusGained
      
    private void jTFDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescuentoFocusLost
        descuento = Double.parseDouble(jTFDescuento.getText());
        costoB = Double.parseDouble(jTFCostoBruto.getText().replace(",", ""));
        int cansiCompra = Integer.parseInt(jTFUM.getText());
        double totalDesc = costoB * (descuento / 100);
        costoU = costoB - totalDesc;
        
        if(jCBEmpaque.getSelectedItem().toString().equals("UN") || jCBEmpaque.getSelectedItem().toString().equals("KG")
               || jCBEmpaque.getSelectedItem().toString().equals("LT") || jCBEmpaque.getSelectedItem().toString().equals("MT")){
            if(jTFDescuento.getText().equals("0") || jTFDescuento.getText().equals(".00")){
                jTFCostoNeto.setText(jTFCostoBruto.getText());
                jTFCostoUnitario.setText(jTFCostoBruto.getText());
            }else{
                // Costo Neto
                //totalDesc = costoB * (descuento / 100);
                costoU = costoB - totalDesc;
                
                // Costo Unitario
                jTFCostoNeto.setText(String.valueOf(costoU));
                jTFCostoUnitario.setText(String.valueOf(costoU));
            }
        }else{
            if(jTFDescuento.getText().equals("0") || jTFDescuento.getText().equals(".00")){
                costoU = costoB / cansiCompra;
                jTFCostoNeto.setText(String.valueOf(costoB));
                jTFCostoUnitario.setText(String.valueOf(costoU));
            }else{
                // Costo Neto       
                double costoN = costoB - totalDesc;
                costoU = costoN / cansiCompra;
                
                // Costo Unitario
                jTFCostoNeto.setText(String.valueOf(costoN));
                jTFCostoUnitario.setText(String.valueOf(costoU));
            }
        }
    }//GEN-LAST:event_jTFDescuentoFocusLost

    private void jTFDescuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescuentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFDescuento.getText().equals("")){
                jTFDescuento.setText("0.00");
            }
            jCBListaPrecioVenta.grabFocus();
        }
    }//GEN-LAST:event_jTFDescuentoKeyPressed

    private void jBConfirmarPreciosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarPreciosActionPerformed
        if(!jTFCostoNeto.getText().equals("")){
            if(inputOK()){
                if(grabaCostoArticulo() && grabaPrecioVentaArticulo()){
                    try{
                        DBManager.conn.commit();
                        JOptionPane.showMessageDialog(this, "Nuevos Precios Registrados!", "Éxito...", JOptionPane.INFORMATION_MESSAGE);
                        jBConfirmarPrecios.setEnabled(false);
                        Articulos.cargaLineasCosto();
                        Articulos.cargaLineasVenta();
                        dispose();
                    }catch(SQLException sqlex){
                        sqlex.printStackTrace();
                        InfoErrores.errores(sqlex);
                        JOptionPane.showMessageDialog(this, "Error al Aplicar Commit a Datos!",
                                "ATENCION...", JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Grabar Datos, Operacion Cancelada!", "ATENCION...", JOptionPane.WARNING_MESSAGE);
                    this.dispose();
                    try {
                        DBManager.conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        InfoErrores.errores(ex);
                        JOptionPane.showMessageDialog(this, "Error al Aplicar Rollback!",
                                "ATENCION...", JOptionPane.WARNING_MESSAGE);
                    }
                }
                jBCancelar.grabFocus();
            }
        }
        formatNumeric();
    }//GEN-LAST:event_jBConfirmarPreciosActionPerformed

    private void jCBListaPrecioVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBListaPrecioVentaActionPerformed
        jLDescListaPrecio.setText(utiles.Utiles.getInfoListaPrecio("descripcion", jCBListaPrecioVenta.getSelectedItem().toString()));
    }//GEN-LAST:event_jCBListaPrecioVentaActionPerformed

    private void jCBListaPrecioVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBListaPrecioVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBSiglaVenta.grabFocus();
        }
    }//GEN-LAST:event_jCBListaPrecioVentaKeyPressed

    private void jCBSiglaVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBSiglaVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jCBSiglaVenta.getSelectedItem().toString().equals("UN") || jCBEmpaque.getSelectedItem().toString().equals("KG")
               || jCBEmpaque.getSelectedItem().toString().equals("LT") || jCBEmpaque.getSelectedItem().toString().equals("MT")){
                jTFUMVenta.setText("1");
                jTFUMVenta.setEditable(false);
                jTFMargenVenta.grabFocus();
            }else{
                jTFUMVenta.setEditable(true);
                jTFUMVenta.grabFocus();
                jTFUMVenta.selectAll();
            }
        }
    }//GEN-LAST:event_jCBSiglaVentaKeyPressed

    private void jTFUMVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFUMVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFMargenVenta.grabFocus();
        }
    }//GEN-LAST:event_jTFUMVentaKeyPressed

    private void jTFMargenVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMargenVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFPrecioVenta.grabFocus();
        }
    }//GEN-LAST:event_jTFMargenVentaKeyPressed

    private void jTFUMVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFUMVentaFocusGained
        jTFUMVenta.selectAll();
    }//GEN-LAST:event_jTFUMVentaFocusGained

    private void jTFMargenVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMargenVentaFocusGained
        jTFMargenVenta.selectAll();
    }//GEN-LAST:event_jTFMargenVentaFocusGained

    private void jTFPrecioVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPrecioVentaFocusGained
        jTFPrecioVenta.selectAll();
    }//GEN-LAST:event_jTFPrecioVentaFocusGained

        
    private void jTFMargenVentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMargenVentaFocusLost
        if(jTFMargenVenta.getText().equals("")){
            jTFMargenVenta.setText("0");
        }
        if(jCBSiglaVenta.getSelectedItem().toString().equals("UN") || jCBSiglaVenta.getSelectedItem().toString().equals("MT")
          || jCBSiglaVenta.getSelectedItem().toString().equals("LT") || jCBSiglaVenta.getSelectedItem().toString().equals("KG")
          || jCBSiglaVenta.getSelectedItem().toString().equals("GR")){  
            costoU = Double.parseDouble(jTFCostoUnitario.getText().replace(",", "")); // es el costo unitario del producto
            margenPVenta = Double.parseDouble(jTFMargenVenta.getText());
            precioVenta = costoU * (margenPVenta / 100) + costoU;
            jTFPrecioVenta.setText(String.valueOf(precioVenta));
        }else{
            costoU = (Double.parseDouble(jTFCostoUnitario.getText().replace(",", "")) * Integer.parseInt(jTFUMVenta.getText()));
            margenPVenta = Double.parseDouble(jTFMargenVenta.getText());
            precioVenta = costoU * (margenPVenta / 100) + costoU;
            jTFPrecioVenta.setText(String.valueOf(precioVenta));
        }
    }//GEN-LAST:event_jTFMargenVentaFocusLost

    private void jTPreciosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTPreciosMouseClicked
        getPrecioClic();
    }//GEN-LAST:event_jTPreciosMouseClicked

    private void jTPreciosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTPreciosKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            getPrecioClic();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            //int lista = Integer.parseInt(jCBListaPrecioVenta.getSelectedItem().toString());
            int lista1 = Integer.parseInt(jTPrecios.getValueAt(jTPrecios.getSelectedRow(), 0).toString());
            eliminarPrecioVenta(Integer.valueOf(codArticulo), lista1);
            getPrecioArt(Long.parseLong(codArticulo));
            //eliminarPrecioVentaFila();
        }
    }//GEN-LAST:event_jTPreciosKeyPressed

    private void jTFPrecioVentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPrecioVentaFocusLost
        double difCostoVenta = 0 ;
        //if(jTFMargenVenta.getText().equals("") || jTFMargenVenta.getText().equals("0")){
        if(jCBSiglaVenta.getSelectedItem().toString().equals("UN") || jCBSiglaVenta.getSelectedItem().toString().equals("MT")
          || jCBSiglaVenta.getSelectedItem().toString().equals("LT") || jCBSiglaVenta.getSelectedItem().toString().equals("KG")
          || jCBSiglaVenta.getSelectedItem().toString().equals("GR")){            
            precioVenta = Double.parseDouble(jTFPrecioVenta.getText().replace(",", ""));
            costoU = Double.parseDouble(jTFCostoUnitario.getText().replace(",", "")); // es el costo unitario del producto
            difCostoVenta = precioVenta - costoU;
            margenPVenta = (difCostoVenta * 100) / costoU;
            jTFMargenVenta.setText(String.valueOf(margenPVenta));
        }else{
            precioVenta = Double.parseDouble(jTFPrecioVenta.getText().replace(",", ""));
            costoU = (Double.parseDouble(jTFCostoUnitario.getText().replace(",", "")) * Integer.parseInt(jTFUMVenta.getText())); // es el costo unitario del producto
            difCostoVenta = precioVenta - costoU;
            margenPVenta = (difCostoVenta * 100) / costoU;
            jTFMargenVenta.setText(String.valueOf(margenPVenta));
            System.out.println("PRECIO VENTA: " + precioVenta + "\nCOSTO UNITARIO: " + costoU + 
                               "\nDIFERENCIA COSTO: " + difCostoVenta + "\nMARGEN VENTA: " + margenPVenta);
        }
    }//GEN-LAST:event_jTFPrecioVentaFocusLost

    private void jTFPrecioVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFPrecioVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBConfirmarPrecios.grabFocus();
        }
    }//GEN-LAST:event_jTFPrecioVentaKeyPressed

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
            java.util.logging.Logger.getLogger(PrecioCosto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrecioCosto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrecioCosto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrecioCosto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrecioCosto(new javax.swing.JFrame(), true, 0, "1");
                /*PrecioCosto dialog = new PrecioCosto(new javax.swing.JFrame(), true, 0, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);*/
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBConfirmarPrecios;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JComboBox<String> jCBEmpaque;
    private javax.swing.JComboBox<String> jCBListaPrecioVenta;
    private javax.swing.JComboBox<String> jCBSiglaVenta;
    private javax.swing.JLabel jLDescListaPrecio;
    private javax.swing.JLabel jLDescripcion;
    private javax.swing.JLabel jLListaCosto;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLNombreSector;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTFCodListaCosto;
    private javax.swing.JTextField jTFCostoBruto;
    private javax.swing.JTextField jTFCostoNeto;
    private javax.swing.JTextField jTFCostoUnitario;
    private javax.swing.JTextField jTFDescuento;
    private javax.swing.JTextField jTFMargenVenta;
    private javax.swing.JTextField jTFPrecioVenta;
    private javax.swing.JTextField jTFUM;
    private javax.swing.JTextField jTFUMVenta;
    private javax.swing.JTable jTPrecios;
    // End of variables declaration//GEN-END:variables
}
