import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Uses Sharding.Ring and Sharding.modShard: run with `java Ex2.java` next to Sharding.java
public class Ex2 {
    public static void main(String[] args) {
        List<String> keys = IntStream.range(0, 100_000).mapToObj(i -> "user:" + i).toList();
        var ring = new Sharding.Ring(100);
        List.of("n1", "n2", "n3", "n4").forEach(ring::add);
        Map<String, String> before = keys.stream().collect(Collectors.toMap(Function.identity(), ring::nodeFor));
        ring.remove("n3");                                       // n3 crashed for good
        Map<String, String> after = keys.stream().collect(Collectors.toMap(Function.identity(), ring::nodeFor));
        List<String> changed = keys.stream().filter(k -> !before.get(k).equals(after.get(k))).toList();
        System.out.printf("ring: n3 removed, %,d keys move; all were on n3: %s%n",
            changed.size(), changed.stream().allMatch(k -> before.get(k).equals("n3")));
        System.out.println("      where n3's keys went: " + new TreeMap<>(changed.stream()
            .collect(Collectors.groupingBy(after::get, Collectors.counting()))));
        long modMoved = keys.stream().filter(k -> !Sharding.modShard(k, 4).equals(Sharding.modShard(k, 3))).count();
        System.out.printf("mod N, 4 -> 3 nodes: %,d keys move%n", modMoved);
    }
}
