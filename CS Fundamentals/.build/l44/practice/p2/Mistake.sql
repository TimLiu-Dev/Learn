INSERT OR REPLACE INTO clicks_daily (code, day, clicks)
  SELECT code, date(at_ms / 1000, 'unixepoch'), count(*)
  FROM click_queue
  GROUP BY 1, 2;

DELETE FROM click_queue;
