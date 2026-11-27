package chat

import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/** Both faces of the chat: the TCP protocol and the HTTP API, sharing one Hub. */
class ChatServer(private val options: Options, users: UserStore) {
    private val hub = Hub()
    private val tokens = Tokens(ByteArray(32).also { SecureRandom().nextBytes(it) }, options.tokenTtl)
    private val loopback = InetAddress.getLoopbackAddress()
    private val listener = ServerSocket(options.tcpPort, 1024, loopback)
    private val http = HttpServer.create(InetSocketAddress(loopback, options.httpPort), 0)
    private val api = HttpApi(http, users, tokens, hub, options.maxQueue)
    private val connections = ConcurrentHashMap.newKeySet<Connection>()

    @Volatile private var stopping = false

    val tcpPort: Int get() = listener.localPort
    val httpPort: Int get() = http.address.port

    fun start() {
        http.executor = Executors.newVirtualThreadPerTaskExecutor()
        http.start()
        Thread.ofPlatform().name("accept").start(::acceptLoop)  // not a daemon: keeps the JVM alive
    }

    private fun acceptLoop() {
        while (true) {
            val socket = try {
                listener.accept()
            } catch (_: IOException) {
                return                                          // listener closed: shutting down
            }
            val connection = Connection(socket, hub, tokens, options.idleTimeout, options.maxQueue) { connections -= it }
            connections += connection
            if (stopping) connection.terminate("ERR 503 shutting_down")
            connection.start()
        }
    }

    /** Graceful stop: no new clients, every client gets a final frame, then everything closes. */
    fun shutdown() {
        stopping = true
        listener.close()
        for (c in connections) c.terminate("ERR 503 shutting_down")
        api.shutdown()
        val deadline = System.nanoTime() + 3_000_000_000L       // a client that never reads can't hold us up
        for (c in connections) c.awaitFlushed(maxOf(0L, (deadline - System.nanoTime()) / 1_000_000))
        for (c in connections) c.closeSocket()
        http.stop(1)
    }
}
