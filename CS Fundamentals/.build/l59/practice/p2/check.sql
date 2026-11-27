SELECT '1. columns' AS test, (SELECT group_concat(name, ' ') FROM (SELECT name FROM pragma_table_info('members') ORDER BY cid)) AS result
UNION ALL SELECT '2. full_name is NOT NULL', "notnull" FROM pragma_table_info('members') WHERE name = 'full_name'
UNION ALL SELECT '3. members with the right full_name', count(*) FROM members m JOIN expected e ON e.id = m.id AND e.full_name = m.full_name
UNION ALL SELECT '4. members_by_joined index', count(*) FROM pragma_index_list('members') WHERE name = 'members_by_joined'
UNION ALL SELECT '5. unique indexes (email)', count(*) FROM pragma_index_list('members') WHERE "unique" = 1
ORDER BY 1;
