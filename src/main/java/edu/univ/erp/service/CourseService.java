package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Course;
import java.sql.SQLException;
import java.util.List;

public class CourseService {

    public List<Course> getAllCourses() throws SQLException {
        return CourseDAO.getAllCourses();
    }

    public boolean createCourse(Course course) throws SQLException {
        return CourseDAO.createCourse(course);
    }

    public Course getCourseById(String courseId) throws SQLException {
        return CourseDAO.findById(courseId);
    }
}