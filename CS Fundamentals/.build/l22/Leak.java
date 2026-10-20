import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Leak {
    static int openFds() { return new File("/dev/fd").list().length; } // macOS and Linux: one entry per open descriptor

    public static void main(String[] args) throws IOException {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 300;
        File file = Files.createTempFile("l22", ".txt").toFile();
        List<FileInputStream> leaked = new ArrayList<>();
        System.out.println("open descriptors at start: " + openFds());
        try {
            for (int i = 0; i < n; i++) leaked.add(new FileInputStream(file)); // opened, never closed
            System.out.println("after opening " + n + " streams:   " + openFds());
        } catch (IOException e) {
            System.out.println("failed after " + leaked.size() + " streams: " + e);
        } finally {
            for (FileInputStream in : leaked) in.close();
            System.out.println("after closing them:         " + openFds());
            file.delete();
        }
    }
}
