import java.util.Map;
import java.util.Objects;

public class Practice {
    record Note(int id, String owner, String text) { }
    record Reply(int status, String body) { }

    static final Map<String, String> TOKENS = Map.of("tok-ana", "ana", "tok-ben", "ben");
    static final Map<Integer, Note> NOTES = Map.of(
            1, new Note(1, "ana", "Ana's plan"),
            2, new Note(2, "ben", "Ben's list"));

    static Reply readNote(String token, int id) {
        Note note = NOTES.get(id);
        if (note == null) return new Reply(404, "not found");
        return new Reply(200, note.text());                          // anyone can read anything!
    }

    // CHECKS
    public static void main(String[] args) {
        check("ana reads her own note", new Reply(200, "Ana's plan"), () -> readNote("tok-ana", 1));
        check("ben cannot read ana's note", new Reply(404, "not found"), () -> readNote("tok-ben", 1));
        check("someone else's note looks exactly like a missing one", readNote("tok-ben", 99), () -> readNote("tok-ben", 1));
        check("no token is 401", new Reply(401, "who are you?"), () -> readNote(null, 1));
        check("an unknown token is 401", new Reply(401, "who are you?"), () -> readNote("tok-eve", 2));
        check("a stranger can't probe which ids exist", new Reply(401, "who are you?"), () -> readNote(null, 99));
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
