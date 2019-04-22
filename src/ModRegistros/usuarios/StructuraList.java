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
public class StructuraList {
    private Integer codigo;
    private String descripcion;
    private Integer codmodulo;
    private Integer codmenu;
    
    /** Creates a new instance of Structura */
    public StructuraList(Integer cod, String des,Integer _codmodulo,Integer _codmenu ) {
        this.codigo = cod;
        this.descripcion = des;
        this.codmodulo = _codmodulo;
        this.codmenu = _codmenu;
        
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String toString() {
        return this.getDescripcion();
    }

    public Integer getCodmodulo() {
        return codmodulo;
    }

    public void setCodmodulo(Integer codmodulo) {
        this.codmodulo = codmodulo;
    }

    public Integer getCodmenu() {
        return codmenu;
    }

    public void setCodmenu(Integer codmenu) {
        this.codmenu = codmenu;
    }
    
}