-- Auth DB (PDF Page 1,6 - UNIX shadow style)
CREATE DATABASE IF NOT EXISTS university_auth;
USE university_auth;

CREATE TABLE users_auth (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('student', 'instructor', 'admin') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status ENUM('active', 'locked') DEFAULT 'active',
    last_login TIMESTAMP NULL,
    failed_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample data (PDF Page 8 - testing accounts)
INSERT INTO users_auth (user_id, username, role, password_hash) VALUES
('admin1', 'admin1', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.MM8VuoG2XoRRC0iYpC6p7p7p7p7p'),
('stu1', 'stu1', 'student', '$2a$10$N9qo8uLOickgx2ZMRZoMye.MM8VuoG2XoRRC0iYpC6p7p7p7p'),
('stu2', 'stu2', 'student', '$2a$10$N9qo8uLOickgx2ZMRZoMye.MM8VuoG2XoRRC0iYpC6p7p7p7p'),
('inst1', 'inst1', 'instructor', '$2a$10$N9qo8uLOickgx2ZMRZoMye.MM8VuoG2XoRRC0iYpC6p7p7p7p');