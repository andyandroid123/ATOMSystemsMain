/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author Andres
 */
public class ButtonReImpresionDocsVentas extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{

    JTable table;
    JButton renderButton;
    JButton detallesButton;
    String text;
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    
    public ButtonReImpresionDocsVentas(JTable table, int column){
        super();
        this.table = table;
        renderButton = new JButton();
        detallesButton = new JButton();
        detallesButton.setFocusPainted(false);
        detallesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir16.png")));
        renderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir16.png")));
        detallesButton.addActionListener(this);
        renderButton.addActionListener(this);
        TableColumnModel columnModel = table.getColumnModel();  
        columnModel.getColumn(column).setCellRenderer( this );  
        columnModel.getColumn(column).setCellEditor( this );
        getDatosReport();
    }
    
    @Override
    public Object getCellEditorValue() {
        return text;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (hasFocus)  
        {  
            renderButton.setForeground(table.getForeground());  
            renderButton.setBackground(UIManager.getColor("Button.background"));  
        }  
        else if (isSelected)  
        {  
            renderButton.setForeground(table.getSelectionForeground());  
            renderButton.setBackground(table.getSelectionBackground());  
        }  
        else  
        {  
            renderButton.setForeground(table.getForeground());  
            renderButton.setBackground(UIManager.getColor("Button.background"));  
        }  

        //renderButton.setText( (value == null) ? "" : value.toString() );
        renderButton.setText("");
        //renderButton.setIcon(new ImageIcon("File.gif"));
        return renderButton; 
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        text = "";
        detallesButton.setText( text );  
        return detallesButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        imprimirComprobante();
    }
    
    private void imprimirComprobante(){
        int row = table.getSelectedRow();
        String razon_soc = utiles.Utiles.getRazonSocialEmpresa(utiles.Utiles.getCodEmpresaDefault());
        int nroTicket = Integer.parseInt(table.getValueAt(row, 1).toString());
        String cajero = table.getValueAt(row, 7).toString();
        String mensaje = getMsgPieBoleta();
        String cliente = table.getValueAt(row, 2).toString();
        int terminal = Integer.parseInt(table.getValueAt(row, 0).toString());
        String fecha = table.getValueAt(row, 3).toString();
        int turno = Integer.parseInt(table.getValueAt(row, 6).toString());
        
        System.out.println("EMPRESA: " + razon_soc);
        
        
        String sql = "SELECT DISTINCT venta_cab.nro_ticket, venta_cab.cod_caja, venta_det.cod_articulo, articulo.des_corta, "
                   + "venta_det.cansi_venta || '-' || venta_det.sigla_venta AS sigla, venta_det.can_venta, "
                   + "(venta_det.mon_venta / venta_det.can_venta) AS precio, venta_det.mon_venta, venta_cab.mon_descuento, "
                   + "venta_cab.fec_comprob "
                   + "FROM venta_cab "
                   + "INNER JOIN venta_det "
                   + "ON venta_cab.nro_ticket = venta_det.nro_ticket "
                   + "INNER JOIN articulo "
                   + "ON venta_det.cod_articulo = articulo.cod_articulo "
                   + "WHERE venta_cab.nro_ticket = " + nroTicket + " AND venta_cab.cod_caja = " + terminal + " AND "
                   + "venta_cab.fec_comprob::date = '" + fecha + "'::date AND venta_cab.nro_turno = " + turno + " "
                   + "AND venta_det.cod_caja = " + terminal + " AND venta_det.nro_ticket = " + nroTicket + " "
                   + "AND venta_det.nro_turno = " + turno;
        
        System.out.println("IMPRESION DE BOLETA DE VENTA: " + sql);
        
        String tipo_impresion = "";
        
        if(getTipoImpresion().equals("S")){
            tipo_impresion = "boletaVenta";
        }else{
            tipo_impresion = "boletaVentaHoja";
        }
        
        try{
            LibReportes.parameters.put("pRazonSocEmpresa", razon_soc);
            LibReportes.parameters.put("pActividadEmpresa", actividadEmpresa);
            LibReportes.parameters.put("pDireccionEmpresa", direccionEmpresa);
            LibReportes.parameters.put("pCiudadEmpresa", ciudadEmpresa);
            LibReportes.parameters.put("pTelEmpresa", telEmpresa);
            LibReportes.parameters.put("pNroTicket", nroTicket);
            LibReportes.parameters.put("pCajero", cajero);
            LibReportes.parameters.put("pMsgPieBoleta", mensaje);
            LibReportes.parameters.put("pNombreCliente", cliente);
            LibReportes.parameters.put("pTerminal", terminal);
            LibReportes.parameters.put("pFecVigencia", fecha);
            LibReportes.parameters.put("pNroTurno", turno);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, tipo_impresion);
            //LibReportes.generarReportes(sql, "boletaVenta");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private String getTipoImpresion(){
        String tipo = "";
        try{
            String sql = "SELECT valor FROM parametros WHERE parametro = 'CAJA_IMPRIME_COMP_TICKET_JASPER'"; 
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    tipo = rs.getString("valor");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return tipo;
    }
    
    private void getDatosReport(){
    
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
    
    private String getMsgPieBoleta(){
        String mensaje = "";
        try{
            String sql = "SELECT valor FROM parametros WHERE parametro = 'CAJA_MSG_PIE_BOLETA'"; 
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    mensaje = rs.getString("valor");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return mensaje;
    }
    
}
