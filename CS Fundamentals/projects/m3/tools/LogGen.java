import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Writes a deterministic access log for the M3 Performance Clinic.
 *
 *   java tools/LogGen.java                          -> 1,000,000 lines, seed 2027, to stdout
 *   java tools/LogGen.java --lines 1500 --seed 7 --out data/small.log
 *
 * The same seed and line count always give the same bytes (java.util.Random's
 * algorithm is fixed by its Javadoc, and only integer maths is used), so the
 * checker can regenerate the exact file you profile. Check.java contains a copy
 * of the Gen class below; keep the two identical.
 */
public class LogGen {
    public static void main(String[] args) throws IOException {
        int lines = 1_000_000;
        long seed = 2027;
        String out = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--lines" -> lines = Integer.parseInt(args[++i]);
                case "--seed" -> seed = Long.parseLong(args[++i]);
                case "--out" -> out = args[++i];
                default -> {
                    System.err.println("usage: java LogGen.java [--lines N] [--seed S] [--out FILE]");
                    System.exit(2);
                }
            }
        }
        Writer w = out == null
                ? new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), 1 << 16)
                : Files.newBufferedWriter(Path.of(out), StandardCharsets.UTF_8);
        try (w) {
            Gen.generate(seed, lines, w);
        }
    }

    // ---- copied verbatim into Check.java ----
    static final class Gen {
        // method, path template, weight, base latency ms, normal status
        // {id} -> a number, {n} -> a small number, {hash} -> hex, {q} -> a word
        private static final Object[][] ENDPOINTS = {
            {"GET", "/api/products/{id}", 30, 20, 200},
            {"GET", "/api/products?page={n}&size=20", 10, 35, 200},
            {"GET", "/api/products/{id}/reviews", 6, 45, 200},
            {"GET", "/api/v2/products/{id}", 3, 18, 200},
            {"HEAD", "/api/products/{id}", 1, 5, 200},
            {"GET", "/api/orders/{id}", 8, 40, 200},
            {"POST", "/api/orders", 4, 120, 201},
            {"GET", "/api/users/{id}/orders", 5, 60, 200},
            {"PUT", "/api/users/{id}", 1, 70, 200},
            {"POST", "/api/cart/{id}/items", 5, 50, 201},
            {"DELETE", "/api/cart/{id}/items/{id}", 2, 30, 204},
            {"GET", "/api/search?q={q}", 8, 80, 200},
            {"POST", "/api/login", 2, 150, 200},
            {"GET", "/api/reports/{n}/monthly", 1, 400, 200},
            {"GET", "/static/app.{hash}.js", 6, 3, 200},
            {"GET", "/static/logo.png", 4, 2, 200},
            {"GET", "/health", 5, 1, 200},
        };
        private static final String[] WORDS = {
            "shoes", "laptop", "coffee", "desk", "lamp", "phone", "book", "chair", "tea", "bag",
        };
        private static final String[] BUNDLES = {"3f9a2c1e", "7b04d6aa", "c21e9f30"}; // three deploys
        private static final int TOTAL_WEIGHT;
        static {
            int t = 0;
            for (Object[] e : ENDPOINTS) t += (Integer) e[2];
            TOTAL_WEIGHT = t;
        }
        static final long START_MS = 1_772_409_600_000L; // 2026-03-02T00:00:00.000Z
        static final long SPAN_MS = 3L * 24 * 60 * 60 * 1000; // about three days of traffic

        static void generate(long seed, int lines, Appendable out) throws IOException {
            Random rnd = new Random(seed);
            int userPool = Math.min(3000, Math.max(20, lines / 50));
            int heavy = Math.min(10, userPool / 2);
            long avgGap = Math.max(1, SPAN_MS / Math.max(1, lines));
            long t = START_MS;
            StringBuilder sb = new StringBuilder(128);
            for (int i = 0; i < lines; i++) {
                t += rnd.nextInt((int) (2 * avgGap + 1));
                sb.setLength(0);
                appendTimestamp(sb, t);
                sb.append(" 10.").append(rnd.nextInt(256)).append('.').append(rnd.nextInt(256))
                  .append('.').append(rnd.nextInt(256)).append(' ');
                int u = rnd.nextInt(100);
                if (u < 8) sb.append('-');
                else if (u < 33) appendUser(sb, 1 + rnd.nextInt(heavy));       // a few heavy users (apps, bots)
                else appendUser(sb, heavy + 1 + rnd.nextInt(userPool - heavy));
                Object[] e = pickEndpoint(rnd);
                String method = (String) e[0];
                int base = (Integer) e[3];
                int status = (Integer) e[4];
                sb.append(' ').append(method).append(' ');
                appendPath(sb, (String) e[1], rnd);
                int s = rnd.nextInt(1000);
                if (s < 20) status = 404;
                else if (s < 25) status = 500;
                else if (s < 28) status = 503;
                else if (method.equals("GET") && ((String) e[1]).startsWith("/static/") && s < 400) status = 304;
                int latency = base + rnd.nextInt(base + 1);
                if (rnd.nextInt(100) < 6) latency += rnd.nextInt(base * 10 + 1);
                if (rnd.nextInt(2000) == 0) latency += rnd.nextInt(20_000);
                if (status == 503) latency += 5_000 + rnd.nextInt(5_000);
                sb.append(' ').append(status).append(' ').append(latency).append(' ');
                if (status == 304 || status == 204 || method.equals("HEAD")) sb.append('-');
                else sb.append(200 + rnd.nextInt(20_000));
                if (rnd.nextInt(1000) == 0) corrupt(sb, rnd);
                out.append(sb).append('\n');
            }
        }

        private static Object[] pickEndpoint(Random rnd) {
            int r = rnd.nextInt(TOTAL_WEIGHT);
            for (Object[] e : ENDPOINTS) {
                r -= (Integer) e[2];
                if (r < 0) return e;
            }
            throw new AssertionError();
        }

        private static void appendUser(StringBuilder sb, int id) {
            sb.append('u');
            if (id < 1000) sb.append('0');
            if (id < 100) sb.append('0');
            if (id < 10) sb.append('0');
            sb.append(id);
        }

        private static void appendPath(StringBuilder sb, String template, Random rnd) {
            int i = 0;
            while (i < template.length()) {
                char c = template.charAt(i);
                if (c == '{') {
                    int end = template.indexOf('}', i);
                    switch (template.substring(i + 1, end)) {
                        case "id" -> sb.append(1 + rnd.nextInt(99_999));
                        case "n" -> sb.append(2020 + rnd.nextInt(7));
                        case "q" -> sb.append(WORDS[rnd.nextInt(WORDS.length)]);
                        case "hash" -> sb.append(BUNDLES[rnd.nextInt(BUNDLES.length)]);
                        default -> throw new AssertionError(template);
                    }
                    i = end + 1;
                } else {
                    sb.append(c);
                    i++;
                }
            }
        }

        /** About one line in a thousand is broken, as in real logs. */
        private static void corrupt(StringBuilder sb, Random rnd) {
            switch (rnd.nextInt(3)) {
                case 0 -> sb.setLength(10 + rnd.nextInt(sb.length() / 2)); // truncated write
                case 1 -> {                                                  // latency missing
                    int end = sb.lastIndexOf(" ");
                    int start = sb.lastIndexOf(" ", end - 1);
                    sb.replace(start + 1, end, "-");
                }
                default -> {                                                 // a stack trace line
                    sb.setLength(0);
                    sb.append("\tat com.shop.api.OrderController.get(OrderController.java:")
                      .append(10 + rnd.nextInt(300)).append(')');
                }
            }
        }

        /** yyyy-MM-ddTHH:mm:ss.SSSZ in UTC, by hand so the bytes never depend on a formatter. */
        static void appendTimestamp(StringBuilder sb, long epochMs) {
            long days = Math.floorDiv(epochMs, 86_400_000L);
            int msOfDay = (int) Math.floorMod(epochMs, 86_400_000L);
            // civil-from-days, Howard Hinnant's algorithm
            long z = days + 719_468;
            long era = Math.floorDiv(z, 146_097);
            long doe = z - era * 146_097;
            long yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365;
            long y = yoe + era * 400;
            long doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
            long mp = (5 * doy + 2) / 153;
            long d = doy - (153 * mp + 2) / 5 + 1;
            long m = mp < 10 ? mp + 3 : mp - 9;
            if (m <= 2) y++;
            sb.append(y).append('-');
            pad(sb, (int) m, 2).append('-');
            pad(sb, (int) d, 2).append('T');
            pad(sb, msOfDay / 3_600_000, 2).append(':');
            pad(sb, msOfDay / 60_000 % 60, 2).append(':');
            pad(sb, msOfDay / 1000 % 60, 2).append('.');
            pad(sb, msOfDay % 1000, 3).append('Z');
        }

        private static StringBuilder pad(StringBuilder sb, int v, int width) {
            String s = Integer.toString(v);
            for (int k = s.length(); k < width; k++) sb.append('0');
            return sb.append(s);
        }
    }
    // ---- end copy ----
}
