/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import ModRegistros.Clientes;
import beans.ClienteBean;
import controls.ClienteCtrl;
import controls.EmpleadoCtrl;
import java.awt.Color;
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
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.CajaMain;
import principal.FormMain;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.StatementManager;
import utiles.Utiles;
import views.busca.BuscaArticuloVenta;
import views.busca.BuscaCliente;
import views.busca.DlgConsultasCuentasMonedas;

/**
 *
 * @author Andres
 */
public class Ventas extends javax.swing.JFrame {

    private TimerTask task;
    private Timer tiempo;
    private int indice = 0;
    private int speed = 80;
    
    private JLabel jLBarrasArticulo = new javax.swing.JLabel();
    private JTextField jTFSVta = new javax.swing.JTextField();
    private JTextField jTFIva = new javax.swing.JTextField();
    
    
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    
    DefaultTableModel dtmDetallesVenta;
    DefaultTableModel dtmFormaPago;
    
    private int filaExistente;
    private boolean esModificacion;
    private double montoRecibido;
    private String vCodigoArticulo, vBarrasArticulo, VSiglaVenta;
    private String fecVigencia;
    String vNroComprob = "", nroTurno = "", codCajero = "";
    
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    
    public Ventas() {
        initComponents();
        jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPVentas));
        cerrarVentana();
        configCampos();
                
        getDatosTurnoAbierto();
        vNroComprob = getNroComprobVta();
        jBTotalizar.setVisible(false);
        jTFCodCliente.setText("1");
        jTFCodCuenta.setText("1");
        configTabla();
        configTablaFormaPago();
        startTitleAnimation();
        getFechaActual();
        getDatosEmpresaReport();
        System.out.println("NRO DE TURNO EN VENTAS: " + nroTurno);
    }

    private void getDatosTurnoAbierto(){
        try{
            String sql = "SELECT nro_turno, cod_cajero FROM turno WHERE cod_caja = " + FormMain.codCaja + " AND fec_cierre_turno IS null";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    nroTurno = rs.getString("nro_turno");
                    codCajero = rs.getString("cod_cajero");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        System.out.println("NRO DE TURNO: " + nroTurno + "\nCOD CAJERO: " + codCajero);
    }
    
    private void configCampos(){
        jTFCodArticulo.setDocument(new MaxLength(35, "UPPER", "ALFA"));
        jTFCodCliente.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFCodCuenta.setDocument(new MaxLength(12, "", "ENTERO"));
        
        jTFCodArticulo.addFocusListener(new Focus());
        jTFCantVenta.addFocusListener(new Focus());
        jTFCodCliente.addFocusListener(new Focus());
        jTFCodCuenta.addFocusListener(new Focus());
        jTFMontoRecibido.addFocusListener(new Focus());
        jTFDescPorc.addFocusListener(new Focus());
        jTFDescMonto.addFocusListener(new Focus());
    }
    
    private String titulo(){
        return "ATOMSystems|Main - Módulo de Ventas (Registro de Ventas)";
    }
    
    private void setTitle(int i){
        this.setTitle(titulo().substring(0, i));
    }
    
    private void startTitleAnimation(){
        tiempo = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(indice == titulo().length()){
                    indice = 1;
                }else{
                    indice++;
                }
                setTitle(indice);
            }
        };
        tiempo.schedule(task, 0, speed);
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                CajaMain.resultExitVentas = false;
                setComponentesGrabado();
            }
        });
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
    
    private void cargaDatosArticulo(String codigo, String sigla){
        String sql = "";
        try{
            if(sigla.equals("")){
                sql = "SELECT articulo.descripcion, articulo.hab_venta, articulo.pct_iva, preciosart.sigla_venta, preciosart.cansi_venta,"
                       + "preciosart.precio_venta, articulo.pct_iva " +
                         "FROM articulo " +
                         "INNER JOIN preciosart " +
                         "ON articulo.cod_articulo = preciosart.cod_articulo " +
                         "WHERE preciosart.vigente = 'S' AND articulo.cod_articulo = " + codigo ;
            }else{
                sql = "SELECT articulo.descripcion, articulo.hab_venta, articulo.pct_iva, preciosart.sigla_venta, preciosart.cansi_venta,"
                           + "preciosart.precio_venta, articulo.pct_iva " +
                             "FROM articulo " +
                             "INNER JOIN preciosart " +
                             "ON articulo.cod_articulo = preciosart.cod_articulo AND sigla_venta = '" + sigla + "' "+
                             "WHERE preciosart.vigente = 'S' AND articulo.cod_articulo = " + codigo ;
            }
            System.out.println("SQL BUSQUEDA: " + sql);
            ResultSet rs1;
            rs1 = DBManager.ejecutarDSL(sql);
            if(rs1 != null){
                if(rs1.next()){
                    if(rs1.getString("hab_venta").equals("N")){
                        jTFDescArticulo.setText("ARTICULO NO HABILITADO PARA VENTA");
                        jTFDescArticulo.setForeground(Color.red);
                        jTFCodArticulo.requestFocus();
                    }else{
                        jTFDescArticulo.setText("");
                        jTFDescArticulo.setForeground(Color.black);
                        jTFDescArticulo.setText(rs1.getString("descripcion"));
                        jTFSiglaVenta.setText(rs1.getString("sigla_venta"));
                        jTFUm.setText(String.valueOf(rs1.getInt("cansi_venta")));
                        jTFPrecioVenta.setText(String.valueOf(decimalFormat.format(rs1.getDouble("precio_venta"))));
                        jTFIva.setText(String.valueOf(rs1.getInt("pct_iva")));
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
    
    private void configTablaFormaPago(){
        dtmFormaPago = (DefaultTableModel) jTFormasPago.getModel();
        jTFormasPago.getColumnModel().getColumn(0).setPreferredWidth(10); // codigo
        jTFormasPago.getColumnModel().getColumn(1).setPreferredWidth(80); // denominacion
        jTFormasPago.getColumnModel().getColumn(2).setPreferredWidth(15); // cotizacion
        jTFormasPago.getColumnModel().getColumn(3).setPreferredWidth(15); // recibido
        jTFormasPago.getColumnModel().getColumn(4).setPreferredWidth(15); // total
    }
    
    private void configTabla(){
        dtmDetallesVenta = (DefaultTableModel) jTDetallesVenta.getModel();
        jTDetallesVenta.getColumnModel().getColumn(0).setPreferredWidth(50); // codigo
        jTDetallesVenta.getColumnModel().getColumn(1).setPreferredWidth(80); // barras
        jTDetallesVenta.getColumnModel().getColumn(2).setPreferredWidth(200); // descripcion
        jTDetallesVenta.getColumnModel().getColumn(3).setPreferredWidth(15); // sigla
        jTDetallesVenta.getColumnModel().getColumn(4).setPreferredWidth(10); // um
        jTDetallesVenta.getColumnModel().getColumn(5).setPreferredWidth(10); // iva
        jTDetallesVenta.getColumnModel().getColumn(6).setPreferredWidth(35); // precio venta
        jTDetallesVenta.getColumnModel().getColumn(7).setPreferredWidth(20); // cantidad
        jTDetallesVenta.getColumnModel().getColumn(8).setPreferredWidth(35); // totalitem
        jTDetallesVenta.setRowHeight(25);
    }
    
    private boolean verificarArticuloEnDetalle(String codigo){
       boolean result = false;
       int cantFilas = dtmDetallesVenta.getRowCount();
       for(int i = 0; i < cantFilas; i++){
           Long codArticuloTabla = ((Long)dtmDetallesVenta.getValueAt(i, 0));
           String codArtTablaStr = String.valueOf(codArticuloTabla);
           if(codArtTablaStr.equalsIgnoreCase(codigo)){
               result = true;
               filaExistente = i;
           }
       }
       return result;
    }
    
    private void addArticuloDetalle(){
        /* COLUMNS
         * codigo (long)
         * barras (string) 
         * descripcion (string)
         * sigla (string)
         * um (long)
         * iva (long) 
         * precio venta (double)
         * cantidad (float) 
         * total (double)
        */
        
        long vCodArticulo = Long.parseLong(jTFCodArticulo.getText().trim());
        String vBarras = vBarrasArticulo;
        String vDescripcion = jTFDescArticulo.getText().trim();
        String vSigla = jTFSiglaVenta.getText().trim();
        int vUm = Integer.parseInt(jTFUm.getText().trim());
        int vIva = Integer.parseInt(jTFIva.getText().trim().replace(".", ""));
        float vCant = Float.parseFloat(jTFCantVenta.getText().trim());
        double vPrecioVenta = Double.parseDouble(jTFPrecioVenta.getText().trim().replace(",", ""));
        double vTotal = vCant * Double.parseDouble(jTFPrecioVenta.getText().trim().replace(",", ""));
        
        //dtmDetallesVenta.addRow(new Object[]{vCodArticulo, vBarras, vDescripcion, vSigla, vUm, vIva, vPrecioVenta, vCant, vTotal});
        
        if(!articuloExisteDetalle(String.valueOf(vCodArticulo))){
            dtmDetallesVenta.addRow(new Object[]{vCodArticulo, vBarras, vDescripcion, vSigla, vUm, vIva, vPrecioVenta, vCant, vTotal});
        }else{
            Float cantDetalle = (float)dtmDetallesVenta.getValueAt(filaExistente, 7);
            vCant = vCant + cantDetalle;
            vTotal = vCant * Double.parseDouble(jTFPrecioVenta.getText().trim().replace(",", ""));
            dtmDetallesVenta.setValueAt(vCant, filaExistente, 7);
            dtmDetallesVenta.setValueAt(vTotal, filaExistente, 8);
        }
        
        jTDetallesVenta.setModel(dtmDetallesVenta);
        jBTotalizar.doClick();
    }
    
    private void addFormaPago(){
        /* Columns
         * codigo (long)
         * denominacion (string)
         * cotizacion (float)
         * recibido (float)
         * total (float)
         */
        
        long vCodCuenta = Long.parseLong(jTFCodCuenta.getText().trim());
        String vDenominacion = jTFDenominacionCta.getText().trim();
        float vCotizacion = 0, vRecibido = 0, vTotal =0;
        vCotizacion = Float.parseFloat(jTFCotizacion.getText().trim().replace(",", ""));
        vRecibido = Float.parseFloat(jTFMontoRecibido.getText().trim().replace(",", ""));
        vTotal = Math.round(vRecibido * vCotizacion);
        System.out.println("TOTAL FORMA DE PAGO REGISTRADO: " + vTotal);
        
        dtmFormaPago.addRow(new Object[]{vCodCuenta, vDenominacion, vCotizacion, vRecibido, vTotal});
        totalizarFormaPago();
    }
    
    private void totalizarFormaPago(){
        double totalRecibido = 0;
        if(dtmFormaPago.getRowCount() > 0){
            for(int i = 0; i < dtmFormaPago.getRowCount(); i++){
                double totalFormaPago = Double.parseDouble(dtmFormaPago.getValueAt(i, 4).toString());
                totalRecibido += totalFormaPago;
                jTFMontoRecibidoTotal.setText(decimalFormat.format(totalRecibido));
                System.out.println("TOTAL FORMA PAGO TABLA: " + jTFMontoRecibidoTotal.getText().trim());
            }
        }else{
            jTFMontoRecibidoTotal.setText("0");
        }
    }
    
    // en el caso de que se quiera sumar sin detallar los valores
    
    private boolean existeFormaPagoDetalle(String codigo){
        esModificacion = false;
        boolean result = false;
        int cantFilas = dtmFormaPago.getRowCount();
        if(cantFilas > 0){
            for(int i = 0; i < cantFilas; i++){
                Long codFormaPagoTabla = ((Long)dtmFormaPago.getValueAt(i, 0));
                String codFormPagoStr = String.valueOf(codFormaPagoTabla);
                if(codFormPagoStr.equalsIgnoreCase(codigo)){
                    esModificacion = true;
                    result = true;
                    filaExistente = i;
                }
            }
        }
        return result;
    }
    
    // en el caso de que se quiera sumar directamente la cantidad de venta
    
    private boolean articuloExisteDetalle(String codigo){
       esModificacion = false;
       boolean result = false;
       int cantFilas = dtmDetallesVenta.getRowCount();
       for(int i = 0; i < cantFilas; i++){
           Long codArticuloTabla = ((Long)dtmDetallesVenta.getValueAt(i, 0));
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
        String sql = "SELECT cod_barras FROM barras WHERE cod_barras = '" + codigo + "' AND vigente = 'S'";
        System.out.println("SQL BARRAS ART: " + sql);
        ResultSet rs = null;
        rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    barras = rs.getString("cod_barras");
                }else{
                    barras = "0";
                }
                System.out.println("BARRAS 1: " + barras);
            }
            
            if(barras.equalsIgnoreCase("0")){
                String sql2 = "SELECT cod_barras FROM barras WHERE cod_articulo = " + codigo + " AND vigente = 'S' LIMIT 1";
                System.out.println("BARRAS 2: " + sql2);
                rs = DBManager.ejecutarDSL(sql2);
                if(rs != null){
                    if(rs.next()){
                        barras = rs.getString("cod_barras");
                    }else{
                        barras = "0";
                    }
                }
                System.out.println("BARRAS 2: " + barras);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return barras;
    }
    
    private void totalizarMontos(){
        int vRowCount = dtmDetallesVenta.getRowCount();
        int vIva = 0;
        double vTotalItem = 0;
        double vTotalVenta = 0;
        double vTotalGrav10 = 0;
        double vTotalGrav5 = 0;
        double vTotalExento = 0;
        double vIva10 = 0;
        double vIva5 = 0;
        
        for(int i = 0; i < vRowCount; i++){
            vIva = Integer.parseInt(dtmDetallesVenta.getValueAt(i, 5).toString());
            vTotalItem = Double.parseDouble(dtmDetallesVenta.getValueAt(i, 8).toString());
            
            if(vIva == 10){
                vTotalGrav10 += vTotalItem;
            }
            
            if(vIva == 5){
                vTotalGrav5 += vTotalItem;
            }
            
            if(vIva == 0){
                vTotalExento += vTotalItem;
            }
            
            vTotalVenta += vTotalItem;
        }
        
        // calculo de ivas
        vIva10 = (vTotalGrav10 / 11);
        vIva5 = (vTotalGrav5 / 21);
        
        jTFExento.setText(decimalFormat.format(vTotalExento));
        jTFGrav5.setText(decimalFormat.format(vTotalGrav5));
        jTFGrav10.setText(decimalFormat.format(vTotalGrav10));
        jTFIva10.setText(decimalFormat.format(vIva10));
        jTFIva5.setText(decimalFormat.format(vIva5));
        jTFTotalVenta.setText(decimalFormat.format(vTotalVenta));
    }
    
    private void getDatosCliente(String codigo){
        ClienteCtrl pCtrl = new ClienteCtrl();
        ClienteBean bean = pCtrl.buscaClienteCodCliente(Integer.parseInt(codigo));
        if(bean != null){
            jTFNombreCliente.setText(bean.getRazonSoc());
            jTFRucCliente.setText(bean.getRucCliente());
        }
    }
    
    private void getCtaDenCotiz(String codigo){
        String sql = "SELECT cod_cuenta, moneda.cotiz_compra, denominacion_cta "
                   + "FROM cuenta "
                   + "INNER JOIN moneda "
                   + "ON cuenta.cod_moneda = moneda.cod_moneda "
                   + "WHERE cod_cuenta = " + codigo + " AND cuenta.activa = 'S'";
        System.out.println("GET DENOMINACION COTIZACION CTA: " + sql);
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFDenominacionCta.setForeground(Color.black);
                    jTFCotizacion.setText(decimalFormat.format(rs.getInt("cotiz_compra")));
                    jTFDenominacionCta.setText(rs.getString("denominacion_cta"));
                }else{
                    jTFDenominacionCta.setForeground(Color.red);
                    jTFCotizacion.setText("1.00");
                    jTFDenominacionCta.setText("FORMA DE PAGO INEXISTENTE");
                    jTFCodCuenta.requestFocus();
                    jTFCodCuenta.setText("1");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void getDenominacionCtaCodCliente(String codigo){
        String sql = "SELECT cod_cuenta, denominacion_cta FROM cuenta WHERE activa = 'S' "
                   + "AND cod_cliente = " + codigo;
        System.out.println("GET DENOMINACION CTA CLIENTE (TECLA C): " + sql);
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFDenominacionCta.setForeground(Color.black);
                    jTFCodCuenta.setText(String.valueOf(rs.getInt("cod_cuenta")));
                    jTFDenominacionCta.setText(rs.getString("denominacion_cta"));
                }else{
                    jTFDenominacionCta.setForeground(Color.red);
                    jTFDenominacionCta.setText("CUENTA INACTIVA");
                    jTFCodCuenta.requestFocus();
                    jTFCodCuenta.setText("1");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void limpiarTablaFormaPago(){
        int nroFilas = dtmFormaPago.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmFormaPago.removeRow(i);
            nroFilas = dtmFormaPago.getRowCount();
            
            if(nroFilas == 1){
                dtmFormaPago.removeRow(i);
                nroFilas = 0;
            }
        }
        jTFormasPago.setModel(dtmFormaPago);
    }
    
    private void limpiarTablaDetalles(){
        int nroFilas = dtmDetallesVenta.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesVenta.removeRow(i);
            nroFilas = dtmDetallesVenta.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesVenta.removeRow(i);
                nroFilas = 0;
            }
        }
        jTFormasPago.setModel(dtmFormaPago);
    }
    
    private boolean grabarDatosVenta(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoDatos = leerDetalles();
        if(!problemLeyendoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = false;
        }
        
        return estadoGrabado;
    }
    
    private boolean leerDetalles(){
        
        int iterador;    
        int cantFilasTablaDetallesVentas = dtmDetallesVenta.getRowCount();
        
        boolean existeStockArticulo = false;
        boolean resultStockArticulo = false;
        boolean problemFound = false;
        boolean resultHistMovimiento = false;
        boolean resultHistVentas = false;
        boolean resultVentasCab = false;
        boolean resultVentasDet = false;
        boolean resultVentasDetCuotas = false;
        boolean resultFormaPago = false;
        boolean resultOperacionGrabaVentas = true;
        boolean resultOperacionGrabaFormaPagoCre = true;
        
        // Datos Globales a grabar
        String vCodEmpresa = utiles.Utiles.getCodEmpresaDefault();
        String vCodLocal = utiles.Utiles.getCodLocalDefault(vCodEmpresa);
        String vCodSector = utiles.Utiles.getCodSectorDefault(vCodLocal);
        int vCodCaja = FormMain.codCaja;
        String vTipoComprob = "FAI"; //Factura Autoimpresa        
        String vCodCliente = jTFCodCliente.getText().trim();
        String vNombreCliente = jTFNombreCliente.getText().trim();
        String vRucCliente = jTFRucCliente.getText().trim();
        int vCodUsuario = Integer.parseInt(codCajero);
        double vTotalMontoExento = Double.parseDouble(jTFExento.getText().trim().replace(",", ""));
        double vTotalVenta = Double.parseDouble(jTFTotalVenta.getText().trim().replace(",", ""));
        double vTotalGrav05 = Double.parseDouble(jTFGrav5.getText().trim().replace(",", ""));
        double vTotalGrav10 = Double.parseDouble(jTFGrav10.getText().trim().replace(",", ""));
        double vIva05 = Double.parseDouble(jTFIva5.getText().trim().replace(",", ""));
        double vIva10 = Double.parseDouble(jTFIva10.getText().trim().replace(",", ""));
        double vGrav05 = (vTotalGrav05 - vIva05);
        double vGrav10 = (vTotalGrav10 - vIva10);
        float vPctDesc = Float.parseFloat(jTFDescPorc.getText().trim());
        double vMontoDesc = Double.parseDouble(jTFDescMonto.getText().trim().replace(",", ""));
        double vVuelto = Double.parseDouble(jTFVueltoTotal.getText().trim().replace(",", ""));
        
        // Venta cabecera
        resultOperacionGrabaVentas = grabarVentaCabecera(vCodEmpresa, vCodLocal, vCodCaja, vTipoComprob, vNroComprob, vCodCliente, 
                                                         vRucCliente, vNombreCliente, vCodUsuario, vTotalMontoExento, vGrav10,
                                                         vGrav05, vIva10, vIva05, vTotalVenta, vPctDesc, vMontoDesc, vCodSector);
        
        
        if(!resultOperacionGrabaVentas){
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar venta cabecera!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            resultVentasCab = true;
            resultVentasDet = true;
            resultVentasDetCuotas = true;
            resultFormaPago = true;
        }
        
        int nroSec;
        for(nroSec = 0; nroSec < dtmFormaPago.getRowCount(); nroSec++){
            long vCodCuenta = ((long)dtmFormaPago.getValueAt(nroSec, 0));
            String vTipoCuenta = getTipoCuenta(vCodCuenta);
            int vCodMoneda = getCodMoneda(vCodCuenta);
            float vTipoCambio = ((float)dtmFormaPago.getValueAt(nroSec, 2));
            float vMontoPago = ((float)dtmFormaPago.getValueAt(nroSec, 3));
            String vNombreLibrador = dtmFormaPago.getValueAt(nroSec, 1).toString();       
            String vFecVencimiento = calculaVencimiento();
            
            System.out.println("TIPO DE CUENTA: " + vTipoCuenta);
            if(vTipoCuenta.equalsIgnoreCase("CRE")){
                resultOperacionGrabaFormaPagoCre = grabaFormaPagoCre(vCodEmpresa, vCodLocal, vCodSector, vCodCaja, vTipoComprob, vNroComprob, 
                                                                     vCodCliente, vFecVencimiento, vMontoPago, vCodMoneda, vTipoCambio);
            }
            
            // Forma pago 
            resultOperacionGrabaVentas = grabarFormaPago(vCodEmpresa, vCodLocal, vCodCaja, vNroComprob, vCodSector, nroSec + 1, vCodCuenta,
                                                         vTipoCuenta, vCodMoneda, vTipoCambio, vMontoPago, vCodUsuario, vNombreLibrador, vTipoComprob);
            System.out.println("NRO SECUENCIA: " + (nroSec + 1 )+ "\nTIPO CUENTA: " + vTipoCuenta);
            if(!resultOperacionGrabaVentas){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar forma pago!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultVentasCab = true;
                resultVentasDet = true;
                resultVentasDetCuotas = true;
                resultFormaPago = true;
            }                        
        }
        
        // hasta el momento el vuelto se esta dando solo en guaranies
        // grabacion del vuelto en forma de pago 
        if(vVuelto > 0){
            resultOperacionGrabaVentas = grabarFormaPago(vCodEmpresa, vCodLocal, vCodCaja, vNroComprob, vCodSector, nroSec + 1, 
                                                         1, "EFE", 1, 1, vVuelto, vCodUsuario, "VUELTO", vTipoComprob);
        }
                       
        // (ventas detalles - venta_det - historico_mov - historico_ventas - stock_articulo)
        
        for(iterador = 0; iterador < cantFilasTablaDetallesVentas; iterador++){
            long vCodArticulo = ((long) dtmDetallesVenta.getValueAt(iterador, 0));
            int vCansiVenta = ((int)dtmDetallesVenta.getValueAt(iterador, 4));
            String vSiglaVenta = dtmDetallesVenta.getValueAt(iterador, 3).toString();
            float cantTabla = ((float)dtmDetallesVenta.getValueAt(iterador, 7));
            float vCantVenta = (cantTabla * vCansiVenta);
            int vPctIva = ((int)dtmDetallesVenta.getValueAt(iterador, 5));
            int vCodGrupo = getCodGrupoArticulo(vCodArticulo);
            int vCodSubgrupo = getCodSubGrupoArticulo(vCodArticulo);
            int vCodMarca = getCodMarcaArticulo(vCodArticulo);
            int vCodProveedor = getCodProveedorArticulo(vCodArticulo);
            
            // -- costo
            double costo = getCostoArticuloVendido(vCodArticulo);
            double vMontoCosto = (costo * vCantVenta);
            double vMontoVenta = ((double)dtmDetallesVenta.getValueAt(iterador, 8));
            
            // -- margen 
            double vMontoIva = 0;
            if(vPctIva == 5){
                vMontoIva = vMontoVenta / 21;
            }
            
            if(vPctIva == 10){
                vMontoIva = vMontoVenta / 11;
            }
            
            double vMontoMargen = (vMontoVenta - vMontoIva) - vMontoCosto;
            
            // -- descuento
            double vMontoDescuento = vMontoVenta * (vPctDesc / 100);
            
            int vCodListaPrecio = getCodListaPrecio(vCodArticulo, vSiglaVenta);
            
            // Venta detalles
            resultOperacionGrabaVentas = grabarVentaDetalle(vCodEmpresa, vCodLocal, vCodCaja, vTipoComprob, vNroComprob, vCodArticulo, 
                                                            vCansiVenta, vSiglaVenta, vCantVenta , vMontoCosto, vMontoMargen, vPctIva, 
                                                            vMontoVenta, vPctDesc, vMontoDescuento, vCodSector, vCodListaPrecio, vMontoIva);
            
            if(!resultOperacionGrabaVentas){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar detalles de venta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultVentasDet = true;
                resultHistMovimiento = true;
                resultHistVentas = true;
                resultStockArticulo = true;
            }
            
            // Stock Articulo
            existeStockArticulo = verExistenciaStock(vCodArticulo, vCodEmpresa, vCodLocal, vCodSector);
            if(existeStockArticulo){
                resultOperacionGrabaVentas = actualizarStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vCansiVenta, vCantVenta);
            }else{
                resultOperacionGrabaVentas = agregarNuevoStock(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vCansiVenta, vCantVenta, vCodUsuario, vMontoCosto);
            }
            
            if(!resultOperacionGrabaVentas){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al actualizar stock!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultStockArticulo = true;
                break;
            }
            
            //Historico articulo 
            if(!resultStockArticulo){
                resultOperacionGrabaVentas = agregarArticulosHistorico(vCodEmpresa, vCodLocal, vCodSector, vCodArticulo, vSiglaVenta, vCansiVenta, 
                                                                       vCantVenta, vTipoComprob, vNroComprob);
            }
            
            if(!resultOperacionGrabaVentas){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al agregar artículo en histórico de movimientos!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultHistMovimiento = true;
                break;
            }
            
            // Historico ventas 
            
            if(!resultHistMovimiento){
                resultOperacionGrabaVentas = agregarArticuloHistoricoVentas(vCodEmpresa, vCodLocal, vCodArticulo, vCantVenta, vMontoCosto, vMontoMargen, 
                                                                            vMontoIva, vTotalVenta, vCodSubgrupo, vCodProveedor, vCodMarca, vCodGrupo, 
                                                                            vCodSector, vTipoComprob, vPctIva);
            }
            
            if(!resultOperacionGrabaVentas){
                JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar articulo en el historico de ventas!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                resultHistVentas = true;
                break;
            }
            
            if(resultHistVentas || resultHistMovimiento || resultStockArticulo || resultVentasDet || resultFormaPago || resultVentasCab){
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
    
    private String calculaVencimiento(){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecActual = new Date();
        try{
            fecActual = sdf.parse(fecVigencia);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        calendar.setTime(fecActual);
        int vence = 30;
        calendar.add(Calendar.DATE, vence);
        result = sdf.format(calendar.getTime());
        return result;
    }
    
    private boolean grabarVentaDetalle(String dCodEmpresa, String dCodLocal, int dCodCaja, String dTipoComp, String dNroComprob, long dCodArticulo, 
                                       int dCansiVenta, String dSiglaVenta, float dCantVenta, double dMontoCosto, double dMontoMargen, int dPctIva, 
                                       double dMontoVenta, float dPctDesc, double dMontoDesc, String dCodSector, int dCodListaPrecio, double dMontoIva){
        String sql = "INSERT INTO venta_det (cod_empresa, cod_local, cod_caja, tip_comprob, nro_comprob, nro_ticket, fec_comprob, cod_articulo, cansi_venta, "
                   + "sigla_venta, can_venta, mon_costo, mon_margen, pct_iva, mon_iva, mon_venta, pct_descuento, mon_descuento, estado, fec_vigencia, "
                   + "cod_sector, procesado, nro_turno, cod_lista_precio, mon_costo_avg, pct_comision_fijo, pct_comision_externo, nro_timbrado, ven_timbrado, "
                   + "can_entregada) "
                   + "VALUES (" + dCodEmpresa + ", "
                   + "" + dCodLocal + ", "
                   + "" + dCodCaja + ", "
                   + "'" + dTipoComp + "', "
                   + "" + dNroComprob + ", "
                   + "" + dNroComprob + ", "
                   + "'now()', "
                   + "" + dCodArticulo + ", "
                   + "" + dCansiVenta + ", "
                   + "'"+ dSiglaVenta + "', "
                   + "" + dCantVenta + ", "
                   + "" + dMontoCosto + ", "
                   + "" + dMontoMargen + ", "
                   + "" + dPctIva + ", "
                   + "" + dMontoIva + ", "
                   + "" + dMontoVenta + ", "
                   + "" + dPctDesc + ", "
                   + "" + dMontoDesc + ", 'V', 'now()', "
                   + "" + dCodSector + ", 'N', " + nroTurno + ", "
                   + "" + dCodListaPrecio + ", "
                   + "" + dMontoCosto + ", 0, 0, 0, 'now()', 0)";
                
        System.out.println("VENTA DETALLE: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabaFormaPagoCre(String vCodEmpresa, String vCodLocal, String vCodSector, int vCodCaja, String vTipoComprob, String vNroTicket, 
                                      String vCodCliente, String vFecVencimiento, double vMontoCuota, int vCodMoneda, double vTipoCambio){
        String sql = "INSERT INTO venta_det_cuotas (cod_empresa, cod_local, cod_sector, cod_caja, tip_comprob, nro_ticket, nro_comprob, fec_comprob, "
                   + "cod_cliente, nro_cuota, can_cuota, fec_vencimiento, monto_cuota, monto_retenido, cod_moneda, tip_cambio, nro_recibo, fec_recibo, "
                   + "estado, cod_usuario, fec_vigencia, nro_pago, observacion, nro_turno, es_venta, nro_timbrado, ven_timbrado, nro_factura, nro_timbrado_factura) "
                   + "VALUES (" + vCodEmpresa + ", "
                   + "" + vCodLocal + ", "
                   + "" + vCodSector + ", "
                   + "" + vCodCaja + ", "
                   + "'" + vTipoComprob + "', "
                   + "" + vNroTicket + ", "
                   + "" + vNroTicket + ", 'now()', "
                   + "" + vCodCliente + ", 1, 1, "
                   + "'" + vFecVencimiento + "', "
                   + "" + vMontoCuota + ", 0, "
                   + "" + vCodMoneda + ", "
                   + "" + vTipoCambio + ", 0, null, 'V', "
                   + "" + FormMain.codUsuario + ", 'now()', 0, '-', " + nroTurno + ", 'S', 0, 'now()', 0, 0)";
        System.out.println("FORMA PAGO VENTAS CRE (VENTAS-DET-CUOTAS): " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarFormaPago(String fCodEmpresa, String fCodLocal, int fCodCaja, String fNroTicket, String fCodSector, int fNroSec, long fCodCuenta, 
                                    String fTipoCuenta, int fCodMoneda, double fTipoCambio, double fMontoPago, int fCodUsuario, String fNombreLibrador, 
                                    String fTipoComprob){
        String sql = "";
        if(fNombreLibrador == "VUELTO"){
            sql = "INSERT INTO forma_pago (cod_empresa, cod_local, cod_caja, nro_ticket, nro_secuencia, nro_turno, cod_cuenta, tipo_cuenta, cod_moneda, "
                       + "tip_cambio, monto_pago, fec_cobro, fec_emision, fec_vencimiento, nro_documento, nro_autorizacion, nom_librador, estado, cod_banco, "
                       + "fec_vigencia, cod_usuario, cod_sector, nro_documento_dep, tip_comprob_dep, cod_cuenta_dep, tip_comprob, nro_timbrado) "
                       + "VALUES (" + fCodEmpresa + ", "
                       + "" + fCodLocal + ", "
                       + "" + fCodCaja + ", "
                       + "" + fNroTicket + ", "
                       + "" + fNroSec + ", " + nroTurno + ", "
                       + "" + fCodCuenta + ", "
                       + "'"+ fTipoCuenta + "', "
                       + "" + fCodMoneda + ", "
                       + "" + fTipoCambio + ", "
                       + "-" + fMontoPago + ", "
                       + "'now()', "
                       + "'now()', "
                       + "'now()', "
                       + "" + fNroTicket + ", 0, "
                       + "'" + fNombreLibrador + "', 'V', 1, 'now()', "
                       + "" + fCodUsuario + ", "
                       + "" + fCodSector + ", 0, '-', 0, "
                       + "'" + fTipoComprob + "', 0)";
        }else{
            sql = "INSERT INTO forma_pago (cod_empresa, cod_local, cod_caja, nro_ticket, nro_secuencia, nro_turno, cod_cuenta, tipo_cuenta, cod_moneda, "
                       + "tip_cambio, monto_pago, fec_cobro, fec_emision, fec_vencimiento, nro_documento, nro_autorizacion, nom_librador, estado, cod_banco, "
                       + "fec_vigencia, cod_usuario, cod_sector, nro_documento_dep, tip_comprob_dep, cod_cuenta_dep, tip_comprob, nro_timbrado) "
                       + "VALUES (" + fCodEmpresa + ", "
                       + "" + fCodLocal + ", "
                       + "" + fCodCaja + ", "
                       + "" + fNroTicket + ", "
                       + "" + fNroSec + ", " + nroTurno + ", "
                       + "" + fCodCuenta + ", "
                       + "'"+ fTipoCuenta + "', "
                       + "" + fCodMoneda + ", "
                       + "" + fTipoCambio + ", "
                       + "" + fMontoPago + ", "
                       + "'now()', "
                       + "'now()', "
                       + "'now()', "
                       + "" + fNroTicket + ", 0, "
                       + "'" + fNombreLibrador + "', 'V', 1, 'now()', "
                       + "" + fCodUsuario + ", "
                       + "" + fCodSector + ", 0, '-', 0, "
                       + "'" + fTipoComprob + "', 0)";
        }
        System.out.println("FORMA PAGO VENTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    
    
    private boolean grabarVentaCabecera(String cCodEmpresa, String cCodLocal, int cCodCaja, String cTipoComprob, String cNroComprob, String cCodCliente, 
                                        String cRucCliente, String cNombreCliente, int cCodCajero, double cMontoExento, double cMontoGrav10, 
                                        double cMontoGrav5, double cMontoIva10, double cMontoIva5, double cMontoTotal, float cPctDesc, double cMontoDesc, 
                                        String cCodSector){
        String sql = "INSERT INTO venta_cab (cod_empresa, cod_local, cod_caja, tip_comprob, nro_comprob, fec_comprob, nro_ticket, nro_turno, cod_cliente, "
                   + "ruc_cliente, nom_cliente, cod_cajero, mon_exento, mon_grava10, mon_grava05, mon_iva10, mon_iva05, mon_total, vuelto_donado, estado, "
                   + "fec_vigencia, punto_exp_fac, pct_descuento, mon_descuento, cod_sector, cod_fiscal, venta_alcosto, nro_recibo, venta_credito, cod_cuenta_afin, "
                   + "cod_banco_afin, tip_venta, cod_comprobante_fiscal, cod_vendedor, cod_lista_precio, cod_punto_venta, nro_timbrado, ven_timbrado, "
                   + "mg10, mg05, nro_asiento, nro_factura, nro_timbrado_factura, entregada, nro_asiento_cmv, cod_usuario, nro_rendicion, nro_ncc, "
                   + "nro_timbrado_ncc, nro_distrib, seleccionada, can_puntos, nro_pedido, serie_pedido) "
                   + "VALUES (" + cCodEmpresa + ", "
                   + "" + cCodLocal + ", "
                   + "" + cCodCaja + ", "
                   + "'" + cTipoComprob + "', "
                   + "" + cNroComprob + ", "
                   + "'now()', "
                   + "" + cNroComprob + ", " + nroTurno + ", "
                   + "" + cCodCliente + ", "
                   + "'" + cRucCliente + "', "
                   + "'" + cNombreCliente + "', "
                   + "" + cCodCajero + ", "
                   + "" + cMontoExento + ", "
                   + "" + cMontoGrav10 + ", "
                   + "" + cMontoGrav5 + ", "
                   + "" + cMontoIva10 + ", "
                   + "" + cMontoIva5 + ", "
                   + "" + cMontoTotal + ", 0, 'V', 'now()', '0', "
                   + "" + cPctDesc + ", "
                   + "" + cMontoDesc + ", "
                   + "" + cCodSector + ", 0, 'N', '0', 'N', 0, 0, 'MER', 0, 0, 0, 0, 0, 'now()', 0, 0, 0, '0', 0, 'S', 0, "
                   + "" + FormMain.codUsuario + ", 0, 0, 0, 0, 'S', 0, 0, 'A')";
        System.out.println("VENTA CABECERA: " + sql);
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
    
    
    private void getFechaActual() {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date fechaActual = new Date();
        String fechaConFormato = sdf.format(fechaActual);
        fecVigencia = fechaConFormato;
        //tfFechaRegistro.setText(dia + "/" + mes + "/" + annio);
    }
    
    private String getNroComprobVta(){
        String result = "0";
        try
        {
            ResultSet rs = DBManager.ejecutarDSL("SELECT MAX(nro_comprob) AS codigo FROM venta_cab WHERE cod_caja = " + FormMain.codCaja);            
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
    
    private String getTipoCuenta(long codCuenta){
        String result = "";
        try{
            String sql = "SELECT tipo_cuenta FROM cuenta WHERE cod_cuenta = " + codCuenta;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("tipo_cuenta");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private int getCodMoneda(long codCuenta){
        int result = 0;
        try{
            String sql = "SELECT cod_moneda FROM cuenta WHERE cod_cuenta = " + codCuenta;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_moneda");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private double getCostoArticuloVendido(long codArticulo){
        double costo = 0;
        int pct_iva = getPctIva(codArticulo);
        double iva_divisor = 0;
        String siTieneIva = "";
        
        if(pct_iva == 10){
            iva_divisor = 1.10;
            siTieneIva = "/" + iva_divisor;
        }
        
        if(pct_iva == 5){
            iva_divisor = 1.05;
            siTieneIva = "/" + iva_divisor;
        }
        
        if(pct_iva == 0){
            siTieneIva = "";
        }
        
        try{
            String sql = "SELECT (costo_neto / cansi_compra)" + siTieneIva + " AS costo FROM costoart WHERE cod_articulo = " + codArticulo + " AND vigente = 'S'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    costo = rs.getDouble("costo");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return costo;
    }
    
    private int getPctIva(long codArticulo){
        int result = 0;
        try{
            String sql = "SELECT pct_iva FROM articulo WHERE cod_articulo = " + codArticulo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("pct_iva");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private int getCodListaPrecio(long codArticulo, String sigla){
        int lista = 0;
        try{
            String sql = "SELECT cod_lista FROM preciosart WHERE cod_articulo = " + codArticulo + " AND sigla_venta = '" + sigla + "' AND vigente = 'S'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    lista = rs.getInt("cod_lista");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return lista;
    }
    
    private boolean verExistenciaStock(long eCodArticulo, String eCodEmpresa, String eCodLocal, String eCodSector){
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
    
    private boolean agregarNuevoStock(String agCodEmpresa, String agCodLocal, String agCodSector, long agCodArticulo, int agCansi, float agCantVenta, 
                                      int agCodUsuario, double agCostoNeto){
        float agStock = -(agCansi * agCantVenta);
        double agCostoPromedio = agCostoNeto / agCansi;
        
        String sql = "INSERT INTO stockart (cod_empresa, cod_local, cod_articulo, stock, giro_dia, fec_ultcompra, "
                   + "fec_vigencia, cod_usuario, cod_sector, costo_promedio_un) VALUES (" + agCodEmpresa + ", " + agCodLocal + ", "+ agCodArticulo + ", " + agStock + ", 0.01, 'now()', "
                   + "'now()', " + agCodUsuario + ", " + agCodSector + ", " + agCostoPromedio + ")";
        System.out.println("INSERT STOCK: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean actualizarStock(String aCodEmpresa, String aCodLocal, String aCodSector, long aCodArticulo, int aCansi, float aCantVenta){
        float cantRecibida = 0;
        cantRecibida = aCansi * aCantVenta;
        String sql = "UPDATE stockart SET stock = stock - " + cantRecibida + ", fec_ultcompra = 'now()', fec_vigencia = 'now()' "
                   + "WHERE cod_empresa = " + aCodEmpresa + " AND cod_local = " + aCodLocal + " AND cod_sector = " + aCodSector + " "
                   + "AND cod_articulo = " +  aCodArticulo;
        System.out.println("ACTUALIZAR STOCK COMPRAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
   
    private boolean agregarArticulosHistorico(String hCodEmpresa, String hCodLocal, String hCodSector, long hCodArticulo, String hSigla, int hCansi, 
                                              float hCantVenta, String hTipoComprob, String hNroComprob){
        
        
        String sql = "INSERT INTO hismovi_articulo (cod_empresa, cod_local, cod_sector, cod_articulo, sigla_venta, cansi_venta, cantidad, "
                   + "tipo_movimiento, tip_comprob, fec_movimiento, fec_vigencia, estado, cod_traspaso, nro_comprob) "
                   + "VALUES (" + hCodEmpresa + ", " + hCodLocal + ", " + hCodSector + ", " + hCodArticulo + ", '" + hSigla + "', "
                   + "" + hCansi + ", " + hCantVenta + ", 'SAL', '" + hTipoComprob + "', 'now()', 'now()', "
                   + "'V', 0, " + hNroComprob + ")";
        System.out.println("INSERT HISMOVI-ARTICULO VENTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean agregarArticuloHistoricoVentas(String codEmpresa, String codLocal, long codArticulo, float cantVenta, double montoCosto, double montoMargen, 
                                                   double montoIva, double montoTotalVenta, int codSubgrupo, int codProveedor, int codMarca, int codGrupo, 
                                                   String codSector, String tipoComprob, int pctIva){
        String sql = "INSERT INTO hisventa_articulo (cod_empresa, cod_local, cod_articulo, fec_venta, cant_venta, pct_iva, monto_costo, monto_margen, "
                   + "monto_iva, monto_total, cod_subgrupo, cod_proveedor, cod_marca, cod_grupo, cod_sector, procesado, fec_vigencia, monto_costo_avg, "
                   + "tip_comprob, nro_asiento_cmv) "
                   + "VALUES (" + codEmpresa + ", "
                   + "" + codLocal + ", "
                   + "" + codArticulo + ", "
                   + "'now()', "
                   + "" + cantVenta + ", "
                   + "" + pctIva + ", "
                   + "" + montoCosto + ", "
                   + "" + montoMargen + ", "
                   + "" + montoIva + ", "
                   + "" + montoTotalVenta + ", "
                   + "" + codSubgrupo + ", "
                   + "" + codProveedor + ", "
                   + "" + codMarca + ", "
                   + "" + codGrupo + ", "
                   + "" + codSector + ", 'N', 'now()', 0, "
                   + "'" + tipoComprob + "', 0)";
        System.out.println("INSERT HISTORICO VENTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private int getCodGrupoArticulo(long codArticulo){
        int codigo = 0;
        try{
            String sql = "SELECT cod_grupo FROM articulo WHERE cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codigo = rs.getInt("cod_grupo");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return codigo;
    }
    
    private int getCodSubGrupoArticulo(long codArticulo){
        int codigo = 0;
        try{
            String sql = "SELECT cod_subgrupo FROM articulo WHERE cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codigo = rs.getInt("cod_subgrupo");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return codigo;
    }
    
    private int getCodMarcaArticulo(long codArticulo){
        int codigo = 0;
        try{
            String sql = "SELECT cod_marca FROM articulo WHERE cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codigo = rs.getInt("cod_marca");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return codigo;
    }
    
    private int getCodProveedorArticulo(long codArticulo){
        int codigo = 0;
        try{
            String sql = "SELECT cod_proveedor FROM articulo WHERE cod_articulo = " + codigo;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codigo = rs.getInt("cod_proveedor");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return codigo;
    }
   
    private void setComponentesGrabado(){
        limpiarTablaDetalles();
        limpiarTablaFormaPago();
        vNroComprob = getNroComprobVta();
        jTFExento.setText("000.00");
        jTFGrav5.setText("000.00");
        jTFGrav10.setText("000.00");
        jTFIva5.setText("000.00");
        jTFIva10.setText("000.00");
        jTFTotalVenta.setText("000.00");
        jTFCodCliente.setText("1");
        jTFDescPorc.setText("000.00");
        jTFDescMonto.setText("000.00");
        jTFCodCuenta.setText("1");
        jTFMontoRecibido.setText("000.00"); 
        jTFFaltanTotal.setText("000.00");
        jTFMontoRecibidoTotal.setText("000.00");
        jTFVueltoTotal.setText("000.00");
        jBConfirmar.setEnabled(false);
    }
    
    private void imprimirBoletaVenta(){
        String sql = "SELECT DISTINCT venta_cab.nro_ticket, venta_cab.cod_caja, venta_det.cod_articulo, articulo.des_corta, "
                   + "venta_det.cansi_venta || '-' || venta_det.sigla_venta AS sigla, venta_det.can_venta, "
                   + "(venta_det.mon_venta / venta_det.can_venta) AS precio, venta_det.mon_venta, venta_cab.mon_descuento, "
                   + "venta_cab.fec_comprob "
                   + "FROM venta_cab "
                   + "INNER JOIN venta_det "
                   + "ON venta_cab.nro_ticket = venta_det.nro_ticket "
                   + "INNER JOIN articulo "
                   + "ON venta_det.cod_articulo = articulo.cod_articulo "
                   + "WHERE venta_cab.nro_ticket = " + vNroComprob + " AND venta_cab.cod_caja = " + FormMain.codCaja + " AND "
                   + "venta_cab.fec_comprob::date = '" + fecVigencia + "'::date AND venta_cab.nro_turno = " + nroTurno + " "
                   + "AND venta_det.cod_caja = " + FormMain.codCaja + " AND venta_det.nro_ticket = " + vNroComprob + " "
                   + "AND venta_det.nro_turno = " + nroTurno;
        
        System.out.println("IMPRESION DE BOLETA DE VENTA: " + sql);
        
        String tipo_impresion = "";
        
        if(getTipoImpresion().equals("S")){
            tipo_impresion = "boletaVenta";
        }else{
            tipo_impresion = "boletaVentaHoja";
        }
        
        try{
            LibReportes.parameters.put("pRazonSocEmpresa", utiles.Utiles.getRazonSocialEmpresa(utiles.Utiles.getCodEmpresaDefault()));
            LibReportes.parameters.put("pActividadEmpresa", actividadEmpresa);
            LibReportes.parameters.put("pDireccionEmpresa", direccionEmpresa);
            LibReportes.parameters.put("pCiudadEmpresa", ciudadEmpresa);
            LibReportes.parameters.put("pTelEmpresa", telEmpresa);
            LibReportes.parameters.put("pNroTicket", Integer.parseInt(vNroComprob));
            LibReportes.parameters.put("pCajero", getNombreCajero(codCajero));
            LibReportes.parameters.put("pMsgPieBoleta", getMsgPieBoleta());
            LibReportes.parameters.put("pNombreCliente", jTFNombreCliente.getText().trim());
            LibReportes.parameters.put("pTerminal", FormMain.codCaja);
            LibReportes.parameters.put("pFecVigencia", fecVigencia);
            LibReportes.parameters.put("pNroTurno", Integer.parseInt(nroTurno));
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
    
    private String getNombreCajero(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            EmpleadoCtrl ctrl = new EmpleadoCtrl();
            result = ctrl.getNombreEmpleado(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private void getDatosEmpresaReport(){
    
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
    
    private int getPctDescuentoVentas(){
        int descuento = 0;
        try{
            String sql = "SELECT valor FROM parametros WHERE parametro = 'CAJA_MAX_PCT_DESCUENTO'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    descuento = rs.getInt("valor");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return descuento;
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPVentas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesVenta = new javax.swing.JTable();
        jPFormaPago = new javax.swing.JPanel();
        jBVolver = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jTFNombreCliente = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFRucCliente = new javax.swing.JTextField();
        jBClientes = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jTFFaltanTotal = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTFVueltoTotal = new javax.swing.JTextField();
        jTFMontoRecibidoTotal = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jBConfirmar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jTFCodCuenta = new javax.swing.JTextField();
        jTFDenominacionCta = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTFMontoRecibido = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTFCotizacion = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTFTotalCambio = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTFormasPago = new javax.swing.JTable();
        jLMsgCuenta = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTFDescPorc = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTFDescMonto = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLDescuento = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTFTotalVenta = new javax.swing.JTextField();
        jTFExento = new javax.swing.JTextField();
        jTFGrav5 = new javax.swing.JTextField();
        jTFGrav10 = new javax.swing.JTextField();
        jTFIva5 = new javax.swing.JTextField();
        jTFIva10 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLPanelUbication = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLAyudaTeclas = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTFCodArticulo = new javax.swing.JTextField();
        jTFDescArticulo = new javax.swing.JTextField();
        jTFSiglaVenta = new javax.swing.JTextField();
        jTFUm = new javax.swing.JTextField();
        jTFPrecioVenta = new javax.swing.JTextField();
        jTFCantVenta = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLMensajes = new javax.swing.JLabel();
        jBTotalizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Ventas");
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 204));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N

        jPVentas.setBackground(new java.awt.Color(204, 255, 204));
        jPVentas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPVentasFocusGained(evt);
            }
        });

        jTDetallesVenta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTDetallesVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Barras", "Descripción", "Sigla", "U/M", "IVA", "Precio Venta", "Cantidad", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Float.class, java.lang.Double.class
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
        jTDetallesVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDetallesVentaFocusGained(evt);
            }
        });
        jTDetallesVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallesVentaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetallesVenta);
        if (jTDetallesVenta.getColumnModel().getColumnCount() > 0) {
            jTDetallesVenta.getColumnModel().getColumn(0).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(1).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(2).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(3).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(4).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(5).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(6).setResizable(false);
            jTDetallesVenta.getColumnModel().getColumn(7).setResizable(false);
        }

        javax.swing.GroupLayout jPVentasLayout = new javax.swing.GroupLayout(jPVentas);
        jPVentas.setLayout(jPVentasLayout);
        jPVentasLayout.setHorizontalGroup(
            jPVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPVentasLayout.setVerticalGroup(
            jPVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ventas", jPVentas);

        jPFormaPago.setBackground(new java.awt.Color(102, 255, 153));

        jBVolver.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBVolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/volver24.png"))); // NOI18N
        jBVolver.setText("Volver a detalle");
        jBVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBVolverActionPerformed(evt);
            }
        });
        jBVolver.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBVolverKeyPressed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        jTFNombreCliente.setEditable(false);
        jTFNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("RUC:");

        jTFRucCliente.setEditable(false);
        jTFRucCliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jBClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/agregar16.png"))); // NOI18N
        jBClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClientesActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(102, 255, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setText("Faltan:");

        jTFFaltanTotal.setEditable(false);
        jTFFaltanTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jTFFaltanTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFFaltanTotal.setText("0.00");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setText("Vuelto:");

        jTFVueltoTotal.setEditable(false);
        jTFVueltoTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jTFVueltoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFVueltoTotal.setText("0.00");

        jTFMontoRecibidoTotal.setEditable(false);
        jTFMontoRecibidoTotal.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jTFMontoRecibidoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoRecibidoTotal.setText("0.00");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel24.setText("Recibido:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFVueltoTotal)
                    .addComponent(jTFMontoRecibidoTotal)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jTFFaltanTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFFaltanTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFMontoRecibidoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFVueltoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBConfirmar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmar.setText("Confirmar");
        jBConfirmar.setEnabled(false);
        jBConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Cuenta:");

        jTFCodCuenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCodCuenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCuenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCuentaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodCuentaFocusLost(evt);
            }
        });
        jTFCodCuenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCuentaKeyPressed(evt);
            }
        });

        jTFDenominacionCta.setEditable(false);
        jTFDenominacionCta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Monto:");

        jTFMontoRecibido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFMontoRecibido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoRecibido.setText("0.00");
        jTFMontoRecibido.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoRecibidoFocusGained(evt);
            }
        });
        jTFMontoRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoRecibidoKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Cotización:");

        jTFCotizacion.setEditable(false);
        jTFCotizacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFCotizacion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCotizacion.setText("0.00");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("Total Cambio:");

        jTFTotalCambio.setEditable(false);
        jTFTotalCambio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalCambio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalCambio.setText("0.00");

        jPanel3.setBackground(new java.awt.Color(102, 255, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Formas de pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jTFormasPago.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFormasPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Denominación", "Cotización", "Recibido", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTFormasPago);
        if (jTFormasPago.getColumnModel().getColumnCount() > 0) {
            jTFormasPago.getColumnModel().getColumn(0).setResizable(false);
            jTFormasPago.getColumnModel().getColumn(1).setResizable(false);
            jTFormasPago.getColumnModel().getColumn(2).setResizable(false);
            jTFormasPago.getColumnModel().getColumn(3).setResizable(false);
            jTFormasPago.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jLMsgCuenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLMsgCuenta.setForeground(new java.awt.Color(255, 0, 0));
        jLMsgCuenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLMsgCuenta.setText("***");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("Descuento:");

        jTFDescPorc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFDescPorc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDescPorc.setText("0.00");
        jTFDescPorc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescPorcFocusGained(evt);
            }
        });
        jTFDescPorc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescPorcKeyPressed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setText("%");

        jTFDescMonto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFDescMonto.setForeground(new java.awt.Color(255, 0, 0));
        jTFDescMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDescMonto.setText("0.00");
        jTFDescMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescMontoFocusGained(evt);
            }
        });
        jTFDescMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescMontoKeyPressed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setText("Gs.");

        jLDescuento.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescuento.setForeground(new java.awt.Color(255, 0, 0));
        jLDescuento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLDescuento.setText("***");

        javax.swing.GroupLayout jPFormaPagoLayout = new javax.swing.GroupLayout(jPFormaPago);
        jPFormaPago.setLayout(jPFormaPagoLayout);
        jPFormaPagoLayout.setHorizontalGroup(
            jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPFormaPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPFormaPagoLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jBVolver)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBConfirmar))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPFormaPagoLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTFDescPorc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDescMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28)
                                .addGap(26, 26, 26))
                            .addComponent(jLDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPFormaPagoLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTFMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel19))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel20))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel21)
                                            .addComponent(jTFTotalCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jBClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLMsgCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPFormaPagoLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jTFCodCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDenominacionCta))
                            .addComponent(jLabel18)))
                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPFormaPagoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBConfirmar, jBVolver});

        jPFormaPagoLayout.setVerticalGroup(
            jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPFormaPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBClientes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTFNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(jTFRucCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFDescPorc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTFCodCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTFDenominacionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(jTFDescMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28)
                        .addComponent(jLabel26)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLDescuento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFTotalCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLMsgCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPFormaPagoLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Forma Pago", jPFormaPago);

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Total GS.");

        jTFTotalVenta.setEditable(false);
        jTFTotalVenta.setBackground(new java.awt.Color(255, 255, 102));
        jTFTotalVenta.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jTFTotalVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalVenta.setText("000.00");

        jTFExento.setEditable(false);
        jTFExento.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFExento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFExento.setText("000.00");

        jTFGrav5.setEditable(false);
        jTFGrav5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFGrav5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFGrav5.setText("000.00");

        jTFGrav10.setEditable(false);
        jTFGrav10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFGrav10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFGrav10.setText("000.00");

        jTFIva5.setEditable(false);
        jTFIva5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFIva5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFIva5.setText("000.00");

        jTFIva10.setEditable(false);
        jTFIva10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFIva10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFIva10.setText("000.00");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Exenta     ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Grav 5%");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Grav 10%");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setText("Totales");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(340, 340, 340))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFGrav10)
                                    .addComponent(jTFGrav5)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jTFExento, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFIva5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFIva10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jTFTotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFExento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFGrav5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIva5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFGrav10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIva10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFTotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/atomLogo128.png"))); // NOI18N

        jLPanelUbication.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLPanelUbication.setText("Ventas");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ESC: Forma de Pago");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("F1: Búsqueda de Artículos");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("F2: Precio de Costo de Artículo");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("DELETE: Eliminar Artículo ");

        jLAyudaTeclas.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLAyudaTeclas.setText("Teclas de Ayuda");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("F5: Anular venta");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLPanelUbication)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLAyudaTeclas, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel25))
                        .addGap(27, 27, 27))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLAyudaTeclas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLPanelUbication)
                .addGap(43, 43, 43))
        );

        jTFCodArticulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
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
        jTFDescArticulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jTFSiglaVenta.setEditable(false);
        jTFSiglaVenta.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jTFUm.setEditable(false);
        jTFUm.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFUm.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFPrecioVenta.setEditable(false);
        jTFPrecioVenta.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFPrecioVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFCantVenta.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTFCantVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCantVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantVentaFocusGained(evt);
            }
        });
        jTFCantVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantVentaKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Código");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Descripción");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("Empaque");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("Precio");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Cantidad");

        jLMensajes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLMensajes.setForeground(new java.awt.Color(255, 0, 0));
        jLMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jBTotalizar.setText("Totalizar");
        jBTotalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTotalizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 399, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLMensajes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTFSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFUm, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jTFCantVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 855, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 54, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(468, 468, 468)
                .addComponent(jBTotalizar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFCodArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFSiglaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFUm, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFCantVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBTotalizar)
                .addContainerGap(14, Short.MAX_VALUE))
        );

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

        setSize(new java.awt.Dimension(1283, 575));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
                
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jTFCodArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodArticuloKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            if(dtmDetallesVenta.getRowCount() > 0){
                jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPFormaPago));
                jTabbedPane1.setEnabledAt(0, false);
                jTabbedPane1.setEnabledAt(1, true);
                jTFFaltanTotal.setForeground(Color.red);
                jTFFaltanTotal.setText(jTFTotalVenta.getText().trim());                                        
                jTFDescArticulo.setText("");
                jTFCodArticulo.setText("");
                jTFSiglaVenta.setText("");
                jTFUm.setText("");
                jTFPrecioVenta.setText("");
                jTFCantVenta.setText("");
                jTFCodCliente.setText("1");
                jTFCodCuenta.setText("1");
                jTFCodCliente.requestFocus();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            BuscaArticuloVenta busqueda = new BuscaArticuloVenta(new JFrame(), true);
            busqueda.dConsultas("articulo", "descripcion", "cod_articulo", "descripcion", "precio_venta", "empaque", "stock", "Código", "Descripción", "Precio de Venta", "Empaque", "Stock");
            busqueda.pack();
            busqueda.setText(jTFCodArticulo, jTFSVta);
            busqueda.setVisible(true);
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFCodArticulo.getText().trim().equals("")){
                jTFCantVenta.requestFocus();
                jTFCantVenta.setText("1");
                if(dtmDetallesVenta.getRowCount() > 0){
                    verificarArticuloEnDetalle(vCodigoArticulo);
                }
            }
        }
    }//GEN-LAST:event_jTFCodArticuloKeyPressed

    private void jBVolverKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBVolverKeyPressed
        jPVentas.setEnabled(true);
        jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPVentas));
    }//GEN-LAST:event_jBVolverKeyPressed

    private void jBVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBVolverActionPerformed
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(0, true);
        jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPVentas));
        jTFCodArticulo.requestFocus();
        jTFCodArticulo.grabFocus();
        limpiarTablaFormaPago();
        totalizarFormaPago();
        jBConfirmar.setEnabled(false);
        jTFVueltoTotal.setText("0.00");
        jTFDescPorc.setText("0.00");
        jTFDescMonto.setText("0.00");
        jTFNombreCliente.setText("");
        jTFDenominacionCta.setText("");
    }//GEN-LAST:event_jBVolverActionPerformed

    private void jTDetallesVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallesVentaKeyPressed
        int filaSeleccionada = jTDetallesVenta.getSelectedRow();
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            for(int i = 0; i < jTDetallesVenta.getRowCount(); i++){
                if(i == filaSeleccionada){
                    dtmDetallesVenta.removeRow(i);
                    jBTotalizar.doClick();
                }
            }
            //jBTotalizar.doClick();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            jTFCodArticulo.requestFocus();
        }
    }//GEN-LAST:event_jTDetallesVentaKeyPressed

    private void jTFCodArticuloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusGained
        jTFCodArticulo.selectAll();
        jLMensajes.setText("");
    }//GEN-LAST:event_jTFCodArticuloFocusGained

    private void jTFCodArticuloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodArticuloFocusLost
        if(!jTFCodArticulo.getText().trim().equals("")){
            String codigo = getCodArticulo(jTFCodArticulo.getText().trim());
            vCodigoArticulo = codigo;
            vBarrasArticulo = getBarrasArticulo(jTFCodArticulo.getText().trim());
            jTFCodArticulo.setText(codigo);
            cargaDatosArticulo(codigo, jTFSVta.getText());
            jTFSVta.setText("");
        }
    }//GEN-LAST:event_jTFCodArticuloFocusLost

    private void jTFCantVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantVentaFocusGained
        jTFCantVenta.selectAll();
        jTFCantVenta.selectAll();
        if(jTFSiglaVenta.getText().trim().equals("UN")){
            jTFCantVenta.setDocument(new MaxLength(12, "", "ENTERO"));
        }else if(!jTFSiglaVenta.getText().trim().equals("UN")){
            jTFCantVenta.setDocument(new MaxLength(12, "", "FLOAT"));
        }
    }//GEN-LAST:event_jTFCantVentaFocusGained

    private void jTFCantVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantVentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCantVenta.getText().trim().equals("0")){
               jTFCodArticulo.requestFocus();
            }else if(!jTFCantVenta.getText().trim().equals("")){
                jTFCodArticulo.requestFocus();
                if(Double.valueOf(jTFCantVenta.getText().trim()) > 1000){
                    String sigla = "DEFAULT";
                    if(jTFSiglaVenta.getText().trim().equals("UN")){
                        sigla = "UNIDADES";
                    }else if(jTFSiglaVenta.getText().trim().equals("KG")){
                        sigla = "KILOGRAMOS";
                    }else if(jTFSiglaVenta.getText().trim().equals("CJ")){
                        sigla = "CAJAS";
                    }else if(jTFSiglaVenta.getText().trim().equals("LT")){
                        sigla = "LITROS";
                    }else if(jTFSiglaVenta.getText().trim().equals("MT")){
                        sigla = "METROS";
                    }
                    JOptionPane.showMessageDialog(this, "No supere la cantidad de 1000 " + sigla + "!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    jTFCantVenta.requestFocus();
                    jTFCantVenta.setText("0");
                }else{
                    addArticuloDetalle();
                }
            }
        }
    }//GEN-LAST:event_jTFCantVentaKeyPressed

    private void jTDetallesVentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDetallesVentaFocusGained
        jLMensajes.setText("DELETE/SUPRIMIR para eliminar artículo en detalle! \nESC para agregar artículo");
    }//GEN-LAST:event_jTDetallesVentaFocusGained

    private void jBTotalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTotalizarActionPerformed
        totalizarMontos();
    }//GEN-LAST:event_jBTotalizarActionPerformed

    private void jPVentasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPVentasFocusGained
        jTFCodArticulo.requestFocus();
    }//GEN-LAST:event_jPVentasFocusGained

    private void jBClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClientesActionPerformed
        Clientes clientes = new Clientes(new JFrame(), true);
        clientes.pack();
        clientes.setVisible(true);
    }//GEN-LAST:event_jBClientesActionPerformed

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jLMsgCuenta.setText("");
        jLDescuento.setText("");
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
            jTFDescPorc.requestFocus();
        }
    }//GEN-LAST:event_jTFCodClienteKeyPressed

    private void jTFCodClienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusLost
        getDatosCliente(jTFCodCliente.getText().trim());
    }//GEN-LAST:event_jTFCodClienteFocusLost

    private void jTFCodCuentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCuentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_C){
            if(!jTFCodCliente.getText().trim().equals("1")){
                getDenominacionCtaCodCliente(jTFCodCliente.getText().trim());
            }else{
                jTFCodCuenta.setText("1");
                jLMsgCuenta.setText("Debe seleccionar un cliente");
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_D){
            evt.consume();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFMontoRecibido.requestFocus();
            jTFMontoRecibido.setText("0");
            jLMsgCuenta.setText("");
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            DlgConsultasCuentasMonedas cuentas = new DlgConsultasCuentasMonedas(new JFrame(), true);
            cuentas.pack();
            cuentas.setText(jTFCodCuenta);
            cuentas.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodCuentaKeyPressed

    private void jTFCodCuentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCuentaFocusLost
        if(!jTFCodCuenta.getText().trim().equals("")){
            getCtaDenCotiz(jTFCodCuenta.getText().trim());
            jLMsgCuenta.setText("");
            double totalVenta = 0, totalRecibido = 0, descuento = 0;
            descuento = Double.parseDouble(jTFDescMonto.getText().trim().replace(",", ""));
            totalVenta = (Double.parseDouble(jTFTotalVenta.getText().trim().replace(",", ""))) - descuento;
            totalRecibido = Double.parseDouble(jTFMontoRecibidoTotal.getText().trim().replace(",", ""));
            if( totalRecibido >= totalVenta ){
                jBConfirmar.setEnabled(true);
                jBConfirmar.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFCodCuentaFocusLost

    private void jTFCodCuentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCuentaFocusGained
        jTFCodCuenta.selectAll();
        jLMsgCuenta.setText("Tecla C para cobrar en cuenta del cliente!");
    }//GEN-LAST:event_jTFCodCuentaFocusGained

    private void jTFMontoRecibidoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoRecibidoFocusGained
        jTFMontoRecibido.selectAll();
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",", ""));
        double montoFaltante = Double.parseDouble(jTFFaltanTotal.getText().trim().replace(",", ""));
        double montoCambio = montoFaltante / cotizacion;
        if(cotizacion != 1){
            jTFTotalCambio.setText(decimalFormat.format(montoCambio));
        }else{
            jTFTotalCambio.setText(jTFCotizacion.getText().trim());
        }
    }//GEN-LAST:event_jTFMontoRecibidoFocusGained

    private void jTFMontoRecibidoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoRecibidoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFMontoRecibido.getText().equals("0") || jTFMontoRecibido.getText().equals("")){
                jTFCodCuenta.requestFocus();
            }else{
                addFormaPago();
                totalizarFormaPago();
                
                //calculo totales 
                    double faltaTotal = 0, recibidoTotal = 0, recibidoAhora = 0, vueltoTotal = 0, cotizacion = 0;
                    faltaTotal = (Double.parseDouble(jTFTotalVenta.getText().trim().replace(",", ""))) - 
                                 (Double.parseDouble(jTFDescMonto.getText().trim().replace(",", "")));
                    
                    recibidoAhora = Double.parseDouble(jTFMontoRecibido.getText().trim().replace(",", ""));
                    cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",", ""));
                    recibidoTotal = Double.parseDouble(jTFMontoRecibidoTotal.getText().trim().replace(",", ""));
                    vueltoTotal = faltaTotal - recibidoTotal;
                    System.out.println("VUELTO TOTAL: " + vueltoTotal);
                    System.out.println("RECIBIDO TOTAL: " + recibidoTotal);
                    
                    if(recibidoTotal > faltaTotal){
                        jTFFaltanTotal.setForeground(Color.black);
                        jTFFaltanTotal.setText("0.00");
                        jTFVueltoTotal.setText(decimalFormat.format(vueltoTotal * (-1)));
                    }

                    if(recibidoTotal == faltaTotal){
                        jTFFaltanTotal.setForeground(Color.black);
                        jTFVueltoTotal.setForeground(Color.black);
                        jTFFaltanTotal.setText("0.00");
                        jTFVueltoTotal.setText("0.00"); 
                    }

                    if(recibidoTotal < faltaTotal){
                        jTFFaltanTotal.setForeground(Color.red);
                        jTFFaltanTotal.setText(decimalFormat.format(vueltoTotal));
                    }

                    jTFCodCuenta.requestFocus();
                    jTFMontoRecibido.setText("0");
                //fin calculo totales
                
            }
        }
    }//GEN-LAST:event_jTFMontoRecibidoKeyPressed

    private void jTFDescPorcFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescPorcFocusGained
        jTFDescPorc.selectAll();
    }//GEN-LAST:event_jTFDescPorcFocusGained

    private void jTFDescMontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescMontoFocusGained
        jTFDescMonto.selectAll();
    }//GEN-LAST:event_jTFDescMontoFocusGained

    private void jTFDescPorcKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescPorcKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFDescPorc.getText().trim().equals("0") && !jTFDescPorc.getText().trim().equals("0.00") && !jTFDescPorc.getText().trim().equals(".00") && 
               !jTFDescPorc.getText().trim().equals("") ){
                System.out.println("ENTRO EN EL CALCULO");
                float porcentajeDescuento = Float.parseFloat(jTFDescPorc.getText().trim());
                double totalVenta = Double.parseDouble(jTFTotalVenta.getText().trim().replace(",", ""));
                double descuentoMonto = Math.round((porcentajeDescuento/100) * totalVenta);
                jTFDescMonto.setText(decimalFormat.format(descuentoMonto));      
                jTFDescMonto.grabFocus();
            }else{
                jTFDescMonto.setText("0");
                jTFDescMonto.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFDescPorcKeyPressed

    private void jTFDescMontoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescMontoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFDescMonto.getText().trim().equals("") ){
                
                double montoDescuento = Double.parseDouble(jTFDescMonto.getText().trim().replace(",", ""));
                double totalVenta = Double.parseDouble(jTFTotalVenta.getText().trim().replace(",", ""));
                double totalRecibido = Double.parseDouble(jTFMontoRecibidoTotal.getText().trim().replace(",", ""));
                double porcDesc = (montoDescuento * 100) / totalVenta;
                jTFDescPorc.setText(decimalFormat.format(porcDesc));

                if(porcDesc <= getPctDescuentoVentas()){
                    double totalConDescuento = (totalVenta - montoDescuento) - (totalRecibido);
                    if(totalRecibido == totalConDescuento){
                        jTFDescMonto.setText(decimalFormat.format(montoDescuento));
                        jTFFaltanTotal.setForeground(Color.black);
                        jTFFaltanTotal.setText("0");
                    }else{
                        jTFDescMonto.setText(decimalFormat.format(montoDescuento));
                        jTFFaltanTotal.setText(decimalFormat.format(totalConDescuento));
                    }
                    jLDescuento.setText("");
                        jTFCodCuenta.requestFocus();
                }else{
                    JOptionPane.showMessageDialog(this, "Supera el % de descuento permitido (" + getPctDescuentoVentas() + "%)", "ATENCIÓN", JOptionPane.WARNING_MESSAGE);
                    jLDescuento.setText("Descuento no aplicado. Supera lo permitido (" + getPctDescuentoVentas() + "%)");
                    jTFDescPorc.grabFocus();
                }                
            }
        }
    }//GEN-LAST:event_jTFDescMontoKeyPressed

    private void jBConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarActionPerformed
        double porcDesc = Double.parseDouble(jTFDescPorc.getText().trim());
        if(porcDesc <= getPctDescuentoVentas()){
            if(grabarDatosVenta()){
                System.out.println("ATENCION: Datos grabados correctamente!");
                System.out.println("VUELTO: " + Double.parseDouble(jTFVueltoTotal.getText().trim().replace(",", "")));
                DlgFinVenta venta = new DlgFinVenta(new JFrame(), true, Double.parseDouble(jTFVueltoTotal.getText().trim().replace(",", "")));
                venta.pack();
                venta.setVisible(true);
                imprimirBoletaVenta();
                jTabbedPane1.setSelectedIndex(jTabbedPane1.indexOfComponent(jPVentas));
                jTFCodArticulo.requestFocus();
                setComponentesGrabado();
            }else{
                System.out.println("ATENCION: Error grabando Venta!");
            }
        }else{
            JOptionPane.showMessageDialog(this, "Supera el % de descuento permitido (" + getPctDescuentoVentas() + "%)", "DESCUENTO", JOptionPane.WARNING_MESSAGE);
            limpiarTablaFormaPago();
            totalizarFormaPago();
            totalizarMontos();
            jTFDescPorc.grabFocus();
        }        
    }//GEN-LAST:event_jBConfirmarActionPerformed

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
            java.util.logging.Logger.getLogger(Ventas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBClientes;
    private javax.swing.JButton jBConfirmar;
    private javax.swing.JButton jBTotalizar;
    private javax.swing.JButton jBVolver;
    private javax.swing.JLabel jLAyudaTeclas;
    private javax.swing.JLabel jLDescuento;
    private javax.swing.JLabel jLMensajes;
    private javax.swing.JLabel jLMsgCuenta;
    private javax.swing.JLabel jLPanelUbication;
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
    private javax.swing.JPanel jPFormaPago;
    private javax.swing.JPanel jPVentas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTDetallesVenta;
    private javax.swing.JTextField jTFCantVenta;
    public static javax.swing.JTextField jTFCodArticulo;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCuenta;
    private javax.swing.JTextField jTFCotizacion;
    private javax.swing.JTextField jTFDenominacionCta;
    private javax.swing.JTextField jTFDescArticulo;
    private javax.swing.JTextField jTFDescMonto;
    private javax.swing.JTextField jTFDescPorc;
    private javax.swing.JTextField jTFExento;
    private javax.swing.JTextField jTFFaltanTotal;
    private javax.swing.JTextField jTFGrav10;
    private javax.swing.JTextField jTFGrav5;
    private javax.swing.JTextField jTFIva10;
    private javax.swing.JTextField jTFIva5;
    private javax.swing.JTextField jTFMontoRecibido;
    private javax.swing.JTextField jTFMontoRecibidoTotal;
    private javax.swing.JTextField jTFNombreCliente;
    private javax.swing.JTextField jTFPrecioVenta;
    private javax.swing.JTextField jTFRucCliente;
    private javax.swing.JTextField jTFSiglaVenta;
    private javax.swing.JTextField jTFTotalCambio;
    private javax.swing.JTextField jTFTotalVenta;
    private javax.swing.JTextField jTFUm;
    private javax.swing.JTextField jTFVueltoTotal;
    private javax.swing.JTable jTFormasPago;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
