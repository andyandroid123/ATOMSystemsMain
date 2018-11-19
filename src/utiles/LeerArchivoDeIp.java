/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Andres
 */
public class LeerArchivoDeIp {
    
    private ArrayList<StructuraLocales> fuente;
    
    public LeerArchivoDeIp() throws Exception{
        String fileName = "/ATOMSystemsMain/conexion.dat";
        String delimitador = ",";
        CsvReader csvRead = null;
        try{
            File fichero = new File(fileName);
            FileReader freader = new FileReader(fichero);
            csvRead = new CsvReader(freader, delimitador.charAt(0));
            String header[] = null;
            if(csvRead.readHeaders()){
                header = csvRead.getHeaders();
                for (int i = 0; i < header.length; i++) {
                    System.out.println(header[i]);
                }
            }
            this.fuente = new ArrayList();
            while(csvRead.readRecord()){
                String local = csvRead.get(header[0]);
                String ip = csvRead.get(header[1]);
                String servidor = csvRead.get(header[2]);
                String baseDato = csvRead.get(header[3]);
                String defaults = csvRead.get(header[4]);
                getFuente().add(new StructuraLocales(local, ip, servidor, baseDato, defaults));
            }
        } catch(Exception e) {
            throw e;
        } finally {
            if (csvRead != null) {
                csvRead.close();
            }
        }
    }

    public ArrayList<StructuraLocales> getFuente(){
        return fuente;
    }
}
