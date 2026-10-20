import java.io.*;
import java.nio.file.*;

public class SyscallCost {
    static final int N = 200_000;

    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("l22", ".bin");
        byte[] data = new byte[N];
        for (int i = 0; i < N; i++) data[i] = (byte) i;
        Files.write(file, data);
        byte[] bytes = Files.readAllBytes(file);

        double bestMem = Double.MAX_VALUE, bestSys = Double.MAX_VALUE;
        long sum = 0;
        for (int round = 0; round < 5; round++) {          // best of 5: the first rounds warm up the JIT
            long t = System.nanoTime();
            for (int i = 0; i < N; i++) sum += bytes[i];   // plain memory, no kernel
            bestMem = Math.min(bestMem, (double) (System.nanoTime() - t) / N);

            t = System.nanoTime();
            try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
                for (int i = 0; i < N; i++) sum += raf.read(); // one read() system call per byte
            }
            bestSys = Math.min(bestSys, (double) (System.nanoTime() - t) / N);
        }
        System.out.printf("array:                  %.2f ns per byte%n", bestMem);
        System.out.printf("RandomAccessFile.read(): %.0f ns per byte%n", bestSys);
        System.out.printf("ratio: about %.0fx  (checksum %d)%n", bestSys / bestMem, sum);
        Files.delete(file);
    }
}
