package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        // EXPORT BUTTON REMOVED

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void loadTranscript() {
        try {
            String studentId = AuthService.getCurrentUserId();
            enrollments = studentService.getStudentEnrollments(studentId);
            tableModel.setRowCount(0);

            for (Enrollment en : enrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());
                Grade finalGrade = grades.stream()
                        .filter(g -> "final".equalsIgnoreCase(g.getComponent()))
                        .findFirst().orElse(null);

                String scoreStr = (finalGrade != null) ? String.valueOf(finalGrade.getScore()) : "IP";
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
}