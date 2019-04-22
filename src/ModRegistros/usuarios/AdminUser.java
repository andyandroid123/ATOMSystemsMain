/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import utiles.DBManager;
import utiles.InfoErrores;
import utiles.StatementManager;
import views.busca.DlgConsultas;

/**
 *
 * @author ANDRES
 */
public class AdminUser extends javax.swing.JDialog {

    DBManager dbm = new DBManager();
    DefaultMutableTreeNode top;
    DefaultTreeModel treeModel;
    ResultSet DataNode1 = null;
    ResultSet DataNode2 = null;
    ResultSet DataNode3 = null;
    ResultSet DataNode4 = null;
    JTextField txtcoduser = new JTextField();
    String usuario;
    
    public AdminUser(java.awt.Frame parent, boolean modal, String cod_usuario) {
        super(parent, modal);
        initComponents();
        usuario = cod_usuario;
        DataNode1 = rsDataNode();
        DataNode2 = rsDataNode();
        DataNode3 = rsDataNode();
        DataNode4 = rsDataNode();
        initTree();
        Conf_IconArbol();
        closeStatements();
    }

    private void initTree(){
        NodeInfo app_nivel0;
        int nivel = 0;
        app_nivel0 = new NodeInfo("Arbol de administracion de usuarios", 0, nivel);
        top = new DefaultMutableTreeNode(app_nivel0.getFuente());
        createNodes(top, "NU");
        treeModel = new DefaultTreeModel(top);
        JTreePrivileges.setModel(treeModel);
    }
    
