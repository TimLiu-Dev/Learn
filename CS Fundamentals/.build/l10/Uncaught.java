public class Uncaught {
    static int parsePort(String text) { return Integer.parseInt(text); }

    static String startServer(String config) { return "listening on " + parsePort(config); }

    public static void main(String[] args) {
        System.out.println(startServer("80x"));   // nobody catches it
        System.out.println("never printed");
    }
}
