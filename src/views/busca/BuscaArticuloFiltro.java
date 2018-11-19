/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.busca;

import controls.GrupoCtrl;
import controls.MarcaCtrl;
import controls.ProveedorCtrl;
import controls.SubGrupoCtrl;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import utiles.CellRendererBusca;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TGridTableModel;
import utiles.Utiles;

/**
 *
 * @author Andres
 */
public class BuscaArticuloFiltro extends javax.swing.JDialog {

    static String tabla, sql;
    static String col1, col2, col3, col4;
    static String campoSele;
    static Vector titColumnas = new Vector();
    static ResultSet resSet = null;
    //  public String input = "";
    private static JTextField jTField = new javax.swing.JTextField();
    
    /**
     * Creates new form BuscaArticuloFiltro
     */
    public BuscaArticuloFiltro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = getSize();
        if (dialogSize.height > screenSize.height) {
            dialogSize.height = screenSize.height;
        }
        if (dialogSize.width > screenSize.width) {
            dialogSize.width = screenSize.width;
        }
        setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        
        jTDetalle.setDefaultRenderer(Object.class, new CellRendererBusca());
        jTDetalle.setRowHeight(21);
        
        jTFDescripcion.setDocument(new MaxLength(40, "UPPER", "ALFA"));
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
        jTFCodProveedor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFCodMarca = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFCodGrupo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFCodSGrupo = new javax.swing.JTextField();
        jLRazonSocProveedor = new javax.swing.JLabel();
        jLDescripcionMarca = new javax.swing.JLabel();
        jLDescripcionGrupo = new javax.swing.JLabel();
        jLDescripcionSubGrupo = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jTFDescripcion = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDetalle = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Búsqueda de Artículos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Proveedor:");

