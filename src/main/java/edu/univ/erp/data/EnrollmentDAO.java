package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public static List<Enrollment> getEnrollmentsByStudent(String studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();

        // --- THIS SQL IS CORRECT ---
        // It joins 'courses' and selects 'c.title as course_title'
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, " +
                "c.code as course_code, c.title as course_title, s.day_time as section_info " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(rs.getString("enrollment_id"));
                enrollment.setStudentId(rs.getString("student_id"));
                enrollment.setSectionId(rs.getString("section_id"));
                enrollment.setStatus(rs.getString("status"));
                enrollment.setCourseCode(rs.getString("course_code"));
                enrollment.setSectionInfo(rs.getString("section_info"));

                // --- THIS SETTER IS CORRECT ---
                // It populates the field added to Enrollment.java
                enrollment.setCourseTitle(rs.getString("course_title"));

                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }

    public static boolean enrollStudent(String studentId, String sectionId) throws SQLException {
        String sql = "INSERT INTO enrollments (enrollment_id, student_id, section_id, status) VALUES (?, ?, ?, 'registered')";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Generate a unique ID for the enrollment
            String enrollmentId = "ENR_" + System.currentTimeMillis();
            stmt.setString(1, enrollmentId);
            stmt.setString(2, studentId);
            stmt.setString(3, sectionId);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean dropEnrollment(String enrollmentId) throws SQLException {
        String sql = "UPDATE enrollments SET status = 'dropped' WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, enrollmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean isStudentEnrolled(String studentId, String sectionId) throws SQLException {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'registered'";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, sectionId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a record is found
        }
    }

    public static List<Enrollment> getEnrollmentsBySection(String sectionId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        // This query is for instructors, so it joins 'students' to get their details
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, s.roll_no, s.program " +
                "FROM enrollments e JOIN students s ON e.student_id = s.user_id " +
                "WHERE e.section_id = ? AND e.status = 'registered'";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Enrollment en = new Enrollment();
                en.setEnrollmentId(rs.getString("enrollment_id"));
                en.setStudentId(rs.getString("student_id"));
                en.setSectionId(rs.getString("section_id"));
                en.setStatus(rs.getString("status"));
                // This sets the student's name for the instructor's gradebook
                en.setStudentName("Roll No: " + rs.getString("roll_no") + " - " + rs.getString("program"));
                enrollments.add(en);
            }
        }
        return enrollments;
    }
}