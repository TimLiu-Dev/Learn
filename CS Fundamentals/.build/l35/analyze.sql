-- analyze.sql: when the planner guesses wrong. Run on a fresh copy of big.db.
CREATE INDEX idx_loans_member ON loans(member_id);
CREATE INDEX idx_loans_loaned ON loans(loaned);
-- 1,200 loans after Sep 20, but 199,000 with member_id > 100:
EXPLAIN QUERY PLAN SELECT count(*) FROM loans WHERE loaned > '2026-09-20' AND member_id > 100;
ANALYZE;
EXPLAIN QUERY PLAN SELECT count(*) FROM loans WHERE loaned > '2026-09-20' AND member_id > 100;
SELECT idx, stat FROM sqlite_stat1 WHERE tbl = 'loans';
.timer on
SELECT count(*) FROM loans WHERE loaned > '2026-09-20' AND member_id > 100;
SELECT count(*) FROM loans WHERE loaned > '2026-09-20' AND +member_id > 100;
.timer off
EXPLAIN QUERY PLAN SELECT count(*) FROM loans WHERE loaned > '2026-09-20' AND +member_id > 100;
