package edu.univ.erp.service;

import edu.univ.erp.domain.Section;
import edu.univ.erp.data.SectionDAO;
import java.sql.SQLException;
import java.util.List;

public class SectionService {

    public int addSection(Section section) throws SQLException {
        return SectionDAO.insertSection(section);
    }

    public Section getSection(int sectionId) throws SQLException {
        return SectionDAO.findById(sectionId);
    }

    public void updateSection(Section section) throws SQLException {
        SectionDAO.updateSection(section);
    }

    public void deleteSection(int sectionId) throws SQLException {
        SectionDAO.deleteSection(sectionId);
    }

    public List<Section> getAllSections() throws SQLException {
        return SectionDAO.listAll();
    }
}
