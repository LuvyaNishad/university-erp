package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.PasswordUtil;
import edu.univ.erp.data.AuthDAO;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import java.sql.SQLException;
import java.util.List;

public class AdminService {

    /**
     * Toggle maintenance mode (PDF: Toggle Maintenance Mode)
     */
    public void setMaintenanceMode(boolean enabled) throws SQLException {
        SettingsDAO.updateSetting("maintenance_on", enabled ? "true" : "false");
    }

    public boolean isMaintenanceMode() throws SQLException {
        String value = SettingsDAO.getSetting("maintenance_on");
        return "true".equals(value);
    }

    /**
     * Create new user in Auth DB (PDF: Add users)
     */
    public boolean createUser(String userId, String username, String password, String role) {
        // This would normally insert into Auth DB
        // For now, we'll just return true as placeholder
        // In real implementation, you'd use AuthDAO to insert into users_auth table
        return true;
    }

    /**
     * Create student profile in ERP DB
     */
    public boolean createStudent(Student student) throws SQLException {
        return StudentDAO.createStudent(student);
    }

    /**
     * Create instructor profile in ERP DB
     */
    public boolean createInstructor(Instructor instructor) throws SQLException {
        return InstructorDAO.createInstructor(instructor);
    }

    /**
     * Create course (PDF: Create courses)
     */
    public boolean createCourse(Course course) throws SQLException {
        return CourseDAO.createCourse(course);
    }

    /**
     * Create section (PDF: Create sections)
     */
    public boolean createSection(Section section) throws SQLException {
        return SectionDAO.createSection(section);
    }

    /**
     * Assign instructor to section (PDF: Assign instructor)
     */
    public boolean assignInstructorToSection(String sectionId, String instructorId) throws SQLException {
        // Implementation would update section's instructor_id
        return true;
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() throws SQLException {
        return StudentDAO.getAllStudents();
    }

    /**
     * Get all instructors
     */
    public List<Instructor> getAllInstructors() throws SQLException {
        return InstructorDAO.getAllInstructors();
    }

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() throws SQLException {
        return CourseDAO.getAllCourses();
    }

    /**
     * Get all sections
     */
    public List<Section> getAllSections() throws SQLException {
        return SectionDAO.getAllSections();
    }
}