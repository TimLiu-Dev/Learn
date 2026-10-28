-- URL shortener schema (SQLite). The capstone reuses this shape.
CREATE TABLE links (
  id         INTEGER PRIMARY KEY,      -- the counter: rowid, 1, 2, 3, ...
  code       TEXT    NOT NULL UNIQUE,  -- base62(id) or a custom alias
  url        TEXT    NOT NULL,
  created_at INTEGER NOT NULL,         -- epoch milliseconds
  expires_at INTEGER                   -- NULL = never expires
);

-- Clicks are aggregated per code per day, not stored one row per click (Estimates: 600 G rows).
CREATE TABLE clicks_daily (
  code   TEXT    NOT NULL,
  day    TEXT    NOT NULL,             -- '2026-10-26'
  clicks INTEGER NOT NULL,
  PRIMARY KEY (code, day)
) WITHOUT ROWID;

-- Only rows that can expire are indexed (a partial index, L35).
CREATE INDEX links_expiring ON links(expires_at) WHERE expires_at IS NOT NULL;
