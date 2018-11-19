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
public class UsuarioBean {
    
    private int codUsuario;
    private String nombre;
    private String clave;
    private String alias;
    private String esCajero;
    private String esFiscal;
    private String activo;
    private String fecCatastro;
    private String fecVigencia;
    private int codPerfil;
    private int codigoUsuario; // usuario logueado - el que realiza la operación
    private int codGrupoUsuario;
    private String fecVenceClave;
    private String clavePlana;
    private String localesHabilitados;

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
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
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
     * @return the esCajero
     */
    public String getEsCajero() {
        return esCajero;
    }

    /**
     * @param esCajero the esCajero to set
     */
    public void setEsCajero(String esCajero) {
        this.esCajero = esCajero;
    }

    /**
     * @return the esFiscal
     */
    public String getEsFiscal() {
        return esFiscal;
    }

    /**
     * @param esFiscal the esFiscal to set
     */
    public void setEsFiscal(String esFiscal) {
        this.esFiscal = esFiscal;
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
     * @return the fecCatastro
     */
    public String getFecCatastro() {
        return fecCatastro;
    }

    /**
     * @param fecCatastro the fecCatastro to set
     */
    public void setFecCatastro(String fecCatastro) {
        this.fecCatastro = fecCatastro;
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
     * @return the codPerfil
     */
    public int getCodPerfil() {
        return codPerfil;
    }

    /**
     * @param codPerfil the codPerfil to set
     */
    public void setCodPerfil(int codPerfil) {
        this.codPerfil = codPerfil;
    }

    /**
     * @return the codigoUsuario
     */
    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    /**
     * @param codigoUsuario the codigoUsuario to set
     */
    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    /**
     * @return the codGrupoUsuario
     */
    public int getCodGrupoUsuario() {
        return codGrupoUsuario;
    }

    /**
     * @param codGrupoUsuario the codGrupoUsuario to set
     */
    public void setCodGrupoUsuario(int codGrupoUsuario) {
        this.codGrupoUsuario = codGrupoUsuario;
    }

    /**
     * @return the fecVenceClave
     */
    public String getFecVenceClave() {
        return fecVenceClave;
    }

    /**
     * @param fecVenceClave the fecVenceClave to set
     */
    public void setFecVenceClave(String fecVenceClave) {
        this.fecVenceClave = fecVenceClave;
    }

    /**
     * @return the clavePlana
     */
    public String getClavePlana() {
        return clavePlana;
    }

    /**
     * @param clavePlana the clavePlana to set
     */
    public void setClavePlana(String clavePlana) {
        this.clavePlana = clavePlana;
    }

    /**
     * @return the localesHabilitados
     */
    public String getLocalesHabilitados() {
        return localesHabilitados;
    }

    /**
     * @param localesHabilitados the localesHabilitados to set
     */
    public void setLocalesHabilitados(String localesHabilitados) {
        this.localesHabilitados = localesHabilitados;
    }
    
}
