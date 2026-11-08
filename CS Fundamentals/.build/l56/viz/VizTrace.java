import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// Generates the logviz widget's steps from the real LogKV and SsTable code.
// Run from .build/l56: java -cp <dir with compiled LogKV, Bloom, Lsm, VizTrace> VizTrace > viz/logviz.json
public class VizTrace {
    static StringBuilder out = new StringBuilder();

    static String q(String s) { return s == null ? "null" : "\"" + s.replace("\"", "\\\"") + "\""; }

    // ── mode 1: the log and its index ──
    record Cell(String k, String v, long off, int size) {}
    static List<Cell> log = new ArrayList<>();
    static Map<String, Long> live = new TreeMap<>();

    static String logStep(Long hl, String note) {
        StringBuilder s = new StringBuilder("{\"log\":[");
        for (int i = 0; i < log.size(); i++) {
            Cell c = log.get(i);
            boolean dead = !Objects.equals(live.get(c.k()), c.off()) || c.v() == null;
            s.append(i > 0 ? "," : "").append(String.format("{\"k\":%s,\"v\":%s,\"off\":%d,\"size\":%d,\"dead\":%b}",
                    q(c.k()), q(c.v()), c.off(), c.size(), dead));
        }
        s.append("],\"index\":[");
        int j = 0;
        for (var e : live.entrySet()) s.append(j++ > 0 ? "," : "").append(String.format("[%s,%d]", q(e.getKey()), e.getValue()));
        return s.append(String.format("],\"hl\":%s,\"note\":%s}", hl == null ? "null" : hl.toString(), q(note))).toString();
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("viz");
        List<String> logSteps = new ArrayList<>();
        try (LogKV db = new LogKV(dir.resolve("data.log"))) {
            String[][] ops = { {"put", "dune", "Herbert"}, {"put", "emma", "Austen"}, {"put", "dune", "F. Herbert"},
                               {"del", "emma", null}, {"get", "dune", null}, {"put", "solaris", "Lem"} };
            for (String[] op : ops) {
                long off = db.fileSize();
                switch (op[0]) {
                    case "put" -> {
                        db.put(op[1], op[2]);
                        boolean over = live.containsKey(op[1]);
                        log.add(new Cell(op[1], op[2], off, (int) (db.fileSize() - off)));
                        live.put(op[1], off);
                        logSteps.add(logStep(off, over
                            ? "put(" + op[1] + ") again: a <b>new</b> record is appended at offset " + off + ". The index now points there; the old record is garbage, still on disk."
                            : "put(" + op[1] + "): append " + (db.fileSize() - off) + " bytes at offset " + off + " (12-byte header + key + value), then point the index at it."));
                    }
                    case "del" -> {
                        db.delete(op[1]);
                        log.add(new Cell(op[1], null, off, (int) (db.fileSize() - off)));
                        live.remove(op[1]);
                        logSteps.add(logStep(off, "delete(" + op[1] + "): append a <b>tombstone</b> (value length −1) and drop the key from the index. Nothing is erased in place."));
                    }
                    case "get" -> {
                        long at = live.get(op[1]);
                        logSteps.add(logStep(at, "get(" + op[1] + "): one hash lookup gives offset " + at + ", then one read at that offset: \"" + db.get(op[1]) + "\"."));
                    }
                }
            }
            long before = db.fileSize(), garbage = db.deadBytes();
            db.compact();
            log.clear();
            for (var e : db.indexSnapshot().entrySet()) {
                Loc loc = e.getValue();
                log.add(new Cell(e.getKey(), db.get(e.getKey()), loc.offset(), loc.size()));
                live.put(e.getKey(), loc.offset());
            }
            logSteps.add(logStep(null, "compact(): copy only the live records into a new file and rename it over the old one. " +
                    before + " bytes → " + db.fileSize() + "; the " + garbage + " bytes of garbage are gone."));
        }

        // ── mode 2: an LSM tree with a tiny memtable ──
        List<String> lsmSteps = new ArrayList<>();
        TreeMap<String, String> mem = new TreeMap<>();
        List<Lsm.SsTable> tables = new ArrayList<>();          // newest first
        List<String> names = new ArrayList<>();
        int[] next = {0};
        Lsm.Stats stats = new Lsm.Stats();

        class Snap {
            String take(Map<Integer, String> marks, String note) throws Exception {
                StringBuilder s = new StringBuilder("{\"mem\":[");
                int i = 0;
                for (var e : mem.entrySet()) s.append(i++ > 0 ? "," : "").append("[").append(q(e.getKey())).append(",").append(q(e.getValue())).append("]");
                s.append("],\"tables\":[");
                for (int t = 0; t < tables.size(); t++) {
                    s.append(t > 0 ? "," : "").append("{\"name\":").append(q(names.get(t))).append(",\"mark\":").append(q(marks.get(t))).append(",\"rows\":[");
                    int r = 0;
                    for (Lsm.Entry e : tables.get(t).entries("")) s.append(r++ > 0 ? "," : "").append("[").append(q(e.key())).append(",").append(q(e.value())).append("]");
                    s.append("]}");
                }
                return s.append("],\"note\":").append(q(note)).append("}").toString();
            }
            void flush() throws Exception {
                List<Map.Entry<String, String>> sorted = new ArrayList<>();
                mem.forEach((k, v) -> sorted.add(new AbstractMap.SimpleEntry<>(k, v)));
                String name = String.format("sst-%04d", next[0]++);
                tables.addFirst(Lsm.SsTable.write(dir.resolve(name), sorted));
                names.addFirst(name);
                mem.clear();
            }
            // Probe each table newest first, exactly as Lsm.get does, and record what happened to each.
            String get(String key) throws Exception {
                if (mem.containsKey(key)) {
                    String v = mem.get(key);
                    return take(Map.of(), "get(" + key + "): found in the memtable" + (v == null ? " as a <b>tombstone</b>, so the answer is null. Older tables are never asked." : ": \"" + v + "\". No disk read at all."));
                }
                Map<Integer, String> marks = new HashMap<>();
                StringBuilder why = new StringBuilder("get(" + key + "): not in the memtable. ");
                for (int t = 0; t < tables.size(); t++) {
                    long skips = stats.bloomSkips;
                    Lsm.Found f = tables.get(t).lookup(key, stats, true);
                    if (stats.bloomSkips > skips) { marks.put(t, "skip"); why.append(names.get(t)).append(": Bloom filter says no, file not read. "); continue; }
                    if (f.found()) { marks.put(t, "hit"); why.append(names.get(t)).append(": found \"").append(f.value()).append("\". Stop here: anything in an older table is an older version."); break; }
                    marks.put(t, "probe"); why.append(names.get(t)).append(": read one block, not there (a Bloom false positive). ");
                }
                return take(marks, why.toString().trim());
            }
        }
        Snap snap = new Snap();
        for (String[] kv : new String[][] { {"cat", "1"}, {"ant", "1"}, {"eel", "1"} }) {
            mem.put(kv[0], kv[1]);
            lsmSteps.add(snap.take(Map.of(), "put(" + kv[0] + "): goes into the memtable, a sorted map in memory (a TreeMap here; RocksDB uses a skip list)."));
        }
        snap.flush();
        lsmSteps.add(snap.take(Map.of(0, "new"), "The memtable is full (3 keys in this demo), so it's written out <b>in key order</b> as an immutable SSTable, in one sequential write."));
        for (String[] kv : new String[][] { {"cat", "2"}, {"bee", "2"}, {"fox", "2"} }) mem.put(kv[0], kv[1]);
        lsmSteps.add(snap.take(Map.of(), "Three more puts, including a newer value for cat. The old cat=1 in sst-0000 is not touched."));
        snap.flush();
        lsmSteps.add(snap.take(Map.of(0, "new"), "Flush again: a second SSTable. Newest tables are listed first, because a read must check them first."));
        mem.put("ant", null);
        lsmSteps.add(snap.take(Map.of(), "delete(ant): a tombstone goes into the memtable. It must hide ant=1 in the older table until a merge removes both."));
        lsmSteps.add(snap.get("cat"));
        lsmSteps.add(snap.get("dog"));
        lsmSteps.add(snap.get("ant"));
        snap.flush();
        lsmSteps.add(snap.take(Map.of(0, "new"), "Flush the tombstone into its own SSTable. Three files now: every read of a missing key could cost three probes without Bloom filters."));
        // compaction: merge newest-first, drop shadowed versions and tombstones
        TreeMap<String, String> merged = new TreeMap<>();
        for (int t = tables.size() - 1; t >= 0; t--) for (Lsm.Entry e : tables.get(t).entries("")) merged.put(e.key(), e.value());
        merged.values().removeIf(Objects::isNull);
        for (Lsm.SsTable t : tables) Files.delete(t.path);
        tables.clear(); names.clear();
        List<Map.Entry<String, String>> sorted = new ArrayList<>(merged.entrySet());
        String name = String.format("sst-%04d", next[0]++);
        tables.add(Lsm.SsTable.write(dir.resolve(name), sorted)); names.add(name);
        lsmSteps.add(snap.take(Map.of(0, "new"), "compact(): merge all tables like merge sort's merge step. For each key keep only the newest version; drop the tombstone and the ant=1 it was hiding. One file, no garbage."));

        out.append("{\"modes\":[{\"name\":\"Log + hash index\",\"kind\":\"log\",\"steps\":[").append(String.join(",", logSteps))
           .append("]},{\"name\":\"LSM tree\",\"kind\":\"lsm\",\"steps\":[").append(String.join(",", lsmSteps)).append("]}]}");
        System.out.println(out);
        try (var s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
