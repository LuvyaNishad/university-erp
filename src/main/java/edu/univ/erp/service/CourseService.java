package edu.univ.erp.service;

import edu.univ.erp.domain.Course;
import edu.univ.erp.data.CourseDAO;
import java.sql.SQLException;
import java.util.List;

public class CourseService {

    public int addCourse(Course course) throws SQLException {
        return CourseDAO.insertCourse(course);
    }

    public Course getCourse(int courseId) throws SQLException {
        return CourseDAO.findById(courseId);
    }

    public void updateCourse(Course course) throws SQLException {
        CourseDAO.updateCourse(course);
    }

    public void deleteCourse(int courseId) throws SQLException {
        CourseDAO.deleteCourse(courseId);
    }

    public List<Course> getAllCourses() throws SQLException {
        return CourseDAO.listAll();
    }
}
