/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controls;

import beans.PaisBean;
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
public class PaisCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodPais       = "SELECT MAX(cod_pais) FROM pais";
    
    String consultaPaisNombre    = "SELECT cod_pais, nombre, nacionalidad, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM pais WHERE nombre LIKE ? ORDER BY nombre";

    String consultaPaisGentilicio  = "SELECT cod_Pais, nombre, nacionalidad, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM pais WHERE gentilicio LIKE ? ORDER BY gentilicio";

    String getNombrePais = "SELECT nombre FROM pais WHERE cod_pais =  ?";

    String getGentilicioPais = "SELECT nacionalidad FROM pais WHERE cod_pais =  ?";
    
    String consultaPaisCodPais = "SELECT cod_pais, nombre, nacionalidad, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM pais WHERE cod_pais =  ?";
    
    String catastraPais = "INSERT INTO pais (nombre, nacionalidad, " +
            " fec_vigencia, cod_usuario) VALUES (?,?,now(),?)";
    
    String modificaPais = "UPDATE Pais SET nombre = ?, nacionalidad = ?, " +
            " fec_vigencia   = now(), cod_usuario    = ? " +
            " WHERE cod_pais = ?";
    
    public PaisCtrl() {
        
    }

    public int buscaMaxCodPais() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodPais);
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
    
    public boolean alterarPais(PaisBean Pais) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaPais);
            pstmt.setString(1, Pais.getNombre());
            pstmt.setString(2, Pais.getNacionalidad());
            pstmt.setInt(   3, Pais.getCodUsuario());
            pstmt.setInt(   4, Pais.getCodPais());  // where
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
    
    public boolean catastrarPais(PaisBean Pais){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraPais);
            pstmt.setString(1, Pais.getNombre());
            pstmt.setString(2, Pais.getNacionalidad());
            pstmt.setInt(   3, Pais.getCodUsuario());
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
    
    public List<PaisBean> listarPaisNombre(String nomPais){
        List<PaisBean> Pais = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaPaisNombre);
            pstmt.setString(1, nomPais); 
            rs = pstmt.executeQuery();
            PaisBean bean = null;
            while (rs.next()){
                bean = new PaisBean();
                bean.setCodPais(rs.getInt("cod_Pais"));
                bean.setNombre(rs.getString("nombre"));
                bean.setNacionalidad(rs.getString("nacionalidad"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                Pais.add(bean);
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
        return Pais;
    }

    public PaisBean buscaPaisCodPais(int codPais){
        PaisBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaPaisCodPais);
            pstmt.setInt(1, codPais);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new PaisBean();
                bean.setCodPais(rs.getInt("cod_Pais"));
                bean.setNombre(rs.getString("nombre"));
                bean.setNacionalidad(rs.getString("nacionalidad"));
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

    public String getNombrePais(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombrePais);
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

    public String getGentilicioPais(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getGentilicioPais);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("nacionalidad");
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

