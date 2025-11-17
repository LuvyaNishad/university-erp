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
        return null; // Will return null if key doesn't exist
    }

    /**
     * --- THIS IS THE CORRECTED METHOD ---
     * Updates a setting value. If the key does not exist, it inserts it.
     * @param key The key (e.g., "maintenance_mode")
     * @param value The new value to set
     */
    public static void updateSetting(String key, String value) throws SQLException {
        // This is an "UPSERT" command. It tries to INSERT a new row.
        // If the PRIMARY KEY (setting_key) already exists, it runs the UPDATE part instead.
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setString(3, value); // Set the value again for the UPDATE part
            stmt.executeUpdate();
        }
    }
}