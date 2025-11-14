package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        updateMaintenanceButton();
    }

    private void setupWindow() {
        setTitle("Admin Dashboard - University ERP");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Maintenance banner
        maintenanceBanner = new JLabel("⚙️ ADMIN MODE - Full Access", JLabel.CENTER);
        maintenanceBanner.setBackground(new Color(173, 216, 230)); // Light blue
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setForeground(Color.BLUE);
        maintenanceBanner.setFont(new Font("Arial", Font.BOLD, 14));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel userLabel = new JLabel("Welcome, Administrator (" + AuthService.getCurrentUserId() + ")");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));

        logoutButton = new JButton("Logout");

        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu panel
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        manageUsersButton = new JButton("<html><center>Manage Users<br/><small>Add students/instructors</small></center></html>");
        manageCoursesButton = new JButton("<html><center>Manage Courses<br/><small>Create/edit courses</small></center></html>");
        manageSectionsButton = new JButton("<html><center>Manage Sections<br/><small>Create sections & assign instructors</small></center></html>");
        maintenanceButton = new JButton("Maintenance: OFF");

        // Style buttons
        styleAdminButton(manageUsersButton, new Color(70, 130, 180));
        styleAdminButton(manageCoursesButton, new Color(60, 179, 113));
        styleAdminButton(manageSectionsButton, new Color(186, 85, 211));
        maintenanceButton.setBackground(Color.GREEN);
        maintenanceButton.setFont(new Font("Arial", Font.BOLD, 14));

        menuPanel.add(manageUsersButton);
        menuPanel.add(manageCoursesButton);
        menuPanel.add(manageSectionsButton);
        menuPanel.add(maintenanceButton);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel contentLabel = new JLabel("ADMINISTRATOR DASHBOARD - Full system control access", JLabel.CENTER);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        contentLabel.setForeground(Color.GRAY);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Add all panels to main panel
        mainPanel.add(maintenanceBanner, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleAdminButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(200, 80));
    }

    private void setupActions() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        maintenanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMaintenanceMode();
            }
        });

        manageUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("User Management", "User management feature would open here.");
            }
        });

        manageCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Course Management", "Course management feature would open here.");
            }
        });

        manageSectionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Section Management", "Section management feature would open here.");
            }
        });
    }

    private void toggleMaintenanceMode() {
        try {
            boolean currentMode = adminService.isMaintenanceMode();
            adminService.setMaintenanceMode(!currentMode);
            updateMaintenanceButton();

            String status = !currentMode ? "ENABLED" : "DISABLED";
            String message = "Maintenance mode " + status + "\n\n";
            message += !currentMode ?
                    "Students and Instructors can only VIEW data." :
                    "All users have normal access permissions.";

            JOptionPane.showMessageDialog(this,
                    message,
                    "System Settings Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating maintenance mode: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMaintenanceButton() {
        try {
            boolean maintenanceOn = adminService.isMaintenanceMode();
            maintenanceButton.setText("Maintenance: " + (maintenanceOn ? "ON" : "OFF"));
            maintenanceButton.setBackground(maintenanceOn ? Color.RED : Color.GREEN);

            // Update banner
            if (maintenanceOn) {
                maintenanceBanner.setText("⚠️ MAINTENANCE MODE ACTIVE - Students/Instructors are view-only");
                maintenanceBanner.setBackground(Color.YELLOW);
                maintenanceBanner.setForeground(Color.RED);
            } else {
                maintenanceBanner.setText("⚙️ ADMIN MODE - Full System Access");
                maintenanceBanner.setBackground(new Color(173, 216, 230));
                maintenanceBanner.setForeground(Color.BLUE);
            }
        } catch (SQLException ex) {
            maintenanceButton.setText("Maintenance: ERROR");
        }
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        new LoginWindow().setVisible(true);
    }
}