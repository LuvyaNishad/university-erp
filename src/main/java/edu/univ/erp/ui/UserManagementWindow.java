package edu.univ.erp.ui;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

/**
 * A window for Admins to create new users (students and instructors).
 * This passes the test: "[ ] Create a new student user..."
 */
public class UserManagementWindow extends JDialog {

    private AdminService adminService;
    private JTextField userIdField, usernameField, passwordField, rollNoField, programField, yearField, deptField;
    private JComboBox<String> roleComboBox;
    private JPanel studentPanel, instructorPanel;

    public UserManagementWindow(JFrame owner) {
        super(owner, "User Management", true);
        this.adminService = new AdminService();

        setSize(500, 600); // This size is correct
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Create New User");
        UITheme.styleSubHeaderLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Form ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.COLOR_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                UITheme.BORDER_TABLE,
                UITheme.BORDER_PADDING_DIALOG
        ));

        // Auth Details
        formPanel.add(createFormField("User ID (e.g., 'stu3'):", userIdField = new JTextField()));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Username (e.g., 'stu3'):", usernameField = new JTextField()));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Password:", passwordField = new JPasswordField()));
        formPanel.add(Box.createVerticalStrut(10));

        roleComboBox = new JComboBox<>(new String[]{"student", "instructor", "admin"});
        formPanel.add(createFormField("Role:", roleComboBox));
        formPanel.add(Box.createVerticalStrut(15));

        // --- Student Panel ---
        studentPanel = new JPanel();
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        studentPanel.setBackground(UITheme.COLOR_WHITE);
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student Profile"));
        studentPanel.add(createFormField("Roll No:", rollNoField = new JTextField()));
        studentPanel.add(Box.createVerticalStrut(10));
        studentPanel.add(createFormField("Program (e.g., 'CS'):", programField = new JTextField()));
        studentPanel.add(Box.createVerticalStrut(10));
        studentPanel.add(createFormField("Year (e.g., 2024):", yearField = new JTextField()));
        formPanel.add(studentPanel);

        // --- Instructor Panel ---
        instructorPanel = new JPanel();
        instructorPanel.setLayout(new BoxLayout(instructorPanel, BoxLayout.Y_AXIS));
        instructorPanel.setBackground(UITheme.COLOR_WHITE);
        instructorPanel.setBorder(BorderFactory.createTitledBorder("Instructor Profile"));
        instructorPanel.add(createFormField("Department:", deptField = new JTextField()));
        formPanel.add(instructorPanel);

        // Toggle panels based on role selection
        roleComboBox.addActionListener(e -> toggleRolePanels());
        toggleRolePanels(); // Initial call

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JButton createButton = new JButton("Create User");
        UITheme.stylePrimaryButton(createButton);
        createButton.setPreferredSize(new Dimension(140, 40));
        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(createButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        createButton.addActionListener(e -> handleCreateUser());

        // This is the crucial part. Add the fully constructed mainPanel
        // to the JDialog's content pane.
        add(mainPanel, BorderLayout.CENTER);
    }

    private void toggleRolePanels() {
        String selectedRole = (String) roleComboBox.getSelectedItem();
        studentPanel.setVisible("student".equals(selectedRole));
        instructorPanel.setVisible("instructor".equals(selectedRole));

        // --- THIS LINE WAS THE BUG ---
        // pack(); // <-- REMOVED. This was causing the window to resize incorrectly.

        // Repaint the container to ensure changes are visible
        revalidate();
        repaint();
    }

    private JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(UITheme.COLOR_WHITE);
        JLabel label = new JLabel(labelText);
        UITheme.styleLabel(label);
        label.setPreferredSize(new Dimension(150, 30));

        if (field instanceof JTextField) {
            UITheme.styleTextField((JTextField) field);
        }
        field.setPreferredSize(new Dimension(200, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void handleCreateUser() {
        try {
            // 1. Get Auth Details
            String userId = userIdField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(((JPasswordField) passwordField).getPassword()).trim();
            String role = (String) roleComboBox.getSelectedItem();

            if (userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showError("Auth fields (User ID, Username, Password) cannot be empty.");
                return;
            }

            // 2. Create Auth Record
            // This is a placeholder, as AdminService.createUser is a placeholder
            boolean authCreated = adminService.createUser(userId, username, password, role);
            if (!authCreated) {
                showError("Failed to create user in Auth DB. User ID or Username might already exist.");
                return;
            }

            // 3. Create Profile Record (if applicable)
            boolean profileCreated = true; // Admins don't need a profile record
            if ("student".equals(role)) {
                Student student = new Student();
                student.setUserId(userId);
                student.setRollNo(rollNoField.getText().trim());
                student.setProgram(programField.getText().trim());
                student.setYear(Integer.parseInt(yearField.getText().trim()));
                profileCreated = adminService.createStudent(student);

            } else if ("instructor".equals(role)) {
                Instructor instructor = new Instructor();
                instructor.setUserId(userId);
                instructor.setDepartment(deptField.getText().trim());
                profileCreated = adminService.createInstructor(instructor);
            }

            if (profileCreated) {
                JOptionPane.showMessageDialog(this, "User '" + username + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields
                userIdField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                rollNoField.setText("");
                programField.setText("");
                yearField.setText("");
                deptField.setText("");
            } else {
                showError("Auth record created, but failed to create profile in ERP DB.");
            }

        } catch (NumberFormatException e) {
            showError("Year must be a valid number.");
        } catch (Exception e) {
            showError("Error creating user: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}