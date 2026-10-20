import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordPipeline {
    static final String TEXT = """
        The cat sat on the mat. The mat was flat,
        and the cat was fat! A cat, a mat, a hat.
        """;

    public static void main(String[] args) {
        Map<String, Long> counts = Pattern.compile("\\p{L}+")          // a run of letters
            .matcher(TEXT.toLowerCase())
            .results()                                                  // Stream<MatchResult>, lazy
            .map(MatchResult::group)
            .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));

        System.out.println("total words: " + counts.values().stream().mapToLong(Long::longValue).sum());
        System.out.println("unique words: " + counts.size());

        Map.Entry<String, Long> top = counts.entrySet().stream()
            .max(Map.Entry.<String, Long>comparingByValue()
                .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
            .orElseThrow();
        System.out.println("most frequent: " + top.getKey() + " (" + top.getValue() + ")");

        String repeated = counts.entrySet().stream()                    // TreeMap: already sorted
            .filter(e -> e.getValue() >= 2)
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(" "));
        System.out.println("repeated: " + repeated);
    }
}
