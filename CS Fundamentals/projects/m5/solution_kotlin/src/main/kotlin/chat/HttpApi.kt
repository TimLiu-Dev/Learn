package chat

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

private val EVENTS_PATH = Regex("/rooms/([^/]+)/events")

/** An SSE client watching one room. Its outbox holds ready-to-write event text. */
class Watcher(maxQueue: Int) : Subscriber {
    val outbox = Outbox(maxQueue)

    override fun deliver(seq: Long, line: String) {
        if (!outbox.offer("id: $seq\ndata: $line\n\n")) {
            outbox.close("event: error\ndata: slow_consumer\n\n", dropQueued = true)
        }
    }
}

/** POST /login and GET /rooms/{room}/events on the JDK's HttpServer (L31, L32). */
class HttpApi(
    private val server: HttpServer,
    private val users: UserStore,
    private val tokens: Tokens,
    private val hub: Hub,
    private val maxQueue: Int,
) {
    private val watchers = ConcurrentHashMap.newKeySet<Watcher>()

    init {
        server.createContext("/") { ex -> ex.use { route(it) } }
    }

    /** Ends every SSE stream with a shutdown event. */
    fun shutdown() {
        for (w in watchers) w.outbox.close("event: shutdown\ndata: shutting_down\n\n", dropQueued = false)
    }

    private fun route(ex: HttpExchange) {
        val path = ex.requestURI.rawPath
        val events = EVENTS_PATH.matchEntire(path)
        when {
            path == "/login" -> if (ex.requestMethod == "POST") login(ex) else notAllowed(ex, "POST")
            events != null -> if (ex.requestMethod == "GET") stream(ex, events.groupValues[1]) else notAllowed(ex, "GET")
            else -> sendJson(ex, 404, error("not_found"))
        }
    }

    private fun login(ex: HttpExchange) {
        val body = ex.requestBody.readNBytes(8193)
        if (body.size > 8192) return sendJson(ex, 413, error("too_large"))
        val json = runCatching { Json.parseToJsonElement(body.decodeToString()) as? JsonObject }.getOrNull()
        val user = json?.string("user")
        val password = json?.string("password")
        if (user == null || password == null) return sendJson(ex, 400, error("bad_request"))
        if (!users.verify(user, password)) return sendJson(ex, 401, error("invalid_credentials"))
        sendJson(ex, 200, buildJsonObject {
            put("token", tokens.issue(user))
            put("expires_in", tokens.ttlSeconds)
        })
    }

    private fun stream(ex: HttpExchange, roomName: String) {
        if (!ROOM_NAME.matches(roomName)) return sendJson(ex, 400, error("bad_room"))
        val header = ex.requestHeaders.getFirst("Authorization").orEmpty()
        val check = if (header.startsWith("Bearer ")) tokens.verify(header.removePrefix("Bearer ")) else TokenCheck.Bad
        if (check !is TokenCheck.Valid) {
            ex.responseHeaders["WWW-Authenticate"] = "Bearer"
            return sendJson(ex, 401, error(if (check == TokenCheck.Expired) "token_expired" else "bad_token"))
        }
        val room = hub.room(roomName)
        val watcher = Watcher(maxQueue)
        watchers += watcher
        room.watch(watcher)                                     // subscribed before the client hears "200"
        try {
            ex.responseHeaders["Content-Type"] = "text/event-stream; charset=utf-8"
            ex.responseHeaders["Cache-Control"] = "no-cache"
            ex.sendResponseHeaders(200, 0)                      // 0 = chunked: the body has no known end
            val out = ex.responseBody
            out.write(": ok\n\n".toByteArray())
            out.flush()
            while (true) {
                val event = watcher.outbox.take() ?: break
                out.write(event.toByteArray())
                if (watcher.outbox.isEmpty()) out.flush()
            }
            out.flush()
        } catch (_: IOException) {
            // the browser went away
        } finally {
            room.unwatch(watcher)
            watchers -= watcher
            watcher.outbox.close(null, dropQueued = true)
        }
    }

    private fun notAllowed(ex: HttpExchange, allow: String) {
        ex.responseHeaders["Allow"] = allow
        sendJson(ex, 405, error("method_not_allowed"))
    }

    private fun error(reason: String) = buildJsonObject { put("error", reason) }

    private fun sendJson(ex: HttpExchange, status: Int, body: JsonObject) {
        val bytes = body.toString().toByteArray()
        ex.responseHeaders["Content-Type"] = "application/json"
        ex.sendResponseHeaders(status, bytes.size.toLong())
        ex.responseBody.write(bytes)
    }

    private fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
}
