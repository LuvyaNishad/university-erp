package edu.univ.erp.service;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.data.GradeDAO;
import java.sql.SQLException;
import java.util.List;

public class GradeService {

    public int addGrade(Grade grade) throws SQLException {
        return GradeDAO.insertGrade(grade);
    }

    public Grade getGrade(int gradeId) throws SQLException {
        return GradeDAO.findById(gradeId);
    }

    public void updateGrade(Grade grade) throws SQLException {
        GradeDAO.updateGrade(grade);
    }

    public void deleteGrade(int gradeId) throws SQLException {
        GradeDAO.deleteGrade(gradeId);
    }

    public List<Grade> getAllGrades() throws SQLException {
        return GradeDAO.listAll();
    }
}
