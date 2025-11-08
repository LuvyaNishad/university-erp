package edu.univ.erp.domain;

import java.time.LocalDate;

public class Student {
    private int roll_no;
    private int userId;
    private String Name;
    private String email;
    private String phone;
    private String program;
    private LocalDate year;

    // Constructors
    public Student() {}

    public Student(int roll_no, int userId, String Name,String email, String phone, String program, LocalDate year) {
        this.roll_no = roll_no;
        this.userId = userId;
        this.Name = Name;
        this.email = email;
        this.phone = phone;
        this.program = program;
        this.year = year;
    }

    // Getters and Setters
    public int getroll_no() { return roll_no; }
    public void setroll_no(int roll_no) { this.roll_no = roll_no; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return Name; }
    public void setName(String Name) { this.Name = Name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getprogram() { return program; }
    public void setprogram(String program) { this.program = program; }

    public LocalDate getyear() { return year; }
    public void setyear(LocalDate year) { this.year = year; }

    @Override
    public String toString() {
        return "Student{" +
                "roll_no=" + roll_no +
                ", userId=" + userId +
                ", name='" + Name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
