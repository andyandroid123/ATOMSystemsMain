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
public class CargoBean {
    
    private int codCargo;
    private String descripcion;
    private String fecVigencia;
    private int codUsuario;
    private String esOcupacion;
    private int codEmpresa;

    /**
     * @return the codCargo
     */
    public int getCodCargo() {
        return codCargo;
    }

    /**
     * @param codCargo the codCargo to set
     */
    public void setCodCargo(int codCargo) {
        this.codCargo = codCargo;
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
     * @return the esOcupacion
     */
    public String getEsOcupacion() {
        return esOcupacion;
    }

    /**
     * @param esOcupacion the esOcupacion to set
     */
    public void setEsOcupacion(String esOcupacion) {
        this.esOcupacion = esOcupacion;
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
    
}
