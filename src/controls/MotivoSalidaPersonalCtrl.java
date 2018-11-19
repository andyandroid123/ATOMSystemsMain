/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.MotivoSalidaPersonalBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class MotivoSalidaPersonalCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodMotivo = "SELECT MAX(cod_motivo_salida) FROM motivo_salida_personal";
    
    String getDescripcionMotivo = "SELECT descripcion FROM motivo_salida_personal WHERE cod_motivo_salida = ?";
    
    String consultaMotivoCodigo = "SELECT cod_motivo_salida, descripcion, indemnizacion, preaviso, vac_proporcional, vac_causada, dcto_preaviso, "
                                + "cod_local, cod_empresa, dcto_indemnizacion, to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fecVigencia, cod_usuario "
                                + "FROM motivo_salida_personal "
                                + "WHERE cod_motivo_salida = ?";
    
    String catastraMotivo = "INSERT INTO motivo_salida_personal (cod_motivo_salida, descripcion, indemnizacion, preaviso, vac_proporcional, vac_causada, "
                          + "dcto_preaviso, cod_local, cod_empresa, dcto_indemnizacion, fec_vigencia, cod_usuario) "
                          + "VALUES(?, ?, ?, ?, ?, ?, "
                                 + "?, ?, ?, ?, 'now()', ?)";
    
    String modificaMotivo = "UPDATE motivo_salida_personal SET descripcion = ?, indemnizacion = ?, preaviso = ?, vac_proporcional = ?, "
                          + "vac_causada = ?, dcto_preaviso = ?, cod_local = ?, cod_empresa = ?, dcto_indemnizacion = ?, fec_vigencia = 'now()',"
                          + "cod_usuario = ? "
                          + "WHERE cod_motivo_salida = ?";
    
    public int buscaMaxCodMotivo() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodMotivo);
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
    
    public boolean alterarMotivo(MotivoSalidaPersonalBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaMotivo);
            pstmt.setString( 1, bean.getDescripcion());
            pstmt.setString( 2, bean.getIndemnizacion());
            pstmt.setString( 3, bean.getPreaviso());
            pstmt.setString( 4, bean.getVacProporcional());
            pstmt.setString( 5, bean.getVacCausada());
            pstmt.setString( 6, bean.getDctoPreaviso());
            pstmt.setInt(    7, bean.getCodLocal());
            pstmt.setInt(    8, bean.getCodEmpresa());
            pstmt.setString( 9, bean.getDctoIndemnizacion());
            pstmt.setInt(10, bean.getCodUsuario());
            pstmt.setInt(   11, bean.getCodMotivoSalida());
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
    
    public boolean catastraMotivo(MotivoSalidaPersonalBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraMotivo);
            pstmt.setInt(    1, bean.getCodMotivoSalida());
            pstmt.setString( 2, bean.getDescripcion());
            pstmt.setString( 3, bean.getIndemnizacion());
            pstmt.setString( 4, bean.getPreaviso());
            pstmt.setString( 5, bean.getVacProporcional());
            pstmt.setString( 6, bean.getVacCausada());
            pstmt.setString( 7, bean.getDctoPreaviso());
            pstmt.setInt(    8, bean.getCodLocal());
            pstmt.setInt(    9, bean.getCodEmpresa());
            pstmt.setString(10, bean.getDctoIndemnizacion());
            pstmt.setInt(   11, bean.getCodUsuario());
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
    
    public MotivoSalidaPersonalBean buscaMotivoCodigo(int codigo){
        MotivoSalidaPersonalBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaMotivoCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new MotivoSalidaPersonalBean();
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodLocal(rs.getInt("cod_local"));
                bean.setCodMotivoSalida(rs.getInt("cod_motivo_salida"));
                bean.setDctoIndemnizacion(rs.getString("dcto_indemnizacion"));
                bean.setDctoPreaviso(rs.getString("dcto_preaviso"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setIndemnizacion(rs.getString("indemnizacion"));
                bean.setPreaviso(rs.getString("preaviso"));
                bean.setVacCausada(rs.getString("vac_causada"));
                bean.setVacProporcional(rs.getString("vac_proporcional"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
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
    
    public String getDescripcionMotivo(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionMotivo);
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
