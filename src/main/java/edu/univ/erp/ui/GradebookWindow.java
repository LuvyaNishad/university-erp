package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A window for instructors to enter and update grades.
 * This passes the tests:
 * - "[ ] Enter scores (quiz/midterm/end-sem) -> saved."
 * - "[ ] Compute final grade using the rule..."
 */
public class GradebookWindow extends JDialog {

    private JComboBox<SectionItem> sectionComboBox;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private GradeService gradeService;
    private List<Section> sectionList;
    private List<Enrollment> currentEnrollments;
    private Map<String, List<Grade>> gradeCache; // Caches grades for saving

    public GradebookWindow(JFrame owner) {
        super(owner, "Gradebook", true);
        this.instructorService = new InstructorService();
        this.gradeService = new GradeService();
        this.gradeCache = new HashMap<>();

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadSections();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- Top Panel (Selector) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JLabel selectLabel = new JLabel("Select a Section:");
        UITheme.styleLabel(selectLabel);
        sectionComboBox = new JComboBox<>();
        sectionComboBox.setPreferredSize(new Dimension(350, 30));
        topPanel.add(selectLabel);
        topPanel.add(sectionComboBox);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Student ID", "Student Name", "Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0); // Editable
        gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton saveButton = new JButton("Save All Grades");
        UITheme.stylePrimaryButton(saveButton);
        saveButton.setPreferredSize(new Dimension(180, 40));

        JButton computeFinalButton = new JButton("Compute Final Grades");
        UITheme.stylePrimaryButton(computeFinalButton);
        computeFinalButton.setBackground(UITheme.COLOR_PRIMARY_BLUE);
        computeFinalButton.setPreferredSize(new Dimension(200, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);
        buttonContainer.add(computeFinalButton);
        buttonContainer.add(saveButton);
        buttonContainer.add(closeButton);
        bottomPanel.add(buttonContainer, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        sectionComboBox.addActionListener(e -> loadGradebook());
        saveButton.addActionListener(e -> saveGrades());
        computeFinalButton.addActionListener(e -> computeFinalGrades());

        add(mainPanel);
    }

    private void loadSections() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            this.sectionList = instructorService.getMySections(instructorId);

            sectionComboBox.removeAllItems();
            sectionComboBox.addItem(new SectionItem(null, "--- Select a section ---"));
            for (Section s : sectionList) {
                sectionComboBox.addItem(new SectionItem(s, s.getCourseTitle() + " (" + s.getSectionId() + ")"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGradebook() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            tableModel.setRowCount(0);
            return;
        }

        try {
            String instructorId = AuthService.getCurrentUserId();
            String sectionId = selectedItem.section.getSectionId();
            this.currentEnrollments = instructorService.getSectionEnrollments(sectionId, instructorId);

            tableModel.setRowCount(0); // Clear table
            gradeCache.clear(); // Clear cache

            for (Enrollment en : currentEnrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                gradeCache.put(en.getEnrollmentId(), grades); // Cache the grades

                if (grades.isEmpty()) {
                    // Add rows for new grade entry
                    tableModel.addRow(new Object[]{en.getStudentId(), en.getStudentName(), "Midterm", 0.0, "N/A"});
                    tableModel.addRow(new Object[]{en.getStudentId(), en.getStudentName(), "Assignment", 0.0, "N/A"});
                    tableModel.addRow(new Object[]{en.getStudentId(), en.getStudentName(), "Final", 0.0, "N/A"});
                } else {
                    for (Grade g : grades) {
                        tableModel.addRow(new Object[]{
                                en.getStudentId(),
                                en.getStudentName(),
                                g.getComponent(),
                                g.getScore(),
                                g.getFinalGrade() != null ? g.getFinalGrade() : "N/A"
                        });
                    }
                }
                // Add separator
                tableModel.addRow(new Object[]{"---", "---", "---", "---", "---"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading gradebook: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveGrades() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            JOptionPane.showMessageDialog(this, "Please select a section first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String instructorId = AuthService.getCurrentUserId();
            String sectionId = selectedItem.section.getSectionId();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String studentId = (String) tableModel.getValueAt(i, 0);
                if (studentId == null || studentId.equals("---")) continue;

                String component = (String) tableModel.getValueAt(i, 2);
                Double score = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                String finalGrade = (String) tableModel.getValueAt(i, 4);

                // Find the enrollmentId for this student
                String enrollmentId = currentEnrollments.stream()
                        .filter(en -> en.getStudentId().equals(studentId))
                        .findFirst().get().getEnrollmentId();

                // Check if grade exists
                List<Grade> existingGrades = gradeCache.get(enrollmentId);
                Grade existing = existingGrades.stream()
                        .filter(g -> g.getComponent().equalsIgnoreCase(component))
                        .findFirst().orElse(null);

                Grade newGrade = new Grade();
                newGrade.setEnrollmentId(enrollmentId);
                newGrade.setComponent(component);
                newGrade.setScore(score);
                newGrade.setFinalGrade("N/A".equals(finalGrade) ? null : finalGrade);

                if (existing != null) {
                    newGrade.setGradeId(existing.getGradeId());
                    instructorService.updateGrade(newGrade, sectionId, instructorId);
                } else {
                    instructorService.enterGrade(newGrade, sectionId, instructorId);
                }
            }
            JOptionPane.showMessageDialog(this, "Grades saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradebook(); // Reload data
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void computeFinalGrades() {
        if (currentEnrollments == null) return;

        try {
            String instructorId = AuthService.getCurrentUserId();
            for (Enrollment en : currentEnrollments) {
                // Get fresh grades from DB
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                // Compute letter grade
                String letterGrade = instructorService.computeFinalGrade(grades);

                // Find the "Final" grade component and update it
                Grade finalGradeEntry = grades.stream()
                        .filter(g -> "final".equalsIgnoreCase(g.getComponent()))
                        .findFirst().orElse(null);

                String sectionId = ((SectionItem) sectionComboBox.getSelectedItem()).section.getSectionId();

                if (finalGradeEntry != null) {
                    finalGradeEntry.setFinalGrade(letterGrade);
                    instructorService.updateGrade(finalGradeEntry, sectionId, instructorId);
                } else {
                    // If no "Final" entry, create one to store the final grade
                    Grade newFinalGrade = new Grade();
                    newFinalGrade.setEnrollmentId(en.getEnrollmentId());
                    newFinalGrade.setComponent("Final");
                    newFinalGrade.setScore(0.0); // Default score, main value is letterGrade
                    newFinalGrade.setFinalGrade(letterGrade);
                    instructorService.enterGrade(newFinalGrade, sectionId, instructorId);
                }
            }
            JOptionPane.showMessageDialog(this, "Final grades computed and saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradebook(); // Refresh to show new letter grades
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error computing grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class
    private static class SectionItem {
        Section section;
        String displayValue;

        SectionItem(Section section, String displayValue) {
            this.section = section;
            this.displayValue = displayValue;
        }

        @Override
        public String toString() {
            return displayValue;
        }
    }
}