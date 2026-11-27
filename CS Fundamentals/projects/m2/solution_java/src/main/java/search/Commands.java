package search;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Runs one command line against an index and returns its output, without the trailing blank line. */
public final class Commands {
    private static final int SHOWN_RESULTS = 10;
    private static final int SUGGESTIONS = 5;

    private final Index index;
    /** Where bench leaves its results, so the JIT can't skip the work as dead code (L16). */
    private static volatile long blackhole;

    public Commands(Index index) { this.index = index; }

    public String run(String line) {
        String[] parts = line.strip().split("\\s+", 2);
        String word = parts[0];
        String rest = parts.length > 1 ? parts[1] : "";
        return switch (word) {
            case "tokens" -> tokens(rest);
            case "stats" -> stats();
            case "doc" -> doc(rest);
            case "q" -> query(rest, true);
            case "or" -> query(rest, false);
            case "prefix" -> prefix(rest);
            case "top" -> top(rest);
            case "bench" -> bench(rest);
            default -> "error: unknown command '" + word + "'";
        };
    }

    private String tokens(String text) {
        List<String> tokens = Tokenizer.tokenize(text);
        return tokens.isEmpty() ? "(none)" : String.join(" ", tokens);
    }

    private String stats() {
        double avg = index.documentCount() == 0 ? 0 : (double) index.tokenCount() / index.documentCount();
        return "documents: " + index.documentCount() + "\n"
                + "tokens: " + index.tokenCount() + "\n"
                + "terms: " + index.termCount() + "\n"
                + "average document length: " + String.format(Locale.ROOT, "%.2f", avg);
    }

    private String doc(String name) {
        if (name.isEmpty()) return "error: usage: doc <name>";
        Index.Document d = index.document(name);
        if (d == null) return "error: no document named '" + name + "'";
        List<Trie.Entry> entries = new ArrayList<>(d.tf().size());
        for (Map.Entry<String, Integer> e : d.tf().entrySet()) entries.add(new Trie.Entry(e.getKey(), e.getValue()));
        List<String> top = new ArrayList<>();
        for (Trie.Entry e : TopK.select(entries, 3, Index.BY_COUNT)) top.add(e.term() + " (" + e.count() + ")");
        return "doc: " + d.name() + "\n"
                + "tokens: " + d.length() + "\n"
                + "terms: " + d.tf().size() + "\n"
                + "top terms: " + (top.isEmpty() ? "(none)" : String.join(", ", top));
    }

    /** Query terms: tokenised like documents, duplicates dropped, first occurrence kept. */
    private static List<String> queryTerms(String text) {
        return new ArrayList<>(new LinkedHashSet<>(Tokenizer.tokenize(text)));
    }

    private String query(String text, boolean and) {
        List<String> terms = queryTerms(text);
        if (terms.isEmpty()) return "error: no searchable terms in query";
        List<Index.Hit> hits = and ? index.and(terms) : index.or(terms);
        StringBuilder out = new StringBuilder("results: " + hits.size());
        int rank = 1;
        for (Index.Hit h : TopK.select(hits, SHOWN_RESULTS, Index.RANKING)) {
            out.append('\n').append(rank++).append(". ").append(h.name()).append(' ')
                    .append(String.format(Locale.ROOT, "%.4f", h.score()));
        }
        return out.toString();
    }

    private String prefix(String text) {
        if (text.isEmpty() || text.chars().anyMatch(Character::isWhitespace)) return "error: usage: prefix <p>";
        String p = text.replace('’', '\'').toLowerCase(Locale.ROOT);
        List<Trie.Entry> best = TopK.select(index.withPrefix(p), SUGGESTIONS, Index.BY_COUNT);
        return best.isEmpty() ? "no suggestions" : numbered(best);
    }

    private String top(String text) {
        int k = positiveInt(text);
        if (k < 1) return "error: usage: top <k> (k is a positive integer)";
        List<Trie.Entry> best = TopK.select(index.termFrequencies(), k, Index.BY_COUNT);
        return best.isEmpty() ? "no terms" : numbered(best);
    }

    private String bench(String text) {
        String[] parts = text.split("\\s+", 2);
        int reps = positiveInt(parts[0]);
        if (reps < 1 || parts.length < 2) return "error: usage: bench <reps> <query>";
        List<String> terms = queryTerms(parts[1]);
        if (terms.isEmpty()) return "error: no searchable terms in query";
        long sink = 0;
        for (int i = 0; i < reps; i++) sink += runOnce(terms);
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++) sink += runOnce(terms);
        long elapsed = System.nanoTime() - start;
        int results = index.and(terms).size();
        blackhole = sink;
        return String.format(Locale.ROOT, "bench: %d queries, %d results, %.3f ms", reps, results, elapsed / 1e6);
    }

    private long runOnce(List<String> terms) {
        List<Index.Hit> hits = index.and(terms);
        return hits.size() + TopK.select(hits, SHOWN_RESULTS, Index.RANKING).size();
    }

    private static String numbered(List<Trie.Entry> entries) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) out.append('\n');
            out.append(i + 1).append(". ").append(entries.get(i).term()).append(' ').append(entries.get(i).count());
        }
        return out.toString();
    }

    /** The text as a positive int, or -1 if it isn't one. */
    private static int positiveInt(String text) {
        try {
            int n = Integer.parseInt(text);
            return n >= 1 ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
