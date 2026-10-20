package katas;

import java.util.*;

public final class Maze {
    public record Cell(int r, int c) {}

    private Maze() {}

    // Shortest path from S to E (both included), or null if E can't be reached.
    public static List<Cell> solve(List<String> maze) {
        Cell start = find(maze, 'S');
        Cell end = find(maze, 'E');
        Map<Cell, Cell> parent = new HashMap<>();   // keys double as the visited set
        parent.put(start, null);
        Deque<Cell> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty()) {
            Cell cell = queue.removeFirst();
            if (cell.equals(end)) {
                List<Cell> path = new ArrayList<>();
                for (Cell c = end; c != null; c = parent.get(c)) path.add(c);
                Collections.reverse(path);
                return path;
            }
            for (Cell next : openNeighbours(maze, cell)) {
                if (!parent.containsKey(next)) {
                    parent.put(next, cell);
                    queue.addLast(next);
                }
            }
        }
        return null;
    }

    public static String draw(List<String> maze, List<Cell> path) {
        char[][] rows = new char[maze.size()][];
        for (int r = 0; r < maze.size(); r++) rows[r] = maze.get(r).toCharArray();
        for (Cell cell : path) {
            if (rows[cell.r()][cell.c()] == '.') rows[cell.r()][cell.c()] = '*';
        }
        StringJoiner out = new StringJoiner("\n");
        for (char[] row : rows) out.add(new String(row));
        return out.toString();
    }

    private static Cell find(List<String> maze, char ch) {
        for (int r = 0; r < maze.size(); r++) {
            int c = maze.get(r).indexOf(ch);
            if (c >= 0) return new Cell(r, c);
        }
        throw new IllegalArgumentException("maze has no '" + ch + "'");
    }

    private static List<Cell> openNeighbours(List<String> maze, Cell cell) {
        List<Cell> result = new ArrayList<>();
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] m : moves) {
            int r = cell.r() + m[0], c = cell.c() + m[1];
            if (r >= 0 && r < maze.size() && c >= 0 && c < maze.get(r).length() && maze.get(r).charAt(c) != '#') {
                result.add(new Cell(r, c));
            }
        }
        return result;
    }
}
