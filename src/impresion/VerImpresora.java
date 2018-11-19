
/*
 * VerImpresora.java
 * Created on 2 de noviembre de 2007, 11:29 AM
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package impresion;

//import jnpout32.*;
/**
 * @author
 */
public class VerImpresora {

    // static short datum;
    // static short Addr;
    // static pPort lpt;
    public boolean Enlinea = true;
    public boolean FaltaPapel_o_EnPausa = false;

    /**
     * Creates a new instance of VerImpresora
     */
    public VerImpresora() {
        // lpt = new pPort();
        //do_read_range();
        Enlinea = true;
        FaltaPapel_o_EnPausa = false;
    }

    /*
     * public void do_read_range() { // Try to read 0x378..0x37F, LPT1: for
     * (Addr = 0x378; (Addr < 0x380); Addr++) { //Read from the port datum =
     * (short) lpt.input(Addr); if
     * (Integer.toHexString(Addr).trim().equals("379")) { if
     * (Integer.toHexString(datum).trim().equals("df")) { Enlinea = true; if
     * (Integer.toHexString(datum).trim().equals("77")) { FaltaPapel_o_EnPausa =
     * true; } else { FaltaPapel_o_EnPausa = false; } } else if
     * (Integer.toHexString(datum).trim().equals("7f")) { Enlinea = false; }
     * else if (Integer.toHexString(datum).trim().equals("77")) {
     * FaltaPapel_o_EnPausa = true; } } }
    }
     */
}
