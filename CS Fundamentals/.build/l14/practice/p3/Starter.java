import java.util.*;
import java.util.stream.*;

public class Practice {
    record Sale(String day, String item, int qty) { }

    static Map<String, Integer> unitsPerItem(List<Sale> sales) {
        throw new UnsupportedOperationException("TODO: groupingBy item into a LinkedHashMap, summing qty");
    }

    static Optional<String> topItem(List<Sale> sales) {
        throw new UnsupportedOperationException("TODO: reuse unitsPerItem, then max by value");
    }

    // Java's partition: a map from true/false to the two lists
    static Map<Boolean, List<Sale>> splitBySize(List<Sale> sales, int min) {
        throw new UnsupportedOperationException("TODO: Collectors.partitioningBy");
    }

    static List<Integer> changes(List<Integer> totals) {
        throw new UnsupportedOperationException("TODO: IntStream.range over the indexes");
    }

    // CHECKS
    static final List<Sale> WEEK = List.of(
            new Sale("Mon", "tea", 3), new Sale("Mon", "cake", 1),
            new Sale("Tue", "tea", 2), new Sale("Tue", "jam", 6),
            new Sale("Wed", "cake", 4), new Sale("Wed", "tea", 4));

    public static void main(String[] args) throws Exception {
        check("units are totalled per item", Map.of("tea", 9, "cake", 5, "jam", 6), () -> unitsPerItem(WEEK));
        check("units keep first-seen order", List.of("tea", "cake", "jam"), () -> List.copyOf(unitsPerItem(WEEK).keySet()));
        check("the top item has the biggest total, not the biggest sale", Optional.of("tea"), () -> topItem(WEEK));
        check("no sales means no top item", Optional.empty(), () -> topItem(List.of()));
        check("splitBySize: the big sales", List.of("jam", "cake", "tea"), () -> splitBySize(WEEK, 4).get(true).stream().map(Sale::item).toList());
        check("splitBySize: the rest", List.of("tea", "cake", "tea"), () -> splitBySize(WEEK, 4).get(false).stream().map(Sale::item).toList());
        check("changes compares each total with the next", List.of(3, -2), () -> changes(List.of(5, 8, 6)));
        check("one day has nothing to compare", List.of(), () -> changes(List.of(7)));
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
