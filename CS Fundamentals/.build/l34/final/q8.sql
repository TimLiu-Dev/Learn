-- 8. Page 2 of the book list, 5 per page, with author names
SELECT b.id, b.title, a.name AS author
FROM books b LEFT JOIN authors a ON a.id = b.author_id
ORDER BY b.title, b.id
LIMIT 5 OFFSET 5;
