/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Andres
 */
public class DebCreEmpleadoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodDebCre = "SELECT MAX()";
}
