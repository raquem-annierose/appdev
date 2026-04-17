-- ============================================================
--  SQL REFERENCE SCRIPT
--  Database: appdev_db_try
--  Tables used: student, user, activity
-- ============================================================

-- ============================================================
--  SETUP (run this first)
-- ============================================================

CREATE DATABASE IF NOT EXISTS appdev_db_try;
USE appdev_db_try;

-- ============================================================
--  1. CREATE TABLE
--  Used to define a new table and its columns
-- ============================================================

CREATE TABLE student (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(100)  NOT NULL,
    last_name     VARCHAR(100)  NOT NULL,
    midterm_grade FLOAT         DEFAULT 0,
    final_grade   FLOAT         DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE user (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    age        INT          DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE activity (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- ============================================================
--  2. INSERT
--  Used to add new rows to a table
-- ============================================================

INSERT INTO student (first_name, last_name, midterm_grade, final_grade) VALUES
    ('Annie',  'Raquem', 85, 90),
    ('Rose',   'Raquem', 95, 97),
    ('Yowro',  'Raquem', 90, 85),
    ('Ann',    'Raquem', 72, 68),
    ('Niera',  'Raquem', 55, 60);

INSERT INTO user (first_name, last_name, age) VALUES
    ('Annie', 'Raquem', 20),
    ('Rose',  'Raquem', 22),
    ('Yowro', 'Raquem', 19);

INSERT INTO activity (name, description) VALUES
    ('Lab Exercise 1', 'Intro to Spring Boot'),
    ('Lab Exercise 2', 'REST API basics'),
    ('Midterm Project', 'CRUD application');

-- ============================================================
--  3. SELECT
--  Used to read/query data from a table
-- ============================================================

-- Get all students
SELECT * FROM student;

-- Get specific columns only
SELECT first_name, last_name, midterm_grade, final_grade FROM student;

-- With a condition (WHERE)
SELECT * FROM student WHERE final_grade >= 75;

-- Sort results (ORDER BY)
SELECT * FROM student ORDER BY last_name ASC, first_name ASC;

-- Limit number of results
SELECT * FROM student LIMIT 3;

-- ============================================================
--  4. UPDATE
--  Used to change existing data in a table
--  ALWAYS use WHERE — without it, ALL rows get updated!
-- ============================================================

-- Update one student's grade
UPDATE student SET final_grade = 88 WHERE id = 1;

-- Update multiple columns at once
UPDATE student SET midterm_grade = 90, final_grade = 92 WHERE first_name = 'Rose';

-- ============================================================
--  5. DELETE
--  Used to remove rows from a table
--  ALWAYS use WHERE — without it, ALL rows get deleted!
-- ============================================================

-- Delete a specific student
DELETE FROM student WHERE id = 5;

-- Delete students who failed both exams
DELETE FROM student WHERE midterm_grade < 75 AND final_grade < 75;

-- ============================================================
--  6. ALTER TABLE
--  Used to modify an existing table structure
-- ============================================================

-- Add a new column
ALTER TABLE student ADD COLUMN section VARCHAR(50);

-- Add with a default value
ALTER TABLE student ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Rename a column
ALTER TABLE student RENAME COLUMN section TO block_section;

-- Change a column's data type
ALTER TABLE student MODIFY COLUMN block_section VARCHAR(100);

-- Remove a column
ALTER TABLE student DROP COLUMN is_active;

-- ============================================================
--  7. AGGREGATION FUNCTIONS
--  Used to compute a single result from multiple rows
--  Common: COUNT, SUM, AVG, MIN, MAX
-- ============================================================

-- COUNT — how many students are there?
SELECT COUNT(*) AS total_students FROM student;

-- AVG — average final grade
SELECT AVG(final_grade) AS average_final FROM student;

-- MAX / MIN — highest and lowest midterm grade
SELECT MAX(midterm_grade) AS highest, MIN(midterm_grade) AS lowest FROM student;

-- SUM — total of all final grades
SELECT SUM(final_grade) AS total_final_grades FROM student;

-- Computed column (average of two columns per row)
SELECT
    first_name,
    last_name,
    (midterm_grade + final_grade) / 2 AS average,
    CASE
        WHEN (midterm_grade + final_grade) / 2 >= 75 THEN 'Pass'
        ELSE 'Failed'
    END AS result
FROM student;

-- ============================================================
--  8. GROUP BY
--  Used with aggregation to group results by a column
--  Example: count students per last name (like per family)
-- ============================================================

-- How many students share the same last name?
SELECT last_name, COUNT(*) AS count FROM student GROUP BY last_name;

-- Average grade per last name group
SELECT last_name, AVG((midterm_grade + final_grade) / 2) AS avg_grade
FROM student
GROUP BY last_name;

-- ============================================================
--  9. HAVING
--  Like WHERE but used AFTER GROUP BY to filter grouped results
-- ============================================================

-- Show last name groups where average grade is above 80
SELECT last_name, AVG((midterm_grade + final_grade) / 2) AS avg_grade
FROM student
GROUP BY last_name
HAVING avg_grade > 80;

-- ============================================================
--  10. JOIN
--  Used to combine rows from two or more tables
-- ============================================================

-- Add a foreign key to link activity to a student
ALTER TABLE activity ADD COLUMN student_id BIGINT;
ALTER TABLE activity ADD CONSTRAINT fk_activity_student
    FOREIGN KEY (student_id) REFERENCES student(id);

UPDATE activity SET student_id = 1 WHERE id = 1;
UPDATE activity SET student_id = 2 WHERE id = 2;
UPDATE activity SET student_id = 1 WHERE id = 3;

-- INNER JOIN — only rows that match on both sides
SELECT
    s.first_name,
    s.last_name,
    a.name        AS activity_name,
    a.created_at
FROM student s
INNER JOIN activity a ON s.id = a.student_id;

-- LEFT JOIN — all students even if they have no activity
SELECT
    s.first_name,
    s.last_name,
    a.name AS activity_name
FROM student s
LEFT JOIN activity a ON s.id = a.student_id;

-- ============================================================
--  11. USEFUL EXTRAS
-- ============================================================

-- Search by partial name (LIKE)
SELECT * FROM student WHERE last_name LIKE '%aque%';

-- Check if value is in a list (IN)
SELECT * FROM student WHERE id IN (1, 2, 3);

-- Count only non-null values
SELECT COUNT(description) AS activities_with_description FROM activity;

-- Show table structure
DESCRIBE student;
DESCRIBE user;
DESCRIBE activity;

-- Drop a table (careful — this deletes everything!)
-- DROP TABLE activity;
