package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import java.sql.SQLException;
import java.util.List;

public class StudentService {

    /**
     * Get course catalog (PDF: Browse course catalog)
     */
    public List<Course> getCourseCatalog() throws SQLException {
        return CourseDAO.getAllCourses();
    }

    /**
     * Get all sections for course catalog display
     */
    public List<Section> getAvailableSections() throws SQLException {
        List<Section> sections = SectionDAO.getAllSections();
        // Add current enrollment info
        for (Section section : sections) {
            int currentEnrollment = SectionDAO.getCurrentEnrollment(section.getSectionId());
            section.setCapacity(currentEnrollment); // Reusing capacity field to show current/total
        }
        return sections;
    }

    /**
     * Register for section (PDF: Register only if seats available and not duplicate)
     */
    public boolean registerForSection(String studentId, String sectionId) throws SQLException {
        // Check access control
        if (!AccessControl.isActionAllowed("register", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode or permissions.");
        }

        // Check if section has available seats
        int currentEnrollment = SectionDAO.getCurrentEnrollment(sectionId);
        Section section = SectionDAO.getAllSections().stream()
                .filter(s -> s.getSectionId().equals(sectionId))
                .findFirst()
                .orElse(null);

        if (section == null || currentEnrollment >= section.getCapacity()) {
            throw new IllegalStateException("Section is full or not found.");
        }

        // Check for duplicate enrollment
        if (EnrollmentDAO.isStudentEnrolled(studentId, sectionId)) {
            throw new IllegalStateException("Already enrolled in this section.");
        }

        return EnrollmentDAO.enrollStudent(studentId, sectionId);
    }

    /**
     * Drop section (PDF: Drop before deadline)
     */
    public boolean dropSection(String enrollmentId, String studentId) throws SQLException {
        // Check access control
        if (!AccessControl.isActionAllowed("drop", studentId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode or permissions.");
        }

        // Add deadline logic here if needed
        return EnrollmentDAO.dropEnrollment(enrollmentId);
    }

    /**
     * Get student enrollments (PDF: View timetable, grades)
     */
    public List<Enrollment> getStudentEnrollments(String studentId) throws SQLException {
        // Check access control - students can only see their own data
        if (!AccessControl.isActionAllowed("view_enrollments", studentId)) {
            throw new SecurityException("Access denied.");
        }

        return EnrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    /**
     * Get student profile
     */
    public Student getStudentProfile(String studentId) throws SQLException {
        return StudentDAO.findByUserId(studentId);
    }
}