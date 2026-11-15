-- books from library.sql. No indexes yet, apart from the rowid.
CREATE TABLE books (
  id          INTEGER PRIMARY KEY,
  title       TEXT NOT NULL,
  author_id   INTEGER,
  year        INTEGER,
  price_cents INTEGER NOT NULL
);
INSERT INTO books (id, title, author_id, year, price_cents) VALUES
  (1, 'A Wizard of Earthsea', 1, 1968, 1099), (2, 'The Left Hand of Darkness', 1, 1969, 1299),
  (3, 'The Dispossessed', 1, 1974, 1399),     (4, 'Kindred', 2, 1979, 1199),
  (5, 'Parable of the Sower', 2, 1993, 1499), (6, 'Guards! Guards!', 3, 1989, 999),
  (7, 'Small Gods', 3, 1992, 999),            (8, 'Stories of Your Life', 4, 2002, 1599),
  (9, 'Exhalation', 4, 2019, 1799),           (10, 'The Fifth Season', 5, 2015, 1699),
  (11, 'Frankenstein', 6, 1818, 599),         (12, 'The Last Man', 6, 1826, 799);
