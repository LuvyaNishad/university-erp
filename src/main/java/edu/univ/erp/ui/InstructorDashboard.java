package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorDashboard extends JFrame {
    private JButton logoutButton;

    public InstructorDashboard() {
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

        // Header with logout button
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButton = new JButton("Logout");
        headerPanel.add(logoutButton);

        // Content area
        JLabel contentLabel = new JLabel("INSTRUCTOR DASHBOARD - Coming Soon!", JLabel.CENTER);
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentLabel.setForeground(Color.GRAY);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentLabel, BorderLayout.CENTER);

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