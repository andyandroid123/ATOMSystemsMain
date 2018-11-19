/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.ClienteBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class ClienteCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodCliente    = "SELECT MAX(cod_cliente) FROM cliente";
    
    String consultaClienteNombre = "SELECT cod_cliente, razon_soc, contacto, ci_contacto, ruc_cliente, es_juridica, cod_ciudad, cod_barrio, direccion, "
                                 + "telefono, fax, email, cod_dpto, cond_pago, limite_credito, cod_usuario, to_char(fec_vigencia, 'dd/MM/yyyy hh:mm:ss am') AS fecVigencia, "
                                 + "acepta_cheque, activo, cod_pcuenta, pct_max_dcto, cod_lista_precio, cod_zona, apodo, to_char(fec_nacimiento, 'dd/MM/yyyy') AS fec_nacimiento, celular, cod_pais, "
                                 + "ci_origen, doc_inmigracion, vive_alquiler, cod_representante, cod_garante, dependientes, giro, cod_clasificacion, "
                                 + "cod_local_pref, sexo, estado_civil, es_contribuyente FROM cliente WHERE razon_soc LIKE ? ORDER BY razon_soc";
    
    String getNombreCliente = "SELECT razon_soc FROM cliente WHERE cod_cliente = ?";
    
    String consultaClienteCodigo = "SELECT cod_cliente, razon_soc, contacto, ci_contacto, ruc_cliente, es_juridica, cod_ciudad, cod_barrio, direccion, "
                                 + "telefono, fax, email, cod_dpto, cond_pago, limite_credito, cod_usuario, to_char(fec_vigencia, 'dd/MM/yyyy hh:mm:ss am') AS fecVigencia, "
                                 + "acepta_cheque, activo, cod_pcuenta, pct_max_dcto, cod_lista_precio, cod_zona, apodo, to_char(fec_nacimiento, 'dd/MM/yyyy') AS fec_nacimiento, celular, cod_pais, "
                                 + "ci_origen, doc_inmigracion, vive_alquiler, cod_representante, cod_garante, dependientes, giro, cod_clasificacion, "
                                 + "cod_local_pref, sexo, estado_civil, es_contribuyente FROM cliente WHERE cod_cliente = ?";
    
    String consultaClienteRuc = "SELECT cod_cliente, razon_soc, contacto, ci_contacto, ruc_cliente, es_juridica, cod_ciudad, cod_barrio, direccion, "
                              + "telefono, fax, email, cod_dpto, cond_pago, limite_credito, cod_usuario, to_char(fec_vigencia, 'dd/MM/yyyy hh:mm:ss am') AS fecVigencia, "
                              + "acepta_cheque, activo, cod_pcuenta, pct_max_dcto, cod_lista_precio, cod_zona, apodo, to_char(fec_nacimiento, 'dd/MM/yyyy') AS fec_nacimiento, celular, cod_pais, "
                              + "ci_origen, doc_inmigracion, vive_alquiler, cod_representante, cod_garante, dependientes, giro, cod_clasificacion, "
                              + "cod_local_pref, sexo, estado_civil, es_contribuyente FROM cliente WHERE ruc_cliente LIKE ?";
    
    String catastrarCliente = "INSERT INTO cliente (cod_cliente, razon_soc, contacto, ci_contacto, ruc_cliente, es_juridica, cod_ciudad, cod_barrio, direccion, "
                            + "telefono, fax, email, cod_dpto, cond_pago, limite_credito, cod_usuario, fec_vigencia, "
                            + "acepta_cheque, activo, cod_pcuenta, pct_max_dcto, cod_lista_precio, cod_zona, apodo, fec_nacimiento, celular, cod_pais, "
                            + "ci_origen, doc_inmigracion, vive_alquiler, cod_representante, cod_garante, dependientes, giro, cod_clasificacion, "
                            + "cod_local_pref, sexo, estado_civil, es_contribuyente) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'now()',?,?,?,?,?,?,?,to_date(?, 'dd/MM/yyyy')',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    String modificaCliente = "UPDATE cliente SET razon_soc = ?, contacto = ?, ci_contacto = ?, ruc_cliente = ?, es_juridica = ?, cod_ciudad = ?, cod_barrio = ?, "
                           + "direccion = ?, telefono = ?, fax = ?, email = ?, cod_dpto = ?, cond_pago = ?, limite_credito = ?, cod_usuario = ?, fec_vigencia = 'now()', "
                           + "acepta_cheque = ?, activo = ?, cod_pcuenta = ?, pct_max_dcto = ?, cod_lista_precio = ?, cod_zona = ?, apodo = ?, fec_nacimiento = to_date(?, 'dd/MM/yyyy'), "
                           + "celular = ?, cod_pais = ?, ci_origen = ?, doc_inmigracion = ?, vive_alquiler = ?, cod_representante = ?, cod_garante = ?, "
                           + "dependientes = ?, giro = ?, cod_clasificacion = ?, cod_local_pref = ?, sexo = ?, estado_civil = ?, es_contribuyente = ? "
                           + "WHERE cod_cliente = ?";
    
    public ClienteCtrl(){}
    
    public List<ClienteBean> listarClienteRuc(String ruc){
        List<ClienteBean> clienteList = new ArrayList<>();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaClienteRuc);
            pstmt.setString(1, ruc);
            rs = pstmt.executeQuery();
            ClienteBean bean = null;
            while (rs.next()) {
                bean = new ClienteBean();
                bean.setCodCliente(rs.getInt("cod_cliente"));
                bean.setRazonSoc(rs.getString("razon_soc"));
                bean.setContacto(rs.getString("contacto"));
                bean.setCiContacto(rs.getString("ci_contacto"));
                bean.setRucCliente(rs.getString("ruc_cliente"));
                bean.setEsJuridica(rs.getString("es_juridica"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setFax(rs.getString("fax"));
                bean.setEmail(rs.getString("email"));
                bean.setCodDpto(rs.getInt("cod_dpto"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setLimiteCredito(rs.getInt("limite_credito"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setAceptaCheque(rs.getString("acepta_cheque"));
                bean.setActivo(rs.getString("activo"));
                bean.setCodPCuenta(rs.getString("cod_pcuenta"));
                bean.setPctMaxDescuento(rs.getInt("pct_max_dcto"));
                bean.setCodListaPrecio(rs.getInt("cod_lista_precio"));
                bean.setCodZona(rs.getInt("cod_zona"));
                bean.setApodo(rs.getString("apodo"));
                bean.setFecNacimiento(rs.getString("fec_nacimiento"));
                bean.setCelular(rs.getString("celular"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCiOrigen(rs.getString("ci_origen"));
                bean.setDocInmigracion(rs.getString("doc_inmigracion"));
                bean.setViveAlquiler(rs.getString("vive_alquiler"));
                bean.setCodRepresentante(rs.getInt("cod_representante"));
                bean.setCodGarante(rs.getInt("cod_garante"));
                bean.setDependientes(rs.getInt("dependientes"));
                bean.setGiro(rs.getInt("giro"));
                bean.setCodClasificacion(rs.getString("cod_clasificacion"));
                bean.setCodLocalPreferencia(rs.getInt("cod_local_pref"));
                bean.setSexo(rs.getString("sexo"));
                bean.setEstadoCivil(rs.getString("estado_civil"));
                bean.setEsContribuyente(rs.getString("es_contribuyente"));
                clienteList.add(bean);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return clienteList;
    }
    
    public List<ClienteBean> listarClienteRazonSoc(String razonSoc){
        List<ClienteBean> clienteList = new ArrayList<>();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaClienteNombre);
            pstmt.setString(1, razonSoc);
            rs = pstmt.executeQuery();
            ClienteBean bean = null;
            while (rs.next()) {
                bean = new ClienteBean();
                bean.setCodCliente(rs.getInt("cod_cliente"));
                bean.setRazonSoc(rs.getString("razon_soc"));
                bean.setContacto(rs.getString("contacto"));
                bean.setCiContacto(rs.getString("ci_contacto"));
                bean.setRucCliente(rs.getString("ruc_cliente"));
                bean.setEsJuridica(rs.getString("es_juridica"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setFax(rs.getString("fax"));
                bean.setEmail(rs.getString("email"));
                bean.setCodDpto(rs.getInt("cod_dpto"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setLimiteCredito(rs.getInt("limite_credito"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setAceptaCheque(rs.getString("acepta_cheque"));
                bean.setActivo(rs.getString("activo"));
                bean.setCodPCuenta(rs.getString("cod_pcuenta"));
                bean.setPctMaxDescuento(rs.getInt("pct_max_dcto"));
                bean.setCodListaPrecio(rs.getInt("cod_lista_precio"));
                bean.setCodZona(rs.getInt("cod_zona"));
                bean.setApodo(rs.getString("apodo"));
                bean.setFecNacimiento(rs.getString("fec_nacimiento"));
                bean.setCelular(rs.getString("celular"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCiOrigen(rs.getString("ci_origen"));
                bean.setDocInmigracion(rs.getString("doc_inmigracion"));
                bean.setViveAlquiler(rs.getString("vive_alquiler"));
                bean.setCodRepresentante(rs.getInt("cod_representante"));
                bean.setCodGarante(rs.getInt("cod_garante"));
                bean.setDependientes(rs.getInt("dependientes"));
                bean.setGiro(rs.getInt("giro"));
                bean.setCodClasificacion(rs.getString("cod_clasificacion"));
                bean.setCodLocalPreferencia(rs.getInt("cod_local_pref"));
                bean.setSexo(rs.getString("sexo"));
                bean.setEstadoCivil(rs.getString("estado_civil"));
                bean.setEsContribuyente(rs.getString("es_contribuyente"));
                clienteList.add(bean);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return clienteList;
    }
    
    public ClienteBean buscaClienteCodCliente(int codigo) {
        ClienteBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaClienteCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new ClienteBean();
                bean.setCodCliente(rs.getInt("cod_cliente"));
                bean.setRazonSoc(rs.getString("razon_soc"));
                bean.setContacto(rs.getString("contacto"));
                bean.setCiContacto(rs.getString("ci_contacto"));
                bean.setRucCliente(rs.getString("ruc_cliente"));
                bean.setEsJuridica(rs.getString("es_juridica"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setFax(rs.getString("fax"));
                bean.setEmail(rs.getString("email"));
                bean.setCodDpto(rs.getInt("cod_dpto"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setLimiteCredito(rs.getInt("limite_credito"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setAceptaCheque(rs.getString("acepta_cheque"));
                bean.setActivo(rs.getString("activo"));
                bean.setCodPCuenta(rs.getString("cod_pcuenta"));
                bean.setPctMaxDescuento(rs.getInt("pct_max_dcto"));
                bean.setCodListaPrecio(rs.getInt("cod_lista_precio"));
                bean.setCodZona(rs.getInt("cod_zona"));
                bean.setApodo(rs.getString("apodo"));
                bean.setFecNacimiento(rs.getString("fec_nacimiento"));
                bean.setCelular(rs.getString("celular"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCiOrigen(rs.getString("ci_origen"));
                bean.setDocInmigracion(rs.getString("doc_inmigracion"));
                bean.setViveAlquiler(rs.getString("vive_alquiler"));
                bean.setCodRepresentante(rs.getInt("cod_representante"));
                bean.setCodGarante(rs.getInt("cod_garante"));
                bean.setDependientes(rs.getInt("dependientes"));
                bean.setGiro(rs.getInt("giro"));
                bean.setCodClasificacion(rs.getString("cod_clasificacion"));
                bean.setCodLocalPreferencia(rs.getInt("cod_local_pref"));
                bean.setSexo(rs.getString("sexo"));
                bean.setEstadoCivil(rs.getString("estado_civil"));
                bean.setEsContribuyente(rs.getString("es_contribuyente"));
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return bean;
    }
    
    public String getNombreCliente(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreCliente);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("razon_soc");
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return result;
    }
    
    public boolean catastrarCliente(ClienteBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastrarCliente);
            pstmt.setInt(    1, bean.getCodCliente());
            pstmt.setString( 2, bean.getRazonSoc());
            pstmt.setString( 3, bean.getContacto());
            pstmt.setString( 4, bean.getCiContacto());
            pstmt.setString( 5, bean.getRucCliente());
            pstmt.setString( 6, bean.getEsJuridica());
            pstmt.setInt(    7, bean.getCodCiudad());
            pstmt.setInt(    8, bean.getCodBarrio());
            pstmt.setString( 9, bean.getDireccion());
            pstmt.setString(10, bean.getTelefono());
            pstmt.setString(11, bean.getFax());
            pstmt.setString(12, bean.getEmail());
            pstmt.setInt(   13, bean.getCodDpto());
            pstmt.setInt(   14, bean.getCondPago());
            pstmt.setDouble(15, bean.getLimiteCredito());
            pstmt.setInt(   16, bean.getCodUsuario());
            pstmt.setString(17, bean.getAceptaCheque());
            pstmt.setString(18, bean.getActivo());
            pstmt.setString(19, bean.getCodPCuenta());
            pstmt.setInt(   20, bean.getPctMaxDescuento());
            pstmt.setInt(   21, bean.getCodListaPrecio());
            pstmt.setInt(   22, bean.getCodZona());
            pstmt.setString(23, bean.getApodo());
            pstmt.setString(  24, bean.getFecNacimiento());
            pstmt.setString(25, bean.getCelular());
            pstmt.setInt(   26, bean.getCodPais());
            pstmt.setString(27, bean.getCiOrigen());
            pstmt.setString(28, bean.getDocInmigracion());
            pstmt.setString(29, bean.getViveAlquiler());
            pstmt.setInt(   30, bean.getCodRepresentante());
            pstmt.setInt(   31, bean.getCodGarante());
            pstmt.setInt(   32, bean.getDependientes());
            pstmt.setInt(   33, bean.getGiro());
            pstmt.setString(34, bean.getCodClasificacion());
            pstmt.setInt(   35, bean.getCodLocalPreferencia());
            pstmt.setString(36, bean.getSexo());
            pstmt.setString(37, bean.getEstadoCivil());
            pstmt.setString(38, bean.getEsContribuyente());
            
            if (pstmt.executeUpdate() > 0) {
                DBManager.conn.commit();
                return true;
            } else {
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean alterarCliente(ClienteBean bean) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaCliente);
            pstmt.setString( 1, bean.getRazonSoc());
            pstmt.setString( 2, bean.getContacto());
            pstmt.setString( 3, bean.getCiContacto());
            pstmt.setString( 4, bean.getRucCliente());
            pstmt.setString( 5, bean.getEsJuridica());
            pstmt.setInt(    6, bean.getCodCiudad());
            pstmt.setInt(    7, bean.getCodBarrio());
            pstmt.setString( 8, bean.getDireccion());
            pstmt.setString( 9, bean.getTelefono());
            pstmt.setString(10, bean.getFax());
            pstmt.setString(11, bean.getEmail());
            pstmt.setInt(   12, bean.getCodDpto());
            pstmt.setInt(   13, bean.getCondPago());
            pstmt.setDouble(14, bean.getLimiteCredito());
            pstmt.setInt(   15, bean.getCodUsuario());
            pstmt.setString(16, bean.getAceptaCheque());
            pstmt.setString(17, bean.getActivo());
            pstmt.setString(18, bean.getCodPCuenta());
            pstmt.setInt(   19, bean.getPctMaxDescuento());
            pstmt.setInt(   20, bean.getCodListaPrecio());
            pstmt.setInt(   21, bean.getCodZona());
            pstmt.setString(22, bean.getApodo());
            pstmt.setString(  23, bean.getFecNacimiento());
            pstmt.setString(24, bean.getCelular());
            pstmt.setInt(   25, bean.getCodPais());
            pstmt.setString(26, bean.getCiOrigen());
            pstmt.setString(27, bean.getDocInmigracion());
            pstmt.setString(28, bean.getViveAlquiler());
            pstmt.setInt(   29, bean.getCodRepresentante());
            pstmt.setInt(   30, bean.getCodGarante());
            pstmt.setInt(   31, bean.getDependientes());
            pstmt.setInt(   32, bean.getGiro());
            pstmt.setString(33, bean.getCodClasificacion());
            pstmt.setInt(   34, bean.getCodLocalPreferencia());
            pstmt.setString(35, bean.getSexo());
            pstmt.setString(36, bean.getEstadoCivil());
            pstmt.setString(37, bean.getEsContribuyente());
            pstmt.setInt(   38, bean.getCodCliente());  // where
            
            if (pstmt.executeUpdate() > 0) {
                DBManager.conn.commit();
                return true;
            } else {
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        } 
    }
    
    public int buscaMaxCodCliente() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodCliente);
            rs = pstmt.executeQuery();
            if (rs.next()){
                result = rs.getInt(1);
            }
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return result;
    }        
}
