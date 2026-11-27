package chat

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** After its final frame, a connection gets this long to drain before its socket is closed regardless. */
val CLOSE_GRACE = 10.seconds

/**
 * One TCP client: a reader thread that parses and handles commands, and a writer thread that
 * drains the outbox onto the socket. Two virtual threads per client (L28).
 */
class Connection(
    private val socket: Socket,
    private val hub: Hub,
    private val tokens: Tokens,
    private val idleTimeout: Duration,
    maxQueue: Int,
    private val onClosed: (Connection) -> Unit,
) : Member {
    private val outbox = Outbox(maxQueue)
    private val joined = ConcurrentHashMap.newKeySet<Room>()
    private val writerDone = CountDownLatch(1)

    @Volatile private var user: String? = null

    override val name: String get() = user ?: "-"
    override val isOpen: Boolean get() = !outbox.isClosed

    fun start() {
        Thread.ofVirtual().name("writer").start(::writeLoop)
        Thread.ofVirtual().name("reader").start(::readLoop)
    }

    override fun reply(line: String) {
        if (!outbox.offer(line)) terminate("ERR 503 slow_consumer", dropQueued = true)
    }

    override fun deliver(seq: Long, line: String) = reply(line)

    /**
     * Ends the conversation: [last] is the final frame, then the socket closes. Safe to call from any
     * thread, many times; only the first call counts. Leaving rooms happens on a fresh thread, because
     * this may run inside a room's lock and must not take another room's lock (L27: lock ordering).
     */
    fun terminate(last: String?, dropQueued: Boolean = false) {
        if (!outbox.close(last, dropQueued)) return
        Thread.ofVirtual().name("cleanup").start {
            for (room in joined) room.leave(this, reply = false)
            user?.let { hub.logout(it, this) }
            if (!writerDone.await(CLOSE_GRACE.inWholeMilliseconds, TimeUnit.MILLISECONDS)) closeSocket()
        }
    }

    /** For shutdown: waits for the final frame to be written. */
    fun awaitFlushed(timeoutMs: Long) = writerDone.await(timeoutMs, TimeUnit.MILLISECONDS)

    fun closeSocket() {
        try { socket.close() } catch (_: IOException) {}
    }

    private fun readLoop() {
        try {
            socket.soTimeout = idleTimeout.inWholeMilliseconds.toInt()   // read() gives up after this much silence
            val input = DataInputStream(BufferedInputStream(socket.getInputStream()))
            try {
                while (true) {
                    val payload = readFrame(input) ?: break
                    if (!isOpen) continue                       // after our final frame we only drain
                    val line = decodeUtf8(payload)
                    if (line == null) reply("ERR 400 bad_utf8") else handle(line)
                }
            } catch (_: FrameTooLarge) {
                terminate("ERR 413 frame_too_large")             // we can't find the next frame boundary
                drain(input)
            } catch (_: SocketTimeoutException) {
                terminate("ERR 408 idle_timeout")
                drain(input)
            }
        } catch (_: IOException) {
            // the client vanished mid-frame, or the socket was closed under us
        } finally {
            terminate(null)
            writerDone.await(CLOSE_GRACE.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            closeSocket()
            onClosed(this)
        }
    }

    /**
     * Reads and discards until the client closes (or 2 s of silence). Closing a socket that still has
     * unread input sends a TCP reset, which can destroy our final frame before the client reads it.
     */
    private fun drain(input: DataInputStream) {
        socket.soTimeout = 2000
        val scrap = ByteArray(8192)
        try {
            while (input.read(scrap) != -1) { /* discard */ }
        } catch (_: IOException) {}
    }

    private fun writeLoop() {
        try {
            val out = BufferedOutputStream(socket.getOutputStream())
            while (true) {
                val line = outbox.take() ?: break
                out.write(encodeFrame(line))
                if (outbox.isEmpty()) out.flush()               // batch whatever is already waiting
            }
            out.flush()
            socket.shutdownOutput()                             // FIN: the client reads to the end, then sees EOF
        } catch (_: IOException) {
            // the client is gone; the reader notices too
        } finally {
            writerDone.countDown()
        }
    }

    private fun handle(line: String) {
        val command = when (val parsed = parseCommand(line)) {
            is Err -> return reply(parsed.toString())
            is Command -> parsed
        }
        val me = user
        if (me == null) {
            when (command) {
                is Command.Auth -> auth(command.token)
                Command.Quit -> terminate("OK")
                else -> reply("ERR 401 not_authenticated")
            }
            return
        }
        when (command) {
            is Command.Auth -> reply("ERR 409 already_authenticated")
            is Command.Join -> {
                val room = hub.room(command.room)
                joined += room                                  // before join(), so cleanup can't miss it
                if (!room.join(this)) reply("ERR 409 already_member")
            }
            is Command.Leave -> {
                val room = hub.existing(command.room)
                if (room != null && room.leave(this, reply = true)) joined -= room
                else reply("ERR 403 not_member")
            }
            is Command.Msg -> {
                val room = hub.existing(command.room)
                if (room == null || !room.say(this, command.text)) reply("ERR 403 not_member")
            }
            is Command.Who -> {
                val names = hub.existing(command.room)?.who().orEmpty()
                reply((listOf("OK") + names).joinToString(" "))
            }
            Command.Quit -> terminate("OK")
        }
    }

    private fun auth(token: String) {
        when (val check = tokens.verify(token)) {
            TokenCheck.Bad -> reply("ERR 401 bad_token")
            TokenCheck.Expired -> reply("ERR 401 token_expired")
            is TokenCheck.Valid ->
                if (hub.login(check.user, this)) {
                    user = check.user
                    reply("OK ${check.user}")
                } else {
                    reply("ERR 409 already_connected")
                }
        }
    }
}
