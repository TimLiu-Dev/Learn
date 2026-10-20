public class FinallyReturn {
    static int risky() {
        try {
            throw new IllegalStateException("important failure");
        } finally {
            return -1;     // DON'T: this silently discards the exception
        }
    }

    public static void main(String[] args) {
        System.out.println(risky());
        System.out.println("no one ever hears about the failure");
    }
}
