CREATE TABLE links (code TEXT PRIMARY KEY, expires_at INTEGER);  -- NULL = never expires
INSERT INTO links VALUES ('docs', NULL), ('sale', 100), ('promo', 300);
-- How many links are still live at time 200?
SELECT count(*) FROM links WHERE NOT (expires_at <= 200);
