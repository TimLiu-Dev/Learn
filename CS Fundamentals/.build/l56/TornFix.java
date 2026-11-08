import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class TornFix {
    // Length of the longest prefix made of whole records whose CRC matches.
    static long validPrefix(FileChannel ch) throws IOException {
        long pos = 0;
        ByteBuffer header = ByteBuffer.allocate(LogKV.HEADER);
        while (true) {
            header.clear();
            if (ch.read(header, pos) < LogKV.HEADER) return pos;
            int crc = header.getInt(0), keyLen = header.getInt(4), valLen = header.getInt(8);
            if (keyLen < 0 || keyLen > LogKV.MAX_LEN || valLen < -1 || valLen > LogKV.MAX_LEN) return pos;
            int size = LogKV.HEADER + keyLen + Math.max(valLen, 0);
            if (pos + size > ch.size()) return pos;
            ByteBuffer body = ByteBuffer.allocate(size);
            ch.read(body, pos);
            CRC32 check = new CRC32();
            check.update(body.array(), 4, size - 4);
            if ((int) check.getValue() != crc) return pos;
            pos += size;
        }
    }

    // Cut the file back to its valid prefix, so the next append lands right after the last good record.
    static long repair(Path file) throws IOException {
        try (FileChannel ch = FileChannel.open(file, READ, WRITE)) {
            long end = validPrefix(ch);
            long cut = ch.size() - end;
            ch.truncate(end);
            ch.force(true);
            return cut;
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        Path t = dir.resolve("t.log");
        try (LogKV db = new LogKV(t)) { for (int i = 1; i <= 3; i++) db.put("k" + i, "v" + i); }
        Files.write(t, Arrays.copyOf(Files.readAllBytes(t), 40));   // the same torn file as before
        System.out.println("repair cut " + repair(t) + " bytes");
        try (LogKV db = new LogKV(t)) { db.put("k4", "v4"); }
        try (LogKV db = new LogKV(t)) {
            System.out.println("reopen: keys = " + db.indexSnapshot().keySet() + ", get(k4) = " + db.get("k4"));
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
