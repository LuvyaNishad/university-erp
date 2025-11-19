package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private JButton logoutButton;
    private JButton changePassButton; // Added
    private JButton backupButton;     // Added for BONUS
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
        updateMaintenanceState();
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
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(UITheme.COLOR_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel titleLabel = new JLabel("Administrator Dashboard");
        UITheme.styleSubHeaderLabel(titleLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(UITheme.FONT_SUB_HEADER);
        JLabel userLabel = new JLabel(AuthService.getCurrentUsername() + " (Admin)");
        UITheme.styleLabel(userLabel);

        changePassButton = new JButton("Password");
        UITheme.styleSecondaryButton(changePassButton);
        changePassButton.setPreferredSize(new Dimension(100, 35));

        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(90, 35));

        userPanel.add(userIcon);
        userPanel.add(userLabel);
        userPanel.add(changePassButton);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        maintenanceBanner = new JLabel("...", JLabel.CENTER);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(maintenanceBanner, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Grid for buttons
        JPanel menuGridPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        menuGridPanel.setBackground(UITheme.COLOR_BACKGROUND);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        manageUsersButton = styleDashboardButton(new JButton("Manage Users"), "👤");
        manageCoursesButton = styleDashboardButton(new JButton("Manage Courses"), "📚");
        manageSectionsButton = styleDashboardButton(new JButton("Manage Sections"), "📦");
        maintenanceButton = styleDashboardButton(new JButton("Maintenance: ..."), "⚙️");
        backupButton = styleDashboardButton(new JButton("Backup DB"), "💾"); // Bonus Button

        menuGridPanel.add(manageUsersButton);
        menuGridPanel.add(manageCoursesButton);
        menuGridPanel.add(manageSectionsButton);
        menuGridPanel.add(maintenanceButton);
        menuGridPanel.add(backupButton);

        add(menuGridPanel, BorderLayout.CENTER);
    }

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled() && !button.equals(maintenanceButton)) {
                    button.setBackground(UITheme.COLOR_PRIMARY_TEAL);
                    button.setForeground(UITheme.COLOR_WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
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
        changePassButton.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));
        manageUsersButton.addActionListener(e -> openUserManagement());
        manageCoursesButton.addActionListener(e -> openCourseManagement());
        manageSectionsButton.addActionListener(e -> openSectionManagement());
        maintenanceButton.addActionListener(e -> toggleMaintenanceMode());
        backupButton.addActionListener(e -> performBackup());
    }

    private void openUserManagement() { SwingUtilities.invokeLater(() -> new UserManagementWindow(this).setVisible(true)); }
    private void openCourseManagement() { SwingUtilities.invokeLater(() -> new CourseManagementWindow(this).setVisible(true)); }
    private void openSectionManagement() { SwingUtilities.invokeLater(() -> new SectionManagementWindow(this).setVisible(true)); }

    private void toggleMaintenanceMode() {
        try {
            boolean currentMode = adminService.isMaintenanceMode();
            adminService.setMaintenanceMode(!currentMode);
            updateMaintenanceState();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateMaintenanceState() {
        try {
            boolean maintenanceOn = adminService.isMaintenanceMode();
            String status = maintenanceOn ? "ON" : "OFF";
            maintenanceButton.setText("<html><center><span style='font-size: 32px;'>⚙️</span><br/><br/>" + "Maintenance: " + status + "</center></html>");

            if (maintenanceOn) {
                maintenanceButton.setBackground(UITheme.COLOR_SUCCESS_GREEN);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);
                maintenanceBanner.setText("⚠️ MAINTENANCE MODE ACTIVE - Students/Instructors are view-only");
                UITheme.styleMaintenanceBanner(maintenanceBanner);
                maintenanceBanner.setVisible(true);
            } else {
                maintenanceButton.setBackground(UITheme.COLOR_DANGER_RED);
                maintenanceButton.setForeground(UITheme.COLOR_WHITE);
                maintenanceBanner.setVisible(false);
            }
        } catch (SQLException ex) {
            maintenanceButton.setBackground(Color.GRAY);
        }
    }

    private void performBackup() {
        try {
            String dbName = "university_erp";
            String dbUser = "root";
            String dbPass = "root123";
            String savePath = "backup_" + System.currentTimeMillis() + ".sql";

            ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump", "-u" + dbUser, "-p" + dbPass, dbName, "-r", savePath
            );
            pb.start().waitFor();
            JOptionPane.showMessageDialog(this, "Backup saved to: " + savePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Backup failed. Ensure 'mysqldump' is installed.\n" + e.getMessage());
        }
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}