/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utiles;

import controls.EmpleadoCtrl;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
/**
 * @author Claudio Kunnen
 * @improved by Andres Melgarejo
 */

public class Utiles {
    
    public static double redondea(double numero, int decimales) {
        return Math.round(numero * Math.pow(10, decimales)) / Math.pow(10, decimales);
    }

    public static boolean isIntegerValid(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isLongValid(String cadena) {
        try {
            Long.parseLong(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isFloatValid(String cadena) {
        try {
            Float.parseFloat(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isDoubleValid(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean esFechaValida(String dataStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false) ; // o modo que vai ver se os numeros ultrapassam o permitido!
        try {
            Date data = sdf.parse(dataStr);
        } catch(ParseException e){
            InfoErrores.errores(e);
            return false;
        }
        return true;
    }
    
    
    public static boolean esSiglaValida(String aliasSig) {
        if (getSiglas("sigla", aliasSig).equals("ERROR")) {
            return false;
        } else {
            return true;
        }
    }
    
    private static String getSiglas(String colResult, String aliasSigla) {
        String result = DBManager.ejecutarConsulta("sigla", "sigla", "sigla", aliasSigla);
        return result;
    }
    
    public static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            InfoErrores.errores(nfe);
            return false;
        }
    }
    
    public static String getSysDateTimeString() {
        String fechaHora = null;
        StatementManager sm = new StatementManager();

        sm.TheSql = "SELECT to_char(now(), 'DD/MM/YYYY HH12:MI:SS AM')";
        //sm.TheSql = "SELECT to_char(now(), 'DD/MM/YYYY')";

        sm.EjecutarSql();
        try {
            if (sm.TheResultSet.next()) {
                fechaHora = sm.TheResultSet.getString(1);
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }
        sm.CerrarStatement();
        sm = null;
        return fechaHora;
    }

    public static void punteroTablaF(JTable jTableF, JFrame jFrame) {
        Set forwardKeys = new HashSet();
        forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        jFrame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        InputMap im = jTableF.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        KeyStroke f5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);

        im.put(enter, im.get(tab));
        im.put(right, im.get(tab));
        im.put(f5, im.get(tab));

        final Action oldTabAction = jTableF.getActionMap().get(im.get(tab));

        Action tabAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                oldTabAction.actionPerformed(e);
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                int originalRow = row;
                int column = table.getSelectedColumn();
                int originalColumn = column;

                while (!table.isCellEditable(row, column)) {
                    oldTabAction.actionPerformed(e);
                    row = table.getSelectedRow();
                    column = table.getSelectedColumn();

                    //  Back to where we started, get out.

                    if (row == originalRow && column == originalColumn) {
                        break;
                    }
                }
            }
        };
        jTableF.getActionMap().put(im.get(tab), tabAction);
    }

    public static void punteroTablaF(JTable jTableF, JDialog jFrame) {
        Set forwardKeys = new HashSet();
        forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        jFrame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        InputMap im = jTableF.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        KeyStroke f5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);

        im.put(enter, im.get(tab));
        im.put(right, im.get(tab));
        im.put(f5, im.get(tab));

        final Action oldTabAction = jTableF.getActionMap().get(im.get(tab));

        Action tabAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                oldTabAction.actionPerformed(e);
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                int originalRow = row;
                int column = table.getSelectedColumn();
                int originalColumn = column;

                while (!table.isCellEditable(row, column)) {
                    oldTabAction.actionPerformed(e);
                    row = table.getSelectedRow();
                    column = table.getSelectedColumn();

                    //  Back to where we started, get out.

                    if (row == originalRow && column == originalColumn) {
                        break;
                    }
                }
            }
        };
        jTableF.getActionMap().put(im.get(tab), tabAction);
    }

    public static java.util.Date formataStringToDate(String pFecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date vDatetoSend = null;
        try {
            vDatetoSend = sdf.parse(pFecha);
        } catch (Exception es) {
            es.printStackTrace();
            InfoErrores.errores(es);
        }
        return vDatetoSend;
    }
     
