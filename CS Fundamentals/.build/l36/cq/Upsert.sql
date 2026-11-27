CREATE TABLE stock (isbn TEXT PRIMARY KEY, copies INTEGER NOT NULL);
INSERT INTO stock VALUES ('A', 3);
INSERT INTO stock VALUES ('A', 5) ON CONFLICT (isbn) DO UPDATE SET copies = copies + excluded.copies;
INSERT INTO stock VALUES ('B', 2) ON CONFLICT (isbn) DO NOTHING;
INSERT INTO stock VALUES ('B', 4) ON CONFLICT (isbn) DO NOTHING;
SELECT group_concat(isbn || '=' || copies, ' ') FROM (SELECT * FROM stock ORDER BY isbn);
