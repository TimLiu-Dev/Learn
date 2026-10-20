import java.util.List;

public class Ex1 {
    static class Duo<A, B> {
        final A first;
        final B second;
        Duo(A first, B second) { this.first = first; this.second = second; }
        Duo<B, A> swap() { return new Duo<>(second, first); }
        @Override public String toString() { return "(" + first + ", " + second + ")"; }
    }

    static <T> T lastOrNull(List<T> items) {
        return items.isEmpty() ? null : items.get(items.size() - 1);
    }

    static <T extends Comparable<T>> boolean isSorted(List<T> items) {
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i - 1).compareTo(items.get(i)) > 0) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(lastOrNull(List.of("a", "b", "c")));
        System.out.println(lastOrNull(List.<String>of()));
        Duo<Integer, String> d = new Duo<>(1, "one");
        System.out.println(d + " -> " + d.swap());
        System.out.println(isSorted(List.of(1, 2, 2, 5)) + " " + isSorted(List.of("b", "a"))
                + " " + isSorted(List.<Integer>of()));
    }
}
