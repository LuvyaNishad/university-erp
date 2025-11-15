package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;

/**
 * Instructor Dashboard
 * This is the main hub for instructors. All buttons are now functional
 * and open their respective JDialog windows.
 */
public class InstructorDashboard extends JFrame {
    private JButton logoutButton;
    private JButton mySectionsButton;
    private JButton gradebookButton;
    private JButton studentsButton;
    private JButton reportsButton;
    private JLabel maintenanceBanner;
    private JLabel welcomeLabel;

    public InstructorDashboard() {
        setupWindow();
        createComponents();
        setupActions();
        checkMaintenanceMode();
    }

    private void setupWindow() {
        setTitle("Instructor Dashboard - University ERP");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.COLOR_BACKGROUND);
    }

    private void createComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- North: Maintenance Banner ---
        maintenanceBanner = new JLabel("⚠️ MAINTENANCE MODE - View Only Access", JLabel.CENTER);
        UITheme.styleMaintenanceBanner(maintenanceBanner);
        maintenanceBanner.setVisible(false);
        mainPanel.add(maintenanceBanner, BorderLayout.NORTH);

        // --- Center: Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(UITheme.BORDER_PADDING);
        headerPanel.setBackground(UITheme.COLOR_HEADER_BACKGROUND);

        welcomeLabel = new JLabel("Welcome, Instructor (" + AuthService.getCurrentUserId() + ")");
        UITheme.styleHeaderLabel(welcomeLabel);

        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(100, 40));

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.CENTER);

        // --- South: Menu Grid Panel ---
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBorder(UITheme.BORDER_PADDING);
        menuPanel.setBackground(UITheme.COLOR_BACKGROUND);

        mySectionsButton = new JButton("<html><center>📖 My Sections<br/><small>View assigned courses</small></center></html>");
        gradebookButton = new JButton("<html><center>📝 Gradebook<br/><small>Enter scores and grades</small></center></html>");
        studentsButton = new JButton("<html><center>👥 Student Lists<br/><small>View enrolled students</small></center></html>");
        reportsButton = new JButton("<html><center>📈 Reports<br/><small>Class statistics & exports</small></center></html>");

        // Style buttons
        UITheme.styleDashboardButton(mySectionsButton, UITheme.COLOR_PRIMARY_BLUE);
        UITheme.styleDashboardButton(gradebookButton, UITheme.COLOR_PRIMARY_GREEN);
        UITheme.styleDashboardButton(studentsButton, UITheme.COLOR_PRIMARY_YELLOW);
        UITheme.styleDashboardButton(reportsButton, UITheme.COLOR_PRIMARY_PURPLE);

        menuPanel.add(mySectionsButton);
        menuPanel.add(gradebookButton);
        menuPanel.add(studentsButton);
        menuPanel.add(reportsButton);

        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupActions() {
        logoutButton.addActionListener(e -> logout());

        // Connect buttons to their respective windows
        mySectionsButton.addActionListener(e -> viewMySections());
        gradebookButton.addActionListener(e -> openGradebook());
        studentsButton.addActionListener(e -> viewStudentLists());
        reportsButton.addActionListener(e -> generateReports());
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);

        // Disable write actions in maintenance mode
        boolean allowWriteActions = !maintenanceOn;
        mySectionsButton.setEnabled(true); // Viewing always allowed
        gradebookButton.setEnabled(allowWriteActions); // Grading is disabled
        studentsButton.setEnabled(true);   // Viewing always allowed
        reportsButton.setEnabled(true);    // Viewing always allowed
    }

    private void viewMySections() {
        SwingUtilities.invokeLater(() -> new MySectionsWindow(this).setVisible(true));
    }

    private void openGradebook() {
        if (!AccessControl.isActionAllowed("enter_grades", AuthService.getCurrentUserId())) {
            JOptionPane.showMessageDialog(this,
                    "Action not allowed during maintenance mode.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new GradebookWindow(this).setVisible(true));
    }

    private void viewStudentLists() {
        SwingUtilities.invokeLater(() -> new StudentListsWindow(this).setVisible(true));
    }

    private void generateReports() {
        SwingUtilities.invokeLater(() -> new ReportWindow(this).setVisible(true));
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}