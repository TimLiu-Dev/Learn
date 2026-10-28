package shelf.store;

import static shelf.store.Db.insert;
import static shelf.store.Db.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import shelf.domain.Book;

public final class SqliteBookStore implements BookStore {
    private static final String SELECT =
        "SELECT b.id, b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id";
    private final Db db;

    public SqliteBookStore(Db db) {
        this.db = db;
    }

    private static Book book(ResultSet rs) throws SQLException {
        int year = rs.getInt(4);
        return new Book(rs.getLong(1), rs.getString(2), rs.getString(3), rs.wasNull() ? null : year);
    }

    // Authors are a separate table (L35); find-or-create the row for this name.
    // (Db.update, not update: this class's own update method would hide a static import.)
    private static long authorId(Connection c, String name) throws SQLException {
        Db.update(c, "INSERT INTO authors (name) VALUES (?) ON CONFLICT (name) DO NOTHING", name);
        return query(c, "SELECT id FROM authors WHERE name = ?", rs -> rs.getLong(1), name).getFirst();
    }

    @Override
    public Book add(String title, String author, Integer year) {
        return db.transaction(c -> {
            long id = insert(c, "INSERT INTO books (title, author_id, year) VALUES (?, ?, ?)", title, authorId(c, author), year);
            return new Book(id, title, author, year);
        });
    }

    @Override
    public Optional<Book> find(long id) {
        return db.read(c -> query(c, SELECT + " WHERE b.id = ?", SqliteBookStore::book, id).stream().findFirst());
    }

    @Override
    public List<Book> list(int limit, int offset, String author) {
        return db.read(c -> author == null
            ? query(c, SELECT + " ORDER BY b.id LIMIT ? OFFSET ?", SqliteBookStore::book, limit, offset)
            : query(c, SELECT + " WHERE a.name = ? ORDER BY b.id LIMIT ? OFFSET ?", SqliteBookStore::book, author, limit, offset));
    }

    @Override
    public int count(String author) {
        return db.read(c -> author == null
            ? query(c, "SELECT COUNT(*) FROM books", rs -> rs.getInt(1)).getFirst()
            : query(c, "SELECT COUNT(*) FROM books b JOIN authors a ON a.id = b.author_id WHERE a.name = ?",
                    rs -> rs.getInt(1), author).getFirst());
    }

    @Override
    public Optional<Book> update(long id, String title, String author, Integer year) {
        return db.transaction(c -> {
            if (query(c, "SELECT 1 FROM books WHERE id = ?", rs -> true, id).isEmpty()) return Optional.empty();
            Db.update(c, "UPDATE books SET title = ?, author_id = ?, year = ? WHERE id = ?", title, authorId(c, author), year, id);
            return Optional.of(new Book(id, title, author, year));
        });
    }

    @Override
    public boolean delete(long id) {
        return db.transaction(c -> Db.update(c, "DELETE FROM books WHERE id = ?", id) == 1);
    }
}
