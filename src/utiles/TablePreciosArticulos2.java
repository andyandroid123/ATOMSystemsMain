/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.sql.ResultSet;
import java.text.NumberFormat;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andres
 */
public class TablePreciosArticulos2 extends AbstractTableModel{
    NumberFormat nf = NumberFormat.getIntegerInstance();
    //Conversor conv = new Conversor();
    int boton = 0;
    private int monto_total=0;
    private int neto = 0;
    private int retencion=0;
    private int colnum=9;
    private int rownum;
    private long oldtipcom = 9;
    private String[] colNames={ "Lista", 
                                "Lista Precio", 
                                "Sigla", 
                                "U/M", 
                                "Margen", 
                                "Precio", 
                                "Fec Vig", 
                                "Usuario", 
                                "Inactivar"
                                };
    Object [][] data = null;

    public TablePreciosArticulos2(ResultSet rs) {
    if(rs != null){        
        try{
            rs.last();
            data = new Object[rs.getRow()][colnum];
            rs.first();
           for (int i = 0; i < data.length; i++) {
                data[i][0]= new Integer(rs.getString("cod_lista"));
                data[i][1]= rs.getString("descripcion");
                data[i][2]= rs.getString("sigla_venta");
                data[i][3]= rs.getString("cansi_venta");
                data[i][4]= rs.getDouble("margen_pct");
                data[i][5]= rs.getDouble("precio_venta");
                data[i][6]= rs.getString("fecVigencia");                
                data[i][7]= rs.getString("nombre");                                                                
                data[i][8]= new Boolean(false);
                
              rs.next();
          }        
        }catch(Exception e){
            e.printStackTrace();
            System.err.print(e.getMessage());
            System.out.print("Falla Tabla");;
        }
    }else{
        boton++;
        data = new Object[1][colnum];
        data[0][0]= new Integer(0);
        data[0][1]= "";
        data[0][2]= "";
        data[0][3]= "";
        data[0][4]= new Integer(0);
        data[0][5]= "";
        data[0][6]= new Long(0);
        data[0][7]= "" ;       
        data[0][8]= new Boolean(false);
        
                
    }    
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return colnum;
    }

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
