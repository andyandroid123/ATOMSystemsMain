/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.ProveedorBean;
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
public class ProveedorCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodProveedor = "SELECT MAX(cod_proveedor) FROM proveedor";
    
    String consultaProveedorDescripcion = "SELECT cod_proveedor, razon_soc, contacto, tel_contacto, ruc_proveedor, direccion, cod_ciudad, cod_barrio, "
                                        + "telefono, fax, email, web, cod_pais, cond_pago, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, "
                                        + "cod_usuario, activo FROM proveedor WHERE razon_soc LIKE ? ORDER BY razon_soc";
    
    String consultaProveedorRuc = "SELECT cod_proveedor, razon_soc, contacto, tel_contacto, ruc_proveedor, direccion, cod_ciudad, cod_barrio, "
                                + "telefono, fax, email, web, cod_pais, cond_pago, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, "
                                + "cod_usuario, activo FROM proveedor WHERE ruc_proveedor LIKE ? ORDER BY razon_soc";
    
    String getRazonSocialProveedor = "SELECT razon_soc FROM proveedor WHERE cod_proveedor = ?";
    
    String consultaProveedorCodigo = "SELECT cod_proveedor, razon_soc, contacto, tel_contacto, ruc_proveedor, direccion, cod_ciudad, cod_barrio, "
                                   + "telefono, fax, email, web, cod_pais, cond_pago, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, "
                                   + "cod_usuario, activo FROM proveedor WHERE cod_proveedor = ?";
    
    String catastraProveedor = "INSERT INTO proveedor (cod_proveedor, razon_soc, contacto, tel_contacto, ruc_proveedor, direccion, cod_ciudad, "
                             + "cod_barrio, telefono, fax, email, web, cod_pais, cond_pago, fec_vigencia, cod_usuario, activo) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?)";
    
    String modificaProveedor = "UPDATE proveedor SET razon_soc = ?, contacto = ?, tel_contacto = ?, ruc_proveedor = ?, direccion = ?, cod_ciudad = ?, "
                             + "cod_barrio = ?, telefono = ?, fax = ?, email = ?, web = ?, cod_pais = ?, cond_pago = ?, fec_vigencia = now(), "
                             + "cod_usuario = ?, activo = ? WHERE cod_proveedor = ?";
    
    public ProveedorCtrl(){}
    
    public int buscaMaxCodProveedor(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodProveedor);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }finally{
            try{
                rs.close();
            }catch(SQLException sqlex){
                sqlex.printStackTrace();
            }
        }
        return result;
    }
    
    
    public boolean modificaProveedor(ProveedorBean proveedor){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaProveedor);
            pstmt.setString(1, proveedor.getRazonSocial());
            pstmt.setString(2, proveedor.getContacto());
            pstmt.setString(3, proveedor.getTelContacto());
            pstmt.setString(4, proveedor.getRucProveedor());
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setInt(6, proveedor.getCodCiudad());
            pstmt.setInt(7, proveedor.getCodBarrio());
            pstmt.setString(8, proveedor.getTelefono());
            pstmt.setString(9, proveedor.getFax());
            pstmt.setString(10, proveedor.getEmail());
            pstmt.setString(11, proveedor.getWeb());
            pstmt.setInt(12, proveedor.getCodPais());
            pstmt.setInt(13, proveedor.getCondPago());
            pstmt.setInt(14, proveedor.getCodUsuario());
            pstmt.setString(15, proveedor.getActivo());
            pstmt.setInt(16, proveedor.getCodProveedor()); // WHERE
            if(pstmt.executeUpdate() > 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean catastraProveedor(ProveedorBean proveedor){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraProveedor);
            pstmt.setInt(1, proveedor.getCodProveedor());
            pstmt.setString(2, proveedor.getRazonSocial());
            pstmt.setString(3, proveedor.getContacto());
            pstmt.setString(4, proveedor.getTelContacto());
            pstmt.setString(5, proveedor.getRucProveedor());
            pstmt.setString(6, proveedor.getDireccion());
            pstmt.setInt(7, proveedor.getCodCiudad());
            pstmt.setInt(8, proveedor.getCodBarrio());
            pstmt.setString(9, proveedor.getTelefono());
            pstmt.setString(10, proveedor.getFax());
            pstmt.setString(11, proveedor.getEmail());
            pstmt.setString(12, proveedor.getWeb());
            pstmt.setInt(13, proveedor.getCodPais());
            pstmt.setInt(14, proveedor.getCondPago());
            pstmt.setInt(15, proveedor.getCodUsuario());
            pstmt.setString(16, proveedor.getActivo());
            if(pstmt.executeUpdate() > 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public List<ProveedorBean> listarProveedorRazonSoc(String razonSoc){
        List<ProveedorBean> proveedorList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaProveedorDescripcion);
            pstmt.setString(1, razonSoc);
            rs = pstmt.executeQuery();
            ProveedorBean bean = null;
            while(rs.next()){
                bean = new ProveedorBean();
                bean.setActivo(rs.getString("activo"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCodProveedor(rs.getInt("cod_proveedor"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setContacto(rs.getString("contacto"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setEmail(rs.getString("direccion"));
                bean.setFax(rs.getString("fax"));
                bean.setFecVigencia("fec_vigencia");
                bean.setRazonSocial(rs.getString("razon_soc"));
                bean.setRucProveedor(rs.getString("ruc_proveedor"));
                bean.setTelContacto(rs.getString("tel_contacto"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setWeb(rs.getString("web"));
                proveedorList.add(bean);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return proveedorList;
    }
    
    public List<ProveedorBean> listarProveedorRuc(String ruc){
        List<ProveedorBean> proveedorList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaProveedorRuc);
            pstmt.setString(1, ruc);
            rs = pstmt.executeQuery();
            ProveedorBean bean = null;
            while(rs.next()){
                bean = new ProveedorBean();
                bean.setActivo(rs.getString("activo"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCodProveedor(rs.getInt("cod_proveedor"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setContacto(rs.getString("contacto"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setEmail(rs.getString("direccion"));
                bean.setFax(rs.getString("fax"));
                bean.setFecVigencia("fec_vigencia");
                bean.setRazonSocial(rs.getString("razon_soc"));
                bean.setRucProveedor(rs.getString("ruc_proveedor"));
                bean.setTelContacto(rs.getString("tel_contacto"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setWeb(rs.getString("web"));
                proveedorList.add(bean);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return proveedorList;
    }
    
    public ProveedorBean buscaProveedorCodigo(int codigo){
        ProveedorBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaProveedorCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new ProveedorBean();
                bean.setActivo(rs.getString("activo"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setCodProveedor(rs.getInt("cod_proveedor"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setCondPago(rs.getInt("cond_pago"));
                bean.setContacto(rs.getString("contacto"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setEmail(rs.getString("direccion"));
                bean.setFax(rs.getString("fax"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setRazonSocial(rs.getString("razon_soc"));
                bean.setRucProveedor(rs.getString("ruc_proveedor"));
                bean.setTelContacto(rs.getString("tel_contacto"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setWeb(rs.getString("web"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return bean;
    }
    
    public String getRazonSocProveedor(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getRazonSocialProveedor);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("razon_soc");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}
