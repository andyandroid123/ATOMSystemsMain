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
public class EstructuraList {
    private Integer codigo;
    private String descripcion;
    private Integer codModulo;
    private Integer codMenu;
    
    public EstructuraList(Integer cod, String desc, Integer _codmodulo, Integer _codmenu){
        this.codigo = cod;
        this.descripcion = desc;
        this.codModulo = _codmodulo;
        this.codMenu = _codmenu;
    }

    /**
     * @return the codigo
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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
     * @return the codModulo
     */
    public Integer getCodModulo() {
        return codModulo;
    }

    /**
     * @param codModulo the codModulo to set
     */
    public void setCodModulo(Integer codModulo) {
        this.codModulo = codModulo;
    }

    /**
     * @return the codMenu
     */
    public Integer getCodMenu() {
        return codMenu;
    }

    /**
     * @param codMenu the codMenu to set
     */
    public void setCodMenu(Integer codMenu) {
        this.codMenu = codMenu;
    }
    
    
}
