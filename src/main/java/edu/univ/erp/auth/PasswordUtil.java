package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            System.out.println("❌ Password check failed: " + e.getMessage());
            return false;
        }
    }
}