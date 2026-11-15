-- The daily counts from earlier flushes (schema.sql from the lesson).
CREATE TABLE clicks_daily (
  code   TEXT    NOT NULL,
  day    TEXT    NOT NULL,             -- '2026-11-14'
  clicks INTEGER NOT NULL,
  PRIMARY KEY (code, day)
) WITHOUT ROWID;
INSERT INTO clicks_daily VALUES
  ('4xdNF0v', '2026-11-13', 5),
  ('docs',    '2026-11-14', 2);

-- Clicks the redirects enqueued since the last flush (at_ms = epoch milliseconds, UTC).
CREATE TABLE click_queue (
  code   TEXT    NOT NULL,
  at_ms  INTEGER NOT NULL
);
INSERT INTO click_queue VALUES
  ('4xdNF0v', 1794614399500),   -- 2026-11-13 23:59:59.5
  ('4xdNF0v', 1794614400100),   -- 2026-11-14 00:00:00.1
  ('docs',    1794646000000),
  ('docs',    1794646001000),
  ('docs',    1794646002000),
  ('g8',      1794656000000);
