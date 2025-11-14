package edu.univ.erp.domain;

public class Course {
    private String courseId;
    private String code;
    private String title;
    private int credits;

    public Course() {}

    public Course(String courseId, String code, String title, int credits) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return code + " - " + title + " (" + credits + " credits)";
    }
}