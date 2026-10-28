import java.net.InetAddress
import java.net.ServerSocket

// A tiny queue broker over TCP (L30's line protocol), using JobQueue.kt.
//   PUSH <body>  -> OK
//   POP          -> MSG <id> <attempt> <body>   or EMPTY
//   ACK <id>     -> OK   or UNKNOWN
fun main(args: Array<String>) {
    val q = JobQueue(capacity = 1000, visibilityMs = 2000, maxAttempts = 5)
    val server = ServerSocket(args[0].toInt(), 50, InetAddress.getLoopbackAddress())
    println("queue broker on 127.0.0.1:${server.localPort}")
    while (true) {
        val socket = server.accept()
        Thread.ofVirtual().start {
            socket.use { s ->
                val out = s.getOutputStream().bufferedWriter()
                s.getInputStream().bufferedReader().lineSequence().forEach { line ->
                    val parts = line.split(" ", limit = 2)
                    val reply = when (parts[0]) {
                        "PUSH" -> { q.push(parts.getOrElse(1) { "" }); "OK" }
                        "POP" -> q.receive(0)?.let { "MSG ${it.id} ${it.attempts} ${it.body}" } ?: "EMPTY"
                        "ACK" -> if (q.ack(parts[1].toLong())) "OK" else "UNKNOWN"
                        else -> "ERR unknown command"
                    }
                    out.write(reply + "\n"); out.flush()
                }
            }
        }
    }
}
