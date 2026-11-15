-- Flush: add the queued clicks to the daily counts, then empty the queue.
INSERT INTO clicks_daily (code, day, clicks)
  SELECT code, date(at_ms / 1000, 'unixepoch'), count(*)
  FROM click_queue
  GROUP BY 1, 2
  ON CONFLICT (code, day) DO UPDATE SET clicks = clicks + excluded.clicks;

DELETE FROM click_queue;
