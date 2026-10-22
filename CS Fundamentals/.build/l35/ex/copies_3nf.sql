-- copies_3nf.sql: one solution. Run after library.sql and copies_flat.sql.
-- barcode -> book_title -> author_name   (transitive: books/authors already exist)
-- barcode -> shelf_code -> floor, topic  (transitive: a new shelves table)
PRAGMA foreign_keys = ON;
.headers on
.mode column
CREATE TABLE shelves (
  code  TEXT PRIMARY KEY,
  floor INTEGER NOT NULL,
  topic TEXT NOT NULL
);
CREATE TABLE copies (
  barcode    TEXT PRIMARY KEY,
  book_id    INTEGER NOT NULL REFERENCES books(id),
  shelf_code TEXT NOT NULL REFERENCES shelves(code),
  condition  TEXT NOT NULL CHECK (condition IN ('new', 'good', 'worn', 'damaged'))
);
INSERT INTO shelves SELECT DISTINCT shelf_code, shelf_floor, shelf_topic FROM copies_flat;
INSERT INTO copies
  SELECT c.barcode, b.id, c.shelf_code, c.condition
  FROM copies_flat c JOIN books b ON b.title = c.book_title;

-- Same five copies, now with one fact per row:
SELECT c.barcode, b.title, a.name AS author, s.code AS shelf, s.floor, c.condition
FROM copies c JOIN books b ON b.id = c.book_id
              JOIN authors a ON a.id = b.author_id
              JOIN shelves s ON s.code = c.shelf_code
ORDER BY c.barcode;
-- Moving shelf SF-2 to floor 3 is now one row:
UPDATE shelves SET floor = 3 WHERE code = 'SF-2';
SELECT changes() AS rows_changed;
