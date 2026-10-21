import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

// Each request: read one line, "work" for workMs (think: a database call), reply.
fun handle(socket: Socket, workMs: Long) = socket.use {
    val line = it.getInputStream().bufferedReader().readLine()
    Thread.sleep(workMs)
    PrintWriter(it.getOutputStream(), true).println("done $line")
}

fun run(title: String, threadPerClient: Boolean, clients: Int, workMs: Long) {
    val loopback = InetAddress.getLoopbackAddress()
    ServerSocket(0, 1024, loopback).use { server ->
        Thread.ofPlatform().daemon().start {                 // the accept loop
            while (true) {
                val s = runCatching { server.accept() }.getOrNull() ?: break
                if (threadPerClient) Thread.ofVirtual().start { handle(s, workMs) }
                else handle(s, workMs)                       // one client at a time
            }
        }
        val start = System.nanoTime()
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            val replies = (1..clients).map { id ->
                pool.submit<String> {
                    Socket(loopback, server.localPort).use { s ->
                        PrintWriter(s.getOutputStream(), true).println("client $id")
                        val reply = s.getInputStream().bufferedReader().readLine()
                        "$reply after ${(System.nanoTime() - start) / 1_000_000} ms"
                    }
                }
            }
            println(title)
            if (clients <= 3) replies.forEach { println("  " + it.get()) }
            else println("  ${replies.count { it.get().startsWith("done") }} replies")
        }
        println("  total: ${(System.nanoTime() - start) / 1_000_000} ms")
    }
}

fun main() {
    run("A. one client at a time, 2 clients, 500 ms of work each", false, 2, 500)
    run("B. a virtual thread per client, 2 clients", true, 2, 500)
    run("C. a virtual thread per client, 1,000 clients, 100 ms each", true, 1000, 100)
}
