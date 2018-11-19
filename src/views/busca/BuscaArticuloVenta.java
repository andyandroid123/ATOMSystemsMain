/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.busca;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import utiles.CellRendererBusca;
import utiles.DBManager;
import utiles.MaxLength;
import utiles.TGridTableModel;

/**
 *
 * @author Andres
 */
public class BuscaArticuloVenta extends javax.swing.JDialog {

    static String tabla, sql;
    static String col1, col2, col3, col4, col5;    
    //static Double col5;
    static String campoSele;
    static Vector titColumnas = new Vector();
    static ResultSet resSet = null;
    //  public String input = "";
    private static JTextField jTFieldCodigo = new javax.swing.JTextField();
    private static JTextField jTFieldSiglaVenta = new javax.swing.JTextField();
    
    public BuscaArticuloVenta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        inicio();
    }

    private void inicio(){
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = getSize();
        if (dialogSize.height > screenSize.height) {
            dialogSize.height = screenSize.height;
        }
        if (dialogSize.width > screenSize.width) {
            dialogSize.width = screenSize.width;
        }
        setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        
        jTDetalles.setRowHeight(21);
        
        jTFDescripcion.setDocument(new MaxLength(40, "UPPER", "ALFA"));
        jTDetalles.setDefaultRenderer(Object.class, new CellRendererBusca());
        jTDetalles.setRowHeight(21);
    }
    
    public static void setText(JTextField jTF, JTextField jTFSigla){
        jTFieldCodigo = jTF;
        jTFieldSiglaVenta = jTFSigla;
    }
    
    private static void setDataSet(ResultSet resultSet, Vector colunmName) {
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
                jTDetalles.setModel(new TGridTableModel(linhas, colunmName));
                jTDetalles.getColumnModel().getColumn(0).setPreferredWidth(010);
                jTDetalles.getColumnModel().getColumn(1).setPreferredWidth(300);
                jTDetalles.getColumnModel().getColumn(2).setPreferredWidth(030);
                jTDetalles.getColumnModel().getColumn(3).setPreferredWidth(010);
                jTDetalles.getColumnModel().getColumn(4).setPreferredWidth(020);
                jTDetalles.setVisible(true);
                jTDetalles.repaint();
                // setModel(this.getTableModel());
                //AcomodaJTable.autoResizeJTable(this);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    
    public static void dConsultas(String tablaSel, String campoSel,
            String colSel1, String colSel2, String colSel3, String colSel4, String colSel5,
            String titCol1, String titCol2, String titCol3, String titCol4, String titCol5) {
        
        tabla = tablaSel;       campoSele = campoSel;
        col1  = colSel1;        col2  = colSel2;
        col3  = colSel3;        col4  = colSel4;
        col5 = colSel5;
        titColumnas.clear();
        if (titCol1 != null) {
            titColumnas.addElement(titCol1); }
        if (titCol2 != null) {
            titColumnas.addElement(titCol2); }
        if (titCol3 != null) {
            titColumnas.addElement(titCol3); }
        if (titCol4 != null) {
            titColumnas.addElement(titCol4); }
        if (titCol5 != null) {
            titColumnas.addElement(titCol5); }
        cargaGrid();
    }
    
    private static void cargaGrid() {
        sql = " (cod_proveedor = " + 0 + " OR " + 0 + " = 0)" +
              " AND (cod_marca = " + 0 + " OR "+0 + " = 0 )" +
              " AND (cod_grupo = " + 0 + " OR "+0+" = 0 )"+
              " AND (cod_subgrupo = " + 0 + " OR " + 0+ " = 0)";
        resSet = DBManager.ejecutarBuscaLikeArticuloVentas(col1, col2, col3, col4, col5, campoSele, tabla, "%ABC%", sql);
        setDataSet(resSet, titColumnas);
        jTDetalles.getColumnModel().getColumn(0).setPreferredWidth(010);
        jTDetalles.getColumnModel().getColumn(1).setPreferredWidth(300);
        jTDetalles.getColumnModel().getColumn(2).setPreferredWidth(030);
        jTDetalles.getColumnModel().getColumn(3).setPreferredWidth(010);
        jTDetalles.getColumnModel().getColumn(4).setPreferredWidth(020);
        jTDetalles.setVisible(true);
        jTDetalles.repaint();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTFDescripcion = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTDetalles = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Búsqueda de Artículos");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Descripción:");

        jTFDescripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTFDescripcion.setText("%");
        jTFDescripcion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFDescripcionFocusGained(evt);
            }
        });
        jTFDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFDescripcionKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFDescripcionKeyTyped(evt);
            }
        });

        jTDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Empaque", "Precio", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetalles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTDetallesMouseClicked(evt);
            }
        });
        jTDetalles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDetallesKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTDetalles);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFDescripcion)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTFDescripcionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFDescripcionFocusGained
        jTFDescripcion.selectAll();
                
    }//GEN-LAST:event_jTFDescripcionFocusGained

    private void jTFDescripcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescripcionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(!jTFDescripcion.getText().trim().equals("")){
                String text = "%" + jTFDescripcion.getText() + "%";
                sql = " (cod_proveedor = " + 0 + " OR " + 0 + " = 0)" +
                      " AND (cod_marca = " + 0 + " OR "+0 + " = 0 )" +
                      " AND (cod_grupo = " + 0 + " OR "+0+" = 0 )"+
                      " AND (cod_subgrupo = " + 0 + " OR " + 0+ " = 0)";
                resSet = DBManager.ejecutarBuscaLikeArticuloVentas(col1, col2, col3, col4, col5, campoSele, tabla, text, sql);
                System.out.println("DATOS DE LA BUSQUEDA: \nCOL1: " + col1 + "\nCOL2: " + col2 + "\nCOL3: " + col3+ "\nCOL4: " + col4 +
                                   "\nCOLD5:" + col5 + "\nCAMPOSELE: " + campoSele + "\nTEXT: " + text + "\nSQL: \n" + sql);
                setDataSet(resSet, titColumnas);
                if (jTDetalles.getRowCount() > 0) {
                    jTDetalles.setRowSelectionInterval(0, 0);
                    jTDetalles.grabFocus();
                }
            }else{
                JOptionPane.showMessageDialog(this, "Ingrese descripción!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            }
        }              
    }//GEN-LAST:event_jTFDescripcionKeyPressed

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jTFDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescripcionKeyTyped
    }//GEN-LAST:event_jTFDescripcionKeyTyped

    private void jTDetallesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDetallesKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            jTFieldCodigo.setText("");
            dispose();
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jTFieldCodigo.setText(String.valueOf(jTDetalles.getValueAt(jTDetalles.getSelectedRow(), 0).toString()));
            String[] sigla = jTDetalles.getValueAt(jTDetalles.getSelectedRow(), 3).toString().split("/");
            jTFieldSiglaVenta.setText(sigla[0]);
            dispose();
        }
    }//GEN-LAST:event_jTDetallesKeyPressed

    private void jTDetallesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTDetallesMouseClicked
        int row = jTDetalles.getSelectedRow();
        int column = jTDetalles.getSelectedColumn();
        
        System.out.println("ROW: " + row + "\nCOLUMN: " + column);
    }//GEN-LAST:event_jTDetallesMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscaArticuloVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscaArticuloVenta dialog = new BuscaArticuloVenta(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTable jTDetalles;
    public static javax.swing.JTextField jTFDescripcion;
    // End of variables declaration//GEN-END:variables
}
