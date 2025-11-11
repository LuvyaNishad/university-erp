package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public static int insertEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (rollno, sectionid, enrollmentstatus, enrollmentdate, dropdate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, enrollment.getRollNo());
            stmt.setInt(2, enrollment.getSectionId());
            stmt.setString(3, enrollment.getEnrollmentStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(enrollment.getEnrollmentDate()));
            if (enrollment.getDropDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(enrollment.getDropDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Insert enrollment failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated.");
            }
        }
    }

    public static Enrollment findById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE enrollmentid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentId(rs.getInt("enrollmentid"));
                e.setRollNo(rs.getInt("rollno"));
                e.setSectionId(rs.getInt("sectionid"));
                e.setEnrollmentStatus(rs.getString("enrollmentstatus"));
                Timestamp enrollmentDate = rs.getTimestamp("enrollmentdate");
                e.setEnrollmentDate(enrollmentDate.toLocalDateTime());
                Timestamp dropDate = rs.getTimestamp("dropdate");
                if (dropDate != null) e.setDropDate(dropDate.toLocalDateTime());
                return e;
            }
        }
        return null;
    }

    public static void updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET rollno=?, sectionid=?, enrollmentstatus=?, enrollmentdate=?, dropdate=? WHERE enrollmentid=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollment.getRollNo());
            stmt.setInt(2, enrollment.getSectionId());
            stmt.setString(3, enrollment.getEnrollmentStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(enrollment.getEnrollmentDate()));
            if (enrollment.getDropDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(enrollment.getDropDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setInt(6, enrollment.getEnrollmentId());
            stmt.executeUpdate();
        }
    }

    public static void deleteEnrollment(int id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollmentid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Enrollment> listAll() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentId(rs.getInt("enrollmentid"));
                e.setRollNo(rs.getInt("rollno"));
                e.setSectionId(rs.getInt("sectionid"));
                e.setEnrollmentStatus(rs.getString("enrollmentstatus"));
                Timestamp enrollmentDate = rs.getTimestamp("enrollmentdate");
                e.setEnrollmentDate(enrollmentDate.toLocalDateTime());
                Timestamp dropDate = rs.getTimestamp("dropdate");
                if (dropDate != null) e.setDropDate(dropDate.toLocalDateTime());
                list.add(e);
            }
        }
        return list;
    }
}
