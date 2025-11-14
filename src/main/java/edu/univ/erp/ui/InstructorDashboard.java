package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.InstructorService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class InstructorDashboard extends JFrame {
    private JButton logoutButton;
    private JButton mySectionsButton;
    private JButton gradebookButton;
    private JButton studentsButton;
    private JButton reportsButton;
    private JLabel maintenanceBanner;
    private JLabel welcomeLabel;
    private InstructorService instructorService;

    public InstructorDashboard() {
        this.instructorService = new InstructorService();
        setupWindow();
        createComponents();
        setupActions();
        checkMaintenanceMode();
    }

    private void setupWindow() {
        setTitle("Instructor Dashboard - University ERP");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Maintenance banner
        maintenanceBanner = new JLabel("⚠️ MAINTENANCE MODE - View Only Access", JLabel.CENTER);
        maintenanceBanner.setBackground(Color.YELLOW);
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setForeground(Color.RED);
        maintenanceBanner.setFont(new Font("Arial", Font.BOLD, 14));
        maintenanceBanner.setVisible(false);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(240, 240, 240));

        welcomeLabel = new JLabel("Welcome, Instructor (" + AuthService.getCurrentUserId() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        logoutButton = new JButton("Logout");

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu panel
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        mySectionsButton = new JButton("<html><center>📖 My Sections<br/><small>View assigned courses</small></center></html>");
        gradebookButton = new JButton("<html><center>📝 Gradebook<br/><small>Enter scores and grades</small></center></html>");
        studentsButton = new JButton("<html><center>👥 Student Lists<br/><small>View enrolled students</small></center></html>");
        reportsButton = new JButton("<html><center>📈 Reports<br/><small>Class statistics & exports</small></center></html>");

        // Style buttons
        styleInstructorButton(mySectionsButton, new Color(70, 130, 180));
        styleInstructorButton(gradebookButton, new Color(60, 179, 113));
        styleInstructorButton(studentsButton, new Color(218, 165, 32));
        styleInstructorButton(reportsButton, new Color(186, 85, 211));

        menuPanel.add(mySectionsButton);
        menuPanel.add(gradebookButton);
        menuPanel.add(studentsButton);
        menuPanel.add(reportsButton);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel contentLabel = new JLabel("INSTRUCTOR DASHBOARD - Manage your courses and students", JLabel.CENTER);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        contentLabel.setForeground(Color.GRAY);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Add all panels to main panel
        mainPanel.add(maintenanceBanner, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleInstructorButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 100));
        button.setFocusPainted(false);
    }

    private void setupActions() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        mySectionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMySections();
            }
        });

        gradebookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGradebook();
            }
        });

        studentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudentLists();
            }
        });

        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReports();
            }
        });
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);

        // Disable write actions in maintenance mode
        boolean allowWriteActions = !maintenanceOn;
        mySectionsButton.setEnabled(true); // Viewing always allowed
        gradebookButton.setEnabled(allowWriteActions);
        studentsButton.setEnabled(true);   // Viewing always allowed
        reportsButton.setEnabled(allowWriteActions);
    }

    private void viewMySections() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            // This would normally fetch from database
            JOptionPane.showMessageDialog(this,
                    "Your Assigned Sections:\n\n" +
                            "CS101 - Introduction to Programming:\n" +
                            "• Section: SEC001\n" +
                            "• Schedule: Mon Wed 10:00-11:30\n" +
                            "• Room: 101\n" +
                            "• Students: 25/30\n\n" +
                            "CS201 - Data Structures:\n" +
                            "• Section: SEC002\n" +
                            "• Schedule: Tue Thu 14:00-15:30\n" +
                            "• Room: 102\n" +
                            "• Students: 20/25",
                    "My Sections",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sections: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openGradebook() {
        try {
            // Check if action is allowed
            if (!AccessControl.isActionAllowed("enter_grades", AuthService.getCurrentUserId())) {
                JOptionPane.showMessageDialog(this,
                        "Action not allowed during maintenance mode.",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Gradebook would open here.\n\n" +
                            "Features:\n" +
                            "• Enter midterm/final/assignment scores\n" +
                            "• Compute final grades automatically\n" +
                            "• Export grades to CSV",
                    "Gradebook",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error opening gradebook: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudentLists() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            // This would normally fetch from database
            JOptionPane.showMessageDialog(this,
                    "Enrolled Students by Section:\n\n" +
                            "CS101 - SEC001:\n" +
                            "• Roll No: 2023001 - Program: CS\n" +
                            "• Roll No: 2023002 - Program: CS\n" +
                            "• ... 23 more students\n\n" +
                            "CS201 - SEC002:\n" +
                            "• Roll No: 2023001 - Program: CS\n" +
                            "• Roll No: 2023002 - Program: CS\n" +
                            "• ... 18 more students",
                    "Student Lists",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading student lists: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReports() {
        try {
            // Check if action is allowed
            if (!AccessControl.isActionAllowed("generate_reports", AuthService.getCurrentUserId())) {
                JOptionPane.showMessageDialog(this,
                        "Action not allowed during maintenance mode.",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Reports would generate here.\n\n" +
                            "Available Reports:\n" +
                            "• Class averages and statistics\n" +
                            "• Grade distribution charts\n" +
                            "• CSV export for all grades",
                    "Reports",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating reports: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        new LoginWindow().setVisible(true);
    }
}