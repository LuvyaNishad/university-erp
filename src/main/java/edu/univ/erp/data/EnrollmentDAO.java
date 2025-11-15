package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public static List<Enrollment> getEnrollmentsByStudent(String studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();

        // --- THIS IS THE CORRECTED QUERY ---
        // It now selects "c.title as course_title"
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, " +
                "c.code as course_code, c.title as course_title, s.day_time as section_info " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " + // <-- Correct JOIN
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

                // --- THIS IS THE CORRESPONDING FIX ---
                // This line sets the title that was fetched from the DB
                enrollment.setCourseTitle(rs.getString("course_title"));

                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }

    // ... other methods (enrollStudent, dropEnrollment, etc.) ...

    public static boolean enrollStudent(String studentId, String sectionId) throws SQLException {
        // This implementation seems to be missing from the provided file,
        // but it would go here.
        String sql = "INSERT INTO enrollments (enrollment_id, student_id, section_id, status) VALUES (?, ?, ?, 'registered')";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            return rs.next();
        }
    }

    public static List<Enrollment> getEnrollmentsBySection(String sectionId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        // This query joins with students to get student details for the instructor's view
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
                // Set studentName to a combo of roll_no and program for the instructor
                en.setStudentName("Roll No: " + rs.getString("roll_no") + " - " + rs.getString("program"));
                enrollments.add(en);
            }
        }
        return enrollments;
    }
}