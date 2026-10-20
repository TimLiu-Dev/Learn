import java.io.*;
import java.nio.file.*;

public class Buffering {
    static final int MB = 8;

    interface Opener { InputStream open() throws IOException; }
    interface Job { long run() throws IOException; }

    static long readByteByByte(Opener opener) throws IOException {
        long sum = 0;
        try (InputStream in = opener.open()) {
            int b;
            while ((b = in.read()) != -1) sum += b;   // one byte per call
        }
        return sum;
    }

    static void time(String label, Job job) throws IOException {
        double best = Double.MAX_VALUE;
        long sum = 0;
        for (int i = 0; i < 3; i++) {
            long t = System.nanoTime();
            sum = job.run();
            best = Math.min(best, (System.nanoTime() - t) / 1e6);
        }
        System.out.printf("%-34s %8.1f ms  %7.0f MB/s  (sum %d)%n", label, best, MB / (best / 1000), sum);
    }

    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("l22", ".bin");
        byte[] data = new byte[MB * 1024 * 1024];
        for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 251);
        Files.write(file, data);

        time("FileInputStream.read()", () -> readByteByByte(() -> new FileInputStream(file.toFile())));
        time("BufferedInputStream.read()", () -> readByteByByte(() -> new BufferedInputStream(new FileInputStream(file.toFile()))));
        time("Files.readAllBytes", () -> {
            long s = 0;
            for (byte b : Files.readAllBytes(file)) s += b & 0xff;
            return s;
        });
        Files.delete(file);
    }
}
