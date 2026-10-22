-- 3. Overdue: not returned and loaned more than 21 days before "today"
WITH today AS (SELECT '2026-10-01' AS d)
SELECT m.name, b.title, l.loaned,
       CAST(julianday(today.d) - julianday(l.loaned) AS INTEGER) AS days_out
FROM loans l
JOIN books b ON b.id = l.book_id
JOIN members m ON m.id = l.member_id
CROSS JOIN today
WHERE l.returned IS NULL AND l.loaned < date(today.d, '-21 days')
ORDER BY days_out DESC;
