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
public class TipoComprobanteBean {
    
    private int codComprobante;
    private String descripcion;
    private String tipComprobante;
    private String tipOperacion;
    private int afecta;
    private String fecVigencia;
    private String esLegal;
    private int codComprobanteFiscal;

    /**
     * @return the codComprobante
     */
    public int getCodComprobante() {
        return codComprobante;
    }

    /**
     * @param codComprobante the codComprobante to set
     */
    public void setCodComprobante(int codComprobante) {
        this.codComprobante = codComprobante;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the tipComprobante
     */
    public String getTipComprobante() {
        return tipComprobante;
    }

    /**
     * @param tipComprobante the tipComprobante to set
     */
    public void setTipComprobante(String tipComprobante) {
        this.tipComprobante = tipComprobante;
    }

    /**
     * @return the tipOperacion
     */
    public String getTipOperacion() {
        return tipOperacion;
    }

    /**
     * @param tipOperacion the tipOperacion to set
     */
    public void setTipOperacion(String tipOperacion) {
        this.tipOperacion = tipOperacion;
    }

    /**
     * @return the afecta
     */
    public int getAfecta() {
        return afecta;
    }

    /**
     * @param afecta the afecta to set
     */
    public void setAfecta(int afecta) {
        this.afecta = afecta;
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
     * @return the esLegal
     */
    public String getEsLegal() {
        return esLegal;
    }

    /**
     * @param esLegal the esLegal to set
     */
    public void setEsLegal(String esLegal) {
        this.esLegal = esLegal;
    }

    /**
     * @return the codComprobanteFiscal
     */
    public int getCodComprobanteFiscal() {
        return codComprobanteFiscal;
    }

    /**
     * @param codComprobanteFiscal the codComprobanteFiscal to set
     */
    public void setCodComprobanteFiscal(int codComprobanteFiscal) {
        this.codComprobanteFiscal = codComprobanteFiscal;
    }
    
}
