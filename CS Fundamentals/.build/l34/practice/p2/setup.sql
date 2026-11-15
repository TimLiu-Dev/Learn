-- Old and anonymous books. year is NULL where the catalogue doesn't know it.
CREATE TABLE books (
  id          INTEGER PRIMARY KEY,
  title       TEXT NOT NULL,
  year        INTEGER,
  price_cents INTEGER NOT NULL
);

INSERT INTO books (id, title, year, price_cents) VALUES
  (1,  'Beowulf',                          NULL,  799),
  (2,  'The Epic of Gilgamesh',            NULL, 1150),
  (3,  'One Thousand and One Nights',      NULL, 1599),
  (4,  'Sir Gawain and the Green Knight',  1400,  899),
  (5,  'Don Quixote',                      1605, 1299),
  (6,  'Frankenstein',                     1818,  999),
  (7,  'Dracula',                          1897, 1099),
  (8,  'A Wizard of Earthsea',             1968, 1299),
  (9,  'Kindred',                          1979, 1399),
  (10, 'Mort',                             1987,  899),
  (11, 'Small Gods',                       1992,  999),
  (12, 'Exhalation',                       2019, 1899);
