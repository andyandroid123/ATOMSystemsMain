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
public class TableModificacionVencDocsClientes extends AbstractTableModel{

    int boton = 0;
    private int colnum = 8;
    private String[] colNames = { "No. doc",       // int
                                  "No. cuota",     // int 
                                  "Cant. cuota",   // int 
                                  "Tipo comprob",  // string 
                                  "Fec. emisión",  // string
                                  "Fec. venc.",    // string
                                  "Monto",         // double
                                  "Modificar"      // boolean
    };
   Object [][] data = null;
    
   public TableModificacionVencDocsClientes (ResultSet rs){
       if(rs != null){
           try{
               rs.last();
               data = new Object[rs.getRow()][colnum];
               rs.first();
               for (int i = 0; i < data.length; i++) {
                    data[i][0]= rs.getInt("nro_comprob");
                    data[i][1]= rs.getInt("nro_cuota");
                    data[i][2]= rs.getInt("can_cuota");
                    data[i][3]= rs.getString("tip_comprob");
                    data[i][4]= rs.getString("fec_emision");
                    data[i][5]= rs.getString("fec_vencimiento");
                    data[i][6]= rs.getDouble("monto_cuota");
                    data[i][7]= Boolean.FALSE;
                    rs.next();
                }
           }catch(Exception ex){
               ex.printStackTrace();
               System.err.print(ex.getMessage());
               System.out.print("Falla Tabla - TableCobranzasClientes");
           }
       }else{
           boton++;
           data = new Object[1][colnum];
           data[0][0]= new Integer(0);
           data[0][1]= new Integer(0);
           data[0][2]= new Integer(0);
           data[0][3]= "";
           data[0][4]= "";
           data[0][5]= "";
           data[0][6]= new Double(0);
           data[0][7]= new Boolean(false);
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
        if(columnIndex == 5 || columnIndex == 7){
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
    
    /* Este metodo es utilizado por el JTABLE para definir el default 
     * renderer o editor para cada celda. Por ejemplo si se tiene un valor booleando 
     * este va a aparecer como un check box 
     * los valores numericos son alineados hacia la derecha
     */
    
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return data[0][columnIndex].getClass();
    }
}
