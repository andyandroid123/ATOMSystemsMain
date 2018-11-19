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
public class TableCobranzasClientes extends AbstractTableModel{

    int boton = 0;
    private int colnum = 12;
    private String[] colNames = { "Caja",       // int
                                  "Turno",      // int 
                                  "Nro.Pago",   // int 
                                  "Nro.Recibo", // string
                                  "Fec.Cobro",  // string
                                  "Cliente",    // string
                                  "Monto Pago", // double 
                                  "Interés",    // double
                                  "Estado",     // string 
                                  "Cobrador",   // string 
                                  "Obs",        // string
                                  "Detalles"    // button
    };
   Object [][] data = null;
    
   public TableCobranzasClientes (ResultSet rs){
       if(rs != null){
           try{
               rs.last();
               data = new Object[rs.getRow()][colnum];
               rs.first();
               for (int i = 0; i < data.length; i++) {
                    data[i][0]= rs.getInt("cod_caja");
                    data[i][1]= rs.getInt("nro_turno");
                    data[i][2]= rs.getInt("nro_pago");
                    data[i][3]= rs.getString("nro_recibo");
                    data[i][4]= rs.getString("fec_pago");
                    data[i][5]= rs.getString("razon_soc");
                    data[i][6]= rs.getDouble("monto_cobro");
                    data[i][7]= rs.getDouble("interes");
                    data[i][8]= rs.getString("estado");
                    data[i][9]= rs.getString("cobrador");
                    data[i][10]= rs.getString("observacion");
                    //data[i][11]= rs.getString("observacion");
                    
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
           data[0][7]= new Double(0);
           data[0][8]= "";
           data[0][9]= "";
           data[0][10]= "";
           //data[0][11]= "";
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
        if(columnIndex < 11){
          return false;
        }else{
            return true;
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
