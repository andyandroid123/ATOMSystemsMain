/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

/**
 *
 * @author Andres
 */
public class StructuraLocales {
    
    private String locales, ips, servidor, baseDato, defaults;

    /**
     * Creates a new instance of StructuraLocales
     */
    public StructuraLocales(String _locales, String _ips, String _servidor, String _baseDato, String _defaults) {
        this.locales = _locales;
        this.ips = _ips;
        this.servidor = _servidor;
        this.defaults = _defaults;
        this.baseDato = _baseDato;
    }

    public String getLocales() {
        return locales;
    }

    public void setLocales(String locales) {
        this.locales = locales;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getDefaults() {
        return defaults;
    }

    public void setDefaults(String defaults) {
        this.defaults = defaults;
    }

    public int getBaseDato() {
        return Integer.valueOf(baseDato);
    }

    public void setBaseDato(int baseDato) {
        this.baseDato = String.valueOf(baseDato);
    }

    @Override
    public String toString() {
        return this.getLocales();
    }
}