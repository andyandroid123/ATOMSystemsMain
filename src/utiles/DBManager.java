package utiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;

public class DBManager {

    public static Connection conn = null;
    static PreparedStatement pstmt = null;
    public static Statement stmt = null;
    public static ResultSet rset = null;
    public static CallableStatement cs = null;

    public DBManager() {
    }

    public static boolean conectar(String usuario, String password, String nombreBD, String IPServer, int baseDato) {
        String url = "jdbc:postgresql://" + IPServer + ":5432/" + nombreBD;
        String usr = usuario;
        String clave = password;
        if (conn != null) {
            cerrarBD();
        }
        
        try {
            // PostGreSQL 8.2
            DriverManager.registerDriver(new org.postgresql.Driver());
            conn = DriverManager.getConnection(url, usr, clave);
            return true;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return false;
        }
    }

    public static String getNombreUser() {
        try {
            return conn.getMetaData().getUserName(); // getDatabaseProductName();
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            return "ERROR";
        }
    }

    public static String getNombreBD() {
        try {
            return conn.getMetaData().getDatabaseProductName();
        } catch (SQLException ex) {
            ex.printStackTrace();
            InfoErrores.errores(ex);
            return "ERROR";
        }
    }

    public static ResultSet ejecutarBuscaLikeArticuloFiltro(String colu1, String colu2,
            String colu3, String colu4,
            String campsel, String tabela,
            String valor, String condicion) {
        try {
            pstmt =
                    conn.prepareStatement("SELECT art.cod_articulo, descripcion, TRUNC(precios.precio_venta), " +
                    "precios.sigla_venta ||'/'|| precios.cansi_venta,art.cod_proveedor,st.Stock " +
                    " FROM articulo art " +
                    " INNER JOIN preciosArt precios on precios.cod_articulo=art.cod_articulo AND precios.vigente='S'" +
                    " left outer join stockart st on st.cod_articulo = art.cod_articulo "+
                    " and st.cod_sector ="+Utiles.getCodSectorDefault(Utiles.getCodLocalDefault(Utiles.getCodEmpresaDefault()))+
                    " WHERE " + campsel + " LIKE ? and "+condicion+" ORDER by " + campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    //" WHERE " + campsel + " LIKE ? and "+condicion + " ORDER BY " + campsel);
            pstmt.setString(1, valor);
            rset = pstmt.executeQuery();
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            pstmt = null;
            rset = null;
            return null;
        }
    }
    
    public static ResultSet ejecutarBuscaLikeArticuloVentas(String colu1, String colu2, String colu3, String colu4, 
                                                            String colu5, String campsel, String tabla, String valor, String condicion){
        
        String sql = "SELECT art.cod_articulo, descripcion, TRUNC(precios.precio_venta), "
                   + "precios.sigla_venta ||'/'|| precios.cansi_venta, st.stock::float "
                   + "FROM articulo art "
                   + "INNER JOIN preciosart preios "
                   + "ON precios.cod_articulo = art.cod_articulo "
                   + "AND precios.vigente = 'S' "
                   + "LEFT OUTER JOIN stockart st "
                   + "ON st.cod_articulo = art.cod_articulo "
                   + "AND st.cod_sector = 1 " 
                   + " WHERE " + campsel + " LIKE ? AND " + condicion + " ORDER BY " + campsel + "";
        try {
            pstmt =
                    conn.prepareStatement("SELECT art.cod_articulo, descripcion, TRUNC(precios.precio_venta), "
                  + "precios.sigla_venta ||'/'|| precios.cansi_venta, st.Stock::float " +
                    " FROM articulo art " +
                    " INNER JOIN preciosArt precios on precios.cod_articulo=art.cod_articulo AND precios.vigente='S'" +
                    " left outer join stockart st on st.cod_articulo = art.cod_articulo "+
                    " and st.cod_sector ="+Utiles.getCodSectorDefault(Utiles.getCodLocalDefault(Utiles.getCodEmpresaDefault()))+
                    " WHERE " + campsel + " LIKE ? and "+condicion+" ORDER by " + campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    //" WHERE " + campsel + " LIKE ? and "+condicion + " ORDER BY " + campsel);
                    System.out.println("SQL BUSQUEDA: " + sql);
            pstmt.setString(1, valor);
            rset = pstmt.executeQuery();
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            pstmt = null;
            rset = null;
            return null;
        }
    }
    
    public static ResultSet ejecutarBuscaLike(String colu1, String colu2,
            String colu3, String colu4, String colu5,
            String campsel, String tabela,
            String valor) {
        try {
            pstmt =
                    conn.prepareStatement("Select " + colu1 + "," + colu2 + "," +
                    colu3 + "," + colu4 + "," + colu5 + " from " +
                    tabela + " where UPPER(" + campsel +
                    ") LIKE ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //" LIKE ? ORDER BY "+campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, valor);
            rset = pstmt.executeQuery();
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }
    
    public static ResultSet ejecutarBuscaLike(String colu1, String colu2,
            String colu3, String colu4, String colu5,
            String campsel, String tabela,
            String valor, String condicion2, String valor2) {
        try {
            pstmt =
                    conn.prepareStatement("Select " + colu1 + "," + colu2 + "," +
                    colu3 + "," + colu4 + "," + colu5 + " from " +
                    tabela + " where " + condicion2 + " = ? and UPPER(" + campsel +
                    ") LIKE ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //" LIKE ? ORDER BY "+campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, valor2);
            pstmt.setString(2, valor);
            rset = pstmt.executeQuery();
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }
    
    public static ResultSet ejecutarBuscaLikeSubgrupo(String colu1, String colu2, String colu3,
                                                      String tabla, String valor, String condicion1, String valor1, 
                                                      String condicion2){
        try{
            pstmt = conn.prepareStatement("SELECT " + colu1 + "," + colu2 + "," + colu3 +
                                         " FROM " + tabla + " WHERE " + condicion1 + " = ?"
                                        + " AND " + condicion2 + " = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, valor);
            pstmt.setString(2, valor1);
            rset = pstmt.executeQuery();
            return rset;
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }

    public static ResultSet ejecutarBuscaLikeIgual(String colu1, String colu2,
            String colu3, String colu4,
            String campsel,
            String campse2,
            String tabela, String valor,
            String valor1) {
        try {
            pstmt =
                    conn.prepareStatement("Select " + colu1 + "," + colu2 + "," +
                    colu3 + "," + colu4 + " from " +
                    tabela + " where " + campsel +
                    " LIKE ?" + " and " + campse2 +
                    " =? order by " + campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, valor);
            pstmt.setString(2, valor1);
            rset = pstmt.executeQuery();
            return rset;
        } catch (SQLException sqlex) {
            InfoErrores.errores(sqlex);
            sqlex.printStackTrace();
            pstmt = null;
            rset = null;
            return null;
        }
    }

    public static String ejecutarConsulta(String tabla, String campoSelec1,
            String campoCondi, String valor) {
        /*try {
            pstmt =
                    conn.prepareStatement("SELECT " + campoSelec1 + " FROM " + tabla +
                    " WHERE " + campoCondi + "=?");
            pstmt.setString(1, valor);
            rset = pstmt.executeQuery();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return "ERROR";
        }
        String result = "ERROR";
        try {
            if (rset.next()) {
                result = rset.getString(1);
            }
            rset.close();
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        }
        return result;*/
            
            try {
            pstmt = conn.prepareStatement("SELECT " + campoSelec1 + " FROM " + tabla +
                    " WHERE " + campoCondi + "=?");            
            try {

                boolean esnumerico = true;

                try {
                    int x = Integer.valueOf(valor);
                    
                } catch(NumberFormatException ex) {
                    esnumerico = false;
                }
                
                if(esnumerico){
                    pstmt.setInt(1, Integer.valueOf(valor));
                }else{
                    pstmt.setString(1, valor);
                }

                rset = pstmt.executeQuery();
            } catch(SQLException ex) {
                ex.printStackTrace();
                InfoErrores.errores(ex);
                try {
                    pstmt.setString(1, valor);
                    rset = pstmt.executeQuery();
                } catch(SQLException ex1) {
                    ex1.printStackTrace();
                    InfoErrores.errores(ex);
                }
            }            
        } catch (Exception sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return "ERROR";
        }
        String result = "ERROR";
        try {
            if (rset.next()) {
                result = rset.getString(1);
            }
            rset.close();
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        }
        return result;
    }

    public static String ejecutarConsulta(String tabla, String campoSelec1,
            String campoCondi, String valor, String condicion2, String valor2) {
        try {
            pstmt =
                    conn.prepareStatement("SELECT " + campoSelec1 + " FROM " + tabla +
                    " WHERE " + campoCondi + "=? and " + condicion2 + " =?");
            pstmt.setString(1, valor);
            pstmt.setString(2, valor2);
            rset = pstmt.executeQuery();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return "ERROR";
        }
        String result = "ERROR";
        try {
            if (rset.next()) {
                result = rset.getString(1);
            }
            rset.close();
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        }
        return result;
    }

    public static String ejecutarConsulta(String sql) {
        try {
            pstmt = conn.prepareStatement(sql);
            //pstmt.setString(1, valor);
            rset = pstmt.executeQuery();
        } catch (SQLException sqlex) {
            InfoErrores.errores(sqlex);
            sqlex.printStackTrace();
            return "ERROR";
        }
        String result = "ERROR";
        try {
            if (rset.next()) {
                result = rset.getString(1);
            }
            rset.close();
        } catch (SQLException sex) {
            InfoErrores.errores(sex);
            sex.printStackTrace();
        }
        return result;
    }

    public static ResultSet ejecutarBuscaLikeIgual(ResultSet resSet, String colu1, String colu2,
            String colu3, String colu4,
            String campsel,
            String campse2,
            String tabela, String valor,
            String valor1) {
        try {
            pstmt =
                    conn.prepareStatement("Select " + colu1 + "," + colu2 + "," +
                    colu3 + "," + colu4 + " from " +
                    tabela + " where " + campsel +
                    " LIKE ?" + " and " + campse2 +
                    " =? order by " + campsel, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, valor);
            pstmt.setString(2, valor1);
            resSet = pstmt.executeQuery();
            return resSet;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }

    public static void setAutocommit(boolean estado) {
        try {
            conn.setAutoCommit(estado);
        } catch (SQLException sex) {
            sex.printStackTrace();
            InfoErrores.errores(sex);
        }
    }

    public static int ejecutarDML(String sql) {
        int fp = 0;
        try {
            stmt = conn.createStatement();
            fp = stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }
        return fp;
    }

    public static ResultSet ejecutarDSL(String sql) {
        ResultSet rset = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //rset = stmt.executeQuery(sql);
            rset = stmt.executeQuery(sql);
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }

    public static ResultSet ejecutarDSL(String sql, ResultSet rset) {
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rset = stmt.executeQuery(sql);
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }

    public static ResultSet ejecutarDSL(String sql, ResultSet rset, Statement st) {
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rset = st.executeQuery(sql);
            return rset;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return null;
        }
    }

    public static long ejecutarSecuencia(String sql) {
        long result = 0;
        try {
            stmt = conn.createStatement();
            //rset = stmt.executeQuery(sql);
            result = stmt.executeUpdate(sql);
            return result;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
            return 0;
        }
    }

    public static void cerrarBD() {
        try {
            if (rset != null) {
                rset.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }
    }

    public static void CerrarStatements() {
        try {
            if (rset != null) {
                rset.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            InfoErrores.errores(e);
        }
    }

    public static String ejecutarDSLsimple(String sql) {
        String res = null;
        ResultSet rst = null;
        Statement rstmt = null;
        try {
            /*
            if(conn.isClosed() || checkDB()){
            reconectar();
            } 
             */
            rstmt = conn.createStatement();
            rst = rstmt.executeQuery(sql);
            if (rst.next()) {
                res = rst.getString("result");
            }

        } catch (SQLException e) {
            InfoErrores.errores(e);
        } finally {
            CerrarStatements();
        }
        return res;
    }

    private static boolean checkDB() {
        boolean result = false;
        try {
            Connection chekconn = conn;
            Statement chekstmt = conn.createStatement();
            String sql = "select 1 from dual";
            ResultSet rs = chekstmt.executeQuery(sql);
            rs.close();
            chekstmt.close();
            result = false;
        } catch (SQLException e) {
            result = true;
        }
        return result;
    }

    public static ResultSet ejecutarSQLDSL(String sql) throws SQLException {
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

}
