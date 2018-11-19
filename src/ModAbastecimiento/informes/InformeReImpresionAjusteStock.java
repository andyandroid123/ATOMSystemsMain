/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento.informes;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;

/**
 *
 * @author Andres
 */
public class InformeReImpresionAjusteStock extends javax.swing.JDialog {

    String fecVigencia = "";
    DefaultTableModel dtmDetallesAjuste;
    
    public InformeReImpresionAjusteStock(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        configCampos();
        configTabla();
        llenarCampos();
        limpiarTabla();
        jCBCodEmpresa.requestFocus();
    }

    private void limpiarTabla(){
        int nroFilas = dtmDetallesAjuste.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesAjuste.removeRow(i);
            nroFilas = dtmDetallesAjuste.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesAjuste.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetallesAjuste.setModel(dtmDetallesAjuste);
    }
    
    private void llenarCampos(){
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
        llenarCombos();
        jLRazonSocEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLDescLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jLDescSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
    }
    
    private void configTabla(){
        dtmDetallesAjuste = (DefaultTableModel) jTDetallesAjuste.getModel();
        jTDetallesAjuste.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTDetallesAjuste.getColumnModel().getColumn(1).setPreferredWidth(5);
        jTDetallesAjuste.getColumnModel().getColumn(2).setPreferredWidth(10);
        jTDetallesAjuste.getColumnModel().getColumn(3).setPreferredWidth(10);
        jTDetallesAjuste.getColumnModel().getColumn(4).setPreferredWidth(150);
        jTDetallesAjuste.setRowHeight(20);
    }
    
