package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GradeDAO {

    public static List<Grade> getGradesByEnrollment(String enrollmentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT grade_id, enrollment_id, component, score, final_grade FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade();
                grade.setGradeId(rs.getString("grade_id"));
                grade.setEnrollmentId(rs.getString("enrollment_id"));
                grade.setComponent(rs.getString("component"));
                grade.setScore(rs.getDouble("score"));
                grade.setFinalGrade(rs.getString("final_grade"));
                grades.add(grade);
            }
        }
        return grades;
    }

    public static boolean saveGrade(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades (grade_id, enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // FIX: Use UUID to prevent ID collision during bulk save loop
            String gradeId = "GRD_" + UUID.randomUUID().toString();
            stmt.setString(1, gradeId);
            stmt.setString(2, grade.getEnrollmentId());
            stmt.setString(3, grade.getComponent());
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getFinalGrade());
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean updateGrade(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET component = ?, score = ?, final_grade = ? WHERE grade_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grade.getComponent());
            stmt.setDouble(2, grade.getScore());
            stmt.setString(3, grade.getFinalGrade());
            stmt.setString(4, grade.getGradeId());
            return stmt.executeUpdate() > 0;
        }
    }
}