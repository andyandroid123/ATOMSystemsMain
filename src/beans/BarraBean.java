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
public class BarraBean {
    private int codBarras;
    private int codArticulo;
    private int cansiVenta;
    private String siglaVenta;
    private int codUsuario;
    private String vigente;

    /**
     * @return the codBarras
     */
    public int getCodBarras() {
        return codBarras;
    }

    /**
     * @param codBarras the codBarras to set
     */
    public void setCodBarras(int codBarras) {
        this.codBarras = codBarras;
    }

    /**
     * @return the codArticulo
     */
    public int getCodArticulo() {
        return codArticulo;
    }

    /**
     * @param codArticulo the codArticulo to set
     */
    public void setCodArticulo(int codArticulo) {
        this.codArticulo = codArticulo;
    }

    /**
     * @return the cansiVenta
     */
    public int getCansiVenta() {
        return cansiVenta;
    }

    /**
     * @param cansiVenta the cansiVenta to set
     */
    public void setCansiVenta(int cansiVenta) {
        this.cansiVenta = cansiVenta;
    }

    /**
     * @return the siglaVenta
     */
    public String getSiglaVenta() {
        return siglaVenta;
    }

    /**
     * @param siglaVenta the siglaVenta to set
     */
    public void setSiglaVenta(String siglaVenta) {
        this.siglaVenta = siglaVenta;
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
}
