package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A central utility class to manage the IIIT Delhi UI theme.
 */
public class UITheme {

    // --- IIITD Color Palette ---
    // #3FADA8 is the official IIIT-Delhi "Keppel" Teal color
    public static final Color COLOR_PRIMARY_TEAL = new Color(63, 173, 168);
    public static final Color COLOR_GRAY_DARKEST = new Color(51, 51, 51);
    public static final Color COLOR_GRAY_DARK = new Color(77, 77, 77);
    public static final Color COLOR_GRAY_MEDIUM = new Color(102, 102, 102);
    public static final Color COLOR_GRAY_LIGHT = new Color(128, 128, 128);

    // --- Standard UI Colors ---
    public static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Color COLOR_HEADER_BACKGROUND = Color.WHITE;
    public static final Color COLOR_TEXT_DARK = COLOR_GRAY_DARKEST;
    public static final Color COLOR_TEXT_LIGHT = new Color(90, 90, 90);
    public static final Color COLOR_TEXT_WHITE = Color.WHITE;
    public static final Color COLOR_DANGER_RED = new Color(220, 53, 69);
    public static final Color COLOR_SUCCESS_GREEN = new Color(40, 167, 69);
    public static final Color COLOR_YELLOW_WARNING = new Color(255, 193, 7);

    // --- Dashboard Button Colors ---
    public static final Color COLOR_PRIMARY_BLUE = new Color(70, 130, 180);
    public static final Color COLOR_PRIMARY_GREEN = new Color(60, 179, 113);
    public static final Color COLOR_PRIMARY_YELLOW = new Color(218, 165, 32);
    public static final Color COLOR_PRIMARY_PURPLE = new Color(148, 0, 211);

    // --- Fonts ---
    private static final String FONT_NAME = "Segoe UI";
    public static final Font FONT_HEADER = new Font(FONT_NAME, Font.BOLD, 24);
    public static final Font FONT_SUB_HEADER = new Font(FONT_NAME, Font.BOLD, 18);
    public static final Font FONT_BUTTON = new Font(FONT_NAME, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font(FONT_NAME, Font.BOLD, 14);

    // --- Borders ---
    public static final Border BORDER_PADDING = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    public static final Border BORDER_PADDING_DIALOG = BorderFactory.createEmptyBorder(15, 15, 15, 15);
    public static final Border BORDER_TEXT_PADDING = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    public static final Border BORDER_TEXT_LINE = BorderFactory.createLineBorder(new Color(200, 200, 200));
    public static final Border BORDER_TABLE = BorderFactory.createLineBorder(new Color(200, 200, 200));

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(COLOR_PRIMARY_TEAL);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(COLOR_GRAY_MEDIUM);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleDashboardButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT_WHITE);
        button.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 100));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static void styleHeaderLabel(JLabel label) {
        label.setFont(FONT_HEADER);
        label.setForeground(COLOR_PRIMARY_TEAL);
    }

    public static void styleSubHeaderLabel(JLabel label) {
        label.setFont(FONT_SUB_HEADER);
        label.setForeground(COLOR_GRAY_DARKEST);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_GRAY_DARK);
    }

    public static void styleTextField(JComponent field) {
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BORDER_TEXT_LINE,
                BORDER_TEXT_PADDING
        ));
    }

    public static void styleMaintenanceBanner(JLabel banner) {
        banner.setBackground(COLOR_YELLOW_WARNING);
        banner.setForeground(COLOR_TEXT_DARK);
        banner.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        banner.setOpaque(true);
    }

    public static void styleTable(JScrollPane scrollPane) {
        JTable table = (JTable) scrollPane.getViewport().getView();
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(COLOR_GRAY_DARK);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setFillsViewportHeight(true);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public static void setupFlatLaf() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("defaultFont", FONT_BODY);
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF. Using default." );
        }
    }
}