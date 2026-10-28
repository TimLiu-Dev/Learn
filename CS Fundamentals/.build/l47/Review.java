import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Review {
    // Leitner boxes: a sheet you know well moves to a box you review less often.
    static final List<Integer> INTERVAL_DAYS = List.of(1, 3, 7, 14, 30);   // box 1..5

    enum Rating { SOLID, NEEDED_DOCS, STUCK }

    record Sheet(String name, int box, LocalDate due) {}

    static Sheet review(Sheet sheet, Rating rating, LocalDate today) {
        int box = switch (rating) {
            case SOLID -> Math.min(sheet.box() + 1, INTERVAL_DAYS.size());
            case NEEDED_DOCS -> sheet.box();
            case STUCK -> 1;
        };
        return new Sheet(sheet.name(), box, today.plusDays(INTERVAL_DAYS.get(box - 1)));
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 11, 2);
        Map<String, Rating> ratings = new LinkedHashMap<>();
        ratings.put("kotlin-java-basics", Rating.SOLID);
        ratings.put("memory-and-equality", Rating.NEEDED_DOCS);
        ratings.put("generics-and-functions", Rating.STUCK);
        ratings.put("concurrency", Rating.STUCK);
        ratings.put("databases", Rating.SOLID);
        ratings.put("system-design", Rating.NEEDED_DOCS);
        List<Sheet> sheets = ratings.keySet().stream()
            .map(name -> review(new Sheet(name, 2, today), ratings.get(name), today))
            .sorted(Comparator.comparing(Sheet::due).thenComparing(Sheet::name))
            .toList();
        sheets.forEach(s -> System.out.println(s.due() + "  box " + s.box() + "  " + s.name()));
        System.out.println("due within a week: " + sheets.stream().filter(s -> !s.due().isAfter(today.plusDays(7))).count());
    }
}
