import java.util.*;

// Generates the windowviz.js specs from real runs of the lesson's algorithms.
// usage: java WindowTrace.java pointers|board|trie   (prints one JSON spec)
public class WindowTrace {
    static String q(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
    static String arr(List<String> xs) { return "[" + String.join(",", xs) + "]"; }
    static String strs(Collection<?> xs) { List<String> o = new ArrayList<>(); for (Object x : xs) o.add(q(String.valueOf(x))); return arr(o); }
    static String row(String label, Collection<?> items) { return "[" + q(label) + "," + strs(items) + "]"; }
    static String c(String s) { return "<code>" + s + "</code>"; }

    // ---- mode 1: pair sum with two pointers --------------------------------------------
    static String pairMode() {
        int[] a = {1, 3, 4, 6, 8, 11, 15};
        int target = 14;
        List<String> steps = new ArrayList<>();
        int lo = 0, hi = a.length - 1;
        steps.add(step(Map.of("lo", lo, "hi", hi), lo, hi, null, List.of(row("TARGET", List.of(target))),
                "Sorted array, target " + target + ". Start with " + c("lo") + " at the smallest value and " + c("hi") + " at the largest."));
        while (lo < hi) {
            int sum = a[lo] + a[hi];
            String note;
            if (sum == target) {
                steps.add(step(Map.of("lo", lo, "hi", hi), lo, hi, List.of(lo, hi), List.of(row("SUM", List.of(a[lo] + " + " + a[hi] + " = " + sum))),
                        a[lo] + " + " + a[hi] + " = " + sum + ": found it, at indexes " + lo + " and " + hi + ". Three checks instead of up to 21 pairs."));
                break;
            } else if (sum < target) {
                note = a[lo] + " + " + a[hi] + " = " + sum + " is too small. Even the biggest partner left (" + a[hi] + ") isn't enough for " + a[lo] + ", so no pair uses " + a[lo] + ": move " + c("lo") + " right.";
                steps.add(step(Map.of("lo", lo, "hi", hi), lo, hi, null, List.of(row("SUM", List.of(sum + " < " + target))), note));
                lo++;
            } else {
                note = a[lo] + " + " + a[hi] + " = " + sum + " is too big. Even the smallest partner left (" + a[lo] + ") is too much for " + a[hi] + ", so no pair uses " + a[hi] + ": move " + c("hi") + " left.";
                steps.add(step(Map.of("lo", lo, "hi", hi), lo, hi, null, List.of(row("SUM", List.of(sum + " > " + target))), note));
                hi--;
            }
        }
        return mode("Pair sum · two pointers", a, steps);
    }

    // ---- mode 2: longest substring without repeats ---------------------------------------
    static String uniqueMode() {
        String s = "abcabcbb";
        List<String> steps = new ArrayList<>();
        Map<Character, Integer> last = new LinkedHashMap<>();
        int left = 0, bestStart = 0, bestLen = 0;
        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            Integer prev = last.get(ch);
            String note;
            if (prev != null && prev >= left) {
                note = "'" + ch + "' is already in the window, at index " + prev + ". Jump " + c("left") + " to " + (prev + 1) + ", just past it.";
                left = prev + 1;
            } else note = "'" + ch + "' is new to the window: grow it on the right.";
            last.put(ch, right);
            if (right - left + 1 > bestLen) { bestLen = right - left + 1; bestStart = left; note += " New best: length " + bestLen + "."; }
            List<String> seen = new ArrayList<>();
            for (var e : last.entrySet()) seen.add(e.getKey() + "→" + e.getValue());
            List<Integer> best = new ArrayList<>();
            for (int i = bestStart; i < bestStart + bestLen; i++) best.add(i);
            steps.add(step(Map.of("left", left, "right", right), left, right, best,
                    List.of(row("LAST SEEN", seen), row("BEST", List.of("\"" + s.substring(bestStart, bestStart + bestLen) + "\""))), note));
        }
        String[] cells = new String[s.length()];
        for (int i = 0; i < cells.length; i++) cells[i] = String.valueOf(s.charAt(i));
        return mode("Longest without repeats · window", cells, steps);
    }

