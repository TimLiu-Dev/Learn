-- flat.sql: one wide table holding everything about a loan.
-- This is the "spreadsheet" design that normalisation fixes.
CREATE TABLE loans_flat (
  loan_id       INTEGER PRIMARY KEY,
  loaned        TEXT NOT NULL,
  returned      TEXT,
  member_name   TEXT NOT NULL,
  member_email  TEXT NOT NULL,
  book_title    TEXT NOT NULL,
  book_year     INTEGER,
  genres        TEXT,            -- comma-separated: breaks 1NF
  author_name   TEXT NOT NULL,
  author_born   INTEGER
);

INSERT INTO loans_flat VALUES
  (1,  '2026-09-01', '2026-09-15', 'Ada',   'ada@example.org',   'A Wizard of Earthsea',      1968, 'Fantasy',             'Ursula K. Le Guin', 1929),
  (2,  '2026-09-16', NULL,         'Ada',   'ada@example.org',   'Kindred',                   1979, 'SF,Time travel',      'Octavia E. Butler', 1947),
  (3,  '2026-08-20', '2026-09-03', 'Grace', 'grace@example.org', 'Guards! Guards!',           1989, 'Fantasy,Comic',       'Terry Pratchett',   1948),
  (4,  '2026-09-04', '2026-09-20', 'Grace', 'grace@example.org', 'Small Gods',                1992, 'Fantasy,Comic',       'Terry Pratchett',   1948),
  (5,  '2026-09-21', NULL,         'Grace', 'grace@example.org', 'Stories of Your Life',      2002, 'SF,Short stories',    'Ted Chiang',        1967),
  (6,  '2026-07-01', '2026-07-30', 'Linus', 'linus@example.org', 'The Left Hand of Darkness', 1969, 'SF',                  'Ursula K. Le Guin', 1929),
  (7,  '2026-08-01', '2026-08-19', 'Linus', 'linus@example.org', 'The Fifth Season',          2015, 'Fantasy,Dark Fantasy','N. K. Jemisin',     1972),
  (8,  '2026-09-30', NULL,         'Linus', 'linus@example.org', 'Exhalation',                2019, 'SF,Short stories',    'Ted Chiang',        1967),
  (9,  '2026-09-10', NULL,         'Hedy',  'hedy@example.org',  'Frankenstein',              1818, 'Horror,Proto-SF',     'Mary Shelley',      1797),
  (10, '2026-10-01', NULL,         'Grace', 'grace@example.org', 'A Wizard of Earthsea',      1968, 'Fantasy',             'Ursula K. Le Guin', 1929);
