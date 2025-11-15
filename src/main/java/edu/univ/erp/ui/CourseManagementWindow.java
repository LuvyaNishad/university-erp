package edu.univ.erp.ui;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for Admins to create new courses.
 * This passes the test: "[ ] Create a course..."
 */
public class CourseManagementWindow extends JDialog {

    private AdminService adminService;
    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JTextField courseIdField, codeField, titleField, creditsField;

    public CourseManagementWindow(JFrame owner) {
        super(owner, "Course Management", true);
        this.adminService = new AdminService();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadCourses();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Manage Courses");
        UITheme.styleSubHeaderLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table of existing courses ---
        String[] columnNames = {"Course ID", "Code", "Title", "Credits"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Form for new course ---
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBackground(UITheme.COLOR_WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Course"));

        formPanel.add(new JLabel("Course ID (e.g., 'CS101')"));
        courseIdField = new JTextField();
        UITheme.styleTextField(courseIdField);
        formPanel.add(courseIdField);

        formPanel.add(new JLabel("Course Code (e.g., 'CS101')"));
        codeField = new JTextField();
        UITheme.styleTextField(codeField);
        formPanel.add(codeField);

        formPanel.add(new JLabel("Title (e.g., 'Intro to CS')"));
        titleField = new JTextField();
        UITheme.styleTextField(titleField);
        formPanel.add(titleField);

        formPanel.add(new JLabel("Credits (e.g., 4)"));
        creditsField = new JTextField();
        UITheme.styleTextField(creditsField);
        formPanel.add(creditsField);

        mainPanel.add(formPanel, BorderLayout.SOUTH);

        // --- Button Panel (Right) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JButton createButton = new JButton("Create Course");
        UITheme.stylePrimaryButton(createButton);
        createButton.setMinimumSize(new Dimension(150, 40));
        createButton.setMaximumSize(new Dimension(150, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setMinimumSize(new Dimension(150, 40));
        closeButton.setMaximumSize(new Dimension(150, 40));

        buttonPanel.add(createButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        createButton.addActionListener(e -> handleCreateCourse());

        add(mainPanel);
    }

    private void loadCourses() {
        try {
            List<Course> courses = adminService.getAllCourses();
            tableModel.setRowCount(0);
            for (Course c : courses) {
                tableModel.addRow(new Object[]{c.getCourseId(), c.getCode(), c.getTitle(), c.getCredits()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateCourse() {
        try {
            Course course = new Course();
            course.setCourseId(courseIdField.getText().trim());
            course.setCode(codeField.getText().trim());
            course.setTitle(titleField.getText().trim());
            course.setCredits(Integer.parseInt(creditsField.getText().trim()));

            if (course.getCourseId().isEmpty() || course.getCode().isEmpty() || course.getTitle().isEmpty()) {
                showError("All fields are required.");
                return;
            }

            boolean success = adminService.createCourse(course);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses(); // Refresh table
                // Clear fields
                courseIdField.setText("");
                codeField.setText("");
                titleField.setText("");
                creditsField.setText("");
            } else {
                showError("Failed to create course. Check if ID or Code already exists.");
            }
        } catch (NumberFormatException e) {
            showError("Credits must be a valid number.");
        } catch (Exception e) {
            showError("Error creating course: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}