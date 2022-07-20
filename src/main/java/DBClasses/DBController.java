package DBClasses;

import java.sql.*;

public class DBController {
    private static final String DRIVER= "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StockTrade";
    private static final int ATTEMPTS = 3;
    private static final String PASS = ""; // <<<<<< set a password
    private static Connection conn = null;

    //static connection singleton
    public static Connection getConn() {
        if(conn == null)
            initConnection();
        return conn;
    }

    private static boolean initConnection(){
        int attempts = ATTEMPTS;
        if(conn != null)
            return true;
        //tries a few times to open connection if it fails
        do {
            try {
                Class.forName(DRIVER);
                conn = DriverManager.getConnection(DB_URL,"root",PASS);

            } catch (Exception e) {
                e.printStackTrace();
            }
            attempts--;

        } while(attempts!=0 && conn!=null);
        return conn != null;
    }

    public static boolean checkConnection()
    {
        return conn!=null;
    }

    public static void closeConnection()
    {
        if(conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuery(Statement stmt, ResultSet rs)
    {
        if(rs!=null)
        {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(stmt!=null)
        {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String colBuilder(String ...strings)
    {
        String result = " (";
        for (String str : strings)
        {
            result += str + " ,";
        }
        result = result.substring(0,result.length()-2);
        result += ")";
        return result;
    }
}
