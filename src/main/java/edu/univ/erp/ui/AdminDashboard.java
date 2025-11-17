package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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
        updateMaintenanceState(); // Sets initial state
    }

    private void setupWindow() {
        setTitle("Admin Dashboard - University ERP");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.COLOR_BACKGROUND);
    }

    private void createComponents() {
        // --- 1. NORTH: Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(UITheme.COLOR_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel titleLabel = new JLabel("Administrator Dashboard");
        UITheme.styleSubHeaderLabel(titleLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // User Panel (Icon + Name + Logout)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(UITheme.FONT_SUB_HEADER);
        JLabel userLabel = new JLabel(AuthService.getCurrentUsername() + " (Admin)");
        UITheme.styleLabel(userLabel);
        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(100, 35));

        userPanel.add(userIcon);
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Maintenance Banner
        maintenanceBanner = new JLabel("...", JLabel.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(maintenanceBanner, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. CENTER: Menu Grid Panel ---
        JPanel menuGridPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        menuGridPanel.setBackground(UITheme.COLOR_BACKGROUND);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Buttons
        manageUsersButton = styleDashboardButton(new JButton("Manage Users"), "👤");
        manageCoursesButton = styleDashboardButton(new JButton("Manage Courses"), "📚");
        manageSectionsButton = styleDashboardButton(new JButton("Manage Sections"), "📦");
        maintenanceButton = styleDashboardButton(new JButton("Maintenance: ..."), "⚙️");

        menuGridPanel.add(manageUsersButton);
        menuGridPanel.add(manageCoursesButton);
        menuGridPanel.add(manageSectionsButton);
        menuGridPanel.add(maintenanceButton);

        add(menuGridPanel, BorderLayout.CENTER);
    }

    /**
     * Helper to style the new dashboard buttons
     */
    private JButton styleDashboardButton(JButton button, String icon) {
        button.setText("<html><center><span style='font-size: 32px;'>" + icon + "</span><br/><br/>"
                + button.getText() + "</center></html>");
        button.setFont(UITheme.FONT_SUB_HEADER);
        button.setForeground(UITheme.COLOR_GRAY_DARKEST);
        button.setBackground(UITheme.COLOR_WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 150));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Only apply hover if NOT the maintenance button
                if (button.isEnabled() && !button.equals(maintenanceButton)) {
                    button.setBackground(UITheme.COLOR_PRIMARY_TEAL);
                    button.setForeground(UITheme.COLOR_WHITE);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Only reset color if NOT the maintenance button
                if (!button.equals(maintenanceButton)) {
                    button.setBackground(UITheme.COLOR_WHITE);
                    button.setForeground(UITheme.COLOR_GRAY_DARKEST);
                }
            }
        });
        return button;
    }

    private void setupActions() {
        logoutButton.addActionListener(e -> logout());
        manageUsersButton.addActionListener(e -> openUserManagement());
        manageCoursesButton.addActionListener(e -> openCourseManagement());
        manageSectionsButton.addActionListener(e -> openSectionManagement());
        maintenanceButton.addActionListener(e -> toggleMaintenanceMode());
    }

    private void openUserManagement() {
        SwingUtilities.invokeLater(() -> new UserManagementWindow(this).setVisible(true));
    }

    private void openCourseManagement() {
        SwingUtilities.invokeLater(() -> new CourseManagementWindow(this).setVisible(true));
    }

    private void openSectionManagement() {
        SwingUtilities.invokeLater(() -> new SectionManagementWindow(this).setVisible(true));
    }

    /**
     * This method correctly toggles the state in the database.
     */
    private void toggleMaintenanceMode() {
        try {
            boolean currentMode = adminService.isMaintenanceMode();
            // This line flips the value and saves it to the DB
            adminService.setMaintenanceMode(!currentMode);

            String status = !currentMode ? "ON" : "OFF";
            String message = "Maintenance mode is now " + status + ".\n\n";
            message += !currentMode ?
                    "Students and Instructors now have VIEW-ONLY access." :
                    "Normal read/write access has been restored.";
            JOptionPane.showMessageDialog(this, message, "Settings Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating maintenance mode: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // This re-reads the new value from the DB and updates the color
            updateMaintenanceState();
        }
    }

    /**
     * This method reads the *current* state and updates the UI.
     * The color logic is now set to your request.
     */
    private void updateMaintenanceState() {
        try {
            // 1. Read the CURRENT state from the database
            boolean maintenanceOn = adminService.isMaintenanceMode();
            String status = maintenanceOn ? "ON" : "OFF";

            maintenanceButton.setText("<html><center><span style='font-size: 32px;'>⚙️</span><br/><br/>"
                    + "Maintenance: " + status + "</center></html>");

            // --- 2. SET COLORS AS REQUESTED ---
            if (maintenanceOn) {
                // User requested GREEN for ON
                maintenanceButton.setBackground(UITheme.COLOR_SUCCESS_GREEN);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);

                // Update Banner
                maintenanceBanner.setText("⚠️ MAINTENANCE MODE ACTIVE - Students/Instructors are view-only");
                UITheme.styleMaintenanceBanner(maintenanceBanner);
                maintenanceBanner.setVisible(true);
            } else {
                // User requested RED for OFF
                maintenanceButton.setBackground(UITheme.COLOR_DANGER_RED);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);

                // Hide Banner
                maintenanceBanner.setVisible(false);
            }
        } catch (SQLException ex) {
            maintenanceButton.setText("<html><center><span style='font-size: 32px;'>⚙️</span><br/><br/>Maintenance: ERROR</center></html>");
            maintenanceButton.setBackground(Color.GRAY);
        }
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}