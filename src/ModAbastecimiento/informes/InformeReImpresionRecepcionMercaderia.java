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
public class InformeReImpresionRecepcionMercaderia extends javax.swing.JDialog {

    String fecVigencia = "";
    DefaultTableModel dtmDetallesRecepcion;
    long pNroRecepcion;
            
    public InformeReImpresionRecepcionMercaderia(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getFecVigencia();
        configCampos();
        configTabla();
        llenarCampos();
        limpiarTabla();
        jCBCodEmpresa.requestFocus();
    }

    private void configCampos(){
        
        jTFNroRecepcion.setDocument(new MaxLength(8, "", "ENTERO"));
        jTFFecDesde.setInputVerifier(new FechaInputVerifier(jTFFecDesde));
        jTFFecHasta.setInputVerifier(new FechaInputVerifier(jTFFecHasta));
        
        jTFNroRecepcion.addFocusListener(new Focus());
        jTFFecDesde.addFocusListener(new Focus());
        jTFFecHasta.addFocusListener(new Focus());
    }
    
    private void configTabla(){
        dtmDetallesRecepcion = (DefaultTableModel) jTDetalleRecepcion.getModel();
        jTDetalleRecepcion.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTDetalleRecepcion.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTDetalleRecepcion.getColumnModel().getColumn(2).setPreferredWidth(10);
        jTDetalleRecepcion.getColumnModel().getColumn(3).setPreferredWidth(1);
        jTDetalleRecepcion.setRowHeight(20);
    }
    
