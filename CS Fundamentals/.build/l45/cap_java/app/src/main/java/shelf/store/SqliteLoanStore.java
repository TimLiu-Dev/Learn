package shelf.store;

import static shelf.store.Db.insert;
import static shelf.store.Db.query;
import static shelf.store.Db.update;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import shelf.domain.Loan;

public final class SqliteLoanStore implements LoanStore {
    private static final String SELECT = "SELECT id, book_id, member_id, loaned, returned FROM loans";
    private final Db db;

    public SqliteLoanStore(Db db) {
        this.db = db;
    }

    private static Loan loan(ResultSet rs) throws SQLException {
        String returned = rs.getString(5);                       // ISO dates as TEXT (L34)
        return new Loan(rs.getLong(1), rs.getLong(2), rs.getLong(3),
                        LocalDate.parse(rs.getString(4)), returned == null ? null : LocalDate.parse(returned));
    }

    @Override
    public Loan add(long bookId, long memberId, LocalDate loaned) {
        return db.transaction(c -> {
            long id = insert(c, "INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)", bookId, memberId, loaned.toString());
            return new Loan(id, bookId, memberId, loaned, null);
        });
    }

    @Override
    public Optional<Loan> find(long id) {
        return db.read(c -> query(c, SELECT + " WHERE id = ?", SqliteLoanStore::loan, id).stream().findFirst());
    }

    @Override
    public Optional<Loan> openForBook(long bookId) {
        return db.read(c ->
            query(c, SELECT + " WHERE book_id = ? AND returned IS NULL", SqliteLoanStore::loan, bookId).stream().findFirst());
    }

    @Override
    public List<Loan> forMember(long memberId) {
        return db.read(c -> query(c, SELECT + " WHERE member_id = ? ORDER BY id", SqliteLoanStore::loan, memberId));
    }

    @Override
    public boolean anyForBook(long bookId) {
        return db.read(c -> !query(c, "SELECT 1 FROM loans WHERE book_id = ? LIMIT 1", rs -> true, bookId).isEmpty());
    }

    @Override
    public boolean close(long id, LocalDate returned) {
        return db.transaction(c ->
            update(c, "UPDATE loans SET returned = ? WHERE id = ? AND returned IS NULL", returned.toString(), id) == 1);
    }
}
