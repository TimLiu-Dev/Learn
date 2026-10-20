import java.util.HashSet;
import java.util.Set;

// A key mutated after insertion can never be found again, so it can never be removed.
public class LostKey {
    static class Cell {                           // equals/hashCode use a MUTABLE field
        int row;
        Cell(int row) { this.row = row; }
        @Override public boolean equals(Object o) { return o instanceof Cell c && c.row == row; }
        @Override public int hashCode() { return Integer.hashCode(row); }
    }

    public static void main(String[] args) {
        Set<Cell> active = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            Cell c = new Cell(i);
            active.add(c);
            c.row += 100;                         // mutate the key while it's inside
            System.out.println("removed? " + active.remove(c) + ", size " + active.size());
        }
    }
}
