-- prefix.sql: which queries can use idx_loans_member_loaned(member_id, loaned)?
CREATE INDEX idx_loans_member_loaned ON loans(member_id, loaned);
.print '(a) member only'
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE member_id = 42;
.print '(b) member and date range'
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE member_id = 42 AND loaned > '2026-01-01';
.print '(c) date range only'
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE loaned > '2026-09-20';
.print '(d) sort by both columns'
EXPLAIN QUERY PLAN SELECT member_id, loaned FROM loans ORDER BY member_id, loaned LIMIT 5;
.print '(e) height of the new index'
SELECT max(length(path) - length(replace(path, '/', ''))) AS levels, count(*) AS pages
FROM dbstat WHERE name = 'idx_loans_member_loaned';
DROP INDEX idx_loans_member_loaned;
