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
public class MarcaBean {
    
    private int codMarca;
    private String descripcion;
    private int codPais;
    private String fecVigencia;
    private int codUsuario;

    /**
     * @return the codMarca
     */
    public int getCodMarca() {
        return codMarca;
    }

    /**
     * @param codMarca the codMarca to set
     */
    public void setCodMarca(int codMarca) {
        this.codMarca = codMarca;
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
     * @return the codPais
     */
    public int getCodPais() {
        return codPais;
    }

    /**
     * @param codPais the codPais to set
     */
    public void setCodPais(int codPais) {
        this.codPais = codPais;
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
