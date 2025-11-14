package edu.univ.erp.domain;

public class Section {
    private String sectionId;
    private String courseId;
    private String instructorId;
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;

    // For display
    private String courseTitle;
    private String instructorName;

    public Section() {}

    public Section(String sectionId, String courseId, String instructorId, String dayTime,
                   String room, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // Getters and Setters
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public String getDayTime() { return dayTime; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public boolean hasAvailableSeats(int currentEnrollment) {
        return currentEnrollment < capacity;
    }

    @Override
    public String toString() {
        return sectionId + " - " + courseTitle + " (" + dayTime + ", " + room + ")";
    }
}