-- 4. The most-borrowed author
SELECT a.name, count(*) AS loans
FROM loans l
JOIN books b   ON b.id = l.book_id
JOIN authors a ON a.id = b.author_id
GROUP BY a.id
ORDER BY loans DESC
LIMIT 1;
