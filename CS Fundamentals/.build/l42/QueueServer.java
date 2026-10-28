import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// A tiny queue broker over TCP (L30's line protocol), using JobQueue.java.
//   PUSH <body>  -> OK
//   POP          -> MSG <id> <attempt> <body>   or EMPTY
//   ACK <id>     -> OK   or UNKNOWN
public class QueueServer {
    public static void main(String[] args) throws IOException {
        var q = new JobQueue(1000, 2000, 5);
        var server = new ServerSocket(Integer.parseInt(args[0]), 50, InetAddress.getLoopbackAddress());
        System.out.println("queue broker on 127.0.0.1:" + server.getLocalPort());
        while (true) {
            Socket socket = server.accept();
            Thread.ofVirtual().start(() -> serve(socket, q));
        }
    }

    static void serve(Socket socket, JobQueue q) {
        try (socket;
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                String reply = switch (parts[0]) {
                    case "PUSH" -> { q.push(parts.length > 1 ? parts[1] : ""); yield "OK"; }
                    case "POP" -> {
                        var m = q.receive(0);
                        yield m == null ? "EMPTY" : "MSG " + m.id() + " " + m.attempts() + " " + m.body();
                    }
                    case "ACK" -> q.ack(Long.parseLong(parts[1])) ? "OK" : "UNKNOWN";
                    default -> "ERR unknown command";
                };
                out.write(reply + "\n");
                out.flush();
            }
        } catch (IOException | InterruptedException e) {
            // client went away
        }
    }
}
