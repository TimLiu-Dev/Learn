-- The same six books twice: as relational tables, and as JSON documents.
CREATE TABLE authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL);
CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, year INTEGER NOT NULL);
CREATE TABLE book_authors (book_id INTEGER REFERENCES books(id), author_id INTEGER REFERENCES authors(id));

INSERT INTO authors VALUES (1, 'Ursula K. Le Guin'), (2, 'Terry Pratchett'), (3, 'Neil Gaiman'), (4, 'Octavia E. Butler');
INSERT INTO books VALUES
  (1, 'The Left Hand of Darkness', 1969), (2, 'The Dispossessed', 1974), (3, 'Good Omens', 1990),
  (4, 'Kindred', 1979), (5, 'Equal Rites', 1987), (6, 'Parable of the Sower', 1993);
INSERT INTO book_authors VALUES (1, 1), (2, 1), (3, 2), (3, 3), (4, 4), (5, 2), (6, 4);

-- Document model: each book carries its authors. Nothing checked these on the way in.
CREATE TABLE book_docs (id INTEGER PRIMARY KEY, doc TEXT NOT NULL);
INSERT INTO book_docs (doc) VALUES
  ('{"title": "The Left Hand of Darkness", "year": "1969", "authors": ["Ursula K. Le Guin"]}'),
  ('{"title": "The Dispossessed", "year": 1974, "authors": ["Ursula K. Le Guin"]}'),
  ('{"title": "Good Omens", "year": 1990, "authors": ["Terry Pratchett", "Neil Gaiman"]}'),
  ('{"title": "Kindred", "year": 1979, "authors": ["Octavia E. Butler"]}'),
  ('{"title": "Equal Rites", "year": 1987, "authors": ["Terry Pratchett"]}'),
  ('{"title": "Parable of the Sower", "year": 1993, "authors": ["Octavia E. Butler"]}');
