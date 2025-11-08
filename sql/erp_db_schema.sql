-- ERP Database Schema
CREATE DATABASE IF NOT EXISTS erp_db;
USE erp_db;

-- Students Table
CREATE TABLE students (
    roll_no INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    Name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    program VARCHAR(50)
    year INT NOT NULL,
    INDEX idx_user_id (user_id)
);

-- Instructors Table
CREATE TABLE instructors (
    instructor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    Name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    department VARCHAR(100),
    INDEX idx_user_id (user_id)
);

-- Courses Table
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credits INT NOT NULL CHECK (credits > 0),
    description TEXT,
    INDEX idx_course_code (course_code)
);

-- Sections Table
CREATE TABLE sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    section_name VARCHAR(10) NOT NULL,
    instructor_id INT,
    semester VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    max_capacity INT NOT NULL CHECK (max_capacity > 0),
    current_enrollment INT DEFAULT 0,
    schedule VARCHAR(100),
    room VARCHAR(50),
    UNIQUE KEY unique_section (course_id, section_number, semester, year),
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL,
    INDEX idx_instructor (instructor_id),
    INDEX idx_semester (semester, year)
);

-- Enrollments Table
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    enrollment_status ENUM('ENROLLED', 'DROPPED', 'COMPLETED') DEFAULT 'ENROLLED',
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    drop_date TIMESTAMP NULL,
    UNIQUE KEY unique_enrollment (student_id, section_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    INDEX idx_student (student_id),
    INDEX idx_section (section_id),
    INDEX idx_status (enrollment_status)
);

-- Grades Table
CREATE TABLE grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT UNIQUE NOT NULL,
    midterm_score DECIMAL(5,2) CHECK (midterm_score >= 0 AND midterm_score <= 100),
    final_score DECIMAL(5,2) CHECK (final_score >= 0 AND final_score <= 100),
    assignment_score DECIMAL(5,2) CHECK (assignment_score >= 0 AND assignment_score <= 100),
    final_grade VARCHAR(2),
    grade_date TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    INDEX idx_enrollment (enrollment_id)
);

-- Settings Table (for maintenance mode, etc.)
CREATE TABLE settings (
    setting_id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(50) UNIQUE NOT NULL,
    setting_value VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    INDEX idx_key (setting_key)
);

-- Insert default maintenance mode setting
INSERT INTO settings (setting_key, setting_value) VALUES 
('maintenance_mode', 'false');
