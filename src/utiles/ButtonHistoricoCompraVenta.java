/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import ModRegistros.informes.HistoricoComprasVentasArticulo;
import ModRegistros.informes.HistoricoDetalleCompras;
import ModRegistros.informes.HistoricoDetalleVentas;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author ANDRES
 */
public class ButtonHistoricoCompraVenta extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{

    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private String text;
    private int compraVenta;
    
    public ButtonHistoricoCompraVenta(JTable table, int column, int compraVenta){
        this.table = table;
        this.compraVenta = compraVenta;
        this.renderButton = new JButton();
        this.editButton = new JButton();
        this.editButton.setFocusPainted(false);
        this.editButton.addActionListener(this);
        try{
            this.editButton.setIcon(new ImageIcon(getClass().getResource("/resources/buscar16.png")));
            this.renderButton.setIcon(new ImageIcon(getClass().getResource("/resources/buscar16.png")));
        }catch(Exception ex){
            ex.printStackTrace();
        }
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }
    
    @Override
    public Object getCellEditorValue() {
        return this.text;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(hasFocus){
            this.renderButton.setForeground(table.getForeground());
            this.renderButton.setBackground(UIManager.getColor("Button.background"));
        }
        else if(isSelected){
            this.renderButton.setForeground(table.getSelectionForeground());
            this.renderButton.setBackground(table.getSelectionBackground());
        }
        else{
            this.renderButton.setForeground(table.getForeground());
            this.renderButton.setBackground(UIManager.getColor("Button.background"));
        }
        this.renderButton.setText("...");
        return this.renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.text = "...";
        this.editButton.setText(this.text);
        return this.editButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            int codEmpresa = Integer.parseInt(utiles.Utiles.getCodEmpresaDefault());
            int codLocal = Integer.valueOf(this.table.getValueAt(this.table.getSelectedRow(), 0).toString()).intValue();
            String periodo = this.table.getValueAt(this.table.getSelectedRow(), 1).toString();
            if(this.compraVenta == 1){
                HistoricoDetalleCompras compras = new HistoricoDetalleCompras(new JFrame(), true, codEmpresa, codLocal, periodo,  
                                                                              Integer.valueOf(HistoricoComprasVentasArticulo.jTFCodArticulo.getText()).intValue(), 
                                                                              HistoricoComprasVentasArticulo.jTFDescripcionArticulo.getText());
                compras.pack();
                compras.setVisible(true);
            }else{
                HistoricoDetalleVentas ventas = new HistoricoDetalleVentas(new JFrame(), true, codEmpresa, codLocal, periodo, 
                                                                           Integer.valueOf(HistoricoComprasVentasArticulo.jTFCodArticulo.getText()).intValue(), 
                                                                           HistoricoComprasVentasArticulo.jTFDescripcionArticulo.getText());
                ventas.pack();
                ventas.setVisible(true);
            }
            
        }catch(HeadlessException ex){ex.printStackTrace();}
    }
    
}
