package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public static Student findByUserId(String userId) throws SQLException {
        String sql = "SELECT user_id, roll_no, program, year FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getString("user_id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setProgram(rs.getString("program"));
                student.setYear(rs.getInt("year"));
                return student;
            }
        }
        return null;
    }

    public static boolean createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getUserId());
            stmt.setString(2, student.getRollNo());
            stmt.setString(3, student.getProgram());
            stmt.setInt(4, student.getYear());
            return stmt.executeUpdate() > 0;
        }
    }

    public static List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT user_id, roll_no, program, year FROM students";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getString("user_id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setProgram(rs.getString("program"));
                student.setYear(rs.getInt("year"));
                students.add(student);
            }
        }
        return students;
    }
}