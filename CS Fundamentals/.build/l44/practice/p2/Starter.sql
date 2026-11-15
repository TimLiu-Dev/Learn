-- Flush: add the queued clicks to the daily counts, then empty the queue.
INSERT INTO clicks_daily (code, day, clicks)
  SELECT code, date(at_ms / 1000, 'unixepoch'), count(*)
  FROM click_queue
  GROUP BY 1, 2;
-- TODO: the insert above fails when (code, day) already has a row. Make it add instead.

-- TODO: empty the queue.
