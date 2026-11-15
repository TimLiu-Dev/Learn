SELECT a.value AS author, count(*) AS books
FROM book_docs d, json_each(d.doc, '$.authors') AS a
WHERE d.doc ->> '$.year' < 1980
GROUP BY a.value
ORDER BY books DESC, author;
