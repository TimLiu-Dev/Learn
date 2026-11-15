-- Equality column first, then the ORDER BY column, then the rest the query reads.
CREATE INDEX idx_books_author_year_title ON books(author_id, year, title);
