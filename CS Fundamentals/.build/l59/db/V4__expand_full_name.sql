-- Expand: add full_name, and let name be NULL so that v3 can stop writing it.
-- Dropping NOT NULL is another change ALTER TABLE can't make, so rebuild again.
CREATE TABLE new_members (
  id        INTEGER PRIMARY KEY,
  name      TEXT,
  full_name TEXT,
  email     TEXT NOT NULL UNIQUE,
  joined    TEXT
);
INSERT INTO new_members (id, name, email, joined)
  SELECT id, name, email, joined FROM members;
DROP TABLE members;
ALTER TABLE new_members RENAME TO members;
