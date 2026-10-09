-- data.sql - runs automatically at startup, AFTER schema.sql.
-- Sample rows. We do not give an id: the database generates it.

INSERT INTO student (name, email, course, marks, enrolled_on) VALUES ('Asha Rao',     'asha@college.edu',  'Java',   92, DATE '2026-06-01');
INSERT INTO student (name, email, course, marks, enrolled_on) VALUES ('Ravi Kumar',   'ravi@college.edu',  'Java',   78, DATE '2026-06-03');
INSERT INTO student (name, email, course, marks, enrolled_on) VALUES ('Meera Shah',   'meera@college.edu', 'Spring', 85, DATE '2026-06-10');
INSERT INTO student (name, email, course, marks, enrolled_on) VALUES ('John Mathew',  'john@college.edu',  'Spring', 67, DATE '2026-07-01');
INSERT INTO student (name, email, course, marks, enrolled_on) VALUES ('Fatima Khan',  'fatima@college.edu','SQL',    88, DATE '2026-07-15');
