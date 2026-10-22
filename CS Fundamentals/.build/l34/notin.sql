SELECT name FROM authors WHERE id NOT IN (SELECT author_id FROM books);
INSERT INTO books (title, author_id, year, price_cents) VALUES ('Piranesi', NULL, 2020, 1399);
SELECT name FROM authors WHERE id NOT IN (SELECT author_id FROM books);
SELECT name FROM authors a WHERE NOT EXISTS (SELECT 1 FROM books b WHERE b.author_id = a.id);
