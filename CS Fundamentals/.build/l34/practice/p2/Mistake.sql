SELECT title, year, price_cents
FROM books
WHERE price_cents < 1300
  AND (year = NULL OR year < 1980)
ORDER BY year NULLS FIRST, title
LIMIT 3 OFFSET 3;
