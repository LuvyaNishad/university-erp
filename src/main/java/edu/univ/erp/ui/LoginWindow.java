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
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.COLOR_WHITE);
        setLayout(new GridLayout(1, 2)); // 1 row, 2 columns
    }

    /**
     * Creates the new two-panel UI components using UITheme palette.
     */
    private void createComponents() {
        // --- 1. Left (Info) Panel (Teal) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(UITheme.COLOR_PRIMARY_TEAL); // Use theme's teal color
        leftPanel.setLayout(new GridBagLayout()); // To center contents

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(10, 10, 10, 10);
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.anchor = GridBagConstraints.CENTER;

        // University Icon (simple text placeholder for "ERP")
        JLabel logoLabel = new JLabel("ERP"); // Simple text logo
        logoLabel.setFont(new Font(UITheme.FONT_HEADER.getName(), Font.BOLD, 120));
        logoLabel.setForeground(UITheme.COLOR_WHITE); // White text
        leftPanel.add(logoLabel, gbcLeft);

        // Title text for the left panel
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(UITheme.FONT_HEADER); // Use theme's header font
        titleLabel.setForeground(UITheme.COLOR_WHITE); // White text
        gbcLeft.gridy = 1; // Move to the next row
        leftPanel.add(titleLabel, gbcLeft);

        add(leftPanel); // Add to the left side of the GridLayout

        // --- 2. Right (Login) Panel (White) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UITheme.COLOR_WHITE); // White background
        rightPanel.setBorder(UITheme.BORDER_PADDING); // Add padding

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(8, 5, 8, 5); // Spacing for form elements
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.anchor = GridBagConstraints.WEST; // Align labels to the left
        gbcRight.fill = GridBagConstraints.HORIZONTAL; // Make fields fill width

        // "Welcome Back" Title
        JLabel welcomeLabel = new JLabel("Welcome Back");
        // Use styleHeaderLabel but override color to be dark
        UITheme.styleHeaderLabel(welcomeLabel);
        welcomeLabel.setForeground(UITheme.COLOR_GRAY_DARKEST); // Use darkest gray
        gbcRight.gridwidth = 2; // Span two columns
        gbcRight.anchor = GridBagConstraints.CENTER; // Center the title
        rightPanel.add(welcomeLabel, gbcRight);

        // "Please enter your details" Subtitle
        JLabel subtitleLabel = new JLabel("Please enter your details to log in.");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.COLOR_GRAY_MEDIUM); // Use medium gray
        gbcRight.gridy = 1;
        gbcRight.insets = new Insets(0, 5, 20, 5); // Space below subtitle
        rightPanel.add(subtitleLabel, gbcRight);

        // --- Form Fields ---
        gbcRight.gridwidth = 1; // Reset to one column
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.insets = new Insets(8, 5, 8, 5); // Reset insets

        // Username
        JLabel userLabel = new JLabel("Username:");
        UITheme.styleLabel(userLabel); // Applies theme font and color
        gbcRight.gridy = 2;
        rightPanel.add(userLabel, gbcRight);

        usernameField = new JTextField(20);
        UITheme.styleTextField(usernameField); // Applies theme border, font, padding
        gbcRight.gridy = 3;
        rightPanel.add(usernameField, gbcRight);

        // Password
        JLabel passLabel = new JLabel("Password:");
        UITheme.styleLabel(passLabel);
        gbcRight.gridy = 4;
        rightPanel.add(passLabel, gbcRight);

        passwordField = new JPasswordField(20);
        UITheme.styleTextField(passwordField);
        gbcRight.gridy = 5;
        rightPanel.add(passwordField, gbcRight);

        // Login Button
        loginButton = new JButton("Login");
        UITheme.stylePrimaryButton(loginButton); // Applies theme teal color
        gbcRight.gridy = 6;
        gbcRight.anchor = GridBagConstraints.CENTER;
        gbcRight.fill = GridBagConstraints.NONE; // Don't stretch
        gbcRight.insets = new Insets(25, 5, 10, 5); // Space above button
        loginButton.setPreferredSize(new Dimension(200, 40));
        rightPanel.add(loginButton, gbcRight);

        add(rightPanel); // Add to the right side of the GridLayout
    }


    /**
     * Sets up the action listeners (no change)
     */
    private void setupActions() {
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        };

        loginButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    /**
     * Handles the login logic (no change)
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        boolean loginSuccess = AuthService.login(username, password);

        if (loginSuccess) {
            openDashboard();
        } else {
            showError("Login failed! Invalid username or password.");
        }
    }

    /**
     * Opens the correct dashboard based on role (no change)
     */
    private void openDashboard() {
        this.dispose();
        String role = AuthService.getCurrentUserRole();

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
                        new LoginWindow().setVisible(true);
                        break;
                }
            }
        });
    }

    // (Helper methods showSuccess and showError are unchanged)
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Starts the application (no change)
     */
    public static void startApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}