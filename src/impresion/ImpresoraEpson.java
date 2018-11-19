/*
 * ImpresoraEmpson.java
 *
 * Created on 5 de noviembre de 2007, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package impresion;

/**
 *
 *  
 */
public class ImpresoraEpson extends Impresora {

    private final String CONDENSADO = "\u001B\u000F";
    private final String FINALIZACONDENSADO = "\u001B\u0012";
    private final String INICIANEGRITA = "\u001B\u0045";
    private final String INICIASUBRAYADO = "\u001B\u002D";
    private final String FINALIZASUBRAYADO = "\u001B\u002D";
    private final String FINALIZANEGRITA = "\u001B\u0046";
    private final String NORMAL = "\u0012";
    private final String ALINHAMENTO_VERTICAL_18 = "\u001B\u0030";
    private final String TAMANHO_DA_PAGINA = "\u001BC\u0044";
    private final String RESET = "\u001B@";
    private final String NEW_LINE = "\n";
    private final String CARRIAGE_RETURN = "\r";
    private final String FORM_FEED = "\f";
    private final String EJECT = "\u000c";

    /**
     * Creates a new instance of ImpresoraEmpson
     */
    public ImpresoraEpson(String impressora) {
        super(impressora);
    }

    public void eject() {
        print(FORM_FEED);
        //print(EJECT);
    }

    public void setCaracterCondensado() {
        print(CONDENSADO);
    }

    public void BackCaracterCondensado() {
        print(FINALIZACONDENSADO);
    }

    public void setCaracterNegrita() {
        print(INICIANEGRITA);
    }

    public void BackSetCaracterNegrita() {
        print(FINALIZANEGRITA);
    }

    public void getCaracterNormal() {
        print(NORMAL);
    }

    public void setCaracterSubrayado() {
        print(INICIASUBRAYADO);
    }

    public void BackCaracterSubrayado() {
        print(FINALIZASUBRAYADO);
    }

    public void setAlinhamentoVertical18() {
        //.print(ALINHAMENTO_VERTICAL_1;
    }

    public void setTamanhoPagina() {
        print(TAMANHO_DA_PAGINA);
    }

    public void reset() {
        print(RESET);
    }
}
