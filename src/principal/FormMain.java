/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import ModCaja.Ventas;
import beans.EmpleadoBean;
import beans.EmpresaBean;
import beans.LocalBean;
import beans.SectorBean;
import java.awt.Frame;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JDialog;
import utiles.DBManager;
import utiles.InfoAppGlobal;
import utiles.SetAppearance;

/**
 *
 * @author Andres
 */
public class FormMain extends javax.swing.JFrame implements Runnable{
    
    // MOSTRAR HORA ACTUAL DEL SO 
    private String hora, minutos, segundos;
    Thread thread;

    SetAppearance appearance;
    public static int codUsuario = 0;
    public static int codCaja = 0;
    public static ServerSocket serverSocket;
    public static int idSocket = 0;
    public static boolean conectadoServer = false;
    public static EmpresaBean empresaBean = null;
    public static LocalBean localBean      = null;
    public static SectorBean sectorBean    = null;
    public static EmpleadoBean empleadoBean= null;
    public static List<EmpresaBean> listBeanEmpresa = null;
    public static List<LocalBean> listBeanLocal = null;
    public static String nombreUsuario = "";
    public static String nombreServidor = "";
    public static FormAbastecimiento formAbast = null;
    public static FormRegistrosBase formRegBase = null;
    public static Ventas formVENTAS = null;
    public static FormRRHH formRRHH = null;
    public static CajaMain cajaMAIN = null;
    public static FormFinanciero formFinanciero = null;
    public static String nombreCarpetaProyecto = "ATOMSystemsMain";
    public static int horasIPSMes = 192;
    public static boolean resultExitAbastecimiento = false; //redundante (para entender)
    public static boolean resultExitRegistros = false; //redundante (para entender)
    public static boolean resultExitRRHH = false; //redundante (para entender)
    public static boolean resultExitVentas = false; //redundante (para entender)
    public static boolean resultExitCajaMain = false; //redundante (para entender)
    public static boolean resultExitFinanciero = false; 

    /**
     * Creates new form FormMain
     */
    public FormMain() {
        initComponents();
        
        appearance.setLookAndFeel();
        setEstadoMenus(false);
        setEstadoBotonesMenus(false);
        this.setVisible(true);
        jBLogin.doClick();
        //jMnuRegistros.setVisible(false);
        cerrarVentana();
        inicio();
        String caja = jLEstacion.getText().trim();
        caja = jLEstacion.getText().substring(caja.length() - 2, caja.length());
        codCaja = 1; // Dejar esta variable ACTIVA en para clientes con una sola caja
        //codCaja = Integer.parseInt(caja);   
        //jBPagos.setVisible(false);
        //jBRRHH.setVisible(false);
        thread = new Thread(this);
        thread.start();
        setVisible(true);
    }

