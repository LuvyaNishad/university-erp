package edu.univ.erp.ui;

import edu.univ.erp.domain.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorDashboard extends JFrame {
    private JButton logoutButton;
    private User currentUser;

    public InstructorDashboard(User user) {
        this.currentUser = user;
        setupWindow();
        createComponents();
        setupActions();
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

        // Header with user info and logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Instructor)");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));

        logoutButton = new JButton("Logout");

        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu panel
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton mySectionsBtn = new JButton("My Sections");
        JButton gradebookBtn = new JButton("Gradebook");
        JButton studentsBtn = new JButton("Student Lists");
        JButton reportsBtn = new JButton("Reports");

        menuPanel.add(mySectionsBtn);
        menuPanel.add(gradebookBtn);
        menuPanel.add(studentsBtn);
        menuPanel.add(reportsBtn);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel contentLabel = new JLabel("INSTRUCTOR DASHBOARD - Select an option from the menu", JLabel.CENTER);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        contentLabel.setForeground(Color.GRAY);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        contentPanel.add(contentLabel, BorderLayout.CENTER);

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