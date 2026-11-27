import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Arrays;

public class Durable {
    static final int WRITES = 200;

    static double timeWrites(boolean force) throws IOException {
        Path file = Files.createTempFile("l22", ".log");
        byte[] record = new byte[64];
        Arrays.fill(record, (byte) 'x');
        long t = System.nanoTime();
        try (FileChannel ch = FileChannel.open(file, StandardOpenOption.WRITE)) {
            for (int i = 0; i < WRITES; i++) {
                ch.write(ByteBuffer.wrap(record));    // returns once the kernel has the bytes
                if (force) ch.force(false);           // waits until the storage device has them
            }
        }
        double ms = (System.nanoTime() - t) / 1e6;
        Files.delete(file);
        return ms;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 2; i++) { timeWrites(false); timeWrites(true); } // warm-up rounds
        double plain = timeWrites(false);
        double forced = timeWrites(true);
        System.out.printf("%d writes, no force:     %7.1f ms  (%.1f µs each)%n", WRITES, plain, plain * 1000 / WRITES);
        System.out.printf("%d writes, force each:   %7.1f ms  (%.1f µs each)%n", WRITES, forced, forced * 1000 / WRITES);
    }
}
