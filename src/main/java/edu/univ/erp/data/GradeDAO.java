package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public static int insertGrade(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades (enrollmentid, midterm_score, final_score, assignment_score, final_grade, grade_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, grade.getEnrollmentId());
            if (grade.getMidtermScore() == null) stmt.setNull(2, Types.DOUBLE);
            else stmt.setDouble(2, grade.getMidtermScore());
            if (grade.getFinalScore() == null) stmt.setNull(3, Types.DOUBLE);
            else stmt.setDouble(3, grade.getFinalScore());
            if (grade.getAssignmentScore() == null) stmt.setNull(4, Types.DOUBLE);
            else stmt.setDouble(4, grade.getAssignmentScore());
            stmt.setString(5, grade.getFinalGrade());
            if (grade.getGradeDate() == null) stmt.setNull(6, Types.TIMESTAMP);
            else stmt.setTimestamp(6, Timestamp.valueOf(grade.getGradeDate()));
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Insert grade failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated.");
            }
        }
    }

    public static Grade findById(int id) throws SQLException {
        String sql = "SELECT * FROM grades WHERE gradeid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Grade g = new Grade();
                g.setGradeId(rs.getInt("gradeid"));
                g.setEnrollmentId(rs.getInt("enrollmentid"));
                g.setMidtermScore(rs.getDouble("midterm_score"));
                g.setFinalScore(rs.getDouble("final_score"));
                g.setAssignmentScore(rs.getDouble("assignment_score"));
                g.setFinalGrade(rs.getString("final_grade"));
                Timestamp gradeDate = rs.getTimestamp("grade_date");
                if (gradeDate != null) g.setGradeDate(gradeDate.toLocalDateTime());
                return g;
            }
        }
        return null;
    }

    public static void updateGrade(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET enrollmentid=?, midterm_score=?, final_score=?, assignment_score=?, final_grade=?, grade_date=? WHERE gradeid=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade.getEnrollmentId());
            if (grade.getMidtermScore() == null) stmt.setNull(2, Types.DOUBLE);
            else stmt.setDouble(2, grade.getMidtermScore());
            if (grade.getFinalScore() == null) stmt.setNull(3, Types.DOUBLE);
            else stmt.setDouble(3, grade.getFinalScore());
            if (grade.getAssignmentScore() == null) stmt.setNull(4, Types.DOUBLE);
            else stmt.setDouble(4, grade.getAssignmentScore());
            stmt.setString(5, grade.getFinalGrade());
            if (grade.getGradeDate() == null) stmt.setNull(6, Types.TIMESTAMP);
            else stmt.setTimestamp(6, Timestamp.valueOf(grade.getGradeDate()));
            stmt.setInt(7, grade.getGradeId());
            stmt.executeUpdate();
        }
    }

    public static void deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE gradeid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Grade> listAll() throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Grade g = new Grade();
                g.setGradeId(rs.getInt("gradeid"));
                g.setEnrollmentId(rs.getInt("enrollmentid"));
                g.setMidtermScore(rs.getDouble("midterm_score"));
                g.setFinalScore(rs.getDouble("final_score"));
                g.setAssignmentScore(rs.getDouble("assignment_score"));
                g.setFinalGrade(rs.getString("final_grade"));
                Timestamp gradeDate = rs.getTimestamp("grade_date");
                if (gradeDate != null) g.setGradeDate(gradeDate.toLocalDateTime());
                list.add(g);
            }
        }
        return list;
    }
}
