/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModCaja;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import views.busca.DlgConsultas;
import views.busca.DlgConsultasCuentasMonedas;

/**
 *
 * @author Andres
 */
public class RegistroCobroClientesFormaPago extends javax.swing.JDialog {

    public static DefaultTableModel dtmFormaPago;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    double total_docs_seleccionados = 0, total_forma_pago = 0, diferencia = 0; 
    String tipo_cuenta = "";
    
    public RegistroCobroClientesFormaPago(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configTabla();
        configCampos();
        total_docs_seleccionados = RegistroCobroClientes.total_docs_mas_interes;
        jTFTotalDocsSeleccionados.setText(decimalFormat.format(total_docs_seleccionados));
        jLHint.setText("");
        jTFFecEmision.setText(RegistroCobroClientes.fecVigencia);
        jTFFecVenc.setText(RegistroCobroClientes.fecVigencia);
        totalizar();
        jTFCodCuenta.setText("1");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void configTabla(){
        dtmFormaPago = (DefaultTableModel)jTFormaPago.getModel();
        
        jTFormaPago.getColumnModel().getColumn(0).setPreferredWidth(20); // cta
        jTFormaPago.getColumnModel().getColumn(1).setPreferredWidth(50); // descripcion 
        jTFormaPago.getColumnModel().getColumn(2).setPreferredWidth(20); // tipo
        jTFormaPago.getColumnModel().getColumn(3).setPreferredWidth(50); // nro doc
        jTFormaPago.getColumnModel().getColumn(4).setPreferredWidth(10); // bco 
        jTFormaPago.getColumnModel().getColumn(5).setPreferredWidth(50); // nombre bco
        jTFormaPago.getColumnModel().getColumn(6).setPreferredWidth(30); // fec emision
        jTFormaPago.getColumnModel().getColumn(7).setPreferredWidth(30); // fec vencimiento
        jTFormaPago.getColumnModel().getColumn(8).setPreferredWidth(50); // valor 
        jTFormaPago.getColumnModel().getColumn(9).setPreferredWidth(50); // cotizacion
        jTFormaPago.getColumnModel().getColumn(10).setPreferredWidth(50); // valor final 
        
        utiles.Utiles.punteroTablaF(jTFormaPago, this);        
        jTFormaPago.setFont(new Font("Tahoma", 1, 10) );
        jTFormaPago.setRowHeight(18);
    }

    private void getCtaDenCotiz(String codigo){        
        String sql = "SELECT cod_cuenta, moneda.cotiz_compra, denominacion_cta, tipo_cuenta "
                   + "FROM cuenta "
                   + "INNER JOIN moneda "
                   + "ON cuenta.cod_moneda = moneda.cod_moneda "
                   + "WHERE cod_cuenta = " + codigo + " AND cuenta.activa = 'S'";
        System.out.println("GET DENOMINACION COTIZACION CTA: " + sql);
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    tipo_cuenta = rs.getString("tipo_cuenta");
                    System.out.println("TIPO CUENTA DESDE FOCUS LOST: " + tipo_cuenta);
                    if(tipo_cuenta.equals("CRE")){
                        jLHint.setText("NO PUEDE PAGAR CON CUENTA CREDITO!");
                    }else{
                        jLHint.setText("");
                        jTFDenominacionCta.setForeground(Color.black);
                        jTFCotizacion.setText(decimalFormat.format(rs.getInt("cotiz_compra")));
                        jTFDenominacionCta.setText(rs.getString("denominacion_cta"));
                        jTFTipoCuenta.setText(rs.getString("tipo_cuenta"));
                    }
                }else{
                    jTFDenominacionCta.setForeground(Color.red);
                    jTFCotizacion.setText("1");
                    jTFTipoCuenta.setText("");
                    jTFDenominacionCta.setText("INEXISTENTE");
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
    
    private void controlFoco(){
        System.out.println("CONTROL TIPO CUENTA DESDE CONTROL FOCO: " + tipo_cuenta);
        if(tipo_cuenta.equals("EFE")){
            jTFNroDoc.setEditable(false);
            jTFNroDoc.setText("0");
            jTFCodBco.setEditable(false);
            jTFCodBco.setText("1");
            jTFNombreBco.setText("GENERICO");
            jTFFecVenc.setEditable(false);
            jTFValor.grabFocus();
        }else{
            jTFNroDoc.setEditable(true);
            jTFCodBco.setEditable(true);
            jTFFecVenc.setEditable(true);
            jTFNroDoc.grabFocus();
        }
    }
    
    private String getNombreBanco(String codigo) {
        String banco = "";
        try{
            String sql = "SELECT nombre FROM banco WHERE cod_banco = " + codigo;
            System.out.println("BANCO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    banco = rs.getString("nombre");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return banco;
    }
    
    private void totalizar(){
        double totalGrilla = 0;
        for(int i = 0; i < dtmFormaPago.getRowCount(); i++){
            totalGrilla += Double.parseDouble(jTFormaPago.getValueAt(i, 10).toString());
        }
        total_docs_seleccionados = Double.parseDouble(jTFTotalDocsSeleccionados.getText().trim().replace(",", ""));
        total_forma_pago = totalGrilla;
        diferencia = total_forma_pago - total_docs_seleccionados;
        jTFTotalFormaPago.setText(decimalFormat.format(total_forma_pago));
        jTFDiferencia.setText(decimalFormat.format(diferencia));
        jTFCodCuenta.requestFocus();
        if(diferencia < 0){
            jLDiferencia.setText("FALTAN");
            jTFDiferencia.setBackground(new Color(255,153,153));
        }
        
        if(diferencia == 0){
            jLDiferencia.setText("SIN DIFERENCIAS");
            jTFDiferencia.setBackground(new Color(240,240,240));
        }
        
        if(diferencia > 0){
            jLDiferencia.setText("VUELTO");
            jTFDiferencia.setBackground(new Color(240,240,240));
        }
    }
    
    private void addFormaPagoDetalle(){
        /* COLUMNS
         * cta (string)
         * descripcion (string) 
         * tipo (string)
         * nro doc (string)
         * bco (string)
         * nom bco (string) 
         * fec emision (string)
         * fec venc (string) 
         * valor (double)
         * cotizacion (double)
         * valor final (double)
        */
        
        String cta = jTFCodCuenta.getText().trim();
        String descCta = jTFDenominacionCta.getText().trim();
        String tipoCta = jTFTipoCuenta.getText().trim();
        String nroDoc = jTFNroDoc.getText().trim();
        String codBco = jTFCodBco.getText().trim();
        String nomBco = jTFNombreBco.getText().trim();
        String fecEmision = jTFFecEmision.getText().trim();
        String fecVenc = jTFFecVenc.getText().trim();
        double valor = Double.parseDouble(jTFValor.getText().trim());
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",", ""));
        double valorFinal = Double.parseDouble(jTFValorFinal.getText().trim().replace(",", ""));
        
        // dtmDetallesVenta.addRow(new Object[]{vCodArticulo, vBarras, vDescripcion, vSigla, vUm, vIva, vPrecioVenta, vCant, vTotal});
        
        dtmFormaPago.addRow(new Object[]{cta, descCta, tipoCta, nroDoc, codBco, nomBco, fecEmision, fecVenc, valor, cotizacion, valorFinal});
    }
    
    private void limpiarTabla(){
        for(int i = 0; i < jTFormaPago.getRowCount(); i++){
            dtmFormaPago.removeRow(i);
            i--;
        }
    }
    
    private void configCampos(){
        jTFCodCuenta.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFNroDoc.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFCodBco.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFValor.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFFecEmision.setInputVerifier(new FechaInputVerifier(jTFFecEmision));
        jTFFecVenc.setInputVerifier(new FechaInputVerifier(jTFFecVenc));
        
        jTFCodCuenta.addFocusListener(new Focus());
        jTFNroDoc.addFocusListener(new Focus());
        jTFCodBco.addFocusListener(new Focus());
        jTFValor.addFocusListener(new Focus());
        jTFFecEmision.addFocusListener(new Focus());
        jTFFecVenc.addFocusListener(new Focus());
    }
    
    private boolean grabarDatos(){
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
        /*int iterador;
        int cantFilasDetallesDocs = RegistroCobroClientes.jTDetalleDocs.getRowCount();
        
        boolean resultPagoCliCab = false;
        boolean resultPagoCliDet = false;
        boolean resultMovTesoreria = false;
        boolean resultUpdateVentaDetCuotas= true;
        boolean problemFound = false;
        boolean resultOperacionGrabaCobro = true;
        
        // Datos globales a grabar
        int cobroCodEmpresa = Integer.parseInt(RegistroCobroClientes.jCBCodEmpresa.getSelectedItem().toString());
        int cobroCodLocal = Integer.parseInt(RegistroCobroClientes.jCBCodLocal.getSelectedItem().toString());
        int cobroCodCaja = Integer.parseInt(RegistroCobroClientes.jTFCodCaja.getText().trim());
        int cobroNroTurno = Integer.parseInt(RegistroCobroClientes.jTFNroTurno.getText().trim());
        int cobroNroPago = Integer.parseInt(RegistroCobroClientes.jTFNroPago.getText().trim());
        int cobroCodCliente = Integer.parseInt(RegistroCobroClientes.jTFCodCliente.getText().trim());
        String cobroFecPago = RegistroCobroClientes.jTFFecCobro.getText().trim();
        double montoPago = Double.parseDouble(jTFTotalFormaPago.getText().trim().replace(",", ""));
        int montoRetenido = 0, nroAsiento = 0;
        String estado = "V";
        int cobroCodCobrador = Integer.parseInt(RegistroCobroClientes.jTFCodEmpleado.getText().trim());
        String cobroNroRecibo = RegistroCobroClientes.jTFNroRecibo.getText().trim();
        String cobroObservacion = RegistroCobroClientes.jTFComentarios.getText().trim();
        double montoVuelto = 0, montoCredito = 0, montoDebito = 0;
        
        // Cobro Cabecera - si es que hay diferencia graba en venta_det_cuotas 
        
        // -- datos para el deb/cre que genera por la diferencia
        montoVuelto = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", ""));
        String tipo_documento_venta_det = "";
        String obs_venta_det = "DIFERENCIA EN COBRO NRO " + cobroNroPago;
        int cod_sector_venta_det = Integer.parseInt(utiles.Utiles.getCodSectorDefault(String.valueOf(cobroCodLocal)));
        String vencimiento_venta_det = calculaVencimiento(cobroFecPago, getCondicionPlazoCreditoCliente(cobroCodCliente));
        String tipo_insert = "";
        // -- fin datos para el deb/cre que genera por la diferencia
        
        if(montoVuelto == 0){
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
        }else if(montoVuelto > 0){
            int grabarMas = JOptionPane.showConfirmDialog(this, "Qué hacer con la diferencia:\nSI = Generar CREDITO en la cuenta del cliente.\nNO = Devolver vuelto.",
                                                          "Diferencia", JOptionPane.YES_NO_CANCEL_OPTION);
            if(grabarMas == 0){
                montoCredito = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) ;
                montoVuelto = 0;
                montoDebito = 0;
                tipo_documento_venta_det = "CRE";
                tipo_insert = "SOBRANTE - CREDITO";
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                insertDiferencia(cobroCodEmpresa, cobroCodLocal, cod_sector_venta_det, cobroCodCaja, tipo_documento_venta_det, cobroNroPago, 
                                 cobroNroPago, cobroFecPago, cobroCodCliente, 1, 1, vencimiento_venta_det, montoCredito, montoRetenido, 1, 
                                 1, 0, estado, cobroCodCobrador, 0, obs_venta_det, cobroNroTurno, "N", 0, "0", 0, tipo_insert);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
            }else if(grabarMas == 1){
                montoCredito = 0;
                montoVuelto = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) * -1;
                montoDebito = 0;
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                }
            }
        }else{ 
            
            int grabarMenos = JOptionPane.showConfirmDialog(this, "Si continua la operación generará un DEBITO por el faltante.", "Diferencia", 
                                                            JOptionPane.YES_NO_OPTION);
            if(grabarMenos == 0){
                montoDebito = Double.parseDouble(jTFDiferencia.getText().trim().replace(",", "")) * -1;
                montoVuelto = 0;
                montoCredito = 0;
                tipo_documento_venta_det = "DEB";
                tipo_insert = "FALTANTE - DEBITO";
                
                resultOperacionGrabaCobro = grabarPagoCab(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroTurno, cobroNroPago, cobroCodCliente, cobroFecPago, 
                                                          montoPago, montoRetenido, cobroNroRecibo, nroAsiento, estado, cobroCodCobrador, 
                                                          cobroObservacion, cobroCodCobrador, montoVuelto, montoCredito, montoDebito);
                
                insertDiferencia(cobroCodEmpresa, cobroCodLocal, cod_sector_venta_det, cobroCodCaja, tipo_documento_venta_det, cobroNroPago, 
                                 cobroNroPago, cobroFecPago, cobroCodCliente, 1, 1, vencimiento_venta_det, montoDebito, montoRetenido, 1,
                                 1, 0, estado, cobroCodCobrador, 0, obs_venta_det, cobroNroTurno, "N", 0, "0", 0,tipo_insert);
                
                if(!resultOperacionGrabaCobro){
                    resultPagoCliCab = true;
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    resultUpdateVentaDetCuotas = true;
                }
            }
        }
        
        // Cobro detalles 
        
        int secuencia;
        for(secuencia = 0; secuencia < cantFilasDetallesDocs; secuencia++){
            if(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 10).toString() == "true"){
                String tipo_comprob = RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 1).toString();
                int nro_comprobante = Integer.parseInt(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 0).toString());
                int nro_cuota = Integer.parseInt(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 2).toString());
                String fec_comprob = RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 4).toString();
                String fec_venc = RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 5).toString();
                int cod_moneda = 1;
                int tipo_cambio = 1, monto_retenido = 0, afecta = 0, nroTimbrado = 0;
                String codPCuenta = "0";
                double monto_comprob = Double.parseDouble(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 7).toString());
                int interes = Integer.parseInt(RegistroCobroClientes.jTFInteres.getText().trim());
                double valor_interes = Double.parseDouble(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 8).toString());
                String observacion = getObservacionDoc(nro_comprobante, cobroCodCliente, tipo_comprob);
                int can_cuota = Integer.parseInt(RegistroCobroClientes.jTDetalleDocs.getValueAt(secuencia, 3).toString());
                int nro_caja_ticket = getNroCaja(nro_comprobante, cobroCodCliente, tipo_comprob);

                resultOperacionGrabaCobro = grabarPagoDet(cobroCodEmpresa, cobroCodLocal, cobroCodCaja, cobroNroPago, secuencia, tipo_comprob, nro_comprobante, 
                                                          nro_cuota, fec_comprob, fec_venc, cod_moneda, tipo_cambio, monto_comprob, montoRetenido, 
                                                          interes, valor_interes, afecta, codPCuenta, observacion, can_cuota, estado, cobroCodCliente, 
                                                          nroTimbrado);                                               
                
                if(!resultOperacionGrabaCobro){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar cobro detalle!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    break;
                }                               
                
                
                resultUpdateVentaDetCuotas = updateDocVentaDetCuotas(nro_comprobante, tipo_comprob, nro_caja_ticket, cobroCodCliente, nro_cuota, cobroNroRecibo, 
                                                                    cobroNroPago, cobroFecPago);
                
                if(!resultUpdateVentaDetCuotas){
                    JOptionPane.showMessageDialog(this, "ATENCION: Error updating detalles de cuota!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                    resultPagoCliDet = true;
                    resultMovTesoreria = true;
                    break;
                }                                
                
                System.out.println("RESULTPAGOCLICAB: " + resultPagoCliCab);
                System.out.println("RESULTPAGOCLIDET: " + resultPagoCliDet);
                System.out.println("RESULTMOVTESORERIA: " + resultMovTesoreria);

                if(resultPagoCliCab || resultPagoCliDet || resultMovTesoreria){
                    System.out.println("ENTRA EN EL PROCESO DE GRABACION - ROLLBACK");
                    problemFound = true;
                    boolean rollback = rollBacktDatos();
                    if(!rollback){
                        System.out.println("RESULTADO DEL ROLLBACK: " + rollback);
                        for(iterador = 0; iterador < 10; iterador++){
                            JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el RollBack!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                        }
                        problemFound = true;
                    }else{
                        break;
                    }
                }else{
                    System.out.println("ENTRA EN EL PROCESO DE GRABACION - COMMIT");
                    boolean commit = commitDatos();
                    if(!commit){
                        System.out.println("RESULTADO DEL COMMIT: " + commit);
                        for(iterador = 0; iterador < 10; iterador++){
                            JOptionPane.showMessageDialog(this, "ATENCION: Error al ejecutar el Commit!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                        }
                        problemFound = true;
                    }else{
                        break;
                    }
                }
            }
        }                
        return problemFound;*/
       return true;
    }
    
    private boolean updateDocVentaDetCuotas(int nroComprob, String tipoComprob, int codCaja, int codCliente, int nroCuota, String nroRecibo, int nroPago, 
                                            String fecPago){
        String sql = "UPDATE venta_det_cuotas SET nro_recibo = " + nroRecibo + ", fec_recibo = '" + fecPago + "'::date, nro_pago = " + nroPago + " "
                   + "WHERE nro_comprob = " + nroComprob + " AND tip_comprob = '" + tipoComprob + "' AND cod_caja = " + codCaja + " AND cod_cliente = " + codCliente + " "
                   + "AND nro_cuota = " + nroCuota;
        System.out.println("UPDATE VENTA_DET_CUOTAS (Coming from cobrocli): " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarPagoDet(int codEmpresa, int codLocal, int codCaja, int nroPago, int nroSecuencia, String tipoComprob, int nroComprob, 
                                  int nroCuota, String fecComprob, String fecVencimiento, int codMoneda, int tipoCambio, double montoComprob, int montoRetenido, 
                                  int pctInteres, double vlrInteres, int afecta, String codPCuenta, String obs, int canCuota, String estado, int codCliente, 
                                  int nroTimbrado){
        String sql = "INSERT INTO pagocli_det (cod_empresa, cod_local, cod_caja, nro_pago, nro_secuencia, tip_comprob, nro_comprob, nro_cuota, "
                   + "fec_comprob, fec_vencimiento, cod_moneda, tip_cambio, monto_comprob, monto_retenido, pct_interes, vlr_interes, afecta, "
                   + "cod_pcuenta, observacion, fec_vigencia, can_cuota, estado, cod_cliente, nro_timbrado) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", " + codCaja + ", " + nroPago + ", " + nroSecuencia + ", '" + tipoComprob + "', "
                   + nroComprob + ", " + nroCuota + ", '" + fecComprob + "'::date, '" + fecVencimiento + "'::date, " + codMoneda + ", " + tipoCambio + ", "
                   + montoComprob + ", " + montoRetenido + ", " + pctInteres + ", " + vlrInteres + ", " + afecta + ", '" + codPCuenta + "', '"
                   + obs + "', current_timestamp, " + canCuota + ", '" + estado + "', " + codCliente + ", " + nroTimbrado + ")";
        System.out.println("COBRO-CLI DETALLE: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean grabarPagoCab(int cod_empresa, int cod_local, int cod_caja, int nro_turno, int nro_pago, int cod_cliente, String fec_pago, 
                                  double monto_pago, int monto_retenido, String nro_recibo, int nro_asiento, String estado, int cod_cobrador, 
                                  String observacion, int cod_usuario, double monto_vuelto, double monto_credito, double monto_debito){
        String sql = "INSERT INTO pagocli_cab (cod_empresa, cod_local, cod_caja, nro_turno, nro_pago, cod_cliente, fec_pago, monto_pago, monto_retenido, "
                   + "nro_recibo, nro_asiento, estado, cod_cobrador, observacion, fec_vigencia, cod_usuario, monto_vuelto, monto_credito, monto_debito) "
                   + "VALUES(" + cod_empresa + ", " + cod_local + ", " + cod_caja + ", " + nro_turno + ", " + nro_pago + ", " + cod_cliente + ", '"
                   + fec_pago + "'::date, " + monto_pago + ", " + monto_retenido + ", '" + nro_recibo + "', " + nro_asiento + ", '" + estado + "', "
                   + cod_cobrador + ", '" + observacion + "', current_timestamp, " + cod_cobrador + ", " + monto_vuelto + ", " + monto_credito + ", "
                   + monto_debito + ")";
        System.out.println("COBRO-CLI CABECERA: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private void insertDiferencia(int codEmpresa, int codLocal, int codSector, int codCaja, String tipoComprob, int nroTicket, int nroComprob, String fecComprob, 
                              int codCliente, int nroCuota, int canCuota, String fecVencimiento, double montoCuota, int montoRetenido, int codMoneda, 
                              double tipoCambio, int nroRecibo, String estado, int codUsuario, int nroPago, String obs, int nroTurno, 
                              String esVenta, int nroTimbrado, String nroFactura, int nroTimbradoFactura, String tipoInsert){
        String sql = "INSERT INTO venta_det_cuotas (cod_empresa, cod_local, cod_sector, cod_caja, tip_comprob, nro_ticket, nro_comprob, fec_comprob, "
                   + "cod_cliente, nro_cuota, can_cuota, fec_vencimiento, monto_cuota, monto_retenido, cod_moneda, tip_cambio, nro_recibo, "
                   + "estado, cod_usuario, fec_vigencia, nro_pago, observacion, nro_turno, es_venta, nro_timbrado, ven_timbrado, nro_factura, nro_timbrado_factura) "
                   + "VALUES (" + codEmpresa + ", " + codLocal + ", " + codSector + ", " + codCaja + ", '" + tipoComprob + "', " + nroTicket + ", "
                   + nroComprob + ", '" + fecComprob + "'::date, " + codCliente + ", " + nroCuota + ", " + canCuota + ", '" + fecVencimiento + "', "
                   + montoCuota + ", " + montoRetenido + ", " + codMoneda + ", " + tipoCambio + ", " + nroRecibo + ", '" + estado + "', " + codUsuario + ", "
                   + "current_timestamp, " + nroPago + ", '" + obs + "', " + nroTurno + ", '" + esVenta + "', " + nroTimbrado + ", current_timestamp, '"
                   + nroFactura + "', " + nroTimbradoFactura + ")";
        System.out.println("INSERT DEBITO - (" + tipoInsert + "): " + sql);
        grabarPrevioCommit(sql);
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
    
    private boolean commitDatos() {
        boolean result = true;
        try {
            DBManager.conn.commit();
        } catch (Exception ex) {
            result = false;
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
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
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private String getObservacionDoc(int nroComprob, int codCliente, String tipComprob){
        String obs = "";
        try{
            String sql = "SELECT observacion FROM venta_det_cuotas WHERE nro_comprob = " + nroComprob + " AND cod_cliente = " + codCliente + " "
                       + "AND tip_comprob = '" + tipComprob + "'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    obs = rs.getString("observacion");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return obs;
    }
    
    private int getNroCaja(int nroComprob, int codCliente, String tipComprob){
        int result = 0;
        try{
            String sql = "SELECT cod_caja FROM venta_det_cuotas WHERE nro_comprob = " + nroComprob + " AND cod_cliente = " + codCliente + " "
                       + "AND tip_comprob = '" + tipComprob + "'";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cod_caja");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private String calculaVencimiento(String fecCobro, int plazo){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecActual = new Date();
        try{
            fecActual = sdf.parse(fecCobro);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        calendar.setTime(fecActual);
        int vence = plazo;
        calendar.add(Calendar.DATE, vence);
        result = sdf.format(calendar.getTime());
        return result;
    }
    
    private int getCondicionPlazoCreditoCliente(int codCliente){
        int result = 0;
        try{
            String sql = "SELECT cond_pago FROM cliente WHERE cod_cliente = " + codCliente;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getInt("cond_pago");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
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
        jLabel1 = new javax.swing.JLabel();
        jTFCodCuenta = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFDenominacionCta = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFTipoCuenta = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFNroDoc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFCodBco = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFNombreBco = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFFecEmision = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFFecVenc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFValor = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFCotizacion = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFValorFinal = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTFormaPago = new javax.swing.JTable();
        jBRefresh = new javax.swing.JButton();
        jBConfirmarValores = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jTFTotalDocsSeleccionados = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFTotalFormaPago = new javax.swing.JTextField();
        jLDiferencia = new javax.swing.JLabel();
        jTFDiferencia = new javax.swing.JTextField();
        jLHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Forma de Pago");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setText("CUENTA");

        jTFCodCuenta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel2.setText("DESCRIPCION");

        jTFDenominacionCta.setEditable(false);
        jTFDenominacionCta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel3.setText("TIPO");

        jTFTipoCuenta.setEditable(false);
        jTFTipoCuenta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFTipoCuenta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel4.setText("NRO. DOC");

        jTFNroDoc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroDocFocusGained(evt);
            }
        });
        jTFNroDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroDocKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel5.setText("BCO");

        jTFCodBco.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCodBco.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodBco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodBcoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodBcoFocusLost(evt);
            }
        });
        jTFCodBco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodBcoKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel6.setText("NOMBRE BANCO");

        jTFNombreBco.setEditable(false);
        jTFNombreBco.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel7.setText("FEC EMISION");

        jTFFecEmision.setEditable(false);
        jTFFecEmision.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecEmision.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel8.setText("FEC VENC");

        jTFFecVenc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFFecVenc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecVenc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecVencFocusGained(evt);
            }
        });
        jTFFecVenc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecVencKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel9.setText("VALOR");

        jTFValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFValor.setText("0");
        jTFValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFValorFocusGained(evt);
            }
        });
        jTFValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFValorKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel10.setText("COTIZACION");

        jTFCotizacion.setEditable(false);
        jTFCotizacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCotizacion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCotizacion.setText("0");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel11.setText("VALOR FINAL");

        jTFValorFinal.setEditable(false);
        jTFValorFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFValorFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFValorFinal.setText("0");

        jTFormaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cta", "Descripción", "Tipo", "Nro.Doc", "Bco.", "Nom. Bco", "Fec. Emisión", "Fec. Venc.", "Valor", "Cotización", "Valor final "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTFormaPago);
        if (jTFormaPago.getColumnModel().getColumnCount() > 0) {
            jTFormaPago.getColumnModel().getColumn(0).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(1).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(2).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(3).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(4).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(5).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(6).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(7).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(8).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(9).setResizable(false);
            jTFormaPago.getColumnModel().getColumn(10).setResizable(false);
        }

        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N
        jBRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshActionPerformed(evt);
            }
        });

        jBConfirmarValores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmarValores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarValoresActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel12.setText("TOTAL DOCS SELECCIONADOS");

        jTFTotalDocsSeleccionados.setEditable(false);
        jTFTotalDocsSeleccionados.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalDocsSeleccionados.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalDocsSeleccionados.setText("0");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel13.setText("TOTAL FORMA DE PAGO");

        jTFTotalFormaPago.setEditable(false);
        jTFTotalFormaPago.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFTotalFormaPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotalFormaPago.setText("0");

        jLDiferencia.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLDiferencia.setText("DIFERENCIA");

        jTFDiferencia.setEditable(false);
        jTFDiferencia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFDiferencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDiferencia.setText("0");

        jLHint.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLHint.setForeground(new java.awt.Color(255, 0, 0));
        jLHint.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLHint.setText("***");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLHint, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFTotalDocsSeleccionados, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(29, 29, 29)
                                .addComponent(jLDiferencia))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFTotalFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDiferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTFCodCuenta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(88, 88, 88)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(49, 49, 49)
                                .addComponent(jLabel7)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel8)
                                .addGap(46, 46, 46)
                                .addComponent(jLabel9)
                                .addGap(49, 49, 49)
                                .addComponent(jLabel10)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel11))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTFDenominacionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCodBco, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTFNombreBco, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTFFecEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBRefresh)
                    .addComponent(jBConfirmarValores))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBConfirmarValores, jBRefresh});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDenominacionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCodBco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreBco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jBRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBConfirmarValores, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLDiferencia)
                    .addComponent(jLHint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFTotalDocsSeleccionados, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFTotalFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDiferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConfirmarValores, jBRefresh});

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

    private void jTFCodCuentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCuentaFocusGained
        jTFCodCuenta.selectAll();
        jLHint.setText("Presione F1 para búsqueda de forma de pago");
        jTFValor.setText("0");
    }//GEN-LAST:event_jTFCodCuentaFocusGained

    private void jTFCodCuentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCuentaFocusLost
        jLHint.setText("");
        if(!jTFCodCuenta.getText().trim().equals("")){
            getCtaDenCotiz(jTFCodCuenta.getText().trim());
            controlFoco();
        }
    }//GEN-LAST:event_jTFCodCuentaFocusLost

    private void jTFCodCuentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCuentaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroDoc.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            DlgConsultasCuentasMonedas cuentas = new DlgConsultasCuentasMonedas(new JFrame(), true);
            cuentas.pack();
            cuentas.setText(jTFCodCuenta);
            cuentas.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodCuentaKeyPressed

    private void jTFNroDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroDocFocusGained
        jTFNroDoc.selectAll();
    }//GEN-LAST:event_jTFNroDocFocusGained

    private void jTFCodBcoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBcoFocusGained
        jTFCodBco.selectAll();
    }//GEN-LAST:event_jTFCodBcoFocusGained

    private void jTFFecVencFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecVencFocusGained
        jTFFecVenc.selectAll();
    }//GEN-LAST:event_jTFFecVencFocusGained

    private void jTFValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFValorFocusGained
        jTFValor.selectAll();
    }//GEN-LAST:event_jTFValorFocusGained

    private void jTFNroDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroDocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodBco.grabFocus();
        }
    }//GEN-LAST:event_jTFNroDocKeyPressed

    private void jTFCodBcoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodBcoKeyPressed
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecVenc.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("ATOMSystems|Main - Consulta de Bancos");
                grupo.dConsultas("banco", "nombre", "cod_banco", "nombre", "activo", null, "Codigo", "Nombre", "Activo", null);
                grupo.setText(jTFCodBco);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodBcoKeyPressed

    private void jTFFecVencKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecVencKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFValor.grabFocus();
        }
    }//GEN-LAST:event_jTFFecVencKeyPressed

    private void jTFCodBcoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBcoFocusLost
        jTFNombreBco.setText(getNombreBanco(jTFCodBco.getText().trim()));
    }//GEN-LAST:event_jTFCodBcoFocusLost

    private void jTFValorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFValorKeyPressed
        double cotizacion = Double.parseDouble(jTFCotizacion.getText().trim().replace(",",""));
        double valor_ingresado = Double.parseDouble(jTFValor.getText().trim().replace(",", ""));
        double valor_final = cotizacion * valor_ingresado;
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(valor_ingresado < 0 || valor_ingresado == 0 || jTFValor.getText().trim().equals("")){
                jTFCodCuenta.requestFocus();
            }
            else{
                jTFValorFinal.setText(decimalFormat.format(valor_final));
                addFormaPagoDetalle();
                totalizar();
                jTFValor.setText("0");
                jTFCodCuenta.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFValorKeyPressed

    private void jBRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshActionPerformed
        limpiarTabla();
        totalizar();
    }//GEN-LAST:event_jBRefreshActionPerformed

    private void jBConfirmarValoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarValoresActionPerformed
        int grabar = JOptionPane.showConfirmDialog(this, "Seguro de grabar cobro?", "Confirmar registro", JOptionPane.YES_NO_OPTION);
        if(grabar == 0){
            if(grabarDatos()){
               /* this.setVisible(false);
                this.dispose();
                RegistroCobroClientes.jBMsg.doClick();
                RegistroCobroClientes.jBImprimir.doClick();
                RegistroCobroClientes.jTDetalleDocs.clearSelection();*/
            }
        }
    }//GEN-LAST:event_jBConfirmarValoresActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroCobroClientesFormaPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientesFormaPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientesFormaPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroCobroClientesFormaPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroCobroClientesFormaPago dialog = new RegistroCobroClientesFormaPago(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBConfirmarValores;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JLabel jLDiferencia;
    private javax.swing.JLabel jLHint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFCodBco;
    private javax.swing.JTextField jTFCodCuenta;
    private javax.swing.JTextField jTFCotizacion;
    private javax.swing.JTextField jTFDenominacionCta;
    private javax.swing.JTextField jTFDiferencia;
    private javax.swing.JTextField jTFFecEmision;
    private javax.swing.JTextField jTFFecVenc;
    private javax.swing.JTextField jTFNombreBco;
    private javax.swing.JTextField jTFNroDoc;
    private javax.swing.JTextField jTFTipoCuenta;
    private javax.swing.JTextField jTFTotalDocsSeleccionados;
    private javax.swing.JTextField jTFTotalFormaPago;
    private javax.swing.JTextField jTFValor;
    private javax.swing.JTextField jTFValorFinal;
    private javax.swing.JTable jTFormaPago;
    // End of variables declaration//GEN-END:variables
}
