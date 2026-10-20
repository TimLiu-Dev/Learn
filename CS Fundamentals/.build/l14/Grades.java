import java.util.*;
import static java.util.stream.Collectors.*;

public class Grades {
    record Score(String student, String subject, int points) {}

    static final List<Score> scores = List.of(
        new Score("Ada", "math", 92), new Score("Ada", "art", 71), new Score("Lin", "math", 48),
        new Score("Lin", "art", 88), new Score("Sam", "math", 75), new Score("Sam", "art", 45),
        new Score("Kim", "math", 81));

    public static void main(String[] args) {
        // 1.
        System.out.println(scores.stream().filter(s -> s.points() < 50)
            .map(Score::student).distinct().sorted().toList());
        // 2.
        Map<String, Double> avg = scores.stream().collect(
            groupingBy(Score::subject, LinkedHashMap::new, averagingInt(Score::points)));
        System.out.println(avg);
        // 3.
        Map<String, Integer> totals = scores.stream().collect(
            groupingBy(Score::student, summingInt(Score::points)));
        System.out.println(totals.entrySet().stream()
            .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null));
        // 4.
        System.out.println(scores.stream().filter(s -> s.subject().equals("math"))
            .sorted(Comparator.comparingInt(Score::points).reversed())
            .map(s -> s.student() + " " + s.points()).collect(joining(", ")));
    }
}
