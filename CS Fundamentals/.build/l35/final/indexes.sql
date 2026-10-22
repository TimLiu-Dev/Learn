-- indexes.sql: one index per query, each justified by its plan.
-- Q1: equality on member_id, then ORDER BY loaned: one composite index
--     serves both, so the temp B-tree for sorting disappears.
CREATE INDEX idx_loans_member_loaned ON loans(member_id, loaned);
-- Q2: a partial index holds only open loans (20% of rows), sorted by date,
--     and carries member_id so no table lookup is needed per row.
CREATE INDEX idx_loans_open_loaned ON loans(loaned, member_id) WHERE returned IS NULL;
-- Q3: equality on year first, then a range on price, already in ORDER BY order:
--     no temp B-tree for sorting.
CREATE INDEX idx_books_year_price ON books(year, price_cents);
-- Q4: the query compares lower(email), so index exactly that expression.
CREATE INDEX idx_members_lower_email ON members(lower(email));
-- Q5: a date range that only needs book_id: a covering index, no table reads.
CREATE INDEX idx_loans_loaned_book ON loans(loaned, book_id);
