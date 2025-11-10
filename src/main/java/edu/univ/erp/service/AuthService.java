package edu.univ.erp.service;

import edu.univ.erp.domain.User;
import edu.univ.erp.data.UserDAO;

public class AuthService {
    public static User login(String username, String password) {
        try {
            // Get user from database
            User user = UserDAO.findByUsername(username);

            if (user != null && user.isActive()) {
                // Simple password verification for now
                if (password.equals(user.getPasswordHash())) {
                    // Update last login
                    UserDAO.updateLastLogin(user.getUserId());
                    return user;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}