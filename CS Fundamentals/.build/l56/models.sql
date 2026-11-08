-- models.sql: the same library data as documents, as rows, and as a graph. Run: /usr/bin/sqlite3 :memory: < models.sql
.mode box
.headers on

-- 1. Document model: each book carries its authors inside it (embedded, denormalised).
CREATE TABLE book_docs (id INTEGER PRIMARY KEY, doc TEXT NOT NULL);
INSERT INTO book_docs (doc) VALUES
  ('{"title": "The Dispossessed", "year": 1974, "authors": ["Ursula K. Le Guin"]}'),
  ('{"title": "Good Omens", "year": 1990, "authors": ["Terry Pratchett", "Neil Gaiman"]}'),
  ('{"title": "The Lathe of Heaven", "authors": ["Ursula K. Le Guin"]}');  -- no year: nothing stops it

SELECT doc ->> '$.title' AS title, doc ->> '$.year' AS year,
       json_array_length(doc -> '$.authors') AS authors
FROM book_docs;

-- Who wrote what? The author names live inside every document that mentions them.
SELECT a.value AS author, count(*) AS books
FROM book_docs, json_each(book_docs.doc, '$.authors') AS a
GROUP BY a.value ORDER BY books DESC, author;

-- 2. Relational model: the schema is checked when you write (L34–L35).
CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, year INTEGER NOT NULL);
INSERT INTO books (title) VALUES ('The Lathe of Heaven');

-- 3. Graph model: who can Ana reach through friends, within 2 hops?
CREATE TABLE friends (a TEXT, b TEXT);
INSERT INTO friends VALUES ('ana','ben'), ('ben','cai'), ('cai','dee'), ('ana','eve'), ('eve','cai'), ('dee','fay');
WITH RECURSIVE reach(person, hops) AS (
  SELECT 'ana', 0
  UNION
  SELECT f.b, r.hops + 1 FROM reach r JOIN friends f ON f.a = r.person WHERE r.hops < 2
)
SELECT person, min(hops) AS hops FROM reach GROUP BY person ORDER BY hops, person;
