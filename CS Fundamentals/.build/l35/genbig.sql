-- genbig.sql: the same schema, filled with 272,000 deterministic rows.
-- Run: /usr/bin/sqlite3 big.db < genbig.sql
-- No randomness: every value is arithmetic on the row number, so every
-- run (and every learner) gets exactly the same database.
-- Careful: || binds tighter than % in SQLite, hence the extra parentheses.
PRAGMA foreign_keys = ON;
BEGIN;
CREATE TABLE authors (
  id    INTEGER PRIMARY KEY,
  name  TEXT NOT NULL,
  born  INTEGER
);

CREATE TABLE books (
  id          INTEGER PRIMARY KEY,
  title       TEXT NOT NULL,
  author_id   INTEGER REFERENCES authors(id),
  year        INTEGER CHECK (year BETWEEN 1400 AND 2100),
  price_cents INTEGER NOT NULL
);

CREATE TABLE members (
  id      INTEGER PRIMARY KEY,
  name    TEXT NOT NULL,
  email   TEXT UNIQUE NOT NULL,
  joined  TEXT
);

CREATE TABLE loans (
  id         INTEGER PRIMARY KEY,
  book_id    INTEGER NOT NULL REFERENCES books(id),
  member_id  INTEGER NOT NULL REFERENCES members(id),
  loaned     TEXT NOT NULL,
  returned   TEXT
);

WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
INSERT INTO authors (id, name, born)
SELECT i, 'Author ' || i, 1900 + (i * 37) % 100 FROM n;

WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 50000)
INSERT INTO books (id, title, author_id, year, price_cents)
SELECT i, 'Book ' || i, (i * 7919) % 2000 + 1, 1950 + (i * 31) % 76, 500 + (i * 97) % 2500 FROM n;

WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 20000)
INSERT INTO members (id, name, email, joined)
SELECT i, 'Member ' || i, 'm' || i || '@example.org',
       date('2020-01-01', '+' || ((i * 13) % 2000) || ' days') FROM n;

WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 200000)
INSERT INTO loans (id, book_id, member_id, loaned, returned)
SELECT i, (i * 104729) % 50000 + 1, (i * 7907) % 20000 + 1,
       date('2024-01-01', '+' || ((i * 17) % 1000) || ' days'),
       CASE WHEN i % 10 < 8
            THEN date('2024-01-01', '+' || ((i * 17) % 1000 + i % 30 + 1) || ' days')
       END
FROM n;
COMMIT;
