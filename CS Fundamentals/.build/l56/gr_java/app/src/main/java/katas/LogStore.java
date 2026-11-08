package katas;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.*;

// A crash-safe key-value store: an append-only log of CRC-checked records plus an in-memory hash index.
// Record: [crc 4][keyLen 4][valLen 4][key][value]; valLen = -1 is a tombstone.
public final class LogStore implements AutoCloseable {
    public static final String DATA = "data.log";
    public static final String COMPACT = "data.log.compact";
    private static final int HEADER = 12;
    private static final int MAX_LEN = 1 << 20;

    private record Rec(String key, String value, int size) {}

    private final Path dir;
    private final Path file;
    private final Sync sync;
    private FileChannel ch;
    private final Map<String, Long> index = new HashMap<>();   // key -> offset of its newest record

    private LogStore(Path dir, Sync sync) throws IOException {
        this.dir = dir;
        this.file = dir.resolve(DATA);
        this.sync = sync;
        Files.deleteIfExists(dir.resolve(COMPACT));             // a compaction that crashed never happened
        ch = FileChannel.open(file, CREATE, READ, WRITE);
        recover();
    }

    public static LogStore open(Path dir) throws IOException { return open(dir, Sync.EVERY_WRITE); }
    public static LogStore open(Path dir, Sync sync) throws IOException { return new LogStore(dir, sync); }

    public int size() { return index.size(); }
    public long fileBytes() throws IOException { return ch.size(); }

    public void put(String key, String value) throws IOException {
        if (key.isEmpty()) throw new IllegalArgumentException("key must not be empty");
        index.put(key, append(key, value));
    }

    public void delete(String key) throws IOException {
        if (index.remove(key) != null) append(key, null);
    }

    public String get(String key) throws IOException {
        Long offset = index.get(key);
        return offset == null ? null : read(ch, offset).value();
    }

    // Write the live records to a new file, make it durable, then rename it over the old one in one step.
    public void compact() throws IOException {
        Path tmp = dir.resolve(COMPACT);
        Map<String, Long> fresh = new HashMap<>();
        try (FileChannel out = FileChannel.open(tmp, CREATE, WRITE)) {
            for (Map.Entry<String, Long> e : index.entrySet()) {
                fresh.put(e.getKey(), out.position());
                ByteBuffer buf = encode(e.getKey(), read(ch, e.getValue()).value());
                while (buf.hasRemaining()) out.write(buf);
            }
            out.force(true);
        }
        ch.close();
        Files.move(tmp, file, ATOMIC_MOVE);
        ch = FileChannel.open(file, READ, WRITE);
        index.clear();
        index.putAll(fresh);
    }

    @Override public void close() throws IOException { ch.close(); }

    private long append(String key, String value) throws IOException {
        ByteBuffer buf = encode(key, value);
        long offset = ch.size();
        long pos = offset;
        while (buf.hasRemaining()) pos += ch.write(buf, pos);
        if (sync == Sync.EVERY_WRITE) ch.force(false);           // only now is the write acknowledged as durable
        return offset;
    }

    // Rebuild the index, then cut off anything after the last good record (a torn or garbage tail).
    private void recover() throws IOException {
        long pos = 0;
        Rec r;
        while ((r = read(ch, pos)) != null) {
            if (r.value() == null) index.remove(r.key()); else index.put(r.key(), pos);
            pos += r.size();
        }
        if (pos < ch.size()) { ch.truncate(pos); ch.force(true); }
    }

    private static ByteBuffer encode(String key, String value) {
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

    private static Rec read(FileChannel ch, long pos) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(HEADER);
        if (ch.read(header, pos) < HEADER) return null;
        int crc = header.getInt(0), keyLen = header.getInt(4), valLen = header.getInt(8);
        if (keyLen < 0 || keyLen > MAX_LEN || valLen < -1 || valLen > MAX_LEN) return null;
        int size = HEADER + keyLen + Math.max(valLen, 0);
        if (pos + size > ch.size()) return null;
        ByteBuffer body = ByteBuffer.allocate(size);
        ch.read(body, pos);
        CRC32 check = new CRC32();
        check.update(body.array(), 4, size - 4);
        if ((int) check.getValue() != crc) return null;
        String value = valLen < 0 ? null : new String(body.array(), HEADER + keyLen, valLen, StandardCharsets.UTF_8);
        return new Rec(new String(body.array(), HEADER, keyLen, StandardCharsets.UTF_8), value, size);
    }
}