    private void cerrarSocket() {
        try {
            serverSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ATENCION: Error al cerrar Socket !!!", "Error...", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void run(){
        Thread current = Thread.currentThread();
        
        while(current == thread){
            time();
            jLHoraActual.setText(hora + ":" + minutos + ":" + segundos);
        }
    }
    
    private void time(){
        Calendar calendario = new GregorianCalendar();
        Date currentTime = new Date();
        calendario.setTime(currentTime);
        hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY): "0" + calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE): "0" + calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND): "0" + calendario.get(Calendar.SECOND);
    }
    
    private void inicio(){
        jLEstacion.setText(utiles.Utiles.getIPAdress());
        jLVersionSistema.setText(getFechaSystema());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        //jLHoraActual.setText(sdf.format(new Date()));
    }
    
    public static String getFechaSystema() {
        Date ultimaModificacao;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy h:mm:ss:SSS", new Locale("es_ES"));
        File arquivo;
        arquivo = new File("/" + FormMain.nombreCarpetaProyecto + "/ATOMSystems-Main.jar");
        ultimaModificacao = new Date(arquivo.lastModified());
        return sdf.format(ultimaModificacao);
    }        
    
    public static void setEstadoMenus(boolean estado){
        jMnuBar.setEnabled(estado);
        jMnuBar.setVisible(estado);
    }
    
    public static void setEstadoBotonesMenus(boolean estado){
        REGISTROS.setEnabled(estado);
        ABASTECIMIENTO.setEnabled(estado);
        VENTAS.setEnabled(estado);
        RRHH.setEnabled(estado);
        FINANCIERO.setEnabled(estado);
        PAGOS.setEnabled(estado);
    }
    
    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            }
        });
    }
    
    private void clearMemory(){
        Runtime garbage = Runtime.getRuntime();
        System.out.println("Free memory before cleaning: " + garbage.freeMemory());
        garbage.gc();
        System.out.println("Memory after cleaning: " + garbage.freeMemory());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jBLogin = new javax.swing.JButton();
        jBLogOut = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanelEmpresaLocal = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFCodEmpresa = new javax.swing.JTextField();
        jTFCodLocal = new javax.swing.JTextField();
        jTFNombreEmpresa = new javax.swing.JTextField();
        jTFNombreLocal = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jLNombreUsuario = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        REGISTROS = new javax.swing.JButton();
        ABASTECIMIENTO = new javax.swing.JButton();
        PAGOS = new javax.swing.JButton();
        FINANCIERO = new javax.swing.JButton();
        VENTAS = new javax.swing.JButton();
        RRHH = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLAlias = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLConectadoDesde = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLHoraActual = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLEstacion = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLVersionSistema = new javax.swing.JLabel();
        jLNombreEmpresa = new javax.swing.JLabel();
        jLDescLocal = new javax.swing.JLabel();
        jMnuBar = new javax.swing.JMenuBar();
        jMnuSistema = new javax.swing.JMenu();
        jMnuIAcerca = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ATOMSystems|Main - Sistema de Gestión");
        setBackground(new java.awt.Color(204, 255, 204));
        setExtendedState(-1);
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jToolBar1.setRollover(true);

        jBLogin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login48.png"))); // NOI18N
        jBLogin.setMnemonic('L');
        jBLogin.setText("Login");
        jBLogin.setFocusable(false);
        jBLogin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBLogin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLoginActionPerformed(evt);
            }
        });
        jToolBar1.add(jBLogin);

        jBLogOut.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBLogOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logout48.png"))); // NOI18N
        jBLogOut.setMnemonic('o');
        jBLogOut.setText("Logout");
        jBLogOut.setFocusable(false);
        jBLogOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBLogOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLogOutActionPerformed(evt);
            }
        });
        jToolBar1.add(jBLogOut);

        jBSalir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir48.png"))); // NOI18N
        jBSalir.setMnemonic('S');
        jBSalir.setText("Salir");
        jBSalir.setFocusable(false);
        jBSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });
        jToolBar1.add(jBSalir);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanelEmpresaLocal.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Empresa:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Local:");

        jTFCodEmpresa.setEnabled(false);

        jTFCodLocal.setEnabled(false);

        jTFNombreEmpresa.setEnabled(false);

        jTFNombreLocal.setEnabled(false);

        javax.swing.GroupLayout jPanelEmpresaLocalLayout = new javax.swing.GroupLayout(jPanelEmpresaLocal);
        jPanelEmpresaLocal.setLayout(jPanelEmpresaLocalLayout);
        jPanelEmpresaLocalLayout.setHorizontalGroup(
            jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmpresaLocalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFCodLocal)
                    .addComponent(jTFCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFNombreEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .addComponent(jTFNombreLocal))
                .addGap(40, 40, 40))
        );
        jPanelEmpresaLocalLayout.setVerticalGroup(
            jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmpresaLocalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFCodEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEmpresaLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCodLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(597, Short.MAX_VALUE)
                .addComponent(jPanelEmpresaLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelEmpresaLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar1.add(jPanel1);

        jToolBar2.setRollover(true);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Usuario conectado:  ");
        jToolBar2.add(jLabel3);

        jLNombreUsuario.setText("Nombre del Usuario Conectado al sistema");
        jToolBar2.add(jLNombreUsuario);

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        REGISTROS.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        REGISTROS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/base24.png"))); // NOI18N
        REGISTROS.setMnemonic('B');
        REGISTROS.setText("Registros Base");
        REGISTROS.setToolTipText("");
        REGISTROS.setEnabled(false);
        REGISTROS.setName("REGISTROS"); // NOI18N
        REGISTROS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REGISTROSActionPerformed(evt);
            }
        });

        ABASTECIMIENTO.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        ABASTECIMIENTO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/abastecimiento24.png"))); // NOI18N
        ABASTECIMIENTO.setMnemonic('A');
        ABASTECIMIENTO.setText("Abastecimiento");
        ABASTECIMIENTO.setEnabled(false);
        ABASTECIMIENTO.setName("ABASTECIMIENTO"); // NOI18N
        ABASTECIMIENTO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ABASTECIMIENTOActionPerformed(evt);
            }
        });

        PAGOS.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        PAGOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pagos24.png"))); // NOI18N
        PAGOS.setMnemonic('P');
        PAGOS.setText("***");
        PAGOS.setEnabled(false);
        PAGOS.setName("PAGOS"); // NOI18N
        PAGOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PAGOSActionPerformed(evt);
            }
        });

        FINANCIERO.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        FINANCIERO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/financiero24.png"))); // NOI18N
        FINANCIERO.setMnemonic('F');
        FINANCIERO.setText("Financiero");
        FINANCIERO.setEnabled(false);
        FINANCIERO.setName("FINANCIERO"); // NOI18N
        FINANCIERO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FINANCIEROActionPerformed(evt);
            }
        });

        VENTAS.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        VENTAS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/caja24.png"))); // NOI18N
        VENTAS.setMnemonic('C');
        VENTAS.setText("Ventas");
        VENTAS.setEnabled(false);
        VENTAS.setName("VENTAS"); // NOI18N
        VENTAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VENTASActionPerformed(evt);
            }
        });

        RRHH.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        RRHH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/rrhh24.png"))); // NOI18N
        RRHH.setMnemonic('R');
        RRHH.setText("RRHH");
        RRHH.setEnabled(false);
        RRHH.setName("RRHH"); // NOI18N
        RRHH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RRHHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(REGISTROS, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ABASTECIMIENTO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PAGOS, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FINANCIERO, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VENTAS, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RRHH, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {FINANCIERO, PAGOS, REGISTROS, RRHH});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(REGISTROS, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ABASTECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PAGOS, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FINANCIERO, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VENTAS, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RRHH, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ABASTECIMIENTO, FINANCIERO, PAGOS, REGISTROS, RRHH});

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Usuario");

        jLAlias.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLAlias.setForeground(new java.awt.Color(102, 102, 102));
        jLAlias.setText("***");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("Conectado desde");

        jLConectadoDesde.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLConectadoDesde.setForeground(new java.awt.Color(102, 102, 102));
        jLConectadoDesde.setText("***");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("Hora actual:");

        jLHoraActual.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLHoraActual.setForeground(new java.awt.Color(102, 102, 102));
        jLHoraActual.setText("***");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLAlias))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLConectadoDesde))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLHoraActual)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLAlias))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLConectadoDesde))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLHoraActual))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/soporte48.png"))); // NOI18N
        jButton1.setText("Soporte");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Estación:");

        jLEstacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLEstacion.setForeground(new java.awt.Color(102, 102, 102));
        jLEstacion.setText("***");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 102, 102));
        jLabel9.setText("Versión del sistema:");

        jLVersionSistema.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLVersionSistema.setForeground(new java.awt.Color(102, 102, 102));
        jLVersionSistema.setText("***");

        jLNombreEmpresa.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLNombreEmpresa.setForeground(new java.awt.Color(153, 153, 153));
        jLNombreEmpresa.setText("***");

        jLDescLocal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLDescLocal.setForeground(new java.awt.Color(153, 153, 153));
        jLDescLocal.setText("***");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLDescLocal)
                    .addComponent(jLNombreEmpresa)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jButton1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLEstacion)
                            .addGap(218, 218, 218)
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLVersionSistema))
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLNombreEmpresa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLDescLocal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLEstacion)
                        .addComponent(jLabel9)
                        .addComponent(jLVersionSistema)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMnuSistema.setText("Sistema");
        jMnuSistema.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIAcerca.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIAcerca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/informacion24.png"))); // NOI18N
        jMnuIAcerca.setText("Acerca del Sistema");
        jMnuIAcerca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIAcercaActionPerformed(evt);
            }
        });
        jMnuSistema.add(jMnuIAcerca);

        jMnuBar.add(jMnuSistema);

        setJMenuBar(jMnuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1280, 669));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLoginActionPerformed
        if(FormMain.conectadoServer){
            cerrarSocket();
            FormMain.conectadoServer = false;
        }
        DlgLogin login = new DlgLogin(this, true);
        login.pack();
        login.setVisible(true);
        jBLogin.setEnabled(false);
    }//GEN-LAST:event_jBLoginActionPerformed

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        if(controlForms()){
            JOptionPane.showMessageDialog(this, "Existen formularios abiertos" , "ATENCION", JOptionPane.WARNING_MESSAGE);
        }else{
            salirDelSistema();
        }
        
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLogOutActionPerformed
        if(controlForms()){
            JOptionPane.showMessageDialog(this, "Existen formularios abiertos" , "ATENCION", JOptionPane.WARNING_MESSAGE);
        }else{            
            int exit = JOptionPane.showConfirmDialog(this, "¿Desloguearse?", "CERRAR CONEXION", JOptionPane.YES_NO_OPTION);
            if(exit == 0){     
                clearMemory();
                cerrarSocket();
                conectadoServer = false;
                jBLogin.setEnabled(true);
                DBManager.CerrarStatements();
                DBManager.cerrarBD();
                setEstadoMenus(false);
                setEstadoBotonesMenus(false);
                FormMain.jLNombreUsuario.setText("*");
            }
        }
        
        
    }//GEN-LAST:event_jBLogOutActionPerformed

    private void REGISTROSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REGISTROSActionPerformed
        if (formRegBase == null){
            clearMemory();
            formRegBase= new FormRegistrosBase(InfoAppGlobal.getUserReal(), InfoAppGlobal.getUserGroupApp());
            formRegBase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formRegBase.setVisible(true);
            resultExitRegistros = true;
        }else{
            formRegBase.setState(Frame.NORMAL);
            formRegBase.setVisible(true);
            resultExitRegistros = true;
        }
    }//GEN-LAST:event_REGISTROSActionPerformed

    private void ABASTECIMIENTOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ABASTECIMIENTOActionPerformed
        if (formAbast == null){
            clearMemory();
            formAbast= new FormAbastecimiento(InfoAppGlobal.getUserReal(), InfoAppGlobal.getUserGroupApp());
            formAbast.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formAbast.setVisible(true);
            resultExitAbastecimiento = true;
        }else{
            formAbast.setVisible(true);
            resultExitAbastecimiento = true;
        }
    }//GEN-LAST:event_ABASTECIMIENTOActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Soporte soporte = new Soporte(new JFrame(), true);
        soporte.pack();
        soporte.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMnuIAcercaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIAcercaActionPerformed
        Acerca acerca = new Acerca(new JFrame(), true);
        acerca.pack();
        acerca.setVisible(true);
    }//GEN-LAST:event_jMnuIAcercaActionPerformed

    private void VENTASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VENTASActionPerformed
        if(cajaMAIN == null){
            clearMemory();
            cajaMAIN = new CajaMain(InfoAppGlobal.getUserReal(), InfoAppGlobal.getUserGroupApp());
            cajaMAIN.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            cajaMAIN.setVisible(true);
            resultExitCajaMain = true;
        }else{
            cajaMAIN.setState(JFrame.NORMAL);
            cajaMAIN.setVisible(true);
            resultExitCajaMain = true;
        }
        /*if (formVENTAS == null){
            formVENTAS = new Ventas();
            formVENTAS.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formVENTAS.setVisible(true);
            resultExitVentas = true;
        }else{
            formVENTAS.setState(Frame.NORMAL);
            formVENTAS.setVisible(true);
            resultExitVentas = true;
        }*/
    }//GEN-LAST:event_VENTASActionPerformed

    private void FINANCIEROActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FINANCIEROActionPerformed
        if (formFinanciero == null){
            clearMemory();
            formFinanciero = new FormFinanciero(InfoAppGlobal.getUserReal(), InfoAppGlobal.getUserGroupApp());
            formFinanciero.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formFinanciero.setVisible(true);
            resultExitFinanciero = true;
        }else{
            formFinanciero.setVisible(true);
            resultExitFinanciero = true;
        }
    }//GEN-LAST:event_FINANCIEROActionPerformed

    private void PAGOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PAGOSActionPerformed
        JOptionPane.showMessageDialog(this, "Módulo NO DISPONIBLE!\nEn desarrollo!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_PAGOSActionPerformed

    private void RRHHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RRHHActionPerformed
        if (formRRHH == null){
            clearMemory();
            formRRHH = new FormRRHH(InfoAppGlobal.getUserReal(), InfoAppGlobal.getUserGroupApp());
            formRRHH.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formRRHH.setVisible(true);
            resultExitRRHH = true;
        }else{
            formRRHH.setState(Frame.NORMAL);
            formRRHH.setVisible(true);
            resultExitRRHH = true;
        }
    }//GEN-LAST:event_RRHHActionPerformed

    private void salirDelSistema()
    {
        int exit = JOptionPane.showConfirmDialog(this, "Desea salir del sistema?", "Salir del sistema", JOptionPane.YES_NO_OPTION);
        if(exit == 0){
            if(FormMain.conectadoServer){
                cerrarSocket();
                FormMain.conectadoServer = false;
            }
            DBManager.cerrarBD();
            FormMain.empresaBean     = null;
            FormMain.listBeanEmpresa = null;
            FormMain.localBean       = null;
            FormMain.listBeanLocal   = null;
            System.exit(0);
        }
    }
    
    private boolean controlForms(){
        boolean result = true;
        if(resultExitAbastecimiento == false && resultExitRegistros == false && resultExitRRHH == false && resultExitCajaMain == false && resultExitFinanciero == false){
            result = false;
        }
        
        if(resultExitAbastecimiento == true){
            formAbast.setState(Frame.NORMAL);
            formAbast.setVisible(true);
        }
        
        if(resultExitRegistros == true){
            formRegBase.setState(Frame.NORMAL);
            formRegBase.setVisible(true);
        }
        
        if(resultExitRRHH == true){
            formRRHH.setState(Frame.NORMAL);
            formRRHH.setVisible(true);
        }
        
        if(resultExitCajaMain == true){
            cajaMAIN.setState(Frame.NORMAL);
            cajaMAIN.setVisible(true);
        }
        
        if(resultExitFinanciero == true){
            formFinanciero.setState(Frame.NORMAL);
            formFinanciero.setVisible(true);
        }
        
        return result;
    }
    
    public static void permisosModulos(boolean estado, ResultSet rs){
        if(InfoAppGlobal.getUserGroupApp().equals("1")){
            REGISTROS.setEnabled(estado);
            ABASTECIMIENTO.setEnabled(estado);
            FINANCIERO.setEnabled(estado);
            PAGOS.setEnabled(estado);
            RRHH.setEnabled(estado);
            VENTAS.setEnabled(estado);
        }else{
            botones();
            rs = obtenerPerfil(InfoAppGlobal.getUserGroupApp());
            try{
                while(rs.next()){
                    recorrerBotones(estado, rs.getString("nombre_objeto"));
                }
            }catch(SQLException sqlex){
                sqlex.printStackTrace();
            }finally{
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static void recorrerBotones(boolean estado, String rs){
        if(ABASTECIMIENTO.getName().equals(rs)){
            ABASTECIMIENTO.setEnabled(estado);
        }else if(REGISTROS.getName().equals(rs)){
            REGISTROS.setEnabled(estado);
        }else if(FINANCIERO.getName().equals(rs)){
            FINANCIERO.setEnabled(estado);
        }else if(PAGOS.getName().equals(rs)){
            PAGOS.setEnabled(estado);
        }else if(RRHH.getName().equals(rs)){
            RRHH.setEnabled(estado);
        }else if(VENTAS.getName().equals(rs)){
            VENTAS.setEnabled(estado);
        }
    }
    
    private static ResultSet obtenerPerfil(String string){
        String sql = "SELECT m.nombre_objeto "
                   + "FROM perfil_usuario pu "
                   + "INNER JOIN usuario u ON pu.cod_usuario_perfil = u.cod_usuario "
                   + "INNER JOIN modulo m ON pu.cod_modulo = m.cod_modulo "
                   + "WHERE pu.cod_usuario_perfil = " + codUsuario + " "
                   + "AND u.activo = 'S' "
                   + "GROUP BY m.nombre_objeto "
                   + "ORDER BY m.nombre_objeto";
        ResultSet rs = DBManager.ejecutarDSL(sql);
        System.out.println("SQL OBTENER PERFIL: " + sql);
        return rs;
    }
    
    private static void botones(){
        REGISTROS.setEnabled(false);
        ABASTECIMIENTO.setEnabled(false);
        FINANCIERO.setEnabled(false);
        PAGOS.setEnabled(false);
        RRHH.setEnabled(false);
        VENTAS.setEnabled(false);
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
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormMain().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton ABASTECIMIENTO;
    private static javax.swing.JButton FINANCIERO;
    private static javax.swing.JButton PAGOS;
    private static javax.swing.JButton REGISTROS;
    private static javax.swing.JButton RRHH;
    private static javax.swing.JButton VENTAS;
    private javax.swing.JButton jBLogOut;
    private javax.swing.JButton jBLogin;
    private javax.swing.JButton jBSalir;
    private javax.swing.JButton jButton1;
    public static javax.swing.JLabel jLAlias;
    public static javax.swing.JLabel jLConectadoDesde;
    public static javax.swing.JLabel jLDescLocal;
    private javax.swing.JLabel jLEstacion;
    public static javax.swing.JLabel jLHoraActual;
    public static javax.swing.JLabel jLNombreEmpresa;
    public static javax.swing.JLabel jLNombreUsuario;
    private javax.swing.JLabel jLVersionSistema;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private static javax.swing.JMenuBar jMnuBar;
    private javax.swing.JMenuItem jMnuIAcerca;
    private javax.swing.JMenu jMnuSistema;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelEmpresaLocal;
    public static javax.swing.JTextField jTFCodEmpresa;
    public static javax.swing.JTextField jTFCodLocal;
    public static javax.swing.JTextField jTFNombreEmpresa;
    public static javax.swing.JTextField jTFNombreLocal;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
}
