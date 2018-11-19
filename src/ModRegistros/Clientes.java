/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros;

import ModRRHH.GenericInfoAuditoria;
import beans.ClienteBean;
import controls.BarrioCtrl;
import controls.CiudadCtrl;
import controls.ClienteCtrl;
import controls.DptoCtrl;
import controls.PaisCtrl;
import controls.UsuarioCtrl;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import principal.FormMain;
import utiles.DBManager;
import utiles.FechaInputVerifier;
import utiles.Focus;
import utiles.InfoErrores;
import utiles.MaxLength;
import utiles.TableGrid;
import utiles.Utiles;
import views.busca.BuscaCliente;
import views.busca.DlgConsultas;

/**
 *
 *
 * @author Andres
 */
public class Clientes extends javax.swing.JDialog {

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    private static String usuario, fecha;
    private ResultSet resultSetLineas = null;
    private TableGrid tGrid = new TableGrid(null);
    private Vector colLineas = new Vector();
    boolean esNuevo = false;
    String mensajeCuentaGrabada = "";
    
    public Clientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configCampos();
        setEstadoComponentes(false);
        cargaCliente();
        jBNuevo.requestDefaultFocus();
        
    }

    private void setEstadoComponentes(boolean estado){
        jTFNroCi.setEnabled(estado);
        jTFRuc.setEnabled(estado);
        jTFNombreCliente.setEnabled(estado);
        jTFRucContacto.setEnabled(estado);
        jTFCodCiudad.setEnabled(estado);
        jTFCodDpto.setEnabled(estado);
        jTFCodBarrio.setEnabled(estado);
        jTFDireccion.setEnabled(estado);
        jTFTelefono.setEnabled(estado);
        jTFFax.setEnabled(estado);
        jTFCelular.setEnabled(estado);
        jTFEmail.setEnabled(estado);
        jTFFecNacimiento.setEnabled(estado);
        jCBSexo.setEnabled(estado);
        jCBEstadoCivil.setEnabled(estado);
        jTFCodPaisOrigen.setEnabled(estado);
        jTFDocPaisOrigen.setEnabled(estado);
        jTFDocInmigracion.setEnabled(estado);
        jCBActivo.setEnabled(estado);
        jCBAceptaCheque.setEnabled(estado);
        jCBCredHabilitado.setEnabled(estado);
        jTFCondPago.setEnabled(estado);
        jTFCodListaPrecio.setEnabled(estado);
        jTFCodGarante.setEnabled(estado);
        jTFLimiteCredito.setEnabled(estado);
        jTFDescuentoMax.setEnabled(estado);
        jCBEsEmpresa.setEnabled(estado);
        jCBEsContribuyente.setEnabled(estado);
        jTFNombreContacto.setEnabled(estado);
    }
    
    private void setEstadoBotonesNuevo(){
        jBNuevo.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBModificar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInfo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
    }
    
    private void setEstadoBotonesCancelar(){
        jBNuevo.setEnabled(true);
        jBGrabar.setEnabled(false);
        jBModificar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInfo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
    }
    
    private void setEstadoBotonesModificar(){
        jBNuevo.setEnabled(false);
        jBGrabar.setEnabled(true);
        jBModificar.setEnabled(false);
        jBCancelar.setEnabled(true);
        jBInfo.setEnabled(false);
        jBSalir.setEnabled(false);
        jBBuscar.setEnabled(false);
    }
    
    private void setEstadoBotonesGrabado(){
        jBNuevo.setEnabled(true);
        jBGrabar.setEnabled(false);
        jBModificar.setEnabled(true);
        jBCancelar.setEnabled(false);
        jBInfo.setEnabled(true);
        jBSalir.setEnabled(true);
        jBBuscar.setEnabled(true);
    }
    
    private void llenarCamposNuevo(){
        jCBEsEmpresa.setSelectedItem("N");
        jCBEsContribuyente.setSelectedItem("S");
        jTFNroCi.setText("");
        jTFRuc.setText("");
        jTFDv.setText("");
        jTFCodCliente.setText(getCodigoCliente());
        jTFNombreCliente.setText("");
        jTFRucContacto.setText("0");
        jTFNombreContacto.setText("SIN NOMBRE");
        jTFCodCiudad.setText("1");
        jTFNombreCiudad.setText("");
        jTFCodDpto.setText("1");
        jTFNombreDpto.setText("");
        jTFCodBarrio.setText("1");
        jTFNombreBarrio.setText("");
        jTFDireccion.setText("-");
        jTFTelefono.setText("0");
        jTFFax.setText("0");
        jTFCelular.setText("0");
        jTFEmail.setText("@");
        jTFFecNacimiento.setText("");
        jTFCodPaisOrigen.setText("1");
        jTFNombrePais.setText("");
        jTFDocPaisOrigen.setText("0");
        jTFDocInmigracion.setText("0");
        jTFCondPago.setText("0");
        jTFLimiteCredito.setText("0");
        jTFCodListaPrecio.setText("1");
        jTFDescListaPrecio.setText("");
        jTFCodGarante.setText("1");
        jTFNombreGarante.setText("");
        jTFDescuentoMax.setText("0");
        jTFCodZona.setText("1");
        jTFNombreZona.setText("GENERICA");
    }
    
    private void configCampos(){
        jTFCondPago.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFNroCi.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFRuc.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFDv.setDocument(new MaxLength(1, "", "ENTERO"));
        jTFNombreCliente.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFRucContacto.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFCodCiudad.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFCodDpto.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFCodBarrio.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFDireccion.setDocument(new MaxLength(35, "UPPER", "ALFA"));
        jTFTelefono.setDocument(new MaxLength(15, "UPPER", "ALFA"));
        jTFFax.setDocument(new MaxLength(15, "UPPER", "ALFA"));
        jTFCelular.setDocument(new MaxLength(15, "UPPER", "ALFA"));
        jTFEmail.setDocument(new MaxLength(35, "LOWER", "ALFA"));
        jTFCodZona.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFCodPaisOrigen.setDocument(new MaxLength(3, "", "ENTERO"));
        jTFCondPago.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFLimiteCredito.setDocument(new MaxLength(12, "", "ENTERO"));
        jTFCodListaPrecio.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFCodGarante.setDocument(new MaxLength(6, "", "ENTERO"));
        jTFDescuentoMax.setDocument(new MaxLength(2, "", "ENTERO"));
        jTFNombreContacto.setDocument(new MaxLength(30, "UPPER", "ALFA"));
        jTFFecNacimiento.setInputVerifier(new FechaInputVerifier(jTFFecNacimiento));
        
        
        jTFCondPago.addFocusListener(new Focus());
        jTFNroCi.addFocusListener(new Focus());
        jTFRuc.addFocusListener(new Focus());
        jTFDv.addFocusListener(new Focus());
        jTFNombreCliente.addFocusListener(new Focus());
        jTFRucContacto.addFocusListener(new Focus());
        jTFCodCiudad.addFocusListener(new Focus());
        jTFCodDpto.addFocusListener(new Focus());
        jTFCodBarrio.addFocusListener(new Focus());
        jTFDireccion.addFocusListener(new Focus());
        jTFTelefono.addFocusListener(new Focus());
        jTFFax.addFocusListener(new Focus());
        jTFCelular.addFocusListener(new Focus());
        jTFEmail.addFocusListener(new Focus());
        jTFCodZona.addFocusListener(new Focus());
        jTFCodPaisOrigen.addFocusListener(new Focus());
        jTFCondPago.addFocusListener(new Focus());
        jTFLimiteCredito.addFocusListener(new Focus());
        jTFCodListaPrecio.addFocusListener(new Focus());
        jTFCodGarante.addFocusListener(new Focus());
        jTFDescuentoMax.addFocusListener(new Focus());
        
    }
    
    private String getNombreCliente(String codigo){
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            ClienteCtrl ctrl = new ClienteCtrl();
            result = ctrl.getNombreCliente(Integer.valueOf(codigo));
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
    
    private String getNombreDpto(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            DptoCtrl ctrl = new DptoCtrl();
            result = ctrl.getNombreDpto(Integer.valueOf(codigo));
            ctrl = null;
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
    
    private String getNombreZona(String codigo) {
        String result = "";
        String sql = "SELECT descripcion FROM zona WHERE cod_zona = " + codigo;
        System.out.println("ZONA: " + sql);
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("descripcion");
                }else{
                    result = "INEXISTENTE";
                }
            }
        }catch(Exception ex){
        }finally{
            DBManager.CerrarStatements();
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
    
    private String getNombreUsuario(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            UsuarioCtrl ctrl = new UsuarioCtrl();
            result = ctrl.getNombreUsuario(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
   
    private String getDescListaPrecio(String codigo){
        String result = "";
        try{
            String sql = "SELECT descripcion FROM listaprecio WHERE cod_lista = " + codigo + " AND activa = 'S'";
            System.out.println("DESCRIPCION LISTA: " + sql);
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("descripcion");
                }else{
                    result = "INEXISTENTE";
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
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
    
    private void cargaCliente() {
        ClienteCtrl pCtrl = new ClienteCtrl();
        int max = pCtrl.buscaMaxCodCliente();
        if (max > 0) {
            getCliente(max);
        } else {
            jBModificar.setEnabled(false);
        }
    }
    
    private void getCliente(int codigo){
        ClienteCtrl pCtrl = new ClienteCtrl();
        ClienteBean bean = pCtrl.buscaClienteCodCliente(codigo);
        if(bean != null){
            jCBEsEmpresa.setSelectedItem(bean.getEsJuridica());
            jCBEsContribuyente.setSelectedItem(bean.getEsContribuyente());
            jTFNroCi.setText(bean.getCiContacto());
            String[] rucCliente = bean.getRucCliente().split("-");
            jTFRuc.setText(rucCliente[0]);
            jTFDv.setText(rucCliente[1]);
            jTFCodCliente.setText(String.valueOf(bean.getCodCliente()));
            jTFNombreCliente.setText(getNombreCliente(jTFCodCliente.getText().trim()));
            String[] contacto = bean.getContacto().split("-");
            jTFNombreContacto.setText(contacto[1]);
            jTFRucContacto.setText(contacto[0]);
            jTFCodCiudad.setText(String.valueOf(bean.getCodCiudad()));
            jTFCodDpto.setText(String.valueOf(bean.getCodDpto()));
            jTFCodBarrio.setText(String.valueOf(bean.getCodBarrio()));
            jTFDireccion.setText(bean.getDireccion());
            jTFTelefono.setText(bean.getTelefono());
            jTFFax.setText(bean.getFax());
            jTFCelular.setText(bean.getCelular());
            jTFEmail.setText(bean.getEmail());
            jTFFecNacimiento.setText(String.valueOf(bean.getFecNacimiento()));
            jCBSexo.setSelectedItem(bean.getSexo());
            jCBEstadoCivil.setSelectedItem(bean.getEstadoCivil());
            jTFCodZona.setText(String.valueOf(bean.getCodZona()));
            jTFNombreZona.setText(getNombreZona(jTFCodZona.getText().trim()));
            jTFCodPaisOrigen.setText(String.valueOf(bean.getCodPais()));
            jTFDocPaisOrigen.setText(bean.getCiOrigen());
            jTFDocInmigracion.setText(bean.getDocInmigracion());
            jCBActivo.setSelectedItem(bean.getActivo());
            jCBAceptaCheque.setSelectedItem(bean.getAceptaCheque());
            jCBCredHabilitado.setSelectedItem(getCuentaHabilitada(jTFCodCliente.getText().trim()));
            jTFCondPago.setText(String.valueOf(bean.getCondPago()));
            jTFCodListaPrecio.setText(String.valueOf(bean.getCodListaPrecio()));
            jTFDescListaPrecio.setText(getDescListaPrecio(jTFCodListaPrecio.getText().trim()));
            jTFCodGarante.setText(String.valueOf(bean.getCodGarante()));
            jTFLimiteCredito.setText(String.valueOf(bean.getLimiteCredito()));
            jTFDescuentoMax.setText(String.valueOf(bean.getPctMaxDescuento()));
            usuario = bean.getCodUsuario() + " - " + getNombreUsuario(String.valueOf(bean.getCodUsuario()));
            fecha = bean.getFecVigencia();
            getDatosCuentaHabilitada(jTFCodCliente.getText().trim());
        }
    }
    
    private void getDatosCuentaHabilitada(String codigoCliente){
        String sql = "SELECT cod_cuenta, denominacion_cta, activa FROM cuenta WHERE cod_cliente = " + codigoCliente;
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    if(!rs.getString("activa").equals("S")){
                        jTFNombreCuentaCliente.setForeground(Color.red);
                        jTFCodCuentaCliente.setForeground(Color.red);
                        jTFNombreCuentaCliente.setText(rs.getString("denominacion_cta") + " - CUENTA DESHABILITADA");
                    }else{
                        jTFNombreCuentaCliente.setForeground(Color.black);
                        jTFCodCuentaCliente.setForeground(Color.black);
                        jTFNombreCuentaCliente.setText(rs.getString("denominacion_cta"));
                    }
                    jTFCodCuentaCliente.setText(String.valueOf(rs.getString("cod_cuenta")));
                }else{
                    jTFNombreCuentaCliente.setForeground(Color.red);
                    jTFCodCuentaCliente.setForeground(Color.red);
                    jTFCodCuentaCliente.setText("0");
                    jTFNombreCuentaCliente.setText("CUENTA NO HABILITADA");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
    }
    
    private String getCuentaHabilitada(String codigoCliente){
        String result = "";
        String sql = "SELECT activa FROM cuenta WHERE cod_cliente = " + codigoCliente;
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = rs.getString("activa");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private void cargaLinea() {
        if (colLineas.isEmpty()) {
            colLineas.addElement("Código");
            colLineas.addElement("Razón Social");
            colLineas.addElement("RUC");
            colLineas.addElement("Teléfono");
        }

        String sql = "SELECT cod_cliente, razon_soc, ruc_cliente, telefono "
                   + "FROM cliente WHERE cod_cliente = " + jTFCodCliente.getText().trim() + " "
                   + "ORDER BY cod_cliente";

        resultSetLineas = DBManager.ejecutarDSL(sql);
        tGrid.setDataSet(resultSetLineas, colLineas);
        tGrid.setVisible(true);
        tGrid.repaint();
    }
    
    private String getCodigoCliente() {
        ClienteCtrl pCtrl = new ClienteCtrl();
        int result = pCtrl.buscaMaxCodCliente();
        result = result + 1;
        return String.valueOf(result);
    }
    
    private String getCodigoNuevaCuenta(){
        String codigo = "";
        try{
            String sql = "SELECT (CASE WHEN cod_cuenta < 100 THEN 100 ELSE MAX(cod_cuenta + 1) END) AS codcuenta " +
                         "FROM cuenta " +
                         "GROUP BY cod_cuenta " +
                         "ORDER BY cod_cuenta DESC LIMIT 1";
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    codigo = String.valueOf(rs.getInt("codcuenta"));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return codigo;
    }
    
    private boolean verificarCampos(){
        if (jTFNroCi.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Nro CI !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFNroCi.grabFocus();
            return false;
        }
        
        if (jTFNombreCliente.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Nombre del Cliente !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFNombreCliente.grabFocus();
            return false;
        }
        
        if (jTFRucContacto.getText().trim().equals("")) {
            jTFRucContacto.setText("0");
            return true;
        }
        
        if (jTFNombreContacto.getText().trim().equals("")) {
            jTFNombreContacto.setText("SIN NOMBRE");
            return true;
        }
        
        if (jTFCodCiudad.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Código Ciudad !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodCiudad.grabFocus();
            return false;
        }
        
        if (jTFCodDpto.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Código Dpto !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodDpto.grabFocus();
            return false;
        }
        
        if (jTFCodBarrio.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Código Barrio !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodBarrio.grabFocus();
            return false;
        }
        
        if (jTFCodBarrio.getText().trim().equals("")) {
            jTFDireccion.setText("-");
            return true;
        }
        
        if (jTFTelefono.getText().trim().equals("")) {
            jTFTelefono.setText("0");
            return true;
        }
        
        if (jTFFax.getText().trim().equals("")) {
            jTFFax.setText("0");
            return true;
        }
        
        if (jTFCelular.getText().trim().equals("")) {
            jTFCelular.setText("0");
            return true;
        }
        
        if (jTFEmail.getText().trim().equals("")) {
            jTFEmail.setText("@");
            return true;
        }
        
        if (jTFCodPaisOrigen.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "ATENCION: Complete el campo Código Pais !!!", "Error", JOptionPane.WARNING_MESSAGE);
            jTFCodPaisOrigen.grabFocus();
            return false;
        }
        
        if (jTFDocPaisOrigen.getText().trim().equals("")) {
            jTFDocPaisOrigen.setText("0");
            return true;
        }
        
        if (jTFDocInmigracion.getText().trim().equals("")) {
            jTFDocInmigracion.setText("0");
            return true;
        }
        
        if (jTFCondPago.getText().trim().equals("")) {
            jTFCondPago.setText("0");
            return true;
        }
        
        if (jTFCodListaPrecio.getText().trim().equals("")) {
            jTFCodListaPrecio.setText("1");
            return true;
        }
        
        if (jTFCodGarante.getText().trim().equals("")) {
            jTFCodGarante.setText("1");
            return true;
        }
        
        if (jTFLimiteCredito.getText().trim().equals("")) {
            jTFLimiteCredito.setText("0");
            return true;
        }
        
        if (jTFDescuentoMax.getText().trim().equals("")) {
            jTFDescuentoMax.setText("0");
            return true;
        }
        return true;
    }
    
    private void grabarClienteSql(){
        int cod_cliente = Integer.parseInt(jTFCodCliente.getText().trim());
        String razon_soc = jTFNombreCliente.getText().trim();
        String contacto = jTFRucContacto.getText().trim() + "-" + jTFNombreContacto.getText().trim();
        String ci_contacto = jTFNroCi.getText().trim();
        String ruc = jTFRuc.getText().trim() + "-" + jTFDv.getText().trim();
        String es_juridica = jCBEsEmpresa.getSelectedItem().toString();
        int cod_ciudad = Integer.parseInt(jTFCodCiudad.getText().trim());
        int cod_barrio = Integer.parseInt(jTFCodBarrio.getText().trim());
        String direccion = jTFDireccion.getText().trim();
        String telefono = jTFTelefono.getText().trim();
        String fax = jTFFax.getText().trim();
        String email = jTFEmail.getText().trim();
        int cod_dpto = Integer.parseInt(jTFCodDpto.getText().trim());
        int cond_pago = Integer.parseInt(jTFCondPago.getText().trim());
        int limite_credito = Integer.parseInt(jTFLimiteCredito.getText().trim());
        int cod_usuario = FormMain.codUsuario;
        String acepta_cheque = jCBAceptaCheque.getSelectedItem().toString();
        String activo = jCBActivo.getSelectedItem().toString();
        String cod_pcuenta = "0";
        float pct_max_descuento = Float.parseFloat(jTFDescuentoMax.getText().trim());
        int cod_lista_precio = Integer.parseInt(jTFCodListaPrecio.getText().trim());
        int cod_zona = Integer.parseInt(jTFCodZona.getText().trim());
        String apodo = "-";
        String fec_nacimiento = jTFFecNacimiento.getText().trim();
        String celular = jTFCelular.getText().trim();
        int cod_pais = Integer.parseInt(jTFCodPaisOrigen.getText().trim());
        String ci_origen = jTFDocPaisOrigen.getText().trim();
        String doc_inmigracion = jTFDocInmigracion.getText().trim();
        String vive_alquiler = "N";
        int cod_representante = 1;
        int cod_garante = Integer.parseInt(jTFCodGarante.getText().trim());
        int dependientes = 0;
        int giro = 0;
        String cod_clasificacion = "NNN";
        int cod_local_preferencia = 1;
        String sexo = jCBSexo.getSelectedItem().toString();
        String estado_civil = jCBEstadoCivil.getSelectedItem().toString();
        String es_contribuyente = jCBEsContribuyente.getSelectedItem().toString();
        
        String sqlInsert = "INSERT INTO cliente (cod_cliente, razon_soc, contacto, ci_contacto, ruc_cliente, es_juridica, cod_ciudad, cod_barrio, direccion, "
                         + "telefono, fax, email, cod_dpto, cond_pago, limite_credito, cod_usuario, fec_vigencia, acepta_cheque, activo, cod_pcuenta, pct_max_dcto, "
                         + "cod_lista_precio, cod_zona, apodo, fec_nacimiento, celular, cod_pais, ci_origen, doc_inmigracion, vive_alquiler, cod_representante, "
                         + "cod_garante, dependientes, giro, cod_clasificacion, cod_local_pref, sexo, estado_civil, es_contribuyente) "
                         + "VALUES (" + cod_cliente + ", '" + razon_soc + "', '" + contacto + "', '" + ci_contacto + "', '" + ruc + "', '" + es_juridica + "', "
                         + "" + cod_ciudad + ", " + cod_barrio + ", '" + direccion + "', '" + telefono + "', '" + fax + "', '" + email + "', " + cod_dpto + ", "
                         + "" + cond_pago + ", " + limite_credito + ", " + cod_usuario + ", current_timestamp, '" + acepta_cheque + "', '" + activo + "', '"
                         + "" + cod_pcuenta + "', " + pct_max_descuento + ", " + cod_lista_precio + ", " + cod_zona + ", '" + apodo + "', to_date('" + fec_nacimiento + "', 'dd/MM/yyyy'), '"
                         + "" + celular + "', " + cod_pais + ", '" + ci_origen + "', '" + doc_inmigracion + "', '" + vive_alquiler + "', " + cod_representante + ", "
                         + "" + cod_garante + ", " + dependientes + ", " + giro + ", '" + cod_clasificacion + "', " + cod_local_preferencia + ", '" + sexo + "', '"
                         + "" + estado_civil + "', '" + es_contribuyente + "')";
        
        String sqlUpdate = "UPDATE cliente SET razon_soc = '" + razon_soc + "', contacto = '" + contacto + "', ci_contacto = '" + ci_contacto + "', "
                         + "ruc_cliente = '" + ruc + "', es_juridica = '" + es_juridica + "', cod_ciudad = " + cod_ciudad + ", direccion = '" + direccion + "', "
                         + "telefono = '" + telefono + "', fax = '" + fax + "', email = '" + email + "', cod_dpto = " + cod_dpto + ", cond_pago = " + cond_pago + ", "
                         + "limite_credito = " + limite_credito + ", cod_usuario = " + cod_usuario + ", fec_vigencia = current_timestamp, acepta_cheque = '" + acepta_cheque + "', "
                         + "activo = '" + activo + "', cod_pcuenta = '" + cod_pcuenta + "', pct_max_dcto = " +  pct_max_descuento + ", cod_lista_precio = " + cod_lista_precio + ", "
                         + "cod_zona = " + cod_zona + ", apodo = '" + apodo + "', fec_nacimiento = to_date('" + fec_nacimiento + "', 'dd/MM/yyyy'), "
                         + "celular = '" + celular + "', cod_pais = " + cod_pais + ", ci_origen = '" + ci_origen + "', doc_inmigracion = '" + doc_inmigracion + "', "
                         + "vive_alquiler = '" + vive_alquiler + "', cod_representante = " + cod_representante + ", cod_garante = " + cod_garante + ", "
                         + "dependientes = " + dependientes + ", giro = " + giro + ", cod_clasificacion = '" + cod_clasificacion + "', cod_local_pref = " + cod_local_preferencia + ", "
                         + "sexo = '" + sexo + "', estado_civil = '" + estado_civil + "', es_contribuyente = '" + es_contribuyente + "' "
                         + "WHERE cod_cliente = " + cod_cliente;
        
        if(esNuevo){
            System.out.println("SQL INSERT CLIENTE: " + sqlInsert);
            if(DBManager.ejecutarDML(sqlInsert) > 0 ){
                if(jCBCredHabilitado.getSelectedItem().toString().equals("S")){
                    if(existeCuenta(jTFCodCliente.getText().trim())){
                        modificarCuentaCliente();
                    }else{
                        registrarNuevaCuenta();
                    }
                }
                try{
                    DBManager.conn.commit();
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();                                        
                    cargaCliente();                    
                    JOptionPane.showMessageDialog(this, "Cliente registrado con éxito! " + mensajeCuentaGrabada, "OK", JOptionPane.INFORMATION_MESSAGE);
                    
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                    JOptionPane.showMessageDialog(this, "Error al Grabar Datos (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Error al Registrar Cliente !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }else{ // modificacion 
            
            System.out.println("SQL UPDATE CLIENTE: " + sqlUpdate);
            
            if(DBManager.ejecutarDML(sqlUpdate) > 0 ){
                if(existeCuenta(jTFCodCliente.getText().trim())){
                    modificarCuentaCliente();
                }else{
                    registrarNuevaCuenta();
                }
                
                try{
                    DBManager.conn.commit();
                    JOptionPane.showMessageDialog(this, "Cliente modificado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();                                                            
                    cargaCliente();
                }catch(Exception ex){
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                    JOptionPane.showMessageDialog(this, "Error al Grabar Datos (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Error al modificar Cliente !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
            }            
        }
        
    }
    
    private void grabarCliente(){
        try{
            ClienteBean bean = new ClienteBean();
            bean.setCodCliente(Integer.parseInt(jTFCodCliente.getText().trim()));
            bean.setRazonSoc(jTFNombreCliente.getText().trim());
            bean.setContacto(jTFRucContacto.getText().trim() + "-" + jTFNombreContacto.getText().trim());
            bean.setCiContacto(jTFNroCi.getText().trim());
            bean.setRucCliente(jTFRuc.getText().trim() + "-" + jTFDv.getText().trim());
            bean.setEsJuridica(jCBEsEmpresa.getSelectedItem().toString());
            bean.setCodCiudad(Integer.parseInt(jTFCodCiudad.getText().trim()));
            bean.setCodBarrio(Integer.parseInt(jTFCodBarrio.getText().trim()));
            bean.setDireccion(jTFDireccion.getText().trim());
            bean.setTelefono(jTFTelefono.getText().trim());
            bean.setFax(jTFFax.getText().trim());
            bean.setEmail(jTFEmail.getText().trim());
            bean.setCodDpto(Integer.parseInt(jTFCodDpto.getText().trim()));
            bean.setCondPago(Integer.parseInt(jTFCondPago.getText().trim()));
            bean.setLimiteCredito(Integer.parseInt(jTFLimiteCredito.getText().trim()));
            bean.setCodUsuario(FormMain.codUsuario);
            bean.setAceptaCheque(jCBAceptaCheque.getSelectedItem().toString());
            bean.setActivo(jCBActivo.getSelectedItem().toString());
            bean.setCodPCuenta("0");
            bean.setPctMaxDescuento(Integer.parseInt(jTFDescuentoMax.getText().trim()));
            bean.setCodListaPrecio(Integer.parseInt(jTFCodListaPrecio.getText().trim()));
            bean.setCodZona(Integer.parseInt(jTFCodZona.getText().trim()));
            bean.setApodo("-");
            bean.setFecNacimiento(jTFFecNacimiento.getText().trim());
            bean.setCelular(jTFCelular.getText().trim());
            bean.setCodPais(Integer.parseInt(jTFCodPaisOrigen.getText().trim()));
            bean.setCiOrigen(jTFDocPaisOrigen.getText().trim());
            bean.setDocInmigracion(jTFDocInmigracion.getText().trim());
            bean.setViveAlquiler("N");
            bean.setCodRepresentante(Integer.parseInt("1"));
            bean.setCodGarante(Integer.parseInt(jTFCodGarante.getText().trim()));
            bean.setDependientes(Integer.parseInt("0"));
            bean.setGiro(0);
            bean.setCodClasificacion("NNN");
            bean.setCodLocalPreferencia(1);
            bean.setSexo(jCBSexo.getSelectedItem().toString());
            bean.setEstadoCivil(jCBEstadoCivil.getSelectedItem().toString());
            bean.setEsContribuyente(jCBEsContribuyente.getSelectedItem().toString());
            
            ClienteCtrl ctrl = new ClienteCtrl();
            if(esNuevo){
                if(jCBCredHabilitado.getSelectedItem().toString().equals("S")){
                    if(existeCuenta(jTFCodCliente.getText().trim())){
                        modificarCuentaCliente();
                    }else{
                        registrarNuevaCuenta();
                    }
                }
                if(ctrl.catastrarCliente(bean)){
                    JOptionPane.showMessageDialog(this, "Cliente registrado con éxito! " + mensajeCuentaGrabada, "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                    cargaCliente();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al Registrar Cliente !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{ //modificacion
                if(existeCuenta(jTFCodCliente.getText().trim())){
                    modificarCuentaCliente();
                }else{
                    registrarNuevaCuenta();
                }
                if(ctrl.alterarCliente(bean)){
                    JOptionPane.showMessageDialog(this, "Cliente modificado con éxito !!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    setEstadoComponentes(false);
                    setEstadoBotonesGrabado();
                    cargaCliente();
                }else{
                    JOptionPane.showMessageDialog(this, "Error al modificar Cliente !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
            JOptionPane.showMessageDialog(this, "ATENCION: Error al grabar Cliente !!!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean existeCuenta(String codigoCliente){
        boolean result = true;
        String sql = "SELECT cod_cuenta, denominacion_cta FROM cuenta WHERE cod_cliente = " + codigoCliente;
        try{
            ResultSet rs = DBManager.ejecutarDSL(sql);
            if(rs != null){
                if(rs.next()){
                    result = true;
                }else{
                    result = false;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    private void modificarCuentaCliente(){
        String sql = "UPDATE cuenta SET activa = '" + jCBCredHabilitado.getSelectedItem().toString() + "' WHERE cod_cliente = " + jTFCodCliente.getText().trim();
        System.out.println("UPDATE CUENTA CLIENTE: " + sql);
        if(DBManager.ejecutarDML(sql) > 0){
            try{
                DBManager.conn.commit();
                System.out.println("CUENTA CLIENTE Nro " + jTFCodCliente.getText().trim() + " modificada!");
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "Error al Grabar Datos (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private String registrarNuevaCuenta(){
        String vCodCuenta = getCodigoNuevaCuenta();
        String sql = "INSERT INTO cuenta (cod_cuenta, denominacion_cta, cod_moneda, tipo_cuenta, plazo_cob_comision, cod_banco, pct_comision, pct_iva_comision, "
                   + "pct_comision_renta, pct_iva_renta, cod_formato_cheque, activa, es_forma_pago, fec_vigencia, cod_usuario, tabla_relacionada, es_detallado, "
                   + "cod_pcuenta, es_valor_pago, es_venta_interna, cod_cuenta_deposito, es_cta_posicion, rango_inicial_nro_cheque, rango_final_nro_cheque, "
                   + "nro_cheque_actual, cod_cliente, es_ajuste_pago, es_asociacion, cod_formato_cheque_dif, cod_proveedor, fec_catastro, cod_pcuenta_transitoria) "
                   + "VALUES (" + vCodCuenta + ", '" + jTFNombreCliente.getText().trim() + "', 1, 'CRE', 0, 1, 0, 0, "
                   + "0, 0, 0, 'S', 'S', 'now()', " + FormMain.codUsuario + ", '-', 'S', "
                   + "0, 'N', 'N', 0, 'N', 0, 0, "
                   + "0, " + jTFCodCliente.getText().trim() + ", 'N', 'N', 0, 0, 'now()', 0)";
        
        System.out.println("SQL INSERT CUENTA CLIENTE: " + sql);
        if(DBManager.ejecutarDML(sql) > 0){
            try{
                DBManager.conn.commit();
                mensajeCuentaGrabada = "\nCUENTA NRO " + vCodCuenta + " HABILITADA!";
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "Error al Grabar Datos (commit), Operacion Cancelada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
        return mensajeCuentaGrabada;
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
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCBEsEmpresa = new javax.swing.JComboBox<>();
        jLEsEmpresa = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jCBEsContribuyente = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jTFNroCi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTFRuc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTFDv = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jTFCodCliente = new javax.swing.JTextField();
        jTFNombreCliente = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFRucContacto = new javax.swing.JTextField();
        jTFNombreContacto = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFCodCiudad = new javax.swing.JTextField();
        jTFNombreCiudad = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFCodDpto = new javax.swing.JTextField();
        jTFNombreDpto = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFCodBarrio = new javax.swing.JTextField();
        jTFNombreBarrio = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFDireccion = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTFTelefono = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFFax = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFCelular = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFEmail = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTFFecNacimiento = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jCBSexo = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jCBEstadoCivil = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jTFCodZona = new javax.swing.JTextField();
        jTFNombreZona = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTFCodPaisOrigen = new javax.swing.JTextField();
        jTFNombrePais = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTFDocPaisOrigen = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTFDocInmigracion = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jCBActivo = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jCBAceptaCheque = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jCBCredHabilitado = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jTFCondPago = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel30 = new javax.swing.JLabel();
        jTFCodListaPrecio = new javax.swing.JTextField();
        jTFDescListaPrecio = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTFCodGarante = new javax.swing.JTextField();
        jTFNombreGarante = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTFLimiteCredito = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jTFDescuentoMax = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jTFCodCuentaCliente = new javax.swing.JTextField();
        jTFNombreCuentaCliente = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jBNuevo = new javax.swing.JButton();
        jBGrabar = new javax.swing.JButton();
        jBModificar = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jBBuscar = new javax.swing.JButton();
        jBInfo = new javax.swing.JButton();
        jBSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Clientes");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("MANTENIMIENTO DE CLIENTES");

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
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Es Empresa:");

        jCBEsEmpresa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBEsEmpresa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBEsEmpresa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jCBEsEmpresaFocusLost(evt);
            }
        });
        jCBEsEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBEsEmpresaKeyPressed(evt);
            }
        });

        jLEsEmpresa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLEsEmpresa.setText("Empresa");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Es contribuyente:");

        jCBEsContribuyente.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBEsContribuyente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBEsContribuyente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jCBEsContribuyenteFocusLost(evt);
            }
        });
        jCBEsContribuyente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBEsContribuyenteKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("(Presenta IVA)");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("C.I.P. Nro:");

        jTFNroCi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNroCi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFNroCi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNroCiFocusGained(evt);
            }
        });
        jTFNroCi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNroCiKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("RUC:");

        jTFRuc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFRuc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFRuc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFRucFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFRucFocusLost(evt);
            }
        });
        jTFRuc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFRucKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("DV:");

        jTFDv.setEditable(false);
        jTFDv.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDv.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Cliente:");

        jTFCodCliente.setEditable(false);
        jTFCodCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCliente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCliente.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodClientePropertyChange(evt);
            }
        });

        jTFNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFNombreClienteFocusGained(evt);
            }
        });
        jTFNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFNombreClienteKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Contacto:");

        jTFRucContacto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFRucContacto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFRucContacto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFRucContactoFocusGained(evt);
            }
        });
        jTFRucContacto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFRucContactoKeyPressed(evt);
            }
        });

        jTFNombreContacto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Ciudad:");

        jTFCodCiudad.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodCiudad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodCiudad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodCiudadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodCiudadFocusLost(evt);
            }
        });
        jTFCodCiudad.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodCiudadPropertyChange(evt);
            }
        });
        jTFCodCiudad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodCiudadKeyPressed(evt);
            }
        });

        jTFNombreCiudad.setEditable(false);
        jTFNombreCiudad.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFNombreCiudad.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFNombreCiudadPropertyChange(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Dpto:");

        jTFCodDpto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodDpto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodDpto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodDptoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodDptoFocusLost(evt);
            }
        });
        jTFCodDpto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodDptoPropertyChange(evt);
            }
        });
        jTFCodDpto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodDptoKeyPressed(evt);
            }
        });

        jTFNombreDpto.setEditable(false);
        jTFNombreDpto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Barrio:");

        jTFCodBarrio.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodBarrio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodBarrio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodBarrioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodBarrioFocusLost(evt);
            }
        });
        jTFCodBarrio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodBarrioPropertyChange(evt);
            }
        });
        jTFCodBarrio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodBarrioKeyPressed(evt);
            }
        });

        jTFNombreBarrio.setEditable(false);
        jTFNombreBarrio.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Dirección:");

        jTFDireccion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Teléfono:");

        jTFTelefono.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFTelefono.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Fax:");

        jTFFax.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Celular:");

        jTFCelular.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCelular.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCelular.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCelularFocusGained(evt);
            }
        });
        jTFCelular.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCelularKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Email:");

        jTFEmail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Fec. Nac:");

        jTFFecNacimiento.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFFecNacimiento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFFecNacimientoFocusGained(evt);
            }
        });
        jTFFecNacimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFFecNacimientoKeyPressed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Sexo:");

        jCBSexo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "M", "F" }));
        jCBSexo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBSexoKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Estado Civil:");

        jCBEstadoCivil.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBEstadoCivil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "C", "D" }));
        jCBEstadoCivil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBEstadoCivilKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Zona:");

        jTFCodZona.setEditable(false);
        jTFCodZona.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodZona.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTFNombreZona.setEditable(false);
        jTFNombreZona.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("País Origen:");

        jTFCodPaisOrigen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodPaisOrigen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodPaisOrigen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodPaisOrigenFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodPaisOrigenFocusLost(evt);
            }
        });
        jTFCodPaisOrigen.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodPaisOrigenPropertyChange(evt);
            }
        });
        jTFCodPaisOrigen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodPaisOrigenKeyPressed(evt);
            }
        });

        jTFNombrePais.setEditable(false);
        jTFNombrePais.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Doc. País Origen:");

        jTFDocPaisOrigen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDocPaisOrigen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDocPaisOrigenFocusGained(evt);
            }
        });
        jTFDocPaisOrigen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDocPaisOrigenKeyPressed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Doc. Inmigración:");

        jTFDocInmigracion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDocInmigracion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDocInmigracionKeyPressed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Activo:");

        jCBActivo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBActivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBActivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBActivoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBEsEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLEsEmpresa))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCBEsContribuyente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTFNroCi, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFRuc, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTFDv, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTFRucContacto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                    .addComponent(jTFCodCliente, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFNombreCliente)
                                    .addComponent(jTFNombreContacto)))
                            .addComponent(jTFDireccion)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTFCodBarrio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                            .addComponent(jTFCodCiudad, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTFNombreCiudad)
                                            .addComponent(jTFNombreBarrio, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFCodDpto, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFNombreDpto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTFTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTFFax, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFEmail)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFFecNacimiento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTFCodPaisOrigen, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                            .addComponent(jTFCodZona))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFNombreZona)
                            .addComponent(jTFNombrePais)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel25))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFDocPaisOrigen)
                                    .addComponent(jTFDocInmigracion)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jCBEsEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLEsEmpresa))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jCBEsContribuyente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTFNroCi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jTFRuc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jTFDv, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTFCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTFRucContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTFCodCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jTFCodDpto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreDpto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTFCodBarrio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreBarrio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTFDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jTFTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(jTFFax, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jTFCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jTFEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFFecNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jCBSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(jCBEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jTFCodZona, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombreZona, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jTFCodPaisOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFNombrePais, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jTFDocPaisOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jTFDocInmigracion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jCBActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Acepta cheque:");

        jCBAceptaCheque.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBAceptaCheque.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBAceptaCheque.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBAceptaChequeKeyPressed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("Créd. Hab:");

        jCBCredHabilitado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCBCredHabilitado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "N" }));
        jCBCredHabilitado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCBCredHabilitadoKeyPressed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Condición de pago:");

        jTFCondPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCondPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("días.");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("Lista de precio:");

        jTFCodListaPrecio.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodListaPrecio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodListaPrecio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodListaPrecioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodListaPrecioFocusLost(evt);
            }
        });
        jTFCodListaPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodListaPrecioKeyPressed(evt);
            }
        });

        jTFDescListaPrecio.setEditable(false);
        jTFDescListaPrecio.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Garante:");

        jTFCodGarante.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFCodGarante.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFCodGarante.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodGaranteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCodGaranteFocusLost(evt);
            }
        });
        jTFCodGarante.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTFCodGarantePropertyChange(evt);
            }
        });
        jTFCodGarante.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCodGaranteKeyPressed(evt);
            }
        });

        jTFNombreGarante.setEditable(false);
        jTFNombreGarante.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setText("Límite de Crédito:");

        jTFLimiteCredito.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFLimiteCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFLimiteCredito.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFLimiteCreditoFocusGained(evt);
            }
        });
        jTFLimiteCredito.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFLimiteCreditoKeyPressed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setText("Gs.");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("Descuento Máx.:");

        jTFDescuentoMax.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTFDescuentoMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFDescuentoMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescuentoMaxFocusGained(evt);
            }
        });
        jTFDescuentoMax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescuentoMaxKeyPressed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setText("%");

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Cta. Habilitada:");

        jTFCodCuentaCliente.setEditable(false);
        jTFCodCuentaCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTFNombreCuentaCliente.setEditable(false);
        jTFNombreCuentaCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jCBAceptaCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBCredHabilitado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFCondPago, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFCodListaPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescListaPrecio))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFCodGarante, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreGarante)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel34)
                            .addComponent(jLabel36))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFDescuentoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel33))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFNombreCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel32)
                                    .addComponent(jTFLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel33))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTFDescuentoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel35)
                                    .addComponent(jLabel34))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel36)
                                    .addComponent(jTFCodCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFNombreCuentaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel26)
                                    .addComponent(jCBAceptaCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27)
                                    .addComponent(jCBCredHabilitado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28)
                                    .addComponent(jTFCondPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel30)
                                    .addComponent(jTFCodListaPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFDescListaPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel31)
                                    .addComponent(jTFCodGarante, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFNombreGarante, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

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

        jBInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/informacion24.png"))); // NOI18N
        jBInfo.setText("Info");
        jBInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInfoActionPerformed(evt);
            }
        });

        jBSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/salir24.png"))); // NOI18N
        jBSalir.setText("Salir");
        jBSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jBNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGrabar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBBuscar, jBCancelar, jBGrabar, jBInfo, jBModificar, jBNuevo, jBSalir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNuevo)
                    .addComponent(jBGrabar)
                    .addComponent(jBModificar)
                    .addComponent(jBCancelar)
                    .addComponent(jBBuscar)
                    .addComponent(jBInfo)
                    .addComponent(jBSalir))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBBuscar, jBCancelar, jBGrabar, jBInfo, jBModificar, jBNuevo, jBSalir});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        setSize(new java.awt.Dimension(1065, 649));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBSalirActionPerformed

    private void jTFNroCiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNroCiFocusGained
        jTFNroCi.selectAll();
    }//GEN-LAST:event_jTFNroCiFocusGained

    private void jTFRucFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRucFocusGained
        jTFRuc.selectAll();
    }//GEN-LAST:event_jTFRucFocusGained

    private void jTFNombreClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFNombreClienteFocusGained
        jTFNombreCliente.selectAll();
    }//GEN-LAST:event_jTFNombreClienteFocusGained

    private void jTFNombreClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNombreClienteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFRucContacto.requestFocus();
        }
    }//GEN-LAST:event_jTFNombreClienteKeyPressed

    private void jTFRucContactoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRucContactoFocusGained
        jTFRucContacto.selectAll();
    }//GEN-LAST:event_jTFRucContactoFocusGained

    private void jTFRucContactoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFRucContactoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodCiudad.requestFocus();
        }
    }//GEN-LAST:event_jTFRucContactoKeyPressed

    private void jTFCodCiudadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCiudadFocusGained
        jTFCodCiudad.selectAll();
    }//GEN-LAST:event_jTFCodCiudadFocusGained

    private void jTFCodCiudadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodCiudadKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodDpto.requestFocus();
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
    }//GEN-LAST:event_jTFCodCiudadKeyPressed

    private void jTFCodCiudadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodCiudadFocusLost
        jTFNombreCiudad.setText(getNombreCiudad(jTFCodCiudad.getText().trim()));
    }//GEN-LAST:event_jTFCodCiudadFocusLost

    private void jTFCodDptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodDptoFocusGained
        jTFCodDpto.selectAll();
    }//GEN-LAST:event_jTFCodDptoFocusGained

    private void jTFCodDptoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodDptoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCodBarrio.requestFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Departamento");
                grupo.dConsultas("departamento", "nombre", "cod_dpto", "nombre", "fec_vigencia", null, "Codigo", "Nombre", "Registro", null);
                grupo.setText(jTFCodDpto);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodDptoKeyPressed

    private void jTFCodDptoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodDptoFocusLost
        jTFNombreDpto.setText(getNombreDpto(jTFCodDpto.getText().trim()));
    }//GEN-LAST:event_jTFCodDptoFocusLost

    private void jTFCodBarrioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBarrioFocusGained
        jTFCodBarrio.selectAll();
    }//GEN-LAST:event_jTFCodBarrioFocusGained

    private void jTFCodBarrioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodBarrioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFDireccion.grabFocus();
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

    private void jTFCodBarrioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodBarrioFocusLost
        jTFNombreBarrio.setText(getNombreBarrio(jTFCodBarrio.getText().trim()));
    }//GEN-LAST:event_jTFCodBarrioFocusLost

    private void jTFDireccionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDireccionFocusGained
        jTFDireccion.selectAll();
    }//GEN-LAST:event_jTFDireccionFocusGained

    private void jTFDireccionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDireccionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFTelefono.grabFocus();
        }
    }//GEN-LAST:event_jTFDireccionKeyPressed

    private void jTFTelefonoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTelefonoFocusGained
        jTFTelefono.selectAll();
    }//GEN-LAST:event_jTFTelefonoFocusGained

    private void jTFTelefonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTelefonoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFFax.grabFocus();
        }
    }//GEN-LAST:event_jTFTelefonoKeyPressed

    private void jTFFaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFaxFocusGained
        jTFFax.selectAll();
    }//GEN-LAST:event_jTFFaxFocusGained

    private void jTFFaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFaxKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCelular.grabFocus();
        }
    }//GEN-LAST:event_jTFFaxKeyPressed

    private void jTFCelularFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCelularFocusGained
        jTFCelular.selectAll();
    }//GEN-LAST:event_jTFCelularFocusGained

    private void jTFCelularKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCelularKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFEmail.grabFocus();
        }
    }//GEN-LAST:event_jTFCelularKeyPressed

    private void jTFEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFEmailFocusGained
        jTFEmail.selectAll();
    }//GEN-LAST:event_jTFEmailFocusGained

    private void jTFEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFEmailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFFecNacimiento.grabFocus();
        }
    }//GEN-LAST:event_jTFEmailKeyPressed

    private void jTFFecNacimientoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFFecNacimientoFocusGained
        jTFFecNacimiento.selectAll();
    }//GEN-LAST:event_jTFFecNacimientoFocusGained

    private void jTFFecNacimientoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFFecNacimientoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jCBSexo.grabFocus();
        }
    }//GEN-LAST:event_jTFFecNacimientoKeyPressed

    private void jCBSexoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBSexoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jCBEstadoCivil.grabFocus();
        }
    }//GEN-LAST:event_jCBSexoKeyPressed

    private void jCBEstadoCivilKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBEstadoCivilKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodPaisOrigen.grabFocus();
        }
    }//GEN-LAST:event_jCBEstadoCivilKeyPressed

    private void jTFCodPaisOrigenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodPaisOrigenFocusGained
        jTFCodPaisOrigen.selectAll();
    }//GEN-LAST:event_jTFCodPaisOrigenFocusGained

    private void jTFCodPaisOrigenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodPaisOrigenKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFDocPaisOrigen.grabFocus();
        }

        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Pais");
                grupo.dConsultas("pais", "nombre", "cod_ciudad", "nombre", "fec_vigencia", null, "Codigo", "Nombre", "Registro", null);
                grupo.setText(jTFCodPaisOrigen);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodPaisOrigenKeyPressed

    private void jTFCodPaisOrigenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodPaisOrigenFocusLost
        jTFNombrePais.setText(getNombrePais(jTFCodPaisOrigen.getText().trim()));
    }//GEN-LAST:event_jTFCodPaisOrigenFocusLost

    private void jTFDocPaisOrigenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDocPaisOrigenFocusGained
        jTFDocPaisOrigen.selectAll();
    }//GEN-LAST:event_jTFDocPaisOrigenFocusGained

    private void jTFDocPaisOrigenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDocPaisOrigenKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFDocInmigracion.grabFocus();
        }
    }//GEN-LAST:event_jTFDocPaisOrigenKeyPressed

    private void jTFDocInmigracionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDocInmigracionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jCBActivo.grabFocus();
        }
    }//GEN-LAST:event_jTFDocInmigracionKeyPressed

    private void jCBActivoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBActivoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jCBAceptaCheque.grabFocus();
        }
    }//GEN-LAST:event_jCBActivoKeyPressed

    private void jCBAceptaChequeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBAceptaChequeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jCBCredHabilitado.grabFocus();
        }
    }//GEN-LAST:event_jCBAceptaChequeKeyPressed

    private void jTFCondPagoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCondPagoFocusGained
        jTFCondPago.selectAll();
    }//GEN-LAST:event_jTFCondPagoFocusGained

    private void jTFCondPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCondPagoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodListaPrecio.grabFocus();
        }
    }//GEN-LAST:event_jTFCondPagoKeyPressed

    private void jTFCodListaPrecioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodListaPrecioFocusGained
        jTFCodListaPrecio.selectAll();
    }//GEN-LAST:event_jTFCodListaPrecioFocusGained

    private void jTFCodListaPrecioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodListaPrecioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTFCodGarante.grabFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Lista de Precio");
                grupo.dConsultas("listaprecio", "descripcion", "cod_lista", "descripcion", "fec_vigencia", "bloqueda", "Codigo", "Descripcion", "Registro", "Bloqueada");
                grupo.setText(jTFCodListaPrecio);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodListaPrecioKeyPressed

    private void jTFCodListaPrecioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodListaPrecioFocusLost
        jTFDescListaPrecio.setText(getDescListaPrecio(jTFCodListaPrecio.getText().trim()));
    }//GEN-LAST:event_jTFCodListaPrecioFocusLost

    private void jTFCodGaranteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodGaranteFocusGained
        jTFCodGarante.selectAll();
    }//GEN-LAST:event_jTFCodGaranteFocusGained

    private void jTFCodGaranteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodGaranteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFLimiteCredito.requestFocus();
        }
        
        if (evt.getKeyCode() == KeyEvent.VK_F1) {
            try {
                DlgConsultas grupo = new DlgConsultas(new JFrame(), true);
                grupo.pack();
                grupo.setTitle("Consulta de Cliente");
                grupo.dConsultas("cliente", "razon_soc", "cod_cliente", "razon_soc", "telefono", "es_juridica", "Codigo", "Nombre", "Teléfono", "Jurídica?");
                grupo.setText(jTFCodGarante);
                grupo.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                JOptionPane.showMessageDialog(this, "No se pudo crear el Formulario de Consulta!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTFCodGaranteKeyPressed

    private void jTFCodGaranteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodGaranteFocusLost
        jTFNombreGarante.setText(getNombreCliente(jTFCodGarante.getText().trim()));
    }//GEN-LAST:event_jTFCodGaranteFocusLost

    private void jTFLimiteCreditoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFLimiteCreditoFocusGained
        jTFLimiteCredito.selectAll();
    }//GEN-LAST:event_jTFLimiteCreditoFocusGained

    private void jTFLimiteCreditoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFLimiteCreditoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFDescuentoMax.requestFocus();
        }
    }//GEN-LAST:event_jTFLimiteCreditoKeyPressed

    private void jTFDescuentoMaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescuentoMaxFocusGained
        jTFDescuentoMax.selectAll();
    }//GEN-LAST:event_jTFDescuentoMaxFocusGained

    private void jTFDescuentoMaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescuentoMaxKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jBGrabar.requestFocus();
        }
    }//GEN-LAST:event_jTFDescuentoMaxKeyPressed

    private void jBNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNuevoActionPerformed
        esNuevo = true;
        setEstadoComponentes(true);
        jCBEsEmpresa.requestFocus();
        setEstadoBotonesNuevo();
        llenarCamposNuevo();
    }//GEN-LAST:event_jBNuevoActionPerformed

    private void jCBEsEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBEsEmpresaKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jCBEsEmpresa.getSelectedItem().toString().equals("S")){
                jTFRuc.requestFocus();
            }else{
                jCBEsContribuyente.requestFocus();
            }
        }
    }//GEN-LAST:event_jCBEsEmpresaKeyPressed

    private void jCBEsContribuyenteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBEsContribuyenteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNroCi.requestFocus();
        }
    }//GEN-LAST:event_jCBEsContribuyenteKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        setEstadoComponentes(false);
        setEstadoBotonesCancelar();
        cargaCliente();
        jBNuevo.requestFocus();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jTFNroCiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNroCiKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(jTFRuc.isEnabled()){
            jTFRuc.requestFocus();
            }else{
                jTFNombreCliente.requestFocus();
            }
        }
    }//GEN-LAST:event_jTFNroCiKeyPressed

    private void jCBEsContribuyenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBEsContribuyenteFocusLost
        if(jCBEsContribuyente.getSelectedItem().toString().equals("S") && jCBEsEmpresa.getSelectedItem().toString().equals("N")){
            jTFRuc.setEnabled(true);
            jTFRuc.requestFocus();
        }else{
            jTFNroCi.requestFocus();
            jTFRuc.setText("44444401");
            jTFDv.setText("7");
            jTFRuc.setEnabled(false);
        }
    }//GEN-LAST:event_jCBEsContribuyenteFocusLost

    private void jTFRucKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFRucKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFNombreCliente.requestFocus();
            if(jCBEsContribuyente.getSelectedItem().toString().equals("S") && jCBEsEmpresa.getSelectedItem().toString().equals("N")){
                jTFNroCi.setText(jTFRuc.getText());
            }
        }
    }//GEN-LAST:event_jTFRucKeyPressed

    private void jTFRucFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFRucFocusLost
        jTFDv.setText(String.valueOf(calculaDigito(jTFRuc.getText(), 11)));
    }//GEN-LAST:event_jTFRucFocusLost

    private void jTFCodClientePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodClientePropertyChange
        jTFNombreCliente.setText(getNombreCliente(jTFCodCliente.getText().trim()));
    }//GEN-LAST:event_jTFCodClientePropertyChange

    private void jTFNombreCiudadPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFNombreCiudadPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFNombreCiudadPropertyChange

    private void jTFCodCiudadPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodCiudadPropertyChange
        jTFNombreCiudad.setText(getNombreCiudad(jTFCodCiudad.getText().trim()));
    }//GEN-LAST:event_jTFCodCiudadPropertyChange

    private void jTFCodDptoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodDptoPropertyChange
        jTFNombreDpto.setText(getNombreDpto(jTFCodDpto.getText().trim()));
    }//GEN-LAST:event_jTFCodDptoPropertyChange

    private void jTFCodBarrioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodBarrioPropertyChange
        jTFNombreBarrio.setText(getNombreBarrio(jTFCodBarrio.getText().trim()));
    }//GEN-LAST:event_jTFCodBarrioPropertyChange

    private void jTFCodPaisOrigenPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodPaisOrigenPropertyChange
        jTFNombrePais.setText(getNombrePais(jTFCodPaisOrigen.getText().trim()));
    }//GEN-LAST:event_jTFCodPaisOrigenPropertyChange

    private void jTFCodGarantePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTFCodGarantePropertyChange
        jTFNombreGarante.setText(getNombreCliente(jTFCodGarante.getText().trim()));
    }//GEN-LAST:event_jTFCodGarantePropertyChange

    private void jBInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInfoActionPerformed
        GenericInfoAuditoria info = new GenericInfoAuditoria(new JFrame(), true, usuario, fecha, "Clientes");
        info.setTitle("ATOMSystems|Main - Auditoria");
        info.pack();
        info.setVisible(true);
    }//GEN-LAST:event_jBInfoActionPerformed

    private void jBModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBModificarActionPerformed
        String codCliente = jTFCodCliente.getText().trim();
        
        if(!(codCliente.equals("1"))){
            esNuevo = false;
            setEstadoComponentes(true);
            setEstadoBotonesModificar();
            jCBEsEmpresa.requestFocus();
        }else{
            JOptionPane.showMessageDialog(this, "Cliente no puede modificarse!", "ATENCIÓN", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jBModificarActionPerformed

    private void jBBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscarActionPerformed
        BuscaCliente bCliente = new BuscaCliente(new JFrame(), true);
        bCliente.pack();
        bCliente.setVisible(true);
        bCliente.jtfInputBusca.setText("%");
        bCliente.jtfInputBusca.requestFocus();
        if(bCliente.codigo != 0){
            getCliente(bCliente.codigo);
            cargaLinea();
        }
        jBModificar.requestFocus();
    }//GEN-LAST:event_jBBuscarActionPerformed

    private void jBGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGrabarActionPerformed
        if(verificarCampos()){
            grabarClienteSql();
        }
    }//GEN-LAST:event_jBGrabarActionPerformed

    private void jCBEsEmpresaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBEsEmpresaFocusLost
        if(jCBEsEmpresa.getSelectedItem().toString().equals("S")){
            jCBEsContribuyente.setSelectedItem("S");
            jCBEsContribuyente.setEnabled(false);
        }else{
            jCBEsContribuyente.setEnabled(true);
        }
    }//GEN-LAST:event_jCBEsEmpresaFocusLost

    private void jCBCredHabilitadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCredHabilitadoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFCondPago.requestFocus();
        }
    }//GEN-LAST:event_jCBCredHabilitadoKeyPressed

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
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Clientes dialog = new Clientes(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jBInfo;
    private javax.swing.JButton jBModificar;
    private javax.swing.JButton jBNuevo;
    private javax.swing.JButton jBSalir;
    private javax.swing.JComboBox<String> jCBAceptaCheque;
    private javax.swing.JComboBox<String> jCBActivo;
    private javax.swing.JComboBox<String> jCBCredHabilitado;
    private javax.swing.JComboBox<String> jCBEsContribuyente;
    private javax.swing.JComboBox<String> jCBEsEmpresa;
    private javax.swing.JComboBox<String> jCBEstadoCivil;
    private javax.swing.JComboBox<String> jCBSexo;
    private javax.swing.JLabel jLEsEmpresa;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTFCelular;
    private javax.swing.JTextField jTFCodBarrio;
    private javax.swing.JTextField jTFCodCiudad;
    private javax.swing.JTextField jTFCodCliente;
    private javax.swing.JTextField jTFCodCuentaCliente;
    private javax.swing.JTextField jTFCodDpto;
    private javax.swing.JTextField jTFCodGarante;
    private javax.swing.JTextField jTFCodListaPrecio;
    private javax.swing.JTextField jTFCodPaisOrigen;
    private javax.swing.JTextField jTFCodZona;
    private javax.swing.JTextField jTFCondPago;
    private javax.swing.JTextField jTFDescListaPrecio;
    private javax.swing.JTextField jTFDescuentoMax;
    private javax.swing.JTextField jTFDireccion;
    private javax.swing.JTextField jTFDocInmigracion;
    private javax.swing.JTextField jTFDocPaisOrigen;
    private javax.swing.JTextField jTFDv;
    private javax.swing.JTextField jTFEmail;
    private javax.swing.JTextField jTFFax;
    private javax.swing.JTextField jTFFecNacimiento;
    private javax.swing.JTextField jTFLimiteCredito;
    private javax.swing.JTextField jTFNombreBarrio;
    private javax.swing.JTextField jTFNombreCiudad;
    private javax.swing.JTextField jTFNombreCliente;
    private javax.swing.JTextField jTFNombreContacto;
    private javax.swing.JTextField jTFNombreCuentaCliente;
    private javax.swing.JTextField jTFNombreDpto;
    private javax.swing.JTextField jTFNombreGarante;
    private javax.swing.JTextField jTFNombrePais;
    private javax.swing.JTextField jTFNombreZona;
    private javax.swing.JTextField jTFNroCi;
    private javax.swing.JTextField jTFRuc;
    private javax.swing.JTextField jTFRucContacto;
    private javax.swing.JTextField jTFTelefono;
    // End of variables declaration//GEN-END:variables
}
