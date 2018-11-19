/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.ConceptoBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class ConceptoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodConcepto = "SELECT MAX(cod_concepto) FROM concepto"; 
    
    String consultaConceptoDescripcion = "SELECT cod_concepto, des_concepto, tip_concepto, activo, to_char(fec_vigencia, 'dd/MM/yyyy') AS fecVigencia, "
                                       + "cod_usuario, debcre, es_imponible_ips, afecta_aguinaldo, cod_empresa, , asiento_anticipo, cod_cta_fin, "
                                       + "afecta_tesoreria FROM concepto WHERE des_concepto LIKE ? ORDER BY des_concepto";
    
    String getDescripcionConcepto = "SELECT des_concepto FROM concepto WHERE cod_concepto = ?";
    
    String consultaConceptoCodigo = "SELECT cod_concepto, des_concepto, tip_concepto, activo, to_char(fec_vigencia, 'dd/MM/yyyy') AS fecVigencia, "
                                  + "cod_usuario, debcre, es_imponible_ips, afecta_aguinaldo, cod_empresa, asiento_anticipo, cod_cta_fin, "
                                  + "afecta_tesoreria FROM concepto WHERE cod_concepto = ?";
    
    String catastraConcepto = "INSERT INTO concepto (cod_concepto, des_concepto, tip_concepto, activo, fec_vigencia, cod_usuario, debcre, es_imponible_ips, "
                            + "afecta_aguinaldo, cod_empresa, asiento_anticipo, cod_cta_fin, afecta_tesoreria) "
                            + "VALUES (?, ?, ?, ?, 'now()', ?, ?, ?, "
                                    + "?, ?, ?, ?, ?)" ;
    
    String modificaConcepto = "UPDATE concepto SET des_concepto = ?, tip_concepto = ?, activo = ?, fec_vigencia = 'now()', cod_usuario = ?, "
                            + "debcre = ?, es_imponible_ips = ?, afecta_aguinaldo = ?, cod_empresa = ?, asiento_anticipo = ?, cod_cta_fin = ?, "
                            + "afecta_tesoreria = ? WHERE cod_concepto = ?";
    
    public int buscaMaxCodConcepto() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodConcepto);
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
    
    public boolean alterarConcepto(ConceptoBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaConcepto);
            pstmt.setString( 1, bean.getDescConcepto());
            pstmt.setString( 2, bean.getTipConcepto());
            pstmt.setString( 3, bean.getActivo());
            pstmt.setInt(    4, bean.getCodUsuario());
            pstmt.setString( 5, bean.getDebCre());
            pstmt.setString( 6, bean.getEsImponibleIps());
            pstmt.setString( 7, bean.getAfectaAguinaldo());
            pstmt.setInt(    8, bean.getCodEmpresa());
            pstmt.setString( 9, bean.getAsientoAnticipo());
            pstmt.setInt(   10, bean.getCodCtaFin());
            pstmt.setString(11, bean.getAfectaTesoreria());
            pstmt.setInt(   12, bean.getCodConcepto());
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
    
    public boolean catastroConcepto(ConceptoBean bean){
        try{
            pstmt =  DBManager.conn.prepareStatement(catastraConcepto);
            pstmt.setInt(    1, bean.getCodConcepto());
            pstmt.setString( 2, bean.getDescConcepto());
            pstmt.setString( 3, bean.getTipConcepto());
            pstmt.setString( 4, bean.getActivo());
            pstmt.setInt(    5, bean.getCodUsuario());
            pstmt.setString( 6, bean.getDebCre());
            pstmt.setString( 7, bean.getEsImponibleIps());
            pstmt.setString( 8, bean.getAfectaAguinaldo());
            pstmt.setInt(    9, bean.getCodEmpresa());
            pstmt.setString(10, bean.getAsientoAnticipo());
            pstmt.setInt(   11, bean.getCodCtaFin());
            pstmt.setString(12, bean.getAfectaTesoreria());
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
    
    public ConceptoBean buscaConceptoCodigo(int codigo){
        ConceptoBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaConceptoCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new ConceptoBean();
                bean.setActivo(rs.getString("activo"));
                bean.setAfectaAguinaldo(rs.getString("afecta_aguinaldo"));
                bean.setAfectaTesoreria(rs.getString("afecta_tesoreria"));
                bean.setAsientoAnticipo(rs.getString("asiento_anticipo"));
                bean.setCodConcepto(rs.getInt("cod_concepto"));
                bean.setCodCtaFin(rs.getInt("cod_cta_fin"));
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDebCre(rs.getString("debcre"));
                bean.setDescConcepto(rs.getString("des_concepto"));
                bean.setEsImponibleIps(rs.getString("es_imponible_ips"));
                bean.setTipConcepto(rs.getString("tip_concepto"));
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
    
    public String getDescripcionConcepto(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionConcepto);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("des_concepto");
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
