INSERT OR IGNORE INTO reviews (book_id, stars, body) VALUES
  (1, 5,    'A classic'),
  (1, 0,    'Zero stars is not a rating'),
  (1, 6,    'Neither is six'),
  (2, NULL, 'Forgot the stars'),
  (2, 4,    NULL),
  (2, 3,    ''),
  (3, 2,    'Soon to be deleted');
DELETE FROM books WHERE id = 3;
SELECT book_id, stars, body FROM reviews ORDER BY id;
