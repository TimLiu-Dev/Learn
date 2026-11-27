UPDATE members SET full_name = name WHERE full_name IS NULL;
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
