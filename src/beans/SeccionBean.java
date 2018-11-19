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
public class SeccionBean {
    
    private int codSeccion;
    private String descripcion;
    private int codCentroCosto;
    private String activo;
    private String fecVigencia;
    private int codUsuario;
    private int codEmpresa;
    private int cargaHoraria;

    /**
     * @return the codSeccion
     */
    public int getCodSeccion() {
        return codSeccion;
    }

    /**
     * @param codSeccion the codSeccion to set
     */
    public void setCodSeccion(int codSeccion) {
        this.codSeccion = codSeccion;
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
     * @return the activo
     */
    public String getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(String activo) {
        this.activo = activo;
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
     * @return the codEmpresa
     */
    public int getCodEmpresa() {
        return codEmpresa;
    }

    /**
     * @param codEmpresa the codEmpresa to set
     */
    public void setCodEmpresa(int codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    /**
     * @return the cargaHoraria
     */
    public int getCargaHoraria() {
        return cargaHoraria;
    }

    /**
     * @param cargaHoraria the cargaHoraria to set
     */
    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }
    
}
