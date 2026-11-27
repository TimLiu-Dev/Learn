-- Step 1: only the rows still missing a value. In production, in small batches.
UPDATE members SET full_name = name WHERE full_name IS NULL;

-- Step 2: steps 4 to 8 of SQLite's table-rebuild recipe.
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
CREATE INDEX members_by_joined ON members(joined);
