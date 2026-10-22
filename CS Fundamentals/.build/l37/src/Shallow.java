import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Shallow {
    // A SHALLOW module: everything is public, and callers must know the right order of calls.
    static class ShallowLoanDesk {
        public final Map<Long, String> books = new HashMap<>();     // id -> title
        public final Map<Long, String> members = new HashMap<>();   // id -> name
        public final Map<Long, Long> openLoans = new HashMap<>();   // bookId -> memberId
        public long nextId = 1;

        public long newId() { return nextId++; }
        public boolean isKnownBook(long id) { return books.containsKey(id); }
        public boolean isKnownMember(long id) { return members.containsKey(id); }
        public boolean isOnLoan(long bookId) { return openLoans.containsKey(bookId); }
        public void recordLoan(long bookId, long memberId) { openLoans.put(bookId, memberId); }
        public void clearLoan(long bookId) {
            if (openLoans.remove(bookId) == null) throw new IllegalStateException("book " + bookId + " is not on loan");
        }
    }

    // Caller 1 knows the ritual: check book, check member, check loan, then record.
    static String lendCarefully(ShallowLoanDesk desk, long bookId, long memberId) {
        if (!desk.isKnownBook(bookId)) return "no such book";
        if (!desk.isKnownMember(memberId)) return "no such member";
        if (desk.isOnLoan(bookId)) return "already on loan";
        desk.recordLoan(bookId, memberId);
        return "lent";
    }

    // Caller 2, written months later by someone else, skips a step.
    static String lendInAHurry(ShallowLoanDesk desk, long bookId, long memberId) {
        if (!desk.isKnownBook(bookId)) return "no such book";
        desk.recordLoan(bookId, memberId);
        return "lent";
    }

    public static void main(String[] args) {
        ShallowLoanDesk desk = new ShallowLoanDesk();
        long dune = desk.newId(); desk.books.put(dune, "Dune");
        long ada = desk.newId(); desk.members.put(ada, "Ada");
        long linus = desk.newId(); desk.members.put(linus, "Linus");

        System.out.println("Ada:   " + lendCarefully(desk, dune, ada));
        System.out.println("Linus: " + lendInAHurry(desk, dune, linus));
        System.out.println("Dune is now with: " + desk.members.get(desk.openLoans.get(dune)));
        try { desk.clearLoan(99); } catch (IllegalStateException e) { System.out.println(e); }

        long methods = Arrays.stream(ShallowLoanDesk.class.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).count();
        long fields = Arrays.stream(ShallowLoanDesk.class.getDeclaredFields()).filter(f -> Modifier.isPublic(f.getModifiers())).count();
        System.out.println("public methods callers can touch: " + methods + ", public fields: " + fields);
    }
}
