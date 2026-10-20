public class Unwind {
    static int parsePort(String text) {
        System.out.println("parsePort: parsing \"" + text + "\"");
        int port = Integer.parseInt(text);     // throws NumberFormatException
        System.out.println("parsePort: done"); // never runs
        return port;
    }

    static String startServer(String config) {
        try {
            int port = parsePort(config);
            return "listening on " + port;
        } finally {
            System.out.println("startServer: finally runs while unwinding");
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(startServer("80x"));
        } catch (NumberFormatException e) {
            System.out.println("main caught: " + e.getMessage());
        }
        System.out.println("main continues");
    }
}
