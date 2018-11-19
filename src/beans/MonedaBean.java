/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

/**
 *
 * @author Andres
 */
public class MonedaBean {
    private int codMoneda;
    private String fecVigencia;
    private String nombre;
    private String alias;
    private int cotCompra;
    private int cotVenta;
    private String vigente;
    private int codUsuario;
    private String operacion;
    private int decimales;
    private int baseRedondeo;

    /**
     * @return the codMoneda
     */
    public int getCodMoneda() {
        return codMoneda;
    }

    /**
     * @param codMoneda the codMoneda to set
     */
    public void setCodMoneda(int codMoneda) {
        this.codMoneda = codMoneda;
    }

    /**
     * @return the fecVigencia
     */
    public String getFecVigencia() {
        return fecVigencia;
    }

    /**
     * @param fecVigencia the fecVigencia to set
     */
    public void setFecVigencia(String fecVigencia) {
        this.fecVigencia = fecVigencia;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the cotCompra
     */
    public int getCotCompra() {
        return cotCompra;
    }

    /**
     * @param cotCompra the cotCompra to set
     */
    public void setCotCompra(int cotCompra) {
        this.cotCompra = cotCompra;
    }

    /**
     * @return the cotVenta
     */
    public int getCotVenta() {
        return cotVenta;
    }

    /**
     * @param cotVenta the cotVenta to set
     */
    public void setCotVenta(int cotVenta) {
        this.cotVenta = cotVenta;
    }

    /**
     * @return the vigente
     */
    public String getVigente() {
        return vigente;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(String vigente) {
        this.vigente = vigente;
    }

    /**
     * @return the codUsuario
     */
    public int getCodUsuario() {
        return codUsuario;
    }

    /**
     * @param codUsuario the codUsuario to set
     */
    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the decimales
     */
    public int getDecimales() {
        return decimales;
    }

    /**
     * @param decimales the decimales to set
     */
    public void setDecimales(int decimales) {
        this.decimales = decimales;
    }

    /**
     * @return the baseRedondeo
     */
    public int getBaseRedondeo() {
        return baseRedondeo;
    }

    /**
     * @param baseRedondeo the baseRedondeo to set
     */
    public void setBaseRedondeo(int baseRedondeo) {
        this.baseRedondeo = baseRedondeo;
    }
}
