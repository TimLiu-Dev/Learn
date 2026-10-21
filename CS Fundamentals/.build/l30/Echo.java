import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Echo {
    // Handle one connection: read lines, echo each one back, stop at "bye".
    static void serveOne(Socket socket) {
        try (socket;                                         // closes the socket when done
             var input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var output = new PrintWriter(socket.getOutputStream(), true)) { // true = autoflush
            String line;
            while ((line = input.readLine()) != null) {      // null = the client closed its side
                output.println("echo: " + line);
                if (line.equals("bye")) break;
            }
        } catch (Exception e) {
            System.out.println("server error: " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 0;  // "Echo 18130" for nc
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getLoopbackAddress())) {
            System.out.println("listening on port " + server.getLocalPort());
            if (port != 0) { serveOne(server.accept()); return; }    // one nc client, then exit

            Thread serverThread = Thread.ofVirtual().start(() -> {
                try { serveOne(server.accept()); } catch (Exception e) { System.out.println(e); }
            });
            try (Socket s = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort());
                 var output = new PrintWriter(s.getOutputStream(), true);
                 var input = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                for (String msg : List.of("hello", "how are you?", "bye")) {
                    output.println(msg);
                    System.out.println("sent: " + msg + "   got: " + input.readLine());
                }
            }
            serverThread.join();
        }
    }
}
