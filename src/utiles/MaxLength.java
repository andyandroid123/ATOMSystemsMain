package utiles;

import java.awt.Toolkit;
import javax.swing.text.*;

/* FixedLengthDocument.java
 * Modificada para tambien convertir en mayusculas
 * */

public class MaxLength extends PlainDocument {
    
    private int iMaxLength;  // Tamano maximo del jtfield
    private String iUppLow;  // UPPER o LOWER
    private String iTipo;    // ALFA, ENTERO, FLOAT, DOUBLE tipos de datos aceptados.
    public MaxLength(int maxlen, String UpperLower, String Tipo) {
        super();
        iMaxLength = maxlen;
        iUppLow = UpperLower;
        iTipo = Tipo;
    }
    
    public void insertString(int offset, String str, AttributeSet attr)
            throws BadLocationException {
        if (offset < iMaxLength) { // primer control de length 
            if ((iTipo.equalsIgnoreCase("DOUBLE") ) | (iTipo.equalsIgnoreCase("FLOAT"))) {
                try {
                    Double.parseDouble(str);
                } catch(NumberFormatException NFEe) {
                    if (str.equals(".")) { // || str.equals(","))
                        if (yaTienePunto( super.getText(0, offset)) ) {
                            str =  null;
                        }
                    } else {
                        str = null;
                    }
                }
            } else {
                if (iTipo.equalsIgnoreCase("ENTERO") ) {
                    try {
                        Integer.parseInt(str);
                    } catch (Exception ex) {
                        str = null;
                    }
                } else { // no es ENTERO, FLOAT ni DOUBLE, es ALFA
                    if (iUppLow.equalsIgnoreCase("UPPER")) {
                        str = str.toUpperCase();
                    } else {
                        if (iUppLow.equalsIgnoreCase("LOWER")) {
                            str = str.toLowerCase();
                        }
                    }
                }
            }
            
            if (str == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            // segundo control de length, caso de cortar y pegar
            int ilen = (getLength() + str.length());
            if (ilen <= iMaxLength)    // se o comprimento final for menor...
                super.insertString(offset, str, attr);   // ...aceita str
            else {
                if (getLength() == iMaxLength) return; // nada a fazer
                String newStr = str.substring(0, (iMaxLength - getLength()));
                super.insertString(offset, newStr, attr);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
      
    private boolean yaTienePunto(String txt) {
        char punto = '.';  
        boolean result = false;
        for (int i = 0; i < txt.length() ; i++) {
            if (txt.charAt(i) == punto)
                result = true;
        }
        return result;
    }
}


/*
    Al JTextField que defines en el metodo setDocument pon el nombre de la clase que
            vas a crear a continuacion:
 
import javax.swing.text.*;
import java.awt.*;
 
/**
Esta clase valida que solo ingreses numeros y ademas que no sobrepacen el numero
 * que tu le pongas como parametro, por ejemplo:
 
JTFnumeros.setDocument(new LimitDocument(10));
 
con esto solo te aceptara 10 numeros si precionas alguna letra o simbolo no te la aceptara
 
 */

/*public class LimitDocument extends PlainDocument {
 
    private int limit;
 
    public LimitDocument(int limit) {
        super();
        setLimit(limit); // store the limit
    }
 
    public final int getLimit() {
        return limit;
    }*/

/**
 * Este metodo es el encargado de validar lo que tu quieres recibir,
 * sea numeros, signos o letras
 **/

  /*  public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
        if(offset < limit) {
            String Snum = new String();
            try {
                for (int i=0;i<s.length();i++) {
                    if (!s.substring(i,i+1).equals("."))
                       if (!s.substring(i,i+1).equals(","))
                            Snum=Snum+s.substring(i,i+1);
   
                    if (s.substring(i,i+1).equals(","))
                        Snum=Snum+".";
                }
                float num = Float.parseFloat(Snum);
                super.insertString(offset,s,attributeSet);
            }
            catch(NumberFormatException NFEe) {
                if (s.equals(".") || s.equals(","))
                    super.insertString(offset,s,attributeSet);
                else {
                    Toolkit.getDefaultToolkit().beep();
                    //System.out.p rintln("Exploto s:"+Snum);
                }
            }
        }
        else {
            Toolkit.getDefaultToolkit().beep();
            //System.out.println("Exploto peor");
        }
    }
   
    public final void setLimit(int newValue) {
        this.limit = newValue;
    }
}*/

/*Espero te sirva.
 
Chao
 
Luis Felipe Hernandez Z.
Pasto - Colombia
 
en los componentes JTextField.
>
>Por e jemplo, algunos componentes solo pueden teclear números y puntos '.', en otros solo pueden teclear en mayúsculas, en otros tento un formato específicos, etc.
>
>He revisado algunas y preguntado en otros foros pero me abruman con tantas respuestas diferentes. Algunos me dicen que utilice el KeyListerner, otros el PlainDocument o también Format.
>
>El asunto es que aún no he podido lograr mi objetivo: de una manera directa y sencilla controlar que en los JTextField el usuario pueda solo teclear lo que me convenga.
>
>Alguién me puede ayudar???
>
>Gracias
>
 
/// ***************************************
 
import javax.swing.*;
import javax.swing.text.*;
 
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
 
public class WholeNumberField extends JTextField {
 
    private Toolkit toolkit;
    private NumberFormat integerFormatter;
 
    public WholeNumberField(int value, int columns) {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        integerFormatter = NumberFormat.getNumberInstance(Locale.US);
        integerFormatter.setParseIntegerOnly(true);
        setValue(value);
    }
 
    public int getValue() {
        int retVal = 0;
        try {
            retVal = integerFormatter.parse(getText()).intValue();
        } catch (ParseException e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            toolkit.beep();
        }
        return retVal;
    }
 
    public void setValue(int value) {
        setText(integerFormatter.format(value));
    }
 
    protected Document createDefaultModel() {
        return new WholeNumberDocument();
    }
 
    protected class WholeNumberDocument extends PlainDocument {
 
        public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
 
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;
 
            for (int i = 0; i < result.length; i++) {
                if (Character.isDigit(source[i]))
                    result[j++] = source[i];
                else {
                    toolkit.beep();
                    System.err.println("insertString: " + source[i]);
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
 
}
 
    /*public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
        if(offset < limit) {
            String Snum = new String();
            try {
                for (int i=0;i<s.length();i++) {
                    if (!s.substring(i,i+1).equals("."))
                       if (!s.substring(i,i+1).equals(","))
                            Snum=Snum+s.substring(i,i+1);
 
                    if (s.substring(i,i+1).equals(","))
                        Snum=Snum+".";
                }
                float num = Float.parseFloat(Snum);
                super.insertString(offset,s,attributeSet);
            }
            catch(NumberFormatException NFEe) {
                if (s.equals(".")) // || s.equals(","))
                    if ( ! yaTienePunto( super.getText(0, offset) ))
                      super.insertString(offset,s,attributeSet);
                    else
                      Toolkit.getDefaultToolkit().beep();
                else {
                    Toolkit.getDefaultToolkit().beep();
                    //System.out.p rintln("Exploto s:"+Snum);
                }
            }
        }
        else {
            Toolkit.getDefaultToolkit().beep();
            //System.out.println("Exploto peor");
        }
    }*/
