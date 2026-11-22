public class Escape {
    static StringBuilder log = new StringBuilder();

    static void handle(Runnable handler) {
        try {
            handler.run();
            log.append("200 ");
        } catch (Exception e) {
            log.append("500 ");
        } finally {
            log.append("closed ");
        }
    }

    public static void main(String[] args) {
        try {
            handle(() -> { throw new IllegalStateException("bug"); });
            handle(() -> { throw new IllegalAccessError("private"); });
            handle(() -> { });
        } catch (Throwable t) {
            log.append("escaped");
        }
        System.out.println(log.toString().trim());
    }
}
