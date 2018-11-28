
package ModFinanciero.informes;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import net.sf.jasperreports.engine.JRException;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.LibReportes;
import utiles.MaxLength;
import utiles.NumeroATexto;
import utiles.StatementManager;

/**
 *
 * @author ANDRES
 */
public class ImpresionRecibosPagares extends javax.swing.JDialog {

    
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    
    public ImpresionRecibosPagares(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargaComboMoneda();
        getNombreMoneda();
        getNombreMonedaPagare();
        configCampos();
        jTFPctInteres.setText("0");
        jTFNroRecibo.requestFocus();
    }

    private void cargaComboMoneda(){
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT alias FROM moneda WHERE vigente = 'S' ORDER BY cod_moneda";
        TheQuery.EjecutarSql();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {
                    jCBMoneda.addItem(TheQuery.TheResultSet.getString("alias"));
                    jCBMonedaPagare.addItem(TheQuery.TheResultSet.getString("alias"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
    }
    
    private void getNombreMoneda(){
        try{
            String sql = "SELECT nombre_plural FROM moneda WHERE alias LIKE '%" + jCBMoneda.getSelectedItem().toString() + "%'";
            System.out.println("SQL NOMBRE MONEDA SELECCIONADA: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreMoneda.setText(rs.getString("nombre_plural"));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    private void getNombreMonedaPagare(){
        try{
            String sql = "SELECT nombre_plural FROM moneda WHERE alias LIKE '%" + jCBMonedaPagare.getSelectedItem().toString() + "%'";
            System.out.println("SQL NOMBRE MONEDA SELECCIONADA: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    jLNombreMonedaPagare.setText(rs.getString("nombre_plural"));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private void configCampos(){
        jTFNroRecibo.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFPctInteres.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFNombreCiudad.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFNombreCiudadPagare.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFRecibidoDe.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFNombreDeudorPagare.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFBeneficiarioPagare.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFConcepto.setDocument(new MaxLength(100, "UPPER", "ALFA"));
        jTFDireccionPagare.setDocument(new MaxLength(70, "UPPER", "ALFA"));
        
        jTFNroRecibo.addFocusListener(new Focus()); 
        jTFNombreCiudad.addFocusListener(new Focus());
        jTFMontoRecibo.addFocusListener(new Focus());
        jTFRecibidoDe.addFocusListener(new Focus());
        jFTFFecha.addFocusListener(new Focus());
        jTFConcepto.addFocusListener(new Focus());
        jTFNombreCiudadPagare.addFocusListener(new Focus());
        jFTFMontoPagare.addFocusListener(new Focus());
        jTFPctInteres.addFocusListener(new Focus());
        jFTFFechaEmisionPagare.addFocusListener(new Focus());
        jFTFFechaVencimientoPagare.addFocusListener(new Focus());
        jTFNombreDeudorPagare.addFocusListener(new Focus());
        jTFCINroPagare.addFocusListener(new Focus());
        jTFDireccionPagare.addFocusListener(new Focus());
        jTFBeneficiarioPagare.addFocusListener(new Focus());
    }
    
    private void imprimirRecibo(){
        
        String sql = "SELECT * FROM cliente WHERE cod_cliente = 1";
        
        String nroRecibo = jTFNroRecibo.getText().trim();
        String nombreCiudad = jTFNombreCiudad.getText().trim();
        String monto_moneda = jTFMontoRecibo.getText().trim();
        double monto = Double.parseDouble(jTFMontoRecibo.getText().trim().replace(",", ""));
        String moneda = jCBMoneda.getSelectedItem().toString();
        String recibidoDe = jTFRecibidoDe.getText().trim();
        String fecha = jFTFFecha.getText().trim();
        String concepto = jTFConcepto.getText().trim();
        
        // -- Convertir en texto el monto
            String mon;
            //String mont = monto_moneda.substring(0, String.valueOf(monto).length() - 2); // ejemplo 300,000.00 deja en 300000.
            String mont = monto_moneda.replace(",", ""); // ejemplo 300,000.00 deja en 300000.
            if(mont.length() > 6){ // si alcanza el millon
                mont = mont.substring(0, mont.length());
                mon = mont.replace(",", ""); // deja en 300000
            }else{
                mon = mont.replace(",", ""); // deja en 300000
            }
            int monto_entero = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = jLNombreMoneda.getText().trim().toLowerCase() + " " + numero.convertirLetras(monto_entero);
        // -- Fin de convertir en texto el monto
        
        String dia = fecha.substring(0, fecha.length() - 8);
        String mes1 = fecha.substring(3, fecha.length() - 5);
        String anho = fecha.substring(6);
        String mesLetras = getNombreMes(mes1);
        String fecha_impresion = nombreCiudad + ", " + dia + " de " + mesLetras + " del " + anho;
        
        try{
            LibReportes.parameters.put("pAliasMoneda", moneda);
            LibReportes.parameters.put("pCiudadFecha", fecha_impresion);
            LibReportes.parameters.put("pRecibidoDe", recibidoDe);
            LibReportes.parameters.put("pMontoMoneda", monto_moneda);
            LibReportes.parameters.put("pMontoLetra", montoTxt);
            LibReportes.parameters.put("pConcepto", concepto);
            LibReportes.parameters.put("pNroRecibo", nroRecibo);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "recibo_dinero_comun");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private void imprimirPagare(){
        
        String sql = "SELECT * FROM cliente WHERE cod_cliente = 1";
        
        String nombreCiudad = jTFNombreCiudadPagare.getText().trim();
        String vencimiento = jFTFFechaVencimientoPagare.getText().trim();
        String fecha = jFTFFechaEmisionPagare.getText().trim();
        String monto_moneda = jFTFMontoPagare.getText().trim();
        String alias_moneda = jCBMonedaPagare.getSelectedItem().toString();
        String deudor = jTFNombreDeudorPagare.getText().trim();
        String direccionDeudor = jTFDireccionPagare.getText().trim();
        String docDeudor = jTFCINroPagare.getText().trim();
        String interes = jTFPctInteres.getText().trim();
        String beneficiario = jTFBeneficiarioPagare.getText().trim();
        
        String dia = vencimiento.substring(0, fecha.length() - 8);
        String mes1 = vencimiento.substring(3, fecha.length() - 5);
        String anho = vencimiento.substring(6);
        String mesLetras = getNombreMes(mes1);
        String fecha_impresion = nombreCiudad + ", " + dia + " de " + mesLetras + " del " + anho;
        
        // -- Convertir en texto el monto
            String mon;
            //String mont = monto_moneda.substring(0, String.valueOf(monto).length() - 2); // ejemplo 300,000.00 deja en 300000.
            String mont = monto_moneda.replace(",", ""); // ejemplo 300,000.00 deja en 300000.
            if(mont.length() > 6){ // si alcanza el millon
                mont = mont.substring(0, mont.length());
                mon = mont.replace(",", ""); // deja en 300000
            }else{
                mon = mont.replace(",", ""); // deja en 300000
            }
            int monto_entero = Integer.parseInt(mon); // valor entero
            NumeroATexto numero = new NumeroATexto(); // clase conversora
            String montoTxt = jLNombreMoneda.getText().trim().toLowerCase() + " " + numero.convertirLetras(monto_entero);
        // -- Fin de convertir en texto el monto
        
        try{
            LibReportes.parameters.put("pVencimiento", vencimiento);
            LibReportes.parameters.put("pCiudadFecha", fecha_impresion);
            LibReportes.parameters.put("pMontoMoneda", monto_moneda);
            LibReportes.parameters.put("pMontoLetra", montoTxt);
            LibReportes.parameters.put("pAliasMoneda", alias_moneda);
            LibReportes.parameters.put("pDeudor", deudor);
            LibReportes.parameters.put("pDireccionDeudor", direccionDeudor);
            LibReportes.parameters.put("pDocDeudor", docDeudor);
            LibReportes.parameters.put("pInteres", interes);
            LibReportes.parameters.put("pDia", dia);
            LibReportes.parameters.put("pMesLetras", mesLetras);
            LibReportes.parameters.put("pAnho", anho);
            LibReportes.parameters.put("pBeneficiario", beneficiario);
            LibReportes.parameters.put("REPORT_CONNECTION", DBManager.conn);
            LibReportes.generarReportes(sql, "pagare");
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }catch(JRException jrex){
            jrex.printStackTrace();
            InfoErrores.errores(jrex);
        }
    }
    
    private String getNombreMes(String mes){
        String result = "";
        switch(mes){
            case ("01"):
                result = "Enero";
                break;
            case ("02"):
                result = "Febrero";
                break;
            case ("03"):
                result = "Marzo";
                break;
            case ("04"):
                result = "Abril";
                break;
            case ("05"):
                result = "Mayo";
                break;
            case ("06"):
                result = "Junio";
                break;
            case ("07"):
                result = "Julio";
                break;
            case ("08"):
                result = "Agosto";
                break;
            case ("09"):
                result = "Septiembre";
                break;
            case ("10"):
                result = "Octubre";
                break;
            case ("11"):
                result = "Noviembre";
                break;
            case ("12"):
                result = "Diciembre";
                break;
            default:
                break;
                
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

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPRecibo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTFNroRecibo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFNombreCiudad = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jCBMoneda = new javax.swing.JComboBox<>();
        jLNombreMoneda = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTFRecibidoDe = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jFTFFecha = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFConcepto = new javax.swing.JTextField();
        jBImprimirRecibo = new javax.swing.JButton();
        jBCancelarRecibo = new javax.swing.JButton();
        jTFMontoRecibo = new javax.swing.JTextField();
        jPPagare = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTFNombreCiudadPagare = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jFTFMontoPagare = new javax.swing.JFormattedTextField();
        jCBMonedaPagare = new javax.swing.JComboBox<>();
        jLNombreMonedaPagare = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTFPctInteres = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jFTFFechaEmisionPagare = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jFTFFechaVencimientoPagare = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTFNombreDeudorPagare = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTFCINroPagare = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFDireccionPagare = new javax.swing.JTextField();
        jBImprimirPagare = new javax.swing.JButton();
        jBCancelarPagare = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jTFBeneficiarioPagare = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Impresión de Recibos / Pagarés");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("IMPRESIÓN DE RECIBOS / PAGARÉS");

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

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 204));

        jPRecibo.setBackground(new java.awt.Color(204, 255, 204));
        jPRecibo.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Número de recibo:");

        jTFNroRecibo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroRecibo.setText("0");
        jTFNroRecibo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroReciboFocusGained(evt);
            }
        });
        jTFNroRecibo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroReciboKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Ciudad:");

        jTFNombreCiudad.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreCiudad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNombreCiudad.setText("***");
        jTFNombreCiudad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNombreCiudadFocusGained(evt);
            }
        });
        jTFNombreCiudad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNombreCiudadKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Monto:");

        jCBMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMonedaActionPerformed(evt);
            }
        });
        jCBMoneda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBMonedaKeyPressed(evt);
            }
        });

        jLNombreMoneda.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLNombreMoneda.setText("***");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Recibí de:");

        jTFRecibidoDe.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFRecibidoDe.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFRecibidoDe.setText("***");
        jTFRecibidoDe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFRecibidoDeFocusGained(evt);
            }
        });
        jTFRecibidoDe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFRecibidoDeKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Fecha:");

        jFTFFecha.setFormatterFactory(new javax.swing.JFormattedTextField.AbstractFormatterFactory() {
            public javax.swing.JFormattedTextField.AbstractFormatter
            getFormatter(javax.swing.JFormattedTextField tf){
                try{
                    return new javax.swing.text.MaskFormatter("##/##/####");
                }catch(java.text.ParseException pe){
                    pe.printStackTrace();
                }
                return null;
            }
        });
        jFTFFecha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFFecha.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFTFFecha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFFechaFocusGained(evt);
            }
        });
        jFTFFecha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFFechaKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("En concepto de:");

        jTFConcepto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFConcepto.setText("***");
        jTFConcepto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFConceptoFocusGained(evt);
            }
        });
        jTFConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFConceptoKeyPressed(evt);
            }
        });

        jBImprimirRecibo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBImprimirRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimirRecibo.setText("Imprimir recibo");
        jBImprimirRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirReciboActionPerformed(evt);
            }
        });

        jBCancelarRecibo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBCancelarRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelarRecibo.setText("Cancelar y Salir");
        jBCancelarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarReciboActionPerformed(evt);
            }
        });

        jTFMontoRecibo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFMontoRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMontoRecibo.setText("0");
        jTFMontoRecibo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFMontoReciboFocusGained(evt);
            }
        });
        jTFMontoRecibo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFMontoReciboKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPReciboLayout = new javax.swing.GroupLayout(jPRecibo);
        jPRecibo.setLayout(jPReciboLayout);
        jPReciboLayout.setHorizontalGroup(
            jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPReciboLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPReciboLayout.createSequentialGroup()
                        .addComponent(jBImprimirRecibo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBCancelarRecibo))
                    .addGroup(jPReciboLayout.createSequentialGroup()
                        .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPReciboLayout.createSequentialGroup()
                                .addComponent(jTFMontoRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLNombreMoneda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jTFRecibidoDe)
                            .addComponent(jFTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFConcepto, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))))
                .addContainerGap(271, Short.MAX_VALUE))
        );
        jPReciboLayout.setVerticalGroup(
            jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPReciboLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFNroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFNombreCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jCBMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreMoneda)
                    .addComponent(jTFMontoRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTFRecibidoDe, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPReciboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBImprimirRecibo)
                    .addComponent(jBCancelarRecibo))
                .addContainerGap(111, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Recibo", jPRecibo);

        jPPagare.setBackground(new java.awt.Color(204, 255, 204));
        jPPagare.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Ciudad:");

        jTFNombreCiudadPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreCiudadPagare.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFNombreCiudadPagare.setText("***");
        jTFNombreCiudadPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNombreCiudadPagareFocusGained(evt);
            }
        });
        jTFNombreCiudadPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNombreCiudadPagareKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Importe a pagar:");

        jFTFMontoPagare.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        jFTFMontoPagare.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFTFMontoPagare.setText("0");
        jFTFMontoPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFTFMontoPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFMontoPagareFocusGained(evt);
            }
        });
        jFTFMontoPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFMontoPagareKeyPressed(evt);
            }
        });

        jCBMonedaPagare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMonedaPagareActionPerformed(evt);
            }
        });
        jCBMonedaPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBMonedaPagareKeyPressed(evt);
            }
        });

        jLNombreMonedaPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLNombreMonedaPagare.setText("***");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Interés:");

        jTFPctInteres.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFPctInteres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFPctInteres.setText("0");
        jTFPctInteres.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFPctInteresFocusGained(evt);
            }
        });
        jTFPctInteres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFPctInteresKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Fecha de Emisión:");

        jFTFFechaEmisionPagare.setFormatterFactory(new javax.swing.JFormattedTextField.AbstractFormatterFactory() {
            public javax.swing.JFormattedTextField.AbstractFormatter
            getFormatter(javax.swing.JFormattedTextField tf){
                try{
                    return new javax.swing.text.MaskFormatter("##/##/####");
                }catch(java.text.ParseException pe){
                    pe.printStackTrace();
                }
                return null;
            }
        });
        jFTFFechaEmisionPagare.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFFechaEmisionPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFTFFechaEmisionPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFFechaEmisionPagareFocusGained(evt);
            }
        });
        jFTFFechaEmisionPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFFechaEmisionPagareKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Primer vencimiento:");

        jFTFFechaVencimientoPagare.setFormatterFactory(new javax.swing.JFormattedTextField.AbstractFormatterFactory() {
            public javax.swing.JFormattedTextField.AbstractFormatter
            getFormatter(javax.swing.JFormattedTextField tf){
                try{
                    return new javax.swing.text.MaskFormatter("##/##/####");
                }catch(java.text.ParseException pe){
                    pe.printStackTrace();
                }
                return null;
            }
        });
        jFTFFechaVencimientoPagare.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFTFFechaVencimientoPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFTFFechaVencimientoPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTFFechaVencimientoPagareFocusGained(evt);
            }
        });
        jFTFFechaVencimientoPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFTFFechaVencimientoPagareKeyPressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del deudor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 11))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Nombre:");

        jTFNombreDeudorPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreDeudorPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNombreDeudorPagareFocusGained(evt);
            }
        });
        jTFNombreDeudorPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNombreDeudorPagareKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("C.I.No.:");

        jTFCINroPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCINroPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCINroPagareFocusGained(evt);
            }
        });
        jTFCINroPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCINroPagareKeyPressed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Dirección:");

        jTFDireccionPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDireccionPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDireccionPagareFocusGained(evt);
            }
        });
        jTFDireccionPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDireccionPagareKeyPressed(evt);
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
                        .addGap(6, 6, 6)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreDeudorPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCINroPagare))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDireccionPagare)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTFNombreDeudorPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jTFCINroPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTFDireccionPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jBImprimirPagare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBImprimirPagare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimirPagare.setText("Imprimir Pagaré");
        jBImprimirPagare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirPagareActionPerformed(evt);
            }
        });

        jBCancelarPagare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBCancelarPagare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelarPagare.setText("Cancelar y Salir");
        jBCancelarPagare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarPagareActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Beneficiario:");

        jTFBeneficiarioPagare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFBeneficiarioPagare.setText("***");
        jTFBeneficiarioPagare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFBeneficiarioPagareFocusGained(evt);
            }
        });
        jTFBeneficiarioPagare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFBeneficiarioPagareKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPPagareLayout = new javax.swing.GroupLayout(jPPagare);
        jPPagare.setLayout(jPPagareLayout);
        jPPagareLayout.setHorizontalGroup(
            jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPPagareLayout.createSequentialGroup()
                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPPagareLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPPagareLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPPagareLayout.createSequentialGroup()
                                .addComponent(jBImprimirPagare)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBCancelarPagare))
                            .addGroup(jPPagareLayout.createSequentialGroup()
                                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jFTFMontoPagare, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                                    .addComponent(jFTFFechaEmisionPagare))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPPagareLayout.createSequentialGroup()
                                        .addComponent(jCBMonedaPagare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLNombreMonedaPagare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPPagareLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jFTFFechaVencimientoPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFPctInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPPagareLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNombreCiudadPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFBeneficiarioPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(13, 13, 13))
        );
        jPPagareLayout.setVerticalGroup(
            jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPPagareLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFNombreCiudadPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jTFBeneficiarioPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jFTFMontoPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBMonedaPagare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLNombreMonedaPagare)
                    .addComponent(jLabel10)
                    .addComponent(jTFPctInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jFTFFechaEmisionPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jFTFFechaVencimientoPagare, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPPagareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBImprimirPagare)
                    .addComponent(jBCancelarPagare))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pagaré", jPPagare);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jCBMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMonedaActionPerformed
        getNombreMoneda();
    }//GEN-LAST:event_jCBMonedaActionPerformed

    private void jTFNroReciboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroReciboKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNombreCiudad.grabFocus();
        }
    }//GEN-LAST:event_jTFNroReciboKeyPressed

    private void jTFNombreCiudadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNombreCiudadKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFMontoRecibo.grabFocus();
        }
    }//GEN-LAST:event_jTFNombreCiudadKeyPressed

    private void jTFRecibidoDeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFRecibidoDeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFFecha.grabFocus();
        }
    }//GEN-LAST:event_jTFRecibidoDeKeyPressed

    private void jFTFFechaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFFechaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFConcepto.grabFocus();
        }
    }//GEN-LAST:event_jFTFFechaKeyPressed

    private void jTFConceptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFConceptoFocusGained
        jTFConcepto.selectAll();
    }//GEN-LAST:event_jTFConceptoFocusGained

    private void jFTFFechaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFFechaFocusGained
        jFTFFecha.selectAll();
    }//GEN-LAST:event_jFTFFechaFocusGained

    private void jTFRecibidoDeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRecibidoDeFocusGained
        jTFRecibidoDe.selectAll();
    }//GEN-LAST:event_jTFRecibidoDeFocusGained

    private void jTFNombreCiudadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNombreCiudadFocusGained
        jTFNombreCiudad.selectAll();
    }//GEN-LAST:event_jTFNombreCiudadFocusGained

    private void jTFNroReciboFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroReciboFocusGained
        jTFNroRecibo.selectAll();
    }//GEN-LAST:event_jTFNroReciboFocusGained

    private void jCBMonedaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBMonedaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFRecibidoDe.grabFocus();
        }
    }//GEN-LAST:event_jCBMonedaKeyPressed

    private void jTFConceptoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFConceptoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBImprimirRecibo.grabFocus();
        }
    }//GEN-LAST:event_jTFConceptoKeyPressed

    private void jBCancelarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarReciboActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarReciboActionPerformed

    private void jTFNombreCiudadPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNombreCiudadPagareFocusGained
        jTFNombreCiudadPagare.selectAll();
    }//GEN-LAST:event_jTFNombreCiudadPagareFocusGained

    private void jTFNombreCiudadPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNombreCiudadPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFBeneficiarioPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFNombreCiudadPagareKeyPressed

    private void jFTFMontoPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFMontoPagareFocusGained
        jFTFMontoPagare.selectAll();
    }//GEN-LAST:event_jFTFMontoPagareFocusGained

    private void jFTFMontoPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFMontoPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jCBMonedaPagare.grabFocus();
        }
    }//GEN-LAST:event_jFTFMontoPagareKeyPressed

    private void jCBMonedaPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBMonedaPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFPctInteres.grabFocus();
        }
    }//GEN-LAST:event_jCBMonedaPagareKeyPressed

    private void jTFPctInteresFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFPctInteresFocusGained
        jTFPctInteres.selectAll();
    }//GEN-LAST:event_jTFPctInteresFocusGained

    private void jTFPctInteresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFPctInteresKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFFechaEmisionPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFPctInteresKeyPressed

    private void jFTFFechaEmisionPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFFechaEmisionPagareFocusGained
        jFTFFechaEmisionPagare.selectAll();
    }//GEN-LAST:event_jFTFFechaEmisionPagareFocusGained

    private void jFTFFechaEmisionPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFFechaEmisionPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFFechaVencimientoPagare.grabFocus();
        }
    }//GEN-LAST:event_jFTFFechaEmisionPagareKeyPressed

    private void jFTFFechaVencimientoPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTFFechaVencimientoPagareFocusGained
        jFTFFechaVencimientoPagare.selectAll();
    }//GEN-LAST:event_jFTFFechaVencimientoPagareFocusGained

    private void jFTFFechaVencimientoPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFTFFechaVencimientoPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNombreDeudorPagare.grabFocus();
        }
    }//GEN-LAST:event_jFTFFechaVencimientoPagareKeyPressed

    private void jTFNombreDeudorPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNombreDeudorPagareFocusGained
        jTFNombreDeudorPagare.selectAll();
    }//GEN-LAST:event_jTFNombreDeudorPagareFocusGained

    private void jTFNombreDeudorPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNombreDeudorPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCINroPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFNombreDeudorPagareKeyPressed

    private void jTFCINroPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCINroPagareFocusGained
        jTFCINroPagare.selectAll();
    }//GEN-LAST:event_jTFCINroPagareFocusGained

    private void jTFCINroPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCINroPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFDireccionPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFCINroPagareKeyPressed

    private void jTFDireccionPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDireccionPagareFocusGained
        jTFDireccionPagare.selectAll();
    }//GEN-LAST:event_jTFDireccionPagareFocusGained

    private void jTFDireccionPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDireccionPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBImprimirPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFDireccionPagareKeyPressed

    private void jCBMonedaPagareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMonedaPagareActionPerformed
        getNombreMonedaPagare();
    }//GEN-LAST:event_jCBMonedaPagareActionPerformed

    private void jBImprimirReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirReciboActionPerformed
        imprimirRecibo();        
    }//GEN-LAST:event_jBImprimirReciboActionPerformed

    private void jTFMontoReciboFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFMontoReciboFocusGained
        jTFMontoRecibo.setText(jTFMontoRecibo.getText().trim().replace(",", ""));
        jTFMontoRecibo.selectAll();
    }//GEN-LAST:event_jTFMontoReciboFocusGained

    private void jTFMontoReciboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFMontoReciboKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFMontoRecibo.setText(decimalFormat.format(Double.parseDouble(jTFMontoRecibo.getText().trim())));
            jTFRecibidoDe.grabFocus();
        }
    }//GEN-LAST:event_jTFMontoReciboKeyPressed

    private void jTFBeneficiarioPagareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFBeneficiarioPagareFocusGained
        jTFBeneficiarioPagare.selectAll();
    }//GEN-LAST:event_jTFBeneficiarioPagareFocusGained

    private void jTFBeneficiarioPagareKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFBeneficiarioPagareKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jFTFMontoPagare.grabFocus();
        }
    }//GEN-LAST:event_jTFBeneficiarioPagareKeyPressed

    private void jBImprimirPagareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirPagareActionPerformed
        imprimirPagare();
    }//GEN-LAST:event_jBImprimirPagareActionPerformed

    private void jBCancelarPagareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarPagareActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBCancelarPagareActionPerformed

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
            java.util.logging.Logger.getLogger(ImpresionRecibosPagares.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImpresionRecibosPagares.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImpresionRecibosPagares.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImpresionRecibosPagares.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ImpresionRecibosPagares dialog = new ImpresionRecibosPagares(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBCancelarPagare;
    private javax.swing.JButton jBCancelarRecibo;
    private javax.swing.JButton jBImprimirPagare;
    private javax.swing.JButton jBImprimirRecibo;
    private javax.swing.JComboBox<String> jCBMoneda;
    private javax.swing.JComboBox<String> jCBMonedaPagare;
    private javax.swing.JFormattedTextField jFTFFecha;
    private javax.swing.JFormattedTextField jFTFFechaEmisionPagare;
    private javax.swing.JFormattedTextField jFTFFechaVencimientoPagare;
    private javax.swing.JFormattedTextField jFTFMontoPagare;
    private javax.swing.JLabel jLNombreMoneda;
    private javax.swing.JLabel jLNombreMonedaPagare;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPPagare;
    private javax.swing.JPanel jPRecibo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTFBeneficiarioPagare;
    private javax.swing.JTextField jTFCINroPagare;
    private javax.swing.JTextField jTFConcepto;
    private javax.swing.JTextField jTFDireccionPagare;
    private javax.swing.JTextField jTFMontoRecibo;
    private javax.swing.JTextField jTFNombreCiudad;
    private javax.swing.JTextField jTFNombreCiudadPagare;
    private javax.swing.JTextField jTFNombreDeudorPagare;
    private javax.swing.JTextField jTFNroRecibo;
    private javax.swing.JTextField jTFPctInteres;
    private javax.swing.JTextField jTFRecibidoDe;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
