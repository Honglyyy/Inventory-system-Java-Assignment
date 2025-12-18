package dao;

import java.sql.*;

public class DBConn {

    private static final String URL = "jdbc:mysql://localhost:3306/invsys";
    private static final String USER = "invsys";
    private static final String PASSWORD = "66667777";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }
    public static void closePreparedStatement(PreparedStatement ps) throws SQLException {
        ps.close();
    }
    public static void closeResultSet(ResultSet rs) throws SQLException {
        rs.close();
    }
    public static void closeStatement(Statement stmt) throws SQLException {
        stmt.close();
    }
}