        jTFCodProveedor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodProveedor.setText("0");
        jTFCodProveedor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodProveedorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodProveedorFocusLost(evt);
            }
        });
        jTFCodProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodProveedorKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Marca:");

        jTFCodMarca.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodMarca.setText("0");
        jTFCodMarca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodMarcaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodMarcaFocusLost(evt);
            }
        });
        jTFCodMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodMarcaKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Grupo:");

        jTFCodGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodGrupo.setText("0");
        jTFCodGrupo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodGrupoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodGrupoFocusLost(evt);
            }
        });
        jTFCodGrupo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodGrupoKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("SubGrupo:");

        jTFCodSGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodSGrupo.setText("0");
        jTFCodSGrupo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodSGrupoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodSGrupoFocusLost(evt);
            }
        });
        jTFCodSGrupo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodSGrupoKeyPressed(evt);
            }
        });

        jLRazonSocProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLRazonSocProveedor.setText("Todos los Proveedores");

        jLDescripcionMarca.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescripcionMarca.setText("Todas las Marcas");

        jLDescripcionGrupo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescripcionGrupo.setText("Todos los Grupos");

        jLDescripcionSubGrupo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLDescripcionSubGrupo.setText("Todos los SubGrupos");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Digite Descripción:");

        jTFDescripcion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFDescripcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescripcionFocusGained(evt);
            }
        });
        jTFDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescripcionKeyPressed(evt);
            }
        });

        jTDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Precio", "Empaque"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class
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
        jTDetalle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetalleKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTDetalle);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescripcion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                    .addComponent(jTFCodMarca)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFCodGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                    .addComponent(jTFCodSGrupo))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLRazonSocProveedor)
                            .addComponent(jLDescripcionMarca)
                            .addComponent(jLDescripcionGrupo)
                            .addComponent(jLDescripcionSubGrupo))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRazonSocProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescripcionMarca))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFCodGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescripcionGrupo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFCodSGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescripcionSubGrupo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodProveedorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusGained
        jTFCodProveedor.selectAll();
    }//GEN-LAST:event_jTFCodProveedorFocusGained

    private void jTFCodMarcaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMarcaFocusGained
        jTFCodMarca.selectAll();
    }//GEN-LAST:event_jTFCodMarcaFocusGained

    private void jTFCodGrupoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodGrupoFocusGained
        jTFCodGrupo.selectAll();
    }//GEN-LAST:event_jTFCodGrupoFocusGained

    private void jTFCodSGrupoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodSGrupoFocusGained
        jTFCodSGrupo.selectAll();
    }//GEN-LAST:event_jTFCodSGrupoFocusGained

    private void jTFCodProveedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodProveedorFocusLost
        if(jTFCodProveedor.getText().equals("")){
            jTFCodProveedor.setText("0");
            jLRazonSocProveedor.setText("Todos los Proveedores");
        }else{
            if(jTFCodProveedor.getText().equals("0")){
                jTFCodProveedor.setText("0");
                jLRazonSocProveedor.setText("Todos los Proveedores");
            }else{
                try{
                    jLRazonSocProveedor.setText(getRazonSocProveedor(jTFCodProveedor.getText().trim()));
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        }
    }//GEN-LAST:event_jTFCodProveedorFocusLost

    private void jTFCodProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodProveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodMarca.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta de Proveedores");
                dCons.dConsultas("proveedor", "razon_soc", "cod_proveedor", "razon_soc", "telefono", null, "Código", "Razón Social", "Teléfono", null);
                dCons.setText(jTFCodProveedor);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodProveedorKeyPressed

    private void jTFCodMarcaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodMarcaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodGrupo.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta de Marcas");
                dCons.dConsultas("marca", "descripcion", "cod_marca", "descripcion", "fec_vigencia", null, "Código", "Descripción", "Registro", null);
                dCons.setText(jTFCodMarca);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodMarcaKeyPressed

    private void jTFCodGrupoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodGrupoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodSGrupo.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try{
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("ATOMSystems|Main - Consulta de Grupos");
                grupo.dConsultas("grupo", "descripcion", "cod_grupo", "descripcion", "margen_base_vta", "fec_vigencia", "Codigo", "Descripcion", "Margen Base", "Fecha");
                grupo.setText(jTFCodGrupo);
                grupo.setVisible(true);
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodGrupoKeyPressed

    private void jTFCodSGrupoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodSGrupoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFDescripcion.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultasSubgrupo subgrupo = new DlgConsultasSubgrupo(new JFrame(), true, "BUSQUEDA");
                subgrupo.pack();
                subgrupo.setText(jTFCodSGrupo);
                subgrupo.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodSGrupoKeyPressed

    private void jTFCodMarcaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodMarcaFocusLost
        if(jTFCodMarca.getText().equals("")){
            jTFCodMarca.setText("0");
            jLDescripcionMarca.setText("Todas las Marcas");
        }else{
            if(jTFCodMarca.getText().equals("0")){
                jTFCodMarca.setText("0");
                jLDescripcionMarca.setText("Todas las Marcas");
            }else{
                try{
                    jLDescripcionMarca.setText(getDescripcionMarca(jTFCodMarca.getText().trim()));
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        }
    }//GEN-LAST:event_jTFCodMarcaFocusLost

    private void jTFCodGrupoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodGrupoFocusLost
        if(jTFCodGrupo.getText().equals("")){
            jTFCodGrupo.setText("0");
            jLDescripcionGrupo.setText("Todos los Grupos");
        }else{
            if(jTFCodGrupo.getText().equals("0")){
                jTFCodGrupo.setText("0");
                jLDescripcionGrupo.setText("Todos los Grupos");
            }else{
                try{
                    jLDescripcionGrupo.setText(getNombreGrupo(jTFCodGrupo.getText().trim()));
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        }
    }//GEN-LAST:event_jTFCodGrupoFocusLost

    private void jTFCodSGrupoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodSGrupoFocusLost
        if(jTFCodSGrupo.getText().equals("")){
            jTFCodSGrupo.setText("0");
            jLDescripcionSubGrupo.setText("Todos los Grupos");
        }else{
            if(jTFCodSGrupo.getText().equals("0")){
                jTFCodSGrupo.setText("0");
                jLDescripcionSubGrupo.setText("Todos los Grupos");
            }else{
                try{
                    jLDescripcionSubGrupo.setText(getNombreSubGrupo(jTFCodSGrupo.getText().trim()));
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        }
    }//GEN-LAST:event_jTFCodSGrupoFocusLost

    private void jTFDescripcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescripcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFDescripcion.getText().equals("")){
                String text = "%" + jTFDescripcion.getText() + "%";
                sql = " (cod_proveedor = " + jTFCodProveedor.getText() + " OR " + jTFCodProveedor.getText() + " = 0)" +
                      " AND (cod_marca = " + jTFCodMarca.getText() + " OR " + jTFCodMarca.getText() + " = 0 )" +
                      " AND (cod_grupo = " + jTFCodGrupo.getText() + " OR " + jTFCodGrupo.getText() + " = 0 )" +
                      " AND (cod_subgrupo = " + jTFCodSGrupo.getText() + " OR " + jTFCodSGrupo.getText() + " = 0)";
                
                resSet = DBManager.ejecutarBuscaLikeArticuloFiltro(col1, col2, col3, col4, campoSele, tabla, text, sql);
                System.out.println("DATOS DE LA BUSQUEDA: \nCOL1: " + col1 + "\nCOL2: " + col2 + "\nCOL3: " + col3+ "\nCOL4: " + col4 +
                                   "\nCAMPOSELE: " + campoSele + "\nTEXT: " + text + "\nSQL: \n" + sql);
                setDataSet(resSet, titColumnas);
                if (jTDetalle.getRowCount() > 0) {
                    jTDetalle.setRowSelectionInterval(0, 0);
                    jTDetalle.grabFocus();
                }
            }else{
                JOptionPane.showMessageDialog(this,"ATENCION... Por favor ingrese una descripción!");
            }
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jTField.setText("");
            dispose();
        }
    }//GEN-LAST:event_jTFDescripcionKeyPressed

    private void jTDetalleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetalleKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //input = String.valueOf(jTDetalle.getValueAt(jTDetalle.getSelectedRow(),0));
            
            jTField.setText(String.valueOf(jTDetalle.getValueAt(jTDetalle.getSelectedRow(),0)));
            
            //JOptionPane.showMessageDialog(this, input);
            dispose();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jTField.setText("");
            dispose();
        }
    }//GEN-LAST:event_jTDetalleKeyPressed

    private void jTFDescripcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescripcionFocusGained
        jTFDescripcion.selectAll();
    }//GEN-LAST:event_jTFDescripcionFocusGained

    public static void dConsultas(String tablaSel, String campoSel,
            String colSel1, String colSel2, String colSel3, String colSel4,
            String titCol1, String titCol2, String titCol3, String titCol4) {
        
        tabla = tablaSel;       campoSele = campoSel;
        col1  = colSel1;        col2  = colSel2;
        col3  = colSel3;        col4  = colSel4;
        titColumnas.clear();
        if (titCol1 != null) {
            titColumnas.addElement(titCol1); }
        if (titCol2 != null) {
            titColumnas.addElement(titCol2); }
        if (titCol3 != null) {
            titColumnas.addElement(titCol3); }
        if (titCol4 != null) {
            titColumnas.addElement(titCol4); }
        titColumnas.addElement("Proveedor");
        titColumnas.addElement("Stock");
        cargaGrid();
    }
    
    private static void cargaGrid() {
        sql = " (cod_proveedor = " + jTFCodProveedor.getText() + " OR " + jTFCodProveedor.getText() + " = 0)" +
              " AND (cod_marca = " + jTFCodMarca.getText() + " OR "+jTFCodMarca.getText() + " = 0 )" +
              " AND (cod_grupo = " + jTFCodGrupo.getText() + " OR "+jTFCodGrupo.getText()+" = 0 )"+
              " AND (cod_subgrupo = " + jTFCodSGrupo.getText() + " OR " + jTFCodSGrupo.getText( )+ " = 0)";
        resSet = DBManager.ejecutarBuscaLikeArticuloFiltro(col1, col2, col3, col4, campoSele, tabla, "%ABC%", sql);
        setDataSet(resSet, titColumnas);
        jTDetalle.getColumnModel().getColumn(0).setPreferredWidth(010);
        jTDetalle.getColumnModel().getColumn(1).setPreferredWidth(300);
        jTDetalle.getColumnModel().getColumn(2).setPreferredWidth(030);
        jTDetalle.getColumnModel().getColumn(3).setPreferredWidth(010);
        jTDetalle.setVisible(true);
        jTDetalle.repaint();
    }
    
    
    private static void setDataSet(ResultSet resultSet, Vector colunmName) {
        try {
            if (resultSet != null && colunmName != null) {
                Vector linhas = new Vector();
                //resultSet.beforeFirst();
                while (resultSet.next()) {
                    Vector novaLinha = new Vector();
                    for (int cont = 1;
                    cont <= resultSet.getMetaData().getColumnCount();
                    cont++) {
                        novaLinha.addElement(resultSet.getObject(cont));
                    }
                    linhas.addElement(novaLinha);
                }
                jTDetalle.setModel(new TGridTableModel(linhas, colunmName));
                jTDetalle.getColumnModel().getColumn(0).setPreferredWidth(010);
                jTDetalle.getColumnModel().getColumn(1).setPreferredWidth(300);
                jTDetalle.getColumnModel().getColumn(2).setPreferredWidth(030);
                jTDetalle.getColumnModel().getColumn(3).setPreferredWidth(010);
                jTDetalle.setVisible(true);
                jTDetalle.repaint();
                // setModel(this.getTableModel());
                //AcomodaJTable.autoResizeJTable(this);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    
    private String getNombreGrupo(String codGrupo){
        String result = "";
        if(Utiles.isIntegerValid(codGrupo)){
            GrupoCtrl grupoCtrl = new GrupoCtrl();
            result = grupoCtrl.getDescripcionGrupo(Integer.valueOf(codGrupo));
            grupoCtrl = null;
        }
        return result;
    }
    
    private String getNombreSubGrupo(String codSGrupo){
        String result = "";
        if(Utiles.isIntegerValid(codSGrupo)){
            SubGrupoCtrl sGrupoCtrl = new SubGrupoCtrl();
            result = sGrupoCtrl.getDescripcionSubGrupo(Integer.valueOf(codSGrupo));
            sGrupoCtrl = null;
        }
        return result;
    }
    
    private String getDescripcionMarca(String codMarca){
        String result = "";
        if(Utiles.isIntegerValid(codMarca)){
            MarcaCtrl marcaCtrl = new MarcaCtrl();
            result = marcaCtrl.getDescripcionMarca(Integer.valueOf(codMarca));
            marcaCtrl = null;
        }
        return result;
    }
    
    private String getRazonSocProveedor(String codProveedor){
        String result = "";
        if(Utiles.isIntegerValid(codProveedor)){
            ProveedorCtrl provCtrl = new ProveedorCtrl();
            result = provCtrl.getRazonSocProveedor(Integer.valueOf(codProveedor));
            provCtrl = null;
        }
        return result;
    }
    
    public static void setText(JTextField jTF){
        jTField = jTF;
    }
    
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
            java.util.logging.Logger.getLogger(BuscaArticuloFiltro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloFiltro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloFiltro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloFiltro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscaArticuloFiltro dialog = new BuscaArticuloFiltro(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLDescripcionGrupo;
    private javax.swing.JLabel jLDescripcionMarca;
    private javax.swing.JLabel jLDescripcionSubGrupo;
    private javax.swing.JLabel jLRazonSocProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private static javax.swing.JTable jTDetalle;
    public static javax.swing.JTextField jTFCodGrupo;
    private static javax.swing.JTextField jTFCodMarca;
    public static javax.swing.JTextField jTFCodProveedor;
    private static javax.swing.JTextField jTFCodSGrupo;
    public static javax.swing.JTextField jTFDescripcion;
    // End of variables declaration//GEN-END:variables
}
