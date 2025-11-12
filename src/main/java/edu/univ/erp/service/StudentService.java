package edu.univ.erp.service;

import edu.univ.erp.domain.Student;
import edu.univ.erp.data.StudentDAO;
import java.sql.SQLException;
import java.util.List;

public class StudentService {

    public int registerStudent(Student student) throws SQLException {
        return StudentDAO.insertStudent(student);
    }

    public Student getStudent(int rollNo) throws SQLException {
        return StudentDAO.findByRollNo(rollNo);
    }

    public void updateStudent(Student student) throws SQLException {
        StudentDAO.updateStudent(student);
    }

    public void deleteStudent(int rollNo) throws SQLException {
        StudentDAO.deleteStudent(rollNo);
    }

    public List<Student> getAllStudents() throws SQLException {
        return StudentDAO.listAll();
    }
}
