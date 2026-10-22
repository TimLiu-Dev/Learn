-- reservations_flat.sql: holds on books, one wide table. Load it into library.db.
CREATE TABLE reservations_flat (
  reservation_id INTEGER PRIMARY KEY,
  placed         TEXT NOT NULL,
  member_email   TEXT NOT NULL,
  member_name    TEXT NOT NULL,
  book_id        INTEGER NOT NULL,
  book_title     TEXT NOT NULL,
  branch_code    TEXT NOT NULL,
  branch_name    TEXT NOT NULL,
  branch_city    TEXT NOT NULL
);
INSERT INTO reservations_flat VALUES
  (1, '2026-10-02', 'ada@example.org',   'Ada',   5,  'Parable of the Sower', 'CEN', 'Central Library', 'Springfield'),
  (2, '2026-10-03', 'grace@example.org', 'Grace', 10, 'The Fifth Season',     'CEN', 'Central Library', 'Springfield'),
  (3, '2026-10-03', 'linus@example.org', 'Linus', 3,  'The Dispossessed',     'RIV', 'Riverside Branch', 'Springfield'),
  (4, '2026-10-05', 'ada@example.org',   'Ada',   12, 'The Last Man',         'RIV', 'Riverside Branch', 'Springfield'),
  (5, '2026-10-06', 'hedy@example.org',  'Hedy',  5,  'Parable of the Sower', 'HIL', 'Hillside Branch',  'Shelbyville'),
  (6, '2026-10-07', 'alan@example.org',  'Alan',  9,  'Exhalation',           'CEN', 'Central Library', 'Springfield'),
  (7, '2026-10-08', 'grace@example.org', 'Grace', 3,  'The Dispossessed',     'HIL', 'Hillside Branch',  'Shelbyville'),
  (8, '2026-10-09', 'linus@example.org', 'Linus', 7,  'Small Gods',           'CEN', 'Central Library', 'Springfield');
