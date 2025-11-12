package edu.univ.erp.service;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.data.EnrollmentDAO;
import java.sql.SQLException;
import java.util.List;

public class EnrollmentService {

    public int addEnrollment(Enrollment enrollment) throws SQLException {
        return EnrollmentDAO.insertEnrollment(enrollment);
    }

    public Enrollment getEnrollment(int enrollmentId) throws SQLException {
        return EnrollmentDAO.findById(enrollmentId);
    }

    public void updateEnrollment(Enrollment enrollment) throws SQLException {
        EnrollmentDAO.updateEnrollment(enrollment);
    }

    public void deleteEnrollment(int enrollmentId) throws SQLException {
        EnrollmentDAO.deleteEnrollment(enrollmentId);
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return EnrollmentDAO.listAll();
    }
}
