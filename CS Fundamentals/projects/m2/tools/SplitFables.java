// Builds projects/m2/corpus/ from Townsend's "Three Hundred Æsop's Fables" (1867).
//
// Source: Project Gutenberg eBook #21, plain text, fetched 2026-11-21 from the
// Foundation's mirror https://gutenberg.pglaf.org/2/21/21-0.txt
// (sha256 444d101edd13595592266993eb532d3e02b3ef27aa0b7821caaee8ca7fef855f).
// The translation was printed in 1867 and is in the public domain in the United
// States. Section 1.C of the Project Gutenberg License allows copying and
// derivative works of such a text "as long as all references to Project
// Gutenberg are removed", so this tool drops the START/END marker lines and
// keeps only the fables themselves: one file per fable, title first.
//
// Usage: java tools/SplitFables.java <21-0.txt> <out-dir>
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;

public class SplitFables {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8);
        Path out = Path.of(args[1]);
        Files.createDirectories(out);

        // 1. Titles, in order, from the table of contents (indented by one space).
        int contents = lines.indexOf("CONTENTS");
        List<String> titles = new ArrayList<>();
        boolean inFables = false;
        for (int i = contents + 1; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.equals(" FOOTNOTES")) break;
            if (inFables && l.startsWith(" ")) titles.add(l.substring(1));
            if (l.equals(" AESOP’S FABLES")) inFables = true;
        }

        // 2. The body runs from the "AESOP’S FABLES" heading to "FOOTNOTES".
        int start = lines.indexOf("AESOP’S FABLES");
        int end = lines.indexOf("FOOTNOTES");
        if (start < 0 || end < start) throw new IllegalStateException("headings not found");

        // 3. Find each title line in order; the text up to the next title is the fable.
        List<Integer> at = new ArrayList<>();
        int from = start + 1;
        for (String t : titles) {
            int found = -1;
            for (int i = from; i < end; i++) if (lines.get(i).equals(t)) { found = i; break; }
            if (found < 0) throw new IllegalStateException("title not found in body: " + t);
            at.add(found);
            from = found + 1;
        }
        at.add(end);

        Set<String> used = new HashSet<>();
        long bytes = 0;
        for (int k = 0; k < titles.size(); k++) {
            List<String> body = new ArrayList<>(lines.subList(at.get(k) + 1, at.get(k + 1)));
            while (!body.isEmpty() && body.get(0).isBlank()) body.remove(0);
            while (!body.isEmpty() && body.get(body.size() - 1).isBlank()) body.remove(body.size() - 1);
            String text = titles.get(k) + "\n\n" + String.join("\n", body) + "\n";
            if (text.toLowerCase(Locale.ROOT).contains("gutenberg")) throw new IllegalStateException("reference left in " + titles.get(k));
            String slug = slug(titles.get(k));
            String name = slug;
            for (int n = 2; !used.add(name); n++) name = slug + "-" + n;
            Files.writeString(out.resolve(name + ".txt"), text, StandardCharsets.UTF_8);
            bytes += text.getBytes(StandardCharsets.UTF_8).length;
        }
        System.out.printf("wrote %d fables, %d bytes%n", titles.size(), bytes);
    }

    static String slug(String title) {
        String s = title.replace("Æ", "Ae").replace("æ", "ae").replace("’", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
