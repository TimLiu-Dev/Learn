-- Two different books share a title. Split the table, then join it back on title.
CREATE TABLE flat (loan_id INTEGER PRIMARY KEY, title TEXT, author TEXT);
INSERT INTO flat VALUES (1, 'Selected Poems', 'W. H. Auden'),
                        (2, 'Selected Poems', 'Sylvia Plath'),
                        (3, 'Kindred',        'Octavia E. Butler');
CREATE TABLE t_loans AS SELECT loan_id, title FROM flat;
CREATE TABLE t_books AS SELECT DISTINCT title, author FROM flat;
SELECT count(*) FROM t_loans l JOIN t_books b ON b.title = l.title;
