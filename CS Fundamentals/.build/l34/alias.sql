SELECT title, price_cents / 100.0 AS price FROM books WHERE price > 17 ORDER BY price DESC;
SELECT title, price_cents / 100.0 AS price, count(*) OVER () AS n FROM books WHERE n > 1;
