import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

// A log-structured merge tree: writes go to a sorted in-memory memtable, which is flushed to SSTables.
public class Lsm {
    record Entry(String key, String value, int size) {}          // value == null: tombstone
    record Found(boolean found, String value) {}
    static final Found MISS = new Found(false, null);
    static final class Stats { long blocksRead, bloomSkips; }

    // Reads one record in Part 1's format (LogKV.encode wrote it), or null at the end of the file.
    static Entry readEntry(FileChannel ch, long pos) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(LogKV.HEADER);
        if (ch.read(header, pos) < LogKV.HEADER) return null;
        int keyLen = header.getInt(4), valLen = header.getInt(8);
        ByteBuffer body = ByteBuffer.allocate(keyLen + Math.max(valLen, 0));
        ch.read(body, pos + LogKV.HEADER);
        String key = new String(body.array(), 0, keyLen, StandardCharsets.UTF_8);
        String value = valLen < 0 ? null : new String(body.array(), keyLen, valLen, StandardCharsets.UTF_8);
        return new Entry(key, value, LogKV.HEADER + body.capacity());
    }

    // An immutable file of records sorted by key, plus a sparse index and a Bloom filter kept in memory.
    static final class SsTable {
        static final int BLOCK = 16;
        final Path path;
        final long bytes;
        private final TreeMap<String, Long> sparse;
        private final Bloom bloom;

        SsTable(Path path, TreeMap<String, Long> sparse, Bloom bloom, long bytes) {
            this.path = path; this.sparse = sparse; this.bloom = bloom; this.bytes = bytes;
        }

        Found lookup(String key, Stats stats, boolean useBloom) throws IOException {
            if (useBloom && !bloom.mightContain(key)) { stats.bloomSkips++; return MISS; }
            Map.Entry<String, Long> start = sparse.floorEntry(key);
            if (start == null) return MISS;                                   // sorts before our first key
            stats.blocksRead++;
            try (FileChannel ch = FileChannel.open(path)) {
                long pos = start.getValue();
                for (int i = 0; i < BLOCK; i++) {                             // the key can only be in this block
                    Entry e = readEntry(ch, pos);
                    if (e == null || e.key().compareTo(key) > 0) return MISS;
                    if (e.key().equals(key)) return new Found(true, e.value());
                    pos += e.size();
                }
            }
            return MISS;
        }

        // Records in key order, starting at the block that could hold `from`.
        List<Entry> entries(String from) throws IOException {
            List<Entry> out = new ArrayList<>();
            Map.Entry<String, Long> start = sparse.floorEntry(from);
            try (FileChannel ch = FileChannel.open(path)) {
                long pos = start == null ? 0 : start.getValue();
                Entry e;
                while ((e = readEntry(ch, pos)) != null) { out.add(e); pos += e.size(); }
            }
            return out;
        }

        static SsTable write(Path path, List<Map.Entry<String, String>> sorted) throws IOException {
            TreeMap<String, Long> sparse = new TreeMap<>();
            Bloom bloom = new Bloom(Math.max(64, sorted.size() * 10), 7);     // 10 bits per key: about 1% false positives
            long pos = 0;
            try (FileChannel ch = FileChannel.open(path, CREATE_NEW, WRITE)) {
                for (int i = 0; i < sorted.size(); i++) {
                    String key = sorted.get(i).getKey();
                    if (i % BLOCK == 0) sparse.put(key, pos);
                    bloom.add(key);                                           // tombstones too: they must be found
                    ByteBuffer buf = LogKV.encode(key, sorted.get(i).getValue());
                    while (buf.hasRemaining()) pos += ch.write(buf);
                }
            }
            return new SsTable(path, sparse, bloom, pos);
        }
    }

    final Stats stats = new Stats();
    boolean useBloom = true;
    private long userBytes = 0, diskBytes = 0;          // a plain log's bytes vs bytes written to SSTables
    private TreeMap<String, String> mem = new TreeMap<>();
    private final List<SsTable> tables = new ArrayList<>();   // newest first
    private final Path dir;
    private final int memLimit;
    private int nextId = 0;

    Lsm(Path dir, int memLimit) { this.dir = dir; this.memLimit = memLimit; }

    long userBytes() { return userBytes; }
    long diskBytes() { return diskBytes; }
    int tableCount() { return tables.size(); }

    void put(String key, String value) throws IOException { write(key, value); }
    void delete(String key) throws IOException { write(key, null); }

    private void write(String key, String value) throws IOException {
        mem.put(key, value);
        userBytes += LogKV.encode(key, value).remaining();
        if (mem.size() >= memLimit) flush();
    }

    String get(String key) throws IOException {
        if (mem.containsKey(key)) return mem.get(key);
        for (SsTable t : tables) {
            Found f = t.lookup(key, stats, useBloom);
            if (f.found()) return f.value();                // the newest version wins, even a tombstone
        }
        return null;
    }

    // All live keys with from <= key < to, in order. Oldest first, so newer versions overwrite older ones.
    List<Map.Entry<String, String>> range(String from, String to) throws IOException {
        TreeMap<String, String> merged = new TreeMap<>();
        for (SsTable t : tables.reversed()) {
            for (Entry e : t.entries(from)) {
                if (e.key().compareTo(to) >= 0) break;
                if (e.key().compareTo(from) >= 0) merged.put(e.key(), e.value());
            }
        }
        merged.putAll(mem.subMap(from, to));
        List<Map.Entry<String, String>> out = new ArrayList<>();
        merged.forEach((k, v) -> { if (v != null) out.add(Map.entry(k, v)); });
        return out;
    }

    void flush() throws IOException {
        if (mem.isEmpty()) return;
        List<Map.Entry<String, String>> sorted = new ArrayList<>();
        mem.forEach((k, v) -> sorted.add(new AbstractMap.SimpleEntry<>(k, v)));   // Map.entry rejects null
        SsTable t = SsTable.write(nextPath(), sorted);
        diskBytes += t.bytes;
        tables.addFirst(t);
        mem = new TreeMap<>();
    }

    // Merge every table into one, like merge sort's merge step (L18) over k inputs with a heap (L20).
    void compact() throws IOException {
        final class Cursor {
            final int age; final Iterator<Entry> it; Entry head;
            Cursor(int age, Iterator<Entry> it) { this.age = age; this.it = it; }
            boolean advance() { head = it.hasNext() ? it.next() : null; return head != null; }
        }
        PriorityQueue<Cursor> heap = new PriorityQueue<>(
                Comparator.comparing((Cursor c) -> c.head.key()).thenComparingInt(c -> c.age));   // ties: newest first
        for (int age = 0; age < tables.size(); age++) {
            Cursor c = new Cursor(age, tables.get(age).entries("").iterator());
            if (c.advance()) heap.add(c);
        }
        List<Map.Entry<String, String>> out = new ArrayList<>();
        while (!heap.isEmpty()) {
            Cursor c = heap.poll();
            Entry e = c.head;
            while (!heap.isEmpty() && heap.peek().head.key().equals(e.key())) {   // older versions: skip
                Cursor old = heap.poll();
                if (old.advance()) heap.add(old);
            }
            if (e.value() != null) out.add(Map.entry(e.key(), e.value()));       // a full merge drops tombstones
            if (c.advance()) heap.add(c);
        }
        SsTable merged = SsTable.write(nextPath(), out);
        diskBytes += merged.bytes;
        for (SsTable t : tables) Files.delete(t.path);
        tables.clear();
        tables.add(merged);
    }

    private Path nextPath() { return dir.resolve(String.format("sst-%04d", nextId++)); }
}
