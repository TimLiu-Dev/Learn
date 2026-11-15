-- The query (the check runs EXPLAIN QUERY PLAN on it after your statements):
--   SELECT year, title FROM books WHERE author_id = 2 ORDER BY year;
-- With no index: SCAN books, then USE TEMP B-TREE FOR ORDER BY.
-- This index gets you halfway. Change its columns.
CREATE INDEX idx_books_author_year_title ON books(author_id);
