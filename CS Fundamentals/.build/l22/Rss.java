import java.io.*;

public class Rss {
    static long rssMb() throws IOException, InterruptedException {
        String pid = String.valueOf(ProcessHandle.current().pid());
        Process ps = new ProcessBuilder("ps", "-o", "rss=", "-p", pid).start(); // rss= : value only, no header
        long kb;
        try (BufferedReader r = ps.inputReader()) { kb = Long.parseLong(r.readLine().trim()); }
        ps.waitFor();
        return kb / 1024;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("pid " + ProcessHandle.current().pid() + ", Java threads: " + Thread.getAllStackTraces().size());
        System.out.println("RSS before: " + rssMb() + " MB");
        byte[] block = new byte[100 * 1024 * 1024];
        System.out.println("RSS after allocating 100 MB: " + rssMb() + " MB");
        for (int i = 0; i < block.length; i += 4096) block[i] = 1;     // write one byte per 4 KB
        System.out.println("RSS after touching it:       " + rssMb() + " MB");
    }
}
