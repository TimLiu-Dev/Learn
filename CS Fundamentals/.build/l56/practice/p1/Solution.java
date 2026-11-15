import java.util.*;

public class Practice {
    // Part 1's store, with the file modelled as a list of records: "put key value" or "del key".
    // Keys contain no spaces. A key's offset is the record's position in the list.
    static class MiniCask {
        final List<String> log;
        private final Map<String, Integer> index = new HashMap<>();   // key -> offset of its newest record

        MiniCask() { this(new ArrayList<>()); }

        MiniCask(List<String> log) {                   // reopening: replay every record, oldest first
            this.log = log;
            for (int offset = 0; offset < log.size(); offset++) {
                String[] parts = log.get(offset).split(" ", 3);
                if (parts[0].equals("put")) index.put(parts[1], offset); else index.remove(parts[1]);
            }
        }

        int liveKeys() { return index.size(); }

        void put(String key, String value) {
            log.add("put " + key + " " + value);
            index.put(key, log.size() - 1);
        }

        String get(String key) {
            Integer offset = index.get(key);
            if (offset == null) return null;
            return log.get(offset).split(" ", 3)[2];
        }

        void delete(String key) {
            if (!index.containsKey(key)) return;
            log.add("del " + key);                     // the tombstone: replay must see the delete too
            index.remove(key);
        }

        void compact() {
            Map<String, String> live = new HashMap<>();
            index.forEach((key, offset) -> live.put(key, log.get(offset)));
            log.clear();
            for (Map.Entry<String, String> e : live.entrySet()) {
                log.add(e.getValue());
                index.put(e.getKey(), log.size() - 1);   // every offset changed
            }
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("put then get", "Herbert", () -> { MiniCask db = new MiniCask(); db.put("dune", "Herbert"); return db.get("dune"); });
        check("a missing key is null", null, () -> new MiniCask().get("emma"));
        check("an overwrite appends and the newest value wins", "Frank Herbert [put dune Herbert, put dune Frank Herbert]", () -> {
            MiniCask db = new MiniCask(); db.put("dune", "Herbert"); db.put("dune", "Frank Herbert");
            return db.get("dune") + " " + db.log;
        });
        check("a delete survives reopening", null, () -> {
            MiniCask db = new MiniCask(); db.put("emma", "Austen"); db.delete("emma");
            return new MiniCask(db.log).get("emma");
        });
        check("compact keeps one record per live key", "[put a 3, put c 4] 3 null 4", () -> {
            MiniCask db = new MiniCask();
            db.put("a", "1"); db.put("b", "2"); db.put("a", "3"); db.put("c", "4"); db.delete("b");
            db.compact();
            List<String> sorted = new ArrayList<>(db.log); Collections.sort(sorted);
            return sorted + " " + db.get("a") + " " + db.get("b") + " " + db.get("c");
        });
        check("reads and writes still work after a compaction", "new 1 2 | 3 new", () -> {
            MiniCask db = new MiniCask();
            db.put("x", "old"); db.put("y", "1"); db.put("x", "new");
            db.compact();
            db.put("z", "2");
            MiniCask reopened = new MiniCask(db.log);
            return db.get("x") + " " + db.get("y") + " " + db.get("z") + " | " + reopened.liveKeys() + " " + reopened.get("x");
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
