import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

// Same 64-byte appends, two ways to "sync" them on macOS.
public class SyncKinds {
    interface Sync { void run(RandomAccessFile f) throws IOException; }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        byte[] record = new byte[64];
        Arrays.fill(record, (byte) 'x');
        int n = 1_000;
        String[] labels = { "FileDescriptor.sync()", "FileChannel.force()" };
        Sync[] syncs = {
            f -> f.getFD().sync(),                 // fsync(2)
            f -> f.getChannel().force(false),      // fcntl(F_FULLFSYNC) on macOS
        };
        for (int round = 1; round <= 2; round++) {                     // round 1 is the warm-up
            for (int k = 0; k < syncs.length; k++) {
                try (RandomAccessFile f = new RandomAccessFile(dir.resolve(labels[k] + "." + round).toFile(), "rw")) {
                    long t0 = System.nanoTime();
                    for (int i = 0; i < n; i++) { f.write(record); syncs[k].run(f); }
                    double us = (System.nanoTime() - t0) / 1000.0 / n;
                    if (round == 2) System.out.println(String.format("%-22s %7.1f µs per write", labels[k], us));
                }
            }
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
