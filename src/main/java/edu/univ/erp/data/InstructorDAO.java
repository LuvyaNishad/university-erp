package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public static Instructor findByUserId(String userId) throws SQLException {
        String sql = "SELECT user_id, department FROM instructors WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getString("user_id"));
                instructor.setDepartment(rs.getString("department"));
                return instructor;
            }
        }
        return null;
    }

    public static boolean createInstructor(Instructor instructor) throws SQLException {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, instructor.getUserId());
            stmt.setString(2, instructor.getDepartment());
            return stmt.executeUpdate() > 0;
        }
    }

    public static List<Instructor> getAllInstructors() throws SQLException {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT user_id, department FROM instructors";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getString("user_id"));
                instructor.setDepartment(rs.getString("department"));
                instructors.add(instructor);
            }
        }
        return instructors;
    }
}