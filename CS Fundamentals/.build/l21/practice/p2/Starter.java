import java.util.*;

public class Practice {
    record Cell(int r, int c) { }

    static Map<Cell, Integer> distances(List<String> grid, Cell start) {
        throw new UnsupportedOperationException("TODO: BFS from start; distance = discoverer's distance + 1");
    }

    static int stepsTo(List<String> grid, Cell start, Cell target) {
        throw new UnsupportedOperationException("TODO: look target up in distances(…); -1 if missing");
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
