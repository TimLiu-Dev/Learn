-- The course's sample library database (L34), copied for L36 with an extra
-- accounts table for the transfer examples.
-- Run:  /usr/bin/sqlite3 library.db < library.sql
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS accounts;

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
  id     INTEGER PRIMARY KEY,
  name   TEXT NOT NULL,
  email  TEXT UNIQUE NOT NULL,
  joined TEXT
);

CREATE TABLE loans (
  id        INTEGER PRIMARY KEY,
  book_id   INTEGER NOT NULL REFERENCES books(id),
  member_id INTEGER NOT NULL REFERENCES members(id),
  loaned    TEXT NOT NULL,
  returned  TEXT
);

INSERT INTO authors (id, name, born) VALUES
  (1, 'Ursula K. Le Guin', 1929),
  (2, 'Octavia E. Butler', 1947),
  (3, 'Ted Chiang',        1967),
  (4, 'N. K. Jemisin',     1972),
  (5, 'Terry Pratchett',   1948),
  (6, 'Liu Cixin',         1963),
  (7, 'Anonymous',         NULL),
  (8, 'Becky Chambers',    1985);   -- no books in the library yet

INSERT INTO books (id, title, author_id, year, price_cents) VALUES
  (1,  'A Wizard of Earthsea',            1, 1968, 1299),
  (2,  'The Left Hand of Darkness',       1, 1969, 1499),
  (3,  'The Dispossessed',                1, 1974, 1599),
  (4,  'Kindred',                         2, 1979, 1399),
  (5,  'Parable of the Sower',            2, 1993, 1499),
  (6,  'Dawn',                            2, 1987, 1199),
  (7,  'Stories of Your Life and Others', 3, 2002, 1699),
  (8,  'Exhalation',                      3, 2019, 1899),
  (9,  'The Fifth Season',                4, 2015, 1599),
  (10, 'The Obelisk Gate',                4, 2016, 1599),
  (11, 'The Stone Sky',                   4, 2017, 1699),
  (12, 'Guards! Guards!',                 5, 1989,  999),
  (13, 'Small Gods',                      5, 1992,  999),
  (14, 'Going Postal',                    5, 2004, 1099),
  (15, 'Mort',                            5, 1987,  899),
  (16, 'The Three-Body Problem',          6, 2008, 1499),
  (17, 'The Dark Forest',                 6, 2008, 1599),
  (18, 'Death''s End',                    6, 2010, 1699),
  (19, 'Beowulf',                         7, NULL,  799),   -- year unknown
  (20, 'Sir Gawain and the Green Knight', 7, 1400,  899);

INSERT INTO members (id, name, email, joined) VALUES
  (1, 'Ada Lovelace',      'ada@example.com',      '2024-01-15'),
  (2, 'Alan Turing',       'alan@example.com',     '2024-03-02'),
  (3, 'Grace Hopper',      'grace@example.com',    '2024-03-20'),
  (4, 'Edsger Dijkstra',   'edsger@example.com',   '2024-07-08'),
  (5, 'Barbara Liskov',    'barbara@example.com',  '2025-01-11'),
  (6, 'Donald Knuth',      'donald@example.com',   '2025-01-30'),
  (7, 'Margaret Hamilton', 'margaret@example.com', '2025-06-05'),
  (8, 'Ken Thompson',      'ken@example.com',      '2026-02-14');  -- never borrowed

INSERT INTO loans (id, book_id, member_id, loaned, returned) VALUES
  (1,  12, 1, '2025-02-01', '2025-02-15'),
  (2,   7, 1, '2025-02-20', '2025-03-05'),
  (3,   8, 1, '2025-03-10', '2025-03-30'),
  (4,   1, 2, '2025-03-12', '2025-04-01'),
  (5,  12, 2, '2025-04-05', '2025-04-19'),
  (6,   4, 3, '2025-04-10', '2025-04-24'),
  (7,   7, 3, '2025-05-01', '2025-05-14'),
  (8,   8, 3, '2025-05-20', '2025-06-10'),
  (9,  13, 4, '2025-06-01', '2025-06-20'),
  (10, 16, 4, '2025-06-15', '2025-07-01'),
  (11, 12, 5, '2025-07-02', '2025-07-16'),
  (12, 14, 5, '2025-07-20', '2025-08-05'),
  (13,  9, 6, '2025-08-01', '2025-08-25'),
  (14, 15, 6, '2025-08-10', '2025-08-30'),
  (15,  2, 7, '2025-09-01', '2025-09-20'),
  (16,  5, 7, '2025-09-15', '2025-10-01'),
  (17, 13, 1, '2025-10-03', '2025-10-17'),
  (18,  7, 2, '2025-11-01', '2025-11-15'),
  (19, 11, 3, '2025-11-20', '2025-12-10'),
  (20, 17, 4, '2026-01-05', '2026-01-25'),
  (21,  6, 5, '2026-02-01', '2026-02-20'),
  (22, 15, 1, '2026-03-01', '2026-03-15'),
  (23, 12, 6, '2026-04-02', '2026-04-20'),
  (24, 14, 2, '2026-05-10', '2026-05-24'),
  (25,  1, 3, '2026-06-01', '2026-06-15'),
  (26,  4, 4, '2026-07-01', NULL),
  (27, 16, 5, '2026-08-15', NULL),
  (28, 13, 6, '2026-09-20', NULL),
  (29,  9, 7, '2026-09-25', NULL),
  (30, 12, 1, '2026-09-01', NULL);

-- L36: money moves between these two rows. The CHECK makes overdrafts fail.
CREATE TABLE accounts (
  id            INTEGER PRIMARY KEY,
  owner         TEXT,
  balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0)
);

INSERT INTO accounts (id, owner, balance_cents) VALUES
  (1, 'Ada', 10000),
  (2, 'Bob',  5000);
