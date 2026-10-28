import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// Exercise solution: the same Leitner rules as Review.java, printed as a day-by-day agenda.
public class ReviewAgenda {
    static final List<Integer> INTERVALS = List.of(1, 3, 7, 14, 30);

    enum Rating { SOLID, NEEDED_DOCS, STUCK }

    static int nextBox(int box, Rating r) {
        return switch (r) {
            case SOLID -> Math.min(box + 1, INTERVALS.size());
            case NEEDED_DOCS -> box;
            case STUCK -> 1;
        };
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 11, 2);
        Map<String, Rating> ratings = Map.of("concurrency", Rating.STUCK, "databases", Rating.SOLID,
                                             "system-design", Rating.NEEDED_DOCS, "networks-and-web", Rating.SOLID);
        Map<LocalDate, List<String>> byDay = ratings.entrySet().stream().collect(Collectors.groupingBy(
            e -> today.plusDays(INTERVALS.get(nextBox(2, e.getValue()) - 1)), TreeMap::new,
            Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        byDay.forEach((day, sheets) -> System.out.println(
            day.getDayOfWeek().name().substring(0, 3) + " " + day + ": " + String.join(", ", sheets.stream().sorted().toList())));
    }
}
