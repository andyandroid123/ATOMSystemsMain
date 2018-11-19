 
package impresion;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFileChooser;


public class OpcionesdeImpresion extends javax.swing.JDialog {

 
    private JTextField jTCallFrom = new javax.swing.JTextField();
    private int vNrodeCopias = 1;

    public OpcionesdeImpresion(java.awt.Frame parent, boolean modal, String pCallFrom) {
        super(parent, modal);
        initComponents();
      
        jBArchivoTexto.setEnabled(false);
        jBArchivoTexto.setVisible(false);
        jBArchivoTexto.setFocusable(false);
        String vPreselected = "Matricial";
        jTCallFrom.setText(pCallFrom);
        if (pCallFrom.trim().equals("AnticipoSalario")) {
            vPreselected = ModRRHH.AnticipoSalario.jTPrinterSelected.getText();
        }else if(pCallFrom.trim().equals("RegistroDescBeneficios")){
            vPreselected = ModRRHH.RegistroDescBeneficios.jTPrinterSelected.getText();
        }

        if (vPreselected.trim().equals("Matricial")) {
            jRMatricial.setSelected(true);
            jRLaser.setSelected(false);
        } else {
            jRMatricial.setSelected(false);
            jRLaser.setSelected(true);
        }
        jBImprimir.grabFocus();
        jTFileName.setText("");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bttomGroupImpresora = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jRMatricial = new javax.swing.JRadioButton();
        jRLaser = new javax.swing.JRadioButton();
        jBArchivoTexto = new javax.swing.JButton();
        jTFileName = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jBImprimir = new javax.swing.JButton();
        jBCancelar = new javax.swing.JButton();
        jLAyudaTeclasRapidas = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Opciones de Impresión");

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jRMatricial.setBackground(new java.awt.Color(204, 255, 204));
        bttomGroupImpresora.add(jRMatricial);
        jRMatricial.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jRMatricial.setSelected(true);
        jRMatricial.setText("Impresora Matricial");
        jRMatricial.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRMatricial.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRMatricial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRMatricialFocusGained(evt);
            }
        });
        jRMatricial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRMatricialKeyPressed(evt);
            }
        });

        jRLaser.setBackground(new java.awt.Color(204, 255, 204));
        bttomGroupImpresora.add(jRLaser);
        jRLaser.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jRLaser.setText("Impresora Laser");
        jRLaser.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRLaser.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRLaser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRLaserFocusGained(evt);
            }
        });
        jRLaser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRLaserKeyPressed(evt);
            }
        });

        jBArchivoTexto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jBArchivoTexto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/guardar24.png"))); // NOI18N
        jBArchivoTexto.setText("Archivo");
        jBArchivoTexto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jBArchivoTextoFocusGained(evt);
            }
        });
        jBArchivoTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBArchivoTextoActionPerformed(evt);
            }
        });
        jBArchivoTexto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBArchivoTextoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRMatricial)
                            .addComponent(jRLaser, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBArchivoTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRMatricial)
                        .addGap(50, 50, 50)
                        .addComponent(jRLaser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jBArchivoTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jBImprimir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/imprimir24.png"))); // NOI18N
        jBImprimir.setText("Imprimir");
        jBImprimir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jBImprimirFocusGained(evt);
            }
        });
        jBImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBImprimirActionPerformed(evt);
            }
        });
        jBImprimir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBImprimirKeyPressed(evt);
            }
        });

        jBCancelar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jBCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cancelar24.png"))); // NOI18N
        jBCancelar.setText("Cancelar");
        jBCancelar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jBCancelarFocusGained(evt);
            }
        });
        jBCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelarActionPerformed(evt);
            }
        });
        jBCancelar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBCancelarKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jBCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBCancelar, jBImprimir});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBImprimir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBCancelar, jBImprimir});

        jLAyudaTeclasRapidas.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLAyudaTeclasRapidas.setText("Teclas de Acceso Rapido : F1 Ir a Directorio.");
        jLAyudaTeclasRapidas.setFocusable(false);

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Opciones de Impresión");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLAyudaTeclasRapidas, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(jLAyudaTeclasRapidas, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(502, 363));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBArchivoTextoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBArchivoTextoFocusGained
        jLAyudaTeclasRapidas.setText("Enviar a Archivo TXT < > F1 >> Ir a Directorio ");
    }//GEN-LAST:event_jBArchivoTextoFocusGained

    private void jBCancelarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBCancelarFocusGained
         jLAyudaTeclasRapidas.setText("ENTER >> Cancelar la Impresion ");
    }//GEN-LAST:event_jBCancelarFocusGained

    private void jBImprimirFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBImprimirFocusGained
        jLAyudaTeclasRapidas.setText("ENTER >> Iniciar la Impresion ");
    }//GEN-LAST:event_jBImprimirFocusGained

    private void jRLaserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRLaserFocusGained
        jLAyudaTeclasRapidas.setText("Imprimir en la Laser ");
    }//GEN-LAST:event_jRLaserFocusGained

    private void jRMatricialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRMatricialFocusGained
        jLAyudaTeclasRapidas.setText("Imprimir en la Matricial ");
    }//GEN-LAST:event_jRMatricialFocusGained

    private void jBArchivoTextoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBArchivoTextoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SaveFile();
        } else if (evt.getKeyCode() == KeyEvent.VK_F1) {
            SaveFile();
        }
    }//GEN-LAST:event_jBArchivoTextoKeyPressed

    private void jBArchivoTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBArchivoTextoActionPerformed
        SaveFile();
    }//GEN-LAST:event_jBArchivoTextoActionPerformed

    private void SaveFile() {
        JFileChooser fc = new JFileChooser(new File("/"));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int res = fc.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File TheArchivo = fc.getSelectedFile();
            jTFileName.setText(String.valueOf(TheArchivo));
        } else {
            JOptionPane.showMessageDialog(this, "ATENCION: Defina un nombre para el archivo !!!", "ATENCION...", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void jRLaserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRLaserKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jBImprimir.grabFocus();
        } else if (evt.getKeyCode() == 38) {
            jRMatricial.grabFocus();
        }
    }//GEN-LAST:event_jRLaserKeyPressed

   

    private void jRMatricialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRMatricialKeyPressed
       if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jBImprimir.grabFocus();
        } else if (evt.getKeyCode() == 40) {
            jRLaser.grabFocus();
        }
    }//GEN-LAST:event_jRMatricialKeyPressed

    private void jBCancelarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBCancelarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            dispose();
        }
    }//GEN-LAST:event_jBCancelarKeyPressed

    private void jBCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelarActionPerformed
        
        if (jTCallFrom.getText().trim().equals("AnticipoSalario")) {
           ModRRHH.AnticipoSalario.jTPrinterSelected.setText("CancelPrint");
        } else if (jTCallFrom.getText().trim().equals("RegistroDescBeneficios")) {
           ModRRHH.RegistroDescBeneficios.jTPrinterSelected.setText("CancelPrint");
        }
        dispose();
    }//GEN-LAST:event_jBCancelarActionPerformed

    private void jBImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBImprimirActionPerformed
        
        if (jRMatricial.isSelected()) {
            if (jTCallFrom.getText().trim().equals("AnticipoSalario")) {
                ModRRHH.AnticipoSalario.jTPrinterSelected.setText("Matricial");
            }else if (jTCallFrom.getText().trim().equals("GeneracionOC")) {
            }else if(jTCallFrom.getText().trim().equals("RegistroDecBeneficios")){
                ModRRHH.RegistroDescBeneficios.jTPrinterSelected.setText("Matricial");
            }
        } else {
            if (jTCallFrom.getText().trim().equals("AnticipoSalario")) {
                ModRRHH.AnticipoSalario.jTPrinterSelected.setText("Laser");
            }else if(jTCallFrom.getText().trim().equals("GeneracionOC")) {
            }else if(jTCallFrom.getText().trim().equals("RegistroDescBeneficios")){
                ModRRHH.RegistroDescBeneficios.jTPrinterSelected.setText("Laser");
            }
        }
        dispose();
    }//GEN-LAST:event_jBImprimirActionPerformed

    private void GeneratetoTxt() {
        try {
            FileOutputStream os = new FileOutputStream(jTFileName.getText() + ".txt");
            PrintStream ps = new PrintStream(os);
            ps.println("VERS<|>1.0<|>");
            ps.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void jBImprimirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBImprimirKeyPressed
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (jRMatricial.isSelected()) {
                if (jTCallFrom.getText().trim().equals("RecepcionDirecta")) {
                }
            } else {
                if (jTCallFrom.getText().trim().equals("AnticipoSalario")) {
                  ModRRHH.AnticipoSalario.jTPrinterSelected.setText("Laser");
                } 
            }
            dispose();
        }
        if (evt.getKeyCode() == 38) {
            jRLaser.grabFocus();
        }
    }//GEN-LAST:event_jBImprimirKeyPressed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bttomGroupImpresora;
    private javax.swing.JButton jBArchivoTexto;
    private javax.swing.JButton jBCancelar;
    private javax.swing.JButton jBImprimir;
    private javax.swing.JLabel jLAyudaTeclasRapidas;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRLaser;
    private javax.swing.JRadioButton jRMatricial;
    private javax.swing.JLabel jTFileName;
    // End of variables declaration//GEN-END:variables
}
