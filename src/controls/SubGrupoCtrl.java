/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utiles.DBManager;

/**
 *
 * @author Andres
 */
public class SubGrupoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String getDescripcionSubGrupo = "SELECT descripcion FROM subgrupo WHERE cod_subgrupo = ?";
    
    public String getDescripcionSubGrupo(int codSGrupo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionSubGrupo);
            pstmt.setInt(1, codSGrupo);
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
