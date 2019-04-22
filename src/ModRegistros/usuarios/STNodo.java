/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

/**
 *
 * @author ANDRES
 */
public class STNodo {
    
    private String NDescripcion;
    private Integer NCodigo;
    private Integer NNivel;
    
    public STNodo(int Ncod, String NDes, int NNiv){
        this.NDescripcion = NDes;
        this.NCodigo = Ncod;
        this.NNivel = NNiv;
    }

    /**
     * @return the NDescripcion
     */
    public String getNDescripcion() {
        return NDescripcion;
    }

    /**
     * @param NDescripcion the NDescripcion to set
     */
    public void setNDescripcion(String NDescripcion) {
        this.NDescripcion = NDescripcion;
    }

    /**
     * @return the NCodigo
     */
    public Integer getNCodigo() {
        return NCodigo;
    }

    /**
     * @param NCodigo the NCodigo to set
     */
    public void setNCodigo(Integer NCodigo) {
        this.NCodigo = NCodigo;
    }

    /**
     * @return the NNivel
     */
    public Integer getNNivel() {
        return NNivel;
    }

    /**
     * @param NNivel the NNivel to set
     */
    public void setNNivel(Integer NNivel) {
        this.NNivel = NNivel;
    }
    
    public String toString() {
        String retValue;
        
        retValue = this.getNDescripcion();
        return retValue;
    }
}
