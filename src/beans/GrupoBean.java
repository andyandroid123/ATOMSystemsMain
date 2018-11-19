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
public class GrupoBean {
    
    private int codGrupo;
    private String descripcion;
    private int codCentroCosto;
    private int codRubro;
    private String fecVigencia;
    private int codUsuario;
    private String esMateriaPrima;
    private double margenBaseVenta;

    /**
     * @return the codGrupo
     */
    public int getCodGrupo() {
        return codGrupo;
    }

    /**
     * @param codGrupo the codGrupo to set
     */
    public void setCodGrupo(int codGrupo) {
        this.codGrupo = codGrupo;
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
     * @return the codCentroCosto
     */
    public int getCodCentroCosto() {
        return codCentroCosto;
    }

    /**
     * @param codCentroCosto the codCentroCosto to set
     */
    public void setCodCentroCosto(int codCentroCosto) {
        this.codCentroCosto = codCentroCosto;
    }

    /**
     * @return the codRubro
     */
    public int getCodRubro() {
        return codRubro;
    }

    /**
     * @param codRubro the codRubro to set
     */
    public void setCodRubro(int codRubro) {
        this.codRubro = codRubro;
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
     * @return the esMateriaPrima
     */
    public String getEsMateriaPrima() {
        return esMateriaPrima;
    }

    /**
     * @param esMateriaPrima the esMateriaPrima to set
     */
    public void setEsMateriaPrima(String esMateriaPrima) {
        this.esMateriaPrima = esMateriaPrima;
    }

    /**
     * @return the margenBaseVenta
     */
    public double getMargenBaseVenta() {
        return margenBaseVenta;
    }

    /**
     * @param margenBaseVenta the margenBaseVenta to set
     */
    public void setMargenBaseVenta(double margenBaseVenta) {
        this.margenBaseVenta = margenBaseVenta;
    }
    
}
