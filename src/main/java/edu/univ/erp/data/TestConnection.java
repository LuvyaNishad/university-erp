package edu.univ.erp.data;

public class TestConnection {
    public static void main(String[] args) {
        if (DatabaseConnection.testConnections()) {
            System.out.println("Both connections successful!");
        } else {
            System.out.println("Database connections failed!");
        }
    }
}
