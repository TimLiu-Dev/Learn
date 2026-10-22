-- migrate.sql: reservations_flat -> 3NF, in one transaction.
-- member_name and book_title already live in members/books (keyed by email/id);
-- branch_name and branch_city depend on branch_code, not on the reservation.
PRAGMA foreign_keys = ON;      -- must run before BEGIN: it's a no-op inside a transaction
.headers on
.mode column
SELECT count(*) AS flat_rows FROM reservations_flat;
BEGIN;
CREATE TABLE branches (
  code TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  city TEXT NOT NULL
);
INSERT INTO branches (code, name, city)
  SELECT DISTINCT branch_code, branch_name, branch_city FROM reservations_flat;

CREATE TABLE reservations (
  id          INTEGER PRIMARY KEY,
  placed      TEXT NOT NULL,
  member_id   INTEGER NOT NULL REFERENCES members(id),
  book_id     INTEGER NOT NULL REFERENCES books(id),
  branch_code TEXT NOT NULL REFERENCES branches(code)
);
INSERT INTO reservations (id, placed, member_id, book_id, branch_code)
  SELECT r.reservation_id, r.placed, m.id, r.book_id, r.branch_code
  FROM reservations_flat r JOIN members m ON m.email = r.member_email;

-- Verify before dropping anything: same row count, and a lossless round trip.
SELECT (SELECT count(*) FROM reservations) AS new_rows,
       (SELECT count(*) FROM branches)     AS branches;
SELECT count(*) AS rows_that_differ FROM (
  SELECT reservation_id, placed, member_email, member_name, book_id, book_title,
         branch_code, branch_name, branch_city FROM reservations_flat
  EXCEPT
  SELECT r.id, r.placed, m.email, m.name, b.id, b.title, br.code, br.name, br.city
  FROM reservations r JOIN members m  ON m.id = r.member_id
                      JOIN books b    ON b.id = r.book_id
                      JOIN branches br ON br.code = r.branch_code
);
DROP TABLE reservations_flat;
COMMIT;
