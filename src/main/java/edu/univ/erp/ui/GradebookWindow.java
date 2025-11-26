package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradebookWindow extends JDialog {

    private JComboBox<SectionItem> sectionComboBox;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private GradeService gradeService;
    private List<Section> sectionList;
    private List<Enrollment> currentEnrollments;
    private Map<String, List<Grade>> gradeCache;

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

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JLabel selectLabel = new JLabel("Select a Section:");
        UITheme.styleLabel(selectLabel);
        sectionComboBox = new JComboBox<>();
        sectionComboBox.setPreferredSize(new Dimension(350, 30));
        topPanel.add(selectLabel);
        topPanel.add(sectionComboBox);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Student ID", "Student Name", "Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton importButton = new JButton("Import CSV");
        UITheme.styleSecondaryButton(importButton);
        importButton.setPreferredSize(new Dimension(120, 40));

        JButton exportButton = new JButton("Export CSV");
        UITheme.styleSecondaryButton(exportButton);
        exportButton.setPreferredSize(new Dimension(120, 40));

        JButton saveButton = new JButton("Save All Grades");
        UITheme.stylePrimaryButton(saveButton);
        saveButton.setPreferredSize(new Dimension(150, 40));

        JButton computeFinalButton = new JButton("Compute Final Grades");
        UITheme.stylePrimaryButton(computeFinalButton);
        computeFinalButton.setBackground(UITheme.COLOR_PRIMARY_BLUE);
        computeFinalButton.setPreferredSize(new Dimension(180, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);

        buttonContainer.add(importButton);
        buttonContainer.add(exportButton);
        buttonContainer.add(computeFinalButton);
        buttonContainer.add(saveButton);
        buttonContainer.add(closeButton);

        bottomPanel.add(buttonContainer, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> dispose());
        sectionComboBox.addActionListener(e -> loadGradebook());
        saveButton.addActionListener(e -> saveGrades());
        computeFinalButton.addActionListener(e -> computeFinalGrades());
        importButton.addActionListener(e -> handleImportCSV());
        exportButton.addActionListener(e -> handleExportCSV());

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

            tableModel.setRowCount(0);
            gradeCache.clear();

            // UPDATED COMPONENTS: Now includes "End Sem" for input and "FINAL" for output
            String[] requiredComponents = {"Midterm", "Assignment", "End Sem", "FINAL"};

            for (Enrollment en : currentEnrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                gradeCache.put(en.getEnrollmentId(), grades);

                for (String comp : requiredComponents) {
                    Grade existing = grades.stream()
                            .filter(g -> g.getComponent().equalsIgnoreCase(comp))
                            .findFirst().orElse(null);

                    if (existing != null) {
                        tableModel.addRow(new Object[]{
                                en.getStudentId(),
                                en.getStudentName(),
                                existing.getComponent(),
                                existing.getScore(),
                                existing.getFinalGrade() != null ? existing.getFinalGrade() : "N/A"
                        });
                    } else {
                        tableModel.addRow(new Object[]{
                                en.getStudentId(),
                                en.getStudentName(),
                                comp,
                                0.0,
                                "N/A"
                        });
                    }
                }
                tableModel.addRow(new Object[]{"---", "---", "---", "---", "---"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading gradebook: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleImportCSV() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            JOptionPane.showMessageDialog(this, "Please select a section first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Grades CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isHeader = true;
                int importedCount = 0;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("\uFEFF")) line = line.substring(1);
                    if (isHeader) { isHeader = false; continue; }

                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;

                    String studentId = parts[0].trim().replace("\"", "");
                    String component = parts[1].trim().replace("\"", "");
                    double score = Double.parseDouble(parts[2].trim().replace("\"", ""));

                    if (updateTableModel(studentId, component, score)) {
                        importedCount++;
                    }
                }
                JOptionPane.showMessageDialog(this, "Imported " + importedCount + " grades. Click 'Save' to commit.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Import Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportCSV() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            JOptionPane.showMessageDialog(this, "No grades to export. Select a section first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Grades to CSV");
        fileChooser.setSelectedFile(new File("grades_export.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.append("Student ID,Student Name,Component,Score,Final Grade\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String studentId = (String) tableModel.getValueAt(i, 0);
                    if (studentId == null || studentId.equals("---")) continue;

                    String name = (String) tableModel.getValueAt(i, 1);
                    String component = (String) tableModel.getValueAt(i, 2);
                    Object scoreObj = tableModel.getValueAt(i, 3);
                    Object finalGradeObj = tableModel.getValueAt(i, 4);

                    String score = scoreObj != null ? scoreObj.toString() : "0.0";
                    String finalGrade = finalGradeObj != null ? finalGradeObj.toString() : "N/A";

                    writer.append(studentId).append(",");
                    writer.append("\"").append(name).append("\",");
                    writer.append(component).append(",");
                    writer.append(score).append(",");
                    writer.append(finalGrade).append("\n");
                }
                JOptionPane.showMessageDialog(this, "Export Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean updateTableModel(String studentId, String component, double score) {
        boolean updated = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String rowStudentId = (String) tableModel.getValueAt(i, 0);
            String rowComponent = (String) tableModel.getValueAt(i, 2);

            if (rowStudentId != null && rowStudentId.equalsIgnoreCase(studentId) &&
                    rowComponent != null && rowComponent.equalsIgnoreCase(component)) {
                tableModel.setValueAt(score, i, 3);
                updated = true;
            }
        }
        return updated;
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

                String enrollmentId = currentEnrollments.stream()
                        .filter(en -> en.getStudentId().equals(studentId))
                        .findFirst().get().getEnrollmentId();

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
            loadGradebook();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void computeFinalGrades() {
        if (currentEnrollments == null) return;

        try {
            String instructorId = AuthService.getCurrentUserId();
            String sectionId = ((SectionItem) sectionComboBox.getSelectedItem()).section.getSectionId();

            for (Enrollment en : currentEnrollments) {
                // 1. Get all grades for this student
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());

                // 2. Calculate weighted numerical score
                double weightedScore = instructorService.calculateWeightedScore(grades);

                // 3. Convert to Letter Grade
                String letterGrade = instructorService.computeFinalGrade(grades);

                // 4. Find OR Create the "FINAL" row (distinct from "End Sem")
                Grade finalGradeEntry = grades.stream()
                        .filter(g -> "FINAL".equalsIgnoreCase(g.getComponent()))
                        .findFirst().orElse(null);

                if (finalGradeEntry != null) {
                    finalGradeEntry.setScore(weightedScore); // Save weighted score here for reporting
                    finalGradeEntry.setFinalGrade(letterGrade);
                    instructorService.updateGrade(finalGradeEntry, sectionId, instructorId);
                } else {
                    Grade newFinalGrade = new Grade();
                    newFinalGrade.setEnrollmentId(en.getEnrollmentId());
                    newFinalGrade.setComponent("FINAL");
                    newFinalGrade.setScore(weightedScore);
                    newFinalGrade.setFinalGrade(letterGrade);
                    instructorService.enterGrade(newFinalGrade, sectionId, instructorId);
                }
            }
            JOptionPane.showMessageDialog(this, "Final grades computed and saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradebook();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SectionItem {
        Section section;
        String displayValue;
        SectionItem(Section section, String displayValue) {
            this.section = section;
            this.displayValue = displayValue;
        }
        @Override public String toString() { return displayValue; }
    }
}