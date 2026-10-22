PRAGMA foreign_keys = ON;
CREATE TABLE reviews (
  id      INTEGER PRIMARY KEY,
  book_id INTEGER NOT NULL REFERENCES books(id) ON DELETE CASCADE,
  stars   INTEGER CHECK (stars BETWEEN 1 AND 5)
);
INSERT INTO reviews (book_id, stars) VALUES (19, 5), (19, 4), (20, 3);
DELETE FROM books WHERE id = 19;
SELECT * FROM reviews;
