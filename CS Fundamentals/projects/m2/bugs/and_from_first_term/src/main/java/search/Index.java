package search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The inverted index: term → postings (document, term frequency), plus what the commands need.
 * Built once, then read-only.
 */
public final class Index {

    /** One loaded document. {@code tf} maps each of its terms to how often it occurs. */
    public record Document(String name, int length, Map<String, Integer> tf) {}

    public record Posting(int doc, int tf) {}

    public record Hit(String name, double score) {}

    /** Score descending (compared at the printed 4 dp), then name ascending. */
    public static final Comparator<Hit> RANKING = Comparator
            .comparingLong((Hit h) -> -Math.round(h.score() * 10_000))
            .thenComparing(Hit::name);

    private final List<Document> docs = new ArrayList<>();
    private final Map<String, Document> byName = new HashMap<>();
    private final Map<String, List<Posting>> postings = new HashMap<>();
    private final Map<String, Long> corpusFrequency = new HashMap<>();
    private final Trie trie = new Trie();
    private long totalTokens;

    /** Loads every regular {@code *.txt} file directly inside {@code dir}, in name order. */
    public static Index load(Path dir) throws IOException {
        List<Path> files;
        try (Stream<Path> list = Files.list(dir)) {
            files = list.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".txt"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
        }
        Index index = new Index();
        for (Path file : files) {
            String name = file.getFileName().toString();
            index.add(name.substring(0, name.length() - ".txt".length()),
                    Files.readString(file, StandardCharsets.UTF_8));
        }
        index.finish();
        return index;
    }

    /** Builds an index from (name, text) pairs: handy for unit tests. */
    public static Index of(Map<String, String> namedTexts) {
        Index index = new Index();
        namedTexts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> index.add(e.getKey(), e.getValue()));
        index.finish();
        return index;
    }

    private Index() {}

    private void add(String name, String text) {
        List<String> tokens = Tokenizer.tokenize(text);
        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) tf.merge(t, 1, Integer::sum);
        int id = docs.size();
        Document doc = new Document(name, tokens.size(), Map.copyOf(tf));
        docs.add(doc);
        byName.put(name, doc);
        // Documents arrive in id order, so every postings list stays sorted by document.
        tf.forEach((term, count) -> {
            postings.computeIfAbsent(term, k -> new ArrayList<>()).add(new Posting(id, count));
            corpusFrequency.merge(term, (long) count, Long::sum);
        });
        totalTokens += tokens.size();
    }

    private void finish() {
        corpusFrequency.forEach(trie::add);
    }

    public int documentCount() { return docs.size(); }
    public long tokenCount() { return totalTokens; }
    public int termCount() { return postings.size(); }

    /** The named document, or null. */
    public Document document(String name) { return byName.get(name); }

    public int df(String term) {
        List<Posting> p = postings.get(term);
        return p == null ? 0 : p.size();
    }

    /** idf(t) = ln((1 + D) / (1 + df(t))) + 1: smoothed, so a term in every document still counts 1. */
    public double idf(String term) {
        return Math.log((1.0 + docs.size()) / (1.0 + df(term))) + 1.0;
    }

    /**
     * Documents containing every term. Walks only the rarest term's postings and checks the
     * others with O(1) hash lookups, so the cost is O(min df × |terms|), not O(documents).
     */
    public List<Hit> and(List<String> terms) {
        List<String> byDf = new ArrayList<>(terms);
        // BUG: no sort, so it walks the FIRST term's postings even when that term is common
        List<Posting> rarest = postings.getOrDefault(byDf.getFirst(), List.of());
        List<Hit> hits = new ArrayList<>();
        for (Posting p : rarest) {
            Document d = docs.get(p.doc());
            boolean all = true;
            for (String t : byDf) if (!d.tf().containsKey(t)) { all = false; break; }
            if (all) hits.add(new Hit(d.name(), score(d, terms)));
        }
        return hits;
    }

    /** Documents containing at least one term: the union of their postings, O(Σ df). */
    public List<Hit> or(List<String> terms) {
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (String t : terms) for (Posting p : postings.getOrDefault(t, List.of())) ids.add(p.doc());
        List<Hit> hits = new ArrayList<>(ids.size());
        for (int id : ids) hits.add(new Hit(docs.get(id).name(), score(docs.get(id), terms)));
        return hits;
    }

    /** score(d) = Σ tf(t, d) × idf(t), summed in query order. */
    private double score(Document d, List<String> terms) {
        double s = 0;
        for (String t : terms) s += d.tf().getOrDefault(t, 0) * idf(t);
        return s;
    }

    public List<Trie.Entry> withPrefix(String prefix) { return trie.withPrefix(prefix); }

    public Iterable<Trie.Entry> termFrequencies() {
        return () -> corpusFrequency.entrySet().stream()
                .map(e -> new Trie.Entry(e.getKey(), e.getValue())).iterator();
    }

    /** Most frequent first, ties alphabetical: the order for prefix, top and doc. */
    public static final Comparator<Trie.Entry> BY_COUNT = Comparator
            .comparingLong((Trie.Entry e) -> -e.count())
            .thenComparing(Trie.Entry::term);
}
