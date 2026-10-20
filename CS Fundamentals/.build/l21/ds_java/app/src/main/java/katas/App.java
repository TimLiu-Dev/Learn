package katas;

import java.util.*;

public class App {
    static final List<String> MAZE = List.of(
        "S.#.......",
        ".##.####.#",
        "....#....#",
        ".####.##.#",
        "......#..E");

    // Always take the biggest coin that fits. Fast, and sometimes wrong.
    static int greedyCoins(List<Integer> coins, int amount) {
        List<Integer> sorted = new ArrayList<>(coins);
        sorted.sort(Comparator.reverseOrder());
        int left = amount, count = 0;
        for (int coin : sorted) {
            count += left / coin;
            left %= coin;
        }
        return left == 0 ? count : -1;
    }

    public static void main(String[] args) {
        List<Maze.Cell> path = Maze.solve(MAZE);
        if (path == null) {
            System.out.println("no path");
        } else {
            System.out.println(Maze.draw(MAZE, path));
            System.out.println("shortest path: " + (path.size() - 1) + " steps");
        }
        System.out.println();
        record Case(List<Integer> coins, int amount) {}
        for (Case k : List.of(new Case(List.of(1, 2, 5), 11), new Case(List.of(1, 3, 4), 6), new Case(List.of(2), 3))) {
            System.out.println("coins " + k.coins() + ", amount " + k.amount() + ": dp=" + CoinChange.minCoins(k.coins(), k.amount())
                + " greedy=" + greedyCoins(k.coins(), k.amount()));
        }
    }
}
