import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    /** The header Git prints above a hunk, such as "@@ -16,10 +16,10 @@". */
    static String hunkHeader(int oldStart, int newStart, List<String> body) {
        throw new UnsupportedOperationException("TODO: count the old and the new lines, then format the two ranges");
    }

    // CHECKS
    public static void main(String[] args) {
        check("context lines count for both files", "@@ -3,6 +3,7 @@",
                () -> hunkHeader(3, 3, List.of(" 3", " 4", " 5", "+NEW", " 6", " 7", " 8")));
        List<String> ai = new ArrayList<>();
        for (int i = 0; i < 11; i++) ai.add(" context " + i);
        for (int i = 0; i < 25; i++) ai.add("+added " + i);
        check("the lesson's AI diff hunk adds up", "@@ -4,11 +4,36 @@", () -> hunkHeader(4, 4, ai));
        check("a one-line change leaves out the counts", "@@ -1 +1 @@", () -> hunkHeader(1, 1, List.of("-a", "+b")));
        check("a no-newline marker counts for neither file", "@@ -1,2 +1,2 @@",
                () -> hunkHeader(1, 1, List.of(" x", "-y", "\\ No newline at end of file", "+z", "\\ No newline at end of file")));
        check("a pure insertion has an empty old range", "@@ -5,0 +6 @@", () -> hunkHeader(5, 6, List.of("+NEW")));
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
