SELECT a.name, count(*) AS loans
FROM loans l JOIN books b ON b.id = l.book_id JOIN authors a ON a.id = b.author_id
GROUP BY a.id
HAVING count(*) >= 4
ORDER BY loans DESC;
SELECT a.name, count(*) AS loans_2026
FROM loans l JOIN books b ON b.id = l.book_id JOIN authors a ON a.id = b.author_id
WHERE l.loaned >= '2026-01-01'
GROUP BY a.id
ORDER BY loans_2026 DESC, a.name;
