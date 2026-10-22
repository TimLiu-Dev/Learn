SELECT title, year FROM books ORDER BY year LIMIT 3;
SELECT title, year FROM books ORDER BY year NULLS LAST LIMIT 3;
SELECT author_id, title, year FROM books WHERE author_id IN (1, 2) ORDER BY author_id, year DESC;
