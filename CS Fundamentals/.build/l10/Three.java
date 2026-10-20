public class Three {
    static class Res implements AutoCloseable {
        private final String name;
        Res(String name) { this.name = name; System.out.println("open " + name); }
        @Override public void close() { System.out.println("close " + name); }
    }

    public static void main(String[] args) {
        try (Res x = new Res("X"); Res y = new Res("Y"); Res z = new Res("Z")) {
            System.out.println("using X, Y, Z");
        }
    }
}
