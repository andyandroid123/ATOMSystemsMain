/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.ArticuloBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class ArticuloCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodArticulo = "SELECT MAX(cod_articulo) FROM articulo";
    
    String consultaArticuloDescripcion = "SELECT cod_articulo, descripcion, des_corta, cod_grupo, cod_subgrupo, cod_proveedor, cod_marca, pct_iva, "
                                       + "fec_catastro, es_compuesto, es_pesable, hab_compra, hab_venta, es_produccion, cod_usuario, fec_vigencia "
                                       + "FROM articulo "
                                       + "WHERE descripcion LIKE ? "
                                       + "ORDER BY descripcion";
    
    String getDescripcionArticulo = "SELECT descripcion FROM articulo WHERE cod_articulo = ?";
    
    String consultaArticuloCodigo = "SELECT cod_articulo, descripcion, des_corta, cod_grupo, cod_subgrupo, cod_proveedor, cod_marca, pct_iva, "
                                  + "fec_catastro, es_compuesto, es_pesable, hab_compra, hab_venta, es_produccion, cod_usuario, fec_vigencia "
                                  + "FROM articulo "
                                  + "WHERE cod_articulo = ?";
    
    String catastraArticulo = "INSERT INTO articulo (cod_articulo, descripcion, des_corta, cod_grupo, cod_subgrupo, cod_proveedor, cod_marca, pct_iva, "
                            + "fec_catastro, es_compuesto, es_pesable, hab_compra, hab_venta, es_produccion, cod_usuario, fec_vigencia) "
                            + "VALUES (?, ?, ?, ?,? , ?,? , ?,now(), ?, ?, ?, ?, ?, ?, now())";
    
    String modificaArticulo = "UPDATE articulo SET descripcion = ?, des_corta = ?, cod_grupo = ?, cod_subgrupo = ?, cod_proveedor = ?, cod_marca = ?, "
                            + "pct_iva = ?, es_compuesto = ?, es_pesable = ?, hab_compra = ?, hab_venta = ?, es_produccion = ?, cod_usuario = ?, fec_vigencia = now() "
                            + "WHERE cod_articulo = ?";
    
    public ArticuloCtrl(){}
    
    public int buscaMaxCodArticulo(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodArticulo);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }finally{
            try{
                rs.close();
            }catch(SQLException sqlex){
                sqlex.printStackTrace();
            }
        }
        return result;
    }
    
    public boolean modificaArticulo(ArticuloBean articulo){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaArticulo);
            pstmt.setString( 1, articulo.getDescripcion());
            pstmt.setString( 2, articulo.getDesCorta());
            pstmt.setInt(    3, articulo.getCodGrupo());
            pstmt.setInt(    4, articulo.getCodSubGrupo());
            pstmt.setInt(    5, articulo.getCodProveedor());
            pstmt.setInt(    6, articulo.getCodMarca());
            pstmt.setInt(    7, articulo.getPctIva());
            pstmt.setString( 8, articulo.getEsCompuesto());
            pstmt.setString( 9, articulo.getEsPesable());
            pstmt.setString(10, articulo.getHabCompra());
            pstmt.setString(11, articulo.getHabVenta());
            pstmt.setString(12, articulo.getEsProduccion());
            pstmt.setInt(   13, articulo.getCodUsuario());
            pstmt.setInt(   14, articulo.getCodArticulo()); // WHERE
            if(pstmt.executeUpdate()> 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean catastraArticulo(ArticuloBean articulo){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraArticulo);
            pstmt.setInt(    1, articulo.getCodArticulo());
            pstmt.setString( 2, articulo.getDescripcion());
            pstmt.setString( 3, articulo.getDesCorta());
            pstmt.setInt(    4, articulo.getCodGrupo());
            pstmt.setInt(    5, articulo.getCodSubGrupo());
            pstmt.setInt(    6, articulo.getCodProveedor());
            pstmt.setInt(    7, articulo.getCodMarca());
            pstmt.setInt(    8, articulo.getPctIva());
            pstmt.setString( 9, articulo.getEsCompuesto());
            pstmt.setString(10, articulo.getEsPesable());
            pstmt.setString(11, articulo.getHabCompra());
            pstmt.setString(12, articulo.getHabVenta());
            pstmt.setString(13, articulo.getEsProduccion());
            pstmt.setInt(   14, articulo.getCodUsuario());
            if(pstmt.executeUpdate() > 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public List<ArticuloBean> listarArticuloDescripcion(String descripcion){
        List<ArticuloBean> articuloList = new ArrayList();
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaArticuloDescripcion);
            pstmt.setString(1, descripcion);
            rs = pstmt.executeQuery();
            ArticuloBean bean = null;
            while(rs.next()){
                bean = new ArticuloBean();
                bean.setCodArticulo(rs.getInt("cod_articulo"));
                bean.setCodGrupo(rs.getInt("cod_grupo"));
                bean.setCodMarca(rs.getInt("cod_marca"));
                bean.setCodProveedor(rs.getInt("cod_proveedor"));
                bean.setCodSubGrupo(rs.getInt("cod_subgrupo"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDesCorta(rs.getString("des_corta"));
                bean.setDescripcion(rs.getString("descripcion"));
                bean.setEsCompuesto(rs.getString("es_compuesto"));
                bean.setEsPesable(rs.getString("es_pesable"));
                bean.setEsProduccion(rs.getString("es_produccion"));
                bean.setFecCatastro(rs.getString("fec_catastro"));
                bean.setFecVigencia(rs.getString("fec_vigencia"));
                bean.setHabCompra(rs.getString("hab_compra"));
                bean.setHabVenta(rs.getString("hab_venta"));
                bean.setPctIva(rs.getInt("pct_iva"));
                articuloList.add(bean);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return articuloList;
    }
    
    public ArticuloBean buscaArticuloCodigo(int codArticulo){
        ArticuloBean articulo = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(consultaArticuloCodigo);
            pstmt.setInt(1, codArticulo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                articulo = new ArticuloBean();
                articulo.setCodArticulo(rs.getInt("cod_articulo"));
                articulo.setCodGrupo(rs.getInt("cod_grupo"));
                articulo.setCodMarca(rs.getInt("cod_marca"));
                articulo.setCodProveedor(rs.getInt("cod_proveedor"));
                articulo.setCodSubGrupo(rs.getInt("cod_subgrupo"));
                articulo.setCodUsuario(rs.getInt("cod_usuario"));
                articulo.setDesCorta(rs.getString("des_corta"));
                articulo.setDescripcion(rs.getString("descripcion"));
                articulo.setEsCompuesto(rs.getString("es_compuesto"));
                articulo.setEsPesable(rs.getString("es_pesable"));
                articulo.setEsProduccion(rs.getString("es_produccion"));
                articulo.setFecCatastro(rs.getString("fec_catastro"));
                articulo.setFecVigencia(rs.getString("fec_vigencia"));
                articulo.setHabCompra(rs.getString("hab_compra"));
                articulo.setHabVenta(rs.getString("hab_venta"));
                articulo.setPctIva(rs.getInt("pct_iva"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return articulo;
    }
    
    public String getDescripcionArticulo(int codArticulo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getDescripcionArticulo);
            pstmt.setInt(1, codArticulo);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("descripcion");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}
