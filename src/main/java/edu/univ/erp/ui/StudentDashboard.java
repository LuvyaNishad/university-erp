package edu.univ.erp.ui;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class StudentDashboard extends JFrame {
    private JButton logoutButton;
    private JButton coursesButton;
    private JButton timetableButton;
    private JButton gradesButton;
    private JButton transcriptButton;
    private JLabel maintenanceBanner;
    private JLabel welcomeLabel;
    private StudentService studentService;

    public StudentDashboard() {
        this.studentService = new StudentService();
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
    }

    private void createComponents() {
        // Main panel with BorderLayout
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

        welcomeLabel = new JLabel("Welcome, Student (" + AuthService.getCurrentUserId() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        logoutButton = new JButton("Logout");

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu panel
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        coursesButton = new JButton("<html><center>📚 Course Catalog<br/><small>Browse and register for courses</small></center></html>");
        timetableButton = new JButton("<html><center>📅 My Timetable<br/><small>View class schedule</small></center></html>");
        gradesButton = new JButton("<html><center>📊 My Grades<br/><small>View scores and final grades</small></center></html>");
        transcriptButton = new JButton("<html><center>📄 Download Transcript<br/><small>Export CSV/PDF report</small></center></html>");

        // Style buttons
        styleStudentButton(coursesButton, new Color(65, 105, 225));
        styleStudentButton(timetableButton, new Color(34, 139, 34));
        styleStudentButton(gradesButton, new Color(218, 165, 32));
        styleStudentButton(transcriptButton, new Color(148, 0, 211));

        menuPanel.add(coursesButton);
        menuPanel.add(timetableButton);
        menuPanel.add(gradesButton);
        menuPanel.add(transcriptButton);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel contentLabel = new JLabel("STUDENT DASHBOARD - Access your academic information", JLabel.CENTER);
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

    private void styleStudentButton(JButton button, Color color) {
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

        coursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCourseCatalog();
            }
        });

        timetableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTimetable();
            }
        });

        gradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGrades();
            }
        });

        transcriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadTranscript();
            }
        });
    }

    private void checkMaintenanceMode() {
        boolean maintenanceOn = AccessControl.shouldShowMaintenanceBanner();
        maintenanceBanner.setVisible(maintenanceOn);

        // Disable action buttons in maintenance mode
        boolean allowActions = !maintenanceOn;
        coursesButton.setEnabled(allowActions);
        timetableButton.setEnabled(true); // Viewing timetable should be allowed
        gradesButton.setEnabled(true);    // Viewing grades should be allowed
        transcriptButton.setEnabled(allowActions);
    }

    private void openCourseCatalog() {
        try {
            // Check if action is allowed (considering maintenance mode)
            if (!AccessControl.isActionAllowed("view_catalog", AuthService.getCurrentUserId())) {
                JOptionPane.showMessageDialog(this,
                        "Action not allowed during maintenance mode.",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Show course catalog
            JOptionPane.showMessageDialog(this,
                    "Course Catalog would open here with available courses.\n\n" +
                            "Features:\n" +
                            "• Browse all courses\n" +
                            "• View capacity and instructor\n" +
                            "• Register for available sections",
                    "Course Catalog",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error accessing course catalog: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openTimetable() {
        try {
            String studentId = AuthService.getCurrentUserId();
            // This would normally fetch from database
            JOptionPane.showMessageDialog(this,
                    "Your Weekly Timetable:\n\n" +
                            "Monday:\n" +
                            "• 10:00-11:30 - CS101 - Room 101\n\n" +
                            "Wednesday:\n" +
                            "• 10:00-11:30 - CS101 - Room 101\n\n" +
                            "Tuesday:\n" +
                            "• 14:00-15:30 - CS201 - Room 102\n\n" +
                            "Thursday:\n" +
                            "• 14:00-15:30 - CS201 - Room 102",
                    "My Timetable",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading timetable: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openGrades() {
        try {
            String studentId = AuthService.getCurrentUserId();
            // This would normally fetch from database
            JOptionPane.showMessageDialog(this,
                    "Your Current Grades:\n\n" +
                            "CS101 - Introduction to Programming:\n" +
                            "• Midterm: 85/100 (A)\n" +
                            "• Assignments: 92/100 (A)\n" +
                            "• Final Grade: A\n\n" +
                            "CS201 - Data Structures:\n" +
                            "• Midterm: 78/100 (B+)\n" +
                            "• Assignments: 88/100 (A-)\n" +
                            "• Final Grade: B+",
                    "My Grades",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading grades: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void downloadTranscript() {
        try {
            // Check if action is allowed
            if (!AccessControl.isActionAllowed("download_transcript", AuthService.getCurrentUserId())) {
                JOptionPane.showMessageDialog(this,
                        "Action not allowed during maintenance mode.",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Transcript downloaded successfully!\n\n" +
                            "File: transcript_" + AuthService.getCurrentUserId() + ".pdf\n" +
                            "Format: PDF\n" +
                            "Contains: Course history with grades and GPA",
                    "Transcript Download",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error downloading transcript: " + ex.getMessage(),
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