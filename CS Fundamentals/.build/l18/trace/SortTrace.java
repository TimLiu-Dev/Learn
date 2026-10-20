import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Runs the lesson's insertion, merge and quick sorts on one small array, recording
// every compare and every write. Prints JSON for assets/sortviz.js.
public class SortTrace {
    static int[] a;
    static long compares;
    static List<String> steps;

    static String arr() { return Arrays.toString(a).replace(" ", ""); }

    // marks: index -> kind (cmp | write | pivot | key); range: active [lo, hi] or null
    static void step(Map<Integer, String> marks, int lo, int hi, String note) {
        StringBuilder m = new StringBuilder("{");
        marks.forEach((k, v) -> m.append(m.length() > 1 ? "," : "").append('"').append(k).append("\":\"").append(v).append('"'));
        m.append('}');
        steps.add(String.format("{\"a\":%s,\"m\":%s,\"r\":%s,\"c\":%d,\"n\":\"%s\"}",
            arr(), m, lo < 0 ? "null" : "[" + lo + "," + hi + "]", compares, note));
    }

    static Map<Integer, String> marks(Object... kv) {
        Map<Integer, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((Integer) kv[i], (String) kv[i + 1]);
        return m;
    }

    // ---------- insertion sort (same as InsertionSort.kt) ----------
    static void insertion() {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            step(marks(i, "key"), 0, i, "Take <b>key = " + key + "</b>. Everything left of it is already sorted.");
            while (true) {
                if (j < 0) { step(marks(0, "key"), 0, i, "Reached the start: nothing left to compare."); break; }
                compares++;
                boolean less = key < a[j];
                step(marks(j, "cmp"), 0, i, "Compare key " + key + " &lt; " + a[j] + "? " + (less ? "yes: shift " + a[j] + " right." : "no: the gap is here."));
                if (!less) break;
                a[j + 1] = a[j];
                step(marks(j + 1, "write"), 0, i, "Shifted " + a[j + 1] + " one step right.");
                j--;
            }
            a[j + 1] = key;
            step(marks(j + 1, "write"), 0, i, "Drop key " + key + " into position " + (j + 1) + ". Sorted prefix is now " + (i + 1) + " long.");
        }
        step(marks(), -1, -1, "Done: " + compares + " compares.");
    }

    // ---------- merge sort (same as MergeSort.kt, with indices into one array) ----------
    static void merge(int lo, int hi) {                // half-open [lo, hi)
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        step(marks(), lo, hi - 1, "Split [" + lo + ".." + (hi - 1) + "] into [" + lo + ".." + (mid - 1) + "] and [" + mid + ".." + (hi - 1) + "].");
        merge(lo, mid);
        merge(mid, hi);
        int[] left = Arrays.copyOfRange(a, lo, mid), right = Arrays.copyOfRange(a, mid, hi);
        step(marks(), lo, hi - 1, "Both halves sorted: " + Arrays.toString(left) + " and " + Arrays.toString(right) + ". Merge them.");
        int i = 0, j = 0, k = lo;
        while (i < left.length && j < right.length) {
            compares++;
            boolean takeRight = right[j] < left[i];
            int v = takeRight ? right[j++] : left[i++];
            a[k] = v;
            step(marks(k, "write"), lo, hi - 1, "Compare " + (takeRight ? v + " &lt; " + left[i] : right[j] + " &lt; " + v) + "? " + (takeRight ? "yes: take " + v + " from the right." : "no: take " + v + " from the left."));
            k++;
        }
        while (i < left.length) { a[k] = left[i++]; step(marks(k, "write"), lo, hi - 1, "Right half used up: copy " + a[k] + "."); k++; }
        while (j < right.length) { a[k] = right[j++]; step(marks(k, "write"), lo, hi - 1, "Left half used up: copy " + a[k] + "."); k++; }
    }

    // ---------- quick sort (same as QuickSort.kt: Lomuto, last element is the pivot) ----------
    static void quick(int lo, int hi) {
        if (lo >= hi) return;
        int pivot = a[hi];
        step(marks(hi, "pivot"), lo, hi, "Partition [" + lo + ".." + hi + "] around the last element, <b>pivot = " + pivot + "</b>.");
        int i = lo;
        for (int j = lo; j < hi; j++) {
            compares++;
            boolean less = a[j] < pivot;
            if (less) {
                int t = a[i]; a[i] = a[j]; a[j] = t;
                step(i == j ? marks(i, "write", hi, "pivot") : marks(i, "write", j, "write", hi, "pivot"), lo, hi,
                    "Compare " + a[i] + " &lt; " + pivot + "? yes: " + (i == j ? "it stays in the small part." : "swap it into the small part (index " + i + ").") );
                i++;
            } else {
                step(marks(j, "cmp", hi, "pivot"), lo, hi, "Compare " + a[j] + " &lt; " + pivot + "? no: leave it in the big part.");
            }
        }
        int t = a[i]; a[i] = a[hi]; a[hi] = t;
        step(marks(i, "pivot"), lo, hi, "Swap the pivot into index " + i + ": its final place. Now sort each side.");
        quick(lo, i - 1);
        quick(i + 1, hi);
    }

    public static void main(String[] args) {
        int[] input = {5, 2, 8, 4, 7, 1, 6, 3};
        List<String> algos = new ArrayList<>();
        String[] names = {"Insertion", "Merge", "Quick"};
        for (String name : names) {
            a = input.clone(); compares = 0; steps = new ArrayList<>();
            step(marks(), -1, -1, "Start: " + Arrays.toString(a) + ".");
            switch (name) {
                case "Insertion" -> insertion();
                case "Merge" -> { merge(0, a.length); step(marks(), -1, -1, "Done: " + compares + " compares."); }
                default -> { quick(0, a.length - 1); step(marks(), -1, -1, "Done: " + compares + " compares."); }
            }
            algos.add("{\"name\":\"" + name + "\",\"steps\":[\n  " + String.join(",\n  ", steps) + "]}");
        }
        System.out.println("{\"algos\":[\n" + String.join(",\n", algos) + "]}");
    }
}
