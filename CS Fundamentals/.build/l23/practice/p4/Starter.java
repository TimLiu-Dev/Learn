import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Practice {
    // "at CLASS.METHOD(FILE:LINE)"
    static final Pattern FRAME = Pattern.compile("at (.+)\\.([^.]+)\\(([^:]+):(\\d+)\\)");
    static final Pattern RANGE = Pattern.compile("^(\\d+):(\\d+):");

    static String retrace(String mapping, String frame) {
        Matcher f = FRAME.matcher(frame);
        if (!f.matches()) return frame;
        String obfClass = f.group(1), obfMethod = f.group(2), file = f.group(3);
        int line = Integer.parseInt(f.group(4));
        throw new UnsupportedOperationException("TODO: find the class line for " + obfClass + ", then the method line that owns line " + line);
    }

    // CHECKS
    static final String MAPPING = """
        com.example.shelf.LoanDesk -> a.b.a:
            java.util.Map loans -> a
            10:14:void lend(java.lang.String,int) -> a
            15:22:boolean giveBack(int) -> a
            23:30:int count() -> b
        com.example.shelf.MainActivity -> a.b.b:
            void onCreate(android.os.Bundle) -> a
        """;

    public static void main(String[] args) {
        check("class and method names come back", "at com.example.shelf.LoanDesk.count(SourceFile:25)", () -> retrace(MAPPING, "at a.b.a.b(SourceFile:25)"));
        check("the line range picks lend", "at com.example.shelf.LoanDesk.lend(SourceFile:12)", () -> retrace(MAPPING, "at a.b.a.a(SourceFile:12)"));
        check("the line range picks giveBack", "at com.example.shelf.LoanDesk.giveBack(SourceFile:17)", () -> retrace(MAPPING, "at a.b.a.a(SourceFile:17)"));
        check("a field named a is not a method named a", "at com.example.shelf.MainActivity.onCreate(SourceFile:40)", () -> retrace(MAPPING, "at a.b.b.a(SourceFile:40)"));
        check("classes R8 never renamed stay as they are", "at java.util.HashMap.get(HashMap.java:556)", () -> retrace(MAPPING, "at java.util.HashMap.get(HashMap.java:556)"));
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
