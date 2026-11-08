import java.nio.file.*;
import java.util.*;

public class Components {
    static int[] parent, size;

    static int find(int p) {
        int r = p;
        while (r != parent[r]) { parent[r] = parent[parent[r]]; r = parent[r]; }   // path halving
        return r;
    }

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Path.of("tinyUF.txt")).stream().filter(l -> !l.isBlank()).toList();
        int n = Integer.parseInt(lines.get(0).trim());
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        int components = n;                                  // every site starts alone
        for (String line : lines.subList(1, lines.size())) {
            String[] f = line.trim().split("\\s+");
            int p = Integer.parseInt(f[0]), q = Integer.parseInt(f[1]);
            int rp = find(p), rq = find(q);
            if (rp == rq) continue;                          // already connected: ignore the pair
            if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp]; }
            else { parent[rq] = rp; size[rp] += size[rq]; }
            components--;                                    // each real union merges two into one
            System.out.println(p + " " + q);
        }
        System.out.println(components + " components");
    }
}
