/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controls;

import beans.LocalBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andrés 
 */

public class LocalCtrl {
    PreparedStatement pstmt;
    ResultSet rs;

    String buscaMaxCodLocal = "SELECT MAX(cod_local) FROM local";

    String consultaLocalDefaultCodEmpresa = "SELECT cod_empresa, cod_local, descripcion, titulo_reporte, es_localdefault, "+
       " es_matriz, actividad, departamento, direccion, barrio, ciudad, "+
       " telefono, fax, activo, cod_usuario, "+
       " to_char(fec_catastro, 'dd/MM/yyyy') AS fec_catastro, "+
       " to_char(fec_vigencia, 'dd/MM/yyyy') AS fec_vigencia, "+
       " cod_sector_default, punto_exp_fac, ip_servidor, nombre_servidor, punto_exp_remision, id_socket "+
       " FROM local "+
       " WHERE cod_empresa = ? "+
       "   AND es_localdefault LIKE ?";
    
    String getDescripcionLocal = "SELECT descripcion FROM local WHERE cod_local =  ?";

    public int buscaMaxCodLocal() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodLocal);
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

    public List<LocalBean> buscaLocalDefaultCodEmpresa(int codEmpresa, String soloLocalDefault) {
        List<LocalBean> listLocalBean = new ArrayList();
        LocalBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaLocalDefaultCodEmpresa);
            pstmt.setInt(1, codEmpresa);
            pstmt.setString(2, soloLocalDefault);
            rs = pstmt.executeQuery();
            while (rs.next()){
                bean = new LocalBean();
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodLocal(rs.getInt("cod_local"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setActividad(rs.getString("actividad"));
                bean.setTituloReporte(rs.getString("titulo_reporte"));
                bean.setLocalDefault(rs.getString("es_localdefault"));
                bean.setEsMatriz(rs.getString("es_matriz"));
                bean.setTelefono(rs.getString("telefono"));
                bean.setFax(rs.getString("fax"));
                bean.setCodSectorDefault(rs.getInt("cod_sector_default"));
                bean.setIpServidor(rs.getString("ip_servidor"));
                bean.setNombreServidor(rs.getString("nombre_servidor"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setBarrio(rs.getString("barrio"));
                bean.setCiudad(rs.getString("ciudad"));
                bean.setPuntoExpFactura(rs.getInt("punto_exp_fac"));
                bean.setPuntoExpRemision(rs.getInt("punto_exp_remision"));
                bean.setActivo(rs.getString("activo"));
                bean.setFecCatastro(rs.getString("fec_catastro"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setIdSocket(rs.getInt("id_socket"));
                listLocalBean.add(bean);
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
        return listLocalBean;
    }

    public String getDescripcionLocal(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionLocal);
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
