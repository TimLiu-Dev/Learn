SELECT title, year, price_cents
FROM books
WHERE year IS NULL OR year < 1980 AND price_cents < 1300
ORDER BY year NULLS FIRST, title
LIMIT 3 OFFSET 3;
