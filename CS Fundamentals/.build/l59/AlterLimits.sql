CREATE TABLE members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL);
INSERT INTO members (name, email) VALUES ('Ada', 'ada@example.com');
ALTER TABLE members ADD COLUMN joined TEXT;
ALTER TABLE members ADD COLUMN phone TEXT NOT NULL;
ALTER TABLE members ADD CONSTRAINT unique_email UNIQUE (email);
ALTER TABLE members ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE members RENAME COLUMN name TO full_name;
SELECT * FROM members;
