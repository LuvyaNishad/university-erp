package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public static int insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (rollno, userid, name, email, phone, program, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, student.getRollNo());
            stmt.setInt(2, student.getUserId());
            stmt.setString(3, student.getName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getProgram());
            stmt.setInt(7, student.getYear());
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Insert student failed.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID obtained.");
            }
        }
    }

    public static Student findByRollNo(int rollNo) throws SQLException {
        String sql = "SELECT rollno, userid, name, email, phone, program, year FROM students WHERE rollno = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rollNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setRollNo(rs.getInt("rollno"));
                s.setUserId(rs.getInt("userid"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setProgram(rs.getString("program"));
                s.setYear(rs.getInt("year"));
                return s;
            }
            return null;
        }
    }

    public static void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET userid=?, name=?, email=?, phone=?, program=?, year=? WHERE rollno=?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setString(5, student.getProgram());
            stmt.setInt(6, student.getYear());
            stmt.setInt(7, student.getRollNo());
            stmt.executeUpdate();
        }
    }

    public static void deleteStudent(int rollNo) throws SQLException {
        String sql = "DELETE FROM students WHERE rollno = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rollNo);
            stmt.executeUpdate();
        }
    }

    public static List<Student> listAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT rollno, userid, name, email, phone, program, year FROM students";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Student s = new Student();
                s.setRollNo(rs.getInt("rollno"));
                s.setUserId(rs.getInt("userid"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setProgram(rs.getString("program"));
                s.setYear(rs.getInt("year"));
                list.add(s);
            }
        }
        return list;
    }
}
