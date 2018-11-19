/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TableUtiles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
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
public class DecimalCellRender extends JLabel implements TableCellRenderer{
    /**  
     * classe para mostrar a celula com formato de data  
     */   
    private static final long serialVersionUID = 1L;   
  
    private JFormattedTextField cell = null;   
     private NumberFormat format;
    
    public DecimalCellRender(int precision){
            format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(precision);
            format.setMinimumFractionDigits(precision);                
    }
    private JFormattedTextField getCell() {

        Font f = new Font("Arial", Font.BOLD, 13);
        
            if (cell == null){
                cell = new JFormattedTextField();
                cell.setBackground(new Color(153,255,153));
                cell.setFont(f);
            }               
        return cell;  
    }   
  
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {   
        if (value != null) {              
            getCell().setText(format.format(Double.valueOf(value.toString().replace(",",""))));
            getCell().setHorizontalAlignment(SwingConstants.RIGHT);   
        } else{                    
            getCell().setText("0");
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
