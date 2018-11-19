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
public class BarrioBean {
    
     private int codBarrio;
    private String nomBarrio;
    private int codCiudad;
    private String fecVigencia;
    private int codUsuario;

    public BarrioBean() {
        
    }
    
    public int getCodBarrio() {
        return codBarrio;
    }

    public void setCodBarrio(int codBarrio) {
        this.codBarrio = codBarrio;
    }

    public int getCodCiudad() {
        return codCiudad;
    }

    public void setCodCiudad(int codCiudad) {
        this.codCiudad = codCiudad;
    }

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getFecVigencia() {
        return fecVigencia;
    }

    public void setFecVigencia(String fecVigencia) {
        this.fecVigencia = fecVigencia;
    }

    public String getNomBarrio() {
        return nomBarrio;
    }

    public void setNomBarrio(String nomBarrio) {
        this.nomBarrio = nomBarrio;
    }
    
}
