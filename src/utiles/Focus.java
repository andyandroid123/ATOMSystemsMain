package utiles;

 

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

  

public class Focus implements FocusListener{
    //es el color del campo para todo el sistema
    Color ElColor = new Color(255,255,102); // amarillo
    //Color ElColor = new Color(0,204,255); // celeste
    //Color ElColor = new Color(153,255,255); // celeste -- como el foco de la seleccion
    
    public Focus() {
    }

    public void focusGained(FocusEvent e) {                
        e.getComponent().setBackground(ElColor);
    }

    public void focusLost(FocusEvent e) {
        e.getComponent().setBackground(Color.white);
    }
    
}
