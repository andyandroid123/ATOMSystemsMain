/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author Andres
 */
public class GestionImagen {
    
    FileInputStream entrada;
    FileOutputStream salida;
    File imagen;
    
    public GestionImagen(){}
    
    // Abrir imagen
    public byte[] AbrirImagen(File archivo){
        Long imagen = archivo.length();
        Integer tam = imagen.intValue();
        byte[] bytesImage = new byte[tam];
        try{
            entrada = new FileInputStream(archivo);
            entrada.read(bytesImage);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return bytesImage;
    }
    
    // Guardar imagen 
    public String guardarImagen(File archivo, byte[] bytesImg){
        String respuesta = null;
        try{
            salida = new FileOutputStream(archivo);
            salida.write(bytesImg);
            respuesta = "La imagen se guardó con éxito!";
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return respuesta;
    }
}
