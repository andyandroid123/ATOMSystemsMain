/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.sql.ResultSet;
import java.util.ArrayList;
import utiles.DBManager;

/**
 *
 * @author ANDRES
 */
public class ItemList {
    private ArrayList<StructuraList> fuente;
    
    public ItemList(ResultSet rs, boolean flag){
        if(flag){
            try{
                this.fuente = new ArrayList();
                while(rs.next()){
                    getFuente().add(new StructuraList(rs.getInt(1), rs.getString(2).trim(), rs.getInt(3), rs.getInt(4)));
                }
            }catch(Exception ex){}
        }else{
            try{
                this.fuente = new ArrayList();
                while(rs.next()){
                    String nombre = getDescripcionItem(rs.getInt("cod_menu"), rs.getInt("cod_item"));
                    getFuente().add(new StructuraList(rs.getInt(1), nombre.trim(), rs.getInt(2), rs.getInt(3)));
                }
            }catch(Exception e){}
        }
    }

    private String getDescripcionItem(int codmenu, int coditem){
        String result = "";
        String sql = null;
        ResultSet rs_m = null;
        if(coditem ==0){
            sql = "SELECT descripcion FROM modulo_menu WHERE cod_menu = " + String.valueOf(codmenu);
        }else{
            sql = "SELECT descripcion FROM modulo_menuitem WHERE cod_item = " + String.valueOf(coditem);
        }
        rs_m = DBManager.ejecutarDSL(sql);
        try{
            if(rs_m.next()){
                result = rs_m.getString("descripcion");
            }
        }catch(Exception ex){}
        finally{
            DBManager.CerrarStatements();
        }
        return result;
    }
    
    /**
     * @return the fuente
     */
    public ArrayList<StructuraList> getFuente() {
        return fuente;
    }
}
