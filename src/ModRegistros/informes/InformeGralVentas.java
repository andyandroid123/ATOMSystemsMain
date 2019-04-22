/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.informes;

import TableUtiles.DecimalCellRender;
import TableUtiles.NumberCellRender;
import TableUtiles.StringCellRender;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utiles.ButtonDetalleCuentaCliente;
import utiles.CellRenderer;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class InformeGralVentas extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector, fecDesde, fecHasta;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    double TABLA_TOTAL_VENTA = 0;
    
    StatementManager sm;
    private SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");
    DefaultTableModel dtmDetallado, dtmResumido;
    
    public InformeGralVentas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString().trim();
        codLocal = jCBCodLocal.getSelectedItem().toString().trim();
        codSector = jCBCodSector.getSelectedItem().toString().trim();
        datePickerFormat();
        llenarCampos();
        jTFCodigo.addFocusListener(new Focus()); 
    }

    private void configuraTablaVtasDetallado(){
        dtmDetallado = new DefaultTableModel(null, new String[]{"Código", "Descripción", "Cantidad", "Costo s/ IVA", "IVA Vta", "% util.", "Total Venta"}); 
        jTDetalles.setModel(dtmDetallado);       
        jTDetalles.setDefaultRenderer(Object.class, new CellRenderer()); 
        
        jTDetalles.getColumnModel().getColumn(0).setPreferredWidth(30); //Código
        jTDetalles.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(1).setPreferredWidth(200); //Descripción
        jTDetalles.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTDetalles.getColumnModel().getColumn(2).setPreferredWidth(80); //Cantidad
        jTDetalles.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(2));
        jTDetalles.getColumnModel().getColumn(3).setPreferredWidth(80); //Costo s/ IVA
        jTDetalles.getColumnModel().getColumn(3).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(4).setPreferredWidth(80); //IVA Vta
        jTDetalles.getColumnModel().getColumn(4).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(5).setPreferredWidth(15); //% util.
        jTDetalles.getColumnModel().getColumn(5).setCellRenderer(new DecimalCellRender(2));
        jTDetalles.getColumnModel().getColumn(6).setPreferredWidth(80); //Total Venta
        jTDetalles.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(0));
        
        // config tabla
        utiles.Utiles.punteroTablaF(jTDetalles, this);        
        jTDetalles.setFont(new Font("Tahoma", 1, 12) );
        jTDetalles.setRowHeight(20);
    }
    
    private void configuraTablaVtasResumido(){
        dtmResumido = new DefaultTableModel(null, new String[]{"Código", "Descripción", "Cantidad", "Costo s/ IVA", "IVA Vta", "Margen", "% util.", "Total Venta", "+ Detalle"});
        jTDetalles.setModel(dtmResumido);
        jTDetalles.setDefaultRenderer(Object.class, new CellRenderer());
        
        jTDetalles.getColumnModel().getColumn(0).setPreferredWidth(30); //Código
        jTDetalles.getColumnModel().getColumn(0).setCellRenderer(new NumberCellRender());
        jTDetalles.getColumnModel().getColumn(1).setPreferredWidth(200); //Descripción
        jTDetalles.getColumnModel().getColumn(1).setCellRenderer(new StringCellRender());
        jTDetalles.getColumnModel().getColumn(2).setPreferredWidth(80); //Cantidad
        jTDetalles.getColumnModel().getColumn(2).setCellRenderer(new DecimalCellRender(2));
        jTDetalles.getColumnModel().getColumn(3).setPreferredWidth(80); //Costo s/ IVA
        jTDetalles.getColumnModel().getColumn(3).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(4).setPreferredWidth(80); //IVA Vta
        jTDetalles.getColumnModel().getColumn(4).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(5).setPreferredWidth(80); //Margen
        jTDetalles.getColumnModel().getColumn(5).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(6).setPreferredWidth(15); //% util.
        jTDetalles.getColumnModel().getColumn(6).setCellRenderer(new DecimalCellRender(2));
        jTDetalles.getColumnModel().getColumn(7).setPreferredWidth(80); //Total Venta
        jTDetalles.getColumnModel().getColumn(7).setCellRenderer(new DecimalCellRender(0));
        jTDetalles.getColumnModel().getColumn(8).setPreferredWidth(20); //+ Detalle
        
        // implementación del botón +Detalle
        ButtonDetalleCuentaCliente button = new ButtonDetalleCuentaCliente(jTDetalles, 8); //->just example
        
        // config tabla
        utiles.Utiles.punteroTablaF(jTDetalles, this);        
        jTDetalles.setFont(new Font("Tahoma", 1, 12) );
        jTDetalles.setRowHeight(20);
    }
    
    private void llenarCampos(){
        jTFCodigo.setText("0");
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
    
    private void limpiarTablaResumido(){
        for(int i = 0; i < jTDetalles.getRowCount(); i++){
            dtmResumido.removeRow(i);
            i--;
        }
    }
    
    private void limpiarTablaDetallado(){
        for(int i = 0; i < jTDetalles.getRowCount(); i++){
            dtmDetallado.removeRow(i);
            i--;
        }
    }
    
    private void llenarTablaResumido(String fecInicio, String fecFin, String codigo, String orden, String campo1, 
                                     String campo2, String tabla, String where){
        System.out.println("CANTIDAD DE FILAS EN LA TABLA: " + jTDetalles.getRowCount());
        if(jTDetalles.getRowCount() > 0){
            limpiarTablaResumido();
        }
        configuraTablaVtasResumido();
        sm = new StatementManager();
        
        String sql = "SELECT g." + campo1 + ", g." + campo2 + ", "
                   + "SUM(h.cant_venta) AS cantidad, SUM(h.monto_costo) AS costo_sin_iva, "
                   + "SUM(h.monto_iva) AS monto_iva, SUM(h.monto_margen) AS monto_margen,"
                   + "CASE WHEN ((SUM(h.monto_margen) != 0)) THEN 0 ELSE ((SUM(h.monto_margen) * 100) / SUM(h.monto_costo)) END AS pct_utilidad, "
                   + "SUM(h.monto_total) AS total_venta "
                   + "FROM hisventa_articulo h "
                   + "INNER JOIN articulo a "
                   + "ON h.cod_articulo = a.cod_articulo "
                   + "INNER JOIN " + tabla + " g "
                   + "ON a." + campo1 + " = g." + campo1 + " "
                   + where + " "
                   + "h.fec_venta::date >= TO_DATE('" + fecInicio + "', 'dd/MM/yyyy') "
                   + "AND h.fec_venta::date <= TO_DATE('" + fecFin + "', 'dd/MM/yyyy') "
                   + "GROUP BY g." + campo1 + ", g." + campo2 + " "
                   + "ORDER BY " + orden;
        System.out.println("SQL FILL TABLA RESUMIDO: " + sql);
        
        try{
            sm.TheSql = sql;
            sm.EjecutarSql();
            if(sm.TheResultSet != null){
                while(sm.TheResultSet.next()){
                    Object [] row = new Object[8];
                    row[0] = sm.TheResultSet.getString(campo1);
                    row[1] = sm.TheResultSet.getString(campo2);
                    row[2] = sm.TheResultSet.getFloat("cantidad");
                    row[3] = sm.TheResultSet.getInt("costo_sin_iva");
                    row[4] = sm.TheResultSet.getInt("monto_iva");
                    row[5] = sm.TheResultSet.getInt("monto_margen");
                    row[6] = sm.TheResultSet.getFloat("pct_utilidad");
                    row[7] = sm.TheResultSet.getInt("total_venta");
                    TABLA_TOTAL_VENTA += sm.TheResultSet.getInt("total_venta");
                    
                    if(TABLA_TOTAL_VENTA == 0){}
                    else{dtmResumido.addRow(row);}
                    
                }
                jTDetalles.updateUI();
            }else{
                JOptionPane.showMessageDialog(this, "ATENCION: No existen datos para las opciones seleccionadas!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
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

        bGFiltro = new javax.swing.ButtonGroup();
        bGOrden = new javax.swing.ButtonGroup();
        bGTipo = new javax.swing.ButtonGroup();
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
        jRBResumido = new javax.swing.JRadioButton();
        jRBDetallado = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jLTipoFiltro = new javax.swing.JLabel();
        jTFCodigo = new javax.swing.JTextField();
        jLDescripcion = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jRBFiltroGrupo = new javax.swing.JRadioButton();
        jRBFiltroMarca = new javax.swing.JRadioButton();
        jRBFiltroCodigo = new javax.swing.JRadioButton();
        jRBFiltroProveedor = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        jRBOrdenCodigo = new javax.swing.JRadioButton();
        jRBOrdenDesc = new javax.swing.JRadioButton();
        jRBOrdenCantidad = new javax.swing.JRadioButton();
        jRBOrdenMonto = new javax.swing.JRadioButton();
        jRBOrdenMargen = new javax.swing.JRadioButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jXDPDesde = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jXDPHasta = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalles = new javax.swing.JTable();
        jBProcesar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe Gral. de Ventas");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("INFORME GENERAL DE VENTAS");

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

        jCBCodEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCodEmpresaActionPerformed(evt);
            }
        });
        jCBCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodEmpresaKeyPressed(evt);
            }
        });

        jLEmpresa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Local:");

        jCBCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodLocalKeyPressed(evt);
            }
        });

        jLLocal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Sector:");

        jCBCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodSectorKeyPressed(evt);
            }
        });

        jLSector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLSector.setText("***");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLSector))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Tipo de Informe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBResumido.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jRBResumido);
        jRBResumido.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBResumido.setSelected(true);
        jRBResumido.setText("Resumido");
        jRBResumido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBResumidoActionPerformed(evt);
            }
        });

        jRBDetallado.setBackground(new java.awt.Color(204, 255, 204));
        bGTipo.add(jRBDetallado);
        jRBDetallado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBDetallado.setText("Detallado");
        jRBDetallado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBDetalladoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBResumido)
                    .addComponent(jRBDetallado))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBResumido)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBDetallado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Información de Selección", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLTipoFiltro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLTipoFiltro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLTipoFiltro.setText("Grupo:");

        jTFCodigo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodigo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodigo.setText("0");
        jTFCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodigoFocusGained(evt);
            }
        });

        jLDescripcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescripcion.setText("***");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLTipoFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTFCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLTipoFiltro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTFCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescripcion))
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filtrar informe por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBFiltroGrupo.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFiltroGrupo);
        jRBFiltroGrupo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFiltroGrupo.setSelected(true);
        jRBFiltroGrupo.setText("Grupo");
        jRBFiltroGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBFiltroGrupoActionPerformed(evt);
            }
        });

        jRBFiltroMarca.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFiltroMarca);
        jRBFiltroMarca.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFiltroMarca.setText("Marca");
        jRBFiltroMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBFiltroMarcaActionPerformed(evt);
            }
        });

        jRBFiltroCodigo.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFiltroCodigo);
        jRBFiltroCodigo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFiltroCodigo.setText("Código");
        jRBFiltroCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBFiltroCodigoActionPerformed(evt);
            }
        });

        jRBFiltroProveedor.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFiltroProveedor);
        jRBFiltroProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFiltroProveedor.setText("Proveedor");
        jRBFiltroProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBFiltroProveedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jRBFiltroGrupo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBFiltroMarca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBFiltroCodigo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBFiltroProveedor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBFiltroGrupo)
                    .addComponent(jRBFiltroMarca)
                    .addComponent(jRBFiltroCodigo)
                    .addComponent(jRBFiltroProveedor))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(204, 255, 204));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ordenado por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBOrdenCodigo.setBackground(new java.awt.Color(204, 255, 204));
        bGOrden.add(jRBOrdenCodigo);
        jRBOrdenCodigo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBOrdenCodigo.setSelected(true);
        jRBOrdenCodigo.setText("Código");

        jRBOrdenDesc.setBackground(new java.awt.Color(204, 255, 204));
        bGOrden.add(jRBOrdenDesc);
        jRBOrdenDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBOrdenDesc.setText("Descripción");

        jRBOrdenCantidad.setBackground(new java.awt.Color(204, 255, 204));
        bGOrden.add(jRBOrdenCantidad);
        jRBOrdenCantidad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBOrdenCantidad.setText("Cantidad");

        jRBOrdenMonto.setBackground(new java.awt.Color(204, 255, 204));
        bGOrden.add(jRBOrdenMonto);
        jRBOrdenMonto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBOrdenMonto.setText("Monto");

        jRBOrdenMargen.setBackground(new java.awt.Color(204, 255, 204));
        bGOrden.add(jRBOrdenMargen);
        jRBOrdenMargen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBOrdenMargen.setText("Margen");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jRBOrdenCodigo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBOrdenDesc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBOrdenCantidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBOrdenMonto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBOrdenMargen)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBOrdenCodigo)
                    .addComponent(jRBOrdenDesc)
                    .addComponent(jRBOrdenCantidad)
                    .addComponent(jRBOrdenMonto)
                    .addComponent(jRBOrdenMargen))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(204, 255, 204));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rango de fechas:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Desde:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Hasta:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jXDPDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jXDPHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTDetalles);

        jBProcesar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBProcesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBProcesar.setText("Procesar Informe");
        jBProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBProcesarActionPerformed(evt);
            }
        });

        jBImprimir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");

        jBSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
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
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(6, 6, 6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBProcesar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBProcesar)
                    .addComponent(jBImprimir)
                    .addComponent(jBSalir))
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

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jCBCodEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCodEmpresaActionPerformed
        jLEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
    }//GEN-LAST:event_jCBCodEmpresaActionPerformed

    private void jRBFiltroGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBFiltroGrupoActionPerformed
        if(jRBFiltroGrupo.isSelected()){
            jLTipoFiltro.setText("Grupo:");
        }
    }//GEN-LAST:event_jRBFiltroGrupoActionPerformed

    private void jRBFiltroMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBFiltroMarcaActionPerformed
        if(jRBFiltroMarca.isSelected()){
            jLTipoFiltro.setText("Marca:");
        }
    }//GEN-LAST:event_jRBFiltroMarcaActionPerformed

    private void jRBFiltroCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBFiltroCodigoActionPerformed
        if(jRBFiltroCodigo.isSelected()){
            jLTipoFiltro.setText("Código:");
        }
    }//GEN-LAST:event_jRBFiltroCodigoActionPerformed

    private void jRBFiltroProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBFiltroProveedorActionPerformed
        if(jRBFiltroProveedor.isSelected()){
            jLTipoFiltro.setText("Proveedor:");
        }
    }//GEN-LAST:event_jRBFiltroProveedorActionPerformed

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
            jTFCodigo.grabFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jRBDetalladoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBDetalladoActionPerformed
        if(jRBDetallado.isSelected()){
            configuraTablaVtasDetallado();
        }
    }//GEN-LAST:event_jRBDetalladoActionPerformed

    private void jRBResumidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBResumidoActionPerformed
        if(jRBResumido.isSelected()){
            configuraTablaVtasResumido();
        }
    }//GEN-LAST:event_jRBResumidoActionPerformed

    private void jBProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBProcesarActionPerformed
        String codigo = jTFCodigo.getText().trim();
        fecDesde = sdf.format(jXDPDesde.getDate());
        fecHasta = sdf.format(jXDPHasta.getDate());
        String orden = "", tabla = "", campo1 = "", campo2 = "", where = "";;        
               
        // filtro 
        if(jRBFiltroGrupo.isSelected()){
            tabla = "grupo";
            campo1 = "cod_grupo";
            campo2 = "descripcion";
        }

        if(jRBFiltroMarca.isSelected()){
            tabla = "marca";
            campo1 = "cod_marca";
            campo2 = "descripcion";
        }

        if(jRBFiltroCodigo.isSelected()){
            tabla = "articulo";
            campo1 = "cod_articulo";
            campo2 = "descripcion";
        }

        if(jRBFiltroProveedor.isSelected()){
            tabla = "proveedor";
            campo1 = "cod_proveedor";
            campo2 = "razon_soc";
        }

        // orden 
        if(jRBOrdenCodigo.isSelected()){
            orden = "g." + campo1;
        }

        if(jRBOrdenDesc.isSelected()){
            orden = "g." + campo1;
        }

        if(jRBOrdenCantidad.isSelected()){
            orden = "cantidad";
        }

        if(jRBOrdenMonto.isSelected()){
            orden = "totalventa";
        }

        if(jRBOrdenMargen.isSelected()){
            orden = "montomargen";
        }
        
        // control codigo vacio
        if(codigo.equals("0")){
            where = "WHERE";            
        }else{
            where = "WHERE a." + campo1 + " = " + codigo + " AND ";
        }
        

        if(jRBResumido.isSelected()){
            llenarTablaResumido(fecDesde, fecHasta, codigo, orden, campo1, campo2, tabla, where);
        }
        
    }//GEN-LAST:event_jBProcesarActionPerformed

    private void jTFCodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodigoFocusGained
        jTFCodigo.selectAll();
    }//GEN-LAST:event_jTFCodigoFocusGained

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
            java.util.logging.Logger.getLogger(InformeGralVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeGralVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeGralVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeGralVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeGralVentas dialog = new InformeGralVentas(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGFiltro;
    private javax.swing.ButtonGroup bGOrden;
    private javax.swing.ButtonGroup bGTipo;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBProcesar;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLDescripcion;
    private javax.swing.JLabel jLEmpresa;
    private javax.swing.JLabel jLLocal;
    private javax.swing.JLabel jLSector;
    private javax.swing.JLabel jLTipoFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRBDetallado;
    private javax.swing.JRadioButton jRBFiltroCodigo;
    private javax.swing.JRadioButton jRBFiltroGrupo;
    private javax.swing.JRadioButton jRBFiltroMarca;
    private javax.swing.JRadioButton jRBFiltroProveedor;
    private javax.swing.JRadioButton jRBOrdenCantidad;
    private javax.swing.JRadioButton jRBOrdenCodigo;
    private javax.swing.JRadioButton jRBOrdenDesc;
    private javax.swing.JRadioButton jRBOrdenMargen;
    private javax.swing.JRadioButton jRBOrdenMonto;
    private javax.swing.JRadioButton jRBResumido;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetalles;
    private javax.swing.JTextField jTFCodigo;
    private org.jdesktop.swingx.JXDatePicker jXDPDesde;
    private org.jdesktop.swingx.JXDatePicker jXDPHasta;
    // End of variables declaration//GEN-END:variables
}
