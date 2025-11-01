package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setupWindow();
        createComponents();
        setupActions();
    }

    private void setupWindow() {
        setTitle("University ERP - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
    }

    private void createComponents() {
        // Create panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username row
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);

        // Password row
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add everything to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30)); // Space

        mainPanel.add(userPanel);
        mainPanel.add(Box.createVerticalStrut(15)); // Space

        mainPanel.add(passPanel);
        mainPanel.add(Box.createVerticalStrut(30)); // Space

        mainPanel.add(loginButton);

        // Add panel to window
        add(mainPanel);
    }

    private void setupActions() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        // Get user input
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Demo login logic
        if (username.equals("admin") && password.equals("password")) {
            showSuccess("Welcome Admin! Dashboard will open here later.");
        }
        else if (username.equals("student") && password.equals("password")) {
            showSuccess("Welcome Student! Dashboard will open here later.");
        }
        else if (username.equals("instructor") && password.equals("password")) {
            showSuccess("Welcome Instructor! Dashboard will open here later.");
        }
        else {
            showError("Login failed! Try:\nadmin/password\nstudent/password\ninstructor/password");
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Method to start the login window
    public static void startApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }

    // Test this window separately
    public static void main(String[] args) {
        startApplication();
    }
}