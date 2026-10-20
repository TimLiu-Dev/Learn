import java.util.*;

public class Islands {
    static final List<String> MAP = List.of(
        "##...#",
        "#....#",
        "...##.",
        "#..##.",
        "#.....");

    // Flood-fill one island, marking every land cell it touches as seen.
    static void sink(int r, int c, boolean[][] seen) {
        if (r < 0 || r >= MAP.size() || c < 0 || c >= MAP.get(r).length()) return;
        if (MAP.get(r).charAt(c) != '#' || seen[r][c]) return;
        seen[r][c] = true;
        sink(r - 1, c, seen); sink(r + 1, c, seen); sink(r, c - 1, seen); sink(r, c + 1, seen);
    }

    public static void main(String[] args) {
        boolean[][] seen = new boolean[MAP.size()][MAP.get(0).length()];
        int islands = 0;
        for (int r = 0; r < MAP.size(); r++) {
            for (int c = 0; c < MAP.get(r).length(); c++) {
                if (MAP.get(r).charAt(c) == '#' && !seen[r][c]) {
                    islands++;
                    sink(r, c, seen);
                }
            }
        }
        System.out.println("islands: " + islands);
    }
}
