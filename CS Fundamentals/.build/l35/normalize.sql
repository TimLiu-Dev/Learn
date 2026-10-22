.headers on
.mode column
-- Step 1 (1NF): one genre per row. A recursive CTE splits the comma list.
CREATE TABLE loans_1nf AS
WITH RECURSIVE split(loan_id, genre, rest) AS (
  SELECT loan_id, '', genres || ',' FROM loans_flat
  UNION ALL
  SELECT loan_id,
         substr(rest, 1, instr(rest, ',') - 1),
         substr(rest, instr(rest, ',') + 1)
  FROM split WHERE rest <> ''
)
SELECT f.loan_id, s.genre, f.loaned, f.returned, f.member_name, f.member_email,
       f.book_title, f.book_year, f.author_name, f.author_born
FROM loans_flat f JOIN split s ON s.loan_id = f.loan_id
WHERE s.genre <> '';

SELECT (SELECT count(*) FROM loans_flat) AS flat_rows,
       (SELECT count(*) FROM loans_1nf)  AS rows_1nf;
SELECT loan_id, genre, book_title FROM loans_1nf WHERE genre = 'SF';

-- Step 2 (2NF): the key of loans_1nf is (loan_id, genre), but most columns
-- depend on loan_id alone. Move them out; genre is really a fact about the book.
CREATE TABLE loans_2nf AS
  SELECT DISTINCT loan_id, loaned, returned, member_name, member_email,
                  book_title, book_year, author_name, author_born
  FROM loans_1nf;
CREATE TABLE book_genres_2nf AS
  SELECT DISTINCT book_title, genre FROM loans_1nf;

-- Step 3 (3NF): remove transitive dependencies.
-- loan -> member_email -> member_name; loan -> book -> author -> author_born.
CREATE TABLE n_members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT UNIQUE NOT NULL);
INSERT INTO n_members (name, email)
  SELECT DISTINCT member_name, member_email FROM loans_2nf ORDER BY member_email;

CREATE TABLE n_authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL, born INTEGER);
INSERT INTO n_authors (name, born)
  SELECT DISTINCT author_name, author_born FROM loans_2nf ORDER BY author_name;

CREATE TABLE n_books (id INTEGER PRIMARY KEY, title TEXT NOT NULL,
                      year INTEGER, author_id INTEGER REFERENCES n_authors(id));
INSERT INTO n_books (title, year, author_id)
  SELECT DISTINCT l.book_title, l.book_year, a.id
  FROM loans_2nf l JOIN n_authors a ON a.name = l.author_name ORDER BY l.book_title;

CREATE TABLE n_loans (id INTEGER PRIMARY KEY, loaned TEXT NOT NULL, returned TEXT,
                      member_id INTEGER NOT NULL REFERENCES n_members(id),
                      book_id   INTEGER NOT NULL REFERENCES n_books(id));
INSERT INTO n_loans (id, loaned, returned, member_id, book_id)
  SELECT l.loan_id, l.loaned, l.returned, m.id, b.id
  FROM loans_2nf l
  JOIN n_members m ON m.email = l.member_email
  JOIN n_books   b ON b.title = l.book_title;

SELECT (SELECT count(*) FROM n_members) AS members, (SELECT count(*) FROM n_authors) AS authors,
       (SELECT count(*) FROM n_books) AS books, (SELECT count(*) FROM n_loans) AS loans;

-- Lossless? Join everything back and compare with the original: 0 rows differ.
SELECT count(*) AS rows_that_differ FROM (
  SELECT loan_id, loaned, returned, member_name, member_email, book_title, book_year, author_name, author_born
  FROM loans_flat
  EXCEPT
  SELECT l.id, l.loaned, l.returned, m.name, m.email, b.title, b.year, a.name, a.born
  FROM n_loans l JOIN n_members m ON m.id = l.member_id
                 JOIN n_books b   ON b.id = l.book_id
                 JOIN n_authors a ON a.id = b.author_id
);

-- The update anomaly is gone: one row, one fact.
UPDATE n_authors SET name = 'Ursula Le Guin' WHERE name = 'Ursula K. Le Guin';
SELECT changes() AS rows_changed;
