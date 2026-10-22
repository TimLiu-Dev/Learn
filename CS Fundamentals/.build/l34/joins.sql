SELECT count(*) AS pairs FROM authors CROSS JOIN books;
SELECT a.name, b.title
FROM books b JOIN authors a ON a.id = b.author_id
WHERE a.id IN (3, 6)
ORDER BY a.name, b.year;
SELECT count(*) AS inner_rows FROM authors a JOIN books b ON b.author_id = a.id;
SELECT count(*) AS left_rows FROM authors a LEFT JOIN books b ON b.author_id = a.id;
SELECT a.name, b.title FROM authors a LEFT JOIN books b ON b.author_id = a.id WHERE b.id IS NULL;
