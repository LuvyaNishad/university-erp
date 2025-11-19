package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String AUTH_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/university_auth";
    private static final String ERP_DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/university_erp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123"; // Make sure this matches your MySQL password

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
        createTablesAndData();
    }

    private static void createDatabases() {
        String baseUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/";

        try (Connection conn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            System.out.println("🚀 Creating databases...");
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_auth");
            stmt.execute("CREATE DATABASE IF NOT EXISTS university_erp");
            System.out.println("✅ Databases created successfully");

        } catch (SQLException e) {
            System.err.println("❌ Database creation failed: " + e.getMessage());
        }
    }

    private static void createTablesAndData() {
        try (Connection conn = getErpConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("📦 Creating tables and sample data...");

            // Create Auth DB tables
            stmt.execute("USE university_auth");
            stmt.execute("CREATE TABLE IF NOT EXISTS users_auth (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'active', " +
                    "last_login TIMESTAMP NULL)");

            // Create ERP DB tables
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

            // Insert sample data with REAL working password hashes
            insertSampleData();

            System.out.println("✅ Tables and sample data created successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Table creation failed: " + e.getMessage());
        }
    }

    private static void insertSampleData() {
        try {
            // Insert into Auth DB with REAL password hashes
            try (Connection authConn = getAuthConnection();
                 Statement authStmt = authConn.createStatement()) {

                // Clear any existing data
                authStmt.execute("DELETE FROM users_auth");

                // Insert with REAL working BCrypt hashes
                // admin123, student123, instructor123
                authStmt.execute("INSERT INTO users_auth (user_id, username, role, password_hash) VALUES " +
                        "('admin1', 'admin1', 'admin', '$2a$10$jOE.4.rG3UqQe1vQszmWUOVfxMQhdROJAxLw42q2ZtuSa72ue02.O'), " +
                        "('stu1', 'stu1', 'student', '$2a$10$ngaHhz716f5IKnRvDe8B0elogj5rqTU1SxLQyfYisqkvjvps1VnmC'), " +
                        "('stu2', 'stu2', 'student', '$2a$10$obITtpGvaLOueUzNRQGzFOXaljtNV3u0OJFWiXE1eGL5mt.lLF4PS'), " +
                        "('inst1', 'inst1', 'instructor', '$2a$10$obITtpGvaLOueUzNRQGzFOXaljtNV3u0OJFWiXE1eGL5mt.lLF4PS')");

                System.out.println("✅ Auth data inserted with REAL password hashes");
            }

            // Insert into ERP DB
            try (Connection erpConn = getErpConnection();
                 Statement erpStmt = erpConn.createStatement()) {

                // --- FIX IS HERE: Clear existing data in CORRECT ORDER ---
                // Must delete child tables (grades, enrollments) before parents (students, sections)
                // to avoid Foreign Key errors.
                erpStmt.execute("DELETE FROM grades");
                erpStmt.execute("DELETE FROM enrollments");
                erpStmt.execute("DELETE FROM sections");
                erpStmt.execute("DELETE FROM students");
                erpStmt.execute("DELETE FROM instructors");
                erpStmt.execute("DELETE FROM courses");
                erpStmt.execute("DELETE FROM settings");

                // Insert sample data
                erpStmt.execute("INSERT INTO students (user_id, roll_no, program, year) VALUES " +
                        "('stu1', '2023001', 'Computer Science', 2023), " +
                        "('stu2', '2023002', 'Computer Science', 2023)");

                erpStmt.execute("INSERT INTO instructors (user_id, department) VALUES " +
                        "('inst1', 'Computer Science')");

                erpStmt.execute("INSERT INTO courses (course_id, code, title, credits) VALUES " +
                        "('CS101', 'CS101', 'Introduction to Programming', 4), " +
                        "('CS201', 'CS201', 'Data Structures', 4)");

                erpStmt.execute("INSERT INTO sections (section_id, course_id, instructor_id, day_time, room, capacity, semester, year) VALUES " +
                        "('SEC001', 'CS101', 'inst1', 'Mon Wed 10:00-11:30', 'Room 101', 30, 'Fall', 2024), " +
                        "('SEC002', 'CS201', 'inst1', 'Tue Thu 14:00-15:30', 'Room 102', 25, 'Fall', 2024)");

                erpStmt.execute("INSERT INTO enrollments (enrollment_id, student_id, section_id, status) VALUES " +
                        "('ENR001', 'stu1', 'SEC001', 'registered'), " +
                        "('ENR002', 'stu2', 'SEC001', 'registered')");

                // Insert default settings (Maintenance Mode OFF)
                // Using INSERT IGNORE or REPLACE wouldn't hurt, but simple INSERT is fine now that we DELETE first.
                erpStmt.execute("INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_on', 'false')");

                System.out.println("✅ ERP data inserted successfully");
            }

            System.out.println("✅ All sample data inserted successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Sample data insertion failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean testConnections() {
        try (Connection authConn = getAuthConnection();
             Connection erpConn = getErpConnection()) {
            System.out.println("✅ MySQL Database Connections: SUCCESS");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ MySQL Connection Failed: " + e.getMessage());
            System.out.println("💡 Check if: MySQL is running and password is correct");
            return false;
        }
    }

    // Test method
    public static void main(String[] args) {
        System.out.println("🧪 Testing Database Connection...");
        boolean success = testConnections();
        if (success) {
            System.out.println("🎉 Database setup complete! Ready for login.");
        } else {
            System.out.println("❌ Database setup failed. Check MySQL installation.");
        }
    }
}