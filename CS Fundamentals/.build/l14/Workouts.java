import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class Workouts {
    record Workout(String day, String sport, int minutes, int kcal) {}

    static final List<Workout> log = List.of(
        new Workout("Mon", "run", 32, 350), new Workout("Mon", "yoga", 20, 90),
        new Workout("Tue", "bike", 45, 420), new Workout("Wed", "run", 25, 280),
        new Workout("Thu", "swim", 40, 380), new Workout("Fri", "run", 50, 540),
        new Workout("Sat", "bike", 90, 810), new Workout("Sun", "yoga", 30, 130));

    public static void main(String[] args) {
        // 1. filter + map
        System.out.println(log.stream().filter(w -> w.sport().equals("run") && w.minutes() >= 30)
            .map(Workout::day).toList());
        // 2. sum of a property: mapToInt gives an IntStream, which has sum()
        System.out.println(log.stream().mapToInt(Workout::minutes).sum());
        // 3. group, then total each group (LinkedHashMap keeps first-seen order)
        Map<String, Integer> minutesBySport = log.stream().collect(
            groupingBy(Workout::sport, LinkedHashMap::new, summingInt(Workout::minutes)));
        System.out.println(minutesBySport);
        // 4. count per group: counting() produces Long values
        Map<String, Long> sessions = log.stream().collect(
            groupingBy(Workout::sport, LinkedHashMap::new, counting()));
        System.out.println(sessions);
        // 5. the max by some property: an Optional, empty if the stream is empty
        System.out.println(log.stream().max(Comparator.comparingInt(Workout::kcal))
            .map(w -> w.day() + " " + w.sport()).orElse(null));
        // 6. sort by two keys, keep the top 3
        System.out.println(log.stream()
            .sorted(Comparator.comparingInt(Workout::minutes).reversed().thenComparing(Workout::day))
            .limit(3).map(w -> w.sport() + ":" + w.minutes()).toList());
        // 7. split in two: a Map<Boolean, List<Workout>>
        Map<Boolean, List<Workout>> parts = log.stream().collect(partitioningBy(w -> w.minutes() >= 40));
        System.out.println(parts.get(true).size() + " long, " + parts.get(false).size() + " short");
        // 8. yes/no questions, and joining
        System.out.println(log.stream().anyMatch(w -> w.sport().equals("swim")) + " "
            + log.stream().allMatch(w -> w.minutes() >= 20) + " "
            + log.stream().noneMatch(w -> w.kcal() > 1000));
        System.out.println(log.stream().map(Workout::sport).distinct().sorted()
            .collect(joining(", ", "[", "]")));
        // 9. first match: an Optional
        System.out.println(log.stream().filter(w -> w.sport().equals("rowing")).findFirst());
    }
}
