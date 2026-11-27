package chat

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Anyone who receives a room's events: TCP members and SSE watchers. */
interface Subscriber {
    /** Called while the room's lock is held, so it must never block. */
    fun deliver(seq: Long, line: String)
}

/** A TCP client inside a room. */
interface Member : Subscriber {
    val name: String
    val isOpen: Boolean
    fun reply(line: String)
}

/**
 * One room. Its lock is the whole ordering story: every event gets its sequence number and is
 * put into every subscriber's outbox while the lock is held, so all subscribers see one order.
 * Nothing here writes to a socket, so a slow client can't hold the lock.
 */
class Room(val name: String) {
    private val lock = ReentrantLock()
    private val members = LinkedHashSet<Member>()
    private val watchers = LinkedHashSet<Subscriber>()
    private var seq = 0L

    /** false = already a member (or already closing). The reply goes out before the JOINED it causes. */
    fun join(member: Member): Boolean = lock.withLock {
        if (!member.isOpen || !members.add(member)) return false
        member.reply("OK")
        publish("JOINED $name ${member.name}")
        true
    }

    /** false = not a member. With [reply] the leaver gets OK; LEFT goes to those who remain. */
    fun leave(member: Member, reply: Boolean): Boolean = lock.withLock {
        if (!members.remove(member)) return false
        if (reply) member.reply("OK")
        publish("LEFT $name ${member.name}")
        true
    }

    /** false = not a member. */
    fun say(member: Member, text: String): Boolean = lock.withLock {
        if (member !in members) return false
        member.reply("OK")
        publish("MSG $name ${member.name} $text")
        true
    }

    fun who(): List<String> = lock.withLock { members.map { it.name }.sorted() }

    fun watch(watcher: Subscriber) = lock.withLock { watchers.add(watcher) }

    fun unwatch(watcher: Subscriber) = lock.withLock { watchers.remove(watcher) }

    private fun publish(line: String) {
        check(lock.isHeldByCurrentThread)
        seq++
        for (m in members) m.deliver(seq, line)
        for (w in watchers) w.deliver(seq, line)
    }
}

class Hub {
    private val rooms = ConcurrentHashMap<String, Room>()
    private val online = ConcurrentHashMap<String, Member>()

    fun room(name: String): Room = rooms.computeIfAbsent(name, ::Room)

    fun existing(name: String): Room? = rooms[name]

    /** One live TCP connection per user. false = that user is already connected. */
    fun login(user: String, member: Member): Boolean = online.putIfAbsent(user, member) == null

    fun logout(user: String, member: Member) {
        online.remove(user, member)
    }
}
