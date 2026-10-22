SELECT count(*), count(year), count(DISTINCT author_id) FROM books;
SELECT sum(price_cents), avg(price_cents), min(year), max(year) FROM books;
SELECT 7 / 2 AS int_div, 7 / 2.0 AS real_div;
