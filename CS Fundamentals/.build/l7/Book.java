import java.util.HashSet;
import java.util.List;

public class Book {
    record Novel(String title, String author, int year) {
        Novel {
            if (title.isBlank()) throw new IllegalArgumentException("title must not be blank");
            if (year < 1450) throw new IllegalArgumentException("no printed books before 1450: " + year);
        }

        Novel withYear(int newYear) { return new Novel(title, author, newYear); }
    }

    public static void main(String[] args) {
        Novel draft = new Novel("Kotlin by Hand", "A. Learner", 2025);
        Novel fixed = draft.withYear(2026);
        System.out.println(draft);
        System.out.println(fixed);
        System.out.println(draft.equals(fixed));
        System.out.println(fixed.title() + " (" + fixed.year() + ")");
        System.out.println(new HashSet<>(List.of(fixed, new Novel("Kotlin by Hand", "A. Learner", 2026))).size());
        try {
            new Novel(" ", "Nobody", 2000);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
