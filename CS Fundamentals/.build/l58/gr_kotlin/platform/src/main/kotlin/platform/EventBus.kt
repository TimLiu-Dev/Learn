package platform

// An in-process event bus. Delivery is synchronous, on the publisher's thread, and in publish
// order: an event published by a handler waits in the queue until the current one is done.
// Not thread-safe; one bus per single-threaded application (L26 would add a lock).
class EventBus {
    private class Subscription(val type: Class<*>, val handler: (Any) -> Unit)

    private val subscriptions = mutableListOf<Subscription>()
    private val queue = ArrayDeque<Any>()
    private var delivering = false

    fun <E : Any> subscribe(type: Class<E>, handler: (E) -> Unit) {
        subscriptions += Subscription(type) { event -> handler(type.cast(event)) }
    }

    fun publish(event: Any) {
        queue.addLast(event)
        if (delivering) return                 // a handler published: deliver it after the current event
        delivering = true
        try {
            while (queue.isNotEmpty()) {
                val next = queue.removeFirst()
                subscriptions.filter { it.type.isInstance(next) }.forEach { it.handler(next) }
            }
        } finally {
            delivering = false
            queue.clear()                      // a handler threw: don't deliver leftovers later, out of context
        }
    }
}

inline fun <reified E : Any> EventBus.subscribe(noinline handler: (E) -> Unit) = subscribe(E::class.java, handler)
