package edu.univ.erp.service;

import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.Grade;
import java.sql.SQLException;
import java.util.List;

public class GradeService {

    public List<Grade> getGradesByEnrollment(String enrollmentId) throws SQLException {
        return GradeDAO.getGradesByEnrollment(enrollmentId);
    }

    public boolean saveGrade(Grade grade) throws SQLException {
        return GradeDAO.saveGrade(grade);
    }

    public boolean updateGrade(Grade grade) throws SQLException {
        return GradeDAO.updateGrade(grade);
    }
}