    public static Timestamp getSysDateTimeStamp() {
        Timestamp fechaHora = null;
        StatementManager sm = new StatementManager();
        //sm.TheSql = "SELECT sysdate FROM dual";
        sm.TheSql = "SELECT to_char(now(), 'DD/MM/YYYY hh:mm:ss') AS sysdate";
        sm.EjecutarSql();
        try {
            if (sm.TheResultSet.next()) {
                fechaHora = sm.TheResultSet.getTimestamp("sysdate");
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }
        sm.CerrarStatement();
        sm.ClearDBManagerstmt();
        sm = null;
        return fechaHora;
    }
    
    public static String getSysDateTimeStampString() {
        String fechaHora = "";
        StatementManager sm = new StatementManager();
        //sm.TheSql = "SELECT sysdate FROM dual";
        sm.TheSql = "SELECT to_char(now(), 'DD/MM/YYYY HH12:MI:SS AM')";
        sm.EjecutarSql();
        try {
            if (sm.TheResultSet.next()) {
                fechaHora = sm.TheResultSet.getString(1);
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }
        sm.CerrarStatement();
        sm.ClearDBManagerstmt();
        sm = null;
        return fechaHora;
    }
    
    /*
    Funcion que te devuelve los dias que tiene el mes dado en el año especificado (necesario solo para el mes de Febrero).
    Los meses se pasan como un entero, siendo el 0 correspondiente a Enero, y el 11 correspondiente a Diciembre.
     */
    public static int diasDelMes(int mes, int ano) {
        mes = mes -1;
        switch (mes) {
            case 0:  // Enero
            case 2:  // Marzo
            case 4:  // Mayo
            case 6:  // Julio
            case 7:  // Agosto
            case 9:  // Octubre
            case 11: // Diciembre
                return 31;
                //break;
            case 3:  // Abril
            case 5:  // Junio
            case 8:  // Septiembre
            case 10: // Noviembre
                return 30;
                //break;
            case 1:  // Febrero
                if (((ano % 100 == 0) && (ano % 400 == 0)) ||
                        ((ano % 100 != 0) && (ano % 4 == 0))) {
                    return 29;  // Año Bisiesto
                } else {
                    return 28;
                }
                //break;
            default:
                throw new java.lang.IllegalArgumentException("El mes debe estar entre 1 y 12");
        }
    }

    /*public static String formataNroComprob(long nroComprob) {
        String aux = String.valueOf(nroComprob).trim();
        String nroCom = padLefth(13, "0", aux);

        String str1 = nroCom.substring(0, 3);
        String str2 = nroCom.substring(3, 6);
        String str3 = nroCom.substring(6, 13);

        return str1 + "-" + str2 + "-" + str3;
    }*/

    public static String space(int cant) {
        String result = "";
        for (int j = 0; j < cant; j++) {
            result = result + " ";
        }
        return result;
    }

    public static String padRight(int indice, String caracter, String cadena) {
        String tmp = "", result = "";
        if (cadena.length() >= indice) {
            result = cadena.substring(0, indice);
        } else {
            int falta = indice - cadena.length();
            for (int j = 0; j < falta; j++) {
                tmp = tmp + caracter;
            }
            result = cadena + tmp;
        }
        return result;
    }

    public static String padLeft(int indice, String caracter, String cadena) {
        String result="", tmp="";
        if (!cadena.equalsIgnoreCase("null")) {
            if (cadena.length() >= indice) {
                result = cadena.substring(0, indice);
            } else {
                int falta = indice - cadena.length();
                for (int j = 0; j < falta; j++) {
                    tmp = caracter + tmp;
                }
                result = tmp + cadena;
            }
        }
        return result;
    }

    public static String formataNroComprob(String nroComprob) { // al imputar via teclado
        String sinPunto = nroComprob.replace(".", "");
        String str1 = "";
        String str2 = "";
        String str3 = "";

        int tam = sinPunto.length();

        if (tam > 0 && tam < 4) {
            nroComprob = padLefth(3, "0", sinPunto);
        } else {
            if (tam > 3 && tam < 7) {
                str1 = sinPunto.substring(0, 3);
                str2 = sinPunto;
                str2 = sinPunto.substring(3, tam);
                nroComprob = str1 + "-" + padLefth(3, "0", str2);
            } else {
                if (tam > 7) {
                    tam  = sinPunto.length();
                    str1 = sinPunto.substring(0, 3);
                    str2 = sinPunto.substring(4, 7);
                    str3 = sinPunto.substring(7, tam);
                    nroComprob = str1 + "-" + str2 + "-" + padLefth(7, "0", str3);
                }
            }
        }
        return nroComprob;
    }

    public static String formataNroComprob(long nroComprob) {  // al recuperar de la BD
        String nroCompr = String.valueOf(nroComprob);
        String sinPunto = String.valueOf(nroComprob);
        String str1 = "";
        String str2 = "";
        String str3 = "";

        int tam = sinPunto.length();

        if (tam > 0 && tam <= 7) {
            nroCompr = padLefth(7, "0", sinPunto);
        } else {  // tam > 7
            nroCompr = padLefth(13, "0", sinPunto);
            str1 = nroCompr.substring(0, 3);
            str2 = nroCompr.substring(3, 6);
            str3 = nroCompr.substring(6,13);
            nroCompr = str1 + "-" + str2 + "-" + str3;
        }
        return nroCompr;
    }
    
    public static String padLefth(int indice, String caracter, String cadena) {
        String tmp = "", result = "";
        if (cadena.length() >= indice) {
            result = cadena.substring(0, indice);
        } else {
            int falta = indice - cadena.length();
            for (int j = 0; j < falta; j++) {
                tmp = tmp + caracter;
            }
            result = tmp + cadena;
        }
        return result;
    }

    public static int getCantidadListaPrecio() {
        int result = 0;
        StatementManager sm = new StatementManager();
        sm.TheSql = "SELECT count(*) from listaprecio";
        sm.EjecutarSql();
        try {
            if (sm.TheResultSet.next()) {
                result = sm.TheResultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        sm.CerrarStatement();
        sm = null;
        return result;
    }
    
    public static String getCodEmpresaDefault() {
        String result = "ERROR";
        StatementManager TheQuery = new StatementManager();
        try {
            TheQuery.TheSql = "SELECT Empresa.COD_EMPRESA FROM Empresa where Empresa.ES_EMPRESADEFAULT='S'";
            TheQuery.EjecutarSql();
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("COD_EMPRESA");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
        return result;
    }
    
    public static String getCodSectorDefault(String codLoc) {
        // retorna el codigo del SECTOR default del LOCAL pasado como parametro
        String result = "ERROR";
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT local.cod_sector_default FROM local WHERE local.cod_local = " + codLoc;
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("cod_sector_default");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        //TheQuery.ClearDBManagerstmt();
        TheQuery = null;
        return result;
    }
    
    public static String getCodLocalDefault(String codEmp) {
        // retorna el codigo del local default de la empresa pasada como parametro
        String result = "ERROR";
        StatementManager TheQuery = new StatementManager();
        try {
            TheQuery.TheSql = "SELECT local.cod_local FROM local WHERE local.cod_empresa = " + codEmp + " AND local.es_localdefault='S'";
            TheQuery.EjecutarSql();
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("cod_local");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
        return result;
    }
    
    public static void cargaComboEmpresas(JComboBox box) {
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT cod_empresa FROM empresa WHERE activa = 'S' ORDER BY cod_empresa";
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                try {
                    box.addItem(TheQuery.TheResultSet.getString("cod_empresa"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
    }

    public static void cargaComboLocales(JComboBox box) {
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT cod_local FROM local WHERE activo = 'S' ORDER BY cod_local";
        TheQuery.EjecutarSql();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {
                    box.addItem(TheQuery.TheResultSet.getString("cod_local"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
    }

    public static void cargaComboSectores(JComboBox box) {
        box.removeAllItems();
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT cod_sector FROM sector WHERE activo = 'S' ORDER BY cod_sector";
        TheQuery.EjecutarSql();
        try {
            while (TheQuery.TheResultSet.next()) {
                try {
                    box.addItem(TheQuery.TheResultSet.getString("cod_sector"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
    }
    
    public static void cargaComboCentroCosto(JComboBox box){
        box.removeAllItems();
        StatementManager TheQuery = new StatementManager();
        TheQuery.TheSql = "SELECT cod_centrocosto FROM centrocosto WHERE activo = 'S' ORDER BY cod_centrocosto";
        TheQuery.EjecutarSql();
        try{
            while (TheQuery.TheResultSet.next()) {
                try {
                    box.addItem(TheQuery.TheResultSet.getString("cod_centrocosto"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }finally{
            TheQuery.CerrarStatement();
            TheQuery = null;
        }
    }
    
    public static String getRazonSocialEmpresa(String vp_codEmp) {
        StatementManager TheQuery = new StatementManager();
        String result = "ERROR";
        String sql = "SELECT Empresa.Descripcion FROM Empresa WHERE cod_empresa = " + vp_codEmp;
        TheQuery.TheSql = sql;
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("Descripcion");
            }
            // resLocal.close();
            // resLocal = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        //TheQuery.ClearDBManagerstmt();
        TheQuery = null;
        return result;
    }
    
    public static String getDescripcionLocal(String vp_codLoc) {
        // retorna el codigo del local default de la empresa pasada como parametro
        //  ResultSet R_LocDescripcion = null;
        StatementManager TheQuery = new StatementManager();
        String result = "ERROR";
        TheQuery.TheSql = "SELECT Local.Descripcion FROM local WHERE local.cod_local = " + vp_codLoc;
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("Descripcion");
            }
            //   R_LocDescripcion.close();
            //    R_LocDescripcion = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        //TheQuery.ClearDBManagerstmt();
        TheQuery = null;
        return result;
    }

    public static String getSectorDescripcion(String vp_codLoc, String vp_CodSector) {
        StatementManager TheQuery = new StatementManager();
        String result = "ERROR";
        TheQuery.TheSql = "SELECT Descripcion "
                + " FROM Sector WHERE cod_local = " + vp_codLoc
                + "  and Cod_Sector = " + vp_CodSector;
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("Descripcion");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
        return result;
    }
    
    public static String getCentroCostoDescripcion(String vp_codLoc, String vp_CodSector) {
        StatementManager TheQuery = new StatementManager();
        String result = "ERROR";
        TheQuery.TheSql = "SELECT nom_centrocosto "
                + " FROM centrocosto WHERE cod_local = " + vp_codLoc
                + "  and Cod_Sector = " + vp_CodSector + " AND activo = 'S'";
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("nom_centrocosto");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
        return result;
    }
    
    public static String getInfoListaPrecio(String colResult, String codLista) {
        String result = "ERROR";
        StatementManager sm = new StatementManager();
        sm.TheSql = "SELECT * FROM listaPrecio WHERE cod_lista = " + codLista;
        sm.EjecutarSql();
        try {
            if (sm.TheResultSet.next()) {
                result = sm.TheResultSet.getString(colResult);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        sm.CerrarStatement();
        sm = null;
        return result;
    }
    
    public static String getCodListaCosto(String vp_codLoc, String vp_CodSector) {
        StatementManager TheQuery = new StatementManager();
        String result = "ERROR";
        TheQuery.TheSql = "SELECT COD_LISTA_COSTO FROM Sector "
                + " WHERE cod_local = " + vp_codLoc
                + "  and Cod_Sector = " + vp_CodSector;
        TheQuery.EjecutarSql();
        try {
            if (TheQuery.TheResultSet.next()) {
                result = TheQuery.TheResultSet.getString("COD_LISTA_COSTO");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        }
        TheQuery.CerrarStatement();
        TheQuery = null;
        return result;
    }
    
    public static String getIPAdress() {
        String ip = "01";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "01";
        }
        return ip;
    }
    
    public static String getFecInicalPeriodoRRHH(String codEmpresa, String codLocal) {
        ResultSet resultPeriodo = null;
        String speriodo = null;
        resultPeriodo = DBManager.ejecutarDSL("SELECT to_char(fechainicial, 'DD/MM/YYYY') fechainicial "
                + " FROM PERIODO_RRHH "
                + " WHERE vigente = 'S' "
                + "   AND cerrado    = 0 "
                + "   and cod_empresa= " + codEmpresa
                + "   and cod_local  = " + codLocal);

        if (resultPeriodo != null) {
            try {
                resultPeriodo.next();
                speriodo = resultPeriodo.getString("fechainicial");
            } catch (SQLException sqlex) {
            } finally {
                DBManager.CerrarStatements();
            }
        }
        return speriodo;
    }
    
    public static String getFecFinalPeriodoRRHH(String codEmpresa, String codLocal) {
        ResultSet resultPeriodo = null;
        String speriodo = null;
        resultPeriodo = DBManager.ejecutarDSL("SELECT to_char(fechafinal, 'DD/MM/YYYY') fechafinal FROM PERIODO_RRHH " +
                                              "WHERE vigente = 'S' AND cerrado = 0 and cod_empresa = " + codEmpresa + " and cod_local = " + codLocal);

        if (resultPeriodo != null) {
            try {
                resultPeriodo.next();
                speriodo = resultPeriodo.getString("fechafinal");
            } catch (SQLException sqlex) {
            } finally {
                DBManager.CerrarStatements();
            }
        }
        return speriodo;
    }
    
    public static String getPeriodoRRHH(String codEmpresa, String codLocal) {
        ResultSet resultPeriodo = null;
        String speriodo = null;
        resultPeriodo = DBManager.ejecutarDSL("SELECT periodo FROM PERIODO_RRHH WHERE vigente = 'S' AND cerrado = 0 " +
                                              "and cod_empresa = " + codEmpresa + " and cod_local = " + codLocal);

        if (resultPeriodo != null) {
            try {
                resultPeriodo.next();
                speriodo = resultPeriodo.getString("periodo");

            } catch (SQLException sqlex) {
            } finally {
                DBManager.CerrarStatements();
            }
        }
        return speriodo;
    }
    
    public static String getSiguientePeriodoRRHH(String per) {
        ResultSet resultFecha = null;
        String sdias = null;
        String speriodo = per;
        String anhio = null;
        String smes;
        anhio = speriodo.substring(speriodo.length() - 4);
        int mes = Integer.parseInt(speriodo.substring(0, speriodo.length() - 4));

        if (mes < 12) {
            mes = mes + 1;
        } else {
            mes = 1;
        }

        smes = String.valueOf(mes);

        if (mes == 1) {
            int ano = Integer.parseInt(anhio);
            ano = ano + 1;
            anhio = String.valueOf(ano);
        }
        speriodo = smes + anhio;
        return speriodo;
    }
    
    public static String getDiasLaboralesRRHH(String codEmpresa, String codLocal) {
        ResultSet resultPeriodo = null;
        String sdias = null;
        resultPeriodo = DBManager.ejecutarDSL("SELECT to_char(diaslaborales,'99') dias  FROM PERIODO_RRHH WHERE vigente = 'S' " +
                                              "AND cerrado = 0 and cod_empresa = " + codEmpresa + " and cod_local = " + codLocal);

        if (resultPeriodo != null) {
            try {
                resultPeriodo.next();
                sdias = resultPeriodo.getString("dias");

            } catch (SQLException sqlex) {
            } finally {
                DBManager.CerrarStatements();
            }
        }
        return sdias;
    }
    
    public static String getIDPeriodoRRHH() {
        String speriodo = null;
        try {
            ResultSet resultPeriodo = DBManager.ejecutarDSL("SELECT idPeriodo "
                    + " FROM PERIODO_RRHH "
                    + " WHERE vigente     ='S'"
                    + "   AND cerrado     = 0 "
                    + "   and cod_empresa = " + getCodEmpresaDefault()
                    + "   and cod_local   = " + getCodLocalDefault(getCodEmpresaDefault()));
            if (resultPeriodo != null) {
                if (resultPeriodo.next()) {
                    speriodo = resultPeriodo.getString("idPeriodo");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
        } finally {
            DBManager.CerrarStatements();
        }
        return speriodo;
    }
    
    
    public static int getCodCentroCosto(int codSeccion) {
        int cc = 0;
        StatementManager sm = new StatementManager();
        try {
            sm.TheSql = "SELECT cod_centrocosto FROM seccion WHERE cod_seccion=" + codSeccion;
            sm.EjecutarSql();
            if (sm.TheResultSet.next()) {
                cc = sm.TheResultSet.getInt("cod_centrocosto");
            }
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        } finally {
            sm.CerrarStatement();
            sm = null;
        }
        return cc;
    }
    
    public static boolean esMultiEmpresa(){
        boolean result = false;
        String esMulti = "";
        StatementManager sm = new StatementManager();
        try{
            sm.TheSql = "SELECT valor::character FROM parametros WHERE parametro = 'MAIN_ES_MULTI_EMPRESA'";
            sm.EjecutarSql();
            if(sm.TheResultSet.next()){
                esMulti = sm.TheResultSet.getString("valor");
            }
            if(esMulti.equals("S")){
                result = true;
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }finally{
            sm.CerrarStatement();
            sm = null;
        }
        return result;
    }
    
    public static boolean esMultiLocal(){
        boolean result = false;
        String esMulti = "";
        StatementManager sm = new StatementManager();
        try{
            sm.TheSql = "SELECT valor::character FROM parametros WHERE parametro = 'MAIN_ES_MULTI_LOCAL'";
            sm.EjecutarSql();
            if(sm.TheResultSet.next()){
                esMulti = sm.TheResultSet.getString("valor");
            }
            if(esMulti.equals("S")){
                result = true;
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }finally{
            sm.CerrarStatement();
            sm = null;
        }
        return result;
    }
    
    public static String getNombreEmpleado(String codigo) {
        String result = "";
        if (Utiles.isIntegerValid(codigo)) {
            EmpleadoCtrl ctrl = new EmpleadoCtrl();
            result = ctrl.getNombreEmpleado(Integer.valueOf(codigo));
            ctrl = null;
        }
        return result;
    }
}

