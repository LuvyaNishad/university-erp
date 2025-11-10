package edu.univ.erp.domain;

import java.time.LocalDateTime;

public class Grade {
    private int gradeId;
    private int enrollmentId;
    private Double midtermScore;
    private Double finalScore;
    private Double assignmentScore;
    private String finalGrade;
    private LocalDateTime gradeDate;

    // For display purposes
    private int studentId;
    private String studentName;
    private String courseCode;

    // Constructors - GOOD
    public Grade() {}

    public Grade(int gradeId, int enrollmentId, Double midtermScore, Double finalScore,
                 Double assignmentScore, String finalGrade, LocalDateTime gradeDate) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
        this.assignmentScore = assignmentScore;
        this.finalGrade = finalGrade;
        this.gradeDate = gradeDate;
    }

    // Getters and Setters - GOOD
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public Double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(Double midtermScore) { this.midtermScore = midtermScore; }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }

    public Double getAssignmentScore() { return assignmentScore; }
    public void setAssignmentScore(Double assignmentScore) { this.assignmentScore = assignmentScore; }

    public String getFinalGrade() { return finalGrade; }
    public void setFinalGrade(String finalGrade) { this.finalGrade = finalGrade; }

    public LocalDateTime getGradeDate() { return gradeDate; }
    public void setGradeDate(LocalDateTime gradeDate) { this.gradeDate = gradeDate; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @Override
    public String toString() {
        return "Grade{" +
                "enrollmentId=" + enrollmentId +
                ", finalGrade='" + finalGrade + '\'' +
                '}';
    }
}