-- ERP DB for IIIT-Delhi
CREATE DATABASE IF NOT EXISTS university_erp;
USE university_erp;

-- Students (PDF: students(user_id, roll_no, program, year))
CREATE TABLE students (
    user_id VARCHAR(50) PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100),
    year INT
);

-- Instructors (PDF: instructors(user_id, department, ...))
CREATE TABLE instructors (
    user_id VARCHAR(50) PRIMARY KEY,
    department VARCHAR(100)
);

-- Courses (PDF: courses(code, title, credits))
CREATE TABLE courses (
    course_id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INT NOT NULL
);

-- Sections (PDF: sections(course_id, instructor_id, day_time, room, capacity, semester, year))
CREATE TABLE sections (
    section_id VARCHAR(50) PRIMARY KEY,
    course_id VARCHAR(50),
    instructor_id VARCHAR(50),
    day_time VARCHAR(100),
    room VARCHAR(50),
    capacity INT,
    semester VARCHAR(50),
    year INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

-- Enrollments (PDF: enrollments(student_id, section_id, status))
CREATE TABLE enrollments (
    enrollment_id VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(50),
    section_id VARCHAR(50),
    status ENUM('registered', 'dropped') DEFAULT 'registered',
    UNIQUE KEY unique_enrollment (student_id, section_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

-- Grades (PDF: grades(enrollment_id, component, score, final_grade))
CREATE TABLE grades (
    grade_id VARCHAR(50) PRIMARY KEY,
    enrollment_id VARCHAR(50),
    component VARCHAR(100),
    score DECIMAL(5,2),
    final_grade VARCHAR(2),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

-- Settings (PDF: settings(key, value))
CREATE TABLE settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(500)
);

-- Insert default settings
INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_on', 'false');

-- Sample Data for IIIT-Delhi
-- Students (Roll numbers follow IIIT-D format YYYYXXX)
INSERT INTO students (user_id, roll_no, program, year) VALUES
('stu1', '2023001', 'B.Tech CSE', 2023),
('stu2', '2023045', 'B.Tech CSD', 2023),
('stu3', '2023102', 'B.Tech CSB', 2023);

-- Instructors (Departments: CSE, ECE, CB, HCD, Math, SSH)
INSERT INTO instructors (user_id, department) VALUES
('inst1', 'CSE'),
('inst2', 'ECE');

-- Courses (Real IIIT-D Course Codes)
INSERT INTO courses (course_id, code, title, credits) VALUES
('CSE101', 'CSE101', 'Introduction to Programming', 4),
('CSE102', 'CSE102', 'Data Structures and Algorithms', 4),
('CSE201', 'CSE201', 'Advanced Programming', 4),
('ECE111', 'ECE111', 'Digital Circuits', 4),
('MTH100', 'MTH100', 'Linear Algebra', 4),
('DES101', 'DES101', 'Introduction to Design', 4),
('COM101', 'COM101', 'Communication Skills', 4);

-- Sections (Semester: Monsoon/Winter)
INSERT INTO sections (section_id, course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
('SEC_IP_01', 'CSE101', 'inst1', 'Mon Wed 10:00-11:30', 'C01', 60, 'Monsoon', 2024),
('SEC_AP_01', 'CSE201', 'inst1', 'Tue Thu 14:00-15:30', 'C02', 55, 'Monsoon', 2024),
('SEC_DC_01', 'ECE111', 'inst2', 'Mon Wed 11:30-13:00', 'C11', 50, 'Monsoon', 2024);