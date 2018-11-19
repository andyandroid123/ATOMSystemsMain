/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRRHH;

import beans.HorarioBean;
import controls.HorarioCtrl;
import controls.UsuarioCtrl;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.Utiles;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class Horarios extends javax.swing.JDialog {

    /**
     * Creates new form Horarios
     */
    
    boolean esNuevo = false;
    public static String usuario = null;
    public static String fecha = null;
    
    public Horarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setConfigCampos();
        setEstadoComponentes(false);
        cargaHorario();
    }

    private void grabarHorario(){
        try{
            HorarioBean bean = new HorarioBean();
            bean.setActivo(jCBActivo.getSelectedItem().toString());
            bean.setCodEmpresa(Integer.parseInt(utiles.Utiles.getCodEmpresaDefault()));
            bean.setCodHorario(Integer.parseInt(jTFCodHorario.getText()));
            bean.setCodUsuario(FormMain.codUsuario);
            bean.setDesHorario(jTFDescripcion.getText());
            bean.setEntrada1(jFTFEntrada1.getText());
            bean.setEntrada2(jFTFEntrada2.getText());
            bean.setEntrada3(jFTFEntrada3.getText());
            bean.setEntrada4(jFTFEntrada4.getText());
            bean.setSalida1(jFTFSalida1.getText());
            bean.setSalida2(jFTFSalida2.getText());
            bean.setSalida3(jFTFSalida3.getText());
            bean.setSalida4(jFTFSalida4.getText());
            bean.setTolerancia(jFTFTolerancia.getText());
            
            HorarioCtrl ctrl = new HorarioCtrl();
            if(esNuevo){
                if(ctrl.catastraHorario(bean)){
                    JOptionPane.showMessageDialog(this, "Horario registrado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                    cargaHorario();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Horario !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                if(ctrl.alterarHorario(bean)){
                    JOptionPane.showMessageDialog(this, "Horario modificado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Horario !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar Horario !!!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean verificaCampos(){
        if(jTFDescripcion.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo DESCRIPCION !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFDescripcion.requestFocus();
            return false;
        }
        
        if(jFTFEntrada1.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo ENTRADA 1 !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFDescripcion.requestFocus();
            return false;
        }
        
        if(jFTFSalida1.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo SALIDA 1 !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFDescripcion.requestFocus();
            return false;
        }
        
        if(jFTFEntrada2.getText().equals("")){
            jFTFEntrada2.setText("00:00");
            return true;
        }
        
        if(jFTFSalida2.getText().equals("")){
            jFTFSalida2.setText("00:00");
            return true;
        }
        
        if(jFTFEntrada3.getText().equals("")){
            jFTFEntrada3.setText("00:00");
            return true;
        }
        
        if(jFTFSalida3.getText().equals("")){
            jFTFSalida3.setText("00:00");
            return true;
        }
        
        if(jFTFEntrada4.getText().equals("")){
            jFTFEntrada4.setText("00:00");
            return true;
        }
        
        if(jFTFSalida4.getText().equals("")){
            jFTFSalida4.setText("00:00");
            return true;
        }
        
        if(jFTFTolerancia.getText().equals("")){
            jFTFTolerancia.setText("00:00");
            return true;
        }
        
        return true;
    }
    
    private void cargaHorario(){
        HorarioCtrl ctrl = new HorarioCtrl();
        int max = ctrl.buscaMaxCodHorario();
        if(max > 0){
            getHorario(max);
        }else{
            jBModificar.grabFocus();
        }
    }
    
    private void getHorario(int codigo){
        HorarioCtrl ctrl = new HorarioCtrl();
        HorarioBean bean = ctrl.buscaHorarioCodigo(codigo);
        if(bean != null){
            jTFCodHorario.setText(String.valueOf(bean.getCodHorario()));
            jTFDescripcion.setText(bean.getDesHorario());
            jFTFEntrada1.setText(bean.getEntrada1());
            jFTFEntrada2.setText(bean.getEntrada2());
            jFTFEntrada3.setText(bean.getEntrada3());
            jFTFEntrada4.setText(bean.getEntrada4());
            jFTFSalida1.setText(bean.getSalida1());
            jFTFSalida2.setText(bean.getSalida2());
            jFTFSalida3.setText(bean.getSalida3());
            jFTFSalida4.setText(bean.getSalida4());
            jFTFTolerancia.setText(bean.getTolerancia());
            jCBActivo.setSelectedItem(bean.getActivo());
            usuario = bean.getCodUsuario() + " - " + getNombreUsuario(String.valueOf(bean.getCodUsuario()));
            fecha = bean.getFecVigencia();
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
    
    private void setConfigCampos(){
        jTFDescripcion.setDocument(new MaxLength(30, "UPPER", "ALFA"));
    }
    
    private void setEstadoComponentes(boolean estado){
        jTFDescripcion.setEnabled(estado);
        jFTFEntrada1.setEnabled(estado);
        jFTFEntrada2.setEnabled(estado);
        jFTFEntrada3.setEnabled(estado);
        jFTFEntrada4.setEnabled(estado);
        jFTFSalida1.setEnabled(estado);
        jFTFSalida2.setEnabled(estado);
        jFTFSalida3.setEnabled(estado);
        jFTFSalida4.setEnabled(estado);
        jFTFTolerancia.setEnabled(estado);
        jCBActivo.setEnabled(estado);
    }
    
    private void llenarCamposRegistro(){
        jTFCodHorario.setText(getCodigoHorario());
        jTFDescripcion.setText("");
        jFTFEntrada1.setText("00:00");
        jFTFEntrada2.setText("00:00");
        jFTFEntrada3.setText("00:00");
        jFTFEntrada4.setText("00:00");
        jFTFSalida1.setText("00:00");
        jFTFSalida2.setText("00:00");
        jFTFSalida3.setText("00:00");
        jFTFSalida4.setText("00:00");
        jFTFTolerancia.setText("00:00");
    }
    
    private String getCodigoHorario() {
        HorarioCtrl pCtrl = new HorarioCtrl();
        int result = pCtrl.buscaMaxCodHorario();
        result = result + 1;
        return String.valueOf(result);
    }
    
    private void setEstadoBotonesModificar(){
        jBSalir.setEnabled(false);
        jBNuevo.setEnabled(false);
        jBModificar.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBCancelar.setEnabled(true);
        jBBuscar.setEnabled(false);
        jBInformacion.setEnabled(false);
    }
    
    private void setEstadoBotonesNuevo(){
        jBBuscar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBGrabar.setEnabled(true);
        jBInformacion.setEnabled(false);
        jBModificar.setEnabled(false);
        jBNuevo.setEnabled(false);
        jBSalir.setEnabled(false);
    }
    
    private void setEstadoBotonesCancelar(){
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
    }
    
    private void setEstadoBotonesGrabado(){
        jBBuscar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGrabar.setEnabled(false);
        jBInformacion.setEnabled(true);
        jBModificar.setEnabled(true);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTFCodHorario = new javax.swing.JTextField();
        jTFDescripcion = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jCBActivo = new javax.swing.JComboBox<>();
        jFTFEntrada1 = new javax.swing.JFormattedTextField();
        jFTFSalida1 = new javax.swing.JFormattedTextField();
        jFTFEntrada2 = new javax.swing.JFormattedTextField();
        jFTFEntrada3 = new javax.swing.JFormattedTextField();
        jFTFEntrada4 = new javax.swing.JFormattedTextField();
        jFTFSalida2 = new javax.swing.JFormattedTextField();
        jFTFSalida3 = new javax.swing.JFormattedTextField();
        jFTFSalida4 = new javax.swing.JFormattedTextField();
        jFTFTolerancia = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jBInformacion = new javax.swing.JButton();

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Mantenimiento de Conceptos");

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Mantenimiento de Horarios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("MANTENIMIENTO DE HORARIOS");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Horario:");

        jTFCodHorario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTFCodHorario.setEnabled(false);

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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Entrada 1:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Salida 1:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Entrada 2:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Salida 2:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Entrada 3:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Salida 3:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Entrada 4:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Salida 4:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Tolerancia:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Activo:");

        jCBActivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBActivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBActivoKeyPressed(evt);
            }
        });

        try {
            jFTFEntrada1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFEntrada1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFEntrada1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFEntrada1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFEntrada1FocusGained(evt);
            }
        });
        jFTFEntrada1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFEntrada1KeyPressed(evt);
            }
        });

        try {
            jFTFSalida1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFSalida1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFSalida1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFSalida1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFSalida1FocusGained(evt);
            }
        });
        jFTFSalida1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFSalida1KeyPressed(evt);
            }
        });

        try {
            jFTFEntrada2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFEntrada2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFEntrada2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFEntrada2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFEntrada2FocusGained(evt);
            }
        });
        jFTFEntrada2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFEntrada2KeyPressed(evt);
            }
        });

        try {
            jFTFEntrada3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFEntrada3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFEntrada3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFEntrada3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFEntrada3FocusGained(evt);
            }
        });
        jFTFEntrada3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFEntrada3KeyPressed(evt);
            }
        });

        try {
            jFTFEntrada4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFEntrada4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFEntrada4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFEntrada4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFEntrada4FocusGained(evt);
            }
        });
        jFTFEntrada4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFEntrada4KeyPressed(evt);
            }
        });

        try {
            jFTFSalida2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFSalida2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFSalida2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFSalida2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFSalida2FocusGained(evt);
            }
        });
        jFTFSalida2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFSalida2KeyPressed(evt);
            }
        });

        try {
            jFTFSalida3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFSalida3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFSalida3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFSalida3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFSalida3FocusGained(evt);
            }
        });
        jFTFSalida3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFSalida3KeyPressed(evt);
            }
        });

        try {
            jFTFSalida4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFSalida4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFSalida4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFSalida4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFSalida4FocusGained(evt);
            }
        });
        jFTFSalida4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFSalida4KeyPressed(evt);
            }
        });

        try {
            jFTFTolerancia.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFTFTolerancia.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFTolerancia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jFTFTolerancia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFToleranciaFocusGained(evt);
            }
        });
        jFTFTolerancia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFToleranciaKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFCodHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescripcion))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFSalida2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFSalida1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFEntrada3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jFTFSalida3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jFTFEntrada4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jFTFTolerancia, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel11)
                                .addComponent(jLabel13))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jFTFSalida4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jFTFEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFSalida1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jFTFEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFSalida2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jFTFEntrada3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFSalida3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jFTFEntrada4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFSalida4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFTolerancia, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

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

        jBGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGrabar.setText("Grabar");
        jBGrabar.setEnabled(false);
        jBGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGrabarActionPerformed(evt);
            }
        });

        jBBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscar.setText("Buscar");
        jBBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jBNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBGrabar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBBuscar))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jBSalir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBInformacion)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBModificar)
                    .addComponent(jBGrabar)
                    .addComponent(jBBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBSalir)
                    .addComponent(jBInformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBCancelar))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(535, 463));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFDescripcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescripcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFEntrada1.grabFocus();
        }
    }//GEN-LAST:event_jTFDescripcionKeyPressed

    private void jFTFEntrada1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFEntrada1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFSalida1.grabFocus();
        }
    }//GEN-LAST:event_jFTFEntrada1KeyPressed

    private void jFTFSalida1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFSalida1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFEntrada2.grabFocus();
        }
    }//GEN-LAST:event_jFTFSalida1KeyPressed

    private void jFTFEntrada2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFEntrada2KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFSalida2.grabFocus();
        }
    }//GEN-LAST:event_jFTFEntrada2KeyPressed

    private void jFTFSalida2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFSalida2KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFEntrada3.grabFocus();
        }
    }//GEN-LAST:event_jFTFSalida2KeyPressed

    private void jFTFEntrada3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFEntrada3KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFSalida3.grabFocus();
        }
    }//GEN-LAST:event_jFTFEntrada3KeyPressed

    private void jFTFSalida3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFSalida3KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFEntrada4.grabFocus();
        }
    }//GEN-LAST:event_jFTFSalida3KeyPressed

    private void jFTFEntrada4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFEntrada4KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFSalida4.grabFocus();
        }
    }//GEN-LAST:event_jFTFEntrada4KeyPressed

    private void jFTFSalida4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFSalida4KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFTolerancia.grabFocus();
        }
    }//GEN-LAST:event_jFTFSalida4KeyPressed

    private void jCBActivoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBActivoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGrabar.grabFocus();
        }
    }//GEN-LAST:event_jCBActivoKeyPressed

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        esNuevo = true;
        setEstadoComponentes(true);
        setEstadoBotonesNuevo();
        llenarCamposRegistro();
        jTFDescripcion.grabFocus();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoBotonesCancelar();
        setEstadoComponentes(false);
        cargaHorario();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFDescripcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescripcionFocusGained
        jTFDescripcion.selectAll();
    }//GEN-LAST:event_jTFDescripcionFocusGained

    private void jFTFEntrada1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFEntrada1FocusGained
        jFTFEntrada1.selectAll();
    }//GEN-LAST:event_jFTFEntrada1FocusGained

    private void jFTFSalida1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFSalida1FocusGained
        jFTFSalida1.selectAll();
    }//GEN-LAST:event_jFTFSalida1FocusGained

    private void jFTFEntrada2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFEntrada2FocusGained
        jFTFEntrada2.selectAll();
    }//GEN-LAST:event_jFTFEntrada2FocusGained

    private void jFTFEntrada3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFEntrada3FocusGained
        jFTFEntrada3.selectAll();
    }//GEN-LAST:event_jFTFEntrada3FocusGained

    private void jFTFEntrada4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFEntrada4FocusGained
        jFTFEntrada4.selectAll();
    }//GEN-LAST:event_jFTFEntrada4FocusGained

    private void jFTFToleranciaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFToleranciaFocusGained
        jFTFTolerancia.selectAll();
    }//GEN-LAST:event_jFTFToleranciaFocusGained

    private void jFTFSalida2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFSalida2FocusGained
        jFTFSalida2.selectAll();
    }//GEN-LAST:event_jFTFSalida2FocusGained

    private void jFTFSalida3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFSalida3FocusGained
        jFTFSalida3.selectAll();
    }//GEN-LAST:event_jFTFSalida3FocusGained

    private void jFTFSalida4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFSalida4FocusGained
        jFTFSalida4.selectAll();
    }//GEN-LAST:event_jFTFSalida4FocusGained

    private void jBInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInformacionActionPerformed
        GenericInfoAuditoria info = new GenericInfoAuditoria(new JFrame(), true, usuario, fecha, "Horarios");
        info.setTitle("ATOMSystems|Main - Auditoria");
        info.pack();
        info.setVisible(true);
    }//GEN-LAST:event_jBInformacionActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        esNuevo = false;
        setEstadoComponentes(true);
        setEstadoBotonesModificar();
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(verificaCampos()){
            grabarHorario();
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        DlgConsultas horarios = new DlgConsultas(new JFrame(), true);
        horarios.pack();
        horarios.setTitle("ATOMSystems|Main - Consulta de Horarios");
        horarios.tfDescripcionBusqueda.setText("%");
        horarios.dConsultas("horario", "des_horario", "cod_horario", "des_horario", "fec_vigencia", null, "Codigo", "Descripción", "Registro", null);
        horarios.setText(jTFCodHorario);
        horarios.setVisible(true);
        getHorario(Integer.parseInt(jTFCodHorario.getText()));
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jFTFToleranciaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFToleranciaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBActivo.grabFocus();
        }
    }//GEN-LAST:event_jFTFToleranciaKeyPressed

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
            java.util.logging.Logger.getLogger(Horarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Horarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Horarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Horarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Horarios dialog = new Horarios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBGrabar;
    private javax.swing.JButton jBInformacion;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBActivo;
    private javax.swing.JFormattedTextField jFTFEntrada1;
    private javax.swing.JFormattedTextField jFTFEntrada2;
    private javax.swing.JFormattedTextField jFTFEntrada3;
    private javax.swing.JFormattedTextField jFTFEntrada4;
    private javax.swing.JFormattedTextField jFTFSalida1;
    private javax.swing.JFormattedTextField jFTFSalida2;
    private javax.swing.JFormattedTextField jFTFSalida3;
    private javax.swing.JFormattedTextField jFTFSalida4;
    private javax.swing.JFormattedTextField jFTFTolerancia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField jTFCodHorario;
    private javax.swing.JTextField jTFDescripcion;
    // End of variables declaration//GEN-END:variables
}
