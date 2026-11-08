import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

// cut -d' ' -f3 requests.log | sort | uniq -c | sort -rn | head -5, as one program
public class TopPaths {
    public static void main(String[] args) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of("requests.log"))) {
            Map<String, Long> counts = lines
                .map(line -> line.split(" ")[2])                                     // cut -d' ' -f3
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));      // sort | uniq -c
            counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())       // sort -rn
                .limit(5)                                                            // head -5
                .forEach(e -> System.out.printf("%4d %s%n", e.getValue(), e.getKey()));
        }
    }
}
