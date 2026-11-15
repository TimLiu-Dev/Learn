public class Budget {
    static class OutOfFuel extends RuntimeException {}

    static class Fuel {
        private long left;
        Fuel(long left) { this.left = left; }
        void tick() { if (--left < 0) throw new OutOfFuel(); }
    }

    // A program that halts after exactly 1,000 steps
    static void program(Fuel fuel) {
        for (int i = 0; i < 1000; i++) fuel.tick();
    }

    static boolean finishesWithin(long steps) {
        try { program(new Fuel(steps)); return true; }
        catch (OutOfFuel e) { return false; }
    }

    public static void main(String[] args) {
        System.out.println(finishesWithin(999) + " " + finishesWithin(1000));
    }
}
