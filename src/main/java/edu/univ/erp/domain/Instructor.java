package edu.univ.erp.domain;

public class Instructor {
    private int instructorId;
    private int userId;
    private String name; // Changed from Name to name
    private String email;
    private String phone;
    private String department;

    // Constructors
    public Instructor() {}

    public Instructor(int instructorId, int userId, String name,
                      String email, String phone, String department) {
        this.instructorId = instructorId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
    }

    // Getters and Setters
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; } // Changed from getName
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}