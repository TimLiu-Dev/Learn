import java.util.ArrayList;
import java.util.List;

// Two ways to handle collisions, same keys, 8 slots, index = floorMod(key, 8)
public class Collide {
    public static void main(String[] args) {
        int[] keys = {10, 18, 3, 26, 11};

        // Separate chaining: each slot holds a list of everything that landed there
        List<List<Integer>> chains = new ArrayList<>();
        for (int i = 0; i < 8; i++) chains.add(new ArrayList<>());
        for (int k : keys) chains.get(Math.floorMod(k, 8)).add(k);
        System.out.println("chaining:");
        for (int i = 0; i < 8; i++)
            if (!chains.get(i).isEmpty()) System.out.println("  [" + i + "] " + chains.get(i));

        // Linear probing: one key per slot; if taken, try the next slot (wrapping)
        Integer[] slots = new Integer[8];
        System.out.println("linear probing:");
        for (int k : keys) {
            int i = Math.floorMod(k, 8);
            int probes = 1;
            while (slots[i] != null) { i = (i + 1) % 8; probes++; }
            slots[i] = k;
            System.out.println("  put " + k + ": home " + Math.floorMod(k, 8)
                    + ", landed in " + i + " after " + probes + " probe(s)");
        }
        StringBuilder row = new StringBuilder("  ");
        for (Integer s : slots) row.append(s == null ? "_" : s).append(' ');
        System.out.println(row.toString().stripTrailing());
    }
}