    private ResultSet rsDataNode(){
        ResultSet rs = null;
        String sql = "SELECT pu.cod_usuario_perfil, u.nombre, pu.cod_modulo, m.descripcion AS modulos, "
                   + "pu.cod_menu, mm.descripcion AS menus, pu.cod_item, mmi.descripcion AS item "
                   + "FROM perfil_usuario pu "
                   + "INNER JOIN usuario u "
                   + "ON pu.cod_usuario_perfil = u.cod_usuario "
                   + "INNER JOIN modulo m "
                   + "ON pu.cod_modulo = m.cod_modulo "
                   + "INNER JOIN modulo_menu mm "
                   + "ON pu.cod_menu = mm.cod_menu "
                   + "INNER JOIN modulo_menuitem mmi "
                   + "ON pu.cod_item = mmi.cod_item "
                   + "WHERE pu.cod_usuario_perfil = " + usuario + " "
                   + "AND u.activo = 'S' "
                   + "ORDER BY u.cod_usuario, pu.cod_modulo";
        System.out.println("SQL RS DATA NODE: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        return rs;
    }
    
    private void createNodes(DefaultMutableTreeNode top, String NU){
        DefaultMutableTreeNode node;
        NodeInfo app;
        int usucod = 0;
        try{
            while(DataNode1.next()){
                if(DataNode1.getInt("cod_usuario_perfil") != usucod){
                    String usercod = String.valueOf(DataNode1.getInt("cod_usuario_perfil")) + "-" + DataNode1.getString("nombre");
                    app = new NodeInfo(usercod, DataNode1.getInt("cod_usuario_perfil"), 0);
                    node = new DefaultMutableTreeNode(app.getFuente());
                    top.add(node);
                    
                    usucod = DataNode1.getInt("cod_usuario_perfil");
                    createIneritNode(node, usucod);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void createIneritNode(DefaultMutableTreeNode parentnode, int usucod){
        DefaultMutableTreeNode node;
        int modulo = 0;
        try{
            while(DataNode2.next()){
                if(DataNode2.getInt("cod_modulo") != modulo && DataNode2.getInt("cod_usuario_perfil") == usucod){
                    node = new DefaultMutableTreeNode(DataNode2.getString("modulos"));
                    parentnode.add(node);
                    modulo = DataNode2.getInt("cod_modulo");
                    createIneritNode1(node, modulo, usucod);
                }
            }
            DataNode2.absolute(1);
            modulo = 0;
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
    }
    
    private void createIneritNode1(DefaultMutableTreeNode parentnode, int modulo, int usucod ){
        DefaultMutableTreeNode node;
        int menu = 0;
        try{
            while(DataNode3.next()){
                if(DataNode3.getInt("cod_modulo") == modulo && DataNode3.getInt("cod_usuario_perfil") == usucod && DataNode3.getInt("cod_menu") != menu){
                    node = new DefaultMutableTreeNode(DataNode3.getString("menus"));
                    parentnode.add(node);
                    menu = DataNode3.getInt("cod_menu");
                    createIneritNode1(node, modulo, usucod, menu);
                }
            }
            DataNode3.absolute(1);
            menu = 0;
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
    }
    
    private void createIneritNode1(DefaultMutableTreeNode parentnode, int modulo, int usucod, int menu){
        DefaultMutableTreeNode node;
        int item = 0;
        try{
            while(DataNode4.next()){
                if(DataNode4.getInt("cod_modulo") == modulo && DataNode4.getInt("cod_usuario_perfil") == usucod && DataNode4.getInt("cod_menu") == menu 
                   && DataNode4.getInt("cod_item") != item){
                    node = new DefaultMutableTreeNode(DataNode4.getString("item"));
                    parentnode.add(node);
                    item = DataNode4.getInt("cod_item");
                }
            }
            DataNode4.absolute(1);
            item = 0;
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
    }
    
    private void Conf_IconArbol(){
        JTreePrivileges.setCellRenderer(new MyTreeRender());
    }
    
    private void closeStatements(){
        try{
            if(DataNode1 != null){
                DataNode1.close();
            }else if(DataNode2 != null){
                DataNode2.close();
            }else if(DataNode3 != null){
                DataNode3.close();
            }else if(DataNode4 != null){
                DataNode4.close();
            }else if(DBManager.rset != null){
                DBManager.rset.close();
            }else if(DBManager.stmt != null){
                DBManager.stmt.cancel();
            }
        }catch(SQLException sqlex){}
    }
    
    public void dispose() {
        super.dispose();
    }
    
    public void imprimirNodos(TreeNode nodo,String busqueda) {
           System.out.println( nodo.toString());           
            if (nodo.getChildCount() >= 0) {
                for (Enumeration e=nodo.children(); e.hasMoreElements(); ) {                                        
                    TreeNode n = (TreeNode)e.nextElement();
                    DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)e.nextElement();                                       
                    String user = n1.toString(); 
                    if(user.equals(busqueda)){
                        int i = n1.getIndex(n1);
                        JTreePrivileges.setSelectionRow(i);
                    }                    
                }
            }
    }
    
    public DefaultMutableTreeNode searchNode(String nodeStr){
        DefaultMutableTreeNode node = null;
        
        //Get the enumeration
        //Enumeration e = m_rootNode.breadthFirstEnumeration();
        Enumeration e = top.breadthFirstEnumeration();
        //iterate through the enumeration
        while(e.hasMoreElements())
        {
            //get the node
            node = (DefaultMutableTreeNode)e.nextElement();
            
            //match the string with the user-object of the node
            String  nodeOb = node.getUserObject().toString();
            if(nodeStr.equals(nodeOb))
            {
                //tree node with string found
                return node;                         
            }
        }
        
        //tree node with string node found return null
        return null;
    }
    
    private String getUserFind(String codigo) {
        String sql="SELECT cod_usuario ||'-'|| nombre AS usuario FROM usuario WHERE cod_usuario = " + codigo;
        String getusuario = null;
        ResultSet rs = DBManager.ejecutarDSL(sql);
        try {
            if(rs.next()){
                  getusuario = rs.getString("usuario");
            }else{
                getusuario="desconocido";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "["+getusuario.trim()+"]";
    }
    
    private boolean yaExiste(String user){
        boolean existe = false;
        StatementManager sm = new StatementManager();
        sm.TheSql = "SELECT USENAME FROM PG_USER WHERE usename = '" + user.toLowerCase() + "'";
        sm.EjecutarSql();
        try{
            if(sm.TheResultSet.next()){
                existe = true;
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
        return existe;
    }
    
    private void creacionUserDB(String user, String passwd){
        try{
            String createUser = "CREATE ROLE " + user + " LOGIN ENCRYPTED PASSWORD '" + passwd + "' "
                              + "SUPERUSER INHERIT CREATEDB CREATEROLE";
            System.out.println("CREATE USER: " + createUser);
            int create = DBManager.ejecutarDML(createUser);
            System.out.println("RESULT DE LA CREACION DE NUEVO USER: " + create);
        }catch(Exception sqlex){
            sqlex.printStackTrace();
        }
        
        
    }
    
    private void alterUserDB(String user, String passwd){
        try{
            String alterUser = "ALTER USER " + user + " WITH PASSWORD '" + passwd + "'" ;
            System.out.println("ALTER USER: " + alterUser);
            int alter = DBManager.ejecutarDML(alterUser);
            System.out.println("RESULT DEL ALTER USER: " + alter);
        }catch(Exception ex){
            ex.printStackTrace();
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

        jMenuContext = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTreePrivileges = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jBBuscarUsuario = new javax.swing.JButton();
        jBReCatastrarUsuariosActivos = new javax.swing.JButton();
        jBPrivilegioUsuario = new javax.swing.JButton();
        jBRefresh = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Administración de Perfil de Usuario");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("ADMINISTRACIÓN DE PERFIL USUARIOS");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        JTreePrivileges.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTreePrivilegesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JTreePrivileges);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(102, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBBuscarUsuario.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBBuscarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscarUsuario.setText("Buscar Usuario");
        jBBuscarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarUsuarioActionPerformed(evt);
            }
        });

        jBReCatastrarUsuariosActivos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBReCatastrarUsuariosActivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/basedatos24.png"))); // NOI18N
        jBReCatastrarUsuariosActivos.setText("Re catastrar Usuarios activos");
        jBReCatastrarUsuariosActivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBReCatastrarUsuariosActivosActionPerformed(evt);
            }
        });

        jBPrivilegioUsuario.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBPrivilegioUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/grupo_user24.png"))); // NOI18N
        jBPrivilegioUsuario.setText("Privilegios de usuarios ");
        jBPrivilegioUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPrivilegioUsuarioActionPerformed(evt);
            }
        });

        jBRefresh.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/refresh24.png"))); // NOI18N
        jBRefresh.setText("Refresh");
        jBRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshActionPerformed(evt);
            }
        });

        jBSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBSalir.setText("Salir del Gestor");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBBuscarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBReCatastrarUsuariosActivos)
                    .addComponent(jBPrivilegioUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscarUsuario, jBPrivilegioUsuario, jBReCatastrarUsuariosActivos, jBRefresh, jBSalir});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBBuscarUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBReCatastrarUsuariosActivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBPrivilegioUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscarUsuario, jBPrivilegioUsuario, jBReCatastrarUsuariosActivos, jBRefresh, jBSalir});

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void JTreePrivilegesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTreePrivilegesMouseClicked
        if(evt.getButton() == 2 || evt.getButton() == 3 ){
            jMenuContext.show(evt.getComponent(),evt.getX(),evt.getY());              
        }
    }//GEN-LAST:event_JTreePrivilegesMouseClicked

    private void jBBuscarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarUsuarioActionPerformed
        try {
                DlgConsultas usuario = new DlgConsultas(new JFrame(), true);
                usuario.pack();
                usuario.setTitle("ATOMSystems|Main - Consulta de Ciudad");
                usuario.dConsultas("usuario", "nombre", "cod_usuario", "nombre", "activo", null, "Codigo", "Nombre", "Activo", null);
                usuario.setText(txtcoduser);
                usuario.tfDescripcionBusqueda.setText("%");
                usuario.tfDescripcionBusqueda.selectAll();
                usuario.setVisible(true);
                String usuarioABuscar = getUserFind(txtcoduser.getText().replace(",", ""));
                
                DefaultMutableTreeNode node = searchNode(usuarioABuscar);
                
                if(node != null){
                    TreeNode[] nodes = treeModel.getPathToRoot(node);
                    TreePath path = new TreePath(nodes);
                    JTreePrivileges.scrollPathToVisible(path);
                    JTreePrivileges.setSelectionPath(path);
                }else{
                    JOptionPane.showMessageDialog(this, "Error en la búsqueda .. Usuario : " + usuarioABuscar + " no encontrado !!!" , 
                                                  "Atención", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
    }//GEN-LAST:event_jBBuscarUsuarioActionPerformed

    private void jBReCatastrarUsuariosActivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBReCatastrarUsuariosActivosActionPerformed
        ResultSet rs = null;
        String sql = "SELECT cod_usuario, alias, clave_plana FROM usuario WHERE activo = 'S'";
        
        rs = DBManager.ejecutarDSL(sql);
        try{
            while(rs.next()){
                String nombreU = rs.getString("alias");
                String claveU = rs.getString("clave_plana");
                if(yaExiste(nombreU)){
                    alterUserDB(nombreU, claveU);
                }else{
                    creacionUserDB(nombreU, claveU);
                }
            }
            DBManager.conn.commit();
            JOptionPane.showMessageDialog(this, "Proceso Finalizado!","Mensaje", JOptionPane.INFORMATION_MESSAGE);
            rs.close();
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
    }//GEN-LAST:event_jBReCatastrarUsuariosActivosActionPerformed

    private void jBPrivilegioUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPrivilegioUsuarioActionPerformed
        PrivilegioUsuario privilegio = new PrivilegioUsuario(new JFrame(), true);
        privilegio.pack();
        privilegio.setVisible(true);
    }//GEN-LAST:event_jBPrivilegioUsuarioActionPerformed

    private void jBRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshActionPerformed
        try{
            DataNode1 = rsDataNode();
            DataNode2 = rsDataNode();
            DataNode3 = rsDataNode();
            DataNode4 = rsDataNode();
            top.removeAllChildren();
            createNodes(top, "NU");
            treeModel = (DefaultTreeModel) JTreePrivileges.getModel();
            treeModel.nodeStructureChanged(top);;
            closeStatements();
        }catch(NullPointerException nullex){}
    }//GEN-LAST:event_jBRefreshActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree JTreePrivileges;
    private javax.swing.JButton jBBuscarUsuario;
    private javax.swing.JButton jBPrivilegioUsuario;
    private javax.swing.JButton jBReCatastrarUsuariosActivos;
    private javax.swing.JButton jBRefresh;
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPopupMenu jMenuContext;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
