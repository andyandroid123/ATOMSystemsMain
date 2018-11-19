/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import ModFinanciero.informes.DetalleDocVenta;
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
public class ButtonDetalleDocCuentaCliente extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{

    JTable table;
    JButton renderButton;
    JButton detallesButton;
    String text;
    
    // ** DATOS REPORT **
    String actividadEmpresa, direccionEmpresa, ciudadEmpresa, telEmpresa;
    
    public ButtonDetalleDocCuentaCliente(JTable table, int column){
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
        String nroDocumento = table.getValueAt(table.getSelectedRow(), 4).toString().trim();
        String nroCaja = table.getValueAt(table.getSelectedRow(), 1).toString().trim();
        String fecDocumento = table.getValueAt(table.getSelectedRow(), 6).toString().trim();
        abrirFormDetalle(nroDocumento, nroCaja, fecDocumento);
    }
    
    private void abrirFormDetalle(String nroDocumento, String codCaja, String fecDocumento){
        DetalleDocVenta detalle_venta = new DetalleDocVenta(new JFrame(), true, nroDocumento, codCaja, fecDocumento, "");
        detalle_venta.pack();
        detalle_venta.setVisible(true);
    }
}
