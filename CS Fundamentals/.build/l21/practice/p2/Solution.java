import java.util.*;

public class Practice {
    record Cell(int r, int c) { }

    static Map<Cell, Integer> distances(List<String> grid, Cell start) {
        Map<Cell, Integer> dist = new HashMap<>();      // doubles as the visited set
        dist.put(start, 0);
        Deque<Cell> queue = new ArrayDeque<>(List.of(start));
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            Cell cell = queue.removeFirst();            // FIFO: closest cells come out first
            for (int[] m : moves) {
                Cell next = new Cell(cell.r() + m[0], cell.c() + m[1]);
                boolean open = next.r() >= 0 && next.r() < grid.size()
                    && next.c() >= 0 && next.c() < grid.get(next.r()).length()
                    && grid.get(next.r()).charAt(next.c()) != '#';
                if (open && !dist.containsKey(next)) {
                    dist.put(next, dist.get(cell) + 1); // set when enqueued, so never twice
                    queue.addLast(next);
                }
            }
        }
        return dist;
    }

    static int stepsTo(List<String> grid, Cell start, Cell target) {
        return distances(grid, start).getOrDefault(target, -1);
    }

    // CHECKS
    static final List<String> GRID = List.of(
        "....#..",
        ".##.#..",
        "....#..");

    public static void main(String[] args) {
        check("the start is 0 moves from itself", 0, () -> stepsTo(GRID, new Cell(0, 0), new Cell(0, 0)));
        check("a straight run along the top", 3, () -> stepsTo(GRID, new Cell(0, 0), new Cell(0, 3)));
        check("the shortest way round the wall", "4 5", () -> stepsTo(GRID, new Cell(0, 0), new Cell(2, 2)) + " " + stepsTo(GRID, new Cell(0, 0), new Cell(2, 3)));
        check("an unreachable target gives -1", -1, () -> stepsTo(GRID, new Cell(0, 0), new Cell(0, 6)));
        check("walls and unreachable cells get no distance", "10 null", () -> { var d = distances(GRID, new Cell(0, 0)); return d.size() + " " + d.get(new Cell(1, 1)); });
        check("distances work from any start", Map.of(new Cell(0, 5), 0, new Cell(0, 6), 1, new Cell(1, 5), 1, new Cell(1, 6), 2, new Cell(2, 5), 2, new Cell(2, 6), 3), () -> distances(GRID, new Cell(0, 5)));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
