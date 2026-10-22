SELECT rowid, id, title FROM books LIMIT 2;
INSERT INTO books (title, author_id, year, price_cents) VALUES ('Piranesi', NULL, 2020, 1399);
SELECT id, title FROM books WHERE title = 'Piranesi';
