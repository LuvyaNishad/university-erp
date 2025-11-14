package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO {

    /**
     * Retrieves a setting value from the database.
     * @param key The key (e.g., "maintenance_mode")
     * @return The value as a String, or null if not found.
     */
    public static String getSetting(String key) throws SQLException {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        }
        return null;
    }

    /**
     * Updates a setting value.
     * @param key The key (e.g., "maintenance_mode")
     * @param value The new value to set
     */
    public static void updateSetting(String key, String value) throws SQLException {
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, key);
            stmt.executeUpdate();
        }
    }
}