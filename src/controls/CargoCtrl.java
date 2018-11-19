/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.CargoBean;
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
public class CargoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodCargo = "SELECT MAX(cod_cargo) FROM cargo";
    
    String consultaCargoDescripcion = "SELECT cod_cargo, descripcion, to_char(fec_vigencia, 'dd/MM/yyyy') as fecVigencia, cod_usuario, "
                                    + "es_ocupacion, cod_empresa FROM cargo WHERE descripcion LIKE ?";
    
    String consultaCargoCodigo = "SELECT cod_cargo, descripcion, to_char(fec_vigencia, 'dd/MM/yyyy') as fecVigencia, cod_usuario, "
                                    + "es_ocupacion, cod_empresa FROM cargo WHERE cod_cargo = ?";
    
    String getDescripcionCargo = "SELECT descripcion FROM cargo WHERE cod_cargo = ?" ;
    
    String catastraCargo = "INSERT INTO (cargo cod_cargo, descripcion, fec_vigencia, cod_usuario, es_ocupacion, cod_empresa) "
                         + "VALUES (?, ?, 'now()', ?, ?, ?)";
    
    String modificaCargo = "UPDATE cargo SET descripcion = ?, fec_vigencia = 'now()', cod_usuario = ?, es_ocupacion = ?, cod_empresa = ? "
                         + "WHERE cod_cargo = ?";
    
    public CargoCtrl(){}
    
    public int buscaMaxCodCargo() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodCargo);
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
    
    public boolean alteraCargo(CargoBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaCargo);
            pstmt.setString(1, bean.getDescripcion());
            pstmt.setInt(2, bean.getCodUsuario());
            pstmt.setString(3, bean.getEsOcupacion());
            pstmt.setInt(4, bean.getCodEmpresa());
            pstmt.setInt(5, bean.getCodCargo());
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
    
    public boolean catastraCargo(CargoBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraCargo);
            pstmt.setInt(1, bean.getCodCargo());
            pstmt.setString(2, bean.getDescripcion());
            pstmt.setInt(3, bean.getCodUsuario());
            pstmt.setString(4, bean.getEsOcupacion());
            pstmt.setInt(5, bean.getCodEmpresa());
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
    
    public List<CargoBean> buscarCargoDescripcion(String descripcion){
        List<CargoBean> cargoList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaCargoDescripcion);
            pstmt.setString(1, descripcion);
            rs = pstmt.executeQuery();
            CargoBean bean = null;
            while(rs.next()){
                bean = new CargoBean();
                bean.setCodCargo(rs.getInt("cod_cargo"));
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setEsOcupacion(rs.getString("es_ocupacion"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                cargoList.add(bean);
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
        return cargoList;
    }
    
    public CargoBean buscarCargoCodigo(int codigo){
        CargoBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaCargoCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new CargoBean();
                bean.setCodCargo(rs.getInt("cod_cargo"));
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setEsOcupacion(rs.getString("es_ocupacion"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
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
        
        return bean;
    }
    
    public String getDescripcionCargo(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionCargo);
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
