import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

// A tiny Redis: SET key value / GET key / DEL key / QUIT, one line per command.
class KvServer(port: Int = 0) : AutoCloseable {
    private val store = ConcurrentHashMap<String, String>()   // shared by every client thread
    private val server = ServerSocket(port, 1024, InetAddress.getLoopbackAddress())
    val port: Int get() = server.localPort
    val size: Int get() = store.size

    init {
        Thread.ofPlatform().daemon().start {
            while (true) {
                val s = runCatching { server.accept() }.getOrNull() ?: break  // closed = stop
                Thread.ofVirtual().start { session(s) }      // one virtual thread per client
            }
        }
    }

    // The protocol, as a pure function: easy to read, easy to test. null means "hang up".
    fun respond(line: String): String? {
        val parts = line.trim().split(" ", limit = 3)        // the value may contain spaces
        return when (parts[0].uppercase()) {
            "SET" -> if (parts.size == 3) { store[parts[1]] = parts[2]; "OK" } else "ERR usage: SET key value"
            "GET" -> if (parts.size == 2) store[parts[1]]?.let { "VALUE $it" } ?: "NOT_FOUND"
                     else "ERR usage: GET key"
            "DEL" -> if (parts.size == 2) { if (store.remove(parts[1]) != null) "OK" else "NOT_FOUND" }
                     else "ERR usage: DEL key"
            "QUIT" -> null
            else -> "ERR unknown command"
        }
    }

    private fun session(socket: Socket) = socket.use {
        val input = it.getInputStream().bufferedReader()
        val out = PrintWriter(it.getOutputStream(), true)
        while (true) {
            val line = input.readLine() ?: break             // client hung up
            val reply = respond(line)
            if (reply == null) { out.println("BYE"); break }
            out.println(reply)
        }
    }

    override fun close() = server.close()
}

fun selfTest() = KvServer().use { kv ->
    val start = System.nanoTime()
    val wrong = Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        (1..100).map { c ->
            pool.submit<Int> {
                Socket(InetAddress.getLoopbackAddress(), kv.port).use { s ->
                    val out = PrintWriter(s.getOutputStream(), true)
                    val input = s.getInputStream().bufferedReader()
                    var bad = 0
                    for (k in 1..100) {
                        out.println("SET c$c:k$k v$c-$k"); if (input.readLine() != "OK") bad++
                        out.println("GET c$c:k$k");       if (input.readLine() != "VALUE v$c-$k") bad++
                    }
                    out.println("QUIT"); if (input.readLine() != "BYE") bad++
                    bad
                }
            }
        }.sumOf { it.get() }
    }
    val ms = (System.nanoTime() - start) / 1_000_000
    println("100 clients x 100 SET/GET pairs: $wrong wrong replies, ${kv.size} keys, $ms ms")

    Socket(InetAddress.getLoopbackAddress(), kv.port).use { s ->
        val out = PrintWriter(s.getOutputStream(), true)
        val input = s.getInputStream().bufferedReader()
        for (cmd in listOf("GET missing", "SET greeting hello world", "GET greeting",
                           "DEL greeting", "DEL greeting", "FLY away", "QUIT")) {
            out.println(cmd)
            println("  %-26s -> %s".format(cmd, input.readLine()))
        }
    }
}

fun main(args: Array<String>) {
    if (args.firstOrNull() == "serve") {                     // KvServer serve 18131
        KvServer(args[1].toInt())
        println("serving on 127.0.0.1:${args[1]} (Ctrl-C to stop)")
        Thread.currentThread().join()                        // wait forever
    } else selfTest()
}
