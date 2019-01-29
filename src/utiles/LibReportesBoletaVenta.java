package utiles;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JDialog;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import principal.FormMain;
import procesando.DlgProcesando;

/**
 *
 */
public class LibReportesBoletaVenta {

    public static Map parameters = new HashMap();
    static Thread hilo;
    static boolean pideParar = false;
    public static DlgProcesando dlg = null;

    /**
     * Con fuente de datos SQL
     */
    public static void generarReportes(final String sql, final String nomReporte) throws SQLException, JRException {
        Runnable miRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    dlg = new DlgProcesando("ATENCION: Procesando Informe : \n" + nomReporte + " - Favor Aguarde !!!");
                    dlg.pack();
                    dlg.setVisible(true);
                    dlg.setAlwaysOnTop(true);
                    JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(100, "/" + FormMain.nombreCarpetaProyecto + "/logs");
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);
                    Statement stm = DBManager.conn.createStatement();
                    ResultSet rs = stm.executeQuery(sql);
                    JDialog viewer = new JDialog(new javax.swing.JFrame(), "ATOMSystems - Reportes. " + nomReporte, true);
                    viewer.setSize(1024, 740);  //1024,768
                    //viewer.setLocation(0, 27);  // posicionar abajo de la barra de titulo form principal
                    viewer.setLocation(150, 27);  // posicionar en el medio y comenzando en la barra de titulo principal del modulo
                    JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
                    JasperReport relatoriosJasper = (JasperReport) JRLoader.loadObject(new File("/" + FormMain.nombreCarpetaProyecto + "/reports/" + nomReporte + ".jasper"));
                    JasperPrint jasperPrint = JasperFillManager.fillReport(relatoriosJasper, parameters, jrRS);
                    imprimir(jasperPrint); // imprime directamente en la impresora 

                    dlg.setVisible(false);
                    dlg.dispose();
                    pideParar = true;
                    hilo = null;
                    /*JasperViewer jrViewer = new JasperViewer(jasperPrint, true);
                    viewer.getContentPane().add(jrViewer.getContentPane());
                    viewer.setVisible(true);
                    viewer.dispose();
                    viewer = null;*/
                    jrRS = null;
                    relatoriosJasper = null;
                    jasperPrint = null;
                 
                    //jrViewer = null;
                    rs.close();
                    stm.close();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                    InfoErrores.errores(e);
                } finally {
                    dlg.setVisible(false);
                    dlg.dispose();
                }
            }
        };
        Thread hilo = new Thread(miRunnable);
        hilo.start();
    }

    /**
     * Con fuente de datos ArrayList
     */
    public static void generarReportes(final ArrayList list, final String nomReporte) throws SQLException, JRException {
        Runnable miRunnable = new Runnable() {

            @Override
            public void run() {
                dlg = new DlgProcesando("ATENCION: Procesando Informe : \n" + nomReporte + " - Favor Aguarde !!!");
                dlg.setVisible(true);
                try {
                    JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(100, "/" + FormMain.nombreCarpetaProyecto + "/logs"); 
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);

                    JDialog viewer = new JDialog(new javax.swing.JFrame(), "ATOMSystems - Reportes. " + nomReporte, true);
                    viewer.setSize(1024, 740);  //1024,768
                    viewer.setLocation(0, 27);  // posicionar abajo de la barra de titulo form principal

                    JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
                    JasperReport relatoriosJasper = (JasperReport) JRLoader.loadObject(new File("/" + FormMain.nombreCarpetaProyecto + "/reports/" + nomReporte + ".jasper"));
                    JasperPrint jasperPrint = JasperFillManager.fillReport(relatoriosJasper, parameters, ds);

                    dlg.setVisible(false);
                    dlg.dispose();
                    pideParar = true;
                    hilo = null;

                    JasperViewer jrViewer = new JasperViewer(jasperPrint, true);
                    viewer.getContentPane().add(jrViewer.getContentPane());
                    viewer.setVisible(true);

                    viewer.dispose();
                    viewer = null;
                    ds = null;
                    relatoriosJasper = null;
                    jasperPrint = null;
                    jrViewer.dispose();
                    jrViewer = null;
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                    InfoErrores.errores(e);
                } finally {
                    dlg.setVisible(false);
                    dlg.dispose();
                }
            }
        };
        Thread hilo = new Thread(miRunnable);
        hilo.start();
    }

    public static void exportToPDF(final String sql, final String nomReporte, final String nombrePDF) {
        try {
            dlg = new DlgProcesando("ATENCION: Exportando Informe : \n" + nomReporte + " - Favor Aguarde !!!");
            dlg.setVisible(true);

            //ServletOutputStream out;
            Statement stm = DBManager.conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);

            // Creamos un objecto jasper con el fichero previamente compilado
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("/" + FormMain.nombreCarpetaProyecto + "/reports/" + nomReporte + ".jasper"));

            byte[] fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, jrRS);

            JRPdfExporter exporter = new JRPdfExporter(); 
             
            dlg.setVisible(false);
            dlg.dispose();
            pideParar = true;
            hilo = null;
            jrRS = null;
            jasperReport = null;
            rs.close();
            stm.close();
            System.gc();
        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
    }

    public static void printReportes(String sql, String nomReporte) throws SQLException, JRException {
        Statement stm = DBManager.conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        /* implementacion de la interfas JRDataSource para DataSource ResultSet */
        JDialog viewer = new JDialog(new javax.swing.JFrame(), "ATOMSystems - Reportes. " + nomReporte, true);
        viewer.setSize(1024, 740);  //1024,768
        viewer.setLocation(0, 27);  // posicionar abajo de la barra de titulo form principal
  
        JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
        JasperReport relatoriosJasper = (JasperReport) JRLoader.loadObject(new File("/" + FormMain.nombreCarpetaProyecto + "/reports/" + nomReporte + ".jasper"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(relatoriosJasper, parameters, jrRS);
        JasperPrintManager.printReport(jasperPrint, true);
        viewer.dispose();
        viewer = null;
        jrRS = null;
        relatoriosJasper = null;
        jasperPrint = null;
        rs.close();
        stm.close();
        System.gc();
    }

    public static void imprimir(JasperPrint jasperPrint) throws JRException {
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        PrintService impresoraPredeterminada = PrintServiceLookup.lookupDefaultPrintService();
      
        float w = jasperPrint.getPageWidth() / 72f;
        float h = jasperPrint.getPageHeight() / 72f;
        // Busco el tamano de papel de la impresora mas parecido
        printRequestAttributeSet.add(MediaSize.findMedia(w, h, MediaSize.INCH));
        // Configuro el area de impresion
        int unidad = MediaPrintableArea.INCH;
        //printRequestAttributeSet.add(new MediaPrintableArea(0, 0, w, h, unidad));
        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, w, h, unidad));
        // Orientaci�n
        OrientationRequested orientation = OrientationRequested.PORTRAIT;
        /*if (jasperPrint.getOrientation() == JRReport.ORIENTATION_LANDSCAPE) {
         orientation = OrientationRequested.LANDSCAPE;
         }*/
        printRequestAttributeSet.add(orientation);
        PrintService service = impresoraPredeterminada;
        JRExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, service);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.exportReport();
    }
}
