/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.GrupoBean;
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
public class GrupoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodGrupo = "SELECT MAX(cod_grupo) FROM grupo";
    
    String consultaGrupoDescripcion = "SELECT cod_grupo, descripcion, cod_centrocosto, cod_rubro, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, "
                               + "cod_usuario, es_mtprima, margen_base_vta FROM grupo WHERE descripcion LIKE ? ORDER BY descripcion";
    
    String getDescripcionGrupo = "SELECT descripcion FROM grupo WHERE cod_grupo = ?";
    
    String consultaGrupoCodigo = "SELECT cod_grupo, descripcion, cod_centrocosto, cod_rubro, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, "
                               + "cod_usuario, es_mtprima, margen_base_vta FROM grupo WHERE cod_grupo = ?";
    
    String catastraGrupo = "INSERT INTO grupo (cod_grupo, descripcion, cod_centrocosto, cod_rubro, fec_vigencia, cod_usuario, es_mtprima, margen_base_vta)"
                        + " VALUES (?, ?, ?, ?, now(), ?, ?, ?)";
    
    String modificaGrupo = "UPDATE grupo SET descripcion = ?, cod_centrocosto = ?, cod_rubro = ?, fec_vigencia = now(), cod_usuario = ?, "
                         + "es_mtprima = ?, margen_base_vta = ? WHERE cod_grupo = ?";
    
    public GrupoCtrl(){}
    
    public int buscaMaxCodGrupo(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodGrupo);
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
    
    public boolean modificaGrupo(GrupoBean grupo){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaGrupo);
            pstmt.setString(1, grupo.getDescripcion());
            pstmt.setInt(   2, grupo.getCodCentroCosto());
            pstmt.setInt(   3, grupo.getCodRubro());
            pstmt.setInt(   4, grupo.getCodUsuario());
            pstmt.setString(5, grupo.getEsMateriaPrima());
            pstmt.setDouble(6, grupo.getMargenBaseVenta());
            pstmt.setInt(   7, grupo.getCodGrupo()); // WHERE
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
    
    public boolean catastraGrupo(GrupoBean grupo){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraGrupo);
            pstmt.setInt(   1, grupo.getCodGrupo());
            pstmt.setString(2, grupo.getDescripcion());
            pstmt.setInt(   3, grupo.getCodCentroCosto());
            pstmt.setInt(   4, grupo.getCodRubro());
            pstmt.setInt(   5, grupo.getCodUsuario());
            pstmt.setString(6, grupo.getEsMateriaPrima());
            pstmt.setDouble(7, grupo.getMargenBaseVenta());
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
    
    public List<GrupoBean> listarGrupoDescripcion(String descripcion){
        List<GrupoBean> grupoList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaGrupoDescripcion);
            pstmt.setString(1, descripcion);
            rs = pstmt.executeQuery();
            GrupoBean bean = null;
            while(rs.next()){
                bean = new GrupoBean();
                bean.setCodCentroCosto(rs.getInt("cod_centrocosto"));
                bean.setCodGrupo(rs.getInt("cod_grupo"));
                bean.setCodRubro(rs.getInt("cod_rubro"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setEsMateriaPrima(rs.getString("es_mtprima"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setMargenBaseVenta(rs.getDouble("margen_base_vta"));
                grupoList.add(bean);
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
        return grupoList;
    }
    
    public GrupoBean buscaGrupoCodigo(int codGrupo){
        GrupoBean grupo = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaGrupoCodigo);
            pstmt.setInt(1, codGrupo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                grupo = new GrupoBean();
                grupo.setCodCentroCosto(rs.getInt("cod_centrocosto"));
                grupo.setCodGrupo(rs.getInt("cod_grupo"));
                grupo.setCodUsuario(rs.getInt("cod_usuario"));
                grupo.setDescripcion(rs.getString("descripcion"));
                grupo.setEsMateriaPrima(rs.getString("es_mtprima"));
                grupo.setFecVigencia(rs.getString("fec_vigencia"));
                grupo.setMargenBaseVenta(rs.getDouble("margen_base_vta"));
                grupo.setCodRubro(rs.getInt("cod_rubro"));
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
        return grupo;
    }
    
    public String getDescripcionGrupo(int codGrupo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionGrupo);
            pstmt.setInt(1, codGrupo);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("descripcion");
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
