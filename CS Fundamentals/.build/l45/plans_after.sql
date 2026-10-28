EXPLAIN QUERY PLAN SELECT b.id, b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id WHERE a.name = 'Frank Herbert' ORDER BY b.id LIMIT 10 OFFSET 0;
