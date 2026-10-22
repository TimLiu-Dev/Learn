.headers on
.mode column
-- "Which loans were science fiction?"
SELECT loan_id, book_title, genres FROM loans_flat WHERE genres LIKE '%SF%';
-- "How many genres does each book have?" Nothing in SQL counts list items in a string.
SELECT DISTINCT book_title, genres,
       length(genres) - length(replace(genres, ',', '')) + 1 AS n_genres
FROM loans_flat ORDER BY book_title LIMIT 4;
