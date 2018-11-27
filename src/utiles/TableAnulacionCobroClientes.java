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
public class TableAnulacionCobroClientes extends AbstractTableModel{
    
    int boton = 0;
    private int colnum = 9;
    private String[] colNames={ "Nro. Pago",       // int 
                                "Nro. Recibo",     // int 
                                "Fec. Pago",       // string
                                "Cód. Cliente",    // int
                                "Nombre Cliente",  // string
                                "Monto pago",      // double
                                "Fec. Operación",  // string
                                "Estado",          // string
                                "Anular?"          // boolean
    };
   Object [][] data = null;
   
   public TableAnulacionCobroClientes(ResultSet rs) {
        
        if(rs != null){        
            try{
                rs.last();
                data = new Object[rs.getRow()][colnum];
                rs.first();
                for (int i = 0; i < data.length; i++) {
                    data[i][0]= rs.getInt("nro_pago");
                    data[i][1]= rs.getInt("nro_recibo");
                    data[i][2]= rs.getString("fec_pago");
                    data[i][3]= rs.getInt("cod_cliente");
                    data[i][4]= rs.getString("razon_soc");
                    data[i][5]= rs.getDouble("monto_pago");
                    data[i][6]= rs.getString("fec_vigencia");
                    data[i][7]= rs.getString("estado");
                    data[i][8]= new Boolean(false);
                    rs.next();
                }        
            }catch(Exception e){
                e.printStackTrace();
                System.err.print(e.getMessage());
                System.out.print("Falla Tabla");
            }   
        }else{
            boton++;
            data = new Object[1][colnum];
            data[0][0]= new Integer(0);
            data[0][1]= new Integer(0);;
            data[0][2]= "";
            data[0][3]= new Integer(0);;
            data[0][4]= "";
            data[0][5]= new Double(0);
            data[0][6]= "";
            data[0][7]= "";
            data[0][8]= new Boolean(false);
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
        if(columnIndex > 7){
          return true;
        }else{
            return false;
        }
    }
    public Class getColumnClass(int c) {
       
            return getValueAt(0, c).getClass();
        
    }
    public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
    }  
}
