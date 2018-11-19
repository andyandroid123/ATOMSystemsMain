/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TableUtiles;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Administrador
 */
public class DateCellRender extends JLabel implements TableCellRenderer {
    /**  
     * classe para mostrar a celula com formato de data  
     */   
    private static final long serialVersionUID = 1L;   
    private JFormattedTextField cell = null;   
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    Color amarillo = new Color(255, 255, 185);
    
    private JFormattedTextField getCell() {
        MaskFormatter mascara;
        try {
            mascara = new MaskFormatter("##/##/####");
            if (cell == null){
                cell = new JFormattedTextField(mascara);
            }            
        } catch (ParseException ex) {
            ex.printStackTrace();
        }        
        return cell;   
    }   
  
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {   
        if (value != null) {   
            getCell().setText(value.toString());
            getCell().setHorizontalAlignment(SwingConstants.CENTER);   
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
