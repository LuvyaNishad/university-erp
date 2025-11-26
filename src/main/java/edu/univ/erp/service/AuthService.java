package edu.univ.erp.service;

import edu.univ.erp.auth.PasswordUtil;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.AuthDAO;
import java.sql.SQLException;

public class AuthService {

    public static String login(String username, String password) {
        System.out.println("🚀 AUTH: Login " + username);
        try {
            String[] result = AuthDAO.verifyLogin(username, password);
            if (result == null) return "FAILED";

            String status = result[0];
            if ("LOCKED".equals(status)) return "LOCKED";
            if ("INVALID".equals(status)) return "FAILED";

            if ("SUCCESS".equals(status)) {
                AuthDAO.updateLastLogin(result[1]);
                SessionManager.getInstance().login(result[1], result[2], result[3]);
                return "SUCCESS";
            }
            return "FAILED";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static boolean changePassword(String oldPass, String newPass) {
        String uid = SessionManager.getInstance().getCurrentUserId();
        if (uid == null) return false;
        try {
            String hash = AuthDAO.getPasswordHash(uid);
            if (hash != null && PasswordUtil.checkPassword(oldPass, hash)) {
                return AuthDAO.updatePassword(uid, PasswordUtil.hashPassword(newPass));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void logout() { SessionManager.getInstance().logout(); }
    public static String getCurrentUserRole() { return SessionManager.getInstance().getCurrentUserRole(); }
    public static String getCurrentUserId() { return SessionManager.getInstance().getCurrentUserId(); }
    public static String getCurrentUsername() { return SessionManager.getInstance().getCurrentUsername(); }
    public static boolean isLoggedIn() { return SessionManager.getInstance().isLoggedIn(); }
}