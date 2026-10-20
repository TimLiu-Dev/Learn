import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Stability {
    record Student(String name, char grade) {}

    // Same Lomuto quicksort as before, made generic with a Comparator.
    static <T> void quickSortBy(List<T> a, Comparator<? super T> cmp, int lo, int hi) {
        if (lo >= hi) return;
        T pivot = a.get(hi);
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (cmp.compare(a.get(j), pivot) < 0) { Collections.swap(a, i, j); i++; }
        }
        Collections.swap(a, i, hi);
        quickSortBy(a, cmp, lo, i - 1);
        quickSortBy(a, cmp, i + 1, hi);
    }

    static void show(String label, List<Student> xs) {
        System.out.println(String.format("%-10s", label)
            + xs.stream().map(s -> s.grade() + ":" + s.name()).collect(Collectors.joining(" ")));
    }

    public static void main(String[] args) {
        // Already in alphabetical order by name.
        List<Student> students = List.of(
            new Student("Ada", 'B'), new Student("Ben", 'A'), new Student("Cy", 'B'),
            new Student("Di", 'A'), new Student("Eve", 'C'), new Student("Flo", 'A'));
        show("input", students);
        List<Student> stable = new ArrayList<>(students);
        stable.sort(Comparator.comparing(Student::grade));        // JDK: stable
        show("stable", stable);
        List<Student> q = new ArrayList<>(students);
        quickSortBy(q, Comparator.comparing(Student::grade), 0, q.size() - 1);   // ours: not stable
        show("quick", q);
    }
}
