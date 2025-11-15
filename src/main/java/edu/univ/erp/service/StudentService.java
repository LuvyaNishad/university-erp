package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public static List<Enrollment> getEnrollmentsByStudent(String studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();

        // --- MODIFIED SQL QUERY ---
        // Added c.title as course_title
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

                // --- SET NEW FIELD ---
                enrollment.setCourseTitle(rs.getString("course_title"));

                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }

    // ... enrollStudent(...) method is unchanged ...
    public static boolean enrollStudent(String studentId, String sectionId) throws SQLException {
        // ... (existing code)
    }

    // ... dropEnrollment(...) method is unchanged ...
    public static boolean dropEnrollment(String enrollmentId) throws SQLException {
        // ... (existing code)
    }

    // ... isStudentEnrolled(...) method is unchanged ...
    public static boolean isStudentEnrolled(String studentId, String sectionId) throws SQLException {
        // ... (existing code)
    }

    // ... getEnrollmentsBySection(...) method is unchanged ...
    public static List<Enrollment> getEnrollmentsBySection(String sectionId) throws SQLException {
        // ... (existing code)
    }
}