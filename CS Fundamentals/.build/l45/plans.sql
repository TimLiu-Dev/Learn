.headers on
.mode box
EXPLAIN QUERY PLAN SELECT id, book_id, member_id, loaned, returned FROM loans WHERE book_id = 7 AND returned IS NULL;
EXPLAIN QUERY PLAN SELECT id, book_id, member_id, loaned, returned FROM loans WHERE member_id = 3 ORDER BY id;
EXPLAIN QUERY PLAN SELECT b.id, b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id WHERE a.name = 'Frank Herbert' ORDER BY b.id LIMIT 10 OFFSET 0;
