package edu.univ.erp.service;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;
import java.sql.SQLException;
import java.util.List;

public class SectionService {

    public List<Section> getAllSections() throws SQLException {
        return SectionDAO.getAllSections();
    }

    public List<Section> getSectionsByInstructor(String instructorId) throws SQLException {
        return SectionDAO.getSectionsByInstructor(instructorId);
    }

    public boolean createSection(Section section) throws SQLException {
        return SectionDAO.createSection(section);
    }

    public int getCurrentEnrollment(String sectionId) throws SQLException {
        return SectionDAO.getCurrentEnrollment(sectionId);
    }
}