package edu.univ.erp.domain;

public class Student {
    private String userId;
    private String rollNo;
    private String program;
    private int year;

    public Student() {}

    public Student(String userId, String rollNo, String program, int year) {
        this.userId = userId;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return "Student{userId='" + userId + "', rollNo='" + rollNo + "', program='" + program + "', year=" + year + "}";
    }
}