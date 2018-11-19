/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Andres
 */
public class FechaInputVerifier extends InputVerifier {

    private JTextField textfield;
    private boolean aceptaFechaPosteriorAlActual;
    private boolean result = true;

    public FechaInputVerifier(JTextField textField) {
        this(textField, true);
    }

    public FechaInputVerifier(JTextField textfield, boolean aceptaFechaPosteriorAlActual) {
        this.textfield = textfield;
        this.aceptaFechaPosteriorAlActual = aceptaFechaPosteriorAlActual;
    }

    @Override
    public boolean verify(JComponent input) {
        Calendar vCalend = Calendar.getInstance();
        int vAnyo = vCalend.get(Calendar.YEAR);
        int vMes = vCalend.get(Calendar.MONTH);
        vMes += 1;
        String vElanoCorr = (new Integer(vAnyo)).toString();
        String vElmesCorr = "";
        if (vMes <= 9) {
            vElmesCorr = "0" + (new Integer(vMes)).toString();
        }else
        {
            vElmesCorr = new Integer(vMes).toString();
        }
        result = true;
        String data = this.textfield.getText().trim().replace("-", "/");
        boolean badFormat = false;

        if (data != null && data.length() > 0) {

            if (data.length() == 2) {

                if (!(data.substring(0, 1).equals("/") || data.substring(1, 1).equals("/"))) {
                    data = data.substring(0, 2) + "/" + vElmesCorr.substring(0, 2) + "/" + vElanoCorr.substring(0, 4);
                    this.textfield.setText(data);
                    System.out.println("FORMATO:" + data);
                } else {
                    badFormat = true;                    
                }

            }
            if (data.length() == 4) {
                if (!(data.substring(0, 1).equals("/") || data.substring(1, 1).equals("/"))) {
                    //   data = data.substring(0, 2) + "/" + vElmesCorr.substring(0, 2) + "/" + vElanoCorr.substring(0,4);
                    data = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + vElanoCorr.substring(0, 4);
                    this.textfield.setText(data);
                } else {
                    badFormat = true;
                }
            } else if (data.length() == 6) {
                if (!(data.substring(2, 3).equals("/") || data.substring(5, 6).equals("/"))) {
                    data = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + vElanoCorr.substring(0, 2) + data.substring(4);
                    this.textfield.setText(data);
                } else {
                    badFormat = true;
                }
            } else if (data.length() == 8) {
                if (!(data.substring(2, 3).equals("/") || data.substring(5, 6).equals("/"))) {
                    data = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + data.substring(4);
                    this.textfield.setText(data);
                } else {
                    badFormat = true;
                }
            } else if (data.length() == 10) {
                if (!(data.substring(2, 3).equals("/") && data.substring(5, 6).equals("/"))) {
                    badFormat = true;
                }
            } else {
                badFormat = true;
            }
        }

        if (badFormat) {
            JOptionPane.showMessageDialog(null,
                    "Digite la Fecha en el formato DD/MM/AAAA", "Error",
                    JOptionPane.ERROR_MESSAGE);
            result = false;
        } else if (data.length() > 0) {
            if (!esFechaValida(data)) {
                //if (! esFechaValida(data)) {
                JOptionPane.showMessageDialog(null, "Fecha Inválida.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                result = false;
            } else if (!aceptaFechaPosteriorAlActual) {
                try {
                    Date dataDigitada = Utiles.formataStringToDate(data);
                    //Timestamp dataDigitada = formataStringToDate(data);
                    //Date dataAtual = Utiles.getSysDateTimeStamp();  // dataAtual();
                    Date dataAtual = Utiles.getSysDateTimeStamp();  // dataAtual();
                    //Timestamp dataAtual = getSysDateTimeStamp();  // dataAtual();
                    if (dataDigitada.after(dataAtual)) {
                        JOptionPane.showMessageDialog(null,
                                "Fecha no puede ser Mayor a Fecha Actual.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        result = false;
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                    InfoErrores.errores(exc);
                }
            }
        }
        return result;
    }

    public static boolean esFechaValida(String dataStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // o mo�o que vai ver se os numeros ultrapassam o permitido!
        try {
            Date data = sdf.parse(dataStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
