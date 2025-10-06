
CREATE DATABASE IF NOT EXISTS sms_db;
USE sms_db;

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    enrollment_date DATE NOT NULL
);

INSERT INTO students (first_name, last_name, email, phone, enrollment_date) VALUES
('Riya', 'Makode', 'riyamakode@pccoepune.org', '9182828282', '2023-09-01'),
('Rohan', 'Sangle', 'rohansangle@pccoepune.org', '8392929922', '2023-09-05'),
('Sakshi', 'Patel', 'sakshipatel@pccoepune.org', '7937272828', '2024-01-16');

SELECT * FROM students;
