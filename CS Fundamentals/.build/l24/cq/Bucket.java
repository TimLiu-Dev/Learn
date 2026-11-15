import java.util.*;

public class Bucket {
    static class Cell {
        int row;
        Cell(int row) { this.row = row; }
        @Override public boolean equals(Object o) { return o instanceof Cell c && c.row == row; }
        @Override public int hashCode() { return row; }
    }

    public static void main(String[] args) {
        Set<Cell> active = new HashSet<>();
        Cell c = new Cell(1);
        active.add(c);
        c.row = 101;
        boolean whileMoved = active.contains(c);
        c.row = 1;                   // put the field back
        System.out.println(whileMoved + " " + active.contains(c) + " " + active.size());
    }
}
