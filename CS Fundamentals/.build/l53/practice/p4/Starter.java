import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // The arguments a program receives after bash expands `line`.
    // vars are the shell's variables; args are the script's arguments ($1, $2, ...).
    static List<String> expand(String line, Map<String, String> vars, List<String> args) {
        throw new UnsupportedOperationException("TODO: split the line on spaces, then expand each token by its form");
    }

    // CHECKS
    public static void main(String[] args) {
        Map<String, String> vars = Map.of("f", "my notes.txt", "dest", "backup", "empty", "");
        List<String> argv = List.of("my notes.txt", "b.txt");
        check("a quoted variable stays one word", List.of("cp", "my notes.txt", "backup"), () -> expand("cp \"$f\" \"$dest\"", vars, argv));
        check("an unquoted variable is split into words", List.of("rm", "my", "notes.txt"), () -> expand("rm $f", vars, argv));
        check("quoted dollar-at keeps each argument whole", List.of("ls", "my notes.txt", "b.txt"), () -> expand("ls \"$@\"", vars, argv));
        check("unquoted dollar-star splits them", List.of("ls", "my", "notes.txt", "b.txt"), () -> expand("ls $*", vars, argv));
        check("an empty unquoted value gives no word", List.of("ls"), () -> expand("ls $empty", vars, argv));
        check("an empty quoted value gives one empty word", List.of("ls", ""), () -> expand("ls \"$empty\"", vars, argv));
        check("an unset variable is an error", "nope: unbound variable", () -> {
            try { expand("cd \"$nope\"", vars, argv); return "no error"; }
            catch (IllegalStateException e) { return e.getMessage(); }
        });
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
