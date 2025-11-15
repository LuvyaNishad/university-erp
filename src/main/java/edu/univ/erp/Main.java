package edu.univ.erp;

import edu.univ.erp.ui.LoginWindow;
import edu.univ.erp.ui.UITheme; // <--- ADD THIS IMPORT

public class Main {
    public static void main(String[] args) {

        // --- ADD THIS LINE to set up the theme ---
        UITheme.setupFlatLaf();
        // ------------------------------------------

        System.out.println("🚀 Starting University ERP System...");

        // Start the login window
        LoginWindow.startApplication();

        System.out.println("✅ Application is running!");
    }
}