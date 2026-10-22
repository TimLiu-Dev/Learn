-- composite.sql: an author's books in a year range, and the leftmost prefix.
CREATE INDEX idx_books_author_year ON books(author_id, year);
.print 'author only'
EXPLAIN QUERY PLAN SELECT title FROM books WHERE author_id = 7;
.print 'author and year range'
EXPLAIN QUERY PLAN SELECT title FROM books WHERE author_id = 7 AND year BETWEEN 1990 AND 2000;
.print 'year range only'
EXPLAIN QUERY PLAN SELECT title FROM books WHERE year BETWEEN 1990 AND 2000;
