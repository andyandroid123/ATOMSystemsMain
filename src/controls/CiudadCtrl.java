/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controls;

import beans.CiudadBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Claudio Kunnen
 */
public class CiudadCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodCiudad    = "SELECT MAX(cod_ciudad) FROM ciudad";
    
    String consultaCiudadNombre = "SELECT cod_ciudad, nombre, cod_dpto, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM ciudad WHERE nombre LIKE ? ORDER BY nombre";
    
    String getNombreCiudad = "SELECT nombre FROM ciudad WHERE cod_ciudad =  ?";
    
    String consultaCiudadCodCiudad = "SELECT cod_ciudad, nombre, cod_dpto, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM ciudad WHERE cod_ciudad =  ?";
    
    String catastraCiudad = "INSERT INTO ciudad (cod_ciudad, nombre, cod_dpto, " +
            " fec_vigencia, cod_usuario) VALUES (?,?,?,now(),?)";
    
    String modificaCiudad = "UPDATE ciudad SET nombre = ?, cod_dpto = ?, " +
            " fec_vigencia = now(), cod_usuario = ? " +
            " WHERE cod_ciudad = ?";
    
    public CiudadCtrl() {
        
    }

    public int buscaMaxCodCiudad() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodCiudad);
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
    
    public boolean alterarCiudad(CiudadBean ciudad) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaCiudad);
            pstmt.setString(1, ciudad.getNombre());
            pstmt.setInt(   2, ciudad.getCodCiudad());
            pstmt.setInt(   3, ciudad.getCodDepartamento());
            pstmt.setInt(   4, ciudad.getCodUsuario());
            pstmt.setInt(   5, ciudad.getCodCiudad());  // where
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
    
    public boolean catastrarCiudad(CiudadBean ciudad){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraCiudad);
            pstmt.setInt(1, ciudad.getCodCiudad());
            pstmt.setString(   2, ciudad.getNombre());
            pstmt.setInt(   3, ciudad.getCodDepartamento());
            pstmt.setInt(   4, ciudad.getCodUsuario());
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
    
    public List<CiudadBean> listarCiudadNombre(String nomCiudad){
        List<CiudadBean> ciudad = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaCiudadNombre);
            pstmt.setString(1, nomCiudad); 
            rs = pstmt.executeQuery();
            CiudadBean bean = null;
            while (rs.next()) {
                bean = new CiudadBean();
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setNombre(rs.getString("nombre"));
                bean.setCodDepartamento(rs.getInt("cod_dpto"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                ciudad.add(bean);
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
        return ciudad;
    }

    public CiudadBean buscaCiudadCodCiudad(int codCiudad) {
        CiudadBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaCiudadCodCiudad);
            pstmt.setInt(1, codCiudad);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CiudadBean();
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setNombre(rs.getString("nombre"));
                bean.setCodDepartamento(rs.getInt("cod_dpto"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
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

    public String getNombreCiudad(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreCiudad);
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

