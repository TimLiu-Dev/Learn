-- members after the expand step and a backfill that ran too early.
CREATE TABLE members (
  id        INTEGER PRIMARY KEY,
  name      TEXT,
  full_name TEXT,
  email     TEXT NOT NULL UNIQUE,
  joined    TEXT
);
CREATE INDEX members_by_joined ON members(joined);
INSERT INTO members VALUES
  (1, 'Ada Lovelace',    'Ada Lovelace',    'ada@example.org',   '2026-01-10'),
  (2, 'Grace Hopper',    'Grace Hopper',    'grace@example.org', '2026-02-03'),
  (3, 'Linus Torvalds',  'Linus Torvalds',  'linus@example.org', '2026-03-21'),
  (4, 'Barbara Liskov',  NULL,              'barbara@example.org', '2026-11-20'),
  (5, 'Edsger Dijkstra', NULL,              'edsger@example.org',  '2026-11-21');
-- What every member's full_name must be at the end.
CREATE TABLE expected (id INTEGER PRIMARY KEY, full_name TEXT);
INSERT INTO expected SELECT id, name FROM members;
