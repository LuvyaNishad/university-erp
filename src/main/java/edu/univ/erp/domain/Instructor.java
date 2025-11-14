package edu.univ.erp.domain;

public class Instructor {
    private String userId;
    private String department;

    public Instructor() {}

    public Instructor(String userId, String department) {
        this.userId = userId;
        this.department = department;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Instructor{userId='" + userId + "', department='" + department + "'}";
    }
}