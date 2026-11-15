import java.util.Objects;

public class Practice {
    // Linear probing for ints, kept at most half full so probe runs stay short.
    static class IntSet {
        private Integer[] slots = new Integer[8];
        private int size = 0;

        int size() { return size; }
        int capacity() { return slots.length; }

        boolean contains(int x) {
            Integer s = slots[find(slots, x)];
            return s != null && s == x;
        }

        boolean add(int x) {
            int i = find(slots, x);
            if (slots[i] != null) return false;   // find stopped on x itself
            slots[i] = x;
            size++;
            if (size > capacity() / 2) resize();
            return true;
        }

        private void resize() {
            throw new UnsupportedOperationException("TODO: double the slots and put every element where it belongs in the new array");
        }

        // The slot holding x, or the first empty slot on its probe path.
        private static int find(Integer[] table, int x) {
            int i = Math.floorMod(x, table.length);
            while (table[i] != null && table[i] != x) i = (i + 1) % table.length;   // Integer vs int: unboxes, so this compares values
            return i;
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("adding past half full doubles the capacity", "8 then 16", () -> {
            IntSet s = new IntSet();
            for (int x = 1; x <= 4; x++) s.add(x);
            int before = s.capacity();
            s.add(5);
            return before + " then " + s.capacity();
        });
        check("keys that probed away are found at their new home", "16 true true", () -> {
            IntSet s = new IntSet();
            for (int x : new int[] {3, 11, 19, 1, 2}) s.add(x);
            return s.capacity() + " " + s.contains(11) + " " + s.contains(19);
        });
        check("a hundred elements survive five resizes", "size 100, capacity 256, all found, no 5", () -> {
            IntSet s = new IntSet();
            for (int x = 0; x < 100; x++) s.add(x * 7);
            boolean all = true;
            for (int x = 0; x < 100; x++) all &= s.contains(x * 7);
            return "size " + s.size() + ", capacity " + s.capacity() + (all ? ", all found" : ", some lost") + (s.contains(5) ? ", has 5" : ", no 5");
        });
        check("duplicates are not added twice, even across a resize", "false 5", () -> {
            IntSet s = new IntSet();
            for (int x = 1; x <= 5; x++) s.add(x);
            return s.add(1) + " " + s.size();
        });
        check("negative numbers move too", "true true", () -> {
            IntSet s = new IntSet();
            for (int x : new int[] {-1, -9, -17, -25, -33}) s.add(x);
            return s.contains(-17) + " " + s.contains(-33);
        });
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
