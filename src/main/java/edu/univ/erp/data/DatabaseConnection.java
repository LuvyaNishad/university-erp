package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String AUTH_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/university_auth";
    private static final String ERP_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/university_erp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    static {
        initializeDatabase();
    }

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

    private static void initializeDatabase() {
        createDatabases();
        // CHANGED: Do NOT force reset tables on every startup.
        // Only create them if they are missing.
        ensureTablesExist();
    }

    private static void createDatabases() {
        String baseUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/";
        try (Connection conn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_auth");
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_erp");
        } catch (SQLException e) {
            System.err.println("❌ Database creation failed: " + e.getMessage());
        }
    }

    private static void ensureTablesExist() {
        try (Connection conn = getErpConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Auth DB Tables
            stmt.execute("USE university_auth");
            stmt.execute("CREATE TABLE IF NOT EXISTS users_auth (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'active', " +
                    "failed_attempts INT DEFAULT 0, " +
                    "last_login TIMESTAMP NULL)");

            // 2. ERP DB Tables
            stmt.execute("USE university_erp");
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "roll_no VARCHAR(20) UNIQUE NOT NULL, " +
                    "program VARCHAR(100), " +
                    "year INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS instructors (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "department VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                    "course_id VARCHAR(50) PRIMARY KEY, " +
                    "code VARCHAR(20) UNIQUE NOT NULL, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "credits INT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS sections (" +
                    "section_id VARCHAR(50) PRIMARY KEY, " +
                    "course_id VARCHAR(50), " +
                    "instructor_id VARCHAR(50), " +
                    "day_time VARCHAR(100), " +
                    "room VARCHAR(50), " +
                    "capacity INT, " +
                    "semester VARCHAR(50), " +
                    "year INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                    "enrollment_id VARCHAR(50) PRIMARY KEY, " +
                    "student_id VARCHAR(50), " +
                    "section_id VARCHAR(50), " +
                    "status VARCHAR(20) DEFAULT 'registered')");

            stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                    "grade_id VARCHAR(50) PRIMARY KEY, " +
                    "enrollment_id VARCHAR(50), " +
                    "component VARCHAR(100), " +
                    "score DECIMAL(5,2), " +
                    "final_grade VARCHAR(2))");

            stmt.execute("CREATE TABLE IF NOT EXISTS settings (" +
                    "setting_key VARCHAR(100) PRIMARY KEY, " +
                    "setting_value VARCHAR(500))");

            // Check if data needs to be seeded (only if users table is empty)
            boolean dataExists = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM university_auth.users_auth")) {
                if (rs.next() && rs.getInt(1) > 0) dataExists = true;
            }

            if (!dataExists) {
                System.out.println("⚠️ Database appears empty. Inserting sample data...");
                insertSampleData();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Public method to force reset if needed manually via main()
    public static void resetTablesAndData() {
        try (Connection conn = getErpConnection();
             Statement stmt = conn.createStatement()) {
            System.out.println("📦 Wiping and resetting database...");
            stmt.execute("USE university_erp");
            stmt.execute("DROP TABLE IF EXISTS grades");
            stmt.execute("DROP TABLE IF EXISTS enrollments");
            stmt.execute("DROP TABLE IF EXISTS sections");
            stmt.execute("DROP TABLE IF EXISTS courses");
            stmt.execute("DROP TABLE IF EXISTS instructors");
            stmt.execute("DROP TABLE IF EXISTS students");
            stmt.execute("DROP TABLE IF EXISTS settings");
            stmt.execute("USE university_auth");
            stmt.execute("DROP TABLE IF EXISTS users_auth");
            ensureTablesExist(); // Re-create
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertSampleData() {
        try {
            String adminHash = PasswordUtil.hashPassword("admin123");
            String studentHash = PasswordUtil.hashPassword("student123");
            String instructorHash = PasswordUtil.hashPassword("instructor123");

            try (Connection authConn = getAuthConnection();
                 Statement authStmt = authConn.createStatement()) {

                String sql = "INSERT INTO users_auth (user_id, username, role, password_hash) VALUES " +
                        "('admin1', 'admin1', 'admin', '" + adminHash + "'), " +
                        "('stu1', 'stu1', 'student', '" + studentHash + "'), " +
                        "('stu2', 'stu2', 'student', '" + studentHash + "'), " +
                        "('inst1', 'inst1', 'instructor', '" + instructorHash + "')";
                authStmt.execute(sql);
            }

            try (Connection erpConn = getErpConnection();
                 Statement erpStmt = erpConn.createStatement()) {

                erpStmt.execute("INSERT INTO students VALUES ('stu1', '2023001', 'CS', 2023), ('stu2', '2023002', 'CS', 2023)");
                erpStmt.execute("INSERT INTO instructors VALUES ('inst1', 'CS')");
                erpStmt.execute("INSERT INTO courses VALUES ('CS101', 'CS101', 'Intro to Prog', 4), ('CS201', 'CS201', 'Data Structures', 4)");
                erpStmt.execute("INSERT INTO sections VALUES ('SEC001', 'CS101', 'inst1', 'MW 10am', 'R101', 30, 'Fall', 2024), ('SEC002', 'CS201', 'inst1', 'TH 2pm', 'R102', 25, 'Fall', 2024)");
                erpStmt.execute("INSERT INTO enrollments VALUES ('ENR001', 'stu1', 'SEC001', 'registered'), ('ENR002', 'stu2', 'SEC001', 'registered')");
                erpStmt.execute("INSERT INTO settings VALUES ('maintenance_on', 'false')");
                erpStmt.execute("INSERT INTO settings VALUES ('deadline', '2025-12-31')");

                System.out.println("✅ Sample data inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnections() {
        try (Connection c1 = getAuthConnection(); Connection c2 = getErpConnection()) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // Run this MANUALLY if you ever want to wipe and reset the DB
    public static void main(String[] args) {
        resetTablesAndData();
        System.out.println("✅ Database reset complete.");
    }
}