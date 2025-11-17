package edu.univ.erp.access;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.SettingsDAO;
import java.sql.SQLException;

public class AccessControl {

    public static boolean isActionAllowed(String action, String resourceOwnerId) {
        SessionManager session = SessionManager.getInstance();

        if (isMaintenanceModeOn() && !session.getCurrentUserRole().equals("admin")) {
            // Define view-only actions that are still allowed in maintenance mode
            boolean isViewAction = action.equals("view_sections") ||
                    action.equals("view_grades") ||
                    action.equals("view_enrollments") ||
                    action.equals("download_transcript");

            if (!isViewAction) {
                // Block actions like "register", "drop", "enter_grades"
                return false;
            }
        }

        String role = session.getCurrentUserRole();
        String currentUserId = session.getCurrentUserId();

        switch (role) {
            case "admin":
                return true; // Admin has full access
            case "instructor":
                // Logic is mostly handled in InstructorService
                return true;
            case "student":
                // Logic is mostly handled in StudentService
                return true;
            default:
                return false;
        }
    }

    /**
     * --- THIS IS THE CORRECTED METHOD ---
     * It now calls the static method SettingsDAO.getSetting() correctly.
     * The previous code was: new SettingsDAO().getSetting(...) which is incorrect.
     */
    public static boolean isMaintenanceModeOn() {
        try {
            // FIX: Call the static method 'getSetting' directly on the class 'SettingsDAO'
            return "true".equals(SettingsDAO.getSetting("maintenance_on"));
        } catch (SQLException e) {
            return true; // Fail-safe: assume maintenance mode on error
        }
    }

    // These methods are not strictly used but are good practice
    private static boolean isInstructorActionAllowed(String action, String resourceOwnerId, String currentUserId) {
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId);
    }

    private static boolean isStudentActionAllowed(String action, String resourceOwnerId, String currentUserId) {
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId);
    }

    /**
     * Quick check for UI to show maintenance banner (PDF Page 1)
     */
    public static boolean shouldShowMaintenanceBanner() {
        return isMaintenanceModeOn();
    }
}