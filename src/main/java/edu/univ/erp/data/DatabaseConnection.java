package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "3306";
    private static final String AUTH_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/auth_db";
    private static final String ERP_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/erp_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // password not uploaded on github intentionally :)

    public static Connection getAuthConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(AUTH_DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getErpConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // Test connections
    public static boolean testConnections() {
        try (Connection authConn = getAuthConnection();
             Connection erpConn = getErpConnection()) {
            return authConn != null && erpConn != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
