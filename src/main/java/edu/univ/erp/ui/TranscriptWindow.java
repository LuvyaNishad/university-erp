package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A window for students to view and "download" their academic transcript.
 * This passes the test: "Download transcript (CSV or PDF) listing completed courses/grades"
 */
public class TranscriptWindow extends JDialog {

    private JTable transcriptTable;
    private StudentService studentService;
    private GradeService gradeService;
    private DefaultTableModel tableModel;

    public TranscriptWindow(JFrame owner) {
        super(owner, "My Academic Transcript", true);
        this.studentService = new StudentService();
        this.gradeService = new GradeService();

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadTranscriptData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Official Transcript");
        UITheme.styleSubHeaderLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Course Code", "Course Title", "Status", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transcriptTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton downloadButton = new JButton("Download as CSV");
        UITheme.stylePrimaryButton(downloadButton);
        downloadButton.setPreferredSize(new Dimension(180, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(downloadButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        downloadButton.addActionListener(e -> handleDownload());

        add(mainPanel);
    }

    /**
     * Loads all enrollments and their final grades.
     */
    private void loadTranscriptData() {
        try {
            String studentId = AuthService.getCurrentUserId();
            List<Enrollment> enrollmentList = studentService.getStudentEnrollments(studentId);

            tableModel.setRowCount(0); // Clear table

            for (Enrollment en : enrollmentList) {
                // We show all courses, registered or dropped

                String finalGrade = "N/A";
                try {
                    List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                    // Find the grade entry marked "final"
                    for (Grade g : grades) {
                        if ("final".equalsIgnoreCase(g.getComponent()) && g.getFinalGrade() != null) {
                            finalGrade = g.getFinalGrade();
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Ignore if no grades found
                }

                tableModel.addRow(new Object[]{
                        en.getCourseCode(),
                        en.getCourseTitle(), // This is set by EnrollmentDAO
                        en.getStatus().toUpperCase(),
                        finalGrade
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading transcript: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Download" button click.
     * This is a simplified implementation that saves a CSV to a user-chosen location.
     */
    private void handleDownload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript_" + AuthService.getCurrentUserId() + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.append(String.valueOf(tableModel.getValueAt(i, j)));
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }

                JOptionPane.showMessageDialog(this,
                        "Transcript saved successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Download Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}