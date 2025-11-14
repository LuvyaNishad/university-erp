package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Enrollment;
import java.sql.SQLException;
import java.util.List;

public class EnrollmentService {

    public List<Enrollment> getEnrollmentsByStudent(String studentId) throws SQLException {
        return EnrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public boolean enrollStudent(String studentId, String sectionId) throws SQLException {
        return EnrollmentDAO.enrollStudent(studentId, sectionId);
    }

    public boolean dropEnrollment(String enrollmentId) throws SQLException {
        return EnrollmentDAO.dropEnrollment(enrollmentId);
    }
}