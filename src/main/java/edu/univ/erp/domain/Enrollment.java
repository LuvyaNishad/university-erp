package edu.univ.erp.domain;

public class Enrollment {
    private String enrollmentId;
    private String studentId;
    private String sectionId;
    private String status;

    // For display
    private String studentName;
    private String courseCode;
    private String sectionInfo;

    public Enrollment() {}

    public Enrollment(String enrollmentId, String studentId, String sectionId, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getSectionInfo() { return sectionInfo; }
    public void setSectionInfo(String sectionInfo) { this.sectionInfo = sectionInfo; }

    @Override
    public String toString() {
        return "Enrollment{" + enrollmentId + ", " + courseCode + ", " + status + "}";
    }
}