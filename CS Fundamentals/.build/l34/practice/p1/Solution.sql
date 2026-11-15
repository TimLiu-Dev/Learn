CREATE TABLE reviews (
  id      INTEGER PRIMARY KEY,
  book_id INTEGER NOT NULL REFERENCES books(id) ON DELETE CASCADE,
  stars   INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5),  -- CHECK alone lets NULL through
  body    TEXT CHECK (body <> '')                          -- NULL passes: the body is optional
);
