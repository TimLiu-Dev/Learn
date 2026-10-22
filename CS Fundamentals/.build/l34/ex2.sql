SELECT title, year, price_cents FROM books
WHERE year BETWEEN 1980 AND 1999 AND price_cents < 1300
ORDER BY price_cents, title;
SELECT name, email FROM members WHERE joined LIKE '2025-%' ORDER BY joined;
SELECT book_id, member_id, loaned FROM loans
WHERE returned IS NULL
ORDER BY loaned
LIMIT 3 OFFSET 3;
