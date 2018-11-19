/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRRHH;

import controls.EmpleadoCtrl;
import impresion.ImpresoraEpson;
import impresion.VerImpresora;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JRException;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.NumeroATexto;
import utiles.StatementManager;
import utiles.Utiles;
import views.busca.BuscaDescBeneficioRRHH;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class RegistroDescBeneficios extends javax.swing.JDialog {

    /**
     * Creates new form AnticipoSalario
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    String fecVigencia = "";
    ResultSet resultConsultas;
    
    /*
    -- IMPRESION
    */
    
    public static JTextField jTPrinterSelected = new javax.swing.JTextField();
    public static JTextField jTCPYDETECT = new javax.swing.JTextField();
    
    public RegistroDescBeneficios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        configuraCampos();
        getFecVigencia();
        setEstadoComponentes(false);
        estadoBotonCancelar();
        habilitarBotonImprimir();
    }

    private void configuraCampos(){
        jTFCodEmpleado.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFNumero.setDocument(new MaxLength(9, "", "ENTERO"));
        jTFObservacion.setDocument(new MaxLength(100, "UPPER", "ALFA"));
        
        jTFFechaAnticipo.setInputVerifier(new FechaInputVerifier(jTFFechaAnticipo));
        
        jTFImporte.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFFechaAnticipo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDebitos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCreditos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFSalario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFSaldo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        
        jTFCodEmpleado.addFocusListener(new Focus());
        jTFNumero.addFocusListener(new Focus());
        jTFFechaAnticipo.addFocusListener(new Focus());
        jTFImporte.addFocusListener(new Focus());
        jTFObservacion.addFocusListener(new Focus());
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
    
    private void setEstadoComponentes(boolean estado){
        jTFCodEmpleado.setEnabled(estado);
        jTFNumero.setEnabled(estado);
        jTFFechaAnticipo.setEnabled(estado);
        jTFImporte.setEnabled(estado);
        jTFObservacion.setEnabled(estado);
        jTFCodConcepto.setEnabled(estado);
    }
    
    private void setValuesField(){
        jTFCodEmpleado.setText("0");
        jTFCreditos.setText("0");
        jTFDebitos.setText("0");
        jTFFechaAnticipo.setText(fecVigencia);
        jTFImporte.setText("0");
        jTFNombreEmpleado.setText("");
        jTFNumero.setText(fecVigencia);
        jTFObservacion.setText("-");
        getSecuenciaAnticipo();
    }
    
    private void setEstadoBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBImprimir.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBCancelar.setEnabled(true);
        jBBuscar.setEnabled(false);
    }
    
    private void setEstadoBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBImprimir.setEnabled(true);
        jBGrabar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBBuscar.setEnabled(true);
        estadoBotonCancelar();
    }
    
    private String getNroMovimientoTesoreria(){
        String result = "";
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('movtesoreria') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    result = TheQuery.TheResultSet.getString(1);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de Movimiento de Tesoreria", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException sqlex) {
                JOptionPane.showMessageDialog(this, "Error en la Obtencion de Secuencia de Movimiento de Tesoreria!! ", "ATENCION", JOptionPane.WARNING_MESSAGE);
                InfoErrores.errores(sqlex);
            }finally
            {
                TheQuery.CerrarStatement();
                TheQuery.ClearDBManagerstmt();
                TheQuery = null;
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de Movimiento de Tesoreria", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private void getSecuenciaAnticipo(){
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT NEXTVAL('seq_debcre_empleado') AS secuencia";
        TheQuery.EjecutarSql();
        if (TheQuery.TheResultSet != null) {
            try {
                if (TheQuery.TheResultSet.next()) {
                    jTFNumero.setText(TheQuery.TheResultSet.getString(1));
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de anticipo", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException sqlex) {
                JOptionPane.showMessageDialog(this, "Error en la Obtencion de Secuencia de anticipo!! ", "ATENCION", JOptionPane.WARNING_MESSAGE);
                InfoErrores.errores(sqlex);
            }finally
            {
                TheQuery.CerrarStatement();
                TheQuery.ClearDBManagerstmt();
                TheQuery = null;
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo Obtener Nueva Secuencia de anticipo", "ATENCION", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void estadoBotonCancelar(){
        if(!jBNuevo.isEnabled()){
            jBCancelar.setText("Cancelar");
            jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png")));            
        }else{
            jBCancelar.setText("Salir");
            jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png")));
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
        jLabel1 = new javax.swing.JLabel();
        jTFCodEmpleado = new javax.swing.JTextField();
        jTFNombreEmpleado = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFSalario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFDebitos = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFCreditos = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFSaldo = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jTFTipoComprob = new javax.swing.JTextField();
        jTFDescripcion = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFNumero = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFCodConcepto = new javax.swing.JTextField();
        jTFDescConcepto = new javax.swing.JTextField();
        jTFDebCreConcepto = new javax.swing.JTextField();
        jTFIngEgConcepto = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFFechaAnticipo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFImporte = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFObservacion = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBImprimir = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Registro de Descuentos - Beneficios");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("REGISTRO DE DESCUENTOS - BENEFICIOS");

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Funcionario:");

        jTFCodEmpleado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodEmpleado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodEmpleadoFocusLost(evt);
            }
        });
        jTFCodEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodEmpleadoKeyPressed(evt);
            }
        });

        jTFNombreEmpleado.setEditable(false);
        jTFNombreEmpleado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Salario:");

        jTFSalario.setEditable(false);
        jTFSalario.setBackground(new java.awt.Color(255, 255, 204));
        jTFSalario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Débitos:");

        jTFDebitos.setEditable(false);
        jTFDebitos.setBackground(new java.awt.Color(255, 255, 204));
        jTFDebitos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Créditos:");

        jTFCreditos.setEditable(false);
        jTFCreditos.setBackground(new java.awt.Color(255, 255, 204));
        jTFCreditos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Saldo:");

        jTFSaldo.setEditable(false);
        jTFSaldo.setBackground(new java.awt.Color(255, 255, 204));
        jTFSaldo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Comprobante:");

        jTFTipoComprob.setEditable(false);
        jTFTipoComprob.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTipoComprob.setText("DCF");

        jTFDescripcion.setEditable(false);
        jTFDescripcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDescripcion.setText("DEBITO - CREDITO FUNCIONARIO");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Número:");

        jTFNumero.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNumero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNumeroFocusGained(evt);
            }
        });
        jTFNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNumeroKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Concepto:");

        jTFCodConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodConcepto.setToolTipText("F10 para Registro de Conceptos");
        jTFCodConcepto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodConceptoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodConceptoFocusLost(evt);
            }
        });
        jTFCodConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodConceptoKeyPressed(evt);
            }
        });

        jTFDescConcepto.setEditable(false);
        jTFDescConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTFDebCreConcepto.setEditable(false);
        jTFDebCreConcepto.setBackground(new java.awt.Color(255, 255, 204));
        jTFDebCreConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDebCreConcepto.setText("D");

        jTFIngEgConcepto.setEditable(false);
        jTFIngEgConcepto.setBackground(new java.awt.Color(255, 255, 204));
        jTFIngEgConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFIngEgConcepto.setText("E");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Fecha:");

        jTFFechaAnticipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFechaAnticipo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFechaAnticipoFocusGained(evt);
            }
        });
        jTFFechaAnticipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFechaAnticipoKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Importe:");

        jTFImporte.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jTFImporte.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFImporteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFImporteFocusLost(evt);
            }
        });
        jTFImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFImporteKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Observación:");

        jTFObservacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFObservacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFObservacionFocusGained(evt);
            }
        });
        jTFObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFObservacionKeyPressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/nuevo24.png"))); // NOI18N
        jBNuevo.setText("Nuevo");
        jBNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNuevoActionPerformed(evt);
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

        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
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
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBGrabar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBCancelar)
                .addGap(28, 28, 28))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscar, jBCancelar, jBGrabar, jBImprimir, jBNuevo});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBGrabar)
                    .addComponent(jBImprimir)
                    .addComponent(jBBuscar)
                    .addComponent(jBCancelar))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTFObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jTFDebitos, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTFCreditos, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTFSalario))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jTFSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTFTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTFNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTFImporte, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTFFechaAnticipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTFCodConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFDescConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFDebCreConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFIngEgConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTFSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFDebitos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTFCreditos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTFSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFTipoComprob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFCodConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDescConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDebCreConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFIngEgConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTFFechaAnticipo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTFImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTFObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(640, 457));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        estadoBotonCancelar();
        
        if(jBCancelar.getText().equals("Salir")){
            dispose();
        }
        
        if(jBCancelar.getText().equals("Cancelar")){
            setEstadoComponentes(false);
            setEstadoBotonesCancelar();
            jTFCodEmpleado.setText("0");
            jTFNumero.setText("0");
            jTFNombreEmpleado.setText("");
            habilitarBotonImprimir();
        }
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFCodEmpleadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNumero.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas empleados = new DlgConsultas(new JFrame(), true);
                empleados.pack();
                empleados.setTitle("ATOMSystems|Main - Consulta de Empleados");
                empleados.dConsultas("empleado", "nombre", "cod_empleado", "nombre", "apellido", "fec_ingreso", "Codigo", "Nombre", "Apellido", "Ingreso");
                empleados.setText(jTFCodEmpleado);
                empleados.tfDescripcionBusqueda.setText("%");
                empleados.tfDescripcionBusqueda.selectAll();
                empleados.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodEmpleadoKeyPressed

    private String getNombreEmpleado(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            EmpleadoCtrl ctrl = new EmpleadoCtrl();
            result = ctrl.getNombreEmpleado(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
    
    private void getDatosConceptoConsulta(String codigo) {
                
        try{
            String sql = " SELECT des_concepto, tip_concepto, debcre FROM concepto WHERE cod_concepto = " + codigo; 
            System.out.println("SQL CONSULTA CONCEPTO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFDescConcepto.setText(rs.getString("des_concepto"));
                    jTFIngEgConcepto.setText(rs.getString("tip_concepto"));
                    jTFDebCreConcepto.setText(rs.getString("debcre"));
                }else{
                    jTFDescConcepto.setText("");
                    jTFIngEgConcepto.setText("");
                    jTFDebCreConcepto.setText("");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    
    private void getDatosConcepto(String codigo) {
                
        try{
            String sql = " SELECT des_concepto, tip_concepto, debcre FROM concepto WHERE cod_concepto = " + codigo + " AND activo = 'S' "
                       + " AND cod_concepto <> (SELECT valor FROM parametro_rrhh WHERE parametro = 'RRHH_COD_CONCEPTO_ANTICIPO')::integer"; 
            System.out.println("SQL CONSULTA CONCEPTO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jTFDescConcepto.setBackground(new java.awt.Color(255,255,204));
                    jTFDescConcepto.setText(rs.getString("des_concepto"));
                    jTFIngEgConcepto.setText(rs.getString("tip_concepto"));
                    jTFDebCreConcepto.setText(rs.getString("debcre"));
                }else{
                    jTFDescConcepto.setText("CONCEPTO NO DISPONIBLE");
                    jTFCodConcepto.grabFocus();
                    jTFDescConcepto.setBackground(new java.awt.Color(255, 0, 0));
                }
            }else{
                JOptionPane.showMessageDialog(this, "No existen datos para la consulta!", "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private int getDebitoEmpleado(){
        int debito = 0;
        try{
            String sql = " SELECT SUM(monto) AS monto FROM debcre_empleado WHERE cod_empleado = " + jTFCodEmpleado.getText() 
                       + " AND debcre = 'D' AND fec_carga BETWEEN (SELECT fechainicial FROM periodo_rrhh WHERE vigente = 'S') AND "
                       + "(SELECT fechafinal FROM periodo_rrhh WHERE vigente = 'S') AND estado = 'V'"; 
            System.out.println("DEBITO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    debito = rs.getInt("monto");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return debito;
    }
    
    private int getCreditoEmpleado(){
        int credito = 0;
        try{
            String sql = " SELECT SUM(monto) AS monto FROM debcre_empleado WHERE cod_empleado = " + jTFCodEmpleado.getText() 
                       + " AND debcre = 'C' AND fec_carga BETWEEN (SELECT fechainicial FROM periodo_rrhh WHERE vigente = 'S') AND "
                       + "(SELECT fechafinal FROM periodo_rrhh WHERE vigente = 'S') AND estado = 'V'"; 
            System.out.println("CREDITO: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    credito = rs.getInt("monto");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return credito;
    }
    
    private int getSalarioEmpleado(){
        int salario = 0;
        try{
            ResultSet rs = DBManager.ejecutarDSL("SELECT sueldo1 FROM empleado WHERE cod_empleado = " + jTFCodEmpleado.getText());
            if(rs != null){
                if(rs.next()){
                    salario = rs.getInt("sueldo1");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }       
        return salario;
    }
    
    private void habilitarBotonImprimir(){
        if((jTFNumero.getText().equals("") && jTFCodEmpleado.getText().equals("")) || (jTFCodEmpleado.getText().equals("0") && jTFNumero.getText().equals("0"))){
            jBImprimir.setEnabled(false);
        }else{
            jBImprimir.setEnabled(true);
        }
    }
    
    
    private void cargarAnticipoSeleccionado(String numero){
        
        try{
            resultConsultas = DBManager.ejecutarDSL("SELECT cod_empleado, nro_comprob, to_char(fec_carga, 'dd/MM/yyyy') as fecCarga, monto, observacion,"
                                                  + "cod_concepto "
                                                  + "FROM debcre_empleado WHERE nro_comprob = '" + jTFNumero.getText() + "'" );
            if(resultConsultas != null){
                if(resultConsultas.next()){
                    jTFCodEmpleado.setText(resultConsultas.getString("cod_empleado"));
                    jTFNombreEmpleado.setText(getNombreEmpleado(jTFCodEmpleado.getText()));
                    jTFFechaAnticipo.setText(resultConsultas.getString("fecCarga"));
                    int importe = resultConsultas.getInt("monto");
                    jTFImporte.setText(decimalFormat.format(importe));
                    jTFObservacion.setText(resultConsultas.getString("observacion"));
                    jTFCodConcepto.setText(resultConsultas.getString("cod_concepto"));
                    getDatosConceptoConsulta(jTFCodConcepto.getText());
                }
                cargaDebitoCreditoSaldo();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void cargaDebitoCreditoSaldo(){                
        int saldo = 0;
        int debito = getDebitoEmpleado();
        int credito = getCreditoEmpleado();
        int salario = getSalarioEmpleado();
        
        try{
            salario = Integer.parseInt(jTFSalario.getText());
        }catch(Exception ex){}
        
        saldo = (salario + credito) - debito;
        
        if(saldo < 0){
            jTFSaldo.setBackground(new java.awt.Color(255, 0, 0));
        }else{
            jTFSaldo.setBackground(new java.awt.Color(255,255,204));
        }
        
        jTFSaldo.setText(String.valueOf(decimalFormat.format(saldo)));
        
        jTFSalario.setText(String.valueOf(decimalFormat.format(salario)));
        jTFDebitos.setText(String.valueOf(decimalFormat.format(debito)));
        jTFCreditos.setText(String.valueOf(decimalFormat.format(credito)));
    }
    
    private void grabarAnticipo(){
        String codEmpresa = utiles.Utiles.getCodEmpresaDefault();
        String codLocal = utiles.Utiles.getCodLocalDefault("1");
        String codEmpleado = jTFCodEmpleado.getText();
        String nroMovimiento = getNroMovimientoTesoreria();
        String codConcepto = jTFCodConcepto.getText();
        String debCre = jTFDebCreConcepto.getText();
        String monto = jTFImporte.getText().replace(",", "");
        String fecCarga = " to_date('" + jTFFechaAnticipo.getText() + "', 'dd/MM/yyyy')";
        String fecVencimiento = " to_date('" + jTFFechaAnticipo.getText() + "', 'dd/MM/yyyy')";
        String tipComprob = jTFTipoComprob.getText();
        String nroComprob = jTFNumero.getText();
        String obs = jTFObservacion.getText();
        int codUsuario = FormMain.codUsuario;
        
        String sql = "INSERT INTO debcre_empleado (cod_empresa, cod_local, cod_empleado, nro_movimiento, cod_concepto, debcre, monto, fec_carga, "
                                                + "fec_vencimiento, tip_comprob, nro_comprob, observacion, cod_usuario, fec_vigencia, estado, can_cuota, "
                                                + "nro_cuota, cod_cuenta, cod_moneda, tip_cambio) " 
                   + "VALUES (" + codEmpresa + ", "
                                + codLocal + ", "
                                + codEmpleado + ", "
                                + nroMovimiento + ", "
                                + codConcepto + ", '"
                                + debCre + "', "
                                + monto + ", "
                                + fecCarga + ", "
                                + fecVencimiento + ", '"
                                + tipComprob + "', "
                                + nroComprob + ", '"
                                + obs + "', "
                                + codUsuario + ", '"
                                + "now()', '"
                                + "V', "
                                + "1, 1, 0, 1, 1)";
        
        System.out.println("GRABAR ANTICIPO: " + sql);
        if(DBManager.ejecutarDML(sql) > 0){
            try{
                DBManager.conn.commit();
                setEstadoComponentes(false);
                setEstadoBotonesCancelar();
                JOptionPane.showMessageDialog(this, "Datos Grabados !!!", "Exito", JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "Error al Grabar Datos (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Error al Grabar Datos, Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                jBCancelar.doClick();
                jBNuevo.grabFocus();
        }
    }
    
    private boolean verificaCampos(){
        if(jTFImporte.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo IMPORTE !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFImporte.requestFocus();
            return false;
        }
        
        if(jTFCodEmpleado.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: No puede dejar el campo vacío !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodEmpleado.requestFocus();
            return false;
        }
        
        if(jTFCodEmpleado.getText().equals("0")){
            JOptionPane.showMessageDialog(this, "ATENCION: Empleado inexistente !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodEmpleado.requestFocus();
            return false;
        }
        
        if(jTFFechaAnticipo.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ATENCION: No puede dejar el campo de FECHA vacío !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFFechaAnticipo.requestFocus();
            return false;
        }
        
        if(jTFDescripcion.getText().equals("")){
            jTFDescripcion.setText("ANTICIPO DE SALARIO, " + jTFNombreEmpleado.getText());
            return false;
        }
        
        return true;
    }
    
    private boolean estadoImpresoraOK() {
        VerImpresora vPrinter = new VerImpresora();
        boolean vEstadoPRN = false;
        if (vPrinter.Enlinea) {
            vEstadoPRN = true;
        } else {
            JOptionPane.showMessageDialog(null, "Impresora Apagada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            vEstadoPRN = false;
        }
        if (vPrinter.FaltaPapel_o_EnPausa) {
            JOptionPane.showMessageDialog(null, "Impresora sin Papel !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            vEstadoPRN = false;
        }
        return vEstadoPRN;
    }
    
    private void imprimir(){
        jTPrinterSelected.setText("Matricial");
        String pCallFrom = "RegistroDescBeneficios";
        new impresion.OpcionesdeImpresion(new javax.swing.JFrame(), true, pCallFrom).setVisible(true);
        if(jTPrinterSelected.getText().equals("Matricial")){
            System.out.println("ENTRA EN MATRICIAL");
            try{
                if(estadoImpresoraOK()){
                    boolean vContinuePrint = true;
                    jTCPYDETECT.setText("NoCopy");
                    while(vContinuePrint){
                        imprimeAnticipoMatricial();
                        vContinuePrint = false;
                    }
                }else{}
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }
        }else if(jTPrinterSelected.getText().equals("Laser")){
            System.out.println("ENTRA EN LASER");
            imprimeDebCreLaser();
        }
    }
    
    private void imprimeDebCreLaser(){
        
        String fechaActual = utiles.Utiles.getSysDateTimeString();
        
        // -- Convertir en texto el monto
            String mont = jTFImporte.getText().replace(",", "").substring(0, jTFImporte.getText().length() - 3); // ejemplo 300,000.00 deja en 300000.
            String mon = mont.replace(".", ""); // deja en 300000
            int monto = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = numero.convertirLetras(monto);
        // -- Fin de convertir en texto el monto
        
        String sql = "SELECT DISTINCT deb.cod_empleado, em.nombre, em.apellido, deb.monto, to_char(deb.fec_carga, 'dd/MM/yyyy') AS fecCarga, " +
                     "deb.nro_comprob, deb.cod_usuario, usu.nombre " +
                     "FROM debcre_empleado deb " +
                     "INNER JOIN empleado em " +
                     "ON deb.cod_empleado = em.cod_empleado " +
                     "INNER JOIN usuario usu " +
                     "ON deb.cod_usuario = usu.cod_usuario " +
                     "WHERE deb.nro_comprob = '" + jTFNumero.getText() + "'";
        
        try{
            LibReportes.parameters.put("pFechaHora", fechaActual);
            LibReportes.parameters.put("pUsuario", FormMain.nombreUsuario);
            LibReportes.parameters.put("pNombreEmpleado", jTFNombreEmpleado.getText());
            LibReportes.parameters.put("pMontoTexto", montoTxt);
            LibReportes.parameters.put("pMonto", jTFImporte.getText());
            LibReportes.parameters.put("pNroComprob", jTFNumero.getText());
            LibReportes.parameters.put("pEmpresa", "ATOMSystems");
            LibReportes.parameters.put("pConceptoDescripcion", jTFDescConcepto.getText());
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "debcreEmpleado");
            
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private void imprimeAnticipoMatricial(){
        ImpresoraEpson epsonPrinter = new ImpresoraEpson("LPT1");
        imprimirAnticipo(epsonPrinter);
    }
    
    private void imprimirAnticipo(ImpresoraEpson printer){
        try{
            printer.setCaracterCondensado();
            printer.println();
            printer.print("C O M P R O B A N T E   D E   A D E L A N T O    ");
            printer.println();
            printer.println();
            String nombreEmpleado = jTFCodEmpleado.getText() + " - " + jTFNombreEmpleado.getText();
            
            // -- Convertir en texto el monto
            String mont = jTFImporte.getText().replace(",", "").substring(0, jTFImporte.getText().length() - 3); // ejemplo 300,000.00 deja en 300000.
            String mon = mont.replace(".", ""); // deja en 300000
            int monto = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = numero.convertirLetras(monto);
            // -- Fin de convertir en texto el monto
            
            printer.print("     Yo, " + nombreEmpleado + " recibí en conformidad la suma de " + montoTxt + ", ");
            printer.print("en concepto de adelanto de salario.");
            printer.println();
            printer.println();
            printer.println();
            printer.println();
            
            printer.print("                                                                                                     IMPORTE: " + jTFImporte.getText());
            
            //printer.print("========================================================================================================================================");
            
            printer.print("=========================");
            printer.print("  " + nombreEmpleado + " ");
            printer.reset();
            printer.flush();
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }        
    }
    
    private void jTFNumeroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNumeroKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodConcepto.grabFocus();
        }
    }//GEN-LAST:event_jTFNumeroKeyPressed

    private void jTFFechaAnticipoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFechaAnticipoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFImporte.grabFocus();
        }
    }//GEN-LAST:event_jTFFechaAnticipoKeyPressed

    private void jTFImporteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFImporteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){            
            jTFObservacion.setText("ANTICIPO DE SALARIO, " + jTFNombreEmpleado.getText());
            jTFObservacion.grabFocus();
        }
    }//GEN-LAST:event_jTFImporteKeyPressed

    private void jTFObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFObservacionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGrabar.grabFocus();
        }
    }//GEN-LAST:event_jTFObservacionKeyPressed

    private void jTFCodEmpleadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusGained
        jTFCodEmpleado.selectAll();
    }//GEN-LAST:event_jTFCodEmpleadoFocusGained

    private void jTFNumeroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNumeroFocusGained
        jTFNumero.selectAll();
    }//GEN-LAST:event_jTFNumeroFocusGained

    private void jTFFechaAnticipoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFechaAnticipoFocusGained
        jTFFechaAnticipo.selectAll();
    }//GEN-LAST:event_jTFFechaAnticipoFocusGained

    private void jTFImporteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFImporteFocusGained
        jTFImporte.selectAll();
    }//GEN-LAST:event_jTFImporteFocusGained

    private void jTFObservacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFObservacionFocusGained
        jTFObservacion.selectAll();
    }//GEN-LAST:event_jTFObservacionFocusGained

    private void jTFCodEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodEmpleadoFocusLost
        int saldo = 0;
        int debito = getDebitoEmpleado();
        int credito = getCreditoEmpleado();
        int salario = getSalarioEmpleado();
        
        try{
            salario = Integer.parseInt(jTFSalario.getText());
        }catch(Exception ex){}
        
        saldo = (salario + credito) - debito;
        
        if(saldo < 0){
            jTFSaldo.setBackground(new java.awt.Color(255, 0, 0));
        }else{
            jTFSaldo.setBackground(new java.awt.Color(255,255,204));
        }
        
        jTFSaldo.setText(String.valueOf(decimalFormat.format(saldo)));
        
        jTFNombreEmpleado.setText(getNombreEmpleado(jTFCodEmpleado.getText()));
        jTFSalario.setText(String.valueOf(decimalFormat.format(salario)));
        jTFDebitos.setText(String.valueOf(decimalFormat.format(debito)));
        jTFCreditos.setText(String.valueOf(decimalFormat.format(credito)));
        
    }//GEN-LAST:event_jTFCodEmpleadoFocusLost

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        setEstadoComponentes(true);
        setValuesField();
        setEstadoBotonesNuevo();
        estadoBotonCancelar();
        jTFCodEmpleado.grabFocus();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jTFImporteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFImporteFocusLost
        if(!jTFImporte.getText().equals(".00")){
            jTFImporte.setText(decimalFormat.format(Integer.parseInt(jTFImporte.getText())));
        }
    }//GEN-LAST:event_jTFImporteFocusLost

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(verificaCampos()){
            grabarAnticipo();
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        if(!jTFCodEmpleado.getText().equals("0")){
            imprimir();
        }
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        BuscaDescBeneficioRRHH desBenf = new BuscaDescBeneficioRRHH(new JFrame(), true, "DESCBENEFICIOS");
        desBenf.setText(jTFNumero);
        desBenf.pack();
        desBenf.setVisible(true);
        if(!jTFNumero.getText().equals("")){
            cargarAnticipoSeleccionado(jTFCodEmpleado.getText());
            habilitarBotonImprimir();
        }
        
        if(!jTFCodEmpleado.getText().equals("")){
            cargarAnticipoSeleccionado(jTFCodEmpleado.getText());
            habilitarBotonImprimir();
        }
        
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jTFCodConceptoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodConceptoKeyPressed
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFImporte.grabFocus();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            try {
                DlgConsultas empleados = new DlgConsultas(new JFrame(), true);
                empleados.pack();
                empleados.setTitle("ATOMSystems|Main - Consulta de Conceptos");
                empleados.dConsultas("concepto", "des_concepto", "cod_concepto", "des_concepto", "tip_concepto", "debcre", "Codigo", "Descripción", "Tipo", "Deb-Cre");
                empleados.setText(jTFCodConcepto);
                empleados.tfDescripcionBusqueda.setText("%");
                empleados.tfDescripcionBusqueda.selectAll();
                empleados.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F10){
            Conceptos concepto = new Conceptos(new JFrame(), true);
            concepto.pack();
            concepto.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodConceptoKeyPressed

    private void jTFCodConceptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodConceptoFocusGained
        jTFCodConcepto.selectAll();
    }//GEN-LAST:event_jTFCodConceptoFocusGained

    private void jTFCodConceptoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodConceptoFocusLost
        if(jTFCodConcepto.getText().equals("") || jTFCodConcepto.getText().equals("0")){
            JOptionPane.showMessageDialog(this, "Debe informar un concepto válido!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            jTFCodConcepto.grabFocus();
        }else{
            getDatosConcepto(jTFCodConcepto.getText());
        }
    }//GEN-LAST:event_jTFCodConceptoFocusLost

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
            java.util.logging.Logger.getLogger(RegistroDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroDescBeneficios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistroDescBeneficios dialog = new RegistroDescBeneficios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBImprimir;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFCodConcepto;
    private javax.swing.JTextField jTFCodEmpleado;
    private javax.swing.JTextField jTFCreditos;
    private javax.swing.JTextField jTFDebCreConcepto;
    private javax.swing.JTextField jTFDebitos;
    private javax.swing.JTextField jTFDescConcepto;
    private javax.swing.JTextField jTFDescripcion;
    private javax.swing.JTextField jTFFechaAnticipo;
    private javax.swing.JTextField jTFImporte;
    private javax.swing.JTextField jTFIngEgConcepto;
    private javax.swing.JTextField jTFNombreEmpleado;
    private javax.swing.JTextField jTFNumero;
    private javax.swing.JTextField jTFObservacion;
    private javax.swing.JTextField jTFSalario;
    private javax.swing.JTextField jTFSaldo;
    private javax.swing.JTextField jTFTipoComprob;
    // End of variables declaration//GEN-END:variables
}
