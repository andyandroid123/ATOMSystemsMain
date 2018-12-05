/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero.informes;

import TableUtiles.DateCellRender;
import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import controls.ClienteCtrl;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.ButtonDetalleCuentaCliente;
import utiles.ButtonDetalleDocCuentaCliente;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.StatementManager;
import views.busca.BuscaCliente;

/**
 *
 * @author ANDRES
 */
public class CuentasClientes extends javax.swing.JDialog {

    String codEmpresa, codLocal, codSector;
    private static DefaultTableModel dtmSaldoCuenta, dtmSaldoCuentaDetallado;    
    double tabla_interesTableSaldoCuenta, pct_interes = 0;
    StatementManager sm = null;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    
    public CuentasClientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        codLocal = jCBCodLocal.getSelectedItem().toString();
        configTableSaldoCuenta();
        configTableSaldoDetallado();
        configCampos();
        llenarCampos();
        pct_interes = Double.parseDouble(jTFInteres.getText().trim());
        jTFCodCliente.grabFocus();        
    }

    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
    }
    
    private void configCampos(){
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFInteres.setDocument(new MaxLength(2, "", "ENTERO"));
        
        jTFInteres.addFocusListener(new Focus()); 
        jTFCodCliente.addFocusListener(new Focus());
    }
    
    private void llenarCampos(){
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jTFCodCliente.setText("0");
        jTFInteres.setText(getInteresCobro());
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
    
    private void getDatosCliente(String codigo){
        ClienteCtrl clieCtrl = new ClienteCtrl();
        String nombre = clieCtrl.getNombreCliente(Integer.parseInt(codigo));
        jLNombreCliente.setText(nombre);
    }
    
    private void configTableSaldoCuenta(){
        dtmSaldoCuenta = (DefaultTableModel)jTSaldoCuenta.getModel();
        
        jTSaldoCuenta.getColumnModel().getColumn(0).setPreferredWidth(30); // cód cliente
        jTSaldoCuenta.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTSaldoCuenta.getColumnModel().getColumn(1).setPreferredWidth(200); // cliente 
        jTSaldoCuenta.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTSaldoCuenta.getColumnModel().getColumn(2).setPreferredWidth(60); // límite
        jTSaldoCuenta.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(0));
        jTSaldoCuenta.getColumnModel().getColumn(3).setPreferredWidth(40); // fec último pago 
        //jTSaldoCuenta.getColumnModel().getColumn(3).setCellRenderer(new DateCellRender());
        jTSaldoCuenta.getColumnModel().getColumn(4).setPreferredWidth(60); // crédito
        jTSaldoCuenta.getColumnModel().getColumn(4).setCellRenderer(new DecimalCellRender(0));
        jTSaldoCuenta.getColumnModel().getColumn(5).setPreferredWidth(60); // docs vencidos
        jTSaldoCuenta.getColumnModel().getColumn(5).setCellRenderer(new DecimalCellRender(0));
        jTSaldoCuenta.getColumnModel().getColumn(6).setPreferredWidth(60); // interés
        jTSaldoCuenta.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(0));
        jTSaldoCuenta.getColumnModel().getColumn(7).setPreferredWidth(60); // docs a vencer
        jTSaldoCuenta.getColumnModel().getColumn(7).setCellRenderer(new DecimalCellRender(0));
        jTSaldoCuenta.getColumnModel().getColumn(8).setPreferredWidth(60); // saldo + interés
        jTSaldoCuenta.getColumnModel().getColumn(8).setCellRenderer(new DecimalCellRender(0));
        
        // implementacion del button - detalle
        ButtonDetalleCuentaCliente button = new ButtonDetalleCuentaCliente(jTSaldoCuenta, 9);
        jTSaldoCuenta.getColumnModel().getColumn(9).setPreferredWidth(20); // Impresion
        jTSaldoCuenta.getColumnModel().getColumn(10).setMaxWidth(0); // Interes_pct
        jTSaldoCuenta.getColumnModel().getColumn(10).setMinWidth(0); // Interes_pct
        jTSaldoCuenta.getTableHeader().getColumnModel().getColumn(10).setMaxWidth(0); // Interes_pct    
        jTSaldoCuenta.getTableHeader().getColumnModel().getColumn(10).setMinWidth(0); // Interes_pct    
        jTSaldoCuenta.getColumnModel().getColumn(10).setCellRenderer(new DecimalCellRender(2));
        
        utiles.Utiles.punteroTablaF(jTSaldoCuenta, this);        
        jTSaldoCuenta.setFont(new Font("Tahoma", 1, 12) );
        jTSaldoCuenta.setRowHeight(20);
    }
    
    private void configTableSaldoDetallado(){
        dtmSaldoCuentaDetallado = (DefaultTableModel)jTSaldoDetallado.getModel();
        
        jTSaldoDetallado.getColumnModel().getColumn(0).setPreferredWidth(20); // local (int) 
        jTSaldoDetallado.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(1).setPreferredWidth(20); // caja (int)  
        jTSaldoDetallado.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(2).setPreferredWidth(50); // cód cliente (int) 
        jTSaldoDetallado.getColumnModel().getColumn(2).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(3).setPreferredWidth(150); // cliente (string) 
        jTSaldoDetallado.getColumnModel().getColumn(3).setCellRenderer(new StringCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(4).setPreferredWidth(50); // nro doc (int)
        jTSaldoDetallado.getColumnModel().getColumn(4).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(5).setPreferredWidth(40); // tipo doc (string)
        jTSaldoDetallado.getColumnModel().getColumn(5).setCellRenderer(new StringCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(6).setPreferredWidth(60); // fec. doc (string)
        jTSaldoDetallado.getColumnModel().getColumn(6).setCellRenderer(new DateCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(7).setPreferredWidth(40); // cuota (String)
        jTSaldoDetallado.getColumnModel().getColumn(7).setCellRenderer(new StringCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(8).setPreferredWidth(60); // vencimiento (string)
        jTSaldoDetallado.getColumnModel().getColumn(8).setCellRenderer(new DateCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(9).setPreferredWidth(30); // días vencidos (int)
        jTSaldoDetallado.getColumnModel().getColumn(9).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(10).setPreferredWidth(60); // fec. pago (string)
        jTSaldoDetallado.getColumnModel().getColumn(10).setCellRenderer(new DateCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(11).setPreferredWidth(40); // nro. pago (int)
        jTSaldoDetallado.getColumnModel().getColumn(11).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(12).setPreferredWidth(40); // nro. recibo (int)
        jTSaldoDetallado.getColumnModel().getColumn(12).setCellRenderer(new NumberCellRender());
        jTSaldoDetallado.getColumnModel().getColumn(13).setPreferredWidth(80); // valor doc (double)
        jTSaldoDetallado.getColumnModel().getColumn(13).setCellRenderer(new DecimalCellRender(0));
        jTSaldoDetallado.getColumnModel().getColumn(14).setPreferredWidth(90); // total + interés (double)
        jTSaldoDetallado.getColumnModel().getColumn(14).setCellRenderer(new DecimalCellRender(0));
        
        // implementacion del button - detalle
        ButtonDetalleDocCuentaCliente button = new ButtonDetalleDocCuentaCliente(jTSaldoDetallado, 15);
        jTSaldoDetallado.getColumnModel().getColumn(15).setPreferredWidth(50); // detalles
        
        utiles.Utiles.punteroTablaF(jTSaldoDetallado, this);        
        jTSaldoDetallado.setFont(new Font("Tahoma", 1, 12) );
        jTSaldoDetallado.setRowHeight(20);
    }
    
    private void limpiarTablaResumido(){
        for(int i = 0; i < jTSaldoCuenta.getRowCount(); i++){
            dtmSaldoCuenta.removeRow(i);
            i--;
        }
    }
    
    private void limpiarTablaDetallado(){
        for(int i = 0; i < jTSaldoDetallado.getRowCount(); i++){
            dtmSaldoCuentaDetallado.removeRow(i);
            i--;
        }
    }
    
    private void calculoInteresTablaPorCliente(){
        sm = new StatementManager();
        String codCliente = "";
        double pct_interes = Double.parseDouble(jTFInteres.getText().trim());
        String sql = "";
        
        if(jTFCodCliente.getText().trim().equals("0")){
            for(int i = 0; i < jTSaldoCuenta.getRowCount(); i++){
                codCliente = jTSaldoCuenta.getValueAt(i, 0).toString();
                tabla_interesTableSaldoCuenta = 0; 
                sql = 
                  "SELECT nro_comprob, monto_cuota, tip_comprob, "
                + "CASE WHEN current_date - fec_vencimiento::date <= 0 THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos "
                + "FROM venta_det_cuotas "
                + "WHERE cod_cliente = " + codCliente 
                + " AND nro_pago = 0 "
                + "AND estado = 'V' "
                + "ORDER BY dias_vencidos DESC";
                try{
                    sm.TheSql = sql;
                    sm.EjecutarSql();

                    if(sm.TheResultSet != null){
                        while(sm.TheResultSet.next()){
                            double dias_vencidos = sm.TheResultSet.getDouble("dias_vencidos");
                            double monto_cuota = sm.TheResultSet.getDouble("monto_cuota");
                            String tipo_comprob = sm.TheResultSet.getString("tip_comprob");
                            double interes = 0;

                            // -- cálculo de interés
                            if(tipo_comprob.equalsIgnoreCase("NCC") || tipo_comprob.equalsIgnoreCase("CRE")){
                            }else{
                                interes = Math.round(((monto_cuota * pct_interes)/100) * (dias_vencidos/30));
                            }
                            tabla_interesTableSaldoCuenta += interes;                            
                        }
                        jTSaldoCuenta.setValueAt(tabla_interesTableSaldoCuenta, i, 6);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }finally{
                    DBManager.CerrarStatements();
                }
            }
            System.out.println("CALCULO DE INTERES - TODOS LOS CLIENTES: " + sql);
        }else{
                codCliente = jTFCodCliente.getText().trim();
                
                sql = 
                  "SELECT nro_comprob, monto_cuota, tip_comprob, "
                + "CASE WHEN current_date - fec_vencimiento::date <= 0 THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos "
                + "FROM venta_det_cuotas "
                + "WHERE cod_cliente = " + codCliente 
                + " AND nro_pago = 0 "
                + "AND estado = 'V' "
                + "ORDER BY dias_vencidos DESC";
            try{
                sm.TheSql = sql;
                sm.EjecutarSql();

                if(sm.TheResultSet != null){
                    while(sm.TheResultSet.next()){
                        double dias_vencidos = sm.TheResultSet.getDouble("dias_vencidos");
                        double monto_cuota = sm.TheResultSet.getDouble("monto_cuota");
                        String tipo_comprob = sm.TheResultSet.getString("tip_comprob");
                        double interes = 0;

                        // -- cálculo de interés
                        if(tipo_comprob.equalsIgnoreCase("NCC") || tipo_comprob.equalsIgnoreCase("CRE")){
                        }else{
                            interes = Math.round(((monto_cuota * pct_interes)/100) * (dias_vencidos/30));
                        }
                        tabla_interesTableSaldoCuenta += interes;                        
                    }
                    jTSaldoCuenta.setValueAt(tabla_interesTableSaldoCuenta, 0, 6);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                DBManager.CerrarStatements();
            }
        }
    }
    
    private void calculoInteresTablaDetallado(){
        double dias_vencidos = 0, monto_cuota = 0, interes, total = 0;        
        String tipo_documento = "";
        for(int i = 0;i < jTSaldoDetallado.getRowCount(); i++){
            tipo_documento = jTSaldoDetallado.getValueAt(i, 5).toString().trim();
            monto_cuota = Double.parseDouble(jTSaldoDetallado.getValueAt(i, 13).toString());
            interes = 0;
            if(tipo_documento.equalsIgnoreCase("CRE") || tipo_documento.equalsIgnoreCase("NCC")){
            }else{
                dias_vencidos = Double.parseDouble(jTSaldoDetallado.getValueAt(i, 9).toString());
                interes = Math.round(((monto_cuota * pct_interes)/100) * (dias_vencidos/30));
            }
            total = monto_cuota + interes;
            jTSaldoDetallado.setValueAt(total, i, 14);
        }
    }
    
    private void llenarTablaResumido(){
        sm = new StatementManager();
        String codCliente = jTFCodCliente.getText().trim();
        String sql = "";
        String orden = "";
        
        if(jRBNombre.isSelected()){
            orden = "cliente";
        }
        
        if(jRBCodigo.isSelected()){
            orden = "cod_cliente";
        }
        
        if(!codCliente.equals("0")){
             sql = 
                  "SELECT DISTINCT v.cod_cliente, c.razon_soc || ' - ' || c.telefono AS cliente, c.limite_credito, "
                + "(SELECT to_char(MAX(fec_recibo), 'dd/MM/yyyy') FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente) AS ultimo_pago, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente AND tip_comprob IN ('NCC', 'CRE') AND nro_pago = 0 "
                + "AND estado = 'V') AS credito, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente "
                + "AND current_date - fec_vencimiento::date > 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS vencidos, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente "
                + "AND current_date - fec_vencimiento::date <= 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS por_vencer "
                + "FROM venta_det_cuotas v "
                + "INNER JOIN cliente c "
                + "ON v.cod_cliente = c.cod_cliente "
                + "WHERE v.cod_cliente = " + codCliente;
        }else{
            sql = 
                  "SELECT DISTINCT v.cod_cliente, c.razon_soc || ' - ' || c.telefono AS cliente, c.limite_credito, "
                + "(SELECT to_char(MAX(fec_recibo), 'dd/MM/yyyy') FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente) AS ultimo_pago, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente AND tip_comprob IN ('NCC', 'CRE') AND nro_pago = 0 "
                + "AND estado = 'V') AS credito, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente "
                + "AND current_date - fec_vencimiento::date > 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS vencidos, "
                + "(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente "
                + "AND current_date - fec_vencimiento::date <= 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS por_vencer "
                + "FROM venta_det_cuotas v "
                + "INNER JOIN cliente c "
                + "ON v.cod_cliente = c.cod_cliente "
                + "ORDER BY " + orden;
        }                
        
        try{
            sm.TheSql = sql;
            System.out.println("SQL DOCS PENDIENTES X CLIENTE: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[11];
                    row[0] = sm.TheResultSet.getInt("cod_cliente");
                    row[1] = sm.TheResultSet.getString("cliente");
                    row[2] = sm.TheResultSet.getDouble("limite_credito");
                    row[3] = sm.TheResultSet.getString("ultimo_pago");
                    row[4] = sm.TheResultSet.getDouble("credito");;
                    row[5] = sm.TheResultSet.getDouble("vencidos");;
                    row[6] = tabla_interesTableSaldoCuenta;//double - interés                  
                    row[7] = sm.TheResultSet.getDouble("por_vencer");
                    row[10] = pct_interes;
                    dtmSaldoCuenta.addRow(row);
                }
                jTSaldoCuenta.updateUI();
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
    
    private void llenarTablaDetallado(){
        sm = new StatementManager();
        String codCliente = jTFCodCliente.getText().trim();
        String sql = "";
        String orden = "";
        
        if(jRBNombre.isSelected()){
            orden = "razon_soc";
        }
        
        if(jRBCodigo.isSelected()){
            orden = "cod_cliente";
        }
        
        if(!codCliente.equals("0")){
            sql = 
                      "SELECT DISTINCT v.cod_local, v.cod_caja, v.cod_cliente, c.razon_soc, v.nro_comprob, v.tip_comprob, "
                    + "to_char(v.fec_comprob, 'dd/MM/yyyy') AS fec_documento, "
                    + "v.nro_cuota || '/' || v.can_cuota AS cuota, to_char(v.fec_vencimiento , 'dd/MM/yyyy') AS fec_vencimiento, "
                    + "to_char(v.fec_recibo, 'dd/MM/yyyy') AS fec_pago, v.nro_pago, v.nro_recibo, v.monto_cuota, "
                    + "CASE WHEN current_date - fec_vencimiento::date <= 0 "
                    + "THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos "
                    + "FROM venta_det_cuotas v "
                    + "INNER JOIN cliente c "
                    + "ON v.cod_cliente = c.cod_cliente "
                    + "WHERE v.nro_pago = 0 "
                    + "AND v.estado = 'V' "
                    + "AND v.cod_cliente = " + codCliente + " "
                    + "ORDER BY " + orden + ", fec_documento desc ";
        }else{
            sql = 
                      "SELECT DISTINCT v.cod_local, v.cod_caja, v.cod_cliente, c.razon_soc, v.nro_comprob, v.tip_comprob, "
                    + "to_char(v.fec_comprob, 'dd/MM/yyyy') AS fec_documento, "
                    + "v.nro_cuota || '/' || v.can_cuota AS cuota, to_char(v.fec_vencimiento , 'dd/MM/yyyy') AS fec_vencimiento, "
                    + "to_char(v.fec_recibo, 'dd/MM/yyyy') AS fec_pago, v.nro_pago, v.nro_recibo, v.monto_cuota, "
                    + "CASE WHEN current_date - fec_vencimiento::date <= 0 "
                    + "THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos "
                    + "FROM venta_det_cuotas v "
                    + "INNER JOIN cliente c "
                    + "ON v.cod_cliente = c.cod_cliente "
                    + "WHERE v.nro_pago = 0 "
                    + "AND v.estado = 'V' "
                    + "ORDER BY " + orden + ", fec_documento desc ";
        }
        
        try{
            sm.TheSql = sql;
            System.out.println("SQL DOCS PENDIENTES X CLIENTE: " + sql);
            
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[16];
                    row[0] = sm.TheResultSet.getInt("cod_local");
                    row[1] = sm.TheResultSet.getInt("cod_caja");
                    row[2] = sm.TheResultSet.getInt("cod_cliente");
                    row[3] = sm.TheResultSet.getString("razon_soc");
                    row[4] = sm.TheResultSet.getInt("nro_comprob");
                    row[5] = sm.TheResultSet.getString("tip_comprob");
                    row[6] = sm.TheResultSet.getString("fec_documento");
                    row[7] = sm.TheResultSet.getString("cuota");
                    row[8] = sm.TheResultSet.getString("fec_vencimiento");
                    row[9] = sm.TheResultSet.getInt("dias_vencidos");
                    row[10] = sm.TheResultSet.getString("fec_pago");
                    row[11] = sm.TheResultSet.getInt("nro_pago");
                    row[12] = sm.TheResultSet.getInt("nro_recibo");
                    row[13] = sm.TheResultSet.getDouble("monto_cuota");
                    row[14] = 0;
                    
                    dtmSaldoCuentaDetallado.addRow(row);
                }
                jTSaldoDetallado.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No exiten detalles para los criterios seleccionados!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        
    }
    
    private void interesMasSaldo(){
        double credito = 0, docsVencidos = 0, interes = 0, docsAVencer = 0;
        for(int i = 0; i < jTSaldoCuenta.getRowCount(); i++){
            credito = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 4).toString()) * (-1);
            docsVencidos = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 5).toString());
            interes = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 6).toString());
            docsAVencer = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 7).toString());
            double saldoMasInteres = credito + docsVencidos + interes + docsAVencer;
            jTSaldoCuenta.setValueAt(saldoMasInteres, i, 8);
        }
    }
    
    private void totalizar(){
        double totalCredito = 0, totalDocsVencidos = 0, totalInteres = 0, totalDocsAVencer = 0, saldoSinInteres = 0, saldoMasInteres = 0;
        for(int i = 0; i < jTSaldoCuenta.getRowCount(); i++){
            totalCredito += Double.parseDouble(jTSaldoCuenta.getValueAt(i, 4).toString());
            totalDocsVencidos += Double.parseDouble(jTSaldoCuenta.getValueAt(i, 5).toString());
            totalInteres += Double.parseDouble(jTSaldoCuenta.getValueAt(i, 6).toString());
            totalDocsAVencer += Double.parseDouble(jTSaldoCuenta.getValueAt(i, 7).toString());            
        }
        
        saldoSinInteres = totalDocsVencidos + totalDocsAVencer - totalCredito;
        saldoMasInteres = totalDocsAVencer + totalDocsVencidos + totalInteres - totalCredito;
        
        jTFTotalCredito.setText(decimalFormat.format(totalCredito));
        jTFTotalDocsVencidos.setText(decimalFormat.format(totalDocsVencidos));
        jTFTotalInteres.setText(decimalFormat.format(totalInteres));
        jTFTotalDocsAVencer.setText(decimalFormat.format(totalDocsAVencer));
        jTFSaldoSinInteres.setText(decimalFormat.format(saldoSinInteres));
        jTFSaldoConInteres.setText(decimalFormat.format(saldoMasInteres));
    }
        
    
    private void totalizarDetallado(){
        double totalCredito = 0, totalDocsVencidos = 0, totalInteres = 0, totalDocsAVencer = 0, saldoSinInteres = 0, saldoMasInteres = 0, 
               sumaTotalMontoCuota = 0, sumaTotalMontoCuotaMasInteres = 0;
        for(int i = 0; i < jTSaldoDetallado.getRowCount(); i++){
            String tip_doc = jTSaldoDetallado.getValueAt(i, 5).toString().trim();
            int diasVencidos = Integer.parseInt(jTSaldoDetallado.getValueAt(i, 9).toString());            
            
            if(tip_doc.equalsIgnoreCase("CRE") || tip_doc.equalsIgnoreCase("NCC")){
                totalCredito += Double.parseDouble(jTSaldoDetallado.getValueAt(i, 13).toString());
            }else{
                
                if(diasVencidos > 0){
                    totalDocsVencidos += Double.parseDouble(jTSaldoDetallado.getValueAt(i, 13).toString());
                }
                
                sumaTotalMontoCuota += Double.parseDouble(jTSaldoDetallado.getValueAt(i, 13).toString());
                sumaTotalMontoCuotaMasInteres += Double.parseDouble(jTSaldoDetallado.getValueAt(i, 14).toString());
            }                        
            
            totalInteres = sumaTotalMontoCuotaMasInteres - sumaTotalMontoCuota;
            
            if(diasVencidos <= 0){
                totalDocsAVencer += Double.parseDouble(jTSaldoDetallado.getValueAt(i, 13).toString());
            }                        
        }
        
        System.out.println("TOTAL CREDITO: " + totalCredito);
        saldoSinInteres = sumaTotalMontoCuota - totalCredito;
        saldoMasInteres = sumaTotalMontoCuotaMasInteres - totalCredito;
        
        jTFTotalCredito.setText(decimalFormat.format(totalCredito));
        jTFTotalDocsVencidos.setText(decimalFormat.format(totalDocsVencidos));
        jTFTotalInteres.setText(decimalFormat.format(totalInteres));
        jTFTotalDocsAVencer.setText(decimalFormat.format(totalDocsAVencer));
        jTFSaldoSinInteres.setText(decimalFormat.format(saldoSinInteres));
        jTFSaldoConInteres.setText(decimalFormat.format(saldoMasInteres));
    }
    
    private void deleteTablaInforme() {
        try{
            String sql = "BEGIN; "
                       + "DELETE FROM inform_cta_clientes_cab; "
                       + "DELETE FROM inform_cta_clientes_det; "
                       + "COMMIT;";
            System.out.println("DELETE FROM TABLA INFORME: " + sql);
            DBManager.ejecutarDML(sql);
            DBManager.conn.commit();
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }

    private void insertTablaInformeCab() {
        String cod_empresa = jCBCodEmpresa.getSelectedItem().toString();
        String cod_local = jCBCodLocal.getSelectedItem().toString();
        double pct_interes = Double.parseDouble(jTFInteres.getText().trim());
        double total_credito = Double.parseDouble(jTFTotalCredito.getText().trim().replace(",", ""));
        double total_vencidos = Double.parseDouble(jTFTotalDocsVencidos.getText().trim().replace(",", ""));
        double total_interes = Double.parseDouble(jTFTotalInteres.getText().trim().replace(",", ""));
        double docs_a_vencer = Double.parseDouble(jTFTotalDocsAVencer.getText().trim().replace(",", ""));
        double saldo_sin_interes = Double.parseDouble(jTFSaldoSinInteres.getText().trim().replace(",", ""));
        double saldo_mas_interes = Double.parseDouble(jTFSaldoConInteres.getText().trim().replace(",", ""));
        
        try{
            String sql = "INSERT INTO inform_cta_clientes_cab (cod_empresa, cod_local, pct_interes, total_credito, total_vencidos, total_interes, "
                       + "docs_a_vencer, saldo_sin_interes, saldo_mas_interes) "
                       + "VALUES (" + cod_empresa + ", " + cod_local + ", " + pct_interes + ", " + total_credito + ", " + total_vencidos + ", "
                       + total_interes + ", " + docs_a_vencer + ", " + saldo_sin_interes + ", " + saldo_mas_interes + ");" ;
            System.out.println("INSERT EN TABLA INFORME: " + sql);
            DBManager.ejecutarDML(sql);
            DBManager.conn.commit();
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }

    private void insertTablaInformeDet(){
        String fec_ultimo_pago = "01/01/1990";
        try{
            for(int i = 0; i < jTSaldoCuenta.getRowCount(); i++){
                String cod_cliente = jTSaldoCuenta.getValueAt(i, 0).toString();
                String razon_soc = jTSaldoCuenta.getValueAt(i, 1).toString();
                double limite_credito = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 2).toString());
                fec_ultimo_pago = jTSaldoCuenta.getValueAt(i, 3).toString();
                double credito = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 4).toString());
                double docs_vencidos = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 5).toString());
                double interes = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 6).toString());
                double docs_a_vencer = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 7).toString());
                double saldo_mas_interes = Double.parseDouble(jTSaldoCuenta.getValueAt(i, 8).toString());
                
                if(fec_ultimo_pago.equals("")){
                    fec_ultimo_pago = "01/01/1990";
                }
                System.out.println("FECHA DE ULTIMO PAGO: " + fec_ultimo_pago);
                
                String sql = "INSERT INTO inform_cta_clientes_det (cod_cliente, razon_soc, limite_credito, fec_ultimo_pago, credito, docs_vencidos, "
                           + "interes, docs_a_vencer, saldo_mas_interes) "
                           + "VALUES (" + cod_cliente + ", '" + razon_soc + "', " + limite_credito + ", '" + fec_ultimo_pago + "'::date, "
                           + credito + ", " + docs_vencidos + ", " + interes + ", " + docs_a_vencer + ", " + saldo_mas_interes + ")";
                
                System.out.println("SQL INSERT DETALLE INFORME: " + sql);
                DBManager.ejecutarDML(sql);
                DBManager.conn.commit();
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }
    
    private void generarReporte() {
        
        String sql = "SELECT d.cod_cliente, d.razon_soc, d.limite_credito, to_char(d.fec_ultimo_pago, 'dd/MM/yyyy') AS fec_ultimo_pago, "
                   + "d.credito, d.docs_vencidos, d.interes, d.docs_a_vencer, d.saldo_mas_interes, c.cod_empresa, c.cod_local, c.pct_interes, "
                   + "c.total_credito, c.total_vencidos, c.total_interes, c.docs_a_vencer AS total_docs_a_vencer, c.saldo_sin_interes, c.saldo_mas_interes AS total_mas_interes "
                   + "FROM inform_cta_clientes_cab c, inform_cta_clientes_det d";
        String empresa = jLNombreEmpresa.getText().trim();
        String local = jLNombreLocal.getText().trim();
        try{
            LibReportes.parameters.put("pEmpresa", empresa);
            LibReportes.parameters.put("pLocal", local);
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " " + FormMain.nombreUsuario);
            LibReportes.parameters.put("pFecActual", utiles.Utiles.getSysDateTimeString());
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "informe_cuenta_clientes_resumido");
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

        bGDocsSegunPago = new javax.swing.ButtonGroup();
        bGTipoInforme = new javax.swing.ButtonGroup();
        bGOrdenar = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreLocal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTFInteres = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jRBPendientes = new javax.swing.JRadioButton();
        jRBPagados = new javax.swing.JRadioButton();
        jRBTodos = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jRBDetallado = new javax.swing.JRadioButton();
        jRBResumido = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jRBNombre = new javax.swing.JRadioButton();
        jRBCodigo = new javax.swing.JRadioButton();
        jBProcesar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPResumido = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTSaldoCuenta = new javax.swing.JTable();
        jPDetallado = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTSaldoDetallado = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTFTotalCredito = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFTotalDocsVencidos = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFTotalInteres = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFTotalDocsAVencer = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFSaldoSinInteres = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFSaldoConInteres = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe de Cuentas de Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("INFORME DE CUENTAS DE CLIENTES");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Empresa:");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Local:");

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCliente.setText("0");
        jTFCodCliente.setToolTipText("F1 Búsqueda de Clientes");
        jTFCodCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodClienteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodClienteFocusLost(evt);
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
        jTFInteres.setText("0");
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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("%");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFInteres, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                            .addComponent(jTFCodCliente, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Docs según pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBPendientes.setBackground(new java.awt.Color(204, 255, 204));
        bGDocsSegunPago.add(jRBPendientes);
        jRBPendientes.setSelected(true);
        jRBPendientes.setText("Pendientes de pago");

        jRBPagados.setBackground(new java.awt.Color(204, 255, 204));
        bGDocsSegunPago.add(jRBPagados);
        jRBPagados.setText("Pagados");
        jRBPagados.setEnabled(false);

        jRBTodos.setBackground(new java.awt.Color(204, 255, 204));
        bGDocsSegunPago.add(jRBTodos);
        jRBTodos.setText("Todos");
        jRBTodos.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBPendientes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBPagados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBTodos)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBPendientes)
                    .addComponent(jRBPagados)
                    .addComponent(jRBTodos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Tipo de Informe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBDetallado.setBackground(new java.awt.Color(204, 255, 204));
        bGTipoInforme.add(jRBDetallado);
        jRBDetallado.setText("Detallado");
        jRBDetallado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBDetalladoActionPerformed(evt);
            }
        });

        jRBResumido.setBackground(new java.awt.Color(204, 255, 204));
        bGTipoInforme.add(jRBResumido);
        jRBResumido.setSelected(true);
        jRBResumido.setText("Resumido");
        jRBResumido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBResumidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBDetallado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBResumido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBDetallado)
                    .addComponent(jRBResumido))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ordenar por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBNombre.setBackground(new java.awt.Color(204, 255, 204));
        bGOrdenar.add(jRBNombre);
        jRBNombre.setSelected(true);
        jRBNombre.setText("Nombre");

        jRBCodigo.setBackground(new java.awt.Color(204, 255, 204));
        bGOrdenar.add(jRBCodigo);
        jRBCodigo.setText("Código");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBCodigo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBNombre)
                    .addComponent(jRBCodigo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBProcesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBProcesar.setText("Procesar");
        jBProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBProcesarActionPerformed(evt);
            }
        });

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
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

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 204));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPResumido.setBackground(new java.awt.Color(204, 255, 204));

        jTSaldoCuenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Cliente", "Límite Créd.", "Féc. Últ. Pago", "Crédito", "Docs. Vencidos", "Interés", "Docs. a vencer", "Saldo + interés", "Detalles", "Interes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTSaldoCuenta);
        if (jTSaldoCuenta.getColumnModel().getColumnCount() > 0) {
            jTSaldoCuenta.getColumnModel().getColumn(0).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(1).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(2).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(3).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(4).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(5).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(6).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(7).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(8).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(9).setResizable(false);
            jTSaldoCuenta.getColumnModel().getColumn(10).setResizable(false);
        }

        javax.swing.GroupLayout jPResumidoLayout = new javax.swing.GroupLayout(jPResumido);
        jPResumido.setLayout(jPResumidoLayout);
        jPResumidoLayout.setHorizontalGroup(
            jPResumidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPResumidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPResumidoLayout.setVerticalGroup(
            jPResumidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPResumidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Saldo de cuentas ", jPResumido);

        jTSaldoDetallado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Local", "Caja", "Código", "Cliente", "Nro. Doc.", "Tipo", "Fec. Doc. ", "Cuota", "Vencimiento", "Días Venc.", "Fec. Pago", "Pago", "Recibo", "Valor Doc", "Total + Interés", "Detalles"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTSaldoDetallado);
        if (jTSaldoDetallado.getColumnModel().getColumnCount() > 0) {
            jTSaldoDetallado.getColumnModel().getColumn(0).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(1).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(2).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(3).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(4).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(5).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(6).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(7).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(8).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(9).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(10).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(11).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(12).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(13).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(14).setResizable(false);
            jTSaldoDetallado.getColumnModel().getColumn(15).setResizable(false);
        }

        javax.swing.GroupLayout jPDetalladoLayout = new javax.swing.GroupLayout(jPDetallado);
        jPDetallado.setLayout(jPDetalladoLayout);
        jPDetalladoLayout.setHorizontalGroup(
            jPDetalladoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPDetalladoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPDetalladoLayout.setVerticalGroup(
            jPDetalladoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPDetalladoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Detallado", jPDetallado);

        jPanel8.setBackground(new java.awt.Color(204, 255, 204));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Totales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Total Crédito Cliente:");

        jTFTotalCredito.setEditable(false);
        jTFTotalCredito.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTotalCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCredito.setText("0");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Total docs vencidos:");

        jTFTotalDocsVencidos.setEditable(false);
        jTFTotalDocsVencidos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTotalDocsVencidos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalDocsVencidos.setText("0");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Total intereses:");

        jTFTotalInteres.setEditable(false);
        jTFTotalInteres.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTotalInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalInteres.setText("0");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Total docs a vencer:");

        jTFTotalDocsAVencer.setEditable(false);
        jTFTotalDocsAVencer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTotalDocsAVencer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalDocsAVencer.setText("0");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Saldo sin interés:");

        jTFSaldoSinInteres.setEditable(false);
        jTFSaldoSinInteres.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFSaldoSinInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFSaldoSinInteres.setText("0");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Saldo con interés:");

        jTFSaldoConInteres.setEditable(false);
        jTFSaldoConInteres.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFSaldoConInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFSaldoConInteres.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFTotalCredito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFTotalDocsVencidos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFTotalInteres))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFTotalDocsAVencer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFSaldoSinInteres))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTFSaldoConInteres))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTotalDocsVencidos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTotalInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTotalDocsAVencer, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldoSinInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldoConInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jBProcesar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBImprimir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBImprimir, jBProcesar, jBSalir});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jBProcesar)
                                    .addComponent(jBImprimir)
                                    .addComponent(jBSalir))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

    private void jTFCodClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCodCliente.getText().trim().equals("0")){
                jLNombreCliente.setText("TODOS");
                jTFInteres.requestFocus();
            }else if(jTFCodCliente.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else{
                getDatosCliente(jTFCodCliente.getText().trim()); 
                jTFInteres.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            BuscaCliente bCliente = new BuscaCliente(new JFrame(), true);
            bCliente.pack();
            bCliente.setVisible(true);
            if(bCliente.codigo != 0){
                jTFCodCliente.setText(String.valueOf(bCliente.codigo));
            }
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFInteresFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFInteresFocusGained
        jTFInteres.selectAll();
    }//GEN-LAST:event_jTFInteresFocusGained

    private void jTFInteresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFInteresKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBProcesar.grabFocus();
        }
    }//GEN-LAST:event_jTFInteresKeyPressed

    private void jBProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBProcesarActionPerformed
        jTFTotalCredito.setText("0");
        jTFTotalDocsVencidos.setText("0");
        jTFTotalInteres.setText("0");
        jTFTotalDocsAVencer.setText("0");
        jTFSaldoSinInteres.setText("0");
        jTFSaldoConInteres.setText("0");
        
        if(jRBResumido.isSelected()){
            limpiarTablaResumido();
            tabla_interesTableSaldoCuenta = 0;
            llenarTablaResumido();        
            calculoInteresTablaPorCliente();
            interesMasSaldo();           
            totalizar();
        }
        
        if(jRBDetallado.isSelected()){
            limpiarTablaDetallado();
            llenarTablaDetallado();
            calculoInteresTablaDetallado();
            totalizarDetallado();
        }
        
    }//GEN-LAST:event_jBProcesarActionPerformed

    private void jTFCodClienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusLost
        if(jTFCodCliente.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "ATENCIÓN", JOptionPane.INFORMATION_MESSAGE);
            jTFCodCliente.grabFocus();
        }
    }//GEN-LAST:event_jTFCodClienteFocusLost

    private void jRBDetalladoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBDetalladoActionPerformed
        jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPDetallado));
    }//GEN-LAST:event_jRBDetalladoActionPerformed

    private void jRBResumidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBResumidoActionPerformed
        jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPResumido));
    }//GEN-LAST:event_jRBResumidoActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        if(jTabbedPane1.getSelectedIndex() == jTabbedPane1.indexOfComponent(jPResumido)){
            jRBResumido.setSelected(true);
        }else{
            jRBDetallado.setSelected(true);
        }
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(jTSaldoCuenta.getRowCount() > 0){
            if(jRBResumido.isSelected()){
                deleteTablaInforme();
                insertTablaInformeCab();
                insertTablaInformeDet();
                generarReporte();
            }
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
            java.util.logging.Logger.getLogger(CuentasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CuentasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CuentasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CuentasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CuentasClientes dialog = new CuentasClientes(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGDocsSegunPago;
    private javax.swing.ButtonGroup bGOrdenar;
    private javax.swing.ButtonGroup bGTipoInforme;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBProcesar;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
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
    private javax.swing.JPanel jPDetallado;
    private javax.swing.JPanel jPResumido;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRBCodigo;
    private javax.swing.JRadioButton jRBDetallado;
    private javax.swing.JRadioButton jRBNombre;
    private javax.swing.JRadioButton jRBPagados;
    private javax.swing.JRadioButton jRBPendientes;
    private javax.swing.JRadioButton jRBResumido;
    private javax.swing.JRadioButton jRBTodos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFInteres;
    private javax.swing.JTextField jTFSaldoConInteres;
    private javax.swing.JTextField jTFSaldoSinInteres;
    private javax.swing.JTextField jTFTotalCredito;
    private javax.swing.JTextField jTFTotalDocsAVencer;
    private javax.swing.JTextField jTFTotalDocsVencidos;
    private javax.swing.JTextField jTFTotalInteres;
    private javax.swing.JTable jTSaldoCuenta;
    private javax.swing.JTable jTSaldoDetallado;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
    
}
