CREATE TABLE authors (id INTEGER PRIMARY KEY, name TEXT);
CREATE TABLE books (id INTEGER PRIMARY KEY, author_id INTEGER);
INSERT INTO authors VALUES (1, 'Chiang'), (2, 'Chambers');
INSERT INTO books VALUES (1, 1), (2, 1);
SELECT a.name, count(*), count(b.id)
FROM authors a LEFT JOIN books b ON b.author_id = a.id
WHERE a.id = 2
GROUP BY a.id;
