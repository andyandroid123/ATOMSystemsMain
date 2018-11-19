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
public class CentroCostoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String getNombreCentroCosto = "SELECT nom_centrocosto FROM centrocosto WHERE cod_centrocosto = ?";
    
    public String getNombreCentroCosto(int codCentroCosto){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreCentroCosto);
            pstmt.setInt(1, codCentroCosto);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("nom_centrocosto");
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
