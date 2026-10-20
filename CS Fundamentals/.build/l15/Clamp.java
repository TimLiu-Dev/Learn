public class Clamp {
    static class Clamped {                               // the reusable part
        private final String name;
        private final int min, max;
        private int value;

        Clamped(String name, int min, int max, int initial) {
            this.name = name; this.min = min; this.max = max;
            this.value = Math.clamp(initial, min, max);
        }

        int get() { return value; }

        void set(int v) {
            int clamped = Math.clamp(v, min, max);
            if (clamped != v) System.out.println("  " + name + ": " + v + " clamped to " + clamped);
            value = clamped;
        }
    }

    static class Speaker {                               // the wiring you write by hand
        private final Clamped volume = new Clamped("volume", 0, 10, 5);
        private final Clamped balance = new Clamped("balance", -5, 5, 0);

        int getVolume() { return volume.get(); }
        void setVolume(int v) { volume.set(v); }
        int getBalance() { return balance.get(); }
        void setBalance(int v) { balance.set(v); }
    }

    public static void main(String[] args) {
        Speaker s = new Speaker();
        s.setVolume(7);
        s.setVolume(42);
        s.setBalance(-9);
        System.out.println("volume=" + s.getVolume() + " balance=" + s.getBalance());
    }
}
