/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.informes;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.StatementManager;

/**
 *
 * @author Andres
 */
public class InformeVentas extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector, fecDesde, fecHasta;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    DefaultTableModel dtmVentasPorCaja, dtmVentasFormaPago, dtmVentasGrupoArticulos;

    StatementManager sm;
    private SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");
    
    public InformeVentas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString().trim();
        codLocal = jCBCodLocal.getSelectedItem().toString().trim();
        codSector = jCBCodSector.getSelectedItem().toString().trim();
        datePickerFormat();
        llenarCampos();
    }

    private void llenarCampos(){
        jLEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jLSector.setText(utiles.Utiles.getSectorDescripcion(codLocal, codSector));
    }
    
    private void datePickerFormat(){
        Date date = new Date();        
        jXDPDesde.setFormats(new String[]{"dd/MM/yyyy"});
        jXDPHasta.setFormats(new String[]{"dd/MM/yyyy"});
        jXDPDesde.setDate(date);
        jXDPHasta.setDate(date);
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
    }
    
    private void getFecVigencia(){
        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            java.util.Date d = new java.util.Date();
            fecVigencia = sdf.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Formateando Fecha...");
        }
    }
    
    private void configuraTablaVtasCaja(){
        dtmVentasPorCaja = new DefaultTableModel(null, new String[]{"Nro Caja", "Descripción", "Costo", "Total Venta", "Utilidad", "Descuento"}); 
        jTVentas.setModel(dtmVentasPorCaja);       
        jTVentas.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTVentas.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTVentas.getColumnModel().getColumn(1).setPreferredWidth(70);
        jTVentas.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(5).setPreferredWidth(100);
    }
    
    private void configuraTablaVtasFormaPago(){
        dtmVentasFormaPago = new DefaultTableModel(null, new String[]{"Código", "Descripción", "Monto", "Cotización", "Total"}); 
        jTVentas.setModel(dtmVentasFormaPago);       
        jTVentas.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTVentas.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTVentas.getColumnModel().getColumn(1).setPreferredWidth(70);
        jTVentas.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(4).setPreferredWidth(100);
    }
    
    private void configuraTablaVtasGrupoArticulo(){
        dtmVentasGrupoArticulos = new DefaultTableModel(null, new String[]{"Código", "Descripción", "Costo", "Total Venta", "Utilidad"}); 
        jTVentas.setModel(dtmVentasGrupoArticulos);       
        jTVentas.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTVentas.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTVentas.getColumnModel().getColumn(1).setPreferredWidth(70);
        jTVentas.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTVentas.getColumnModel().getColumn(4).setPreferredWidth(100);
    }
    
    private boolean cargarVentasPorCaja(){
        boolean result = true;
        double totalVenta = 0, totalCosto = 0, utilidad = 0;
        try{
            String sql = "SELECT DISTINCT venta_det.cod_caja, ('CAJA - ' || venta_det.cod_caja) AS descripcion, SUM(venta_det.mon_descuento) AS descuento, "
                       + "SUM(venta_det.mon_costo) AS costo, SUM(venta_det.mon_venta) AS ventas, "
                       + "(SUM(venta_det.mon_venta) - SUM(venta_det.mon_costo)) AS utilidad "
                       + "FROM venta_det "
                       + "INNER JOIN venta_cab "
                       + "ON venta_det.nro_ticket = venta_cab.nro_ticket "
                       + "WHERE venta_det.fec_comprob::date >= to_date('" + fecDesde + "', 'dd/MM/yyyy') AND venta_det.fec_comprob::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                       + "AND venta_cab.fec_comprob::date >= to_date('" + fecDesde + "', 'dd/MM/yyyy') AND venta_cab.fec_comprob::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                       + "AND venta_det.estado = 'V' AND venta_cab.estado = 'V' "
                       + "GROUP BY venta_det.cod_caja";
            System.out.println("INFORME DE VENTAS X CAJA (SCRIPT): " + sql);
            sm.TheSql = sql;
            sm.EjecutarSql();
            
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[6];
                    row[0] = sm.TheResultSet.getString("cod_caja");
                    row[1] = sm.TheResultSet.getString("descripcion");
                    row[2] = sm.TheResultSet.getDouble("costo");
                    row[3] = sm.TheResultSet.getDouble("ventas");
                    row[4] = sm.TheResultSet.getDouble("utilidad");
                    double descuento = Math.ceil(sm.TheResultSet.getDouble("descuento"));
                    row[5] = descuento;
                    totalVenta += sm.TheResultSet.getDouble("ventas");
                    totalCosto += sm.TheResultSet.getDouble("costo");
                    utilidad += sm.TheResultSet.getDouble("utilidad");
                    dtmVentasPorCaja.addRow(row);
                }
                jLTotales.setText("Utilidad:");
                jTFTotalCosto.setText(decimalFormat.format(totalCosto));
                jTFTotalVentas.setText(decimalFormat.format(totalVenta));
                jTFUtilidad.setText(decimalFormat.format(utilidad));
                jTVentas.updateUI();
                if(dtmVentasPorCaja.getRowCount() > 0){
                    jBImprimir.setEnabled(true);
                }else{
                    jBImprimir.setEnabled(false);
                }
            }else{
                result = false;
            }
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
    
    private boolean cargarVentasPorFormaPago(){
        boolean result = true;
        double total = 0;
        try{
            String sql = "SELECT forma_pago.nom_librador, SUM(forma_pago.monto_pago) AS total_cobro, "
                       + "forma_pago.tip_cambio, (sum(forma_pago.monto_pago) * forma_pago.tip_cambio) as total_cambio, "
                       + "CASE WHEN forma_pago.tipo_cuenta = 'CRE' THEN cuenta.cod_cliente ELSE forma_pago.cod_cuenta END as codigo "
                       + "FROM forma_pago "
                       + "INNER JOIN cuenta "
                       + "ON forma_pago.cod_cuenta = cuenta.cod_cuenta "
                       + "WHERE forma_pago.fec_cobro::date >= to_date('" + fecDesde + "', 'dd/MM/yyyy') and forma_pago.fec_cobro::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                       + "GROUP BY forma_pago.nom_librador, forma_pago.cod_cuenta, forma_pago.tip_cambio, forma_pago.tipo_cuenta, cuenta.cod_cliente "
                       + "ORDER BY forma_pago.cod_cuenta ";
            System.out.println("INFORME DE VENTAS X FORMA DE PAGO (SCRIPT): " + sql);
            sm.TheSql = sql;
            sm.EjecutarSql();
            
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[5];
                    row[0] = sm.TheResultSet.getString("codigo");
                    row[1] = sm.TheResultSet.getString("nom_librador");
                    row[2] = sm.TheResultSet.getDouble("total_cobro");
                    row[3] = sm.TheResultSet.getDouble("tip_cambio");
                    row[4] = sm.TheResultSet.getDouble("total_cambio");
                    total += sm.TheResultSet.getDouble("total_cambio");
                    dtmVentasFormaPago.addRow(row);
                }
                jLTotales.setText("Total forma de pago:");
                jTFUtilidad.setText(decimalFormat.format(total));
                jTVentas.updateUI();
                if(dtmVentasFormaPago.getRowCount() > 0){
                    jBImprimir.setEnabled(true);
                }else{
                    jBImprimir.setEnabled(false);
                }
            }else{
                result = false;
            }
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
    
    private boolean cargarVentasPorGrupoArticulo(){
        boolean result = true;
        double totalVenta = 0, totalCosto = 0, utilidad = 0;
        try{
            String sql = "SELECT DISTINCT grupo.cod_grupo AS codigo, grupo.descripcion AS descripcion, "
                       + "SUM(venta_det.mon_costo) AS costo, SUM(venta_det.mon_venta) AS total_venta, "
                       + "(SUM(venta_det.mon_venta) - SUM(venta_det.mon_costo)) AS utilidad "
                       + "FROM venta_det "
                       + "INNER JOIN articulo "
                       + "ON venta_det.cod_articulo = articulo.cod_articulo "
                       + "INNER JOIN grupo "
                       + "ON grupo.cod_grupo = articulo.cod_grupo "
                       + "WHERE venta_det.fec_comprob::date >= to_date('" + fecDesde + "', 'dd/MM/yyyy') AND venta_det.fec_comprob::date <= to_date('" + fecHasta + "', 'dd/MM/yyyy') "
                       + "AND venta_det.estado = 'V' "
                       + "GROUP BY grupo.cod_grupo";
            
            System.out.println("INFORME DE VENTAS X GRUPO DE ARTICULOS (SCRIPT): " + sql);
            sm.TheSql = sql;
            sm.EjecutarSql();
            
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[5];
                    row[0] = sm.TheResultSet.getString("codigo");
                    row[1] = sm.TheResultSet.getString("descripcion");
                    row[2] = sm.TheResultSet.getDouble("costo");
                    row[3] = sm.TheResultSet.getDouble("total_venta");
                    row[4] = sm.TheResultSet.getDouble("utilidad");
                    totalCosto += sm.TheResultSet.getDouble("costo");
                    totalVenta += sm.TheResultSet.getDouble("total_venta");
                    utilidad += sm.TheResultSet.getDouble("utilidad");
                    dtmVentasGrupoArticulos.addRow(row);
                }
                jLTotales.setText("Utilidad:");
                jTFTotalCosto.setText(decimalFormat.format(totalCosto));
                jTFTotalVentas.setText(decimalFormat.format(totalVenta));
                jTFUtilidad.setText(decimalFormat.format(utilidad));
                jTVentas.updateUI();
                if(dtmVentasGrupoArticulos.getRowCount() > 0){
                    jBImprimir.setEnabled(true);
                }else{
                    jBImprimir.setEnabled(false);
                }
            }else{
                result = false;
            }
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
    
    private void limpiarTabla(DefaultTableModel model){
        int nroFilas = jTVentas.getRowCount();
        int i = 0;
        while(i < nroFilas){
            model.removeRow(i);
            nroFilas = model.getRowCount();
            
            if(nroFilas == 1){
                model.removeRow(i);
                nroFilas = 0;
            }
        }
        jTVentas.setModel(model);
    }
    
    private void limpiarTotales(){
        jTFTotalVentas.setText("");
        jTFTotalCosto.setText("");
        jTFUtilidad.setText("");
    }
    
    private void informeVentasPorCaja(){
        String sql = "SELECT DISTINCT venta_det.cod_caja, ('CAJA - ' || venta_det.cod_caja) AS descripcion, SUM(venta_cab.mon_descuento) AS descuento, "
                   + "SUM(venta_det.mon_costo) AS costo, SUM(venta_det.mon_venta) AS total_venta, "
                   + "(SUM(venta_det.mon_venta) - SUM(venta_det.mon_costo)) AS utilidad "
                   + "FROM venta_det "
                   + "INNER JOIN venta_cab "
                   + "ON venta_det.nro_ticket = venta_cab.nro_ticket "
                   + "WHERE venta_det.fec_comprob::date >= '" + fecDesde + "'::date AND venta_det.fec_comprob::date <= '" + fecHasta + "'::date "
                   + "AND venta_cab.fec_comprob::date >= '" + fecDesde + "'::date AND venta_cab.fec_comprob::date <= '" + fecHasta + "'::date "
                   + "AND venta_det.estado = 'V' AND venta_cab.estado = 'V' "
                   + "GROUP BY venta_det.cod_caja";
        System.out.println("IMPRESION DE INFORME DE VENTAS X CAJA (SCRIPT): " + sql);
        
        try{
            LibReportes.parameters.put("pFechaActual", fecVigencia);
            LibReportes.parameters.put("pFechaDesde", fecDesde);
            LibReportes.parameters.put("pFechaHasta", fecHasta);
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " - " + FormMain.nombreUsuario);
            
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "informe_ventas_por_caja");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private void informeVentasPorFormaPago(){
        String sql = "SELECT forma_pago.nom_librador, SUM(forma_pago.monto_pago) AS total_cobro, "
                       + "forma_pago.tip_cambio, (sum(forma_pago.monto_pago) * forma_pago.tip_cambio) as total_cambio, "
                       + "CASE WHEN forma_pago.tipo_cuenta = 'CRE' THEN cuenta.cod_cliente ELSE forma_pago.cod_cuenta END as codigo "
                       + "FROM forma_pago "
                       + "INNER JOIN cuenta "
                       + "ON forma_pago.cod_cuenta = cuenta.cod_cuenta "
                       + "WHERE forma_pago.fec_cobro::date >= '" + fecDesde + "'::date and forma_pago.fec_cobro::date <= '" + fecHasta + "'::date "
                       + "GROUP BY forma_pago.nom_librador, forma_pago.cod_cuenta, forma_pago.tip_cambio, forma_pago.tipo_cuenta, cuenta.cod_cliente "
                       + "ORDER BY forma_pago.cod_cuenta ";
            System.out.println("IMPRESION DE INFORME DE VENTAS X FORMA DE PAGO (SCRIPT): " + sql);
        
        try{
            LibReportes.parameters.put("pFechaActual", fecVigencia);
            LibReportes.parameters.put("pFechaDesde", fecDesde);
            LibReportes.parameters.put("pFechaHasta", fecHasta);
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " - " + FormMain.nombreUsuario);
            
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "informe_ventas_por_formas_pago");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private void informeVentasPorGrupoArticulos(){
        String sql = "SELECT DISTINCT grupo.cod_grupo AS codigo, grupo.descripcion AS descripcion, "
                   + "SUM(venta_det.mon_costo) AS costo, SUM(venta_det.mon_venta) AS total_venta, "
                   + "(SUM(venta_det.mon_venta) - SUM(venta_det.mon_costo)) AS utilidad "
                   + "FROM venta_det "
                   + "INNER JOIN articulo "
                   + "ON venta_det.cod_articulo = articulo.cod_articulo "
                   + "INNER JOIN grupo "
                   + "ON grupo.cod_grupo = articulo.cod_grupo "
                   + "WHERE venta_det.fec_comprob::date >= '" + fecDesde + "'::date AND venta_det.fec_comprob::date <= '" + fecHasta + "'::date "
                   + "AND venta_det.estado = 'V' "
                   + "GROUP BY grupo.cod_grupo";
            
            System.out.println("IMPRESION DE INFORME DE VENTAS X GRUPO DE ARTICULOS (SCRIPT): " + sql);
        
        try{
            LibReportes.parameters.put("pFechaActual", fecVigencia);
            LibReportes.parameters.put("pFechaDesde", fecDesde);
            LibReportes.parameters.put("pFechaHasta", fecHasta);
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " - " + FormMain.nombreUsuario);
            
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "informe_ventas_por_grupo_articulos");
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

        bGVentas = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLEmpresa = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLLocal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodSector = new javax.swing.JComboBox<>();
        jLSector = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jXDPDesde = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jXDPHasta = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jRBVentasPorCaja = new javax.swing.JRadioButton();
        jRBVentasPorFormaPago = new javax.swing.JRadioButton();
        jRBVentasPorGrupoArticulos = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        jBProcesar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTVentas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLTotal = new javax.swing.JLabel();
        jTFTotalVentas = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFTotalCosto = new javax.swing.JTextField();
        jLTotales = new javax.swing.JLabel();
        jTFUtilidad = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe de Ventas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("INFORME DE VARIOS DE VENTAS");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Sector:");

        jLSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLSector.setText("***");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLEmpresa)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLLocal)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLSector))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Desde:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Hasta:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Visualizar por:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jRBVentasPorCaja.setBackground(new java.awt.Color(204, 255, 204));
        bGVentas.add(jRBVentasPorCaja);
        jRBVentasPorCaja.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jRBVentasPorCaja.setSelected(true);
        jRBVentasPorCaja.setText("Ventas por Caja");

        jRBVentasPorFormaPago.setBackground(new java.awt.Color(204, 255, 204));
        bGVentas.add(jRBVentasPorFormaPago);
        jRBVentasPorFormaPago.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jRBVentasPorFormaPago.setText("Ventas por Forma de Pago");

        jRBVentasPorGrupoArticulos.setBackground(new java.awt.Color(204, 255, 204));
        bGVentas.add(jRBVentasPorGrupoArticulos);
        jRBVentasPorGrupoArticulos.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jRBVentasPorGrupoArticulos.setText("Ventas por Grupo de Artículos");

        jBProcesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBProcesar.setText("Procesar");
        jBProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBProcesarActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addComponent(jBProcesar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBImprimir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBSalir))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 106, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBVentasPorCaja)
                    .addComponent(jRBVentasPorFormaPago)
                    .addComponent(jRBVentasPorGrupoArticulos))
                .addGap(15, 15, 15))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBImprimir, jBProcesar, jBSalir});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(6, 6, 6)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBProcesar)
                    .addComponent(jBSalir)
                    .addComponent(jBImprimir))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRBVentasPorCaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRBVentasPorFormaPago)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRBVentasPorGrupoArticulos)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBImprimir, jBProcesar, jBSalir});

        jTVentas = new JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        jTVentas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTVentas);

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Resumen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLTotal.setText("Total Ventas:");

        jTFTotalVentas.setEditable(false);
        jTFTotalVentas.setBackground(new java.awt.Color(255, 255, 102));
        jTFTotalVentas.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalVentas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalVentas.setText("0.00");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Total Costo:");

        jTFTotalCosto.setEditable(false);
        jTFTotalCosto.setBackground(new java.awt.Color(255, 255, 102));
        jTFTotalCosto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCosto.setText("0.00");

        jLTotales.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLTotales.setText("Utilidad:");

        jTFUtilidad.setEditable(false);
        jTFUtilidad.setBackground(new java.awt.Color(255, 255, 102));
        jTFUtilidad.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFUtilidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFUtilidad.setText("0.00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLTotales)
                    .addComponent(jLabel9)
                    .addComponent(jLTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFTotalVentas)
                    .addComponent(jTFUtilidad)
                    .addComponent(jTFTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTotal)
                    .addComponent(jTFTotalVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTotales)
                    .addComponent(jTFUtilidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBProcesarActionPerformed
        fecDesde = sdf.format(jXDPDesde.getDate());
        fecHasta = sdf.format(jXDPHasta.getDate());
        limpiarTotales();
        sm = new StatementManager();
        if(jRBVentasPorCaja.isSelected()){
            configuraTablaVtasCaja();
            limpiarTabla(dtmVentasPorCaja);
            if(!cargarVentasPorCaja()){
                JOptionPane.showMessageDialog(this, "No existen datos para la fecha seleccionada!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        if(jRBVentasPorFormaPago.isSelected()){
            configuraTablaVtasFormaPago();
            limpiarTabla(dtmVentasFormaPago);
            if(!cargarVentasPorFormaPago()){
                JOptionPane.showMessageDialog(this, "No existen datos para la fecha seleccionada!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        if(jRBVentasPorGrupoArticulos.isSelected()){
            configuraTablaVtasGrupoArticulo();
            limpiarTabla(dtmVentasGrupoArticulos);
            if(!cargarVentasPorGrupoArticulo()){
                JOptionPane.showMessageDialog(this, "No existen datos para la fecha seleccionada!", "ATENCION", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_jBProcesarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(jRBVentasPorCaja.isSelected()){
            informeVentasPorCaja();
        }
        if(jRBVentasPorFormaPago.isSelected()){
            informeVentasPorFormaPago();
        }
        if(jRBVentasPorGrupoArticulos.isSelected()){
            informeVentasPorGrupoArticulos();
        }
        
    }//GEN-LAST:event_jBImprimirActionPerformed

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
            java.util.logging.Logger.getLogger(InformeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeVentas dialog = new InformeVentas(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGVentas;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBProcesar;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLEmpresa;
    private javax.swing.JLabel jLLocal;
    private javax.swing.JLabel jLSector;
    private javax.swing.JLabel jLTotal;
    private javax.swing.JLabel jLTotales;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRBVentasPorCaja;
    private javax.swing.JRadioButton jRBVentasPorFormaPago;
    private javax.swing.JRadioButton jRBVentasPorGrupoArticulos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTFTotalCosto;
    private javax.swing.JTextField jTFTotalVentas;
    private javax.swing.JTextField jTFUtilidad;
    private javax.swing.JTable jTVentas;
    private org.jdesktop.swingx.JXDatePicker jXDPDesde;
    private org.jdesktop.swingx.JXDatePicker jXDPHasta;
    // End of variables declaration//GEN-END:variables
}
