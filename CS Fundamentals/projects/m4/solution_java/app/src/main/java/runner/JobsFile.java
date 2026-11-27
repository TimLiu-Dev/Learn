package runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Parses and validates a jobs file. Every problem is an {@link InputError} naming the line. */
public final class JobsFile {
    private static final Pattern ID = Pattern.compile("[A-Za-z0-9_-]+");
    private static final Set<String> KEYS = Set.of("id", "duration", "fail", "deps");

    private JobsFile() {}

    public static List<Job> parse(String text) throws InputError {
        List<Job> jobs = new ArrayList<>();
        Map<String, Integer> lineOf = new HashMap<>();
        String[] lines = text.split("\n", -1);
        for (int index = 0; index < lines.length; index++) {
            int n = index + 1;
            String line = lines[index].strip();
            if (line.isEmpty() || line.startsWith("#")) continue;
            Map<String, String> fields = new LinkedHashMap<>();
            for (String token : line.split("\\s+")) {
                int eq = token.indexOf('=');
                if (eq < 0) throw new InputError("line " + n + ": expected key=value, got '" + token + "'");
                String key = token.substring(0, eq);
                if (!KEYS.contains(key)) throw new InputError("line " + n + ": unknown key '" + key + "'");
                if (fields.put(key, token.substring(eq + 1)) != null) throw new InputError("line " + n + ": '" + key + "' given twice");
            }
            String id = fields.get("id");
            if (id == null) throw new InputError("line " + n + ": missing id");
            if (!ID.matcher(id).matches()) throw new InputError("line " + n + ": bad id '" + id + "'");
            Integer first = lineOf.putIfAbsent(id, n);
            if (first != null) throw new InputError("line " + n + ": duplicate id '" + id + "' (first on line " + first + ")");
            String duration = fields.get("duration");
            if (duration == null) throw new InputError("line " + n + ": missing duration");
            long fail = number(fields.getOrDefault("fail", "0"), "fail", n);
            List<String> deps = Arrays.stream(fields.getOrDefault("deps", "").split(","))
                .filter(s -> !s.isEmpty()).toList();
            jobs.add(new Job(id, number(duration, "duration", n), (int) fail, deps));
        }
        for (Job job : jobs) {
            for (String dep : job.deps()) {
                if (!lineOf.containsKey(dep))
                    throw new InputError("line " + lineOf.get(job.id()) + ": unknown dependency '" + dep + "' in job '" + job.id() + "'");
            }
        }
        return jobs;
    }

    private static long number(String value, String key, int line) throws InputError {
        try {
            long v = Long.parseLong(value);
            if (v >= 0) return v;
        } catch (NumberFormatException ignored) {
            // fall through to the error below
        }
        throw new InputError("line " + line + ": " + key + " must be a whole number ≥ 0, got '" + value + "'");
    }
}
