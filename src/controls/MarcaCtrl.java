/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controls;

import beans.MarcaBean;
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
public class MarcaCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodMarca       = "SELECT MAX(cod_marca) FROM marca";
    
    String consultaMarcaDescripcion    = "SELECT cod_marca, descripcion, cod_pais, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM marca WHERE descripcion LIKE ? ORDER BY descripcion";

    String getMarcaDescripcion = "SELECT descripcion FROM marca WHERE cod_marca =  ?";

    String consultaMarcaCodigo = "SELECT cod_marca, descripcion, cod_pais, " +
            " to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fec_vigencia, cod_usuario " +
            " FROM marca WHERE cod_marca =  ?";
    
    String catastraMarca = "INSERT INTO marca (cod_marca, descripcion, cod_pais, " +
            " fec_vigencia, cod_usuario) VALUES (?,?,?,now(),?)";
    
    String modificaMarca = "UPDATE marca SET descripcion = ?, cod_pais = ?, " +
            " fec_vigencia   = now(), cod_usuario    = ? " +
            " WHERE cod_marca = ?";
    
    public MarcaCtrl() {
        
    }

    public int buscaMaxCodMarca() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodMarca);
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
    
    public boolean alterarMarca(MarcaBean marca) {
        try{
            pstmt = DBManager.conn.prepareStatement(modificaMarca);
            pstmt.setString(1, marca.getDescripcion());
            pstmt.setInt(2, marca.getCodPais());
            pstmt.setInt(3, marca.getCodUsuario());
            pstmt.setInt(4, marca.getCodMarca());  // where
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
    
    public boolean catastrarMarca(MarcaBean marca){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraMarca);
            pstmt.setInt(1, marca.getCodMarca());
            pstmt.setString(2, marca.getDescripcion());
            pstmt.setInt(3, marca.getCodPais());
            pstmt.setInt(4, marca.getCodUsuario());
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
    
    public List<MarcaBean> listarMarcaDescripcion(String descripcion){
        List<MarcaBean> marca = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaMarcaDescripcion);
            pstmt.setString(1, descripcion); 
            rs = pstmt.executeQuery();
            MarcaBean bean = null;
            while (rs.next()){
                bean = new MarcaBean();
                bean.setCodMarca(rs.getInt("cod_marca"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setCodPais(rs.getInt("cod_pais"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                marca.add(bean);
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
        return marca;
    }

    public MarcaBean buscaMarcaCodigo(int codigo){
        MarcaBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaMarcaCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new MarcaBean();
                bean.setCodMarca(rs.getInt("cod_marca"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setCodPais(rs.getInt("cod_pais"));
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

    public String getDescripcionMarca(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getMarcaDescripcion);
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

