-- overdue.sql: an index that doesn't help. Overdue = still out after 21 days, as of 2026-10-01.
SELECT count(*) AS overdue FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
EXPLAIN QUERY PLAN SELECT member_id FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
.timer on
SELECT sum(member_id) FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
SELECT sum(member_id) FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
.timer off
CREATE INDEX idx_loans_returned_loaned ON loans(returned, loaned);
EXPLAIN QUERY PLAN SELECT member_id FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
.timer on
SELECT sum(member_id) FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
SELECT sum(member_id) FROM loans WHERE returned IS NULL AND loaned < '2026-09-10';
.timer off
