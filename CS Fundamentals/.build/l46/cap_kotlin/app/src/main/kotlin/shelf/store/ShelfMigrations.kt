package shelf.store

// Shelf's schema history, oldest first. A migration that has run anywhere is never edited:
// the runner's checksum would refuse to start. Change the schema by adding the next version (L59).
// V1 says IF NOT EXISTS so that a database created before migrations existed is adopted, not rejected.
val SHELF_MIGRATIONS = listOf(
    Migration(1, "initial schema", """
        CREATE TABLE IF NOT EXISTS authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE);
        CREATE TABLE IF NOT EXISTS books (
          id        INTEGER PRIMARY KEY,
          title     TEXT NOT NULL,
          author_id INTEGER NOT NULL REFERENCES authors(id),
          year      INTEGER CHECK (year BETWEEN 1400 AND 2100));
        CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE);
        CREATE TABLE IF NOT EXISTS loans (
          id        INTEGER PRIMARY KEY,
          book_id   INTEGER NOT NULL REFERENCES books(id),
          member_id INTEGER NOT NULL REFERENCES members(id),
          loaned    TEXT NOT NULL,
          returned  TEXT);
        -- At most one open loan per book: the database itself refuses a double loan (L36).
        CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL;
        CREATE INDEX IF NOT EXISTS loans_by_member ON loans(member_id);
    """.trimIndent()),
    // L45's Exercise 3: the ?author= filter stops scanning books.
    Migration(2, "books by author", "CREATE INDEX IF NOT EXISTS books_by_author ON books(author_id);"),
)
