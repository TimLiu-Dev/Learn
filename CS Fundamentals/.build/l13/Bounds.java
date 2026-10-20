import java.util.List;

public class Bounds {
    // T must be comparable to itself, so compareTo is allowed on T
    static <T extends Comparable<T>> T largest(List<T> items) {
        T best = items.get(0);
        for (T item : items) if (item.compareTo(best) > 0) best = item;
        return best;
    }

    // two bounds: join them with &
    static <T extends CharSequence & Comparable<T>> T shortestSorted(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.length() < best.length()
                    || (item.length() == best.length() && item.compareTo(best) < 0)) best = item;
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println(largest(List.of(3, 9, 4)));
        System.out.println(largest(List.of("pear", "apple", "fig")));
        System.out.println(shortestSorted(List.of("kiwi", "fig", "date", "ash")));
    }
}
