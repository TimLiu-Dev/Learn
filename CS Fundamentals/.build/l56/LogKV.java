import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.CRC32;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.*;

// Where a key's newest record lives in the file.
record Loc(long offset, int size) {}

// One decoded record: value == null means a tombstone.
record Rec(String key, String value, int size) {}

// A Bitcask-style store: an append-only log file plus an in-memory hash index.
// Record layout: [crc 4][keyLen 4][valLen 4][key bytes][value bytes]; valLen = -1 is a tombstone.
public class LogKV implements AutoCloseable {
    static final int HEADER = 12;
    static final int MAX_LEN = 1 << 20;

    private final Path path;
    private FileChannel ch;
    private final Map<String, Loc> index = new HashMap<>();
    private long deadBytes = 0;

    public LogKV(Path path) throws IOException {
        this.path = path;
        this.ch = FileChannel.open(path, CREATE, READ, WRITE);
        recover();
    }

    public long deadBytes() { return deadBytes; }
    public int keys() { return index.size(); }
    public long fileSize() throws IOException { return ch.size(); }

    public void put(String key, String value) throws IOException { append(key, value); }
    public void delete(String key) throws IOException { if (index.containsKey(key)) append(key, null); }

    public String get(String key) throws IOException {
        Loc loc = index.get(key);
        if (loc == null) return null;
        Rec r = readAt(loc.offset());
        return r == null ? null : r.value();
    }

    public Map<String, Loc> indexSnapshot() { return new TreeMap<>(index); }

    public void sync() throws IOException { ch.force(false); }

    // Rewrite only the live records into a new file, then swap it in with one atomic rename.
    public void compact() throws IOException {
        Path tmp = path.resolveSibling(path.getFileName() + ".compact");
        Map<String, Loc> fresh = new HashMap<>();
        try (FileChannel out = FileChannel.open(tmp, CREATE, WRITE)) {
            long pos = 0;
            for (Map.Entry<String, Loc> e : index.entrySet()) {
                ByteBuffer buf = encode(e.getKey(), readAt(e.getValue().offset()).value());
                int size = buf.remaining();
                while (buf.hasRemaining()) out.write(buf);
                fresh.put(e.getKey(), new Loc(pos, size));
                pos += size;
            }
            out.force(true);
        }
        ch.close();
        Files.move(tmp, path, ATOMIC_MOVE);
        ch = FileChannel.open(path, READ, WRITE);
        index.clear();
        index.putAll(fresh);
        deadBytes = 0;
    }

    @Override public void close() throws IOException { ch.close(); }

    private void append(String key, String value) throws IOException {
        ByteBuffer buf = encode(key, value);
        long offset = ch.size();
        int size = buf.remaining();
        long pos = offset;
        while (buf.hasRemaining()) pos += ch.write(buf, pos);
        Loc old = value == null ? index.remove(key) : index.put(key, new Loc(offset, size));
        if (old != null) deadBytes += old.size();
        if (value == null) deadBytes += size;         // a tombstone is garbage once compacted away
    }

    // Scan the whole file and rebuild the index: Bitcask's startup. Stops at the first bad record.
    private void recover() throws IOException {
        long pos = 0;
        Rec r;
        while ((r = readAt(pos)) != null) {
            Loc old = r.value() == null ? index.remove(r.key()) : index.put(r.key(), new Loc(pos, r.size()));
            if (old != null) deadBytes += old.size();
            if (r.value() == null) deadBytes += r.size();
            pos += r.size();
        }
    }

    // Returns the record at pos, or null if it is missing or corrupt.
    private Rec readAt(long pos) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(HEADER);
        if (ch.read(header, pos) < HEADER) return null;
        header.flip();
        int crc = header.getInt(), keyLen = header.getInt(), valLen = header.getInt();
        if (keyLen < 0 || keyLen > MAX_LEN || valLen < -1 || valLen > MAX_LEN) return null;
        int size = HEADER + keyLen + Math.max(valLen, 0);
        if (pos + size > ch.size()) return null;
        ByteBuffer body = ByteBuffer.allocate(size);
        ch.read(body, pos);
        CRC32 check = new CRC32();
        check.update(body.array(), 4, size - 4);
        if ((int) check.getValue() != crc) return null;
        String key = new String(body.array(), HEADER, keyLen, StandardCharsets.UTF_8);
        String value = valLen < 0 ? null : new String(body.array(), HEADER + keyLen, valLen, StandardCharsets.UTF_8);
        return new Rec(key, value, size);
    }

    static ByteBuffer encode(String key, String value) {
        byte[] k = key.getBytes(StandardCharsets.UTF_8);
        byte[] v = value == null ? null : value.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(HEADER + k.length + (v == null ? 0 : v.length));
        buf.position(4);
        buf.putInt(k.length).putInt(v == null ? -1 : v.length).put(k);
        if (v != null) buf.put(v);
        CRC32 crc = new CRC32();
        crc.update(buf.array(), 4, buf.capacity() - 4);
        buf.putInt(0, (int) crc.getValue());
        return buf.flip();
    }
}
