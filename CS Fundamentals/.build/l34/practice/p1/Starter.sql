-- Add the constraints. Every column is unchecked for now.
CREATE TABLE reviews (
  id      INTEGER PRIMARY KEY,
  book_id INTEGER,
  stars   INTEGER,
  body    TEXT
);
