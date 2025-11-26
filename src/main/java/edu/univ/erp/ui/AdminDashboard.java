package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.AuthService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private JButton logoutButton;
    private JButton changePassButton;
    private JButton backupButton;
    private JButton restoreButton;
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
        setTitle("Admin Dashboard - IIIT-Delhi ERP");
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

        JLabel titleLabel = new JLabel("IIIT-Delhi Administration");
        UITheme.styleSubHeaderLabel(titleLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(UITheme.FONT_SUB_HEADER);
        JLabel userLabel = new JLabel(AuthService.getCurrentUsername() + " (Admin)");
        UITheme.styleLabel(userLabel);

        changePassButton = new JButton("Change Password");
        UITheme.stylePrimaryButton(changePassButton);
        changePassButton.setPreferredSize(new Dimension(150, 35));

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

        JPanel menuGridPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        menuGridPanel.setBackground(UITheme.COLOR_BACKGROUND);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        manageUsersButton = styleDashboardButton(new JButton("Manage Users"), "👤");
        manageCoursesButton = styleDashboardButton(new JButton("Manage Courses"), "📚");
        manageSectionsButton = styleDashboardButton(new JButton("Manage Sections"), "📦");
        maintenanceButton = styleDashboardButton(new JButton("Maintenance: ..."), "⚙️");
        backupButton = styleDashboardButton(new JButton("Backup DB"), "💾");
        restoreButton = styleDashboardButton(new JButton("Restore DB"), "♻️");

        menuGridPanel.add(manageUsersButton);
        menuGridPanel.add(manageCoursesButton);
        menuGridPanel.add(manageSectionsButton);
        menuGridPanel.add(maintenanceButton);
        menuGridPanel.add(backupButton);
        menuGridPanel.add(restoreButton);

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
        restoreButton.addActionListener(e -> performRestore());
    }

    private void openUserManagement() { SwingUtilities.invokeLater(() -> new UserManagementWindow(this).setVisible(true)); }
    private void openCourseManagement() { SwingUtilities.invokeLater(() -> new CourseManagementWindow(this).setVisible(true)); }
    private void openSectionManagement() { SwingUtilities.invokeLater(() -> new SectionManagementWindow(this).setVisible(true)); }

    private void toggleMaintenanceMode() {
        try {
            boolean currentMode = adminService.isMaintenanceMode();
            adminService.setMaintenanceMode(!currentMode);
            updateMaintenanceState();
            JOptionPane.showMessageDialog(this, "Maintenance Mode " + (!currentMode ? "Enabled" : "Disabled"), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // --- Helper to find MySQL executables automatically ---
    private String getMySQLToolPath(String toolName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) return toolName;

        // Check standard paths
        String[] paths = {
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin",
                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin",
                "C:\\Program Files\\MySQL\\MySQL Server 8.1\\bin",
                "C:\\Program Files\\MySQL\\MySQL Server 8.2\\bin",
                "C:\\Program Files\\MySQL\\MySQL Server 8.3\\bin",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin"
        };

        for (String path : paths) {
            File tool = new File(path, toolName + ".exe");
            if (tool.exists()) return tool.getAbsolutePath();
        }
        return toolName;
    }

    // --- Helper to ask user for tool location ---
    private String promptUserForTool(String toolName) {
        JOptionPane.showMessageDialog(this,
                "Could not automatically find '" + toolName + "'.\nPlease locate the file manually to proceed.",
                "Tool Not Found", JOptionPane.WARNING_MESSAGE);

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Locate " + toolName + (System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : ""));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    private void performBackup() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Backup SQL");
            fileChooser.setSelectedFile(new File("university_erp_backup_" + System.currentTimeMillis() + ".sql"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String tool = getMySQLToolPath("mysqldump");

                try {
                    runBackupProcess(tool, file);
                    JOptionPane.showMessageDialog(this, "Backup Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    // Fallback: Ask user for tool
                    String manualTool = promptUserForTool("mysqldump");
                    if (manualTool != null) {
                        try {
                            runBackupProcess(manualTool, file);
                            JOptionPane.showMessageDialog(this, "Backup Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Backup Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Backup Cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runBackupProcess(String tool, File destination) throws Exception {
        String dbUser = "root";
        String dbPass = "root123";
        ProcessBuilder pb = new ProcessBuilder(
                tool, "-u" + dbUser, "-p" + dbPass, "--databases", "university_erp", "university_auth", "-r", destination.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new Exception("Process exited with code " + exitCode);
    }

    private void performRestore() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select SQL Backup to Restore");
            fileChooser.setFileFilter(new FileNameExtensionFilter("SQL Files", "sql"));

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String tool = getMySQLToolPath("mysql");

                try {
                    runRestoreProcess(tool, file);
                    JOptionPane.showMessageDialog(this, "Restore Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    // Fallback: Ask user for tool
                    String manualTool = promptUserForTool("mysql");
                    if (manualTool != null) {
                        try {
                            runRestoreProcess(manualTool, file);
                            JOptionPane.showMessageDialog(this, "Restore Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Restore Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Restore Cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runRestoreProcess(String tool, File source) throws Exception {
        String dbUser = "root";
        String dbPass = "root123";
        ProcessBuilder pb = new ProcessBuilder(tool, "-u" + dbUser, "-p" + dbPass);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (OutputStream os = p.getOutputStream();
             FileInputStream fis = new FileInputStream(source)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }

        int exitCode = p.waitFor();
        if (exitCode != 0) throw new Exception("Process exited with code " + exitCode);
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}