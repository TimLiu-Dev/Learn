package library;

import java.util.List;

final class Schema {
    private Schema() {}

    // One string per statement, so JDBC can run them one at a time.
    static final List<String> STATEMENTS = List.of(
        """
        CREATE TABLE IF NOT EXISTS books (
          id          INTEGER PRIMARY KEY,
          title       TEXT NOT NULL,
          year        INTEGER CHECK (year BETWEEN 1400 AND 2100)
        )""",
        """
        CREATE TABLE IF NOT EXISTS members (
          id     INTEGER PRIMARY KEY,
          name   TEXT NOT NULL,
          email  TEXT UNIQUE NOT NULL
        )""",
        """
        CREATE TABLE IF NOT EXISTS loans (
          id        INTEGER PRIMARY KEY,
          book_id   INTEGER NOT NULL REFERENCES books(id),
          member_id INTEGER NOT NULL REFERENCES members(id),
          loaned    TEXT NOT NULL,          -- ISO dates: 'YYYY-MM-DD' sorts correctly as text
          returned  TEXT
        )""",
        // A partial unique index: at most ONE open loan per book. The database itself refuses a double loan.
        "CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL");
}
