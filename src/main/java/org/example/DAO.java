//database access object;
// it connects database to GUI interface and allow users to conduct CRUD actions to the database
package org.example;
import java.sql.*;
public class DAO {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/recipe";

    static final String USER = "root";
    static final String PASS = "";

    public static Connection connectToDB() {
        Connection conn = null;
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to DB...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

        }catch(SQLException se){
            // Handle JDBC error.
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    public static ResultSet executeQuery(Connection conn, String queryString) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(queryString);

        }catch (SQLException e1) {
            e1.printStackTrace();
        }
        return rs;
    }

    public static void terminateConn(Connection conn) {
        try{
            if(conn!=null) conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DAO.connectToDB();
    }
}
