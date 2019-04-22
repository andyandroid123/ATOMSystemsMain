/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import utiles.DBManager;

/**
 *
 * @author ANDRES
 */
public class PrivilegioUsuario extends javax.swing.JDialog {

    String cod_modulo = "0";
    String cod_grupo = "0";
    TListJMenu model = new TListJMenu();
    TListJMenu modelAsignation = new TListJMenu();
    int hayasignacion = 0;
    
    public PrivilegioUsuario(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargarBoxGrupo();
        cargarBoxModulo();
        Conf_List();
        JListAsig.setModel(modelAsignation);
        recuperarAsignaciones(cod_grupo, cod_modulo);
    }

    private void cargarBoxGrupo(){
         ResultSet rs = null;
         rs = DBManager.ejecutarDSL("SELECT cod_grupo_usuario, descripcion FROM grupo_usuario ORDER BY descripcion");
         Jbox ProveeDatos = new Jbox(rs);
         try{             
              for(Structura stu : ProveeDatos.getFuente()){
                     JBoxGrupos.addItem(stu);
                 }
         }catch(Exception e){}finally{
             DBManager.CerrarStatements();
         }
         
     }
    
    private void cargarBoxModulo(){
         ResultSet rs = null;
         rs = DBManager.ejecutarDSL("SELECT cod_modulo, descripcion FROM modulo ORDER BY descripcion");
         Jbox ProveeDatos = new Jbox(rs);
         try{             
              for(Structura stu : ProveeDatos.getFuente()){
                     JBoxModulos.addItem(stu);
                 }
         }catch(Exception e){}finally{
             DBManager.CerrarStatements();
         }
         
     }
    
    private void Conf_List() {
        JListPG.setCellRenderer(new MyListRenderer());
        JListAsig.setCellRenderer(new MyListRenderer());
    }
    
    private void recuperarAsignaciones(String cod_grupo,String cod_modulo) {
         ResultSet rs = null;
         if(cod_modulo == null){
             cod_modulo="1";
         }
         //String sql ="SELECT B.COD_MENU,B.DESCRIPCION,A.COD_MODULO,A.COD_ITEM FROM PERFIL_GRUPO A INNER JOIN MODULO_MENU B ON A.COD_MENU = B.COD_MENU WHERE A.COD_GRUPO_USUARIO ="+cod_grupo+" AND A.COD_MODULO = "+cod_modulo;
         String sql = "SELECT a.cod_menu, a.cod_modulo, a.cod_item "
                    + "FROM perfil_grupo a "
                    + "WHERE a.cod_grupo_usuario = " + cod_grupo + " "
                    + "AND a.cod_modulo = " + cod_modulo;
         System.out.println("SQL DE LAS ASIGNACIONES: " + sql);
         rs = DBManager.ejecutarDSL(sql);         
         modelAsignation.removeAll();
         ItemList itenesR = new ItemList(rs, false);         
         try{             
              for(StructuraList stu : itenesR.getFuente()){
                     modelAsignation.addElement(stu);
                 }
          JListAsig.setModel(modelAsignation);
         }catch(Exception e){}finally{
             DBManager.CerrarStatements();
         }
         
    }
    
    private void cargarListMenus(String cod_modulo) {
         ResultSet rs = null;
         //String sql ="SELECT COD_MENU,DESCRIPCION,COD_MODULO, 0 AS ITEM FROM MODULO_MENU WHERE COD_MODULO ="+cod_modulo+" ORDER BY DESCRIPCION";
         String sql ="SELECT cod_menu,'>> '||descripcion, cod_modulo, 0 AS cod_item "
                   + "FROM modulo_menu "
                   + "WHERE cod_modulo = " + cod_modulo + " "
                   + "UNION ALL SELECT cod_menu, descripcion, cod_modulo, cod_item "
                   + "FROM modulo_menuitem "
                   + "WHERE cod_modulo = " + cod_modulo + " ORDER BY 1";
         rs = DBManager.ejecutarDSL(sql);
         model.removeAll();
         ItemList itenes = new ItemList(rs,true);         
         try{             
              for(StructuraList stu : itenes.getFuente()){
                     model.addElement(stu);
                 }
          JListPG.setModel(model);
         }catch(Exception e){}finally{
             DBManager.CerrarStatements();
         }
    }
    
    private void grabarDatos(){
        eliminarExistentes(cod_grupo, cod_modulo);
        deletarModuloPU(cod_modulo);
        
        String sql = null;
        int fila = 0;
        
        String usuario = "1";
        hayasignacion = modelAsignation.getSize();
        if(hayasignacion != 0){
            for(int i = 0; i < modelAsignation.getSize(); i++){
                sql = "";
                StructuraList strucsel = (StructuraList) modelAsignation.getElementAt(i);
                String cod = String.valueOf(strucsel.getCodigo());
                String menuI = String.valueOf(strucsel.getCodmenu());
                sql = "INSERT INTO perfil_grupo VALUES ('" + cod_grupo + "', '" + cod_modulo + "', '" + usuario + "', now(), '" + cod + "', '" + menuI + "')";
                System.out.println("INSERT EN PERFIL GRUPO: " + sql);
                fila = DBManager.ejecutarDML(sql);
                try{
                    DBManager.conn.commit();
                }catch(SQLException ex){}
                if(fila == 0){
                    break;
                }
            }
        }else{
            deletarModuloPU(cod_modulo);
        }
        
        if(hayasignacion != 0){
            if(!heredarPerfilUsuario(cod_grupo)){
                heredarModulos(cod_grupo);
            }
        }
    }
    
