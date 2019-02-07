/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import principal.FormMain;

/**
 *
 * @author ANDRES
 */
public class MyListRenderer extends JLabel implements ListCellRenderer {
    ImageIcon selected, notSelected;
    
    public MyListRenderer(){
        selected = new ImageIcon("/" + FormMain.nombreCarpetaProyecto + "/Glyphs/image1.gif");
        notSelected = new ImageIcon("/" + FormMain.nombreCarpetaProyecto + "/Glyphs/image2.gif");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            setIcon(selected);
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setIcon(notSelected);
        }
        
        setOpaque(true);
        setText(value.toString());
        setFont(list.getFont());
        return (JLabel)this;
    }
}
