-- Expand, as in db/V4, plus a trigger that keeps full_name in step
-- when v1 (which knows only name) renames a member.
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
CREATE TRIGGER members_sync_full_name AFTER UPDATE OF name ON members
  WHEN NEW.name IS NOT NULL
  BEGIN
    UPDATE members SET full_name = NEW.name WHERE id = NEW.id;
  END;
