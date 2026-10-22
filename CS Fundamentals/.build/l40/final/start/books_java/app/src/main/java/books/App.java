package books;

public class App {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 18032;
        long seconds = args.length > 1 ? Long.parseLong(args[1]) : 300;    // never leave a server running
        var api = new BookApi(port);
        api.start();
        System.out.println("Books API on http://127.0.0.1:" + api.port() + "/books (stops after " + seconds + " s)");
        Thread.sleep(seconds * 1000);
        api.stop();
        System.out.println("stopped");
    }
}
