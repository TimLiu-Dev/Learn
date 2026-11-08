import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class KillDemo {
    static final int ACKED = 20_000;

    // The child writes ACKED records, says so, then waits to be killed.
    static void child(String mode, Path file) throws Exception {
        if (mode.equals("direct")) {
            LogKV db = new LogKV(file);                              // one write() system call per record
            for (int i = 0; i < ACKED; i++) db.put("k" + i, "value-" + i);
        } else {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file.toFile()), 8192); // 8 KB in the JVM
            for (int i = 0; i < ACKED; i++) out.write(LogKV.encode("k" + i, "value-" + i).array());
        }
        System.out.println("acked " + ACKED);
        System.out.flush();
        Thread.sleep(60_000);
    }

    // Re-run this same program as a child process, with different arguments.
    static Process startChild(String mode, Path file) throws IOException {
        ProcessHandle.Info me = ProcessHandle.current().info();
        String[] args = me.arguments().orElseThrow();
        List<String> cmd = new ArrayList<>();
        cmd.add(me.command().orElseThrow());
        cmd.addAll(Arrays.asList(args).subList(0, args.length - 1)); // drop "parent"
        cmd.addAll(List.of("child", mode, file.toString()));
        return new ProcessBuilder(cmd).redirectErrorStream(true).start();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) { child(args[1], Path.of(args[2])); return; }
        Path dir = Files.createTempDirectory("l56");
        for (String mode : List.of("buffered", "direct")) {
            Path file = dir.resolve(mode + ".log");
            Process p = startChild(mode, file);
            String line = p.inputReader().readLine();                 // blocks until the child has acked
            p.destroyForcibly();                                      // SIGKILL, like kill -9 (L53)
            int exit = p.waitFor();
            long size = Files.size(file);
            long valid;
            try (FileChannel ch = FileChannel.open(file)) { valid = TornFix.validPrefix(ch); }
            int keys;
            try (LogKV db = new LogKV(file)) { keys = db.keys(); }
            System.out.println(String.format("%-8s child said \"%s\", exit %d | file %,d bytes, torn tail %d bytes | recovered %,d of %,d",
                    mode, line, exit, size, size - valid, keys, ACKED));
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
