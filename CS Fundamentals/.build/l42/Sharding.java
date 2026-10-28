import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sharding {
    // A well-mixed 64-bit hash of a string: the first 8 bytes of its MD5 digest.
    static long hash64(String s) {
        try {
            byte[] d = MessageDigest.getInstance("MD5").digest(s.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(d).getLong();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    static String modShard(String key, int n) { return "n" + (Math.floorMod(hash64(key), n) + 1); }

    // Consistent hashing: nodes and keys share one ring of 64-bit positions.
    static class Ring {
        private final int vnodes;
        private final TreeMap<Long, String> ring = new TreeMap<>();     // ring position -> node
        Ring(int vnodes) { this.vnodes = vnodes; }
        void add(String node) { for (int i = 0; i < vnodes; i++) ring.put(hash64(node + "#" + i), node); }
        void remove(String node) { for (int i = 0; i < vnodes; i++) ring.remove(hash64(node + "#" + i)); }
        // Walk clockwise from the key's position to the first node position (wrap around at the end).
        String nodeFor(String key) {
            var e = ring.ceilingEntry(hash64(key));
            return (e != null ? e : ring.firstEntry()).getValue();
        }
    }

    public static void main(String[] args) {
        List<String> keys = IntStream.range(0, 100_000).mapToObj(i -> "user:" + i).toList();

        long moved = keys.stream().filter(k -> !modShard(k, 4).equals(modShard(k, 5))).count();
        System.out.printf("hash mod N, 4 -> 5 nodes: %,d of %,d keys move (%.0f%%)%n", moved, keys.size(), 100.0 * moved / keys.size());

        for (int vnodes : new int[] { 1, 100 }) {
            Ring ring = new Ring(vnodes);
            List.of("n1", "n2", "n3", "n4").forEach(ring::add);
            Map<String, String> before = keys.stream().collect(Collectors.toMap(Function.identity(), ring::nodeFor));
            var load = new TreeMap<>(before.values().stream().collect(Collectors.groupingBy(n -> n, Collectors.counting())));
            ring.add("n5");
            Map<String, String> after = keys.stream().collect(Collectors.toMap(Function.identity(), ring::nodeFor));
            List<String> changed = keys.stream().filter(k -> !before.get(k).equals(after.get(k))).toList();
            System.out.printf("ring, %3d vnode(s) per node: load %s%n", vnodes, load);
            System.out.printf("   add n5: %,d keys move (%.0f%%), all of them to n5: %s%n",
                changed.size(), 100.0 * changed.size() / keys.size(), changed.stream().allMatch(k -> after.get(k).equals("n5")));
        }
    }
}
