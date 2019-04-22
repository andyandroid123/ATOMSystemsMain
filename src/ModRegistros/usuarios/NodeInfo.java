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
public class NodeInfo {
    private ArrayList<STNodo> fuente;
    
    public NodeInfo(String des, int cod, int nivel){
        try{
            this.fuente = new ArrayList();
            getFuente().add(new STNodo(cod, des.trim(), nivel));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * @return the fuente
     */
    public ArrayList<STNodo> getFuente() {
        return fuente;
    }
}
