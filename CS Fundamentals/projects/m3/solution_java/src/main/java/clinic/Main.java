package clinic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * logreport: reads an access log and prints a traffic report.
 *
 *   logreport access.log
 *
 * Same output, byte for byte, as the original; see the FIX comments for what changed.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: logreport <access.log>");
            System.exit(2);
        }
        File file = new File(args[0]);
        if (!file.isFile() || !file.canRead()) {
            System.err.println("error: cannot read " + args[0]);
            System.exit(2);
        }

        Analyzer analyzer = new Analyzer();
        // FIX (I/O): a 64 KiB BufferedReader and readLine() instead of read() per char plus
        // `line += ch`, which copied every line once per character. Lines are now streamed:
        // nothing keeps the whole file in memory.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 1 << 16)) {
            String line;
            while ((line = reader.readLine()) != null) {
                analyzer.add(line);
            }
        }
        System.out.print(new ReportWriter().render(analyzer.finish()));
    }
}
