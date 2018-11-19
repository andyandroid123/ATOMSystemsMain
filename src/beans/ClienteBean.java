/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.sql.Date;


/**
 *
 * @author Andres
 */
public class ClienteBean {

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
    
    private int codCliente;
    private String razonSoc;
    private String contacto;
    private String ciContacto;
    private String rucCliente;
    private String esJuridica;
    private int codCiudad;
    private int codBarrio;
    private String direccion;
    private String telefono;
    private String fax;
    private String email;
    private int codDpto;
    private int condPago;
    private int limiteCredito;
    private String fecVigencia;
    private String aceptaCheque;
    private String activo;
    private String codPCuenta;
    private int pctMaxDescuento;
    private int codListaPrecio;
    private int codZona;
    private String apodo;
    private String fecNacimiento;
    private String celular;
    private int codPais;
    private String ciOrigen;
    private String docInmigracion;
    private String viveAlquiler;
    private int codRepresentante;
    private int codGarante;
    private int dependientes;
    private int giro;
    private String codClasificacion;
    private int codLocalPreferencia;
    private String sexo;
    private String estadoCivil;
    private String esContribuyente;
    private int codUsuario;

    /**
     * @return the codCliente
     */
    public int getCodCliente() {
        return codCliente;
    }

    /**
     * @param codCliente the codCliente to set
     */
    public void setCodCliente(int codCliente) {
        this.codCliente = codCliente;
    }

    /**
     * @return the razonSoc
     */
    public String getRazonSoc() {
        return razonSoc;
    }

    /**
     * @param razonSoc the razonSoc to set
     */
    public void setRazonSoc(String razonSoc) {
        this.razonSoc = razonSoc;
    }

    /**
     * @return the contacto
     */
    public String getContacto() {
        return contacto;
    }

    /**
     * @param contacto the contacto to set
     */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /**
     * @return the ciContacto
     */
    public String getCiContacto() {
        return ciContacto;
    }

    /**
     * @param ciContacto the ciContacto to set
     */
    public void setCiContacto(String ciContacto) {
        this.ciContacto = ciContacto;
    }

    /**
     * @return the rucCliente
     */
    public String getRucCliente() {
        return rucCliente;
    }

    /**
     * @param rucCliente the rucCliente to set
     */
    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    /**
     * @return the esJuridica
     */
    public String getEsJuridica() {
        return esJuridica;
    }

    /**
     * @param esJuridica the esJuridica to set
     */
    public void setEsJuridica(String esJuridica) {
        this.esJuridica = esJuridica;
    }

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
     * @return the codBarrio
     */
    public int getCodBarrio() {
        return codBarrio;
    }

