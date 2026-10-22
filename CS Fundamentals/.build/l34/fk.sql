PRAGMA foreign_keys;
INSERT INTO loans (book_id, member_id, loaned) VALUES (999, 1, '2026-10-01');
PRAGMA foreign_key_check;
PRAGMA foreign_keys = ON;
INSERT INTO loans (book_id, member_id, loaned) VALUES (998, 1, '2026-10-01');
DELETE FROM authors WHERE id = 5;
