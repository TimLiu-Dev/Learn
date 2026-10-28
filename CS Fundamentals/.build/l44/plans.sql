.read schema.sql
INSERT INTO links(code, url, created_at, expires_at) VALUES
  ('1', 'https://example.com/a', 1761475200000, NULL),
  ('2', 'https://example.com/b', 1761475200000, 1761561600000),
  ('docs', 'https://example.com/docs', 1761475200000, NULL);
INSERT INTO clicks_daily VALUES ('1', '2026-10-26', 5)
  ON CONFLICT(code, day) DO UPDATE SET clicks = clicks + excluded.clicks;
INSERT INTO clicks_daily VALUES ('1', '2026-10-26', 3)
  ON CONFLICT(code, day) DO UPDATE SET clicks = clicks + excluded.clicks;
.headers on
.mode column
.echo on
-- the redirect: the hottest query in the system
EXPLAIN QUERY PLAN SELECT url, expires_at FROM links WHERE code = 'docs';
-- the stats page
EXPLAIN QUERY PLAN SELECT sum(clicks) FROM clicks_daily WHERE code = '1';
-- the nightly purge
EXPLAIN QUERY PLAN DELETE FROM links WHERE expires_at < 1761600000000;
SELECT code, day, clicks FROM clicks_daily;
