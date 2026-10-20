import java.util.*;

public class GridPaths {
    static final List<String> GRID = List.of(
        "....",
        ".#..",
        "...#",
        "#...");

    // How many paths from top-left to bottom-right, moving only right or down?
    static long[][] countPaths() {
        int rows = GRID.size(), cols = GRID.get(0).length();
        long[][] dp = new long[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (GRID.get(r).charAt(c) == '#') dp[r][c] = 0;         // blocked: no way through
                else if (r == 0 && c == 0) dp[r][c] = 1;                // base case: the start
                else dp[r][c] = (r > 0 ? dp[r - 1][c] : 0)              // arrived from above
                              + (c > 0 ? dp[r][c - 1] : 0);             // arrived from the left
            }
        }
        return dp;
    }

    public static void main(String[] args) {
        long[][] dp = countPaths();
        for (long[] row : dp) {
            StringJoiner line = new StringJoiner(" ");
            for (long v : row) line.add(String.format("%2d", v));
            System.out.println(line);
        }
        System.out.println("paths: " + dp[dp.length - 1][dp[0].length - 1]);
    }
}
