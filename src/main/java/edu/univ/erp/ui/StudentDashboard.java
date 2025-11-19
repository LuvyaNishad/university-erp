package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    private JButton logoutButton;
    private JButton changePassButton; // Updated button
    private JButton coursesButton;
    private JButton timetableButton;
    private JButton gradesButton;
    private JButton transcriptButton;
    private JLabel maintenanceBanner;

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

        JLabel titleLabel = new JLabel("Student Dashboard");
        UITheme.styleSubHeaderLabel(titleLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // User Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(UITheme.FONT_SUB_HEADER);
        JLabel userLabel = new JLabel(AuthService.getCurrentUsername() + " (Student)");
        UITheme.styleLabel(userLabel);

        // --- UPDATED BUTTON ---
        changePassButton = new JButton("Change Password");
        UITheme.stylePrimaryButton(changePassButton); // Now Teal
        changePassButton.setPreferredSize(new Dimension(150, 35)); // Wider
        // ---------------------

        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(90, 35));

        userPanel.add(userIcon);
        userPanel.add(userLabel);
        userPanel.add(changePassButton);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        maintenanceBanner = new JLabel("⚠️ MAINTENANCE MODE - View Only Access", JLabel.CENTER);
        UITheme.styleMaintenanceBanner(maintenanceBanner);
        maintenanceBanner.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(maintenanceBanner, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel menuGridPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        menuGridPanel.setBackground(UITheme.COLOR_BACKGROUND);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        coursesButton = styleDashboardButton(new JButton("Course Catalog"), "📚");
        timetableButton = styleDashboardButton(new JButton("My Timetable"), "📅");
        gradesButton = styleDashboardButton(new JButton("My Grades"), "📊");
        transcriptButton = styleDashboardButton(new JButton("Transcript"), "📄");

        menuGridPanel.add(coursesButton);
        menuGridPanel.add(timetableButton);
        menuGridPanel.add(gradesButton);
        menuGridPanel.add(transcriptButton);

        add(menuGridPanel, BorderLayout.CENTER);
    }

    private JButton styleDashboardButton(JButton button, String icon) {
        button.setText("<html><center><span style='font-size: 32px;'>" + icon + "</span><br/><br/>" + button.getText() + "</center></html>");
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
                if (button.isEnabled()) {
                    button.setBackground(UITheme.COLOR_PRIMARY_TEAL);
                    button.setForeground(UITheme.COLOR_WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
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
        coursesButton.addActionListener(e -> openCourseCatalog());
        timetableButton.addActionListener(e -> openTimetable());
        gradesButton.addActionListener(e -> openGrades());
        transcriptButton.addActionListener(e -> downloadTranscript());
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);
        boolean allowActions = !maintenanceOn;
        coursesButton.setEnabled(allowActions);
        timetableButton.setEnabled(true);
        gradesButton.setEnabled(true);
        transcriptButton.setEnabled(true);
        if (!allowActions) {
            coursesButton.setToolTipText("Registration is disabled during maintenance mode");
            coursesButton.setBackground(new Color(230, 230, 230));
            coursesButton.setForeground(UITheme.COLOR_GRAY_LIGHT);
        }
    }

    private void openCourseCatalog() { SwingUtilities.invokeLater(() -> new CourseCatalogWindow(this).setVisible(true)); }
    private void openTimetable() { SwingUtilities.invokeLater(() -> new MyTimetableWindow(this).setVisible(true)); }
    private void openGrades() { SwingUtilities.invokeLater(() -> new MyGradesWindow(this).setVisible(true)); }
    private void downloadTranscript() { SwingUtilities.invokeLater(() -> new TranscriptWindow(this).setVisible(true)); }

    private void logout() {
        AuthService.logout();
        this.dispose();
        LoginWindow.startApplication();
    }
}