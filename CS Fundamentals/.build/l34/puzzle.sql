SELECT a.name, count(b.id) AS recent_books
FROM authors a LEFT JOIN books b ON b.author_id = a.id
WHERE b.year > 2000
GROUP BY a.id ORDER BY a.id;
SELECT a.name, count(b.id) AS recent_books
FROM authors a LEFT JOIN books b ON b.author_id = a.id AND b.year > 2000
GROUP BY a.id ORDER BY a.id;
