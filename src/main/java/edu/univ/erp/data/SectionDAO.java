package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public static int insertSection(Section section) throws SQLException {
        String sql = "INSERT INTO sections (courseid, sectionname, instructorid, semester, year, maxcapacity, currentenrollment, schedule, room) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, section.getCourseId());
            stmt.setString(2, section.getSectionName());
            stmt.setInt(3, section.getInstructorId());
            stmt.setString(4, section.getSemester());
            stmt.setInt(5, section.getYear());
            stmt.setInt(6, section.getMaxCapacity());
            stmt.setInt(7, section.getCurrentEnrollment());
            stmt.setString(8, section.getSchedule());
            stmt.setString(9, section.getRoom());
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Insert section failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated.");
            }
        }
    }

    public static Section findById(int id) throws SQLException {
        String sql = "SELECT * FROM sections WHERE sectionid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Section s = new Section();
                s.setSectionId(rs.getInt("sectionid"));
                s.setCourseId(rs.getInt("courseid"));
                s.setSectionName(rs.getString("sectionname"));
                s.setInstructorId(rs.getInt("instructorid"));
                s.setSemester(rs.getString("semester"));
                s.setYear(rs.getInt("year"));
                s.setMaxCapacity(rs.getInt("maxcapacity"));
                s.setCurrentEnrollment(rs.getInt("currentenrollment"));
                s.setSchedule(rs.getString("schedule"));
                s.setRoom(rs.getString("room"));
                return s;
            }
        }
        return null;
    }

    public static void updateSection(Section section) throws SQLException {
        String sql = "UPDATE sections SET courseid=?, sectionname=?, instructorid=?, semester=?, year=?, maxcapacity=?, currentenrollment=?, schedule=?, room=? WHERE sectionid=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, section.getCourseId());
            stmt.setString(2, section.getSectionName());
            stmt.setInt(3, section.getInstructorId());
            stmt.setString(4, section.getSemester());
            stmt.setInt(5, section.getYear());
            stmt.setInt(6, section.getMaxCapacity());
            stmt.setInt(7, section.getCurrentEnrollment());
            stmt.setString(8, section.getSchedule());
            stmt.setString(9, section.getRoom());
            stmt.setInt(10, section.getSectionId());
            stmt.executeUpdate();
        }
    }

    public static void deleteSection(int id) throws SQLException {
        String sql = "DELETE FROM sections WHERE sectionid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Section> listAll() throws SQLException {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Section s = new Section();
                s.setSectionId(rs.getInt("sectionid"));
                s.setCourseId(rs.getInt("courseid"));
                s.setSectionName(rs.getString("sectionname"));
                s.setInstructorId(rs.getInt("instructorid"));
                s.setSemester(rs.getString("semester"));
                s.setYear(rs.getInt("year"));
                s.setMaxCapacity(rs.getInt("maxcapacity"));
                s.setCurrentEnrollment(rs.getInt("currentenrollment"));
                s.setSchedule(rs.getString("schedule"));
                s.setRoom(rs.getString("room"));
                list.add(s);
            }
        }
        return list;
    }
}
