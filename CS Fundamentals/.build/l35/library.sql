-- library.sql: the Phase 10 sample database (small seed).
-- Run: /usr/bin/sqlite3 library.db < library.sql
PRAGMA foreign_keys = ON;

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

INSERT INTO authors (id, name, born) VALUES
  (1, 'Ursula K. Le Guin', 1929),
  (2, 'Octavia E. Butler', 1947),
  (3, 'Terry Pratchett',   1948),
  (4, 'Ted Chiang',        1967),
  (5, 'N. K. Jemisin',     1972),
  (6, 'Mary Shelley',      1797);

INSERT INTO books (id, title, author_id, year, price_cents) VALUES
  (1,  'A Wizard of Earthsea',          1, 1968, 1099),
  (2,  'The Left Hand of Darkness',     1, 1969, 1299),
  (3,  'The Dispossessed',              1, 1974, 1399),
  (4,  'Kindred',                       2, 1979, 1199),
  (5,  'Parable of the Sower',          2, 1993, 1499),
  (6,  'Guards! Guards!',               3, 1989,  999),
  (7,  'Small Gods',                    3, 1992,  999),
  (8,  'Stories of Your Life',          4, 2002, 1599),
  (9,  'Exhalation',                    4, 2019, 1799),
  (10, 'The Fifth Season',              5, 2015, 1699),
  (11, 'Frankenstein',                  6, 1818,  599),
  (12, 'The Last Man',                  6, 1826,  799);

INSERT INTO members (id, name, email, joined) VALUES
  (1, 'Ada',   'ada@example.org',   '2024-01-15'),
  (2, 'Grace', 'grace@example.org', '2024-03-02'),
  (3, 'Linus', 'linus@example.org', '2025-06-20'),
  (4, 'Hedy',  'hedy@example.org',  '2025-09-01'),
  (5, 'Alan',  'alan@example.org',  '2026-02-11');

INSERT INTO loans (id, book_id, member_id, loaned, returned) VALUES
  (1,  1,  1, '2026-09-01', '2026-09-15'),
  (2,  4,  1, '2026-09-16', NULL),
  (3,  6,  2, '2026-08-20', '2026-09-03'),
  (4,  7,  2, '2026-09-04', '2026-09-20'),
  (5,  8,  2, '2026-09-21', NULL),
  (6,  2,  3, '2026-07-01', '2026-07-30'),
  (7,  10, 3, '2026-08-01', '2026-08-19'),
  (8,  9,  3, '2026-09-30', NULL),
  (9,  11, 4, '2026-09-10', NULL),
  (10, 1,  2, '2026-10-01', NULL);
