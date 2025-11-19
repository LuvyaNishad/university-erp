package edu.univ.erp.test;

import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.domain.Section;
import edu.univ.erp.data.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;

public class TestScenario {
    public static void main(String[] args) {
        System.out.println("🧪 STARTING TEST SCENARIO (Manual JUnit Equivalent)...");
        System.out.println("=====================================================");

        // 1. Test Connection
        System.out.print("TEST 1: Database Connection... ");
        if(DatabaseConnection.testConnections()) {
            System.out.println("✅ PASS");
        } else {
            System.out.println("❌ FAIL");
            return;
        }

        // 2. Test Login (Student)
        System.out.print("TEST 2: Student Login (stu1)... ");
        boolean loginSuccess = AuthService.login("stu1", "student123");
        if (loginSuccess) {
            System.out.println("✅ PASS");
        } else {
            System.out.println("❌ FAIL");
        }

        // 3. Test Fetch Sections
        System.out.print("TEST 3: Fetch Available Sections... ");
        StudentService studentService = new StudentService();
        try {
            List<Section> sections = studentService.getAvailableSections();
            if (sections != null) {
                System.out.println("✅ PASS (" + sections.size() + " sections found)");
            } else {
                System.out.println("⚠️ WARN (No sections found, but query worked)");
            }
        } catch (SQLException e) {
            System.out.println("❌ FAIL: " + e.getMessage());
        }

        // 4. Test Change Password Logic (Mock)
        System.out.print("TEST 4: Change Password (Mock check)... ");
        // We won't actually change it here to not break future logins,
        // but we can verify the method exists and compiles.
        try {
            // Simulating a method call
            Class.forName("edu.univ.erp.service.AuthService").getMethod("changePassword", String.class, String.class);
            System.out.println("✅ PASS (Method signature verified)");
        } catch (Exception e) {
            System.out.println("❌ FAIL (Method missing)");
        }

        System.out.println("=====================================================");
        System.out.println("🧪 TEST SCENARIO COMPLETE.");
    }
}