SELECT title, price_cents FROM books
WHERE price_cents > (SELECT avg(price_cents) FROM books) AND author_id = 4;
SELECT name FROM members
WHERE id IN (SELECT member_id FROM loans WHERE book_id IN (SELECT id FROM books WHERE author_id = 5));
SELECT name FROM members m
WHERE NOT EXISTS (SELECT 1 FROM loans l WHERE l.member_id = m.id);
WITH pratchett_loans AS (
  SELECT l.member_id FROM loans l JOIN books b ON b.id = l.book_id WHERE b.author_id = 5
)
SELECT m.name, count(*) AS n FROM pratchett_loans p JOIN members m ON m.id = p.member_id
GROUP BY m.id ORDER BY n DESC, m.name;
