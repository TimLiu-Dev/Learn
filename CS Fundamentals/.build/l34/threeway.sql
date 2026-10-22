SELECT m.name, b.title, l.loaned
FROM loans l
JOIN books b   ON b.id = l.book_id
JOIN members m ON m.id = l.member_id
WHERE l.returned IS NULL
ORDER BY l.loaned;
