package utiles;

/**
 * @author Alessandro  Ferreira Leite
 * @version 1.0, $Date, 27/06/2004
 * @link E-mail: alessandro.l@pop.com.br
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * @author Alessandro  Ferreira Leite
 * @version 1.0, $Date, 27/06/2004
 * @link E-mail: alessandro.l@pop.com.br
 */
public class TableGrid extends JTable {
    //private DefaultTableModel tableModel;
    private TGridTableModel tableModel;  // Modelo p/tgrid ser de solo lectura
    //private DefatultTableColumnModel
    private Vector vFmtColumnas = new Vector(); //= null;
    private Vector vColDate = new Vector(); //null;
    
    public TableGrid(Vector formatoColumnas) {
        super();
        vFmtColumnas = formatoColumnas;
        this.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //this.setForeground(Color.blue);
        //this.setBackground(new Color(231, 255, 181));
        JTableHeader jth = this.getTableHeader();
        jth.setReorderingAllowed(false);
    }
    
    public TableGrid() {
        super();
        this.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        //this.setAutoResizeMode(AUTO_RESIZE_OFF);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setRowHeight(25);
        
        //this.setForeground(Color.blue);
        //this.setBackground(new Color(231, 255, 181));
        JTableHeader jth = this.getTableHeader();
        jth.setReorderingAllowed(false);
    }
    
    //private void setTableModel(DefaultTableModel _tableModel) {
    private void setTableModel(TGridTableModel _tableModel) {
        this.tableModel = _tableModel;
    }
    
    private DefaultTableModel getTableModel() {
        return this.tableModel;
    }
    
    public void setDataSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int numColunas = metaData.getColumnCount();
                Vector colunas = new Vector();
                boolean adiciona = true;
                for (int column = 0; column < numColunas; column++) {
                    colunas.addElement(metaData.getColumnLabel(column + 1));
                }
                Vector linhas = new Vector();
                //resultSet.beforeFirst(); //pau no firebird, nao soporta
                while (resultSet.next()) {
                    Vector novaLinha = new Vector();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        novaLinha.addElement(resultSet.getObject(i));
                    }
                    linhas.addElement(novaLinha);
                }
                //this.setTableModel(new DefaultTableModel(linhas, colunas));
                this.setTableModel(new TGridTableModel(linhas, colunas));
                setModel(this.getTableModel());
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
            InfoErrores.errores(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }
    
    public void setNombreColumnas(Vector colunmName) {
        try {
            this.setTableModel(new TGridTableModel(null, colunmName));
            setModel(this.getTableModel());
            //AcomodaJTable.autoResizeJTable(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }
    
    /**
     * @param resultSet  - representa o Objeto(java.sql.ResultSet) com os dados retornados da base
     * @param colunmName - representa o Objeto(java.util.Vector) com o nomes das colunas da tabela
     */
    public void setDataSet(ResultSet resultSet, Vector colunmName) {
        try {
            if (resultSet != null && colunmName != null) {
                Vector linhas = new Vector();
                //resultSet.beforeFirst();
                while (resultSet.next()) {
                    Vector novaLinha = new Vector();
                    for (int cont = 1;
                    cont <= resultSet.getMetaData().getColumnCount();
                    cont++) {
                        novaLinha.addElement(resultSet.getObject(cont));
                    }
                    linhas.addElement(novaLinha);
                }
                this.setTableModel(new TGridTableModel(linhas, colunmName));
                setModel(this.getTableModel());
                //AcomodaJTable.autoResizeJTable(this);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            InfoErrores.errores(sqlEx);
        }
    }
    
    public void setDataSet(Vector dados, Vector linhas) {
        this.setTableModel(new TGridTableModel(linhas, dados));
        setModel(this.getTableModel());
    }
    
    /**
     *
     * M�todo respons�vel por trazer todos os dados relativo a linha selecionada
     * @author - Alessandro Ferreira Leite
     * @param linha
     * @param coluna
     * @return
     */
    public Object retornaDado(int linha) {
        if (this.tableModel != null && linha >= 0) {
            return ((Vector)this.tableModel.getDataVector().get(linha)).toArray();
        } else {
            return null;
        }
    }
    
    public Object retornaDado(int linha, int coluna) {
        return (this.getModel().getValueAt(linha, coluna));
    }
    
    // Inserta nueva Fila
    public void insertaLinea(Vector nLinea) {
        //tableModel.insertRow(tableModel.getRowCount(), nLinea); // Al final
        tableModel.insertRow(0, nLinea);                          // Al Inicio
    }
    
    public void borraLinea(int fila) {
        tableModel.removeRow( fila );
    }
    
    public void borraAllLineas() {
        for (int i=0; i<this.getRowCount(); i++) {
            tableModel.removeRow(i);
        }
    }
    
    public void setDataSet(Object[][] dados, Object[] colunasNames) {
        setModel(new DefaultTableModel(dados, colunasNames));
    }
    
    public Class getColumnClass(int column) {
        Class dataType = super.getColumnClass(column);
        Class dtype = tipoClass(column);
        if (dtype == null) {
            return dataType;
        } else {
            return dtype;
        }
    }
    
    private Class tipoClass(int column) {
        Class dt = null;  String ncol = ""; String tipoFmt = "";
        if (vFmtColumnas != null) {
            for(int i=0; i < vFmtColumnas.size(); i++){
                String datoV = (String)vFmtColumnas.elementAt(i);
                if ( datoV.length() == 3 ) { // Verifica si parametro esta Ok ej: "03C"
                    ncol    = datoV.substring(0,2).trim();  // toma el nro. de columna
                    tipoFmt = datoV.substring(2,3).trim();  // toma el formato
                    if (column == Integer.parseInt(ncol) ) { //(Integer)vFmtColumnas.elementAt(i) {
                        if (tipoFmt.equals("C")) {  // Currency
                            dt = Float.class;
                        } else  {
                            if (tipoFmt.equals("L")) { // Long
                                dt = Long.class;
                            } else {
                                if (tipoFmt.equals("I")) { // Integer
                                    dt = Integer.class;
                                } else {
                                    if (tipoFmt.equals("F")) { // Fecha
                                        dt = java.util.Date.class;
                                        //dt = java.sql.Timestamp.class;
                                        //dt = oracle.sql.TIMESTAMP.class;
                                    } else {
                                        if (tipoFmt.equals("S")) { // String
                                            dt = String.class;
                                        } else {
                                            JOptionPane.showMessageDialog(this,
                                                    "Codigo de Operacion Invalido: "+tipoFmt,
                                                    "Error...",
                                                    JOptionPane.WARNING_MESSAGE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return dt;
    }
    
}
