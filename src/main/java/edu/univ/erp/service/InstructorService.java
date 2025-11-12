package edu.univ.erp.service;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.data.InstructorDAO;
import java.sql.SQLException;
import java.util.List;

public class InstructorService {

    public int addInstructor(Instructor instructor) throws SQLException {
        return InstructorDAO.insertInstructor(instructor);
    }

    public Instructor getInstructor(int instructorId) throws SQLException {
        return InstructorDAO.findById(instructorId);
    }

    public void updateInstructor(Instructor instructor) throws SQLException {
        InstructorDAO.updateInstructor(instructor);
    }

    public void deleteInstructor(int instructorId) throws SQLException {
        InstructorDAO.deleteInstructor(instructorId);
    }

    public List<Instructor> getAllInstructors() throws SQLException {
        return InstructorDAO.listAll();
    }
}
