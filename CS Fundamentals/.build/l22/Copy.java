import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class Copy {
    interface Job { void run() throws IOException; }

    static void copyByteByByte(Path src, Path dst) throws IOException {
        try (InputStream in = new FileInputStream(src.toFile());
             OutputStream out = new FileOutputStream(dst.toFile())) {
            int b;
            while ((b = in.read()) != -1) out.write(b);
        }
    }

    static void copyWithBuffer(Path src, Path dst, int size) throws IOException {
        byte[] buffer = new byte[size];
        try (InputStream in = new FileInputStream(src.toFile());
             OutputStream out = new FileOutputStream(dst.toFile())) {
            int n;
            while ((n = in.read(buffer)) != -1) // up to buffer.length bytes per system call
                out.write(buffer, 0, n);        // write only the n bytes we got
        }
    }

    static void timed(String label, Path src, Path dst, Job copy) throws IOException {
        long t = System.nanoTime();
        copy.run();
        double ms = (System.nanoTime() - t) / 1e6;
        System.out.printf("%-24s %8.1f ms  identical: %b%n", label, ms, Files.mismatch(src, dst) == -1L);
    }

    public static void main(String[] args) throws IOException {
        Path src = Files.createTempFile("src", ".bin");
        Path dst = Files.createTempFile("dst", ".bin");
        byte[] data = new byte[4 * 1024 * 1024];                    // 4 MB of random bytes
        new Random(7).nextBytes(data);
        Files.write(src, data);

        timed("byte by byte", src, dst, () -> copyByteByByte(src, dst));
        timed("16-byte buffer", src, dst, () -> copyWithBuffer(src, dst, 16));
        timed("8 KB buffer", src, dst, () -> copyWithBuffer(src, dst, 8192));
        timed("Files.copy", src, dst, () -> Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING));
        Files.delete(src);
        Files.delete(dst);
    }
}
