/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author ANDRES
 */
public class ClassMenu {
    
    static int posicion = 0;
    boolean mostrado = false;
    
    public ClassMenu(){}
    
    public void ensamblarMenu(JMenuBar jMnuBar){}
    
    public void permisosMenu(String user, String grupo, JMenuBar jMnuBar){
        ResultSet rsMnu = null;
        ResultSet rsMnuItem = null;
        
        if(!grupo.equals("1")){
            DesMenus(jMnuBar);
            DesItenes(jMnuBar);
            
            String sql =  "SELECT c.nombre_objeto AS menu "
                        + "FROM perfil_usuario a, modulo_menuitem b, modulo_menu c "
                        + "WHERE a.cod_item = b.cod_item "
                        + "AND a.cod_modulo = b.cod_modulo "
                        + "AND a.cod_menu = b.cod_menu "
                        + "AND a.cod_usuario_perfil = " + user + " "
                        + "AND a.cod_modulo = c.cod_modulo "
                        + "AND a.cod_menu = c.cod_menu "
                        + "GROUP BY c.nombre_objeto" ;
            
            String sql0 = "SELECT b.nombre_objeto AS menuitem " 
                        + "FROM perfil_usuario a, modulo_menuitem b, modulo_menu c " 
                        + "WHERE a.cod_item = b.cod_item  " 
                        + "AND a.cod_modulo = b.cod_modulo " 
                        + "AND a.cod_menu = b.cod_menu " 
                        + "AND a.cod_usuario_perfil  = " + user + " " 
                        + "AND a.cod_modulo = c.cod_modulo " 
                        + "AND a.cod_menu = c.cod_menu";
            
            System.out.println("SQL MENU: " + sql);
            System.out.println("SQL MENU-ITEM: " + sql0);
            
            rsMnu = DBManager.ejecutarDSL(sql);
            rsMnuItem = DBManager.ejecutarDSL(sql0);
            
            try{
                while(rsMnu.next()){
                    jMnuBar.setVisible(true);
                    if(jMnuBar.getName().equals("")){
                        JOptionPane.showMessageDialog(null, "nulo");
                    }
                    RecorrerMenus(rsMnu.getString("menu"), jMnuBar);
                }
                
                while(rsMnuItem.next()){
                    RecorrerItenes(rsMnuItem.getString("menuitem"), jMnuBar);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            jMnuBar.setVisible(true);
            Recorrer(jMnuBar);
        }
    }
    
    public void RecorrerMenus(String string, JMenuBar jmenubar) {
        Component[] arr = jmenubar.getComponents();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof JMenu) {
                JMenu jmenu = (JMenu) arr[i];
                String menu = "";
                
                menu = jmenu.getName();

                try{
                if (menu.equals(string)) {
                    jmenu.setVisible(true);
                    jmenu.setEnabled(true);
                } else {
                    if (!jmenu.isEnabled()) {
                        jmenu.setVisible(false);
                    }

                }
                }catch (Exception ex){
                    //JOptionPane.showMessageDialog(new JFrame(), jmenu.getText());
                }
            }
        }
    }
    
    public void RecorrerItenes(String menuitem, JMenuBar jmenubar) {
        Component[] arrMC;
        Component[] arr = jmenubar.getComponents();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof JMenu) {
                JMenu jmenu = (JMenu) arr[i];
                if (jmenu.isVisible()) {
                    arrMC = jmenu.getMenuComponents();
                    for (int im = 0; im < arrMC.length; im++) {
                        if (arrMC[im] instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) arrMC[im];
                            String item_menu = item.getName();
                            System.out.println("ITEM MENU (Viene de la class menu): " + item_menu );
                            if (item_menu.equals(menuitem)) {
                                item.setVisible(true);
                                item.setEnabled(true);
                                posicion = posicion + 1;
                            } else {
                                if (!item.isEnabled()) {
                                    item.setVisible(false);
                                }
                            }

                        } else {
                            arrMC[im].setVisible(false);
                        }
                    }
                }
            }
        }
    }
    
    public void Recorrer(JMenuBar jmenubar) {
        Component[] arrMC;
        Component[] arr = jmenubar.getComponents();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof JMenu) {
                JMenu jmenu = (JMenu) arr[i];
                if (jmenu.isVisible()) {
                    arrMC = jmenu.getMenuComponents();
                    for (int im = 0; im < arrMC.length; im++) {
                        if (arrMC[im] instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) arrMC[im];
                            item.setEnabled(true);
                        }
                    }
                }
            }
        }
    }
    
    public void DesMenus(JMenuBar jmenubar) {
        Component[] arr = jmenubar.getComponents();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof JMenu) {
                JMenu jmenu = (JMenu) arr[i];
                jmenu.setEnabled(false);
            }
        }
    }
    
    public void DesItenes(JMenuBar jmenubar) {
        Component[] arrMC;
        Component[] arr = jmenubar.getComponents();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof JMenu) {
                JMenu jmenu = (JMenu) arr[i];
                arrMC = jmenu.getMenuComponents();
                for (int im = 0; im < arrMC.length; im++) {
                    if (arrMC[im] instanceof JMenuItem) {
                        JMenuItem item = (JMenuItem) arrMC[im];
                        item.setEnabled(false);
                    }
                }
            }
        }
    }
    
}
