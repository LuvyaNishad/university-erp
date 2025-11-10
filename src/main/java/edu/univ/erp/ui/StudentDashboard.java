package edu.univ.erp.ui;

import edu.univ.erp.domain.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentDashboard extends JFrame {
    private JButton logoutButton;
    private User currentUser;
    private JLabel welcomeLabel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        setupWindow();
        createComponents();
        setupActions();
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

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(240, 240, 240));

        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Student)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        logoutButton = new JButton("Logout");

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu panel
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton coursesBtn = new JButton("View Courses");
        JButton timetableBtn = new JButton("My Timetable");
        JButton gradesBtn = new JButton("My Grades");
        JButton transcriptBtn = new JButton("Download Transcript");

        menuPanel.add(coursesBtn);
        menuPanel.add(timetableBtn);
        menuPanel.add(gradesBtn);
        menuPanel.add(transcriptBtn);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel contentLabel = new JLabel("STUDENT DASHBOARD - Select an option from the menu", JLabel.CENTER);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        contentLabel.setForeground(Color.GRAY);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupActions() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void logout() {
        this.dispose(); // Close dashboard
        new LoginWindow().setVisible(true); // Return to login
    }
}