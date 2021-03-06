/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import ModRRHH.GenericInfoAuditoria;
import beans.UsuarioBean;
import controls.UsuarioCtrl;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
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
public class Usuarios extends javax.swing.JDialog {

    private static String usuario, fecha;
    static boolean esNuevo, actualizar;
    Cifrador cifrar;
    private ResultSet resultUsuario = null;
    
    public Usuarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cerrarVentana();
        estadoComponentes(false);
        setConfigCampos();
        jBCancelar.setEnabled(false);
        cargaUsuario();
    }

    private void configBotonesModificar(){
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(true);
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
            jTFNombreUsuario.setText(bean.getNombre());
            jTFAlias.setText(bean.getAlias());
            jPClave.setText(bean.getClavePlana());
            usuario = bean.getCodUsuario() + " - " + getNombreUsuario(String.valueOf(bean.getCodUsuario()));
            fecha = bean.getFecVigencia();
            String activo = bean.getActivo();
            String esCajero = bean.getEsCajero();
            String esFiscal = bean.getEsFiscal();
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
        String clave = cifrar.encriptar("0xab", jPClave.getText().trim());
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
                        + "" + vCodUsuarioLogueado + ", 1, "
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
                        + "clave_plana = '" + vClavePlana + "' "
                        + "WHERE cod_usuario = " + vCodUsuario;
            System.out.println("UPDATE USER: " + sqlUsuarios);
            actualizar = true;
        }
        
        try{
            int row = DBManager.ejecutarDML(sqlUsuarios);
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
    }
    
    private boolean creacionUserDB(String USER, String PASSW)
    {
        boolean result = true;
        try
        {
            
            String createUser = "CREATE ROLE " + USER.toUpperCase() + " LOGIN ENCRYPTED PASSWORD '" + PASSW + "' "
                              + "SUPERUSER INHERIT CREATEDB CREATEROLE";
            DBManager.ejecutarDML(createUser);
            System.out.println("CREAR USUARIO BD: " + createUser);
                
            /*if(FMain.baseDato == 1)
            {
                String createUser = "CREATE USER " + USER + " IDENTIFIED BY " + PASSW + " DEFAULT TABLESPACE replication";
                String Grant = "GRANT RVITAL TO " + USER;
                int filaU = DBManager.ejecutarDML(createUser);
                int filaG = DBManager.ejecutarDML(Grant);
            }else
            {
                String createUser = "CREATE ROLE " + USER.toUpperCase() + " LOGIN ENCRYPTED PASSWORD '" + PASSW + "' "
                                    + "SUPERUSER INHERIT CREATEDB CREATEROLE";
                int filaU = DBManager.ejecutarDML(createUser);
            }*/
            
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
            DBManager.ejecutarDML(sqlAlter);
            System.out.println(sqlAlter);
            System.out.println("ALTER USER: " + sqlAlter);
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
        jPanel4 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBInformacion = new javax.swing.JButton();

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jChBEsCajero)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jChBEsFiscal))
                            .addComponent(jTFCodUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPClave, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                                .addComponent(jTFAlias, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jRBActivo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRBInactivo))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
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
                    .addComponent(jChBEsFiscal))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBActivo)
                    .addComponent(jRBInactivo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        DlgConsultas moneda = new DlgConsultas(new JFrame(), true);
        moneda.pack();
        moneda.setTitle("ATOMSystems|Main - Consulta Usuarios");
        moneda.dConsultas("usuario", "nombre", "cod_usuario", "nombre", "alias", "fec_vigencia", "Código", "Nombre", "Alias", "Registro");
        moneda.setText(jTFCodUsuario);
        moneda.tfDescripcionBusqueda.setText("%");
        moneda.setVisible(true);
        getUsuario(Integer.parseInt(jTFCodUsuario.getText()));
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        if(jBModificar.getText().equals("Modificar")){
            jBModificar.setText("Guardar");
            jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
            estadoComponentes(true);
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
        jTFNombreUsuario.requestFocus();
        jChBEsCajero.setSelected(false);
        jChBEsFiscal.setSelected(false);
        jRBActivo.setSelected(true);
        
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        jBModificar.setText("Modificar");
        jBModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png")));
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
        }
    }//GEN-LAST:event_jPClaveKeyPressed

    private void jPClaveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPClaveFocusGained
        jPClave.selectAll();
    }//GEN-LAST:event_jPClaveFocusGained

    private void configBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
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
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuarios dialog = new Usuarios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBInformacion;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JCheckBox jChBEsCajero;
    private javax.swing.JCheckBox jChBEsFiscal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jPClave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRBActivo;
    private javax.swing.JRadioButton jRBInactivo;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFAlias;
    private javax.swing.JTextField jTFCodUsuario;
    private javax.swing.JTextField jTFNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
