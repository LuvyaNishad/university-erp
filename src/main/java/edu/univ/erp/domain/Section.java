package edu.univ.erp.domain;

public class Section {
    private int sectionId;
    private int courseId;
    private String sectionName;
    private int instructorId;
    private String semester;
    private int year;
    private int maxCapacity;
    private int currentEnrollment;
    private String schedule;
    private String room;

    // For display purposes
    private String courseName;
    private String courseCode;
    private String instructorName;

    // Constructors
    public Section() {}

    public Section(int sectionId, int courseId, String sectionName, int instructorId, 
                   String semester, int year, int maxCapacity, int currentEnrollment, 
                   String schedule, String room) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.sectionName = sectionName;
        this.instructorId = instructorId;
        this.semester = semester;
        this.year = year;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = currentEnrollment;
        this.schedule = schedule;
        this.room = room;
    }

    // Getters and Setters
    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public boolean hasAvailableSeats() {
        return currentEnrollment < maxCapacity;
    }

    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment;
    }

    @Override
    public String toString() {
        return courseCode + " - Section " + sectionName + 
               " (" + semester + " " + year + ")";
    }
}