    private void heredarModulos(String cod_grupo1) {
        int c=0;
        String usuauxi="";
        ResultSet rs = null;       
        boolean result = false;
        int hayRegistros = 0;
        ResultSet rsitem = null;
        String sql = "SELECT u.cod_usuario, pg.cod_modulo, pg.cod_menu, pg.cod_item "
                   + "FROM usuario u, perfil_grupo pg, perfil_usuario pu "
                   + "WHERE u.cod_usuario = pu.cod_usuario_perfil "
                   + "AND u.cod_grupo_usuario = pg.cod_grupo_usuario "
                   + "AND pg.cod_grupo_usuario = " + cod_grupo1 + " "
                   + "ORDER BY u.cod_usuario";
        String sqlItem = "SELECT cod_item FROM modulo_menuitem WHERE cod_menu=";
        String usuario = "1";
        System.out.println("HEREDAR MODULOS: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        
        
        try {           
            while(rs.next()){
                c++;
                hayRegistros = 1;                
                String usu = String.valueOf(rs.getInt("cod_usuario"));
                if(!usu.equals(usuauxi)){
                    eliminarUsuarioP(usu);
                    usuauxi = usu;
                }
                //ElimarExis(usu,menu);
                String mod = String.valueOf(rs.getInt("cod_modulo"));
                String menu = String.valueOf(rs.getInt("cod_menu"));
                
                String mitem = String.valueOf(rs.getInt("cod_item"));
                if(rs.getInt("cod_item") == 0){
                   //ElimarExisM(usu,menu);    
                rsitem = DBManager.ejecutarDSL(sqlItem+menu);
                while(rsitem.next()){
                    String item = String.valueOf(rsitem.getInt("cod_item"));
                    String sqlpu ="INSERT INTO perfil_usuario VALUES('" + usu + "', '" + mod + "', '" +usuario + "', now(), '" + menu + "', '" + item + "')";
                    int fila = DBManager.ejecutarDML(sqlpu);
                }
                }else{
                    //ElimarExisMI(usu,menu,mitem);
                    if(cod_modulo.equals(mod)){
                        String sqlput ="INSERT INTO perfil_usuario VALUES('" + usu + "', '" + mod + "', '" + usuario + "', now(), '" + menu + "', '" + mitem + "')";
                        int fila = DBManager.ejecutarDML(sqlput);
                            try{
                                DBManager.conn.commit();
                            }catch(SQLException ex){}
                        }
                    }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
             DBManager.CerrarStatements();
         }
        if (hayRegistros == 1){
            result = true;
        }else{
            result = false;
        }
        //return result;        
        return;
    }
    
    private void eliminarUsuarioP(String usu){
        String del = "DELETE FROM perfil_usuario a WHERE a.cod_usuario_perfil = " + usu + " AND a.cod_modulo = " + cod_modulo;
        int fila = DBManager.ejecutarDML(del);
    }
    
    private boolean heredarPerfilUsuario(String CodG) {
        ResultSet rs = null;       
        boolean result = false;
        int hayRegistros = 0;
        ResultSet rsitem = null;
        String sql = "SELECT DISTINCT c.cod_usuario, a.cod_modulo, a.cod_menu "
                   + "FROM perfil_grupo a, perfil_usuario b, usuario c "
                   + "WHERE a.cod_grupo_usuario  = c.cod_grupo_usuario "
                   + "AND b.cod_usuario_perfil = c.cod_usuario "
                   + "AND a.cod_modulo = b.cod_modulo "
                   + "AND a.cod_menu NOT IN(SELECT cod_menu FROM perfil_usuario)";
        String sqlItem = "SELECT cod_item FROM modulo_menuitem WHERE cod_menu=";
        String usuario = "1";
       
        rs = DBManager.ejecutarDSL(sql);
        
        try {           
            while(rs.next()){
                hayRegistros = 1;
                String usu = String.valueOf(rs.getInt("cod_usuario"));
                String mod = String.valueOf(rs.getInt("cod_modulo"));
                String menu = String.valueOf(rs.getInt("cod_menu"));
                rsitem = DBManager.ejecutarDSL(sqlItem+menu);
                while(rsitem.next()){
                    String item = String.valueOf(rsitem.getInt("cod_item"));
                    String sqlpu ="INSERT INTO perfil_usuario VALUES('" + usu + "', '" + mod + "', '" + usuario + "', now(), '" + menu + "', '" + item + "')";
                    int fila = DBManager.ejecutarDML(sqlpu);
                }                
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
             DBManager.CerrarStatements();
         }
        if (hayRegistros == 1){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
    
    private void deletarModuloPU(String cod_modulo){
        String del = "DELETE FROM perfil_usuario "
                   + "WHERE cod_modulo = " + cod_modulo + " "
                   + "AND cod_usuario_perfil IN(SELECT a.cod_usuario_perfil "
                                             + "FROM perfil_usuario a, usuario b "
                                             + "WHERE a.cod_usuario_perfil = b.cod_usuario "
                                             + "AND a.cod_modulo = " + cod_modulo + " "
                                             + "AND b.cod_grupo_usuario = " + cod_grupo + ")";
        int fila = DBManager.ejecutarDML(del);  
        try{
            DBManager.conn.commit();
        }catch(SQLException ex){}
    }
    
    private void eliminarExistentes(String cod_grupo, String cod_modulo){
        String sql_perfil_grupo = "DELETE FROM perfil_grupo WHERE cod_grupo_usuario = " + cod_grupo + " AND cod_modulo = " + cod_modulo;
        String sql_usuario = "DELETE FROM perfil_usuario WHERE cod_usuario_perfil = " + cod_grupo + " AND cod_modulo = " + cod_modulo;
        System.out.println("DELETE PERFIL_USUARIO: " + sql_usuario);
        System.out.println("DELETE PERFIL_GRUPO: " + sql_perfil_grupo);
        DBManager.ejecutarDML(sql_perfil_grupo);
        DBManager.ejecutarDML(sql_usuario);
        try{
            DBManager.conn.commit();
        }catch(SQLException ex){}
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
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        JBoxGrupos = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        JBoxModulos = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        JListAsig = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        JListPG = new javax.swing.JList();
        jBAceptar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Privilegios de Usuarios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("MANTENIMIENTO DE PRIVILEGIOS ");

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

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Grupo:");

        JBoxGrupos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBoxGruposActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Módulo:");

        JBoxModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBoxModulosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBoxModulos, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(JBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(JBoxModulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JListAsig.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JListAsig.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JListAsigMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JListAsig);

        JListPG.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JListPG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JListPGMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(JListPG);

        jBAceptar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/check24.png"))); // NOI18N
        jBAceptar.setText("Aceptar");
        jBAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAceptarActionPerformed(evt);
            }
        });

        jBSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBSalir))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBAceptar, jBSalir});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBAceptar)
                    .addComponent(jBSalir))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBAceptar, jBSalir});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        try{
            DBManager.conn.rollback();
        }catch(SQLException sqlex){}
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void JBoxGruposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBoxGruposActionPerformed
        JComboBox cbxaux = (JComboBox)evt.getSource();
        Structura strucselecionada = (Structura)cbxaux.getSelectedItem();
        cod_grupo = String.valueOf(strucselecionada.getCodigo());
        recuperarAsignaciones(cod_grupo, cod_modulo);
    }//GEN-LAST:event_JBoxGruposActionPerformed

    private void JBoxModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBoxModulosActionPerformed
        JComboBox cbxaux = (JComboBox)evt.getSource();
        Structura strucselecionada = (Structura)cbxaux.getSelectedItem();
        cod_modulo = String.valueOf(strucselecionada.getCodigo());
        cargarListMenus(cod_modulo);
        recuperarAsignaciones(cod_grupo,cod_modulo);
    }//GEN-LAST:event_JBoxModulosActionPerformed

    private void JListAsigMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JListAsigMouseClicked
        if(evt.getClickCount() == 2){
           int index = JListAsig.getSelectedIndex();                                 
           modelAsignation.removeElement(index);
           JListAsig.setModel(modelAsignation);
      }
    }//GEN-LAST:event_JListAsigMouseClicked

    private void JListPGMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JListPGMouseClicked
        if(evt.getClickCount() == 2){
            StructuraList strucsel = (StructuraList) JListPG.getSelectedValue();
            int cod = strucsel.getCodigo();
            String des = strucsel.getDescripcion();
            int cod1 = strucsel.getCodmodulo();
            int cod2 = strucsel.getCodmenu();
            if(cod2 != 0){
                NewAsignationItem newItem1 = new NewAsignationItem(cod, des, cod1, cod2);
                for(StructuraList stu : newItem1.getFuente()){
                    modelAsignation.addElement(stu);
                 }
                JListAsig.setModel(modelAsignation);        
           }else{
                JOptionPane.showMessageDialog(this, "Debe Seleccionar Modulo MenuItem!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_JListPGMouseClicked

    private void jBAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAceptarActionPerformed
        grabarDatos();
        try{
            DBManager.conn.commit();
            JOptionPane.showMessageDialog(this, "Perfil para el grupo agregado correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
        }catch(SQLException sqlex){
            JOptionPane.showMessageDialog(this, "Error grabando perfil de usuario.", "Mensaje", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jBAceptarActionPerformed

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
            java.util.logging.Logger.getLogger(PrivilegioUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrivilegioUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrivilegioUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrivilegioUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PrivilegioUsuario dialog = new PrivilegioUsuario(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox JBoxGrupos;
    private javax.swing.JComboBox JBoxModulos;
    private javax.swing.JList JListAsig;
    private javax.swing.JList JListPG;
    private javax.swing.JButton jBAceptar;
    private javax.swing.JButton jBSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