    private void llenarCampos(){
        jTFFecDesde.setText(fecVigencia);
        jTFFecHasta.setText(fecVigencia);
        jTFNroRecepcion.setText("0");
        llenarCombos();
        jLRazonSocEmpresa.setText(utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString()));
        jLDescripcionLocal.setText(utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString()));
        jLDescripcionSector.setText(utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString()));
    }
    
    private void llenarCombos(){
        utiles.Utiles.cargaComboEmpresas(this.jCBCodEmpresa);
        utiles.Utiles.cargaComboLocales(this.jCBCodLocal);
        utiles.Utiles.cargaComboSectores(this.jCBCodSector);
    }
    
    private void limpiarTabla(){
        int nroFilas = dtmDetallesRecepcion.getRowCount();
        int i = 0;
        while(i < nroFilas){
            dtmDetallesRecepcion.removeRow(i);
            nroFilas = dtmDetallesRecepcion.getRowCount();
            
            if(nroFilas == 1){
                dtmDetallesRecepcion.removeRow(i);
                nroFilas = 0;
            }
        }
        jTDetalleRecepcion.setModel(dtmDetallesRecepcion);
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
    
    private void generarReporteRecepcion(){
        String fecActual = utiles.Utiles.getSysDateTimeString();
        String pTipoCopia = "COPIA - RE IMPRESION";
        String pOperador = FormMain.nombreUsuario;
        String pEmpresa = utiles.Utiles.getRazonSocialEmpresa(jCBCodEmpresa.getSelectedItem().toString());
        String pLocal = utiles.Utiles.getDescripcionLocal(jCBCodLocal.getSelectedItem().toString());
        String pSector = utiles.Utiles.getSectorDescripcion(jCBCodLocal.getSelectedItem().toString(), jCBCodSector.getSelectedItem().toString());
        int vMontoTotal = 0;
                                
        
        String sql = "SELECT DISTINCT (CASE WHEN recep.estado = 'V' THEN 'RECIBIDO' WHEN recep.estado = 'A' THEN 'ANULADO' ELSE 'No disponible' END) "
                   + "AS estado, to_char(recep.fec_recepcion, 'dd/MM/yyyy hh:mm:ss') AS fecRecepcion, recep.nro_comprob AS nroRecepcion, recep.serie_pedido "
                   + "AS serie, prov.cod_proveedor || ' - ' || prov.razon_soc AS proveedor, prov.ruc_proveedor AS ruc, recep.cod_barras AS barras, "
                   + "recep.cod_articulo AS codArticulo, art.descripcion AS descripcion, recep.sigla_compra AS empaque, recep.cant_recepcion, recep.mon_total "
                   + "FROM recepcion_ped recep "
                   + "INNER JOIN proveedor prov "
                   + "ON recep.cod_proveedor = prov.cod_proveedor "
                   + "INNER JOIN articulo art "
                   + "ON recep.cod_articulo = art.cod_articulo "
                   + "WHERE recep.nro_comprob = " + pNroRecepcion + " "
                   + "ORDER BY recep.cod_articulo";
        
        try{
            String sqlMontoTotal = "SELECT sum(mon_total) AS monto FROM recepcion_ped WHERE nro_comprob = " + pNroRecepcion + "";
            ResultSet rs = DBManager.ejecutarDSL(sqlMontoTotal);
            if(rs != null){
                if(rs.next()){
                    vMontoTotal = rs.getInt("monto");
                }
            }
            
            LibReportes.parameters.put("pEmpresa", pEmpresa);
            LibReportes.parameters.put("pLocal", pLocal);
            LibReportes.parameters.put("pSector", pSector);
            LibReportes.parameters.put("pFechaActual", fecActual);
            LibReportes.parameters.put("pOperador", pOperador);
            LibReportes.parameters.put("pTipoCopia", pTipoCopia);
            LibReportes.parameters.put("pNroRecepcion", pNroRecepcion);
            LibReportes.parameters.put("pMontoTotal", vMontoTotal);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "recepcionMercaderias");
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBCodEmpresa = new javax.swing.JComboBox<>();
        jCBCodLocal = new javax.swing.JComboBox<>();
        jCBCodSector = new javax.swing.JComboBox<>();
        jLRazonSocEmpresa = new javax.swing.JLabel();
        jLDescripcionLocal = new javax.swing.JLabel();
        jLDescripcionSector = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFNroRecepcion = new javax.swing.JTextField();
        jBBuscar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTFFecDesde = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFFecHasta = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalleRecepcion = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Re Impresión de Recepción Directa de Mercaderías");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("RE IMPRESION DE RECEPCION DIRECTA DE MERCADERIAS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Sector:");

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

        jLDescripcionLocal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLDescripcionLocal.setText("***");

        jLDescripcionSector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLDescripcionSector.setText("***");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Nro. Recepción:");

        jTFNroRecepcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroRecepcion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNroRecepcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroRecepcionFocusGained(evt);
            }
        });
        jTFNroRecepcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroRecepcionKeyPressed(evt);
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

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Desde:");

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

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Hasta:");

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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLRazonSocEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(305, 305, 305)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNroRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLDescripcionLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLDescripcionSector, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jBBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBImprimir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBSalir)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(192, 192, 192))))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscar, jBImprimir, jBSalir});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCBCodEmpresa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jCBCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLDescripcionLocal))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jCBCodSector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLDescripcionSector)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTFFecDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jTFFecHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jTFNroRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLRazonSocEmpresa))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBBuscar)
                            .addComponent(jBImprimir)
                            .addComponent(jBSalir))))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscar, jBImprimir, jBSalir});

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jTDetalleRecepcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTDetalleRecepcion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Número", "Proveedor", "Fecha", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTDetalleRecepcion);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(134, 134, 134))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        setSize(new java.awt.Dimension(889, 580));
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

    private void jTFFecDesdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecDesdeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFFecHasta.requestFocus();
        }
    }//GEN-LAST:event_jTFFecDesdeKeyPressed

    private void jTFFecHastaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecHastaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroRecepcion.requestFocus();
        }
    }//GEN-LAST:event_jTFFecHastaKeyPressed

    private void jTFNroRecepcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroRecepcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBBuscar.requestFocus();
        }
    }//GEN-LAST:event_jTFNroRecepcionKeyPressed

    private void jTFFecDesdeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecDesdeFocusGained
        jTFFecDesde.selectAll();
    }//GEN-LAST:event_jTFFecDesdeFocusGained

    private void jTFFecHastaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecHastaFocusGained
        jTFFecHasta.selectAll();
    }//GEN-LAST:event_jTFFecHastaFocusGained

    private void jTFNroRecepcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroRecepcionFocusGained
        jTFNroRecepcion.selectAll();
    }//GEN-LAST:event_jTFNroRecepcionFocusGained

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        
        limpiarTabla();
        String fecDesde = "to_date('" + jTFFecDesde.getText() + "', 'dd/MM/yyyy')";
        String fecHasta = "to_date('" + jTFFecHasta.getText() + "', 'dd/MM/yyyy')";
        long vNroRecepcion = 0;
        String vProveedor = "";
        String vFecRegistro = "";
        String vEstado = "";
        
        
        String sqlGral = "SELECT recep.nro_comprob AS nroRecepcion, prov.razon_soc AS proveedor, to_char(recep.fec_recepcion, 'dd/MM/yyyy hh:mm:ss') AS "
                       + "fecha, CASE WHEN recep.estado = 'V' THEN 'RECIBIDO' WHEN recep.estado = 'A' THEN 'ANULADO' END AS estado "
                       + "FROM recepcion_ped recep "
                       + "INNER JOIN proveedor prov "
                       + "ON recep.cod_proveedor = prov.cod_proveedor "
                       + "WHERE recep.fec_recepcion >= " + fecDesde + " AND recep.fec_recepcion <= " + fecHasta + " "
                       + "GROUP BY recep.nro_comprob, prov.razon_soc, recep.fec_recepcion, recep.estado "
                       + "ORDER BY recep.fec_recepcion, "
                       + "recep.nro_comprob";
        System.out.println("SQL BUSQUEDA RECEPCION: " + sqlGral);
        ResultSet rs = DBManager.ejecutarDSL(sqlGral);
        try{
            if(rs != null){
                while(rs.next()){
                    vNroRecepcion = rs.getLong("nroRecepcion");
                    vProveedor = rs.getString("proveedor");
                    vFecRegistro = rs.getString("fecha");
                    vEstado = rs.getString("estado");
                    
                    dtmDetallesRecepcion.addRow(new Object[]{new Long(vNroRecepcion), new String(vProveedor), new String(vFecRegistro), new String(vEstado)});
                    jTDetalleRecepcion.setModel(dtmDetallesRecepcion);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        pNroRecepcion = 0;
        if(!jTFNroRecepcion.getText().trim().equals("") && !jTFNroRecepcion.getText().trim().equals("0")){
            pNroRecepcion = Long.parseLong(jTFNroRecepcion.getText().trim());
            generarReporteRecepcion();
        }else{
            if(dtmDetallesRecepcion.getRowCount() > 0 && !(jTDetalleRecepcion.getSelectedRow() == -1)){                
                int filaSeleccionada = jTDetalleRecepcion.getSelectedRow();
                pNroRecepcion = (Long)dtmDetallesRecepcion.getValueAt(filaSeleccionada, 0);
                generarReporteRecepcion();
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
            java.util.logging.Logger.getLogger(InformeReImpresionRecepcionMercaderia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionRecepcionMercaderia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionRecepcionMercaderia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeReImpresionRecepcionMercaderia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeReImpresionRecepcionMercaderia dialog = new InformeReImpresionRecepcionMercaderia(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLDescripcionLocal;
    private javax.swing.JLabel jLDescripcionSector;
    private javax.swing.JLabel jLRazonSocEmpresa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTDetalleRecepcion;
    private javax.swing.JTextField jTFFecDesde;
    private javax.swing.JTextField jTFFecHasta;
    private javax.swing.JTextField jTFNroRecepcion;
    // End of variables declaration//GEN-END:variables
}
