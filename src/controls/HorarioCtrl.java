/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.HorarioBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class HorarioCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String getDescHorario = "SELECT des_horario FROM horario WHERE cod_horario =  ?";
    
    String buscaMaxCodHorario = "SELECT MAX(cod_horario) FROM horario";
    
    String consultaHorarioCodigo = "SELECT cod_horario, des_horario, entrada1, salida1, entrada2, salida2, entrada3, salida3, tolerancia, activo, "
                                 + "to_char(fec_vigencia, 'dd-MM-yyyy hh:mm:ss') AS fecVigencia, cod_usuario, entrada4, salida4, cod_empresa "
                                 + "FROM horario "
                                 + "WHERE cod_horario = ?";
    
    String catastraHorario = "INSERT INTO horario (cod_horario, des_horario, entrada1, salida1, entrada2, salida2, entrada3, salida3, tolerancia, activo, "
                           + "fec_vigencia, cod_usuario, entrada4, salida4, cod_empresa) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                           + "'now()', ?, ?, ?, ?)";
    
    String modificaHorario = "UPDATE horario SET des_horario = ?, entrada1 = ?, salida1 = ?, entrada2 = ?, salida2 = ?, entrada3 = ?, salida3 = ?, "
                           + "tolerancia = ?, activo = ?, fec_vigencia = 'now()', cod_usuario = ?, entrada4 = ?, salida4 = ?, cod_empresa = ? "
                           + "WHERE cod_horario = ?";
    
    public HorarioCtrl(){}
    
    public int buscaMaxCodHorario() {
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodHorario);
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
    
    public boolean alterarHorario(HorarioBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaHorario);
            pstmt.setString( 1, bean.getDesHorario());
            pstmt.setString( 2, bean.getEntrada1());
            pstmt.setString( 3, bean.getSalida1());
            pstmt.setString( 4, bean.getEntrada2());
            pstmt.setString( 5, bean.getSalida2());
            pstmt.setString( 6, bean.getEntrada3());
            pstmt.setString( 7, bean.getSalida3());
            pstmt.setString( 8, bean.getTolerancia());
            pstmt.setString( 9, bean.getActivo());
            pstmt.setInt(   10, bean.getCodUsuario());
            pstmt.setString(11, bean.getEntrada4());
            pstmt.setString(12, bean.getSalida4());
            pstmt.setInt(   13, bean.getCodEmpresa());
            pstmt.setInt(   14, bean.getCodHorario());
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
    
    public boolean catastraHorario(HorarioBean bean){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraHorario);
            pstmt.setInt(    1, bean.getCodHorario());
            pstmt.setString( 2, bean.getDesHorario());
            pstmt.setString( 3, bean.getEntrada1());
            pstmt.setString( 4, bean.getSalida1());
            pstmt.setString( 5, bean.getEntrada2());
            pstmt.setString( 6, bean.getSalida2());
            pstmt.setString( 7, bean.getEntrada3());
            pstmt.setString( 8, bean.getSalida3());
            pstmt.setString( 9, bean.getTolerancia());
            pstmt.setString(10, bean.getActivo());
            pstmt.setInt(   11, bean.getCodUsuario());
            pstmt.setString(12, bean.getEntrada4());
            pstmt.setString(13, bean.getSalida4());
            pstmt.setInt(   14, bean.getCodEmpresa());
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
    
    public HorarioBean buscaHorarioCodigo(int codigo){
        HorarioBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaHorarioCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new HorarioBean();
                bean.setActivo(rs.getString("activo"));
                bean.setCodEmpresa(rs.getInt("cod_empresa"));
                bean.setCodHorario(rs.getInt("cod_horario"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDesHorario(rs.getString("des_horario"));
                bean.setEntrada1(rs.getString("entrada1"));
                bean.setEntrada2(rs.getString("entrada2"));
                bean.setEntrada3(rs.getString("entrada3"));
                bean.setEntrada4(rs.getString("entrada4"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setSalida1(rs.getString("salida1"));
                bean.setSalida2(rs.getString("salida2"));
                bean.setSalida3(rs.getString("salida3"));
                bean.setSalida4(rs.getString("salida4"));
                bean.setTolerancia(rs.getString("tolerancia"));
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
    
    public String getDescHorario(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescHorario);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("des_horario");
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
