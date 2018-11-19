/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Andres
 */
public class CellRendererBusca extends DefaultTableCellRenderer{
    
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
    
    
    public CellRendererBusca()
    {
        super();
    }
    
    public void setValue(Object value)
    {
        if((value != null) && (value instanceof Integer) || (value instanceof Long))
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
            Number numberValue = (Number) value;
            
            //NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
            DecimalFormat df = (DecimalFormat)nf;
            df.applyPattern("###,###,###.00");
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            value = df.format(numberValue.longValue()); // doubleValue()
            //value = df.format(numberValue.longValue()); // doubleValue()
        }else
        {
            if(value instanceof Float) //value instanceof Double
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
                Number numberValue = (Number) value;
                
                //NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
                //NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
                DecimalFormat df = (DecimalFormat)nf;
                df.applyPattern("###,###,###.00");
                nf.setMaximumFractionDigits(2); // 2
                nf.setMinimumFractionDigits(2); // 2
                //value = df.format(numberValue.doubleValue());
                value = df.format(numberValue.floatValue());
            }else
            {
                if(value instanceof String)
                {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
        }
        super.setValue(value);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if((row % 2) == 0)
        
            super.setBackground(Color.WHITE);
        else
        
            super.setBackground(new Color(255, 251, 234));
            
            JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(new java.awt.Font("Tahoma", 0, 13));
        
        return label;
    }
    
}
