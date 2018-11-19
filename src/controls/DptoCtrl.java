/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controls;

import beans.DptoBean;
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
public class DptoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodDpto    = "SELECT MAX(cod_dpto) FROM departamento";
    
    String consultaDptoNombre = "SELECT cod_dpto, nombre, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM departamento WHERE nombre LIKE ? ORDER BY nombre";
    
    String getNombreDpto = "SELECT nombre FROM departamento WHERE cod_dpto =  ?";
    
    String consultaDptoCodDpto = "SELECT cod_dpto, nombre, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM departamento WHERE cod_dpto =  ?";
    
    String catastraDpto = "INSERT INTO departamento (nombre, fec_vigencia, cod_usuario) VALUES (?,now(),?)";
    
    String modificaDpto = "UPDATE departamento SET nombre = ?, fec_vigencia = now(), cod_usuario = ? "+
            " WHERE cod_dpto = ?";
    
    public DptoCtrl() {
        
    }

    public int buscaMaxCodDpto() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodDpto);
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
    
    public boolean alterarDpto(DptoBean Dpto) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaDpto);
            pstmt.setString(1, Dpto.getNomDpto());
            pstmt.setInt(   2, Dpto.getCodUsuario());
            pstmt.setInt(   3, Dpto.getCodDpto());  // where
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
    
    public boolean catastrarDpto(DptoBean Dpto){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraDpto);
            pstmt.setString(1, Dpto.getNomDpto());
            pstmt.setInt(   2, Dpto.getCodUsuario());
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
    
    public List<DptoBean> listarDptoNombre(String nomDpto){
        List<DptoBean> Dpto = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaDptoNombre);
            pstmt.setString(1, nomDpto); 
            rs = pstmt.executeQuery();
            DptoBean bean = null;
            while (rs.next()){
                bean = new DptoBean();
                bean.setCodDpto(rs.getInt("cod_Dpto"));
                bean.setNomDpto(rs.getString("nombre"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                Dpto.add(bean);
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
        return Dpto;
    }

    public DptoBean buscaDptoCodDpto(int codDpto){
        DptoBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaDptoCodDpto);
            pstmt.setInt(1, codDpto);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new DptoBean();
                bean.setCodDpto(rs.getInt("cod_Dpto"));
                bean.setNomDpto(rs.getString("nombre"));
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

    public String getNombreDpto(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreDpto);
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

