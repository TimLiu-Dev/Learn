-- fts.sql: why LIKE '%word%' scans, and what an FTS5 index does instead. Run: /usr/bin/sqlite3 :memory: < fts.sql
CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, blurb TEXT NOT NULL);
INSERT INTO books (title, blurb) VALUES
  ('Guards! Guards!', 'A secret society summons a dragon to take over the city of Ankh-Morpork.'),
  ('The Hobbit', 'A hobbit joins thirteen dwarves to win back their mountain home from a dragon.'),
  ('Kindred', 'A writer in 1976 California is pulled back in time to a Maryland plantation.'),
  ('The Fifth Season', 'A woman crosses a broken continent to find her daughter as the world ends.'),
  ('Small Gods', 'A novice learns that his god is now a small tortoise with very few believers.'),
  ('A Wizard of Earthsea', 'A young wizard sets loose a shadow and hunts it across the islands, meeting a dragon.'),
  ('The Dispossessed', 'A physicist leaves his anarchist moon to visit the rich planet his people fled.'),
  ('Cottage Garden Flowers', 'How to grow foxgloves, snapdragons and hollyhocks in a small city garden.');
CREATE INDEX idx_books_blurb ON books(blurb);
PRAGMA case_sensitive_like = ON;
.print '1. a prefix can use the B-tree; a word in the middle cannot'
EXPLAIN QUERY PLAN SELECT id FROM books WHERE blurb LIKE 'A secret%';
EXPLAIN QUERY PLAN SELECT id FROM books WHERE blurb LIKE '%dragon%';
.print '2. LIKE matches characters, not words'
SELECT id, title FROM books WHERE blurb LIKE '%dragon%';
CREATE VIRTUAL TABLE books_fts USING fts5(title, blurb, content='books', content_rowid='id');
INSERT INTO books_fts(books_fts) VALUES ('rebuild');
.print '3. FTS5: the word dragon'
SELECT rowid, title FROM books_fts WHERE books_fts MATCH 'dragon';
.print '4. both words (AND is implied)'
SELECT rowid, title FROM books_fts WHERE books_fts MATCH 'dragon city';
.print '5. either word, best match first'
SELECT rowid, title FROM books_fts WHERE books_fts MATCH 'small OR wizard' ORDER BY rank;
.print '6. the plan: a virtual table, not a scan of books'
EXPLAIN QUERY PLAN SELECT rowid FROM books_fts WHERE books_fts MATCH 'dragon';
