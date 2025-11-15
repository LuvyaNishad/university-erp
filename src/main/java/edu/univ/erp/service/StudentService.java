package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for handling student-specific business logic.
 * This file was missing and has been reconstructed.
 */
public class StudentService {

    /**
     * Gets all enrollments for a specific student.
     * (Called by MyGradesWindow, MyTimetableWindow, TranscriptWindow)
     */
    public List<Enrollment> getStudentEnrollments(String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("view_enrollments", studentId)) {
            throw new SecurityException("Access denied: Cannot view enrollments for this user.");
        }
        // EnrollmentDAO.getEnrollmentsByStudent already joins course code and title
        return EnrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    /**
     * Gets all available sections for registration.
     * (Called by CourseCatalogWindow)
     */
    public List<Section> getAvailableSections() throws SQLException {
        // We must use the corrected SectionDAO.getAllSections() which joins all required fields
        List<Section> sections = SectionDAO.getAllSections();

        // A good service layer populates transient fields like currentEnrollment
        for (Section s : sections) {
            int currentEnrollment = SectionDAO.getCurrentEnrollment(s.getSectionId());
            s.setCurrentEnrollment(currentEnrollment);
        }
        return sections;
    }

    /**
     * Registers a student for a specific section.
     * (Called by CourseCatalogWindow)
     */
    public boolean registerForSection(String studentId, String sectionId) throws SQLException {
        if (!AccessControl.isActionAllowed("register", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }

        // 1. Check for duplicates
        if (EnrollmentDAO.isStudentEnrolled(studentId, sectionId)) {
            throw new SQLException("You are already enrolled in this section.");
        }

        // 2. Check capacity. We need a way to get a single section.
        // Since SectionDAO has no findById, we must get all and filter.
        List<Section> allSections = SectionDAO.getAllSections(); // Inefficient, but matches available DAOs
        Section targetSection = allSections.stream()
                .filter(s -> s.getSectionId().equals(sectionId))
                .findFirst()
                .orElse(null);

        if (targetSection == null) {
            throw new SQLException("Section " + sectionId + " not found.");
        }

        int currentEnrollment = SectionDAO.getCurrentEnrollment(sectionId);
        if (currentEnrollment >= targetSection.getCapacity()) {
            throw new SQLException("Registration failed: Section is full.");
        }

        // 3. Enroll
        return EnrollmentDAO.enrollStudent(studentId, sectionId);
    }

    /**
     * Drops a student from a section.
     * (Called by MyTimetableWindow)
     */
    public boolean dropSection(String enrollmentId, String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("drop", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }
        // Note: A more robust check would verify that the studentId owns this enrollmentId
        return EnrollmentDAO.dropEnrollment(enrollmentId);
    }

    /**
     * Gets a student's profile information.
     */
    public Student getStudentProfile(String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("view_profile", studentId)) {
            throw new SecurityException("Access denied.");
        }
        return StudentDAO.findByUserId(studentId);
    }
}