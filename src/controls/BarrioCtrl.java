/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.BarrioBean;
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
public class BarrioCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodBarrio    = "SELECT MAX(cod_Barrio) FROM Barrio";
    
    String consultaBarrioNombre = "SELECT cod_barrio, nombre, cod_ciudad, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM barrio WHERE nombre LIKE ? ORDER BY nombre";
    
    String getNombreBarrio = "SELECT nombre FROM barrio WHERE cod_barrio =  ?";
    
    String consultaBarrioCodBarrio = "SELECT cod_barrio, nombre, cod_ciudad, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM barrio WHERE cod_barrio =  ?";
    
    String catastraBarrio = "INSERT INTO barrio (cod_barrio, nombre, cod_ciudad, " +
            " fec_vigencia, cod_usuario) VALUES (?,?,now(),?)";
    
    String modificaBarrio = "UPDATE Barrio SET nombre = ?, cod_ciudad = ?, " +
            " fec_vigencia   = now(), cod_usuario    = ? " +
            " WHERE cod_barrio = ?";
    
    /*public BarrioCtrl() {
        
    }*/

    public int buscaMaxCodBarrio() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodBarrio);
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
    
    public boolean alterarBarrio(BarrioBean Barrio) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaBarrio);
            pstmt.setString(1, Barrio.getNomBarrio());
            pstmt.setInt(   2, Barrio.getCodCiudad());
            pstmt.setInt(   3, Barrio.getCodUsuario());
            pstmt.setInt(   4, Barrio.getCodBarrio());  // where
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
    
    public boolean catastrarBarrio(BarrioBean Barrio){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraBarrio);
            pstmt.setInt(1, Barrio.getCodBarrio());
            pstmt.setString(   2, Barrio.getNomBarrio());
            pstmt.setInt(   3, Barrio.getCodCiudad());
            pstmt.setInt(   4, Barrio.getCodUsuario());
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
    
    public List<BarrioBean> listarBarrioNombre(String nomBarrio){
        List<BarrioBean> Barrio = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaBarrioNombre);
            pstmt.setString(1, nomBarrio); 
            rs = pstmt.executeQuery();
            BarrioBean bean = null;
            while (rs.next()){
                bean = new BarrioBean();
                bean.setCodBarrio(rs.getInt("cod_Barrio"));
                bean.setNomBarrio(rs.getString("nombre"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                Barrio.add(bean);
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
        return Barrio;
    }

    public BarrioBean buscaBarrioCodBarrio(int codBarrio){
        BarrioBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaBarrioCodBarrio);
            pstmt.setInt(1, codBarrio);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new BarrioBean();
                bean.setCodBarrio(rs.getInt("cod_Barrio"));
                bean.setNomBarrio(rs.getString("nombre"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
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

    public String getNombreBarrio(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreBarrio);
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
