-- 5. Average price by decade (books with no year form their own group)
SELECT (year / 10) * 10 AS decade,
       count(*) AS books,
       round(avg(price_cents) / 100.0, 2) AS avg_price
FROM books
GROUP BY decade
ORDER BY decade NULLS LAST;
