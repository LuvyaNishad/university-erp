package edu.univ.erp.service;

import edu.univ.erp.domain.User;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.auth.PasswordUtil; // <-- 1. Import the new utility

public class AuthService {
    public static User login(String username, String password) {
        try {
            // Get user from database
            User user = UserDAO.findByUsername(username);

            if (user != null && user.isActive()) {

                // 2. Use secure hash comparison instead of plaintext
                // This checks the user's typed password against the hash from the DB
                if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                    // Update last login
                    UserDAO.updateLastLogin(user.getUserId());
                    return user;
                }
            }
            return null; // Return null if user not found or password incorrect
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}