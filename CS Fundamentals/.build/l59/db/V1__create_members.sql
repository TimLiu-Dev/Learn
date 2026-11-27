CREATE TABLE members (
  id    INTEGER PRIMARY KEY,
  name  TEXT NOT NULL,
  email TEXT NOT NULL
);
CREATE TABLE loans (
  id        INTEGER PRIMARY KEY,
  member_id INTEGER NOT NULL REFERENCES members(id),
  book      TEXT NOT NULL
);
CREATE INDEX loans_by_member ON loans(member_id);
