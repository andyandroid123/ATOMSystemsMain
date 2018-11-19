/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 *
 * @author Andres
 */
public class SetAppearance {
    
    public static void setLookAndFeel(){
        try {
            //UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
            JFrame.setDefaultLookAndFeelDecorated(true); //que nos permite dejar a Substance la decoracion ( por asi decirlo)
            //UIManager.setLookAndFeel("org.jvnet.substance.SubstanceBusinessBlueSteelLookAndFeel"); // SubstanceBusinessBlackSteelLookAndFeel");
            SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.MistAquaSkin"); // Setencia que aplica el skin Creme de Substance
            //SubstanceLookAndFeel.setCurrentTheme("org.jvnet.substance.theme.SubstanceTerracottaTheme"); //  SubstanceSunsetTheme"); // Se aplica el tema Aqui de Substance
            //SubstanceLookAndFeel.setCurrentTheme("org.jvnet.substance.theme.SubstanceSunsetTheme"); // Se aplica el tema Aqui de Substance
           SubstanceLookAndFeel.setCurrentWatermark("org.jvnet.substance.watermark.SubstanceBubblesWatermark"); // SubstanceBinaryWatermark");//Ejemplo de aplicacion de un watermark de Substance
            //SubstanceLookAndFeel.setCurrentWatermark( new SubstanceImageWatermark("/VITALMarket/Recursos/VITALMarket.jpg"));
            //SubstanceLookAndFeel.setImageWatermarkOpacity(new Float(0.3)); //valor aproximado de la opacidad por default de imageWatermark, (0..1)
        } catch (Exception ex) {
            //ex.printStackTrace();
            InfoErrores.errores(ex);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exemp) {
                exemp.printStackTrace();
                InfoErrores.errores(exemp);
            }
        }
    }
}
