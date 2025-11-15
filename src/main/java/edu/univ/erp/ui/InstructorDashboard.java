package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private JButton logoutButton;
    private JButton mySectionsButton;
    private JButton gradebookButton;
    private JButton studentsButton;
    private JButton reportsButton;
    private JLabel maintenanceBanner;

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

        JLabel titleLabel = new JLabel("Instructor Dashboard");
        UITheme.styleSubHeaderLabel(titleLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // User Panel (Icon + Name + Logout)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false); // Transparent background
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(UITheme.FONT_SUB_HEADER);
        JLabel userLabel = new JLabel(AuthService.getCurrentUsername() + " (Instructor)");
        UITheme.styleLabel(userLabel);
        logoutButton = new JButton("Logout");
        UITheme.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(100, 35));

        userPanel.add(userIcon);
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        maintenanceBanner = new JLabel("⚠️ MAINTENANCE MODE - View Only Access", JLabel.CENTER);
        UITheme.styleMaintenanceBanner(maintenanceBanner);
        maintenanceBanner.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(maintenanceBanner, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. CENTER: Menu Grid Panel ---
        JPanel menuGridPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        menuGridPanel.setBackground(UITheme.COLOR_BACKGROUND);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Buttons from PDF
        mySectionsButton = styleDashboardButton(new JButton("My Sections"), "📦");
        gradebookButton = styleDashboardButton(new JButton("Gradebook"), "📓");
        studentsButton = styleDashboardButton(new JButton("Student Lists"), "👤");
        reportsButton = styleDashboardButton(new JButton("Reports"), "📊");

        menuGridPanel.add(mySectionsButton);
        menuGridPanel.add(gradebookButton);
        menuGridPanel.add(studentsButton);
        menuGridPanel.add(reportsButton);

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
        mySectionsButton.addActionListener(e -> viewMySections());
        gradebookButton.addActionListener(e -> openGradebook());
        studentsButton.addActionListener(e -> viewStudentLists());
        reportsButton.addActionListener(e -> generateReports());
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);

        boolean allowWriteActions = !maintenanceOn;
        mySectionsButton.setEnabled(true);
        gradebookButton.setEnabled(allowWriteActions); // Grading is disabled
        studentsButton.setEnabled(true);
        reportsButton.setEnabled(true);

        if (!allowWriteActions) {
            gradebookButton.setToolTipText("Gradebook is disabled during maintenance mode");
            // Set disabled look for the button
            gradebookButton.setBackground(new Color(230, 230, 230));
            gradebookButton.setForeground(UITheme.COLOR_GRAY_LIGHT);
        }
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