SELECT a.name, count(b.id) AS books
FROM authors a LEFT JOIN books b ON b.author_id = a.id
GROUP BY a.id ORDER BY books DESC, a.name;
SELECT a.name, round(avg(b.price_cents) / 100.0, 2) AS avg_price
FROM authors a JOIN books b ON b.author_id = a.id
GROUP BY a.id HAVING avg(b.price_cents) > 1500
ORDER BY avg_price DESC;
SELECT m.name, count(DISTINCT b.author_id) AS authors_read
FROM members m
JOIN loans l ON l.member_id = m.id
JOIN books b ON b.id = l.book_id
GROUP BY m.id ORDER BY authors_read DESC, m.name;
