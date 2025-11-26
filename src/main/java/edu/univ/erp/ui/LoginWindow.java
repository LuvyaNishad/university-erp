package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("IIIT-Delhi ERP - Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.COLOR_WHITE);
        setLayout(new GridLayout(1, 2));
        createComponents();
        setupActions();
    }

    private void createComponents() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.COLOR_PRIMARY_TEAL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel logo = new JLabel("IIIT-D"); // Updated Logo Text
        logo.setFont(new Font("Segoe UI", Font.BOLD, 80));
        logo.setForeground(Color.WHITE);
        left.add(logo, gbc);
        add(left);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5,5,5,5); gbc2.gridx=0; gbc2.gridy=0; gbc2.fill=GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome");
        UITheme.styleHeaderLabel(title);
        right.add(title, gbc2);

        gbc2.gridy++;
        right.add(new JLabel("Username:"), gbc2);
        gbc2.gridy++;
        usernameField = new JTextField(20);
        UITheme.styleTextField(usernameField);
        right.add(usernameField, gbc2);

        gbc2.gridy++;
        right.add(new JLabel("Password:"), gbc2);
        gbc2.gridy++;
        passwordField = new JPasswordField(20);
        UITheme.styleTextField(passwordField);
        right.add(passwordField, gbc2);

        gbc2.gridy++;
        loginButton = new JButton("Login");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new Dimension(200, 40));
        right.add(loginButton, gbc2);

        add(right);
    }

    private void setupActions() {
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if(user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter credentials", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String res = AuthService.login(user, pass);
        if("SUCCESS".equals(res)) {
            dispose();
            String role = AuthService.getCurrentUserRole();
            SwingUtilities.invokeLater(() -> {
                if("admin".equals(role)) new AdminDashboard().setVisible(true);
                else if("student".equals(role)) new StudentDashboard().setVisible(true);
                else if("instructor".equals(role)) new InstructorDashboard().setVisible(true);
            });
        } else if ("LOCKED".equals(res)) {
            JOptionPane.showMessageDialog(this, "🚫 Account Locked! Too many failed attempts.", "Locked", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void startApplication() { SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true)); }
}