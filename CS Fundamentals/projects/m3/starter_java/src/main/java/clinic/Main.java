package clinic;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * logreport: reads an access log and prints a traffic report.
 *
 *   logreport access.log
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

        List<String> lines = new LogReader(file).readLines();
        Stats stats = new Analyzer().analyze(lines);
        System.out.print(new ReportWriter().render(stats));
    }
}
