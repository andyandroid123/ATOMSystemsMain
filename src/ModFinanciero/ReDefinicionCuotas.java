/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModFinanciero;

import TableUtiles.DateCellRender;
import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import principal.FormMain;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableDocsPendientesRedefinicionCuotas;
import views.busca.BuscaCliente;

/**
 *
 * @author Administrador
 */
public class ReDefinicionCuotas extends javax.swing.JDialog {

    private TableRowSorter sorter;
    boolean hayFilas = false;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    private static String fecVigencia = "";
    private static DefaultTableModel dtmRedefinicion; 
    
    String codCaja = "", tipoComprob = "";
    
    public ReDefinicionCuotas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configTablaRedefinicion();
        configCampos();
        getFecVigencia();
        jTFVencimiento.setText(fecVigencia);
    }

    private void getDatosCliente(String codigo){        
        try{
            String sql = "SELECT cliente.razon_soc, cuenta.cod_cuenta "
                       + "FROM cliente "
                       + "INNER JOIN cuenta "
                       + "ON cliente.cod_cliente = cuenta.cod_cliente "
                       + "WHERE cliente.cod_cliente = " + codigo; 
            System.out.println("SQL DATOS CLIENTE: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreCliente.setText(rs.getString("razon_soc"));
                    jTFCodCta.setText(rs.getString("cod_cuenta"));
                }else{
                    jLNombreCliente.setText("INEXISTENTE");
                    jTFCodCta.setText("0");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private int getDiasVencimientoCliente(String codCliente){
        int dias_vencimiento = 0;
        try{
            String sql = "SELECT cond_pago FROM cliente WHERE cod_cliente = " + codCliente;
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    dias_vencimiento = rs.getInt("cond_pago");
                }else{
                    dias_vencimiento = 0;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return dias_vencimiento;
    }
    
    private void llenarTabla(){
        String codCliente = jTFCodCliente.getText().trim();
        ResultSet updateTabla = rSDetallesDocs(codCliente);
        if(updateTabla != null){
            jTDocsPendientes.setModel(new TableDocsPendientesRedefinicionCuotas(updateTabla));
            configTablaDocsPendientes();
            sorter = new TableRowSorter(jTDocsPendientes.getModel());
            jTDocsPendientes.setRowSorter(sorter);
            checkTable();
        }
    }
    
    private ResultSet rSDetallesDocs(String codCliente){
        ResultSet rs = null;
        String sql = "SELECT DISTINCT nro_comprob, nro_cuota, can_cuota, TO_CHAR(fec_comprob, 'dd/MM/yyyy') AS fec_emision, "
                   + "TO_CHAR(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, monto_cuota, cod_caja, tip_comprob "
                   + "FROM venta_det_cuotas "
                   + "WHERE cod_cliente = " + codCliente + " AND nro_pago = 0 AND estado = 'V' AND tip_comprob = 'FAI' "
                   + "ORDER BY nro_comprob";
        System.out.println("DETALLES DE DOCS PENDIENTES - MOD FEC VENCIMIENTO: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private void checkTable(){
        if(jTDocsPendientes.getRowCount() == 0){
            hayFilas = false;
            JOptionPane.showMessageDialog(this,"No se han encontrado registros!","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            jTFCodCliente.requestFocus();
        }else{
            hayFilas = true;
        }
    }
    
    private void configTablaDocsPendientes(){
        jTDocsPendientes.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTDocsPendientes.getColumnModel().getColumn(0).setPreferredWidth(40); // nro documento
        jTDocsPendientes.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDocsPendientes.getColumnModel().getColumn(1).setPreferredWidth(20); // nro cuota 
        jTDocsPendientes.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTDocsPendientes.getColumnModel().getColumn(2).setPreferredWidth(20); // cant cuota
        jTDocsPendientes.getColumnModel().getColumn(2).setCellRenderer(new NumberCellRender());
        jTDocsPendientes.getColumnModel().getColumn(3).setPreferredWidth(20); // cod caja
        jTDocsPendientes.getColumnModel().getColumn(3).setCellRenderer(new NumberCellRender());
        jTDocsPendientes.getColumnModel().getColumn(4).setPreferredWidth(20); // tipo comprobante
        jTDocsPendientes.getColumnModel().getColumn(4).setCellRenderer(new StringCellRender());
        jTDocsPendientes.getColumnModel().getColumn(5).setPreferredWidth(40); // fec emision
        jTDocsPendientes.getColumnModel().getColumn(5).setCellRenderer(new DateCellRender());
        jTDocsPendientes.getColumnModel().getColumn(6).setPreferredWidth(40); // fec vencimiento 
        jTDocsPendientes.getColumnModel().getColumn(6).setCellRenderer(new DateCellRender());
        //jTDetalleDocs.getColumnModel().getColumn(4).setCellRenderer(new StringCellRenderModVencimiento());
        jTDocsPendientes.getColumnModel().getColumn(7).setPreferredWidth(60); // valor cuota
        jTDocsPendientes.getColumnModel().getColumn(7).setCellRenderer(new DecimalCellRender(0));
        
        utiles.Utiles.punteroTablaF(jTDocsPendientes, this);        
        jTDocsPendientes.setFont(new Font("Tahoma", 1, 11) );
        jTDocsPendientes.setRowHeight(18);
    }
    
    private void configTablaRedefinicion(){
        dtmRedefinicion = (DefaultTableModel)jTRedefinicion.getModel();
        
        jTRedefinicion.getColumnModel().getColumn(0).setPreferredWidth(40); // nro cuota
        jTRedefinicion.getColumnModel().getColumn(1).setPreferredWidth(20); // cant cuota 
        jTRedefinicion.getColumnModel().getColumn(2).setPreferredWidth(20); // fec vencimiento
        jTRedefinicion.getColumnModel().getColumn(3).setPreferredWidth(20); // monto
        
        utiles.Utiles.punteroTablaF(jTRedefinicion, this);        
        jTRedefinicion.setFont(new Font("Tahoma", 1, 10) );
        jTRedefinicion.setRowHeight(18);
    }
    
    private void configCampos(){
        jTFCantCuotas.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFVencimiento.setInputVerifier(new FechaInputVerifier(jTFVencimiento));
        jTFCodCliente.addFocusListener(new Focus());
        jTFCantCuotas.addFocusListener(new Focus());
        jTFMontoCuota.addFocusListener(new Focus());
        jTFVencimiento.addFocusListener(new Focus());
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
    
    private void addRedefinicion(){
        /* COLUMNS
         * no cuota (int)
         * cant cuota (int)
         * fec vencimiento (string)
         * monto (double)
         */
        int dias_vencimiento = getDiasVencimientoCliente(jTFCodCliente.getText().trim());
        int cantidad_cuota = Integer.parseInt(jTFCantCuotas.getText().trim());
        double monto = Double.parseDouble(jTFMontoTotal.getText().trim().replace(",", ""));
        double monto_primera_cuota = Double.parseDouble(jTFMontoCuota.getText().trim().replace(",", ""));
        double monto_restante = monto - monto_primera_cuota;
        double monto_cuotas = Math.round(monto_restante / (cantidad_cuota - 1));
        String fechaVencimiento = jTFVencimiento.getText().trim();        
        String fecha_vencimiento2 = "";
        
        String fecha = "01/01/2000";
        for(int i = 0; i < cantidad_cuota; i++){
            int nro_cuota = i+ 1;                         
            
            fecha_vencimiento2 = calculaVencimiento(dias_vencimiento, fecha); 

            if(nro_cuota == 1){
                fecha = fechaVencimiento;
                dtmRedefinicion.addRow(new Object[]{nro_cuota, cantidad_cuota, fecha, monto_primera_cuota});
            }else{
                dtmRedefinicion.addRow(new Object[]{nro_cuota, cantidad_cuota, fecha_vencimiento2, monto_cuotas});
                fecha = fecha_vencimiento2;
            }
        }
        
    }
    
    private void limpiarTablaRedefinicion(){
        for(int i = 0; i < jTRedefinicion.getRowCount(); i++){
            dtmRedefinicion.removeRow(i);
            i--;
        }
    }
    
    private String calculaVencimiento(int condPago, String fecha){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecActual = new Date();
        try{
            fecActual = sdf.parse(fecha);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        calendar.setTime(fecActual);
        int vence = condPago;
        calendar.add(Calendar.MONTH, 1); // mantiene siempre la misma fecha y le suma + 1 al mes 
        result = sdf.format(calendar.getTime());
        return result;
    }
    
    private void confirmarDatos(){
        double montoTotal = Double.parseDouble(jTFMontoTotal.getText().trim().replace(",", ""));
            double montoTable = 0;
            
            for(int i = 0; i < jTRedefinicion.getRowCount(); i++){
                montoTable += Double.parseDouble(jTRedefinicion.getValueAt(i, 3).toString());
            }
            
            System.out.println("MONTO TOTAL: " + montoTotal + "\nMONTO TABLE: " + montoTable);
            if(montoTotal == montoTable){
                jBGuardar.setEnabled(true);
                jBGuardar.grabFocus();
            }else{
                jBGuardar.setEnabled(false);
            } 
    }
    
    private boolean grabarCuotas(){
        boolean estadoGrabado = false;
        
        boolean problemLeyendoDatos = leerTablaDetallesCuotas();
        if(!problemLeyendoDatos){
            estadoGrabado = true;
        }else{
            estadoGrabado = true;
        }
        return estadoGrabado;
    }
    
    private boolean leerTablaDetallesCuotas(){
        
        boolean resultOperacion = false;
        boolean resultUpdateVentaDetCuotas = false;
        boolean resultOperacionInsertCuotas = false;
        boolean problemFound = false;
        
        String vCodEmpresa = utiles.Utiles.getCodEmpresaDefault();
        String vCodLocal = utiles.Utiles.getCodLocalDefault(vCodEmpresa);
        String vCodSector = utiles.Utiles.getCodSectorDefault(vCodLocal);
        String vTipoComprob = "DEB";
        int vCodCaja = 99; // nro de caja para las operaciones administrativas
        String vCodCliente = jTFCodCliente.getText().trim();
        int nroComprob = Integer.parseInt(jTFNroDoc.getText().trim());
        int cantidad_cuotas = Integer.parseInt(jTFCantCuotas.getText().trim());
        String vObs = "REDEFINICION DE CUOTAS - CUOTA NRO ";
        
        resultOperacion = updateVentaDetCuotas(vCodCliente, codCaja, nroComprob, tipoComprob);
        
        if(!resultOperacion){
            resultUpdateVentaDetCuotas = true;
        }
        
        for(int i = 0; i < jTRedefinicion.getRowCount(); i++){
            
            String fec_vencimiento = jTRedefinicion.getValueAt(i, 2).toString();
            double monto_cuota = Double.parseDouble(jTRedefinicion.getValueAt(i, 3).toString());
            int nro_cuota = Integer.parseInt(jTRedefinicion.getValueAt(i, 0).toString()); 
            vObs = vObs + " " + nro_cuota;
            
            resultOperacion = insertNuevasCuotas(vCodEmpresa, vCodLocal, vCodSector, vCodCaja, vTipoComprob, nroComprob, vCodCliente, fec_vencimiento, 
                                                 monto_cuota, nro_cuota, cantidad_cuotas, vObs);
            
            if(!resultOperacion){
                resultOperacionInsertCuotas = true;
                break;
            }
        }
        
        if(resultUpdateVentaDetCuotas || resultOperacionInsertCuotas){
            problemFound = true;
            if(!rollBacktDatos()){
                problemFound = true;
            }
        }else{
            if(!commitDatos()){
                problemFound = true;
            }
        }
        return problemFound;
    }
    
    private boolean insertNuevasCuotas(String codEmpresa, String codLocal, String codSector, int codCaja, String tipoComprob, int nroTicket, 
                                       String codCliente, String fecVencimiento, double montoCuota, int nroCuota, int cantCuotas, String obs){
        String sql = "INSERT INTO venta_det_cuotas (cod_empresa, cod_local, cod_sector, cod_caja, tip_comprob, nro_ticket, nro_comprob, fec_comprob, "
                   + "cod_cliente, nro_cuota, can_cuota, fec_vencimiento, monto_cuota, monto_retenido, cod_moneda, tip_cambio, nro_recibo, fec_recibo, "
                   + "estado, cod_usuario, fec_vigencia, nro_pago, observacion, nro_turno, es_venta, nro_timbrado, ven_timbrado, nro_factura, nro_timbrado_factura) "
                   + "VALUES (" + codEmpresa + ", "
                   + codLocal + ", "
                   + codSector + ", "
                   + codCaja + ", '"
                   + tipoComprob + "', "
                   + nroTicket + ", "
                   + nroTicket + ", current_timestamp, "
                   + codCliente + ", "
                   + nroCuota + ", "
                   + cantCuotas + ", "
                   + "TO_DATE('" + fecVencimiento + "', 'dd/MM/yyyy'), "
                   + montoCuota + ", "
                   + "0, " // monto retenido
                   + "1, " // codigo moneda
                   + "1, " // tipo de cambio
                   + "0, " // nro recibo
                   + "null, " // fec_recibo
                   + "'V', " // estado 
                   + FormMain.codUsuario + ", "
                   + "current_timestamp, " // fec_vigencia
                   + "0, '" // nro pago
                   + obs + "', "
                   + "0, " // nro turno 
                   + "'N', " // es venta
                   + "0, " // nro timbrado 
                   + "current_timestamp, " // vencimiento timbrado 
                   + "0, 0)" ; // nro de factura, nro_timbrado_factura
        System.out.println("INSERT NUEVAS CUOTAS: " + sql);
        return (grabarPrevioCommit(sql));
    }
    
    private boolean updateVentaDetCuotas(String codCliente, String nroCaja, int nroComprob, String tipoComprob){
        String sql = "UPDATE venta_det_cuotas SET estado = 'A' WHERE cod_cliente = " + codCliente + " AND cod_caja = " + nroCaja + " "
                   + "AND nro_comprob = " + nroComprob + " AND tip_comprob = '" + tipoComprob + "'";
        System.out.println("UPDATE VTA-DET-CUOTAS DEL DOC MODIFICADO: " + sql);
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
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodCta = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDocsPendientes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTFNroDoc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFMontoTotal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jTFCantCuotas = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFMontoCuota = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFVencimiento = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTRedefinicion = new javax.swing.JTable();
        jBRefresh = new javax.swing.JButton();
        jBConfirmar = new javax.swing.JButton();
        jBGuardar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Re definición de cuotas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("RE DEFINICIÓN DE CUOTAS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Cliente:");

        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCliente.setText("0");
        jTFCodCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodClienteFocusGained(evt);
            }
        });
        jTFCodCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodClienteKeyPressed(evt);
            }
        });

        jLNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreCliente.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Cod. Cta.:");

        jTFCodCta.setEditable(false);
        jTFCodCta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCta.setText("0");

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jTDocsPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTDocsPendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTDocsPendientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTDocsPendientes);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del doc a modificar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel4.setText("No. Doc:");

        jTFNroDoc.setEditable(false);
        jTFNroDoc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFNroDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroDoc.setText("0");

        jLabel5.setText("Monto:");

        jTFMontoTotal.setEditable(false);
        jTFMontoTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFMontoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoTotal.setText("0,00");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Modificar:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Cant. Cuotas:");

        jTFCantCuotas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFCantCuotas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCantCuotas.setText("0");
        jTFCantCuotas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCantCuotasFocusGained(evt);
            }
        });
        jTFCantCuotas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCantCuotasKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Monto 1ra cuota:");

        jTFMontoCuota.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFMontoCuota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoCuota.setText("0,00");
        jTFMontoCuota.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoCuotaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFMontoCuotaFocusLost(evt);
            }
        });
        jTFMontoCuota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoCuotaKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Fec. 1er venc.:");

        jTFVencimiento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTFVencimiento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFVencimiento.setText("dd/MM/yyyy");
        jTFVencimiento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFVencimientoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFVencimientoFocusLost(evt);
            }
        });
        jTFVencimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFVencimientoKeyPressed(evt);
            }
        });

        jTRedefinicion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Cuota", "Cant. Cuota", "Fec. Venc.", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTRedefinicion);
        if (jTRedefinicion.getColumnModel().getColumnCount() > 0) {
            jTRedefinicion.getColumnModel().getColumn(0).setResizable(false);
            jTRedefinicion.getColumnModel().getColumn(1).setResizable(false);
            jTRedefinicion.getColumnModel().getColumn(2).setResizable(false);
            jTRedefinicion.getColumnModel().getColumn(3).setResizable(false);
        }

        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N
        jBRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshActionPerformed(evt);
            }
        });

        jBConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel7)
                                .addComponent(jLabel9))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTFCantCuotas)
                                .addComponent(jTFMontoCuota)
                                .addComponent(jTFVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBConfirmar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBConfirmar, jBRefresh});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTFNroDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jTFMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTFCantCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTFMontoCuota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTFVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jBRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBConfirmar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBConfirmar, jBRefresh});

        jBGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGuardar.setText("Guardar cambios");
        jBGuardar.setEnabled(false);
        jBGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGuardarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar y Salir");
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)))
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
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodCta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBGuardar)
                    .addComponent(jBCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodClienteFocusGained
        jTFCodCliente.selectAll();
    }//GEN-LAST:event_jTFCodClienteFocusGained

    private void jTFCodClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFCodCliente.getText().trim().equals("0")){
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else if(jTFCodCliente.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else{
                getDatosCliente(jTFCodCliente.getText().trim()); 
                jBBuscar.grabFocus();
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

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        llenarTabla();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jTDocsPendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTDocsPendientesMouseClicked
        double montoTotal = Double.parseDouble(jTDocsPendientes.getValueAt(jTDocsPendientes.getSelectedRow(), 7).toString());
        codCaja = jTDocsPendientes.getValueAt(jTDocsPendientes.getSelectedRow(), 3).toString();
        tipoComprob = jTDocsPendientes.getValueAt(jTDocsPendientes.getSelectedRow(), 4).toString();
        limpiarTablaRedefinicion();
        jTFNroDoc.setText(jTDocsPendientes.getValueAt(jTDocsPendientes.getSelectedRow(), 0).toString());
        jTFMontoTotal.setText(decimalFormat.format(montoTotal));
        jTFCantCuotas.setText("1");
        jTFMontoCuota.setText(jTFMontoTotal.getText().trim());
        jTFCantCuotas.grabFocus();
    }//GEN-LAST:event_jTDocsPendientesMouseClicked

    private void jTFCantCuotasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCantCuotasFocusGained
        jTFCantCuotas.selectAll();
    }//GEN-LAST:event_jTFCantCuotasFocusGained

    private void jTFMontoCuotaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoCuotaFocusGained
        jTFMontoCuota.setText(jTFMontoCuota.getText().trim().replace(",", ""));
        jTFMontoCuota.selectAll();
    }//GEN-LAST:event_jTFMontoCuotaFocusGained

    private void jTFVencimientoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFVencimientoFocusGained
        jTFVencimiento.selectAll();
    }//GEN-LAST:event_jTFVencimientoFocusGained

    private void jTFCantCuotasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCantCuotasKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFMontoCuota.grabFocus();
        }
    }//GEN-LAST:event_jTFCantCuotasKeyPressed

    private void jTFMontoCuotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoCuotaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFVencimiento.grabFocus();
        }
    }//GEN-LAST:event_jTFMontoCuotaKeyPressed

    private void jTFVencimientoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFVencimientoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){            
            jBConfirmar.grabFocus();
        }
    }//GEN-LAST:event_jTFVencimientoKeyPressed

    private void jTFMontoCuotaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoCuotaFocusLost
        double monto = Double.parseDouble(jTFMontoCuota.getText().trim());
        jTFMontoCuota.setText(decimalFormat.format(monto));
    }//GEN-LAST:event_jTFMontoCuotaFocusLost

    private void jBRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshActionPerformed
        limpiarTablaRedefinicion();
        jBGuardar.setEnabled(false);
        jTFCantCuotas.grabFocus();
    }//GEN-LAST:event_jBRefreshActionPerformed

    private void jTFVencimientoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFVencimientoFocusLost
        limpiarTablaRedefinicion();
        addRedefinicion();
    }//GEN-LAST:event_jTFVencimientoFocusLost

    private void jBConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarActionPerformed
        confirmarDatos();
    }//GEN-LAST:event_jBConfirmarActionPerformed

    private void jBGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGuardarActionPerformed
        if(grabarCuotas()){
            JOptionPane.showMessageDialog(this, "Cuotas grabadas!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarTablaRedefinicion();
            jBGuardar.setEnabled(false);
            jTFNroDoc.setText("0");
            jTFMontoTotal.setText("0,00");
            jTFCantCuotas.setText("");
            jTFMontoCuota.setText("0,00");
            jTFVencimiento.setText(fecVigencia);
            jBBuscar.doClick();
            jTFCodCliente.grabFocus();
        }else{
            JOptionPane.showMessageDialog(this, "Error grabando cuotas", "Éxito", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jBGuardarActionPerformed

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
            java.util.logging.Logger.getLogger(ReDefinicionCuotas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReDefinicionCuotas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReDefinicionCuotas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReDefinicionCuotas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReDefinicionCuotas dialog = new ReDefinicionCuotas(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBBuscar;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBConfirmar;
    private javax.swing.JButton jBGuardar;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTDocsPendientes;
    private javax.swing.JTextField jTFCantCuotas;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCta;
    private javax.swing.JTextField jTFMontoCuota;
    private javax.swing.JTextField jTFMontoTotal;
    private javax.swing.JTextField jTFNroDoc;
    private javax.swing.JTextField jTFVencimiento;
    private javax.swing.JTable jTRedefinicion;
    // End of variables declaration//GEN-END:variables
}
