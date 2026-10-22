SELECT title, author_id, price_cents FROM books
WHERE author_id = 5 OR author_id = 1 AND price_cents < 1000;
SELECT title, author_id, price_cents FROM books
WHERE (author_id = 5 OR author_id = 1) AND price_cents < 1000;
