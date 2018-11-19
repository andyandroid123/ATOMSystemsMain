/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento.informes;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;

/**
 *
 * @author Andres
 */
public class InformeComprasGastos extends javax.swing.JDialog {

    String fecVigencia = "";
    String codEmpresa, codLocal, codSector;
    
    public InformeComprasGastos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        llenarCombos();
        codEmpresa = jCBCodEmpresa.getSelectedItem().toString();
        codLocal = jCBCodLocal.getSelectedItem().toString();
        codSector = jCBCodSector.getSelectedItem().toString();
        configCampos();
        llenarCampos();
    }

    private void llenarCampos(){
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
        jLDescEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(codEmpresa));
        jLDescLocal.setText(utiles.Utiles.getDescripcionLocal(codLocal));
        jLDescSector.setText(utiles.Utiles.getSectorDescripcion(codLocal, codSector));
    }
    
    private void configCampos(){
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        
        jTFFecDesde.addFocusListener(new Focus()); 
        jTFFecHasta.addFocusListener(new Focus()); 
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
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
    
    private void generarInforme(){
        String vFecDesde = "'" +jTFFecDesde.getText() + "'::date";
        String vFecHasta =  "'" +jTFFecHasta.getText() + "'::date";
        String horaFechaActual = utiles.Utiles.getSysDateTimeString();
        String nombreCampoFecha = "";
        String vFiltro = "";
        String condicion = "";
        String NOMBRE_REPORTE = "";
        
        if(jRBFecComprob.isSelected()){
            nombreCampoFecha = "compra_cab.fec_comprob::date";
            vFiltro = "(Listado por fecha de Comprobante)";
        }
        
        if(jRBFecRec.isSelected()){
            nombreCampoFecha = "compra_cab.fec_vigencia::date";
            vFiltro = "(Listado por fecha de Recepción)";
        }
        
        if(jRBMercaderias.isSelected()){
            condicion = "AND compra_cab.cod_tipomerc = 1 ";
        }
        
        if(jRBGastos.isSelected()){
            condicion = "AND compra_cab.cod_tipomerc <> 1 ";
        }
        
        if(jRBDetallado.isSelected()){
            NOMBRE_REPORTE = "informComprasDetallado";
        }
        
        if(jRBResumido.isSelected()){
            NOMBRE_REPORTE = "informCompras";
        }
        
        String sql = "SELECT compra_cab.fec_vigencia, compra_cab.fec_comprob, compra_cab.nro_comprob, compra_cab.tip_comprob, " +
                     "compra_cab.nro_timbrado, compra_cab.cod_proveedor || ' ' || proveedor.razon_soc AS proveedor, compra_cab.total_grava10, " +
                     "compra_cab.total_grava05, compra_cab.total_exento, SUM(compra_cab.total_grava10 + compra_cab.total_grava05 + compra_cab.total_exento)" +
                     "AS total,(CASE WHEN compra_cab.cod_tipomerc = 1 THEN 'MERCADERIAS' ELSE tipo_mercaderia.descripcion END) AS descripcion " +
                     "FROM compra_cab " +
                     "INNER JOIN proveedor " +
                     "ON compra_cab.cod_proveedor = proveedor.cod_proveedor " +
                     "INNER JOIN tipo_mercaderia " +
                     "ON tipo_mercaderia.cod_tipomerc = compra_cab.cod_tipomerc " +
                     "WHERE " + nombreCampoFecha + " >= " + vFecDesde + " AND " + nombreCampoFecha + " <= " + vFecHasta + " " +
                     "AND cod_empresa = " + codEmpresa + " AND cod_local = " + codLocal + " AND cod_sector = " + codSector + " " + condicion + " " +
                     "GROUP BY compra_cab.fec_vigencia, compra_cab.fec_comprob, compra_cab.nro_comprob, compra_cab.tip_comprob, compra_cab.nro_timbrado, proveedor, " +
                     "compra_cab.total_grava10, compra_cab.total_grava05, compra_cab.total_exento, compra_cab.cod_tipomerc, tipo_mercaderia.descripcion " +
                     "ORDER BY compra_cab.fec_comprob, proveedor";
        
        System.out.println("SQL REPORTE: " + sql);
        
        try{
            LibReportes.parameters.put("pEmpresa", jLDescEmpresa.getText().trim());
            LibReportes.parameters.put("pLocal", jLDescLocal.getText().trim());
            LibReportes.parameters.put("pSector", jLDescSector.getText().trim());
            LibReportes.parameters.put("pFechaDesde", jTFFecDesde.getText().trim());
            LibReportes.parameters.put("pFechaHasta", jTFFecHasta.getText().trim());
            LibReportes.parameters.put("pFechaActual", horaFechaActual);
            LibReportes.parameters.put("pOperador", FormMain.nombreUsuario);
            LibReportes.parameters.put("pFiltro", vFiltro);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, NOMBRE_REPORTE);
            
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

        bGFiltro = new javax.swing.ButtonGroup();
        bGMercGastos = new javax.swing.ButtonGroup();
        bGResumidoDetallado = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jLDescEmpresa = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jLDescLocal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodSector = new javax.swing.JComboBox<>();
        jLDescSector = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jRBFecComprob = new javax.swing.JRadioButton();
        jRBFecRec = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jTFFecDesde = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFFecHasta = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jRBMercaderias = new javax.swing.JRadioButton();
        jRBGastos = new javax.swing.JRadioButton();
        jRBTodos = new javax.swing.JRadioButton();
        jBGenerarInforme = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jRBDetallado = new javax.swing.JRadioButton();
        jRBResumido = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe Compras y Gastos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("INFORME DE COMPRAS Y GASTOS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Empresa:");

        jCBCodEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodEmpresaKeyPressed(evt);
            }
        });

        jLDescEmpresa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescEmpresa.setText("***");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Local:");

        jCBCodLocal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodLocalKeyPressed(evt);
            }
        });

        jLDescLocal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescLocal.setText("***");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Sector:");

        jCBCodSector.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCodSectorKeyPressed(evt);
            }
        });

        jLDescSector.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLDescSector.setText("***");

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
                        .addComponent(jLDescEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescEmpresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescSector))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Filtrar por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBFecComprob.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFecComprob);
        jRBFecComprob.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFecComprob.setSelected(true);
        jRBFecComprob.setText("Fecha Comprobante");

        jRBFecRec.setBackground(new java.awt.Color(204, 255, 204));
        bGFiltro.add(jRBFecRec);
        jRBFecRec.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBFecRec.setText("Fecha Recepción");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Desde:");

        jTFFecDesde.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Hasta:");

        jTFFecHasta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRBFecComprob)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRBFecRec))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBFecComprob)
                    .addComponent(jRBFecRec))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Mercaderías/Gastos:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBMercaderias.setBackground(new java.awt.Color(204, 255, 204));
        bGMercGastos.add(jRBMercaderias);
        jRBMercaderias.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBMercaderias.setText("Mercaderías");
        jRBMercaderias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMercaderiasActionPerformed(evt);
            }
        });

        jRBGastos.setBackground(new java.awt.Color(204, 255, 204));
        bGMercGastos.add(jRBGastos);
        jRBGastos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBGastos.setText("Gastos");
        jRBGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBGastosActionPerformed(evt);
            }
        });

        jRBTodos.setBackground(new java.awt.Color(204, 255, 204));
        bGMercGastos.add(jRBTodos);
        jRBTodos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBTodos.setSelected(true);
        jRBTodos.setText("Todos");
        jRBTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBMercaderias)
                    .addComponent(jRBGastos)
                    .addComponent(jRBTodos))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRBMercaderias)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBGastos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBTodos)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jBGenerarInforme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/generarReport24.png"))); // NOI18N
        jBGenerarInforme.setText("Generar Informe");
        jBGenerarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerarInformeActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Sailr");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jRBDetallado.setBackground(new java.awt.Color(204, 255, 204));
        bGResumidoDetallado.add(jRBDetallado);
        jRBDetallado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBDetallado.setText("Detallado");
        jRBDetallado.setEnabled(false);

        jRBResumido.setBackground(new java.awt.Color(204, 255, 204));
        bGResumidoDetallado.add(jRBResumido);
        jRBResumido.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRBResumido.setSelected(true);
        jRBResumido.setText("Resumido");
        jRBResumido.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBGenerarInforme)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jRBDetallado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRBResumido)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRBDetallado)
                            .addComponent(jRBResumido))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBGenerarInforme)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(544, 375));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

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
            jTFFecDesde.requestFocus();
        }
    }//GEN-LAST:event_jCBCodSectorKeyPressed

    private void jTFFecDesdeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDesdeFocusGained
        jTFFecDesde.selectAll();
    }//GEN-LAST:event_jTFFecDesdeFocusGained

    private void jTFFecDesdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDesdeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecHasta.requestFocus();
        }
    }//GEN-LAST:event_jTFFecDesdeKeyPressed

    private void jTFFecHastaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHastaFocusGained
        jTFFecHasta.selectAll();
    }//GEN-LAST:event_jTFFecHastaFocusGained

    private void jTFFecHastaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecHastaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGenerarInforme.requestFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jBGenerarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGenerarInformeActionPerformed
        generarInforme();
    }//GEN-LAST:event_jBGenerarInformeActionPerformed

    private void jRBTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTodosActionPerformed
        jRBResumido.setEnabled(false);
        jRBDetallado.setEnabled(false);
        jRBResumido.setSelected(true);
    }//GEN-LAST:event_jRBTodosActionPerformed

    private void jRBGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBGastosActionPerformed
        jRBResumido.setEnabled(false);
        jRBDetallado.setEnabled(false);
        jRBResumido.setSelected(true);
    }//GEN-LAST:event_jRBGastosActionPerformed

    private void jRBMercaderiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBMercaderiasActionPerformed
        jRBResumido.setEnabled(true);
        jRBDetallado.setEnabled(true);
    }//GEN-LAST:event_jRBMercaderiasActionPerformed

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
            java.util.logging.Logger.getLogger(InformeComprasGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeComprasGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeComprasGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeComprasGastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeComprasGastos dialog = new InformeComprasGastos(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGMercGastos;
    private javax.swing.ButtonGroup bGResumidoDetallado;
    private javax.swing.JButton jBGenerarInforme;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLDescEmpresa;
    private javax.swing.JLabel jLDescLocal;
    private javax.swing.JLabel jLDescSector;
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
    private javax.swing.JRadioButton jRBDetallado;
    private javax.swing.JRadioButton jRBFecComprob;
    private javax.swing.JRadioButton jRBFecRec;
    private javax.swing.JRadioButton jRBGastos;
    private javax.swing.JRadioButton jRBMercaderias;
    private javax.swing.JRadioButton jRBResumido;
    private javax.swing.JRadioButton jRBTodos;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    // End of variables declaration//GEN-END:variables
}
