import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;

// Prefix search over every distinct word in Alice's Adventures in Wonderland, three ways.
// Run from this folder with Trie.java next to it:  java TrieMemory.java
public class TrieMemory {
    static List<String> bookWords(String path) throws IOException {
        String text = Files.readString(Path.of(path));
        int start = text.indexOf("***", text.indexOf("*** START OF") + 3) + 3;
        String body = text.substring(start, text.indexOf("*** END OF"));
        return Arrays.stream(body.toLowerCase().split("[^a-z]+")).filter(w -> !w.isEmpty()).toList();
    }

    static double usedMb() throws InterruptedException {
        for (int i = 0; i < 3; i++) { System.gc(); Thread.sleep(50); }
        Runtime rt = Runtime.getRuntime();
        return (rt.totalMemory() - rt.freeMemory()) / 1_048_576.0;
    }

    static long sink = 0;

    static void timeIt(String name, List<String> prefixes, Function<String, List<String>> f) {
        for (int i = 0; i < 3; i++) for (String p : prefixes) sink += f.apply(p).size();   // warm-up (L16)
        long t0 = System.nanoTime();
        for (String p : prefixes) sink += f.apply(p).size();
        double us = (System.nanoTime() - t0) / 1000.0 / prefixes.size();
        System.out.printf("%-12s %8.2f µs per query%n", name, us);
    }

    public static void main(String[] args) throws Exception {
        List<String> all = bookWords("data/alice.txt");
        List<String> distinct = new ArrayList<>(new TreeSet<>(all));
        int letters = distinct.stream().mapToInt(String::length).sum();
        System.out.println(all.size() + " words, " + distinct.size() + " distinct, " + letters + " letters in the distinct words");

        double base = usedMb();
        Trie trie = new Trie();
        distinct.forEach(trie::insert);
        double trieMb = usedMb() - base;
        System.out.printf("trie:       %d nodes, about %.1f MB%n", trie.nodes(), trieMb);

        double base2 = usedMb();
        Map<String, List<String>> byPrefix = new HashMap<>();   // every prefix -> every word that starts with it
        for (String w : distinct)
            for (int end = 1; end <= w.length(); end++)
                byPrefix.computeIfAbsent(w.substring(0, end), k -> new ArrayList<>()).add(w);
        double mapMb = usedMb() - base2;
        int slots = byPrefix.values().stream().mapToInt(List::size).sum();
        int keyChars = byPrefix.keySet().stream().mapToInt(String::length).sum();
        System.out.printf("prefix map: %d keys holding %d chars, %d list slots, about %.1f MB%n", byPrefix.size(), keyChars, slots, mapMb);

        for (String p : List.of("th", "alic", "q", "zz")) {     // same answers?
            List<String> scan = distinct.stream().filter(w -> w.startsWith(p)).toList();
            if (!trie.withPrefix(p).equals(scan) || !byPrefix.getOrDefault(p, List.of()).equals(scan))
                throw new IllegalStateException(p);
        }
        System.out.println("withPrefix(\"alic\") = " + trie.withPrefix("alic"));

        // Time 2,000 lookups of 3-letter prefixes taken from the words themselves.
        List<String> prefixes = new ArrayList<>();
        for (String w : distinct) if (w.length() >= 3) prefixes.add(w.substring(0, 3));
        Collections.shuffle(prefixes, new Random(50));
        List<String> sample = prefixes.subList(0, 2_000);
        timeIt("scan list", sample, p -> distinct.stream().filter(w -> w.startsWith(p)).toList());
        timeIt("trie", sample, trie::withPrefix);
        timeIt("prefix map", sample, p -> byPrefix.getOrDefault(p, List.of()));
        System.out.println("(sink " + sink + ")");
    }
}
