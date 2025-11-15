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

    // --- ADD THESE 5 FIELDS ---
    // For display (from joined tables)
    private String courseCode;
    private String courseTitle;
    private String instructorName; // This will just be the instructor_id for now
    private int credits;
    private int currentEnrollment; // For tracking Filled/Total

    public Section() {}

    // (Constructor is fine, we will use setters)

    // --- Getters and Setters for all fields ---

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

    // --- GETTERS/SETTERS FOR NEW FIELDS ---

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }


    // (toString is fine)
    @Override
    public String toString() {
        return sectionId + " - " + courseTitle + " (" + dayTime + ", " + room + ")";
    }
}