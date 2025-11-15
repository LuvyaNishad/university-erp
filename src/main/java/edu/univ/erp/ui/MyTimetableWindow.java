package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for students to view their timetable and drop courses.
 * This passes the test: "[ ] Drop before deadline..."
 */
public class MyTimetableWindow extends JDialog {

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private List<Enrollment> enrollmentList;
    private JLabel statusLabel;

    public MyTimetableWindow(JFrame owner) {
        super(owner, "My Timetable", true);
        this.studentService = new StudentService();

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadTimetable();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("My Enrolled Courses");
        UITheme.styleSubHeaderLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Course", "Title", "Section Info", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        statusLabel = new JLabel("Select a section to drop.");
        statusLabel.setFont(UITheme.FONT_BODY);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton dropButton = new JButton("Drop Selected Section");
        dropButton.setBackground(UITheme.COLOR_DANGER_RED);
        dropButton.setForeground(UITheme.COLOR_WHITE);
        dropButton.setFont(UITheme.FONT_BUTTON);
        dropButton.setFocusPainted(false);
        dropButton.setPreferredSize(new Dimension(200, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);
        buttonContainer.add(dropButton);
        buttonContainer.add(closeButton);

        bottomPanel.add(buttonContainer, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        dropButton.addActionListener(e -> handleDrop());

        add(mainPanel);
    }

    /**
     * Loads the student's current enrollments.
     */
    private void loadTimetable() {
        try {
            String studentId = AuthService.getCurrentUserId();
            this.enrollmentList = studentService.getStudentEnrollments(studentId);

            // Clear existing data
            tableModel.setRowCount(0);

            for (Enrollment en : enrollmentList) {
                tableModel.addRow(new Object[]{
                        en.getCourseCode(),
                        en.getCourseTitle(), // Assumes this is set by EnrollmentDAO
                        en.getSectionInfo(), // Assumes this is set by EnrollmentDAO
                        en.getStatus().toUpperCase()
                });
            }
            statusLabel.setText(enrollmentList.size() + " sections found.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading timetable: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the drop button click.
     */
    private void handleDrop() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to drop.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String studentId = AuthService.getCurrentUserId();
            Enrollment selectedEnrollment = enrollmentList.get(selectedRow);

            if (!"registered".equalsIgnoreCase(selectedEnrollment.getStatus())) {
                JOptionPane.showMessageDialog(this, "This section has already been dropped.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to drop " + selectedEnrollment.getCourseCode() + "?",
                    "Confirm Drop",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // This service call handles the drop logic
            boolean success = studentService.dropSection(selectedEnrollment.getEnrollmentId(), studentId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Successfully dropped " + selectedEnrollment.getCourseCode() + ".", "Section Dropped", JOptionPane.INFORMATION_MESSAGE);
                loadTimetable(); // Refresh list
            } else {
                showError("Failed to drop section.");
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}