/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Andres
 */
public class InfoErrores {
    
    /**
     * Creates a new instance of InfoErrores
     */
    public static void errores(Exception e) {
        try {
            // Create a memory handler with a memory of 100 records
            // and dumps the records into the file my.log when a
            // some abitrary condition occurs
            String ip = ipAdress().replace(".", "");
            Date fechaHora = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(" dd-MM-yyyy hh-mm-ss a");
            String fecha = sdf.format(fechaHora);
            String pattern = "/ATOMSystemsMain/logs/" + ip + " " + fecha + ".log";
            //int limit = 1000000; // 1 Mb
            //int numLogFiles = 10;
            LogRecord log, log1 = null;
            Logger logger = Logger.getLogger(InfoErrores.class.getClasses().toString());
            FileHandler fhandler = new FileHandler(pattern, true);
            fhandler.setFormatter(new SimpleFormatter());
            log1 = new LogRecord(Level.OFF, e.getMessage());
            fhandler.publish(log1);
            StackTraceElement elements[] = e.getStackTrace();
            for (int i = 0, n = elements.length; i < n; i++) {
                log = new LogRecord(Level.ALL, elements[i] + "ok");
                fhandler.publish(log);
            }
            int numRec = 100000;
            MemoryHandler mhandler = new MemoryHandler(fhandler, numRec, Level.ALL) {

                public synchronized void publish(LogRecord e) {
                    // Log it before checking the condition
                    super.publish(e);

                    boolean condition = false;
                    if (condition) {
                        // Condition occurred so dump buffered records
                        push();
                    }
                }
            };
            // Add to the desired logger
            logger.addHandler(mhandler);
            mhandler.close();
            fhandler.close();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String ipAdress() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return ip;
    }
    
}
