CREATE VIEW open_loans AS
  SELECT m.name AS member, b.title, l.loaned
  FROM loans l JOIN members m ON m.id = l.member_id JOIN books b ON b.id = l.book_id
  WHERE l.returned IS NULL;
SELECT * FROM open_loans ORDER BY loaned;
CREATE VIEW member_directory AS SELECT id, name FROM members;
SELECT * FROM member_directory WHERE id <= 3;
UPDATE open_loans SET loaned = '2026-01-01';
