package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Admin Dashboard - The main hub for administrators.
 * This file has been updated to use UITheme and to open the functional
 * dialogs for each management task.
 */
public class AdminDashboard extends JFrame {
    private JButton logoutButton;
    private JButton maintenanceButton;
    private JButton manageUsersButton;
    private JButton manageCoursesButton;
    private JButton manageSectionsButton;
    private AdminService adminService;
    private JLabel maintenanceBanner;

    public AdminDashboard() {
        this.adminService = new AdminService();
        setupWindow();
        createComponents();
        setupActions();
        updateMaintenanceButton(); // Set initial state
    }

    private void setupWindow() {
        setTitle("Admin Dashboard - University ERP");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.COLOR_BACKGROUND);
    }

    private void createComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- Top Panel (Header + Banner) ---
        JPanel topPanel = new JPanel(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(UITheme.BORDER_PADDING);
        headerPanel.setBackground(UITheme.COLOR_HEADER_BACKGROUND);

        JLabel userLabel = new JLabel("Welcome, Administrator (" + AuthService.getCurrentUserId() + ")");
        UITheme.styleHeaderLabel(userLabel);

        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(100, 40));

        headerPanel.add(userLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Maintenance banner (styled in updateMaintenanceButton)
        maintenanceBanner = new JLabel("...", JLabel.CENTER);

        topPanel.add(maintenanceBanner, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Menu Grid Panel ---
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20)); // Gaps
        menuPanel.setBorder(UITheme.BORDER_PADDING);
        menuPanel.setBackground(UITheme.COLOR_BACKGROUND);

        manageUsersButton = new JButton("<html><center>Manage Users<br/><small>Add students/instructors</small></center></html>");
        manageCoursesButton = new JButton("<html><center>Manage Courses<br/><small>Create/edit courses</small></center></html>");
        manageSectionsButton = new JButton("<html><center>Manage Sections<br/><small>Create sections & assign instructors</small></center></html>");
        maintenanceButton = new JButton("Maintenance: ..."); // Text set in updateMaintenanceButton

        // Style buttons
        UITheme.styleDashboardButton(manageUsersButton, UITheme.COLOR_PRIMARY_TEAL);
        UITheme.styleDashboardButton(manageCoursesButton, UITheme.COLOR_GRAY_DARK);
        UITheme.styleDashboardButton(manageSectionsButton, UITheme.COLOR_GRAY_MEDIUM);

        // Maintenance button is styled dynamically
        maintenanceButton.setFont(UITheme.FONT_BUTTON);
        maintenanceButton.setPreferredSize(new Dimension(200, 100));

        menuPanel.add(manageUsersButton);
        menuPanel.add(manageCoursesButton);
        menuPanel.add(manageSectionsButton);
        menuPanel.add(maintenanceButton);

        mainPanel.add(menuPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void setupActions() {
        logoutButton.addActionListener(e -> logout());
        maintenanceButton.addActionListener(e -> toggleMaintenanceMode());

        // Connect buttons to open their respective functional windows
        manageUsersButton.addActionListener(e -> openUserManagement());
        manageCoursesButton.addActionListener(e -> openCourseManagement());
        manageSectionsButton.addActionListener(e -> openSectionManagement());
    }

    /**
     * Opens the User Management dialog.
     */
    private void openUserManagement() {
        SwingUtilities.invokeLater(() -> new UserManagementWindow(this).setVisible(true));
    }

    /**
     * Opens the Course Management dialog.
     */
    private void openCourseManagement() {
        SwingUtilities.invokeLater(() -> new CourseManagementWindow(this).setVisible(true));
    }

    /**
     * Opens the Section Management dialog.
     */
    private void openSectionManagement() {
        SwingUtilities.invokeLater(() -> new SectionManagementWindow(this).setVisible(true));
    }

    /**
     * Toggles the maintenance mode setting in the database.
     */
    private void toggleMaintenanceMode() {
        try {
            boolean currentMode = adminService.isMaintenanceMode();
            adminService.setMaintenanceMode(!currentMode);

            // Show success message
            String status = !currentMode ? "ON" : "OFF";
            String message = "Maintenance mode is now " + status + ".\n\n";
            message += !currentMode ?
                    "Students and Instructors now have VIEW-ONLY access." :
                    "Normal read/write access has been restored.";
            JOptionPane.showMessageDialog(this, message, "Settings Updated", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating maintenance mode: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Always update the button and banner text
            updateMaintenanceButton();
        }
    }

    /**
     * Reads the current maintenance state and updates the button/banner.
     */
    private void updateMaintenanceButton() {
        try {
            boolean maintenanceOn = adminService.isMaintenanceMode();

            // Update Button
            maintenanceButton.setText("<html><center>Toggle Maintenance<br/><small>STATUS: " + (maintenanceOn ? "ON" : "OFF") + "</small></center></html>");
            if (maintenanceOn) {
                maintenanceButton.setBackground(UITheme.COLOR_DANGER_RED);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);
            } else {
                maintenanceButton.setBackground(UITheme.COLOR_SUCCESS_GREEN);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);
            }

            // Update Banner
            if (maintenanceOn) {
                maintenanceBanner.setText("⚠️ MAINTENANCE MODE ACTIVE - Students/Instructors are view-only");
                UITheme.styleMaintenanceBanner(maintenanceBanner); // Yellow style
                maintenanceBanner.setVisible(true);
            } else {
                maintenanceBanner.setText("⚙️ ADMIN MODE - Full System Access");
                maintenanceBanner.setBackground(new Color(220, 220, 255)); // Light blue admin
                maintenanceBanner.setForeground(UITheme.COLOR_PRIMARY_BLUE);
                maintenanceBanner.setFont(UITheme.FONT_LABEL);
                maintenanceBanner.setOpaque(true);
                maintenanceBanner.setVisible(true);
            }
        } catch (SQLException ex) {
            maintenanceButton.setText("Maintenance: ERROR");
            maintenanceButton.setBackground(Color.GRAY);
        }
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}