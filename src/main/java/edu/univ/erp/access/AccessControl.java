package edu.univ.erp.access;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.SettingsDAO;
import java.sql.SQLException;

public class AccessControl {

    /**
     * Checks if action is allowed based on role and maintenance mode (PDF Page 3,7)
     */
    public static boolean isActionAllowed(String action, String resourceOwnerId) {
        SessionManager session = SessionManager.getInstance();

        // Check maintenance mode (PDF: When ON, students/instructors can view but not change)
        if (isMaintenanceModeOn() && !session.getCurrentUserRole().equals("admin")) {
            return false;
        }

        // Role-based access control (PDF: Admin-full, Instructor-own sections, Student-own data)
        String role = session.getCurrentUserRole();
        String currentUserId = session.getCurrentUserId();

        switch (role) {
            case "admin":
                return true; // Admin has full access
            case "instructor":
                return isInstructorActionAllowed(action, resourceOwnerId, currentUserId);
            case "student":
                return isStudentActionAllowed(action, resourceOwnerId, currentUserId);
            default:
                return false;
        }
    }

    public static boolean isMaintenanceModeOn() {
        try {
            SettingsDAO settingsDao = new SettingsDAO();
            return "true".equals(settingsDao.getSetting("maintenance_on"));
        } catch (SQLException e) {
            return true; // Fail-safe: assume maintenance mode on error
        }
    }

    private static boolean isInstructorActionAllowed(String action, String resourceOwnerId, String currentUserId) {
        // Instructors can only manage their own sections (PDF Page 1)
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId);
    }

    private static boolean isStudentActionAllowed(String action, String resourceOwnerId, String currentUserId) {
        // Students can only access their own data (PDF Page 1)
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId);
    }

    /**
     * Quick check for UI to show maintenance banner (PDF Page 1)
     */
    public static boolean shouldShowMaintenanceBanner() {
        return isMaintenanceModeOn();
    }
}