    // ---- mode 3: sliding-window maximum with a monotonic deque ---------------------------
    static String dequeMode() {
        int[] a = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        List<String> steps = new ArrayList<>();
        Deque<Integer> dq = new ArrayDeque<>();
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            List<String> notes = new ArrayList<>();
            if (!dq.isEmpty() && dq.peekFirst() <= i - k) notes.add("index " + dq.pollFirst() + " slid out of the window, so drop it from the front");
            List<Integer> popped = new ArrayList<>();
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) popped.add(a[dq.pollLast()]);
            if (!popped.isEmpty()) notes.add("pop " + popped + " from the back: they're ≤ " + a[i] + " and leave before it, so they can never be a maximum again");
            dq.addLast(i);
            notes.add("push " + a[i] + " (index " + i + ")");
            if (i >= k - 1) { out.add(a[dq.peekFirst()]); notes.add("the front, " + a[dq.peekFirst()] + ", is the window's maximum"); }
            List<String> dqv = new ArrayList<>();
            for (int j : dq) dqv.add(a[j] + " @" + j);
            String note = String.join("; ", notes);
            note = Character.toUpperCase(note.charAt(0)) + note.substring(1) + ".";
            steps.add(step(Map.of("i", i), Math.max(0, i - k + 1), i, dq.stream().toList(),
                    List.of(row("DEQUE", dqv), row("MAXES", out)), note));
        }
        String[] cells = new String[a.length];
        for (int i = 0; i < a.length; i++) cells[i] = String.valueOf(a[i]);
        return mode("Window maximum · monotonic deque", cells, steps);
    }

    static String mode(String name, int[] a, List<String> steps) {
        String[] cells = new String[a.length];
        for (int i = 0; i < a.length; i++) cells[i] = String.valueOf(a[i]);
        return mode(name, cells, steps);
    }
    static String mode(String name, String[] cells, List<String> steps) {
        return "{\"name\":" + q(name) + ",\"cells\":" + strs(Arrays.asList(cells)) + ",\"steps\":" + arr(steps) + "}";
    }
    static String step(Map<String, Integer> ptr, int l, int r, List<Integer> mark, List<String> rows, String note) {
        List<String> ps = new ArrayList<>();
        for (var e : new TreeMap<>(ptr).entrySet()) ps.add(q(e.getKey()) + ":" + e.getValue());
        return "{\"ptr\":{" + String.join(",", ps) + "},\"range\":[" + l + "," + r + "],\"mark\":" + (mark == null ? "[]" : mark.toString().replace(" ", ""))
                + ",\"rows\":" + arr(rows) + ",\"note\":" + q(note) + "}";
    }

    // ---- board: 4-queens backtracking -----------------------------------------------------
    static List<String> boardSteps = new ArrayList<>();
    static int sols = 0;
    static String qs(int[] qq) { return Arrays.toString(qq).replace(" ", ""); }

    static void queens(int[] qq, int row, int n) {
        if (row == n) {
            sols++;
            boardSteps.add("{\"q\":" + qs(qq) + ",\"try\":null,\"bad\":false,\"sols\":" + sols + ",\"note\":" + q("All " + n + " rows filled: solution " + sols + ". Record it, then backtrack to look for more.") + "}");
            return;
        }
        for (int col = 0; col < n; col++) {
            String attacker = null;
            for (int r = 0; r < row; r++) {
                if (qq[r] == col) { attacker = "the queen in row " + r + " (same column)"; break; }
                if (Math.abs(qq[r] - col) == row - r) { attacker = "the queen in row " + r + " (same diagonal)"; break; }
            }
            if (attacker != null) {
                boardSteps.add("{\"q\":" + qs(qq) + ",\"try\":[" + row + "," + col + "],\"bad\":true,\"sols\":" + sols + ",\"note\":" + q("Row " + row + ", column " + col + " is attacked by " + attacker + ". Prune: skip it.") + "}");
                continue;
            }
            qq[row] = col;
            boardSteps.add("{\"q\":" + qs(qq) + ",\"try\":[" + row + "," + col + "],\"bad\":false,\"sols\":" + sols + ",\"note\":" + q("Row " + row + ", column " + col + " is safe. Choose it and recurse into row " + (row + 1) + ".") + "}");
            queens(qq, row + 1, n);
            qq[row] = -1;
            boardSteps.add("{\"q\":" + qs(qq) + ",\"try\":null,\"bad\":false,\"sols\":" + sols + ",\"note\":" + q("Back in row " + row + ": un-choose column " + col + " and try the next column.") + "}");
        }
    }

    // ---- trie ------------------------------------------------------------------------------
    static String trie() {
        List<String> steps = new ArrayList<>();
        List<String> words = new ArrayList<>();
        Set<String> prefixes = new HashSet<>();
        steps.add(trieStep(words, "", List.of(), List.of(), "An empty trie is just the root."));
        for (String w : "she sells sea shells by the sea shore".split(" ")) {
            List<String> fresh = new ArrayList<>();
            for (int i = 1; i <= w.length(); i++) if (prefixes.add(w.substring(0, i))) fresh.add(w.substring(0, i));
            boolean dup = words.contains(w);
            if (!dup) words.add(w);
            String note = dup ? "Insert \"" + w + "\": every node already exists and is already marked as a word end. Nothing changes."
                    : fresh.size() == w.length() ? "Insert \"" + w + "\": no shared prefix, so " + fresh.size() + " new nodes. The last one is marked as a word end (double ring)."
                    : "Insert \"" + w + "\": walk the existing prefix \"" + w.substring(0, w.length() - fresh.size()) + "\", then add " + fresh.size() + " new node" + (fresh.size() == 1 ? "" : "s") + ". Shared prefixes are stored once.";
            steps.add(trieStep(words, w, fresh, List.of(), note + " Nodes so far: " + (prefixes.size() + 1) + "."));
        }
        List<String> found = new ArrayList<>();
        for (String w : new TreeSet<>(words)) if (w.startsWith("sh")) found.add(w);
        steps.add(trieStep(words, "sh", List.of(), List.of(), "Query withPrefix(\"sh\"): follow s, then h. Two steps, however many words the trie holds."));
        steps.add(trieStep(words, "sh", List.of(), found, "Then a depth-first walk below the \"sh\" node collects every word end: " + found + ". The rest of the trie is never visited."));
        steps.add(trieStep(words, "se", List.of(), List.of(), "contains(\"se\"): the path exists but the node isn't a word end, so the answer is false. hasPrefix(\"se\") is true."));
        return "{\"kind\":\"trie\",\"steps\":" + arr(steps) + "}";
    }
    static String trieStep(List<String> words, String path, List<String> fresh, List<String> found, String note) {
        return "{\"words\":" + strs(words) + ",\"path\":" + q(path) + ",\"fresh\":" + strs(fresh) + ",\"found\":" + strs(found) + ",\"note\":" + q(note) + "}";
    }

    public static void main(String[] args) {
        switch (args[0]) {
            case "pointers" -> System.out.println("{\"kind\":\"array\",\"modes\":[" + pairMode() + "," + uniqueMode() + "," + dequeMode() + "]}");
            case "board" -> {
                int[] qq = {-1, -1, -1, -1};
                boardSteps.add("{\"q\":[-1,-1,-1,-1],\"try\":null,\"bad\":false,\"sols\":0,\"note\":" + q("4 queens, one per row. Fill rows top to bottom, trying columns left to right.") + "}");
                queens(qq, 0, 4);
                System.out.println("{\"kind\":\"board\",\"n\":4,\"steps\":" + arr(boardSteps) + "}");
            }
            case "trie" -> System.out.println(trie());
            default -> throw new IllegalArgumentException(args[0]);
        }
    }
}
