-- 6. Members who borrowed every book by Ted Chiang (author 3):
--    there is no Chiang book they have NOT borrowed.
SELECT m.name
FROM members m
WHERE NOT EXISTS (
  SELECT 1 FROM books b
  WHERE b.author_id = 3
    AND NOT EXISTS (SELECT 1 FROM loans l WHERE l.book_id = b.id AND l.member_id = m.id)
)
ORDER BY m.name;
