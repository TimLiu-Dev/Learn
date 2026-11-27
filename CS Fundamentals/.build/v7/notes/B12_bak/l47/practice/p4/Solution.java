import java.time.LocalDate;
import java.util.*;

public class Practice {
    record Sheet(String name, LocalDate due) {}

    /** Spreads reviews over days from today: at most perDay a day, most overdue first. */
    static Map<LocalDate, List<String>> plan(List<Sheet> sheets, LocalDate today, int perDay) {
        if (perDay < 1) throw new IllegalArgumentException("perDay must be at least 1, was " + perDay);
        Map<LocalDate, List<String>> days = new TreeMap<>();
        List<Sheet> ordered = new ArrayList<>(sheets);
        ordered.sort(Comparator.comparing(Sheet::due).thenComparing(Sheet::name));
        for (Sheet sheet : ordered) {
            LocalDate day = sheet.due().isBefore(today) ? today : sheet.due();
            while (days.getOrDefault(day, List.of()).size() >= perDay) day = day.plusDays(1);   // full: try the next day
            days.computeIfAbsent(day, d -> new ArrayList<>()).add(sheet.name());
        }
        return days;
    }

    // CHECKS
    static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    static LocalDate day(long n) { return TODAY.plusDays(n); }

    public static void main(String[] args) {
        check("a full day spills over to the next",
            Map.of(day(0), List.of("concurrency"), day(1), List.of("databases"), day(2), List.of("shell")),
            () -> plan(List.of(new Sheet("shell", TODAY), new Sheet("databases", TODAY), new Sheet("concurrency", TODAY)), TODAY, 1));
        check("the most overdue sheet goes first",
            Map.of(day(0), List.of("zebra-tree"), day(1), List.of("algebra")),
            () -> plan(List.of(new Sheet("algebra", TODAY), new Sheet("zebra-tree", TODAY.minusDays(2))), TODAY, 1));
        check("spilled sheets keep their place ahead of later ones",
            Map.of(day(0), List.of("b", "c"), day(1), List.of("d", "a")),
            () -> plan(List.of(new Sheet("b", TODAY), new Sheet("c", TODAY), new Sheet("d", TODAY), new Sheet("a", day(1))), TODAY, 2));
        check("days come back in date order", List.of(day(1), day(5)),
            () -> new ArrayList<>(plan(List.of(new Sheet("late", day(5)), new Sheet("soon", day(1))), TODAY, 3).keySet()));
        check("no day is ever over the limit, and every sheet is planned", "true 20", () -> {
            List<Sheet> sheets = new ArrayList<>();
            for (int i = 1; i <= 20; i++) sheets.add(new Sheet("s" + i, TODAY.plusDays(i % 3)));
            Map<LocalDate, List<String>> p = plan(sheets, TODAY, 3);
            return p.values().stream().allMatch(l -> l.size() <= 3) + " " + p.values().stream().mapToInt(List::size).sum();
        });
        check("a limit below one is rejected", "IllegalArgumentException", () -> {
            try {
                plan(List.of(), TODAY, 0);
                return "no exception";
            } catch (IllegalArgumentException e) {
                return "IllegalArgumentException";
            }
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
