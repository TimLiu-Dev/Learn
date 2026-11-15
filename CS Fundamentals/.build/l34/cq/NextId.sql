CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL);
INSERT INTO books (title) VALUES ('Mort'), ('Dawn');
INSERT INTO books (id, title) VALUES (10, 'Kindred');
INSERT INTO books (title) VALUES ('Exhalation');
SELECT id FROM books WHERE title = 'Exhalation';
