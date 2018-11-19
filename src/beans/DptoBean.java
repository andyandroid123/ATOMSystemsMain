/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;

/**
 *
 * @author Claudio Kunnen
 */

public class DptoBean {
    
    private int codDpto;
    private String nomDpto;
    private String fecVigencia;
    private int codUsuario;

    public DptoBean() {
        
    }
    
    public int getCodDpto() {
        return codDpto;
    }

    public void setCodDpto(int codDpto) {
        this.codDpto = codDpto;
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

    public String getNomDpto() {
        return nomDpto;
    }

    public void setNomDpto(String nomDpto) {
        this.nomDpto = nomDpto;
    }                
}
