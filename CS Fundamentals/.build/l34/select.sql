SELECT title, price_cents / 100.0 AS price FROM books WHERE author_id = 3;
SELECT author_id FROM books WHERE year < 1980;
SELECT DISTINCT author_id FROM books WHERE year < 1980;
