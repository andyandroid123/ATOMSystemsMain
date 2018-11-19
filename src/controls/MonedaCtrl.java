/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.MonedaBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class MonedaCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodMoneda = "SELECT MAX(cod_moneda) FROM moneda";
    
    String getNombreMoneda = "SELECT nombre FROM moneda WHERE cod_moneda = ?";
    
    String catastraMoneda = "INSERT INTO moneda (cod_moneda, fec_vigencia, nombre, alias, cotiz_compra, cotiz_venta, vigente, cod_usuario, "
                          + "operacion, decimales, base_redondeo) VALUES (?,now(),?,?,?,?,?, ?,?,?,?)";
    
    String modificaMoneda = "UPDATE moneda SET fec_vigencia = now(), nombre = ?, alias = ?, cotiz_compra = ?, cotiz_venta = ?, cod_usuario = ? "
                          + "WHERE cod_moneda = ?" ;
    
    String consultaMonedaCodMoneda = "SELECT cod_moneda, nombre, alias, cotiz_compra, cotiz_venta, to_char(fec_vigencia, 'dd/MM/yyyy hh:mm:ss') AS fecVigencia, "
                                   + "cod_usuario FROM moneda "
                                   + "WHERE cod_moneda = ?";
    
    public MonedaBean buscaMonedaCodMoneda(int codigo){
        MonedaBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaMonedaCodMoneda);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new MonedaBean();
                bean.setCodMoneda(rs.getInt("cod_moneda"));
                bean.setNombre(rs.getString("nombre"));
                bean.setAlias(rs.getString("alias"));
                bean.setCotCompra(rs.getInt("cotiz_compra"));
                bean.setCotVenta(rs.getInt("cotiz_venta"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return bean;
    }
    
    public boolean catastraMoneda(MonedaBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraMoneda);
            pstmt.setInt(1, bean.getCodMoneda());
            pstmt.setString(2, bean.getNombre());
            pstmt.setString(3, bean.getAlias());
            pstmt.setInt(4, bean.getCotCompra());
            pstmt.setInt(5, bean.getCotVenta());
            pstmt.setString(6, bean.getVigente());
            pstmt.setInt(7, bean.getCodUsuario());
            pstmt.setString(8, bean.getOperacion());
            pstmt.setInt(9, bean.getDecimales());
            pstmt.setInt(10, bean.getBaseRedondeo());
            if (pstmt.executeUpdate() > 0) {
                DBManager.conn.commit();
                return true;
            } else {
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean modificaMoneda(MonedaBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaMoneda);
            pstmt.setString(1, bean.getNombre());
            pstmt.setString(2, bean.getAlias());
            pstmt.setInt(3, bean.getCotCompra());
            pstmt.setInt(4, bean.getCotVenta());
            pstmt.setInt(5, bean.getCodUsuario());
            pstmt.setInt(6, bean.getCodMoneda());
            
            if (pstmt.executeUpdate() > 0) {
                DBManager.conn.commit();
                return true;
            } else {
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public int buscaMaxCodMoneda() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodMoneda);
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
    
    public String getNombreMoneda(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreMoneda);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("nombre");
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
    
}
