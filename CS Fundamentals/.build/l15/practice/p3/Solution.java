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

        // No delegates: the compare-and-mark is repeated in every setter, name typed by hand.
        String getTitle() { return title; }
        void setTitle(String v) { if (!v.equals(title)) dirty.add("title"); title = v; }
        String getBody() { return body; }
        void setBody(String v) { if (!v.equals(body)) dirty.add("body"); body = v; }
        int getWords() { return words; }
        void setWords(int v) { if (v != words) dirty.add("words"); words = v; }

        String save() {
            String message = dirty.isEmpty() ? "nothing to save" : "saved " + String.join(", ", new TreeSet<>(dirty));
            dirty.clear();
            return message;
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
