CREATE TABLE loan_counts (book_id INTEGER PRIMARY KEY, n INTEGER NOT NULL, last_loan TEXT);
INSERT INTO loan_counts VALUES (7, 1, '2026-11-02')
  ON CONFLICT (book_id) DO UPDATE SET n = n + 1, last_loan = excluded.last_loan;
INSERT INTO loan_counts VALUES (7, 1, '2026-11-09')
  ON CONFLICT (book_id) DO UPDATE SET n = n + 1, last_loan = excluded.last_loan;
INSERT INTO loan_counts VALUES (9, 1, '2026-11-09')
  ON CONFLICT (book_id) DO UPDATE SET n = n + 1, last_loan = excluded.last_loan;
SELECT * FROM loan_counts;
INSERT OR REPLACE INTO loan_counts (book_id, last_loan) VALUES (7, '2026-11-10');
