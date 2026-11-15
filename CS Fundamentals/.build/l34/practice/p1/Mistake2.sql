CREATE TABLE reviews (
  id      INTEGER PRIMARY KEY,
  book_id INTEGER NOT NULL REFERENCES books(id),
  stars   INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5),
  body    TEXT CHECK (body <> '')
);
