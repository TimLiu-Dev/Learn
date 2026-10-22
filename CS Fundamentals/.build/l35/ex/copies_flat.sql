-- copies_flat.sql: the exercise table. Each physical copy of a book on a shelf.
CREATE TABLE copies_flat (
  barcode      TEXT PRIMARY KEY,
  book_title   TEXT NOT NULL,
  author_name  TEXT NOT NULL,
  shelf_code   TEXT NOT NULL,
  shelf_floor  INTEGER NOT NULL,
  shelf_topic  TEXT NOT NULL,
  condition    TEXT NOT NULL
);
INSERT INTO copies_flat VALUES
  ('C-0001', 'Kindred',          'Octavia E. Butler', 'SF-2', 2, 'Science fiction', 'good'),
  ('C-0002', 'Kindred',          'Octavia E. Butler', 'SF-2', 2, 'Science fiction', 'worn'),
  ('C-0003', 'Small Gods',       'Terry Pratchett',   'FA-1', 1, 'Fantasy',         'good'),
  ('C-0004', 'Exhalation',       'Ted Chiang',        'SF-2', 2, 'Science fiction', 'new'),
  ('C-0005', 'The Fifth Season', 'N. K. Jemisin',     'FA-1', 1, 'Fantasy',         'good');
