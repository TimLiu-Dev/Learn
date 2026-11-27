-- Contract: no running version reads or writes name any more.
-- full_name becomes NOT NULL, so this fails (and rolls back) if the backfill missed a row.
CREATE TABLE new_members (
  id        INTEGER PRIMARY KEY,
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL UNIQUE,
  joined    TEXT
);
INSERT INTO new_members (id, full_name, email, joined)
  SELECT id, full_name, email, joined FROM members;
DROP TABLE members;
ALTER TABLE new_members RENAME TO members;
