package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;

/**
 * Student Dashboard
 * This is the main hub for students. All buttons are now functional
 * and open their respective JDialog windows.
 */
public class StudentDashboard extends JFrame {

    private JButton logoutButton;
    private JButton coursesButton;
    private JButton timetableButton;
    private JButton gradesButton;
    private JButton transcriptButton;
    private JLabel maintenanceBanner;
    private JLabel welcomeLabel;

    public StudentDashboard() {
        setupWindow();
        createComponents();
        setupActions();
        checkMaintenanceMode();
    }

    private void setupWindow() {
        setTitle("Student Dashboard - University ERP");
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
        maintenanceBanner.setVisible(false); // Hide by default
        mainPanel.add(maintenanceBanner, BorderLayout.NORTH);

        // --- Center: Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(UITheme.BORDER_PADDING);
        headerPanel.setBackground(UITheme.COLOR_HEADER_BACKGROUND);

        welcomeLabel = new JLabel("Welcome, Student (" + AuthService.getCurrentUserId() + ")");
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

        coursesButton = new JButton("<html><center>📚 Course Catalog<br/><small>Browse and register for courses</small></center></html>");
        timetableButton = new JButton("<html><center>📅 My Timetable<br/><small>View class schedule</small></center></html>");
        gradesButton = new JButton("<html><center>📊 My Grades<br/><small>View scores and final grades</small></center></html>");
        transcriptButton = new JButton("<html><center>📄 Download Transcript<br/><small>Export CSV/PDF report</small></center></html>");

        // Style buttons
        UITheme.styleDashboardButton(coursesButton, UITheme.COLOR_PRIMARY_BLUE);
        UITheme.styleDashboardButton(timetableButton, UITheme.COLOR_PRIMARY_GREEN);
        UITheme.styleDashboardButton(gradesButton, UITheme.COLOR_PRIMARY_YELLOW);
        UITheme.styleDashboardButton(transcriptButton, UITheme.COLOR_PRIMARY_PURPLE);

        menuPanel.add(coursesButton);
        menuPanel.add(timetableButton);
        menuPanel.add(gradesButton);
        menuPanel.add(transcriptButton);

        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupActions() {
        logoutButton.addActionListener(e -> logout());

        // Connect buttons to their respective windows
        coursesButton.addActionListener(e -> openCourseCatalog());
        timetableButton.addActionListener(e -> openTimetable());
        gradesButton.addActionListener(e -> openGrades());
        transcriptButton.addActionListener(e -> downloadTranscript());
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);

        // Disable action buttons in maintenance mode
        boolean allowActions = !maintenanceOn;
        coursesButton.setEnabled(allowActions); // Registering is disabled
        timetableButton.setEnabled(true); // Viewing timetable should be allowed
        gradesButton.setEnabled(true);    // Viewing grades should be allowed
        transcriptButton.setEnabled(true); // Viewing/downloading transcript allowed
    }

    private void openCourseCatalog() {
        SwingUtilities.invokeLater(() -> new CourseCatalogWindow(this).setVisible(true));
    }

    private void openTimetable() {
        SwingUtilities.invokeLater(() -> new MyTimetableWindow(this).setVisible(true));
    }

    private void openGrades() {
        SwingUtilities.invokeLater(() -> new MyGradesWindow(this).setVisible(true));
    }

    private void downloadTranscript() {
        SwingUtilities.invokeLater(() -> new TranscriptWindow(this).setVisible(true));
    }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}