    private void configCampos(){
        
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        
        jTFFecDesde.addFocusListener(new Focus());
        jTFFecHasta.addFocusListener(new Focus());
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
    
    private void cargaBusqueda(){
        String fecDesde = "to_date('" + jTFFecDesde.getText() + "', 'dd/MM/yyyy')";
        String fecHasta = "to_date('" + jTFFecHasta.getText() + "', 'dd/MM/yyyy')";
        long vNroAjuste = 0;
        String vTipoAjuste = "";
        String vFecAjuste = "";
        double vMontoAjuste = 0;
        String vComentarios = "";
        
        String sql = "SELECT traspaso_cab.nro_traspaso, traspaso_cab.fec_traspaso::date, traspaso_cab.comentario, " +
                     "(CASE WHEN traspaso_cab.tipo_traspaso = 'ENT' THEN 'Entrada' ELSE 'Salida' END) AS tipoajuste " +
                     "FROM traspaso_cab " +
                     "INNER JOIN traspaso_det " +
                     "ON traspaso_cab.nro_traspaso = traspaso_det.nro_traspaso "
                   + "WHERE traspaso_cab.fec_traspaso >= " + fecDesde + " AND traspaso_cab.fec_traspaso <= " + fecHasta + " " +
                     "GROUP BY traspaso_cab.nro_traspaso, traspaso_cab.fec_traspaso, traspaso_cab.comentario, traspaso_cab.tipo_traspaso, traspaso_cab.estado = 'V' " +
                     "ORDER BY traspaso_cab.nro_traspaso, traspaso_cab.fec_traspaso, traspaso_cab.estado = 'V'";
        
        String sqlCosto = "SELECT traspaso_cab.nro_traspaso, SUM(traspaso_det.cant_traspaso * traspaso_det.costo_neto) AS monto " +
                          "FROM traspaso_cab " +
                          "INNER JOIN traspaso_det " +
                          "ON traspaso_cab.nro_traspaso = traspaso_det.nro_traspaso "
                        + "WHERE traspaso_cab.fec_traspaso >= " + fecDesde + " AND traspaso_cab.fec_traspaso <= " + fecHasta + " " +
                          "GROUP BY traspaso_cab.nro_traspaso " +
                          "ORDER BY traspaso_cab.nro_traspaso";
        System.out.println("SQL TRASPASO CAB: " + sql);
        System.out.println("SQL TRASPASO DET: " + sqlCosto);
        ResultSet rs, rs1; 
        rs = DBManager.ejecutarDSL(sql);
        rs1 = DBManager.ejecutarDSL(sqlCosto);
        try{
            if(rs != null && rs1 != null){
                while(rs.next() && rs1.next()){
                    vNroAjuste = rs.getInt("nro_traspaso");
                    vTipoAjuste = rs.getString("tipoajuste");
                    vFecAjuste = rs.getString("fec_traspaso");
                    vComentarios = rs.getString("comentario");
                    vMontoAjuste = rs1.getDouble("monto");
                    dtmDetallesAjuste.addRow(new Object[]{vNroAjuste, vTipoAjuste, vFecAjuste, vMontoAjuste, vComentarios});
                    jTDetallesAjuste.setModel(dtmDetallesAjuste);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void generarReporteAjuste(){
        String fecActual = utiles.Utiles.getSysDateTimeString();
        String pTipoCopia = "COPIA - RE IMPRESION";
        String pOperador = FormMain.nombreUsuario;
        int pNroAjuste = Integer.parseInt(dtmDetallesAjuste.getValueAt(jTDetallesAjuste.getSelectedRow(), 0).toString());
        int vMontoTotal = 0;
        
        String sql = "SELECT traspaso_cab.nro_traspaso, traspaso_cab.fec_traspaso::date, traspaso_cab.comentario, " +
                     "traspaso_det.cod_articulo, articulo.descripcion, traspaso_det.sigla_venta, " +
                     "(CASE WHEN traspaso_det.tipo_traspaso = 'ENT' THEN traspaso_det.cant_traspaso ELSE 0 END) AS entrada, " +
                     "(CASE WHEN traspaso_det.tipo_traspaso = 'SAL' THEN traspaso_det.cant_traspaso ELSE 0 END) AS salida, " +
                     "traspaso_det.stock_anterior, traspaso_det.costo_neto, " +
                     "(traspaso_det.cant_traspaso * traspaso_det.costo_neto) AS totalcosto " +
                     "FROM traspaso_cab " +
                     "INNER JOIN traspaso_det " +
                     "ON traspaso_cab.nro_traspaso = traspaso_det.nro_traspaso " +
                     "INNER JOIN articulo " +
                     "ON traspaso_det.cod_articulo = articulo.cod_articulo " +
                     "WHERE traspaso_cab.nro_traspaso = " + pNroAjuste + " " +
                     "ORDER BY traspaso_det.cod_articulo, articulo.descripcion, traspaso_det.fec_vigencia";
        
        try{
            LibReportes.parameters.put("pEmpresa", jLRazonSocEmpresa.getText().trim());
            LibReportes.parameters.put("pLocal", jLDescLocal.getText().trim());
            LibReportes.parameters.put("pSector", jLDescSector.getText().trim());
            LibReportes.parameters.put("pFechaActual", fecActual);
            LibReportes.parameters.put("pOperador", pOperador);
            LibReportes.parameters.put("pTipoCopia", pTipoCopia);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "ajusteStock");
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

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jCBCodSector = new javax.swing.JComboBox<>();
        jLRazonSocEmpresa = new javax.swing.JLabel();
        jLDescLocal = new javax.swing.JLabel();
        jLDescSector = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTFFecDesde = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFFecHasta = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetallesAjuste = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Re Impresión de Ajuste de Stock");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("RE IMPRESION DE AJUSTE DE STOCK");

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

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Empresa:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Local:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Sector:");

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

        jLRazonSocEmpresa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLRazonSocEmpresa.setText("***");

        jLDescLocal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLDescLocal.setText("***");

        jLDescSector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLDescSector.setText("***");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Desde:");

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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Hasta:");

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

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLRazonSocEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLDescSector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jBBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir)))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscar, jBImprimir, jBSalir});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRazonSocEmpresa)
                    .addComponent(jLabel1)
                    .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLDescLocal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLDescSector)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBBuscar)
                            .addComponent(jBImprimir)
                            .addComponent(jBSalir))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscar, jBImprimir, jBSalir});

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jTDetallesAjuste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTDetallesAjuste.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro. Ajuste", "Tipo ", "Fecha", "Valor", "Comentarios"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
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
        jScrollPane1.setViewportView(jTDetallesAjuste);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        setSize(new java.awt.Dimension(775, 575));
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
            jBBuscar.requestFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        limpiarTabla();
        cargaBusqueda();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(dtmDetallesAjuste.getRowCount() > 0 && !(jTDetallesAjuste.getSelectedRow() == -1)){
            generarReporteAjuste();
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
            java.util.logging.Logger.getLogger(InformeReImpresionAjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionAjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionAjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionAjusteStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeReImpresionAjusteStock dialog = new InformeReImpresionAjusteStock(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBCodEmpresa;
    private javax.swing.JComboBox<String> jCBCodLocal;
    private javax.swing.JComboBox<String> jCBCodSector;
    private javax.swing.JLabel jLDescLocal;
    private javax.swing.JLabel jLDescSector;
    private javax.swing.JLabel jLRazonSocEmpresa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetallesAjuste;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    // End of variables declaration//GEN-END:variables
}
