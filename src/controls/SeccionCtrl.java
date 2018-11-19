/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.SeccionBean;
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
public class SeccionCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodSeccion = "SELECT MAX(cod_seccion) FROM seccion";
    
    String consultaSeccionDescripcion = "SELECT cod_seccion, des_seccion, cod_centrocosto, activo, to_char(fec_vigencia, 'dd/MM/yyyy') as fecVigencia, "
                                      + "cod_usuario, cod_empresa, carga_horaria FROM seccion WHERE des_seccion LIKE ?";
    
    String getDescripcionSeccion = "SELECT des_seccion FROM seccion WHERE cod_seccion = ?";
    
    String consultaSeccionCodigo = "SELECT cod_seccion, des_seccion, cod_centrocosto, activo, to_char(fec_vigencia, 'dd/MM/yyyy') as fecVigencia, "
                                 + "cod_usuario, cod_empresa, carga_horaria FROM seccion WHERE cod_seccion = ?";
    
    String catastraSeccion = "INSERT INTO seccion (cod_seccion, des_seccion, cod_centrocosto, activo, fec_vigencia, cod_usuario, cod_empresa, "
                           + "carga_horaria) VALUES (?, ?, ?, ?,'now()', ?, ?, ?)";
    
    String modificaSeccion = "UPDATE seccion SET des_seccion = ?, cod_centrocosto = ?, activo = ?, fec_vigencia = 'now()', cod_usuario = ?, "
                           + "cod_empresa = ?, carga_horaria = ? WHERE cod_seccion = ?";   
    
    public int buscaMaxCodSeccion() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodSeccion);
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
    
    public boolean alteraSeccion(SeccionBean sBean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaSeccion);
            pstmt.setString(1, sBean.getDescripcion());
            pstmt.setInt(2, sBean.getCodCentroCosto());
            pstmt.setString(3, sBean.getActivo());
            pstmt.setInt(4, sBean.getCodUsuario());
            pstmt.setInt(5, sBean.getCodEmpresa());
            pstmt.setInt(6, sBean.getCargaHoraria());
            pstmt.setInt(7, sBean.getCodSeccion()); // where
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
    
    public boolean catastrarSeccion(SeccionBean sBean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraSeccion);
            pstmt.setInt(1, sBean.getCodSeccion());
            pstmt.setString(2, sBean.getDescripcion());
            pstmt.setInt(3, sBean.getCodCentroCosto());
            pstmt.setString(4, sBean.getActivo());
            pstmt.setInt(5, sBean.getCodUsuario());
            pstmt.setInt(6, sBean.getCodEmpresa());
            pstmt.setInt(7, sBean.getCargaHoraria());
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
    
    public List<SeccionBean> buscaSeccionDescripcion(String descripcion){
        List<SeccionBean> seccionList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaSeccionDescripcion);
            pstmt.setString(1, descripcion);
            rs = pstmt.executeQuery();
            SeccionBean sBean = null;
            while(rs.next()){
                sBean = new SeccionBean();
                sBean.setActivo(rs.getString("activo"));
                sBean.setCargaHoraria(rs.getInt("carga_horaria"));
                sBean.setCodCentroCosto(rs.getInt("cod_centrocosto"));
                sBean.setCodEmpresa(rs.getInt("cod_empresa"));
                sBean.setCodSeccion(rs.getInt("cod_seccion"));
                sBean.setCodUsuario(rs.getInt("cod_usuario"));
                sBean.setDescripcion(rs.getString("des_seccion"));
                sBean.setFecVigencia(rs.getString("fecVigencia"));
                seccionList.add(sBean);
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
        return seccionList;
    }
    
    public SeccionBean buscaSeccionCodigo(int codigo){
        SeccionBean sBean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaSeccionCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                sBean = new SeccionBean();
                sBean.setActivo(rs.getString("activo"));
                sBean.setCargaHoraria(rs.getInt("carga_horaria"));
                sBean.setCodCentroCosto(rs.getInt("cod_centrocosto"));
                sBean.setCodEmpresa(rs.getInt("cod_empresa"));
                sBean.setCodSeccion(rs.getInt("cod_seccion"));
                sBean.setCodUsuario(rs.getInt("cod_usuario"));
                sBean.setDescripcion(rs.getString("des_seccion"));
                sBean.setFecVigencia(rs.getString("fecVigencia"));
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
        return sBean;
    }
    
    public String getDescripcionSeccion(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionSeccion);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("des_seccion");
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
