public class Exit {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("hook")));
        try {
            System.out.print("work ");
            System.exit(0);
        } finally {
            System.out.print("finally ");
        }
    }
}
