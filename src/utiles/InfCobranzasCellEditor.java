/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Administrador
 */
public class InfCobranzasCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener{

    Boolean currentValue;
    JButton button;
    protected static final String EDIT = "edit";
    private JTable jTable;
    
    public InfCobranzasCellEditor(JTable jTable){
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(true);
        this.jTable = jTable;
    }
    
    // implement the one CellEditor method that AbstractCellEditor doesn't
    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }

    // Implement the one method defined by TableCellEditor 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Va a mostrar el boton solo en la ultima fila, de otro forma solo muestra un espacio en blanco 
        if(row == table.getModel().getRowCount() - 1){
            currentValue = (Boolean) value;
            return button;
        }else{
            return new JLabel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // mymodel t = (mymodel) jtable.getmodel();
        // t.addNewRecord();
        fireEditingStopped();
    }
    
}
