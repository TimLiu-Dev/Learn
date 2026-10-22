WITH ranked AS (
  SELECT m.name, b.title, l.loaned,
         ROW_NUMBER() OVER (PARTITION BY l.member_id ORDER BY l.loaned DESC) AS rn
  FROM loans l JOIN members m ON m.id = l.member_id JOIN books b ON b.id = l.book_id
)
SELECT name, title, loaned FROM ranked WHERE rn = 1 ORDER BY loaned DESC;
