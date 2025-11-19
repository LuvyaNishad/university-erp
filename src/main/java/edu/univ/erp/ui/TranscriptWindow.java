package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

public class TranscriptWindow extends JDialog {

    private JTable transcriptTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private GradeService gradeService;
    private List<Enrollment> enrollments;

    public TranscriptWindow(JFrame owner) {
        super(owner, "Academic Transcript", true);
        this.studentService = new StudentService();
        this.gradeService = new GradeService();

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadTranscript();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Unofficial Transcript");
        UITheme.styleSubHeaderLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Course Code", "Course Title", "Final Score", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transcriptTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton exportButton = new JButton("Export CSV");
        UITheme.stylePrimaryButton(exportButton);
        exportButton.setPreferredSize(new Dimension(150, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        bottomPanel.add(exportButton);
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> dispose());
        exportButton.addActionListener(e -> handleExport());

        add(mainPanel);
    }

    private void loadTranscript() {
        try {
            String studentId = AuthService.getCurrentUserId();
            enrollments = studentService.getStudentEnrollments(studentId);
            tableModel.setRowCount(0);

            for (Enrollment en : enrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                // Find "Final" grade
                Grade finalGrade = grades.stream()
                        .filter(g -> "final".equalsIgnoreCase(g.getComponent()))
                        .findFirst().orElse(null);

                String scoreStr = (finalGrade != null) ? String.valueOf(finalGrade.getScore()) : "IP"; // In Progress
                String gradeStr = (finalGrade != null && finalGrade.getFinalGrade() != null) ? finalGrade.getFinalGrade() : "--";

                tableModel.addRow(new Object[]{
                        en.getCourseCode(),
                        en.getCourseTitle(),
                        scoreStr,
                        gradeStr
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading transcript: " + e.getMessage());
        }
    }

    private void handleExport() {
        if (enrollments == null || enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript as CSV");
        fileChooser.setSelectedFile(new java.io.File("transcript.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // --- IMPROVED ROBUST CSV LOGIC ---
                writer.append("\"Course Code\",\"Course Title\",\"Final Score\",\"Grade\"\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String code = (String) tableModel.getValueAt(i, 0);
                    String title = (String) tableModel.getValueAt(i, 1);
                    String score = (String) tableModel.getValueAt(i, 2);
                    String grade = (String) tableModel.getValueAt(i, 3);

                    // Wrap in quotes to handle commas safely
                    writer.append("\"").append(code).append("\",");
                    writer.append("\"").append(title).append("\",");
                    writer.append("\"").append(score).append("\",");
                    writer.append("\"").append(grade).append("\"\n");
                }

                JOptionPane.showMessageDialog(this, "Export successful!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}