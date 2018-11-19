/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import ModFinanciero.informes.DetalleCuentaCliente;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Andres
 */

public class ButtonDetalleCuentaCliente extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{

    JTable table;
    JButton renderButton;
    JButton detallesButton;
    String text;
    
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    
    public ButtonDetalleCuentaCliente(JTable table, int column){
        super();
        this.table = table;
        renderButton = new JButton();
        detallesButton = new JButton();
        detallesButton.setFocusPainted(false);
        detallesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar16.png")));
        renderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buscar16.png")));
        detallesButton.addActionListener(this);
        renderButton.addActionListener(this);
        TableColumnModel columnModel = table.getColumnModel();  
        columnModel.getColumn(column).setCellRenderer( this );  
        columnModel.getColumn(column).setCellEditor( this );
    }
    
    @Override
    public Object getCellEditorValue() {
        return text;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (hasFocus)  
        {  
            renderButton.setForeground(table.getForeground());  
            renderButton.setBackground(UIManager.getColor("Button.background"));  
        }  
        else if (isSelected)  
        {  
            renderButton.setForeground(table.getSelectionForeground());  
            renderButton.setBackground(table.getSelectionBackground());  
        }  
        else  
        {  
            renderButton.setForeground(table.getForeground());  
            renderButton.setBackground(UIManager.getColor("Button.background"));  
        }  

        //renderButton.setText( (value == null) ? "" : value.toString() );
        renderButton.setText("");
        //renderButton.setIcon(new ImageIcon("File.gif"));
        return renderButton; 
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        text = "";
        detallesButton.setText( text );  
        return detallesButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int codigoCliente = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString().trim());
        double pct_interes = Double.parseDouble(table.getValueAt(table.getSelectedRow(), 10).toString().trim());
        String cli [] = table.getValueAt(table.getSelectedRow(), 1).toString().split("-");
        String cliente = codigoCliente + " - " + cli[0];
        abrirDetalleCuenta(cliente, codigoCliente, pct_interes);
    }
    
    private void abrirDetalleCuenta(String cliente, int codigoCliente, double interes){
        DetalleCuentaCliente detalle = new DetalleCuentaCliente(new JFrame(), true, cliente, codigoCliente, interes);
        detalle.pack();
        detalle.setVisible(true);
    }
}
