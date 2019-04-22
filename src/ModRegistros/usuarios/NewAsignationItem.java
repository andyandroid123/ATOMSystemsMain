/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.util.ArrayList;

/**
 *
 * @author ANDRES
 */
public class NewAsignationItem {
    
    private ArrayList<StructuraList> fuente;
    
    public NewAsignationItem(int cod, String des, int cod1, int cod2){
        try{
            this.fuente = new ArrayList();
            getFuente().add(new StructuraList(cod, des, cod1, cod2));
        }catch(Exception ex){}
    }

    /**
     * @return the fuente
     */
    public ArrayList<StructuraList> getFuente() {
        return fuente;
    }
}
