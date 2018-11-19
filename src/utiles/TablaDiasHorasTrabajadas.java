/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andres
 */


public class TablaDiasHorasTrabajadas extends AbstractTableModel{

    private int colNum = 8;
    private String[] colNames={ "Codigo",
                                "Empleado",
                                "Periodo",
                                "Días Laborales",
                                "Días Trabajados",
                                "Horas Trabajadas",
                                "Horas Extras",
                                "Conf?"};
    private ArrayList<Object[]> ResultSets;
    
    public TablaDiasHorasTrabajadas(ResultSet rs){
        ResultSets = new ArrayList<Object[]> ();
        if(rs != null){
                try{
                    while(rs.next()){
                        Object[] row={
                          rs.getInt("cod_empleado"),
                          rs.getString("empleado"),
                          rs.getString("periodo"),
                          rs.getString("dias_laborales"),
                          rs.getString("dias_trabajados"),
                          rs.getString("horas_trabajadas"),
                          rs.getString("horas_extras"),
                          new Boolean(true),
                          rs.getInt("dias_ingreso")
                        };
                        ResultSets.add(row);
                    }
                }catch(SQLException e){}
                int cantRegistros = ResultSets.size();
        }
    }
    
    @Override
    public int getRowCount() {
        return ResultSets.size();
    }

    @Override
    public int getColumnCount() {
        return colNum;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = ResultSets.get(rowIndex);
        return row[columnIndex] == null ? "" : row[columnIndex];
    }
    
    public String getColumnName(int param){
       return colNames[param];
    }
    
    public void anhadir(){
           
       Object[] row_new={"","",
                        "","",
                        "","",
                        new Integer(0),new Boolean(false),new Integer(0)};
       ResultSets.add(row_new);       
       fireTableRowsInserted(ResultSets.size(),ResultSets.size());
    }
    
    public void setValueAt(Object value, int row, int col) {
        Object[] Ob = ResultSets.get(row);        
        Ob[col]= value;         
        fireTableCellUpdated(row, col);
    }
    
    public void eliminar(int index){
        ResultSets.remove(index);
        fireTableRowsDeleted(ResultSets.size(),ResultSets.size());    
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
         boolean result = false;   

         if((columnIndex == 7 )){
                    result = true;
                }else{
                    result =  false;
                }    

         return result;
    }
    
    public Class getColumnClass(int c) {
        Class obj = String.class;
        if(c == 7){
          obj= getValueAt(0, c).getClass();
        }
        return obj;
    }
    
    public void limpiar(){
        ResultSets.clear();
        fireTableRowsDeleted(ResultSets.size(),ResultSets.size()); 
    }
}
