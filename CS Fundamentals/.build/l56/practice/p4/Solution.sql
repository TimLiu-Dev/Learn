SELECT a.value AS author, count(*) AS books
FROM book_docs d, json_each(d.doc, '$.authors') AS a
WHERE CAST(d.doc ->> '$.year' AS INTEGER) < 1980    -- one document stores its year as text
GROUP BY a.value
ORDER BY books DESC, author;
