import java.util.*;
import java.util.function.BiFunction;
import static java.util.stream.Collectors.*;

public class Report {
    record Tx(int id, String customer, String category, long cents, boolean refunded) {}
    record Tally(int count, long cents) {}

    static final List<Tx> txs = List.of(
        new Tx(1, "ada", "books", 2499, false), new Tx(2, "lin", "games", 5999, false),
        new Tx(3, "ada", "games", 1999, true), new Tx(4, "sam", "books", 1250, false),
        new Tx(5, "kim", "music", 899, false), new Tx(6, "lin", "books", 3400, false),
        new Tx(7, "sam", "games", 4999, false), new Tx(8, "ada", "music", 1299, false),
        new Tx(9, "kim", "books", 2100, true), new Tx(10, "lin", "music", 999, false));

    static String money(long cents) { return String.format("$%d.%02d", cents / 100, cents % 100); }

    static <T, A> A myFold(Iterable<? extends T> items, A initial,
                           BiFunction<? super A, ? super T, ? extends A> combine) {
        A acc = initial;
        for (T item : items) acc = combine.apply(acc, item);
        return acc;
    }

    public static void main(String[] args) {
        List<Tx> kept = txs.stream().filter(t -> !t.refunded()).toList();

        System.out.println("revenue: " + money(kept.stream().mapToLong(Tx::cents).sum()));

        System.out.println("by category:");
        Map<String, Long> byCategory = kept.stream().collect(groupingBy(Tx::category, summingLong(Tx::cents)));
        byCategory.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> System.out.println("  " + e.getKey() + " " + money(e.getValue())));

        Map<String, Long> byCustomer = kept.stream().collect(groupingBy(Tx::customer, summingLong(Tx::cents)));
        byCustomer.entrySet().stream().max(Map.Entry.comparingByValue())
            .ifPresent(e -> System.out.println("top customer: " + e.getKey() + " " + money(e.getValue())));

        System.out.println("refunded customers: " + txs.stream().filter(Tx::refunded)
            .map(Tx::customer).distinct().sorted().toList());

        System.out.println("average by category:");
        Map<String, Double> avg = kept.stream().collect(groupingBy(Tx::category, TreeMap::new, averagingLong(Tx::cents)));
        avg.forEach((category, a) -> System.out.println("  " + category + String.format(" %.2f", a / 100)));

        Tally t = myFold(txs, new Tally(0, 0), (acc, tx) ->
            tx.refunded() ? new Tally(acc.count() + 1, acc.cents() + tx.cents()) : acc);
        System.out.println("refunds: " + t.count() + " transactions, " + money(t.cents()));
    }
}
