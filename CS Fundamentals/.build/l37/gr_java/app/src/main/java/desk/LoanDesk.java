package desk;

import java.util.HashMap;
import java.util.Map;

/**
 * A library loan desk. Callers say <em>what</em> they want; the order of checks,
 * the id scheme and the storage are hidden in here.
 */
public final class LoanDesk {
    public enum LendResult { LENT, ALREADY_ON_LOAN, NO_SUCH_BOOK, NO_SUCH_MEMBER }

    private final Map<Long, String> books = new HashMap<>();
    private final Map<Long, String> members = new HashMap<>();
    private final Map<Long, Long> openLoans = new HashMap<>();   // bookId -> memberId
    private long nextId = 1;

    public long addBook(String title) {
        long id = nextId++;
        books.put(id, title);
        return id;
    }

    public long addMember(String name) {
        long id = nextId++;
        members.put(id, name);
        return id;
    }

    public LendResult lend(long bookId, long memberId) {
        if (!books.containsKey(bookId)) return LendResult.NO_SUCH_BOOK;
        if (!members.containsKey(memberId)) return LendResult.NO_SUCH_MEMBER;
        if (openLoans.containsKey(bookId)) return LendResult.ALREADY_ON_LOAN;
        openLoans.put(bookId, memberId);
        return LendResult.LENT;
    }

    /** Takes a book back. Returning a book that is not on loan does nothing: there is no error case. */
    public void giveBack(long bookId) {
        openLoans.remove(bookId);
    }

    /** Name of the member who has {@code bookId}, or {@code null} if it is on the shelf (or unknown). */
    public String borrowerOf(long bookId) {
        Long memberId = openLoans.get(bookId);
        return memberId == null ? null : members.get(memberId);
    }
}
