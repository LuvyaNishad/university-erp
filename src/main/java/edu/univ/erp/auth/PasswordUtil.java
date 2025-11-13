package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Hashes a plain-text password using BCrypt.
     * This is used to generate hashes to store in the database.
     * @param plainTextPassword The password to hash.
     * @return A BCrypt hash string.
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks a plain-text password against a stored BCrypt hash.
     * This is used during the login process.
     * @param plainTextPassword The password from the user.
     * @param hashedPassword The hash from the database.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        // BCrypt.checkpw handles extracting the salt from the hash and
        // correctly comparing the password.
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}