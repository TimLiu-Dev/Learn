package katas;

import java.nio.file.Path;

// Run as its own process by the kill -9 test: write forever, acknowledging each put on stdout.
public class KillChild {
    public static void main(String[] args) throws Exception {
        try (LogStore db = LogStore.open(Path.of(args[0]), Sync.NEVER)) {
            for (int i = 0; ; i++) {
                db.put("k" + i, "v" + i);
                System.out.println("acked " + i);       // printed only after put() returned
                System.out.flush();
            }
        }
    }
}
