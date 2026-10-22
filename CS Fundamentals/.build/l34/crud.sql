INSERT INTO members (name, email, joined) VALUES ('Linus Torvalds', 'linus@example.com', '2026-10-01');
UPDATE members SET email = 'linus@kernel.example' WHERE name = 'Linus Torvalds';
SELECT id, name, email FROM members WHERE id = 9;
DELETE FROM members WHERE id = 9;
SELECT changes();
UPDATE books SET price_cents = 0;
SELECT changes();
SELECT count(*) AS free_books FROM books WHERE price_cents = 0;
