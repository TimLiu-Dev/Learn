import kotlinx.coroutines.*
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

// Same 1,000 clients and 100 ms of blocking work, but the server runs each
// handler as a coroutine on Dispatchers.IO instead of on a virtual thread.
fun main() = runBlocking {
    val loopback = InetAddress.getLoopbackAddress()
    ServerSocket(0, 1024, loopback).use { server ->
        val acceptor = launch(Dispatchers.IO) {
            while (isActive) {
                val s = runCatching { server.accept() }.getOrNull() ?: break
                launch(Dispatchers.IO) {                     // blocking I/O belongs on IO...
                    s.use {
                        val line = it.getInputStream().bufferedReader().readLine()
                        Thread.sleep(100)                    // ...but this blocks an IO thread
                        PrintWriter(it.getOutputStream(), true).println("done $line")
                    }
                }
            }
        }
        val start = System.nanoTime()
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->   // same clients as Servers.kt
            val replies = (1..1000).map { id ->
                pool.submit<String> {
                    Socket(loopback, server.localPort).use { s ->
                        PrintWriter(s.getOutputStream(), true).println("client $id")
                        s.getInputStream().bufferedReader().readLine()
                    }
                }
            }
            println("${replies.count { it.get().startsWith("done") }} replies")
        }
        println("total: ${(System.nanoTime() - start) / 1_000_000} ms")
        server.close()
        acceptor.cancel()
    }
}
