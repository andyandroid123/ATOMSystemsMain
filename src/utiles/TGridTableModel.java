package utiles;

/* Modelo para tornar jTable read-only */

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class TGridTableModel extends DefaultTableModel { 
    
    public TGridTableModel() {
        super();
    }
    
    public TGridTableModel(Vector v1, Vector v2) {
        super(v1, v2);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
}
