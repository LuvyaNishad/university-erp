package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPassField, newPassField, confirmPassField;

    public ChangePasswordDialog(JFrame owner) {
        super(owner, "Change Password", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        formPanel.setBackground(UITheme.COLOR_WHITE);

        formPanel.add(new JLabel("Old Password:"));
        oldPassField = new JPasswordField();
        UITheme.styleTextField(oldPassField);
        formPanel.add(oldPassField);

        formPanel.add(new JLabel("New Password:"));
        newPassField = new JPasswordField();
        UITheme.styleTextField(newPassField);
        formPanel.add(newPassField);

        formPanel.add(new JLabel("Confirm New Password:"));
        confirmPassField = new JPasswordField();
        UITheme.styleTextField(confirmPassField);
        formPanel.add(confirmPassField);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JButton saveBtn = new JButton("Change Password");
        UITheme.stylePrimaryButton(saveBtn);

        saveBtn.addActionListener(e -> handleChange());
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void handleChange() {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password cannot be empty.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.");
            return;
        }

        if (AuthService.changePassword(oldPass, newPass)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect old password or database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}