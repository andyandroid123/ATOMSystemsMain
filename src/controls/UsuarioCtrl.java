/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.UsuarioBean;
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
public class UsuarioCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodUsuario = "SELECT MAX(cod_usuario) FROM usuario";
    
    String consultaUsuarioNombre = "SELECT cod_usuario, nombre, clave, alias, es_cajero, es_fiscal, activo, to_char(fec_catastro, 'dd-MM-yyyy hh:mm:ss'), "
                                 + "to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss'), cod_perfil, codigo_usuario, cod_grupo_usuario, fec_vence_clave, clave_plana, locales_habilitados "
                                 + "FROM usuario WHERE nombre like ? ORDER BY nombre";
    
    String getNombreUsuario = "SELECT nombre FROM usuario WHERE cod_usuario = ?";
    
    String consultaUsuarioCodigo = "SELECT cod_usuario, nombre, clave, alias, es_cajero, es_fiscal, activo, to_char(fec_catastro, 'dd-MM-yyyy hh:mm:ss') AS fecCatastro, "
                                 + "to_char(fec_vigencia, 'dd/MM/yyyy hh:mm:ss') AS fecVigencia, cod_perfil, codigo_usuario, cod_grupo_usuario, fec_vence_clave, clave_plana, locales_habilitados "
                                 + "FROM usuario WHERE cod_usuario = ?";
    
    String catastraUsuario = "INSERT INTO usuario (cod_usuario, nombre, clave, alias, es_cajero, es_fiscal, activo, fec_catastro, fec_vigencia, cod_perfil, "
                           + "codigo_usuario, cod_grupo_usuario, fec_vence_clave, clave_plana, locales_habilitados) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?, ?, ?)";
    
    String modificaUsuario = "UPDATE usuario SET nombre = ?, clave = ?, alias = ?, es_cajero = ?, es_fiscal = ?, activo = ?, "
                           + "fec_vigencia = now(), cod_perfil = ?, codigo_usuario = ?, cod_grupo_usuario = ?, fec_vence_clave = ?, clave_plana = ?, locales_habilitados = ? "
                           + "WHERE cod_usuario = ?";
    
    public UsuarioCtrl(){}
    
    public int buscaMaxCodUsuario(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodUsuario);
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
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    public boolean modificaUsuario(UsuarioBean usuario){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaUsuario);
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getClave());
            pstmt.setString(3, usuario.getAlias());
            pstmt.setString(4, usuario.getEsCajero());
            pstmt.setString(5, usuario.getEsFiscal());
            pstmt.setString(6, usuario.getActivo());
            pstmt.setInt(7, usuario.getCodPerfil());
            pstmt.setInt(8, usuario.getCodigoUsuario());
            pstmt.setInt(9, usuario.getCodGrupoUsuario());
            pstmt.setString(10, usuario.getFecVenceClave());
            pstmt.setString(11, usuario.getClavePlana());
            pstmt.setString(12, usuario.getLocalesHabilitados());
            pstmt.setInt(13, usuario.getCodUsuario()); // WHERE
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
    
    public boolean catastraUsuario(UsuarioBean usuario){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraUsuario);
            pstmt.setInt(1, usuario.getCodUsuario());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getClave());
            pstmt.setString(4, usuario.getAlias());
            pstmt.setString(5, usuario.getEsCajero());
            pstmt.setString(6, usuario.getEsFiscal());
            pstmt.setString(7, usuario.getActivo());            
            pstmt.setString(8, usuario.getFecCatastro());
            pstmt.setInt(9, usuario.getCodPerfil());
            pstmt.setInt(10, usuario.getCodigoUsuario());
            pstmt.setInt(11, usuario.getCodGrupoUsuario());
            pstmt.setString(12, usuario.getFecVenceClave());
            pstmt.setString(13, usuario.getClavePlana());
            pstmt.setString(14, usuario.getLocalesHabilitados());
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
    
    public List<UsuarioBean> listarUsuarioNombre(String nombre){
        List<UsuarioBean> usuarioList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaUsuarioNombre);
            pstmt.setString(1, nombre);
            rs = pstmt.executeQuery();
            UsuarioBean usuario = null;
            while(rs.next()){
                usuario = new UsuarioBean();
                usuario.setActivo(rs.getString("activo"));
                usuario.setAlias(rs.getString("alias"));
                usuario.setClave(rs.getString("clave"));
                usuario.setClavePlana(rs.getString("clave_plana"));
                usuario.setCodGrupoUsuario(rs.getInt("cod_grupo_usuario"));
                usuario.setCodPerfil(rs.getInt("cod_perfil"));
                usuario.setCodUsuario(rs.getInt("cod_usuario"));
                usuario.setCodigoUsuario(rs.getInt("codigo_usuario"));
                usuario.setEsCajero(rs.getString("es_cajero"));
                usuario.setEsFiscal(rs.getString("es_fiscal"));
                usuario.setFecCatastro(rs.getString("fec_catastro"));
                usuario.setFecVenceClave(rs.getString("fec_vence_clave"));
                usuario.setFecVigencia(rs.getString("fec_vigencia"));
                usuario.setLocalesHabilitados(rs.getString("locales_habilitados"));
                usuario.setNombre(rs.getString("nombre"));
                usuarioList.add(usuario);
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
        return usuarioList;
    }
    
    public UsuarioBean buscaUsuarioCodigo(int codUsuario){
        UsuarioBean usuarioBean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaUsuarioCodigo);
            pstmt.setInt(1, codUsuario);
            rs = pstmt.executeQuery();
            while(rs.next()){
                usuarioBean = new UsuarioBean();
                usuarioBean.setActivo(rs.getString("activo"));
                usuarioBean.setAlias(rs.getString("alias"));
                usuarioBean.setClave(rs.getString("clave"));
                usuarioBean.setClavePlana(rs.getString("clave_plana"));
                usuarioBean.setCodGrupoUsuario(rs.getInt("cod_grupo_usuario"));
                usuarioBean.setCodPerfil(rs.getInt("cod_perfil"));
                usuarioBean.setCodUsuario(rs.getInt("cod_usuario"));
                usuarioBean.setCodigoUsuario(rs.getInt("codigo_usuario"));
                usuarioBean.setEsCajero(rs.getString("es_cajero"));
                usuarioBean.setEsFiscal(rs.getString("es_fiscal"));
                usuarioBean.setFecCatastro(rs.getString("fecCatastro"));
                usuarioBean.setFecVenceClave(rs.getString("fec_vence_clave"));
                usuarioBean.setFecVigencia(rs.getString("fecVigencia"));
                usuarioBean.setLocalesHabilitados(rs.getString("locales_habilitados"));
                usuarioBean.setNombre(rs.getString("nombre"));
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
        return usuarioBean;
    }
    
    public String getNombreUsuario(int codUsuario){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreUsuario);
            pstmt.setInt(1, codUsuario);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("nombre");
            }
        }catch(SQLException ex){
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
