/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModAbastecimiento.informes;

import controls.GrupoCtrl;
import controls.MarcaCtrl;
import controls.ProveedorCtrl;
import controls.SubGrupoCtrl;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class InformeArticulos extends javax.swing.JDialog {

    /**
     * Creates new form InformeArticulos
     */
    public InformeArticulos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        jTFCodFiltro.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodFiltro.addFocusListener(new Focus());
        jTFCodFiltro.requestFocus();
        jTFCodFiltro.setText("0");
        jLDescFiltro.setText("TODOS");
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
    
    private void busquedaGrupos(){
        try{
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("ATOMSystems|Main - Consulta de Grupos");
                grupo.dConsultas("grupo", "descripcion", "cod_grupo", "descripcion", "margen_base_vta", "fec_vigencia", "Codigo", "Descripcion", "Margen Base", "Fecha");
                grupo.setText(jTFCodFiltro);
                grupo.setVisible(true);
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    private void busquedaSubGrupos(){
        try {
                DlgConsultas subgrupo = new DlgConsultas(new JFrame(), true);
                subgrupo.pack();
                subgrupo.setTitle("ATOMSystems|Main - Consulta de Sub Grupos");
                subgrupo.dConsultas("subgrupo", "descripcion", "cod_subgrupo", "descripcion", "margen_base_vta", "fec_vigencia", "Codigo", "Descripcion", "Margen Base", "Fecha");
                subgrupo.setText(jTFCodFiltro);
                subgrupo.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    private void busquedaProveedor(){
        try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta de Proveedores");
                dCons.dConsultas("proveedor", "razon_soc", "cod_proveedor", "razon_soc", "telefono", null, "Código", "Razón Social", "Teléfono", null);
                dCons.setText(jTFCodFiltro);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    private void busquedaMarcas(){
        try {
                DlgConsultas dCons = new DlgConsultas(new JFrame(), true); // false : busca local
                dCons.pack();
                dCons.setTitle("ATOMSystems|Main - Consulta de Marcas");
                dCons.dConsultas("marca", "descripcion", "cod_marca", "descripcion", "fec_vigencia", null, "Código", "Descripción", "Registro", null);
                dCons.setText(jTFCodFiltro);
                dCons.setVisible(true);
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    private void generarInforme(){
        String codigoFiltro = jTFCodFiltro.getText().trim();
        String campo_tabla_ordenar = "";
        String campo_tabla_filtro = "";
        String param_filtro1 = "";
        String param_filtro2 = "";
        String param_desc_filtro = jTFCodFiltro.getText().trim() + " " + jLDescFiltro.getText().trim();
        String horaFechaActual = utiles.Utiles.getSysDateTimeString();
        String sql = "";
        String informe_jasper = "";
        String campo_articulo_listado_todos = "";
        String campo_desc_listado_todos = "";
        String TABLA_INNER_JOIN = "";
        String CAMPO_ON = "";
        String CONDICION_STOCK_MENOR_IGUAL_CERO = "";

        if(jRBGrupo.isSelected()){
            campo_tabla_filtro = "cod_grupo";
            param_filtro2 = "Grupo: ";
            param_filtro1 = "(Filtro por Grupo)";
            campo_articulo_listado_todos = "cod_grupo";
            campo_desc_listado_todos = "grupo.descripcion";
            TABLA_INNER_JOIN = "grupo";
            CAMPO_ON = "grupo.cod_grupo";
        }
        
        if(jRBSubGrupo.isSelected()){
            campo_tabla_filtro = "cod_subgrupo";
            param_filtro2 = "Sub grupo: ";
            param_filtro1 = "(Filtro por Sub Grupo)";
            campo_articulo_listado_todos = "cod_subgrupo";
            campo_desc_listado_todos = "subgrupo.descripcion";
            TABLA_INNER_JOIN = "subgrupo";
            CAMPO_ON = "subgrupo.cod_subgrupo";
        }
        
        if(jRBProveedor.isSelected()){
            campo_tabla_filtro = "cod_proveedor";
            param_filtro2 = "Proveedor: ";
            param_filtro1 = "(Filtro por Proveedor)";
            campo_articulo_listado_todos = "cod_proveedor";
            campo_desc_listado_todos = "proveedor.razon_soc";
            TABLA_INNER_JOIN = "proveedor";
            CAMPO_ON = "proveedor.cod_proveedor";
        }
        
        if(jRBMarca.isSelected()){
            campo_tabla_filtro = "cod_marca";
            param_filtro2 = "Marca: ";
            param_filtro1 = "(Filtro por Marca)";
            campo_articulo_listado_todos = "cod_marca";
            campo_desc_listado_todos = "marca.descripcion";
            TABLA_INNER_JOIN = "marca";
            CAMPO_ON = "marca.cod_marca";
        }
        
        if(jRBOrdenCodigo.isSelected()){
            campo_tabla_ordenar = "cod_articulo";
        }
        
        if(jRBOrdenDescripcion.isSelected()){
            campo_tabla_ordenar = "descripcion";
        }
        
        if(jChBStockMenor0.isSelected()){
            CONDICION_STOCK_MENOR_IGUAL_CERO = " AND stockart.stock <= 0";
        }
        
        if(codigoFiltro.equals("0")){
            sql = "SELECT articulo.descripcion, articulo.cod_articulo, stockart.stock, costoart.costo_neto, preciosart.precio_venta, "
                + "articulo." + campo_articulo_listado_todos +  " || ' ' || " + campo_desc_listado_todos + " AS grupo,"
                + "(SELECT COUNT(articulo.cod_articulo) FROM articulo) AS cantidad, "
                + "(costoart.costo_neto * stockart.stock) AS total_costo, "
                + "(preciosart.precio_venta * stockart.stock) AS total_venta "
                + "FROM articulo "
                + "RIGHT OUTER JOIN stockart "
                + "ON stockart.cod_articulo = articulo.cod_articulo "
                + "INNER JOIN costoart "
                + "ON costoart.cod_articulo = articulo.cod_articulo "
                + "INNER JOIN preciosart "
                + "ON preciosart.cod_articulo = articulo.cod_articulo "
                + "INNER JOIN " + TABLA_INNER_JOIN + " "
                + "ON " + CAMPO_ON + " = articulo." + campo_articulo_listado_todos + " "
                + "WHERE costoart.vigente = 'S' AND preciosart.vigente = 'S' AND preciosart.cod_lista = 1 " + CONDICION_STOCK_MENOR_IGUAL_CERO + " "
                + "ORDER BY articulo." + campo_articulo_listado_todos + ", articulo." + campo_tabla_ordenar;
            informe_jasper = "informe_articulo_agrupado";
        }else{
            sql = "SELECT articulo.descripcion, articulo.cod_articulo, stockart.stock, costoart.costo_neto, preciosart.precio_venta, "
                + "(SELECT COUNT(articulo.cod_articulo) FROM articulo) AS cantidad,"
                + "(costoart.costo_neto * stockart.stock) AS total_costo, "
                + "(preciosart.precio_venta * stockart.stock) AS total_venta "
                + "FROM articulo "
                + "RIGHT OUTER JOIN stockart "
                + "ON stockart.cod_articulo = articulo.cod_articulo "
                + "INNER JOIN costoart "
                + "ON costoart.cod_articulo = articulo.cod_articulo "
                + "INNER JOIN preciosart "
                + "ON preciosart.cod_articulo = articulo.cod_articulo "
                + "WHERE costoart.vigente = 'S' AND preciosart.vigente = 'S' AND preciosart.cod_lista = 1 "
                + "AND articulo." + campo_tabla_filtro + " = " + codigoFiltro + CONDICION_STOCK_MENOR_IGUAL_CERO + " "
                + "ORDER BY articulo." + campo_tabla_ordenar;
            informe_jasper = "informe_articulo";
        }
        System.out.println("SQL REPORTE ARTICULOS: " + sql);       
        
        try{
            LibReportes.parameters.put("pFiltro", param_filtro1);
            LibReportes.parameters.put("pFiltroPor", param_filtro2);
            LibReportes.parameters.put("pFechaActual", horaFechaActual);
            LibReportes.parameters.put("pOperador", FormMain.nombreUsuario);
            LibReportes.parameters.put("pDescFiltro", param_desc_filtro);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, informe_jasper);
            
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

        bGroupFiltro = new javax.swing.ButtonGroup();
        bGroupOrden = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jRBGrupo = new javax.swing.JRadioButton();
        jRBSubGrupo = new javax.swing.JRadioButton();
        jRBProveedor = new javax.swing.JRadioButton();
        jRBMarca = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jRBOrdenDescripcion = new javax.swing.JRadioButton();
        jRBOrdenCodigo = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jLFiltro = new javax.swing.JLabel();
        jTFCodFiltro = new javax.swing.JTextField();
        jLDescFiltro = new javax.swing.JLabel();
        jBGenerarInforme = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jChBStockMenor0 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Informe de Artículos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("INFORME DE ARTICULOS");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Filtrar por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBGrupo.setBackground(new java.awt.Color(204, 255, 204));
        bGroupFiltro.add(jRBGrupo);
        jRBGrupo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBGrupo.setSelected(true);
        jRBGrupo.setText("Grupo");
        jRBGrupo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRBGrupoItemStateChanged(evt);
            }
        });
        jRBGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBGrupoActionPerformed(evt);
            }
        });

        jRBSubGrupo.setBackground(new java.awt.Color(204, 255, 204));
        bGroupFiltro.add(jRBSubGrupo);
        jRBSubGrupo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBSubGrupo.setText("Sub Grupo");
        jRBSubGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSubGrupoActionPerformed(evt);
            }
        });

        jRBProveedor.setBackground(new java.awt.Color(204, 255, 204));
        bGroupFiltro.add(jRBProveedor);
        jRBProveedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBProveedor.setText("Proveedor");
        jRBProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBProveedorActionPerformed(evt);
            }
        });

        jRBMarca.setBackground(new java.awt.Color(204, 255, 204));
        bGroupFiltro.add(jRBMarca);
        jRBMarca.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBMarca.setText("Marca");
        jRBMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMarcaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jRBGrupo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBSubGrupo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBProveedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRBMarca)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBGrupo)
                    .addComponent(jRBSubGrupo)
                    .addComponent(jRBProveedor)
                    .addComponent(jRBMarca))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Ordenar por:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jRBOrdenDescripcion.setBackground(new java.awt.Color(204, 255, 204));
        bGroupOrden.add(jRBOrdenDescripcion);
        jRBOrdenDescripcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBOrdenDescripcion.setSelected(true);
        jRBOrdenDescripcion.setText("Descripción");

        jRBOrdenCodigo.setBackground(new java.awt.Color(204, 255, 204));
        bGroupOrden.add(jRBOrdenCodigo);
        jRBOrdenCodigo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRBOrdenCodigo.setText("Código");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jRBOrdenDescripcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBOrdenCodigo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBOrdenDescripcion)
                    .addComponent(jRBOrdenCodigo))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLFiltro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLFiltro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLFiltro.setText("Grupo: ");

        jTFCodFiltro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodFiltro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodFiltro.setToolTipText("F1 para búsquedas");
        jTFCodFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodFiltroFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodFiltroFocusLost(evt);
            }
        });
        jTFCodFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodFiltroKeyPressed(evt);
            }
        });

        jLDescFiltro.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLDescFiltro.setText("***");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCodFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLDescFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLFiltro)
                    .addComponent(jTFCodFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDescFiltro))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jBGenerarInforme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/generarReport24.png"))); // NOI18N
        jBGenerarInforme.setText("Generar Informe");
        jBGenerarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerarInformeActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jChBStockMenor0.setBackground(new java.awt.Color(204, 255, 204));
        jChBStockMenor0.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jChBStockMenor0.setText("Listar artículos con stock negativo (-) y cero (0)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBGenerarInforme)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jChBStockMenor0)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jChBStockMenor0)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodFiltroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodFiltroFocusGained
        jTFCodFiltro.selectAll();
        
    }//GEN-LAST:event_jTFCodFiltroFocusGained

    private void jRBGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBGrupoActionPerformed
        jLFiltro.setText("Grupo: ");
        jLDescFiltro.setText("TODOS");
        jTFCodFiltro.setText("0");
        jTFCodFiltro.requestFocus();
    }//GEN-LAST:event_jRBGrupoActionPerformed

    private void jRBSubGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSubGrupoActionPerformed
        jLFiltro.setText("Sub Grupo: ");
        jLDescFiltro.setText("TODOS");
        jTFCodFiltro.setText("0");
        jTFCodFiltro.requestFocus();
    }//GEN-LAST:event_jRBSubGrupoActionPerformed

    private void jRBProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBProveedorActionPerformed
        jLFiltro.setText("Proveedor: ");
        jLDescFiltro.setText("TODOS");
        jTFCodFiltro.setText("0");
        jTFCodFiltro.requestFocus();
    }//GEN-LAST:event_jRBProveedorActionPerformed

    private void jRBMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBMarcaActionPerformed
        jLFiltro.setText("Marca: ");
        jLDescFiltro.setText("TODOS");
        jTFCodFiltro.setText("0");
        jTFCodFiltro.requestFocus();
    }//GEN-LAST:event_jRBMarcaActionPerformed

    private void jRBGrupoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRBGrupoItemStateChanged
        jLFiltro.setText("Grupo: ");
    }//GEN-LAST:event_jRBGrupoItemStateChanged

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFCodFiltroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodFiltroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            if(jRBGrupo.isSelected()){
                busquedaGrupos();
            }
            
            if(jRBSubGrupo.isSelected()){
                busquedaSubGrupos();
            }
            
            if(jRBProveedor.isSelected()){
                busquedaProveedor();
            }
            
            if(jRBMarca.isSelected()){
                busquedaMarcas();
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGenerarInforme.requestFocus();
        }
    }//GEN-LAST:event_jTFCodFiltroKeyPressed

    private void jTFCodFiltroFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodFiltroFocusLost
        if(jRBGrupo.isSelected()){
                if(jTFCodFiltro.getText().trim().equals("0")){
                    jLDescFiltro.setText("TODOS");
                }else{
                    jLDescFiltro.setText(getNombreGrupo(jTFCodFiltro.getText()));
                }
            }
            
            if(jRBSubGrupo.isSelected()){
                if(jTFCodFiltro.getText().trim().equals("0")){
                    jLDescFiltro.setText("TODOS");
                }else{
                    jLDescFiltro.setText(getNombreSubGrupo(jTFCodFiltro.getText()));
                }
            }
            
            if(jRBProveedor.isSelected()){
                if(jTFCodFiltro.getText().trim().equals("0")){
                    jLDescFiltro.setText("TODOS");
                }else{
                    jLDescFiltro.setText(getRazonSocProveedor(jTFCodFiltro.getText()));
                }
            }
            
            if(jRBMarca.isSelected()){
                if(jTFCodFiltro.getText().trim().equals("0")){
                    jLDescFiltro.setText("TODOS");
                }else{
                    jLDescFiltro.setText(getDescripcionMarca(jTFCodFiltro.getText()));

                }
            }
    }//GEN-LAST:event_jTFCodFiltroFocusLost

    private void jBGenerarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGenerarInformeActionPerformed
        generarInforme();
    }//GEN-LAST:event_jBGenerarInformeActionPerformed

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
            java.util.logging.Logger.getLogger(InformeArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InformeArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InformeArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InformeArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InformeArticulos dialog = new InformeArticulos(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bGroupFiltro;
    private javax.swing.ButtonGroup bGroupOrden;
    private javax.swing.JButton jBGenerarInforme;
    private javax.swing.JButton jBSalir;
    private javax.swing.JCheckBox jChBStockMenor0;
    private javax.swing.JLabel jLDescFiltro;
    private javax.swing.JLabel jLFiltro;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRBGrupo;
    private javax.swing.JRadioButton jRBMarca;
    private javax.swing.JRadioButton jRBOrdenCodigo;
    private javax.swing.JRadioButton jRBOrdenDescripcion;
    private javax.swing.JRadioButton jRBProveedor;
    private javax.swing.JRadioButton jRBSubGrupo;
    private javax.swing.JTextField jTFCodFiltro;
    // End of variables declaration//GEN-END:variables
}
