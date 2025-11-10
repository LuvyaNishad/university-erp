package edu.univ.erp.domain;

import java.time.LocalDate;

public class Student {
    private int rollNo; // Changed from roll_no to rollNo (Java convention)
    private int userId;
    private String name; // Changed from Name to name
    private String email;
    private String phone;
    private String program; // Changed from program to program
    private int year; // Changed from LocalDate to int to match database

    // Constructors
    public Student() {}

    public Student(int rollNo, int userId, String name, String email,
                   String phone, String program, int year) {
        this.rollNo = rollNo;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.program = program;
        this.year = year;
    }

    // Getters and Setters
    public int getRollNo() { return rollNo; } // Changed from getroll_no
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; } // Changed from getName
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProgram() { return program; } // Changed from getprogram
    public void setProgram(String program) { this.program = program; }

    public int getYear() { return year; } // Changed from getyear and return type
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return "Student{" +
                "rollNo=" + rollNo +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", program='" + program + '\'' +
                ", year=" + year +
                '}';
    }
}