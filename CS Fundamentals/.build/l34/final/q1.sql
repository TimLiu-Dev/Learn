-- 1. Books never loaned
SELECT b.id, b.title
FROM books b
WHERE NOT EXISTS (SELECT 1 FROM loans l WHERE l.book_id = b.id)
ORDER BY b.id;
