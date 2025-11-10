package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;
import edu.univ.erp.domain.User;
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
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        // Main panel with centered alignment
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title - Centered
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel with fixed width
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(300, 150));

        // Username row
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userPanel.setMaximumSize(new Dimension(300, 35));
        JLabel userLabel = new JLabel("Username:");
        userLabel.setPreferredSize(new Dimension(80, 25));
        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(180, 25));
        userPanel.add(userLabel);
        userPanel.add(usernameField);

        // Password row
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passPanel.setMaximumSize(new Dimension(300, 35));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setPreferredSize(new Dimension(80, 25));
        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(180, 25));
        passPanel.add(passLabel);
        passPanel.add(passwordField);

        // Login button - Centered
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(100, 30));

        // Add everything with consistent spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passPanel);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(loginButton);

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
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Use AuthService with real database
        User user = AuthService.login(username, password);

        if (user != null) {
            showSuccess("Welcome, " + user.getUsername() + "!");
            openDashboard(user);
        } else {
            showError("Login failed! Try:\nadmin/admin123\nstudent1/student123\ninstructor1/instructor123");
        }
    }

    private void openDashboard(User user) {
        this.setVisible(false); // Hide login window

        switch (user.getRole()) {
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
                break;
            case "STUDENT":
                new StudentDashboard(user).setVisible(true);
                break;
            case "INSTRUCTOR":
                new InstructorDashboard(user).setVisible(true);
                break;
            default:
                showError("Unknown user role: " + user.getRole());
                break;
        }
    }

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

    public static void main(String[] args) {
        startApplication();
    }
}