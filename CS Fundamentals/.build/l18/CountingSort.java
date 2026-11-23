import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CountingSort {
    record Student(String name, char grade) {}

    // Counting sort by grade 'A'..'F'. It never compares two students:
    // the grade itself is used as an array index.
    static List<Student> countingSortByGrade(List<Student> xs) {
        int k = 'F' - 'A' + 1;                                   // 6 possible keys
        int[] count = new int[k + 1];
        for (Student s : xs) count[s.grade() - 'A' + 1]++;       // 1. count each key
        for (int g = 1; g <= k; g++) count[g] += count[g - 1];   // 2. prefix sums: count[g] = first slot for key g
        Student[] out = new Student[xs.size()];
        for (Student s : xs) out[count[s.grade() - 'A']++] = s;  // 3. place, left to right: stable
        return new ArrayList<>(List.of(out));
    }

    public static void main(String[] args) {
        List<Student> students = List.of(
            new Student("Ada", 'B'), new Student("Ben", 'A'), new Student("Cy", 'B'),
            new Student("Di", 'A'), new Student("Eve", 'C'), new Student("Flo", 'A'));
        System.out.println(countingSortByGrade(students).stream()
            .map(s -> s.grade() + ":" + s.name()).collect(Collectors.joining(" ")));
    }
}
