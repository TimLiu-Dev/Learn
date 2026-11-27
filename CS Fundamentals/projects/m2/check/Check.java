// Black-box checker for Mini project M2 · Mini Search Engine.
//
//   java check/Check.java <path-to-launcher> [--stage N] [--corpus DIR]
//
// The launcher is your build/install/search/bin/search (from ./gradlew installDist).
// --stage N runs stages 1..N (default: all 5). Run it from projects/m2 so it can find
// corpus/ (the Aesop fables), or pass --corpus. JDK only; every check is deterministic.
//
// Stage 1 · load and stats          Stage 4 · prefix autocomplete and top-k
// Stage 2 · tokenisation            Stage 5 · performance: doubling experiment
// Stage 3 · AND / OR queries and ranking
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Check {

    // ───────────────────────────── reporting ─────────────────────────────
    static int passed, failed;

    static void pass(int stage, String what) {
        passed++;
        System.out.println("PASS stage " + stage + " · " + what);
    }

    static void fail(int stage, String what, String detail) {
        failed++;
        System.out.println("FAIL stage " + stage + " · " + what);
        for (String line : detail.split("\n", -1)) System.out.println("      " + line);
    }

    static void note(String text) { System.out.println("      " + text); }

    /** Shows a multi-line block readably inside a message. */
    static String show(String block) {
        if (block == null) return "(nothing: the program printed no output for this command)";
        if (block.isEmpty()) return "(an empty line)";
        return block.replace("\n", "\n          ");
    }

    // ───────────────────────────── running the program ─────────────────────────────
    static Path launcher;
    static Path work;

    record Run(int exit, String out, String err, boolean timedOut, long millis) {}

    static Run run(List<String> args, String stdin, int timeoutSeconds) {
        try {
            Path in = Files.createTempFile(work, "in", ".txt");
            Path out = Files.createTempFile(work, "out", ".txt");
            Path err = Files.createTempFile(work, "err", ".txt");
            Files.writeString(in, stdin, StandardCharsets.UTF_8);
            List<String> cmd = new ArrayList<>();
            cmd.add(launcher.toString());
            cmd.addAll(args);
            ProcessBuilder pb = new ProcessBuilder(cmd)
                    .redirectInput(in.toFile()).redirectOutput(out.toFile()).redirectError(err.toFile());
            long start = System.nanoTime();
            Process p = pb.start();
            boolean done = p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!done) {
                p.descendants().forEach(ProcessHandle::destroyForcibly);
                p.destroyForcibly();
                p.waitFor(5, TimeUnit.SECONDS);
            }
            long millis = (System.nanoTime() - start) / 1_000_000;
            return new Run(done ? p.exitValue() : -1,
                    Files.readString(out, StandardCharsets.UTF_8).replace("\r\n", "\n"),
                    Files.readString(err, StandardCharsets.UTF_8).replace("\r\n", "\n"),
                    !done, millis);
        } catch (IOException e) {
            return new Run(-1, "", "could not start " + launcher + ": " + e.getMessage(), false, 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Run(-1, "", "interrupted", true, 0);
        }
    }

    /** One program run over a corpus with a list of commands; stdout split into blocks. */
    static final class Session {
        final List<String> commands;
        final Run run;
        final List<String> blocks = new ArrayList<>();
        boolean troubleShown;

        Session(Path corpus, List<String> commands, int timeoutSeconds) {
            this.commands = commands;
            this.run = Check.run(List.of(corpus.toString()), String.join("\n", commands) + "\nquit\n", timeoutSeconds);
            // Every block ends with one empty line: the startup line is block 0, command i is block i + 1.
            StringBuilder cur = new StringBuilder();
            boolean open = false;
            String[] lines = run.out.split("\n", -1);
            // The text after the final newline is not a line of its own.
            int count = lines[lines.length - 1].isEmpty() ? lines.length - 1 : lines.length;
            for (int li = 0; li < count; li++) {
                String line = lines[li];
                String l = line.stripTrailing();
                if (l.isEmpty()) {
                    blocks.add(cur.toString());
                    cur.setLength(0);
                    open = false;
                } else {
                    if (open) cur.append('\n');
                    cur.append(l);
                    open = true;
                }
            }
            if (open) blocks.add(cur.toString());
        }

        String startup() { return blocks.isEmpty() ? null : blocks.getFirst(); }

        String block(int i) { return i + 1 < blocks.size() ? blocks.get(i + 1) : null; }

        /** Why the output might be missing or misaligned, for failure messages. */
        String trouble() {
            if (troubleShown) return "\n(the same run as the check above: see its stderr)";
            troubleShown = true;
            StringBuilder t = new StringBuilder();
            if (run.timedOut) t.append("\nThe program was still running after the time limit and was stopped. "
                    + "Does it exit on 'quit' and at end of input?");
            else if (run.exit != 0) t.append("\nThe program exited with code ").append(run.exit).append(" (expected 0).");
            if (!run.err.isBlank()) t.append("\nstderr: ").append(firstLines(run.err, 6));
            if (run.err.contains("TODO") || run.err.contains("NotImplementedError"))
                t.append("\nThat's a TODO left in the starter: start with the milestone it names on the project page.");
            if (!run.timedOut && run.exit == 0 && blocks.size() != commands.size() + 1)
                t.append("\nI counted ").append(blocks.size()).append(" output blocks for ").append(commands.size())
                        .append(" commands plus the startup line. Every command's output (and the startup line) must end with exactly one empty line.");
            return t.toString();
        }
    }

    static String firstLines(String text, int n) {
        String[] lines = text.strip().split("\n");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < Math.min(n, lines.length); i++) b.append(i == 0 ? "" : "\n        ").append(lines[i]);
        if (lines.length > n) b.append("\n        … (").append(lines.length - n).append(" more lines)");
        return b.toString();
    }

    /** Compares the output of command i with the expected block. */
    static boolean expect(int stage, String what, Session s, int i, String expected, String hint) {
        String got = s.block(i);
        if (expected.equals(got)) {
            pass(stage, what);
            return true;
        }
        StringBuilder d = new StringBuilder();
        d.append("input:    ").append(s.commands.get(i)).append('\n');
        d.append("expected: ").append(show(expected)).append('\n');
        d.append("got:      ").append(show(got));
        String firstDiff = firstDifference(expected, got);
        if (firstDiff != null) d.append('\n').append(firstDiff);
        if (got == null || s.run.exit != 0 || s.run.timedOut) d.append(s.trouble());
        if (hint != null) d.append("\nhint: ").append(hint);
        fail(stage, what, d.toString());
        return false;
    }

    static String firstDifference(String expected, String got) {
        if (got == null) return null;
        String[] e = expected.split("\n"), g = got.split("\n");
        for (int i = 0; i < Math.max(e.length, g.length); i++) {
            String a = i < e.length ? e[i] : "(no line)";
            String b = i < g.length ? g[i] : "(no line)";
            if (!a.equals(b)) return "first difference on line " + (i + 1) + ": expected \"" + a + "\", got \"" + b + "\"";
        }
        return null;
    }

    static boolean expectStartup(int stage, String what, Session s, String expected, String hint) {
        String got = s.startup();
        if (expected.equals(got)) {
            pass(stage, what);
            return true;
        }
        fail(stage, what, "expected first line: " + expected + "\n"
                + "got:                 " + (got == null ? "(nothing)" : got.replace("\n", " ⏎ ")) + s.trouble()
                + (hint == null ? "" : "\nhint: " + hint));
        return false;
    }

    static Path corpus(String name, Map<String, String> files) throws IOException {
        Path dir = work.resolve(name);
        Files.createDirectories(dir);
        for (var e : files.entrySet()) {
            Path f = dir.resolve(e.getKey());
            Files.createDirectories(f.getParent());
            Files.writeString(f, e.getValue(), StandardCharsets.UTF_8);
        }
        return dir;
    }

    // ───────────────────────────── the oracle: a deliberately naive reference ─────────────────────────────
    // Obviously-correct brute force: no index, no trie, no heap. Used to compute expected output on
    // generated corpora and on the Aesop fables. Hand-computed literals cover the small cases.
    static final Set<String> STOP = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
            "from", "had", "has", "have", "he", "her", "him", "his", "i", "in",
            "is", "it", "its", "me", "my", "of", "on", "or", "she", "so",
            "that", "the", "their", "them", "then", "there", "they", "this", "to", "was",
            "we", "were", "what", "when", "which", "who", "will", "with", "you", "your");

    static List<String> tokens(String text) {
        int[] c = text.replace('’', '\'').codePoints().toArray();
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < c.length) {
            if (!Character.isLetterOrDigit(c[i])) { i++; continue; }
            int j = i;
            // extend over letters/digits, and over an apostrophe only if a letter/digit follows it
            while (j < c.length && (Character.isLetterOrDigit(c[j])
                    || (c[j] == '\'' && j + 1 < c.length && Character.isLetterOrDigit(c[j + 1])))) j++;
            String t = new String(c, i, j - i).toLowerCase(Locale.ROOT);
            if (!STOP.contains(t)) out.add(t);
            i = j;
        }
        return out;
    }

    static final class Oracle {
        final TreeMap<String, List<String>> docs = new TreeMap<>();

        Oracle(Path dir) throws IOException {
            try (var list = Files.list(dir)) {
                for (Path p : list.toList()) {
                    String n = p.getFileName().toString();
                    if (Files.isRegularFile(p) && n.endsWith(".txt"))
                        docs.put(n.substring(0, n.length() - 4), tokens(Files.readString(p, StandardCharsets.UTF_8)));
                }
            }
        }

        long tokenCount() { return docs.values().stream().mapToLong(List::size).sum(); }

        Map<String, Long> cf() {
            Map<String, Long> cf = new TreeMap<>();
            for (List<String> d : docs.values()) for (String t : d) cf.merge(t, 1L, Long::sum);
            return cf;
        }

        String loaded() {
            return "loaded " + docs.size() + " documents, " + tokenCount() + " tokens, " + cf().size() + " terms";
        }

        String stats() {
            double avg = docs.isEmpty() ? 0 : (double) tokenCount() / docs.size();
            return "documents: " + docs.size() + "\ntokens: " + tokenCount() + "\nterms: " + cf().size()
                    + "\naverage document length: " + String.format(Locale.ROOT, "%.2f", avg);
        }

        String doc(String name) {
            List<String> d = docs.get(name);
            if (d == null) return "error: no document named '" + name + "'";
            Map<String, Long> tf = new TreeMap<>();
            for (String t : d) tf.merge(t, 1L, Long::sum);
            List<String> top = tf.entrySet().stream().sorted(byCount()).limit(3)
                    .map(e -> e.getKey() + " (" + e.getValue() + ")").toList();
            return "doc: " + name + "\ntokens: " + d.size() + "\nterms: " + tf.size()
                    + "\ntop terms: " + (top.isEmpty() ? "(none)" : String.join(", ", top));
        }

        String query(String text, boolean and) {
            List<String> q = new ArrayList<>(new LinkedHashSet<>(tokens(text)));
            if (q.isEmpty()) return "error: no searchable terms in query";
            record Hit(String name, double score) {}
            List<Hit> hits = new ArrayList<>();
            for (var e : docs.entrySet()) {
                boolean match = and ? e.getValue().containsAll(q) : q.stream().anyMatch(e.getValue()::contains);
                if (!match) continue;
                double s = 0;
                for (String t : q) s += Collections.frequency(e.getValue(), t) * idf(t);
                hits.add(new Hit(e.getKey(), s));
            }
            hits.sort(Comparator.comparingLong((Hit h) -> -Math.round(h.score() * 10_000)).thenComparing(Hit::name));
            StringBuilder b = new StringBuilder("results: " + hits.size());
            for (int i = 0; i < Math.min(10, hits.size()); i++)
                b.append('\n').append(i + 1).append(". ").append(hits.get(i).name()).append(' ')
                        .append(String.format(Locale.ROOT, "%.4f", hits.get(i).score()));
            return b.toString();
        }

        double idf(String t) {
            long df = docs.values().stream().filter(d -> d.contains(t)).count();
            return Math.log((1.0 + docs.size()) / (1.0 + df)) + 1.0;
        }

        String prefix(String p) {
            String lp = p.replace('’', '\'').toLowerCase(Locale.ROOT);
            return numbered(cf().entrySet().stream().filter(e -> e.getKey().startsWith(lp)).sorted(byCount()).limit(5).toList(),
                    "no suggestions");
        }

        String top(int k) {
            return numbered(cf().entrySet().stream().sorted(byCount()).limit(k).toList(), "no terms");
        }

        static String numbered(List<Map.Entry<String, Long>> list, String none) {
            if (list.isEmpty()) return none;
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < list.size(); i++)
                b.append(i == 0 ? "" : "\n").append(i + 1).append(". ").append(list.get(i).getKey()).append(' ').append(list.get(i).getValue());
            return b.toString();
        }

        static Comparator<Map.Entry<String, Long>> byCount() {
            return Comparator.comparingLong((Map.Entry<String, Long> e) -> -e.getValue()).thenComparing(Map.Entry::getKey);
        }
    }

    /** A small random corpus with mixed case, apostrophes, accents and stop words. */
    static Map<String, String> randomCorpus(long seed, int docs) {
        String[] vocab = {"Fox", "fox", "FOX", "fox's", "grapes", "sour", "Lion", "lion", "mouse", "mice", "café",
                "Café", "crow", "cheese", "crown", "crowd", "the", "and", "of", "a", "wolf", "lamb", "lambs'",
                "don’t", "tortoise", "hare", "race", "raced", "racer", "1867", "ant", "ants", "grass", "grasshopper"};
        Random r = new Random(seed);
        Map<String, String> files = new TreeMap<>();
        for (int d = 0; d < docs; d++) {
            StringBuilder t = new StringBuilder();
            int n = 3 + r.nextInt(25);
            for (int i = 0; i < n; i++) {
                // squaring skews the choice towards the front of the list, like real word frequencies
                double x = r.nextDouble();
                t.append(vocab[(int) (x * x * vocab.length)]).append(r.nextInt(6) == 0 ? ", " : " ");
            }
            files.put(String.format("doc%02d.txt", d), t.toString());
        }
        return files;
    }

    // ───────────────────────────── stage 1 · load and stats ─────────────────────────────
    static final Map<String, String> SMALL = Map.of(
            "alpha.txt", "The Fox jumped over the lazy dog. The DOG slept!",
            "beta.txt", "A dog, a fox, and 3 hens: 3 hens and a fox.",
            "gamma.txt", "Hens sleep. Foxes never sleep. -- the end --",
            "notes.md", "not a document: ignored because it doesn't end in .txt",
            "sub/delta.txt", "inside a sub-folder: ignored, the load is not recursive");

    static void stage1() throws IOException {
        int st = 1;
        Run none = run(List.of(), "", 30);
        if (none.exit == 2 && none.err.contains("usage: search <corpus-dir>") && none.out.isEmpty())
            pass(st, "no arguments → usage on stderr, exit code 2");
        else fail(st, "no arguments → usage on stderr, exit code 2",
                "command:  search   (no arguments)\nexpected: exit 2, stderr containing \"usage: search <corpus-dir>\", nothing on stdout\n"
                        + "got:      exit " + none.exit + ", stderr \"" + firstLines(none.err, 3) + "\", stdout \"" + firstLines(none.out, 2) + "\""
                        + "\nhint: if this is a stack trace saying TODO, start with Milestone 1: argument checks, then loading the folder.");

        String missing = work.resolve("no-such-folder").toString();
        Run bad = run(List.of(missing), "", 30);
        if (bad.exit == 2 && bad.err.contains("error: not a directory: " + missing))
            pass(st, "a missing folder → \"error: not a directory: …\" on stderr, exit 2");
        else fail(st, "a missing folder → \"error: not a directory: …\" on stderr, exit 2",
                "command:  search " + missing + "\nexpected: exit 2, stderr \"error: not a directory: " + missing + "\"\n"
                        + "got:      exit " + bad.exit + ", stderr \"" + firstLines(bad.err, 3) + "\"");

        Path small = corpus("small", SMALL);
        // alpha: fox jumped over lazy dog dog slept (7)   beta: dog fox 3 hens 3 hens fox (7)
        // gamma: hens sleep foxes never sleep end (6)      → 20 tokens, 12 distinct terms
        Session s = new Session(small, List.of("stats", "doc alpha", "doc beta", "doc nope", "doc", "frobnicate now"), 30);
        String hint = "only *.txt files directly inside the folder are documents; split on anything that isn't a letter or digit, "
                + "lower-case, then drop the stop words.";
        expectStartup(st, "startup line counts documents, tokens and distinct terms", s,
                "loaded 3 documents, 20 tokens, 12 terms", hint);
        expect(st, "stats", s, 0, """
                documents: 3
                tokens: 20
                terms: 12
                average document length: 6.67""", hint + " The average has 2 decimals, Locale.ROOT.");
        expect(st, "doc <name>: length, distinct terms, top 3 by count", s, 1, """
                doc: alpha
                tokens: 7
                terms: 6
                top terms: dog (2), fox (1), jumped (1)""", "top terms: most frequent first, ties in alphabetical order.");
        expect(st, "doc: ties in the top 3 are alphabetical", s, 2, """
                doc: beta
                tokens: 7
                terms: 4
                top terms: 3 (2), fox (2), hens (2)""", "\"3\" sorts before \"fox\": compare strings with compareTo.");
        expect(st, "doc with an unknown name", s, 3, "error: no document named 'nope'", null);
        expect(st, "doc with no name", s, 4, "error: usage: doc <name>", null);
        expect(st, "an unknown command is an error, and the program carries on", s, 5, "error: unknown command 'frobnicate'",
                "print the error on stdout, then the empty line, and keep reading commands.");

        Session b = new Session(small, List.of("stats", "", "   ", "stats"), 30);
        if (b.blocks.size() == 3 && b.block(0) != null && b.block(0).equals(b.block(1)))
            pass(st, "blank input lines produce no output");
        else fail(st, "blank input lines produce no output",
                "input:    stats, (empty line), (spaces), stats\nexpected: two stats blocks and nothing for the blank lines\n"
                        + "got:      " + b.blocks.size() + " blocks: " + show(String.join("\n---\n", b.blocks)) + b.trouble());

        Run q = run(List.of(small.toString()), "stats\nquit\nstats\n", 30);
        long statsBlocks = q.out.lines().filter(l -> l.startsWith("documents:")).count();
        if (q.exit == 0 && !q.timedOut && statsBlocks == 1)
            pass(st, "quit stops the program with exit code 0");
        else fail(st, "quit stops the program with exit code 0",
                "input:    stats, quit, stats\nexpected: one stats block, then exit 0\ngot:      exit " + q.exit
                        + (q.timedOut ? " (timed out)" : "") + ", " + statsBlocks + " stats block(s)");

        Path empty = corpus("empty", Map.of("readme.md", "no txt files here"));
        Session e = new Session(empty, List.of("stats"), 30);
        expectStartup(st, "an empty folder loads 0 documents", e, "loaded 0 documents, 0 tokens, 0 terms", null);
        expect(st, "stats on an empty corpus (no division by zero)", e, 0, """
                documents: 0
                tokens: 0
                terms: 0
                average document length: 0.00""", "with 0 documents the average is defined as 0.00.");
    }

    // ───────────────────────────── stage 2 · tokenisation ─────────────────────────────
    static void stage2(Path aesop) throws IOException {
        int st = 2;
        String[][] cases = {
                {"tokens Hello, World!", "hello world", "lower-case every token with toLowerCase(Locale.ROOT) / lowercase()."},
                {"tokens The cat AND the hat", "cat hat", "drop stop words after lower-casing, so THE and The go too."},
                {"tokens well-known e-mail x_y", "well known e mail x y", "a hyphen or underscore is not a letter or digit, so it separates tokens."},
                {"tokens don't dogs' 'tis", "don't dogs tis", "keep an apostrophe only when a letter or digit is on BOTH sides."},
                {"tokens rock'n'roll x''y ''", "rock'n'roll x y", "two apostrophes in a row: neither has letters on both sides."},
                {"tokens Æsop’s FABLES", "æsop's fables", "replace ’ (U+2019) with ' before anything else; Æ lower-cases to æ."},
                {"tokens naïve CAFÉ Ελλάδα", "naïve café ελλάδα", "use Character.isLetterOrDigit, not [a-z0-9]: accented and Greek letters are letters."},
                {"tokens C3PO 3.14 2026", "c3po 3 14 2026", "digits are token characters; a dot separates."},
                {"tokens it's its not", "it's not", "\"its\" is a stop word, \"it's\" and \"not\" are not."},
                {"tokens 𝐀𝐁c!", "𝐀𝐁c", "𝐀 (U+1D400) is ONE letter stored as TWO chars. Iterate code points (String.codePoints()), not chars."},
                {"tokens -- ... ’’ ' !", "(none)", "print (none) when no tokens are left."},
                {"tokens a an and are as at be but by for from had has have he her him his i in is it its me my of on or she so "
                        + "that the their them then there they this to was we were what when which who will with you your", "(none)",
                        "all 50 stop words from the spec must be dropped; compare your list with the spec."},
                {"tokens", "(none)", "\"tokens\" with no text prints (none)."},
        };
        List<String> commands = new ArrayList<>();
        for (String[] c : cases) commands.add(c[0]);
        Path small = corpus("small2", SMALL);
        Session s = new Session(small, commands, 30);
        for (int i = 0; i < cases.length; i++) {
            String in = cases[i][0];
            expect(st, "tokens: " + (in.length() > 40 ? in.substring(7, 40) + "…" : in.substring(Math.min(7, in.length()))),
                    s, i, cases[i][1], cases[i][2]);
        }

        Path cased = corpus("cased", Map.of("tale.txt", "Fox FOX fox’s. The FOX's tale; fox-trot."));
        Session c = new Session(cased, List.of("doc tale"), 30);
        expect(st, "documents use the same tokeniser (case folded, ’ normalised)", c, 0, """
                doc: tale
                tokens: 7
                terms: 4
                top terms: fox (3), fox's (2), tale (1)""", "index tokenize(fileText): Fox, FOX and fox are one term.");

        if (aesop == null) {
            note("NOTE stage 2 · Aesop corpus not found: run from projects/m2 or pass --corpus DIR (skipped 2 checks)");
            return;
        }
        Oracle o = new Oracle(aesop);
        Session a = new Session(aesop, List.of("doc the-lion-and-the-mouse"), 60);
        expectStartup(st, "Aesop corpus: startup line", a, o.loaded(),
                "the fables use ’ in words like Lion’s, and Æ: check the apostrophe and Unicode rules.");
        expect(st, "Aesop corpus: doc the-lion-and-the-mouse", a, 0, o.doc("the-lion-and-the-mouse"), null);
    }

    // ───────────────────────────── stage 3 · queries and ranking ─────────────────────────────
    // D = 5. df: tale 5, fox 3, grapes 2, sour 2, milk 1, eel 1.
    // idf(fox) = ln(6/4)+1 = 1.4055   idf(grapes) = idf(sour) = ln(6/3)+1 = 1.6931   idf(milk) = ln(6/2)+1 = 2.0986   idf(tale) = 1
    static final Map<String, String> FABLES = Map.of(
            "ant.txt", "Tale: the fox and the grapes. Fox!",
            "bee.txt", "A tale. A sour fox.",
            "cat.txt", "Tale tale. Sour grapes? Sour milk.",
            "dog.txt", "fox, a tale",
            "eel.txt", "The tale of the eel");

    static void stage3(Path aesop) throws IOException {
        int st = 3;
        Path dir = corpus("fables", FABLES);
        Session s = new Session(dir, List.of("q fox", "q tale", "q fox grapes", "q FOX Fox fox", "q fox milk", "q zebra",
                "q the of and", "or sour grapes", "or milk eel zebra", "q"), 30);
        String formula = "score(d) = Σ tf(t,d) × idf(t), idf(t) = ln((1 + D) / (1 + df(t))) + 1 with the NATURAL log (Math.log, ln).";
        expect(st, "q fox: tf-idf scores, ties broken by name", s, 0, """
                results: 3
                1. ant 2.8109
                2. bee 1.4055
                3. dog 1.4055""", "idf(fox) = ln(6/4) + 1 = 1.4055, ant has fox twice. " + formula
                + " Equal scores (at 4 decimals) go in ascending name order: bee before dog.");
        expect(st, "q tale: a term in every document still has idf 1 (smoothing)", s, 1, """
                results: 5
                1. cat 2.0000
                2. ant 1.0000
                3. bee 1.0000
                4. dog 1.0000
                5. eel 1.0000""", "with D = 5 and df = 5, idf = ln(6/6) + 1 = 1. Scores of 0.0000 mean the +1 smoothing is missing: "
                + "use ln((1 + D) / (1 + df)) + 1, not ln(D / df). "
                + "The four 1.0000 scores tie, so they must be in ascending name order.");
        expect(st, "q fox grapes: AND needs every term, scores add up", s, 2, """
                results: 1
                1. ant 4.5041""", "2 × 1.4055 + 1 × 1.6931. Only ant has both words.");
        expect(st, "q FOX Fox fox: query terms are tokenised and de-duplicated", s, 3, """
                results: 3
                1. ant 2.8109
                2. bee 1.4055
                3. dog 1.4055""", "tokenise the query like a document, then drop repeated terms (keep the first).");
        expect(st, "q fox milk: no document has both", s, 4, "results: 0", null);
        expect(st, "q zebra: unknown term", s, 5, "results: 0", "a term with no postings matches nothing; don't crash on a missing key.");
        expect(st, "q with only stop words", s, 6, "error: no searchable terms in query", null);
        expect(st, "or sour grapes: union, ranked, ties by name", s, 7, """
                results: 3
                1. cat 5.0794
                2. ant 1.6931
                3. bee 1.6931""", "OR sums tf × idf over the query terms each document contains. " + formula);
        expect(st, "or milk eel zebra: equal scores in name order", s, 8, """
                results: 2
                1. cat 2.0986
                2. eel 2.0986""", "idf(milk) = idf(eel) = ln(6/2) + 1 = 2.0986, so cat and eel tie; cat comes first by name.");
        expect(st, "q with no terms at all", s, 9, "error: no searchable terms in query", null);

        // More than ten matches: only the top 10 lines, but the full count.
        Map<String, String> many = new TreeMap<>();
        for (int i = 1; i <= 12; i++) many.put(String.format("d%02d.txt", i), ("word ").repeat(1 + (i * 7) % 4) + "filler" + i);
        Session m = new Session(corpus("many", many), List.of("q word"), 30);
        Oracle om = new Oracle(work.resolve("many"));
        expect(st, "more than 10 matches: count them all, print the top 10", m, 0, om.query("word", true),
                "\"results: N\" counts every match; only the first 10 ranked lines are printed.");

        // Differential checks against the naive oracle on random corpora.
        for (long seed : new long[] {1, 2}) {
            Path dir2 = corpus("random" + seed, randomCorpus(seed, 30));
            Oracle o = new Oracle(dir2);
            Random r = new Random(seed * 31);
            String[] words = {"fox", "Lion", "café", "mouse", "grapes", "crow", "fox's", "hare", "race", "1867", "lambs", "ants", "zebra"};
            List<String> cmds = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                String q = words[r.nextInt(words.length)] + (r.nextBoolean() ? " " + words[r.nextInt(words.length)] : "");
                cmds.add((i % 2 == 0 ? "q " : "or ") + q);
            }
            Session rs = new Session(dir2, cmds, 30);
            int bad = -1;
            for (int i = 0; i < cmds.size() && bad < 0; i++) {
                String want = cmds.get(i).startsWith("q ") ? o.query(cmds.get(i).substring(2), true) : o.query(cmds.get(i).substring(3), false);
                if (!want.equals(rs.block(i))) bad = i;
            }
            if (bad < 0) pass(st, "12 random AND/OR queries on generated corpus #" + seed + " match the reference");
            else {
                String c = rs.commands.get(bad);
                expect(st, "12 random AND/OR queries on generated corpus #" + seed + " match the reference (corpus in " + dir2 + ")",
                        rs, bad, c.startsWith("q ") ? o.query(c.substring(2), true) : o.query(c.substring(3), false),
                        "the generated corpus is kept (path above): run this query by hand against it.");
            }
        }

        if (aesop != null) {
            Oracle o = new Oracle(aesop);
            List<String> cmds = List.of("q fox grapes", "or lion mouse", "q Æsop’s", "q wolf lamb", "or tortoise hare race", "q crow cheese");
            Session a = new Session(aesop, cmds, 60);
            for (int i = 0; i < cmds.size(); i++) {
                String c = cmds.get(i);
                expect(st, "Aesop: " + c, a, i, c.startsWith("q ") ? o.query(c.substring(2), true) : o.query(c.substring(3), false), null);
            }
        } else note("NOTE stage 3 · Aesop corpus not found: run from projects/m2 or pass --corpus DIR (skipped 6 checks)");
    }

    // ───────────────────────────── stage 4 · prefix and top-k ─────────────────────────────
    // cf: card 5, cat 4, car 3, care 2, carp 2, cart 2, carton 1, dog 1
    static final Map<String, String> CARS = Map.of(
            "one.txt", "The card, the card and the CAR. A cat! Cat. Card card card.",
            "two.txt", "car car care care Carp carp cart cart carton cat cat dog");

    static void stage4(Path aesop) throws IOException {
        int st = 4;
        Path dir = corpus("cars", CARS);
        Session s = new Session(dir, List.of("prefix car", "prefix ca", "prefix carto", "prefix CAR", "prefix zzz", "prefix",
                "top 3", "top 6", "top 100", "top 0", "top many"), 30);
        expect(st, "prefix car: includes the word car itself, top 5 by frequency", s, 0, """
                1. card 5
                2. car 3
                3. care 2
                4. carp 2
                5. cart 2""", "the node for the prefix itself can be a word: collect it too (car is a term!). "
                + "Then most frequent first, ties alphabetical, at most 5.");
        expect(st, "prefix ca: a prefix that is not itself a word", s, 1, """
                1. card 5
                2. cat 4
                3. car 3
                4. care 2
                5. carp 2""", "\"ca\" is only a path in the trie, not a term: never list it.");
        expect(st, "prefix carto: only real terms are suggested", s, 2, "1. carton 1",
                "list a node only if a term ENDS there (count > 0), not every node you pass through.");
        expect(st, "prefix CAR: the prefix is lower-cased", s, 3, """
                1. card 5
                2. car 3
                3. care 2
                4. carp 2
                5. cart 2""", null);
        expect(st, "prefix zzz: nothing matches", s, 4, "no suggestions", "an unknown prefix walks off the trie: return an empty list, don't crash.");
        expect(st, "prefix with no argument", s, 5, "error: usage: prefix <p>", null);
        expect(st, "top 3", s, 6, """
                1. card 5
                2. cat 4
                3. car 3""", "count every occurrence in the whole corpus (corpus frequency), not the number of documents.");
        expect(st, "top 6: ties in alphabetical order", s, 7, """
                1. card 5
                2. cat 4
                3. car 3
                4. care 2
                5. carp 2
                6. cart 2""", "care, carp and cart all have 2: alphabetical. With a min-heap of size k, the root must be the "
                + "WORST of the current top k: lowest count, and among equal counts the alphabetically LAST.");
        expect(st, "top 100: k larger than the vocabulary prints every term", s, 8, """
                1. card 5
                2. cat 4
                3. car 3
                4. care 2
                5. carp 2
                6. cart 2
                7. carton 1
                8. dog 1""", null);
        expect(st, "top 0 is an error", s, 9, "error: usage: top <k> (k is a positive integer)", null);
        expect(st, "top many is an error", s, 10, "error: usage: top <k> (k is a positive integer)", null);

        for (long seed : new long[] {3, 4}) {
            Path dir2 = corpus("random" + seed, randomCorpus(seed, 25));
            Oracle o = new Oracle(dir2);
            List<String> cmds = List.of("prefix c", "prefix cr", "prefix crow", "prefix f", "prefix fox", "prefix lamb", "prefix r",
                    "prefix race", "prefix g", "top 1", "top 7", "top 40");
            Session rs = new Session(dir2, cmds, 30);
            int bad = -1;
            for (int i = 0; i < cmds.size() && bad < 0; i++) {
                String c = cmds.get(i);
                String want = c.startsWith("prefix ") ? o.prefix(c.substring(7)) : o.top(Integer.parseInt(c.substring(4)));
                if (!want.equals(rs.block(i))) bad = i;
            }
            if (bad < 0) pass(st, "12 random prefix/top commands on generated corpus #" + seed + " match the reference");
            else {
                String c = cmds.get(bad);
                expect(st, "12 random prefix/top commands on generated corpus #" + seed + " match the reference (corpus in " + dir2 + ")",
                        rs, bad, c.startsWith("prefix ") ? o.prefix(c.substring(7)) : o.top(Integer.parseInt(c.substring(4))), null);
            }
        }

        if (aesop != null) {
            Oracle o = new Oracle(aesop);
            List<String> cmds = List.of("prefix fo", "prefix lion", "prefix æ", "prefix wolf", "top 12");
            Session a = new Session(aesop, cmds, 60);
            for (int i = 0; i < cmds.size(); i++) {
                String c = cmds.get(i);
                expect(st, "Aesop: " + c, a, i, c.startsWith("prefix ") ? o.prefix(c.substring(7)) : o.top(12), null);
            }
        } else note("NOTE stage 4 · Aesop corpus not found: run from projects/m2 or pass --corpus DIR (skipped 5 checks)");
    }

    // ───────────────────────────── stage 5 · performance ─────────────────────────────
    static final int[] SIZES = {2_000, 4_000, 8_000, 16_000};
    static final int PLANTED = 25;
    static final int REPS = 2_000;

    /** n documents of ~40 words; "common" in half of them; "rarealpha rarebeta" in exactly 25, whatever n is. */
    static Path scaledCorpus(int n) throws IOException {
        Path dir = work.resolve("scaled" + n);
        Files.createDirectories(dir);
        Random r = new Random(2026);
        String[] syllables = {"ka", "lo", "mi", "nu", "pe", "ra", "si", "to", "vu", "ze"};
        for (int d = 0; d < n; d++) {
            StringBuilder t = new StringBuilder();
            boolean planted = d % (n / PLANTED) == 0;
            if (planted || r.nextBoolean()) t.append("common ");
            if (planted) t.append("rarealpha rarebeta ");
            for (int i = 0; i < 40; i++) {
                double x = r.nextDouble();
                int w = (int) (x * x * 3000); // ~3000-word vocabulary, skewed like real text
                t.append(syllables[w % 10]).append(syllables[w / 10 % 10]).append(syllables[w / 100 % 10]).append(w / 1000).append(' ');
            }
            Files.writeString(dir.resolve(String.format("doc%05d.txt", d)), t.toString());
        }
        return dir;
    }

    static void stage5() throws IOException {
        int st = 5;
        String query = "common rarealpha rarebeta";
        double[] best = new double[SIZES.length];
        note("doubling experiment: bench " + REPS + " × q " + query + "  (best of 3; 'common' is in half the documents)");
        for (int k = 0; k < SIZES.length; k++) {
            int n = SIZES[k];
            Path dir = scaledCorpus(n);
            String bench = "bench " + REPS + " " + query;
            Session s = new Session(dir, List.of(bench, bench, bench), 120);
            deleteTree(dir); // thousands of files: never keep these
            best[k] = Double.MAX_VALUE;
            String problem = null;
            for (int i = 0; i < 3; i++) {
                String b = s.block(i);
                java.util.regex.Matcher mt = b == null ? null
                        : java.util.regex.Pattern.compile("bench: " + REPS + " queries, (\\d+) results, (\\d+\\.\\d{3}) ms").matcher(b);
                if (mt == null || !mt.matches()) {
                    problem = "input:    " + bench + "\nexpected: bench: " + REPS + " queries, " + PLANTED + " results, <ms with 3 decimals> ms\n"
                            + "got:      " + show(b) + s.trouble();
                    break;
                }
                if (Integer.parseInt(mt.group(1)) != PLANTED) {
                    problem = "input:    " + bench + "\nexpected " + PLANTED + " results (exactly " + PLANTED
                            + " documents contain rarealpha and rarebeta), got: " + b;
                    break;
                }
                best[k] = Math.min(best[k], Double.parseDouble(mt.group(2)));
            }
            if (problem != null) {
                fail(st, "bench on " + n + " documents", problem);
                return;
            }
            note(String.format(Locale.ROOT, "n = %,6d documents   %9.3f ms   ratio %s   (whole run %.1f s)", n, best[k],
                    k == 0 ? "  -" : String.format(Locale.ROOT, "%4.1f", best[k] / Math.max(best[k - 1], 0.001)), s.run.millis / 1000.0));
        }
        pass(st, "bench prints the planted " + PLANTED + " results at every size");

        double growth = best[SIZES.length - 1] / Math.max(best[0], 0.001);
        // 8× the documents. A scan grows ~8×; an index that starts from the rarest postings list stays ~1×.
        // Generous: pass below 3×, or when even the biggest corpus answers 2,000 queries in under 10 ms (too fast to time reliably).
        String what = String.format(Locale.ROOT, "indexed AND stays sub-linear: 8× the documents → %.1f× the time", growth);
        if (growth < 3.0 || best[SIZES.length - 1] < 10.0) pass(st, what);
        else fail(st, what, String.format(Locale.ROOT, "expected the time to grow less than 3× when the corpus grows 8× (it was %.1f×).%n"
                + "A linear-time query grows about 8×: it looks at every document, or walks the postings of 'common' "
                + "(half the documents).%nhint: answer AND from the postings list of the RAREST term (smallest df), "
                + "and check the other terms with O(1) lookups. Never loop over all documents in q.", growth));
    }

    // ───────────────────────────── main ─────────────────────────────
    public static void main(String[] args) throws Exception {
        int stages = 5;
        Path aesop = null;
        String launcherArg = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--stage" -> stages = Integer.parseInt(args[++i]);
                case "--corpus" -> aesop = Path.of(args[++i]);
                default -> launcherArg = args[i];
            }
        }
        if (launcherArg == null || stages < 1 || stages > 5) {
            System.out.println("usage: java check/Check.java <path-to-launcher> [--stage 1..5] [--corpus DIR]");
            System.out.println("       the launcher is build/install/search/bin/search, made by ./gradlew installDist");
            System.exit(2);
        }
        launcher = Path.of(launcherArg).toAbsolutePath();
        if (!Files.isRegularFile(launcher) || !Files.isExecutable(launcher)) {
            System.out.println("FAIL stage 1 · the launcher exists");
            System.out.println("      no executable file at " + launcher);
            System.out.println("      hint: run ./gradlew installDist in your project, then pass build/install/search/bin/search");
            System.exit(1);
        }
        if (aesop == null) {
            for (String c : new String[] {"corpus", "../corpus", "projects/m2/corpus"})
                if (Files.isDirectory(Path.of(c))) { aesop = Path.of(c).toAbsolutePath(); break; }
        }
        work = Files.createTempDirectory("m2check");
        try {
            stage1();
            if (stages >= 2) stage2(aesop);
            if (stages >= 3) stage3(aesop);
            if (stages >= 4) stage4(aesop);
            if (stages >= 5) stage5();
        } finally {
            if (failed == 0) deleteTree(work);
            else System.out.println("\n(test corpora kept in " + work + " so you can rerun a failing command by hand)");
        }
        System.out.println();
        System.out.println(failed == 0
                ? "ALL " + passed + " CHECKS PASSED (stages 1–" + stages + ")"
                : passed + " passed, " + failed + " FAILED (stages 1–" + stages + "). Fix the first FAIL first: later ones often follow from it.");
        System.exit(failed == 0 ? 0 : 1);
    }

    static void deleteTree(Path root) throws IOException {
        try (var walk = Files.walk(root)) {
            for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(p);
        }
    }
}
