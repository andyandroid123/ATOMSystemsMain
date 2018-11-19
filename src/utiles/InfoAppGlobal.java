package utiles;

//package appIncio;
/*
 * InfoAppGlobal.java
 *
 * Created on 12 de agosto de 2007, 7:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author nestor
 */
public class InfoAppGlobal {
    static String UserDb,PasswdDb,NameDb,HostDb;
    static String UserNameApp,UserGroupApp,UserReal;
    static String varTGrid;
    static String CodArt;
    static String num_param_recibo;
    
    /** Creates a new instance of InfoAppGlobal */
    public InfoAppGlobal() {
    }

    public static String getUserDb() {
        return UserDb;
    }

    public static void setUserDb(String aUserDb) {
        UserDb = aUserDb;
    }

    public static String getPasswdDb() {
        return PasswdDb;
    }

    public static void setPasswdDb(String aPasswdDb) {
        PasswdDb = aPasswdDb;
    }

    public static String getNameDb() {
        return NameDb;
    }

    public static void setNameDb(String aNameDb) {
        NameDb = aNameDb;
    }

    public static String getHostDb() {
        return HostDb;
    }

    public static void setHostDb(String aHostDb) {
        HostDb = aHostDb;
    }

    public static String getUserNameApp() {
        return UserNameApp;
    }

    public static void setUserNameApp(String aUserNameApp) {
        UserNameApp = aUserNameApp;
    }

    public static String getUserGroupApp() {
        return UserGroupApp;
    }

    public static void setUserGroupApp(String aUserGroupApp) {
        UserGroupApp = aUserGroupApp;
    }

    public static String getUserReal() {
        return UserReal;
    }

    public static void setUserReal(String aUserReal) {
        UserReal = aUserReal;
    }

    public static String getVarTGrid() {
        return varTGrid;
    }

    public static void setVarTGrid(String aVarTGrid) {
        varTGrid = aVarTGrid;
    }

    public static String getCodArt() {
        return CodArt;
    }

    public static void setCodArt(String aCodArt) {
        CodArt = aCodArt;
    }    

    public static String setUserReal() {
        return null;
    }

    public static String getNum_param_recibo() {
        return num_param_recibo;
    }

    public static void setNum_param_recibo(String aNum_param_recibo) {
        num_param_recibo = aNum_param_recibo;
    }
}
