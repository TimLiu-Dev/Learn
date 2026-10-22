.headers on
.mode column
-- Three loans repeat the same author name.
SELECT loan_id, member_name, book_title, author_name
FROM loans_flat WHERE author_name LIKE 'Ursula%';

-- 1. Update anomaly: fix the spelling in one row only.
UPDATE loans_flat SET author_name = 'Ursula Le Guin' WHERE loan_id = 1;
SELECT DISTINCT author_name, author_born FROM loans_flat WHERE author_born = 1929;

-- 2. Insert anomaly: record a new author who has no loans yet.
INSERT INTO loans_flat (author_name, author_born) VALUES ('Ann Leckie', 1966);

-- 3. Delete anomaly: Hedy returns her only book and we delete the loan.
SELECT member_name, member_email FROM loans_flat WHERE member_name = 'Hedy';
DELETE FROM loans_flat WHERE loan_id = 9;
SELECT count(*) AS hedy_rows FROM loans_flat WHERE member_name = 'Hedy';
