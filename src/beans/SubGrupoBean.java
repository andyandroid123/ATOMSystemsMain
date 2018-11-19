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
public class SubGrupoBean {
    
    private int codSubGrupo;
    private String descripcion;
    private int codGrupo;
    private double margenBaseVenta;
    private String fecVigencia;
    private int codUsuario;
    private double margenTransferencia;
    private String esContabilizado;
    private double descTransferencia;

    /**
     * @return the codSubGrupo
     */
    public int getCodSubGrupo() {
        return codSubGrupo;
    }

    /**
     * @param codSubGrupo the codSubGrupo to set
     */
    public void setCodSubGrupo(int codSubGrupo) {
        this.codSubGrupo = codSubGrupo;
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
     * @return the margenTransferencia
     */
    public double getMargenTransferencia() {
        return margenTransferencia;
    }

    /**
     * @param margenTransferencia the margenTransferencia to set
     */
    public void setMargenTransferencia(double margenTransferencia) {
        this.margenTransferencia = margenTransferencia;
    }

    /**
     * @return the esContabilizado
     */
    public String getEsContabilizado() {
        return esContabilizado;
    }

    /**
     * @param esContabilizado the esContabilizado to set
     */
    public void setEsContabilizado(String esContabilizado) {
        this.esContabilizado = esContabilizado;
    }

    /**
     * @return the descTransferencia
     */
    public double getDescTransferencia() {
        return descTransferencia;
    }

    /**
     * @param descTransferencia the descTransferencia to set
     */
    public void setDescTransferencia(double descTransferencia) {
        this.descTransferencia = descTransferencia;
    }
    
}
