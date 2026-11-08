package books;

import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 18055;
        long seconds = args.length > 1 ? Long.parseLong(args[1]) : 300;    // never leave a server running
        // Demo users. In real life passwords come from a sign-up form, never from source code.
        var auth = new Auth(Map.of("ana", "correct horse", "ben", "battery staple", "root", "admin admin"), Set.of("root"));
        var api = new BookApi(auth, port);
        api.start();
        System.out.println("Books API on http://127.0.0.1:" + api.port() + "/books (stops after " + seconds + " s)");
        Thread.sleep(seconds * 1000);
        api.stop();
        System.out.println("stopped");
    }
}
