SELECT author_id, title, max(price_cents) FROM books GROUP BY author_id HAVING author_id <= 3;
SELECT author_id, title, count(*) FROM books GROUP BY author_id HAVING author_id <= 3;
