/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TableUtiles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Administrador
 */
public class StringCellRenderModVencimiento extends JLabel implements TableCellRenderer {
    /**  
     * classe para mostrar a celula com formato de data  
     */   
    private static final long serialVersionUID = 1L;   
  
    private JFormattedTextField cell = null;   
    
    private JFormattedTextField getCell() {


            if (cell == null){
                cell = new JFormattedTextField();
            }               
        return cell;  
    }   
  
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {   
        Font f = new Font("Arial", Font.BOLD, 13);
        if (value != null) {              
            getCell().setText(value.toString());
            getCell().setHorizontalAlignment(SwingConstants.RIGHT); 
            cell.setBackground(new Color(153,255,153));
            cell.setFont(f);
        } else{            
            getCell().setText("");
        }
         getCell().setBorder(null);
         getCell().setOpaque(false);
        if(hasFocus){
            getCell().setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        }else{
            getCell().setBorder(null);
        }              
        return getCell();   
    }
}
