-- defeat.sql: four ways to stop an index from helping. Run on a fresh copy of big.db.
CREATE INDEX idx_loans_member ON loans(member_id);
.print '1a. function on the column (members.email has a UNIQUE index)'
EXPLAIN QUERY PLAN SELECT id FROM members WHERE email = 'm42@example.org';
EXPLAIN QUERY PLAN SELECT id FROM members WHERE lower(email) = 'm42@example.org';
.print '1b. fix: index the expression itself'
CREATE INDEX idx_members_lower_email ON members(lower(email));
EXPLAIN QUERY PLAN SELECT id FROM members WHERE lower(email) = 'm42@example.org';
.print '2a. LIKE: default (case-insensitive) mode'
EXPLAIN QUERY PLAN SELECT id FROM members WHERE email LIKE 'm42%';
.print '2b. LIKE with case_sensitive_like on: prefix uses the index, suffix cannot'
PRAGMA case_sensitive_like = ON;
EXPLAIN QUERY PLAN SELECT id FROM members WHERE email LIKE 'm42%';
EXPLAIN QUERY PLAN SELECT id FROM members WHERE email LIKE '%42@example.org';
PRAGMA case_sensitive_like = OFF;
.print '3. types: SQLite converts the literal; a CAST on the column defeats the index'
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE member_id = '42';
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE CAST(member_id AS TEXT) = '42';
.print '4. OR across two columns: one index is not enough'
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE member_id = 42 OR book_id = 7;
CREATE INDEX idx_loans_book ON loans(book_id);
EXPLAIN QUERY PLAN SELECT id FROM loans WHERE member_id = 42 OR book_id = 7;
