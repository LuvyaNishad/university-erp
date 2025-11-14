package edu.univ.erp.domain;

public class Grade {
    private String gradeId;
    private String enrollmentId;
    private String component;
    private Double score;
    private String finalGrade;

    // For display
    private String studentName;
    private String courseCode;

    public Grade() {}

    public Grade(String gradeId, String enrollmentId, String component, Double score, String finalGrade) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    public String getGradeId() { return gradeId; }
    public void setGradeId(String gradeId) { this.gradeId = gradeId; }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getFinalGrade() { return finalGrade; }
    public void setFinalGrade(String finalGrade) { this.finalGrade = finalGrade; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @Override
    public String toString() {
        return component + ": " + score + " (" + finalGrade + ")";
    }
}