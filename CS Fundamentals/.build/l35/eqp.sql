-- eqp.sql: reading EXPLAIN QUERY PLAN. Run on a fresh copy of big.db.
.print '1. no index'
EXPLAIN QUERY PLAN SELECT id, book_id, loaned FROM loans WHERE member_id = 42;
CREATE INDEX idx_loans_member ON loans(member_id);
.print '2. with idx_loans_member'
EXPLAIN QUERY PLAN SELECT id, book_id, loaned FROM loans WHERE member_id = 42;
.print '3. only needs what the index holds'
EXPLAIN QUERY PLAN SELECT count(*) FROM loans WHERE member_id = 42;
.print '4. one member, newest loans first'
EXPLAIN QUERY PLAN SELECT id, loaned FROM loans WHERE member_id = 42 ORDER BY loaned DESC;
CREATE INDEX idx_loans_member_loaned ON loans(member_id, loaned);
.print '5. same query, composite index'
EXPLAIN QUERY PLAN SELECT id, loaned FROM loans WHERE member_id = 42 ORDER BY loaned DESC;
.print '6. a join'
EXPLAIN QUERY PLAN
SELECT b.title, l.loaned FROM loans l JOIN books b ON b.id = l.book_id WHERE l.member_id = 42;
