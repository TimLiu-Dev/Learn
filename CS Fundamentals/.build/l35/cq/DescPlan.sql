CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT, author_id INTEGER, year INTEGER);
CREATE INDEX idx_books_author_year ON books(author_id, year);
EXPLAIN QUERY PLAN
SELECT title FROM books WHERE author_id = 3 ORDER BY year DESC;
