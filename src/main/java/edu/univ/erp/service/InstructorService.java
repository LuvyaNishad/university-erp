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

    public List<Section> getMySections(String instructorId) throws SQLException {
        if (!AccessControl.isActionAllowed("view_sections", instructorId)) {
            throw new SecurityException("Access denied.");
        }
        return SectionDAO.getSectionsByInstructor(instructorId);
    }

    public List<Enrollment> getSectionEnrollments(String sectionId, String instructorId) throws SQLException {
        List<Section> mySections = getMySections(instructorId);
        boolean ownsSection = mySections.stream()
                .anyMatch(section -> section.getSectionId().equals(sectionId));

        if (!ownsSection) {
            throw new SecurityException("Not your section.");
        }
        return EnrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public boolean enterGrade(Grade grade, String sectionId, String instructorId) throws SQLException {
        if (!AccessControl.isActionAllowed("enter_grades", instructorId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }
        return GradeDAO.saveGrade(grade);
    }

    public boolean updateGrade(Grade grade, String sectionId, String instructorId) throws SQLException {
        if (!AccessControl.isActionAllowed("enter_grades", instructorId)) {
            throw new SecurityException("Action not allowed. Check maintenance mode.");
        }
        return GradeDAO.updateGrade(grade);
    }

    public List<Grade> getGradesForEnrollment(String enrollmentId, String instructorId) throws SQLException {
        return GradeDAO.getGradesByEnrollment(enrollmentId);
    }

    /**
     * UPDATED: Calculates weighted score using:
     * - Assignment (20%)
     * - Midterm (30%)
     * - End Sem (50%) <-- Changed from "Final" to avoid confusion
     */
    public double calculateWeightedScore(List<Grade> grades) {
        double assignmentScore = 0.0;
        double midtermScore = 0.0;
        double endSemScore = 0.0;

        for (Grade g : grades) {
            if (g.getScore() == null) continue;

            String comp = g.getComponent().toLowerCase();
            if (comp.contains("assignment")) {
                assignmentScore = g.getScore();
            } else if (comp.contains("midterm")) {
                midtermScore = g.getScore();
            } else if (comp.equals("end sem")) {
                // "End Sem" is the 50% Exam component
                endSemScore = g.getScore();
            }
        }

        // Weighting Rule: 20% Assign, 30% Mid, 50% End Sem
        return (assignmentScore * 0.20) + (midtermScore * 0.30) + (endSemScore * 0.50);
    }

    public String computeFinalGrade(List<Grade> grades) {
        double weightedAvg = calculateWeightedScore(grades);
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