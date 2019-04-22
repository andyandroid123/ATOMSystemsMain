/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author ANDRES
 */
public class Jbox {
    
    private ArrayList<Structura> fuente;
    
    public Jbox(ResultSet rs){
        try{
            this.fuente = new ArrayList();
            while(rs.next()){
                getFuente().add(new Structura(rs.getInt(1), rs.getString(2).trim()));
            }
        }catch(Exception ex){}
    }

    /**
     * @return the fuente
     */
    public ArrayList<Structura> getFuente() {
        return fuente;
    }
    
}
