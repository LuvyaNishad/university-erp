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
    private static final String DB_PASSWORD = "root123"; // Ensure this matches your MySQL password

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
        // FORCE RESET: Drops and recreates tables to ensure schema/passwords are correct
        resetTablesAndData();
    }

    private static void createDatabases() {
        String baseUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/";
        try (Connection conn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            System.out.println("🚀 Checking databases...");
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_auth");
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_erp");
        } catch (SQLException e) {
            System.err.println("❌ Database creation failed: " + e.getMessage());
        }
    }

    private static void resetTablesAndData() {
        try (Connection conn = getErpConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("📦 Resetting database schema and data...");

            // 1. Drop Old Tables
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

            // 2. Create Auth DB Tables
            stmt.execute("CREATE TABLE users_auth (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'active', " +
                    "failed_attempts INT DEFAULT 0, " +
                    "last_login TIMESTAMP NULL)");

            // 3. Create ERP DB Tables
            stmt.execute("USE university_erp");
            stmt.execute("CREATE TABLE students (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "roll_no VARCHAR(20) UNIQUE NOT NULL, " +
                    "program VARCHAR(100), " +
                    "year INT)");

            stmt.execute("CREATE TABLE instructors (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "department VARCHAR(100))");

            stmt.execute("CREATE TABLE courses (" +
                    "course_id VARCHAR(50) PRIMARY KEY, " +
                    "code VARCHAR(20) UNIQUE NOT NULL, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "credits INT NOT NULL)");

            stmt.execute("CREATE TABLE sections (" +
                    "section_id VARCHAR(50) PRIMARY KEY, " +
                    "course_id VARCHAR(50), " +
                    "instructor_id VARCHAR(50), " +
                    "day_time VARCHAR(100), " +
                    "room VARCHAR(50), " +
                    "capacity INT, " +
                    "semester VARCHAR(50), " +
                    "year INT)");

            stmt.execute("CREATE TABLE enrollments (" +
                    "enrollment_id VARCHAR(50) PRIMARY KEY, " +
                    "student_id VARCHAR(50), " +
                    "section_id VARCHAR(50), " +
                    "status VARCHAR(20) DEFAULT 'registered')");

            stmt.execute("CREATE TABLE grades (" +
                    "grade_id VARCHAR(50) PRIMARY KEY, " +
                    "enrollment_id VARCHAR(50), " +
                    "component VARCHAR(100), " +
                    "score DECIMAL(5,2), " +
                    "final_grade VARCHAR(2))");

            stmt.execute("CREATE TABLE settings (" +
                    "setting_key VARCHAR(100) PRIMARY KEY, " +
                    "setting_value VARCHAR(500))");

            // 4. Insert Fresh Data
            insertSampleData();

        } catch (SQLException e) {
            System.err.println("❌ Database reset failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertSampleData() {
        try {
            // Generate hashes dynamically
            String adminHash = PasswordUtil.hashPassword("admin123");
            String studentHash = PasswordUtil.hashPassword("student123");
            String instructorHash = PasswordUtil.hashPassword("instructor123");

            try (Connection authConn = getAuthConnection();
                 Statement authStmt = authConn.createStatement()) {

                String sql = "INSERT INTO users_auth (user_id, username, role, password_hash) VALUES " +
                        "('admin1', 'admin1', 'admin', '" + adminHash + "'), " +
                        "('stu1', 'stu1', 'student', '" + studentHash + "'), " +
                        "('stu2', 'stu2', 'student', '" + studentHash + "'), " +
                        "('stu3', 'stu3', 'student', '" + studentHash + "'), " +
                        "('inst1', 'inst1', 'instructor', '" + instructorHash + "'), " +
                        "('inst2', 'inst2', 'instructor', '" + instructorHash + "')";
                authStmt.execute(sql);
                System.out.println("✅ Auth data inserted (Passwords: admin123, student123, instructor123)");
            }

            try (Connection erpConn = getErpConnection();
                 Statement erpStmt = erpConn.createStatement()) {

                // IIIT-Delhi Students
                erpStmt.execute("INSERT INTO students VALUES " +
                        "('stu1', '2023001', 'B.Tech CSE', 2023), " +
                        "('stu2', '2023045', 'B.Tech CSD', 2023), " +
                        "('stu3', '2023102', 'B.Tech CSB', 2023)");

                // IIIT-Delhi Instructors
                erpStmt.execute("INSERT INTO instructors VALUES " +
                        "('inst1', 'CSE'), " +
                        "('inst2', 'ECE')");

                // IIIT-Delhi Courses
                erpStmt.execute("INSERT INTO courses VALUES " +
                        "('CSE101', 'CSE101', 'Introduction to Programming', 4), " +
                        "('CSE102', 'CSE102', 'Data Structures and Algorithms', 4), " +
                        "('CSE201', 'CSE201', 'Advanced Programming', 4), " +
                        "('ECE111', 'ECE111', 'Digital Circuits', 4), " +
                        "('MTH100', 'MTH100', 'Linear Algebra', 4), " +
                        "('DES101', 'DES101', 'Introduction to Design', 4), " +
                        "('COM101', 'COM101', 'Communication Skills', 4)");

                // IIIT-Delhi Sections (Monsoon Semester)
                erpStmt.execute("INSERT INTO sections VALUES " +
                        "('SEC_IP_01', 'CSE101', 'inst1', 'Mon Wed 10:00-11:30', 'C01', 60, 'Monsoon', 2024), " +
                        "('SEC_AP_01', 'CSE201', 'inst1', 'Tue Thu 14:00-15:30', 'C02', 55, 'Monsoon', 2024), " +
                        "('SEC_DC_01', 'ECE111', 'inst2', 'Mon Wed 11:30-13:00', 'C11', 50, 'Monsoon', 2024)");

                // Enrollments
                erpStmt.execute("INSERT INTO enrollments VALUES " +
                        "('ENR001', 'stu1', 'SEC_IP_01', 'registered'), " +
                        "('ENR002', 'stu2', 'SEC_IP_01', 'registered'), " +
                        "('ENR003', 'stu1', 'SEC_AP_01', 'registered')");

                // Settings
                erpStmt.execute("INSERT INTO settings VALUES ('maintenance_on', 'false')");

                System.out.println("✅ ERP data inserted (IIIT-Delhi configuration)");
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

    public static void main(String[] args) {
        System.out.println("⚠️ Manually starting database initialization...");
        initializeDatabase();
        System.out.println("✅ Database reset complete.");
    }
}