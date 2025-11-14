package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import java.sql.SQLException;
import java.util.List;

public class InstructorService {

    /**
     * Get instructor's sections (PDF: See My Sections)
     */
    public List<Section> getMySections(String instructorId) throws SQLException {
        // Check access control - instructors can only see their own sections
        if (!AccessControl.isActionAllowed("view_sections", instructorId)) {
            throw new SecurityException("Access denied.");
        }

        return SectionDAO.getSectionsByInstructor(instructorId);
    }

    /**
     * Get enrollments for a section (PDF: Enter scores)
     */
    public List<Enrollment> getSectionEnrollments(String sectionId, String instructorId) throws SQLException {
        // Verify instructor owns this section
        List<Section> mySections = getMySections(instructorId);
        boolean ownsSection = mySections.stream()
                .anyMatch(section -> section.getSectionId().equals(sectionId));

        if (!ownsSection) {
            throw new SecurityException("Not your section.");
        }

        return EnrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    /**
     * Enter grade for student (PDF: Enter scores for assessments)
     */
    public boolean enterGrade(Grade grade, String sectionId, String instructorId) throws SQLException {
        // Check access control
        if (!AccessControl.isActionAllowed("enter_grades", instructorId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode or permissions.");
        }

        // Verify instructor owns the section
        List<Section> mySections = getMySections(instructorId);
        boolean ownsSection = mySections.stream()
                .anyMatch(section -> section.getSectionId().equals(sectionId));

        if (!ownsSection) {
            throw new SecurityException("Not your section.");
        }

        return GradeDAO.saveGrade(grade);
    }

    /**
     * Update existing grade
     */
    public boolean updateGrade(Grade grade, String sectionId, String instructorId) throws SQLException {
        // Same access checks as enterGrade
        if (!AccessControl.isActionAllowed("enter_grades", instructorId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode or permissions.");
        }

        List<Section> mySections = getMySections(instructorId);
        boolean ownsSection = mySections.stream()
                .anyMatch(section -> section.getSectionId().equals(sectionId));

        if (!ownsSection) {
            throw new SecurityException("Not your section.");
        }

        return GradeDAO.updateGrade(grade);
    }

    /**
     * Get grades for enrollment
     */
    public List<Grade> getGradesForEnrollment(String enrollmentId, String instructorId) throws SQLException {
        // Check access control
        if (!AccessControl.isActionAllowed("view_grades", instructorId)) {
            throw new SecurityException("Access denied.");
        }

        return GradeDAO.getGradesByEnrollment(enrollmentId);
    }

    /**
     * Compute final grade (PDF: Compute final using weighting rule)
     */
    public String computeFinalGrade(List<Grade> grades) {
        // Simple weighting rule: 30% midterm, 50% final, 20% assignments
        double midterm = 0, finals = 0, assignments = 0;
        int midtermCount = 0, finalsCount = 0, assignmentsCount = 0;

        for (Grade grade : grades) {
            switch (grade.getComponent().toLowerCase()) {
                case "midterm":
                    midterm += grade.getScore();
                    midtermCount++;
                    break;
                case "final":
                    finals += grade.getScore();
                    finalsCount++;
                    break;
                case "assignment":
                    assignments += grade.getScore();
                    assignmentsCount++;
                    break;
            }
        }

        // Calculate weighted average
        double weightedAvg = 0;
        if (midtermCount > 0) weightedAvg += (midterm / midtermCount) * 0.3;
        if (finalsCount > 0) weightedAvg += (finals / finalsCount) * 0.5;
        if (assignmentsCount > 0) weightedAvg += (assignments / assignmentsCount) * 0.2;

        // Convert to letter grade
        return convertToLetterGrade(weightedAvg);
    }

    private String convertToLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}