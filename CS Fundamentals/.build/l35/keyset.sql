-- keyset.sql: two ways to fetch rows 100,001-100,005 of "newest loans first". Run on a fresh copy of big.db.
.scanstats on
.print '1. OFFSET, no index'
SELECT id, loaned FROM loans ORDER BY loaned DESC, id DESC LIMIT 5 OFFSET 100000;
CREATE INDEX idx_loans_loaned_id ON loans(loaned, id);
.print '2. OFFSET, with the index'
SELECT id, loaned FROM loans ORDER BY loaned DESC, id DESC LIMIT 5 OFFSET 100000;
.print '3. keyset: after the last row the client saw, (2025-05-15, 500)'
SELECT id, loaned FROM loans WHERE (loaned, id) < ('2025-05-15', 500)
ORDER BY loaned DESC, id DESC LIMIT 5;
