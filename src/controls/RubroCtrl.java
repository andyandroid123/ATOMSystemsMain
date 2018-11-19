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
public class RubroCtrl {
    
    
    PreparedStatement pstmt;
    String getDescripcionRubro = "SELECT descripcion FROM rubro WHERE cod_rubro = ?";
    
    public String getDescripcionGrupo(int codRubro){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionRubro);
            pstmt.setInt(1, codRubro);
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
