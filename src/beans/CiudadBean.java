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
public class CiudadBean {
    
    private int codCiudad;
    private String nombre;
    private int codDepartamento;
    private String fecVigencia;
    private int codUsuario;

    /**
     * @return the codCiudad
     */
    public int getCodCiudad() {
        return codCiudad;
    }

    /**
     * @param codCiudad the codCiudad to set
     */
    public void setCodCiudad(int codCiudad) {
        this.codCiudad = codCiudad;
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
     * @return the codDepartamento
     */
    public int getCodDepartamento() {
        return codDepartamento;
    }

    /**
     * @param codDepartamento the codDepartamento to set
     */
    public void setCodDepartamento(int codDepartamento) {
        this.codDepartamento = codDepartamento;
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
    
}
