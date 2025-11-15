package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A window for instructors to view simple reports and statistics for their sections.
 * This is for the "Reports" button.
 */
public class ReportWindow extends JDialog {

    private JComboBox<SectionItem> sectionComboBox;
    private JLabel totalStudentsLabel;
    private JLabel avgGradeLabel;
    private InstructorService instructorService;
    private GradeService gradeService;

    public ReportWindow(JFrame owner) {
        super(owner, "Class Reports", true);
        this.instructorService = new InstructorService();
        this.gradeService = new GradeService(); // For calculating averages

        setSize(600, 400);
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

        // --- Center: Stats Panel ---
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(UITheme.COLOR_WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                UITheme.BORDER_TABLE,
                UITheme.BORDER_PADDING
        ));

        totalStudentsLabel = new JLabel("Total Enrolled: N/A");
        UITheme.styleSubHeaderLabel(totalStudentsLabel);

        avgGradeLabel = new JLabel("Class Average (Finals): N/A");
        UITheme.styleSubHeaderLabel(avgGradeLabel);

        statsPanel.add(totalStudentsLabel);
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(avgGradeLabel);

        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        sectionComboBox.addActionListener(e -> loadReportData());

        add(mainPanel);
    }

    /**
     * Loads the instructor's sections into the combo box.
     */
    private void loadSections() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            List<Section> sectionList = instructorService.getMySections(instructorId);

            sectionComboBox.removeAllItems();
            sectionComboBox.addItem(new SectionItem(null, "--- Select a section ---"));
            for (Section s : sectionList) {
                sectionComboBox.addItem(new SectionItem(s, s.getCourseTitle() + " (" + s.getSectionId() + ")"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads statistics for the selected section.
     */
    private void loadReportData() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            totalStudentsLabel.setText("Total Enrolled: N/A");
            avgGradeLabel.setText("Class Average (Finals): N/A");
            return;
        }

        try {
            String instructorId = AuthService.getCurrentUserId();
            String sectionId = selectedItem.section.getSectionId();
            List<Enrollment> enrollments = instructorService.getSectionEnrollments(sectionId, instructorId);

            // 1. Total Students
            totalStudentsLabel.setText("Total Enrolled: " + enrollments.size());

            // 2. Class Average
            double totalScore = 0;
            int gradeCount = 0;
            for (Enrollment en : enrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                for (Grade g : grades) {
                    // Calculate average of "final" components only
                    if ("final".equalsIgnoreCase(g.getComponent()) && g.getScore() != null) {
                        totalScore += g.getScore();
                        gradeCount++;
                        break; // Only count one "final" grade per student
                    }
                }
            }

            if (gradeCount > 0) {
                double average = totalScore / gradeCount;
                avgGradeLabel.setText(String.format("Class Average (Finals): %.2f", average));
            } else {
                avgGradeLabel.setText("Class Average (Finals): N/A (No final grades entered)");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading report data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class to store Section object in JComboBox
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