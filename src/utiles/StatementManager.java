package utiles;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementManager {
    
    /** Creates a new instance of StatementManager */
    public StatementManager() {
        try {
            TheStatement  =  DBManager.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
    
    private Statement TheStatement;
    public ResultSet TheResultSet;
    public String TheSql;
    
    public void EjecutarSql() {
        try {
            TheResultSet  = TheStatement.executeQuery(TheSql);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
    
    public void CerrarStatement() {
        try {
            CerrarResultSet();
            TheStatement.close();
            TheStatement = null;
        } catch ( SQLException excepcionSQL ) {
            excepcionSQL.printStackTrace();
        }
    }
    
    public void CerrarResultSet() {
        try {
            TheResultSet.close();
            TheResultSet = null;
        } catch ( SQLException excepcionSQL ) {
            excepcionSQL.printStackTrace();
        }
    }

    private void ClearDBManagerrset() {
        try {
            if (DBManager.rset != null){
                DBManager.rset.close();
                DBManager.rset = null;
            }
        } catch ( SQLException excepcionSQL ) {
            excepcionSQL.printStackTrace();
        }
    }
    
    public void ClearDBManagerstmt() {
        ClearDBManagerrset();
        try {
            if (DBManager.stmt != null){
                DBManager.stmt.close();
                DBManager.stmt = null;
            }
        } catch ( SQLException excepcionSQL ) {
            excepcionSQL.printStackTrace();
        }
    }
    
}
