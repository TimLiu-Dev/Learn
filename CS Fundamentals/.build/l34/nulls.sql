SELECT title FROM books WHERE year = NULL;
SELECT title FROM books WHERE year IS NULL;
SELECT NULL = NULL AS eq, NULL IS NULL AS is_null, 1 + NULL AS sum;
SELECT title, COALESCE(year, 'unknown') AS year FROM books WHERE author_id = 7;
