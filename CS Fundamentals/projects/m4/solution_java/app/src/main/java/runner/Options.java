package runner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record Options(String file, int workers, int retries, long timeoutMs, int queue, long graceMs) {
    public static final String USAGE =
        "usage: runner <jobs-file> --workers N [--retries R] [--timeout-ms T] [--queue Q] [--grace-ms G]";
    private static final Set<String> FLAGS = Set.of("workers", "retries", "timeout-ms", "queue", "grace-ms");

    public static Options parse(String[] args) throws InputError {
        String file = null;
        Map<String, Long> flags = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                String name = arg.substring(2);
                if (!FLAGS.contains(name)) throw new InputError("unknown option " + arg + "\n" + USAGE);
                if (i + 1 >= args.length) throw new InputError(arg + " needs a value\n" + USAGE);
                String value = args[++i];
                try {
                    flags.put(name, Long.parseLong(value));
                } catch (NumberFormatException e) {
                    throw new InputError(arg + " must be a number, got '" + value + "'");
                }
            } else {
                if (file != null) throw new InputError("only one jobs file, got '" + file + "' and '" + arg + "'\n" + USAGE);
                file = arg;
            }
        }
        if (file == null) throw new InputError("missing jobs file\n" + USAGE);
        if (!flags.containsKey("workers")) throw new InputError("missing --workers\n" + USAGE);
        var opts = new Options(file,
            flags.get("workers").intValue(),
            flags.getOrDefault("retries", 0L).intValue(),
            flags.getOrDefault("timeout-ms", 0L),
            flags.getOrDefault("queue", 16L).intValue(),
            flags.getOrDefault("grace-ms", 3000L));
        if (opts.workers < 1) throw new InputError("--workers must be at least 1, got " + opts.workers);
        if (opts.queue < 1) throw new InputError("--queue must be at least 1, got " + opts.queue);
        if (opts.retries < 0) throw new InputError("--retries must be 0 or more, got " + opts.retries);
        if (opts.timeoutMs < 0) throw new InputError("--timeout-ms must be 0 or more, got " + opts.timeoutMs);
        if (opts.graceMs < 0) throw new InputError("--grace-ms must be 0 or more, got " + opts.graceMs);
        return opts;
    }
}
