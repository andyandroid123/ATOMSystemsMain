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
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.ButtonDetallesCobroClientes;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.TableCobranzasClientes;
import views.busca.BuscaCliente;

/**
 *
 * @author Andres
 */
public class InfCobranzasClientes extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector;
    DefaultTableModel dtm = new DefaultTableModel(null, new String[]{"Caja", "Turno", "Nro.Pago", "Nro.Recibo", "Fec. Cobro", "Cliente", "Monto pago", 
                                                                     "Interés", "Estado", "Cobrador", "Obs", "Detalles"});
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    
    TableCobranzasClientes tableCobranzas;
    private TableRowSorter sorter;
    boolean hayFilas = false;
    
    public InfCobranzasClientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        codLocal = jCBCodLocal.getSelectedItem().toString();
        configCampos();
        llenarCampos();
        //configTabla();
        jTFCodCliente.grabFocus();
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
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
    }
    
    private void configCampos(){
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        jTFCodCliente.setDocument(new MaxLength(6, "", "ENTERO"));
        
        jTFFecDesde.addFocusListener(new Focus()); 
        jTFFecHasta.addFocusListener(new Focus()); 
        jTFCodCliente.addFocusListener(new Focus());
    }
    
    private void llenarCampos(){
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
        jLNombreEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLNombreLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jTFCodCliente.setText("0");
    }
    
    private void configTabla(){
        jTDetallesCobro.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTDetallesCobro.getColumnModel().getColumn(0).setPreferredWidth(20);    // Caja
        jTDetallesCobro.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetallesCobro.getColumnModel().getColumn(1).setPreferredWidth(20);    // Turno
        jTDetallesCobro.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRender());
        jTDetallesCobro.getColumnModel().getColumn(2).setPreferredWidth(20);    // Nro pago 
        jTDetallesCobro.getColumnModel().getColumn(2).setCellRenderer(new NumberCellRender());
        
        jTDetallesCobro.getColumnModel().getColumn(3).setPreferredWidth(20);    // Nro Recibo 
        jTDetallesCobro.getColumnModel().getColumn(3).setCellRenderer(new StringCellRender());
        jTDetallesCobro.getColumnModel().getColumn(4).setPreferredWidth(60);    // Fecha cobro
        jTDetallesCobro.getColumnModel().getColumn(4).setCellRenderer(new DateCellRender());
        jTDetallesCobro.getColumnModel().getColumn(5).setPreferredWidth(100);   // Cliente
        jTDetallesCobro.getColumnModel().getColumn(5).setCellRenderer(new StringCellRender());
        jTDetallesCobro.getColumnModel().getColumn(6).setPreferredWidth(60);    // Monto pago 
        jTDetallesCobro.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(0));
        jTDetallesCobro.getColumnModel().getColumn(7).setPreferredWidth(60);    // Monto interes
        jTDetallesCobro.getColumnModel().getColumn(7).setCellRenderer(new DecimalCellRender(0));
        jTDetallesCobro.getColumnModel().getColumn(8).setPreferredWidth(20);    // Estado
        jTDetallesCobro.getColumnModel().getColumn(8).setCellRenderer(new StringCellRender());
        jTDetallesCobro.getColumnModel().getColumn(9).setPreferredWidth(100);   // Cobrador
        jTDetallesCobro.getColumnModel().getColumn(9).setCellRenderer(new StringCellRender());
        jTDetallesCobro.getColumnModel().getColumn(10).setPreferredWidth(80);   // Observacion 
        jTDetallesCobro.getColumnModel().getColumn(10).setCellRenderer(new StringCellRender());
        //jTDetallesCobro.getColumnModel().getColumn(11).setPreferredWidth(60);   // Detalles
        
        // implementacion del button
        ButtonDetallesCobroClientes bDCobro = new ButtonDetallesCobroClientes(jTDetallesCobro, 11);
        jTDetallesCobro.getColumnModel().getColumn(11).setPreferredWidth(60);   // Detalles
    }
    
    private void llenarTabla(){
        String cod_cliente = jTFCodCliente.getText().trim();
        String fecDesde = jTFFecDesde.getText().trim();
        String fecHasta = jTFFecHasta.getText().trim();
        ResultSet updateTabla = rSCobros(cod_cliente, fecDesde, fecHasta);
        if(updateTabla != null){
            jTDetallesCobro.setModel(new TableCobranzasClientes(updateTabla));
            configTabla();
            jTDetallesCobro.setFont(new Font("Tahoma", 1, 11));
            jTDetallesCobro.setRowHeight(22);
            sorter = new TableRowSorter(jTDetallesCobro.getModel());
            jTDetallesCobro.setRowSorter(sorter);
            checkTable();
        }
    }
    
    private void checkTable(){
        if(jTDetallesCobro.getRowCount() == 0){
            hayFilas = false;
            JOptionPane.showMessageDialog(this,"No se han encontrado registros!","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            jTFCodCliente.requestFocus();
        }else{
            hayFilas = true;
        }
    }
    
    private void getDatosCliente(String codigo){
        ClienteCtrl clieCtrl = new ClienteCtrl();
        String nombre = clieCtrl.getNombreCliente(Integer.parseInt(codigo));
        jLNombreCliente.setText(nombre);
    }
    
    private ResultSet rSCobros(String cod_cliente, String fecDesde, String fecHasta){
        ResultSet rs = null;
        String sql = "";
        String estado = "";
        
        if(jRBAnulados.isSelected()){
            estado = "AND cab.estado = 'A'";
        }
        if(jRBVigentes.isSelected()){
            estado = "AND cab.estado = 'V'";
        }
        
        if(jTFCodCliente.getText().trim().equals("0")){
            String sqlTodos = 
                        "SELECT cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, TO_CHAR(cab.fec_pago, 'dd/MM/yyyy') AS fec_pago, "
                       + "cli.razon_soc, (cab.monto_pago + cab.monto_vuelto) as monto_cobro, SUM(det.vlr_interes) AS interes, cab.estado, "
                       + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cobrador) AS cobrador, cab.observacion "
                       + "FROM pagocli_cab cab "
                       + "LEFT OUTER JOIN pagocli_det det "
                       + "ON cab.nro_pago = det.nro_pago "
                       + "LEFT OUTER JOIN cliente cli "
                       + "ON cab.cod_cliente = cli.cod_cliente "
                       + "WHERE cab.fec_vigencia::date >= '" + fecDesde + "'::date AND cab.fec_vigencia::date <= '" + fecHasta + "'::date "
                       + estado + " "
                       + "GROUP BY cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, cab.fec_pago, cli.razon_soc, cab.monto_pago, "
                       + "cab.estado, cab.cod_cobrador, cab.observacion, cab.monto_vuelto "
                       + "ORDER BY cab.fec_pago, cab.nro_pago";
            sql = sqlTodos;
        }else{
            String sqlCodCliente = 
                         "SELECT cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, TO_CHAR(cab.fec_pago, 'dd/MM/yyyy') AS fec_pago, "
                       + "cli.razon_soc, (cab.monto_pago + cab.monto_vuelto) as monto_cobro, SUM(det.vlr_interes) AS interes, cab.estado, "
                       + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cobrador) AS cobrador, cab.observacion "
                       + "FROM pagocli_cab cab "
                       + "LEFT OUTER JOIN pagocli_det det "
                       + "ON cab.nro_pago = det.nro_pago "
                       + "LEFT OUTER JOIN cliente cli "
                       + "ON cab.cod_cliente = cli.cod_cliente "
                       + "WHERE cab.cod_cliente = " + cod_cliente + " AND cab.fec_pago::date >= '" + fecDesde + "'::date AND cab.fec_vigencia::date <= '" + fecHasta + "'::date "
                       + estado + " "
                       + "GROUP BY cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, cab.fec_pago, cli.razon_soc, cab.monto_pago, "
                       + "cab.estado, cab.cod_cobrador, cab.observacion, cab.monto_vuelto "
                       + "ORDER BY cab.fec_pago, cab.nro_pago";
            sql = sqlCodCliente;
        }
        System.out.println("COBRO DE CLIENTES: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private void totalizar(){
        double total = 0;        
        if(jTDetallesCobro.getRowCount() > 0){
            for(int i = 0; i < jTDetallesCobro.getRowCount(); i++){
                total += Double.parseDouble(jTDetallesCobro.getValueAt(i, 6).toString());
            }
        }
        jTFTotales.setText(decimalFormat.format(total));
    }
    
    private void imprimirResumen(){
        String codCliente = jTFCodCliente.getText().trim();
        String empresa = jLNombreEmpresa.getText().trim();
        String local = jLNombreLocal.getText().trim();
        String fecDesde = jTFFecDesde.getText().trim();
        String fecHasta = jTFFecHasta.getText().trim();
        String filtro = "", sql = "", estado = "";
        
        if(jRBAnulados.isSelected()){
            filtro = "(Anulados)";
            estado = "('A')";
        }
        
        if(jRBTodos.isSelected()){
            filtro = "(Todos)";
            estado = "('V', 'A')";
        }
        
        if(jRBVigentes.isSelected()){
            filtro = "(Vigentes)";
            estado = "('V')";
        }
        
        if(jTFCodCliente.getText().trim().equals("0")){
            String sqlTodos = 
                  "SELECT cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, TO_CHAR(cab.fec_pago, 'dd/MM/yyyy') AS fec_pago, cli.razon_soc, "
                + "(cab.monto_pago + cab.monto_vuelto) as monto_cobro, SUM(det.vlr_interes) AS interes, cab.estado, "
                + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cobrador) AS cobrador, cab.observacion "
                + "FROM pagocli_cab cab "
                + "LEFT OUTER JOIN pagocli_det det "
                + "ON cab.nro_pago = det.nro_pago "
                + "LEFT OUTER JOIN cliente cli "
                + "ON cab.cod_cliente = cli.cod_cliente "
                + "WHERE cab.fec_vigencia::date >= '" + fecDesde + "'::date AND cab.fec_vigencia::date <= '" + fecHasta + "'::date AND cab.estado IN " + estado + " "
                + "GROUP BY cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, cab.fec_pago, cli.razon_soc, cab.monto_pago, cab.estado, "
                + "cab.cod_cobrador, cab.observacion, cab.monto_vuelto "
                + "ORDER BY cab.fec_pago, cab.nro_pago";
            sql = sqlTodos;
        }else{
            String sqlCodCliente = 
                  "SELECT cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, TO_CHAR(cab.fec_pago, 'dd/MM/yyyy') AS fec_pago, cli.razon_soc, "
                + "(cab.monto_pago + cab.monto_vuelto) as monto_cobro, SUM(det.vlr_interes) AS interes, cab.estado, "
                + "(SELECT nombre || ' ' || apellido FROM empleado WHERE cod_empleado = cab.cod_cobrador) AS cobrador, cab.observacion "
                + "FROM pagocli_cab cab "
                + "LEFT OUTER JOIN pagocli_det det "
                + "ON cab.nro_pago = det.nro_pago "
                + "LEFT OUTER JOIN cliente cli "
                + "ON cab.cod_cliente = cli.cod_cliente "
                + "WHERE cab.fec_vigencia::date >= '" + fecDesde + "'::date AND cab.fec_vigencia::date <= '" + fecHasta + "'::date AND cab.estado IN " + estado + " "
                + "AND cab.cod_cliente = " + codCliente + " "
                + "GROUP BY cab.cod_caja, cab.nro_turno, cab.nro_pago, cab.nro_recibo, cab.fec_pago, cli.razon_soc, cab.monto_pago, cab.estado, "
                + "cab.cod_cobrador, cab.observacion, cab.monto_vuelto "
                + "ORDER BY cab.fec_pago, cab.nro_pago";
            sql = sqlCodCliente;
        }
        
        
        System.out.println("IMPRESION DE RESUMEN DE COBRO: " + sql);
        
        try{
            LibReportes.parameters.put("pTipoFiltro", filtro);
            LibReportes.parameters.put("pFecDesde", fecDesde);
            LibReportes.parameters.put("pFecHasta", fecHasta);
            LibReportes.parameters.put("pEmpresa", empresa);
            LibReportes.parameters.put("pLocal", local);
            LibReportes.parameters.put("pOperador", FormMain.codUsuario + " " + FormMain.nombreUsuario);
            LibReportes.parameters.put("pFecActual", utiles.Utiles.getSysDateTimeString());
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "informe_cobros_clientes");
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

        bGPagos = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jRBVigentes = new javax.swing.JRadioButton();
        jRBAnulados = new javax.swing.JRadioButton();
        jRBTodos = new javax.swing.JRadioButton();
        jTFFecDesde = new javax.swing.JTextField();
        jTFFecHasta = new javax.swing.JTextField();
        jBGenerar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jLNombreCliente = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLNombreLocal = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLNombreEmpresa = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesCobro = new javax.swing.JTable();
        jTFTotales = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe de Cobro de Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("INFORME DE COBROS DE CLIENTES");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pagos emitidos:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Desde:");

        jLabel6.setText("Hasta:");

        jRBVigentes.setBackground(new java.awt.Color(204, 255, 204));
        bGPagos.add(jRBVigentes);
        jRBVigentes.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jRBVigentes.setSelected(true);
        jRBVigentes.setText("Vigentes");
        jRBVigentes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBVigentesActionPerformed(evt);
            }
        });

        jRBAnulados.setBackground(new java.awt.Color(204, 255, 204));
        bGPagos.add(jRBAnulados);
        jRBAnulados.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jRBAnulados.setText("Anulados");
        jRBAnulados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBAnuladosActionPerformed(evt);
            }
        });

        jRBTodos.setBackground(new java.awt.Color(204, 255, 204));
        bGPagos.add(jRBTodos);
        jRBTodos.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jRBTodos.setText("Todos");
        jRBTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTodosActionPerformed(evt);
            }
        });

        jTFFecDesde.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecDesde.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecDesde.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecDesdeFocusGained(evt);
            }
        });
        jTFFecDesde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecDesdeKeyPressed(evt);
            }
        });

        jTFFecHasta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecHasta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFFecHasta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecHastaFocusGained(evt);
            }
        });
        jTFFecHasta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecHastaKeyPressed(evt);
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
                        .addComponent(jRBVigentes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRBAnulados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRBTodos))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBVigentes)
                    .addComponent(jRBAnulados)
                    .addComponent(jRBTodos))
                .addContainerGap())
        );

        jBGenerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBGenerar.setText("Generar");
        jBGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerarActionPerformed(evt);
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

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Empresa:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Local:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Cliente:");

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

        jLNombreLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreLocal.setText("***");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreEmpresa.setText("***");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 43, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreCliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTDetallesCobro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTDetallesCobro);

        jTFTotales.setBackground(new java.awt.Color(102, 255, 102));
        jTFTotales.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTotales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFTotales.setText("0");

        jLabel7.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel7.setText("Totales:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBGenerar, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jBImprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBSalir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jBGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBImprimir, jBSalir});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                jTFFecDesde.requestFocus();
            }else if(jTFCodCliente.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this, "Campo no puede quedar vacío!", "Atención", JOptionPane.INFORMATION_MESSAGE);
                jTFCodCliente.requestFocus();
                jLNombreCliente.setText("");
            }else{
                getDatosCliente(jTFCodCliente.getText().trim()); 
                jTFFecDesde.requestFocus();
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

    private void jTFFecDesdeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDesdeFocusGained
        jTFFecDesde.selectAll();
    }//GEN-LAST:event_jTFFecDesdeFocusGained

    private void jTFFecDesdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDesdeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecHasta.grabFocus();
        }
    }//GEN-LAST:event_jTFFecDesdeKeyPressed

    private void jTFFecHastaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHastaFocusGained
        jTFFecHasta.selectAll();
    }//GEN-LAST:event_jTFFecHastaFocusGained

    private void jTFFecHastaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecHastaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGenerar.grabFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jBGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGenerarActionPerformed
        llenarTabla();
        totalizar();
    }//GEN-LAST:event_jBGenerarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(jTDetallesCobro.getRowCount() > 0){
            imprimirResumen();
        }
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void jRBTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTodosActionPerformed
        jBImprimir.setEnabled(false);
    }//GEN-LAST:event_jRBTodosActionPerformed

    private void jRBAnuladosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBAnuladosActionPerformed
        jBImprimir.setEnabled(true);
    }//GEN-LAST:event_jRBAnuladosActionPerformed

    private void jRBVigentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBVigentesActionPerformed
        jBImprimir.setEnabled(true);
    }//GEN-LAST:event_jRBVigentesActionPerformed

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
            java.util.logging.Logger.getLogger(InfCobranzasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InfCobranzasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InfCobranzasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InfCobranzasClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InfCobranzasClientes dialog = new InfCobranzasClientes(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGPagos;
    private javax.swing.JButton jBGenerar;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JLabel jLNombreCliente;
    private javax.swing.JLabel jLNombreEmpresa;
    private javax.swing.JLabel jLNombreLocal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRBAnulados;
    private javax.swing.JRadioButton jRBTodos;
    private javax.swing.JRadioButton jRBVigentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesCobro;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    private javax.swing.JTextField jTFTotales;
    // End of variables declaration//GEN-END:variables
}
