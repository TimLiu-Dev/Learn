import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

// Handle one connection: read lines, echo each one back, stop at "bye".
fun serveOne(socket: Socket) {
    socket.use {                                            // closes the socket when done
        val input = it.getInputStream().bufferedReader()
        val output = PrintWriter(it.getOutputStream(), true) // true = flush after println
        while (true) {
            val line = input.readLine() ?: break            // null = the client closed its side
            output.println("echo: $line")
            if (line == "bye") break
        }
    }
}

fun main(args: Array<String>) {
    val port = args.firstOrNull()?.toInt() ?: 0             // "Echo 18130" for the nc demo
    ServerSocket(port, 50, InetAddress.getLoopbackAddress()).use { server ->
        println("listening on port ${server.localPort}")
        if (port != 0) { serveOne(server.accept()); return } // serve one nc client, then exit

        val serverThread = Thread.ofVirtual().start { serveOne(server.accept()) }
        Socket(InetAddress.getLoopbackAddress(), server.localPort).use { s ->
            val output = PrintWriter(s.getOutputStream(), true)
            val input = s.getInputStream().bufferedReader()
            for (msg in listOf("hello", "how are you?", "bye")) {
                output.println(msg)
                println("sent: $msg   got: ${input.readLine()}")
            }
        }
        serverThread.join()
    }
}
