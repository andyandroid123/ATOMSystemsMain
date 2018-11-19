/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Administrador
 */
public class InfCobranzasCellRender extends JLabel implements TableCellRenderer {

    boolean isBordered = true;
    
    public InfCobranzasCellRender(boolean isBordered){
        this.isBordered = isBordered;
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
        // Va a mostrar el boton solo en la ultima fila 
        // de otra forma muestra un espacio en blanco
        if(row == table.getModel().getRowCount() - 1){
            return new JButton("Detalles");
        }else{
            setBackground(new Color(0xffffff));
            return this;
        }
    }
    
}
