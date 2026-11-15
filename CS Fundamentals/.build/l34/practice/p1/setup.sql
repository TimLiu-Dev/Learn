-- A few rows of the library's books table (the full library.sql has 20).
PRAGMA foreign_keys = ON;

CREATE TABLE books (
  id          INTEGER PRIMARY KEY,
  title       TEXT NOT NULL,
  year        INTEGER,
  price_cents INTEGER NOT NULL
);

INSERT INTO books (id, title, year, price_cents) VALUES
  (1, 'A Wizard of Earthsea', 1968, 1299),
  (2, 'Kindred',              1979, 1399),
  (3, 'Exhalation',           2019, 1899);
