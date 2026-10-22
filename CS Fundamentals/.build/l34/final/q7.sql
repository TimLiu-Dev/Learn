-- 7. Top 3 books per author by number of loans
WITH counts AS (
  SELECT a.name AS author, b.title, count(l.id) AS loans
  FROM books b
  JOIN authors a     ON a.id = b.author_id
  LEFT JOIN loans l  ON l.book_id = b.id
  GROUP BY b.id
), ranked AS (
  SELECT author, title, loans,
         ROW_NUMBER() OVER (PARTITION BY author ORDER BY loans DESC, title) AS rank
  FROM counts
)
SELECT author, rank, title, loans FROM ranked
WHERE rank <= 3 AND author IN ('Terry Pratchett', 'Octavia E. Butler')
ORDER BY author, rank;
