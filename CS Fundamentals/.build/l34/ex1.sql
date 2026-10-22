PRAGMA foreign_keys = ON;
CREATE TABLE events (
  id         INTEGER PRIMARY KEY,
  title      TEXT NOT NULL,
  starts     TEXT NOT NULL,
  capacity   INTEGER NOT NULL CHECK (capacity > 0),
  host_id    INTEGER REFERENCES members(id) ON DELETE SET NULL
);
INSERT INTO events (title, starts, capacity, host_id) VALUES ('Book club', '2026-11-05', 12, 8);
INSERT INTO events (title, starts, capacity, host_id) VALUES ('Empty room', '2026-11-06', 0, 1);
INSERT INTO events (title, starts, capacity, host_id) VALUES ('Ghost host', '2026-11-07', 5, 42);
INSERT INTO events (starts, capacity) VALUES ('2026-11-08', 5);
DELETE FROM members WHERE id = 8;
SELECT * FROM events;
