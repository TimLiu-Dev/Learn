import java.util.*;
import java.util.zip.CRC32;

public class Practice {
    // A fixed position on the ring for any string: its CRC32, a number from 0 to 2^32 - 1.
    static long position(String s) {
        CRC32 crc = new CRC32();
        crc.update(s.getBytes());
        return crc.getValue();
    }

    static class Ring {
        private final int vnodes;
        private final TreeMap<Long, String> ring = new TreeMap<>();   // position -> node

        Ring(int vnodes) { this.vnodes = vnodes; }

        // Node "n1" with 3 vnodes sits at position("n1#0"), position("n1#1") and position("n1#2").
        void add(String node) {
            throw new UnsupportedOperationException("TODO: put the node at each of its vnode positions");
        }

        void remove(String node) {
            throw new UnsupportedOperationException("TODO: take the node off each of its positions");
        }

        // Clockwise from the key to the first node position; past the end, wrap round to the start.
        String nodeFor(String key) {
            throw new UnsupportedOperationException("TODO: ceilingEntry finds the next position; what if there is none?");
        }
    }

    // CHECKS
    static final List<String> FOUR = List.of("n1", "n2", "n3", "n4");
    static final List<String> KEYS = new ArrayList<>();
    static { for (int i = 0; i < 10_000; i++) KEYS.add("user:" + i); }

    static Ring ringOf(int vnodes, List<String> nodes) {
        Ring r = new Ring(vnodes);
        nodes.forEach(r::add);
        return r;
    }

    static Map<String, String> owners(Ring r) {
        Map<String, String> m = new HashMap<>();
        for (String k : KEYS) m.put(k, r.nodeFor(k));
        return m;
    }

    public static void main(String[] args) {
        check("with one node, that node owns every key", Set.of("n1"), () -> {
            Ring r = ringOf(3, List.of("n1"));
            Set<String> seen = new HashSet<>();
            for (String k : KEYS.subList(0, 100)) seen.add(r.nodeFor(k));
            return seen;
        });
        check("a key belongs to the first node position at or after it", "all 1000 agree", () -> {
            Ring r = ringOf(10, FOUR);
            TreeMap<Long, String> positions = new TreeMap<>();
            for (String n : FOUR) for (int i = 0; i < 10; i++) positions.put(position(n + "#" + i), n);
            for (String k : KEYS.subList(0, 1000)) {
                long h = position(k);
                String expected = null;
                for (Map.Entry<Long, String> e : positions.entrySet()) if (e.getKey() >= h) { expected = e.getValue(); break; }
                if (expected == null) expected = positions.firstEntry().getValue();
                if (!expected.equals(r.nodeFor(k))) return k + " went to " + r.nodeFor(k) + ", not " + expected;
            }
            return "all 1000 agree";
        });
        check("a key past the last position wraps round to the first", "k0 -> n1", () -> {
            Ring r = ringOf(10, FOUR);
            return "k0 -> " + r.nodeFor("k0");    // k0's position is past every node position
        });
        check("adding n5 moves keys only onto n5", "2110 moved, all to n5", () -> {
            Ring r = ringOf(50, FOUR);
            Map<String, String> before = owners(r);
            r.add("n5");
            int moved = 0;
            boolean allToN5 = true;
            for (String k : KEYS) {
                String now = r.nodeFor(k);
                if (!now.equals(before.get(k))) { moved++; allToN5 &= now.equals("n5"); }
            }
            return moved + " moved, " + (allToN5 ? "all to n5" : "some between old nodes");
        });
        check("removing n3 moves only n3's keys", "2201 moved, all were n3's", () -> {
            Ring r = ringOf(50, FOUR);
            Map<String, String> before = owners(r);
            r.remove("n3");
            int moved = 0;
            boolean allN3 = true;
            for (String k : KEYS) {
                if (!r.nodeFor(k).equals(before.get(k))) { moved++; allN3 &= before.get(k).equals("n3"); }
            }
            return moved + " moved, " + (allN3 ? "all were n3's" : "some were not n3's");
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
