package edu.univ.erp.util;

import edu.univ.erp.auth.PasswordUtil;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        System.out.println("🔐 GENERATING REAL PASSWORD HASHES");
        System.out.println("===================================");

        // Generate REAL hashes
        String adminHash = PasswordUtil.hashPassword("admin123");
        String studentHash = PasswordUtil.hashPassword("student123");
        String instructorHash = PasswordUtil.hashPassword("instructor123");

        System.out.println("REAL HASHES (Copy these):");
        System.out.println("=========================");
        System.out.println("admin123:      " + adminHash);
        System.out.println("student123:    " + studentHash);
        System.out.println("instructor123: " + instructorHash);

        System.out.println("\n📋 SQL UPDATE COMMANDS:");
        System.out.println("=========================");
        System.out.println("USE university_auth;");
        System.out.println("UPDATE users_auth SET password_hash = '" + adminHash + "' WHERE username = 'admin1';");
        System.out.println("UPDATE users_auth SET password_hash = '" + studentHash + "' WHERE username = 'stu1';");
        System.out.println("UPDATE users_auth SET password_hash = '" + instructorHash + "' WHERE username = 'stu2';");
        System.out.println("UPDATE users_auth SET password_hash = '" + instructorHash + "' WHERE username = 'inst1';");
    }
}