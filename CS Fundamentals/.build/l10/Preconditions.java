public class Preconditions {
    static class Turnstile {
        boolean open = false;
        private int passes = 0;

        void pass(int ticket) {
            if (ticket <= 0) throw new IllegalArgumentException("ticket must be positive, was " + ticket);
            if (!open) throw new IllegalStateException("turnstile is closed");
            passes++;
        }
    }

    static String describe(int code) {
        return switch (code) {
            case 1 -> "ok";
            case 2 -> "retry";
            default -> throw new IllegalStateException("unknown status code " + code);
        };
    }

    static void attempt(String label, Runnable action) {
        try {
            action.run();
            System.out.println(label + ": ok");
        } catch (RuntimeException e) {
            System.out.println(label + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Turnstile t = new Turnstile();
        attempt("pass(-5)", () -> t.pass(-5));
        attempt("pass(7) while closed", () -> t.pass(7));
        t.open = true;
        attempt("pass(7) while open", () -> t.pass(7));
        attempt("describe(9)", () -> describe(9));
    }
}
