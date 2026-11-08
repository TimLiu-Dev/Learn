import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class GradeBook {
        private final List<Integer> scores;

        GradeBook(List<Integer> scores) { this.scores = scores; }   // keeps the caller's list

        void add(int score) { scores.add(score); }

        List<Integer> all() { return scores; }                      // hands out the real list
    }

    // CHECKS
    public static void main(String[] args) {
        check("the book starts with the given scores", List.of(70, 80), () -> new GradeBook(new ArrayList<>(List.of(70, 80))).all());
        check("add still works", List.of(70, 80, 90), () -> {
            GradeBook book = new GradeBook(new ArrayList<>(List.of(70, 80)));
            book.add(90);
            return book.all();
        });
        check("the caller's later changes don't reach the book", List.of(70, 80), () -> {
            List<Integer> start = new ArrayList<>(List.of(70, 80));
            GradeBook book = new GradeBook(start);
            start.add(100);
            return book.all();
        });
        check("a list you got earlier doesn't change", List.of(70, 80), () -> {
            GradeBook book = new GradeBook(new ArrayList<>(List.of(70, 80)));
            List<Integer> earlier = book.all();
            book.add(90);
            return earlier;
        });
        check("outside code can't change the book", List.of(70, 80), () -> {
            GradeBook book = new GradeBook(new ArrayList<>(List.of(70, 80)));
            try { book.all().add(0); } catch (UnsupportedOperationException refused) { }
            return book.all();
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
