/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.MarcaBean;
import beans.ProfesionBean;
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
public class ProfesionCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodProfesion       = "SELECT MAX(cod_profesion) FROM profesion";
    
    String consultaProfesionDescripcion    = "SELECT cod_profesion, descripcion, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM profesion WHERE descripcion LIKE ? ORDER BY descripcion";

    String getProfesionDescripcion = "SELECT descripcion FROM profesion WHERE cod_profesion =  ?";

    String consultaProfesionCodigo = "SELECT cod_profesion, descripcion, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM profesion WHERE cod_profesion =  ?";
    
    String catastraProfesion = "INSERT INTO profesion (cod_profesion, descripcion, " +
            " fec_vigencia, cod_usuario) VALUES (?, ?, 'now()', ?)";
    
    String modificaProfesion = "UPDATE profesion SET descripcion = ?, " +
            " fec_vigencia   = now(), cod_usuario    = ? " +
            " WHERE cod_profesion = ?";
    
    public ProfesionCtrl(){}
    
    public int buscaMaxCodProfesion() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodProfesion);
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
    
    public boolean alterarProfesion(ProfesionBean profesion) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaProfesion);
            pstmt.setString(1, profesion.getDescripcion());
            pstmt.setInt(2, profesion.getCodUsuario());
            pstmt.setInt(3, profesion.getCodProfesion());  // where
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
    
    public boolean catastrarProfesion(ProfesionBean profesion){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraProfesion);
            pstmt.setInt(1, profesion.getCodProfesion());
            pstmt.setString(2, profesion.getDescripcion());
            pstmt.setInt(3, profesion.getCodUsuario());
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
    
    public List<ProfesionBean> listarProfesionDescripcion(String descripcion){
        List<ProfesionBean> profesion = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaProfesionDescripcion);
            pstmt.setString(1, descripcion); 
            rs = pstmt.executeQuery();
            ProfesionBean bean = null;
            while (rs.next()){
                bean = new ProfesionBean();
                bean.setCodProfesion(rs.getInt("cod_profesion"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                profesion.add(bean);
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
        return profesion;
    }

    public ProfesionBean listarProfesionCodigo(int codigo){
        ProfesionBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaProfesionCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new ProfesionBean();
                bean.setCodProfesion(rs.getInt("cod_profesion"));
                bean.setDescripcion(rs.getString("descripcion"));
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

    public String getDescripcionProfesion(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getProfesionDescripcion);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("descripcion");
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
