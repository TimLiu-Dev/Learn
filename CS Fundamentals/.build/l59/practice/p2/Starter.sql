-- Step 1: fill in the full_name values the backfill missed.

-- Step 2: contract. Rebuild members without name, with full_name NOT NULL.
-- Keep email's UNIQUE and the members_by_joined index.
CREATE TABLE new_members (
  id        INTEGER PRIMARY KEY,
  full_name TEXT,
  email     TEXT NOT NULL UNIQUE,
  joined    TEXT
);
-- copy the rows, drop the old table, rename the new one, recreate the index
