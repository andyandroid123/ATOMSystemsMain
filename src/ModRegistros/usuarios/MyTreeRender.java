/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import principal.FormMain;

/**
 *
 * @author ANDRES
 */
public class MyTreeRender extends JLabel implements TreeCellRenderer{

    private ImageIcon iconoHoja, iconoAbierto, iconoCerrado;
    
    public MyTreeRender(){
        iconoHoja = new ImageIcon("/" + FormMain.nombreCarpetaProyecto + "/Glyphs/hoja.gif");
        iconoAbierto = new ImageIcon("/" + FormMain.nombreCarpetaProyecto + "/Glyphs/proyecto.gif");
        iconoCerrado = new ImageIcon("/" + FormMain.nombreCarpetaProyecto + "/Glyphs/entrenador.gif");
        
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(selected){
            setBackground(tree.getBackground());
        }else{
            setForeground(tree.getForeground());
        }
        
        if(leaf){
            setIcon(iconoHoja);
        }else if(expanded){
            setIcon(iconoAbierto);
        }else{
            setIcon(iconoCerrado);
        }
        setOpaque(true);
        setFont(tree.getFont());
        setText(value.toString());
        return (JLabel) this;
    }
    
}
