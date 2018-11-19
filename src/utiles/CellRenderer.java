/*
 * CellRenderer.java
 *
 * Created on 18 de mayo de 2007, 05:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package utiles;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
 
public class CellRenderer extends DefaultTableCellRenderer {

    
    public CellRenderer() {
        super();
    }

    public void setValue(Object value) {
        if ((value != null) && (((value instanceof Integer) || (value instanceof Long) || (value instanceof Double)))) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            Number numberValue = (Number) value;
            //Locale loc = new Locale();
            //System.out.println( Locale.getDefault() );
            NumberFormat nf = NumberFormat.getNumberInstance( Locale.getDefault() ); //getIntegerInstance(); // getCurrencyInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            value = nf.format(numberValue.floatValue()); // doubleValue());
        } else {
            if (value instanceof Number) { //(value instanceof Double) {
                setHorizontalAlignment(SwingConstants.CENTER);
                Number numberValue = (Number) value;
                //Locale loc = new Locale();
                //System.out.println( Locale.getDefault() );
                NumberFormat nf = NumberFormat.getNumberInstance( Locale.getDefault() ); //getIntegerInstance(); // getCurrencyInstance();
                nf.setMaximumFractionDigits(2); //2
                nf.setMinimumFractionDigits(2); //2
                value = nf.format(numberValue.doubleValue());
            } else {
                if (value instanceof String) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
        }
        super.setValue(value);
        
        /*if ((value != null) && (value instanceof Number)) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            Number numberValue = (Number) value;
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault()); //getIntegerInstance(); // getCurrencyInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2); // por defecto 2, con valores int igual le pone como double
            value = nf.format(numberValue.doubleValue());
        } else {
            if (value instanceof String) {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
        }
        super.setValue(value);*/
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if ((row % 2) == 0)
            super.setBackground(Color.WHITE);
        //super.setBackground(Color.yellow);
        else
            super.setBackground(new Color(225, 251, 234));
        //super.setBackground(Color.LIGHT_GRAY);
        
        // -- para pintar la columna
        /*if(column > 3)
            super.setBackground(Color.YELLOW);*/
        
        //if (isSelected)
        //    super.setBackground (Color.RED);
        //else
        //    super.setBackground (Color.YELLOW); 

        JLabel label = (JLabel)super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        
        label.setFont(new java.awt.Font("Tahoma", 0, 11));
        
        //return super.getTableCellRendererComponent(
        //               table, value, isSelected, hasFocus, row, column);
        return label;
        /*if ((row % 2) == 0) {
            super.setBackground(Color.WHITE);
        }
        else {
            super.setBackground(new Color(225, 251, 234));
        }

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        label.setFont(new java.awt.Font("Tahoma", 0, 14));

        return label;*/
    }
}
