import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Practice {
    static class Draft {
        final Set<String> dirty = new HashSet<>();
        private String title = "";
        private String body = "";
        private int words = 0;

        String getTitle() { return title; }
        void setTitle(String v) { throw new UnsupportedOperationException("TODO: store; mark \"title\" dirty if it changed"); }
        String getBody() { return body; }
        void setBody(String v) { throw new UnsupportedOperationException("TODO: store; mark \"body\" dirty if it changed"); }
        int getWords() { return words; }
        void setWords(int v) { throw new UnsupportedOperationException("TODO: store; mark \"words\" dirty if it changed"); }

        String save() {
            throw new UnsupportedOperationException("TODO: \"saved a, b\" sorted, or \"nothing to save\"; then clear dirty");
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("a fresh draft is clean", "|0|[]", () -> { Draft d = new Draft(); return d.getTitle() + "|" + d.getWords() + "|" + d.dirty; });
        check("a new value is stored and marked dirty", "Lazy pipelines|120|[title, words]", () -> {
            Draft d = new Draft();
            d.setTitle("Lazy pipelines");
            d.setWords(120);
            return d.getTitle() + "|" + d.getWords() + "|" + new TreeSet<>(d.dirty);
        });
        check("assigning the same value is not a change", Set.of(), () -> { Draft d = new Draft(); d.setBody(""); d.setWords(0); return d.dirty; });
        check("save lists the changes sorted and starts clean", "saved body, title|nothing to save|nothing to save", () -> {
            Draft d = new Draft();
            d.setTitle("T");
            d.setBody("B");
            String first = d.save();
            String second = d.save();
            d.setTitle("T");
            return first + "|" + second + "|" + d.save();
        });
        check("two drafts don't share values", "|[]", () -> {
            Draft a = new Draft();
            Draft b = new Draft();
            a.setTitle("A");
            return b.getTitle() + "|" + b.dirty;
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
