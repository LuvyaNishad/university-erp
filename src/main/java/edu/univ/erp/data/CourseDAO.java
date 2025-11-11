package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public static int insertCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (coursecode, coursename, credits, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDescription());
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Insert course failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated.");
            }
        }
    }

    public static Course findById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE courseid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("courseid"));
                c.setCourseCode(rs.getString("coursecode"));
                c.setCourseName(rs.getString("coursename"));
                c.setCredits(rs.getInt("credits"));
                c.setDescription(rs.getString("description"));
                return c;
            }
        }
        return null;
    }

    public static void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET coursecode=?, coursename=?, credits=?, description=? WHERE courseid=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDescription());
            stmt.setInt(5, course.getCourseId());
            stmt.executeUpdate();
        }
    }

    public static void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE courseid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Course> listAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("courseid"));
                c.setCourseCode(rs.getString("coursecode"));
                c.setCourseName(rs.getString("coursename"));
                c.setCredits(rs.getInt("credits"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        }
        return list;
    }
}
