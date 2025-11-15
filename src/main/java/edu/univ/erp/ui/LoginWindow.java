package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
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
        setSize(450, 450); // Give it more breathing room
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Set the background color for the content pane
        getContentPane().setBackground(UITheme.COLOR_BACKGROUND);
    }

    private void createComponents() {
        // Main panel with centered alignment
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(UITheme.BORDER_PADDING);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND); // Use theme background

        // Title - Centered
        JLabel titleLabel = new JLabel("University ERP System");
        UITheme.styleHeaderLabel(titleLabel); // Use theme header style
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel with fixed width
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(300, 200)); // Increased height
        formPanel.setBackground(UITheme.COLOR_BACKGROUND); // Match background

        // --- Username row ---
        JLabel userLabel = new JLabel("Username:");
        UITheme.styleLabel(userLabel); // Use theme label style
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField(15);
        UITheme.styleTextField(usernameField); // Use theme text field style
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(300, 45)); // Set max size

        // --- Password row ---
        JLabel passLabel = new JLabel("Password:");
        UITheme.styleLabel(passLabel); // Use theme label style
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(15);
        UITheme.styleTextField(passwordField); // Use theme text field style
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(300, 45)); // Set max size

        // Login button - Centered
        loginButton = new JButton("Login");
        UITheme.stylePrimaryButton(loginButton); // Use theme primary button style
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setMaximumSize(new Dimension(100, 40));

        // Add everything with consistent spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40)); // More space

        // Add components to form panel
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20)); // More space
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(30)); // More space
        mainPanel.add(loginButton);

        add(mainPanel);
    }

    private void setupActions() {
        // Create a single action listener for logging in
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        };

        loginButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction); // Also triggers on "Enter"
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Use the AuthService to login
        boolean loginSuccess = AuthService.login(username, password);

        if (loginSuccess) {
            // Don't show a popup, just open the dashboard
            openDashboard();
        } else {
            showError("Login failed! Invalid username or password.");
        }
    }

    private void openDashboard() {
        this.dispose(); // Close login window

        String role = AuthService.getCurrentUserRole();

        // Use invokeLater to ensure UI updates are on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (role) {
                    case "admin":
                        new AdminDashboard().setVisible(true);
                        break;
                    case "student":
                        new StudentDashboard().setVisible(true);
                        break;
                    case "instructor":
                        new InstructorDashboard().setVisible(true);
                        break;
                    default:
                        showError("Unknown user role: " + role);
                        new LoginWindow().setVisible(true); // Show login again
                        break;
                }
            }
        });
    }

    // No changes to showSuccess or showError
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void startApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}