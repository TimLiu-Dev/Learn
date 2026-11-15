import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Practice {
    enum Action { READ, EDIT, DELETE, SHARE }

    record User(String name, boolean admin) {}
    record Note(String owner, boolean isPublic) {}

    // Every request asks this one method. user is null when nobody is logged in; note is null when it doesn't exist.
    static int decide(User user, Action action, Note note) {
        if (note == null) return 404;
        if (action == Action.READ && note.isPublic()) return 200;            // public on purpose, and said so
        if (user == null) return 401;                                         // who are you? (authentication)
        if (user.name().equals(note.owner()) && EnumSet.of(Action.READ, Action.EDIT, Action.DELETE).contains(action)) return 200;
        if (user.admin() && EnumSet.of(Action.READ, Action.DELETE).contains(action)) return 200;   // least privilege: no EDIT
        return 403;                                                           // no rule matched: deny by default
    }

    // CHECKS
    static final User ANA = new User("ana", false), BEN = new User("ben", false), ROOT = new User("root", true);
    static final Note DIARY = new Note("ana", false), RECIPE = new Note("ana", true);

    public static void main(String[] args) {
        check("anonymous may read a public note", 200, () -> decide(null, Action.READ, RECIPE));
        check("ben may read a public note", 200, () -> decide(BEN, Action.READ, RECIPE));
        check("no login: reading a private note is 401", 401, () -> decide(null, Action.READ, DIARY));
        check("no login: editing a public note is 401", 401, () -> decide(null, Action.EDIT, RECIPE));
        for (Action a : List.of(Action.READ, Action.EDIT, Action.DELETE)) {
            check("the owner may " + a, 200, () -> decide(ANA, a, DIARY));
            check("ben may not " + a + " ana's diary", 403, () -> decide(BEN, a, DIARY));
        }
        check("ben may not edit ana's public recipe", 403, () -> decide(BEN, Action.EDIT, RECIPE));
        check("an admin may read", 200, () -> decide(ROOT, Action.READ, DIARY));
        check("an admin may delete", 200, () -> decide(ROOT, Action.DELETE, DIARY));
        check("an admin may not edit", 403, () -> decide(ROOT, Action.EDIT, DIARY));
        check("SHARE is denied to the owner", 403, () -> decide(ANA, Action.SHARE, DIARY));
        check("SHARE is denied to the admin", 403, () -> decide(ROOT, Action.SHARE, DIARY));
        check("a missing note is 404", 404, () -> decide(ANA, Action.READ, null));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
