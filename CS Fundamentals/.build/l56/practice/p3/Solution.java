import java.util.*;

public class Practice {
    // Part 3's LSM tree, with the SSTables kept in memory. A null value is a tombstone.
    static class MiniLsm {
        private final int memLimit;
        private TreeMap<String, String> mem = new TreeMap<>();
        final List<TreeMap<String, String>> runs = new ArrayList<>();   // newest first

        MiniLsm(int memLimit) { this.memLimit = memLimit; }

        void put(String key, String value) { write(key, value); }
        void delete(String key) { write(key, null); }

        private void write(String key, String value) {
            mem.put(key, value);
            if (mem.size() >= memLimit) flush();
        }

        void flush() {
            if (mem.isEmpty()) return;
            runs.add(0, mem);                    // the run keeps this map, so start a new one, don't clear it
            mem = new TreeMap<>();
        }

        String get(String key) {
            if (mem.containsKey(key)) return mem.get(key);
            for (TreeMap<String, String> run : runs) {
                if (run.containsKey(key)) return run.get(key);   // a tombstone (null) here hides every older run
            }
            return null;
        }

        void compact() {
            flush();
            TreeMap<String, String> merged = mergeRuns(runs, true);
            runs.clear();
            runs.add(merged);
        }
    }

    // runs.get(0) is the newest. Returns one sorted run holding the newest version of every key.
    static TreeMap<String, String> mergeRuns(List<TreeMap<String, String>> runs, boolean dropTombstones) {
        TreeMap<String, String> merged = new TreeMap<>();
        for (TreeMap<String, String> run : runs.reversed()) merged.putAll(run);   // oldest first; newer overwrite
        if (dropTombstones) merged.values().removeIf(Objects::isNull);
        return merged;
    }

    // CHECKS
    static TreeMap<String, String> sorted(String... kv) {     // "a", "1", "c", null, ...
        TreeMap<String, String> m = new TreeMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }

    public static void main(String[] args) {
        check("a full memtable becomes the newest run", "[{a=1, b=2, c=3}] 4", () -> {
            MiniLsm db = new MiniLsm(3);
            db.put("c", "3"); db.put("a", "1"); db.put("b", "2"); db.put("d", "4");
            return db.runs + " " + db.get("d");
        });
        check("the newest version wins across runs", "2 new 1", () -> {
            MiniLsm db = new MiniLsm(3);
            db.put("a", "old"); db.put("b", "1"); db.put("c", "2");
            db.put("a", "new"); db.put("x", "3"); db.put("y", "4");
            return db.runs.size() + " " + db.get("a") + " " + db.get("b");
        });
        check("a tombstone hides every older value", "null null 3", () -> {
            MiniLsm db = new MiniLsm(3);
            db.put("a", "1"); db.put("b", "2"); db.put("c", "3");
            db.delete("a"); db.put("x", "4"); db.put("y", "5");
            db.delete("b");
            return db.get("a") + " " + db.get("b") + " " + db.get("c");
        });
        check("mergeRuns keeps the newest version of each key", "{a=new, b=b1, c=null}",
            () -> mergeRuns(List.of(sorted("a", "new", "c", null), sorted("a", "old", "b", "b1", "c", "c1")), false).toString());
        check("a full merge drops tombstones and the values they hide", "{a=new, b=b1}",
            () -> mergeRuns(List.of(sorted("a", "new", "c", null), sorted("a", "old", "b", "b1", "c", "c1")), true).toString());
        check("compaction changes no answer", "1 run, 0 wrong", () -> {
            MiniLsm db = new MiniLsm(4);
            Map<String, String> truth = new HashMap<>();
            for (int i = 0; i < 40; i++) {
                String key = "k" + (i * 7 % 11);
                if (i % 5 == 4) { db.delete(key); truth.remove(key); } else { db.put(key, "v" + i); truth.put(key, "v" + i); }
            }
            db.compact();
            int wrong = 0;
            for (int k = 0; k < 11; k++) if (!Objects.equals(truth.get("k" + k), db.get("k" + k))) wrong++;
            return db.runs.size() + " run, " + wrong + " wrong";
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
