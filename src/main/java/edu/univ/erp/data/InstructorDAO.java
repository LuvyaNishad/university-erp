package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public static int insertInstructor(Instructor ins) throws SQLException {
        String sql = "INSERT INTO instructors (userid, name, email, phone, department) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ins.getUserId());
            stmt.setString(2, ins.getName());
            stmt.setString(3, ins.getEmail());
            stmt.setString(4, ins.getPhone());
            stmt.setString(5, ins.getDepartment());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Insert instructor failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No generated key.");
            }
        }
    }

    public static Instructor findById(int id) throws SQLException {
        String sql = "SELECT * FROM instructors WHERE instructorid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Instructor ins = new Instructor();
                ins.setInstructorId(rs.getInt("instructorid"));
                ins.setUserId(rs.getInt("userid"));
                ins.setName(rs.getString("name"));
                ins.setEmail(rs.getString("email"));
                ins.setPhone(rs.getString("phone"));
                ins.setDepartment(rs.getString("department"));
                return ins;
            }
        }
        return null;
    }

    public static void updateInstructor(Instructor ins) throws SQLException {
        String sql = "UPDATE instructors SET userid=?, name=?, email=?, phone=?, department=? WHERE instructorid=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ins.getUserId());
            stmt.setString(2, ins.getName());
            stmt.setString(3, ins.getEmail());
            stmt.setString(4, ins.getPhone());
            stmt.setString(5, ins.getDepartment());
            stmt.setInt(6, ins.getInstructorId());
            stmt.executeUpdate();
        }
    }

    public static void deleteInstructor(int id) throws SQLException {
        String sql = "DELETE FROM instructors WHERE instructorid = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Instructor> listAll() throws SQLException {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT * FROM instructors";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Instructor ins = new Instructor();
                ins.setInstructorId(rs.getInt("instructorid"));
                ins.setUserId(rs.getInt("userid"));
                ins.setName(rs.getString("name"));
                ins.setEmail(rs.getString("email"));
                ins.setPhone(rs.getString("phone"));
                ins.setDepartment(rs.getString("department"));
                list.add(ins);
            }
        }
        return list;
    }
}
