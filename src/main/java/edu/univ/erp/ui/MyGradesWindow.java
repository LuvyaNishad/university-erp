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

/**
 * A window for students to view their grades for all enrolled courses.
 * This passes the test: "[ ] View grades for registered courses and sections."
 */
public class MyGradesWindow extends JDialog {

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private GradeService gradeService;

    public MyGradesWindow(JFrame owner) {
        super(owner, "My Grades", true);
        this.studentService = new StudentService();
        this.gradeService = new GradeService();

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadGrades();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("My Grades");
        UITheme.styleSubHeaderLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Course Code", "Course Title", "Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

        add(mainPanel);
    }

    /**
     * Loads all grades for all enrollments for the current student.
     */
    private void loadGrades() {
        try {
            String studentId = AuthService.getCurrentUserId();
            // 1. Get all enrollments
            List<Enrollment> enrollments = studentService.getStudentEnrollments(studentId);

            // Clear existing data
            tableModel.setRowCount(0);

            if (enrollments.isEmpty()) {
                tableModel.addRow(new Object[]{"N/A", "No courses enrolled", "N/A", "N/A", "N/A"});
                return;
            }

            // 2. For each enrollment, get all grades
            for (Enrollment en : enrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(en.getEnrollmentId());

                if (grades.isEmpty()) {
                    tableModel.addRow(new Object[]{
                            en.getCourseCode(),
                            en.getCourseTitle(),
                            "(No grades entered)",
                            "N/A",
                            "N/A"
                    });
                } else {
                    for (Grade g : grades) {
                        tableModel.addRow(new Object[]{
                                en.getCourseCode(),
                                en.getCourseTitle(),
                                g.getComponent(),
                                g.getScore() != null ? g.getScore() : "N/A",
                                g.getFinalGrade() != null ? g.getFinalGrade() : "N/A"
                        });
                    }
                }
                // Add a separator row
                tableModel.addRow(new Object[]{"---", "---", "---", "---", "---"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}