package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public static List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, code, title, credits FROM courses";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getString("course_id"));
                course.setCode(rs.getString("code"));
                course.setTitle(rs.getString("title"));
                course.setCredits(rs.getInt("credits"));
                courses.add(course);
            }
        }
        return courses;
    }

    public static boolean createCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_id, code, title, credits) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCode());
            stmt.setString(3, course.getTitle());
            stmt.setInt(4, course.getCredits());
            return stmt.executeUpdate() > 0;
        }
    }

    public static Course findById(String courseId) throws SQLException {
        String sql = "SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getString("course_id"));
                course.setCode(rs.getString("code"));
                course.setTitle(rs.getString("title"));
                course.setCredits(rs.getInt("credits"));
                return course;
            }
        }
        return null;
    }
}