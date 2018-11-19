/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.sql.ResultSet;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andres
 */
public class TableReImpresionDocsVentas extends AbstractTableModel{

    int boton = 0;
    private int colnum = 8;
    private String[] colNames = { "Caja",           // int
                                  "No. Comprob.",   // int 
                                  "Cliente",        // String 
                                  "Fec. comprob.",  // string
                                  "Monto",          // double
                                  "Re-imprimir",    // button
                                  "No. Turno",      // int
                                  "Cajero"          // string
    };
   Object [][] data = null;
    
   public TableReImpresionDocsVentas (ResultSet rs){
       if(rs != null){
           try{
               rs.last();
               data = new Object[rs.getRow()][colnum];
               rs.first();
               for (int i = 0; i < data.length; i++) {
                    data[i][0]= rs.getInt("cod_caja");
                    data[i][1]= rs.getInt("nro_comprob");
                    data[i][2]= rs.getString("cliente");
                    data[i][3]= rs.getString("fecha");
                    data[i][4]= rs.getDouble("mon_total");
                    data[i][6]= rs.getInt("nro_turno");
                    data[i][7]= rs.getString("cajero");
                    rs.next();
                }
           }catch(Exception ex){
               ex.printStackTrace();
               System.err.print(ex.getMessage());
               System.out.print("Falla Tabla - TableReImpresionDocsVentas");
           }
       }else{
           boton++;
           data = new Object[1][colnum];
           data[0][0]= new Integer(0);
           data[0][1]= new Integer(0);
           data[0][2]= "";
           data[0][3]= "";
           data[0][4]= new Double(0);
           data[0][6]= new Integer(0);
           data[0][7]= "";
       }
   }
   
    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return colnum;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
    
    public String getColumnName(int param){
       return colNames[param];
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 5){
            return true;
        }else{
            return false;
        }
    }
    
//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//    }
    
    public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
    }
}