    /**
     * @param codBarrio the codBarrio to set
     */
    public void setCodBarrio(int codBarrio) {
        this.codBarrio = codBarrio;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the codDpto
     */
    public int getCodDpto() {
        return codDpto;
    }

    /**
     * @param codDpto the codDpto to set
     */
    public void setCodDpto(int codDpto) {
        this.codDpto = codDpto;
    }

    /**
     * @return the condPago
     */
    public int getCondPago() {
        return condPago;
    }

    /**
     * @param condPago the condPago to set
     */
    public void setCondPago(int condPago) {
        this.condPago = condPago;
    }

    /**
     * @return the limiteCredito
     */
    public int getLimiteCredito() {
        return limiteCredito;
    }

    /**
     * @param limiteCredito the limiteCredito to set
     */
    public void setLimiteCredito(int limiteCredito) {
        this.limiteCredito = limiteCredito;
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
     * @return the aceptaCheque
     */
    public String getAceptaCheque() {
        return aceptaCheque;
    }

    /**
     * @param aceptaCheque the aceptaCheque to set
     */
    public void setAceptaCheque(String aceptaCheque) {
        this.aceptaCheque = aceptaCheque;
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
     * @return the codPCuenta
     */
    public String getCodPCuenta() {
        return codPCuenta;
    }

    /**
     * @param codPCuenta the codPCuenta to set
     */
    public void setCodPCuenta(String codPCuenta) {
        this.codPCuenta = codPCuenta;
    }

    /**
     * @return the pctMaxDescuento
     */
    public int getPctMaxDescuento() {
        return pctMaxDescuento;
    }

    /**
     * @param pctMaxDescuento the pctMaxDescuento to set
     */
    public void setPctMaxDescuento(int pctMaxDescuento) {
        this.pctMaxDescuento = pctMaxDescuento;
    }

    /**
     * @return the codListaPrecio
     */
    public int getCodListaPrecio() {
        return codListaPrecio;
    }

    /**
     * @param codListaPrecio the codListaPrecio to set
     */
    public void setCodListaPrecio(int codListaPrecio) {
        this.codListaPrecio = codListaPrecio;
    }

    /**
     * @return the codZona
     */
    public int getCodZona() {
        return codZona;
    }

    /**
     * @param codZona the codZona to set
     */
    public void setCodZona(int codZona) {
        this.codZona = codZona;
    }

    /**
     * @return the apodo
     */
    public String getApodo() {
        return apodo;
    }

    /**
     * @param apodo the apodo to set
     */
    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    /**
     * @return the fecNacimiento
     */
    public String getFecNacimiento() {
        return fecNacimiento;
    }

    /**
     * @param fecNacimiento the fecNacimiento to set
     */
    public void setFecNacimiento(String fecNacimiento) {
        this.fecNacimiento = fecNacimiento;
    }

    /**
     * @return the celular
     */
    public String getCelular() {
        return celular;
    }

    /**
     * @param celular the celular to set
     */
    public void setCelular(String celular) {
        this.celular = celular;
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
     * @return the ciOrigen
     */
    public String getCiOrigen() {
        return ciOrigen;
    }

    /**
     * @param ciOrigen the ciOrigen to set
     */
    public void setCiOrigen(String ciOrigen) {
        this.ciOrigen = ciOrigen;
    }

    /**
     * @return the docInmigracion
     */
    public String getDocInmigracion() {
        return docInmigracion;
    }

    /**
     * @param docInmigracion the docInmigracion to set
     */
    public void setDocInmigracion(String docInmigracion) {
        this.docInmigracion = docInmigracion;
    }

    /**
     * @return the viveAlquiler
     */
    public String getViveAlquiler() {
        return viveAlquiler;
    }

    /**
     * @param viveAlquiler the viveAlquiler to set
     */
    public void setViveAlquiler(String viveAlquiler) {
        this.viveAlquiler = viveAlquiler;
    }

    /**
     * @return the codRepresentante
     */
    public int getCodRepresentante() {
        return codRepresentante;
    }

    /**
     * @param codRepresentante the codRepresentante to set
     */
    public void setCodRepresentante(int codRepresentante) {
        this.codRepresentante = codRepresentante;
    }

    /**
     * @return the codGarante
     */
    public int getCodGarante() {
        return codGarante;
    }

    /**
     * @param codGarante the codGarante to set
     */
    public void setCodGarante(int codGarante) {
        this.codGarante = codGarante;
    }

    /**
     * @return the dependientes
     */
    public int getDependientes() {
        return dependientes;
    }

    /**
     * @param dependientes the dependientes to set
     */
    public void setDependientes(int dependientes) {
        this.dependientes = dependientes;
    }

    /**
     * @return the giro
     */
    public int getGiro() {
        return giro;
    }

    /**
     * @param giro the giro to set
     */
    public void setGiro(int giro) {
        this.giro = giro;
    }

    /**
     * @return the codClasificacion
     */
    public String getCodClasificacion() {
        return codClasificacion;
    }

    /**
     * @param codClasificacion the codClasificacion to set
     */
    public void setCodClasificacion(String codClasificacion) {
        this.codClasificacion = codClasificacion;
    }

    /**
     * @return the codLocalPreferencia
     */
    public int getCodLocalPreferencia() {
        return codLocalPreferencia;
    }

    /**
     * @param codLocalPreferido the codLocalPreferencia to set
     */
    public void setCodLocalPreferencia(int codLocalPreferido) {
        this.codLocalPreferencia = codLocalPreferido;
    }

    /**
     * @return the sexo
     */
    public String getSexo() {
        return sexo;
    }

    /**
     * @param sexo the sexo to set
     */
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    /**
     * @return the estadoCivil
     */
    public String getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * @param estadoCivil the estadoCivil to set
     */
    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    /**
     * @return the esContribuyente
     */
    public String getEsContribuyente() {
        return esContribuyente;
    }

    /**
     * @param esContribuyente the esContribuyente to set
     */
    public void setEsContribuyente(String esContribuyente) {
        this.esContribuyente = esContribuyente;
    }
}
