package edu.univ.erp.domain;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int rollNo; // Changed from roll_no to rollNo
    private int sectionId;
    private String enrollmentStatus; // ENROLLED, DROPPED, COMPLETED
    private LocalDateTime enrollmentDate;
    private LocalDateTime dropDate;

    // For display purposes
    private String courseCode;
    private String courseName;
    private String sectionName; // Changed from sectionNumber to sectionName
    private String semester;
    private int year;

    // Constructors
    public Enrollment() {}

    public Enrollment(int enrollmentId, int rollNo, int sectionId, String enrollmentStatus,
                      LocalDateTime enrollmentDate, LocalDateTime dropDate) {
        this.enrollmentId = enrollmentId;
        this.rollNo = rollNo;
        this.sectionId = sectionId;
        this.enrollmentStatus = enrollmentStatus;
        this.enrollmentDate = enrollmentDate;
        this.dropDate = dropDate;
    }

    // Getters and Setters
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getRollNo() { return rollNo; } // Changed from getroll_no
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public LocalDateTime getDropDate() { return dropDate; }
    public void setDropDate(LocalDateTime dropDate) { this.dropDate = dropDate; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSectionName() { return sectionName; } // Changed from getSectionNumber
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return courseCode + " - Section " + sectionName + " [" + enrollmentStatus + "]";
    }
}