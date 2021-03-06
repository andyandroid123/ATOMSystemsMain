/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andres
 */
public class TablePreciosArticulos extends AbstractTableModel{
    
    private int cantidadColumnas = 9;
    private String[] nombreColumnas = {"Lista", 
                                       "Lista Precio", 
                                       "Sigla", 
                                       "U/M", 
                                       "Margen", 
                                       "Precio", 
                                       "Fec Vig", 
                                       "Usuario", 
                                       "Inactivar"};
    
    private ArrayList<Object[]> ResultSets;
    
    public TablePreciosArticulos(ResultSet resultSet){
        ResultSets = new ArrayList<Object[]>();
        if(resultSet != null){
            try{
                while(resultSet.next()){
                    Object [] row = {
                        resultSet.getString("cod_lista"),
                        resultSet.getString("descripcion"),
                        resultSet.getString("sigla_venta"),
                        resultSet.getInt("cansi_venta"),
                        resultSet.getDouble("margen_pct"),
                        resultSet.getDouble("precio_venta"),
                        resultSet.getString("fecVigencia"),
                        resultSet.getString("nombre"), 
                        false
                    };
                    ResultSets.add(row);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                InfoErrores.errores(ex);
            }
        }
    }

    @Override
    public int getRowCount() {
        return ResultSets.size();
    }

    @Override
    public int getColumnCount() {
        return cantidadColumnas;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = ResultSets.get(rowIndex);
        return row[columnIndex] == null ? "" : row[columnIndex];
    }
    
    public String getColumnName(int param) {
        return nombreColumnas[param];
    }

    public void anhadir(String fecha) {
        Object[] row_new = {"", "", "", "", "", "", "", "", new Boolean(false)};
        ResultSets.add(row_new);
        fireTableRowsInserted(ResultSets.size(), ResultSets.size());
    }

    public void setValueAt(Object value, int row, int col) {
        Object[] Ob = ResultSets.get(row);
        Ob[col] = value;
        fireTableCellUpdated(row, col);
        /*if (col != 12) {
            Ob[12] = true;
            fireTableCellUpdated(row, 12);
        }*/
    }

    public void eliminar(int index) {
        ResultSets.remove(index);
        fireTableRowsDeleted(ResultSets.size(), ResultSets.size());
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean result = false;
        if (columnIndex == 9) {
            result = true;
        }
        return result;
    }

    public Class getColumnClass(int c) {
        Class obj = String.class;
        if (c == 12) {
            obj = getValueAt(0, c).getClass();
        }
        return obj;
    }

    public void limpiar() {
        ResultSets.clear();
        fireTableRowsDeleted(ResultSets.size(), ResultSets.size());
    }
}
