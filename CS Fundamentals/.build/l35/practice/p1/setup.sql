-- fines_flat: every unpaid fine, with the member copied into each row.
PRAGMA foreign_keys = ON;
CREATE TABLE fines_flat (
  fine_id       INTEGER PRIMARY KEY,
  member_email  TEXT NOT NULL,
  member_name   TEXT NOT NULL,
  amount_cents  INTEGER NOT NULL,
  reason        TEXT NOT NULL
);
INSERT INTO fines_flat VALUES
  (1, 'ada@example.org',   'Ada',   150, 'late: Kindred'),
  (2, 'grace@example.org', 'Grace', 300, 'late: Small Gods'),
  (3, 'ada@example.org',   'Ada',   500, 'damaged cover'),
  (4, 'linus@example.org', 'Linus', 150, 'late: Exhalation'),
  (5, 'grace@example.org', 'Grace', 200, 'late: Stories of Your Life'),
  (6, 'grace@example.org', 'Grace',  50, 'lost bookmark');
