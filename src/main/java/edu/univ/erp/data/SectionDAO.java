package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public static List<Section> getAllSections() throws SQLException {
        List<Section> sections = new ArrayList<>();
        // --- MODIFIED SQL QUERY ---
        // Added c.code, c.credits, and a LEFT JOIN for instructor name
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
                "s.capacity, s.semester, s.year, " +
                "c.code as course_code, c.title as course_title, c.credits, " +
                "i.user_id as instructor_name " + // Using user_id as name, as no 'name' field exists
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id"; // LEFT JOIN in case instructor is unassigned

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Section section = new Section();
                section.setSectionId(rs.getString("section_id"));
                section.setCourseId(rs.getString("course_id"));
                section.setInstructorId(rs.getString("instructor_id"));
                section.setDayTime(rs.getString("day_time"));
                section.setRoom(rs.getString("room"));
                section.setCapacity(rs.getInt("capacity"));
                section.setSemester(rs.getString("semester"));
                section.setYear(rs.getInt("year"));

                // --- SET NEW FIELDS ---
                section.setCourseCode(rs.getString("course_code"));
                section.setCourseTitle(rs.getString("course_title"));
                section.setCredits(rs.getInt("credits"));
                section.setInstructorName(rs.getString("instructor_name")); // This is the instructor's user_id

                sections.add(section);
            }
        }
        return sections;
    }

    public static List<Section> getSectionsByInstructor(String instructorId) throws SQLException {
        List<Section> sections = new ArrayList<>();
        // --- MODIFIED SQL QUERY ---
        // Added c.code, c.credits, and instructor name (which is just the ID here)
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
                "s.capacity, s.semester, s.year, " +
                "c.code as course_code, c.title as course_title, c.credits " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE s.instructor_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Section section = new Section();
                section.setSectionId(rs.getString("section_id"));
                section.setCourseId(rs.getString("course_id"));
                section.setInstructorId(rs.getString("instructor_id"));
                section.setDayTime(rs.getString("day_time"));
                section.setRoom(rs.getString("room"));
                section.setCapacity(rs.getInt("capacity"));
                section.setSemester(rs.getString("semester"));
                section.setYear(rs.getInt("year"));

                // --- SET NEW FIELDS ---
                section.setCourseCode(rs.getString("course_code"));
                section.setCourseTitle(rs.getString("course_title"));
                section.setCredits(rs.getInt("credits"));
                section.setInstructorName(rs.getString("instructor_id")); // Set name as the ID

                sections.add(section);
            }
        }
        return sections;
    }

    public static boolean createSection(Section section) throws SQLException {
        String sql = "INSERT INTO sections (section_id, course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, section.getSectionId());
            stmt.setString(2, section.getCourseId());
            stmt.setString(3, section.getInstructorId());
            stmt.setString(4, section.getDayTime());
            stmt.setString(5, section.getRoom());
            stmt.setInt(6, section.getCapacity());
            stmt.setString(7, section.getSemester());
            stmt.setInt(8, section.getYear());
            return stmt.executeUpdate() > 0;
        }
    }

    public static int getCurrentEnrollment(String sectionId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM enrollments WHERE section_id = ? AND status = 'registered'";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}