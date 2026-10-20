import java.util.*;

public class GridGraph {
    record Cell(int r, int c) {}

    static final List<String> GRID = List.of(
        "..#.",
        ".##.",
        "....");

    // No edge list at all: the neighbours are computed from the grid on demand.
    static List<Cell> neighbours(Cell cell) {
        List<Cell> result = new ArrayList<>();
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] m : moves) {
            int r = cell.r() + m[0], c = cell.c() + m[1];
            if (r >= 0 && r < GRID.size() && c >= 0 && c < GRID.get(r).length() && GRID.get(r).charAt(c) != '#') {
                result.add(new Cell(r, c));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(neighbours(new Cell(0, 0)));
        System.out.println(neighbours(new Cell(2, 1)));
        System.out.println(neighbours(new Cell(0, 3)));
    }
}
