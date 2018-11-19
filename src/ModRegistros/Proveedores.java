/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import beans.ProveedorBean;
import controls.BarrioCtrl;
import controls.CiudadCtrl;
import controls.PaisCtrl;
import controls.ProveedorCtrl;
import controls.UsuarioCtrl;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.DBManager;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableGrid;
import utiles.Utiles;
import views.busca.BuscaProveedor;
import views.busca.DlgConsultas;

/**
 *
 * @author Andres
 */
public class Proveedores extends javax.swing.JDialog {

    /**
     * Creates new form Proveedores
     */
    private ResultSet resultSetLineas = null;
    private TableGrid tGrid = new TableGrid(null);
    private Vector colLineas = new Vector();

    boolean esNuevo = false;

    public Proveedores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setConfigCampos();
        cargaProveedor();
    }

    private void setConfigCampos() {

        jTFCondPago.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFContacto.setDocument(new MaxLength(20, "UPPER", "ALFA"));
        jTFDireccion.setDocument(new MaxLength(40, "UPPER", "ALFA"));
        jTFEmail.setDocument(new MaxLength(40, "LOWER", "ALFA"));
        jTFFax.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFCodBarrio.setDocument(new MaxLength(5, "", "ENTERO"));
        jTFCodCiudad.setDocument(new MaxLength(5, "", "ENTERO"));
        jTFCodPais.setDocument(new MaxLength(5, "", "ENTERO"));
        jTFRazonSoc.setDocument(new MaxLength(40, "UPPER", "ALFA"));
        jTFRuc.setDocument(new MaxLength(10, "", "ENTERO"));
        jTFTelefono.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFTelefonoContacto.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFWeb.setDocument(new MaxLength(40, "LOWER", "ALFA"));
        
        jTFCondPago.addFocusListener(new Focus());
        jTFContacto.addFocusListener(new Focus());
        jTFDireccion.addFocusListener(new Focus());
        jTFEmail.addFocusListener(new Focus());
        jTFFax.addFocusListener(new Focus());
        jTFCodBarrio.addFocusListener(new Focus());
        jTFCodCiudad.addFocusListener(new Focus());
        jTFCodPais.addFocusListener(new Focus());
        jTFRazonSoc.addFocusListener(new Focus());
        jTFRuc.addFocusListener(new Focus());
        jTFTelefono.addFocusListener(new Focus());
        jTFTelefonoContacto.addFocusListener(new Focus());
        jTFWeb.addFocusListener(new Focus());
    }

    private void setComponentesCancelar() {
        jTFRuc.setEnabled(false);
        jTFRazonSoc.setEnabled(false);
        jTFDireccion.setEnabled(false);
        jTFCodCiudad.setEnabled(false);
        jTFCodBarrio.setEnabled(false);
        jTFCodPais.setEnabled(false);
        jTFTelefono.setEnabled(false);
        jTFFax.setEnabled(false);
        jTFCondPago.setEnabled(false);
        jTFEmail.setEnabled(false);
        jTFWeb.setEnabled(false);
        jTFContacto.setEnabled(false);
        jTFTelefonoContacto.setEnabled(false);
        jCBActivo.setEnabled(false);
        jBNuevo.grabFocus();

        jBBuscarCodigo.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGuardar.setEnabled(false);
        jBModificar.setEnabled(true);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
    }

    private void setComponentesModificar() {
        jTFRuc.setEnabled(true);
        jTFRazonSoc.setEnabled(true);
        jTFDireccion.setEnabled(true);
        jTFCodCiudad.setEnabled(true);
        jTFCodBarrio.setEnabled(true);
        jTFCodPais.setEnabled(true);
        jTFTelefono.setEnabled(true);
        jTFFax.setEnabled(true);
        jTFCondPago.setEnabled(true);
        jTFEmail.setEnabled(true);
        jTFWeb.setEnabled(true);
        jTFContacto.setEnabled(true);
        jTFTelefonoContacto.setEnabled(true);
        jCBActivo.setEnabled(true);

        jTFRuc.grabFocus();

        jBBuscarCodigo.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBGuardar.setEnabled(true);
        jBModificar.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
    }
    
    private void setComponentesNuevo() {
        jTFRuc.setEnabled(true);
        jTFRazonSoc.setEnabled(true);
        jTFDireccion.setEnabled(true);
        jTFCodCiudad.setEnabled(true);
        jTFCodBarrio.setEnabled(true);
        jTFCodPais.setEnabled(true);
        jTFTelefono.setEnabled(true);
        jTFFax.setEnabled(true);
        jTFCondPago.setEnabled(true);
        jTFEmail.setEnabled(true);
        jTFWeb.setEnabled(true);
        jTFContacto.setEnabled(true);
        jTFTelefonoContacto.setEnabled(true);
        jCBActivo.setEnabled(true);

        jTFRuc.grabFocus();

        jBBuscarCodigo.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBGuardar.setEnabled(true);
        jBModificar.setEnabled(false);
        jBNuevo.setEnabled(true);
        jBSalir.setEnabled(true);
    }

    private boolean verificaCampos() {

        if (jTFRuc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo RUC !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFRuc.grabFocus();
            return false;
        }
        if (jTFRazonSoc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Debe llenar el campo RAZON SOCIAL !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFRazonSoc.grabFocus();
            return false;
        }
        if (jTFDireccion.getText().trim().equals("")) {
            jTFDireccion.setText("-");
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo DIRECCION !!!", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (jTFCodCiudad.getText().trim().equals("0") || jTFCodCiudad.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Problemas informando CIUDAD !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodCiudad.grabFocus();
            return false;
        }
        if (jTFCodBarrio.getText().trim().equals("0") || jTFCodBarrio.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Problemas informando BARRIO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodBarrio.grabFocus();
            return false;
        }
        if (jTFCodPais.getText().trim().equals("0") || jTFCodPais.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Problemas informando PAIS !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodBarrio.grabFocus();
            return false;
        }
        if (jTFTelefono.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo TELEFONO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFTelefono.grabFocus();
            return false;
        }
        if (jTFFax.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo FAX !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFFax.grabFocus();
            return false;
        }
        if (jTFCondPago.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo COND PAGO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCondPago.grabFocus();
            return false;
        }
        if (jTFEmail.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo EMAIL !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFEmail.setText("-");
            jTFEmail.grabFocus();
            return false;
        }
        if (jTFWeb.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo WEB !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFWeb.setText("-");
            jTFWeb.grabFocus();
            return false;
        }
        if (jTFContacto.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo CONTACTO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFContacto.setText("-");
            return false;
        }
        if (jTFTelefonoContacto.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo TELEFONO CONTACTO !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFTelefonoContacto.setText("0");
            return false;
        }

        return true;
    }

    private int calculaDigito(String p_numero, int p_basemax) {
        int result = 0;
        int v_total = 0;
        int v_resto = 0;
        int k = 0;
        int v_numero_aux = 0;
        String v_numero_al = "";
        String v_caracter = "";
        int v_digit = 0;
        int i = 0;
        char letra = '0';
        // cambia la ultima letra por ascii en caso de que la cedula termine en letras
        for (i = 0; i < p_numero.length(); i++) {
            v_caracter = p_numero.substring(i, i + 1);
            letra = v_caracter.charAt(0);
            if (v_caracter.equals("0") || (v_caracter.equals("1"))
                    || v_caracter.equals("2") || (v_caracter.equals("3"))
                    || v_caracter.equals("4") || (v_caracter.equals("5"))
                    || v_caracter.equals("6") || (v_caracter.equals("7"))
                    || v_caracter.equals("8") || (v_caracter.equals("9"))) { // control del 0 al 9

                v_numero_al = v_numero_al + v_caracter;
            } else {
                v_numero_al = v_numero_al + String.valueOf((int) letra);
            }
        }
        k = 2;
        v_total = 0;
        for (int x = v_numero_al.length(); x > 0; x--) {
            if (k > p_basemax) {
                k = 2;
            }
            v_numero_aux = Integer.valueOf(v_numero_al.substring(x - 1, x));
            v_total = v_total + (v_numero_aux * k);
            k = k + 1;
        }
        v_resto = v_total % 11;
        if (v_resto > 1) {
            result = 11 - v_resto;
        } else {
            result = 0;
        }
        return result;
    }

    private void grabarProveedor() {
        try {
            String fecVigencia = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                java.util.Date d = new java.util.Date();
                fecVigencia = sdf.format(d);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Formatando Fecha...");
            }

            ProveedorBean pBean = new ProveedorBean();
            pBean.setActivo(jCBActivo.getSelectedItem().toString());
            pBean.setCodBarrio(Integer.valueOf(jTFCodBarrio.getText().trim()));
            pBean.setCodCiudad(Integer.valueOf(jTFCodCiudad.getText().trim()));
            pBean.setCodPais(Integer.valueOf(jTFCodPais.getText().trim()));
            pBean.setCodProveedor(Integer.valueOf(jTFCodigo.getText().trim()));
            pBean.setCodUsuario(FormMain.codUsuario);
            pBean.setCondPago(Integer.valueOf(jTFCondPago.getText().trim()));
            pBean.setContacto(jTFContacto.getText().trim());
            pBean.setDireccion(jTFDireccion.getText().trim());
            pBean.setEmail(jTFEmail.getText().trim());
            pBean.setFax(jTFFax.getText().trim());
            pBean.setFecVigencia(fecVigencia);
            pBean.setRazonSocial(jTFRazonSoc.getText().trim());
            pBean.setRucProveedor(jTFRuc.getText().trim() + "-" + jTFDv.getText().trim());
            pBean.setTelContacto(jTFTelefonoContacto.getText().trim());
            pBean.setTelefono(jTFTelefono.getText().trim());
            pBean.setWeb(jTFWeb.getText().trim());

            ProveedorCtrl pCtrl = new ProveedorCtrl();
            if (esNuevo) {
                if (pCtrl.catastraProveedor(pBean)) {
                    JOptionPane.showMessageDialog(this, "Proveedor registrado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setComponentesGrabado();
                    cargaProveedor();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al Registrar Proveedor !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } else { // modificacion
                if (pCtrl.modificaProveedor(pBean)) {
                    JOptionPane.showMessageDialog(this, "Proveedor Modificado con Suceso !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setComponentesGrabado();
                } else {
                    JOptionPane.showMessageDialog(this, "ATENCION: Error al Modificar Proveedor !!!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar Proveedor !!!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setComponentesGrabado() {

        jTFRuc.setEnabled(false);
        jTFRazonSoc.setEnabled(false);
        jTFDireccion.setEnabled(false);
        jTFCodCiudad.setEnabled(false);
        jTFCodBarrio.setEnabled(false);
        jTFCodPais.setEnabled(false);
        jTFTelefono.setEnabled(false);
        jTFFax.setEnabled(false);
        jTFCondPago.setEnabled(false);
        jTFEmail.setEnabled(false);
        jTFWeb.setEnabled(false);
        jTFContacto.setEnabled(false);
        jTFTelefonoContacto.setEnabled(false);
        jCBActivo.setEnabled(false);

        jBNuevo.setEnabled(true);
        jBModificar.setEnabled(true);
        jBBuscarCodigo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBGuardar.setEnabled(false);
        jBNuevo.grabFocus();
    }

    private void cargaProveedor() {
        ProveedorCtrl pCtrl = new ProveedorCtrl();
        int max = pCtrl.buscaMaxCodProveedor();
        if (max > 0) {
            getProveedor(max);
        } else {
            jBModificar.setEnabled(false);
        }
    }

    private void getProveedor(int codProveedor) {
        ProveedorCtrl pCtrl = new ProveedorCtrl();
        ProveedorBean pBean = pCtrl.buscaProveedorCodigo(codProveedor);
        if (pBean != null) {
            String[] digito = pBean.getRucProveedor().split("-");
            String dv = digito[1];
            jTFDv.setText(dv);
            jTFEmail.setText(pBean.getEmail());
            jTFFax.setText(pBean.getFax());
            jTFFecVigencia.setText(pBean.getFecVigencia());
            //jCBActivo.
            jTFRazonSoc.setText(pBean.getRazonSocial());
            jTFRuc.setText(digito[0]);
            jTFTelefono.setText(pBean.getTelefono());
            jTFTelefonoContacto.setText(pBean.getTelContacto());
            jTFUsuario.setText(pBean.getCodUsuario() + " " + getNombreUsuario(String.valueOf(pBean.getCodUsuario())));
            jTFWeb.setText(pBean.getWeb());
            jTFDireccion.setText(pBean.getDireccion());
            jTFCodBarrio.setText(String.valueOf(pBean.getCodBarrio()));
            jTFCodCiudad.setText(String.valueOf(pBean.getCodCiudad()));
            jTFCodPais.setText(String.valueOf(pBean.getCodPais()));
            jTFNombreBarrio.setText(getNombreBarrio(jTFCodBarrio.getText().trim()));
            jTFNombreCiudad.setText(getNombreCiudad(jTFCodCiudad.getText().trim()));
            jTFNombrePais.setText(getNombrePais(jTFCodPais.getText().trim()));
            jTFCodigo.setText(String.valueOf(pBean.getCodProveedor()));
            jTFContacto.setText(pBean.getContacto());
            jTFCondPago.setText(String.valueOf(pBean.getCondPago()));

        }
    }

    private void cargaLinea() {
        if (colLineas.isEmpty()) {
            colLineas.addElement("Código");
            colLineas.addElement("Razón Social");
            colLineas.addElement("RUC");
            colLineas.addElement("Teléfono");
        }

        String sql = "SELECT cod_proveedor, razon_soc, ruc_proveedor, telefono "
                + "FROM proveedor WHERE cod_proveedor = " + jTFCodigo.getText().trim() + " "
                + "ORDER BY cod_proveedor";

        resultSetLineas = DBManager.ejecutarDSL(sql);
        tGrid.setDataSet(resultSetLineas, colLineas);
        tGrid.setVisible(true);
        tGrid.repaint();
    }

    private String getCodigoProveedor() {
        ProveedorCtrl pCtrl = new ProveedorCtrl();
        int result = pCtrl.buscaMaxCodProveedor();
        result = result + 1;
        return String.valueOf(result);
    }

    private String getNombreUsuario(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            UsuarioCtrl usuarioCtrl = new UsuarioCtrl();
            result = usuarioCtrl.getNombreUsuario(Integer.valueOf(codigo));
            usuarioCtrl = null;
        }
        return result;
    }

    private String getNombreBarrio(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            BarrioCtrl ctrl = new BarrioCtrl();
            result = ctrl.getNombreBarrio(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }

    private String getNombreCiudad(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            CiudadCtrl ctrl = new CiudadCtrl();
            result = ctrl.getNombreCiudad(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }

    private String getNombrePais(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            PaisCtrl ctrl = new PaisCtrl();
            result = ctrl.getNombrePais(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }

    private void llenarCamposRegistro() {

        jTFCodigo.setText(getCodigoProveedor());
        jTFRazonSoc.setText("");
        jTFRuc.setText("");
        jTFDv.setText("");
        jTFDireccion.setText("-");
        jTFCodCiudad.setText("1");
        jTFCodBarrio.setText("1");
        jTFCodPais.setText("1");
        jTFTelefono.setText("0");
        jTFFax.setText("0");
        jTFCondPago.setText("0");
        jTFEmail.setText("@");
        jTFWeb.setText("www");
        jTFContacto.setText("-");
        jTFTelefonoContacto.setText("0");

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTFCodigo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFRazonSoc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFRuc = new javax.swing.JTextField();
        jTFDv = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFDireccion = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFCodCiudad = new javax.swing.JTextField();
        jTFNombreCiudad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTFCodBarrio = new javax.swing.JTextField();
        jTFNombreBarrio = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFTelefono = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFFax = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jCBActivo = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jTFCondPago = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTFEmail = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFWeb = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFCodPais = new javax.swing.JTextField();
        jTFNombrePais = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFContacto = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFTelefonoContacto = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTFFecVigencia = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTFUsuario = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBGuardar = new javax.swing.JButton();
        jBBuscarCodigo = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Mantenimiento de Artículos");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Proveedores");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("MANTENIMIENTO DE PROVEEDORES");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Datos "));

        jLabel3.setText("Código:");

        jTFCodigo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodigo.setEnabled(false);
        jTFCodigo.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel4.setText("Razón Social:");

        jTFRazonSoc.setEnabled(false);
        jTFRazonSoc.setMinimumSize(new java.awt.Dimension(6, 25));
        jTFRazonSoc.setPreferredSize(new java.awt.Dimension(59, 25));
        jTFRazonSoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFRazonSocFocusGained(evt);
            }
        });
        jTFRazonSoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFRazonSocKeyPressed(evt);
            }
        });

        jLabel5.setText("RUC:");

        jTFRuc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFRuc.setEnabled(false);
        jTFRuc.setPreferredSize(new java.awt.Dimension(59, 25));
        jTFRuc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFRucFocusGained(evt);
            }
        });
        jTFRuc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFRucKeyPressed(evt);
            }
        });

        jTFDv.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDv.setEnabled(false);
        jTFDv.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel6.setText("Dirección:");

        jTFDireccion.setEnabled(false);
        jTFDireccion.setPreferredSize(new java.awt.Dimension(6, 25));
        jTFDireccion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDireccionFocusGained(evt);
            }
        });
        jTFDireccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDireccionKeyPressed(evt);
            }
        });

        jLabel7.setText("Ciudad:");

        jTFCodCiudad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCiudad.setEnabled(false);
        jTFCodCiudad.setPreferredSize(new java.awt.Dimension(59, 25));
        jTFCodCiudad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCiudadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodCiudadFocusLost(evt);
            }
        });
        jTFCodCiudad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCiudadKeyPressed(evt);
            }
        });

        jTFNombreCiudad.setEnabled(false);
        jTFNombreCiudad.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel8.setText("Barrio:");

        jTFCodBarrio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodBarrio.setEnabled(false);
        jTFCodBarrio.setPreferredSize(new java.awt.Dimension(59, 25));
        jTFCodBarrio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodBarrioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodBarrioFocusLost(evt);
            }
        });
        jTFCodBarrio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodBarrioKeyPressed(evt);
            }
        });

        jTFNombreBarrio.setEnabled(false);
        jTFNombreBarrio.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel9.setText("Teléfono:");

        jTFTelefono.setEnabled(false);
        jTFTelefono.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTelefonoFocusGained(evt);
            }
        });
        jTFTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTelefonoKeyPressed(evt);
            }
        });

        jLabel10.setText("Fax:");

        jTFFax.setEnabled(false);
        jTFFax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFaxFocusGained(evt);
            }
        });
        jTFFax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFaxKeyPressed(evt);
            }
        });

        jLabel11.setText("Activo:");

        jCBActivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBActivo.setEnabled(false);

        jLabel12.setText("Cond. Pago:");

        jTFCondPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCondPago.setEnabled(false);
        jTFCondPago.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCondPagoFocusGained(evt);
            }
        });
        jTFCondPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCondPagoKeyPressed(evt);
            }
        });

        jLabel13.setText("días.");

        jLabel14.setText("Email:");

        jTFEmail.setEnabled(false);
        jTFEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFEmailFocusGained(evt);
            }
        });
        jTFEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFEmailKeyPressed(evt);
            }
        });

        jLabel15.setText("Web:");

        jTFWeb.setEnabled(false);
        jTFWeb.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFWebFocusGained(evt);
            }
        });
        jTFWeb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFWebKeyPressed(evt);
            }
        });

        jLabel16.setText("País:");

        jTFCodPais.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodPais.setEnabled(false);
        jTFCodPais.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodPaisFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodPaisFocusLost(evt);
            }
        });
        jTFCodPais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodPaisKeyPressed(evt);
            }
        });

        jTFNombrePais.setEnabled(false);

        jLabel17.setText("Contacto:");

        jTFContacto.setEnabled(false);
        jTFContacto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFContactoFocusGained(evt);
            }
        });
        jTFContacto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFContactoKeyPressed(evt);
            }
        });

        jLabel18.setText("Teléfono:");

        jTFTelefonoContacto.setEnabled(false);
        jTFTelefonoContacto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTelefonoContactoFocusGained(evt);
            }
        });
        jTFTelefonoContacto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTelefonoContactoKeyPressed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFRazonSoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTFDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTFCodCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFNombreCiudad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTFCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFRuc, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTFCodPais)
                                    .addComponent(jTFCodBarrio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFNombreBarrio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTFNombrePais)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(6, 6, 6)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTFTelefono)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFFax, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFCondPago, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFContacto)
                                    .addComponent(jTFEmail))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFWeb, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFTelefonoContacto)))))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTFRuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFRazonSoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTFDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFCodCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFCodBarrio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombreBarrio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCodPais, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFNombrePais, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jTFFax, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(jTFCondPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(jTFTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTFEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jTFWeb, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTFContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jTFTelefonoContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Últimas modificaciones"));

        jLabel19.setText("Fecha:");

        jTFFecVigencia.setEnabled(false);

        jLabel20.setText("Usuario:");

        jTFUsuario.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFFecVigencia, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFUsuario)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTFFecVigencia, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jTFUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Opciones"));

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

        jBGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBGuardar.setText("Grabar");
        jBGuardar.setEnabled(false);
        jBGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGuardarActionPerformed(evt);
            }
        });

        jBBuscarCodigo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar24.png"))); // NOI18N
        jBBuscarCodigo.setText("Buscar");
        jBBuscarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscarCodigoActionPerformed(evt);
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
        jBCancelar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBCancelarKeyPressed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
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
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscarCodigo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscarCodigo, jBCancelar, jBGuardar, jBModificar, jBNuevo, jBSalir});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBModificar)
                    .addComponent(jBGuardar)
                    .addComponent(jBBuscarCodigo)
                    .addComponent(jBCancelar)
                    .addComponent(jBSalir))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscarCodigo, jBCancelar, jBGuardar, jBModificar, jBNuevo, jBSalir});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        setSize(new java.awt.Dimension(709, 595));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        esNuevo = true;
        setComponentesNuevo();
        llenarCamposRegistro();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jTFRucKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFRucKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (jTFRuc.getText().trim().equals("")) {
                jTFRuc.setText("0");
                jTFRazonSoc.grabFocus();
            } else {
                jTFDv.setText(String.valueOf(calculaDigito(jTFRuc.getText(), 11)));
                jTFRazonSoc.grabFocus();
            }
        }
    }//GEN-LAST:event_jTFRucKeyPressed

    private void jTFRazonSocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFRazonSocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFDireccion.grabFocus();
        }
    }//GEN-LAST:event_jTFRazonSocKeyPressed

    private void jTFDireccionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDireccionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodCiudad.grabFocus();
        }
    }//GEN-LAST:event_jTFDireccionKeyPressed

    private void jTFCodCiudadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCiudadKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodBarrio.grabFocus();
        }

        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Ciudad");
                grupo.dConsultas("ciudad", "nombre", "cod_ciudad", "nombre", "fec_vigencia", null, "Codigo", "Nombre", "Registro", null);
                grupo.setText(jTFCodCiudad);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F10){
            Ciudades ciudades = new Ciudades(new JFrame(), true);
            ciudades.pack();
            ciudades.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodCiudadKeyPressed

    private void jTFCodBarrioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodBarrioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodPais.grabFocus();
        }

        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Barrio");
                grupo.dConsultas("barrio", "nombre", "cod_barrio", "nombre", "fec_vigencia", null, "Codigo", "Nombre", "Registro", null);
                grupo.setText(jTFCodBarrio);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_F10){
            Barrios barrios = new Barrios(new JFrame(), true);
            barrios.pack();
            barrios.setVisible(true);
        }
    }//GEN-LAST:event_jTFCodBarrioKeyPressed

    private void jTFCodPaisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodPaisKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFTelefono.grabFocus();
        }

        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Pais");
                grupo.dConsultas("pais", "nombre", "cod_ciudad", "nombre", "fec_vigencia", null, "Codigo", "Nombre", "Registro", null);
                grupo.setText(jTFCodPais);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodPaisKeyPressed

    private void jTFTelefonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTelefonoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFFax.grabFocus();
        }
    }//GEN-LAST:event_jTFTelefonoKeyPressed

    private void jTFFaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFaxKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCondPago.grabFocus();
        }
    }//GEN-LAST:event_jTFFaxKeyPressed

    private void jTFCondPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCondPagoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFEmail.grabFocus();
        }
    }//GEN-LAST:event_jTFCondPagoKeyPressed

    private void jTFEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFEmailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFWeb.grabFocus();
        }
    }//GEN-LAST:event_jTFEmailKeyPressed

    private void jTFWebKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFWebKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFContacto.grabFocus();
        }
    }//GEN-LAST:event_jTFWebKeyPressed

    private void jTFContactoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFContactoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFTelefonoContacto.grabFocus();
        }
    }//GEN-LAST:event_jTFContactoKeyPressed

    private void jTFTelefonoContactoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTelefonoContactoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jBGuardar.grabFocus();
        }
    }//GEN-LAST:event_jTFTelefonoContactoKeyPressed

    private void jBCancelarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBCancelarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBCancelarKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setComponentesCancelar();
        cargaProveedor();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGuardarActionPerformed
        if (verificaCampos()) {
            //JOptionPane.showMessageDialog(this, "ATENCION: OK los campos están completos !!!", "Error", JOptionPane.WARNING_MESSAGE);
            grabarProveedor();
        }
    }//GEN-LAST:event_jBGuardarActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        esNuevo = false;
        setComponentesModificar();
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jTFCodCiudadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCiudadFocusLost
        jTFNombreCiudad.setText(getNombreCiudad(jTFCodCiudad.getText()));
    }//GEN-LAST:event_jTFCodCiudadFocusLost

    private void jTFCodBarrioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBarrioFocusLost
        jTFNombreBarrio.setText(getNombreBarrio(jTFCodBarrio.getText().trim()));
    }//GEN-LAST:event_jTFCodBarrioFocusLost

    private void jTFCodPaisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodPaisFocusLost
        jTFNombrePais.setText(getNombrePais(jTFCodPais.getText().trim()));
    }//GEN-LAST:event_jTFCodPaisFocusLost

    private void jTFCodCiudadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCiudadFocusGained
        jTFCodCiudad.selectAll();
    }//GEN-LAST:event_jTFCodCiudadFocusGained

    private void jTFCodBarrioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBarrioFocusGained
        jTFCodBarrio.selectAll();
    }//GEN-LAST:event_jTFCodBarrioFocusGained

    private void jTFCodPaisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodPaisFocusGained
        jTFCodPais.selectAll();
    }//GEN-LAST:event_jTFCodPaisFocusGained

    private void jBBuscarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarCodigoActionPerformed
        BuscaProveedor bProveedor = new BuscaProveedor(new JFrame(), true);
        bProveedor.pack();
        bProveedor.setVisible(true);
        if (bProveedor.codigo != 0) {
            getProveedor(bProveedor.codigo);
            cargaLinea();
        }
        jBModificar.grabFocus();
    }//GEN-LAST:event_jBBuscarCodigoActionPerformed

    private void jTFRucFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRucFocusGained
        jTFRuc.selectAll();
    }//GEN-LAST:event_jTFRucFocusGained

    private void jTFRazonSocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRazonSocFocusGained
        jTFRazonSoc.selectAll();
    }//GEN-LAST:event_jTFRazonSocFocusGained

    private void jTFDireccionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDireccionFocusGained
        jTFDireccion.selectAll();
    }//GEN-LAST:event_jTFDireccionFocusGained

    private void jTFTelefonoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTelefonoFocusGained
        jTFTelefono.selectAll();
    }//GEN-LAST:event_jTFTelefonoFocusGained

    private void jTFFaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFaxFocusGained
        jTFFax.selectAll();
    }//GEN-LAST:event_jTFFaxFocusGained

    private void jTFCondPagoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCondPagoFocusGained
        jTFCondPago.selectAll();
    }//GEN-LAST:event_jTFCondPagoFocusGained

    private void jTFEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFEmailFocusGained
        jTFEmail.selectAll();
    }//GEN-LAST:event_jTFEmailFocusGained

    private void jTFWebFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFWebFocusGained
        jTFWeb.selectAll();
    }//GEN-LAST:event_jTFWebFocusGained

    private void jTFContactoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFContactoFocusGained
        jTFContacto.selectAll();
    }//GEN-LAST:event_jTFContactoFocusGained

    private void jTFTelefonoContactoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTelefonoContactoFocusGained
        jTFTelefonoContacto.selectAll();
    }//GEN-LAST:event_jTFTelefonoContactoFocusGained

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
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Proveedores dialog = new Proveedores(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBBuscarCodigo;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBGuardar;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBActivo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField jTFCodBarrio;
    private javax.swing.JTextField jTFCodCiudad;
    private javax.swing.JTextField jTFCodPais;
    private javax.swing.JTextField jTFCodigo;
    private javax.swing.JTextField jTFCondPago;
    private javax.swing.JTextField jTFContacto;
    private javax.swing.JTextField jTFDireccion;
    private javax.swing.JTextField jTFDv;
    private javax.swing.JTextField jTFEmail;
    private javax.swing.JTextField jTFFax;
    private javax.swing.JTextField jTFFecVigencia;
    private javax.swing.JTextField jTFNombreBarrio;
    private javax.swing.JTextField jTFNombreCiudad;
    private javax.swing.JTextField jTFNombrePais;
    private javax.swing.JTextField jTFRazonSoc;
    private javax.swing.JTextField jTFRuc;
    private javax.swing.JTextField jTFTelefono;
    private javax.swing.JTextField jTFTelefonoContacto;
    private javax.swing.JTextField jTFUsuario;
    private javax.swing.JTextField jTFWeb;
    // End of variables declaration//GEN-END:variables
}
