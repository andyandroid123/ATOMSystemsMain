/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import ModRRHH.GenericInfoAuditoria;
import ModRegistros.usuarios.AdminUser;
import ModRegistros.usuarios.Jbox;
import ModRegistros.usuarios.Structura;
import beans.UsuarioBean;
import controls.UsuarioCtrl;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.Cifrador;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class Usuarios1 extends javax.swing.JDialog {

    private static String usuario, fecha;
    static boolean esNuevo, actualizar;
    Cifrador cifrar;
    private ResultSet resultUsuario = null;
    private boolean check = false;
    
    public Usuarios1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cerrarVentana();
        estadoComponentes(false);
        setConfigCampos();
        cargaUsuario();
    }
        
    private void configBotonesModificar(){
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(true);
        jBCambiarGrupoUsuario.setEnabled(true);
    }
    
    private void cargaUsuario() {
        UsuarioCtrl bCtrl = new UsuarioCtrl();
        int max = bCtrl.buscaMaxCodUsuario();
        if (max > 0) {
            getUsuario(max);
        } else {
            jBModificar.setEnabled(false);
        }
    }
    
    private void getUsuario(int codigo){
        UsuarioCtrl ctrl = new UsuarioCtrl();
        UsuarioBean bean = ctrl.buscaUsuarioCodigo(codigo);
        if(bean != null){
            jTFCodUsuario.setText(String.valueOf(bean.getCodUsuario()));
            jTFCodUsuarioPrivilegios.setText(String.valueOf(bean.getCodUsuario()));
            jTFNombreUsuario.setText(bean.getNombre());
            jLNombreUsuarioPrivilegios.setText(bean.getAlias());
            jTFAlias.setText(bean.getAlias());
            jPClave.setText(bean.getClavePlana());
            usuario = bean.getCodUsuario() + " - " + getNombreUsuario(String.valueOf(bean.getCodUsuario()));
            fecha = bean.getFecVigencia();
            String activo = bean.getActivo();
            String esCajero = bean.getEsCajero();
            String esFiscal = bean.getEsFiscal();
            int codGrupoUsuario = bean.getCodGrupoUsuario();
            jCBGrupoUsuarios.removeAllItems();
            jCBGrupoUsuarios.addItem(getDescripcionGrupoUsuario(codGrupoUsuario));
            if(activo.equals("S")){
                jRBActivo.setSelected(true);
                jRBInactivo.setSelected(false);
            }else{
                jRBActivo.setSelected(false);
                jRBInactivo.setSelected(true);
            }
            
            if(esCajero.equals("S")){
                jChBEsCajero.setSelected(true);
            }else{
                jChBEsCajero.setSelected(false);
            }
            
            if(esFiscal.equals("S")){
                jChBEsFiscal.setSelected(true);
            }else{
                jChBEsFiscal.setSelected(false);
            }
        }
    }
    
    private String getNombreUsuario(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            UsuarioCtrl ctrl = new UsuarioCtrl();
            result = ctrl.getNombreUsuario(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private void estadoComponentes(boolean estado){
        jTFNombreUsuario.setEnabled(estado);
        jTFAlias.setEnabled(estado);
        jPClave.setEnabled(estado);
        jRBActivo.setEnabled(estado);
        jRBInactivo.setEnabled(estado);
        jChBEsCajero.setEnabled(estado);
        jChBEsFiscal.setEnabled(estado);
        jCBGrupoUsuarios.setEnabled(estado);
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){}
        });
    }
    
    private boolean verificarCampos(){
        boolean result = true;
        if(jTFNombreUsuario.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe completar el campo NOMBRE!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFNombreUsuario.requestFocus();
            result = false;
        }
        
        if(jTFAlias.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe completar el campo ALIAS!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFAlias.requestFocus();
            result = false;
        }
        
        if(jPClave.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Debe completar el campo CLAVE!", "Error", JOptionPane.WARNING_MESSAGE);
            jPClave.requestFocus();
            result = false;
        }
        return result;
    }
    
    private void configBotonesGrabado(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
    }
    
    private void grabarUsuario(){
        boolean result = true;
        String sqlUsuarios;
        String vEsCajero = jChBEsCajero.isSelected() ? "S" : "N";
        String vEsFiscal = jChBEsFiscal.isSelected() ? "S" : "N";
        String vActivo = jRBActivo.isSelected() ? "S": "N";
        String clave = cifrar.encriptar("0xab", jPClave.getText());
        String vCodUsuario = jTFCodUsuario.getText().trim();
        String vNombreUsuario = jTFNombreUsuario.getText().trim();
        String vAlias = jTFAlias.getText().trim();
        int vCodUsuarioLogueado = FormMain.codUsuario;
        String vClavePlana = jPClave.getText().trim();
        
        if(esNuevo){
            sqlUsuarios = "INSERT INTO usuario(cod_usuario, nombre, clave, alias, es_cajero, es_fiscal, activo, fec_catastro, fec_vigencia, cod_perfil, "
                        + "codigo_usuario, cod_grupo_usuario, fec_vence_clave, clave_plana, locales_habilitados) VALUES ("
                        + "" + vCodUsuario + ", '"
                        + "" + vNombreUsuario + "', '"
                        + "" + clave + "', '"
                        + "" + vAlias + "', '"
                        + "" + vEsCajero + "', '"
                        + "" + vEsFiscal + "', '"
                        + "" + vActivo + "', '"
                        + "now()', 'now()', 1, "
                        + "" + vCodUsuarioLogueado + ", " + getCodigoGrupoUsuario(jCBGrupoUsuarios.getSelectedItem().toString().trim()) + ", "
                        + "" + null + ", '"
                        + "" + vClavePlana + "', 1)";
            System.out.println("INSERT USER: " + sqlUsuarios);
            actualizar = false;
        }else{
            sqlUsuarios = "UPDATE usuario SET nombre = '" + vNombreUsuario + "', "
                        + "clave = '" + clave + "', "
                        + "alias = '" + vAlias + "', "
                        + "es_cajero = '" + vEsCajero + "', "
                        + "es_fiscal = '" + vEsFiscal + "', "
                        + "activo = '" + vActivo + "', "
                        + "fec_vigencia = 'now()', "
                        + "codigo_usuario = " + vCodUsuarioLogueado + ", "
                        + "clave_plana = '" + vClavePlana + "', "
                        + "cod_grupo_usuario = " + getCodigoGrupoUsuario(jCBGrupoUsuarios.getSelectedItem().toString().trim()) + " "
                        + "WHERE cod_usuario = " + vCodUsuario;
            System.out.println("UPDATE USER: " + sqlUsuarios);
            actualizar = true;
        }
        
        try{
            int row = DBManager.ejecutarDML(sqlUsuarios);
            grabarPerfilUsuario(getCodigoGrupoUsuario(jCBGrupoUsuarios.getSelectedItem().toString()), vCodUsuario);
            if(row != 0){
                if(actualizar){
                    result = alterarUserBD(vAlias, vClavePlana);
                }else{
                    result = creacionUserDB(vAlias, vClavePlana);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        
        try{
            if(result)
            {
                DBManager.conn.commit();
                JOptionPane.showMessageDialog(this, "Exito: \n Datos Registrados!", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }else
            {
                DBManager.conn.rollback();
                JOptionPane.showMessageDialog(this, "ATENCION: \n Error al Grabar Datos \n Operación Cancelada!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            result = false;
        }
    }
    
    private void setConfigCampos() {

        jTFNombreUsuario.setDocument(new MaxLength(40, "UPPER", "ALFA"));
        jTFAlias.setDocument(new MaxLength(10, "UPPER", "ALFA"));
        jPClave.setDocument(new MaxLength(10, "UPPER", "ALFA"));
        
        jTFNombreUsuario.addFocusListener(new Focus());
        jTFAlias.addFocusListener(new Focus());
        jPClave.addFocusListener(new Focus());
        jTFCodUsuarioPrivilegios.addFocusListener(new Focus());
    }
    
    private boolean creacionUserDB(String USER, String PASSW)
    {
        boolean result = true;
        try
        {
            String createUser = "CREATE ROLE " + USER.toUpperCase() + " LOGIN ENCRYPTED PASSWORD '" + PASSW + "' "
                              + "SUPERUSER INHERIT CREATEDB CREATEROLE";
            int create = DBManager.ejecutarDML(createUser);
            System.out.println("CREAR USUARIO BD: " + createUser);
            System.out.println("RESULT DEL CREATE NUEVO USUARIO: " + create);
                
        }catch(Exception ex)
        {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            result = false;
        }
        return result;
    }
    
    private boolean alterarUserBD(String ALIAS, String PASSW)
    {
        boolean result = true;
        try
        {
            String sqlAlter = "ALTER USER " + ALIAS + " WITH PASSWORD '" + PASSW + "'";
            int alter = DBManager.ejecutarDML(sqlAlter);
            System.out.println("ALTER USER: " + sqlAlter);
            System.out.println("ALTER USER EN ABM USUARIOS: " + alter);
            /*if(FormMain.baseDato == 1)
            {
                String sqlAlter = "alter user " + ALIAS + " identified by " + PASSW;
                String Grant = "GRANT RVITAL TO " + ALIAS;
                DBManager.ejecutarDML(sqlAlter);
                DBManager.ejecutarDML(Grant);
            }else
            {
                String sqlAlter = "alter user " + ALIAS + " with password '" + PASSW + "'";
                DBManager.ejecutarDML(sqlAlter);
                System.out.println(sqlAlter);
            }*/
        }catch(Exception ex)
        {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            result = false;
        }
        return result;
    }
    
    private void cargarComboGrupoUsuario(){
        jCBGrupoUsuarios.removeAllItems();
        ResultSet rs = null;
        String sql = "SELECT a.cod_grupo_usuario, a.descripcion "
                   + "FROM grupo_usuario a, perfil_grupo b "
                   + "WHERE a.cod_grupo_usuario = b.cod_grupo_usuario "
                   + "GROUP BY a.cod_grupo_usuario, a.descripcion "
                   + "ORDER BY cod_grupo_usuario";
        System.out.println("GRUPO DE USUARIOS: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        Jbox proveeDatos = new Jbox(rs);
        try{
            for(Structura stru : proveeDatos.getFuente()){
                jCBGrupoUsuarios.addItem(stru.toString());
            }
        }catch(Exception ex){}finally{DBManager.CerrarStatements();}
    }
    
    private void grabarPerfilUsuario(String cod_grupo_usuario, String cod_usuario){
        borrarPerfilAnterior(cod_usuario);
        if(!check){
            ResultSet rs = null;
            String sql = "SELECT a.cod_modulo, a.cod_menu, d.cod_item "
                       + "FROM perfil_grupo a "
                       + "INNER JOIN modulo b "
                       + "ON a.cod_modulo = b.cod_modulo "
                       + "INNER JOIN modulo_menu c "
                       + "ON a.cod_menu = c.cod_menu "
                       + "INNER JOIN modulo_menuitem d "
                       + "ON a.cod_menu = d.cod_menu "
                       + "WHERE a.cod_grupo_usuario = " + cod_grupo_usuario + " "
                       + "AND a.cod_item = 0 "
                       + "UNION ALL "
                       + "SELECT a.cod_modulo, a.cod_menu, a.cod_item "
                       + "FROM perfil_grupo a "
                       + "WHERE a.cod_grupo_usuario = " + cod_grupo_usuario + " "
                       + "AND a.cod_item <> 0";
            rs = DBManager.ejecutarDSL(sql);
            try{
                while(rs.next()){
                    String COD_MODULO = rs.getString("cod_modulo");
                    String COD_MENU = rs.getString("cod_menu");
                    String COD_ITEM = rs.getString("cod_item");
                    String sqlInsertUser = "INSERT INTO perfil_usuario VALUES (" + cod_usuario + ", " + COD_MODULO + ", 1, 'now()', "
                                         + COD_MENU + ", " + COD_ITEM + ")";
                    int row = DBManager.ejecutarDML(sqlInsertUser);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }finally{
                DBManager.CerrarStatements();
            }
        }else{
            check = false;
        }
    }
    
    private void borrarPerfilAnterior(String cod_usuario){
        String sql = "DELETE FROM perfil_usuario WHERE cod_usuario_perfil = " + cod_usuario;
        int row = DBManager.ejecutarDML(sql);
    }
    
    private String getCodigoGrupoUsuario(String descripcion){
        String result = "";
        ResultSet rs = null;
        String sql = "SELECT cod_grupo_usuario FROM grupo_usuario WHERE descripcion LIKE '%" + descripcion + "%'";
        System.out.println("RESULTADO DEL CODIGO DE GRUPO DE USUARIO: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    result = String.valueOf(rs.getInt("cod_grupo_usuario"));
                }else{
                    result = "0";
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private String getDescripcionGrupoUsuario(int codigo){
        String result = "";
        ResultSet rs = null;
        String sql = "SELECT descripcion FROM grupo_usuario WHERE cod_grupo_usuario = '" + codigo + "'";
        System.out.println("GRUPO DE USUARIO: " + sql);
        rs = DBManager.ejecutarDSL(sql);
        try{
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("descripcion");
                }
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }finally{
            DBManager.CerrarStatements();
        }
        System.out.println("RESULT: " + result);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFNombreUsuario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFAlias = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jRBActivo = new javax.swing.JRadioButton();
        jRBInactivo = new javax.swing.JRadioButton();
        jPClave = new javax.swing.JPasswordField();
        jChBEsCajero = new javax.swing.JCheckBox();
        jChBEsFiscal = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jCBGrupoUsuarios = new javax.swing.JComboBox<>();
        jBCambiarGrupoUsuario = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBInformacion = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jBPrivilegiosUsuario = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTFCodUsuarioPrivilegios = new javax.swing.JTextField();
        jLNombreUsuarioPrivilegios = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Usuarios del Sistema");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("REGISTRO DE USUARIOS DEL SISTEMA");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Código:");

        jTFCodUsuario.setEditable(false);
        jTFCodUsuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Nombre:");

        jTFNombreUsuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNombreUsuarioFocusGained(evt);
            }
        });
        jTFNombreUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNombreUsuarioKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Alias:");

        jTFAlias.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFAlias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFAliasFocusGained(evt);
            }
        });
        jTFAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFAliasKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Clave:");

        jRBActivo.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBActivo);
        jRBActivo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRBActivo.setSelected(true);
        jRBActivo.setText("Activo");

        jRBInactivo.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup1.add(jRBInactivo);
        jRBInactivo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRBInactivo.setText("Inactivo");

        jPClave.setText("jPasswordField1");
        jPClave.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPClaveFocusGained(evt);
            }
        });
        jPClave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPClaveKeyPressed(evt);
            }
        });

        jChBEsCajero.setBackground(new java.awt.Color(204, 255, 204));
        jChBEsCajero.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBEsCajero.setText("Es Cajero");

        jChBEsFiscal.setBackground(new java.awt.Color(204, 255, 204));
        jChBEsFiscal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jChBEsFiscal.setText("Es Fiscal");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Grupo:");

        jCBGrupoUsuarios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jBCambiarGrupoUsuario.setText("Cambiar Grupo");
        jBCambiarGrupoUsuario.setEnabled(false);
        jBCambiarGrupoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCambiarGrupoUsuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTFCodUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPClave)
                                    .addComponent(jTFAlias)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jChBEsCajero)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jChBEsFiscal)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jRBActivo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRBInactivo)
                                        .addGap(13, 13, 13))))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBGrupoUsuarios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCambiarGrupoUsuario)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jPClave, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChBEsCajero)
                    .addComponent(jChBEsFiscal)
                    .addComponent(jRBInactivo)
                    .addComponent(jRBActivo))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jCBGrupoUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBCambiarGrupoUsuario))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
            }
        });

        jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png"))); // NOI18N
        jBModificar.setText("Modificar");
        jBModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBModificarActionPerformed(evt);
            }
        });

        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.setEnabled(false);
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        jBInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/informacion24.png"))); // NOI18N
        jBInformacion.setText("Info");
        jBInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInformacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBModificar)
                    .addComponent(jBCancelar)
                    .addComponent(jBBuscar)
                    .addComponent(jBSalir)
                    .addComponent(jBInformacion))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBPrivilegiosUsuario.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBPrivilegiosUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jBPrivilegiosUsuario.setText("Privilegios Usuario");
        jBPrivilegiosUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPrivilegiosUsuarioActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Usuario:");

        jTFCodUsuarioPrivilegios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodUsuarioPrivilegios.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFCodUsuarioPrivilegios.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodUsuarioPrivilegiosFocusGained(evt);
            }
        });
        jTFCodUsuarioPrivilegios.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodUsuarioPrivilegiosKeyPressed(evt);
            }
        });

        jLNombreUsuarioPrivilegios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreUsuarioPrivilegios.setForeground(new java.awt.Color(0, 153, 153));
        jLNombreUsuarioPrivilegios.setText("***");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Rol:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLNombreUsuarioPrivilegios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBPrivilegiosUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jTFCodUsuarioPrivilegios, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFCodUsuarioPrivilegios, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLNombreUsuarioPrivilegios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBPrivilegiosUsuario)
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        setSize(new java.awt.Dimension(659, 398));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInformacionActionPerformed
        GenericInfoAuditoria info = new GenericInfoAuditoria(new JFrame(), true, usuario, fecha, "Usuarios");
        info.setTitle("ATOMSystems|Main - Auditoria");
        info.pack();
        info.setVisible(true);
    }//GEN-LAST:event_jBInformacionActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        DlgConsultas usuario = new DlgConsultas(new JFrame(), true);
        usuario.pack();
        usuario.setTitle("ATOMSystems|Main - Consulta Usuarios");
        usuario.dConsultas("usuario", "nombre", "cod_usuario", "nombre", "alias", "activo", "Código", "Nombre", "Alias", "Activo");
        usuario.setText(jTFCodUsuario);
        usuario.tfDescripcionBusqueda.setText("%");
        usuario.setVisible(true);
        jCBGrupoUsuarios.removeAllItems();
        getUsuario(Integer.parseInt(jTFCodUsuario.getText()));
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        if(jBModificar.getText().equals("Modificar")){
            jBModificar.setText("Guardar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
            estadoComponentes(true);
            jCBGrupoUsuarios.setEnabled(false);
            configBotonesModificar();
            jTFNombreUsuario.requestFocus();
            esNuevo = false;
        }else
        if(jBModificar.getText().equals("Guardar")){
            jBModificar.setText("Modificar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png")));
            if(verificarCampos()){
                grabarUsuario();
            }
            estadoComponentes(false);
            configBotonesGrabado();
        }
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        if(jBModificar.getText().equals("Modificar")){
            jBModificar.setText("Guardar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
        }else{
            jBModificar.setText("Modificar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png")));
        }
        esNuevo = true;
        estadoComponentes(true);
        jTFCodUsuario.setText(getCodigoUsuario());
        limpiarCampos();
        configBotonesNuevo();
        cargarComboGrupoUsuario();
        jTFNombreUsuario.requestFocus();
        jChBEsCajero.setSelected(false);
        jChBEsFiscal.setSelected(false);
        jRBActivo.setSelected(true);
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        jBModificar.setText("Modificar");
        jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/modificar24.png")));
        configBotonesCancelar();
        estadoComponentes(false);
        cargaUsuario();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFNombreUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNombreUsuarioFocusGained
        jTFNombreUsuario.selectAll();
    }//GEN-LAST:event_jTFNombreUsuarioFocusGained

    private void jTFNombreUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNombreUsuarioKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFAlias.requestFocus();
        }
    }//GEN-LAST:event_jTFNombreUsuarioKeyPressed

    private void jTFAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFAliasKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jPClave.requestFocus();
        }
    }//GEN-LAST:event_jTFAliasKeyPressed

    private void jTFAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFAliasFocusGained
        jTFAlias.selectAll();
    }//GEN-LAST:event_jTFAliasFocusGained

    private void jPClaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPClaveKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBModificar.requestFocus();
            String clave_encriptada = Cifrador.encriptar("0xab", jPClave.getText());
            System.out.println("CLAVE ENCRIPTADA: " + clave_encriptada);
        }
    }//GEN-LAST:event_jPClaveKeyPressed

    private void jPClaveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPClaveFocusGained
        jPClave.selectAll();
    }//GEN-LAST:event_jPClaveFocusGained

    private void jBPrivilegiosUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPrivilegiosUsuarioActionPerformed
        AdminUser adminUser = new AdminUser(new JFrame(), true, jTFCodUsuarioPrivilegios.getText());
        adminUser.pack();
        adminUser.setVisible(true);
    }//GEN-LAST:event_jBPrivilegiosUsuarioActionPerformed

    private void jTFCodUsuarioPrivilegiosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodUsuarioPrivilegiosFocusGained
        jTFCodUsuarioPrivilegios.selectAll();
    }//GEN-LAST:event_jTFCodUsuarioPrivilegiosFocusGained

    private void jTFCodUsuarioPrivilegiosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodUsuarioPrivilegiosKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas usuario = new DlgConsultas(new JFrame(), true);
                usuario.pack();
                usuario.setTitle("ATOMSystems|Main - Consulta de Ciudad");
                usuario.dConsultas("usuario", "nombre", "cod_usuario", "nombre", "activo", null, "Codigo", "Nombre", "Activo", null);
                usuario.setText(jTFCodUsuarioPrivilegios);
                usuario.tfDescripcionBusqueda.setText("%");
                usuario.tfDescripcionBusqueda.selectAll();
                usuario.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jLNombreUsuarioPrivilegios.setText(getNombreUsuario(jTFCodUsuarioPrivilegios.getText()));
        }
    }//GEN-LAST:event_jTFCodUsuarioPrivilegiosKeyPressed

    private void jBCambiarGrupoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCambiarGrupoUsuarioActionPerformed
        jCBGrupoUsuarios.setEnabled(true);
        cargarComboGrupoUsuario();
    }//GEN-LAST:event_jBCambiarGrupoUsuarioActionPerformed

    private void configBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
        jBCambiarGrupoUsuario.setEnabled(false);
    }
    
    private String getCodigoUsuario(){
        UsuarioCtrl bCtrl = new UsuarioCtrl();
        int result = bCtrl.buscaMaxCodUsuario();
        result = result + 1;
        return String.valueOf(result);
    }
    
    private void configBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(true);
        jBCambiarGrupoUsuario.setEnabled(false);
    }
    
    private void limpiarCampos(){
        jTFAlias.setText("");
        jTFNombreUsuario.setText("");
        jPClave.setText("");
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
            java.util.logging.Logger.getLogger(Usuarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Usuarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Usuarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Usuarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuarios1 dialog = new Usuarios1(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBBuscar;
    private javax.swing.JButton jBCambiarGrupoUsuario;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBInformacion;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBPrivilegiosUsuario;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBGrupoUsuarios;
    private javax.swing.JCheckBox jChBEsCajero;
    private javax.swing.JCheckBox jChBEsFiscal;
    private javax.swing.JLabel jLNombreUsuarioPrivilegios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPasswordField jPClave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRBActivo;
    private javax.swing.JRadioButton jRBInactivo;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFAlias;
    private javax.swing.JTextField jTFCodUsuario;
    private javax.swing.JTextField jTFCodUsuarioPrivilegios;
    private javax.swing.JTextField jTFNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
