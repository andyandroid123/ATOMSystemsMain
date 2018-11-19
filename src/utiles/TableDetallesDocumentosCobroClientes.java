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
public class TableDetallesDocumentosCobroClientes extends AbstractTableModel{
    
    int boton = 0;
    private int colnum = 11;
    private String[] colNames = { "Nro. Comp.",
                                  "Tipo",
                                  "Cuota",
                                  "Cant",
                                  "Fec. Emisión",
                                  "Fec. Venc.",
                                  "Días venc.",
                                  "Valor cuota", 
                                  "Vlr interés",
                                  "Cuota + interés",
                                  "Seleccionar",
    };
   Object [][] data = null;
   
   public TableDetallesDocumentosCobroClientes(ResultSet rs) {
        
        if(rs != null){        
            try{
                rs.last();
                data = new Object[rs.getRow()][colnum];
                rs.first();
                for (int i = 0; i < data.length; i++) {
                    data[i][0]= rs.getString("nro_comprob");
                    data[i][1]= rs.getString("tip_comprob");
                    data[i][2]= rs.getString("nro_cuota");
                    data[i][3]= rs.getString("can_cuota");
                    data[i][4]= rs.getString("fec_comprob");
                    data[i][5]= rs.getString("fec_vencimiento");
                    data[i][6]= rs.getString("dias_vencido");
                    data[i][7]= rs.getDouble("monto_cuota");
                    data[i][8]= rs.getString("valor_interes");
                    data[i][9]= rs.getDouble("cuota_mas_interes");
                    data[i][10]= new Boolean(false);
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
            data[0][0]= "";
            data[0][1]= "";
            data[0][2]= "";
            data[0][3]= "";
            data[0][4]= "";
            data[0][5]= "";
            data[0][6]= "";
            data[0][7]= new Double(0);;
            data[0][8]= "";
            data[0][9]= new Double(0);
            data[0][10]= new Boolean(false);
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
        if(columnIndex > 11){
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
