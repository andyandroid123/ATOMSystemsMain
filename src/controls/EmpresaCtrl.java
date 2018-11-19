/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.EmpresaBean;
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
public class EmpresaCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodEmpresa = "SELECT MAX(cod_empresa) FROM empresa";
    
    String consultaEmpresaDefault = "SELECT cod_empresa, titulo_reportes, descripcion, cod_per_juridica, "
            + "ruc_empresa, direccion, ciudad, departamento, barrio, actividad, "
            + "nro_patronal, activa, es_empresadefault, cod_usuario, "
            + "to_char(fec_catastro, 'dd/MM/yyyy') AS fec_catastro, "
            + "to_char(fec_vigencia, 'dd/MM/yyyy') AS fec_vigencia "
            + "FROM empresa "
            + "WHERE es_empresadefault LIKE ?";
    
    String consultaEmpresaCodEmpresa = "SELECT cod_empresa, titulo_reportes, descripcion, cod_per_juridica, "
            + "ruc_empresa, direccion, ciudad, departamento, barrio, actividad, "
            + "nro_patronal, activa, es_empresadefault, barrio, actividad, "
            + "to_char(fec_catastro, 'dd/MM/yyyy') AS fec_catastro, "
            + "to_char(fec_vigencia, 'dd/MM/yyyy') AS fec_vigencia"
            + "FROM empresa "
            + "WHERE cod_empresa = ?";
    
    String getDescripcionEmpresa = "SELECT descripcion FROM empresa WHERE cod_empresa = ?";
    
    public int buscaMaxCodEmpresa(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodEmpresa);
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
    
    public List<EmpresaBean> buscaEmpresaDefault(String soloEmpresaDefault){
        List<EmpresaBean> listEmpresaBean = new ArrayList();
        EmpresaBean empresaBean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaEmpresaDefault);
            pstmt.setString(1, soloEmpresaDefault);
            rs = pstmt.executeQuery();
            while(rs.next()){
                empresaBean = new EmpresaBean();
                empresaBean.setCodEmpresa(rs.getInt("cod_empresa"));
                empresaBean.setDescripcion(rs.getString("descripcion"));
                empresaBean.setActiva(rs.getString("actividad"));
                empresaBean.setCodPersonaJuridica(rs.getString("cod_per_juridica"));
                empresaBean.setRucEmpresa(rs.getString("ruc_empresa"));
                empresaBean.setNroPatronal(rs.getString("nro_patronal"));
                empresaBean.setTituloReportes(rs.getString("titulo_reportes"));
                empresaBean.setActiva(rs.getString("activa"));
                empresaBean.setEmpresaDefault(rs.getString("es_empresadefault"));
                empresaBean.setDireccion(rs.getString("direccion"));
                empresaBean.setBarrio(rs.getString("barrio"));
                empresaBean.setCiudad(rs.getString("ciudad"));
                empresaBean.setFecCatastro(rs.getString("fec_catastro"));
                empresaBean.setFecVigencia(rs.getString("fec_vigencia"));
                empresaBean.setCodUsuario(rs.getInt("cod_usuario"));
                listEmpresaBean.add(empresaBean);
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
        return listEmpresaBean;
    }
    
    
    public EmpresaBean buscaEmpresaCodEmpresa(int codEmpresa){
        EmpresaBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaEmpresaCodEmpresa);
            pstmt.setInt(1, codEmpresa);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new EmpresaBean();
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setActividad(rs.getString("actividad"));
                bean.setCodPersonaJuridica(rs.getString("cod_per_juridica"));
                bean.setRucEmpresa(rs.getString("ruc_empresa"));
                bean.setNroPatronal(rs.getString("nro_patronal"));
                bean.setTituloReportes(rs.getString("titulo_reportes"));
                bean.setActiva(rs.getString("activa"));
                bean.setEmpresaDefault(rs.getString("es_empresadefault"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setBarrio(rs.getString("barrio"));
                bean.setCiudad(rs.getString("ciudad"));
                bean.setFecCatastro(rs.getString("fec_catastro"));
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
    
    public String getDescripcionEmpresa(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionEmpresa);
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
