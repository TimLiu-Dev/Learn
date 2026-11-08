import java.util.Objects;

public class Practice {
    static class Battery {
        private final int capacity;
        private int level;
        private int recharges;

        Battery(int capacity, int initialLevel) {
            this.capacity = capacity;
            level = initialLevel;               // TODO: the starting value must be clamped too
        }

        int getLevel() { return level; }

        void setLevel(int value) {
            level = value;                      // TODO: clamp into 0..capacity
        }

        int getPercent() {
            throw new UnsupportedOperationException("TODO: level as a whole-number percentage of capacity");
        }

        int getRecharges() { return recharges; }

        void setRecharges(int value) { recharges = value; }   // TODO: outsiders must not assign this

        void use(int amount) {
            throw new UnsupportedOperationException("TODO: drain by amount, never below 0");
        }

        void recharge() {
            setLevel(capacity);
            recharges++;
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("level is clamped on every assignment", "100 0", () -> {
            Battery b = new Battery(100, 50);
            b.setLevel(150);
            int high = b.getLevel();
            b.setLevel(-5);
            return high + " " + b.getLevel();
        });
        check("the starting level is clamped too", "100 0", () -> new Battery(100, 250).getLevel() + " " + new Battery(100, -1).getLevel());
        check("percent is computed on every read", "25 100", () -> {
            Battery b = new Battery(200, 50);
            int before = b.getPercent();
            b.setLevel(200);
            return before + " " + b.getPercent();
        });
        check("use drains but never goes below zero", "20 0", () -> {
            Battery b = new Battery(100, 50);
            b.use(30);
            int once = b.getLevel();
            b.use(30);
            return once + " " + b.getLevel();
        });
        check("recharges can be read but not assigned from outside", "2 100 no setter", () -> {
            Battery b = new Battery(100, 10);
            b.recharge();
            b.recharge();
            boolean setter = java.util.Arrays.stream(Battery.class.getDeclaredMethods()).anyMatch(m -> m.getName().equals("setRecharges"));
            return b.getRecharges() + " " + b.getLevel() + (setter ? " has a setter" : " no setter");
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
