-- ERP DB (PDF Page 4 - exact table structure)
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

-- Sample ERP data (PDF Page 8)
INSERT INTO students (user_id, roll_no, program, year) VALUES
('stu1', '2023001', 'Computer Science', 2023),
('stu2', '2023002', 'Computer Science', 2023);

INSERT INTO instructors (user_id, department) VALUES
('inst1', 'Computer Science');

INSERT INTO courses (course_id, code, title, credits) VALUES
('CS101', 'CS101', 'Introduction to Programming', 4),
('CS201', 'CS201', 'Data Structures', 4);

INSERT INTO sections (section_id, course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
('SEC001', 'CS101', 'inst1', 'Mon Wed 10:00-11:30', 'Room 101', 30, 'Fall', 2024),
('SEC002', 'CS201', 'inst1', 'Tue Thu 14:00-15:30', 'Room 102', 25, 'Fall', 2024);