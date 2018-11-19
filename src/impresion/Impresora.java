/*
 * Impressora.java
 *
 * Created on 5 de noviembre de 2007, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package impresion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author  
 */
public abstract class Impresora {

    private String NEW_LINE = "\n";
    private String CARRIAGE_RETURN = "\r";
    private String FORM_FEED = "\f";
    private StringBuffer buffer = null;
    private String puertaImpresora = null;

    /**
     * Creates a new instance of Impressora
     */
    public Impresora(String puertaImpresora) {
        /*
         * Deve ser passado o nome da porta da impressora "/dev/lp0" no LINUX
         * "LPT1:" no WINDOWS
         */
        this.puertaImpresora = puertaImpresora;
        init();
    }

    public void setCaracterCondensado() {
    }

    public void setCaracterNegrita() {
    }

    public void BackCaracterCondensado() {
    }

    public void setCaracterSubrayado() {
    }

    public void BackCaracterSubrayado() {
    }

    public void BackSetCaracterNegrita() {
    }

    public void getCaracterNormal() {
    }

    public void setAlinhamentoVertical18() {
    }

    public void setTamanhoPagina() {
    }

    public void reset() {
    }

    public void setFuente(String unicode) {
        print("\u001b" + "!" + unicode);
    }

    public void setColor(String unicode) {
        // color cinta
    }

    public void print(String s) {
        //JOptionPane.showMessageDialog(null, CompletarSpace(pCol), "CompletarSpace(pCol) ATENCION", JOptionPane.WARNING_MESSAGE);
        buffer.append(s);
    }

    public void printinCol(int pCol, String s) {
        //JOptionPane.showMessageDialog(null, AsingSpace(pCol).length(), "AsingSpace(pCol).length() ATENCION", JOptionPane.WARNING_MESSAGE);
        buffer.append(AsingSpace(pCol) + s);
    }

    public String AsingSpace(int vSpace) {
        // JOptionPane.showMessageDialog(null, vSpace, "vSpace ATENCION", JOptionPane.WARNING_MESSAGE);
        String vStrSpacing = "";
        int i;
        if (vSpace > 0) {
            for (i = 0; i < vSpace; i++) {
                vStrSpacing += " ";
            }
        }
        return vStrSpacing;
    }

    public void println(String s) {
        while (EstadoImpresoraOK() == false);
        buffer.append(s);
        println();
    }

    private boolean EstadoImpresoraOK() {
        VerImpresora vPrinter = new VerImpresora();
        boolean vEstadoPRN = false;
        if (vPrinter.Enlinea) {
            //. JOptionPane.showMessageDialog(null, "Impresora en Linea !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            vEstadoPRN = true;
        } else {
            JOptionPane.showMessageDialog(null, "ATENCION: \n Impresora Apagada !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            vEstadoPRN = false;
        }

        if (vPrinter.FaltaPapel_o_EnPausa) {
            JOptionPane.showMessageDialog(null, "ATENCION: \n Impresora sin Papel !!!", "ATENCION", JOptionPane.WARNING_MESSAGE);
            vEstadoPRN = false;
        }
        // else {
        //   vEstadoPRN = true;
        // }
        return vEstadoPRN;
    }

    public void println() {
        buffer.append(NEW_LINE + CARRIAGE_RETURN);
    }

    public void printff() {
        buffer.append(FORM_FEED);
    }

    public void init() {
        buffer = new StringBuffer();
    }

    public void flush() {
        FileOutputStream fos = null;
        boolean impresso;

        do {
            try {
                //String puerto = Utiles.getPuerto(Utiles.ipAdress().replace(".", ""));
                //if (!puerto.equals("sp")) {
                //    impresora = puerto;
                //}
                fos = new FileOutputStream(puertaImpresora);
            } catch (FileNotFoundException fnfe) {
                 fnfe.getMessage();
                 fnfe.printStackTrace();
                fos = null;
                if (JOptionPane.showConfirmDialog(null,
                        "ATENCION: \n Impresora NO esta lista (Puerta: " + puertaImpresora + ") !!! \n Verifique el problema y presione OK para re-intentar...",
                        "Error de Comunicacion con la Impresora", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
        } while (fos == null);

        try {
            impresso = false;
            do {
                try {
                    fos.write(buffer.toString().getBytes());
                    fos.flush();
                    impresso = true;
                } catch (IOException ioe) {
                     ioe.getMessage();
                     ioe.printStackTrace();
                    if (JOptionPane.showConfirmDialog(null,
                            "ATENCION: \n Impresora NO esta lista (Puerta: " + puertaImpresora + ") !!! \n Verifique el problema y presione OK para re-intentar...",
                            "Error de Comunicacion con la Impresora", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
            } while (!impresso);
        } finally {
            try {
                fos.close();
            } catch (IOException ioe) {
                ioe.getMessage();
                ioe.printStackTrace();
            }
        }
    }
}
