-- ALTER TABLE can't add a constraint, so rebuild the table
-- (sqlite.org/lang_altertable.html, steps 3-9; the runner does 1, 2, 10, 11, 12).
CREATE TABLE new_members (
  id     INTEGER PRIMARY KEY,
  name   TEXT NOT NULL,
  email  TEXT NOT NULL UNIQUE,
  joined TEXT
);
INSERT INTO new_members (id, name, email, joined)
  SELECT id, name, email, joined FROM members;
DROP TABLE members;
ALTER TABLE new_members RENAME TO members;
