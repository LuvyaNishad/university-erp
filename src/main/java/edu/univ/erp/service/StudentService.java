package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentService {

    public List<Enrollment> getStudentEnrollments(String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("view_enrollments", studentId)) {
            throw new SecurityException("Access denied: Cannot view enrollments for this user.");
        }
        return EnrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public List<Section> getAvailableSections() throws SQLException {
        List<Section> sections = SectionDAO.getAllSections();
        for (Section s : sections) {
            int currentEnrollment = SectionDAO.getCurrentEnrollment(s.getSectionId());
            s.setCurrentEnrollment(currentEnrollment);
        }
        return sections;
    }

    public boolean registerForSection(String studentId, String sectionId) throws SQLException {
        if (!AccessControl.isActionAllowed("register", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }

        // FIX: Check Deadline
        checkDeadline();

        if (EnrollmentDAO.isStudentEnrolled(studentId, sectionId)) {
            throw new SQLException("You are already enrolled in this section.");
        }

        List<Section> allSections = SectionDAO.getAllSections();
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

        return EnrollmentDAO.enrollStudent(studentId, sectionId);
    }

    public boolean dropSection(String enrollmentId, String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("drop", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }

        // FIX: Check Deadline
        checkDeadline();

        return EnrollmentDAO.dropEnrollment(enrollmentId);
    }

    // NEW: Helper to validate date against deadline
    private void checkDeadline() throws SQLException {
        String deadlineStr = SettingsDAO.getSetting("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            try {
                LocalDate deadline = LocalDate.parse(deadlineStr);
                if (LocalDate.now().isAfter(deadline)) {
                    throw new SecurityException("Action failed: The deadline (" + deadlineStr + ") has passed.");
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid deadline format in DB: " + deadlineStr);
            }
        }
    }

    public Student getStudentProfile(String studentId) throws SQLException {
        if (!AccessControl.isActionAllowed("view_profile", studentId)) {
            throw new SecurityException("Access denied.");
        }
        return StudentDAO.findByUserId(studentId);
    }
}