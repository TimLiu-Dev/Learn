SELECT code, day, clicks FROM clicks_daily
UNION ALL SELECT 'left in queue', '', count(*) FROM click_queue
ORDER BY 1, 2;
