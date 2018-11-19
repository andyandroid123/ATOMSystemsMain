/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import ModRRHH.AnticipoSalario;
import ModRRHH.AnulacionDescBeneficio;
import ModRRHH.CalculoDiasHoras;
import ModRRHH.Cargo;
import ModRRHH.CierrePeriodo;
import ModRRHH.Conceptos;
import ModRRHH.Empleados;
import ModRRHH.Horarios;
import ModRRHH.MotivoLiquidacionPersonal;
import ModRRHH.PreLiquidacionSalarios;
import ModRRHH.Profesion;
import ModRRHH.RegistroDescBeneficios;
import ModRRHH.RegistroModificacionDiasHoras;
import ModRRHH.Seccion;
import ModRRHH.informes.InformeDescBeneficios;
import ModRegistros.Barrios;
import ModRegistros.Ciudades;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Andres
 */
public class FormRRHH extends javax.swing.JFrame {

    /**
     * Creates new form FormRRHH
     */
    public FormRRHH() {
        initComponents();
        cerrarVentana();
    }

    private void cerrarVentana()
    {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                FormMain.resultExitRRHH = false;
            }
        });
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
        jMenuBar1 = new javax.swing.JMenuBar();
        jMnuConceptos = new javax.swing.JMenu();
        jMnuIEmpleados = new javax.swing.JMenuItem();
        jMnuIProfesion = new javax.swing.JMenuItem();
        jMnuISeccion = new javax.swing.JMenuItem();
        jMnuICargo = new javax.swing.JMenuItem();
        jMnuICiudad = new javax.swing.JMenuItem();
        jMnuIBarrio = new javax.swing.JMenuItem();
        jMnuIConceptos = new javax.swing.JMenuItem();
        jMnuIHorarios = new javax.swing.JMenuItem();
        jMnuIMotivoLiquidacion = new javax.swing.JMenuItem();
        jMnuMantenimiento = new javax.swing.JMenu();
        jMnuIRegistro = new javax.swing.JMenu();
        jMnuIAnticipo = new javax.swing.JMenuItem();
        jMnuIDescuentoBeneficio = new javax.swing.JMenuItem();
        jMnuIAnulacionDescBen = new javax.swing.JMenuItem();
        jMnuIRegModDiasHoras = new javax.swing.JMenuItem();
        jMnuInformes = new javax.swing.JMenu();
        jMnuIDescBen = new javax.swing.JMenuItem();
        jMnuILiqSalarios = new javax.swing.JMenuItem();
        jMnuIRecibos = new javax.swing.JMenuItem();
        jMnuProcesos = new javax.swing.JMenu();
        jMnuIPreLiquidacionSalarios = new javax.swing.JMenuItem();
        jMnuILiquidacionDefinitivaSalarios = new javax.swing.JMenuItem();
        jMnuICalculoDiasHoras = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATOMSystems|Main - Módulo de RRHH");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1264, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );

        jMnuConceptos.setText("Registros");
        jMnuConceptos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIEmpleados.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIEmpleados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/empleado24.png"))); // NOI18N
        jMnuIEmpleados.setText("Empleados");
        jMnuIEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIEmpleadosActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIEmpleados);

        jMnuIProfesion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIProfesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/profesion24.png"))); // NOI18N
        jMnuIProfesion.setText("Profesiones");
        jMnuIProfesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIProfesionActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIProfesion);

        jMnuISeccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuISeccion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/seccion24.png"))); // NOI18N
        jMnuISeccion.setText("Secciones");
        jMnuISeccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuISeccionActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuISeccion);

        jMnuICargo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuICargo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/cargo24.png"))); // NOI18N
        jMnuICargo.setText("Cargos");
        jMnuICargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuICargoActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuICargo);

        jMnuICiudad.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuICiudad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ciudades24.png"))); // NOI18N
        jMnuICiudad.setText("Ciudades");
        jMnuICiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuICiudadActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuICiudad);

        jMnuIBarrio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIBarrio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/barrios24.png"))); // NOI18N
        jMnuIBarrio.setText("Barrios");
        jMnuIBarrio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIBarrioActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIBarrio);

        jMnuIConceptos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIConceptos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/conceptos24.png"))); // NOI18N
        jMnuIConceptos.setText("Conceptos ");
        jMnuIConceptos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIConceptosActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIConceptos);

        jMnuIHorarios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIHorarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/horarios24.png"))); // NOI18N
        jMnuIHorarios.setText("Horarios");
        jMnuIHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIHorariosActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIHorarios);

        jMnuIMotivoLiquidacion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIMotivoLiquidacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/motivoLiquidacion24.png"))); // NOI18N
        jMnuIMotivoLiquidacion.setText("Motivo Liquidación");
        jMnuIMotivoLiquidacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIMotivoLiquidacionActionPerformed(evt);
            }
        });
        jMnuConceptos.add(jMnuIMotivoLiquidacion);

        jMenuBar1.add(jMnuConceptos);

        jMnuMantenimiento.setText("Mantenimiento");
        jMnuMantenimiento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIRegistro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/descuentoBeneficios24.png"))); // NOI18N
        jMnuIRegistro.setText("Registro de Descuento/Beneficios");
        jMnuIRegistro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIAnticipo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIAnticipo.setText("Anticipo");
        jMnuIAnticipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIAnticipoActionPerformed(evt);
            }
        });
        jMnuIRegistro.add(jMnuIAnticipo);

        jMnuIDescuentoBeneficio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIDescuentoBeneficio.setText("Otros descuentos/beneficios");
        jMnuIDescuentoBeneficio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIDescuentoBeneficioActionPerformed(evt);
            }
        });
        jMnuIRegistro.add(jMnuIDescuentoBeneficio);

        jMnuMantenimiento.add(jMnuIRegistro);

        jMnuIAnulacionDescBen.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIAnulacionDescBen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/anulacion24.png"))); // NOI18N
        jMnuIAnulacionDescBen.setText("Anulación Descuentos/Beneficios");
        jMnuIAnulacionDescBen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIAnulacionDescBenActionPerformed(evt);
            }
        });
        jMnuMantenimiento.add(jMnuIAnulacionDescBen);

        jMnuIRegModDiasHoras.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIRegModDiasHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/diashoras24.png"))); // NOI18N
        jMnuIRegModDiasHoras.setText("Registro/Modificación de Dias y Horas Trabajadas");
        jMnuIRegModDiasHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIRegModDiasHorasActionPerformed(evt);
            }
        });
        jMnuMantenimiento.add(jMnuIRegModDiasHoras);

        jMenuBar1.add(jMnuMantenimiento);

        jMnuInformes.setText("Informes");
        jMnuInformes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIDescBen.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIDescBen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/descuentoBeneficios24.png"))); // NOI18N
        jMnuIDescBen.setText("Descuentos y Beneficios");
        jMnuIDescBen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIDescBenActionPerformed(evt);
            }
        });
        jMnuInformes.add(jMnuIDescBen);

        jMnuILiqSalarios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuILiqSalarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/liquidacion_definitiva24.png"))); // NOI18N
        jMnuILiqSalarios.setText("Liquidación de Salarios");
        jMnuInformes.add(jMnuILiqSalarios);

        jMnuIRecibos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIRecibos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/recibo24.png"))); // NOI18N
        jMnuIRecibos.setText("Recibos de Salarios");
        jMnuInformes.add(jMnuIRecibos);

        jMenuBar1.add(jMnuInformes);

        jMnuProcesos.setText("Procesos");
        jMnuProcesos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMnuIPreLiquidacionSalarios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuIPreLiquidacionSalarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pre_liquidacion24.png"))); // NOI18N
        jMnuIPreLiquidacionSalarios.setText("Pre Liquidación de Salarios");
        jMnuIPreLiquidacionSalarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuIPreLiquidacionSalariosActionPerformed(evt);
            }
        });
        jMnuProcesos.add(jMnuIPreLiquidacionSalarios);

        jMnuILiquidacionDefinitivaSalarios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuILiquidacionDefinitivaSalarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/liquidacion_definitiva24.png"))); // NOI18N
        jMnuILiquidacionDefinitivaSalarios.setText("Liquidación Definitiva de Salarios");
        jMnuILiquidacionDefinitivaSalarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuILiquidacionDefinitivaSalariosActionPerformed(evt);
            }
        });
        jMnuProcesos.add(jMnuILiquidacionDefinitivaSalarios);

        jMnuICalculoDiasHoras.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMnuICalculoDiasHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/calculo_dias_horas24.png"))); // NOI18N
        jMnuICalculoDiasHoras.setText("Cálculo de Días/Horas Trabajadas");
        jMnuICalculoDiasHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuICalculoDiasHorasActionPerformed(evt);
            }
        });
        jMnuProcesos.add(jMnuICalculoDiasHoras);

        jMenuBar1.add(jMnuProcesos);

        setJMenuBar(jMenuBar1);

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

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMnuIEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIEmpleadosActionPerformed
        Empleados empleados = new Empleados(new JFrame(), true);
        empleados.pack();
        empleados.setVisible(true);
    }//GEN-LAST:event_jMnuIEmpleadosActionPerformed

    private void jMnuIProfesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIProfesionActionPerformed
        Profesion profesion = new Profesion(new JFrame(), true);
        profesion.pack();
        profesion.setVisible(true);
    }//GEN-LAST:event_jMnuIProfesionActionPerformed

    private void jMnuISeccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuISeccionActionPerformed
        Seccion seccion = new Seccion(new JFrame(), true);
        seccion.pack();
        seccion.setVisible(true);
    }//GEN-LAST:event_jMnuISeccionActionPerformed

    private void jMnuICargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuICargoActionPerformed
        Cargo cargo = new Cargo(new JFrame(), true);
        cargo.pack();
        cargo.setVisible(true);
    }//GEN-LAST:event_jMnuICargoActionPerformed

    private void jMnuICiudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuICiudadActionPerformed
        Ciudades ciudad = new Ciudades(new JFrame(), true);
        ciudad.pack();
        ciudad.setVisible(true);
    }//GEN-LAST:event_jMnuICiudadActionPerformed

    private void jMnuIBarrioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIBarrioActionPerformed
        Barrios barrio = new Barrios(new JFrame(), true);
        barrio.pack();
        barrio.setVisible(true);
    }//GEN-LAST:event_jMnuIBarrioActionPerformed

    private void jMnuIConceptosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIConceptosActionPerformed
        Conceptos conceptos = new Conceptos(new JFrame(), true);
        conceptos.pack();
        conceptos.setVisible(true);
    }//GEN-LAST:event_jMnuIConceptosActionPerformed

    private void jMnuIHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIHorariosActionPerformed
        Horarios horarios = new Horarios(new JFrame(), true);
        horarios.pack();
        horarios.setVisible(true);
    }//GEN-LAST:event_jMnuIHorariosActionPerformed

    private void jMnuIMotivoLiquidacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIMotivoLiquidacionActionPerformed
        MotivoLiquidacionPersonal motivo = new MotivoLiquidacionPersonal(new JFrame(), true);
        motivo.pack();
        motivo.setVisible(true);
    }//GEN-LAST:event_jMnuIMotivoLiquidacionActionPerformed

    AnticipoSalario anticipo;
    
    private void jMnuIAnticipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIAnticipoActionPerformed
        
        if(anticipo == null){
            anticipo = new AnticipoSalario(new JFrame(), true); // el diálogo procesando aparece detrás del form if modal = true
            anticipo.pack();
            anticipo.setVisible(true);
        }else{
            anticipo.setVisible(true);
        }
        
    }//GEN-LAST:event_jMnuIAnticipoActionPerformed

    RegistroDescBeneficios regDescBen;
    
    private void jMnuIDescuentoBeneficioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIDescuentoBeneficioActionPerformed
        if(regDescBen == null){
            regDescBen = new RegistroDescBeneficios(new JFrame(), true);
            regDescBen.pack();
            regDescBen.setVisible(true);
        }else{
            regDescBen.setVisible(true);
        }
    }//GEN-LAST:event_jMnuIDescuentoBeneficioActionPerformed

    private void jMnuIAnulacionDescBenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIAnulacionDescBenActionPerformed
        AnulacionDescBeneficio anulacion = new AnulacionDescBeneficio(new JFrame(), true);
        anulacion.pack();
        anulacion.setVisible(true);
    }//GEN-LAST:event_jMnuIAnulacionDescBenActionPerformed

    private void jMnuIRegModDiasHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIRegModDiasHorasActionPerformed
        RegistroModificacionDiasHoras diasHoras = new RegistroModificacionDiasHoras(new JFrame(), true);
        diasHoras.pack();
        diasHoras.setVisible(true);
    }//GEN-LAST:event_jMnuIRegModDiasHorasActionPerformed

    private void jMnuICalculoDiasHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuICalculoDiasHorasActionPerformed
        CalculoDiasHoras calculo = new CalculoDiasHoras(new JFrame(), true);
        calculo.pack();
        calculo.setVisible(true);
    }//GEN-LAST:event_jMnuICalculoDiasHorasActionPerformed

    private void jMnuIPreLiquidacionSalariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIPreLiquidacionSalariosActionPerformed
        PreLiquidacionSalarios preLiquidacion = new PreLiquidacionSalarios(new JFrame(), true);
        preLiquidacion.pack();
        preLiquidacion.setVisible(true);
    }//GEN-LAST:event_jMnuIPreLiquidacionSalariosActionPerformed

    private void jMnuILiquidacionDefinitivaSalariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuILiquidacionDefinitivaSalariosActionPerformed
        CierrePeriodo cierre = new CierrePeriodo(new JFrame(), true);
        cierre.pack();
        cierre.setVisible(true);
    }//GEN-LAST:event_jMnuILiquidacionDefinitivaSalariosActionPerformed

    InformeDescBeneficios iDesBenef;
    
    private void jMnuIDescBenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuIDescBenActionPerformed
        if(iDesBenef == null){
            iDesBenef = new InformeDescBeneficios(new JFrame(), true);
            iDesBenef.pack();
            iDesBenef.setVisible(true);
        }else{
            iDesBenef.setVisible(true);
        }
    }//GEN-LAST:event_jMnuIDescBenActionPerformed

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
            java.util.logging.Logger.getLogger(FormRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormRRHH.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormRRHH().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMnuConceptos;
    private javax.swing.JMenuItem jMnuIAnticipo;
    private javax.swing.JMenuItem jMnuIAnulacionDescBen;
    private javax.swing.JMenuItem jMnuIBarrio;
    private javax.swing.JMenuItem jMnuICalculoDiasHoras;
    private javax.swing.JMenuItem jMnuICargo;
    private javax.swing.JMenuItem jMnuICiudad;
    private javax.swing.JMenuItem jMnuIConceptos;
    private javax.swing.JMenuItem jMnuIDescBen;
    private javax.swing.JMenuItem jMnuIDescuentoBeneficio;
    private javax.swing.JMenuItem jMnuIEmpleados;
    private javax.swing.JMenuItem jMnuIHorarios;
    private javax.swing.JMenuItem jMnuILiqSalarios;
    private javax.swing.JMenuItem jMnuILiquidacionDefinitivaSalarios;
    private javax.swing.JMenuItem jMnuIMotivoLiquidacion;
    private javax.swing.JMenuItem jMnuIPreLiquidacionSalarios;
    private javax.swing.JMenuItem jMnuIProfesion;
    private javax.swing.JMenuItem jMnuIRecibos;
    private javax.swing.JMenuItem jMnuIRegModDiasHoras;
    private javax.swing.JMenu jMnuIRegistro;
    private javax.swing.JMenuItem jMnuISeccion;
    private javax.swing.JMenu jMnuInformes;
    private javax.swing.JMenu jMnuMantenimiento;
    private javax.swing.JMenu jMnuProcesos;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
