SELECT title, year, price_cents
FROM books
WHERE price_cents < 1300
  AND (year IS NULL OR year < 1980)   -- parentheses: AND binds tighter than OR
ORDER BY year NULLS FIRST, title
LIMIT 3 OFFSET 3;                     -- page 2: skip page 1's three rows
